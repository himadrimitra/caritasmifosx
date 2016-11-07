/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.ReportAudits.service;

import java.util.Map;

import com.finflux.ReportAudits.domain.ReportAudit;

public interface ReportAuditWritePlatformService {
	
	public ReportAudit createReportAudit(ReportAudit reportAudit);
	
	public ReportAudit createReportAudit(String reportName,Map<String, String> reportParams);
	
	public void saveReportAudit(ReportAudit reportAudit, int status);
}
