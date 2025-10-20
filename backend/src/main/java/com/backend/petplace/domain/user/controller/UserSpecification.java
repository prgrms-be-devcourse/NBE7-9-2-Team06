package com.backend.petplace.domain.user.controller;

import com.backend.petplace.domain.user.dto.request.UserSignupRequest;
import com.backend.petplace.domain.user.dto.response.UserSignupResponse;
import com.backend.petplace.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "User", description = "회원 API")
public interface UserSpecification {

  @Operation(summary = "회원가입", description = "이용자가 회원가입을 제출합니다. 이름, 비밀번호, 이메일, 주소, 우편번호는 필수이며 상세주소는 선택입니다.")
  ResponseEntity<ApiResponse<UserSignupResponse>> signup(
      @Parameter(description = "이름, 비밀번호, 이메일, 주소, 우편번호, 상세주소(선택)", required = true) UserSignupRequest request
  );

  /*@Operation(summary = "로그인", description = "이용자가 로그인을 합니다. 이름, 비밀번호 필수입니다.")
  void login(
      @Parameter(description = "이름, 비밀번호", required = true) UserSignupRequest request
  );*/

}
