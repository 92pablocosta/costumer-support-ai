package io.pablo.aicustomersupport.tenant.repository;

import io.pablo.aicustomersupport.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findByExternalKey(String externalKey);
}
