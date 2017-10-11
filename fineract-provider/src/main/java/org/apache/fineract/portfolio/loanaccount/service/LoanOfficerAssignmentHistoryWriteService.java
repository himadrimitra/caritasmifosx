/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.service;

import org.joda.time.LocalDate;

public interface LoanOfficerAssignmentHistoryWriteService {

    void updateLoanOfficer(final Long loanOfficerId, final Long loanOfficerAssignmentHistoryId);
    
    void updateStartDate(final Long loanOfficerAssignmentHistoryId, final LocalDate startDate);

    void updateEndDate(final Long loanOfficerAssignmentHistoryId, final LocalDate endDate);

    void createLoanOfficerAssignmentHistory(final Long loanOfficerId, final Long loanId, final LocalDate startDate);
}
