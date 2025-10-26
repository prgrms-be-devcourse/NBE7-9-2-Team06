package com.backend.petplace.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "장소 리뷰 목록에 포함될 개별 리뷰 정보")
public class ReviewInfo {

  @Schema(description = "리뷰 ID", example = "10")
  private Long reviewId;

  @Schema(description = "작성자 이름", example = "멍멍이집사")
  private String userName;

  @Schema(description = "리뷰 내용", example = "정말 좋은 곳이었어요! 강아지가 좋아하네요.")
  private String content;

  @Schema(description = "별점", example = "5")
  private int rating;

  @Schema(description = "첨부 이미지 URL (없을 경우 null)", example = "https://s3.bucket/image.jpg")
  private String imageUrl;

  @Schema(description = "작성일", example = "2025-10-15")
  private LocalDate createdDate;

  public ReviewInfo(Long reviewId, String userName, String content, int rating, String imageUrl,
      LocalDateTime createdDate) {
    this.reviewId = reviewId;
    this.userName = userName;
    this.content = content;
    this.rating = rating;
    this.imageUrl = imageUrl;
    this.createdDate = createdDate.toLocalDate();
  }

  // S3 경로를 전체 URL로 교체하는 생성자
  private ReviewInfo(ReviewInfo dto, String fullImageUrl) {
    this.reviewId = dto.reviewId;
    this.userName = dto.userName;
    this.content = dto.content;
    this.rating = dto.rating;
    this.createdDate = dto.createdDate;
    this.imageUrl = fullImageUrl;
  }

  public static ReviewInfo withFullImageUrl(ReviewInfo dto, String fullImageUrl) {
    return new ReviewInfo(dto, fullImageUrl);
  }
}