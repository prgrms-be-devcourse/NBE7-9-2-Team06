package com.backend.petplace.domain.review.service;

import com.backend.petplace.domain.review.dto.request.ReviewCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReviewService {

  public void createReview(Long userId,ReviewCreateRequest request, MultipartFile image) {
  }
}
