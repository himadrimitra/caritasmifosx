package com.finflux.mandates.handler;

import com.finflux.mandates.service.MandatesProcessingWritePlatformService;
import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "MANDATES", action = "TRANSACTIONS_DOWNLOAD")
public class TransactionsDownloadCommandHandler implements NewCommandSourceHandler {

        private final MandatesProcessingWritePlatformService writePlatformService;

        @Autowired
        public TransactionsDownloadCommandHandler(final MandatesProcessingWritePlatformService writePlatformService) {
                this.writePlatformService = writePlatformService;
        }

        @Transactional
        @Override
        public CommandProcessingResult processCommand(final JsonCommand command) {
                return this.writePlatformService.downloadTransactions(command);
        }
}