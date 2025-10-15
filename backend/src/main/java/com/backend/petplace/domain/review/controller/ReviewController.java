package com.backend.petplace.domain.review.controller;

import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest;
import com.backend.petplace.domain.review.dto.response.MyReviewResponse;
import com.backend.petplace.domain.review.dto.response.PlaceReviewsResponse;
import com.backend.petplace.domain.review.dto.response.PointHistoryResponse;
import com.backend.petplace.global.response.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController implements ReviewSpecification {

  @PostMapping(value = "/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<Void>> createReview(
      @RequestPart("request") ReviewCreateRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image) {

    // TODO: 리뷰 생성 및 포인트 적립 로직 구현

    return ResponseEntity.ok(ApiResponse.success());
  }

  @GetMapping("/places/{placeId}/reviews")
  public ResponseEntity<ApiResponse<PlaceReviewsResponse>> getReviewsByPlace(
      @PathVariable Long placeId) {

    // TODO: 장소별 리뷰 데이터 조회 로직 구현

    return ResponseEntity.ok(ApiResponse.success(new PlaceReviewsResponse()));
  }

  @GetMapping("/my/reviews")
  public ResponseEntity<ApiResponse<List<MyReviewResponse>>> getMyReviews() {

    // TODO: 현재 사용자의 리뷰 목록 조회 로직 구현

    return ResponseEntity.ok(ApiResponse.success(List.of(new MyReviewResponse())));
  }

  @GetMapping("/my/points")
  public ResponseEntity<ApiResponse<PointHistoryResponse>> getMyPointHistory() {

    // TODO: 현재 사용자의 포인트 적립 내역 조회 로직 구현

    return ResponseEntity.ok(ApiResponse.success(new PointHistoryResponse()));
  }
}