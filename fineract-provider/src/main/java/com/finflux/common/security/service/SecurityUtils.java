package com.finflux.common.security.service;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final ConfigurationDomainService configurationDomainService;

    @Autowired
    public SecurityUtils(final ConfigurationDomainService configurationDomainService) {
        this.configurationDomainService = configurationDomainService;
    }

    public String mask(String value) {
        String maskRegex = configurationDomainService.getMaskedRegex();
        String maskReplaceCharacter = configurationDomainService.getMaskedCharacter();
        if (value != null) { return value.replaceAll(maskRegex, maskReplaceCharacter); }
        return null;
    }
}
