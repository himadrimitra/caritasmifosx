package com.finflux.smartcard.api;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SmartCardApiConstants {

	public static final String SMARTCARD_RESOURCE_NAME = "smartcard";

	/**
	 * Smartcard entityType constants
	 */
	public static final String enumTypeClients = "CLIENTS";
	public static final String enumTypeLoans = "LOANS";
	public static final String enumTypeSavings = "SAVINGS";

	/**
	 * Common Parameters
	 */
	public static final String localeParamName = "locale";
	public static final String dateFormatParamName = "dateFormat";

	/**
	 * smartcard Parameters
	 */
	public static final String smartcardParamName = "smartcards";
	public static final String clientIdParamName = "clientId";
	public static final String entityIdParamName = "entityId";
	public static final String entityTypeEnumParamName = "entityType";
	public static final String cardNumberParamName = "cardNumber";
	public static final String cardStatusParamName = "cardStatus";
	public static final String cardNoteParamName = "note";
	public static final String cardDeactivatedById = "deactivatedById";
	public static final String cardDeactivatedDate = "deactivatedDate";

	public static final Set<String> CREATE_SMARTCARD_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
			smartcardParamName, entityIdParamName, entityTypeEnumParamName, cardNumberParamName, cardStatusParamName,
			cardNoteParamName, cardDeactivatedById, cardDeactivatedDate, localeParamName, dateFormatParamName));

	/**
	 * These parameters will match the class level parameters of
	 * {@link SmartcardData}. Where possible, we try to get response parameters
	 * to match those of request parameters.
	 */
	public static final Set<String> SMARTCARD_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList());

}
