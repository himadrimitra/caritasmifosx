package com.finflux.smartcard.services;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import java.util.Collection;

import com.finflux.smartcard.data.SmartCardData;

public interface SmartCardReadPlatformServices {

	Collection<SmartCardData> retriveSmartCardData(final Long clientId);
	SmartCardData retrieveOne(final String cardNumber);

}
