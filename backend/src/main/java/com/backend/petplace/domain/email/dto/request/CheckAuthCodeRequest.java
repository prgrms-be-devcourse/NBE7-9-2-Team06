package com.backend.petplace.domain.email.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckAuthCodeRequest {

  @NotBlank(message = "이메일은 필수입니다.")
  @Email
  private String email;

  @NotBlank(message = "인증번호는 필수입니다.")
  private String authCode;
}
