package com.backend.petplace.domain.orderproduct.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.petplace.domain.orderproduct.entity.OrderProduct;

public interface OrderProductRepository  extends JpaRepository<OrderProduct, Long> {

}
