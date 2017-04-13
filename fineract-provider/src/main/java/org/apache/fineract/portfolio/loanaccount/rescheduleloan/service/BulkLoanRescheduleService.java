/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.portfolio.loanaccount.rescheduleloan.service;

import java.util.Date;

import org.apache.fineract.organisation.staff.data.StaffAccountSummaryCollectionData;

public interface BulkLoanRescheduleService {

    /**
     * 
     * 
     * @param loanOfficerId
     *            Id of the loan Officer, loans under him/her needs to be
     *            rescheduled
     * @param dueDate
     *            Scheduled re-payment date which needs to be adjusted.
     * @return
     */
    StaffAccountSummaryCollectionData retrieveLoanOfficerAccountSummary(Long loanOfficerId, Date dueDate);
}
