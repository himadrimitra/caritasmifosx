package com.finflux.portfolio.loan.mandate.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface MandateWritePlatformService {

        CommandProcessingResult createMandate(JsonCommand command);

        CommandProcessingResult updateMandate(JsonCommand command);

        CommandProcessingResult cancelMandate(JsonCommand command);

        CommandProcessingResult editMandate(JsonCommand command);

        CommandProcessingResult deleteMandate(JsonCommand command);

}
