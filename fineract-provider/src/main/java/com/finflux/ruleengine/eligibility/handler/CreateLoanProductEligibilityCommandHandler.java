package com.finflux.ruleengine.eligibility.handler;

import com.finflux.ruleengine.eligibility.service.LoanProductEligibilityWritePlatformService;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "LOANPRODUCTELIGIBILITY", action = "CREATE")
public class CreateLoanProductEligibilityCommandHandler implements NewCommandSourceHandler {

    private final LoanProductEligibilityWritePlatformService writePlatformService;

    @Autowired
    public CreateLoanProductEligibilityCommandHandler(final LoanProductEligibilityWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.createLoanProductEligibility(command.getProductId(),command);
    }
}