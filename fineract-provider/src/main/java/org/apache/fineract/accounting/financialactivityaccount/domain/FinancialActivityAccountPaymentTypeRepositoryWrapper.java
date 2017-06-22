/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.accounting.financialactivityaccount.domain;

import org.apache.fineract.accounting.financialactivityaccount.exception.FinancialActivityAccountNotFoundException;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Wrapper for {@link FinancialActivityAccountRepository} that adds NULL
 * checking and Error handling capabilities
 * </p>
 */
@Service
public class FinancialActivityAccountPaymentTypeRepositoryWrapper {

    private final FinancialActivityAccountPaymentTypeMappingRepository repository;

    @Autowired
    public FinancialActivityAccountPaymentTypeRepositoryWrapper(final FinancialActivityAccountPaymentTypeMappingRepository repository) {
        this.repository = repository;
    }

    public FinancialActivityAccountPaymentTypeMapping findByPaymentType(PaymentType paymentType) {
        final FinancialActivityAccountPaymentTypeMapping financialActivityAccountPaymentTypeMapping = this.repository.findByPaymentType(paymentType);
        return financialActivityAccountPaymentTypeMapping;
    } 
}