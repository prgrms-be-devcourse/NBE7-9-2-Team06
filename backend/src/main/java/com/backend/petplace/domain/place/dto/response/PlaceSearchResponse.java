package com.backend.petplace.domain.place.dto.response;

import com.backend.petplace.domain.place.entity.Category2Type;
import com.backend.petplace.domain.place.projection.PlaceSearchRow;
import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceSearchResponse(
    @Schema(description = "장소 ID", example = "123") Long id,
    @Schema(description = "장소명", example = "행복동물병원") String name,
    @Schema(description = "중분류(검색 필터 주요 기준)", example = "VET_HOSPITAL", implementation = Category2Type.class)
    Category2Type category2,
    @Schema(description = "위도", example = "37.5665") double latitude,
    @Schema(description = "경도", example = "126.9780")double longitude,
    @Schema(description = "요청 좌표로부터 거리(미터)", example = "842", minimum = "0") int distanceMeters,
    @Schema(description = "평균 평점", example = "4.6", minimum = "0", maximum = "5") Double averageRating,
    @Schema(description = "주소", example = "서울특별시 중구 세종대로 110") String address
) {
  public static PlaceSearchResponse from(PlaceSearchRow row) {
    return new PlaceSearchResponse(
        row.getId(),
        row.getName(),
        Category2Type.valueOf(row.getCategory2()),
        row.getLatitude(),
        row.getLongitude(),
        row.getDistanceMeters(),
        row.getAverageRating(),
        row.getAddress()
    );
  }
}
