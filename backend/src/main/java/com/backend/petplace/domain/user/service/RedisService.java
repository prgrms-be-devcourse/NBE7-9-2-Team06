package com.backend.petplace.domain.user.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final RedisTemplate<String, Object> redisTemplate;

  // RefreshToken 저장
  public void saveRefreshToken(Long userId, String refreshToken, long expirationMillis) {
    String key = "RefreshToken:" + userId; // 키 이름 예시: RefreshToken:123
    redisTemplate.opsForValue().set(key, refreshToken, expirationMillis, TimeUnit.MILLISECONDS);
  }

  // RefreshToken 조회
  public String getRefreshToken(Long userId) {
    String key = "RefreshToken:" + userId;
    Object value = redisTemplate.opsForValue().get(key);
    return value != null ? value.toString() : null;
  }

  // RefreshToken 삭제 (로그아웃 시)
  public void deleteRefreshToken(Long userId) {
    String key = "RefreshToken:" + userId;
    redisTemplate.delete(key);
  }

  // AccessToken 블랙리스트 등록
  public void setBlackList(String accessToken, long expirationMillis) {
    String key = "BlackList:" + accessToken;
    redisTemplate.opsForValue().set(key, "logout", expirationMillis, TimeUnit.MILLISECONDS);
  }

  // AccessToken이 블랙리스트에 있는지 확인
  public boolean isBlackListed(String accessToken) {
    String key = "BlackList:" + accessToken;
    return redisTemplate.hasKey(key);
  }
}