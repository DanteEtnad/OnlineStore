package org.example.controller;

import org.example.model.Product;
import org.example.model.Customer;
import org.example.model.Order;
import org.example.model.Bank;
import org.example.model.BankTransfer;
import org.example.repository.ProductRepository;
import org.example.repository.CustomerRepository;
import org.example.repository.OrderRepository;
import org.example.repository.BankRepository;
import org.example.Service.BankService;
import org.example.Service.BankTransferService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/store")
public class StoreController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private BankService bankService;

    @Autowired
    private BankTransferService bankTransferService;

    private PasswordEncoder passwordEncoder;

    // Get all product information
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Register a new user
    @PostMapping("/login")
    public CustomerResponse<Object> login(@PathVariable String  name, @PathVariable String password) {
        Optional<Customer> optionalCustomer = customerRepository.findByName(name);

        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();

            if (passwordEncoder.matches(password, customer.getPassword())) {
                return new CustomerResponse<>("success", "Login successful", customer.getName());
            } else {
                return new CustomerResponse<>("error", "Incorrect password", null);
            }
        } else {
            return new CustomerResponse<>("error", "Customer not found", null);
        }
    }

    // Log in
    @PostMapping("/register")
    public CustomerResponse<Object> register(@PathVariable String name, @PathVariable String email, @PathVariable String password) {
        if (customerRepository.findByEmail(email).isPresent()) {
            return new CustomerResponse<>("error", "Email is already registered", null);
        }

        String encodedPassword = passwordEncoder.encode(password);
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPassword(encodedPassword);
        customerRepository.save(customer);

        return new CustomerResponse<>("success", "Registration successful", customer.getCustomerId());
    }

    // Order function
    @PostMapping("/{customerId}/order")
    public CustomerResponse<Object> placeOrder(@PathVariable Long customerId, @RequestParam Long productId, @RequestParam Integer quantity) {
        // Step 1: Find Products
        Optional<Product> optionalProduct = productRepository.findById(productId);
        Optional<Customer> customer = customerRepository.findById(customerId);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            // Step 2: Calculate the total price
            Double totalAmount = product.getPrice() * quantity;

            // Step 3: Create and save order
            Order order = new Order();
            order.setProduct(product);
            order.setQuantity(quantity);
            order.setTotalAmount(totalAmount);
            orderRepository.save(order);

            // Step 4: Return order information
            return new CustomerResponse<>("success", "Order placed successfully",
                    new OrderResponse(customer.get().getCustomerId(), order.getOrderId(), product.getProductId(), quantity, totalAmount));
        } else {
            return new CustomerResponse<>("error", "Product not found", null);
        }
    }

    // Create a payment invoice
    @PostMapping("/{orderId}/payment")
    public CustomerResponse<Object> createPayment(@PathVariable Long orderId, @RequestParam Long fromAccountId) {
        BankService bankService = new BankService();
        try {
            Long toAccountId = this.getStoreAccountId();
            bankService.createPaymentBill(fromAccountId, toAccountId);
            return new CustomerResponse<>("success", "Payment bills created successfully", null);
        } catch (Exception e) {
            return new CustomerResponse<>("error", e.getMessage(), null);
        }
    }

    @PostMapping("/{orderId}/refund")
    public RefundResponse refundOrder(@PathVariable Long orderId, @RequestParam Long fromAccountId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            // Check if the order is eligible for a refund (paid)
            if (!order.getStatus().equals("paid")) {
                return new RefundResponse(orderId, "Refund not possible. The order is not paid.");
            }

            // Assuming you have a method to get the store's account
            Long storeAccountId = getStoreAccountId(); // You need to implement this method
            Double amountToRefund = order.getTotalAmount();

            // Create the refund transaction
            BankTransfer refundTransfer = bankTransferService.createBankTransfer(storeAccountId, fromAccountId, amountToRefund);

            // Optionally, you can update the order status to "refunded"
            order.setStatus("refunded");
            order.setBankTransfer(refundTransfer);
            orderRepository.save(order);

            return new RefundResponse(orderId, refundTransfer.getBankTransferId(), "Refund initiated successfully.");
        } else {
            return new RefundResponse(orderId, "Order not found.");
        }
    }

    @PostMapping("/checkOrderStatus/{orderId}")
    public OrderStatusResponse checkOrderStatus(@PathVariable Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            // Check if the order is in 'paying' status
            if (order.getStatus().equals("paying")) {
                // Get the associated BankTransfer
                BankTransfer bankTransfer = order.getBankTransfer();

                if (bankTransfer != null) {
                    // Check the status of the BankTransfer
                    if (bankTransfer.getStatus().equals("success")) {
                        // Update the order status to 'paid'
                        order.setStatus("paid");
                        orderRepository.save(order);
                        return new OrderStatusResponse("Order status updated to 'paid' for order ID: " + orderId);
                    } else {
                        return new OrderStatusResponse("Bank transfer status is not successful. Current status: ", bankTransfer.getStatus());
                    }
                } else {
                    return new OrderStatusResponse("No associated bank transfer found for this order.", bankTransfer.getStatus());
                }
            } else {
                return new OrderStatusResponse("Order is not in 'paying' status. Current status: ", order.getStatus());
            }
        } else {
            return new OrderStatusResponse("Order not found with ID: " + orderId);
        }
    }

    // Method to get the store account ID
    private Long getStoreAccountId() {
        // Check if a bank account with accountType 'store' exists
        Optional<Bank> storeAccountOpt = bankRepository.findByAccountType("store");

        // If it exists, return its accountId
        if (storeAccountOpt.isPresent()) {
            return storeAccountOpt.get().getAccountId();
        } else {
            // If it does not exist, create a new store account
            Bank newStoreAccount = new Bank();
            newStoreAccount.setAccountType("store");
            newStoreAccount.setName("StoreAccount"); // Set a default name
            newStoreAccount.setBalance(1000.00); // Set the initial balance

            // Save the new store account
            Bank savedStoreAccount = bankRepository.save(newStoreAccount);
            return savedStoreAccount.getAccountId(); // Return the newly created accountId
        }
    }


    //Inner Class for response
    public static class CustomerResponse<T> {
        private String status;
        private String message;
        private T data;

        public CustomerResponse(String status, String message, T data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }
    }

    // Inner class used to return order information
    public static class OrderResponse {
        private Long customerId;
        private Long orderId;
        private Long productId;
        private Integer quantity;
        private Double totalAmount;

        public OrderResponse(Long customerId, Long orderId, Long productId, Integer quantity, Double totalAmount) {
            this.customerId = customerId;
            this.orderId = orderId;
            this.productId = productId;
            this.quantity = quantity;
            this.totalAmount = totalAmount;
        }
    }

    // Inner class for refund response
    public static class RefundResponse {
        private Long orderId;
        private Long refundTransferId;
        private String message;

        public RefundResponse(Long orderId, String message) {
            this.orderId = orderId;
            this.message = message;
        }

        public RefundResponse(Long orderId, Long refundTransferId, String message) {
            this.orderId = orderId;
            this.refundTransferId = refundTransferId;
            this.message = message;
        }
    }

    //Inner Class for order status response
    public static class OrderStatusResponse {
        private String message;
        private String status;

        public OrderStatusResponse(String message, String status) {
            this.message = message;
            this.status = status;
        }
        public OrderStatusResponse(String message) {
            this.message = message;
        }
    }
}
