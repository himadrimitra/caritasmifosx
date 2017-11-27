package com.finflux.portfolio.investmenttracker.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface InvestmentAccountSavingsLinkagesRepository  extends JpaRepository<InvestmentAccountSavingsLinkages, Long>, JpaSpecificationExecutor<InvestmentAccountSavingsLinkages>{

}
