/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.ReportAudits.service;



import org.apache.fineract.infrastructure.core.data.PaginationParameters;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;

import com.finflux.ReportAudits.data.ReportAuditData;

public interface ReportAuditReadPlatformService {
	
	public Page<ReportAuditData> retrieveAllReportAudits(final SearchParameters searchParameters, final PaginationParameters parameters);
	
	public ReportAuditData getReportAudits(final Long id);
}
