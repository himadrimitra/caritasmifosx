/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.service;

import java.util.List;

import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;

import com.finflux.reconcilation.bankstatement.data.BankStatementDetailsData;

public interface BankLoanTransactionsReadPlatformService {

    public LoanTransactionData getReconciledLoanTransaction(Long loanTransactionId);
    
    public List<LoanTransactionData> getLoanTransactionOptions(BankStatementDetailsData bankStatementDetailData);
}
