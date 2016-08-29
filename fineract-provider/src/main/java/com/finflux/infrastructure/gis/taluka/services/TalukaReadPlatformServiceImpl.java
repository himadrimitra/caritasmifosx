package com.finflux.infrastructure.gis.taluka.services;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.gis.taluka.data.TalukaData;

@Service
public class TalukaReadPlatformServiceImpl implements TalukaReadPlatformServices{
    
    @SuppressWarnings("unused")
    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final TalukaDataMapper dataMapper;
    
    @Autowired
    public TalukaReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataMapper = new TalukaDataMapper();
    }

    @Override
    public TalukaData retrieveOne(Long talukaId) {
        try {
            if (talukaId != null && talukaId > 0) {
                final String sql = "SELECT " + this.dataMapper.schema() + " WHERE t.id = ? ";
                return this.jdbcTemplate.queryForObject(sql, this.dataMapper, new Object[] { talukaId });
            }
        } catch (final EmptyResultDataAccessException e) {}
       
        return null;
    }

    @Override
    public Collection<TalukaData> retrieveAllTalukaDataByTalukaIds(List<Long> talukaIds) {
        try {
            if (talukaIds != null && !talukaIds.isEmpty()) {
                final String talukaIdsStr = StringUtils.join(talukaIds, ',');
                final String sql = "SELECT " + this.dataMapper.schema() + " WHERE t.id IN (" + talukaIdsStr + ") ";
                return this.jdbcTemplate.query(sql, this.dataMapper);
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    @Override
    public Collection<TalukaData> retrieveAllTalukaDataByDistrictId(Long districtId) {
        try {
            if (districtId != null && districtId > 0) {
                final String sql = "SELECT " + this.dataMapper.schema() + " WHERE t.district_id = ? ";
                return this.jdbcTemplate.query(sql, this.dataMapper, new Object[] { districtId });
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }

    @Override
    public Collection<TalukaData> retrieveAllTalukaDataByDistrictIds(List<Long> districtIds) {
        try {
            if (districtIds != null && !districtIds.isEmpty()) {
                final String districtIdsStr = StringUtils.join(districtIds, ',');
                final String sql = "SELECT " + this.dataMapper.schema() + " WHERE t.district_id IN(" + districtIdsStr + ") ";
                return this.jdbcTemplate.query(sql, this.dataMapper);
            }
        } catch (final EmptyResultDataAccessException e) {}
        return null;
    }
    
    private static final class TalukaDataMapper implements RowMapper<TalukaData> {

        private final String schema;

        public TalukaDataMapper() {
            final StringBuilder builder = new StringBuilder(200);
            builder.append("t.id As talukaId, t.district_id AS districtId, t.iso_taluka_code AS isoTalukaCode, ");
            builder.append("t.taluka_name As talukaName ");
            builder.append("FROM f_taluka t ");
            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public TalukaData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long talukaId = rs.getLong("talukaId");
            final Long districtId = rs.getLong("districtId");
            final String isoTalukaCode = rs.getString("isoTalukaCode");
            final String talukaName = rs.getString("talukaName");
            return TalukaData.instance(talukaId, districtId, isoTalukaCode, talukaName);
        }
    }

}
