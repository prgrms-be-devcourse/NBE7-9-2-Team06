package com.backend.petplace.domain.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.petplace.domain.point.entity.Point;

public interface PointRepository  extends JpaRepository<Point, Long> {
	
}
