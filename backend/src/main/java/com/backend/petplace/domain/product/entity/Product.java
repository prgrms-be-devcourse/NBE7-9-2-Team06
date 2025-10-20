package com.backend.petplace.domain.product.entity;

import com.backend.petplace.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Product extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long productId;

  @Column(nullable = false)
  private String productName;

  @Column(nullable = false)
  private Long price;

  @Column(nullable = false)
  private Long stock;

  private String description;


  @Builder
  public Product(String productName, Long price, Long stock, String description) {
    this.productName = productName;
    this.price = price;
    this.stock = stock;
    this.description = description;
  }

  //정적 팩토리 메서드를 통한 product 객체 생성
  public static Product createProduct(String productName, Long price, Long stock, String description) {
    return Product.builder()
        .productName(productName)
        .price(price)
        .stock(stock)
        .description(description)
        .build();
  }
}
