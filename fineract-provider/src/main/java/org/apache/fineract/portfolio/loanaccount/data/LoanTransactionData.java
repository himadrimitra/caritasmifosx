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
package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.account.data.AccountTransferData;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.useradministration.data.AppUserData;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.finflux.fingerprint.data.FingerPrintData;
import com.finflux.organisation.transaction.authentication.data.TransactionAuthenticationData;

/**
 * Immutable data object representing a loan transaction.
 */
public class LoanTransactionData {

    private final Long id;
    private final Long officeId;
    private final String officeName;

    private final LoanTransactionEnumData type;

    private final LocalDate date;
    private final LocalDateTime createdDate;
    private final LocalDateTime updatedDate;
    private final AppUserData createdBy;
    private final AppUserData updatedBy;

    private final CurrencyData currency;
    private final PaymentDetailData paymentDetailData;

    private final BigDecimal amount;
    private final BigDecimal principalPortion;
    private final BigDecimal interestPortion;
    private final BigDecimal feeChargesPortion;
    private final BigDecimal penaltyChargesPortion;
    private final BigDecimal overpaymentPortion;
    private final BigDecimal unrecognizedIncomePortion;
    private final String externalId;
    private final AccountTransferData transfer;
    private final BigDecimal fixedEmiAmount;
    private final BigDecimal outstandingLoanBalance;
    private final LocalDate submittedOnDate;
    private final boolean manuallyReversed;
    private final LocalDate possibleNextRepaymentDate;
    @SuppressWarnings("unused")
    private BigDecimal netDisbursalAmount;
    @SuppressWarnings("unused")
    private BigDecimal discountOnDisbursalAmount;
    @SuppressWarnings("unused")
    private LocalDate expectedFirstRepaymentOnDate;

    // templates
    final Collection<PaymentTypeData> paymentTypeOptions;
    @SuppressWarnings("unused")
    private String groupExternalId = null;
    @SuppressWarnings("unused")
    private String loanAccountNumber = null;
    @SuppressWarnings("unused")
    private  Collection<CodeValueData> writeOffReasonOptions = null;
    private final Collection<TransactionAuthenticationData> transactionAuthenticationOptions;
    @SuppressWarnings("unused")
    private final LoanAccountData loanAccountData;
    private final Collection<FingerPrintData> fingerPrintData;
    private List<GroupLoanIndividualMonitoringTransactionData> glimTransactions;
    @SuppressWarnings("unused")
    private final LoanOverdueData loanOverdueData;

    public static LoanTransactionData templateOnTop(final LoanTransactionData loanTransactionData,
            final Collection<PaymentTypeData> paymentTypeOptions) {
        return new LoanTransactionData(loanTransactionData.id, loanTransactionData.officeId, loanTransactionData.officeName,
                loanTransactionData.type, loanTransactionData.paymentDetailData, loanTransactionData.currency, loanTransactionData.date,
                loanTransactionData.amount, loanTransactionData.principalPortion, loanTransactionData.interestPortion,
                loanTransactionData.feeChargesPortion, loanTransactionData.penaltyChargesPortion, loanTransactionData.overpaymentPortion,
                loanTransactionData.unrecognizedIncomePortion, paymentTypeOptions, loanTransactionData.externalId,
                loanTransactionData.transfer, loanTransactionData.fixedEmiAmount, loanTransactionData.outstandingLoanBalance,
                loanTransactionData.manuallyReversed, loanTransactionData.createdDate, loanTransactionData.updatedDate, 
                loanTransactionData.createdBy, loanTransactionData.updatedBy, loanTransactionData.glimTransactions);

    }

