/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.portfolio.loanaccount.rescheduleloan.data;

import org.apache.fineract.organisation.staff.data.StaffAccountSummaryCollectionData;
import org.joda.time.LocalDate;


/**
 * Immutable data object returned for bulk loan payment reschedule.
 */
public class BulkRescheduleLoansData {

    @SuppressWarnings("unused")
    private final Long officeId;
    @SuppressWarnings("unused")
    private final Long loanOfficerId;
    @SuppressWarnings("unused")
    private final StaffAccountSummaryCollectionData accountSummaryCollection;

    public static BulkRescheduleLoansData template(final Long officeId, final Long fromLoanOfficerId,
            final StaffAccountSummaryCollectionData accountSummaryCollection) {
        return new BulkRescheduleLoansData(officeId, fromLoanOfficerId, accountSummaryCollection);
    }

    private BulkRescheduleLoansData(final Long officeId, final Long fromLoanOfficerId,
            final StaffAccountSummaryCollectionData accountSummaryCollection) {
        this.officeId = officeId;
        this.loanOfficerId = fromLoanOfficerId;
        this.accountSummaryCollection = accountSummaryCollection;
    }
}