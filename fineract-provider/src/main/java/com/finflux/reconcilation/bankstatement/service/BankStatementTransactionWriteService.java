/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.service;

import org.apache.fineract.portfolio.loanaccount.domain.ChangedTransactionDetail;

public interface BankStatementTransactionWriteService {
	
	 void updateLoanTransactionOnReversal(final ChangedTransactionDetail changedTransactionDetail);
}