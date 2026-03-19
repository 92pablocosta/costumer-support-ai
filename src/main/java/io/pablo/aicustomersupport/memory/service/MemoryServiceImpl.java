package io.pablo.aicustomersupport.memory.service;

import io.pablo.aicustomersupport.message.entity.Message;
import io.pablo.aicustomersupport.message.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class MemoryServiceImpl implements MemoryService {

    private final MessageService messageService;

    public MemoryServiceImpl(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public List<Message> getRelevantHistory(UUID tenantId, UUID conversationId, int limit) {
        List<Message> messages = messageService.getConversationMessages(tenantId, conversationId);
        return messages.stream()
                .sorted(Comparator.comparing(Message::getMessageOrder).reversed())
                .limit(limit)
                .sorted(Comparator.comparing(Message::getMessageOrder))
                .toList();
    }
}
