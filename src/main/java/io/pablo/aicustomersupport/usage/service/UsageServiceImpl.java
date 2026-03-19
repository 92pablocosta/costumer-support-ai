package io.pablo.aicustomersupport.usage.service;

import io.pablo.aicustomersupport.conversation.entity.Conversation;
import io.pablo.aicustomersupport.message.entity.Message;
import io.pablo.aicustomersupport.tenant.entity.Tenant;
import io.pablo.aicustomersupport.usage.entity.UsageRecord;
import io.pablo.aicustomersupport.usage.entity.UsageStatus;
import io.pablo.aicustomersupport.usage.repository.UsageRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UsageServiceImpl implements UsageService {

    private final UsageRecordRepository usageRecordRepository;

    public UsageServiceImpl(UsageRecordRepository usageRecordRepository) {
        this.usageRecordRepository = usageRecordRepository;
    }

    @Override
    public void record(Tenant tenant,
                       Conversation conversation,
                       Message assistantMessage,
                       String provider,
                       String modelName,
                       int promptTokens,
                       int completionTokens,
                       long responseTimeMs,
                       UsageStatus status,
                       String errorMessage) {
        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setId(UUID.randomUUID());
        usageRecord.setTenant(tenant);
        usageRecord.setConversation(conversation);
        usageRecord.setMessage(assistantMessage);
        usageRecord.setProvider(provider);
        usageRecord.setModelName(modelName);
        usageRecord.setPromptTokens(promptTokens);
        usageRecord.setCompletionTokens(completionTokens);
        usageRecord.setTotalTokens(promptTokens + completionTokens);
        usageRecord.setResponseTimeMs(responseTimeMs);
        usageRecord.setStatus(status);
        usageRecord.setErrorMessage(errorMessage);
        usageRecordRepository.save(usageRecord);
    }
}
