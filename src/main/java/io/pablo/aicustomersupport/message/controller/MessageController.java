package io.pablo.aicustomersupport.message.controller;

import io.pablo.aicustomersupport.message.dto.MessageListResponse;
import io.pablo.aicustomersupport.message.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/conversations/{conversationId}/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public MessageListResponse listMessages(@PathVariable UUID tenantId,
                                            @PathVariable UUID conversationId) {
        return messageService.getConversationMessagesResponse(tenantId, conversationId);
    }
}
