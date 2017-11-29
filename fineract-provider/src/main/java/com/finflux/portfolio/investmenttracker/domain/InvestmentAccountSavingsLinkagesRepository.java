package com.finflux.portfolio.investmenttracker.domain;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface InvestmentAccountSavingsLinkagesRepository  extends JpaRepository<InvestmentAccountSavingsLinkages, Long>, JpaSpecificationExecutor<InvestmentAccountSavingsLinkages>{

    @Query("SELECT linkageAccount.id from InvestmentAccountSavingsLinkages linkageAccount WHERE linkageAccount.investmentAccount.id = :investmentAccountId and linkageAccount.status = :status")
    Collection<Long> findIdsByInvestmentAccountIdAndStatus(@Param("investmentAccountId") Long investmentAccountId,@Param("status") Integer status);
    
}
