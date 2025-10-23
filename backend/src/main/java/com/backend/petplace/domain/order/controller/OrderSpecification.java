package com.backend.petplace.domain.order.controller;

import static com.backend.petplace.global.response.ErrorCode.INVALID_ORDER_STATUS;
import static com.backend.petplace.global.response.ErrorCode.NOT_ENOUGH_POINT;
import static com.backend.petplace.global.response.ErrorCode.NOT_ENOUGH_STOCK;
import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_MEMBER;
import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_ORDER;
import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_PRODUCT;

import com.backend.petplace.domain.order.dto.request.OrderCreateRequest;
import com.backend.petplace.domain.order.dto.response.OrderReadByIdResponse;
import com.backend.petplace.global.config.swagger.ApiErrorCodeExamples;
import com.backend.petplace.global.jwt.CustomUserDetails;
import com.backend.petplace.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface OrderSpecification {
  @ApiErrorCodeExamples({NOT_ENOUGH_POINT, NOT_FOUND_PRODUCT, NOT_ENOUGH_STOCK})
  @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
  ResponseEntity<ApiResponse<Long>> createOrder(
      @Parameter(description = "가격 총액, 주문객체 리스트") OrderCreateRequest request,
      @Parameter(description = "JWT토큰에서 받은 유저 정보") CustomUserDetails userDetails);


  @Operation(summary = "사용자 주문 조회", description = "특정 사용자의 모든 주문을 조회합니다.")
  ResponseEntity<ApiResponse<List<OrderReadByIdResponse>>> getOrderById(
      @Parameter(description = "JWT토큰에서 받은 유저 정보") CustomUserDetails userDetails);

  @ApiErrorCodeExamples({NOT_FOUND_ORDER, INVALID_ORDER_STATUS})
  @Operation(summary = "주문 취소", description = "특정 주문을 취소 상태로 업데이트합니다.")
  ResponseEntity<ApiResponse<Void>> cancelOrder(
      @Parameter(description = "요청에서 받은 주문 아이디") Long orderId,
      @Parameter(description = "JWT토큰에서 받은 유저 정보") CustomUserDetails userDetails);

  @ApiErrorCodeExamples({NOT_FOUND_MEMBER})
  @Operation(summary = "사용자 포인트 조회", description = "특정 사용자의 현재 포인트 잔액을 조회합니다.")
  ResponseEntity<ApiResponse<Integer>> getUserPoints(
      @Parameter(description = "JWT토큰에서 받은 유저 정보") CustomUserDetails userDetails);
}
