package com.backend.petplace.domain.mypage.dto.response;

import com.backend.petplace.domain.mypage.dto.MyPageUserInfo;
import com.backend.petplace.domain.mypage.dto.MyPageUserPets;
import com.backend.petplace.domain.mypage.dto.MyPageUserPoints;
import com.backend.petplace.domain.review.dto.response.MyReviewResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@Schema(description = "마이페이지 조회용 API")
public class MyPageResponse {

  private final MyPageUserInfo userInfo;
  private final List<MyReviewResponse> reviews;
  private final List<MyPageUserPoints> points;
  private final List<MyPageUserPets> pets;

}
