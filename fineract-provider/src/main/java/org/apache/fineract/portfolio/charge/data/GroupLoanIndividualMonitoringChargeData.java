/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.charge.data;

import java.math.BigDecimal;

public class GroupLoanIndividualMonitoringChargeData {

    private final Long id;
    private final Long glim;
    private final Long client;
    private final Long charge;
    private final BigDecimal feeAmount;
    private final BigDecimal revisedFeeAmount;
    private final BigDecimal waivedChargeAmount;

    public GroupLoanIndividualMonitoringChargeData(final Long id, final Long glim, final Long client, final Long charge,
            final BigDecimal feeAmount, final BigDecimal revisedFeeAmount, final BigDecimal waivedChargeAmount) {
        this.id = id;
        this.glim = glim;
        this.client = client;
        this.charge = charge;
        this.feeAmount = feeAmount;
        this.revisedFeeAmount = revisedFeeAmount;
        this.waivedChargeAmount = waivedChargeAmount;
    }

    public static GroupLoanIndividualMonitoringChargeData instance(final Long id, final Long glim, final Long client, final Long charge,
            final BigDecimal feeAmount, final BigDecimal revisedFeeAmount, final BigDecimal waivedChargeAmount) {
        return new GroupLoanIndividualMonitoringChargeData(id, glim, client, charge, feeAmount, revisedFeeAmount, waivedChargeAmount);
    }

    public Long getId() {
        return this.id;
    }

    public Long getGlim() {
        return this.glim;
    }

    public Long getClient() {
        return this.client;
    }

    public Long getCharge() {
        return this.charge;
    }

    public BigDecimal getFeeAmount() {
        return this.feeAmount;
    }

    public BigDecimal getRevisedFeeAmount() {
        return this.revisedFeeAmount;
    }

    public BigDecimal getWaivedChargeAmount() {
        return this.waivedChargeAmount;
    }

}
