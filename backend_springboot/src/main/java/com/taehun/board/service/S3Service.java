package com.taehun.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final String awsRegion; // 의존성 주입으로 리전 정보 가져옴

    public String uploadFile(InputStream inputStream, String fileName, String contentType) {
        if (!isSupportedFileType(fileName, contentType)) {
            throw new RuntimeException("지원되지 않는 파일 형식입니다.");
        }

        String uniqueFileName = UUID.randomUUID() + "_" + fileName;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(uniqueFileName)
                            .contentType(contentType)
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, inputStream.available())
            );
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
        }

        return getPublicUrl(uniqueFileName);
    }

    private boolean isSupportedFileType(String fileName, String contentType) {
        List<String> supportedExtensions = List.of("jpg", "jpeg", "png");
        List<String> supportedContentTypes = List.of("image/jpeg", "image/png");

        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return supportedExtensions.contains(fileExtension) && supportedContentTypes.contains(contentType);
    }

    private String getPublicUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                awsRegion, // 주입된 리전 사용
                fileName
        );
    }
}

