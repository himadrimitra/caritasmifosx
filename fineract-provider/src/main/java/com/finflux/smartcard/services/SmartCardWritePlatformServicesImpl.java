package com.finflux.smartcard.services;

import java.util.Random;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
import javax.transaction.Transactional;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finflux.smartcard.api.SmartCardApiConstants;
import com.finflux.smartcard.data.SmartCardDataValidator;
import com.finflux.smartcard.domain.SmartCard;
import com.finflux.smartcard.domain.SmartCardRepositoryWrapper;
import com.finflux.smartcard.domain.SmartCardStatusTypeEnum;

@Service
public class SmartCardWritePlatformServicesImpl implements SmartCardWritePlatformServices {

	private final static Logger logger = LoggerFactory.getLogger(SmartCardWritePlatformServicesImpl.class);
	public static final String entityTypeClients = "10";
	public static final String entityTypeLoans = "20";
	public static final String entityTypeSavings = "30";

	private final PlatformSecurityContext context;
	private final SmartCardRepositoryWrapper smartCardRepositoryWrapper;
	private final SmartCardDataValidator smartCardDataValidator;
	private final ClientRepositoryWrapper clientRespository;

	@Autowired
	public SmartCardWritePlatformServicesImpl(final PlatformSecurityContext context,
			final SmartCardRepositoryWrapper smartCardRepositoryWrapper,
			final SmartCardDataValidator smartCardDataValidator, final ClientRepositoryWrapper clientRespository) {

		this.context = context;
		this.smartCardRepositoryWrapper = smartCardRepositoryWrapper;
		this.smartCardDataValidator = smartCardDataValidator;
		this.clientRespository = clientRespository;
	}

	@Transactional
	@Override
	public CommandProcessingResult activate(final Long clientId, final Integer entityType, final JsonCommand command) {

		try {
			this.context.authenticatedUser();
			this.smartCardDataValidator.validateForCreate(clientId, command.json());
			final String cardNumber = command.stringValueOfParameterNamed(SmartCardApiConstants.cardNumberParamName);
			SmartCard smartCard = this.smartCardRepositoryWrapper.findOneWithNotFoundDetection(cardNumber);
			this.smartCardDataValidator.validateForEntityType(smartCard, entityType);
			smartCard = smartCard.activate(command);

			this.smartCardRepositoryWrapper.save(smartCard);
			return new CommandProcessingResultBuilder() //
					.withCommandId(command.commandId()) //
					.withEntityId(smartCard.getId()) //
					.build();
		} catch (final DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(clientId, dve);
			return CommandProcessingResult.empty();
		}
	}

	private AppUser getAppUserIfPresent() {
		AppUser user = null;
		if (this.context != null) {
			user = this.context.getAuthenticatedUserIfPresent();
		}
		return user;
	}

	private void handleDataIntegrityIssues(final Long clientId, DataIntegrityViolationException dve) {
		/**
		 * Checking for duplicate smartcard
		 */

		final Throwable realCause = dve.getMostSpecificCause();

		if (realCause.getMessage().contains("f_smartcard_UNIQUE")) {
			throw new PlatformDataIntegrityException("error.msg.card duplicate", "smartcard already exists for client",
					"clientId `" + clientId + "`", "clientId", clientId);
		}

		logAsErrorUnexpectedDataIntegrityException(dve);

		throw new PlatformDataIntegrityException("error.msg.smartcard.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource.");
	}

	private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
		logger.error(dve.getMessage(), dve);
	}

	@Override
	public SmartCard generateUniqueSmartCardNumber(final Long clientId, final String entityType,
			final String entityId) {
		SmartCard smartCard = null;
		try {
			if (clientId != null && clientId > 0) {

				this.clientRespository.findOneWithNotFoundDetection(clientId);
				final String cardNumber = this.generateRandom(10, entityType);
				final String status = SmartCardStatusTypeEnum.enumTypePending;
				final SmartCardStatusTypeEnum optionData = SmartCardStatusTypeEnum.getEntityType(entityType);
				final Integer entityTypeId = optionData.getValue();
				smartCard = SmartCard.create(clientId, entityTypeId, entityId, cardNumber, status);
				this.smartCardRepositoryWrapper.save(smartCard);
			}
		} catch (final DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(clientId, dve);
		}
		return smartCard;
	}

	public String generateRandom(final int length, final String entityType) {
		Random random = new Random();
		String entity = null;
		final SmartCardStatusTypeEnum optionData = SmartCardStatusTypeEnum.getEntityType(entityType);
		if (optionData != null) {
			switch (optionData) {
			case CLIENTS:
				entity = entityTypeClients;
				break;
			case LOANS:
				entity = entityTypeLoans;
				break;
			case SAVINGS:
				entity = entityTypeSavings;
				break;
			default:
				break;
			}
		}
		char[] digits = new char[length];
		digits[0] = (char) (random.nextInt(9) + '1');
		for (int i = 1; i < length; i++) {
			digits[i] = (char) (random.nextInt(10) + '0');
		}
		return (entity + (new String(digits)));
	}

	@Override
	public CommandProcessingResult inActivate(Long clientId, Integer entityType, JsonCommand command) {
		try {
			final AppUser currentUser = getAppUserIfPresent();
			this.context.authenticatedUser();
			this.smartCardDataValidator.validateForCreate(clientId, command.json());
			final String cardNumber = command.stringValueOfParameterNamed(SmartCardApiConstants.cardNumberParamName);
			SmartCard smartCard = this.smartCardRepositoryWrapper.findOneWithNotFoundDetection(cardNumber);
			this.smartCardDataValidator.validateForEntityType(smartCard, entityType);
			smartCard = smartCard.inActivate(currentUser, command);

			this.smartCardRepositoryWrapper.save(smartCard);
			return new CommandProcessingResultBuilder() //
					.withCommandId(command.commandId()) //
					.withEntityId(smartCard.getId()) //
					.build();
		} catch (final DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(clientId, dve);
			return CommandProcessingResult.empty();
		}
	}
}
