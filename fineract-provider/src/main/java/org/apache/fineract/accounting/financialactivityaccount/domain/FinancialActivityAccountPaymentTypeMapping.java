/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.accounting.financialactivityaccount.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_financial_activity_account_payment_type_mapping")
public class FinancialActivityAccountPaymentTypeMapping extends AbstractPersistable<Long>{

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gl_account_id")
    private GLAccount glAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_type_id")
    private PaymentType paymentType;

    public static FinancialActivityAccountPaymentTypeMapping createNew(final GLAccount glAccount, final PaymentType paymentType) {
        return new FinancialActivityAccountPaymentTypeMapping(glAccount, paymentType);
    }

    
    protected FinancialActivityAccountPaymentTypeMapping() {
        //
    }

    private FinancialActivityAccountPaymentTypeMapping(final GLAccount glAccount, final PaymentType paymentType) {
        this.glAccount = glAccount;
        this.paymentType = paymentType;
    };

    public GLAccount getGlAccount() {
        return this.glAccount;
    }

    public void updateGlAccount(final GLAccount glAccount) {
        this.glAccount = glAccount;
    }
    
    public void updatePaymentType(final PaymentType paymentType){
        this.paymentType = paymentType;
    }
    
    public PaymentType getPaymentType (){
        return this.paymentType;
    }

}