    public LoanTransactionData(final Long id, final Long officeId, final String officeName, final LoanTransactionEnumData transactionType,
            final PaymentDetailData paymentDetailData, final CurrencyData currency, final LocalDate date, final BigDecimal amount,
            final BigDecimal principalPortion, final BigDecimal interestPortion, final BigDecimal feeChargesPortion,
            final BigDecimal penaltyChargesPortion, final BigDecimal overpaymentPortion, final String externalId,
            final AccountTransferData transfer, BigDecimal fixedEmiAmount, BigDecimal outstandingLoanBalance,
            final BigDecimal unrecognizedIncomePortion,final boolean manuallyReversed) {
        this(id, officeId, officeName, transactionType, paymentDetailData, currency, date, amount, principalPortion, interestPortion,
                feeChargesPortion, penaltyChargesPortion, overpaymentPortion, unrecognizedIncomePortion, null, externalId, transfer,
                fixedEmiAmount, outstandingLoanBalance,manuallyReversed);
    }
 
    public LoanTransactionData(final Long id, final Long officeId, final String officeName, final LoanTransactionEnumData transactionType,
            final PaymentDetailData paymentDetailData, final CurrencyData currency, final LocalDate date, final BigDecimal amount,
            final BigDecimal principalPortion, final BigDecimal interestPortion, final BigDecimal feeChargesPortion,
            final BigDecimal penaltyChargesPortion, final BigDecimal overpaymentPortion, BigDecimal unrecognizedIncomePortion,
            final Collection<PaymentTypeData> paymentTypeOptions, final String externalId, final AccountTransferData transfer,
            final BigDecimal fixedEmiAmount, BigDecimal outstandingLoanBalance,boolean manuallyReversed) {
        this(id, officeId, officeName, transactionType, paymentDetailData, currency, date, amount, principalPortion, interestPortion,
                feeChargesPortion, penaltyChargesPortion, overpaymentPortion, unrecognizedIncomePortion, paymentTypeOptions, externalId,
                transfer, fixedEmiAmount, outstandingLoanBalance, null,manuallyReversed);
    }

    public LoanTransactionData(final Long id, final Long officeId, final String officeName, final LoanTransactionEnumData transactionType,
            final PaymentDetailData paymentDetailData, final CurrencyData currency, final LocalDate date, final BigDecimal amount,
            final BigDecimal principalPortion, final BigDecimal interestPortion, final BigDecimal feeChargesPortion,
            final BigDecimal penaltyChargesPortion, final BigDecimal overpaymentPortion, final BigDecimal unrecognizedIncomePortion,
            final String externalId, final AccountTransferData transfer, BigDecimal fixedEmiAmount, BigDecimal outstandingLoanBalance,
            LocalDate submittedOnDate,final boolean manuallyReversed) {
        this(id, officeId, officeName, transactionType, paymentDetailData, currency, date, amount, principalPortion, interestPortion,
                feeChargesPortion, penaltyChargesPortion, overpaymentPortion, unrecognizedIncomePortion, null, externalId, transfer,
                fixedEmiAmount, outstandingLoanBalance, submittedOnDate,manuallyReversed);
    }
    
    public LoanTransactionData(final Long id, final Long officeId, final String officeName, final LoanTransactionEnumData transactionType,
            final PaymentDetailData paymentDetailData, final CurrencyData currency, final LocalDate date, final BigDecimal amount,
            final BigDecimal principalPortion, final BigDecimal interestPortion, final BigDecimal feeChargesPortion,
            final BigDecimal penaltyChargesPortion, final BigDecimal overpaymentPortion, BigDecimal unrecognizedIncomePortion,
            final Collection<PaymentTypeData> paymentTypeOptions, final String externalId, final AccountTransferData transfer,
            final BigDecimal fixedEmiAmount, BigDecimal outstandingLoanBalance, boolean manuallyReversed, final LocalDateTime createdDate, 
            final LocalDateTime updatedDate, final AppUserData createdBy, final AppUserData updatedBy, final List<GroupLoanIndividualMonitoringTransactionData> glimTransactions) {
        this(id, officeId, officeName, transactionType, paymentDetailData, currency, date, amount, principalPortion, interestPortion,
                feeChargesPortion, penaltyChargesPortion, overpaymentPortion, unrecognizedIncomePortion, paymentTypeOptions, externalId,
                transfer, fixedEmiAmount, outstandingLoanBalance, null, manuallyReversed, createdDate, updatedDate, createdBy, updatedBy, glimTransactions);
    }

