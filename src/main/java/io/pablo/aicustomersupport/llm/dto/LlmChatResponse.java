package io.pablo.aicustomersupport.llm.dto;

public record LlmChatResponse(
        String content,
        String model,
        int promptTokens,
        int completionTokens
) {
}
