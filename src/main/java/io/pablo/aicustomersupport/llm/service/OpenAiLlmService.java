package io.pablo.aicustomersupport.llm.service;

import io.pablo.aicustomersupport.common.config.OpenAiProperties;
import io.pablo.aicustomersupport.common.exception.ExternalServiceException;
import io.pablo.aicustomersupport.llm.client.OpenAiApiRequest;
import io.pablo.aicustomersupport.llm.client.OpenAiApiResponse;
import io.pablo.aicustomersupport.llm.dto.LlmChatRequest;
import io.pablo.aicustomersupport.llm.dto.LlmChatResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class OpenAiLlmService implements LlmService {

    private final RestClient openAiRestClient;
    private final OpenAiProperties openAiProperties;

    public OpenAiLlmService(RestClient openAiRestClient, OpenAiProperties openAiProperties) {
        this.openAiRestClient = openAiRestClient;
        this.openAiProperties = openAiProperties;
    }

    @Override
    public LlmChatResponse generateReply(LlmChatRequest request) {
        if (openAiProperties.getApiKey() == null || openAiProperties.getApiKey().isBlank()) {
            throw new ExternalServiceException("OpenAI API key is not configured");
        }

        OpenAiApiRequest payload = new OpenAiApiRequest(
                request.model(),
                request.messages().stream()
                        .map(message -> new OpenAiApiRequest.OpenAiApiMessage(message.role(), message.content()))
                        .toList()
        );

        try {
            OpenAiApiResponse response = openAiRestClient.post()
                    .uri(openAiProperties.getChatPath())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiProperties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(OpenAiApiResponse.class);

            if (response == null || response.choices() == null || response.choices().isEmpty()) {
                throw new ExternalServiceException("OpenAI returned an empty response");
            }

            String content = response.choices().get(0).message().content();
            int promptTokens = response.usage() != null ? response.usage().promptTokens() : 0;
            int completionTokens = response.usage() != null ? response.usage().completionTokens() : 0;

            return new LlmChatResponse(content, response.model(), promptTokens, completionTokens);
        } catch (RestClientException exception) {
            throw new ExternalServiceException("Failed to call OpenAI", exception);
        }
    }
}
