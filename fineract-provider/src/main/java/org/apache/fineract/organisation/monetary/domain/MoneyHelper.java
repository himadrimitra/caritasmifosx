/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.organisation.monetary.domain;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MoneyHelper {
    
    private static Map<String,RoundingMode> roundingMode = new HashMap<>();
    private static Map<String,RoundingMode> adjustedAmountRoundingMode = new HashMap<>();
    private static ConfigurationDomainService staticConfigurationDomainService;
    private static final RoundingMode DEFAULT_INTERNAL_ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final int DEFAULT_INTERNAL_SCALE = 2;
    
    @Autowired
    private ConfigurationDomainService configurationDomainService;

    @PostConstruct
    public void someFunction () {
        staticConfigurationDomainService = configurationDomainService;
    }

    
    public static RoundingMode getRoundingMode() {
        FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
        if (roundingMode.get(tenant.getTenantIdentifier()) == null) {
            roundingMode.put(tenant.getTenantIdentifier(),RoundingMode.valueOf(staticConfigurationDomainService.getRoundingMode()));
        }
        return roundingMode.get(tenant.getTenantIdentifier());
    }
    
    public static RoundingMode getRoundingModeForInternalCalculations() {
        return DEFAULT_INTERNAL_ROUNDING_MODE;
    }

    public static int getScaleForInternalCalculations() {
        return DEFAULT_INTERNAL_SCALE;
    }
    
    public static RoundingMode getAdjustedAmountRoundingMode() {
        FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
        if (adjustedAmountRoundingMode.get(tenant.getTenantIdentifier()) == null) {
            adjustedAmountRoundingMode.put(tenant.getTenantIdentifier(),RoundingMode.valueOf(staticConfigurationDomainService.getAdjustedAmountRoundingMode()));
        }
        return adjustedAmountRoundingMode.get(tenant.getTenantIdentifier());
    }

}
