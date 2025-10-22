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

  public MyPageUserPoints(Long id, PointDescription description, int amount,
      LocalDateTime createdDate) {
    this.id = id;
    this.description = description;
    this.amount = amount;
    this.createdDate = createdDate;
  }
}
