package com.backend.petplace.domain.place.controller;

import com.backend.petplace.domain.place.dto.response.PlaceDetailResponse;
import com.backend.petplace.domain.place.service.PlaceService;
import com.backend.petplace.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/places")
@RequiredArgsConstructor
public class PlaceController implements PlaceSpecification{

  private final PlaceService placeService;

  @GetMapping("/{placeId}")
  public ResponseEntity<ApiResponse<PlaceDetailResponse>> getPlaceDetail(@PathVariable Long placeId) {

    return ResponseEntity.ok(ApiResponse.success(placeService.getPlaceDetail(placeId)));
  }
}
