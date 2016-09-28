package com.finflux.infrastructure.core.service;

import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class CustomCurrentTenantIdentifierResolver implements
		CurrentTenantIdentifierResolver {

	@Override
	public String resolveCurrentTenantIdentifier() {
		return ThreadLocalContextUtil.getTenant()==null?"platform-tenants":ThreadLocalContextUtil.getTenant().getTenantIdentifier();
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}

}
