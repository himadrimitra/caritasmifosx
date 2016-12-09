package com.finflux.transaction.execution.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.finflux.portfolio.bank.domain.BankAccountDetails;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BankAccountTransactionRepository extends JpaRepository<BankAccountTransaction, Long>, JpaSpecificationExecutor<BankAccountTransaction> {

	List<BankAccountTransaction> findByStatusOrderByExternalServiceIdAsc(Integer status);
}
