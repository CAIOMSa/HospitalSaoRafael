package com.sao_rafael.crm_core.adapter.web.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ServiceStatusDto {
    String name;
    String url;
    String status; // online | offline | degraded
    Long responseTimeMs;
    String error;
}
