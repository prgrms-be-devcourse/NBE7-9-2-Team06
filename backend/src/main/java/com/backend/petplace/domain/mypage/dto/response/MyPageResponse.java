package com.backend.petplace.domain.mypage.dto.response;

import com.backend.petplace.domain.mypage.dto.MyPageUserInfo;
import com.backend.petplace.domain.mypage.dto.MyPageUserPets;
import com.backend.petplace.domain.mypage.dto.MyPageUserPoints;
import com.backend.petplace.domain.mypage.dto.MyPageUserReviews;
import com.backend.petplace.domain.pet.entity.Pet;
import com.backend.petplace.domain.point.entity.Point;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "마이페이지 조회용 API")
public class MyPageResponse {

  private final MyPageUserInfo userInfo;
  private final List<MyPageUserReviews> reviews;
  private final List<MyPageUserPoints> points;
  private final List<MyPageUserPets> pets;

  public MyPageResponse(MyPageUserInfo userInfo, List<MyPageUserReviews> reviews,
      List<MyPageUserPoints> points, List<MyPageUserPets> pets) {
    this.userInfo = userInfo;
    this.reviews = reviews;
    this.points = points;
    this.pets = pets;
  }


}
