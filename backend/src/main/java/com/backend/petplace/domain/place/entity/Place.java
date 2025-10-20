package com.backend.petplace.domain.place.entity;

import com.backend.petplace.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Place extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // uniqueKey = 장소명 + 우편번호 (중복되는 데이터가 생기는 걸 방지하기 위한 컬럼)
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
  private String openingHours;   // 예: "월~금 09:00~18:00"

  @Column(length = 300)
  private String closedDays;     // 예: "매주 토/일, 공휴일"

  private Boolean parking;

  private Boolean petAllowed;

  @Column(length = 300)
  private String petRestriction; // 예: "야외만", "목줄", "제한 없음"

  @Column(length = 30)
  private String tel;

  @Column(length = 500)
  private String url;

  @Column(length = 10)
  private String postalCode;     // 5자리 우편번호

  @Column(length = 300)
  private String address;        // 우편번호 제거한 주소 본문

  @Column(nullable = false)
  private Double latitude;       // 위도

  @Column(nullable = false)
  private Double longitude;      // 경도

  @Column(length = 1000)
  private String rawDescription; // 원본 description

  @Builder.Default
  private Double averageRating = 0.0;

  @Builder.Default
  private Integer totalReviewCount = 0;

  public void updateReviewStats(int newRating) {
    double totalScore = this.averageRating * this.totalReviewCount;
    this.totalReviewCount++;
    this.averageRating = (totalScore + newRating) / this.totalReviewCount;
  }

  // 업서트 업데이트 시 필드 동기화 메서드
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
