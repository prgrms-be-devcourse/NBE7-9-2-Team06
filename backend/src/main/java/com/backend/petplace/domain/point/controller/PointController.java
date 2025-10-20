package com.backend.petplace.domain.point.controller;

import com.backend.petplace.domain.review.dto.response.PointHistoryResponse;
import com.backend.petplace.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class PointController {

  @GetMapping("/my/points")
  public ResponseEntity<ApiResponse<PointHistoryResponse>> getMyPointHistory() {

    // TODO: 현재 사용자의 포인트 적립 내역 조회 로직 구현

    return ResponseEntity.ok(ApiResponse.success(new PointHistoryResponse()));
  }
}
