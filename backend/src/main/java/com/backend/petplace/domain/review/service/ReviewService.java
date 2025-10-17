package com.backend.petplace.domain.review.service;

import com.backend.petplace.domain.place.entity.Place;
import com.backend.petplace.domain.place.repository.PlaceRepository;
import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest;
import com.backend.petplace.domain.review.entity.Review;
import com.backend.petplace.domain.review.repository.ReviewRepository;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.domain.user.service.UserService;
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
  private final UserRepository userRepository;
  private final PlaceRepository placeRepository;
  private final S3Uploader s3Uploader;

  private static final String REVIEW_IMAGE_DIR = "reviews";

  public void createReview(Long userId,ReviewCreateRequest request, MultipartFile image) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

    Place place = placeRepository.findById(request.getPlaceId())
        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_PLACE));

    String imageUrl = null;
    if (image != null && !image.isEmpty()) {
      imageUrl = s3Uploader.upload(image, REVIEW_IMAGE_DIR);
    }

    Review review = Review.createNewReview(user, place, request.getContent(), request.getRating(), imageUrl);
    reviewRepository.save(review);
  }
}
