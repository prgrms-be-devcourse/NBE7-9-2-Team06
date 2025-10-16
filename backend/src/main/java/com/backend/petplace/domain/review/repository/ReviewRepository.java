package com.backend.petplace.domain.review.repository;

import com.backend.petplace.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
