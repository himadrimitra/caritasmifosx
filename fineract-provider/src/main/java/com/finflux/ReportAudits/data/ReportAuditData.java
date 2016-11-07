/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.ReportAudits.data;

import java.util.Date;

import org.apache.fineract.useradministration.data.AppUserData;

@SuppressWarnings("unused")
public class ReportAuditData {
	
	private final Long id;
	private final Integer reportId;
	private final String reportName;
	private final String reportType;
	private final AppUserData user;
	private final Date executionStartDate;
	private final Date executionEndDate;
	private final String reportParameters;
	private final String executionTime;
	
	private ReportAuditData(final Long id, final Integer reportId, final String reportName, final String reportType, 
			final AppUserData user,final Date executionStartDate, final Date executionEndDate, final String reportParameters,
			final String executionTime) {
		this.id = id;
		this.reportId = reportId;
		this.reportName = reportName;
		this.reportType = reportType;
		this.user = user;
		this.executionStartDate = executionStartDate;
		this.executionEndDate = executionEndDate;
		this.reportParameters = reportParameters;
		this.executionTime = executionTime;
	}
	
	public static ReportAuditData instance(final Long id, final Integer reportId, String reportName, final String reportType,
			final AppUserData user,final Date executionStartDate, final Date executionEndDate, final String reportParameters,
			final String executionTime){
		return new ReportAuditData(id, reportId, reportName, reportType, user, executionStartDate, executionEndDate, reportParameters, executionTime);
	}
	
}
