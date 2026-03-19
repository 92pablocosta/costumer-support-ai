package io.pablo.aicustomersupport.memory.dto;

import io.pablo.aicustomersupport.message.entity.Message;

import java.util.List;

public record MemoryContext(
        List<Message> recentMessages,
        List<Message> relevantMessages
) {
}
