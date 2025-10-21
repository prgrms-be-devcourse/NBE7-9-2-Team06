package com.backend.petplace.domain.point.type;

import lombok.Getter;

@Getter
public enum PointPolicy {

  DAILY_LIMIT(1000),
  REVIEW_PHOTO_POINTS(100),
  REVIEW_TEXT_POINTS(50);

  private final int value;

  PointPolicy(int value) {
    this.value = value;
  }
}
