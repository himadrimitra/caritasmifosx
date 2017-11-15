package com.finflux.portfolio.investmenttracker.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface InvestmentAccountRepository extends JpaRepository<InvestmentAccount, Long>, JpaSpecificationExecutor<InvestmentAccount>{

}
