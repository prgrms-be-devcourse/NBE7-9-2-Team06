package com.backend.petplace.domain.place.dto.response;

import com.backend.petplace.domain.place.entity.Category2Type;

public record PlaceSearchResponse(
    Long id,
    String name,
    Category2Type category2,
    double latitude,
    double longitude,
    int distanceMeters,
    Double averageRating,
    String address
) {}
