package com.finflux.risk.creditbureau.configuration.service;

import com.finflux.risk.creditbureau.configuration.domain.CreditBureauConfiguration;

import java.util.List;
import java.util.Map;

public interface CreditBureauConfigurationReadPlatformService {
    List<CreditBureauConfiguration> retrieveEditableCreditBureauConfiguration(String implementationKey);
    List<CreditBureauConfiguration> retrieveAllCreditBureauConfiguration(String implementationKey);
}
