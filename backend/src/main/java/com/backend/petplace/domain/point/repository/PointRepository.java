package com.backend.petplace.domain.point.repository;

import com.backend.petplace.domain.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {

}
