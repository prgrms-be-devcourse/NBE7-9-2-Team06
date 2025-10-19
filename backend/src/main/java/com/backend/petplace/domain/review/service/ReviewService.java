package com.backend.petplace.domain.review.service;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.place.repository.PlaceRepository;
import com.backend.petplace.domain.point.entity.Point;
import com.backend.petplace.domain.point.repository.PointRepository;
import com.backend.petplace.domain.point.service.PointService;
import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.review.repository.ReviewRepository;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import com.backend.petplace.global.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final PointService pointService;
  private final UserRepository userRepository;
  private final PlaceRepository placeRepository;
  private final S3Uploader s3Uploader;

  private static final String REVIEW_IMAGE_DIR = "reviews";

  public void createReview(Long userId, ReviewCreateRequest request, MultipartFile image) {
    User user = findUserById(userId);
    Place place = findPlaceById(request.getPlaceId());

    String imageUrl = uploadImageIfPresent(image);

    Review review = Review.createNewReview(user, place, request.getContent(), request.getRating(),
        imageUrl);
    Review savedReview = reviewRepository.save(review);

    place.updateReviewStats(savedReview.getRating());

    pointService.addPointsForReview(user, savedReview);
  }

  private User findUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
  }

  private Place findPlaceById(Long placeId) {
    return placeRepository.findById(placeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));
  }

  private String uploadImageIfPresent(MultipartFile image) {
    if (image != null && !image.isEmpty()) {
      return s3Uploader.upload(image, REVIEW_IMAGE_DIR);
    }
    return null;
  }
}
