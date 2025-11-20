package com.backend.petplace.domain.mypage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.backend.petplace.domain.mypage.dto.MyPageUserPets;
import com.backend.petplace.domain.mypage.dto.MyPageUserPoints;
import com.backend.petplace.domain.mypage.dto.response.MyPageResponse;
import com.backend.petplace.domain.pet.entity.Gender;
import com.backend.petplace.domain.pet.repository.PetRepository;
import com.backend.petplace.domain.point.entity.PointDescription;
import com.backend.petplace.domain.point.repository.PointRepository;
import com.backend.petplace.domain.review.dto.response.MyReviewResponse;
import com.backend.petplace.domain.review.repository.ReviewRepository;
import com.backend.petplace.domain.review.service.S3Service;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

@ExtendWith(MockitoExtension.class) //mockito를 통해 테스트
class MyPageServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PointRepository pointRepository;
  @Mock private ReviewRepository reviewRepository;
  @Mock private PetRepository petRepository;
  @Mock private S3Service s3Service;

  @InjectMocks //mock 객체에 필요한 의존성 주입
  private MyPageService myPageService;

  private User user;

  @Value("${cloud.aws.cloudfront.domain}")
  private String cloudFrontDomain;

  @BeforeEach
  void init() {
    user = User.builder()
        .id(1L)
        .nickName("user1")
        .email("user1@example.com")
        .password("pwd")
        .address("주소")
        .zipcode("11111")
        .build();
  }

  @Test
  @DisplayName("마이페이지 조회 - earnablePoints 계산 및 DTO 조립 검증")
  void myPageTest() {
    // given
    given(userRepository.findById(1L)).willReturn(Optional.of(user));

    //오늘 획득한 포인트
    given(pointRepository.findTodaysPointsSumByUser(eq(user), any()))
        .willReturn(900);

    // 포인트 내역
    given(pointRepository.findMyPagePointHistory(user))
        .willReturn(List.of(new MyPageUserPoints(1L, PointDescription.REVIEW_PHOTO, 100, LocalDateTime.now())));

    // 리뷰 + S3 URL
    MyReviewResponse review = new MyReviewResponse(1L, 1L, "장소 이름", "주소", 5, "30자 적은 리뷰 글", "image.png", LocalDateTime.now(), 100);
    given(reviewRepository.findMyReviewsWithProjection(user))
        .willReturn(List.of(review));

    //url 조합
    given(s3Service.getPublicUrl("image.png"))
        .willReturn(cloudFrontDomain + "/image.png");

    // 펫 목록
    given(petRepository.findByUserWithActivatedPet(user))
        .willReturn(List.of(new MyPageUserPets(1L, "뚜뚜", Gender.Female, LocalDate.of(2021, 1, 1), "말티즈")));

    // when
    MyPageResponse response = myPageService.myPage(1L);

    // then
    // 유저 정보
    assertThat(1L).isEqualTo(response.getUserInfo().getId()); //유저 아이디
    assertThat(100).isEqualTo(response.getUserInfo().getEarnablePoints()); // 오늘 획득 가능한 남은 포인트 계산 1000 - 900
    assertThat(1).isEqualTo(response.getReviews().size()); //등록한 리뷰 수

    // 리뷰
    assertThat(1L).isEqualTo(response.getReviews().getFirst().getReviewId()); //첫 번째 리뷰 아이디
    assertThat(1L).isEqualTo(response.getReviews().getFirst().getPlace().getPlaceId()); //첫 번째 리뷰 아이디의 장소 id
    assertThat(100).isEqualTo(response.getReviews().getFirst().getPointsAwarded()); //첫 번째 리뷰에서 얻은 포인트
    assertThat(cloudFrontDomain + "/image.png").isEqualTo(response.getReviews().getFirst().getImageUrl()); //첫 번째 리뷰 이미지

    //반려동물
    assertThat(1).isEqualTo(response.getPets().size()); //반려동물 등록 수
    assertThat(1L).isEqualTo(response.getPets().getFirst().getId()); //아이디 확인
    assertThat("뚜뚜").isEqualTo(response.getPets().getFirst().getName()); //이름 확인
  }
}
