package org.apache.fineract.portfolio.client.service;

import org.apache.fineract.portfolio.client.domain.ClientAccountLimitsData;

public interface ClientAccountLimitsReadPlatformService {

    ClientAccountLimitsData retrieveOne(final Long clientId);

}
