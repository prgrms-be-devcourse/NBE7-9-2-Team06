package com.backend.petplace.domain.order.controller;

import com.backend.petplace.domain.order.dto.request.OrderCreateRequest;
import com.backend.petplace.domain.order.dto.response.OrderReadByIdResponse;
import com.backend.petplace.domain.order.service.OrderService;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.jwt.CustomUserDetails;
import com.backend.petplace.global.response.ApiResponse;
import com.backend.petplace.global.response.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
  public ResponseEntity<ApiResponse<Void>> createOrder(
      @RequestBody OrderCreateRequest request) {

    orderService.createOrder(request, getUserIdFromJWTToken());
    return ResponseEntity.ok(ApiResponse.success());
  }

  @Override
  @GetMapping()
  public ResponseEntity<ApiResponse<List<OrderReadByIdResponse>>> getOrderById() {

    List<OrderReadByIdResponse> responses = orderService.getOrdersByUserId(getUserIdFromJWTToken());
    return ResponseEntity.ok(ApiResponse.success(responses));
  }

  @Override
  @PatchMapping("/{orderid}/cancel")
  public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable("orderid") Long orderId) {

    orderService.cancelOrder(getUserIdFromJWTToken(), orderId);
    return ResponseEntity.ok(ApiResponse.success());
  }

  private Long getUserIdFromJWTToken() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null ||
        !(authentication.getPrincipal() instanceof CustomUserDetails)) {
      throw new BusinessException(ErrorCode.NOT_FOUND_MEMBER);
    }
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    return userDetails.getUserId();
  }
}