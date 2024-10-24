package org.example.controller;

import org.example.model.BankTransfer;
import org.example.repository.BankTransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/bank-transfers")
public class BankTransferController {

    @Autowired
    private BankTransferRepository bankTransferRepository;

    // Endpoint to create a new bank transfer
    public BankTransfer createBankTransfer(Long fromAccount, Long toAccount, Double amount) {
        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setFromAccount(fromAccount);
        bankTransfer.setToAccount(toAccount);
        bankTransfer.setAmount(amount);
        return bankTransferRepository.save(bankTransfer);
    }
}
