package com.backend.petplace.domain.user.service;

import com.backend.petplace.domain.email.entity.EmailAuthCode;
import com.backend.petplace.domain.email.repository.EmailAuthCodeRepository;
import com.backend.petplace.domain.user.dto.request.UserLoginRequest;
import com.backend.petplace.domain.user.dto.request.UserSignupRequest;
import com.backend.petplace.domain.user.dto.response.BoolResultResponse;
import com.backend.petplace.domain.user.dto.response.UserLoginResponse;
import com.backend.petplace.domain.user.dto.response.UserSignupResponse;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.jwt.JwtTokenProvider;
import com.backend.petplace.global.response.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final EmailAuthCodeRepository emailAuthCodeRepository;
  private final RedisService redisService;

  private static final int MAX_AGE_SECONDS = 7 * 24 * 60 * 60; // 7일

  @Transactional
  public UserSignupResponse signup(UserSignupRequest request) {
     validateDuplicateNickName(request.getNickName());

     validateDuplicateEmail(request.getEmail());

    // 이메일 인증 체크
    checkAuthCode(request);

    User user = User.create(request, passwordEncoder.encode(request.getPassword()));
    userRepository.save(user);

    return new UserSignupResponse(user.getId());
  }

  @Transactional
  protected void checkAuthCode(UserSignupRequest request) {
    EmailAuthCode emailAuthCode = emailAuthCodeRepository.findByEmailAndAuthCode
            (request.getEmail(), request.getAuthCode())
        .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_CODE_NOT_FOUND));

    if (emailAuthCode.isVerified()) {
      emailAuthCodeRepository.delete(emailAuthCode);
      return;
    }
    throw new BusinessException(ErrorCode.AUTH_CODE_NOT_VERIFIED);
  }

  @Transactional(readOnly = true)
  public BoolResultResponse validateDuplicateNickName(String nickName) {
    if (userRepository.existsByNickName(nickName)) {
      throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
    }
    return new BoolResultResponse(true);
  }

  @Transactional(readOnly = true)
  public BoolResultResponse validateDuplicateEmail(String email) {
    if (userRepository.existsByEmail(email)) {
      throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
    }
    return new BoolResultResponse(true);
  }

  @Transactional
  public UserLoginResponse login(UserLoginRequest request, HttpServletResponse response) {
    User user = userRepository.findByNickName(request.getNickName())
        .orElseThrow(() -> new BusinessException(ErrorCode.BAD_CREDENTIAL));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BusinessException(ErrorCode.BAD_CREDENTIAL);
    }

    String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
    String refreshToken = jwtTokenProvider.generateRefreshToken();

    // Redis에 Refresh Token 저장 (7일 만료)
    redisService.saveRefreshToken(user.getId(), refreshToken);

    // Refresh Token을 쿠키에 저장
    addRefreshTokenCookie(refreshToken, response);

    return new UserLoginResponse(accessToken);
  }

  private void addRefreshTokenCookie(String refreshToken,  HttpServletResponse response) {
    ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .path("/api/v1/auth/refresh") // 쿠키 전송 범위를 재발급 엔드포인트로 제한
        .maxAge(MAX_AGE_SECONDS)
        .build();

    response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  @Transactional(readOnly = true)
  public UserLoginResponse refreshAccessToken(HttpServletRequest request) {
    String refreshToken = extractRefreshTokenFromCookie(request);

    // Redis에 저장된 토큰 확인
    Long userId = redisService.getUserIdByRefreshToken(refreshToken);
    if (userId == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ACCESS);
    }

    // 새로운 Access Token 발급
    String newAccessToken = jwtTokenProvider.generateAccessToken(userId);
    return new UserLoginResponse(newAccessToken);
  }

  private String extractRefreshTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ACCESS);
    }
    for (Cookie cookie : request.getCookies()) {
      if ("refreshToken".equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    throw new BusinessException(ErrorCode.NOT_LOGIN_ACCESS);
  }

  @Transactional
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = extractRefreshTokenFromCookie(request);
    String accessToken = jwtTokenProvider.resolveToken(request);

    if (refreshToken != null && !refreshToken.isBlank()) {
      redisService.deleteRefreshToken(refreshToken);
    }

    if (accessToken != null && !accessToken.isBlank()) {
      try {
        // 토큰이 유효하면 남은 만료시간(ms)을 구함
        long remainingMs = jwtTokenProvider.getRemainingMilliseconds(accessToken);
        if (remainingMs > 0) {
          redisService.setBlackList(accessToken, remainingMs);
        }
      } catch (Exception e) {
        // 토큰이 이미 만료됐거나 검증 실패면 블랙리스트 등록X, 로그아웃은 처리
        log.debug("AccessToken 검증 실패 또는 만료: {}", e.getMessage());
      }
    }

    ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
        .httpOnly(true)
        .secure(true)
        .sameSite("None") // 원래 설정에 맞춰 동일하게
        .path("/api/v1/auth/refresh") // 쿠키 생성 시 사용한 path와 동일해야 함
        .maxAge(0)
        .build();
    response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
  }
}