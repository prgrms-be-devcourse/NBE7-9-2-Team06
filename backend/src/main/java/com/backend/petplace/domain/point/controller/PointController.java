package com.backend.petplace.domain.point.controller;

import com.backend.petplace.domain.point.dto.response.PointHistoryResponse;
import com.backend.petplace.domain.point.service.PointService;
import com.backend.petplace.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class PointController implements PointSpecification {

  private final PointService pointService;

  @GetMapping("/my/points")
  public ResponseEntity<ApiResponse<PointHistoryResponse>> getMyPointHistory() {

    // TODO: Spring Security 도입 후, 실제 인증된 사용자 정보 넘겨주기
    Long currentUserId = 1L;

    PointHistoryResponse response = pointService.getPointHistory(currentUserId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
