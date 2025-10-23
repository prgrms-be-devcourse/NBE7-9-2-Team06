package com.backend.petplace.domain.user.controller;

import static com.backend.petplace.global.response.ErrorCode.AUTH_CODE_NOT_FOUND;
import static com.backend.petplace.global.response.ErrorCode.AUTH_CODE_NOT_VERIFIED;
import static com.backend.petplace.global.response.ErrorCode.BAD_CREDENTIAL;
import static com.backend.petplace.global.response.ErrorCode.DUPLICATE_EMAIL;
import static com.backend.petplace.global.response.ErrorCode.DUPLICATE_NICKNAME;

import com.backend.petplace.domain.user.dto.request.UserLoginRequest;
import com.backend.petplace.domain.user.dto.request.UserSignupRequest;
import com.backend.petplace.domain.user.dto.response.BoolResultResponse;
import com.backend.petplace.domain.user.dto.response.UserLoginResponse;
import com.backend.petplace.domain.user.dto.response.UserSignupResponse;
import com.backend.petplace.global.config.swagger.ApiErrorCodeExamples;
import com.backend.petplace.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User", description = "회원 API")
public interface UserSpecification {

  @ApiErrorCodeExamples({DUPLICATE_NICKNAME})
  @Operation(summary = "회원가입 중, 이름 중복 검사", description = "중복 검사 버튼을 누르면 검사를 실행합니다. (유효성 검사: 길이(2~12), 영어, 한글, 숫자만 가능)")
  ResponseEntity<ApiResponse<BoolResultResponse>> checkNickName(
      @RequestParam
      @NotBlank
      @Size(min = 2, max = 12)
      @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$")
      String nickName
  );

  @ApiErrorCodeExamples({DUPLICATE_EMAIL})
  @Operation(summary = "회원가입 중, 이메일 중복 검사", description = "중복 검사 버튼을 누르면 검사를 실행합니다. (유효성 검사: 이메일 형식, 빈 칸)")
  ResponseEntity<ApiResponse<BoolResultResponse>> checkEmail(
      @RequestParam
      @NotBlank
      @Email
      String email
  );

  @ApiErrorCodeExamples({DUPLICATE_NICKNAME, DUPLICATE_EMAIL, AUTH_CODE_NOT_FOUND,
      AUTH_CODE_NOT_VERIFIED})
  @Operation(summary = "회원가입", description = "이용자가 회원가입을 제출합니다. 이름, 비밀번호, 이메일, 인증번호, 주소, 우편번호는 필수이며 상세주소는 선택입니다.")
  ResponseEntity<ApiResponse<UserSignupResponse>> signup(
      @Parameter(description = "이름, 비밀번호, 이메일, 인증번호, 주소, 우편번호, 상세주소(선택)", required = true) @Valid UserSignupRequest request
  );

  @ApiErrorCodeExamples({BAD_CREDENTIAL})
  @Operation(summary = "로그인", description = "이용자가 로그인을 합니다. 이름, 비밀번호 필수입니다.")
  ResponseEntity<ApiResponse<UserLoginResponse>> login(
      @Parameter(description = "이름, 비밀번호", required = true) @Valid UserLoginRequest request
  );
}