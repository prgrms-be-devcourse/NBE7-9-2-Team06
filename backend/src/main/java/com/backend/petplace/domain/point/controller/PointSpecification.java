package com.backend.petplace.domain.point.controller;

import com.backend.petplace.domain.point.dto.response.PointHistoryResponse;
import com.backend.petplace.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Point", description = "포인트 관련 API")
public interface PointSpecification {

  @Operation(summary = "내 포인트 내역 조회", description = "현재 로그인한 사용자의 총 보유 포인트와 포인트 적립 내역을 조회합니다.")
  ResponseEntity<ApiResponse<PointHistoryResponse>> getMyPointHistory();
}
