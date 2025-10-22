package com.backend.petplace.domain.mypage.controller;

import com.backend.petplace.domain.mypage.dto.response.MyPageResponse;
import com.backend.petplace.domain.mypage.service.MyPageService;
import com.backend.petplace.global.jwt.CustomUserDetails;
import com.backend.petplace.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MyPageController implements MyPageSpecification{

  private final MyPageService myPageService;

  @Override
  @GetMapping("/my-page")
  public ResponseEntity<ApiResponse<MyPageResponse>> myPage(
      @AuthenticationPrincipal CustomUserDetails user){
    Long userId = user.getUserId();
    MyPageResponse response = myPageService.myPage(userId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

}
