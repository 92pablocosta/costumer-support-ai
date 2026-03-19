package io.pablo.aicustomersupport.usage.service;

import io.pablo.aicustomersupport.conversation.entity.Conversation;
import io.pablo.aicustomersupport.message.entity.Message;
import io.pablo.aicustomersupport.tenant.entity.Tenant;
import io.pablo.aicustomersupport.usage.entity.UsageStatus;

public interface UsageService {

    void record(Tenant tenant,
                Conversation conversation,
                Message assistantMessage,
                String provider,
                String modelName,
                int promptTokens,
                int completionTokens,
                long responseTimeMs,
                UsageStatus status,
                String errorMessage);
}
