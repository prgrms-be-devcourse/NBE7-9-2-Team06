package com.backend.petplace.domain.place;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import com.backend.petplace.domain.place.dto.response.PlaceDetailResponse;
import com.backend.petplace.domain.place.dto.response.PlaceSearchResponse;
import com.backend.petplace.domain.place.entity.Category1Type;
import com.backend.petplace.domain.place.entity.Category2Type;
import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.place.projection.PlaceSearchRow;
import com.backend.petplace.domain.place.repository.PlaceRepository;
import com.backend.petplace.domain.place.service.PlaceService;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

  @Mock
  private PlaceRepository placeRepository;

  @InjectMocks
  private PlaceService placeService;

  /**
   * 검색 쿼리 결과를 흉내 내기 위한 테스트용 구현체
   */
  private static class TestPlaceSearchRow implements PlaceSearchRow {
    private final Long id;
    private final String name;
    private final String category2;
    private final Double latitude;
    private final Double longitude;
    private final Integer distanceMeters;
    private final Double averageRating;
    private final String address;

    private TestPlaceSearchRow(
        Long id,
        String name,
        String category2,
        Double latitude,
        Double longitude,
        Integer distanceMeters,
        Double averageRating,
        String address
    ) {
      this.id = id;
      this.name = name;
      this.category2 = category2;
      this.latitude = latitude;
      this.longitude = longitude;
      this.distanceMeters = distanceMeters;
      this.averageRating = averageRating;
      this.address = address;
    }

    public static TestPlaceSearchRow of(
        Long id,
        String name,
        Category2Type category2,
        Double latitude,
        Double longitude,
        Integer distanceMeters,
        Double averageRating,
        String address
    ) {
      return new TestPlaceSearchRow(
          id,
          name,
          category2.name(),
          latitude,
          longitude,
          distanceMeters,
          averageRating,
          address
      );
    }

    @Override public Long getId() { return id; }
    @Override public String getName() { return name; }
    @Override public String getCategory2() { return category2; }
    @Override public Double getLatitude() { return latitude; }
    @Override public Double getLongitude() { return longitude; }
    @Override public Integer getDistanceMeters() { return distanceMeters; }
    @Override public Double getAverageRating() { return averageRating; }
    @Override public String getAddress() { return address; }
  }

  @Nested
  @DisplayName("searchPlaces()")
  class SearchPlaces {

    @Test
    @DisplayName("성공 - radiusKm, category2, keyword 모두 없으면 기본 반경(10km) + 필터 없이 검색")
    void searchPlaces_defaultRadius_noFilters() {
      // given
      double lat = 37.5665;
      double lon = 126.9780;
      Integer radiusKm = null;
      List<Category2Type> category2List = null;
      String keyword = null;

      // 리포지토리에서 반환해 줄 더미 Row
      PlaceSearchRow row1 = TestPlaceSearchRow.of(
          1L, "행복동물병원", Category2Type.VET_HOSPITAL,
          lat, lon, 500, 4.5, "서울 어딘가 1"
      );
      PlaceSearchRow row2 = TestPlaceSearchRow.of(
          2L, "즐거운카페", Category2Type.VET_HOSPITAL,
          lat, lon, 800, 4.0, "서울 어딘가 2"
      );

      given(placeRepository.searchWithinRadius(
          anyDouble(), anyDouble(),
          anyDouble(), anyDouble(),
          anyDouble(), anyDouble(),
          anyInt(),
          anyList(),
          anyInt(),
          any(),     // keyword
          anyInt(),
          anyInt()
      )).willReturn(List.of(row1, row2));

      // when
      List<PlaceSearchResponse> results = placeService.searchPlaces(
          lat, lon, radiusKm, category2List, keyword
      );

      // then: 결과 매핑 확인
      assertEquals(2, results.size());

      PlaceSearchResponse r1 = results.get(0);
      assertEquals(1L, r1.id());
      assertEquals("행복동물병원", r1.name());
      assertEquals(Category2Type.VET_HOSPITAL, r1.category2());
      assertEquals(500, r1.distanceMeters());
      assertEquals(4.5, r1.averageRating());
      assertEquals("서울 어딘가 1", r1.address());

      PlaceSearchResponse r2 = results.get(1);
      assertEquals(2L, r2.id());
      assertEquals("즐거운카페", r2.name());

      // then: 리포지토리에 전달된 파라미터 검증
      ArgumentCaptor<Double> latCaptor = ArgumentCaptor.forClass(Double.class);
      ArgumentCaptor<Double> lonCaptor = ArgumentCaptor.forClass(Double.class);
      ArgumentCaptor<Double> minLatCaptor = ArgumentCaptor.forClass(Double.class);
      ArgumentCaptor<Double> maxLatCaptor = ArgumentCaptor.forClass(Double.class);
      ArgumentCaptor<Double> minLonCaptor = ArgumentCaptor.forClass(Double.class);
      ArgumentCaptor<Double> maxLonCaptor = ArgumentCaptor.forClass(Double.class);
      ArgumentCaptor<Integer> radiusMetersCaptor = ArgumentCaptor.forClass(Integer.class);
      ArgumentCaptor<List<String>> cat2ListCaptor = ArgumentCaptor.forClass(List.class);
      ArgumentCaptor<Integer> cat2CountCaptor = ArgumentCaptor.forClass(Integer.class);
      ArgumentCaptor<String> keywordCaptor = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);
      ArgumentCaptor<Integer> offsetCaptor = ArgumentCaptor.forClass(Integer.class);

      verify(placeRepository).searchWithinRadius(
          latCaptor.capture(),
          lonCaptor.capture(),
          minLatCaptor.capture(),
          maxLatCaptor.capture(),
          minLonCaptor.capture(),
          maxLonCaptor.capture(),
          radiusMetersCaptor.capture(),
          cat2ListCaptor.capture(),
          cat2CountCaptor.capture(),
          keywordCaptor.capture(),
          limitCaptor.capture(),
          offsetCaptor.capture()
      );

      // 기본 반경 10km → 10,000m
      assertEquals(10_000, radiusMetersCaptor.getValue());
      // category2List 없으므로 빈 리스트 + count 0
      assertEquals(0, cat2ListCaptor.getValue().size());
      assertEquals(0, cat2CountCaptor.getValue());
      // keyword 없음 → null
      assertEquals(null, keywordCaptor.getValue());
      // limit / offset 기본값
      assertEquals(300, limitCaptor.getValue());
      assertEquals(0, offsetCaptor.getValue());
      // lat / lon 그대로 들어갔는지
      assertEquals(lat, latCaptor.getValue());
      assertEquals(lon, lonCaptor.getValue());
    }

    @Test
    @DisplayName("성공 - radiusKm가 MAX(30km)를 넘으면 30km로 캡핑, Category2/keyword 필터 정상 적용")
    void searchPlaces_radiusOverMax_andFilters() {
      // given
      double lat = 37.0;
      double lon = 127.0;
      Integer radiusKm = 50; // MAX_RADIUS_KM = 30 → 30km로 캡핑
      List<Category2Type> category2List = List.of(Category2Type.VET_HOSPITAL);
      String keyword = "  병원  "; // 앞뒤 공백 → trim 후 "병원"

      PlaceSearchRow row = TestPlaceSearchRow.of(
          1L, "병원A", Category2Type.VET_HOSPITAL,
          lat, lon, 1000, 4.2, "주소 A"
      );

      given(placeRepository.searchWithinRadius(
          anyDouble(), anyDouble(),
          anyDouble(), anyDouble(),
          anyDouble(), anyDouble(),
          anyInt(),
          anyList(),
          anyInt(),
          any(),
          anyInt(),
          anyInt()
      )).willReturn(List.of(row));

      // when
      List<PlaceSearchResponse> results = placeService.searchPlaces(
          lat, lon, radiusKm, category2List, keyword
      );

      // then: 매핑 확인
      assertEquals(1, results.size());
      assertEquals(1L, results.get(0).id());
      assertEquals("병원A", results.get(0).name());
      assertEquals(Category2Type.VET_HOSPITAL, results.get(0).category2());

      // then: 실제 전달된 파라미터 검증
      ArgumentCaptor<Integer> radiusMetersCaptor = ArgumentCaptor.forClass(Integer.class);
      ArgumentCaptor<List<String>> cat2ListCaptor = ArgumentCaptor.forClass(List.class);
      ArgumentCaptor<Integer> cat2CountCaptor = ArgumentCaptor.forClass(Integer.class);
      ArgumentCaptor<String> keywordCaptor = ArgumentCaptor.forClass(String.class);

      verify(placeRepository).searchWithinRadius(
          anyDouble(), anyDouble(),
          anyDouble(), anyDouble(),
          anyDouble(), anyDouble(),
          radiusMetersCaptor.capture(),
          cat2ListCaptor.capture(),
          cat2CountCaptor.capture(),
          keywordCaptor.capture(),
          anyInt(),
          anyInt()
      );

      // 30km로 캡핑 → 30,000m
      assertEquals(30_000, radiusMetersCaptor.getValue());
      // Category2Type → String name() 으로 변환
      List<String> cat2 = cat2ListCaptor.getValue();
      assertEquals(1, cat2.size());
      assertEquals(Category2Type.VET_HOSPITAL.name(), cat2.get(0));
      assertEquals(1, cat2CountCaptor.getValue());
      // keyword는 trim 후 들어가야 함
      assertEquals("병원", keywordCaptor.getValue());
    }

    @Test
    @DisplayName("성공 - keyword가 공백 문자열이면 NULL로 처리하여 이름 필터를 적용하지 않는다")
    void searchPlaces_blankKeyword_treatedAsNull() {
      // given
      double lat = 37.5;
      double lon = 127.1;
      Integer radiusKm = 5;
      List<Category2Type> category2List = List.of(Category2Type.VET_HOSPITAL);
      String keyword = "   "; // 공백만

      PlaceSearchRow row = TestPlaceSearchRow.of(
          1L, "병원B", Category2Type.VET_HOSPITAL,
          lat, lon, 1200, 4.7, "주소 B"
      );

      given(placeRepository.searchWithinRadius(
          anyDouble(), anyDouble(),
          anyDouble(), anyDouble(),
          anyDouble(), anyDouble(),
          anyInt(),
          anyList(),
          anyInt(),
          any(),
          anyInt(),
          anyInt()
      )).willReturn(List.of(row));

      // when
      placeService.searchPlaces(lat, lon, radiusKm, category2List, keyword);

      // then
      ArgumentCaptor<String> keywordCaptor = ArgumentCaptor.forClass(String.class);
      verify(placeRepository).searchWithinRadius(
          anyDouble(), anyDouble(),
          anyDouble(), anyDouble(),
          anyDouble(), anyDouble(),
          anyInt(),
          anyList(),
          anyInt(),
          keywordCaptor.capture(),
          anyInt(),
          anyInt()
      );

      // 공백 문자열 → null 로 전달되어야 함
      assertEquals(null, keywordCaptor.getValue());
    }
  }

  @Nested
  @DisplayName("getPlaceDetail()")
  class GetPlaceDetail {

    @Test
    @DisplayName("성공 - placeId로 조회 성공 시 PlaceDetailResponse로 매핑하여 반환")
    void getPlaceDetail_success() {
      // given
      Long placeId = 1L;

      Place place = Place.builder()
          .id(placeId)
          .uniqueKey("UNIQ_001")
          .name("행복동물병원")
          .category1(Category1Type.PET_MEDICAL)
          .category2(Category2Type.VET_HOSPITAL)
          .openingHours("월~금 09:00~18:00")
          .closedDays("토/일 휴무")
          .parking(true)
          .petAllowed(true)
          .petRestriction("소형견만")
          .tel("02-123-4567")
          .url("https://example.com")
          .postalCode("06236")
          .address("서울시 어딘가 1")
          .latitude(37.0)
          .longitude(127.0)
          .rawDescription("원본 설명")
          .averageRating(4.5)
          .totalReviewCount(10)
          .build();

      given(placeRepository.findById(placeId)).willReturn(Optional.of(place));

      // when
      PlaceDetailResponse response = placeService.getPlaceDetail(placeId);

      // then
      assertEquals(place.getId(), response.id());
      assertEquals(place.getName(), response.name());
      assertEquals(place.getCategory1(), response.category1());
      assertEquals(place.getCategory2(), response.category2());
      assertEquals(place.getOpeningHours(), response.openingHours());
      assertEquals(place.getClosedDays(), response.closedDays());
      assertEquals(place.getParking(), response.parking());
      assertEquals(place.getPetAllowed(), response.petAllowed());
      assertEquals(place.getPetRestriction(), response.petRestriction());
      assertEquals(place.getTel(), response.tel());
      assertEquals(place.getUrl(), response.url());
      assertEquals(place.getPostalCode(), response.postalCode());
      assertEquals(place.getAddress(), response.address());
      assertEquals(place.getLatitude(), response.latitude());
      assertEquals(place.getLongitude(), response.longitude());
      assertEquals(place.getAverageRating(), response.averageRating());
      assertEquals(place.getTotalReviewCount(), response.totalReviewCount());
      assertEquals(place.getRawDescription(), response.rawDescription());
    }

    @Test
    @DisplayName("실패 - placeId로 조회 실패 시 BusinessException(NOT_FOUND_PLACE) 발생")
    void getPlaceDetail_notFound() {
      // given
      Long placeId = 999L;
      given(placeRepository.findById(placeId)).willReturn(Optional.empty());

      // when & then
      BusinessException ex = assertThrows(
          BusinessException.class,
          () -> placeService.getPlaceDetail(placeId)
      );

      assertEquals(ErrorCode.NOT_FOUND_PLACE, ex.getErrorCode());
    }
  }
}
