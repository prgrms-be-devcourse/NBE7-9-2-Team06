package com.backend.petplace.domain.point.dto;

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

  @Schema(description = "장소 전체 주소 -  우편번호 제외", example = "서울 강남구 테헤란로 123")
  private String fullAddress;

  PlaceInfo(Long placeId, String placeName, String address) {
    this.placeId = placeId;
    this.placeName = placeName;
    this.fullAddress = address;
  }

  public static PlaceInfo fromProjection(Long placeId, String placeName, String address) {
    return new PlaceInfo(placeId, placeName, address);
  }

  public static PlaceInfo from(Place place) {
    return new PlaceInfo(
        place.getId(),
        place.getName(),
        place.getAddress()
    );
  }
}