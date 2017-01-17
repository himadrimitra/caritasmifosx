package com.finflux.portfolio.loan.utilization.data;

import org.joda.time.LocalDate;

public class LoanUtilizationCheckData {

    private final Long id;
    private final Long loanId;
    private final Long auditDoneById;
    private final String auditDoneByName;
    private final LocalDate auditDoneOn;
    private LoanUtilizationCheckDetailData loanUtilizationCheckDetailData;

    private LoanUtilizationCheckData(final Long id, final Long loanId, final Long auditDoneById, final String auditDoneByName,
            final LocalDate auditDoneOn) {
        this.id = id;
        this.loanId = loanId;
        this.auditDoneById = auditDoneById;
        this.auditDoneByName = auditDoneByName;
        this.auditDoneOn = auditDoneOn;
    }

    public static LoanUtilizationCheckData instance(final Long id, final Long loanId, final Long auditDoneById,
            final String auditDoneByName, final LocalDate auditDoneOn) {
        return new LoanUtilizationCheckData(id, loanId, auditDoneById, auditDoneByName, auditDoneOn);
    }

    public void addLoanUtilizationCheckDetailData(final LoanUtilizationCheckDetailData loanUtilizationCheckDetailData) {
        this.loanUtilizationCheckDetailData = loanUtilizationCheckDetailData;
    }

    public LoanUtilizationCheckDetailData getLoanUtilizationCheckDetailData() {
        return this.loanUtilizationCheckDetailData;
    }

    public void setLoanUtilizationCheckDetailData(LoanUtilizationCheckDetailData loanUtilizationCheckDetailData) {
        this.loanUtilizationCheckDetailData = loanUtilizationCheckDetailData;
    }

    public Long getId() {
        return this.id;
    }

    public Long getLoanId() {
        return this.loanId;
    }

    public Long getAuditDoneById() {
        return this.auditDoneById;
    }

    public String getAuditDoneByName() {
        return this.auditDoneByName;
    }

    public LocalDate getAuditDoneOn() {
        return this.auditDoneOn;
    }
}
