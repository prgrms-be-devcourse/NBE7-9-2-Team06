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
  private final String imageUrl;
  private final int rating;
  private final LocalDateTime createdDate;

  public MyPageUserReviews(Long id, String placename, String address, String content, String imageUrl, int rating,
      LocalDateTime createdDate) {
    this.id = id;
    this.placename = placename;
    this.address = address;
    this.content = content;
    this.imageUrl = imageUrl;
    this.rating = rating;
    this.createdDate = createdDate;
  }

}
