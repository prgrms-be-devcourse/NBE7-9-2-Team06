package com.backend.petplace.domain.review.repository;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.review.dto.ReviewInfo;
import com.backend.petplace.domain.review.dto.response.MyReviewResponse;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  List<Review> findByUserOrderByIdDesc(User user);

  List<Review> findByPlaceOrderByIdDesc(Place place);

  @Query("SELECT new com.backend.petplace.domain.review.dto.response.MyReviewResponse(" +
      "r.id, r.place.id, r.place.name, r.place.address, " +
      "r.rating, r.content, r.imageUrl, r.createdDate, pt.amount) " +
      "FROM Review r " +
      "JOIN r.place pl " +
      "JOIN r.user u " +
      "LEFT JOIN r.point pt " +
      "WHERE r.user = :user")
  List<MyReviewResponse> findMyReviewsWithProjection(@Param("user") User user);

  @Query("SELECT new com.backend.petplace.domain.review.dto.ReviewInfo(" +
      "r.id, u.nickName, r.content, r.rating, r.imageUrl, r.createdDate) " +
      "FROM Review r " +
      "JOIN r.user u " +
      "WHERE r.place = :place")
  List<ReviewInfo> findReviewInfosByPlaceWithProjection(@Param("place") Place place);
}
