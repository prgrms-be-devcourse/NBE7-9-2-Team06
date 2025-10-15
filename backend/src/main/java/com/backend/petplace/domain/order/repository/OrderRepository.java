package com.backend.petplace.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.petplace.domain.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
