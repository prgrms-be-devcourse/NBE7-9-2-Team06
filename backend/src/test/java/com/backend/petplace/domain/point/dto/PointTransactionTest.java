package com.backend.petplace.domain.point.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.petplace.domain.point.entity.PointDescription;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PointTransactionTest {

  private final Long POINT_ID = 1L;
  private final Long PLACE_ID = 2L;
  private final String PLACE_NAME = "멍멍카페";
  private final String ADDRESS = "서울 강남구 테헤란로 123";
  private final LocalDate REWARD_DATE = LocalDate.of(2025, 11, 19);
  private final Integer POINT_AMOUNT = 100;

  @Test
  @DisplayName("사진 리뷰 등록 시 hasImage = true")
  void testHasImageTrueForPhotoReview() {

    // given
    PointDescription photoDesc = PointDescription.REVIEW_PHOTO;

    // when
    PointTransaction transaction = new PointTransaction(
        POINT_ID,
        PLACE_ID,
        PLACE_NAME,
        ADDRESS,
        photoDesc,
        REWARD_DATE,
        POINT_AMOUNT
    );

    // then
    // hasImage 필드 검증
    assertThat(transaction.isHasImage()).isTrue();

    // description 필드 검증
    assertThat(transaction.getDescription()).isEqualTo("사진 리뷰 작성");

    assertThat(transaction.getPlace().getPlaceId()).isEqualTo(PLACE_ID);
  }

  @Test
  @DisplayName("텍스트 리뷰 등록 시 hasImage = false")
  void testHasImageFalseForTextReview() {
    // given
    PointDescription textDesc = PointDescription.REVIEW_TEXT;

    // when
    PointTransaction transaction = new PointTransaction(
        POINT_ID,
        PLACE_ID,
        PLACE_NAME,
        ADDRESS,
        textDesc,
        REWARD_DATE,
        POINT_AMOUNT
    );

    // then
    // hasImage 필드 검증
    assertThat(transaction.isHasImage()).isFalse();

    // description 필드 검증
    assertThat(transaction.getDescription()).isEqualTo("텍스트 리뷰 작성");

    assertThat(transaction.getPoints()).isEqualTo(POINT_AMOUNT);
    assertThat(transaction.getCreatedDate()).isEqualTo(REWARD_DATE);
  }
}
