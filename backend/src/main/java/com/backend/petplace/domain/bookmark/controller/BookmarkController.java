package com.backend.petplace.domain.bookmark.controller;

import com.backend.petplace.domain.bookmark.dto.response.MyBookmarkPlaceResponse;
import com.backend.petplace.domain.bookmark.service.BookmarkService;
import com.backend.petplace.global.jwt.CustomUserDetails;
import com.backend.petplace.global.response.ApiResponse;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Positive.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("api/v1/bookmark")
@RequiredArgsConstructor
public class BookmarkController implements BookmarkSpecification{

  private final BookmarkService bookmarkService;

  @Override
  @PostMapping("/places/{placeId}")
  public ResponseEntity<ApiResponse<Long>> addBookmark(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable @Positive Long placeId
  ) {
    return ResponseEntity.ok(ApiResponse.create(
        bookmarkService.addBookmark(userDetails.getUserId(), placeId)));
  }

  @Override
  @DeleteMapping("/places/{placeId}")
  public ResponseEntity<ApiResponse<Void>> removeBookmark(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable @Positive Long placeId
  ) {
    bookmarkService.removeBookmark(userDetails.getUserId(), placeId);
    return ResponseEntity.ok(ApiResponse.success());
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<List<MyBookmarkPlaceResponse>>> getMyBookmarks(
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    List<MyBookmarkPlaceResponse> results = bookmarkService.getMyBookmarks(userDetails.getUserId());

    return ResponseEntity.ok(ApiResponse.success(results));
  }

}