package org.example.Service;

import org.example.model.BankTransfer;
import org.example.repository.BankTransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankTransferService {

    @Autowired
    private BankTransferRepository bankTransferRepository;

    @Transactional
    public BankTransfer createBankTransfer(Long fromAccount, Long toAccount, Double amount) {
        // Create a new BankTransfer object
        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setFromAccount(fromAccount);
        bankTransfer.setToAccount(toAccount);
        bankTransfer.setAmount(amount);

        // Save the bank transfer to the database
        return bankTransferRepository.save(bankTransfer);
    }
}
