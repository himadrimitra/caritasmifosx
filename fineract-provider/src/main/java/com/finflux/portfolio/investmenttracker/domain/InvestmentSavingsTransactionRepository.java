package com.finflux.portfolio.investmenttracker.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface InvestmentSavingsTransactionRepository  extends JpaRepository<InvestmentSavingsTransaction, Long>,
JpaSpecificationExecutor<InvestmentSavingsTransaction> {

}
