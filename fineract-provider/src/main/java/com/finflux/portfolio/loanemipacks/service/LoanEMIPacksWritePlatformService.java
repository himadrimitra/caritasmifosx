package com.finflux.portfolio.loanemipacks.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface LoanEMIPacksWritePlatformService {

        CommandProcessingResult create(JsonCommand command);

        CommandProcessingResult update(JsonCommand command);

        CommandProcessingResult delete(JsonCommand command);

}
