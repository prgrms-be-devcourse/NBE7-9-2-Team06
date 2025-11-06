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
    String key = "RT:" + refreshToken;
    redisTemplate.opsForValue().set(key, userId.toString(), expirationMillis, TimeUnit.MILLISECONDS);
  }

  // RefreshToken 조회
  public Long getUserIdByRefreshToken(String refreshToken) {
    String value = (String) redisTemplate.opsForValue().get("RT:" + refreshToken);
    return value != null ? Long.parseLong(value) : null;
  }

  // RefreshToken 삭제 (로그아웃 시)
  public void deleteRefreshToken(String refreshToken) {
    redisTemplate.delete("RT:" + refreshToken);
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