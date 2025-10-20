package com.backend.petplace.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 도메인별 발생할 Error Code 담당 enum
 */
@Getter
public enum ErrorCode {

  // 회원
  DUPLICATE_NICKNAME("U001", HttpStatus.CONFLICT, "이미 사용 중인 이름입니다."),
  DUPLICATE_EMAIL("U002", HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
  NOT_FOUND_MEMBER("U003", HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
  BAD_CREDENTIAL("U004", HttpStatus.UNAUTHORIZED, "아이디나 비밀번호가 틀렸습니다."),
  INVALID_TOKEN("U005", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
  EXPIRED_TOKEN("U006", HttpStatus.UNAUTHORIZED, "엑세스 토큰이 만료되었습니다. 토큰을 갱신해주세요."),
  NOT_LOGIN_ACCESS("U007", HttpStatus.UNAUTHORIZED, "로그인되어 있지 않습니다. 로그인 해 주십시오."),

  // 장소
  NOT_FOUND_PLACE("P001", HttpStatus.NOT_FOUND, "존재하지 않는 장소입니다."),

  // S3 - 이미지 업로드
  FAIL_TO_UPLOAD_IMAGE("S001", HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
  INVALID_FILE_NAME("S002", HttpStatus.BAD_REQUEST, "잘못된 형식의 파일 이름입니다."),

  // 주문, 주문상품, 상품
  NOT_FOUND_PRODUCT("ORO002", HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
  NOT_ENOUGH_STOCK("ORO003", HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
  NOT_ENOUGH_POINT("ORO004", HttpStatus.BAD_REQUEST, "포인트가 부족합니다."),
  NOT_FOUND_ORDER("ORO005", HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다."),
  INVALID_ORDER_STATUS("ORO006", HttpStatus.BAD_REQUEST, "취소할 수 없는 주문 상태입니다."),

  // 반려동물
  NOT_FOUND_PET("PET001", HttpStatus.NOT_FOUND, "존재하지 않는 반려동물입니다.");

  private final String code;
  private final HttpStatus status;
  private final String message;

  ErrorCode(String code, HttpStatus status, String message) {
    this.code = code;
    this.status = status;
    this.message = message;
  }
}