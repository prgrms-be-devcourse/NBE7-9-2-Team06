package com.backend.petplace.domain.review.service;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.place.repository.PlaceRepository;
import com.backend.petplace.domain.point.service.PointService;
import com.backend.petplace.domain.point.type.PointAddResult;
import com.backend.petplace.domain.review.dto.ReviewInfo;
import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest;
import com.backend.petplace.domain.review.dto.response.MyReviewResponse;
import com.backend.petplace.domain.review.dto.response.PlaceReviewsResponse;
import com.backend.petplace.domain.review.dto.response.ReviewCreateResponse;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.review.repository.ReviewRepository;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final PointService pointService;
  private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;
  private final PlaceRepository placeRepository;
  private final S3Service s3Service;

  @Transactional
  public ReviewCreateResponse createReview(Long userId, ReviewCreateRequest request) {
    User user = findUserById(userId);
    Place place = findPlaceById(request.getPlaceId());
    String imageUrl = request.getS3ImagePath();

    Review review = Review.createNewReview(user, place, request.getContent(), request.getRating(),
        imageUrl);
    Review savedReview = reviewRepository.save(review);
    place.updateReviewStats(savedReview.getRating());

    PointAddResult result = pointService.addPointsForReview(user, savedReview);
    String resultMessage = result.getMessage();

    return new ReviewCreateResponse(savedReview.getId(), resultMessage);
  }

  @Transactional(readOnly = true)
  public List<MyReviewResponse> getMyReviews(Long currentUserId) {

    User user = findUserById(currentUserId);

    List<MyReviewResponse> dtosWithS3Path = reviewRepository.findMyReviewsWithProjection(user);

    return dtosWithS3Path.stream()
        .map(dto -> MyReviewResponse.withFullImageUrl(
            dto,
            s3Service.getPublicUrl(dto.getImageUrl())
        ))
        .toList();
  }

  @Transactional(readOnly = true)
  public PlaceReviewsResponse getReviewByPlace(Long placeId) {

    Place place = findPlaceById(placeId);

    List<ReviewInfo> dtosWithS3Path = reviewRepository.findReviewInfosByPlaceWithProjection(place);

    List<ReviewInfo> dtosWithFullUrl = dtosWithS3Path.stream()
        .map(dto -> ReviewInfo.withFullImageUrl(
            dto,
            s3Service.getPublicUrl(dto.getImageUrl())
        ))
        .toList();

    return new PlaceReviewsResponse(place, dtosWithFullUrl);
  }

  private User findUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
  }

  private Place findPlaceById(Long placeId) {
    return placeRepository.findById(placeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));
  }

}
