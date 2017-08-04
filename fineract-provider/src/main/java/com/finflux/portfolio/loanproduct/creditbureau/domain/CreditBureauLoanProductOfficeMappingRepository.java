/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.portfolio.loanproduct.creditbureau.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CreditBureauLoanProductOfficeMappingRepository extends JpaRepository<CreditBureauLoanProductOfficeMapping, Long> {

    CreditBureauLoanProductOfficeMapping findByLoanProductIdAndOfficeId(final Long loanProductId, final Long officeId);

    @Query("from CreditBureauLoanProductOfficeMapping mapping where mapping.loanProductId=:loanProductId and mapping.officeId=:officeId")
    CreditBureauLoanProductOfficeMapping retrieveCreditBureauAndLoanProductOfficeMapping(@Param("loanProductId") Long loanProductId,
            @Param("officeId") Long officeId);

    @Query("from CreditBureauLoanProductOfficeMapping mapping where mapping.loanProductId=:loanProductId and mapping.officeId is null")
    CreditBureauLoanProductOfficeMapping retrieveDefaultCreditBureauAndLoanProductOfficeMapping(@Param("loanProductId") Long loanProductId);

}
