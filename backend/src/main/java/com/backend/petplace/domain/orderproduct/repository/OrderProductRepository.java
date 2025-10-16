package com.backend.petplace.domain.orderproduct.repository;

import com.backend.petplace.domain.orderproduct.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

}
