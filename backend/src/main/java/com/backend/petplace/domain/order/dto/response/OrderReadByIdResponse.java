package com.backend.petplace.domain.order.dto.response;

import com.backend.petplace.domain.order.entity.Order;
import com.backend.petplace.domain.order.entity.OrderStatus;
import com.backend.petplace.domain.orderproduct.entity.OrderProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
@Schema(description = "주문 상세 조회 응답 DTO")
public class OrderReadByIdResponse {

  @Schema(description = "주문 ID", example = "1L")
  private final Long orderId;

  @Schema(description = "주문 수정 일시", example = "2023-10-05T14:30:00")
  private final LocalDateTime updatedAt;

  @Schema(description = "주문 상태", example = "ORDERED")
  private final OrderStatus orderStatus;

  @Schema(description = "총 주문 금액", example = "50000")
  private final Integer totalPrice;

  @Schema(description = "주문한 상품 목록")
  private final List<OrderProductDto> orderProducts;

  // 생성자
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
  @Schema(description = "주문 상품 정보 DTO를 생성하기 위한 내부 클래스")
  public static class OrderProductDto {

    private final String productName;
    private final long quantity;

    public OrderProductDto(OrderProduct orderProduct) {
      this.productName = orderProduct.getProduct().getProductName();
      this.quantity = orderProduct.getQuantity();
    }
  }
}
