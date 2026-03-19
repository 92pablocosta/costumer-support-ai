package io.pablo.aicustomersupport.tenant.service;

import io.pablo.aicustomersupport.tenant.dto.CreateTenantRequest;
import io.pablo.aicustomersupport.tenant.dto.TenantResponse;
import io.pablo.aicustomersupport.tenant.entity.Tenant;

import java.util.List;
import java.util.UUID;

public interface TenantService {

    TenantResponse createTenant(CreateTenantRequest request);

    TenantResponse getTenant(UUID tenantId);

    List<TenantResponse> listTenants();

    Tenant getTenantEntity(UUID tenantId);
}
