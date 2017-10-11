/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.portfolio.loanaccount.data;

import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.joda.time.LocalDate;

public class LoanOfficerAssignmentHistoryData {

    private final Long loanOfficerId;
    private final Long latestHistoryRecordId;
    private final LocalDate loanSubmittedOnDate;
    private final LocalDate latestHistoryRecordEndDate;
    private final LocalDate latestHistoryRecordStartdate;
    private final LoanStatus status;

    public LoanOfficerAssignmentHistoryData(final Long loanOfficerId, final Long latestHistoryRecordId, final LocalDate loanSubmittedOnDate,
            final LocalDate latestHistoryRecordEndDate, final LocalDate latestHistoryRecordStartdate, final LoanStatus status) {
        this.loanOfficerId = loanOfficerId;
        this.latestHistoryRecordId = latestHistoryRecordId;
        this.loanSubmittedOnDate = loanSubmittedOnDate;
        this.latestHistoryRecordEndDate = latestHistoryRecordEndDate;
        this.status = status;
        this.latestHistoryRecordStartdate = latestHistoryRecordStartdate;

    }

    public Long getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public Long getLatestHistoryRecordId() {
        return this.latestHistoryRecordId;
    }

    public LocalDate getLoanSubmittedOnDate() {
        return this.loanSubmittedOnDate;
    }

    public LocalDate getLatestHistoryRecordEndDate() {
        return this.latestHistoryRecordEndDate;
    }

    public LocalDate getLatestHistoryRecordStartdate() {
        return this.latestHistoryRecordStartdate;
    }

    public LoanStatus getStatus() {
        return this.status;
    }

    public boolean loanOfficerIdentifiedBy(final Long loanOfficerId) {
        return getLoanOfficerId().equals(loanOfficerId);
    }

    public boolean hasLoanOfficer(final Long fromLoanOfficerId) {
        boolean matchesCurrentLoanOfficer = false;
        if (getLoanOfficerId() != null) {
            matchesCurrentLoanOfficer = loanOfficerIdentifiedBy(fromLoanOfficerId);
        } else {
            matchesCurrentLoanOfficer = fromLoanOfficerId == null;
        }

        return matchesCurrentLoanOfficer;
    }

    public boolean hasHistoryStartDateBefore(final LocalDate matchingDate) {
        return matchingDate.isBefore(getLatestHistoryRecordStartdate());
    }
    
    public boolean matchesStartDateOfLatestHistory(final LocalDate matchingDate) {
        return getLatestHistoryRecordStartdate().isEqual(matchingDate);
    }

}
