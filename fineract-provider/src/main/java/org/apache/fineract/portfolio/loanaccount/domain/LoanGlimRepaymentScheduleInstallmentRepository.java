/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.domain;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanGlimRepaymentScheduleInstallmentRepository extends JpaRepository<LoanGlimRepaymentScheduleInstallment, Long>,
        JpaSpecificationExecutor<LoanGlimRepaymentScheduleInstallment> {
    
    
    @Query("from LoanGlimRepaymentScheduleInstallment loanGlimRepayment where loanGlimRepayment.groupLoanIndividualMonitoring.id IN :ids ")
    List<LoanGlimRepaymentScheduleInstallment> getLoanGlimRepaymentScheduleInstallmentByGlimIds(@Param("ids") Collection<Long> ids);
    
    @Query("from LoanGlimRepaymentScheduleInstallment loanGlimRepayment where loanGlimRepayment.groupLoanIndividualMonitoring.id =:glimId and loanGlimRepayment.loanRepaymentScheduleInstallment.id =:ids ")
    LoanGlimRepaymentScheduleInstallment getLoanGlimRepaymentScheduleByLoanRepaymentScheduleId(@Param("ids") Long ids, @Param("glimId") Long glimId);

}