package com.finflux.loanapplicationreference.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.loanapplicationreference.service.LoanApplicationReferenceWritePlatformService;

@Service
@CommandType(entity = "COAPPLICANTS", action = "CREATE")
public class AddCoApplicantsCommandHandler implements NewCommandSourceHandler {

        private final LoanApplicationReferenceWritePlatformService writePlatformService;

        @Autowired
        public AddCoApplicantsCommandHandler(final LoanApplicationReferenceWritePlatformService writePlatformService) {
                this.writePlatformService = writePlatformService;
        }

        @Transactional
        @Override
        public CommandProcessingResult processCommand(final JsonCommand command) {
                return this.writePlatformService.addCoApplicant(command);
        }
}
