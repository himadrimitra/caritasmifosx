package com.finflux.portfolio.loanemipacks.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanEMIPackRepository  extends JpaRepository<LoanEMIPack, Long>, JpaSpecificationExecutor<LoanEMIPack> {

}
