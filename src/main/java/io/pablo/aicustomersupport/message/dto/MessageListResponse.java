package io.pablo.aicustomersupport.message.dto;

import java.util.List;
import java.util.UUID;

public record MessageListResponse(
        UUID conversationId,
        UUID tenantId,
        List<MessageResponse> messages
) {
}
