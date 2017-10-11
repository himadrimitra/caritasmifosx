package com.finflux.risk.creditbureau.configuration.service;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface CreditBureauProductWritePlatformService {
    
    CommandProcessingResult activateCreditBureau(Long creditBureauId);
    
    CommandProcessingResult deactivateCreditBureau(Long creditBureauId);

}
