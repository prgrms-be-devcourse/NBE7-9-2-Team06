package com.backend.petplace.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.petplace.domain.product.entity.Product;

public interface ProductRepository  extends JpaRepository<Product, Long> {
}
