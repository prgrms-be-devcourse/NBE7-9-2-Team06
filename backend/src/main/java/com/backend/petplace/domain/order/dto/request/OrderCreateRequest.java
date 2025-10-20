package com.backend.petplace.domain.order.dto.request;

import com.backend.petplace.domain.orderproduct.dto.request.OrderProductCreateRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;

@Getter
@Schema(description = "주문 생성 요청 DTO")
public class OrderCreateRequest {
  private Long totalPrice;
  private List<OrderProductCreateRequest> orderProducts;
}
