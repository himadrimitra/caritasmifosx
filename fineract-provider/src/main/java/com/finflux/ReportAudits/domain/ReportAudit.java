/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.ReportAudits.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.dataqueries.domain.Report;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;

@SuppressWarnings("serial")
@Entity
@Table(name = "f_stretchy_report_logs")
public class ReportAudit  extends AbstractPersistable<Long> {


    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    
    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;
    

    @Column(name = "execution_start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date executionStartDate;
    

    @Column(name = "execution_end_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date executionEndDate;
    
    @Column(name = "param_as_json")
    private String reportParameters;
    
    
    @Autowired
	private ReportAudit(final AppUser user, final Report report, final Date executionStartDate,
			final Date executionEndDate, final String reportParameters) {
		this.user = user;
		this.report = report;
		this.executionStartDate = executionStartDate;
		this.executionEndDate = executionEndDate;
		this.reportParameters = reportParameters;
	}
    
    public static ReportAudit instance(final AppUser user, final Report report, final Date executionStartDate,
			final Date executionEndDate, final String reportParameters){
    	return new ReportAudit(user, report, executionStartDate, executionEndDate, reportParameters);
    }

	public ReportAudit() {
		super();
	}

	public void setExecutionEndDate(Date executionEndDate) {
		this.executionEndDate = executionEndDate;
	}
       

}
