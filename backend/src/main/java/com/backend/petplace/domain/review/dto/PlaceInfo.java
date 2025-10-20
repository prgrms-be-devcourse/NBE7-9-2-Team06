package com.backend.petplace.domain.review.dto;

import com.backend.petplace.domain.place.entity.Place;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "리뷰 응답에 포함될 장소 요약 정보")
public class PlaceInfo {

  @Schema(description = "장소 ID", example = "1")
  private Long placeId;

  @Schema(description = "장소 이름", example = "멍멍카페")
  private String placeName;

  @Schema(description = "장소 전체 주소", example = "[06123] 서울 강남구 테헤란로 123")
  private String fullAddress;

  public PlaceInfo(Place place) {
    this.placeId = place.getId();
    this.placeName = place.getName();
    this.fullAddress = String.format("[%s] %s", place.getPostalCode(), place.getAddress());
  }
}