package com.backend.petplace.domain.review.controller;

import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_MEMBER;
import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_PLACE;

import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest;
import com.backend.petplace.domain.review.dto.response.MyReviewResponse;
import com.backend.petplace.domain.review.dto.response.PlaceReviewsResponse;
import com.backend.petplace.domain.review.dto.response.PresignedUrlResponse;
import com.backend.petplace.domain.review.dto.response.ReviewCreateResponse;
import com.backend.petplace.global.config.swagger.ApiErrorCodeExamples;
import com.backend.petplace.global.jwt.CustomUserDetails;
import com.backend.petplace.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Review", description = "리뷰 API")
public interface ReviewSpecification {

  @Operation(summary = "리뷰 이미지 Presigned URL 생성", description = "S3에 직접 이미지를 업로드할 수 있는 Presigned URL을 생성합니다.")
  ResponseEntity<ApiResponse<PresignedUrlResponse>> getPresignedUrl(
      @Parameter(description = "업로드할 원본 파일명", required = true) String filename
  );

  @ApiErrorCodeExamples({NOT_FOUND_MEMBER, NOT_FOUND_PLACE})
  @Operation(summary = "리뷰 등록", description = "특정 장소에 대한 리뷰를 등록합니다. 내용, 별점은 필수이며 이미지는 선택입니다. ✅ Presigned URL로 이미지 업로드 후, 리뷰 정보를 최종 저장합니다.")
  ResponseEntity<ApiResponse<ReviewCreateResponse>> createReview(
      @Parameter(description = "리뷰 정보 - 장소ID, 내용, 별점, S3 이미지 경로", required = true) ReviewCreateRequest request,
      CustomUserDetails userDetails
  );

  @ApiErrorCodeExamples({NOT_FOUND_PLACE})
  @Operation(summary = "장소 리뷰 목록 조회", description = "특정 장소에 등록된 모든 리뷰 목록과 별점 평균, 리뷰 총 개수를 조회합니다.")
  ResponseEntity<ApiResponse<PlaceReviewsResponse>> getReviewsByPlace(
      @Parameter(in = ParameterIn.PATH, description = "장소 ID", required = true) Long placeId
  );

  @ApiErrorCodeExamples({NOT_FOUND_MEMBER})
  @Operation(summary = "내 리뷰 목록 조회", description = "현재 로그인한 사용자가 작성한 모든 리뷰 목록을 조회합니다.")
  ResponseEntity<ApiResponse<List<MyReviewResponse>>> getMyReviews(CustomUserDetails userDetails);
}