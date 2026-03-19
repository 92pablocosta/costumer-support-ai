package io.pablo.aicustomersupport.chat.service;

import io.pablo.aicustomersupport.chat.dto.ChatRequest;
import io.pablo.aicustomersupport.chat.dto.ChatResponse;

import java.util.UUID;

public interface ChatService {

    ChatResponse sendMessage(UUID tenantId, UUID conversationId, ChatRequest request);
}
