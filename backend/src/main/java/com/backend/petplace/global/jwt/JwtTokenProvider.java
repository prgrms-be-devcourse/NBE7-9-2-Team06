package com.backend.petplace.global.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.access-expiration-ms}")
  private long accessTokenExpirationMilliseconds;

  private Key key;

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
}
