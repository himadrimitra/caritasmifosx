package com.finflux.reconcilation.bankstatement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BankStatementRepository extends JpaRepository<BankStatement, Long>, JpaSpecificationExecutor<BankStatement> {

}
