package com.finflux.risk.existingloans.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.risk.existingloans.service.ExistingLoanWritePlatformService;

   	@Service
   	@CommandType(entity = "EXISTINGLOAN", action = "DELETE")
   	public class ExistingLoanDeleteCommandHandler implements NewCommandSourceHandler {
   	 private final ExistingLoanWritePlatformService existingLoanWritePlatformService;

	    @Autowired
	    public ExistingLoanDeleteCommandHandler(final ExistingLoanWritePlatformService existingLoanWritePlatformService) {
	        this.existingLoanWritePlatformService = existingLoanWritePlatformService;
	    }

	    @Transactional
	    @Override
	    public CommandProcessingResult processCommand(final JsonCommand command) {
	        return this.existingLoanWritePlatformService.deleteExistingLoan(command.getClientId(),command.entityId());
	    }
}
