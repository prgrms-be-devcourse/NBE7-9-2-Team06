package com.backend.petplace.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Presigned URL 응답 DTO")
public class PresignedUrlResponse {

  @Schema(description = "클라이언트가 PUT 요청 보낼 URL")
  private String presignedUrl;

  @Schema(description = "업로드 완료 후 서버에 전달할 S3 파일 경로")
  private String s3FilePath;
}
