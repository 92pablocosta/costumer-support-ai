package io.pablo.aicustomersupport.conversation.dto;

import io.pablo.aicustomersupport.conversation.entity.ConversationStatus;

import java.time.Instant;
import java.util.UUID;

public record ConversationResponse(
        UUID id,
        UUID tenantId,
        String customerId,
        String subject,
        ConversationStatus status,
        Instant createdAt
) {
}
