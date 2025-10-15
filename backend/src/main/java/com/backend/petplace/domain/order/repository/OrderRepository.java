package com.backend.petplace.domain.order.repository;

import com.backend.petplace.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
