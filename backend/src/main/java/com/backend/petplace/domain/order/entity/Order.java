package com.backend.petplace.domain.order.entity;

import com.backend.petplace.domain.orderproduct.entity.OrderProduct;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long orderId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @NotNull
  @Min(0)
  private Integer totalPrice;

  @NotNull
  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderProduct> orderProducts = new ArrayList<>();

  @Builder
  public Order(User user, Integer totalPrice, OrderStatus orderStatus) {
    this.user = user;
    this.totalPrice = totalPrice;
    this.orderStatus = orderStatus;
  }

  //정적 팩토리 메서드를 통한 Order 객체 생성
  public static Order createOrder(User user, Integer totalPrice) {
    return Order.builder()
        .user(user)
        .totalPrice(totalPrice)
        .orderStatus(OrderStatus.ORDERED)
        .build();
  }

  //객체 생성 후 orderProducts에 order 추가 메소드
  public void addOrderProducts(List<OrderProduct> orderProducts) {
    for (OrderProduct orderProduct : orderProducts) {
      addOrderProduct(orderProduct);
    }
  }

  private void addOrderProduct(OrderProduct orderProduct) {
    this.orderProducts.add(orderProduct);
    orderProduct.setOrder(this);
  }

  //객체 생성 후 orders에 order 추가 메소드
  public void setUser(User user) {
    // 기존 user와의 연관관계 제거
    if (this.user != null) {
      this.user.getOrders().remove(this);
    }

    // 새로운 user와의 연관관계 설정
    this.user = user;

    // 새로운 user의 orders 리스트에 현재 객체 추가
    if (user != null && !user.getOrders().contains(this)) { // 아직 연결 안 되어 있으면
      user.getOrders().add(this);        // User 쪽 리스트에도 추가
    }
  }

  public void cancelOrder() {

    this.orderStatus = OrderStatus.CANCELED;
  }

  public void setOrderStatusDelivered() {
    this.orderStatus = OrderStatus.DELIVERED;
  }
}
