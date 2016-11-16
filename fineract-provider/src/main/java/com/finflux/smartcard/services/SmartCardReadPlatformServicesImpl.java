package com.finflux.smartcard.services;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.smartcard.data.SmartCardData;
import com.finflux.smartcard.domain.SmartCardRepositoryWrapper;
import com.finflux.smartcard.domain.SmartCardStatusTypeEnum;

@Service
public class SmartCardReadPlatformServicesImpl implements SmartCardReadPlatformServices {

	private final JdbcTemplate jdbcTemplate;
	private final SmartCardDataMapper dataMapper;
	private final SmartCardRepositoryWrapper smartCardRepositoryWrapper;

	 @Autowired
	public SmartCardReadPlatformServicesImpl(final RoutingDataSource dataSource,
			final SmartCardRepositoryWrapper smartCardRepositoryWrapper) {

		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataMapper = new SmartCardDataMapper();
		this.smartCardRepositoryWrapper = smartCardRepositoryWrapper;
	}

	@Override
	public Collection<SmartCardData> retriveSmartCardData(final Long clientId) {

		this.smartCardRepositoryWrapper.findEntityIdWithNotFoundDetection(clientId);

		try {
			if (clientId != null && clientId > 0) {

				final String sql = "SELECT " + this.dataMapper.schema() + " WHERE s.client_id = ?";
				return this.jdbcTemplate.query(sql, this.dataMapper, new Object[] { clientId });
			}

		} catch (EmptyResultDataAccessException e) {
		}

		return new ArrayList<SmartCardData>();
	}
	@Override
	public SmartCardData retrieveOne(final String cardNumber) {
		this.smartCardRepositoryWrapper.findOneWithNotFoundDetection(cardNumber);
		try{
			final String sql = "SELECT " + this.dataMapper.schema() + " WHERE s.card_number = ?";
			final  Collection<SmartCardData> smartCardDatas =  this.jdbcTemplate.query(sql, this.dataMapper, new Object[] { cardNumber });
			if(!smartCardDatas.isEmpty()){
				return smartCardDatas.iterator().next();
			}
		}catch(EmptyResultDataAccessException e){
			
		}
		return null;
	}
	
	private static final class SmartCardDataMapper implements RowMapper<SmartCardData> {

		private final String schema;

		public SmartCardDataMapper() {
			final StringBuilder builder = new StringBuilder(200);
			builder.append("s.id AS cardId, s.client_id AS clientId, s.entity_type AS entityType, ");
			builder.append("s.entity_id AS entityId, s.card_number AS cardNumber, ");
			builder.append("s.card_status AS cardStatus, ");
			builder.append("s.note AS note, ");
			builder.append("s.created_date AS createdDate, s.deactivated_date AS deactivatedDate ");
			builder.append("FROM f_smartcard s ");
			this.schema = builder.toString();
		}

		public String schema() {

			return this.schema;
		}

		@Override
		public SmartCardData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final Long cardId = rs.getLong("cardId");
			final Long clientId = rs.getLong("clientId");
			final Integer entityType = rs.getInt("entityType");
			final String entityId = rs.getString("entityId");
			final String cardNumber = rs.getString("cardNumber");
			final Integer cardStatus = rs.getInt("cardStatus");
			final String note = rs.getString("note");
			final LocalDate createdDate = JdbcSupport.getLocalDate(rs, "createdDate");
			final LocalDate deactivatedDate =JdbcSupport.getLocalDate(rs, "deactivatedDate");
			final EnumOptionData cardStatusType = SmartCardStatusTypeEnum.smartCardEntity(cardStatus);
			final EnumOptionData cardEntityType = SmartCardStatusTypeEnum.smartCardEntity(entityType);
			return SmartCardData.instance(cardId, clientId, entityType, entityId, cardNumber, cardStatus, note, cardStatusType, cardEntityType,createdDate,deactivatedDate);
		}

	}

}
