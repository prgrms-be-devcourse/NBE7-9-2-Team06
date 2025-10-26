package com.backend.petplace.domain.mypage.service;

import com.backend.petplace.domain.mypage.dto.MyPageUserInfo;
import com.backend.petplace.domain.mypage.dto.MyPageUserPets;
import com.backend.petplace.domain.mypage.dto.MyPageUserPoints;
import com.backend.petplace.domain.mypage.dto.response.MyPageResponse;
import com.backend.petplace.domain.pet.repository.PetRepository;
import com.backend.petplace.domain.point.repository.PointRepository;
import com.backend.petplace.domain.point.type.PointPolicy;
import com.backend.petplace.domain.review.dto.response.MyReviewResponse;
import com.backend.petplace.domain.review.repository.ReviewRepository;
import com.backend.petplace.domain.review.service.S3Service;
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
  private final S3Service s3Service;

  @Transactional(readOnly = true)
  public MyPageResponse myPage(Long userId) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
    int earnablePoints = Math.max(
        PointPolicy.DAILY_LIMIT.getValue() - pointRepository.findTodaysPointsSumByUser(user,
            LocalDate.now()), 0); //음수면 0으로 자동 설정\

    List<MyPageUserPoints> pointDto = pointRepository.findMyPagePointHistory(user);
    List<MyReviewResponse> reviewDtoWithS3Path = reviewRepository.findMyReviewsWithProjection(user);

    List<MyReviewResponse> reviewDto = reviewDtoWithS3Path.stream()
        .map(dto -> MyReviewResponse.withFullImageUrl(
            dto,
            s3Service.getPublicUrl(dto.getImageUrl())
        ))
        .toList();

    List<MyPageUserPets> petDto = petRepository.findByUserWithActivatedPet(user);
    MyPageUserInfo userDto = MyPageUserInfo.from(user, earnablePoints, reviewDto.size());

    return new MyPageResponse(userDto, reviewDto, pointDto, petDto);
  }
}
