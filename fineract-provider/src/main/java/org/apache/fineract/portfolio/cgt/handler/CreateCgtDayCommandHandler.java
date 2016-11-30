package org.apache.fineract.portfolio.cgt.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.cgt.service.CgtDayWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "CGTDAY", action = "CREATE")
public class CreateCgtDayCommandHandler implements NewCommandSourceHandler {

    private final CgtDayWritePlatformService cgtDayWritePlatformService;

    @Autowired
    public CreateCgtDayCommandHandler(final CgtDayWritePlatformService cgtDayWritePlatformService) {
        this.cgtDayWritePlatformService = cgtDayWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        return cgtDayWritePlatformService.createCgtDay(command);
    }

}
