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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableEagerFetchCreatedBy;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.account.data.AccountTransferData;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * All monetary transactions against a loan are modelled through this entity.
 * Disbursements, Repayments, Waivers, Write-off etc
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "m_loan_transaction", uniqueConstraints = { @UniqueConstraint(columnNames = { "external_id" }, name = "external_id_UNIQUE") })
public final class LoanTransaction extends AbstractAuditableEagerFetchCreatedBy<AppUser, Long> {

    private final static Logger logger = LoggerFactory.getLogger(LoanTransaction.class);

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    @ManyToOne(optional = true)
    @JoinColumn(name = "payment_detail_id", nullable = true)
    private PaymentDetail paymentDetail;

    @Column(name = "transaction_type_enum", nullable = false)
    private final Integer typeOf;

    @Column(name = "transaction_sub_type_enum", nullable = true)
    private final Integer subTypeOf;

    @Temporal(TemporalType.DATE)
    @Column(name = "transaction_date", nullable = false)
    private final Date dateOf;

    @Temporal(TemporalType.DATE)
    @Column(name = "submitted_on_date", nullable = false)
    private final Date submittedOnDate;

    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;

    @Column(name = "principal_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal principalPortion;

    @Column(name = "interest_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal interestPortion;

    @Column(name = "fee_charges_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal feeChargesPortion;

    @Column(name = "penalty_charges_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal penaltyChargesPortion;

    @Column(name = "overpayment_portion_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal overPaymentPortion;

    @Column(name = "unrecognized_income_portion", scale = 6, precision = 19, nullable = true)
    private BigDecimal unrecognizedIncomePortion;

    @Column(name = "is_reversed", nullable = false)
    private boolean reversed;

    @Column(name = "external_id", length = 100, nullable = true, unique = true)
    private String externalId;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loanTransaction", orphanRemoval = true)
    private Set<LoanChargePaidBy> loanChargesPaid = new HashSet<>();

    @Column(name = "outstanding_loan_balance_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal outstandingLoanBalance;

    @Column(name = "manually_adjusted_or_reversed", nullable = false)
    private boolean manuallyAdjustedOrReversed;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "loan_transaction_id", referencedColumnName = "id", nullable = false)
    private final Set<LoanTransactionToRepaymentScheduleMapping> loanTransactionToRepaymentScheduleMappings = new HashSet<>();

    @Column(name = "is_reconciled")
    private boolean isReconciled;

    @Column(name = "orig_transaction_id", nullable = true)
    private Long originalTransactionId;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "loanTransaction", orphanRemoval = true)
    private final Set<GroupLoanIndividualMonitoringTransaction> groupLoanIndividualMonitoringTransactions = new HashSet<>();

    @Column(name = "transaction_association_id")
    private Long associatedTransactionId;

    @Transient
    private final Set<LoanChargePaidBy> loanChargesPaidTemp = new HashSet<>();

    protected LoanTransaction() {
        this.loan = null;
        this.dateOf = null;
        this.typeOf = null;
        this.subTypeOf = null;
        this.submittedOnDate = DateUtils.getDateOfTenant();
    }

    public static LoanTransaction incomePosting(final Loan loan, final Office office, final Date dateOf, final BigDecimal amount,
            final BigDecimal interestPortion, final BigDecimal feeChargesPortion, final BigDecimal penaltyChargesPortion) {
        final Integer typeOf = LoanTransactionType.INCOME_POSTING.getValue();
        final BigDecimal principalPortion = BigDecimal.ZERO;
        final BigDecimal overPaymentPortion = BigDecimal.ZERO;
        final boolean reversed = false;
        final PaymentDetail paymentDetail = null;
        final String externalId = null;
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(loan, office, typeOf, loanTransactionSubType, dateOf, amount, principalPortion, interestPortion,
                feeChargesPortion, penaltyChargesPortion, overPaymentPortion, reversed, paymentDetail, externalId);

    }

    public static LoanTransaction disbursement(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate disbursementDate, final String externalId) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(null, office, LoanTransactionType.DISBURSEMENT, loanTransactionSubType, paymentDetail,
                amount.getAmount(), disbursementDate, externalId);
    }

    public static LoanTransaction brokenPeriodInterestPosting(final Office office, final Money amount, final LocalDate disbursementDate) {
        final Integer loanTransactionSubType = null;
        final PaymentDetail paymentDetail = null;
        final String externalId = null;
        return new LoanTransaction(null, office, LoanTransactionType.BROKEN_PERIOD_INTEREST_POSTING, loanTransactionSubType, paymentDetail,
                amount.getAmount(), disbursementDate, externalId);
    }

    public static LoanTransaction repayment(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(null, office, LoanTransactionType.REPAYMENT, loanTransactionSubType, paymentDetail, amount.getAmount(),
                paymentDate, externalId);
    }

    public static LoanTransaction addOrRevokeLoanSubsidy(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId, final LoanTransactionType loanTransactionType) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(null, office, loanTransactionType, loanTransactionSubType, paymentDetail, amount.getAmount(),
                paymentDate, externalId);
    }

    public static LoanTransaction realizationLoanSubsidy(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId) {
        return new LoanTransaction(null, office, LoanTransactionType.REPAYMENT, LoanTransactionSubType.REALIZATION_SUBSIDY.getValue(),
                paymentDetail, amount.getAmount(), paymentDate, externalId);
    }

    public static LoanTransaction prepayment(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId) {
        return new LoanTransaction(null, office, LoanTransactionType.REPAYMENT, LoanTransactionSubType.PRE_PAYMENT.getValue(),
                paymentDetail, amount.getAmount(), paymentDate, externalId);
    }

    public static LoanTransaction repaymentForNPALoan(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId) {
        return new LoanTransaction(null, office, LoanTransactionType.REPAYMENT, LoanTransactionSubType.TRANSACTION_IN_NPA_STATE.getValue(),
                paymentDetail, amount.getAmount(), paymentDate, externalId);
    }

    public static LoanTransaction recoveryRepayment(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(null, office, LoanTransactionType.RECOVERY_REPAYMENT, loanTransactionSubType, paymentDetail,
                amount.getAmount(), paymentDate, externalId);
    }

    public static LoanTransaction loanPayment(final Loan loan, final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId, final LoanTransactionType transactionType,
            final LoanTransactionSubType subType) {
        Integer loanTransactionSubType = null;
        if (subType != null) {
            loanTransactionSubType = subType.getValue();
        }
        return new LoanTransaction(loan, office, transactionType, loanTransactionSubType, paymentDetail, amount.getAmount(), paymentDate,
                externalId);
    }

    public static LoanTransaction repaymentAtDisbursement(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(null, office, LoanTransactionType.REPAYMENT_AT_DISBURSEMENT, loanTransactionSubType, paymentDetail,
                amount.getAmount(), paymentDate, externalId);
    }

    public static LoanTransaction waiver(final Office office, final Loan loan, final Money amount, final LocalDate waiveDate,
            final Money waived, final Money unrecognizedPortion) {
        Integer loanTransactionSubType = null;
        if (loan.isNpa()) {
            loanTransactionSubType = LoanTransactionSubType.TRANSACTION_IN_NPA_STATE.getValue();
        }
        final LoanTransaction loanTransaction = new LoanTransaction(loan, office, LoanTransactionType.WAIVE_INTEREST,
                loanTransactionSubType, amount.getAmount(), waiveDate, null);
        loanTransaction.updateInterestComponent(waived, unrecognizedPortion);
        return loanTransaction;
    }

    public static LoanTransaction accrueInterest(final Office office, final Loan loan, final Money amount,
            final LocalDate interestAppliedDate) {
        final BigDecimal principalPortion = null;
        final BigDecimal feesPortion = null;
        final BigDecimal penaltiesPortion = null;
        final BigDecimal interestPortion = amount.getAmount();
        final BigDecimal overPaymentPortion = null;
        final boolean reversed = false;
        final PaymentDetail paymentDetail = null;
        final String externalId = null;
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(loan, office, LoanTransactionType.ACCRUAL.getValue(), loanTransactionSubType,
                interestAppliedDate.toDate(), interestPortion, principalPortion, interestPortion, feesPortion, penaltiesPortion,
                overPaymentPortion, reversed, paymentDetail, externalId);
    }

    public static LoanTransaction accrual(final Loan loan, final Office office, final Money amount, final Money interest,
            final Money feeCharges, final Money penaltyCharges, final LocalDate transactionDate) {
        return accrueTransaction(loan, office, transactionDate, amount.getAmount(), interest.getAmount(), feeCharges.getAmount(),
                penaltyCharges.getAmount());
    }

    public static LoanTransaction accrueTransaction(final Loan loan, final Office office, final LocalDate dateOf, final BigDecimal amount,
            final BigDecimal interestPortion, final BigDecimal feeChargesPortion, final BigDecimal penaltyChargesPortion) {
        final BigDecimal principalPortion = null;
        final BigDecimal overPaymentPortion = null;
        final boolean reversed = false;
        final PaymentDetail paymentDetail = null;
        final String externalId = null;
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(loan, office, LoanTransactionType.ACCRUAL.getValue(), loanTransactionSubType, dateOf.toDate(), amount,
                principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion, overPaymentPortion, reversed, paymentDetail,
                externalId);
    }

    public static LoanTransaction accrualSuspense(final Loan loan, final Office office, final Money amount, final Money interest,
            final Money feeCharges, final Money penaltyCharges) {
        final LoanTransactionType transactionType = LoanTransactionType.ACCRUAL_SUSPENSE;
        final LocalDate transactionDate = DateUtils.getLocalDateOfTenant();
        return accrualTransaction(loan, office, amount, interest, feeCharges, penaltyCharges, transactionType, transactionDate);
    }

    public static LoanTransaction accrualSuspense(final Loan loan, final Office office, final Money amount, final Money interest,
            final Money feeCharges, final Money penaltyCharges, final LocalDate transactionDate) {
        final LoanTransactionType transactionType = LoanTransactionType.ACCRUAL_SUSPENSE;
        return accrualTransaction(loan, office, amount, interest, feeCharges, penaltyCharges, transactionType, transactionDate);
    }

    public static LoanTransaction accrualSuspenseReverse(final Loan loan, final Office office, final Money amount, final Money interest,
            final Money feeCharges, final Money penaltyCharges, final LocalDate transactionDate) {
        final LoanTransactionType transactionType = LoanTransactionType.ACCRUAL_SUSPENSE_REVERSE;
        return accrualTransaction(loan, office, amount, interest, feeCharges, penaltyCharges, transactionType, transactionDate);
    }

    public static LoanTransaction accrualWriteOff(final Loan loan, final Office office, final Money amount, final Money interest,
            final Money feeCharges, final Money penaltyCharges) {
        final LocalDate transactionDate = DateUtils.getLocalDateOfTenant();
        return accrualWriteOff(loan, office, amount, interest, feeCharges, penaltyCharges, transactionDate);
    }

    public static LoanTransaction accrualWriteOff(final Loan loan, final Office office, final Money amount, final Money interest,
            final Money feeCharges, final Money penaltyCharges, final LocalDate transactionDate) {
        final LoanTransactionType transactionType = LoanTransactionType.ACCRUAL_WRITEOFF;
        return accrualTransaction(loan, office, amount, interest, feeCharges, penaltyCharges, transactionType, transactionDate);
    }

    private static LoanTransaction accrualTransaction(final Loan loan, final Office office, final Money amount, final Money interest,
            final Money feeCharges, final Money penaltyCharges, final LoanTransactionType transactionType,
            final LocalDate transactionDate) {
        final BigDecimal principalPortion = null;
        final BigDecimal overPaymentPortion = null;
        final BigDecimal interestPortion = defaultToNullIfZero(interest.getAmount());
        final BigDecimal feeChargesPortion = defaultToNullIfZero(feeCharges.getAmount());
        final BigDecimal penaltyChargesPortion = defaultToNullIfZero(penaltyCharges.getAmount());
        final boolean reversed = false;
        final PaymentDetail paymentDetail = null;
        final String externalId = null;
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(loan, office, transactionType.getValue(), loanTransactionSubType, transactionDate.toDate(),
                amount.getAmount(), principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion, overPaymentPortion,
                reversed, paymentDetail, externalId);
    }

    public static LoanTransaction initiateTransfer(final Office office, final Loan loan, final LocalDate transferDate) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(loan, office, LoanTransactionType.INITIATE_TRANSFER.getValue(), loanTransactionSubType,
                transferDate.toDateTimeAtStartOfDay().toDate(), loan.getSummary().getTotalOutstanding(),
                loan.getSummary().getTotalPrincipalOutstanding(), loan.getSummary().getTotalInterestOutstanding(),
                loan.getSummary().getTotalFeeChargesOutstanding(), loan.getSummary().getTotalPenaltyChargesOutstanding(), null, false, null,
                null);
    }

    public static LoanTransaction approveTransfer(final Office office, final Loan loan, final LocalDate transferDate) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(loan, office, LoanTransactionType.APPROVE_TRANSFER.getValue(), loanTransactionSubType,
                transferDate.toDateTimeAtStartOfDay().toDate(), loan.getSummary().getTotalOutstanding(),
                loan.getSummary().getTotalPrincipalOutstanding(), loan.getSummary().getTotalInterestOutstanding(),
                loan.getSummary().getTotalFeeChargesOutstanding(), loan.getSummary().getTotalPenaltyChargesOutstanding(), null, false, null,
                null);
    }

