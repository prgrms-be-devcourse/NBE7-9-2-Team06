package com.backend.petplace.global.jwt;

import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.access-expiration-ms}")
  private long accessTokenExpirationMilliseconds;

  private SecretKey key;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  }

  public String generateAccessToken(Long userId) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + accessTokenExpirationMilliseconds);

    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userId);

    return Jwts.builder()
        .claims(claims)
        .issuedAt(now)
        .expiration(expiry)
        .signWith(key)
        .compact();
  }

  public void validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token);
    } catch (ExpiredJwtException e) {
      throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
    } catch (UnsupportedJwtException e) {
      throw new BusinessException(ErrorCode.UNSUPPORTED_TOKEN);
    } catch (IllegalArgumentException e) {
      throw new BusinessException(ErrorCode.EMPTY_TOKEN);
    } catch (JwtException e) {
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }
  }
}
