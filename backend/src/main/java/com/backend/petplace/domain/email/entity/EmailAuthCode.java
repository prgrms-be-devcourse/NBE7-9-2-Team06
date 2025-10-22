package com.backend.petplace.domain.email.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailAuthCode {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String authCode;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private LocalDateTime expiredAt;

  @Builder
  public EmailAuthCode(String authCode, String email, LocalDateTime expiredAt) {
    this.authCode = authCode;
    this.email = email;
    this.expiredAt = expiredAt;
  }

  public static EmailAuthCode create(String email, String authCode, long authCodeExpirationTime) {
    return EmailAuthCode.builder()
        .email(email)
        .authCode(authCode)
        .expiredAt(LocalDateTime.now().plusMinutes(authCodeExpirationTime))
        .build();
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiredAt);
  }
}
