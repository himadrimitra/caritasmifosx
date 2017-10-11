package com.finflux.smartcard.services;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

import com.finflux.smartcard.domain.SmartCard;

public interface SmartCardWritePlatformServices {
	
	CommandProcessingResult activate(final Long clientId, final Integer entityType, final JsonCommand command);
	CommandProcessingResult inActivate(final Long clientId, final Integer entityType, final JsonCommand command);
	SmartCard generateUniqueSmartCardNumber(final Long clientId, final String entityType, final String enittyId);

}
