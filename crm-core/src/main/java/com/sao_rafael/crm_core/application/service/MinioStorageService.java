package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.adapter.web.dto.HostedFileDto;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class MinioStorageService {

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioStorageService(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket-name}") String bucketName
    ) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucketName;
    }

    public List<HostedFileDto> listFiles(String prefix) {
        ensureBucketExists();
        String safePrefix = sanitizePrefix(prefix);
        List<HostedFileDto> files = new ArrayList<>();

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(safePrefix)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.isDir()) {
                    continue;
                }

                files.add(new HostedFileDto(
                        item.objectName(),
                        extractName(item.objectName()),
                        item.size(),
                        item.lastModified() != null ? item.lastModified().toInstant() : null,
                        item.objectName(),
                        ""
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao listar arquivos no MinIO.", e);
        }

        files.sort(Comparator.comparing(HostedFileDto::uploadedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return files;
    }

    public HostedFileDto uploadFile(MultipartFile file, String folder) {
        ensureBucketExists();
        String safeFolder = sanitizePrefix(folder);
        String objectName = buildObjectName(safeFolder, file.getOriginalFilename());

        try (InputStream stream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(stream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            var stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            Instant uploadedAt = stat.lastModified() != null ? stat.lastModified().toInstant() : Instant.now();

            return new HostedFileDto(
                    objectName,
                    extractName(objectName),
                    stat.size(),
                    uploadedAt,
                    objectName,
                    ""
            );
        } catch (Exception e) {
            throw new RuntimeException("Falha ao enviar arquivo para o MinIO.", e);
        }
    }

    public byte[] downloadFile(String path) {
        ensureBucketExists();
        String objectName = sanitizeObjectPath(path);

        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        )) {
            return stream.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao baixar arquivo do MinIO.", e);
        }
    }

    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao validar bucket no MinIO.", e);
        }
    }

    private static String sanitizePrefix(String prefix) {
        String value = prefix == null ? "" : prefix.trim();
        value = value.replace("\\", "/").replace("..", "");
        if (value.startsWith("/")) {
            value = value.substring(1);
        }
        if (!value.isEmpty() && !value.endsWith("/")) {
            value = value + "/";
        }
        return value;
    }

    private static String sanitizeObjectPath(String path) {
        String value = path == null ? "" : path.trim();
        value = value.replace("\\", "/").replace("..", "");
        while (value.startsWith("/")) {
            value = value.substring(1);
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("Caminho do arquivo e obrigatorio.");
        }
        return value;
    }

    private static String buildObjectName(String folder, String originalName) {
        String cleanName = originalName == null || originalName.isBlank() ? "arquivo.bin" : originalName.trim().replace("/", "-");
        return folder + UUID.randomUUID() + "-" + cleanName;
    }

    private static String extractName(String objectName) {
        int index = objectName.lastIndexOf('/');
        if (index < 0 || index >= objectName.length() - 1) {
            return objectName;
        }
        return objectName.substring(index + 1);
    }
}