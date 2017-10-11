/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.charge.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;


public class ChargeSlabData {
    
    private final Long id;
    @SuppressWarnings("unused")
    private BigDecimal minValue;
    @SuppressWarnings("unused")
    private BigDecimal maxValue;
    @SuppressWarnings("unused")
    private BigDecimal amount;
    private EnumOptionData type;
    Collection<ChargeSlabData> subSlabs;
    
    
    public ChargeSlabData(final Long id, final BigDecimal minValue, final BigDecimal maxValue, final BigDecimal amount,
            final EnumOptionData type) {
        this.id = id;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.amount = amount;
        this.type = type;
        this.subSlabs = null;
    }
    
    public static ChargeSlabData createChargeSlabData(final Long id, final BigDecimal minValue, final BigDecimal maxValue, 
            final BigDecimal amount,final EnumOptionData type) {
        return new ChargeSlabData(id,  minValue, maxValue, amount, type);
    }
    
    public Long getId() {
        return this.id;
    }
    
    public Collection<ChargeSlabData> getSubSlabs() {
        return this.subSlabs;
    }
    
    public void setSubSlabs(Collection<ChargeSlabData> subSlabs) {
        this.subSlabs = subSlabs;
    }
    
    public EnumOptionData getType() {
        return this.type;
    }
    
}
