package io.pablo.aicustomersupport.memory.service;

import io.pablo.aicustomersupport.memory.config.MemoryProperties;
import io.pablo.aicustomersupport.memory.dto.MemoryContext;
import io.pablo.aicustomersupport.message.entity.Message;
import io.pablo.aicustomersupport.message.service.MessageService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MemoryServiceImpl implements MemoryService {

    private final MessageService messageService;
    private final MemoryProperties memoryProperties;

    public MemoryServiceImpl(MessageService messageService, MemoryProperties memoryProperties) {
        this.messageService = messageService;
        this.memoryProperties = memoryProperties;
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

    @Override
    @Cacheable(cacheNames = "memoryContext", key = "T(String).valueOf(#tenantId).concat(':').concat(T(String).valueOf(#conversationId)).concat(':').concat(#latestUserMessage.toLowerCase())")
    public MemoryContext buildMemoryContext(UUID tenantId, UUID conversationId, String latestUserMessage) {
        List<Message> allMessages = messageService.getConversationMessages(tenantId, conversationId);
        List<Message> recentMessages = getRelevantHistory(tenantId, conversationId, memoryProperties.getRecentHistoryLimit());
        Set<UUID> recentMessageIds = recentMessages.stream().map(Message::getId).collect(Collectors.toSet());
        Set<String> keywords = extractKeywords(latestUserMessage);

        List<Message> relevantMessages = allMessages.stream()
                .filter(message -> !recentMessageIds.contains(message.getId()))
                .filter(message -> computeKeywordScore(message.getContent(), keywords) > 0)
                .sorted(Comparator.comparingInt((Message message) -> computeKeywordScore(message.getContent(), keywords)).reversed()
                        .thenComparing(Message::getMessageOrder, Comparator.reverseOrder()))
                .limit(memoryProperties.getSemanticSearchLimit())
                .sorted(Comparator.comparing(Message::getMessageOrder))
                .toList();

        return new MemoryContext(recentMessages, relevantMessages);
    }

    private Set<String> extractKeywords(String content) {
        if (content == null || content.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(content.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
                .filter(token -> token.length() >= 4)
                .limit(memoryProperties.getMaxKeywordCount())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private int computeKeywordScore(String content, Set<String> keywords) {
        if (content == null || content.isBlank() || keywords.isEmpty()) {
            return 0;
        }

        String normalizedContent = content.toLowerCase(Locale.ROOT);
        int score = 0;
        for (String keyword : keywords) {
            if (normalizedContent.contains(keyword)) {
                score++;
            }
        }
        return score;
    }
}
