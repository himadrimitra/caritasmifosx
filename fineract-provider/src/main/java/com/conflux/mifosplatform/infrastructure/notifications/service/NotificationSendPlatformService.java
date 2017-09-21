package com.conflux.mifosplatform.infrastructure.notifications.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.stereotype.Service;

public interface NotificationSendPlatformService {
	
	CommandProcessingResult sendNotification(JsonCommand command);

}
