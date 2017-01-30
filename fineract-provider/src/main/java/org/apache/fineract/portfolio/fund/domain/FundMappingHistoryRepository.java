/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.fund.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FundMappingHistoryRepository extends JpaRepository<FundMappingHistory, Long>, JpaSpecificationExecutor<FundMappingHistory> {

    public static final String FIND_PREVIOUS_HISTORY_BY_LOAN = "from FundMappingHistory f where "
            + "f.loan.id = :loanId order by f.id DESC  ";

    @Query(FIND_PREVIOUS_HISTORY_BY_LOAN)
    List<FundMappingHistory> findPreviousHistoryByLoan(@Param("loanId") Long loanId);
}
