/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

public interface GroupLoanIndividualMonitoringRepository extends JpaRepository<GroupLoanIndividualMonitoring, Long>,
        JpaSpecificationExecutor<GroupLoanIndividualMonitoring> {

    List<GroupLoanIndividualMonitoring> findByLoanIdAndIsClientSelected(@Param("loanId") Long loanId,
            @Param("isClientSelected") Boolean isClientSelected);

    List<GroupLoanIndividualMonitoring> findByLoanId(@Param("loanId") Long loanId);
    
    List<GroupLoanIndividualMonitoring> findByClientId(@Param("clientId") Long clientId);

}
