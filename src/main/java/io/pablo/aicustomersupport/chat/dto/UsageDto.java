package io.pablo.aicustomersupport.chat.dto;

public record UsageDto(
        int promptTokens,
        int completionTokens,
        int totalTokens,
        long responseTimeMs
) {
}
