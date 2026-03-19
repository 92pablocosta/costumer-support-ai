package io.pablo.aicustomersupport.conversation.controller;

import io.pablo.aicustomersupport.conversation.dto.ConversationResponse;
import io.pablo.aicustomersupport.conversation.dto.CreateConversationRequest;
import io.pablo.aicustomersupport.conversation.service.ConversationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse createConversation(@PathVariable UUID tenantId,
                                                   @Valid @RequestBody CreateConversationRequest request) {
        return conversationService.createConversation(tenantId, request);
    }

    @GetMapping("/{conversationId}")
    public ConversationResponse getConversation(@PathVariable UUID tenantId,
                                                @PathVariable UUID conversationId) {
        return conversationService.getConversation(tenantId, conversationId);
    }

    @GetMapping
    public List<ConversationResponse> listConversations(@PathVariable UUID tenantId) {
        return conversationService.listConversations(tenantId);
    }
}
