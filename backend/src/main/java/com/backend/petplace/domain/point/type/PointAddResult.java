package com.backend.petplace.domain.point.type;

import lombok.Getter;

@Getter
public enum PointAddResult {
  SUCCESS("포인트가 정상적으로 적립되었습니다."),
  ALREADY_AWARDED("이미 오늘 해당 장소의 리뷰 포인트를 지급받아, 포인트가 적립되지 않았습니다."),
  DAILY_LIMIT_EXCEEDED("일일 포인트 적립 한도를 초과하여, 포인트가 적립되지 않았습니다.");

  private String message;

  PointAddResult(String message) {
    this.message = message;
  }
}
