package com.backend.petplace.global.initdata;

import com.backend.petplace.domain.product.entity.Product;
import com.backend.petplace.domain.product.repository.ProductRepository;
import com.backend.petplace.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BaseInitData implements CommandLineRunner {

  private final ProductRepository productRepository;

  @Override
  public void run(String... args) throws Exception {

    Product p1 = new Product();
    p1.setPrice(150L);
    p1.setStock(9999L);
    p1.setDescription("상품 1");

    Product p2 = new Product();
    p2.setPrice(80L);
    p2.setStock(9999L);
    p2.setDescription("상품 2");

    Product p3 = new Product();
    p3.setPrice(120L);
    p3.setStock(9999L);
    p3.setDescription("상품 3");

    Product p4 = new Product();
    p4.setPrice(200L);
    p4.setStock(9999L);
    p4.setDescription("상품 4");

    // DB에 저장
    productRepository.save(p1);
    productRepository.save(p2);
    productRepository.save(p3);
    productRepository.save(p4);
  }

}
