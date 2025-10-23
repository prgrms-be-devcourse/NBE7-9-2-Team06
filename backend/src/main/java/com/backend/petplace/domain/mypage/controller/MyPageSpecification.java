package com.backend.petplace.domain.mypage.controller;

import static com.backend.petplace.global.response.ErrorCode.NOT_FOUND_MEMBER;

import com.backend.petplace.domain.mypage.dto.response.MyPageResponse;
import com.backend.petplace.global.config.swagger.ApiErrorCodeExamples;
import com.backend.petplace.global.jwt.CustomUserDetails;
import com.backend.petplace.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "마이페이지 API", description = "사용자 마이페이지 정보를 불러오는 API입니다.")
public interface MyPageSpecification {

  @ApiErrorCodeExamples({NOT_FOUND_MEMBER})
  @Operation(summary = "마이페이지 불러오기", description = "마이페이지 정보를 불러옵니다.")
  public ResponseEntity<ApiResponse<MyPageResponse>> myPage(CustomUserDetails user);
}
