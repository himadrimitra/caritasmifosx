package org.apache.fineract.portfolio.deduplication.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.deduplication.service.DeDuplicationService;
import org.apache.fineract.portfolio.deduplication.service.DeDuplicationWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "CLIENTDEDUPWEIGHTAGES", action = "UPDATE")
public class UpdateDedupWeightageCommandHandler implements NewCommandSourceHandler {
        private final DeDuplicationWritePlatformService deDuplicationService;

        @Autowired
        public UpdateDedupWeightageCommandHandler(final DeDuplicationWritePlatformService deDuplicationService){
                this.deDuplicationService = deDuplicationService;
        }

        @Transactional
        @Override
        public CommandProcessingResult processCommand(final JsonCommand command) {
                return this.deDuplicationService.updateDedupWeightage(command);
        }

}
