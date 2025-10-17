package com.backend.petplace.domain.point.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointDescription {
  REVIEW_PHOTO("사진 리뷰 작성"),
  REVIEW_TEXT("텍스트 리뷰 작성");

  private final String description;
}
