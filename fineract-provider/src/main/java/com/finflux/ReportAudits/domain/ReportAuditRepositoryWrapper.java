/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.ReportAudits.domain;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.ReportAudits.exception.ReportAuditNotFoundException;

@Service
public class ReportAuditRepositoryWrapper {
	
	private final ReportAuditRepository reportAuditRepository;

	@Autowired
	public ReportAuditRepositoryWrapper(
			ReportAuditRepository reportAuditRepository) {
		this.reportAuditRepository = reportAuditRepository;
	}
	
	public void save(final ReportAudit reportAudit) {
        this.reportAuditRepository.save(reportAudit);
    }
	
	public void save(final Collection<ReportAudit> reportAudits) {
        this.reportAuditRepository.save(reportAudits);
    }

    public void saveAndFlush(final ReportAudit reportAudit) {
        this.reportAuditRepository.saveAndFlush(reportAudit);
    }

    public void delete(final ReportAudit reportAudit) {
        this.reportAuditRepository.delete(reportAudit);
    }
    
    public ReportAudit findOneWithNotFoundDetection(final Long id) {
        final ReportAudit reportAudit = this.reportAuditRepository.findOne(id);
        if (reportAudit == null) { throw new ReportAuditNotFoundException(id); }
        return reportAudit;
    }
    
    
}
