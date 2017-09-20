package org.apache.fineract.portfolio.loanaccount.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.portfolio.loanaccount.service.LoanSchedularService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "OVERDUECHARGE", action = "EXECUTE")
public class ExecuteOverdueChargeCommandHandler implements NewCommandSourceHandler {

    private final LoanSchedularService writePlatformService;

    @Autowired
    public ExecuteOverdueChargeCommandHandler(final LoanSchedularService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.executeJobForLoans(command, JobName.APPLY_PENALTY_CHARGE_FOR_BROKEN_PERIODS);
    }
}