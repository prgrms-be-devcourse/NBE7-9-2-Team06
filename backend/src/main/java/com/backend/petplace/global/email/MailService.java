package com.backend.petplace.global.email;

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

@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender javaMailSender;
  private static final String TITLE = "[PetPlace] Email 인증 코드";

  @Value("${spring.mail.username}")
  private String senderEmail;

  public String createCode() {
    Random random = new Random();
    StringBuilder key = new StringBuilder();

    for (int i = 0; i < 6; i++) { // 인증 코드 6자리
      int index = random.nextInt(2); // 0~1까지 랜덤, 랜덤값으로 switch문 실행

      switch (index) {
        case 0 -> key.append((char) (random.nextInt(26) + 65)); // 대문자
        case 1 -> key.append(random.nextInt(10)); // 숫자
      }
    }
    return key.toString();
  }

  public MimeMessage createMail(String mail, String authCode) {
    MimeMessage message = javaMailSender.createMimeMessage();

    ValidateEmailFormat(mail);

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

  private void ValidateEmailFormat(String email) {
     if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
       throw new BusinessException(ErrorCode.INVALID_EMAIL_FORMAT);
     }
  }

  // 메일 발송
  public void sendMail(String sendEmail) {
    String authCode = createCode(); // 랜덤 인증번호 생성
    MimeMessage message = createMail(sendEmail, authCode); // 메일 생성

    try {
      javaMailSender.send(message); // 메일 발송
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
  }
}