package com.backend.petplace.domain.point.service;

import com.backend.petplace.domain.point.entity.Point;
import com.backend.petplace.domain.point.repository.PointRepository;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.user.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

  private final PointRepository pointRepository;

  private static final int DAILY_POINT_LIMIT = 1000;

  public void addPointsForReview(User user, Review review) {
    if (!isEligibleForPoints(user, review)) {
      return;
    }

    Point point = Point.createFromReview(review);
    pointRepository.save(point);

    user.addPoints(point.getAmount());
  }

  private boolean isEligibleForPoints(User user, Review review) {
    LocalDate today = LocalDate.now();

    if (pointRepository.existsByUserAndPlaceAndRewardDate(user, review.getPlace(), today)) {
      return false;
    }

    Integer todaysPoints = pointRepository.findTodaysPointsSumByUser(user, today);
    if (todaysPoints >= DAILY_POINT_LIMIT) {
      return false;
    }

    return true;
  }
}