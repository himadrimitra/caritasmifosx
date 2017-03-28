package com.finflux.mandates.service;

import com.finflux.mandates.data.MandateProcessCounts;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.domain.MandateProcessStatusEnum;
import com.finflux.portfolio.loan.mandate.data.MandateData;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.apache.fineract.infrastructure.documentmanagement.domain.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public interface MandatesProcessingWritePlatformService {

        CommandProcessingResult downloadMandates(JsonCommand command);
        CommandProcessingResult uploadMandates(JsonCommand command);
        CommandProcessingResult downloadTransactions(JsonCommand command);
        CommandProcessingResult uploadTransactions(JsonCommand command);
}
