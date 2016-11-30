package org.apache.fineract.portfolio.cgt.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class CgtGlobalConfigurationNotEnabledException extends AbstractPlatformDomainRuleException {

	public CgtGlobalConfigurationNotEnabledException() {
		super("error.msg.cgt.global.configuration.not.enabled", "global configuration for CGT not enabled", "global configuration for CGT not enabled");
	}

}
