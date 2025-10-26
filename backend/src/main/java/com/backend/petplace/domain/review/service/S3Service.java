package com.backend.petplace.domain.review.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.backend.petplace.domain.review.dto.response.PresignedUrlResponse;
import java.net.URL;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3Service {

  private final AmazonS3Client amazonS3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private static final long PRESIGNED_URL_EXPIRATION_MS = 1000 * 60 * 10;

  /**
   * S3에 파일을 PUT 방식으로 업로드할 수 있는 Presigned URL 생성
   */
  public PresignedUrlResponse generatePresignedUrl(String dirName, String originalFilename) {
    String uniqueFileName = createUniqueFileName(originalFilename, dirName);
    Date expiration = new Date(System.currentTimeMillis() + PRESIGNED_URL_EXPIRATION_MS);

    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(bucket, uniqueFileName)
            .withMethod(HttpMethod.PUT)
            .withExpiration(expiration);

    URL presignedUrl = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

    // 클라이언트에게 URL과 함께, 업로드 후 서버에 알려줄 파일 경로도 전달
    return new PresignedUrlResponse(presignedUrl.toString(), uniqueFileName);
  }

  public String getPublicUrl(String s3Path) {
    if (s3Path == null || s3Path.isEmpty()) {
      return null;
    }
    return amazonS3Client.getUrl(bucket, s3Path).toString();
  }

  private String createUniqueFileName(String originalFilename, String dirName) {
    String extension = "";
    if (originalFilename != null && originalFilename.contains(".")) {
      extension = originalFilename.substring(originalFilename.lastIndexOf("."));
    }
    // 디렉토리/UUID.확장자 형식으로 고유 파일명 생성
    return dirName + "/" + UUID.randomUUID() + extension;
  }
}
