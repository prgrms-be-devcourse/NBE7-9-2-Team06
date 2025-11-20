package com.backend.petplace.domain.point.service;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.point.entity.Point;
import com.backend.petplace.domain.point.type.PointAddResult;
import com.backend.petplace.domain.point.repository.PointRepository;
import com.backend.petplace.domain.point.type.PointPolicy;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

  @InjectMocks
  private PointService pointService;

  @Mock
  private PointRepository pointRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private User mockUser;

  private Review mockReviewWithImage;
  private Review mockReviewWithoutImage;

  @BeforeEach
  void setUp() {
    Place mockPlace = Place.builder().id(10L).name("Test Place").build();

    Review rawReviewWithImage = Review.builder()
        .id(1L)
        .user(mockUser)
        .place(mockPlace)
        .imageUrl("key.jpg")
        .build();
    mockReviewWithImage = rawReviewWithImage;

    Review rawReviewWithoutImage = Review.builder()
        .id(2L)
        .user(mockUser)
        .place(mockPlace)
        .imageUrl(null)
        .build();
    mockReviewWithoutImage = rawReviewWithoutImage;

  }

  @Test
  @DisplayName("1-1. 이미지 리뷰 등록 시 100 포인트 정상 적립")
  void addPointsForReview_Success_WithImage() {
    // given
    int expectedAmount = PointPolicy.REVIEW_PHOTO_POINTS.getValue();

    when(pointRepository.existsByUserAndPlaceAndRewardDate(any(), any(), any())).thenReturn(false);
    when(pointRepository.findTodaysPointsSumByUser(any(), any())).thenReturn(0);

    // when
    PointAddResult result = pointService.addPointsForReview(mockUser, mockReviewWithImage);

    // then
    assertThat(result).isEqualTo(PointAddResult.SUCCESS);

    verify(pointRepository, times(1)).save(any(Point.class));
    verify(mockUser, times(1)).addPoints(expectedAmount); // User의 addPoints가 호출되었는지 확인
  }

  @Test
  @DisplayName("1-2. 텍스트 리뷰 등록 시 50 포인트가 정상 적립")
  void addPointsForReview_Success_WithoutImage() {
    // given
    int expectedAmount = PointPolicy.REVIEW_TEXT_POINTS.getValue();
    when(pointRepository.existsByUserAndPlaceAndRewardDate(any(), any(), any())).thenReturn(false);
    when(pointRepository.findTodaysPointsSumByUser(any(), any())).thenReturn(0);

    // when
    PointAddResult result = pointService.addPointsForReview(mockUser, mockReviewWithoutImage);

    // then
    assertThat(result).isEqualTo(PointAddResult.SUCCESS);

    verify(pointRepository, times(1)).save(any(Point.class));
    verify(mockUser, times(1)).addPoints(expectedAmount);
  }

  @Test
  @DisplayName("1-3. 이미 당일 적립된 장소의 리뷰는 ALREADY_AWARDED 반환")
  void addPointsForReview_AlreadyAwarded() {
    // given
    when(pointRepository.existsByUserAndPlaceAndRewardDate(any(), any(), any()))
        .thenReturn(true);

    // when
    PointAddResult result = pointService.addPointsForReview(mockUser, mockReviewWithImage);

    // then
    assertThat(result).isEqualTo(PointAddResult.ALREADY_AWARDED);

    verify(pointRepository, never()).save(any());
    verify(mockUser, never()).addPoints(anyInt());
  }

  @Test
  @DisplayName("1-4. 일일 적립 한도 초과 시 DAILY_LIMIT_EXCEEDED 반환")
  void addPointsForReview_DailyLimitExceeded() {
    // given
    int dailyLimit = PointPolicy.DAILY_LIMIT.getValue();

    when(pointRepository.existsByUserAndPlaceAndRewardDate(any(), any(), any())).thenReturn(false);
    when(pointRepository.findTodaysPointsSumByUser(any(), any())).thenReturn(dailyLimit);

    // when
    PointAddResult result = pointService.addPointsForReview(mockUser, mockReviewWithImage);

    // then
    assertThat(result).isEqualTo(PointAddResult.DAILY_LIMIT_EXCEEDED);

    verify(pointRepository, never()).save(any());
    verify(mockUser, never()).addPoints(anyInt());
  }

  @Test
  @DisplayName("2-1. 포인트 내역 조회 시 응답 DTO 정상적 생성")
  void getPointHistory_Success() {

    // given
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
    when(mockUser.getId()).thenReturn(1L);
    when(mockUser.getTotalPoint()).thenReturn(100);

    when(pointRepository.findPointHistoryByUser(any())).thenReturn(Collections.emptyList());

    // when
    pointService.getPointHistory(mockUser.getId());

    // then
    verify(userRepository, times(1)).findById(mockUser.getId());
    verify(pointRepository, times(1)).findPointHistoryByUser(mockUser);
  }

  @Test
  @DisplayName("2-2. 포인트 내역 조회 시 사용자가 없으면 예외 발생")
  void getPointHistory_UserNotFound() {
    // given
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when & then
    assertThrows(BusinessException.class, () -> {
      pointService.getPointHistory(999L);
    });

    verify(pointRepository, never()).findPointHistoryByUser(any());
  }
}