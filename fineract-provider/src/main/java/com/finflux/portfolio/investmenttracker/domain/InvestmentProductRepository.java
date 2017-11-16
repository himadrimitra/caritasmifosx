package com.finflux.portfolio.investmenttracker.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvestmentProductRepository extends JpaRepository<InvestmentProduct, Long>, JpaSpecificationExecutor<InvestmentProduct> {

}
