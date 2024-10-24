package org.example.controller;

import org.example.model.Product;
import org.example.model.Customer;
import org.example.model.Order;
import org.example.model.Bank;
import org.example.model.BankTransfer;
import org.example.Service.BankService;
import org.example.repository.ProductRepository;
import org.example.repository.CustomerRepository;
import org.example.repository.OrderRepository;
import org.example.repository.BankRepository;
import org.example.Service.BankTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@CrossOrigin(origins = "http://localhost:3000")
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

    // Get all product information
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Inner class for hash password
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Log in
    @PostMapping("/login")
    public CustomerResponse<Object> login(@RequestParam String name, @RequestParam String password) {
        Optional<Customer> optionalCustomer = customerRepository.findByName(name);

        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();

            if (hashPassword(password).equals(customer.getPassword())) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("customerId", customer.getCustomerId());
                userInfo.put("name", customer.getName());

                return new CustomerResponse<>("success", "Login successful", userInfo);
            } else {
                return new CustomerResponse<>("error", "Incorrect password", null);
            }
        } else {
            return new CustomerResponse<>("error", "Customer not found", null);
        }
    }

    // Register
    @PostMapping("/register")
    @ResponseBody
    public CustomerResponse<Object> register(@RequestParam String name, @RequestParam String email, @RequestParam String password) {
        if (customerRepository.findByEmail(email).isPresent()) {
            return new CustomerResponse<>("error", "Email is already registered", null);
        }

        if (customerRepository.findByName(name).isPresent()) {
            return new CustomerResponse<>("error", "Username is already registered", null);
        }

        String encodedPassword = hashPassword(password);
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPassword(encodedPassword);
        customerRepository.save(customer);

        return new CustomerResponse<>("success", "Registration successful", customer.getCustomerId());
    }

    @PostMapping("/{customerId}/order")
    public CustomerResponse<Object> placeOrder(@PathVariable Long customerId, @RequestParam Long productId, @RequestParam Integer quantity) {
        // Step 1: Find Products
        Optional<Product> optionalProduct = productRepository.findById(productId);
        Optional<Customer> customer = customerRepository.findById(customerId);

        if (optionalProduct.isPresent() && customer.isPresent()) {
            Product product = optionalProduct.get();
            Customer foundCustomer = customer.get();

            // Step 2: Calculate the total price
            Double totalAmount = product.getPrice() * quantity;

            // Step 3: Create and save order
            Order order = new Order();
            order.setProduct(product);
            order.setQuantity(quantity);
            order.setTotalAmount(totalAmount);
            order.setOrderDate(LocalDateTime.now()); // 设置订单日期为当前时间
            order.setCustomer(foundCustomer); // 设置客户信息

            orderRepository.save(order);

            // Step 4: Return order information
            return new CustomerResponse<>("success", "Order placed successfully",
                    new OrderResponse(foundCustomer.getCustomerId(), order.getOrderId(), product.getProductId(), product.getProductName(), quantity, product.getPrice(), totalAmount)); // 确保传递产品名称
        } else {
            return new CustomerResponse<>("error", "Product or customer not found", null);
        }
    }


    // Create a payment invoice
    @PostMapping("/{customerId}/{orderId}/payment")
    public CustomerResponse<Object> createPayment(@PathVariable Long customerId, @PathVariable Long orderId, @RequestParam Long fromAccountId) {
        try {
            Long toAccountId = this.getStoreAccountId();
            bankService.createPaymentBill(fromAccountId, toAccountId, orderId);
            return new CustomerResponse<>("success", "Payment bills created successfully", null);
        } catch (Exception e) {
            return new CustomerResponse<>("error", e.getMessage(), null);
        }
    }

    @PostMapping("/{customerId}/{orderId}/refund")
    public RefundResponse refundOrder(@PathVariable Long customerId, @PathVariable Long orderId, @RequestParam Long fromAccountId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            // Check if the order is eligible for a refund (paid)
            if ( !(order.getStatus().equals("paying") || order.getStatus().equals("payed")) ) {
                return new RefundResponse(orderId, "Refund not possible. The order is not paid.");
            }

            BankTransfer orderbankTransfer = order.getBankTransfer();
            String orderStatus = orderbankTransfer.getStatus();

            if (!orderStatus.equals("completed")) {
                return new RefundResponse(orderId, "Refund not possible. The order is not paid.");
            }

            // Get the store's account
            Long storeAccountId = getStoreAccountId();
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

    // Only by calling this function can the order status be changed to paid, and then the deliveryCo will detect the paid status and then delivery.
    @PostMapping("/{customerId}/checkOrderStatus/{orderId}")
    public OrderStatusResponse checkOrderStatus(@PathVariable Long customerId, @PathVariable Long orderId) {
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

    // Inner method to get the store account ID
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

    // Inner Class for response
    public static class CustomerResponse<T> {
        private String status;
        private String message;
        private T data;

        public CustomerResponse(String status, String message, T data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public T getData() {
            return data;
        }
    }

    // Inner class used to return order information
    public static class OrderResponse {
        private Long customerId;
        private Long orderId;
        private Long productId;
        private String productName; // 添加产品名称
        private Integer quantity;
        private Double unitPrice; // 单价
        private Double totalAmount;

        public OrderResponse(Long customerId, Long orderId, Long productId, String productName, Integer quantity, Double unitPrice, Double totalAmount) {
            this.customerId = customerId;
            this.orderId = orderId;
            this.productId = productId;
            this.productName = productName; // 赋值产品名称
            this.quantity = quantity;
            this.unitPrice = unitPrice; // 赋值单价
            this.totalAmount = totalAmount;
        }

        public Long getCustomerId() {
            return customerId;
        }

        public Long getOrderId() {
            return orderId;
        }

        public Long getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName; // 返回产品名称
        }

        public Integer getQuantity() {
            return quantity;
        }

        public Double getUnitPrice() {
            return unitPrice; // 返回单价
        }

        public Double getTotalAmount() {
            return totalAmount;
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

        public Long getOrderId() {
            return orderId;
        }

        public Long getRefundTransferId() {
            return refundTransferId;
        }

        public String getMessage() {
            return message;
        }
    }

    // Inner Class for order status response
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

        public String getMessage() {
            return message;
        }

        public String getStatus() {
            return status;
        }
    }
}
