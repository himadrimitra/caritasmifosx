package com.finflux.portfolio.investmenttracker.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface InvestmentAccountSavingsChargeRepository  extends JpaRepository<InvestmentAccountSavingsCharge, Long>, JpaSpecificationExecutor<InvestmentAccountSavingsCharge>{
    
    public static final String FIND_BY_SAVING_LIKAGE_AND_INVESTMENT_CHARGE = "from InvestmentAccountSavingsCharge charge where charge.investmentAccountCharge.id =:investmentAccountCharge and "
            + " charge.investmentAccountSavingsLinkages.id =:investmentAccountSavingsLinkageId";
    
     @Query(FIND_BY_SAVING_LIKAGE_AND_INVESTMENT_CHARGE)
     InvestmentAccountSavingsCharge findByInvestmentChargeAndSavingLinkedAccount(@Param("investmentAccountCharge") Long investmentAccountCharge, @Param("investmentAccountSavingsLinkageId") Long investmentAccountSavingsLinkageId);
     
     public static final String FIND_BY_SAVING_LIKAGE = "from InvestmentAccountSavingsCharge charge where charge.investmentAccountSavingsLinkages.id =:investmentAccountSavingsLinkageId ";
     
      @Query(FIND_BY_SAVING_LIKAGE)
      List<InvestmentAccountSavingsCharge> findBySavingLinkedAccount(@Param("investmentAccountSavingsLinkageId") Long investmentAccountSavingsLinkageId);
}
