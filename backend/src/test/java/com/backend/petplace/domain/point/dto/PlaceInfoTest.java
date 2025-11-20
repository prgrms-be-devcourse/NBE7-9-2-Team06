package com.backend.petplace.domain.point.dto;

import com.backend.petplace.domain.place.entity.Place;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PlaceInfoTest {

  private final Long PLACE_ID = 1L;
  private final String PLACE_NAME = "멍멍카페";
  private final String ADDRESS = "서울 강남구 테헤란로 123";

  @Test
  @DisplayName("fronProjection: Repository 결과 받아 정확히 DTO 생성")
  void testFrontProjection() {

    // when
    // 정적 팩토리 메서드 검증이므로 직접 호출 (builder 패턴 X)
    PlaceInfo info = PlaceInfo.fromProjection(PLACE_ID, PLACE_NAME, ADDRESS);

    //then
    assert info.getPlaceId().equals(PLACE_ID);
    assert info.getPlaceName().equals(PLACE_NAME);
    assert info.getFullAddress().equals(ADDRESS);
  }

  @Test
  @DisplayName("from: Place 엔티티 받아 정확히 DTO 생성")
  void testFromEntity() {

    // given
    Place mockPlace = Place.builder()
        .id(PLACE_ID)
        .name(PLACE_NAME)
        .address(ADDRESS)
        .build();

    // when
    PlaceInfo info = PlaceInfo.from(mockPlace);

    //then
    assert info.getPlaceId().equals(PLACE_ID);
    assert info.getPlaceName().equals(PLACE_NAME);
    assert info.getFullAddress().equals(ADDRESS);
  }
}
