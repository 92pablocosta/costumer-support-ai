package io.pablo.aicustomersupport.llm.dto;

import java.util.List;

public record LlmChatRequest(
        String model,
        List<LlmMessage> messages
) {
}
