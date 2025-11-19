package com.backend.petplace.domain.review.service;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.place.repository.PlaceRepository;
import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest;
import com.backend.petplace.domain.review.dto.ReviewInfo;
import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest;
import com.backend.petplace.domain.review.dto.response.MyReviewResponse;
import com.backend.petplace.domain.review.dto.response.ReviewCreateResponse;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.review.repository.ReviewRepository;
import com.backend.petplace.domain.point.service.PointService;
import com.backend.petplace.domain.point.type.PointAddResult;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.domain.review.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @InjectMocks
  private ReviewService reviewService;

  @Mock
  private PointService pointService;

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PlaceRepository placeRepository;

  @Mock
  private S3Service s3Service;

  private User mockUser;
  private Place mockPlace;
  private ReviewCreateRequest reviewRequest;
  private Review mockReview;

  @BeforeEach
  void setUp() {
    mockUser = User.builder()
        .id(1L)
        .nickName("testUser")
        .email("test@mail.com")
        .build();

    mockPlace = Place.builder()
        .id(10L)
        .name("testPlace")
        .build();

    mockPlace = spy(mockPlace); // updateReviewStats 검증을 위해 spy로 생성

    reviewRequest = ReviewCreateRequest.builder()
        .placeId(10L)
        .content("Good")
        .rating(5)
        .s3ImagePath("reviews/image_test.jpg")
        .build();

    mockReview = Review.builder()
        .id(10L)
        .user(mockUser)
        .place(mockPlace)
        .content(reviewRequest.getContent())
        .rating(reviewRequest.getRating())
        .imageUrl(reviewRequest.getS3ImagePath())
        .build();
  }

  @Test
  @DisplayName("리뷰 생성 및 포인트 적립 로직이 정상적으로 실행되어야 한다")
  void createReview_Success() {
    // given
    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(mockUser));
    when(placeRepository.findById(anyLong()))
        .thenReturn(Optional.of(mockPlace));

    when(reviewRepository.save(any(Review.class)))
        .thenReturn(mockReview);

    when(pointService.addPointsForReview(any(User.class), any(Review.class)))
        .thenReturn(PointAddResult.SUCCESS);

    // place.updateReviewStats()는 void 메서드이므로 doNothing 설정
    doNothing().when(mockPlace).updateReviewStats(anyInt());

    // when
    ReviewCreateResponse response = reviewService.createReview(mockUser.getId(), reviewRequest);

    // then
    // 응답 검증: PointAddResult enum의 메시지를 사용
    assertThat(response.getReviewId()).isEqualTo(10L);
    assertThat(response.getPointResultMessage()).isEqualTo(PointAddResult.SUCCESS.getMessage());

    // 핵심 메서드 호출 검증
    verify(reviewRepository, times(1)).save(any(Review.class));
    verify(mockPlace, times(1)).updateReviewStats(mockReview.getRating());
    verify(pointService, times(1)).addPointsForReview(mockUser, mockReview);
  }

  @Test
  @DisplayName("리뷰 생성 시 유효하지 않은 사용자 ID면 예외를 발생시켜야 한다")
  void createReview_UserNotFound_ThrowsException() {
    // given
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when & then
    assertThrows(BusinessException.class, () -> {
      reviewService.createReview(999L, reviewRequest);
    });

    // 예외가 발생했으므로 save나 pointService는 호출되지 않아야 함
    verify(reviewRepository, never()).save(any());
    verify(pointService, never()).addPointsForReview(any(), any());
  }

  @Test
  @DisplayName("내가 작성한 리뷰 조회 시 CloudFront URL로 변환되어야 한다")
  void getMyReviews_Success() {
    // given
    String s3Path = "reviews/image.jpg";
    String expectedFullUrl = "https://cdn.cloudfront.net/reviews/image.jpg";

    MyReviewResponse dtoWithS3Path = MyReviewResponse.builder()
        .reviewId(1L)
        .imageUrl(s3Path) // S3 경로만 포함된 DTO
        .build();

    when(userRepository.findById(anyLong()))
        .thenReturn(Optional.of(mockUser));
    when(reviewRepository.findMyReviewsWithProjection(any(User.class)))
        .thenReturn(List.of(dtoWithS3Path));

    // S3Service가 CloudFront URL을 반환하도록 설정
    when(s3Service.getPublicUrl(s3Path)).thenReturn(expectedFullUrl);

    // when
    List<MyReviewResponse> results = reviewService.getMyReviews(mockUser.getId());

    // then
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getImageUrl()).isEqualTo(expectedFullUrl);
    verify(s3Service, times(1)).getPublicUrl(s3Path);
  }

  @Test
  @DisplayName("장소별 리뷰 조회 시 CloudFront URL로 변환되어야 한다")
  void getReviewByPlace_Success() {
    // given
    String s3Path = "reviews/place_image.png";
    String expectedFullUrl = "https://cdn.cloudfront.net/reviews/place_image.png";

    ReviewInfo dtoWithS3Path = ReviewInfo.builder()
        .reviewId(2L)
        .imageUrl(s3Path)
        .build();

    when(placeRepository.findById(anyLong()))
        .thenReturn(Optional.of(mockPlace));
    when(reviewRepository.findReviewInfosByPlaceWithProjection(any(Place.class)))
        .thenReturn(List.of(dtoWithS3Path));

    when(s3Service.getPublicUrl(s3Path)).thenReturn(expectedFullUrl);

    // when
    reviewService.getReviewByPlace(mockPlace.getId());

    // then
    verify(reviewRepository, times(1)).findReviewInfosByPlaceWithProjection(mockPlace);
    verify(s3Service, times(1)).getPublicUrl(s3Path);
  }
}

