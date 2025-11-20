package com.backend.petplace.domain.point.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.point.type.PointPolicy;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.user.entity.User;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PointTest {

  private User createMockUser() {
    return User.builder()
        .id(1L)
        .email("test@example.com")
        .build();
  }

  private Place createMockPlace() {
    return Place.builder()
        .id(1L)
        .name("Test Place")
        .address("123 Test St")
        .build();
  }

  @Test
  @DisplayName("createFronReview: 사진 리뷰 등록 시 사진 리뷰 포인트 생성")
  void createFromReview_PhotoReview() {

    // given
    Review reviewWithImage = Review.builder()
        .user(createMockUser())
        .place(createMockPlace())
        .content("Great place!")
        .rating(5)
        .imageUrl("reviews/test-image.jpg")
        .build();

    // when
    Point point = Point.createFromReview(reviewWithImage);

    // then
    // 사진 리뷰 적립 확인
    assertThat(point.getAmount()).isEqualTo(PointPolicy.REVIEW_PHOTO_POINTS.getValue());

    // 적립 포인트 설명 확인
    assertThat(point.getDescription()).isEqualTo(PointDescription.REVIEW_PHOTO);

    // 필드 매핑 확인
    assertThat(point.getUser()).isEqualTo(reviewWithImage.getUser());
    assertThat(point.getPlace()).isEqualTo(reviewWithImage.getPlace());
    assertThat(point.getRewardDate()).isEqualTo(LocalDate.now());
  }

  @Test
  @DisplayName("createFromReview: 텍스트 리뷰 등록 시 텍스트 리뷰 포인트 생성")
  void createFromReview_TextReview() {

    // given
    Review reviewWithoutImage = Review.builder()
        .user(createMockUser())
        .place(createMockPlace())
        .content("Nice place!")
        .rating(4)
        .imageUrl(null)
        .build();

    // when
    Point point = Point.createFromReview(reviewWithoutImage);

    // then
    // 텍스트 리뷰 적립 확인
    assertThat(point.getAmount()).isEqualTo(PointPolicy.REVIEW_TEXT_POINTS.getValue());

    // 적립 포인트 설명 확인
    assertThat(point.getDescription()).isEqualTo(PointDescription.REVIEW_TEXT);
  }

  @Test
  @DisplayName("createFromReview: 공백 이미지 URL인 경우 텍스트 리뷰 포인트 생성")
  void createFromReview_BlankImageUrl() {
    // given
    Review reviewWithBlankImage = Review.builder()
        .user(createMockUser())
        .place(createMockPlace())
        .content("Average place.")
        .rating(3)
        .imageUrl("   ") // 공백 이미지 URL (빈 문자열 포함)
        .build();

    // when
    Point point = Point.createFromReview(reviewWithBlankImage);

    // then
    // 텍스트 리뷰 적립 확인
    assertThat(point.getAmount()).isEqualTo(PointPolicy.REVIEW_TEXT_POINTS.getValue());

    // 적립 포인트 설명 확인
    assertThat(point.getDescription()).isEqualTo(PointDescription.REVIEW_TEXT);
  }
}
