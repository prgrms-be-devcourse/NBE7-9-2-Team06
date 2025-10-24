package com.backend.petplace.domain.order.controller;

import com.backend.petplace.domain.order.dto.request.OrderCreateRequest;
import com.backend.petplace.domain.order.dto.response.OrderReadByIdResponse;
import com.backend.petplace.domain.order.service.OrderService;
import com.backend.petplace.global.jwt.CustomUserDetails;
import com.backend.petplace.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController implements OrderSpecification {

  private final OrderService orderService;

  @Override
  @PostMapping()
  public ResponseEntity<ApiResponse<Long>> createOrder(
      @RequestBody OrderCreateRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long userId = userDetails.getUserId();
    Long orderId = orderService.createOrder(request, userId);
    return ResponseEntity.ok(ApiResponse.success(orderId));
  }

  @Override
  @GetMapping()
  public ResponseEntity<ApiResponse<List<OrderReadByIdResponse>>> getOrderById(
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long userId = userDetails.getUserId();
    List<OrderReadByIdResponse> responses = orderService.getOrdersByUserId(userId);
    return ResponseEntity.ok(ApiResponse.success(responses));
  }

  @Override
  @PatchMapping("/{orderid}/cancel")
  public ResponseEntity<ApiResponse<Void>> cancelOrder(
      @PathVariable("orderid") Long orderId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long userId = userDetails.getUserId();
    orderService.cancelOrder(userId, orderId);
    return ResponseEntity.ok(ApiResponse.success());
  }

  @Override
  @GetMapping("/points")
  public ResponseEntity<ApiResponse<Integer>> getUserPoints(@AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails.getUserId();
    Integer points = orderService.getUserPoints(userId);
    return ResponseEntity.ok(ApiResponse.success(points));
  }
}