package com.backend.petplace.domain.place.service;

import com.backend.petplace.domain.place.dto.response.PlaceDetailResponse;
import com.backend.petplace.domain.place.dto.response.PlaceSearchResponse;
import com.backend.petplace.domain.place.entity.Category2Type;
import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.place.projection.PlaceSearchRow;
import com.backend.petplace.domain.place.repository.PlaceRepository;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {

  private final PlaceRepository placeRepository;

  private static final int DEFAULT_RADIUS_KM = 10;
  private static final int MAX_RADIUS_KM = 30;
  private static final int DEFAULT_SIZE = 300;

  @Transactional(readOnly = true)
  public List<PlaceSearchResponse> searchPlaces(
      double lat, double lon,
      Integer radiusKm,
      List<Category2Type> category2List,
      String keyword
  ) {
    int rk = (radiusKm == null ? DEFAULT_RADIUS_KM : Math.min(radiusKm, MAX_RADIUS_KM));
    int radiusMeters = rk * 1_000;

    // 1) 바운딩 박스(BBox) 계산
    BBox box = bbox(lat, lon, rk);

    // 2) 카테고리2 문자열 목록
    List<String> cat2 = (category2List == null) ? List.of()
        : category2List.stream().map(Enum::name).toList();

    // 키워드 가공
    String normalizedKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

    // 3) 쿼리 호출
    List<PlaceSearchRow> rows = placeRepository.searchWithinRadius(
        lat, lon, box.minLat(), box.maxLat(), box.minLon(), box.maxLon(), radiusMeters, cat2, cat2.size(),
        normalizedKeyword, DEFAULT_SIZE, 0);

    return rows.stream()
        .map(PlaceSearchResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public PlaceDetailResponse getPlaceDetail(Long placeId) {
    Place place = placeRepository.findById(placeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));

    return PlaceDetailResponse.from(place);
  }

  private record BBox(double minLat, double maxLat, double minLon, double maxLon) {}

  // 바운딩박스 계산
  private static BBox bbox(double lat, double lon, double radiusKm) {
    double R = 6371.0; // km
    double dLat = Math.toDegrees(radiusKm / R);
    double dLon = Math.toDegrees(radiusKm / (R * Math.cos(Math.toRadians(lat))));
    return new BBox(lat - dLat, lat + dLat, lon - dLon, lon + dLon);
  }
}
