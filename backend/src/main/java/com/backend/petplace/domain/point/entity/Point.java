package com.backend.petplace.domain.point.entity;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.user.entity.User;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

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

  @Builder
  public Point(Long id, User user, Place place, Review review, Integer amount,
      PointDescription description) {
    this.id = id;
    this.user = user;
    this.place = place;
    this.review = review;
    this.amount = amount;
    this.description = description;
  }
}
