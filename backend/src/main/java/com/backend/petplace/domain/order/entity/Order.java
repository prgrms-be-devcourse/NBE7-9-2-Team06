package com.backend.petplace.domain.order.entity;

import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long orderId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @NotNull
  @Min(0)
  private Long totalPrice;

  @NotNull
  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  @Builder
  public Order(User user, Long totalPrice, OrderStatus orderStatus) {
    this.user = user;
    this.totalPrice = totalPrice;
    this.orderStatus = orderStatus;
  }

  public static Order createOrder(User user, Long totalPrice) {
    return Order.builder()
        .user(user)
        .totalPrice(totalPrice)
        .orderStatus(OrderStatus.ORDERED)
        .build();
  }
}
