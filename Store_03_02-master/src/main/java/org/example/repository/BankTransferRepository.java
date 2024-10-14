package org.example.repository;

import org.example.model.BankTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankTransferRepository extends JpaRepository<BankTransfer, Long> {
    List<BankTransfer> findByStatusIn(List<String> statuses);
}