    public static LoanTransaction withdrawTransfer(final Office office, final Loan loan, final LocalDate transferDate) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(loan, office, LoanTransactionType.WITHDRAW_TRANSFER.getValue(), loanTransactionSubType,
                transferDate.toDateTimeAtStartOfDay().toDate(), loan.getSummary().getTotalOutstanding(),
                loan.getSummary().getTotalPrincipalOutstanding(), loan.getSummary().getTotalInterestOutstanding(),
                loan.getSummary().getTotalFeeChargesOutstanding(), loan.getSummary().getTotalPenaltyChargesOutstanding(), null, false, null,
                null);
    }

    public static LoanTransaction refund(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(null, office, LoanTransactionType.REFUND, loanTransactionSubType, paymentDetail, amount.getAmount(),
                paymentDate, externalId);
    }

    public static LoanTransaction refundWithLoanTransactionSubType(final Office office, final Money amount,
            final PaymentDetail paymentDetail, final LocalDate paymentDate, final String externalId, final Integer loanTransactionSubType) {
        return new LoanTransaction(null, office, LoanTransactionType.REFUND, loanTransactionSubType, paymentDetail, amount.getAmount(),
                paymentDate, externalId);
    }

    public static LoanTransaction copyTransactionProperties(final LoanTransaction loanTransaction) {
        final Long originalTransactionId = (loanTransaction.getOriginalTransactionId() == null) ? loanTransaction.getId()
                : loanTransaction.getOriginalTransactionId();
        final LoanTransaction copyTransaction = new LoanTransaction(loanTransaction.loan, loanTransaction.officeId, loanTransaction.typeOf,
                loanTransaction.subTypeOf, loanTransaction.dateOf, loanTransaction.amount, loanTransaction.principalPortion,
                loanTransaction.interestPortion, loanTransaction.feeChargesPortion, loanTransaction.penaltyChargesPortion,
                loanTransaction.overPaymentPortion, loanTransaction.reversed, loanTransaction.paymentDetail, loanTransaction.externalId,
                loanTransaction.getCreatedDate(), loanTransaction.getCreatedBy(), loanTransaction.isReconciled, originalTransactionId,
                loanTransaction.unrecognizedIncomePortion);
        for (final LoanChargePaidBy loanChargePaidBy : loanTransaction.getLoanChargesPaid()) {
            copyTransaction.getLoanChargesPaidTemp().add(new LoanChargePaidBy(copyTransaction, loanChargePaidBy.getLoanCharge(),
                    loanChargePaidBy.getAmount(), loanChargePaidBy.getInstallmentNumber()));
        }
        return copyTransaction;
    }

    private LoanTransaction(final Loan loan, final Long officeId, final Integer typeOf, final Integer loanTransactionSubType,
            final Date dateOf, final BigDecimal amount, final BigDecimal principalPortion, final BigDecimal interestPortion,
            final BigDecimal feeChargesPortion, final BigDecimal penaltyChargesPortion, final BigDecimal overPaymentPortion,
            final boolean reversed, final PaymentDetail paymentDetail, final String externalId, final DateTime createdDate,
            final AppUser appUser, final boolean isReconciled, final Long originalTransactionId, final BigDecimal unrecognizedAmount) {
        this.loan = loan;
        this.typeOf = typeOf;
        this.subTypeOf = loanTransactionSubType;
        this.dateOf = dateOf;
        this.amount = amount;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.feeChargesPortion = feeChargesPortion;
        this.penaltyChargesPortion = penaltyChargesPortion;
        this.overPaymentPortion = overPaymentPortion;
        this.reversed = reversed;
        this.paymentDetail = paymentDetail;
        this.officeId = officeId;
        this.externalId = externalId;
        this.submittedOnDate = DateUtils.getDateOfTenant();
        this.updateCreatedDate(createdDate);
        setCreatedBy(appUser);
        this.isReconciled = isReconciled;
        this.originalTransactionId = originalTransactionId;
        this.unrecognizedIncomePortion = unrecognizedAmount;
    }

    public static LoanTransaction accrueLoanCharge(final Loan loan, final Office office, final Money amount, final LocalDate applyDate,
            final Money feeCharges, final Money penaltyCharges) {
        final String externalId = null;
        final Integer loanTransactionSubType = null;
        final LoanTransaction applyCharge = new LoanTransaction(loan, office, LoanTransactionType.ACCRUAL, loanTransactionSubType,
                amount.getAmount(), applyDate, externalId);
        applyCharge.updateChargesComponents(feeCharges, penaltyCharges);
        return applyCharge;
    }

    public static LoanTransaction refundForActiveLoan(final Office office, final Money amount, final PaymentDetail paymentDetail,
            final LocalDate paymentDate, final String externalId) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(null, office, LoanTransactionType.REFUND_FOR_ACTIVE_LOAN, loanTransactionSubType, paymentDetail,
                amount.getAmount(), paymentDate, externalId);
    }

    public static boolean transactionAmountsMatch(final MonetaryCurrency currency, final LoanTransaction loanTransaction,
            final LoanTransaction newLoanTransaction) {
        if (loanTransaction.getAmount(currency).isEqualTo(newLoanTransaction.getAmount(currency))
                && loanTransaction.getPrincipalPortion(currency).isEqualTo(newLoanTransaction.getPrincipalPortion(currency))
                && loanTransaction.getInterestPortion(currency).isEqualTo(newLoanTransaction.getInterestPortion(currency))
                && loanTransaction.getFeeChargesPortion(currency).isEqualTo(newLoanTransaction.getFeeChargesPortion(currency))
                && loanTransaction.getPenaltyChargesPortion(currency).isEqualTo(newLoanTransaction.getPenaltyChargesPortion(currency))
                && loanTransaction.getOverPaymentPortion(currency)
                        .isEqualTo(newLoanTransaction.getOverPaymentPortion(currency))) { return true; }
        return false;
    }

    private LoanTransaction(final Loan loan, final Office office, final Integer typeOf, final Integer loanTransactionSubType,
            final Date dateOf, final BigDecimal amount, final BigDecimal principalPortion, final BigDecimal interestPortion,
            final BigDecimal feeChargesPortion, final BigDecimal penaltyChargesPortion, final BigDecimal overPaymentPortion,
            final boolean reversed, final PaymentDetail paymentDetail, final String externalId) {
        this.loan = loan;
        this.typeOf = typeOf;
        this.subTypeOf = loanTransactionSubType;
        this.dateOf = dateOf;
        this.amount = amount;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.feeChargesPortion = feeChargesPortion;
        this.penaltyChargesPortion = penaltyChargesPortion;
        this.overPaymentPortion = overPaymentPortion;
        this.reversed = reversed;
        this.paymentDetail = paymentDetail;
        if (office != null) {
            this.officeId = office.getId();
        }

        this.externalId = externalId;
        this.submittedOnDate = DateUtils.getDateOfTenant();
    }

    public LoanTransaction(final Long id, final Integer typeOf, final Integer loanTransactionSubType, final Date dateOf,
            final BigDecimal amount, final BigDecimal principalPortion, final BigDecimal interestPortion,
            final BigDecimal feeChargesPortion, final BigDecimal penaltyChargesPortion, final BigDecimal unrecognizedIncome,
            final BigDecimal overPaymentPortion, final Date createdDate, final Date submittedOnDate) {
        setId(id);
        this.typeOf = typeOf;
        this.subTypeOf = loanTransactionSubType;
        this.dateOf = dateOf;
        this.amount = amount;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.feeChargesPortion = feeChargesPortion;
        this.penaltyChargesPortion = penaltyChargesPortion;
        this.overPaymentPortion = overPaymentPortion;
        this.unrecognizedIncomePortion = unrecognizedIncome;
        this.submittedOnDate = submittedOnDate;
        this.updateCreatedDate(createdDate);
    }

    public static LoanTransaction waiveLoanCharge(final Loan loan, final Office office, final Money waived, final LocalDate waiveDate,
            final Money feeChargesWaived, final Money penaltyChargesWaived, final Money unrecognizedCharge) {
        Integer loanTransactionSubType = null;
        if (loan.isNpa()) {
            loanTransactionSubType = LoanTransactionSubType.TRANSACTION_IN_NPA_STATE.getValue();
        }
        final LoanTransaction waiver = new LoanTransaction(loan, office, LoanTransactionType.WAIVE_CHARGES, loanTransactionSubType,
                waived.getAmount(), waiveDate, null);
        waiver.updateChargesComponents(feeChargesWaived, penaltyChargesWaived, unrecognizedCharge);

        return waiver;
    }

    public static LoanTransaction writeoff(final Loan loan, final Office office, final LocalDate writeOffDate, final String externalId) {
        final Integer loanTransactionSubType = null;
        return new LoanTransaction(loan, office, LoanTransactionType.WRITEOFF, loanTransactionSubType, BigDecimal.ZERO, writeOffDate,
                externalId);
    }

    public static LoanTransaction writeOffForGlimLoan(final Loan loan, final Office office, final LocalDate writeOffDate,
            final String externalId, final Integer subTypeOf) {
        final BigDecimal amount = null;
        return new LoanTransaction(loan, office, LoanTransactionType.WRITEOFF, subTypeOf, amount, writeOffDate, externalId);
    }

    public static LoanTransaction waiveGlimCharge(final Loan loan, final Office office, final LocalDate writeOffDate,
            final String externalId) {
        final BigDecimal amount = null;
        final Integer subTypeOf = null;
        return new LoanTransaction(loan, office, LoanTransactionType.WAIVE_CHARGES, subTypeOf, amount, writeOffDate, externalId);
    }

    private LoanTransaction(final Loan loan, final Office office, final LoanTransactionType type, final Integer transactionSubType,
            final BigDecimal amount, final LocalDate date, final String externalId) {
        this.loan = loan;
        this.typeOf = type.getValue();
        this.subTypeOf = transactionSubType;
        this.amount = amount;
        this.dateOf = date.toDateTimeAtStartOfDay().toDate();
        this.externalId = externalId;
        if (office != null) {
            this.officeId = office.getId();
        }
        this.submittedOnDate = DateUtils.getDateOfTenant();
    }

    private LoanTransaction(final Loan loan, final Office office, final LoanTransactionType type, final Integer transactionSubType,
            final PaymentDetail paymentDetail, final BigDecimal amount, final LocalDate date, final String externalId) {
        this.loan = loan;
        this.typeOf = type.getValue();
        this.subTypeOf = transactionSubType;
        this.paymentDetail = paymentDetail;
        this.amount = amount;
        this.dateOf = date.toDateTimeAtStartOfDay().toDate();
        this.externalId = externalId;
        if (office != null) {
            this.officeId = office.getId();
        }
        this.submittedOnDate = DateUtils.getDateOfTenant();
    }

    public void reverse() {
        if (isAnyTypeOfRepayment() && !isManuallyAdjustedOrReversed()) {
            logger.debug("Reversal for Loan[" + this.loan.getId() + "] transactionId[" + getId() + "]");
            final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            for (final StackTraceElement element : stackTraceElements) {
                logger.debug(element.toString());
            }
        }
        this.reversed = true;
        this.loanTransactionToRepaymentScheduleMappings.clear();
    }

    public void reverseAndResetTransaction() {
        this.reversed = true;
        this.loanTransactionToRepaymentScheduleMappings.clear();
        this.manuallyAdjustedOrReversed = false;
    }

    public void resetDerivedComponents() {
        this.principalPortion = null;
        this.interestPortion = null;
        this.feeChargesPortion = null;
        this.penaltyChargesPortion = null;
        this.overPaymentPortion = null;
        this.outstandingLoanBalance = null;
    }

    public void resetDerivedComponents(final boolean resetUnrecignizedPortion) {
        this.principalPortion = null;
        this.interestPortion = null;
        this.feeChargesPortion = null;
        this.penaltyChargesPortion = null;
        this.overPaymentPortion = null;
        this.outstandingLoanBalance = null;
        if (resetUnrecignizedPortion) {
            this.unrecognizedIncomePortion = null;
        }
    }

    public void updateLoan(final Loan loan) {
        this.loan = loan;
    }

    /**
     * This updates the derived fields of a loan transaction for the principal,
     * interest and interest waived portions.
     *
     * This accumulates the values passed to the already existent values for
     * each of the portions.
     */
    public void updateComponents(final Money principal, final Money interest, final Money feeCharges, final Money penaltyCharges) {
        final MonetaryCurrency currency = principal.getCurrency();
        this.principalPortion = defaultToNullIfZero(getPrincipalPortion(currency).plus(principal).getAmount());
        this.interestPortion = defaultToNullIfZero(getInterestPortion(currency).plus(interest).getAmount());
        updateChargesComponents(feeCharges, penaltyCharges);
    }

    public void updateChargesComponents(final Money feeCharges, final Money penaltyCharges) {
        final MonetaryCurrency currency = feeCharges.getCurrency();
        this.feeChargesPortion = defaultToNullIfZero(getFeeChargesPortion(currency).plus(feeCharges).getAmount());
        this.penaltyChargesPortion = defaultToNullIfZero(getPenaltyChargesPortion(currency).plus(penaltyCharges).getAmount());
    }

    private void updateChargesComponents(final Money feeCharges, final Money penaltyCharges, final Money unrecognizedCharges) {
        final MonetaryCurrency currency = feeCharges.getCurrency();
        this.feeChargesPortion = defaultToNullIfZero(getFeeChargesPortion(currency).plus(feeCharges).getAmount());
        this.penaltyChargesPortion = defaultToNullIfZero(getPenaltyChargesPortion(currency).plus(penaltyCharges).getAmount());
        this.unrecognizedIncomePortion = defaultToNullIfZero(getUnrecognizedIncomePortion(currency).plus(unrecognizedCharges).getAmount());
    }

    public void updateInterestComponent(final Money interest, final Money unrecognizedInterest) {
        final MonetaryCurrency currency = interest.getCurrency();
        this.interestPortion = defaultToNullIfZero(getInterestPortion(currency).plus(interest).getAmount());
        this.unrecognizedIncomePortion = defaultToNullIfZero(getUnrecognizedIncomePortion(currency).plus(unrecognizedInterest).getAmount());
    }

    public void adjustInterestComponent(final MonetaryCurrency currency) {
        this.interestPortion = defaultToNullIfZero(getInterestPortion(currency).minus(getUnrecognizedIncomePortion(currency)).getAmount());
    }

    public void updateComponentsAndTotal(final Money principal, final Money interest, final Money feeCharges, final Money penaltyCharges) {
        updateComponents(principal, interest, feeCharges, penaltyCharges);

        final MonetaryCurrency currency = principal.getCurrency();
        this.amount = getPrincipalPortion(currency).plus(getInterestPortion(currency)).plus(getFeeChargesPortion(currency))
                .plus(getPenaltyChargesPortion(currency)).getAmount();
    }
    
    public void updateComponentsAndTotalForAccruals(final BigDecimal interest, final BigDecimal feeCharges, final BigDecimal penaltyCharges) {
        this.interestPortion = defaultToNullIfZero(MathUtility.add(getInterestPortion(),interest));
        this.feeChargesPortion = defaultToNullIfZero(MathUtility.add(getFeeChargesPortion(),feeCharges));
        this.penaltyChargesPortion = defaultToNullIfZero(MathUtility.add(getPenaltyChargesPortion(),penaltyCharges));
        this.amount = MathUtility.add(this.interestPortion,this.feeChargesPortion,this.penaltyChargesPortion);
    }
    
    public void updateComponentsAndTotal(final Money principal, final Money interest, final Money feeCharges, final Money penaltyCharges,
            final Money unrecognizedInterest) {
        updateComponents(principal, interest, feeCharges, penaltyCharges);
        updateUnrecognizedIncomePortion(unrecognizedInterest);
        final MonetaryCurrency currency = principal.getCurrency();
        this.amount = getPrincipalPortion(currency).plus(getInterestPortion(currency)).plus(getFeeChargesPortion(currency))
                .plus(getPenaltyChargesPortion(currency)).plus(getUnrecognizedIncomePortion(currency)).getAmount();
    }

    private void updateUnrecognizedIncomePortion(final Money unrecognizedInterest) {
        this.unrecognizedIncomePortion = defaultToNullIfZero(
                getUnrecognizedIncomePortion(unrecognizedInterest.getCurrency()).plus(unrecognizedInterest).getAmount());
    }

    public void updateComponentsAndTotalForGlim(final Money principal, final Money interest, final Money feeCharges,
            final Money penaltyCharges, final BigDecimal loanPrincipalOutstandingBalance) {
        updateComponents(principal, interest, feeCharges, penaltyCharges);

        final MonetaryCurrency currency = principal.getCurrency();
        this.amount = getPrincipalPortion(currency).plus(getInterestPortion(currency)).plus(getFeeChargesPortion(currency))
                .plus(getPenaltyChargesPortion(currency)).getAmount();
        this.outstandingLoanBalance = loanPrincipalOutstandingBalance;
    }

    public void updateOverPayments(final Money overPayment) {
        final MonetaryCurrency currency = overPayment.getCurrency();
        this.overPaymentPortion = defaultToNullIfZero(getOverPaymentPortion(currency).plus(overPayment).getAmount());
    }

    public Money getPrincipalPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.principalPortion);
    }

    public BigDecimal getPrincipalPortion() {
        return this.principalPortion;
    }

    public Money getInterestPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.interestPortion);
    }

    public Money getUnrecognizedIncomePortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.unrecognizedIncomePortion);
    }

    public Money getFeeChargesPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.feeChargesPortion);
    }

    public Money getPenaltyChargesPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.penaltyChargesPortion);
    }

    public Money getOverPaymentPortion(final MonetaryCurrency currency) {
        return Money.of(currency, this.overPaymentPortion);
    }

    public Money getAmount(final MonetaryCurrency currency) {
        return Money.of(currency, this.amount);
    }

    public LocalDate getTransactionDate() {
        return new LocalDate(this.dateOf);
    }

    public Date getDateOf() {
        return this.dateOf;
    }

    public LoanTransactionType getTypeOf() {
        return LoanTransactionType.fromInt(this.typeOf);
    }

    public boolean isReversed() {
        return this.reversed;
    }

    public boolean isNotReversed() {
        return !isReversed();
    }

    public boolean isAnyTypeOfRepayment() {
        return isRepayment() || isRepaymentAtDisbursement() || isRecoveryRepayment();
    }

    public boolean isRepayment() {
        return LoanTransactionType.REPAYMENT.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isRealizationLoanSubsidyRepayment() {
        return LoanTransactionSubType.REALIZATION_SUBSIDY.getValue().equals(getSubTypeOf()) && isNotReversed();
    }

    public boolean isNotRepayment() {
        return !isRepayment();
    }

    public boolean isIncomePosting() {
        return LoanTransactionType.INCOME_POSTING.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isNotIncomePosting() {
        return !isIncomePosting();
    }

    public boolean isDisbursement() {
        return LoanTransactionType.DISBURSEMENT.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isDisbursementIncludeReversal() {
        return LoanTransactionType.DISBURSEMENT.equals(getTypeOf());
    }

    public boolean isRepaymentAtDisbursement() {
        return LoanTransactionType.REPAYMENT_AT_DISBURSEMENT.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isNotRecoveryRepayment() {
        return !isRecoveryRepayment();
    }

    public boolean isRecoveryRepayment() {
        return LoanTransactionType.RECOVERY_REPAYMENT.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isRecoveryRepaymentTransaction() {
        return LoanTransactionType.RECOVERY_REPAYMENT.equals(getTypeOf());
    }

    public boolean isAddSubsidy() {
        return LoanTransactionType.ADD_SUBSIDY.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isRevokeSubsidy() {
        return LoanTransactionType.REVOKE_SUBSIDY.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isInterestWaiver() {
        return LoanTransactionType.WAIVE_INTEREST.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isChargesWaiver() {
        return LoanTransactionType.WAIVE_CHARGES.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isNotInterestWaiver() {
        return !isInterestWaiver();
    }

    public boolean isWaiver() {
        return isInterestWaiver() || isChargesWaiver();
    }

    public boolean isNotWaiver() {
        return !isInterestWaiver() && !isChargesWaiver();
    }

    public boolean isChargePayment() {
        return getTypeOf().isChargePayment() && isNotReversed();
    }

    public boolean isPenaltyPayment() {
        boolean isPenalty = false;
        if (isChargePayment()) {
            for (final LoanChargePaidBy chargePaidBy : this.loanChargesPaid) {
                isPenalty = chargePaidBy.getLoanCharge().isPenaltyCharge();
                break;
            }
        }
        return isPenalty;
    }

    public boolean isWriteOff() {
        return getTypeOf().isWriteOff() && isNotReversed();
    }

    public boolean isIdentifiedBy(final Long identifier) {
        return getId().equals(identifier);
    }

    public boolean isBelongingToLoanOf(final Loan check) {
        return this.loan.getId().equals(check.getId());
    }

    public boolean isNotBelongingToLoanOf(final Loan check) {
        return !isBelongingToLoanOf(check);
    }

    public boolean isNonZero() {
        return this.amount.subtract(BigDecimal.ZERO).doubleValue() > 0;
    }

    public boolean isGreaterThan(final Money monetaryAmount) {
        return getAmount(monetaryAmount.getCurrency()).isGreaterThan(monetaryAmount);
    }

    public boolean isGreaterThanZero(final MonetaryCurrency currency) {
        return getAmount(currency).isGreaterThanZero();
    }

    public boolean isNotZero(final MonetaryCurrency currency) {
        return !getAmount(currency).isZero();
    }

    private static BigDecimal defaultToNullIfZero(final BigDecimal value) {
        BigDecimal result = value;
        if (BigDecimal.ZERO.compareTo(value) == 0) {
            result = null;
        }
        return result;
    }

    public LoanTransactionData toData(final CurrencyData currencyData, final AccountTransferData transfer) {
        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(this.typeOf);
        PaymentDetailData paymentDetailData = null;
        if (this.paymentDetail != null) {
            paymentDetailData = this.paymentDetail.toData();
        }
        final String officeName = null;
        return new LoanTransactionData(getId(), this.officeId, officeName, transactionType, paymentDetailData, currencyData,
                getTransactionDate(), this.amount, this.principalPortion, this.interestPortion, this.feeChargesPortion,
                this.penaltyChargesPortion, this.overPaymentPortion, this.externalId, transfer, null, this.outstandingLoanBalance,
                this.unrecognizedIncomePortion, this.manuallyAdjustedOrReversed);
    }

    public Map<String, Object> toMapData(final CurrencyData currencyData) {
        final Map<String, Object> thisTransactionData = new LinkedHashMap<>();

        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(this.typeOf);
        thisTransactionData.put("id", getId());
        thisTransactionData.put("officeId", this.officeId);
        thisTransactionData.put("type", transactionType);
        thisTransactionData.put("subType", this.subTypeOf);
        thisTransactionData.put("reversed", Boolean.valueOf(isReversed()));
        thisTransactionData.put("date", getTransactionDate());
        thisTransactionData.put("currency", currencyData);
        thisTransactionData.put("amount", this.amount);
        thisTransactionData.put("principalPortion", this.principalPortion);
        thisTransactionData.put("interestPortion", this.interestPortion);
        thisTransactionData.put("feeChargesPortion", this.feeChargesPortion);
        thisTransactionData.put("penaltyChargesPortion", this.penaltyChargesPortion);
        thisTransactionData.put("overPaymentPortion", this.overPaymentPortion);

        if (this.subTypeOf == null) {
            final LoanTransactionEnumData transactionSubType = LoanEnumerations
                    .transactionSubType(LoanTransactionSubType.INVALID.getValue());
            thisTransactionData.put("subType", transactionSubType);
        } else {
            final LoanTransactionEnumData transactionSubType = LoanEnumerations.transactionSubType(this.subTypeOf);
            thisTransactionData.put("subType", transactionSubType);
        }

        if (this.paymentDetail != null) {
            thisTransactionData.put("paymentTypeId", this.paymentDetail.getPaymentType().getId());
        }

        if (!this.loanChargesPaid.isEmpty()) {
            final List<Map<String, Object>> loanChargesPaidData = loanChargesPaidDataMap(this.loanChargesPaid);
            thisTransactionData.put("loanChargesPaid", loanChargesPaidData);
        }
        return thisTransactionData;
    }

    public List<Map<String, Object>> loanChargesPaidDataMap(final Set<LoanChargePaidBy> loanChargesPaid) {
        final List<Map<String, Object>> loanChargesPaidData = new ArrayList<>();
        for (final LoanChargePaidBy chargePaidBy : loanChargesPaid) {
            final Map<String, Object> loanChargePaidData = new LinkedHashMap<>();
            loanChargePaidData.put("chargeId", chargePaidBy.getLoanCharge().getCharge().getId());
            loanChargePaidData.put("isPenalty", chargePaidBy.getLoanCharge().isPenaltyCharge());
            loanChargePaidData.put("loanChargeId", chargePaidBy.getLoanCharge().getId());
            loanChargePaidData.put("amount", chargePaidBy.getAmount());
            loanChargePaidData.put("isCapitalized", chargePaidBy.getLoanCharge().isCapitalized());
            final List<LoanChargeTaxDetailsPaidBy> taxDetails = chargePaidBy.getLoanChargeTaxDetailsPaidBy();
            final List<Map<String, Object>> taxData = new ArrayList<>();
            for (final LoanChargeTaxDetailsPaidBy taxDetail : taxDetails) {
                final Map<String, Object> taxDetailsData = new HashMap<>();
                taxDetailsData.put("amount", taxDetail.getAmount());
                if (taxDetail.getTaxComponent().getCreditAcount() != null) {
                    taxDetailsData.put("creditAccountId", taxDetail.getTaxComponent().getCreditAcount().getId());
                }
                taxData.add(taxDetailsData);
            }
            loanChargePaidData.put("taxDetails", taxData);
            loanChargesPaidData.add(loanChargePaidData);
        }
        return loanChargesPaidData;
    }

    public Loan getLoan() {
        return this.loan;
    }

    public Set<LoanChargePaidBy> getLoanChargesPaid() {
        return this.loanChargesPaid;
    }

    public void setLoanChargesPaid(final Set<LoanChargePaidBy> loanChargesPaid) {
        this.loanChargesPaid = loanChargesPaid;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public boolean isRefund() {
        return LoanTransactionType.REFUND.equals(getTypeOf()) && isNotReversed();
    }

    public void updateExternalId(final String externalId) {
        this.externalId = externalId;
    }

    public boolean isAccrual() {
        return LoanTransactionType.ACCRUAL.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isAccrualSuspense() {
        return getTypeOf().isAccrualSuspense() && isNotReversed();
    }

    public boolean isAccrualWrittenOff() {
        return getTypeOf().isAccrualWrittenOff() && isNotReversed();
    }

    public boolean isAccrualSuspenseReverse() {
        return getTypeOf().isAccrualSuspenseReverse() && isNotReversed();
    }

    public boolean isNonMonetaryTransaction() {
        return isNotReversed() && (LoanTransactionType.CONTRA.equals(getTypeOf())
                || LoanTransactionType.MARKED_FOR_RESCHEDULING.equals(getTypeOf())
                || LoanTransactionType.APPROVE_TRANSFER.equals(getTypeOf()) || LoanTransactionType.INITIATE_TRANSFER.equals(getTypeOf())
                || LoanTransactionType.REJECT_TRANSFER.equals(getTypeOf()) || LoanTransactionType.WITHDRAW_TRANSFER.equals(getTypeOf()));
    }

    public void updateOutstandingLoanBalance(final BigDecimal outstandingLoanBalance) {
        this.outstandingLoanBalance = outstandingLoanBalance;
    }

    public boolean isNotRefundForActiveLoan() {
        // TODO Auto-generated method stub
        return !isRefundForActiveLoan();
    }

    public boolean isRefundForActiveLoan() {
        return LoanTransactionType.REFUND_FOR_ACTIVE_LOAN.equals(getTypeOf()) && isNotReversed();
    }

    public boolean isManuallyAdjustedOrReversed() {
        return this.manuallyAdjustedOrReversed;
    }

    public boolean isNotManuallyAdjustedOrReversed() {
        return !this.manuallyAdjustedOrReversed;
    }

    public void manuallyAdjustedOrReversed() {
        this.manuallyAdjustedOrReversed = true;
    }

    public boolean isLastTransaction(final LoanTransaction loanTransaction) {
        boolean isLatest = false;
        if (loanTransaction != null) {
            isLatest = getTransactionDate().isBefore(loanTransaction.getTransactionDate())
                    || (getTransactionDate().isEqual(loanTransaction.getTransactionDate())
                            && getCreatedDate().isBefore(loanTransaction.getCreatedDate()));
        }
        return isLatest;
    }

    public boolean isLatestTransaction(final LoanTransaction loanTransaction) {
        boolean isLatest = false;
        if (loanTransaction != null) {
            isLatest = getCreatedDate().isBefore(loanTransaction.getCreatedDate());
        }
        return isLatest;
    }

    public void updateLoanTransactionToRepaymentScheduleMappings(final Collection<LoanTransactionToRepaymentScheduleMapping> mappings) {
        final Collection<LoanTransactionToRepaymentScheduleMapping> retainMappings = new ArrayList<>();
        for (final LoanTransactionToRepaymentScheduleMapping updatedrepaymentScheduleMapping : mappings) {
            updateMapingDetail(retainMappings, updatedrepaymentScheduleMapping);
        }
        this.loanTransactionToRepaymentScheduleMappings.retainAll(retainMappings);
    }

    private boolean updateMapingDetail(final Collection<LoanTransactionToRepaymentScheduleMapping> retainMappings,
            final LoanTransactionToRepaymentScheduleMapping updatedrepaymentScheduleMapping) {
        boolean isMappingUpdated = false;
        for (final LoanTransactionToRepaymentScheduleMapping repaymentScheduleMapping : this.loanTransactionToRepaymentScheduleMappings) {
            if (updatedrepaymentScheduleMapping.getLoanRepaymentScheduleInstallment().getId() != null
                    && repaymentScheduleMapping.getLoanRepaymentScheduleInstallment().getDueDate()
                            .equals(updatedrepaymentScheduleMapping.getLoanRepaymentScheduleInstallment().getDueDate())) {
                repaymentScheduleMapping.setComponents(updatedrepaymentScheduleMapping.getPrincipalPortion(),
                        updatedrepaymentScheduleMapping.getInterestPortion(), updatedrepaymentScheduleMapping.getFeeChargesPortion(),
                        updatedrepaymentScheduleMapping.getPenaltyChargesPortion());
                isMappingUpdated = true;
                retainMappings.add(repaymentScheduleMapping);
                break;
            }
        }
        if (!isMappingUpdated) {
            this.loanTransactionToRepaymentScheduleMappings.add(updatedrepaymentScheduleMapping);
            retainMappings.add(updatedrepaymentScheduleMapping);
        }
        return isMappingUpdated;
    }

    public Set<LoanTransactionToRepaymentScheduleMapping> getLoanTransactionToRepaymentScheduleMappings() {
        return this.loanTransactionToRepaymentScheduleMappings;
    }

    public Boolean isAllowTypeTransactionAtTheTimeOfLastUndo() {
        return isDisbursement() || isAccrualTransaction() || isRepaymentAtDisbursement() || isBrokenPeriodInterestPosting();
    }

    public void updateCreatedDate(final Date createdDate) {
        DateTime date = null;
        if (createdDate != null) {
            date = new DateTime(createdDate);
        }
        updateCreatedDate(date);
    }

    public boolean isReconciled() {
        return this.isReconciled;
    }

    public void setReconciled(final boolean isReconciled) {
        this.isReconciled = isReconciled;
    }

    public Integer getSubTypeOf() {
        return this.subTypeOf;
    }

    public LoanTransactionSubType getTransactionSubTye() {
        LoanTransactionSubType type = LoanTransactionSubType.INVALID;
        if (this.subTypeOf != null) {
            type = LoanTransactionSubType.fromInt(this.subTypeOf);
        }
        return type;
    }

    public Date getSubmittedOnDate() {
        return this.submittedOnDate;
    }

    public boolean isAccrualTransaction() {
        return isAccrual() || isAccrualSuspense() || isAccrualWrittenOff() || isAccrualSuspenseReverse();
    }

    public boolean isPaymentTransaction() {
        return isNotReversed() && !(isDisbursement() || isAccrualTransaction() || isRepaymentAtDisbursement() || isNonMonetaryTransaction()
                || isIncomePosting() || isWaiverAtDisbursement());
    }

    private boolean isWaiverAtDisbursement() {
        boolean isWaiverAtDisbursement = false;
        if (isChargesWaiver()) {
            final Set<LoanChargePaidBy> paidBy = getLoanChargesPaid();
            for (final LoanChargePaidBy chargePaidBy : paidBy) {
                isWaiverAtDisbursement = chargePaidBy.getLoanCharge().isDueAtDisbursement();
            }
        }

        return isWaiverAtDisbursement;
    }

    public BigDecimal getInterestPortion() {
        return this.interestPortion;
    }

    public BigDecimal getFeeChargesPortion() {
        return this.feeChargesPortion;
    }

    public Set<GroupLoanIndividualMonitoringTransaction> getGlimTransaction() {
        return this.groupLoanIndividualMonitoringTransactions;
    }

    public PaymentDetail getPaymentDetail() {
        return this.paymentDetail;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public boolean isBrokenPeriodInterestPosting() {
        return getTypeOf().isBrokenPeriodInterestPosting() && isNotReversed();
    }

    public BigDecimal getOverPaymentPortion() {
        return this.overPaymentPortion;
    }

    public void setOverPaymentPortion(final BigDecimal overPaymentPortion) {
        this.overPaymentPortion = overPaymentPortion;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public Long getOriginalTransactionId() {
        return this.originalTransactionId;
    }

    public void setOriginalTransactionId(final Long originalTransactionId) {
        this.originalTransactionId = originalTransactionId;
    }

    public Set<LoanChargePaidBy> getLoanChargesPaidTemp() {
        return this.loanChargesPaidTemp;
    }

    public Set<LoanChargePaidBy> getLoanChargesPaidForProcessing() {
        Set<LoanChargePaidBy> paidBy = this.loanChargesPaid;
        if (this.loanChargesPaid.isEmpty()) {
            paidBy = this.loanChargesPaidTemp;
        }
        return paidBy;
    }

    public boolean isTypeOfAndNotReversed(final Collection<Integer> types) {
        return isNotReversed() && types.contains(this.typeOf);
    }

    public void copyChargesPaidByFrom(final LoanTransaction loanTransaction) {
        final Set<LoanChargePaidBy> chargesfrom = loanTransaction.getLoanChargesPaid();
        final Set<LoanChargePaidBy> chargesTo = getLoanChargesPaid();
        for (final LoanChargePaidBy chargePaidByfrom : chargesfrom) {
            final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(this, chargePaidByfrom.getLoanCharge(),
                    chargePaidByfrom.getAmount(), chargePaidByfrom.getInstallmentNumber());
            chargesTo.add(loanChargePaidBy);
        }
    }

    public void setAssociatedTransactionId(final Long associatedTransactionId) {
        this.associatedTransactionId = associatedTransactionId;
    }

    public Long getAssociatedTransactionId() {
        return this.associatedTransactionId;
    }

    public void setPaymentDetail(final PaymentDetail paymentDetail) {
        this.paymentDetail = paymentDetail;
    }

    public BigDecimal getPenaltyChargesPortion() {
        return MathUtility.zeroIfNull(this.penaltyChargesPortion);
    }

    public BigDecimal getOutstandingLoanBalance() {
        return this.outstandingLoanBalance;
    }

    public boolean isPaidFromAndUpToAndIncluding(final LocalDate fromNotInclusive, final LocalDate upToAndInclusive) {
        final LocalDate dueDate = getTransactionDate();
        return occursOnDayFromAndUpToAndIncluding(fromNotInclusive, upToAndInclusive, dueDate);
    }

    private boolean occursOnDayFromAndUpToAndIncluding(final LocalDate fromNotInclusive, final LocalDate upToAndInclusive,
            final LocalDate target) {
        return target != null && target.isAfter(fromNotInclusive) && !target.isAfter(upToAndInclusive);
    }

    public Long getOfficeId() {
        return this.officeId;
    }
}