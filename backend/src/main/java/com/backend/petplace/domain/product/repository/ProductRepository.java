package com.backend.petplace.domain.product.repository;

import com.backend.petplace.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
