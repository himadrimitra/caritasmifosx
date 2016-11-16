package com.finflux.smartcard.domain;

import java.util.Date;

/*
 * Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved DO NOT
 * ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER. This code is
 * proprietary and confidential software; you can't redistribute it and/or
 * modify it unless agreed to in writing. Unauthorized copying of this file,
 * via any medium is strictly prohibited
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;

import com.finflux.smartcard.api.SmartCardApiConstants;

@Entity
@Table(name = "f_smartcard")
public class SmartCard extends AbstractAuditableCustom<AppUser, Long> {

	@Column(name = "client_id", nullable = false)
	private Long clientId;

	@Column(name = "entity_id", length = 100, nullable = false)
	private String entityId;

	@Column(name = "entity_type", length = 3, nullable = false)
	private Integer entityType;

	@Column(name = "card_number", length = 20, nullable = false)
	private String cardNumber;

	@Column(name = "card_status", length = 3, nullable = false)
	private Integer cardStatus;

	@Column(name = "note", length = 1000, nullable = true)
	private String note;

	@Temporal(TemporalType.DATE)
	@Column(name = "deactivated_date")
	private Date deactivatedDate;

	@LazyCollection(LazyCollectionOption.TRUE)
	@ManyToOne(optional = true)
	@JoinColumn(name = "deactivatedby_id", nullable = true)
	private AppUser deactivatedBy;

	protected SmartCard() {

	}

	public SmartCard(final Long clientId, final Integer entityType, final String entityId, final String cardNumber,
			final Integer cardStatus, final String note, final AppUser deactivatedById, final Date deactivatedDate) {

		this.clientId = clientId;
		this.entityType = entityType;
		this.entityId = entityId;
		this.cardNumber = cardNumber;
		this.cardStatus = cardStatus;
		this.note = note;
		this.deactivatedBy = deactivatedById;
		this.deactivatedDate = deactivatedDate;
	}

	public static SmartCard create(final Long clientId, final Integer entityType, final String entityId,
			final String cardNumber, final String status) {
		Integer cardStatus = null;
		String note = null;
		final SmartCardStatusTypeEnum optionData = SmartCardStatusTypeEnum.getEntityType(status);
		if (optionData != null) {
			cardStatus = optionData.getValue();
		}

		return new SmartCard(clientId, entityType, entityId, cardNumber, cardStatus, note, null, null);
	}

	public SmartCard activate(final JsonCommand command) {
		
		final SmartCardStatusTypeEnum statusEnum = SmartCardStatusTypeEnum.fromInt(SmartCardStatusTypeEnum.ACTIVE.getValue());
		 if (!statusEnum.hasStateOf(SmartCardStatusTypeEnum.fromInt(this.cardStatus))) {
	            this.cardStatus = statusEnum.getValue();
	            this.deactivatedBy = null;
				this.deactivatedDate = null;
		 }
		 
		final String noteText = command.stringValueOfParameterNamed(SmartCardApiConstants.cardNoteParamName);
		if (StringUtils.isNotBlank(noteText)) {
		this.note = noteText;
		}else{
		this.note = null;
		}
		return this;
	}

	public SmartCard inActivate(final AppUser currentUser, final JsonCommand command) {
		
		final SmartCardStatusTypeEnum statusEnum = SmartCardStatusTypeEnum.fromInt(SmartCardStatusTypeEnum.INACTIVE.getValue());
		 if (!statusEnum.hasStateOf(SmartCardStatusTypeEnum.fromInt(this.cardStatus))) {
	            this.cardStatus = statusEnum.getValue();
	            LocalDate deactivatedOnDate = new LocalDate();
				this.deactivatedBy = currentUser;
				this.deactivatedDate = deactivatedOnDate.toDate();
		 }
		
		final String noteText = command.stringValueOfParameterNamed(SmartCardApiConstants.cardNoteParamName);
		if (StringUtils.isNotBlank(noteText)) {
		this.note = noteText;
		}
		return this;
	}

	public Long getClientId() {
		return this.clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getDeactivatedDate() {
		return this.deactivatedDate;
	}

	public void setDeactivatedDate(Date deactivatedDate) {
		this.deactivatedDate = deactivatedDate;
	}

	public AppUser getDeactivatedBy() {
		return this.deactivatedBy;
	}

	public void setDeactivatedBy(AppUser deactivatedBy) {
		this.deactivatedBy = deactivatedBy;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public Integer getEntityType() {
		return this.entityType;
	}

	public void setEntityType(Integer entityType) {
		this.entityType = entityType;
	}

	public String getCardNumber() {
		return this.cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public Integer getCardStatus() {
		return this.cardStatus;
	}

	public void setCardStatus(Integer cardStatus) {
		this.cardStatus = cardStatus;
	}

}
