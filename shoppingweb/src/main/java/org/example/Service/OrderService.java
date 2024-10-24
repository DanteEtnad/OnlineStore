package org.example.Service;

import org.example.model.BankTransfer;
import org.example.model.Order;
import org.example.repository.BankTransferRepository;
import org.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BankTransferRepository bankTransferRepository;

    @Transactional
    @Scheduled(fixedRate = 5000)
    public void checkBankTransferStatus() {
        List<Order> orders = orderRepository.findByStatusIn(List.of("paying"));

        for (Order order : orders) {
            BankTransfer bankTransfer = order.getBankTransfer();

            if (bankTransfer != null && bankTransfer.getStatus().equals("failed")) {
                order.setStatus("pending");
                orderRepository.save(order);
                System.out.println("Order " + order.getOrderId() + " status set to pending due to failed bank transfer.");
            }else if (bankTransfer == null) {
                order.setStatus("pending");
                orderRepository.save(order);
                System.out.println("Order " + order.getOrderId() + " has no associated BankTransfer.");
            }
        }
    }
}
