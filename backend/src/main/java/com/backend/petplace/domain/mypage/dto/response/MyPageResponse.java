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

  public static MyPageResponse from(User user, List<Review> review, List<Point> point, List<Pet> pet, int earnablePoints){
    return MyPageResponse.builder()
        .userInfo(MyPageUserInfo.from(user, earnablePoints, review.size()))
        .reviews(review.stream().map(MyPageUserReviews::from).toList())
        .points(point.stream().map(MyPageUserPoints::from).toList())
        .pets(pet.stream().map(MyPageUserPets::from).toList())
        .build();
  }


}
