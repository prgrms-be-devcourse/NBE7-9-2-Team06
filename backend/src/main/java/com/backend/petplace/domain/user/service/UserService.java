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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final EmailAuthCodeRepository emailAuthCodeRepository;

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

  @Transactional(readOnly = true)
  public UserLoginResponse login(UserLoginRequest request) {
    User user = userRepository.findByNickName(request.getNickName())
        .orElseThrow(() -> new BusinessException(ErrorCode.BAD_CREDENTIAL));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BusinessException(ErrorCode.BAD_CREDENTIAL);
    }

    return new UserLoginResponse(jwtTokenProvider.generateAccessToken(user.getId()));
  }
}