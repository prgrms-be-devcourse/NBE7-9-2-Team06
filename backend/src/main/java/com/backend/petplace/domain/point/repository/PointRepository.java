package com.backend.petplace.domain.point.repository;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.point.entity.Point;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.user.entity.User;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointRepository extends JpaRepository<Point, Long> {

  @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p WHERE p.user = :user AND p.rewardDate = :today")
  Integer findTodaysPointsSumByUser(@Param("user") User user, @Param("today") LocalDate today);

  boolean existsByUserAndPlaceAndRewardDate(User user, Place place, LocalDate today);

  Optional<Point> findByReview(Review review);
}
