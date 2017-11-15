/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.portfolio.loanproduct.creditbureau.service;

public interface CreditBureauLoanProductOfficeMappingReadPlatformService {

    Integer retrieveCreditBureauAndLoanProductMappingCount(Long creditBureauProductId, Long loanProductId);

    Integer retrieveLoanProductDefaultMappingCount(Long loanProductId);

    Integer retrieveDefaultCreditBureauAndLoanProductMappingCount(Long creditBureauProductMappingId, Long loanProductId);

    Integer retrieveCurrentCreditBureauAndLoanProductMappingCount(Long creditBureauProductId, Long loanProductId,
            Long creditBureauLoanProductMappingId);

}