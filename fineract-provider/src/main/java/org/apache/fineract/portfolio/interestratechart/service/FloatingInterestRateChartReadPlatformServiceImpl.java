/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.portfolio.interestratechart.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.interestratechart.data.FloatingInterestRateChartData;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class FloatingInterestRateChartReadPlatformServiceImpl implements FloatingInterestRateChartReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FloatingInterestRateChartReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        ;
    }

    @Override
    public Collection<FloatingInterestRateChartData> retrieveByProductId(final Long productId) {
        try {
            final FloatingInterestRateChartMapper chartRowMapper = new FloatingInterestRateChartMapper();
            String sql = "select " + chartRowMapper.schema() + " where firc.savings_product_id = ?";
            return this.jdbcTemplate.query(sql, chartRowMapper, new Object[] { productId });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    public static final class FloatingInterestRateChartMapper implements RowMapper<FloatingInterestRateChartData> {

        private final String schemaSql;

        public String schema() {
            return this.schemaSql;
        }

        private FloatingInterestRateChartMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(400);

            sqlBuilder.append("firc.id as id, firc.effective_date as effectiveFromDate, ")
                    .append("firc.interest_rate as interestRate ")
                    .append("from f_floating_interest_rate_chart firc  ");
            this.schemaSql = sqlBuilder.toString();
        }

        @Override
        public FloatingInterestRateChartData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLongDefaultToNullIfZero(rs, "id");
            final LocalDate effectiveFromDate = JdbcSupport.getLocalDate(rs, "effectiveFromDate");
            final BigDecimal interestRate = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "interestRate");

            return FloatingInterestRateChartData.instance(id, effectiveFromDate, interestRate);
        }

    }

}
