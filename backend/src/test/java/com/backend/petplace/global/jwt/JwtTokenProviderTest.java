package com.backend.petplace.global.jwt;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

  @InjectMocks
  private JwtTokenProvider jwtTokenProvider;

  @Mock
  private UserDetailsService userDetailsService;

  @BeforeEach
  void setUp() {
    String base64Key = Base64.getEncoder().encodeToString("test-secret-key-12345678901234567890".getBytes());
    ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", base64Key);
    ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpirationMilliseconds", 60000L); // 1분
    jwtTokenProvider.init();
  }

  @DisplayName("JwtTokenProvider 통합 테스트: 암호화된 토큰 생성 & 파싱 확인")
  @Test
  void token_shouldBeGeneratedAndParsedCorrectly() {
    // 토큰 생성
    String token = jwtTokenProvider.generateAccessToken(123L);
    assertNotNull(token);

    // 토큰 검증
    assertDoesNotThrow(() -> jwtTokenProvider.validateToken(token));

    // 인증 정보 추출
    UserDetails mockUser = new User("123", "pw", List.of());
    when(userDetailsService.loadUserByUsername("123")).thenReturn(mockUser);

    Authentication auth = jwtTokenProvider.getAuthentication(token);
    assertEquals("123", auth.getName());
  }
}