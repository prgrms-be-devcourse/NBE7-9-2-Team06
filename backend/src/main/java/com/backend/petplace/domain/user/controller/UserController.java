package com.backend.petplace.domain.user.controller;

import com.backend.petplace.domain.user.dto.request.UserLoginRequest;
import com.backend.petplace.domain.user.dto.request.UserSignupRequest;
import com.backend.petplace.domain.user.dto.response.UserSignupResponse;
import com.backend.petplace.domain.user.service.UserService;
import com.backend.petplace.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController implements UserSpecification {

  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<UserSignupResponse>> signup(
      @Valid @RequestBody UserSignupRequest request) {

    UserSignupResponse response = userService.signup(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<String>> login(
      @RequestBody UserLoginRequest request) {

    String response = userService.login(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}