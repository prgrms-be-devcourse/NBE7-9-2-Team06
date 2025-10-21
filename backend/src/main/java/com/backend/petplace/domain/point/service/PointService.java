package com.backend.petplace.domain.point.service;

import com.backend.petplace.domain.point.dto.PointTransaction;
import com.backend.petplace.domain.point.dto.response.PointHistoryResponse;
import com.backend.petplace.domain.point.entity.Point;
import com.backend.petplace.domain.point.repository.PointRepository;
import com.backend.petplace.domain.point.type.PointAddResult;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

  private final PointRepository pointRepository;
  private final UserRepository userRepository;

  private static final int DAILY_POINT_LIMIT = 1000;

  public PointAddResult addPointsForReview(User user, Review review) {
    LocalDate today = LocalDate.now();

    if (pointRepository.existsByUserAndPlaceAndRewardDate(user, review.getPlace(), today)) {
      return PointAddResult.ALREADY_AWARDED;
    }

    Integer todaysPoints = pointRepository.findTodaysPointsSumByUser(user, today);
    if (todaysPoints >= DAILY_POINT_LIMIT) {
      return PointAddResult.DAILY_LIMIT_EXCEEDED;
    }

    Point point = Point.createFromReview(review);
    pointRepository.save(point);
    user.addPoints(point.getAmount());

    return PointAddResult.SUCCESS;
  }

  public PointHistoryResponse getPointHistory(Long userId) {
    User user = findUserById(userId);

    List<Point> points = pointRepository.findByUserOrderByIdDesc(user);

    List<PointTransaction> history = points.stream()
        .map(PointTransaction::from)
        .toList();

    return new PointHistoryResponse(user.getTotalPoint(), history);
  }

  private User findUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
  }
}