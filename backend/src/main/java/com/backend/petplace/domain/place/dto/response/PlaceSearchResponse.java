package com.backend.petplace.domain.place.dto.response;

import com.backend.petplace.domain.place.entity.Category2Type;
import com.backend.petplace.domain.place.projection.PlaceSearchRow;

public record PlaceSearchResponse(
    Long id,
    String name,
    Category2Type category2,
    double latitude,
    double longitude,
    int distanceMeters,
    Double averageRating,
    String address
) {
  public static PlaceSearchResponse from(PlaceSearchRow row) {
    return new PlaceSearchResponse(
        row.getId(),
        row.getName(),
        Category2Type.valueOf(row.getCategory2()), // 문자열 → Enum 변환
        row.getLatitude(),
        row.getLongitude(),
        row.getDistanceMeters(),
        row.getAverageRating(),
        row.getAddress()
    );
  }
}
