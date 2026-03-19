package io.pablo.aicustomersupport.conversation.dto;

import jakarta.validation.constraints.Size;

public record CreateConversationRequest(
        @Size(max = 100) String customerId,
        @Size(max = 255) String subject
) {
}
