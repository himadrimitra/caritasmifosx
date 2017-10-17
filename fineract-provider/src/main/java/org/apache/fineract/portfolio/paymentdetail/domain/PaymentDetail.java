/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.paymentdetail.domain;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.paymentdetail.PaymentDetailConstants;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_payment_detail")
public final class PaymentDetail extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "payment_type_id", nullable = false)
    private PaymentType paymentType;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "check_number", length = 50)
    private String checkNumber;

    @Column(name = "routing_code", length = 50)
    private String routingCode;

    @Column(name = "receipt_number", length = 50)
    private String receiptNumber;

    @Column(name = "bank_number", length = 50)
    private String bankNumber;

    @Column(name = "branch_name", length = 200, nullable = true)
    private String branchName;

    @Temporal(TemporalType.DATE)
    @Column(name = "payment_date", nullable = true)
    private Date paymentDate;

    protected PaymentDetail() {

    }

    public static PaymentDetail generatePaymentDetail(final PaymentType paymentType, final JsonCommand command,
            final Map<String, Object> changes) {
        final String accountNumber = command.stringValueOfParameterNamed(PaymentDetailConstants.accountNumberParamName);
        final String checkNumber = command.stringValueOfParameterNamed(PaymentDetailConstants.checkNumberParamName);
        final String routingCode = command.stringValueOfParameterNamed(PaymentDetailConstants.routingCodeParamName);
        final String receiptNumber = command.stringValueOfParameterNamed(PaymentDetailConstants.receiptNumberParamName);
        final String bankNumber = command.stringValueOfParameterNamed(PaymentDetailConstants.bankNumberParamName);

        if (StringUtils.isNotBlank(accountNumber)) {
            changes.put(PaymentDetailConstants.accountNumberParamName, accountNumber);
        }
        if (StringUtils.isNotBlank(checkNumber)) {
            changes.put(PaymentDetailConstants.checkNumberParamName, checkNumber);
        }
        if (StringUtils.isNotBlank(routingCode)) {
            changes.put(PaymentDetailConstants.routingCodeParamName, routingCode);
        }
        if (StringUtils.isNotBlank(receiptNumber)) {
            changes.put(PaymentDetailConstants.receiptNumberParamName, receiptNumber);
        }
        if (StringUtils.isNotBlank(bankNumber)) {
            changes.put(PaymentDetailConstants.bankNumberParamName, bankNumber);
        }
        final String branchName = null;
        final Date paymentDate = null;
        final PaymentDetail paymentDetail = new PaymentDetail(paymentType, accountNumber, checkNumber, routingCode, receiptNumber,
                bankNumber, branchName, paymentDate);
        return paymentDetail;
    }

    public static PaymentDetail instance(final PaymentType paymentType, final String accountNumber, final String checkNumber,
            final String routingCode, final String receiptNumber, final String bankNumber) {
        final String branchName = null;
        final Date paymentDate = null;
        return new PaymentDetail(paymentType, accountNumber, checkNumber, routingCode, receiptNumber, bankNumber, branchName, paymentDate);
    }

    public static PaymentDetail instance(final PaymentType paymentType, final String checkNumber, final String bankNumber,
            final String branchName, final Date paymentDate) {
        final String accountNumber = null;
        final String routingCode = null;
        final String receiptNumber = null;
        return new PaymentDetail(paymentType, accountNumber, checkNumber, routingCode, receiptNumber, bankNumber, branchName, paymentDate);
    }

    private PaymentDetail(final PaymentType paymentType, final String accountNumber, final String checkNumber, final String routingCode,
            final String receiptNumber, final String bankNumber, final String branchName, final Date paymentDate) {
        this.paymentType = paymentType;
        this.accountNumber = accountNumber;
        this.checkNumber = checkNumber;
        this.routingCode = routingCode;
        this.receiptNumber = receiptNumber;
        this.bankNumber = bankNumber;
        this.branchName = branchName;
        this.paymentDate = paymentDate;
    }

    public PaymentDetailData toData() {
        final PaymentTypeData paymentTypeData = this.paymentType.toData();
        final PaymentDetailData paymentDetailData = new PaymentDetailData(getId(), paymentTypeData, this.accountNumber, this.checkNumber,
                this.routingCode, this.receiptNumber, this.bankNumber, this.branchName, this.paymentDate);
        return paymentDetailData;
    }

    public PaymentType getPaymentType() {
        return this.paymentType;
    }

    public boolean setPaymentType(final PaymentType paymentType) {
        boolean changed = false;
        if (!this.paymentType.getId().equals(paymentType.getId())) {
            this.paymentType = paymentType;
            changed = true;
        }
        return changed;

    }

    public boolean setCheckNumber(final String checkNumber) {
        boolean changed = false;
        if (this.checkNumber == null || !this.checkNumber.equals(checkNumber)) {
            this.checkNumber = checkNumber;
            changed = true;
        }
        return changed;

    }

    public boolean setPaymentDate(final Date paymentDate) {
        boolean changed = false;
        if (this.paymentDate == null || !this.paymentDate.equals(paymentDate)) {
            this.paymentDate = paymentDate;
            changed = true;
        }
        return changed;

    }

    public boolean setBankNumber(final String bankNumber) {
        boolean changed = false;
        if (this.bankNumber == null || !this.bankNumber.equals(bankNumber)) {
            this.bankNumber = bankNumber;
            changed = true;
        }
        return changed;
    }

    public boolean setBranchName(final String branchName) {
        boolean changed = false;
        if (this.branchName == null || !this.branchName.equals(branchName)) {
            this.branchName = branchName;
            changed = true;
        }
        return changed;
    }

    public void updatePaymentDetail(final String paymentDetailAccountNumber, final String paymentDetailChequeNumber,
            final String routingCode, final String receiptNumber, final String paymentDetailBankNumber) {
        this.accountNumber = paymentDetailAccountNumber;
        this.checkNumber = paymentDetailChequeNumber;
        this.routingCode = routingCode;
        this.receiptNumber = receiptNumber;
        this.bankNumber = paymentDetailBankNumber;
    }

    public String getReceiptNumber() {
        return this.receiptNumber;
    }
}