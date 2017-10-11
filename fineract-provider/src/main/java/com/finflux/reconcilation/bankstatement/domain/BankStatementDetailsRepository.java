/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BankStatementDetailsRepository extends JpaRepository<BankStatementDetails, Long>,
        JpaSpecificationExecutor<BankStatementDetails> {
	
	@Query("from BankStatementDetails bankStatementDetail where bankStatementDetail.loanTransaction.id = :loanTransactionId ")
	BankStatementDetails getBankStatementDetailsByLoanTransction(@Param("loanTransactionId") Long loanTransactionId);
	
	@Query("from BankStatementDetails bankStatementDetail where bankStatementDetail.bankStatement = :bankStatementId and bankStatementDetail.bankStatementDetailType = :bankStatementDetailType and bankStatementDetail.transactionId IS NULL")
	List<BankStatementDetails> retrieveBankStatementDetailsToCreateNonPortfolioTransactions(@Param("bankStatementId") BankStatement bankStatementId, @Param("bankStatementDetailType") Integer bankStatementDetailType);

}