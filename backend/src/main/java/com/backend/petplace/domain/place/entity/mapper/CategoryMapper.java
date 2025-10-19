package com.backend.petplace.domain.place.entity.mapper;

import static com.backend.petplace.domain.place.entity.Category1Type.*;

import com.backend.petplace.domain.place.entity.Category1Type;

public final class CategoryMapper {

  private CategoryMapper() {
  }

  public static Category1Type mapCategory1(String koLabel) {
    if (koLabel == null) {
      return ETC;
    }
    return switch (koLabel.trim()) {
      case "반려의료" -> PET_MEDICAL;
      case "반려동반여행" -> PET_TRAVEL;
      case "반려동물식당카페" -> PET_CAFE_RESTAURANT;
      case "반려동물 서비스" -> PET_SERVICE;
      default -> ETC;
    };
  }

}
