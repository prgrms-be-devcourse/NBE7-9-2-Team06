package com.backend.petplace.domain.orderproduct.entity;

import com.backend.petplace.domain.order.entity.Order;
import com.backend.petplace.domain.product.entity.Product;
import com.backend.petplace.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderProduct extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long orderProductId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "orders", nullable = false)
  Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product")
  Product product;

  private long quantity;

  @Builder
  private OrderProduct(Order order, Product product, long quantity) {
    this.order = order;
    this.product = product;
    this.quantity = quantity;
  }

  public static OrderProduct createOrderProduct(Order order, Product product, long quantity) {
    return OrderProduct.builder()
        .order(order)
        .product(product)
        .quantity(quantity)
        .build();
  }
}
