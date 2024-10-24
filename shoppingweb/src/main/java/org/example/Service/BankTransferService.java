package org.example.Service;

import org.example.controller.BankTransferController;
import org.example.model.BankTransfer;
import org.example.repository.BankRepository;
import org.example.repository.BankTransferRepository;
import org.example.model.Bank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class BankTransferService {

    @Autowired
    private BankTransferRepository bankTransferRepository;

    @Autowired
    private BankTransferController bankTransferController;

    @Autowired
    private BankRepository bankRepository;

    @Transactional
    public BankTransfer createBankTransfer(Long fromAccount, Long toAccount, Double amount) {
        // Create a new BankTransfer object
        // Save the bank transfer to the database
        return bankTransferController.createBankTransfer(fromAccount, toAccount, amount);
    }

    @Transactional
    @Scheduled(fixedRate = 2000)
    public void processPendingTransfers() {
        // Step 1: Find all pending transfers
        List<BankTransfer> pendingTransfers = bankTransferRepository.findByStatus("pending");

        for (BankTransfer transfer : pendingTransfers) {
            try {
                // Step 2: Retrieve accounts
                Bank fromAccount = bankRepository.findById(transfer.getFromAccount())
                        .orElseThrow(() -> new IllegalArgumentException("From account not found"));
                Bank toAccount = bankRepository.findById(transfer.getToAccount())
                        .orElseThrow(() -> new IllegalArgumentException("To account not found"));

                // Step 3: Perform the transfer
                if (fromAccount.getBalance() < transfer.getAmount()) {
                    throw new IllegalArgumentException("Insufficient funds in from account");
                }
                fromAccount.setBalance(-transfer.getAmount());
                toAccount.setBalance(transfer.getAmount());

                // Step 4: Update the accounts and transfer status
                bankRepository.save(fromAccount);
                bankRepository.save(toAccount);

                transfer.setStatus("completed");
                bankTransferRepository.save(transfer);

                System.out.println("Transfer " + transfer.getBankTransferId() + " completed.");

            } catch (Exception e) {
                System.err.println("Error processing transfer: " + transfer.getBankTransferId() + " - " + e.getMessage());
                transfer.setStatus("failed");
                bankTransferRepository.save(transfer);
            }
        }
    }

    public Optional<BankTransfer> findById(Long bankTransferId) {
        return bankTransferRepository.findById(bankTransferId);
    }
}
