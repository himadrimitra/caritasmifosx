/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.organisation.workingdays.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.organisation.workingdays.data.WorkingDayExemptionsData;
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
			this.schema = "select * from f_workingday_exumption where portfolio_type = ";
		}

		public String schema() {
			return this.schema;
		}

		@Override
		public WorkingDayExemptionsData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum)
				throws SQLException {
			final int portfolioType = rs.getInt("portfolio_type");
			final int action = rs.getInt("applicable_property");
			final String expression = rs.getString("expression");
			final int actionToBePerformed = rs.getInt("action_tobe_performed");
			final int updateType = rs.getInt("update_type");
			return new WorkingDayExemptionsData(portfolioType, action, expression, actionToBePerformed, updateType);
		}

	}

	@Override
	public List<WorkingDayExemptionsData> getWorkingDayExemptionsForEntityType(int type) {
		WorkingDayExumptionsMapper workingDayExumptionsMapper = new WorkingDayExumptionsMapper();
		String sql = workingDayExumptionsMapper.schema() + type;
		return this.jdbcTemplate.query(sql, workingDayExumptionsMapper, new Object[] {});
	}

}
