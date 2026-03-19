package io.pablo.aicustomersupport.llm.client;

import java.util.List;

public record OpenAiApiRequest(
        String model,
        List<OpenAiApiMessage> messages
) {
    public record OpenAiApiMessage(
            String role,
            String content
    ) {
    }
}
