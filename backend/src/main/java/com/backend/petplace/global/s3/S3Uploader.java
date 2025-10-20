package com.backend.petplace.global.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.backend.petplace.global.exception.BusinessException;
import com.backend.petplace.global.response.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class S3Uploader {

  private final AmazonS3Client amazonS3Client;
  private final String bucket;

  public S3Uploader(AmazonS3Client amazonS3Client, @Value("${cloud.aws.s3.bucket}") String bucket) {
    this.amazonS3Client = amazonS3Client;
    this.bucket = bucket;
  }

  public String upload(MultipartFile multipartFile, String dirName) {
    validateFileName(multipartFile.getOriginalFilename());

    String uniqueFileName = createUniqueFileName(multipartFile.getOriginalFilename(), dirName);
    ObjectMetadata metadata = createObjectMetadata(multipartFile);

    try {
      amazonS3Client.putObject(bucket, uniqueFileName, multipartFile.getInputStream(), metadata);
    } catch (IOException e) {
      throw new BusinessException(ErrorCode.FAIL_TO_UPLOAD_IMAGE, e);
    }
    return amazonS3Client.getUrl(bucket, uniqueFileName).toString();
  }

  private void validateFileName(String originalFilename) {
    if (!StringUtils.hasText(originalFilename)) {
      throw new BusinessException(ErrorCode.INVALID_FILE_NAME);
    }
  }

  private ObjectMetadata createObjectMetadata(MultipartFile multipartFile) {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(multipartFile.getSize());
    metadata.setContentType(multipartFile.getContentType());
    return metadata;
  }

  private String createUniqueFileName(String originalFilename, String dirName) {
    return dirName + "/" + UUID.randomUUID() + "-" + originalFilename;
  }
}