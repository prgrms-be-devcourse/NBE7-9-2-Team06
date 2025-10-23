package com.backend.petplace.domain.mypage.dto;

import com.backend.petplace.domain.user.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageUserInfo {

  private final Long id;
  private final String nickname;
  private final String userEmail;
  private final LocalDateTime createdDate;
  private final String address;
  private final int point;
  private final int earnablePoints;
  private final int totalReviews;

  public static MyPageUserInfo from(User user, int earnablePoints, int totalReviews){ //dto 내부 조립이 필요하면 from을 씁시다
    return MyPageUserInfo.builder()
        .id(user.getId())
        .nickname(user.getNickName())
        .userEmail(user.getEmail())
        .createdDate(user.getCreatedDate())
        .address(user.getAddress() + " " + user.getAddressDetail())
        .point(user.getTotalPoint())
        .earnablePoints(earnablePoints)
        .totalReviews(totalReviews)
        .build();
  }
}
