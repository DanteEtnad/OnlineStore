package org.example.Service;

import org.example.model.BankTransfer;
import org.example.model.Order;
import org.example.repository.BankTransferRepository;
import org.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BankTransferRepository bankTransferRepository;


}
