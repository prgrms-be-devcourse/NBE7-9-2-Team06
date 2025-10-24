package com.backend.petplace.domain.place.entity;

import com.backend.petplace.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "place",
    indexes = {
        @Index(name = "idx_place_lat", columnList = "latitude"),
        @Index(name = "idx_place_lon", columnList = "longitude"),
        @Index(name = "idx_place_category2", columnList = "category2"),
        @Index(name = "idx_place_name", columnList = "name")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Place extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100, unique = true)
  private String uniqueKey;

  @Column(nullable = false, length = 200)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private Category1Type category1;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private Category2Type category2;

  @Column(length = 500)
  private String openingHours;

  @Column(length = 300)
  private String closedDays;

  private Boolean parking;

  private Boolean petAllowed;

  @Column(length = 300)
  private String petRestriction;

  @Column(length = 30)
  private String tel;

  @Column(length = 500)
  private String url;

  @Column(length = 10)
  private String postalCode;

  @Column(length = 300)
  private String address;

  @Column(nullable = false)
  private Double latitude;

  @Column(nullable = false)
  private Double longitude;

  @Column(length = 1000)
  private String rawDescription;

  @Builder.Default
  private Double averageRating = 0.0;

  @Builder.Default
  private Integer totalReviewCount = 0;

  public void updateReviewStats(int newRating) {
    double totalScore = this.averageRating * this.totalReviewCount;
    this.totalReviewCount++;
    this.averageRating = (totalScore + newRating) / this.totalReviewCount;
  }

  public void apply(String name, Category1Type c1, Category2Type c2, String openingHours,
      String closedDays, Boolean parking, Boolean petAllowed, String petRestriction,
      String tel, String url, String postalCode, String address, Double lat, Double lng,
      String rawDescription
  ) {
    this.name = name;
    this.category1 = c1;
    this.category2 = c2;
    this.openingHours = openingHours;
    this.closedDays = closedDays;
    this.parking = parking;
    this.petAllowed = petAllowed;
    this.petRestriction = petRestriction;
    this.tel = tel;
    this.url = url;
    this.postalCode = postalCode;
    this.address = address;
    this.latitude = lat;
    this.longitude = lng;
    this.rawDescription = rawDescription;
  }
}
