package io.pablo.aicustomersupport.message.repository;

import io.pablo.aicustomersupport.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findByTenantIdAndConversationIdOrderByMessageOrderAsc(UUID tenantId, UUID conversationId);

    Optional<Message> findTopByTenantIdAndConversationIdOrderByMessageOrderDesc(UUID tenantId, UUID conversationId);
}
