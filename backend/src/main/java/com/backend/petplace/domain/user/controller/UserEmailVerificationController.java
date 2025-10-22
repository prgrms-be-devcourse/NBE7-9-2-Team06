package com.backend.petplace.domain.user.controller;

import com.backend.petplace.global.email.MailService;
import com.backend.petplace.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserEmailVerificationController {

  private final MailService mailService;

  @GetMapping("/email/auth/{email}")
  public ResponseEntity<ApiResponse<Void>> sendVerificationCodeToEmail
      (@PathVariable String email) {

    mailService.sendMail(email);
    return ResponseEntity.ok(ApiResponse.success());
  }
}