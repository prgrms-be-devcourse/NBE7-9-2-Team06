package com.backend.petplace.domain.review.dto.response;

import com.backend.petplace.domain.review.dto.PlaceInfo;
import com.backend.petplace.domain.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "내 리뷰 목록 조회 응답 DTO")
public class MyReviewResponse {

  @Schema(description = "리뷰 ID", example = "10")
  private Long reviewId;

  @Schema(description = "리뷰가 달린 장소 정보")
  private PlaceInfo place;

  @Schema(description = "내가 등록한 별점", example = "4")
  private int rating;

  @Schema(description = "리뷰 내용", example = "다음에 또 방문하고 싶네요.")
  private String content;

  @Schema(description = "첨부한 이미지 URL", example = "https://s3...")
  private String imageUrl;

  @Schema(description = "리뷰 작성일", example = "2025-10-15")
  private LocalDate createdDate;

  @Schema(description = "해당 리뷰로 적립된 포인트", example = "100")
  private int pointsAwarded;

  public static MyReviewResponse from(Review review, int pointsAwarded) {
    MyReviewResponse response = new MyReviewResponse();
    response.reviewId = review.getId();
    response.place = new PlaceInfo(review.getPlace());
    response.rating = review.getRating();
    response.content = review.getContent();
    response.imageUrl = review.getImageUrl();
    response.createdDate = review.getCreatedDate().toLocalDate();
    response.pointsAwarded = pointsAwarded;
    return response;
  }
}