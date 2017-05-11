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
import java.util.Locale;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.apache.fineract.portfolio.loanaccount.data.AdjustedLoanTransactionDetails;

public interface LoanAccountDomainService {

    LoanTransaction makeRepayment(Loan loan, CommandProcessingResultBuilder builderResult, LocalDate transactionDate,
            BigDecimal transactionAmount, PaymentDetail paymentDetail, String noteText, String txnExternalId,
            final boolean isRecoveryRepayment, boolean isAccountTransfer, HolidayDetailDTO holidatDetailDto, Boolean isHolidayValidationDone);

    LoanTransaction makeRefund(Long accountId, CommandProcessingResultBuilder builderResult, LocalDate transactionDate,
            BigDecimal transactionAmount, PaymentDetail paymentDetail, String noteText, String txnExternalId, Boolean isAccountTransfer);

    LoanTransaction makeDisburseTransaction(Long loanId, LocalDate transactionDate, BigDecimal transactionAmount,
            PaymentDetail paymentDetail, String noteText, String txnExternalId, boolean isLoanToLoanTransfer);

    void reverseTransfer(LoanTransaction loanTransaction);

    LoanTransaction makeChargePayment(Loan loan, Long chargeId, LocalDate transactionDate, BigDecimal transactionAmount,
            PaymentDetail paymentDetail, String noteText, String txnExternalId, Integer transactionType, Integer installmentNumber);

    LoanTransaction makeDisburseTransaction(Long loanId, LocalDate transactionDate, BigDecimal transactionAmount,
            PaymentDetail paymentDetail, String noteText, String txnExternalId);

    LoanTransaction makeRefundForActiveLoan(Long accountId, CommandProcessingResultBuilder builderResult, LocalDate transactionDate,
            BigDecimal transactionAmount, PaymentDetail paymentDetail, String noteText, String txnExternalId);
    
    LoanTransaction addOrRevokeLoanSubsidy(Loan loan, CommandProcessingResultBuilder builderResult, LocalDate transactionDate,
            Money transactionAmount, PaymentDetail paymentDetail, String txnExternalId, boolean isAccountTransfer,
            HolidayDetailDTO holidatDetailDto, LoanTransactionType loanTransactionType);
    
    /**
     * This method is to recalculate and accrue the income till the last accrued
     * date. this method is used when the schedule changes due to interest
     * recalculation
     * 
     * @param loan
     */
    void recalculateAccruals(Loan loan);

    LoanTransaction makeRepayment(Loan loan, CommandProcessingResultBuilder builderResult, LocalDate transactionDate,
            BigDecimal transactionAmount, PaymentDetail paymentDetail, String noteText, String txnExternalId, boolean isRecoveryRepayment,
            boolean isAccountTransfer, HolidayDetailDTO holidayDetailDto, Boolean isHolidayValidationDone, boolean isLoanToLoanTransfer);

    void saveLoanWithDataIntegrityViolationChecks(Loan loan);

    AdjustedLoanTransactionDetails reverseLoanTransactions(Loan loan, Long transactionId, LocalDate transactionDate,
            BigDecimal transactionAmount, String txnExternalId, Locale locale, DateTimeFormatter dateFormat, String noteText,
            PaymentDetail paymentDetail, boolean isAccountTransfer);

    LoanTransaction foreCloseLoan(final Loan loan, final LocalDate foreClourseDate, String noteText, boolean isAccountTransfer, boolean isLoanToLoanTransfer, Map<String, Object> changes);
    
    /**
     * Disables all standing instructions linked to a closed loan
     * 
     * @param loan {@link Loan} object
     */
    void disableStandingInstructionsLinkedToClosedLoan(Loan loan);

    void recalculateAccruals(Loan loan, boolean isInterestCalcualtionHappened);
    
    LoanTransaction waiveInterest(Loan loan, CommandProcessingResultBuilder builderResult, LocalDate transactionDate,
            BigDecimal transactionAmount, String noteText, Map<String, Object> changes);

    LoanTransaction writeOffForGlimLoan(JsonCommand command, Loan loan, CommandProcessingResultBuilder builderResult, String noteText, Map<String, Object> changes);

    LoanTransaction makeRepayment(final Long loanId, final CommandProcessingResultBuilder builderResult,
            final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail, final String noteText,
            final String txnExternalId, final boolean isRecoveryRepayment, boolean isAccountTransfer, HolidayDetailDTO holidayDetailDto,
            Boolean isHolidayValidationDone, final boolean isLoanToLoanTransfer, final boolean isPrepayment) ;
    
    LoanTransaction makeRepayment(Loan loan, CommandProcessingResultBuilder builderResult, LocalDate transactionDate,
            BigDecimal transactionAmount, PaymentDetail paymentDetail, String noteText, String txnExternalId, boolean isRecoveryRepayment,
            boolean isAccountTransfer, HolidayDetailDTO holidayDetailDto, Boolean isHolidayValidationDone, boolean isLoanToLoanTransfer,
            boolean isPrepayment);

}
