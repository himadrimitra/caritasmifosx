package com.finflux.organisation.transaction.authentication.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionAuthenticationRepository extends JpaRepository<TransactionAuthentication, Long> {
	
	List<TransactionAuthentication> findByPortfolioTypeAndTransactionTypeId(final Integer portfolioType, final Integer transactionTypeId);

}
