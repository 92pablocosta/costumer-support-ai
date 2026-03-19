package io.pablo.aicustomersupport.tenant.service;

import io.pablo.aicustomersupport.common.exception.ResourceNotFoundException;
import io.pablo.aicustomersupport.tenant.dto.CreateTenantRequest;
import io.pablo.aicustomersupport.tenant.dto.TenantResponse;
import io.pablo.aicustomersupport.tenant.entity.Tenant;
import io.pablo.aicustomersupport.tenant.entity.TenantStatus;
import io.pablo.aicustomersupport.tenant.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    public TenantServiceImpl(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public TenantResponse createTenant(CreateTenantRequest request) {
        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        tenant.setName(request.name());
        tenant.setExternalKey(request.externalKey());
        tenant.setStatus(TenantStatus.ACTIVE);
        Tenant savedTenant = tenantRepository.save(tenant);
        return map(savedTenant);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponse getTenant(UUID tenantId) {
        return map(getTenantEntity(tenantId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantResponse> listTenants() {
        return tenantRepository.findAll().stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Tenant getTenantEntity(UUID tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
    }

    private TenantResponse map(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getExternalKey(),
                tenant.getStatus(),
                tenant.getCreatedAt()
        );
    }
}
