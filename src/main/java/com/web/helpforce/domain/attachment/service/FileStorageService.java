package com.web.helpforce.domain.attachment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public String storeFile(MultipartFile file) {
        try {
            // 파일 크기 검증 (10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
            }

            log.info("파일 저장 시작: {}, 크기: {}", file.getOriginalFilename(), file.getSize());

            // 업로드 디렉토리 생성
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("업로드 디렉토리 생성: {}", uploadPath.toAbsolutePath());
            }

            // 파일명 생성 (UUID + 원본 파일명)
            String originalFilename = file.getOriginalFilename();
            String filename = UUID.randomUUID().toString() + "_" + originalFilename;

            // 파일 저장
            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("파일 저장 완료: {}", targetLocation.toAbsolutePath());

            // URL 형태로 반환 (실제로는 CDN URL 또는 서버 URL로 반환)
            return "/uploads/" + filename;

        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }
}
