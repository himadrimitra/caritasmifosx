package com.finflux.risk.creditbureau.configuration.service;

import java.util.Collection;

import com.finflux.risk.creditbureau.configuration.data.CreditBureauData;

public interface CreditBureauProductReadPlatformService {
    
    Collection<CreditBureauData> retrieveCreditBureaus();
    
    CreditBureauData retrieveCreditBureau(final Long creditBureauId);
}
