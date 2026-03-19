package io.pablo.aicustomersupport.conversation.service;

import io.pablo.aicustomersupport.conversation.dto.ConversationResponse;
import io.pablo.aicustomersupport.conversation.dto.CreateConversationRequest;
import io.pablo.aicustomersupport.conversation.entity.Conversation;

import java.util.List;
import java.util.UUID;

public interface ConversationService {

    ConversationResponse createConversation(UUID tenantId, CreateConversationRequest request);

    ConversationResponse getConversation(UUID tenantId, UUID conversationId);

    List<ConversationResponse> listConversations(UUID tenantId);

    Conversation getConversationEntity(UUID tenantId, UUID conversationId);
}
