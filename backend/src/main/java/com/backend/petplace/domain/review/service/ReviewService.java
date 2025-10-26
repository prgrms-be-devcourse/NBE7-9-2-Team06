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
import java.util.stream.Collectors;
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

    return reviewRepository.findMyReviews(user);
  }

  @Transactional(readOnly = true)
  public PlaceReviewsResponse getReviewByPlace(Long placeId) {

    Place place = findPlaceById(placeId);

    List<Review> reviews = reviewRepository.findAllByPlace(place);

    //List<ReviewInfo> reviewInfos = reviewRepository.findReviewInfosByPlace(place);
    List<ReviewInfo> reviewInfos = reviews.stream()
        .map(review -> new ReviewInfo(
            review.getId(), // (확인) Review 엔티티의 ID 필드
            review.getUser().getNickName(), // (가정) User 엔티티의 닉네임 필드
            review.getContent(),
            review.getRating(),
            s3Service.getPublicUrl(review.getImageUrl()), // (중요) S3Service로 전체 URL 변환
            review.getCreatedDate() // (가정) Review 엔티티의 생성 날짜 필드
        ))
        .collect(Collectors.toList());

    return new PlaceReviewsResponse(place, reviewInfos);
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
