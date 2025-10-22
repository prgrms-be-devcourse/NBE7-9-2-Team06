package com.backend.petplace.domain.email.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckAuthCodeRequest {

  @NotBlank
  @Email
  private String email;

  @NotBlank
  private String authCode;
}
