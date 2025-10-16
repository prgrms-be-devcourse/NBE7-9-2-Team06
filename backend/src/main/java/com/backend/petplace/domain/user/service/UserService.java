package com.backend.petplace.domain.user.service;

import com.backend.petplace.domain.user.dto.request.UserSignupRequest;
import com.backend.petplace.domain.user.dto.response.UserSignupResponse;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public UserSignupResponse signup(UserSignupRequest request) {

    if (userRepository.existByUserName(request.getName())) {
      throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
    }

    // TODO:이메일 인증 기능 추가

    if (userRepository.existByEmail(request.getEmail())) {
      throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
    }

    User user = User.builder()
        .name(request.getName())
        .password(passwordEncoder.encode(request.getPassword()))
        .email(request.getEmail())
        .address(request.getAddress())
        .zipcode(request.getZipcode())
        .addressDetail(request.getAddressDetail())
        .build();

    userRepository.save(user);

    return new UserSignupResponse(user.getId());
  }
}
