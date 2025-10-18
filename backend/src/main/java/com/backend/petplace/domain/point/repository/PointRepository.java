package com.backend.petplace.domain.point.repository;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.point.entity.Point;
import com.backend.petplace.domain.user.entity.User;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointRepository extends JpaRepository<Point, Long> {

  @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p WHERE p.user = :user AND p.createdDate BETWEEN :startOfDay AND :endOfDay")
  Integer findTodaysPointsSumByUser(@Param("user") User user, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

  boolean existsByUserAndPlaceAndCreatedDateBetween(User user, Place place, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
