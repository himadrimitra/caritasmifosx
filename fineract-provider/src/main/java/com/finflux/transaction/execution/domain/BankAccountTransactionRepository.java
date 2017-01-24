package com.finflux.transaction.execution.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BankAccountTransactionRepository extends JpaRepository<BankAccountTransaction, Long>, JpaSpecificationExecutor<BankAccountTransaction> {

	List<BankAccountTransaction> findByStatusOrderByExternalServiceIdAsc(Integer status);

	Long countByEntityTypeAndEntityIdAndEntityTransactionIdAndStatusIsIn(Integer entityType,Long entityId,
																		 Long entityTransactionId, List<Integer> statusList);
}
