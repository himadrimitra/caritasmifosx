package com.finflux.risk.creditbureau.provider.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by dhirendra on 23/08/16.
 */
@Component
@Scope("singleton")
public class CreditBureauProviderFactory {

    Map<String, CreditBureauProvider> creditBureauProviderMap = new HashMap();

    @Autowired
    public CreditBureauProviderFactory(final Set<CreditBureauProvider> creditBureauProviders) {
        for (CreditBureauProvider creditBureauProvider : creditBureauProviders) {
            creditBureauProviderMap.put(creditBureauProvider.getKey(), creditBureauProvider);
        }
    }

    public CreditBureauProvider getCreditBureauProvider(String key) {
        return creditBureauProviderMap.get(key);
    }
}
