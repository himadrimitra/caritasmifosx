/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.organisation.workingdays.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.fineract.infrastructure.accountnumberformat.domain.EntityAccountType;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.organisation.workingdays.data.WorkingDayExemptionsData;
import org.apache.fineract.organisation.workingdays.domain.ApplicableProperty;
import org.apache.fineract.organisation.workingdays.domain.RepaymentRescheduleType;
import org.apache.fineract.organisation.workingdays.domain.RepaymentScheduleUpdationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class WorkingDayExemptionsReadPlatformServiceImpl implements WorkingDayExemptionsReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public WorkingDayExemptionsReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class WorkingDayExumptionsMapper implements RowMapper<WorkingDayExemptionsData> {

        private final String schema;

        public WorkingDayExumptionsMapper() {
            this.schema = "select * from f_workingday_exumption where portfolio_type = ? ";
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public WorkingDayExemptionsData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final int portfolioTypeId = rs.getInt("portfolio_type");
            final EnumOptionData protfolioType = EntityAccountType.entityAccountTypeOptionData(portfolioTypeId);
            final int applicablePropertyId = rs.getInt("applicable_property");
            final EnumOptionData applicableProperty = ApplicableProperty.applicablePropertyOptionData(applicablePropertyId);
            final String expression = rs.getString("expression");
            final int repaymentRescheduleTypeId = rs.getInt("action_tobe_performed");
            final EnumOptionData repaymentRescheduleType = RepaymentRescheduleType
                    .repaymentRescheduleTypeOptionData(repaymentRescheduleTypeId);
            final int repaymentScheduleUpdationTypeId = rs.getInt("update_type");
            final EnumOptionData repaymentScheduleUpdationType = RepaymentScheduleUpdationType
                    .repaymentScheduleUpdationTypeOptionData(repaymentScheduleUpdationTypeId);
            return new WorkingDayExemptionsData(protfolioType, applicableProperty, expression, repaymentRescheduleType,
                    repaymentScheduleUpdationType);
        }

    }

    @Override
    public List<WorkingDayExemptionsData> getWorkingDayExemptionsForEntityType(final int type) {
        final WorkingDayExumptionsMapper workingDayExumptionsMapper = new WorkingDayExumptionsMapper();
        return this.jdbcTemplate.query(workingDayExumptionsMapper.schema(), workingDayExumptionsMapper, new Object[] { type });
    }

}
