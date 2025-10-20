package com.backend.petplace.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "리뷰 등록 응답 DTO")
public class ReviewCreateResponse {

  @Schema(description = "등록된 리뷰 ID", example = "10")
  private Long reviewId;

  @Schema(description = "포인트 적립 안내를 알리는 메시지", example = "일일 포인트 적립 한도를 초과하여, 포인트가 적립되지 않았습니다.")
  private String pointResultMessage;

}
