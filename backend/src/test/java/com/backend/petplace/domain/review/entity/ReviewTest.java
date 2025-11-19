package com.backend.petplace.domain.review.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ReviewTest {

  @Test
  @DisplayName("createNewReview: 필수 필드를 포함하여 새로운 Review 엔티티 생성")
  void createNewReview() {
    
    // given
    User mockUser = User.builder()
        .nickName("TestUser")
        .password("password")
        .build();

    Place mockPlace = Place.builder()
        .id(1L)
        .name("TestPlace")
        .address("123 Test St")
        .build();

    int rating = 5;
    String content = "This is a test review.";
    String imageUrl = "reviews/test-image.jpg";

  // when
    Review newReview = Review.createNewReview(mockUser, mockPlace, content, rating, imageUrl);

    // then
    assertThat(newReview).isNotNull();
    assertThat(newReview.getId()).isNull(); // 아직 저장되지 않았으므로 ID는 null이어야 함

    assertThat(newReview.getUser()).isEqualTo(mockUser);
    assertThat(newReview.getPlace()).isEqualTo(mockPlace);
    assertThat(newReview.getContent()).isEqualTo(content);
    assertThat(newReview.getRating()).isEqualTo(rating);
    assertThat(newReview.getImageUrl()).isEqualTo(imageUrl);
  }
}
