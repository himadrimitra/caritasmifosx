package com.finflux.portfolio.loan.mandate.handler;

import com.finflux.portfolio.loan.mandate.service.MandateWritePlatformService;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "MANDATE", action = "DELETE")
public class DeleteMandateCommandHandler implements NewCommandSourceHandler {

        private final MandateWritePlatformService writePlatformService;

        @Autowired
        public DeleteMandateCommandHandler(final MandateWritePlatformService writePlatformService) {
                this.writePlatformService = writePlatformService;
        }

        @Transactional
        @Override
        public CommandProcessingResult processCommand(final JsonCommand command) {
                return this.writePlatformService.deleteMandate(command);
        }
}
