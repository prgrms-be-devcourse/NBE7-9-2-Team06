package com.backend.petplace.domain.mypage.service;

import com.backend.petplace.domain.mypage.dto.response.MyPageResponse;
import com.backend.petplace.domain.pet.entity.Pet;
import com.backend.petplace.domain.pet.repository.PetRepository;
import com.backend.petplace.domain.point.entity.Point;
import com.backend.petplace.domain.point.repository.PointRepository;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.review.repository.ReviewRepository;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

  private final UserRepository userRepository;
  private final PointRepository pointRepository;
  private final ReviewRepository reviewRepository;
  private final PetRepository petRepository;

  private static final int DAILY_POINT_LIMIT = 1000; // <- 건의사항 : 포인트 관련 policy는 전역으로 둬서 Enum으로 관리하는게 낫지 않을까요?

  @Transactional(readOnly = true)
  public MyPageResponse myPage(Long userId){

    User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
    List<Point> points = pointRepository.findByUserOrderByIdDesc(user);
    List<Review> reviews = reviewRepository.findByUserWithPlace(user);
    List<Pet> pets = petRepository.findByUserWithActivatedPet(user);
    int earnablePoints = Math.max(DAILY_POINT_LIMIT - pointRepository.findTodaysPointsSumByUser(user, LocalDate.now()), 0); //음수면 0으로 자동 설정
    //고민 1. point에서 오늘 획득 가능한 포인트를 조회하기 위해 쿼리를 한 번 더쓸지
    //고민 2. 최적화 하기위해 직접 연관관계를 이용해서 스프링 내부적으로 계산을 할지 고민입니다.
    //찾아보니 DB에서 하는게 더 효율적이라고 합니다. (네트워크 인아웃풋만 소폭 잡아먹는다고 하네요)

    return MyPageResponse.from(user, reviews, points, pets, earnablePoints);
  }

}
