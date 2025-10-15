package com.backend.petplace.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.petplace.domain.review.entity.Review;

public interface ReviewRepository  extends JpaRepository<Review, Long> {
}
