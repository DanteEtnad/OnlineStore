package org.example.Service;

import org.example.model.Bank;
import org.example.model.Order;
import org.example.model.BankTransfer;
import org.example.repository.BankRepository;
import org.example.repository.BankTransferRepository;
import org.example.repository.OrderRepository;
import org.example.controller.BankTransferController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankService {

    @Autowired
    private BankTransferRepository bankTransferRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BankTransferController bankTransferController;

    @Transactional
    public BankTransfer createPaymentBill(Long fromAccountId, Long toAccountId, Long orderId) {
        // Step 1: Validate fromAccount
        Bank fromAccount = bankRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("From account not found"));

        // Step 2: Validate toAccount
        Bank toAccount = bankRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("To account not found"));

        // Check if toAccount is a store account
        if (!"store".equals(toAccount.getAccountType())) {
            throw new IllegalArgumentException("To account must be a store account");
        }

        // Step 3: Retrieve the specific order by orderId
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Check if the order status is "pending"
        if (!"pending".equals(order.getStatus())) {
            throw new IllegalArgumentException("Order status must be 'pending'");
        }

        // Step 4: Create payment bill
        BankTransfer bankTransfer = bankTransferController.createBankTransfer(fromAccountId, toAccountId, order.getTotalAmount());
        bankTransferRepository.save(bankTransfer);

        // Step 5: Update order status to "payment in progress"
        order.setStatus("paying");
        order.setBankTransfer(bankTransfer);
        orderRepository.save(order);
        return bankTransfer;
    }

    @Transactional
    public void processRefund(Long orderId) {
        // Step 1: Find the order and check if it's paid
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!"paid".equals(order.getStatus())) {
            throw new IllegalArgumentException("Refund can only be processed for paid orders");
        }

        // Step 2: Find the corresponding BankTransfer for this order
        BankTransfer bankTransfer = bankTransferRepository.findById(order.getBankTransfer().getBankTransferId()).orElseThrow(() ->
                new IllegalArgumentException("No bank transfer found for this order"));

        // Step 3: Get fromAccount (customer) and toAccount (store) from the bank transfer
        Long fromAccountId = bankTransfer.getFromAccount(); // The customer account
        Long toAccountId = bankTransfer.getToAccount(); // The store account

        // Step 4: Create a refund bank transfer
        double refundAmount = order.getTotalAmount(); // Assuming Order has a method to get the total price

        // Step 5: Update account balances
        Bank fromAccount = bankRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("From account not found"));
        Bank toAccount = bankRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("To account not found"));

        // Check if the fromAccount has sufficient balance
        if (toAccount.getBalance() < refundAmount) {
            throw new IllegalArgumentException("Insufficient balance in to account for refund");
        }

        // Deduct the amount from the store account and add it to the customer account
        toAccount.setBalance(toAccount.getBalance() - refundAmount);
        fromAccount.setBalance(fromAccount.getBalance() + refundAmount);

        // Save updated accounts
        bankRepository.save(fromAccount);
        bankRepository.save(toAccount);

        // Step 6: Create a bank transfer record for the refund
        BankTransfer refundTransfer = bankTransferController.createBankTransfer(toAccountId, fromAccountId, refundAmount);
        bankTransferRepository.save(refundTransfer);

        // Step 7: Update the order status to "refunded"
        order.setStatus("refunded");
        orderRepository.save(order);
    }
}
