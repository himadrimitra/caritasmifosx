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
package org.apache.fineract.portfolio.loanaccount.loanschedule.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleRequest;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_loan_repayment_schedule_history")
public class LoanRepaymentScheduleHistory extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @OneToOne(optional = true)
    @JoinColumn(name = "loan_reschedule_request_id")
    private LoanRescheduleRequest loanRescheduleRequest;

    @Column(name = "installment", nullable = false)
    private Integer installmentNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "fromdate", nullable = true)
    private Date fromDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "duedate", nullable = false)
    private Date dueDate;

    @Column(name = "principal_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal principal;

    @Column(name = "interest_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal interestCharged;

    @Column(name = "fee_charges_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal feeChargesCharged;

    @Column(name = "penalty_charges_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal penaltyCharges;

    @Column(name = "version")
    private Integer version;

    @Column(name = "recalculated_interest_component", nullable = false)
    private Boolean recalculatedInterestComponent;

    /**
     * LoanRepaymentScheduleHistory constructor
     **/
    protected LoanRepaymentScheduleHistory() {}

    /**
     * LoanRepaymentScheduleHistory constructor
     **/
    private LoanRepaymentScheduleHistory(final Loan loan, final LoanRescheduleRequest loanRescheduleRequest,
            final Integer installmentNumber, final Date fromDate, final Date dueDate, final BigDecimal principal,
            final BigDecimal interestCharged, final BigDecimal feeChargesCharged, final BigDecimal penaltyCharges, final Integer version,
            final Boolean recalculatedInterestComponent) {

        this.loan = loan;
        this.loanRescheduleRequest = loanRescheduleRequest;
        this.installmentNumber = installmentNumber;
        this.fromDate = fromDate;
        this.dueDate = dueDate;
        this.principal = principal;
        this.interestCharged = interestCharged;
        this.feeChargesCharged = feeChargesCharged;
        this.penaltyCharges = penaltyCharges;
        this.version = version;
        this.recalculatedInterestComponent = recalculatedInterestComponent;
    }

    /**
     * @return an instance of the LoanRepaymentScheduleHistory class
     **/
    public static LoanRepaymentScheduleHistory instance(final Loan loan, final LoanRescheduleRequest loanRescheduleRequest,
            final Integer installmentNumber, final Date fromDate, final Date dueDate, final BigDecimal principal,
            final BigDecimal interestCharged, final BigDecimal feeChargesCharged, final BigDecimal penaltyCharges, final Integer version,
            final boolean recalculatedInterestComponent) {

        return new LoanRepaymentScheduleHistory(loan, loanRescheduleRequest, installmentNumber, fromDate, dueDate, principal,
                interestCharged, feeChargesCharged, penaltyCharges, version, recalculatedInterestComponent);

    }

}