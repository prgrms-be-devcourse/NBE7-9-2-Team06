package com.backend.petplace.domain.bookmark.controller;

import static com.backend.petplace.global.response.ErrorCode.ALREADY_BOOKMARKED;
import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_BOOKMARK;
import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_MEMBER;
import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_PLACE;

import com.backend.petplace.global.config.swagger.ApiErrorCodeExamples;
import com.backend.petplace.global.jwt.CustomUserDetails;
import com.backend.petplace.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;

@Tag(name = "Bookmark", description = "북마크 API")
public interface BookmarkSpecification {

  @ApiErrorCodeExamples({NOT_FOUND_MEMBER, NOT_FOUND_PLACE, ALREADY_BOOKMARKED})
  @Operation(
      summary = "장소 북마크 추가",
      description = """
          로그인한 사용자가 특정 장소를 북마크합니다.
          - PathVariable `placeId`는 북마크할 장소의 ID입니다.
          - 이미 북마크된 경우 `ALREADY_BOOKMARKED` 예외가 발생합니다.
          """
  )
  ResponseEntity<ApiResponse<Long>> addBookmark(
      @Parameter(hidden = true)
      CustomUserDetails userDetails,

      @Parameter(
          in = ParameterIn.PATH,
          description = "북마크할 장소 ID",
          required = true,
          example = "1"
      )
      @Positive Long placeId
  );

  @ApiErrorCodeExamples({NOT_FOUND_MEMBER, NOT_FOUND_PLACE, NOT_FOUND_BOOKMARK})
  @Operation(
      summary = "장소 북마크 삭제",
      description = """
          로그인한 사용자가 특정 장소에 대해 등록한 북마크를 삭제합니다.
          - PathVariable `placeId`는 북마크를 삭제할 장소의 ID입니다.
          - 해당 사용자의 해당 장소 북마크가 없으면 `NOT_FOUND_BOOKMARK` 예외가 발생합니다.
          """
  )
  ResponseEntity<ApiResponse<Void>> removeBookmark(
      @Parameter(hidden = true)
      CustomUserDetails userDetails,

      @Parameter(
          in = ParameterIn.PATH,
          description = "북마크를 삭제할 장소 ID",
          required = true,
          example = "1"
      )
      @Positive Long placeId
  );
}

