package com.finflux.portfolio.loanproduct.creditbureau.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface CreditBureauLoanProductMappingWritePlatformService {

    CommandProcessingResult create(final JsonCommand command);

    CommandProcessingResult update(final Long cblpMappingId, JsonCommand command);

    CommandProcessingResult active(final JsonCommand command, final Long cblpMappingId);

    CommandProcessingResult inActive(final JsonCommand command, final Long cblpMappingId);
}