    public static LoanTransactionData populateLoanTransactionData(final Long id, final BigDecimal amount) {
        final Long officeId = null;
        final String officeName = null;
        final LocalDate date = null;
        final CurrencyData currency = null;
        final PaymentDetailData paymentDetailData = null;
        final BigDecimal principalPortion = null;
        final BigDecimal interestPortion = null;
        final BigDecimal feeChargesPortion = null;
        final BigDecimal penaltyChargesPortion = null;
        final BigDecimal overpaymentPortion = null;
        final BigDecimal unrecognizedIncomePortion = null;
        final String externalId = null;
        final AccountTransferData transfer = null;
        final BigDecimal fixedEmiAmount = null;
        final BigDecimal outstandingLoanBalance = null;
        final LocalDate submittedOnDate = null;
        final boolean manuallyReversed = false;
        final Collection<PaymentTypeData> paymentTypeOptions = null;
        final LoanTransactionEnumData transactionType = null;
        return new LoanTransactionData(id, officeId, officeName, transactionType, paymentDetailData, currency, date, amount, principalPortion, interestPortion,
                feeChargesPortion, penaltyChargesPortion, overpaymentPortion, unrecognizedIncomePortion, paymentTypeOptions, externalId, transfer,
                fixedEmiAmount, outstandingLoanBalance, submittedOnDate,manuallyReversed);
    }

    public LoanTransactionData(final Long id, final Long officeId, final String officeName, final LoanTransactionEnumData transactionType,
            final PaymentDetailData paymentDetailData, final CurrencyData currency, final LocalDate date, final BigDecimal amount,
            final BigDecimal principalPortion, final BigDecimal interestPortion, final BigDecimal feeChargesPortion,
            final BigDecimal penaltyChargesPortion, final BigDecimal overpaymentPortion, final BigDecimal unrecognizedIncomePortion,
            final Collection<PaymentTypeData> paymentTypeOptions, final String externalId, final AccountTransferData transfer,
            final BigDecimal fixedEmiAmount, BigDecimal outstandingLoanBalance, final LocalDate submittedOnDate,final boolean manuallyReversed) {
        this.id = id;
        this.officeId = officeId;
        this.officeName = officeName;
        this.type = transactionType;
        this.paymentDetailData = paymentDetailData;
        this.currency = currency;
        this.date = date;
        this.amount = amount;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.feeChargesPortion = feeChargesPortion;
        this.penaltyChargesPortion = penaltyChargesPortion;
        this.unrecognizedIncomePortion = unrecognizedIncomePortion;
        this.paymentTypeOptions = paymentTypeOptions;
        this.externalId = externalId;
        this.transfer = transfer;
        this.overpaymentPortion = overpaymentPortion;
        this.fixedEmiAmount = fixedEmiAmount;
        this.outstandingLoanBalance = outstandingLoanBalance;
        this.submittedOnDate = submittedOnDate;
        this.manuallyReversed = manuallyReversed;
        this.possibleNextRepaymentDate = null;
        this.transactionAuthenticationOptions = null;
        this.loanAccountData = null;
        this.fingerPrintData = null;
        this.createdDate = null;
        this.updatedDate = null;
        this.createdBy = null;
        this.updatedBy = null;
        this.glimTransactions = null;
        this.loanOverdueData = null;
    }
    
