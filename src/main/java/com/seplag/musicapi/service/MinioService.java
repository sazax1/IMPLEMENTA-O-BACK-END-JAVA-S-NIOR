package com.seplag.musicapi.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.presigned-url-expiry}")
    private int presignedUrlExpiry;

    public String uploadFile(MultipartFile file, Long albumId) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            String fileKey = String.format("albums/%d/%s%s", albumId, UUID.randomUUID(), extension);

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileKey)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build());
            }

            log.info("Arquivo {} enviado para MinIO com key: {}", originalFilename, fileKey);
            return fileKey;

        } catch (Exception e) {
            log.error("Erro ao fazer upload para MinIO: {}", e.getMessage());
            throw new RuntimeException("Falha ao fazer upload do arquivo", e);
        }
    }

    public String getPresignedUrl(String fileKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileKey)
                            .expiry(presignedUrlExpiry, TimeUnit.SECONDS)
                            .build());
        } catch (Exception e) {
            log.error("Erro ao gerar URL pré-assinada: {}", e.getMessage());
            throw new RuntimeException("Falha ao gerar URL pré-assinada", e);
        }
    }

    public void deleteFile(String fileKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileKey)
                            .build());
            log.info("Arquivo {} removido do MinIO", fileKey);
        } catch (Exception e) {
            log.error("Erro ao remover arquivo do MinIO: {}", e.getMessage());
        }
    }
}
