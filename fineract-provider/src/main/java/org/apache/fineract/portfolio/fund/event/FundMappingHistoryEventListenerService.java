/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.fund.event;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;

public interface FundMappingHistoryEventListenerService {

    public void updateFundMappingHistory(final Loan loan);

    public void deleteFundMappingHistory(final Loan loan);
}
