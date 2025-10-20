package com.backend.petplace.domain.order.controller;

import com.backend.petplace.domain.order.dto.request.OrderCreateRequest;
import com.backend.petplace.domain.order.dto.response.OrderReadByIdResponse;
import com.backend.petplace.domain.order.service.OrderService;
import com.backend.petplace.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping(value = "/orders")
  public ResponseEntity<ApiResponse<Void>> createOrder(
      @RequestBody OrderCreateRequest request,
      @CookieValue("userId") Long userId
  ) {
    orderService.createOrder(request, userId);

    return ResponseEntity.ok(ApiResponse.success());
  }

  @GetMapping(value = "/orders")
  public ResponseEntity<ApiResponse<List<OrderReadByIdResponse>>> getOrderById(
      @CookieValue("userId") Long userId) {

    List<OrderReadByIdResponse> responses = orderService.getOrdersByUserId(userId);

    return ResponseEntity.ok(ApiResponse.success(responses));
  }

  @PatchMapping("/orders/{orderid}/cancel")
  public ResponseEntity<ApiResponse<Void>> cancelOrder(
      @PathVariable("orderid") Long orderId,
      @CookieValue("userId") Long userId

  ) {
    orderService.cancelOrder(userId, orderId);

    return ResponseEntity.ok(ApiResponse.success());
  }
}