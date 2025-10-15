package com.backend.petplace.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "리뷰 등록 요청 DTO")
public class ReviewCreateRequest {

  @Schema(description = "리뷰를 등록할 장소의 ID", example = "1")
  @NotBlank
  private Long placeId;

  @Schema(description = "리뷰 내용 (30자 이상)", example = "여기 분위기도 좋고 강아지가 정말 좋아했어요!")
  @NotBlank
  private String content;

  @Schema(description = "별점 (1~5)", example = "5")
  @NotBlank
  private int rating;

}
