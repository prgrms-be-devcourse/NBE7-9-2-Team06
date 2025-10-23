package com.backend.petplace.domain.user.controller;

import com.backend.petplace.domain.email.dto.request.CheckAuthCodeRequest;
import com.backend.petplace.domain.email.service.EmailAuthCodeService;
import com.backend.petplace.domain.user.dto.response.BoolResultResponse;
import com.backend.petplace.global.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1/email/auth")
@RequiredArgsConstructor
public class UserEmailVerificationController implements UserEmailVerificationSpecification{

  private final EmailAuthCodeService EMailAuthCodeService;

  @GetMapping
  public ResponseEntity<ApiResponse<BoolResultResponse>> sendAuthCodeToEmail
      (@RequestParam @NotBlank @Email String email) {

    BoolResultResponse response = EMailAuthCodeService.sendMail(email);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PatchMapping
  public ResponseEntity<ApiResponse<BoolResultResponse>> checkAuthCode
      (@RequestBody @Valid CheckAuthCodeRequest request) {

    BoolResultResponse response = EMailAuthCodeService.checkAuthCode(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}