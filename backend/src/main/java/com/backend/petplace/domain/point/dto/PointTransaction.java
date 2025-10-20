package com.backend.petplace.domain.point.dto;

import com.backend.petplace.domain.point.entity.Point;
import com.backend.petplace.domain.point.entity.PointDescription;
import com.backend.petplace.domain.review.dto.PlaceInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "개별 포인트 거래 내역 정보")
public class PointTransaction {

  @Schema(description = "포인트 내역 ID", example = "1")
  private Long pointId;

  @Schema(description = "포인트가 적립된 장소 정보")
  private PlaceInfo place;

  @Schema(description = "이미지 첨부 여부", example = "true")
  private boolean hasImage;

  @Schema(description = "적립 날짜", example = "2025-10-15")
  private LocalDate createdDate;

  @Schema(description = "적립된 포인트", example = "100")
  private int points;

  @Schema(description = "적립 설명", example = "사진 리뷰 작성")
  private String description;

  public static PointTransaction from(Point point) {
    PointTransaction transaction = new PointTransaction();
    transaction.pointId = point.getId();
    transaction.place = new PlaceInfo(point.getPlace());
    transaction.hasImage = point.getDescription() == PointDescription.REVIEW_PHOTO;
    transaction.createdDate = point.getRewardDate();
    transaction.points = point.getAmount();
    transaction.description = point.getDescription().getDescription();
    return transaction;
  }
}