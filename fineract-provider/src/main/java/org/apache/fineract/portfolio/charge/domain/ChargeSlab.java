/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.charge.domain;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_charge_slab")
public class ChargeSlab extends AbstractPersistable<Long> {

    @Column(name = "from_loan_amount", scale = 6, precision = 19)
    private BigDecimal minValue;

    @Column(name = "to_loan_amount", scale = 6, precision = 19)
    private BigDecimal maxValue;

    @Column(name = "amount", scale = 6, precision = 19)
    private BigDecimal amount;
    
    @Column(name = "type")
    private Integer type;
    
    @ManyToOne(fetch = FetchType.LAZY, optional=true)
    @JoinColumn(name = "parent_id")
    private ChargeSlab parent;
    
    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(mappedBy="parent", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<ChargeSlab> subSlabs = new LinkedList<>();

    public ChargeSlab() {

    }

    private ChargeSlab(final BigDecimal minValue, final BigDecimal maxValue, BigDecimal amount, final Integer type,final ChargeSlab parent) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.amount = amount;
        this.type = type;
        this.parent = parent;
        this.subSlabs = new LinkedList<>();
    }

    public static ChargeSlab createNew(final BigDecimal minValue, final BigDecimal maxValue, BigDecimal amount, final Integer type,final ChargeSlab parent) {
        return new ChargeSlab(minValue, maxValue, amount,type, parent);
    }

    public boolean isValueFallsInSlab(final BigDecimal principalAmount) {
        boolean isValueFallsInSlab = false;
        if (MathUtility.isEqualOrGreater(principalAmount, this.minValue) && MathUtility.isLesserOrEqualTo(principalAmount, this.maxValue)) {
            isValueFallsInSlab = true;
        }
        return isValueFallsInSlab;
    }

    
    public List<ChargeSlab> getSubSlabs() {
        return this.subSlabs;
    }    
    
    public ChargeSlab getParent() {
        return this.parent;
    }

    
    public void setParent(ChargeSlab parent) {
        this.parent = parent;
    }

    public void setSubSlabs(List<ChargeSlab> subSlabs) {
        this.subSlabs.addAll(subSlabs);
    }

    public Integer getType() {
        return this.type;
    }

    
    public BigDecimal getMinValue() {
        return this.minValue;
    }
 
    public BigDecimal getMaxValue() {
        return this.maxValue;
    }
    
    
}
