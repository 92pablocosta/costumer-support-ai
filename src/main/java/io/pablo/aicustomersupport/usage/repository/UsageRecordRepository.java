package io.pablo.aicustomersupport.usage.repository;

import io.pablo.aicustomersupport.usage.entity.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsageRecordRepository extends JpaRepository<UsageRecord, UUID> {
}
