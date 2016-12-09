package org.apache.fineract.portfolio.cgt.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface CgtWritePlatformService {

	public CommandProcessingResult createCgt(JsonCommand command);

	public CommandProcessingResult updateCgt(JsonCommand command);
	
	public CommandProcessingResult rejectCgt(JsonCommand command);
	
	public CommandProcessingResult completeCgt(JsonCommand command);

}
