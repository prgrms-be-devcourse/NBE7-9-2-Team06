package com.backend.petplace.domain.review.repository;

import com.backend.petplace.domain.mypage.dto.MyPageUserReviews;
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
      "r.id, p.id, p.name, p.address, r.rating, r.content, r.imageUrl, r.createdDate, pt.amount) " +
      "FROM Review r " +
      "JOIN r.place p " + // Place 정보 JOIN
      "LEFT JOIN Point pt ON pt.review = r " + // Point 정보 LEFT JOIN (포인트 없을 수도 있음)
      "WHERE r.user = :user ORDER BY r.id DESC")
  List<MyReviewResponse> findMyReviews(@Param("user") User user);

  @Query("SELECT new com.backend.petplace.domain.review.dto.ReviewInfo(" +
      "r.id, r.user.nickName, r.content, r.rating, r.imageUrl, r.createdDate) " +
      "FROM Review r " +
      // User 정보는 Review 엔티티에 연관관계가 있으므로 JOIN FETCH 또는 DTO에서 직접 User ID 조회 방식 선택 가능
      // 여기서는 간단하게 r.user.nickName 사용 (쿼리는 늘지만 N+1은 아님)
      // 만약 User 정보가 더 많이 필요하면 JOIN FETCH r.user 추가 고려
      "WHERE r.place = :place ORDER BY r.id DESC")
  List<ReviewInfo> findReviewInfosByPlace(@Param("place") Place place);

  @Query("SELECT new com.backend.petplace.domain.mypage.dto.MyPageUserReviews("+
  "r.id, p.name, p.address, r.content, r.imageUrl, r.rating, r.createdDate) " +
  "FROM Review r " +
  "JOIN r.place p " +
  "WHERE r.user = :user ORDER BY r.id DESC")
  List<MyPageUserReviews> findByUserWithPlace(@Param("user") User user);
}
