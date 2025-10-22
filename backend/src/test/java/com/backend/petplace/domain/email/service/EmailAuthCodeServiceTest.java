package com.backend.petplace.domain.email.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.backend.petplace.domain.email.dto.request.CheckAuthCodeRequest;
import com.backend.petplace.domain.email.entity.EmailAuthCode;
import com.backend.petplace.domain.email.repository.EmailAuthCodeRepository;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailAuthCodeServiceTest {

  @Mock
  private EmailAuthCodeRepository emailAuthCodeRepository;

  @InjectMocks
  private EmailAuthCodeService emailAuthCodeService;

  @Test
  void 인증번호가_만료되었으면_예외를_던진다() {
    // given
    String email = "test@example.com";
    String authCode = "ABC1234";

    EmailAuthCode expiredCode = EmailAuthCode.create(email, authCode, -1); // 이미 만료됨
    CheckAuthCodeRequest request = new CheckAuthCodeRequest(email, authCode);

    when(emailAuthCodeRepository.findByEmailAndAuthCode(email, authCode))
        .thenReturn(Optional.of(expiredCode));

    // when & then
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      emailAuthCodeService.checkAuthCode(request);
    });

    assertEquals(ErrorCode.AUTH_CODE_EXPIRED, exception.getErrorCode());
  }
}