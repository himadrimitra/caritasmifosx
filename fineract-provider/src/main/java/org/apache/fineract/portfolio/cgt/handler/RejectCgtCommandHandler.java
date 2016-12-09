package org.apache.fineract.portfolio.cgt.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.cgt.service.CgtWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "CGT", action = "REJECT")
public class RejectCgtCommandHandler implements NewCommandSourceHandler {

    private final CgtWritePlatformService cgtWritePlatformService;

    @Autowired
    public RejectCgtCommandHandler(final CgtWritePlatformService cgtWritePlatformService) {
        this.cgtWritePlatformService = cgtWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        return cgtWritePlatformService.rejectCgt(command);
    }

}