    public LoanTransactionData(final Long id, final Long officeId, final String officeName, final LoanTransactionEnumData transactionType,
            final PaymentDetailData paymentDetailData, final CurrencyData currency, final LocalDate date, final BigDecimal amount,
            final BigDecimal principalPortion, final BigDecimal interestPortion, final BigDecimal feeChargesPortion,
            final BigDecimal penaltyChargesPortion, final BigDecimal overpaymentPortion, final BigDecimal unrecognizedIncomePortion,
            final Collection<PaymentTypeData> paymentTypeOptions, final String externalId, final AccountTransferData transfer,
            final BigDecimal fixedEmiAmount, BigDecimal outstandingLoanBalance, final LocalDate submittedOnDate, final boolean manuallyReversed, 
            final LocalDateTime createdDate, final LocalDateTime updatedDate, final AppUserData createdBy, final AppUserData updatedBy,
            final List<GroupLoanIndividualMonitoringTransactionData> glimTransactions) {
        this.id = id;
        this.officeId = officeId;
        this.officeName = officeName;
        this.type = transactionType;
        this.paymentDetailData = paymentDetailData;
        this.currency = currency;
        this.date = date;
        this.amount = amount;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.feeChargesPortion = feeChargesPortion;
        this.penaltyChargesPortion = penaltyChargesPortion;
        this.unrecognizedIncomePortion = unrecognizedIncomePortion;
        this.paymentTypeOptions = paymentTypeOptions;
        this.externalId = externalId;
        this.transfer = transfer;
        this.overpaymentPortion = overpaymentPortion;
        this.fixedEmiAmount = fixedEmiAmount;
        this.outstandingLoanBalance = outstandingLoanBalance;
        this.submittedOnDate = submittedOnDate;
        this.manuallyReversed = manuallyReversed;
        this.possibleNextRepaymentDate = null;
        this.transactionAuthenticationOptions = null;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.loanAccountData = null;
        this.fingerPrintData = null;
        this.glimTransactions = glimTransactions;
        this.loanOverdueData = null;
    }

    public LoanTransactionData(Long id, LoanTransactionEnumData transactionType, LocalDate date, BigDecimal totalAmount,
            BigDecimal principalPortion, BigDecimal interestPortion, BigDecimal feeChargesPortion, BigDecimal penaltyChargesPortion,
            BigDecimal overPaymentPortion, BigDecimal unrecognizedIncomePortion, BigDecimal outstandingLoanBalance,final boolean manuallyReversed) {
        this(id, null, null, transactionType, null, null, date, totalAmount, principalPortion, interestPortion, feeChargesPortion,
                penaltyChargesPortion, overPaymentPortion, unrecognizedIncomePortion, null, null, null, null, outstandingLoanBalance, null,
                manuallyReversed);
    }
    
    public static LoanTransactionData LoanTransactionDataForDisbursalTemplate(final LoanTransactionEnumData transactionType, final LocalDate expectedDisbursedOnLocalDateForTemplate, 
			final BigDecimal disburseAmountForTemplate,	final Collection<PaymentTypeData> paymentOptions,
			final BigDecimal retriveLastEmiAmount, final LocalDate possibleNextRepaymentDate,
			final Collection<TransactionAuthenticationData> transactionAuthenticationOptions,
			final Collection<FingerPrintData> fingerPrintData) {
		    final Long id = null;
		    final Long officeId = null;
		    final String officeName = null;
		    final PaymentDetailData paymentDetailData = null;
		    final CurrencyData currency = null;
		    final BigDecimal unrecognizedIncomePortion = null;
		    final BigDecimal principalPortion = null;;
		    final BigDecimal interestPortion = null;
		    final BigDecimal feeChargesPortion = null;
		    final BigDecimal penaltyChargesPortion = null;
		    final BigDecimal overpaymentPortion = null;
		    final String externalId = null;
		    final BigDecimal outstandingLoanBalance = null;
		    final AccountTransferData transfer = null;
		    final LocalDate submittedOnDate = null;
		    final boolean manuallyReversed = false;
			return new LoanTransactionData(id, officeId, officeName, transactionType, paymentDetailData, currency, expectedDisbursedOnLocalDateForTemplate,
					disburseAmountForTemplate, principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion, overpaymentPortion,	unrecognizedIncomePortion, 
					paymentOptions, transfer, externalId, retriveLastEmiAmount, outstandingLoanBalance, submittedOnDate, manuallyReversed, possibleNextRepaymentDate, transactionAuthenticationOptions,fingerPrintData);
		
	}

