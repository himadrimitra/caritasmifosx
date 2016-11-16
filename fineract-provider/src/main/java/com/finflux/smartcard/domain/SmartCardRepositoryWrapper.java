package com.finflux.smartcard.domain;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.smartcard.exception.SmartCardNotFoundException;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
@Service
public class SmartCardRepositoryWrapper {

	private final SmartCardRepository smartCardRepository;

	@Autowired
	public SmartCardRepositoryWrapper(final SmartCardRepository smartCardRepository) {
		this.smartCardRepository = smartCardRepository;
	}

	public Collection<SmartCard> findEntityIdWithNotFoundDetection(final Long clientId) {
		final Collection<SmartCard> entity = this.smartCardRepository.findByClientId(clientId);
		if (entity == null) {
			throw new SmartCardNotFoundException(clientId.toString());
		}
		return entity;
	}

	public SmartCard findOneWithNotFoundDetection(final String cardNumber) {
		final SmartCard smartCard = this.smartCardRepository.findByCardNumber(cardNumber);
		if (smartCard == null) {
			throw new SmartCardNotFoundException(cardNumber);
		}
		return smartCard;
	}

	public void save(final SmartCard smartCard) {
		this.smartCardRepository.save(smartCard);
	}

	public void saveAndFlush(final SmartCard smartCard) {
		this.smartCardRepository.saveAndFlush(smartCard);
	}
}
