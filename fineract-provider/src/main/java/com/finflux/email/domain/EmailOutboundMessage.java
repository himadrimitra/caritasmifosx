/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.email.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_email_messages_outbound")
public class EmailOutboundMessage extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "loanId", referencedColumnName = "id", nullable = false)
    private Loan loan;

    @Column(name = "event_type", nullable = false)
    private String event_type;

    @Column(name = "product_type", nullable = false)
    private Integer product_type;

    @Column(name = "mail_status", nullable = false)
    private String status;

    @Column(name = "sent_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date sent_date;
    
    @Column(name = "loan_amount", nullable = true)
    private BigDecimal loanAmount;
    
    public EmailOutboundMessage() {

    }

    public EmailOutboundMessage(final Loan loan, final String email_id, final Integer productType, final String status, Date sent_date,
            final BigDecimal loanAmount) {
        this.loan = loan;
        this.event_type = email_id;
        this.product_type = productType;
        this.status = status;
        this.sent_date = sent_date;
        this.loanAmount = loanAmount;
    }

    /**
     * @return a new instance of the EmailOutboundMessage class
     **/
    public static EmailOutboundMessage instance(final Loan loan, final String emaill_id, final Integer productType, String status,
            Date sent_Date, BigDecimal loanAmount) {

        return new EmailOutboundMessage(loan, emaill_id, productType, status, sent_Date, loanAmount);
    }

    public Loan getLoan() {
        return this.loan;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public Integer getProduct_type() {
        return this.product_type;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
    public void setSent_date(Date sent_date) {
        this.sent_date = sent_date;
    }
    
    public BigDecimal getLoanAmount(){
        return this.loanAmount;
    }


}
