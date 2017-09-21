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
package org.apache.fineract.portfolio.loanaccount.domain;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.loanaccount.data.ScheduleGeneratorDTO;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.joda.time.LocalDate;

public interface LoanAccountDomainService {

    LoanTransaction makeRepayment(Loan loan, CommandProcessingResultBuilder builderResult, LocalDate transactionDate,
            BigDecimal transactionAmount, PaymentDetail paymentDetail, String noteText, String txnExternalId,
            final boolean isRecoveryRepayment, boolean isAccountTransfer);

    LoanTransaction makeRefund(Long accountId, CommandProcessingResultBuilder builderResult, LocalDate transactionDate,
            BigDecimal transactionAmount, PaymentDetail paymentDetail, String noteText, String txnExternalId);

    void reverseTransfer(LoanTransaction loanTransaction);

    LoanTransaction makeChargePayment(Loan loan, Long chargeId, LocalDate transactionDate, BigDecimal transactionAmount,
            PaymentDetail paymentDetail, String noteText, String txnExternalId, Integer transactionType, Integer installmentNumber);

    LoanTransaction makeDisburseTransaction(Long loanId, LocalDate transactionDate, BigDecimal transactionAmount,
            PaymentDetail paymentDetail, String noteText, String txnExternalId);

    LocalDate getCalculatedRepaymentsStartingFromDate(LocalDate actualDisbursementDate, Loan loan, CalendarInstance calendarInstance);
    
    LoanTransaction makeRefundForActiveLoan(Long accountId, CommandProcessingResultBuilder builderResult,
			LocalDate transactionDate, BigDecimal transactionAmount,
			PaymentDetail paymentDetail, String noteText, String txnExternalId);

    /**
     * This method is to recalculate and accrue the income till the last accrued
     * date. this method is used when the schedule changes due to interest
     * recalculation
     * 
     * @param loan
     */
    void recalculateAccruals(Loan loan);

    /**
     * This method is used for building ScheduleGeneratorDTO from loan, usages
     * are as, if have loan and some of the terms are modified which may impact
     * the schedule, in that case need to recompute the schedule, so this method
     * helps to build the ScheduleGeneratorDTO from modified loan.
     * 
     * @param loan
     */

    public ScheduleGeneratorDTO buildScheduleGeneratorDTO(final Loan loan);

}
