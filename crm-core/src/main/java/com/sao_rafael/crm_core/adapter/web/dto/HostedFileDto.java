package com.sao_rafael.crm_core.adapter.web.dto;

import java.time.Instant;

public record HostedFileDto(
        String id,
        String name,
        Long size,
        Instant uploadedAt,
        String path,
        String downloadUrl
) {
}