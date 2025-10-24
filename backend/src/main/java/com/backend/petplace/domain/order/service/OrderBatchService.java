package com.backend.petplace.domain.order.service;

import com.backend.petplace.domain.order.entity.Order;
import com.backend.petplace.domain.order.repository.OrderRepository;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.stereotype.Service;

@Service
public class OrderBatchService {
  private final OrderRepository orderRepository;

  public OrderBatchService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void processBatch(List<Order> batch) {
    for (Order order : batch) {
      order.setOrderStatusDelivered();
    }
    orderRepository.flush();
    orderRepository.clear();
  }
}



