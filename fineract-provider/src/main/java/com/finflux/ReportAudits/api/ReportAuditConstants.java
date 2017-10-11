/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.ReportAudits.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ReportAuditConstants {
	
	 public static final String REPORT_AUDIT_RESOURCE_NAME = "reportaudits";   
	 public static final Set<String> REPORT_AUDIT_RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "reportId", "reportName",
	    		"reportType", "user", "executionStartDate", "executionEndDate", "reportParameters", "executionTime"));
}
