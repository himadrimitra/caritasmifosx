package com.finflux.portfolio.loanemipacks.handler;

import com.finflux.portfolio.loanemipacks.service.LoanEMIPacksWritePlatformService;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "LOANEMIPACKS", action = "DELETE")
public class DeleteLoanEMIPackCommandHandler implements NewCommandSourceHandler {

        private final LoanEMIPacksWritePlatformService writePlatformService;

        @Autowired
        public DeleteLoanEMIPackCommandHandler(final LoanEMIPacksWritePlatformService writePlatformService){
                this.writePlatformService = writePlatformService;
        }

        @Transactional
        @Override
        public CommandProcessingResult processCommand(final JsonCommand command) {
                return this.writePlatformService.delete(command);
        }
}
