package com.backend.petplace.domain.order.controller;

import com.backend.petplace.domain.order.dto.request.OrderCreateRequest;
import com.backend.petplace.domain.order.dto.response.OrderReadByIdResponse;
import com.backend.petplace.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface OrderSpecification {

  @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
  ResponseEntity<ApiResponse<Void>> createOrder(
      @Parameter(description = "가격 총액, 주문객체 리스트") OrderCreateRequest request);

  @Operation(summary = "사용자 주문 조회", description = "특정 사용자의 모든 주문을 조회합니다.")
  ResponseEntity<ApiResponse<List<OrderReadByIdResponse>>> getOrderById();

  @Operation(summary = "주문 취소", description = "특정 주문을 취소 상태로 업데이트합니다.")
  ResponseEntity<ApiResponse<Void>> cancelOrder(
      @Parameter(description = "요청에서 받은 주문 아이디") Long orderId);

}
