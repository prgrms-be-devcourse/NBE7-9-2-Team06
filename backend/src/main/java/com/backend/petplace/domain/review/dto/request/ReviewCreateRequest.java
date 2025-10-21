package com.backend.petplace.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Schema(description = "리뷰 등록 요청 DTO")
public class ReviewCreateRequest {

  @Schema(description = "리뷰를 등록할 장소의 ID", example = "1")
  @NotNull(message = "장소 ID는 필수입니다.")
  private Long placeId;

  @Schema(description = "리뷰 내용", example = "여기 분위기도 좋고 강아지가 정말 좋아했어요!")
  @NotBlank(message = "본문은 필수입니다.")
  @Length(min = 30, message = "본문은 30자 이상 작성 필수입니다.")
  private String content;

  @Schema(description = "별점 (1~5)", example = "5")
  @Range(min = 1, max = 5, message = "별점은 1점에서 5점 사이여야 합니다.")
  private int rating;

  @Schema(description = "S3에 업로드된 이미지 파일 경로", example = "reviews/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.jpg")
  private String s3ImagePath;

}
