package com.backend.petplace.domain.point.dto.response;

import com.backend.petplace.domain.point.dto.PointTransaction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "내 포인트 내역 응답 DTO")
public class PointHistoryResponse {

  @Schema(description = "현재 보유중인 총 포인트", example = "1500")
  private int totalPoints;

  @Schema(description = "포인트 적립/사용 내역 목록")
  private List<PointTransaction> history;

  public PointHistoryResponse(int totalPoints, List<PointTransaction> history) {
    this.totalPoints = totalPoints;
    this.history = history;
  }
}