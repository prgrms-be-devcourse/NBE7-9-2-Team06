package com.backend.petplace.domain.point.entity;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "point", uniqueConstraints = {
    @UniqueConstraint(name = "UK_point_review_id", columnNames = {"reviewId"}),
    @UniqueConstraint(name = "UK_point_user_place_date", columnNames = {"userId", "placeId",
        "rewardDate"})
})
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pointId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "placeId", nullable = false)
  private Place place;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reviewId")
  private Review review;

  @Column(nullable = false)
  private Integer amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PointDescription description;

  @Column(nullable = false)
  private LocalDate rewardDate;

  @Builder
  public Point(Long id, User user, Place place, Review review, Integer amount,
      PointDescription description, LocalDate rewardDate) {
    this.id = id;
    this.user = user;
    this.place = place;
    this.review = review;
    this.amount = amount;
    this.description = description;
    this.rewardDate = rewardDate;
  }

  private static final int POINTS_FOR_PHOTO_REVIEW = 100;
  private static final int POINTS_FOR_TEXT_REVIEW = 50;

  public static Point createFromReview(Review review) {
    boolean hasImage = (review.getImageUrl() != null && !review.getImageUrl().isBlank());

    int amount = hasImage ? POINTS_FOR_PHOTO_REVIEW : POINTS_FOR_TEXT_REVIEW;
    PointDescription description =
        hasImage ? PointDescription.REVIEW_PHOTO : PointDescription.REVIEW_TEXT;

    return Point.builder()
        .user(review.getUser())
        .place(review.getPlace())
        .review(review)
        .amount(amount)
        .description(description)
        .rewardDate(LocalDate.now())
        .build();
  }
}
