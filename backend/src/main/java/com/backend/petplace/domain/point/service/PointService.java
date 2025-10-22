package com.backend.petplace.domain.point.service;

import com.backend.petplace.domain.point.dto.PointTransaction;
import com.backend.petplace.domain.point.dto.response.PointHistoryResponse;
import com.backend.petplace.domain.point.entity.Point;
import com.backend.petplace.domain.point.repository.PointRepository;
import com.backend.petplace.domain.point.type.PointAddResult;
import com.backend.petplace.domain.point.type.PointPolicy;
import com.backend.petplace.domain.review.entity.Review;
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
public class PointService {

  private final PointRepository pointRepository;
  private final UserRepository userRepository;

  @Transactional
  public PointAddResult addPointsForReview(User user, Review review) {
    LocalDate today = LocalDate.now();

    if (pointRepository.existsByUserAndPlaceAndRewardDate(user, review.getPlace(), today)) {
      return PointAddResult.ALREADY_AWARDED;
    }

    Integer todaysPoints = pointRepository.findTodaysPointsSumByUser(user, today);

    if (todaysPoints >= PointPolicy.DAILY_LIMIT.getValue()) {
      return PointAddResult.DAILY_LIMIT_EXCEEDED;
    }

    Point point = Point.createFromReview(review);
    pointRepository.save(point);
    user.addPoints(point.getAmount());

    return PointAddResult.SUCCESS;
  }

  @Transactional(readOnly = true)
  public PointHistoryResponse getPointHistory(Long userId) {
    User user = findUserById(userId);

    List<PointTransaction> history = pointRepository.findPointHistoryByUser(user);
    return new PointHistoryResponse(user.getTotalPoint(), history);
  }

  private User findUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
  }
}