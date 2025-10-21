package com.backend.petplace.domain.place.controller;

import com.backend.petplace.domain.place.dto.response.PlaceDetailResponse;
import com.backend.petplace.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;

@Tag(name = "Place", description = "장소 API")
public interface PlaceSpecification {

  @Operation(summary = "장소 상세 조회", description = "장소 ID로 상세 정보를 조회합니다.")
  ResponseEntity<ApiResponse<PlaceDetailResponse>> getPlaceDetail(
      @Parameter(in = ParameterIn.PATH, description = "장소 ID", required = true)
      @Positive Long placeId
  );

}
