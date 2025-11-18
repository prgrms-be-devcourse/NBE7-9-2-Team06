package com.backend.petplace.domain.review.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.petplace.domain.place.entity.Category1Type;
import com.backend.petplace.domain.place.entity.Category2Type;
import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.place.repository.PlaceRepository;
import com.backend.petplace.domain.point.repository.PointRepository;
import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest;
import com.backend.petplace.domain.review.repository.ReviewRepository;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@SpringBootTest
public class ReveiwServiceTest {

  @Autowired
  private ReviewService reviewService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PlaceRepository placeRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private PointRepository pointRepository;

  private User savedUserA;

  private User savedUserB;

  private Place savedPlace;

  @BeforeEach
  void setup() {
    User userA = User.builder()
        .nickName("TestUserA")
        .email("TestUserA@test.com")
        .password("password")
        .address("Test Address A")
        .zipcode("12345")
        .build();
    savedUserA = userRepository.save(userA);

    User userB = User.builder()
        .nickName("TestUserB")
        .email("TestUserB@test.com")
        .password("password")
        .address("Test Address B")
        .zipcode("12345")
        .build();
    savedUserB = userRepository.save(userB);

    Place place = Place.builder()
        .name("Test Place")
        .uniqueKey("test-key-12345")
        .category1(Category1Type.PET_CAFE_RESTAURANT)
        .category2(Category2Type.CAFE)
        .latitude(37.123)
        .longitude(127.123)
        .build();
    savedPlace = placeRepository.save(place);
  }

  @AfterEach
  void tearDown() {
    pointRepository.deleteAllInBatch();
    reviewRepository.deleteAllInBatch();
    placeRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("서로 다른 유저가 같은 장소에 리뷰 동시 등록할 시 갱신 분실 방지 테스트")
  void placeOptimisticLockTest() throws InterruptedException {

    // given
    int threadCount = 2;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount); // 스레드 풀 생성
    CountDownLatch latch = new CountDownLatch(1); // 모든 스레드가 준비될 때까지 대기 (신호 기다림)

    // 동시성 환경에서 안전하게 성공/실패 횟수 세기 위한 변수
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);

    // 요청 1 (별점 5점)
    ReviewCreateRequest requestA = new ReviewCreateRequest(
        savedPlace.getId(),
        "첫 번째 리뷰(User A) : 아주 좋습니다! 별점 5점",
        5,
        null);
    // 요청 2 (별점 1점)
    ReviewCreateRequest requestB = new ReviewCreateRequest(
        savedPlace.getId(),
        "두 번째 리뷰(User B) : 별로예요!!!! 별점 1점",
        1,
        null);

    // when
    Runnable taskA = () -> {
      try {
        latch.await(); // 모든 스레드가 준비될 때까지 대기 (신호 기다림)
        reviewService.createReview(savedUserA.getId(), requestA); // 리뷰 등록 시도
        successCount.incrementAndGet(); // 성공 횟수 증가
      }
      // 충돌 예외
      catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
        failureCount.incrementAndGet(); // 실패 횟수 증가
      }
      // 그 외 예외
      catch (Exception e) {
        e.printStackTrace();
      }
  };

    Runnable taskB = () -> {
      try {
        latch.await();
        reviewService.createReview(savedUserB.getId(), requestB);
        successCount.incrementAndGet();
      } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
        failureCount.incrementAndGet();
      } catch (Exception e) {
        e.printStackTrace();
      }
    };

    // 모든 스레드 준비 완료, 동시에 시작
    executorService.submit(taskA);
    executorService.submit(taskB);

    // 신호 보내기
    latch.countDown();

    // 스레드 풀 종료 대기
    Thread.sleep(1000);

    // then
    Place resultPlace = placeRepository.findById(savedPlace.getId()).get(); // DB에서 최신 상태 조회 (최종 결과)

    // 결과 검증 (성공 1회, 실패 1회여야 함)
    assertThat(successCount.get()).isEqualTo(1);
    assertThat(failureCount.get()).isEqualTo(1);

    // DB 데이터 검증 (리뷰 1개, 평균 별점은 성공한 리뷰의 별점이어야 함)
    assertThat(reviewRepository.count()).isEqualTo(1);
    assertThat(resultPlace.getAverageRating()).isIn(1.0, 5.0); // 성공한 리뷰 별점
    assertThat(resultPlace.getTotalReviewCount()).isEqualTo(1);
    assertThat(resultPlace.getVersion()).isEqualTo(1L);
  }

  @Test
  @DisplayName("한 사용자 같은 장소에 리뷰 2개 동시 등록할 시 DB UNIQUE 제약 테스트")
  void placeDbUniqueTest() throws InterruptedException {

    // given
    int threadCount = 2;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount); // 스레드 풀 생성
    CountDownLatch latch = new CountDownLatch(1); // 모든 스레드가 준비될 때까지 대기 (신호 기다림)

    // 동시성 환경에서 안전하게 성공/실패 횟수 세기 위한 변수
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);

    // 요청 1 (별점 5점)
    ReviewCreateRequest requestA = new ReviewCreateRequest(
        savedPlace.getId(),
        "첫 번째 리뷰 : 아주 좋습니다! 별점 5점",
        5,
        null);
    // 요청 2 (별점 1점)
    ReviewCreateRequest requestB = new ReviewCreateRequest(
        savedPlace.getId(),
        "두 번째 리뷰 : 별로예요!!!! 별점 1점",
        1,
        null);

    // when
    Runnable taskA = () -> {
      try {
        latch.await(); // 모든 스레드가 준비될 때까지 대기 (신호 기다림)
        reviewService.createReview(savedUserA.getId(), requestA); // 리뷰 등록 시도
        successCount.incrementAndGet(); // 성공 횟수 증가
      }
      // 충돌 예외
      catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
        failureCount.incrementAndGet(); // 실패 횟수 증가
      }
      // DB UNIQUE 제약조건 위반 예외
      catch (DataIntegrityViolationException e) {
        failureCount.incrementAndGet(); // 실패 횟수 증가
      }
      // 그 외 예외
      catch (Exception e) {
        e.printStackTrace();
      }
    };

    Runnable taskB = () -> {
      try {
        latch.await();
        reviewService.createReview(savedUserA.getId(), requestB);
        successCount.incrementAndGet();
      } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
        failureCount.incrementAndGet();
      } catch (DataIntegrityViolationException e) {
        failureCount.incrementAndGet();
      } catch (Exception e) {
        e.printStackTrace();
      }
    };

    // 모든 스레드 준비 완료, 동시에 시작
    executorService.submit(taskA);
    executorService.submit(taskB);

    // 신호 보내기
    latch.countDown();

    // 스레드 풀 종료 대기
    Thread.sleep(1000);

    // then
    Place resultPlace = placeRepository.findById(savedPlace.getId()).get(); // DB에서 최신 상태 조회 (최종 결과)

    // 결과 검증 (성공 1회, 실패 1회여야 함)
    assertThat(successCount.get()).isEqualTo(1);
    assertThat(failureCount.get()).isEqualTo(1);

    // DB 데이터 검증 (리뷰 1개, 평균 별점은 성공한 리뷰의 별점이어야 함)
    assertThat(reviewRepository.count()).isEqualTo(1);
    assertThat(resultPlace.getAverageRating()).isIn(1.0, 5.0); // 성공한 리뷰 별점
    assertThat(resultPlace.getTotalReviewCount()).isEqualTo(1);
    assertThat(resultPlace.getVersion()).isEqualTo(1L);
  }
}
