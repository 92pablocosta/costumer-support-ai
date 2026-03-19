package io.pablo.aicustomersupport.tenant.dto;

import io.pablo.aicustomersupport.tenant.entity.TenantStatus;

import java.time.Instant;
import java.util.UUID;

public record TenantResponse(
        UUID id,
        String name,
        String externalKey,
        TenantStatus status,
        Instant createdAt
) {
}
