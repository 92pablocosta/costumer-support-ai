package io.pablo.aicustomersupport.conversation.service;

import io.pablo.aicustomersupport.common.exception.ResourceNotFoundException;
import io.pablo.aicustomersupport.conversation.dto.ConversationResponse;
import io.pablo.aicustomersupport.conversation.dto.CreateConversationRequest;
import io.pablo.aicustomersupport.conversation.entity.Conversation;
import io.pablo.aicustomersupport.conversation.entity.ConversationStatus;
import io.pablo.aicustomersupport.conversation.repository.ConversationRepository;
import io.pablo.aicustomersupport.tenant.entity.Tenant;
import io.pablo.aicustomersupport.tenant.service.TenantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final TenantService tenantService;

    public ConversationServiceImpl(ConversationRepository conversationRepository, TenantService tenantService) {
        this.conversationRepository = conversationRepository;
        this.tenantService = tenantService;
    }

    @Override
    public ConversationResponse createConversation(UUID tenantId, CreateConversationRequest request) {
        Tenant tenant = tenantService.getTenantEntity(tenantId);

        Conversation conversation = new Conversation();
        conversation.setId(UUID.randomUUID());
        conversation.setTenant(tenant);
        conversation.setCustomerId(request.customerId());
        conversation.setSubject(request.subject());
        conversation.setStatus(ConversationStatus.OPEN);

        return map(conversationRepository.save(conversation));
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationResponse getConversation(UUID tenantId, UUID conversationId) {
        return map(getConversationEntity(tenantId, conversationId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> listConversations(UUID tenantId) {
        tenantService.getTenantEntity(tenantId);
        return conversationRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Conversation getConversationEntity(UUID tenantId, UUID conversationId) {
        return conversationRepository.findByIdAndTenantId(conversationId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found for tenant"));
    }

    private ConversationResponse map(Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getTenant().getId(),
                conversation.getCustomerId(),
                conversation.getSubject(),
                conversation.getStatus(),
                conversation.getCreatedAt()
        );
    }
}
