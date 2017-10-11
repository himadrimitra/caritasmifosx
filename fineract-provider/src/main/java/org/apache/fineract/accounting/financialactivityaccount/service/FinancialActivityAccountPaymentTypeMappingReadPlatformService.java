/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.accounting.financialactivityaccount.service;

import java.util.List;

import org.apache.fineract.accounting.financialactivityaccount.data.FinancialActivityAccountPaymentTypeMappingData;

public interface FinancialActivityAccountPaymentTypeMappingReadPlatformService {

    List<FinancialActivityAccountPaymentTypeMappingData> retrieve(Long financialActivityAccountId);
    
}