    private LoanTransactionData(Long id , final Long officeId, final String officeName, LoanTransactionEnumData transactionType, final PaymentDetailData paymentDetailData,
    		final CurrencyData currency, final LocalDate date,	BigDecimal amount, final BigDecimal principalPortion, final BigDecimal interestPortion, 
    		final BigDecimal feeChargesPortion, final BigDecimal penaltyChargesPortion, final BigDecimal overpaymentPortion, BigDecimal unrecognizedIncomePortion,	Collection<PaymentTypeData> paymentOptions,
    		final AccountTransferData transfer, final String externalId, final BigDecimal fixedEmiAmount, BigDecimal outstandingLoanBalance, 
    		final LocalDate submittedOnDate, final boolean manuallyReversed,
			final LocalDate possibleNextRepaymentDate,
			final Collection<TransactionAuthenticationData> transactionAuthenticationOptions,
			final Collection<FingerPrintData> fingerPrintData) {
    	 this.id = id;
         this.officeId = officeId;
         this.officeName = officeName;
         this.type = transactionType;
         this.paymentDetailData = paymentDetailData;
         this.currency = currency;
         this.date = date;
         this.amount = amount;
         this.principalPortion = principalPortion;
         this.interestPortion = interestPortion;
         this.feeChargesPortion = feeChargesPortion;
         this.penaltyChargesPortion = penaltyChargesPortion;
         this.unrecognizedIncomePortion = unrecognizedIncomePortion;
         this.paymentTypeOptions = paymentOptions;
         this.externalId = externalId;
         this.transfer = transfer;
         this.overpaymentPortion = overpaymentPortion;
         this.fixedEmiAmount = fixedEmiAmount;
         this.outstandingLoanBalance = outstandingLoanBalance;
         this.submittedOnDate = submittedOnDate;
         this.manuallyReversed = manuallyReversed;
         this.possibleNextRepaymentDate = possibleNextRepaymentDate;
         this.transactionAuthenticationOptions = transactionAuthenticationOptions;
         this.loanAccountData = null;
         this.fingerPrintData = fingerPrintData;
         this.createdDate = null;
         this.updatedDate = null;
         this.createdBy = null;
         this.updatedBy = null;
         this.glimTransactions = null;
         this.loanOverdueData = null;
	}

    private LoanTransactionData(Long id, final Long officeId, final String officeName, LoanTransactionEnumData transactionType,
            final LocalDate date, final BigDecimal amount,final String groupExternalId, final String loanAccountNumber) {
        this.id = id;
        this.officeId = officeId;
        this.officeName = officeName;
        this.type = transactionType;
        this.groupExternalId = groupExternalId;
        this.loanAccountNumber = loanAccountNumber;
        this.date = date;
        this.amount = amount;
        this.paymentDetailData = null;
        this.paymentTypeOptions = null;
        this.externalId = null;
        this.submittedOnDate = null;
        this.unrecognizedIncomePortion = null;
        this.transfer = null;
        this.principalPortion = null;
        this.possibleNextRepaymentDate = null;
        this.penaltyChargesPortion = null;
        this.overpaymentPortion = null;
        this.outstandingLoanBalance = null;
        this.manuallyReversed = false;
        this.interestPortion = null;
        this.fixedEmiAmount = null;
        this.feeChargesPortion = null;
        this.currency = null;
        this.transactionAuthenticationOptions = null;
        this.groupExternalId = groupExternalId;
        this.loanAccountNumber = loanAccountNumber;
        this.loanAccountData = null;
        this.fingerPrintData = null;
        this.createdDate = null;
        this.updatedDate = null;
        this.createdBy = null;
        this.updatedBy = null;
        this.glimTransactions = null;
        this.loanOverdueData = null;
    }

