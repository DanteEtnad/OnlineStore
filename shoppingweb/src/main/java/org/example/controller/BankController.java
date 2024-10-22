package org.example.controller;

import org.example.model.Bank;
import org.example.model.BankTransfer;
import org.example.repository.BankRepository;
import org.example.repository.BankTransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/bank")
public class BankController {

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private BankTransferRepository bankTransferRepository;

    @PostMapping("/createAccount")
    public ResponseAccount<Bank> createAccount(@RequestParam String name,
                                              @RequestParam String accountType,
                                              @RequestParam Double balance) {
        Bank newAccount = new Bank();
        newAccount.setName(name);
        newAccount.setAccountType(accountType);
        newAccount.setBalance(balance);
        bankRepository.save(newAccount);

        return new ResponseAccount<>("Account successfully created", newAccount);
    }

    //Get Balance by Id
    @GetMapping("/{accountId}/balance")
    public BankController.AccountBalanceResponse getAccountBalanceById(@PathVariable Long accountId) {
        Bank bank = bankRepository.findById(accountId).orElseThrow(() ->
                new IllegalArgumentException("Balance not found by id: " + accountId));
        return new BankController.AccountBalanceResponse(bank.getAccountId(), bank.getBalance());
    }

    //Get Account Details by transfer
    @GetMapping("/transfer/{bankTransferId}/details")
    public BankTransferDetailsResponse getTransferDetailsById(@PathVariable Long bankTransferId) {
        BankTransfer bankTransfer = bankTransferRepository.findById(bankTransferId).orElseThrow(() ->
                new IllegalArgumentException("Transfer not found for id: " + bankTransferId));
        Bank fromAccount = bankRepository.findById(bankTransfer.getFromAccount()).orElseThrow(() ->
                new IllegalArgumentException("From account not found for id: " + bankTransfer.getFromAccount()));
        Bank toAccount = bankRepository.findById(bankTransfer.getToAccount()).orElseThrow(() ->
                new IllegalArgumentException("To account not found for id: " + bankTransfer.getToAccount()));
        return new BankTransferDetailsResponse(bankTransferId, fromAccount.getAccountId(), toAccount.getAccountId(),
                bankTransfer.getStatus());
    }

    //check the amount to make sure there is enough money in the account, Update Balance
    @PostMapping("/transfer/{bankTransferId}")
    public BankTransfer bankTransferAmount(@PathVariable Long bankTransferId) {
        BankTransfer bankTransfer = bankTransferRepository.findById(bankTransferId).orElseThrow(() ->
                new IllegalArgumentException("Transfer not found for id: " + bankTransferId));
        Bank fromAccount = bankRepository.findById(bankTransfer.getFromAccount()).orElseThrow(() ->
                new IllegalArgumentException("Source account not found: " + bankTransfer.getFromAccount()));
        Bank toAccount = bankRepository.findById(bankTransfer.getToAccount()).orElseThrow(() ->
                new IllegalArgumentException("Destination account not found: " + bankTransfer.getToAccount()));

        String status;

        if (fromAccount.getBalance() < bankTransfer.getAmount()) {
            // If the balance is insufficient, the status will be set to failed and a prompt will be returned.
            status = "FAILED";
            bankTransfer.setStatus(status);
            bankTransferRepository.save(bankTransfer);

            return bankTransfer;
        }

        // If the balance is sufficient, execute the transfer and set the status to success
        fromAccount.setBalance(-bankTransfer.getAmount());
        toAccount.setBalance(bankTransfer.getAmount());
        bankRepository.save(fromAccount);
        bankRepository.save(toAccount);
        status = "SUCCESS";
        bankTransfer.setStatus(status);
        bankTransferRepository.save(bankTransfer);

        return bankTransfer;
    }

    // Get the status of all bankTransfer
    @GetMapping("/bank_status")
    public List<BankController.BankTransferStatusResponse> getAllBankTransferStatuses() {
        return bankTransferRepository.findAll().stream()
                .map(bankTransfer -> new BankController.BankTransferStatusResponse(bankTransfer.getBankTransferId(), bankTransfer.getStatus()))
                .collect(Collectors.toList());
    }

    // Internal Classes for balance Get
    public static class AccountBalanceResponse {
        private Long accountId;
        private double balance;

        public AccountBalanceResponse(Long accountId, double balance) {
            this.accountId = accountId;
            this.balance = balance;
        }

        // Getters
        public Long getAccountId() {
            return accountId;
        }

        public double getBalance() {
            return balance;
        }
    }

    public class ResponseAccount<T> {
        private String message;
        private T data;

        public ResponseAccount(String message, T data) {
            this.message = message;
            this.data = data;
        }

        // Getters and Setters
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    // Internal Classes for transfer info Get
    public static class BankTransferDetailsResponse {
        private Long bankTransferId;
        private Long fromAccount;
        private Long toAccount;
        private String status;

        public BankTransferDetailsResponse(Long bankTransferId, Long fromAccount, Long toAccount, String status) {
            this.bankTransferId = bankTransferId;
            this.fromAccount = fromAccount;
            this.toAccount = toAccount;
            this.status = status;
        }

        // Getters
        public Long getBankTransferId() {
            return bankTransferId;
        }

        public Long getFromAccount() {
            return fromAccount;
        }

        public Long getToAccount() {
            return toAccount;
        }

        public String getStatus() {
            return status;
        }
    }

    // Internal Classes for Responding to BankTransfer Status
    public static class BankTransferStatusResponse {
        private Long bankTransferId;
        private String status;

        public BankTransferStatusResponse(Long bankTransferId, String status) {
            this.bankTransferId = bankTransferId;
            this.status = status;
        }
        // Getters
        public Long getBankTransferId() {
            return bankTransferId;
        }

        public String getStatus() {
            return status;
        }
    }
}
