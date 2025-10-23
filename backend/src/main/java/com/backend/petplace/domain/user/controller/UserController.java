package com.backend.petplace.domain.user.controller;

import com.backend.petplace.domain.user.dto.request.UserLoginRequest;
import com.backend.petplace.domain.user.dto.request.UserSignupRequest;
import com.backend.petplace.domain.user.dto.response.BoolResultResponse;
import com.backend.petplace.domain.user.dto.response.UserLoginResponse;
import com.backend.petplace.domain.user.dto.response.UserSignupResponse;
import com.backend.petplace.domain.user.service.UserService;
import com.backend.petplace.global.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController implements UserSpecification {

  private final UserService userService;

  @GetMapping("/signup-username")
  public ResponseEntity<ApiResponse<BoolResultResponse>> checkNickName(
      @RequestParam
      @NotBlank
      @Size(min = 2, max = 12)
      @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$")
      String nickName) {

    BoolResultResponse response = userService.validateDuplicateNickName(nickName);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/signup-email")
  public ResponseEntity<ApiResponse<BoolResultResponse>> checkEmail(
      @RequestParam @NotBlank @Email String email) {

    BoolResultResponse response = userService.validateDuplicateEmail(email);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<UserSignupResponse>> signup(
      @Valid @RequestBody UserSignupRequest request) {

    UserSignupResponse response = userService.signup(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<UserLoginResponse>> login(
      @Valid @RequestBody UserLoginRequest request) {

    UserLoginResponse response = userService.login(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}