    public static LoanTransactionData LoanTransactionDataForReconciliationLoanTransactionData(Long id, final Long officeId, final String officeName, LoanTransactionEnumData transactionType,
            final LocalDate date, final BigDecimal amount,final String groupExternalId, final String loanAccountNumber) {
        return new LoanTransactionData(id, officeId, officeName, transactionType, date, amount,groupExternalId, loanAccountNumber);

    }
    
    public LoanTransactionData(final LoanTransactionData loanTransactionData, final LoanAccountData loanAccountData){
    	this.id = loanTransactionData.id;
        this.officeId = loanTransactionData.officeId;
        this.officeName = loanTransactionData.officeName;
        this.type = loanTransactionData.type;
        this.paymentDetailData = loanTransactionData.paymentDetailData;
        this.currency = loanTransactionData.currency;
        this.date = loanTransactionData.date;
        this.amount = loanTransactionData.amount;
        this.principalPortion = loanTransactionData.principalPortion;
        this.interestPortion = loanTransactionData.interestPortion;
        this.feeChargesPortion = loanTransactionData.feeChargesPortion;
        this.penaltyChargesPortion = loanTransactionData.penaltyChargesPortion;
        this.unrecognizedIncomePortion = loanTransactionData.unrecognizedIncomePortion;
        this.paymentTypeOptions = loanTransactionData.paymentTypeOptions;
        this.externalId = loanTransactionData.externalId;
        this.transfer = loanTransactionData.transfer;
        this.overpaymentPortion = loanTransactionData.overpaymentPortion;
        this.fixedEmiAmount = loanTransactionData.fixedEmiAmount;
        this.outstandingLoanBalance = loanTransactionData.outstandingLoanBalance;
        this.submittedOnDate = loanTransactionData.submittedOnDate;
        this.manuallyReversed = loanTransactionData.manuallyReversed;
        this.possibleNextRepaymentDate = loanTransactionData.possibleNextRepaymentDate;
        this.transactionAuthenticationOptions = loanTransactionData.transactionAuthenticationOptions;
        this.loanAccountData = loanAccountData;
        this.fingerPrintData = loanTransactionData.fingerPrintData;
        this.createdDate = loanTransactionData.createdDate;
        this.updatedDate = loanTransactionData.updatedDate;
        this.createdBy = loanTransactionData.createdBy;
        this.updatedBy = loanTransactionData.updatedBy;
        this.glimTransactions = loanTransactionData.glimTransactions;
        this.loanOverdueData = null;
    }
    
    public static LoanTransactionData LoanTransactionRepaymentTemplate(final LoanTransactionData loanTransactionData, final LoanAccountData loanAccountData){
    	return new LoanTransactionData(loanTransactionData, loanAccountData);
    }

	public static LoanTransactionData LoanTransactionRefundData(final LoanTransactionEnumData transactionType, final LocalDate date,
			final BigDecimal amount, final Collection<PaymentTypeData> paymentOptions) {
		final Long id = null;
		final Long officeId = null;
		final String officeName = null;
		final PaymentDetailData paymentDetailData = null;
		final CurrencyData currency = null;
		final BigDecimal unrecognizedIncomePortion = null;
		final BigDecimal principalPortion = null;
		final BigDecimal interestPortion = null;
		final BigDecimal feeChargesPortion = null;
		final BigDecimal penaltyChargesPortion = null;
		final BigDecimal overpaymentPortion = null;
		final String externalId = null;
		final BigDecimal outstandingLoanBalance = null;
		final AccountTransferData transfer = null;
		final LocalDate submittedOnDate = null;
		final boolean manuallyReversed = false;
		final BigDecimal retriveLastEmiAmount = null;
		final LocalDate possibleNextRepaymentDate = null;
		final Collection<TransactionAuthenticationData> transactionAuthenticationOptions = null;
		final Collection<FingerPrintData> fingerPrintData = null;
		return new LoanTransactionData(id, officeId, officeName, transactionType, paymentDetailData, currency, date,
				amount, principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion, overpaymentPortion,
				unrecognizedIncomePortion, paymentOptions, transfer, externalId, retriveLastEmiAmount,
				outstandingLoanBalance, submittedOnDate, manuallyReversed, possibleNextRepaymentDate, transactionAuthenticationOptions, fingerPrintData);

	}

