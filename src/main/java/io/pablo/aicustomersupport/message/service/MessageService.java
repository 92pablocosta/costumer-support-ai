package io.pablo.aicustomersupport.message.service;

import io.pablo.aicustomersupport.conversation.entity.Conversation;
import io.pablo.aicustomersupport.message.dto.MessageListResponse;
import io.pablo.aicustomersupport.message.dto.MessageResponse;
import io.pablo.aicustomersupport.message.entity.Message;
import io.pablo.aicustomersupport.message.entity.MessageRole;
import io.pablo.aicustomersupport.tenant.entity.Tenant;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message createMessage(Tenant tenant,
                          Conversation conversation,
                          MessageRole role,
                          String content,
                          String modelName,
                          Integer promptTokens,
                          Integer completionTokens);

    List<Message> getConversationMessages(UUID tenantId, UUID conversationId);

    MessageListResponse getConversationMessagesResponse(UUID tenantId, UUID conversationId);

    MessageResponse toResponse(Message message);
}
