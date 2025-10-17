package com.backend.petplace.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 도메인별 발생할 Error Code 담당 enum
 */
@Getter
public enum ErrorCode {

  // 회원
  CONFLICT_REGISTER("U001", HttpStatus.CONFLICT, "이미 가입된 회원입니다."),
  NOT_FOUND_MEMBER("U002", HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
  BAD_CREDENTIAL("U004", HttpStatus.UNAUTHORIZED, "아이디나 비밀번호가 틀렸습니다."),
  INVALID_TOKEN("U005", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
  EXPIRED_TOKEN("U006", HttpStatus.UNAUTHORIZED, "엑세스 토큰이 만료되었습니다. 토큰을 갱신해주세요."),
  NOT_LOGIN_ACCESS("U007", HttpStatus.UNAUTHORIZED, "로그인되어 있지 않습니다. 로그인 해 주십시오."),

  // S3 - 이미지 업로드
  FAIL_TO_UPLOAD_IMAGE("S001", HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
  INVALID_FILE_NAME("S002", HttpStatus.BAD_REQUEST, "잘못된 형식의 파일 이름입니다."),
  ;

  private final String code;
  private final HttpStatus status;
  private final String message;

  ErrorCode(String code, HttpStatus status, String message) {
    this.code = code;
    this.status = status;
    this.message = message;
  }
}