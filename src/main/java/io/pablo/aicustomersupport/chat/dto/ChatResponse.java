package io.pablo.aicustomersupport.chat.dto;

import io.pablo.aicustomersupport.message.dto.MessageResponse;

import java.util.UUID;

public record ChatResponse(
        UUID tenantId,
        UUID conversationId,
        MessageResponse userMessage,
        MessageResponse assistantMessage,
        UsageDto usage
) {
}
