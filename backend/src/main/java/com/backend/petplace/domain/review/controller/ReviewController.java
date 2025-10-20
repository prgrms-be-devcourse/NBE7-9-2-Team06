package com.backend.petplace.domain.review.controller;

import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest;
import com.backend.petplace.domain.review.dto.response.MyReviewResponse;
import com.backend.petplace.domain.review.dto.response.PlaceReviewsResponse;
import com.backend.petplace.domain.review.dto.response.PointHistoryResponse;
import com.backend.petplace.domain.review.dto.response.ReviewCreateResponse;
import com.backend.petplace.domain.review.service.ReviewService;
import com.backend.petplace.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController implements ReviewSpecification {

  private final ReviewService reviewService;

  @PostMapping(value = "/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<ReviewCreateResponse>> createReview(
      @Valid @RequestPart("request") ReviewCreateRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image) {

    // TODO: Spring Security 도입 후, 실제 인증된 사용자 정보 넘겨주기
    Long currentUserId = 1L;

    ReviewCreateResponse response = reviewService.createReview(currentUserId, request, image);
    return ResponseEntity.ok(ApiResponse.create(response));
  }

  @GetMapping("/places/{placeId}/reviews")
  public ResponseEntity<ApiResponse<PlaceReviewsResponse>> getReviewsByPlace(
      @PathVariable Long placeId) {

    PlaceReviewsResponse response = reviewService.getReviewByPlace(placeId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/my/reviews")
  public ResponseEntity<ApiResponse<List<MyReviewResponse>>> getMyReviews() {

    // TODO: Spring Security 도입 후, 실제 인증된 사용자 정보 넘겨주기
    Long currentUserId = 1L;

    List<MyReviewResponse> myReviews = reviewService.getMyReviews(currentUserId);
    return ResponseEntity.ok(ApiResponse.success(myReviews));
  }
}