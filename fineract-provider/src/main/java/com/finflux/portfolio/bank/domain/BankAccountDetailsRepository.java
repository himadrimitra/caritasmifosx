package com.finflux.portfolio.bank.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BankAccountDetailsRepository extends JpaRepository<BankAccountDetails, Long>, JpaSpecificationExecutor<BankAccountDetails> {

}
