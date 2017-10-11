/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.ReportAudits.service;

import java.util.Map;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.dataqueries.domain.Report;
import org.apache.fineract.infrastructure.dataqueries.service.ReadReportingService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.ReportAudits.domain.ReportAudit;
import com.finflux.ReportAudits.domain.ReportAuditRepositoryWrapper;

@Service
public class ReportAuditWritePlatformServiceJpaRepositoryImpl implements ReportAuditWritePlatformService{
	
	private final ReportAuditRepositoryWrapper reportAuditRepositoryWrapper;
	private final ReadReportingService readReportingService;
	private final PlatformSecurityContext context;
	
	@Autowired
	public ReportAuditWritePlatformServiceJpaRepositoryImpl(
			ReportAuditRepositoryWrapper reportAuditRepositoryWrapper,
			final ReadReportingService readReportingService,
			final PlatformSecurityContext context) {
		this.reportAuditRepositoryWrapper = reportAuditRepositoryWrapper;
		this.readReportingService = readReportingService;
		this.context = context;
	}


	@Override
	public ReportAudit createReportAudit(ReportAudit reportAudit) {
		 this.reportAuditRepositoryWrapper.save(reportAudit);
		 return reportAudit;
	}


	@Override
	public ReportAudit createReportAudit(String reportName,
			Map<String, String> reportParams) {
		Report report = this.readReportingService.retrieveReportByName(reportName);
        ReportAudit reportAudit  = null;
        if(report != null && report.isTrackUsage() ){
            reportAudit  = ReportAudit.instance(this.context.authenticatedUser(), report, DateUtils.getLocalDateTimeOfTenant().toDate(), null,reportParams.toString());
        }
        return reportAudit;
	}


	@Override
	public void saveReportAudit(ReportAudit reportAudit, int status) {
		if(reportAudit != null && status == 200){
			reportAudit.setExecutionEndDate(DateUtils.getLocalDateTimeOfTenant().toDate());
	    	createReportAudit(reportAudit);			
		}
	}

}
