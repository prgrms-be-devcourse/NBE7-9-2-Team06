package com.backend.petplace.domain.place.entity.mapper;

import static com.backend.petplace.domain.place.entity.Category1Type.*;
import static com.backend.petplace.domain.place.entity.Category2Type.*;

import com.backend.petplace.domain.place.entity.Category1Type;
import com.backend.petplace.domain.place.entity.Category2Type;

public final class CategoryMapper {

  private CategoryMapper() {
  }

  public static Category1Type mapCategory1(String koLabel) {
    if (koLabel == null) {
      return Category1Type.ETC;
    }
    return switch (koLabel.trim()) {
      case "반려의료" -> PET_MEDICAL;
      case "반려동반여행" -> PET_TRAVEL;
      case "반려동물식당카페" -> PET_CAFE_RESTAURANT;
      case "반려동물 서비스" -> PET_SERVICE;
      default -> Category1Type.ETC;
    };
  }

  public static Category2Type mapCategory2(String koLabel) {
    if (koLabel == null) {
      return Category2Type.ETC;
    }
    return switch (koLabel.trim()) {
      case "동물약국" -> VET_PHARMACY;
      case "박물관" -> MUSEUM;
      case "카페" -> CAFE;
      case "동물병원" -> VET_HOSPITAL;
      case "반려동물용품" -> PET_SUPPLIES;
      case "미용" -> GROOMING;
      case "문예회관" -> ART_CENTER;
      case "펜션" -> PENSION;
      case "식당" -> RESTAURANT;
      case "여행지" -> DESTINATION;
      case "위탁관리" -> DAYCARE;
      case "미술관" -> ART_MUSEUM;
      default -> Category2Type.ETC;
    };
  }

}
