package com.backend.petplace.domain.bookmark.controller;

import com.backend.petplace.domain.bookmark.service.BookmarkService;
import com.backend.petplace.global.jwt.CustomUserDetails;
import com.backend.petplace.global.response.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("api/v1/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

  private final BookmarkService bookmarkService;

  @PostMapping
  public ResponseEntity<ApiResponse<Long>> addBookmark (
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam @NotNull Long placeId
  ) {
    Long userId = userDetails.getUserId();
    Long bookmarkId = bookmarkService.addBookmark(userId, placeId);
    return ResponseEntity.ok(ApiResponse.create(bookmarkId));
  }

}