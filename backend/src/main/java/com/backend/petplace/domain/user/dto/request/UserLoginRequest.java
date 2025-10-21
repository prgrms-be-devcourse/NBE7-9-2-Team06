package com.backend.petplace.domain.user.dto.request;

public record UserLoginRequest (
  String nickname,
  String password
) {}
