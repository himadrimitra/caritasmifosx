package com.finflux.portfolio.loan.utilization.data;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.LocalDate;

public class LoanUtilizationCheckData {

    private final Long id;
    private final Long auditDoneById;
    private final String auditDoneByName;
    private final LocalDate auditDoneOn;
    private Set<LoanUtilizationCheckDetailData> loanUtilizationCheckDetailData;

    private LoanUtilizationCheckData(final Long id, final Long auditDoneById, final String auditDoneByName, final LocalDate auditDoneOn) {
        this.id = id;
        this.auditDoneById = auditDoneById;
        this.auditDoneByName = auditDoneByName;
        this.auditDoneOn = auditDoneOn;
    }

    public static LoanUtilizationCheckData instance(final Long id, final Long auditDoneById, final String auditDoneByName,
            final LocalDate auditDoneOn) {
        return new LoanUtilizationCheckData(id, auditDoneById, auditDoneByName, auditDoneOn);
    }

    public void addLoanUtilizationCheckDetails(final LoanUtilizationCheckDetailData loanUtilizationCheckDetailData) {
        if (this.loanUtilizationCheckDetailData == null) {
            this.loanUtilizationCheckDetailData = new HashSet<>();
        }
        this.loanUtilizationCheckDetailData.add(loanUtilizationCheckDetailData);
    }
}
