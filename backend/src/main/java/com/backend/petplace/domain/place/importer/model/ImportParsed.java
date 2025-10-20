package com.backend.petplace.domain.place.importer.model;

import com.backend.petplace.domain.place.entity.Category1Type;
import com.backend.petplace.domain.place.entity.Category2Type;

public record ImportParsed(
    String name,
    Category1Type category1,
    Category2Type category2,
    String openingHours,
    String closedDays,
    Boolean parking,
    Boolean petAllowed,
    String petRestriction,
    String tel,
    String url,
    String postalCode,
    String address,
    Double latitude,
    Double longitude,
    String rawDescription,
    String uniqueKey
) {}
