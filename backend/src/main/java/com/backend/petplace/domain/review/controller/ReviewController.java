package com.backend.petplace.domain.review.controller;

import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest;
import com.backend.petplace.domain.review.dto.response.MyReviewResponse;
import com.backend.petplace.domain.review.dto.response.PlaceReviewsResponse;
import com.backend.petplace.domain.review.dto.response.PresignedUrlResponse;
import com.backend.petplace.domain.review.dto.response.ReviewCreateResponse;
import com.backend.petplace.domain.review.service.ReviewService;
import com.backend.petplace.domain.review.service.S3Service;
import com.backend.petplace.global.jwt.CustomUserDetails;
import com.backend.petplace.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController implements ReviewSpecification {

  private final ReviewService reviewService;
  private final S3Service s3Service;

  /**
   * 리뷰 이미지를 업로드할 Presigned URL을 요청하는 API
   */
  @Override
  @PostMapping("/presigned-url")
  public ResponseEntity<ApiResponse<PresignedUrlResponse>> getPresignedUrl(
      @RequestParam String filename) {

    PresignedUrlResponse response = s3Service.generatePresignedUrl("reviews", filename);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Override
  @PostMapping("/reviews")
  public ResponseEntity<ApiResponse<ReviewCreateResponse>> createReview(
      @Valid @RequestBody ReviewCreateRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long currentUserId = userDetails.getUserId();

    ReviewCreateResponse response = reviewService.createReview(currentUserId, request);
    return ResponseEntity.ok(ApiResponse.create(response));
  }

  @Override
  @GetMapping("/places/{placeId}/reviews")
  public ResponseEntity<ApiResponse<PlaceReviewsResponse>> getReviewsByPlace(
      @PathVariable Long placeId) {

    PlaceReviewsResponse response = reviewService.getReviewByPlace(placeId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Override
  @GetMapping("/my/reviews")
  public ResponseEntity<ApiResponse<List<MyReviewResponse>>> getMyReviews(
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    Long currentUserId = userDetails.getUserId();

    List<MyReviewResponse> myReviews = reviewService.getMyReviews(currentUserId);
    return ResponseEntity.ok(ApiResponse.success(myReviews));
  }
}