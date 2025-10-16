package com.backend.petplace.domain.user.service;

import com.backend.petplace.domain.user.dto.request.UserSignupRequest;
import com.backend.petplace.domain.user.dto.response.UserSignupResponse;
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
  public UserSignupResponse signup(UserSignupRequest user) {
    // TODO: 이름 중복 체크, 길이 체크(2~12), 문자 형식 체크(영어, 한글, 숫자만 허용)
    // TODO: 비밀번호 형식 체크(프론트), 암호화
    // TODO: 이메일 중복 검사, 길이, 이메일 형식 등 검사 -> 이건 메일 인증 기능을 넣으면 안해도 됨
    // TODO: 주소지 검사는 딱히 안해도?
    // TODO: @Builder 사용 -> 암호화 때문에


    if (userRepository.existByUserName(user.getUserName())){
      throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
    }

    // 이메일 인증 후에 실행
    if (userRepository.existByEmail(user.getUserEmail())) {
      throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
    }

    return new UserSignupResponse();
  }
}
