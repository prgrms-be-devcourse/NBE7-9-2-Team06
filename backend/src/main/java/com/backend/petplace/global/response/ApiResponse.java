package com.backend.petplace.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiResponse<T> {

  private String code;
  private String message;
  private T data;

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(ResponseCode.OK.getCode(), ResponseCode.OK.getMessage(), data);
  }

  public static <T> ApiResponse<T> success() {
    return new ApiResponse<>(ResponseCode.OK.getCode(), ResponseCode.OK.getMessage(), null);
  }

  public static <T> ApiResponse<T> create() {
    return new ApiResponse<>(ResponseCode.CREATED.getCode(), ResponseCode.CREATED.getMessage(), null);
  }

  public static <T> ApiResponse<T> create(T data) {
    return new ApiResponse<>(ResponseCode.CREATED.getCode(), ResponseCode.CREATED.getMessage(), data);
  }

  public static <T> ApiResponse<T> error(ResponseCode code) {
    return new ApiResponse<>(code.getCode(), code.getMessage(), null);
  }

  public static <T> ApiResponse<T> error(ErrorCode code) {
    return new ApiResponse<>(code.getCode(), code.getMessage(), null);
  }
}