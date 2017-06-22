/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.accounting.financialactivityaccount.data;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;

public class FinancialActivityAccountPaymentTypeMappingData {

    private final Long id;
    private final GLAccountData gLAccountData;
    private final PaymentTypeData paymentTypeData;

    public FinancialActivityAccountPaymentTypeMappingData(Long id, GLAccountData gLAccountData,
            PaymentTypeData paymentTypeData  ) {
        super();
        this.id = id;
        this.gLAccountData = gLAccountData;
        this.paymentTypeData = paymentTypeData;
    }

}
