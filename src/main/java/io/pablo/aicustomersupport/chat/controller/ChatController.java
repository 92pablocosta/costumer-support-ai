package io.pablo.aicustomersupport.chat.controller;

import io.pablo.aicustomersupport.chat.dto.ChatRequest;
import io.pablo.aicustomersupport.chat.dto.ChatResponse;
import io.pablo.aicustomersupport.chat.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/conversations/{conversationId}/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@PathVariable UUID tenantId,
                             @PathVariable UUID conversationId,
                             @Valid @RequestBody ChatRequest request) {
        return chatService.sendMessage(tenantId, conversationId, request);
    }
}
