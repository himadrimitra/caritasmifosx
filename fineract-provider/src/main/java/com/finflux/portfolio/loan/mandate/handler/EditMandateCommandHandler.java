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
@CommandType(entity = "MANDATE", action = "EDIT")
public class EditMandateCommandHandler implements NewCommandSourceHandler {

        private final MandateWritePlatformService writePlatformService;

        @Autowired
        public EditMandateCommandHandler(final MandateWritePlatformService writePlatformService) {
                this.writePlatformService = writePlatformService;
        }

        @Transactional
        @Override
        public CommandProcessingResult processCommand(final JsonCommand command) {
                return this.writePlatformService.editMandate(command);
        }
}
