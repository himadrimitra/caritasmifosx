package com.finflux.smartcard.data;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
import java.util.Collection;

import com.finflux.smartcard.domain.SmartCard;

public class SmartCardAuthData {

	private final SmartCard smartCard;
	private final Collection<SmartCardData> smartcardData;

	private SmartCardAuthData(final SmartCard smartCard, final Collection<SmartCardData> smartcardData) {

		this.smartCard = smartCard;
		this.smartcardData = smartcardData;
	}

	public static SmartCardAuthData instance(final SmartCard smartCard,
			final Collection<SmartCardData> smartcardData) {
		return new SmartCardAuthData(smartCard, smartcardData);
	}

	public SmartCard getSmartCard() {
		return this.smartCard;
	}

	public Collection<SmartCardData> getSmartcardData() {
		return this.smartcardData;
	}
	
}
