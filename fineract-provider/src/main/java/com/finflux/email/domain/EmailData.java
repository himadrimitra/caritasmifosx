/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.email.domain;


import java.io.File;
import java.math.BigDecimal;

/**
 * data object representing Email data.
 **/
public class EmailData {

    private final String to;
    private final String text;
    private final String subject;
    private final File attachment;
    private final String accountNumber;
    private final String centerDisplayName;

    public EmailData(final String accountNumber, final String to, final File attachment, final Long clientId, BigDecimal loanAmount,
            final String centerDisplayName) {
        this.accountNumber = accountNumber;
        this.to = to;
        this.text = "Loan is dibursed. Please find the attached file containing information about Repayment schedule";
        this.subject = "Account Number " + accountNumber + " ClientId  " + clientId + " Amount " + loanAmount
                + " - Loan Disbursement and Repayment Details";
        this.attachment = attachment;
        this.centerDisplayName = centerDisplayName;
    }

    /**
     * @return the to
     */
    public String getTo() {
        return to;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return the attachment
     */
    public File getAttachment() {
        return attachment;
    }

    /**
     * @return the accountNumber
     */
    public String getAccountNumber() {
        return this.accountNumber;
    }
    
    public String getCenterDisplayName() {
        return this.centerDisplayName;
    }

}
