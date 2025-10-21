package com.backend.petplace.domain.orderproduct.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "주문 상품 생성 요청 DTO")
public class OrderProductCreateRequest {

  @Schema(description = "상품 ID", example = "1L")
  Long productId;

  @Schema(description = "상품 수량", example = "2")
  int quantity;
}
