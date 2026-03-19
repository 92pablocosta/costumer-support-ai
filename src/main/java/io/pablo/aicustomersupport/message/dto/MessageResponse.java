package io.pablo.aicustomersupport.message.dto;

import io.pablo.aicustomersupport.message.entity.MessageRole;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        MessageRole role,
        String content,
        Integer messageOrder,
        String modelName,
        Instant createdAt
) {
}
