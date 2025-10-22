package com.backend.petplace.domain.point.controller;

import com.backend.petplace.domain.point.dto.response.PointHistoryResponse;
import com.backend.petplace.domain.point.service.PointService;
import com.backend.petplace.global.jwt.CustomUserDetails;
import com.backend.petplace.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class PointController implements PointSpecification {

  private final PointService pointService;

  @Override
  @GetMapping("/my/points")
  public ResponseEntity<ApiResponse<PointHistoryResponse>> getMyPointHistory(
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long currentUserId = userDetails.getUserId();

    PointHistoryResponse response = pointService.getPointHistory(currentUserId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
