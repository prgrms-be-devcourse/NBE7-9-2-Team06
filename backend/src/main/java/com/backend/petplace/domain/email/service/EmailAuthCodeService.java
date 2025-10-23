package com.backend.petplace.domain.email.service;

import com.backend.petplace.domain.email.dto.request.CheckAuthCodeRequest;
import com.backend.petplace.domain.email.entity.EmailAuthCode;
import com.backend.petplace.domain.email.repository.EmailAuthCodeRepository;
import com.backend.petplace.domain.user.dto.response.BoolResultResponse;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.net.SocketTimeoutException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailAuthCodeService {

  private final JavaMailSender javaMailSender;
  private final EmailAuthCodeRepository emailAuthCodeRepository;

  @Value("${spring.mail.username}")
  private String senderEmail;

  @Value("${spring.mail.properties.auth-code-expiration-minutes}")
  private long authCodeExpirationTime;

  private static final String TITLE = "[PetPlace] Email 인증 코드";

  public String createCode() {
    Random random = new Random();
    StringBuilder key = new StringBuilder();

    for (int i = 0; i < 7; i++) { // 인증 코드 6자리
      int index = random.nextInt(3); // 0: 대문자, 1: 소문자, 2: 숫자, 랜덤값으로 switch문 실행

      switch (index) {
        case 0 -> key.append((char) (random.nextInt(26) + 65)); // A–Z
        case 1 -> key.append((char) (random.nextInt(26) + 97)); // a–z
        case 2 -> key.append(random.nextInt(10));               // 0–9
      }
    }
    return key.toString();
  }

  public MimeMessage createMail(String mail, String authCode) {
    MimeMessage message = javaMailSender.createMimeMessage();

    try {
      message.setFrom(senderEmail);
      message.setRecipients(MimeMessage.RecipientType.TO, mail);
      message.setSubject(TITLE);
      String body = "";
      body += "<h3>요청하신 인증 번호입니다.</h3>";
      body += "<h1>" + authCode + "</h1>";
      body += "<h3>감사합니다.</h3>";
      message.setText(body, "UTF-8", "html");
    } catch (MessagingException e) {
      throw new BusinessException(ErrorCode.MAIL_CREATION_FAILED);
    }
    return message;
  }

  // 메일 발송
  @Transactional
  public BoolResultResponse sendMail(String sendEmail) {
    String authCode = createCode(); // 랜덤 인증번호 생성
    MimeMessage message = createMail(sendEmail, authCode); // 메일 생성

    try {
      javaMailSender.send(message); // 메일 발송
      saveEmailAuthCode(sendEmail, authCode);
    } catch (MailAuthenticationException e) {
      throw new BusinessException(ErrorCode.MAIL_AUTH_FAILED);
    } catch (MailSendException e) {
      if (e.getCause() instanceof SocketTimeoutException) {
        throw new BusinessException(ErrorCode.SMTP_CONNECTION_FAILED);
      }
      throw new BusinessException(ErrorCode.MAIL_SEND_FAILED);
    } catch (MailException e) {
      throw new BusinessException(ErrorCode.MAIL_SEND_FAILED);
    }
    return  new BoolResultResponse(true);
  }

  @Transactional
  protected void saveEmailAuthCode(String email, String authCode) {
    EmailAuthCode emailAuthCode = EmailAuthCode.create(email, authCode, authCodeExpirationTime);
    emailAuthCodeRepository.save(emailAuthCode);
  }

  @Transactional
  public BoolResultResponse checkAuthCode(CheckAuthCodeRequest request) {
    EmailAuthCode emailAuthCode = emailAuthCodeRepository.findByEmailAndAuthCode
            (request.getEmail(), request.getAuthCode())
        .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_CODE_NOT_FOUND));

    if (emailAuthCode.isExpired()) {
      throw new BusinessException(ErrorCode.AUTH_CODE_EXPIRED);
    }

    // 성공하면 인증번호 값을 true로 변경
    if (!emailAuthCode.isVerified()) {
      emailAuthCode.markVerifiedTrue();
    }
    return new BoolResultResponse(true);
  }
}