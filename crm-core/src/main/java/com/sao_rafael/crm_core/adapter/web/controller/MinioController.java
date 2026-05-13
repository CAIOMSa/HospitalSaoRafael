package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.adapter.web.dto.HostedFileDto;
import com.sao_rafael.crm_core.application.service.MinioStorageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/minio/files")
@CrossOrigin(origins = "*")
public class MinioController {

    private final MinioStorageService minioStorageService;

    public MinioController(MinioStorageService minioStorageService) {
        this.minioStorageService = minioStorageService;
    }

    @GetMapping
    public ResponseEntity<List<HostedFileDto>> listFiles(
            @RequestParam(name = "prefix", required = false, defaultValue = "relatorios") String prefix
    ) {
        return ResponseEntity.ok(minioStorageService.listFiles(prefix));
    }

    @PostMapping("/upload")
    public ResponseEntity<HostedFileDto> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "folder", required = false, defaultValue = "relatorios") String folder
    ) {
        return ResponseEntity.ok(minioStorageService.uploadFile(file, folder));
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(
            @RequestParam("path") String path
    ) {
        byte[] content = minioStorageService.downloadFile(path);
        String fileName = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }
}