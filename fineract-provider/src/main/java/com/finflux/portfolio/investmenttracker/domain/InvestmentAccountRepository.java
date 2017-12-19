package com.finflux.portfolio.investmenttracker.domain;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface InvestmentAccountRepository extends JpaRepository<InvestmentAccount, Long>, JpaSpecificationExecutor<InvestmentAccount>{

    @Query("from InvestmentAccount investmentAccount WHERE investmentAccount.status = :status and  investmentAccount.maturityOnDate <= :maturityOnDate")
    Collection<InvestmentAccount> findByStatusAndMaturityOnDate(@Param("status") Integer status, @Param("maturityOnDate") Date maturityOnDate);
}
