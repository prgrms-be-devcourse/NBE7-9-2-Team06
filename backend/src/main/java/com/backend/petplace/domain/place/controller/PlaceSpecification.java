package com.backend.petplace.domain.place.controller;

import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_PLACE;

import com.backend.petplace.domain.place.dto.response.PlaceDetailResponse;
import com.backend.petplace.domain.place.dto.response.PlaceSearchResponse;
import com.backend.petplace.domain.place.entity.Category2Type;
import com.backend.petplace.global.config.swagger.ApiErrorCodeExamples;
import com.backend.petplace.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Place", description = "장소 API")
public interface PlaceSpecification {

  @Operation(summary = "장소 검색",
      description = """
          사용자의 위치를 기준으로 반경 내의 장소를 검색합니다.
          - `lat`, `lon`은 필수입니다.
          - `radiusKm`은 선택이며 기본값은 10km입니다.
          - `category2`는 다중 선택이 가능하며 예: `?category2=CAFE&category2=VET_HOSPITAL`
          - `keyword`는 키워드 검색어(장소명 포함 검색)입니다.
          - 현재 정렬은 거리순 정렬로 고정되어 있습니다.
          """
  )
  ResponseEntity<ApiResponse<List<PlaceSearchResponse>>> searchPlaces(
      @Parameter(in = ParameterIn.QUERY, description = "사용자 위도", required = true, example = "37.5665")
      @NotNull Double lat,

      @Parameter(in = ParameterIn.QUERY, description = "사용자 경도", required = true, example = "126.9780")
      @NotNull Double lon,

      @Parameter(in = ParameterIn.QUERY, description = "검색 반경 (단위: km), 기본값 10km", example = "10")
      Integer radiusKm,

      @Parameter(
          in = ParameterIn.QUERY,
          description = "카테고리2 (중분류), 다중 선택 가능",
          array = @ArraySchema(schema = @Schema(implementation = Category2Type.class))
      )
      List<Category2Type> category2,

      @Parameter(in = ParameterIn.QUERY, description = "검색어 (장소명 부분 일치)", example = "동물병원")
      String keyword
  );

  @ApiErrorCodeExamples({NOT_FOUND_PLACE})
  @Operation(summary = "장소 상세 조회", description = "장소 ID로 상세 정보를 조회합니다.")
  ResponseEntity<ApiResponse<PlaceDetailResponse>> getPlaceDetail(
      @Parameter(in = ParameterIn.PATH, description = "장소 ID", required = true)
      @Positive Long placeId
  );

}
