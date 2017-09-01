/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.service;

import java.util.Date;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoanOfficerAssignmentHistoryWriteServiceImpl implements LoanOfficerAssignmentHistoryWriteService {

	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public LoanOfficerAssignmentHistoryWriteServiceImpl(final PlatformSecurityContext context,
			final RoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	@Override
	public void updateLoanOfficer(final Long loanOfficerId, final Long loanOfficerAssignmentHistoryId) {
		AppUser user = this.context.getAuthenticatedUserIfPresent();
		LocalDate today = DateUtils.getLocalDateOfTenant();
		StringBuilder sql = new StringBuilder("UPDATE ");
		sql.append("m_loan_officer_assignment_history history SET ");
		sql.append("history.loan_officer_id = '" + loanOfficerId + "'");
		sql.append(" ,history.lastmodifiedby_id = '" + user.getId() + "'");
		sql.append(" ,history.lastmodified_date = '" + today + "'");
		sql.append(" where history.id = '" + loanOfficerAssignmentHistoryId + "'");
		this.jdbcTemplate.execute(sql.toString());
	}

	@Override
	public void updateEndDate(final Long loanOfficerAssignmentHistoryId, final LocalDate endDate) {
		AppUser user = this.context.getAuthenticatedUserIfPresent();
		LocalDate today = DateUtils.getLocalDateOfTenant();
		StringBuilder sql = new StringBuilder("UPDATE ");
		sql.append("m_loan_officer_assignment_history history SET ");
		sql.append("history.end_date = '" + endDate + "'");
		sql.append(" ,history.lastmodifiedby_id = '" + user.getId() + "'");
		sql.append(" ,history.lastmodified_date = '" + today + "'");
		sql.append(" where history.id = '" + loanOfficerAssignmentHistoryId + "'");
		this.jdbcTemplate.execute(sql.toString());
	}

	@Override
	public void updateStartDate(final Long loanOfficerAssignmentHistoryId, final LocalDate startDate) {
		AppUser user = this.context.getAuthenticatedUserIfPresent();
		LocalDate today = DateUtils.getLocalDateOfTenant();
		StringBuilder sql = new StringBuilder("UPDATE ");
		sql.append("m_loan_officer_assignment_history history SET ");
		sql.append("history.start_date = '" + startDate + "'");
		sql.append(" ,history.lastmodifiedby_id = '" + user.getId() + "'");
		sql.append(" ,history.lastmodified_date = '" + today + "'");
		sql.append(" where history.id = '" + loanOfficerAssignmentHistoryId + "'");
		this.jdbcTemplate.execute(sql.toString());
	}

	@Override
	public void createLoanOfficerAssignmentHistory(final Long loanOfficerId, final Long loanId, final LocalDate startDate) {
		AppUser user = this.context.getAuthenticatedUserIfPresent();
		LocalDate today = DateUtils.getLocalDateOfTenant();
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append("m_loan_officer_assignment_history ");
		sql.append(
				"(loan_id, loan_officer_id, start_date, createdby_id, created_date, lastmodified_date, lastmodifiedby_id)");
		sql.append("VALUES");
		sql.append("(" + loanId + " ," + loanOfficerId + " ,'" + startDate + "' ," + user.getId() + " ,'" + today
				+ "' ,'" + today + "' ," + user.getId() + ")");
		this.jdbcTemplate.execute(sql.toString());
	}

}