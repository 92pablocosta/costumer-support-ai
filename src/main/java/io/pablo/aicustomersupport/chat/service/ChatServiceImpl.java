package io.pablo.aicustomersupport.chat.service;

import io.pablo.aicustomersupport.chat.dto.ChatRequest;
import io.pablo.aicustomersupport.chat.dto.ChatResponse;
import io.pablo.aicustomersupport.chat.dto.UsageDto;
import io.pablo.aicustomersupport.common.config.OpenAiProperties;
import io.pablo.aicustomersupport.common.exception.ExternalServiceException;
import io.pablo.aicustomersupport.conversation.entity.Conversation;
import io.pablo.aicustomersupport.conversation.service.ConversationService;
import io.pablo.aicustomersupport.llm.dto.LlmChatRequest;
import io.pablo.aicustomersupport.llm.dto.LlmChatResponse;
import io.pablo.aicustomersupport.llm.dto.LlmMessage;
import io.pablo.aicustomersupport.llm.service.LlmService;
import io.pablo.aicustomersupport.memory.dto.MemoryContext;
import io.pablo.aicustomersupport.memory.service.MemoryService;
import io.pablo.aicustomersupport.message.entity.Message;
import io.pablo.aicustomersupport.message.entity.MessageRole;
import io.pablo.aicustomersupport.message.service.MessageService;
import io.pablo.aicustomersupport.tenant.entity.Tenant;
import io.pablo.aicustomersupport.tenant.service.TenantService;
import io.pablo.aicustomersupport.usage.entity.UsageStatus;
import io.pablo.aicustomersupport.usage.service.UsageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private static final String SYSTEM_PROMPT = """
            You are an AI customer support assistant.
            Be concise, helpful, and do not invent policies not present in the context.
            """;

    private final TenantService tenantService;
    private final ConversationService conversationService;
    private final MessageService messageService;
    private final MemoryService memoryService;
    private final LlmService llmService;
    private final UsageService usageService;
    private final OpenAiProperties openAiProperties;

    public ChatServiceImpl(TenantService tenantService,
                           ConversationService conversationService,
                           MessageService messageService,
                           MemoryService memoryService,
                           LlmService llmService,
                           UsageService usageService,
                           OpenAiProperties openAiProperties) {
        this.tenantService = tenantService;
        this.conversationService = conversationService;
        this.messageService = messageService;
        this.memoryService = memoryService;
        this.llmService = llmService;
        this.usageService = usageService;
        this.openAiProperties = openAiProperties;
    }

    @Override
    public ChatResponse sendMessage(UUID tenantId, UUID conversationId, ChatRequest request) {
        Tenant tenant = tenantService.getTenantEntity(tenantId);
        Conversation conversation = conversationService.getConversationEntity(tenantId, conversationId);

        Message userMessage = messageService.createMessage(
                tenant,
                conversation,
                MessageRole.USER,
                request.message(),
                null,
                null,
                null
        );

        String selectedModel = request.model() == null || request.model().isBlank()
                ? openAiProperties.getDefaultModel()
                : request.model();

        MemoryContext memoryContext = memoryService.buildMemoryContext(tenantId, conversationId, request.message());
        List<LlmMessage> promptMessages = buildPromptMessages(memoryContext);

        long startTime = System.currentTimeMillis();
        LlmChatResponse llmResponse;
        long responseTimeMs;
        try {
            llmResponse = llmService.generateReply(new LlmChatRequest(selectedModel, promptMessages));
            responseTimeMs = System.currentTimeMillis() - startTime;
        } catch (ExternalServiceException exception) {
            responseTimeMs = System.currentTimeMillis() - startTime;
            usageService.record(
                    tenant,
                    conversation,
                    null,
                    "OPENAI",
                    selectedModel,
                    0,
                    0,
                    responseTimeMs,
                    UsageStatus.ERROR,
                    exception.getMessage()
            );
            throw exception;
        }

        Message assistantMessage = messageService.createMessage(
                tenant,
                conversation,
                MessageRole.ASSISTANT,
                llmResponse.content(),
                llmResponse.model(),
                llmResponse.promptTokens(),
                llmResponse.completionTokens()
        );

        usageService.record(
                tenant,
                conversation,
                assistantMessage,
                "OPENAI",
                llmResponse.model(),
                llmResponse.promptTokens(),
                llmResponse.completionTokens(),
                responseTimeMs,
                UsageStatus.SUCCESS,
                null
        );

        return new ChatResponse(
                tenantId,
                conversationId,
                messageService.toResponse(userMessage),
                messageService.toResponse(assistantMessage),
                new UsageDto(
                        llmResponse.promptTokens(),
                        llmResponse.completionTokens(),
                        llmResponse.promptTokens() + llmResponse.completionTokens(),
                        responseTimeMs
                )
        );
    }

    private List<LlmMessage> buildPromptMessages(MemoryContext memoryContext) {
        List<LlmMessage> promptMessages = new java.util.ArrayList<>();
        promptMessages.add(new LlmMessage("system", SYSTEM_PROMPT));

        if (!memoryContext.relevantMessages().isEmpty()) {
            String relevantContext = memoryContext.relevantMessages().stream()
                    .map(message -> "[" + message.getRole().name() + "] " + message.getContent())
                    .reduce((left, right) -> left + "\n" + right)
                    .orElse("");
            promptMessages.add(new LlmMessage(
                    "system",
                    "Relevant prior conversation context:\n" + relevantContext
            ));
        }

        memoryContext.recentMessages().forEach(message ->
                promptMessages.add(new LlmMessage(message.getRole().name().toLowerCase(), message.getContent()))
        );
        return promptMessages;
    }
}
