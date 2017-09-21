package com.finflux.portfolio.loan.utilization.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.apache.tomcat.jni.Local;
import org.joda.time.LocalDate;

public class AuditDateBeforeDisbursementDateException extends AbstractPlatformDomainRuleException {

    public AuditDateBeforeDisbursementDateException(LocalDate auditDate) {
        super("error.msg.Audit.date.before.disbursement.date", "auditdate:" + auditDate + "shold not before disbursal date", auditDate);
        // TODO Auto-generated constructor stub
    }

}
