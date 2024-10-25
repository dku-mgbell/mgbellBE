package com.mgbell.global.s3.service;

import com.mgbell.global.s3.exception.FailedToLoadImage;
import com.mgbell.global.s3.exception.ImageNotFound;
import com.mgbell.global.s3.exception.OnlyJpegOrPngIsAvailable;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Operations s3Operations;

    @Transactional
    public void upload(MultipartFile image, String fileDir) {
        if (!MediaType.IMAGE_PNG.toString().equals(image.getContentType()) &&
                !MediaType.IMAGE_JPEG.toString().equals(image.getContentType()))
            throw new OnlyJpegOrPngIsAvailable();

        try (InputStream is = image.getInputStream()) {
            s3Operations.upload(bucket, fileDir, is,
                    ObjectMetadata.builder().contentType(image.getContentType()).build());

//            s3Operations.store(bucket, fileDir, convertToThumbnail(image, fileDir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // todo 썸네일 변환한거 s3에 다시 올라가게
    private File convertToThumbnail(MultipartFile imageFile, String fileDir) throws IOException {
        File file = File.createTempFile(Objects.requireNonNull(imageFile.getOriginalFilename()), "");
        Thumbnails.of(imageFile.getInputStream())
                .size(80, 80)
                .toFile(file);

        return file;
    }

    @Transactional
    public void delete(String fileDir){

        s3Operations.deleteObject(bucket, fileDir);
    }

    @Transactional
    public ResponseEntity<?> download(String fileDir) {
        S3Resource s3Resource = s3Operations.download(bucket, fileDir);

        if (MediaType.IMAGE_PNG.toString().equals(s3Resource.contentType())) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(s3Resource);
        }

        if (MediaType.IMAGE_JPEG.toString().equals(s3Resource.contentType())) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(s3Resource);
        }

        return ResponseEntity.badRequest().body("사진 파일만 다운로드 가능합니다");
    }
}
