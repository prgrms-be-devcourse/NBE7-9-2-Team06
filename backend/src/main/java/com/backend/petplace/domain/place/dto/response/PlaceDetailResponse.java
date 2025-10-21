package com.backend.petplace.domain.place.dto.response;

import com.backend.petplace.domain.place.entity.Category1Type;
import com.backend.petplace.domain.place.entity.Category2Type;
import com.backend.petplace.domain.place.entity.Place;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PlaceDetailResponse", description = "장소 상세 응답 DTO")
public record PlaceDetailResponse(
    @Schema(description = "장소 ID", example = "123") Long id,
    @Schema(description = "장소명", example = "행복동물병원") String name,
    @Schema(description = "대분류") Category1Type category1,
    @Schema(description = "중분류") Category2Type category2,
    @Schema(description = "영업시간", example = "월~금 09:00~18:00") String openingHours,
    @Schema(description = "휴무일", example = "매주 토/일, 공휴일") String closedDays,
    @Schema(description = "주차 가능") Boolean parking,
    @Schema(description = "반려동물 동반 가능") Boolean petAllowed,
    @Schema(description = "반려동물 제한", example = "야외만") String petRestriction,
    @Schema(description = "전화번호", example = "031-000-0000") String tel,
    @Schema(description = "웹사이트 URL", example = "https://www.naver.com/~") String url,
    @Schema(description = "우편번호", example = "06236") String postalCode,
    @Schema(description = "주소", example = "~시 ~구 ~로 65") String address,
    @Schema(description = "위도", example = "33.0000000") Double latitude,
    @Schema(description = "경도", example = "126.000000") Double longitude,
    @Schema(description = "평균 평점", example = "7") Double averageRating,
    @Schema(description = "리뷰 수", example = "70") Integer totalReviewCount,
    @Schema(description = "원본 설명", example = "운영시간:~|휴무일 :~|주차 불가|반려동물 동반가능|반려동물 제한사항 :~")
    String rawDescription
) {

  public static PlaceDetailResponse from(Place place) {
    return new PlaceDetailResponse(
        place.getId(),
        place.getName(),
        place.getCategory1(),
        place.getCategory2(),
        place.getOpeningHours(),
        place.getClosedDays(),
        place.getParking(),
        place.getPetAllowed(),
        place.getPetRestriction(),
        place.getTel(),
        place.getUrl(),
        place.getPostalCode(),
        place.getAddress(),
        place.getLatitude(),
        place.getLongitude(),
        place.getAverageRating(),
        place.getTotalReviewCount(),
        place.getRawDescription());
  }
}
