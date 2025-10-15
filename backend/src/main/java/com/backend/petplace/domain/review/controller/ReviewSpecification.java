package com.backend.petplace.domain.review.controller;

import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest;
import com.backend.petplace.domain.review.dto.response.MyReviewResponse;
import com.backend.petplace.domain.review.dto.response.PlaceReviewsResponse;
import com.backend.petplace.domain.review.dto.response.PointHistoryResponse;
import com.backend.petplace.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Review", description = "리뷰 API")
public interface ReviewSpecification {

    @Operation(summary = "리뷰 등록", description = "특정 장소에 대한 리뷰를 등록합니다. 내용, 별점은 필수이며 이미지는 선택입니다.")
    ResponseEntity<ApiResponse<Void>> createReview(
            @Parameter(description = "리뷰 정보", required = true) ReviewCreateRequest request,
            @Parameter(description = "업로드할 이미지 파일 (선택)") MultipartFile image
    );

    @Operation(summary = "장소 리뷰 목록 조회", description = "특정 장소에 등록된 모든 리뷰 목록과 별점 평균, 리뷰 총 개수를 조회합니다.")
    ResponseEntity<ApiResponse<PlaceReviewsResponse>> getReviewsByPlace(
            @Parameter(in = ParameterIn.PATH, description = "장소 ID", required = true) Long placeId
    );

    @Operation(summary = "내 리뷰 목록 조회", description = "현재 로그인한 사용자가 작성한 모든 리뷰 목록을 조회합니다.")
    ResponseEntity<ApiResponse<List<MyReviewResponse>>> getMyReviews();

    @Operation(summary = "내 포인트 내역 조회", description = "현재 로그인한 사용자의 총 보유 포인트와 포인트 적립 내역을 조회합니다.")
    ResponseEntity<ApiResponse<PointHistoryResponse>> getMyPointHistory();
}