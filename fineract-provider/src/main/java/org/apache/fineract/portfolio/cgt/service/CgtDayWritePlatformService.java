package org.apache.fineract.portfolio.cgt.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface CgtDayWritePlatformService {

	public CommandProcessingResult createCgtDay(JsonCommand command);

	public CommandProcessingResult updateCgtDay(JsonCommand command);
	
	public CommandProcessingResult completeCgtDay(JsonCommand command);
	
}
