package com.backend.petplace.global.initdata;

import com.backend.petplace.domain.product.entity.Product;
import com.backend.petplace.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BaseInitData implements CommandLineRunner {

  private final ProductRepository productRepository;

  @Override
  public void run(String... args) throws Exception {

    Product p1 = Product.builder()
        .price(150L)
        .stock(9999L)
        .description("상품 1")
        .build();

    Product p2 = Product.builder()
        .price(80L)
        .stock(9999L)
        .description("상품 2")
        .build();

    Product p3 = Product.builder()
        .price(120L)
        .stock(9999L)
        .description("상품 3")
        .build();

    Product p4 = Product.builder()
        .price(200L)
        .stock(9999L)
        .description("상품 4")
        .build();

    // DB에 저장
    productRepository.save(p1);
    productRepository.save(p2);
    productRepository.save(p3);
    productRepository.save(p4);
  }
}
