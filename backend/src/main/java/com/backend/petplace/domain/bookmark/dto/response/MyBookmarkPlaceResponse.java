package com.backend.petplace.domain.bookmark.dto.response;

import com.backend.petplace.domain.place.entity.Category1Type;
import com.backend.petplace.domain.place.entity.Category2Type;
import com.backend.petplace.domain.place.entity.Place;

public record MyBookmarkPlaceResponse(
    Long placeId,
    String name,
    Category1Type category1,
    Category2Type category2,
    String address,
    Double latitude,
    Double longitude,
    Boolean parking,
    Boolean petAllowed,
    Double averageRating,
    Integer totalReviewCount
) {
  public static MyBookmarkPlaceResponse from(Place place) {
    return new MyBookmarkPlaceResponse(
        place.getId(),
        place.getName(),
        place.getCategory1(),
        place.getCategory2(),
        place.getAddress(),
        place.getLatitude(),
        place.getLongitude(),
        place.getParking(),
        place.getPetAllowed(),
        place.getAverageRating(),
        place.getTotalReviewCount()
    );
  }
}
