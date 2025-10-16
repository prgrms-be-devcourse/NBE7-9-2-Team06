package com.backend.petplace.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "리뷰 응답에 포함될 장소 요약 정보")
public class PlaceInfo {

  @Schema(description = "장소 ID", example = "1")
  private Long placeId;

  @Schema(description = "장소 이름", example = "멍멍카페")
  private String placeName;

  @Schema(description = "장소 전체 주소", example = "서울시 강남구 테헤란로 123, 1층")
  private String fullAddress;
}