package io.pablo.aicustomersupport.llm.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenAiApiResponse(
        String model,
        List<Choice> choices,
        Usage usage
) {
    public record Choice(
            Message message
    ) {
    }

    public record Message(
            String role,
            String content
    ) {
    }

    public record Usage(
            @JsonProperty("prompt_tokens") int promptTokens,
            @JsonProperty("completion_tokens") int completionTokens
    ) {
    }
}
