package com.backend.petplace.domain.place.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Place", description = "장소 API")
public interface PlaceSpecification {

  @Operation(summary = "장소 상세 조회", description = "장소 ID로 상세 정보를 조회합니다.")
  ResponseEntity<ApiResponse<PlaceDetailResponse>> getPlaceDetail(
      @Parameter(in = ParameterIn.PATH, description = "장소 ID", required = true) Long id
  );

}
