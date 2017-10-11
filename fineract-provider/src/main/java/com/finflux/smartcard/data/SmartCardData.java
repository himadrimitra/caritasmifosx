package com.finflux.smartcard.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.joda.time.LocalDate;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

public class SmartCardData {

	private final Long cardId;
	private final Long clientId;
	private final String entityId;
	private final Integer entityType;
	private final String cardNumber;
	private final Integer cardStatus;
	private final String note;
	private final LocalDate createdDate;
	private final LocalDate deactivatedDate;
	private final EnumOptionData statusEnumOptionData;
	private final EnumOptionData entityEnumOptionData;
	
	private SmartCardData(final Long cardId, final Long clientId, final Integer entityType, final String entityId, final String cardNumber, final Integer cardStatus, final String note, final EnumOptionData statusEnumOptionData, final EnumOptionData entityEnumOptionData, final LocalDate createdDate, final LocalDate deactivatedDate){
		this.cardId = cardId;
		this.clientId = clientId;
		this.entityId = entityId;
		this.entityType = entityType;
		this.cardNumber = cardNumber;
		this.cardStatus = cardStatus;
		this.note = note;
		this.createdDate = createdDate;
		this.deactivatedDate = deactivatedDate;
		this.statusEnumOptionData = statusEnumOptionData;
		this.entityEnumOptionData = entityEnumOptionData;
	}

	public static SmartCardData instance (final Long cardId, final Long clientId, final Integer entityType, final String entityId, final String cardNumber, final Integer cardStatus, final String note, final EnumOptionData statusEnumOptionData, final EnumOptionData entityEnumOptionData, final LocalDate createdDate, final LocalDate deactivatedDate){
		return new SmartCardData(cardId, clientId, entityType, entityId, cardNumber, cardStatus, note,statusEnumOptionData, entityEnumOptionData,createdDate,deactivatedDate);
	}
	
	public LocalDate getCreateDate() {
		return this.createdDate;
	}

	public LocalDate getDeactivatedDate() {
		return this.deactivatedDate;
	}

	public EnumOptionData getStatusEnumOptionData() {
		return this.statusEnumOptionData;
	}

	public EnumOptionData getEnumOptionData() {
		return this.statusEnumOptionData;
	}

	public Long getCardId() {
		return this.cardId;
	}
	
	public String getNote() {
		return this.note;
	}

	public String getEntityId() {
		return this.entityId;
	}
	
	public Integer getEntityType() {
		return this.entityType;
	}

	public EnumOptionData getEntityEnumOptionData() {
		return this.entityEnumOptionData;
	}

	public Long getClientId() {
		return this.clientId;
	}

	public String getCardNumber() {
		return this.cardNumber;
	}

	public Integer getCardStatus() {
		return this.cardStatus;
	}
	
}
