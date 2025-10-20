package com.backend.petplace.domain.review.service;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.place.repository.PlaceRepository;
import com.backend.petplace.domain.point.entity.Point;
import com.backend.petplace.domain.point.repository.PointRepository;
import com.backend.petplace.domain.point.service.PointService;
import com.backend.petplace.domain.point.type.PointAddResult;
import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest;
import com.backend.petplace.domain.review.dto.response.MyReviewResponse;
import com.backend.petplace.domain.review.dto.response.ReviewCreateResponse;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.review.repository.ReviewRepository;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import com.backend.petplace.global.s3.S3Uploader;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

  private final PointService pointService;
  private final PointRepository pointRepository;
  private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;
  private final PlaceRepository placeRepository;
  private final S3Uploader s3Uploader;

  private static final String REVIEW_IMAGE_DIR = "reviews";

  public ReviewCreateResponse createReview(Long userId, ReviewCreateRequest request,
      MultipartFile image) {
    User user = findUserById(userId);
    Place place = findPlaceById(request.getPlaceId());

    String imageUrl = uploadImageIfPresent(image);

    Review review = Review.createNewReview(user, place, request.getContent(), request.getRating(),
        imageUrl);
    Review savedReview = reviewRepository.save(review);

    place.updateReviewStats(savedReview.getRating());

    PointAddResult result = pointService.addPointsForReview(user, savedReview);
    String resultMessage = result.getMessage();

    return new ReviewCreateResponse(savedReview.getId(), resultMessage);
  }

  public List<MyReviewResponse> getMyReviews(Long currentUserId) {
    User user = findUserById(currentUserId);

    List<Review> reviews = reviewRepository.findByUserOrderByIdDesc(user);

    return reviews.stream()
        .map(review -> {
          int points = pointRepository.findByReview(review)
              .map(Point::getAmount)
              .orElse(0);

          return MyReviewResponse.from(review, points);
        })
        .collect(Collectors.toList());
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
