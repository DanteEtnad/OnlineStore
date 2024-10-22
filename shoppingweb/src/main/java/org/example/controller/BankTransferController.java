package org.example.controller;

import org.example.model.BankTransfer;
import org.example.Service.BankTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/bank-transfers")
public class BankTransferController {

    @Autowired
    private BankTransferService bankTransferService;

    // Endpoint to create a new bank transfer
    @PostMapping("/create")
    public BankTransfer createBankTransfer(
            @RequestParam Long fromAccount,
            @RequestParam Long toAccount,
            @RequestParam Double amount) {
        return bankTransferService.createBankTransfer(fromAccount, toAccount, amount);
    }
}
