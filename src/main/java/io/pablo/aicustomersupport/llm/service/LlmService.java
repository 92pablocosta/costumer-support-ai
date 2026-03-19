package io.pablo.aicustomersupport.llm.service;

import io.pablo.aicustomersupport.llm.dto.LlmChatRequest;
import io.pablo.aicustomersupport.llm.dto.LlmChatResponse;

public interface LlmService {

    LlmChatResponse generateReply(LlmChatRequest request);
}
