/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.charge.data;

import java.math.BigDecimal;


public class ChargeSlabData {
    
    private final Long id;
    private BigDecimal fromLoanAmount;
    private BigDecimal toLoanAmount;
    private BigDecimal amount;
    
    
    
    public ChargeSlabData(final Long id, final BigDecimal fromLoanAmount, final BigDecimal toLoanAmount, final BigDecimal amount) {
        this.id = id;
        this.fromLoanAmount = fromLoanAmount;
        this.toLoanAmount = toLoanAmount;
        this.amount = amount;
    }
    
    public static ChargeSlabData createChargeSlabData(final Long id, final BigDecimal fromLoanAmount, final BigDecimal toLoanAmount, 
            final BigDecimal amount) {
        return new ChargeSlabData(id,  fromLoanAmount, toLoanAmount, amount);
    }
    
}
