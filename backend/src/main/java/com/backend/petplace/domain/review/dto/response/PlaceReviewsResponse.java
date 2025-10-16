package com.backend.petplace.domain.review.dto.response;

import com.backend.petplace.domain.review.dto.ReviewInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;

@Getter
@Schema(description = "장소 리뷰 목록 응답 DTO")
public class PlaceReviewsResponse {

  @Schema(description = "장소의 평균 별점", example = "4.5")
  private double averageRating;

  @Schema(description = "장소의 전체 리뷰 수", example = "120")
  private int totalReviewCount;

  @Schema(description = "리뷰 목록")
  private List<ReviewInfo> reviews;
}