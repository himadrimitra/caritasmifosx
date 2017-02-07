package org.apache.fineract.portfolio.deduplication.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface DeDuplicationWritePlatformService {

        CommandProcessingResult updateDedupWeightage(JsonCommand command);
}
