
/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.interestratechart.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_floating_interest_rate_chart")
public class FloatingInterestRateChart extends AbstractPersistable<Long> {

    @Temporal(TemporalType.DATE)
    @Column(name = "effective_date", nullable = false)
    private Date effectiveFromDate;

    @Column(name = "interest_rate", scale = 6, precision = 19, nullable = false)
    private BigDecimal interestRate;
    
    protected FloatingInterestRateChart(){
        
    }
    public FloatingInterestRateChart(final LocalDate effectiveFromDate, final BigDecimal interestRate) {
        this.effectiveFromDate = effectiveFromDate.toDate();
        this.interestRate = interestRate;
    }

    public LocalDate getEffectiveFromAsLocalDate() {
        return new LocalDate(this.effectiveFromDate); 
    }
    
    public BigDecimal getInterestRate(){
        return this.interestRate;
    }
    
    public void updateEffectiveFromDate(LocalDate effectiveFromDate){
        if(effectiveFromDate != null){
            this.effectiveFromDate =  effectiveFromDate.toDate(); 
        }
    }
    
    public void updateInterestRate(BigDecimal interestRate){
        this.interestRate = interestRate;
    }
    
}

