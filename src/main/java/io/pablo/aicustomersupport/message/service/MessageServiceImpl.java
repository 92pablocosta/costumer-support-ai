package io.pablo.aicustomersupport.message.service;

import io.pablo.aicustomersupport.conversation.entity.Conversation;
import io.pablo.aicustomersupport.conversation.service.ConversationService;
import io.pablo.aicustomersupport.message.dto.MessageListResponse;
import io.pablo.aicustomersupport.message.dto.MessageResponse;
import io.pablo.aicustomersupport.message.entity.Message;
import io.pablo.aicustomersupport.message.entity.MessageRole;
import io.pablo.aicustomersupport.message.repository.MessageRepository;
import io.pablo.aicustomersupport.tenant.entity.Tenant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationService conversationService;

    public MessageServiceImpl(MessageRepository messageRepository, ConversationService conversationService) {
        this.messageRepository = messageRepository;
        this.conversationService = conversationService;
    }

    @Override
    public Message createMessage(Tenant tenant,
                                 Conversation conversation,
                                 MessageRole role,
                                 String content,
                                 String modelName,
                                 Integer promptTokens,
                                 Integer completionTokens) {
        int nextOrder = messageRepository.findTopByTenantIdAndConversationIdOrderByMessageOrderDesc(
                        tenant.getId(),
                        conversation.getId()
                )
                .map(existing -> existing.getMessageOrder() + 1)
                .orElse(1);

        Message message = new Message();
        message.setId(UUID.randomUUID());
        message.setTenant(tenant);
        message.setConversation(conversation);
        message.setRole(role);
        message.setContent(content);
        message.setMessageOrder(nextOrder);
        message.setModelName(modelName);
        message.setPromptTokens(promptTokens);
        message.setCompletionTokens(completionTokens);
        return messageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getConversationMessages(UUID tenantId, UUID conversationId) {
        conversationService.getConversationEntity(tenantId, conversationId);
        return messageRepository.findByTenantIdAndConversationIdOrderByMessageOrderAsc(tenantId, conversationId);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageListResponse getConversationMessagesResponse(UUID tenantId, UUID conversationId) {
        List<MessageResponse> messages = getConversationMessages(tenantId, conversationId).stream()
                .map(this::toResponse)
                .toList();
        return new MessageListResponse(conversationId, tenantId, messages);
    }

    @Override
    public MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getMessageOrder(),
                message.getModelName(),
                message.getCreatedAt()
        );
    }
}
