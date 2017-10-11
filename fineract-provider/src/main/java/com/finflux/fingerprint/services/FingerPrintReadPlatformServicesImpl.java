package com.finflux.fingerprint.services;

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
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.fingerprint.data.FingerPrintData;
import com.finflux.fingerprint.data.FingerPrintEntityTypeEnums;
import com.finflux.fingerprint.domain.FingerPrintRepositoryWrapper;

@Service
public class FingerPrintReadPlatformServicesImpl implements FingerPrintReadPlatformServices {

    private final JdbcTemplate jdbcTemplate;
    private final FingerPrintDataMapper dataMapper;
    private final FingerPrintRepositoryWrapper fingerPrintRepository;

    @Autowired
    public FingerPrintReadPlatformServicesImpl(final RoutingDataSource dataSource,
            final FingerPrintRepositoryWrapper fingerPrintRepository) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataMapper = new FingerPrintDataMapper();
        this.fingerPrintRepository = fingerPrintRepository;
    }

    @Override
    public Collection<FingerPrintData> retriveFingerPrintData(final Long clientId) {

        this.fingerPrintRepository.findEntityIdWithNotFoundDetection(clientId);
        try {
            if (clientId != null && clientId > 0) {

                final String sql = "SELECT " + this.dataMapper.schema() + " WHERE f.client_id = ?";
                return this.jdbcTemplate.query(sql, this.dataMapper, new Object[] { clientId });
            }

        } catch (EmptyResultDataAccessException e) {}

        return new ArrayList<FingerPrintData>();
    }

    private static final class FingerPrintDataMapper implements RowMapper<FingerPrintData> {

        private final String schema;

        public FingerPrintDataMapper() {
            final StringBuilder builder = new StringBuilder(200);
            builder.append("f.client_id AS clientId, ");
            builder.append("f.finger_id AS fingerId,");
            builder.append("f.finger_print As fingerPrint ");
            builder.append("FROM f_client_fingerprint f ");
            this.schema = builder.toString();
        }

        public String schema() {

            return this.schema;
        }

        @Override
        public FingerPrintData mapRow(ResultSet rs, int rowNum) throws SQLException {

            final Long clientId = rs.getLong("clientId");
            final Integer fingerId = rs.getInt("fingerId");
            final String fingerPrint = rs.getString("fingerprint");
            return FingerPrintData.instance(clientId, fingerId, fingerPrint);
        }

    }

	@Override
	public Collection<EnumOptionData> retriveFingerPrintTemplate() {
		final Collection<EnumOptionData> fingerOptions = FingerPrintEntityTypeEnums.entityTypeOptions();
		return fingerOptions;
	
	}

}
