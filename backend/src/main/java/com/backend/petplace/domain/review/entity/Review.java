package com.backend.petplace.domain.review.entity;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.point.entity.Point;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "reviewId")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "placeId", nullable = false)
  private Place place;

  @OneToOne(mappedBy = "review", fetch = FetchType.LAZY)
  private Point point;

  @Column(nullable = false)
  private int rating;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  private String imageUrl;

  @Builder
  public Review(Long id, User user, Place place, String content, int rating, String imageUrl) {
    this.id = id;
    this.user = user;
    this.place = place;
    this.content = content;
    this.rating = rating;
    this.imageUrl = imageUrl;
  }

  public static Review createNewReview(User user, Place place, String content, int rating,
      String imageUrl) {
    return Review.builder()
        .user(user)
        .place(place)
        .content(content)
        .rating(rating)
        .imageUrl(imageUrl)
        .build();
  }
}