    public LoanTransactionData(final Long loanId, final BigDecimal emi, final LocalDate nextEMIDate, final BigDecimal totalOutstanding,
            final BigDecimal totalOverdue, final BigDecimal OverdueWithNextEMI) {
        this.id = loanId;
        this.officeId = null;
        this.officeName = null;
        this.type = null;
        this.paymentDetailData = null;
        this.currency = null;
        this.date = nextEMIDate;
        this.amount = null;
        this.principalPortion = null;
        this.interestPortion = null;
        this.feeChargesPortion = null;
        this.penaltyChargesPortion = null;
        this.unrecognizedIncomePortion = null;
        this.paymentTypeOptions = null;
        this.externalId = null;
        this.transfer = null;
        this.overpaymentPortion = null;
        this.fixedEmiAmount = emi;
        this.outstandingLoanBalance = totalOutstanding;
        this.submittedOnDate = null;
        this.manuallyReversed = false;
        this.possibleNextRepaymentDate = null;
        this.transactionAuthenticationOptions = null;
        this.loanAccountData = null;
        this.fingerPrintData = null;
        this.createdDate = null;
        this.updatedDate = null;
        this.createdBy = null;
        this.updatedBy = null;
        this.glimTransactions = null;
        this.loanOverdueData = new LoanOverdueData(totalOverdue, OverdueWithNextEMI);
    }
	public LocalDate dateOf() {
        return this.date;
    }

    public boolean isNotDisbursement() {
        return Integer.valueOf(1).equals(this.type.id());
    }

    
    public BigDecimal getAmount() {
        return this.amount;
    }

    
    public BigDecimal getUnrecognizedIncomePortion() {
        return this.unrecognizedIncomePortion;
    }

    
    public BigDecimal getInterestPortion() {
        return this.interestPortion;
    }    
    
    public LoanTransactionEnumData getType() {
		return this.type;
	}

	public LocalDate getDate() {
		return this.date;
	}

	public CurrencyData getCurrency() {
		return this.currency;
	}

	public BigDecimal getPrincipalPortion() {
		return this.principalPortion;
	}

	public BigDecimal getFeeChargesPortion() {
		return this.feeChargesPortion;
	}

	public void setWriteOffReasonOptions(Collection<CodeValueData> writeOffReasonOptions){
    	this.writeOffReasonOptions =writeOffReasonOptions;
    }

    public Long getId() {
        return this.id;
    }
    
    public void updateGlimTransactions(final List<GroupLoanIndividualMonitoringTransactionData> glimTransactions) {
        if (this.glimTransactions == null) {
            this.glimTransactions = new ArrayList<>();
        }
        this.glimTransactions.addAll(glimTransactions);
    }

    
    public void setNetDisbursalAmount(BigDecimal netDisbursalAmount) {
        this.netDisbursalAmount = netDisbursalAmount;
    }

    
    public void setDiscountOnDisbursalAmount(BigDecimal discountOnDisbursalAmount) {
        this.discountOnDisbursalAmount = discountOnDisbursalAmount;
    }

    
    public void setExpectedFirstRepaymentOnDate(LocalDate expectedFirstRepaymentOnDate) {
        this.expectedFirstRepaymentOnDate = expectedFirstRepaymentOnDate;
    }
	
}