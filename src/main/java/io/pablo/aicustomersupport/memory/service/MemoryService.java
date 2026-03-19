package io.pablo.aicustomersupport.memory.service;

import io.pablo.aicustomersupport.message.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MemoryService {

    List<Message> getRelevantHistory(UUID tenantId, UUID conversationId, int limit);
}
