package io.pablo.aicustomersupport.conversation.repository;

import io.pablo.aicustomersupport.conversation.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    List<Conversation> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);

    Optional<Conversation> findByIdAndTenantId(UUID conversationId, UUID tenantId);
}
