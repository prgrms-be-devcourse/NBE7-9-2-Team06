package com.backend.petplace.domain.mypage.dto;

import com.backend.petplace.domain.point.entity.Point;
import com.backend.petplace.domain.point.entity.PointDescription;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageUserPoints {

  private final Long id;
  private final PointDescription description;
  private final int amount;
  private final LocalDateTime createdDate;

  public static MyPageUserPoints from(Point point){
    return MyPageUserPoints.builder()
        .id(point.getId())
        .description(point.getDescription())
        .amount(point.getAmount())
        .createdDate(point.getCreatedDate())
        .build();
  }
}
