package com.backend.petplace.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignupRequest {

  @NotBlank(message = "이름은 필수입니다.")
  private String userName;

  @NotBlank(message = "비밀번호는 필수입니다.")
  private String password;

  @NotBlank(message = "이메일은 필수입니다.")
  private String userEmail;

  @NotBlank(message = "주소는 필수입니다.")
  private String address;

  @NotBlank(message = "우편번호는 필수입니다.")
  private String zipcode;

  private String addressDetail;
}
