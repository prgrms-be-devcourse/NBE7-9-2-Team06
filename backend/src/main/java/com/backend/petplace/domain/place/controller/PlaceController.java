package com.backend.petplace.domain.place.controller;

import com.backend.petplace.domain.place.dto.response.PlaceDetailResponse;
import com.backend.petplace.domain.place.dto.response.PlaceSearchResponse;
import com.backend.petplace.domain.place.entity.Category2Type;
import com.backend.petplace.domain.place.service.PlaceService;
import com.backend.petplace.global.response.ApiResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("api/v1/places")
@RequiredArgsConstructor
public class PlaceController implements PlaceSpecification {

  private final PlaceService placeService;

  @Override
  @GetMapping("/search")
  public ResponseEntity<ApiResponse<List<PlaceSearchResponse>>> searchPlaces(
      @RequestParam @NotNull Double lat, @RequestParam @NotNull Double lon,
      @RequestParam(required = false) Integer radiusKm,
      @RequestParam(required = false) List<Category2Type> category2,
      @RequestParam(required = false) String keyword
  ) {

    List<PlaceSearchResponse> results = placeService.searchPlaces(lat, lon, radiusKm, category2,
        keyword);

    return ResponseEntity.ok(ApiResponse.success(results));
  }

  @Override
  @GetMapping("/{placeId}")
  public ResponseEntity<ApiResponse<PlaceDetailResponse>> getPlaceDetail(
      @PathVariable @Positive Long placeId) {

    return ResponseEntity.ok(ApiResponse.success(placeService.getPlaceDetail(placeId)));
  }
}
