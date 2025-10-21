package com.backend.petplace.domain.review.repository;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  List<Review> findByUserOrderByIdDesc(User user);

  List<Review> findByPlaceOrderByIdDesc(Place place);

  @Query("select r from Review r join fetch r.place p where r.user = :user order by r.createdDate desc") //N+1 문제 해소, 최신순으로 정렬
  List<Review> findByUserWithPlace(@Param("user") User user);

}
