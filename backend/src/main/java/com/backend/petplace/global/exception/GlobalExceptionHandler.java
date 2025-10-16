package com.backend.petplace.global.exception;

import com.backend.petplace.global.response.ApiResponse;
import com.backend.petplace.global.response.ErrorCode;
import com.backend.petplace.global.response.ResponseCode;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<String>> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    // 모든 에러 메시지를 ", "로 연결하여 하나의 문자열로 만듭니다.
    String errorMessage = ex.getBindingResult()
        .getAllErrors()
        .stream()
        .map(error -> error.getDefaultMessage())
        .collect(Collectors.joining(", "));

    log.warn("Validation failed: {}", errorMessage);

    ApiResponse<String> response = new ApiResponse<>(
        ResponseCode.BAD_REQUEST.getCode(),
        ResponseCode.BAD_REQUEST.getMessage(),
        errorMessage
    );

    return new ResponseEntity<>(response, ResponseCode.BAD_REQUEST.getStatus());
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
    ErrorCode errorCode = ex.getErrorCode();
    log.error("Business exception occurred: Code - {}, Message - {}", errorCode.getCode(),
        errorCode.getMessage());

    ApiResponse<Void> response = ApiResponse.error(errorCode);

    return new ResponseEntity<>(response, errorCode.getStatus());
  }
}