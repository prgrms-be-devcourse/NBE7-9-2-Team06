package com.backend.petplace.domain.order.dto.response;

import com.backend.petplace.domain.order.entity.Order;
import com.backend.petplace.domain.order.entity.OrderStatus;
import com.backend.petplace.domain.orderproduct.entity.OrderProduct;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class OrderReadByIdResponse {

  private Long orderId;
  private LocalDateTime updatedAt;
  private OrderStatus orderStatus;
  private Long totalPrice;
  private List<OrderProductDto> orderProducts;

  public OrderReadByIdResponse(Order order) {
    this.orderId = order.getOrderId();
    this.updatedAt = order.getModifiedDate();
    this.orderStatus = order.getOrderStatus();
    this.totalPrice = order.getTotalPrice();
    this.orderProducts = order.getOrderProducts().stream()
        .map(OrderProductDto::new)
        .toList();
  }

  @Getter
  public static class OrderProductDto {

    private String productName;
    private long quantity;

    public OrderProductDto(OrderProduct orderProduct) {
      this.productName = orderProduct.getProduct().getProductName();
      this.quantity = orderProduct.getQuantity();
    }
  }
}
