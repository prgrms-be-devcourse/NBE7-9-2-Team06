package com.backend.petplace.domain.review.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ReviewInfoTest {

  private ReviewInfo creatDummy() {
    return ReviewInfo.builder()
        .reviewId(1L)
        .userName("TestUser")
        .content("ReviewInfoDtoTest입니다.")
        .rating(4)
        .imageUrl("reviews/dummy-image-key.jpg") // S3 Path (기존 데이터)
        .createdDate(LocalDate.now())
        .build();
  }

  @Test
  @DisplayName("withFullImageUrl: S3 경로를 CloudFront URL로 교체")
  void withFullImageUrl() {

    // given
    ReviewInfo originalDto = creatDummy();
    String expectedCloudFrontUrl = "https://d123.cloudfront.net";
    String originalS3Path = originalDto.getImageUrl();
    String expectedFullImageUrl = expectedCloudFrontUrl + "/" + originalS3Path;

    // when
    ReviewInfo resultDto = ReviewInfo.withFullImageUrl(originalDto, expectedFullImageUrl);

    // then
    assertThat(resultDto).isNotSameAs(originalDto); // 새로운 객체인지 확인

    assertThat(resultDto.getReviewId()).isEqualTo(originalDto.getReviewId());
    assertThat(resultDto.getUserName()).isEqualTo(originalDto.getUserName());
    assertThat(resultDto.getContent()).isEqualTo(originalDto.getContent());
    assertThat(resultDto.getRating()).isEqualTo(originalDto.getRating());

    assertThat(resultDto.getImageUrl()).isEqualTo(expectedFullImageUrl); // URL이 교체되었는지 확인

    assertThat(resultDto.getCreatedDate()).isEqualTo(LocalDate.now());
  }

}
