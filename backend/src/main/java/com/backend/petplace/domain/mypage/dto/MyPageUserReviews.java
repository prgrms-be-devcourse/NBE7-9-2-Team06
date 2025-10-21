package com.backend.petplace.domain.mypage.dto;

import com.backend.petplace.domain.review.entity.Review;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageUserReviews {

  private final Long id;
  private final String placename;
  private final String address;
  private final String content;
  private final int rating;
  private final LocalDateTime createdDate;

  public static MyPageUserReviews from(Review review){
    return MyPageUserReviews.builder()
        .id(review.getId())
        .placename(review.getPlace().getName()) //fetch join으로 N+1 해결
        .address(review.getPlace().getAddress()) //fetch join으로 N+1 해결
        .content(review.getContent())
        .rating(review.getRating())
        .createdDate(review.getCreatedDate())
        .build();
  }

}
