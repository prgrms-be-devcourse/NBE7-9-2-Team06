package com.backend.petplace.domain.review.repository;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  List<Review> findByUserOrderByIdDesc(User user);

  List<Review> findByPlaceOrderByIdDesc(Place place);

}
