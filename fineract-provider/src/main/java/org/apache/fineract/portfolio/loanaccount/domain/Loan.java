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
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.UnsupportedParameterException;
import org.apache.fineract.infrastructure.core.serialization.JsonParserHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.security.service.RandomPasswordGenerator;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.holiday.service.HolidayUtil;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.monetary.domain.MoneyHelper;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.workingdays.domain.WorkingDays;
import org.apache.fineract.organisation.workingdays.service.WorkingDaysUtil;
import org.apache.fineract.portfolio.accountdetails.domain.AccountType;
import org.apache.fineract.portfolio.calendar.data.CalendarHistoryDataWrapper;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.charge.api.ChargesApiConstants;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.domain.GroupLoanIndividualMonitoringCharge;
import org.apache.fineract.portfolio.charge.exception.LoanChargeCannotBeAddedException;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.collateral.data.CollateralData;
import org.apache.fineract.portfolio.collateral.domain.LoanCollateral;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.domain.DayOfWeekType;
import org.apache.fineract.portfolio.common.domain.NthDayType;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.floatingrates.data.FloatingRateDTO;
import org.apache.fineract.portfolio.floatingrates.data.FloatingRatePeriodData;
import org.apache.fineract.portfolio.fund.domain.Fund;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.command.LoanChargeCommand;
import org.apache.fineract.portfolio.loanaccount.data.DisbursementData;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.data.LoanTermVariationsData;
import org.apache.fineract.portfolio.loanaccount.data.ScheduleGeneratorDTO;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.exception.ClientAlreadyWriteOffException;
import org.apache.fineract.portfolio.loanaccount.exception.ExceedingTrancheCountException;
import org.apache.fineract.portfolio.loanaccount.exception.IRRCalculationException;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidLoanStateTransitionException;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidLoanTransactionTypeException;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidRefundDateException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanApplicationDateException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanDisbursalException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanForeclosureException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanOfficerAssignmentDateException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanOfficerAssignmentException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanOfficerUnassignmentDateException;
import org.apache.fineract.portfolio.loanaccount.exception.MultiDisbursementDataRequiredException;
import org.apache.fineract.portfolio.loanaccount.exception.UndoLastTrancheDisbursementException;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanScheduleDTO;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.AprCalculator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.DefaultScheduledDateGenerator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanApplicationTerms;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleGenerator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModel;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModelPeriod;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.ScheduledDateGenerator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.financial.function.IRRCalculator;
import org.apache.fineract.portfolio.loanaccount.service.GroupLoanIndividualMonitoringAssembler;
import org.apache.fineract.portfolio.loanaccount.service.GroupLoanIndividualMonitoringTransactionAssembler;
import org.apache.fineract.portfolio.loanaccount.service.LoanUtilService;
import org.apache.fineract.portfolio.loanproduct.domain.InterestMethod;
import org.apache.fineract.portfolio.loanproduct.domain.InterestRecalculationCompoundingMethod;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRelatedDetail;
import org.apache.fineract.portfolio.loanproduct.domain.LoanRescheduleStrategyMethod;
import org.apache.fineract.portfolio.loanproduct.domain.LoanTransactionProcessingStrategy;
import org.apache.fineract.portfolio.loanproduct.domain.RecalculationFrequencyType;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.useradministration.domain.AppUser;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Component;

import com.finflux.portfolio.loan.purpose.domain.LoanPurpose;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@Entity
@Component
@Table(name = "m_loan", uniqueConstraints = { @UniqueConstraint(columnNames = { "account_no" }, name = "loan_account_no_UNIQUE"),
        @UniqueConstraint(columnNames = { "external_id" }, name = "loan_externalid_UNIQUE") })
public class Loan extends AbstractPersistable<Long> {

    /** Disable optimistic locking till batch jobs failures can be fixed **/
    @Version
    int version;
    
    @Column(name ="repayment_history_version")
    private Integer repaymentHistoryVersion;

    @Column(name = "account_no", length = 20, unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "external_id")
    private String externalId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = true)
    private Group group;

    @Column(name = "loan_type_enum", nullable = false)
    private Integer loanType;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private LoanProduct loanProduct;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "fund_id", nullable = true)
    private Fund fund;

    @ManyToOne
    @JoinColumn(name = "loan_officer_id", nullable = true)
    private Staff loanOfficer;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "loan_purpose_id", nullable = true)
    private LoanPurpose loanPurpose;

    @ManyToOne
    @JoinColumn(name = "loan_transaction_strategy_id", nullable = true)
    private LoanTransactionProcessingStrategy transactionProcessingStrategy;

    @Embedded
    private LoanProductRelatedDetail loanRepaymentScheduleDetail;

    @Column(name = "term_frequency", nullable = false)
    private Integer termFrequency;

    @Column(name = "term_period_frequency_enum", nullable = false)
    private Integer termPeriodFrequencyType;

    @Column(name = "loan_status_id", nullable = false)
    private Integer loanStatus;

    @Column(name = "sync_disbursement_with_meeting", nullable = true)
    private Boolean syncDisbursementWithMeeting;

    // loan application states
    @Temporal(TemporalType.DATE)
    @Column(name = "submittedon_date")
    private Date submittedOnDate;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "submittedon_userid", nullable = true)
    private AppUser submittedBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "rejectedon_date")
    private Date rejectedOnDate;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "rejectedon_userid", nullable = true)
    private AppUser rejectedBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "withdrawnon_date")
    private Date withdrawnOnDate;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "withdrawnon_userid", nullable = true)
    private AppUser withdrawnBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "approvedon_date")
    private Date approvedOnDate;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "approvedon_userid", nullable = true)
    private AppUser approvedBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "expected_disbursedon_date")
    private Date expectedDisbursementDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "disbursedon_date")
    private Date actualDisbursementDate;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "disbursedon_userid", nullable = true)
    private AppUser disbursedBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "closedon_date")
    private Date closedOnDate;

    @ManyToOne(optional = true,fetch=FetchType.LAZY)
    @JoinColumn(name = "closedon_userid", nullable = true)
    private AppUser closedBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "writtenoffon_date")
    private Date writtenOffOnDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "rescheduledon_date")
    private Date rescheduledOnDate;

    @ManyToOne(optional = true, fetch=FetchType.LAZY)
    @JoinColumn(name = "rescheduledon_userid", nullable = true)
    private AppUser rescheduledByUser;

    @Temporal(TemporalType.DATE)
    @Column(name = "expected_maturedon_date")
    private Date expectedMaturityDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "maturedon_date")
    private Date actualMaturityDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "expected_firstrepaymenton_date")
    private Date expectedFirstRepaymentOnDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "interest_calculated_from_date")
    private Date interestChargedFromDate;

    @Column(name = "total_overpaid_derived", scale = 6, precision = 19)
    private BigDecimal totalOverpaid;

    @Column(name = "loan_counter")
    private Integer loanCounter;

    @Column(name = "loan_product_counter")
    private Integer loanProductCounter;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loan", orphanRemoval = true)
    private Set<LoanCharge> charges = new HashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loan", orphanRemoval = true)
    private Set<LoanTrancheCharge> trancheCharges = new HashSet<>();

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loan", orphanRemoval = true)
    private Set<LoanCollateral> collateral = null;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loan", orphanRemoval = true)
    private Set<LoanOfficerAssignmentHistory> loanOfficerHistory = new HashSet<>();

    // see
    // http://stackoverflow.com/questions/4334970/hibernate-cannot-simultaneously-fetch-multiple-bags
    @LazyCollection(LazyCollectionOption.FALSE)
    @OrderBy(value = "installmentNumber")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loan", orphanRemoval = true)
    private final List<LoanRepaymentScheduleInstallment> repaymentScheduleInstallments = new ArrayList<>();

    // see
    // http://stackoverflow.com/questions/4334970/hibernate-cannot-simultaneously-fetch-multiple-bags
    @OrderBy(value = "dateOf, id")
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loan", orphanRemoval = true)
    private final List<LoanTransaction> loanTransactions = new ArrayList<>();

    @Embedded
    private LoanSummary summary;

    @Transient
    private boolean accountNumberRequiresAutoGeneration = false;
    @Transient
    private LoanRepaymentScheduleTransactionProcessorFactory transactionProcessorFactory;

    @Transient
    private LoanLifecycleStateMachine loanLifecycleStateMachine;
    @Transient
    private LoanSummaryWrapper loanSummaryWrapper;

    @Column(name = "principal_amount_proposed", scale = 6, precision = 19, nullable = false)
    private BigDecimal proposedPrincipal;

    @Column(name = "approved_principal", scale = 6, precision = 19, nullable = false)
    private BigDecimal approvedPrincipal;

    @Column(name = "fixed_emi_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal fixedEmiAmount;

    @Column(name = "max_outstanding_loan_balance", scale = 6, precision = 19, nullable = false)
    private BigDecimal maxOutstandingLoanBalance;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loan", orphanRemoval = true)
    @OrderBy(value = "expectedDisbursementDate, id")
    @Where(clause = "is_active = '1'")
    private List<LoanDisbursementDetails> disbursementDetails = new ArrayList<>();

    @OrderBy(value = "termApplicableFrom, id")
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loan", orphanRemoval = true)
    private final List<LoanTermVariations> loanTermVariations = new ArrayList<>();

    @Column(name = "total_recovered_derived", scale = 6, precision = 19)
    private BigDecimal totalRecovered;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "loan", optional = true, orphanRemoval = true)
    private LoanInterestRecalculationDetails loanInterestRecalculationDetails;

    @Column(name = "is_npa", nullable = false)
    private boolean isNpa;

    @Temporal(TemporalType.DATE)
    @Column(name = "accrued_till")
    private Date accruedTill;

    @Column(name = "create_standing_instruction_at_disbursement", nullable = true)
    private Boolean createStandingInstructionAtDisbursement;

    @Column(name = "guarantee_amount_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal guaranteeAmountDerived;

    @Temporal(TemporalType.DATE)
    @Column(name = "interest_recalcualated_on")
    private Date interestRecalculatedOn;

    @Column(name = "is_floating_interest_rate", nullable = true)
    private Boolean isFloatingInterestRate;

    @Column(name = "interest_rate_differential", scale = 6, precision = 19, nullable = true)
    private BigDecimal interestRateDifferential;
    
    @ManyToOne
    @JoinColumn(name = "writeoff_reason_cv_id", nullable = true)
    private CodeValue writeOffReason;

    @Column(name = "first_emi_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal firstEmiAmount;

    @Column(name = "loan_sub_status_id", nullable = true)
    private Integer loanSubStatus;

    @Column(name = "is_topup", nullable = false)
    private boolean isTopup = false;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "loan", optional = true, orphanRemoval = true, fetch=FetchType.LAZY)
    private LoanTopupDetails loanTopupDetails;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "last_repayment_date")
    private Date lastRepaymentDate;

    @Transient
    List<GroupLoanIndividualMonitoring> glimList = new ArrayList<>();
    
    @Transient
    List<GroupLoanIndividualMonitoring> defaultGlimMembers = new ArrayList<>();

    @Column(name = "total_charges_capitalized_at_disbursement_derived", scale = 6, precision = 19, nullable = true)
    private BigDecimal totalCapitalizedCharges;
    
    @ManyToOne(optional = true,fetch=FetchType.LAZY)
    @JoinColumn(name = "expected_disbursal_payment_type_id", nullable = true)
    private PaymentType expectedDisbursalPaymentType;
    
    @ManyToOne(optional = true,fetch=FetchType.LAZY)
    @JoinColumn(name = "expected_repayment_payment_type_id", nullable = true)
    private PaymentType expectedRepaymentPaymentType;
    
    @Column(name = "flat_interest_rate", scale = 6, precision = 19, nullable = true)
    private BigDecimal flatInterestRate;

    @Column(name = "broken_period_interest", scale = 6, precision = 19, nullable = true)
    private BigDecimal brokenPeriodInterest;
    
    @Column(name = "calculated_installment_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal calculatedInstallmentAmount;
    
    @Column(name = "discount_on_disbursal_amount", scale = 6, precision = 19, nullable = true)
    private BigDecimal discountOnDisbursalAmount;
    
    @Column(name = "glim_payment_as_group", nullable = false)
    private boolean isGlimPaymentAsGroup;
    
    @Column(name = "amount_for_upfront_collection", scale = 6, precision = 19, nullable = true)
    private BigDecimal amountForUpfrontCollection;

    public static Loan newIndividualLoanApplication(final String accountNo, final Client client, final Integer loanType,
            final LoanProduct loanProduct, final Fund fund, final Staff officer, final LoanPurpose loanPurpose,
            final LoanTransactionProcessingStrategy transactionProcessingStrategy,
            final LoanProductRelatedDetail loanRepaymentScheduleDetail, final Set<LoanCharge> loanCharges,
            final Set<LoanCollateral> collateral, final BigDecimal fixedEmiAmount, final List<LoanDisbursementDetails> disbursementDetails,
            final BigDecimal maxOutstandingLoanBalance, final Boolean createStandingInstructionAtDisbursement,
            final Boolean isFloatingInterestRate, final BigDecimal interestRateDifferential, final PaymentType expectedDisbursalPaymentType,
            final PaymentType expectedRepaymentPaymentType, final BigDecimal amountForUpfrontCollection) {
        final LoanStatus status = null;
        final Group group = null;
        final Boolean syncDisbursementWithMeeting = null;
        return new Loan(accountNo, client, group, loanType, fund, officer, loanPurpose, transactionProcessingStrategy, loanProduct,
                loanRepaymentScheduleDetail, status, loanCharges, collateral, syncDisbursementWithMeeting, fixedEmiAmount,
                disbursementDetails, maxOutstandingLoanBalance, createStandingInstructionAtDisbursement, isFloatingInterestRate,
                interestRateDifferential, expectedDisbursalPaymentType, expectedRepaymentPaymentType, amountForUpfrontCollection);
    }

    public static Loan newGroupLoanApplication(final String accountNo, final Group group, final Integer loanType,
            final LoanProduct loanProduct, final Fund fund, final Staff officer, final LoanPurpose loanPurpose,
            final LoanTransactionProcessingStrategy transactionProcessingStrategy,
            final LoanProductRelatedDetail loanRepaymentScheduleDetail, final Set<LoanCharge> loanCharges,
            final Set<LoanCollateral> collateral, final Boolean syncDisbursementWithMeeting, final BigDecimal fixedEmiAmount,
            final List<LoanDisbursementDetails> disbursementDetails, final BigDecimal maxOutstandingLoanBalance,
            final Boolean createStandingInstructionAtDisbursement, final Boolean isFloatingInterestRate,
            final BigDecimal interestRateDifferential, final PaymentType expectedDisbursalPaymentType,final PaymentType expectedRepaymentPaymentType, 
            final BigDecimal amountForUpfrontCollection) {
        final LoanStatus status = null;
        final Client client = null;
        return new Loan(accountNo, client, group, loanType, fund, officer, loanPurpose, transactionProcessingStrategy, loanProduct,
                loanRepaymentScheduleDetail, status, loanCharges, collateral, syncDisbursementWithMeeting, fixedEmiAmount,
                disbursementDetails, maxOutstandingLoanBalance, createStandingInstructionAtDisbursement, isFloatingInterestRate,
                interestRateDifferential, expectedDisbursalPaymentType, expectedRepaymentPaymentType, amountForUpfrontCollection);
    }

    public static Loan newIndividualLoanApplicationFromGroup(final String accountNo, final Client client, final Group group,
            final Integer loanType, final LoanProduct loanProduct, final Fund fund, final Staff officer, final LoanPurpose loanPurpose,
            final LoanTransactionProcessingStrategy transactionProcessingStrategy,
            final LoanProductRelatedDetail loanRepaymentScheduleDetail, final Set<LoanCharge> loanCharges,
            final Set<LoanCollateral> collateral, final Boolean syncDisbursementWithMeeting, final BigDecimal fixedEmiAmount,
            final List<LoanDisbursementDetails> disbursementDetails, final BigDecimal maxOutstandingLoanBalance,
            final Boolean createStandingInstructionAtDisbursement, final Boolean isFloatingInterestRate,
            final BigDecimal interestRateDifferential, final PaymentType expectedDisbursalPaymentType, final PaymentType expectedRepaymentPaymentType, 
            final BigDecimal amountForUpfrontCollection) {
        final LoanStatus status = null;
        return new Loan(accountNo, client, group, loanType, fund, officer, loanPurpose, transactionProcessingStrategy, loanProduct,
                loanRepaymentScheduleDetail, status, loanCharges, collateral, syncDisbursementWithMeeting, fixedEmiAmount,
                disbursementDetails, maxOutstandingLoanBalance, createStandingInstructionAtDisbursement, isFloatingInterestRate,
                interestRateDifferential, expectedDisbursalPaymentType, expectedRepaymentPaymentType, amountForUpfrontCollection);
    }

    protected Loan() {
        this.client = null;
    }

    private Loan(final String accountNo, final Client client, final Group group, final Integer loanType, final Fund fund,
            final Staff loanOfficer, final LoanPurpose loanPurpose, final LoanTransactionProcessingStrategy transactionProcessingStrategy,
            final LoanProduct loanProduct, final LoanProductRelatedDetail loanRepaymentScheduleDetail, final LoanStatus loanStatus,
            final Set<LoanCharge> loanCharges, final Set<LoanCollateral> collateral, final Boolean syncDisbursementWithMeeting,
            final BigDecimal fixedEmiAmount, final List<LoanDisbursementDetails> disbursementDetails,
            final BigDecimal maxOutstandingLoanBalance, final Boolean createStandingInstructionAtDisbursement,
            final Boolean isFloatingInterestRate, final BigDecimal interestRateDifferential, final PaymentType expectedDisbursalPaymentType,
            final PaymentType expectedRepaymentPaymentType, final BigDecimal amountForUpfrontCollection) {

        this.loanRepaymentScheduleDetail = loanRepaymentScheduleDetail;
        this.loanRepaymentScheduleDetail.validateRepaymentPeriodWithGraceSettings();

        this.isFloatingInterestRate = isFloatingInterestRate;
        this.interestRateDifferential = interestRateDifferential;

        if (StringUtils.isBlank(accountNo)) {
            this.accountNumber = new RandomPasswordGenerator(19).generate();
            this.accountNumberRequiresAutoGeneration = true;
        } else {
            this.accountNumber = accountNo;
        }
        this.client = client;
        this.group = group;
        this.loanType = loanType;
        this.fund = fund;
        this.loanOfficer = loanOfficer;
        this.loanPurpose = loanPurpose;

        this.transactionProcessingStrategy = transactionProcessingStrategy;
        this.loanProduct = loanProduct;
        if (loanStatus != null) {
            this.loanStatus = loanStatus.getValue();
        } else {
            this.loanStatus = null;
        }
        if (loanCharges != null && !loanCharges.isEmpty()) {
            this.charges = associateChargesWithThisLoan(loanCharges);
            this.summary = updateSummaryWithTotalFeeChargesDueAtDisbursement(deriveSumTotalOfChargesDueAtDisbursement());
            
        } else {
            this.charges = new HashSet<>();
            this.summary = new LoanSummary();
            final Money principal = this.loanRepaymentScheduleDetail.getPrincipal();
            this.summary.setTotalNetPrincipalDisbursed(principal.getAmount());
            
        }
        if (collateral != null && !collateral.isEmpty()) {
            this.collateral = associateWithThisLoan(collateral);
        } else {
            this.collateral = null;
        }
        this.loanOfficerHistory = new HashSet<>();

        this.syncDisbursementWithMeeting = syncDisbursementWithMeeting;
        this.fixedEmiAmount = fixedEmiAmount;
        this.maxOutstandingLoanBalance = maxOutstandingLoanBalance;
        if(disbursementDetails != null && disbursementDetails.size() > 0){
            this.disbursementDetails = disbursementDetails;
        }else{
            this.disbursementDetails = new ArrayList<>();
        }
        this.approvedPrincipal = this.loanRepaymentScheduleDetail.getPrincipal().getAmount();
        this.createStandingInstructionAtDisbursement = createStandingInstructionAtDisbursement;

        /*
         * During loan origination stage and before loan is approved
         * principal_amount, approved_principal and principal_amount_demanded
         * will same amount and that amount is same as applicant loan demanded
         * amount.
         */

        this.proposedPrincipal = this.loanRepaymentScheduleDetail.getPrincipal().getAmount();
        this.expectedDisbursalPaymentType = expectedDisbursalPaymentType;
        this.expectedRepaymentPaymentType = expectedRepaymentPaymentType;
        updateNetAmountForTranches(getLoanCharges());
        this.amountForUpfrontCollection = amountForUpfrontCollection;
    }

    private LoanSummary updateSummaryWithTotalFeeChargesDueAtDisbursement(final BigDecimal feeChargesDueAtDisbursement) {
        if (this.summary == null) {
            final Money principal = this.loanRepaymentScheduleDetail.getPrincipal();
            this.summary = LoanSummary.create(feeChargesDueAtDisbursement,principal);
        } else {
        	final Money principal = this.loanRepaymentScheduleDetail.getPrincipal();
            this.summary.updateTotalFeeChargesDueAtDisbursementAndNetPrincipal(feeChargesDueAtDisbursement,principal);
        }
        return this.summary;
    }
    
    private LoanSummary updateSummaryWithTotalFeeChargesDueAtDisbursement() {
    	final BigDecimal feeChargesDueAtDisbursement = deriveSumTotalOfChargesDueAtDisbursement();
        return updateSummaryWithTotalFeeChargesDueAtDisbursement(feeChargesDueAtDisbursement);
    }
    
    private BigDecimal deriveSumTotalOfChargesDueAtDisbursement() {

        Money chargesDue = Money.of(getCurrency(), BigDecimal.ZERO);

        for (final LoanCharge charge : charges()) {
            if (this.loanStatus != null && this.loanStatus.equals(LoanStatus.ACTIVE.getValue()) && charge.isTrancheDisbursementCharge()) {
                LoanDisbursementDetails loanDisbursementDetails = charge.getTrancheDisbursementCharge().getloanDisbursementDetails();
                if (loanDisbursementDetails != null && loanDisbursementDetails.actualDisbursementDate() != null) {
                    chargesDue = chargesDue.plus(charge.amount());
                }
            } else if (charge.isDueAtDisbursement()) {
                chargesDue = chargesDue.plus(charge.amount());
            }
        }

        return chargesDue.getAmount();
    }

    private Set<LoanCharge> associateChargesWithThisLoan(final Set<LoanCharge> loanCharges) {
        for (final LoanCharge loanCharge : loanCharges) {
            loanCharge.update(this);
            if (loanCharge.getTrancheDisbursementCharge() != null) {
                addTrancheLoanCharge(loanCharge.getCharge(), loanCharge.amountOrPercentage());
            }
        }
        return loanCharges;
    }

    private Set<LoanCollateral> associateWithThisLoan(final Set<LoanCollateral> collateral) {
        for (final LoanCollateral item : collateral) {
            item.associateWith(this);
        }
        return collateral;
    }

    public boolean isAccountNumberRequiresAutoGeneration() {
        return this.accountNumberRequiresAutoGeneration;
    }

    public void setAccountNumberRequiresAutoGeneration(final boolean accountNumberRequiresAutoGeneration) {
        this.accountNumberRequiresAutoGeneration = accountNumberRequiresAutoGeneration;
    }

    public void addLoanCharge(final LoanCharge loanCharge) {

        validateLoanIsNotClosed(loanCharge);

        if (isChargesAdditionAllowed() && loanCharge.isDueAtDisbursement()) {
            // Note: added this constraint to restrict adding disbursement
            // charges to a loan
            // after it is disbursed
            // if the loan charge payment type is 'Disbursement'.
            // To undo this constraint would mean resolving how charges due are
            // disbursement are handled at present.
            // When a loan is disbursed and has charges due at disbursement, a
            // transaction is created to auto record
            // payment of the charges (user has no choice in saying they were or
            // werent paid) - so its assumed they were paid.

            final String defaultUserMessage = "This charge which is due at disbursement cannot be added as the loan is already disbursed.";
            throw new LoanChargeCannotBeAddedException("loanCharge", "due.at.disbursement.and.loan.is.disbursed", defaultUserMessage,
                    getId(), loanCharge.name());
        }

        validateChargeHasValidSpecifiedDateIfApplicable(loanCharge, getDisbursementDate(), getLastRepaymentPeriodDueDate(false));

        loanCharge.update(this);

        final BigDecimal amount = calculateAmountPercentageAppliedTo(loanCharge);
        BigDecimal chargeAmt = BigDecimal.ZERO;
        BigDecimal totalChargeAmt = BigDecimal.ZERO;
        if (loanCharge.getChargeCalculation().isPercentageBased()) {
            chargeAmt = loanCharge.getPercentage();
            if (loanCharge.isInstalmentFee()) {
                totalChargeAmt = calculatePerInstallmentChargeAmount(loanCharge);
            } else if (loanCharge.isOverdueInstallmentCharge()) {
                totalChargeAmt = loanCharge.amountOutstanding();
            }
        } else {
            chargeAmt = loanCharge.amountOrPercentage();
        }
        loanCharge.update(chargeAmt, loanCharge.getDueLocalDate(), amount, fetchNumberOfInstallmensAfterExceptions(), totalChargeAmt);
        
        // NOTE: must add new loan charge to set of loan charges before
        // reporcessing the repayment schedule.
        if (this.charges == null) {
            this.charges = new HashSet<>();
        }

        this.charges.add(loanCharge);

        this.summary = updateSummaryWithTotalFeeChargesDueAtDisbursement(deriveSumTotalOfChargesDueAtDisbursement());

        // store Id's of existing loan transactions and existing reversed loan
        // transactions
        final LoanRepaymentScheduleProcessingWrapper wrapper = new LoanRepaymentScheduleProcessingWrapper();
        wrapper.reprocess(getCurrency(), getDisbursementDate(), this.repaymentScheduleInstallments, charges(), disbursementDetails);
        updateLoanSummaryDerivedFields();

    }

    public ChangedTransactionDetail reprocessTransactions() {
        ChangedTransactionDetail changedTransactionDetail = null;
        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                .determineProcessor(this.transactionProcessingStrategy);
        final List<LoanTransaction> allNonContraTransactionsPostDisbursement = retreiveListOfTransactionsPostDisbursement();
        changedTransactionDetail = loanRepaymentScheduleTransactionProcessor.handleTransaction(getDisbursementDate(),
                allNonContraTransactionsPostDisbursement, getCurrency(), this.repaymentScheduleInstallments, charges(), disbursementDetails);
        for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
            mapEntry.getValue().updateLoan(this);
        }
        this.loanTransactions.addAll(changedTransactionDetail.getNewTransactionMappings().values());
        updateLoanSummaryDerivedFields();
        this.loanTransactions.removeAll(changedTransactionDetail.getNewTransactionMappings().values());
        return changedTransactionDetail;
    }

    /**
     * Creates a loanTransaction for "Apply Charge Event" with transaction date
     * set to "suppliedTransactionDate". The newly created transaction is also
     * added to the Loan on which this method is called.
     * 
     * If "suppliedTransactionDate" is not passed Id, the transaction date is
     * set to the loans due date if the due date is lesser than todays date. If
     * not, the transaction date is set to todays date
     * 
     * @param loanCharge
     * @param suppliedTransactionDate
     * @return
     */
    public LoanTransaction handleChargeAppliedTransaction(final LoanCharge loanCharge, final LocalDate suppliedTransactionDate) {
        final Money chargeAmount = loanCharge.getAmount(getCurrency());
        Money feeCharges = chargeAmount;
        Money penaltyCharges = Money.zero(loanCurrency());
        if (loanCharge.isPenaltyCharge()) {
            penaltyCharges = chargeAmount;
            feeCharges = Money.zero(loanCurrency());
        }

        LocalDate transactionDate = null;

        if (suppliedTransactionDate != null) {
            transactionDate = suppliedTransactionDate;
        } else {
            transactionDate = loanCharge.getDueLocalDate();
            final LocalDate currentDate = DateUtils.getLocalDateOfTenant();

            // if loan charge is to be applied on a future date, the loan
            // transaction would show todays date as applied date
            if (transactionDate == null || currentDate.isBefore(transactionDate)) {
                transactionDate = currentDate;
            }
        }
        final LoanTransaction applyLoanChargeTransaction = LoanTransaction.accrueLoanCharge(this, getOffice(), chargeAmount,
                transactionDate, feeCharges, penaltyCharges);
        Integer installmentNumber = null;
        final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(applyLoanChargeTransaction, loanCharge, loanCharge.getAmount(
                getCurrency()).getAmount(), installmentNumber);
        applyLoanChargeTransaction.getLoanChargesPaid().add(loanChargePaidBy);
        this.loanTransactions.add(applyLoanChargeTransaction);
        return applyLoanChargeTransaction;
    }

    private void handleChargePaidTransaction(final LoanCharge charge, final LoanTransaction chargesPayment,
            final LoanLifecycleStateMachine loanLifecycleStateMachine, final Integer installmentNumber) {
        chargesPayment.updateLoan(this);
        final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(chargesPayment, charge, chargesPayment.getAmount(getCurrency())
                .getAmount(), installmentNumber);
        chargesPayment.getLoanChargesPaid().add(loanChargePaidBy);
        this.loanTransactions.add(chargesPayment);
        final LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_CHARGE_PAYMENT,
                LoanStatus.fromInt(this.loanStatus));
        this.loanStatus = statusEnum.getValue();

        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                .determineProcessor(this.transactionProcessingStrategy);
        final List<LoanRepaymentScheduleInstallment> chargePaymentInstallments = new ArrayList<>();
        LocalDate startDate = getDisbursementDate();
        for (final LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
            if (installmentNumber == null && charge.isDueForCollectionFromAndUpToAndIncluding(startDate, installment.getDueDate())) {
                chargePaymentInstallments.add(installment);
                break;
            } else if (installmentNumber != null && installment.getInstallmentNumber().equals(installmentNumber)) {
                chargePaymentInstallments.add(installment);
                break;
            }
            startDate = installment.getDueDate();
        }
        final Set<LoanCharge> loanCharges = new HashSet<>(1);
        loanCharges.add(charge);
        loanRepaymentScheduleTransactionProcessor.handleTransaction(chargesPayment, getCurrency(), chargePaymentInstallments, loanCharges);
        updateLoanSummaryDerivedFields();
        doPostLoanTransactionChecks(chargesPayment.getTransactionDate(), loanLifecycleStateMachine);
    }

    private void validateLoanIsNotClosed(final LoanCharge loanCharge) {
        if (isClosed()) {
            final String defaultUserMessage = "This charge cannot be added as the loan is already closed.";
            throw new LoanChargeCannotBeAddedException("loanCharge", "loan.is.closed", defaultUserMessage, getId(), loanCharge.name());
        }
    }

    private void validateLoanChargeIsNotWaived(final LoanCharge loanCharge) {
        if (loanCharge.isWaived()) {
            final String defaultUserMessage = "This loan charge cannot be removed as the charge as already been waived.";
            throw new LoanChargeCannotBeAddedException("loanCharge", "loanCharge.is.waived", defaultUserMessage, getId(), loanCharge.name());
        }
    }

    private void validateChargeHasValidSpecifiedDateIfApplicable(final LoanCharge loanCharge, final LocalDate disbursementDate,
            final LocalDate lastRepaymentPeriodDueDate) {
        if (loanCharge.isSpecifiedDueDate()
                && !(loanCharge.isDueForCollectionFromAndUpToAndIncluding(disbursementDate, DateUtils.getLocalDateOfTenant())
                        || loanCharge.isDueForCollectionFromAndUpToAndIncluding(disbursementDate, lastRepaymentPeriodDueDate))) {
            final String defaultUserMessage = "This charge with specified due date cannot be added as the it is not in schedule range.";
            throw new LoanChargeCannotBeAddedException("loanCharge", "specified.due.date.outside.range", defaultUserMessage,
                    getDisbursementDate(), lastRepaymentPeriodDueDate, loanCharge.name());
        }
    }

    private LocalDate getLastRepaymentPeriodDueDate(final boolean includeRecalculatedInterestComponent) {
        LocalDate lastRepaymentDate = getDisbursementDate();
        for (LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
            if (((includeRecalculatedInterestComponent || installment.getPrincipal(getCurrency()).isGreaterThanZero()) || !installment
                    .isRecalculatedInterestComponent()) && installment.getDueDate().isAfter(lastRepaymentDate)) {
                lastRepaymentDate = installment.getDueDate();
            }
        }
        return lastRepaymentDate;
    }

    public void removeLoanCharge(final LoanCharge loanCharge) {

        validateLoanIsNotClosed(loanCharge);

        // NOTE: to remove this constraint requires that loan transactions
        // that represent the waive of charges also be removed (or reversed)M
        // if you want ability to remove loan charges that are waived.
        validateLoanChargeIsNotWaived(loanCharge);

        final boolean removed = loanCharge.isActive();
        if (removed) {
            loanCharge.setActive(false);
            final LoanRepaymentScheduleProcessingWrapper wrapper = new LoanRepaymentScheduleProcessingWrapper();
            wrapper.reprocess(getCurrency(), getDisbursementDate(), this.repaymentScheduleInstallments, charges(), disbursementDetails);
            updateSummaryWithTotalFeeChargesDueAtDisbursement(deriveSumTotalOfChargesDueAtDisbursement());
        }

        removeOrModifyTransactionAssociatedWithLoanChargeIfDueAtDisbursement(loanCharge);

        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                .determineProcessor(this.transactionProcessingStrategy);
        if (!loanCharge.isDueAtDisbursement() && loanCharge.isPaidOrPartiallyPaid(loanCurrency())) {
            /****
             * TODO Vishwas Currently we do not allow removing a loan charge
             * after a loan is approved (hence there is no need to adjust any
             * loan transactions).
             * 
             * Consider removing this block of code or logically completing it
             * for the future by getting the list of affected Transactions
             ***/
            final List<LoanTransaction> allNonContraTransactionsPostDisbursement = retreiveListOfTransactionsPostDisbursement();
            loanRepaymentScheduleTransactionProcessor.handleTransaction(getDisbursementDate(), allNonContraTransactionsPostDisbursement,
                    getCurrency(), this.repaymentScheduleInstallments, charges(), disbursementDetails);
        }
        this.charges.remove(loanCharge);
        updateLoanSummaryDerivedFields();
    }

    private void removeOrModifyTransactionAssociatedWithLoanChargeIfDueAtDisbursement(final LoanCharge loanCharge) {
        if (loanCharge.isDueAtDisbursement()) {
            LoanTransaction transactionToRemove = null;
            for (final LoanTransaction transaction : this.loanTransactions) {
                if (transaction.isRepaymentAtDisbursement() && transaction.getLoanChargesPaid().contains(loanCharge)) {

                    final MonetaryCurrency currency = loanCurrency();
                    final Money chargeAmount = Money.of(currency, loanCharge.amount());
                    if (transaction.isGreaterThan(chargeAmount)) {
                        final Money principalPortion = Money.zero(currency);
                        final Money interestPortion = Money.zero(currency);
                        final Money penaltychargesPortion = Money.zero(currency);

                        final Money feeChargesPortion = chargeAmount;
                        transaction.updateComponentsAndTotal(principalPortion, interestPortion, feeChargesPortion, penaltychargesPortion);
                    } else {
                        transactionToRemove = transaction;
                    }
                }
            }

            if (transactionToRemove != null) {
                this.loanTransactions.remove(transactionToRemove);
            }
        }
    }

    public Map<String, Object> updateLoanCharge(final LoanCharge loanCharge, final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(3);

        validateLoanIsNotClosed(loanCharge);
        if (charges().contains(loanCharge)) {
            final BigDecimal amount = calculateAmountPercentageAppliedTo(loanCharge);
            final Map<String, Object> loanChargeChanges = loanCharge.update(command, amount);
            actualChanges.putAll(loanChargeChanges);
            updateSummaryWithTotalFeeChargesDueAtDisbursement(deriveSumTotalOfChargesDueAtDisbursement());
        }

        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                .determineProcessor(this.transactionProcessingStrategy);
        if (!loanCharge.isDueAtDisbursement()) {
            /****
             * TODO Vishwas Currently we do not allow waiving updating loan
             * charge after a loan is approved (hence there is no need to adjust
             * any loan transactions).
             * 
             * Consider removing this block of code or logically completing it
             * for the future by getting the list of affected Transactions
             ***/
            final List<LoanTransaction> allNonContraTransactionsPostDisbursement = retreiveListOfTransactionsPostDisbursement();
            loanRepaymentScheduleTransactionProcessor.handleTransaction(getDisbursementDate(), allNonContraTransactionsPostDisbursement,
                    getCurrency(), this.repaymentScheduleInstallments, charges(), disbursementDetails);
        } else {
            // reprocess loan schedule based on charge been waived.
            final LoanRepaymentScheduleProcessingWrapper wrapper = new LoanRepaymentScheduleProcessingWrapper();
            wrapper.reprocess(getCurrency(), getDisbursementDate(), this.repaymentScheduleInstallments, charges(), disbursementDetails);
        }

        updateLoanSummaryDerivedFields();

        return actualChanges;
    }

    /**
     * @param loanCharge
     * @return
     */
    private BigDecimal calculateAmountPercentageAppliedTo(final LoanCharge loanCharge) {
        BigDecimal amount = BigDecimal.ZERO;
        if (loanCharge.isOverdueInstallmentCharge()) { return loanCharge.getAmountPercentageAppliedTo(); }
        switch (loanCharge.getChargeCalculation()) {
            case PERCENT_OF_AMOUNT:
                amount = getDerivedAmountForCharge(loanCharge);
            break;
            case PERCENT_OF_AMOUNT_AND_INTEREST:
                final BigDecimal totalInterestCharged = getTotalInterest();
                amount = getPrincpal().getAmount().add(totalInterestCharged);
            break;
            case PERCENT_OF_INTEREST:
                amount = getTotalInterest();
            break;
            case PERCENT_OF_DISBURSEMENT_AMOUNT:
                if (loanCharge.getTrancheDisbursementCharge() != null) {
                	amount = loanCharge.getTrancheDisbursementCharge().getloanDisbursementDetails().principal();
                } else {
                    amount = getPrincpal().getAmount();
                }
            break;
            case PERCENT_OF_AMOUNT_INTEREST_AND_FEES:
            	final BigDecimal totalInterestChargd = getTotalInterest();
                amount = getPrincpal().getAmount().add(totalInterestChargd).add(this.getSummary().getTotalFeeChargesCharged());
            break;	
            default:
            break;
        }
        return amount;
    }

    /**
     * @return
     */
    public BigDecimal getTotalInterest() {
        return this.loanSummaryWrapper.calculateTotalInterestCharged(this.repaymentScheduleInstallments, getCurrency()).getAmount();
    }

    public BigDecimal calculatePerInstallmentChargeAmount(final LoanCharge loanCharge) {
        if (loanCharge.getChargeCalculation().getValue() == ChargeCalculationType.PERCENT_OF_DISBURSEMENT_AMOUNT.getValue()
                && loanCharge.isInstalmentFee()) { return loanCharge.amount(); }
        return calculatePerInstallmentChargeAmount(loanCharge.getChargeCalculation(), loanCharge.getPercentage());
    }

    public BigDecimal calculatePerInstallmentChargeAmount(final ChargeCalculationType calculationType, final BigDecimal percentage) {
        Money amount = Money.zero(getCurrency());
        for (final LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
            amount = amount.plus(calculateInstallmentChargeAmount(calculationType, percentage, installment));
        }
        return amount.getAmount();
    }

    public BigDecimal getTotalWrittenOff() {
        return this.summary.getTotalWrittenOff();
    }

    /**
     * @param calculationType
     * @param percentage
     * @param installment
     * @return
     */
    private Money calculateInstallmentChargeAmount(final ChargeCalculationType calculationType, final BigDecimal percentage,
            final LoanRepaymentScheduleInstallment installment) {
        Money amount = Money.zero(getCurrency());
        Money percentOf = Money.zero(getCurrency());
        switch (calculationType) {
            case PERCENT_OF_AMOUNT:
                percentOf = installment.getPrincipal(getCurrency());
            break;
            case PERCENT_OF_AMOUNT_AND_INTEREST:
                percentOf = installment.getPrincipal(getCurrency()).plus(installment.getInterestCharged(getCurrency()));
            break;
            case PERCENT_OF_INTEREST:
                percentOf = installment.getInterestCharged(getCurrency());
            break;
            case PERCENT_OF_DISBURSEMENT_AMOUNT:
                percentOf = installment.getPrincipal(getCurrency());
            break;
            case PERCENT_OF_AMOUNT_INTEREST_AND_FEES:
                percentOf = installment.getPrincipal(getCurrency()).plus(installment.getInterestCharged(getCurrency()))
                	.plus(installment.getFeeChargesCharged(getCurrency()));
            break;
            default:
            break;
        }
        amount = amount.plus(LoanCharge.percentageOf(percentOf.getAmount(), percentage));
        return amount;
    }

    public LoanTransaction waiveLoanCharge(final LoanCharge loanCharge, final LoanLifecycleStateMachine loanLifecycleStateMachine,
            final Map<String, Object> changes, final List<Long> existingTransactionIds, final List<Long> existingReversedTransactionIds,
            final Integer loanInstallmentNumber, final ScheduleGeneratorDTO scheduleGeneratorDTO, final Money accruedCharge,
            final AppUser currentUser) {

        validateLoanIsNotClosed(loanCharge);
        
        final Money amountWaived = loanCharge.waive(loanCurrency(), loanInstallmentNumber);

        changes.put("amount", amountWaived.getAmount());

        Money unrecognizedIncome = amountWaived.zero();
        Money chargeComponent = amountWaived;
        if (isPeriodicAccrualAccountingEnabledOnLoanProduct()) {
            Money receivableCharge = Money.zero(getCurrency());
            if (loanInstallmentNumber != null) {
                receivableCharge = accruedCharge.minus(loanCharge.getInstallmentLoanCharge(loanInstallmentNumber).getAmountPaid(
                        getCurrency()));
            } else {
                receivableCharge = accruedCharge.minus(loanCharge.getAmountPaid(getCurrency()));
            }
            if (receivableCharge.isLessThanZero()) {
                receivableCharge = amountWaived.zero();
            }
            if (amountWaived.isGreaterThan(receivableCharge)) {
                chargeComponent = receivableCharge;
                unrecognizedIncome = amountWaived.minus(receivableCharge);
            }
        }
        Money feeChargesWaived = chargeComponent;
        Money penaltyChargesWaived = Money.zero(loanCurrency());
        if (loanCharge.isPenaltyCharge()) {
            penaltyChargesWaived = chargeComponent;
            feeChargesWaived = Money.zero(loanCurrency());
        }

        LocalDate transactionDate = getDisbursementDate();
        if (loanCharge.isDueDateCharge()) {
            if (loanCharge.getDueLocalDate().isAfter(DateUtils.getLocalDateOfTenant())) {
                transactionDate = DateUtils.getLocalDateOfTenant();
            } else {
                transactionDate = loanCharge.getDueLocalDate();
            }
        }else if(loanCharge.isInstalmentFee()){
            transactionDate = loanCharge.getInstallmentLoanCharge(loanInstallmentNumber).getRepaymentInstallment().getDueDate();
            if(transactionDate.isAfter(DateUtils.getLocalDateOfTenant())){
                transactionDate = DateUtils.getLocalDateOfTenant();
            }
        }
        
        scheduleGeneratorDTO.setRecalculateFrom(transactionDate);

        updateSummaryWithTotalFeeChargesDueAtDisbursement(deriveSumTotalOfChargesDueAtDisbursement());

        existingTransactionIds.addAll(findExistingTransactionIds());
        existingReversedTransactionIds.addAll(findExistingReversedTransactionIds());
        final LoanTransaction waiveLoanChargeTransaction = LoanTransaction.waiveLoanCharge(this, getOffice(), amountWaived,
                transactionDate, feeChargesWaived, penaltyChargesWaived, unrecognizedIncome);
        final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(waiveLoanChargeTransaction, loanCharge, waiveLoanChargeTransaction
                .getAmount(getCurrency()).getAmount(), loanInstallmentNumber);
        waiveLoanChargeTransaction.getLoanChargesPaid().add(loanChargePaidBy);
        this.loanTransactions.add(waiveLoanChargeTransaction);

        if (!loanCharge.isDueAtDisbursement()) {
            if (this.repaymentScheduleDetail().isInterestRecalculationEnabled()
                    && (loanCharge.getDueLocalDate() == null || DateUtils.getLocalDateOfTenant().isAfter(loanCharge.getDueLocalDate()))) {
                regenerateRepaymentScheduleWithInterestRecalculation(scheduleGeneratorDTO, currentUser);
            }
            // Waive of charges whose due date falls after latest 'repayment'
            // transaction dont require entire loan schedule to be reprocessed.
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                    .determineProcessor(this.transactionProcessingStrategy);
            if (loanCharge.isPaidOrPartiallyPaid(loanCurrency())) {
                /****
                 * TODO Vishwas Currently we do not allow waiving fully paid
                 * loan charge and waiving partially paid loan charges only
                 * waives the remaining amount.
                 * 
                 * Consider removing this block of code or logically completing
                 * it for the future by getting the list of affected
                 * Transactions
                 ***/
                final List<LoanTransaction> allNonContraTransactionsPostDisbursement = retreiveListOfTransactionsPostDisbursement();
                loanRepaymentScheduleTransactionProcessor.handleTransaction(getDisbursementDate(),
                        allNonContraTransactionsPostDisbursement, getCurrency(), this.repaymentScheduleInstallments, charges(), disbursementDetails);
            } else {
                // reprocess loan schedule based on charge been waived.
                final LoanRepaymentScheduleProcessingWrapper wrapper = new LoanRepaymentScheduleProcessingWrapper();
                wrapper.reprocess(getCurrency(), getDisbursementDate(), this.repaymentScheduleInstallments, charges(), disbursementDetails);
            }
        }

        updateLoanSummaryDerivedFields();

        doPostLoanTransactionChecks(waiveLoanChargeTransaction.getTransactionDate(), loanLifecycleStateMachine);

        return waiveLoanChargeTransaction;
    }
    
    /**
     * its create transaction for waive of glim charges
     * 
     */
    public LoanTransaction waiveGlimLoanCharge(final LoanCharge loanCharge, final List<Long> existingTransactionIds,
            final List<Long> existingReversedTransactionIds, final Integer loanInstallmentNumber,
            final ScheduleGeneratorDTO scheduleGeneratorDTO, final Money accruedCharge, final AppUser currentUser, Money clientcharge) {

        validateLoanIsNotClosed(loanCharge);

        Money amountWaived = loanCharge.waiveForGlim(loanCurrency(), clientcharge, loanInstallmentNumber);

        Money unrecognizedIncome = amountWaived.zero();
        Money chargeComponent = amountWaived;
        if (isPeriodicAccrualAccountingEnabledOnLoanProduct()) {
            Money receivableCharge = Money.zero(getCurrency());
            if (loanInstallmentNumber != null) {
                receivableCharge = accruedCharge.minus(loanCharge.getInstallmentLoanCharge(loanInstallmentNumber).getAmountPaid(
                        getCurrency()));
            } else {
                receivableCharge = accruedCharge.minus(loanCharge.getAmountPaid(getCurrency()));
            }
            if (receivableCharge.isLessThanZero()) {
                receivableCharge = amountWaived.zero();
            }
            if (amountWaived.isGreaterThan(receivableCharge)) {
                chargeComponent = receivableCharge;
                unrecognizedIncome = amountWaived.minus(receivableCharge);
            }
        }
        Money feeChargesWaived = chargeComponent;
        Money penaltyChargesWaived = Money.zero(loanCurrency());
        if (loanCharge.isPenaltyCharge()) {
            penaltyChargesWaived = chargeComponent;
            feeChargesWaived = Money.zero(loanCurrency());
        }

        LocalDate transactionDate = getDisbursementDate();
        if (loanCharge.isSpecifiedDueDate()) {
            transactionDate = loanCharge.getDueLocalDate();
        }
        scheduleGeneratorDTO.setRecalculateFrom(transactionDate);

        updateSummaryWithTotalFeeChargesDueAtDisbursement(deriveSumTotalOfChargesDueAtDisbursement());

        existingTransactionIds.addAll(findExistingTransactionIds());
        existingReversedTransactionIds.addAll(findExistingReversedTransactionIds());

        final LoanTransaction waiveLoanChargeTransaction = LoanTransaction.waiveLoanCharge(this, getOffice(), amountWaived,
                transactionDate, feeChargesWaived, penaltyChargesWaived, unrecognizedIncome);

        if (this.repaymentScheduleDetail().isInterestRecalculationEnabled()
                && (loanCharge.getDueLocalDate() == null || DateUtils.getLocalDateOfTenant().isAfter(loanCharge.getDueLocalDate()))) {
            regenerateRepaymentScheduleWithInterestRecalculation(scheduleGeneratorDTO, currentUser);
        }
        // Waive of charges whose due date falls after latest 'repayment'
        // transaction dont require entire loan schedule to be reprocessed.
        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                .determineProcessor(this.transactionProcessingStrategy);
        if (!loanCharge.isDueAtDisbursement() && loanCharge.isPaidOrPartiallyPaid(loanCurrency())) {
            /****
             * TODO Vishwas Currently we do not allow waiving fully paid loan
             * charge and waiving partially paid loan charges only waives the
             * remaining amount.
             * 
             * Consider removing this block of code or logically completing it
             * for the future by getting the list of affected Transactions
             ***/
            final List<LoanTransaction> allNonContraTransactionsPostDisbursement = retreiveListOfTransactionsPostDisbursement();
            loanRepaymentScheduleTransactionProcessor.handleTransaction(getDisbursementDate(), allNonContraTransactionsPostDisbursement,
                    getCurrency(), this.repaymentScheduleInstallments, charges(), disbursementDetails);
        } else {
            // reprocess loan schedule based on charge been waived.
            final LoanRepaymentScheduleProcessingWrapper wrapper = new LoanRepaymentScheduleProcessingWrapper();
            wrapper.reprocess(getCurrency(), getDisbursementDate(), this.repaymentScheduleInstallments, charges(), disbursementDetails);
        }

        return waiveLoanChargeTransaction;
    }

    public Client client() {
        return this.client;
    }

    public LoanProduct loanProduct() {
        return this.loanProduct;
    }

    public LoanProductRelatedDetail repaymentScheduleDetail() {
        return this.loanRepaymentScheduleDetail;
    }

    public void updateClient(final Client client) {
        this.client = client;
    }

    public void updateLoanProduct(final LoanProduct loanProduct) {
        this.loanProduct = loanProduct;
    }

    public void updateAccountNo(final String newAccountNo) {
        this.accountNumber = newAccountNo;
        this.accountNumberRequiresAutoGeneration = false;
    }

    public void updateFund(final Fund fund) {
        this.fund = fund;
    }

    public void updateLoanPurpose(final LoanPurpose loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public void updateLoanOfficerOnLoanApplication(final Staff newLoanOfficer) {
        if (!isSubmittedAndPendingApproval()) {
            Long loanOfficerId = null;
            if (this.loanOfficer != null) {
                loanOfficerId = this.loanOfficer.getId();
            }
            throw new LoanOfficerAssignmentException(getId(), loanOfficerId);
        }
        this.loanOfficer = newLoanOfficer;
    }
    
	public void updateLoanOfficer(final Staff newLoanOfficer) {
		if(newLoanOfficer != null){
		this.loanOfficer = newLoanOfficer;
		}
	}

    public void updateTransactionProcessingStrategy(final LoanTransactionProcessingStrategy strategy) {
        this.transactionProcessingStrategy = strategy;
    }

    public void updateLoanCharges(final Set<LoanCharge> loanCharges) {
        List<Long> existingCharges = fetchAllLoanChargeIds();

        /** Process new and updated charges **/
        for (final LoanCharge loanCharge : loanCharges) {
            LoanCharge charge = loanCharge;
            // add new charges
            if (loanCharge.getId() == null) {
                LoanTrancheDisbursementCharge loanTrancheDisbursementCharge = null;
                loanCharge.update(this);
                if (this.loanProduct.isMultiDisburseLoan() && loanCharge.isTrancheDisbursementCharge()) {
                    loanCharge.getTrancheDisbursementCharge().getloanDisbursementDetails().updateLoan(this);
                    for (final LoanDisbursementDetails loanDisbursementDetails : this.disbursementDetails) {
                        if (loanDisbursementDetails.isActive() && loanCharge.getTrancheDisbursementCharge().getloanDisbursementDetails().getId() == null) {
                            if (loanCharge.getTrancheDisbursementCharge().getloanDisbursementDetails().equals(loanDisbursementDetails)) {
                                loanTrancheDisbursementCharge = new LoanTrancheDisbursementCharge(loanCharge, loanDisbursementDetails);
                                loanCharge.updateLoanTrancheDisbursementCharge(loanTrancheDisbursementCharge);
                            }
                        }
                    }
                }
                this.charges.add(loanCharge);
            } else {
                charge = fetchLoanChargesById(charge.getId());
                if (charge != null) existingCharges.remove(charge.getId());
            }
            final BigDecimal amount = calculateAmountPercentageAppliedTo(loanCharge);
            BigDecimal chargeAmt = BigDecimal.ZERO;
            BigDecimal totalChargeAmt = BigDecimal.ZERO;
            if (loanCharge.getChargeCalculation().isPercentageBased()) {
                chargeAmt = loanCharge.getPercentage();
                if (loanCharge.isInstalmentFee()) {
                    totalChargeAmt = calculatePerInstallmentChargeAmount(loanCharge);
                }
            } else {
                chargeAmt = loanCharge.amountOrPercentage();
            }
            if (charge != null) {
                charge.update(chargeAmt, loanCharge.getDueLocalDate(), amount, fetchNumberOfInstallmensAfterExceptions(), totalChargeAmt);
            }
        }

        /** Updated deleted charges **/
        for (Long id : existingCharges) {
            fetchLoanChargesById(id).setActive(false);
        }
        updateSummaryWithTotalFeeChargesDueAtDisbursement(deriveSumTotalOfChargesDueAtDisbursement());
        updateNetAmountForTranches(loanCharges);
    }

    public void updateLoanCollateral(final Set<LoanCollateral> loanCollateral) {
        if (this.collateral == null) {
            this.collateral = new HashSet<>();
        }
        this.collateral.clear();
        this.collateral.addAll(associateWithThisLoan(loanCollateral));
    }

    public void updateLoanSchedule(final LoanScheduleModel modifiedLoanSchedule, AppUser currentUser) {
        this.repaymentScheduleInstallments.clear();

        for (final LoanScheduleModelPeriod scheduledLoanInstallment : modifiedLoanSchedule.getPeriods()) {

            if (scheduledLoanInstallment.isRepaymentPeriod()) {
                final LoanRepaymentScheduleInstallment installment = new LoanRepaymentScheduleInstallment(this,
                        scheduledLoanInstallment.periodNumber(), scheduledLoanInstallment.periodFromDate(),
                        scheduledLoanInstallment.periodDueDate(), scheduledLoanInstallment.principalDue(),
                        scheduledLoanInstallment.interestDue(), scheduledLoanInstallment.feeChargesDue(),
                        scheduledLoanInstallment.penaltyChargesDue(), scheduledLoanInstallment.isRecalculatedInterestComponent(),
                        scheduledLoanInstallment.getLoanCompoundingDetails(), scheduledLoanInstallment.advancePayment());
                addRepaymentScheduleInstallment(installment);
            }
        }

        updateLoanScheduleDependentDerivedFields();
        updateLoanSummaryDerivedFields();
        applyAccurals(currentUser);

    }

    public void updateLoanSchedule(final Collection<LoanRepaymentScheduleInstallment> installments, AppUser currentUser) {
        this.repaymentScheduleInstallments.clear();
        for (final LoanRepaymentScheduleInstallment installment : installments) {
            addRepaymentScheduleInstallment(installment);
        }
        updateLoanScheduleDependentDerivedFields();
        updateLoanSummaryDerivedFields();
        applyAccurals(currentUser);

    }

    /**
     * method updates accrual derived fields on installments and reverse the
     * unprocessed transactions
     */
    private void applyAccurals(AppUser currentUser) {
        if (this.loanInterestRecalculationDetails != null && this.loanInterestRecalculationDetails.isCompoundingToBePostedAsTransaction()) { return; }
        Collection<LoanTransaction> accruals = retreiveListOfAccrualTransactions();
        if (isPeriodicAccrualAccountingEnabledOnLoanProduct()) {
            applyPeriodicAccruals(accruals);
        } else if (isNoneOrCashOrUpfrontAccrualAccountingEnabledOnLoanProduct()) {
            updateAccrualsForNonPeriodicAccruals(accruals);
        }
    }

    private void applyPeriodicAccruals(final Collection<LoanTransaction> accruals) {

        for (LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
            Money interest = Money.zero(getCurrency());
            Money fee = Money.zero(getCurrency());
            Money penality = Money.zero(getCurrency());
            for (LoanTransaction loanTransaction : accruals) {
                if (loanTransaction.getTransactionDate().isAfter(installment.getFromDate())
                        && !loanTransaction.getTransactionDate().isAfter(installment.getDueDate())) {
                    interest = interest.plus(loanTransaction.getInterestPortion(getCurrency()));
                    fee = fee.plus(loanTransaction.getFeeChargesPortion(getCurrency()));
                    penality = penality.plus(loanTransaction.getPenaltyChargesPortion(getCurrency()));
                    if (installment.getFeeChargesCharged(getCurrency()).isLessThan(fee)
                            || installment.getInterestCharged(getCurrency()).isLessThan(interest)
                            || installment.getPenaltyChargesCharged(getCurrency()).isLessThan(penality)) {
                        interest = interest.minus(loanTransaction.getInterestPortion(getCurrency()));
                        fee = fee.minus(loanTransaction.getFeeChargesPortion(getCurrency()));
                        penality = penality.minus(loanTransaction.getPenaltyChargesPortion(getCurrency()));
                        loanTransaction.reverse();
                    }
                }
            }
            installment.updateAccrualPortion(interest, fee, penality);
        }
        LoanRepaymentScheduleInstallment lastInstallment = this.repaymentScheduleInstallments
                .get(this.repaymentScheduleInstallments.size() - 1);
        for (LoanTransaction loanTransaction : accruals) {
            if (loanTransaction.getTransactionDate().isAfter(lastInstallment.getDueDate()) && !loanTransaction.isReversed()) {
                loanTransaction.reverse();
            }
        }
    }

    private void updateAccrualsForNonPeriodicAccruals(final Collection<LoanTransaction> accruals) {

        final Money interestApplied = Money.of(getCurrency(), this.summary.getTotalInterestCharged());
        for (LoanTransaction loanTransaction : accruals) {
            if (loanTransaction.getInterestPortion(getCurrency()).isGreaterThanZero()) {
                if (loanTransaction.getInterestPortion(getCurrency()).isNotEqualTo(interestApplied)) {
                    loanTransaction.reverse();
                    final LoanTransaction interestAppliedTransaction = LoanTransaction.accrueInterest(getOffice(), this, interestApplied,
                            getDisbursementDate());
                    this.loanTransactions.add(interestAppliedTransaction);
                }
            } else {
                Set<LoanChargePaidBy> chargePaidBies = loanTransaction.getLoanChargesPaid();
                for (final LoanChargePaidBy chargePaidBy : chargePaidBies) {
                    LoanCharge loanCharge = chargePaidBy.getLoanCharge();
                    Money chargeAmount = loanCharge.getAmount(getCurrency());
                    if (chargeAmount.isNotEqualTo(loanTransaction.getAmount(getCurrency()))) {
                        loanTransaction.reverse();
                        handleChargeAppliedTransaction(loanCharge, loanTransaction.getTransactionDate());
                    }

                }
            }
        }

    }

    public void updateLoanScheduleDependentDerivedFields() {
        this.expectedMaturityDate = determineExpectedMaturityDate().toDate();
        this.actualMaturityDate = determineExpectedMaturityDate().toDate();
    }

    private void updateLoanSummaryDerivedFields() {

        if (isNotDisbursed()) {
            this.summary.zeroFields();
            this.totalOverpaid = null;
        } else {
            final Money overpaidBy = calculateTotalOverpayment();
            this.totalOverpaid = overpaidBy.getAmountDefaultedToNullIfZero();

            final Money recoveredAmount = calculateTotalRecoveredPayments();
            this.totalRecovered = recoveredAmount.getAmountDefaultedToNullIfZero();

            final Money principal = getPrincipalWithBrokenPeriodInterest();
            this.summary.updateSummary(loanCurrency(), principal, this.repaymentScheduleInstallments, this.loanSummaryWrapper,
                    isDisbursed(), this.charges);
            updateLoanOutstandingBalaces();
        }
    }

    public void updateLoanSummaryAndStatus() {
        updateLoanSummaryDerivedFields();
        doPostLoanTransactionChecks(getLastUserTransactionDate(), loanLifecycleStateMachine);
    }

    public Map<String, Object> loanApplicationModification(final JsonCommand command, final Set<LoanCharge> possiblyModifedLoanCharges,
            final Set<LoanCollateral> possiblyModifedLoanCollateralItems, final AprCalculator aprCalculator, boolean isChargesModified) {

        final Map<String, Object> actualChanges = this.loanRepaymentScheduleDetail.updateLoanApplicationAttributes(command, aprCalculator);
        if (!actualChanges.isEmpty()) {
            final boolean recalculateLoanSchedule = !(actualChanges.size() == 1 && actualChanges.containsKey("inArrearsTolerance"));
            actualChanges.put("recalculateLoanSchedule", recalculateLoanSchedule);
            isChargesModified = true;
        }
        final String interestRatePerPeriodParamName = "interestRatePerPeriod";
        if(actualChanges.containsKey(interestRatePerPeriodParamName)){
            if(getFlatInterestRate() != null){
                final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(interestRatePerPeriodParamName);
                this.flatInterestRate = newValue;
                actualChanges.put("recalculateLoanSchedule", true);
            }
        }

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        final String accountNoParamName = "accountNo";
        if (command.isChangeInStringParameterNamed(accountNoParamName, this.accountNumber)) {
            final String newValue = command.stringValueOfParameterNamed(accountNoParamName);
            actualChanges.put(accountNoParamName, newValue);
            this.accountNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        final String createSiAtDisbursementParameterName = "createStandingInstructionAtDisbursement";
        if (command.isChangeInBooleanParameterNamed(createSiAtDisbursementParameterName, shouldCreateStandingInstructionAtDisbursement())) {
            final Boolean valueAsInput = command.booleanObjectValueOfParameterNamed(createSiAtDisbursementParameterName);
            actualChanges.put(createSiAtDisbursementParameterName, valueAsInput);
            this.createStandingInstructionAtDisbursement = valueAsInput;
        }

        final String externalIdParamName = "externalId";
        if (command.isChangeInStringParameterNamed(externalIdParamName, this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed(externalIdParamName);
            actualChanges.put(externalIdParamName, newValue);
            this.externalId = StringUtils.defaultIfEmpty(newValue, null);
        }

        // add clientId, groupId and loanType changes to actual changes

        final String clientIdParamName = "clientId";
        final Long clientId = this.client == null ? null : this.client.getId();
        if (command.isChangeInLongParameterNamed(clientIdParamName, clientId)) {
            final Long newValue = command.longValueOfParameterNamed(clientIdParamName);
            actualChanges.put(clientIdParamName, newValue);
        }

        // FIXME: AA - We may require separate api command to move loan from one
        // group to another
        final String groupIdParamName = "groupId";
        final Long groupId = this.group == null ? null : this.group.getId();
        if (command.isChangeInLongParameterNamed(groupIdParamName, groupId)) {
            final Long newValue = command.longValueOfParameterNamed(groupIdParamName);
            actualChanges.put(groupIdParamName, newValue);
        }

        final String productIdParamName = "productId";
        if (command.isChangeInLongParameterNamed(productIdParamName, this.loanProduct.getId())) {
            final Long newValue = command.longValueOfParameterNamed(productIdParamName);
            actualChanges.put(productIdParamName, newValue);
            actualChanges.put("recalculateLoanSchedule", true);
        }

        final String isFloatingInterestRateParamName = "isFloatingInterestRate";
        if (command.isChangeInBooleanParameterNamed(isFloatingInterestRateParamName, this.isFloatingInterestRate)) {
            final Boolean newValue = command.booleanObjectValueOfParameterNamed(isFloatingInterestRateParamName);
            actualChanges.put(isFloatingInterestRateParamName, newValue);
            this.isFloatingInterestRate = newValue;
        }

        final String interestRateDifferentialParamName = "interestRateDifferential";
        if (command.isChangeInBigDecimalParameterNamed(interestRateDifferentialParamName, this.interestRateDifferential)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(interestRateDifferentialParamName);
            actualChanges.put(interestRateDifferentialParamName, newValue);
            this.interestRateDifferential = newValue;
        }

        Long existingFundId = null;
        if (this.fund != null) {
            existingFundId = this.fund.getId();
        }
        final String fundIdParamName = "fundId";
        if (command.isChangeInLongParameterNamed(fundIdParamName, existingFundId)) {
            final Long newValue = command.longValueOfParameterNamed(fundIdParamName);
            actualChanges.put(fundIdParamName, newValue);
        }

        Long existingLoanOfficerId = null;
        if (this.loanOfficer != null) {
            existingLoanOfficerId = this.loanOfficer.getId();
        }
        final String loanOfficerIdParamName = "loanOfficerId";
        if (command.isChangeInLongParameterNamed(loanOfficerIdParamName, existingLoanOfficerId)) {
            final Long newValue = command.longValueOfParameterNamed(loanOfficerIdParamName);
            actualChanges.put(loanOfficerIdParamName, newValue);
        }

        Long existingLoanPurposeId = null;
        if (this.loanPurpose != null) {
            existingLoanPurposeId = this.loanPurpose.getId();
        }
        final String loanPurposeIdParamName = "loanPurposeId";
        if (command.isChangeInLongParameterNamed(loanPurposeIdParamName, existingLoanPurposeId)) {
            final Long newValue = command.longValueOfParameterNamed(loanPurposeIdParamName);
            actualChanges.put(loanPurposeIdParamName, newValue);
        }

        final String strategyIdParamName = "transactionProcessingStrategyId";
        if (command.isChangeInLongParameterNamed(strategyIdParamName, this.transactionProcessingStrategy.getId())) {
            final Long newValue = command.longValueOfParameterNamed(strategyIdParamName);
            actualChanges.put(strategyIdParamName, newValue);
        }

        final String submittedOnDateParamName = "submittedOnDate";
        if (command.isChangeInLocalDateParameterNamed(submittedOnDateParamName, getSubmittedOnDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(submittedOnDateParamName);
            actualChanges.put(submittedOnDateParamName, valueAsInput);
            actualChanges.put("dateFormat", dateFormatAsInput);
            actualChanges.put("locale", localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(submittedOnDateParamName);
            this.submittedOnDate = newValue.toDate();
        }

        final String expectedDisbursementDateParamName = "expectedDisbursementDate";
        if (command.isChangeInLocalDateParameterNamed(expectedDisbursementDateParamName, getExpectedDisbursedOnLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(expectedDisbursementDateParamName);
            actualChanges.put(expectedDisbursementDateParamName, valueAsInput);
            actualChanges.put("dateFormat", dateFormatAsInput);
            actualChanges.put("locale", localeAsInput);
            actualChanges.put("recalculateLoanSchedule", true);

            final LocalDate newValue = command.localDateValueOfParameterNamed(expectedDisbursementDateParamName);
            this.expectedDisbursementDate = newValue.toDate();
            removeFirstDisbursementTransaction();
        }

        final String repaymentsStartingFromDateParamName = "repaymentsStartingFromDate";
        if (command.isChangeInLocalDateParameterNamed(repaymentsStartingFromDateParamName, getExpectedFirstRepaymentOnDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(repaymentsStartingFromDateParamName);
            actualChanges.put(repaymentsStartingFromDateParamName, valueAsInput);
            actualChanges.put("dateFormat", dateFormatAsInput);
            actualChanges.put("locale", localeAsInput);
            actualChanges.put("recalculateLoanSchedule", true);

            final LocalDate newValue = command.localDateValueOfParameterNamed(repaymentsStartingFromDateParamName);
            if (newValue != null) {
                this.expectedFirstRepaymentOnDate = newValue.toDate();
            } else {
                this.expectedFirstRepaymentOnDate = null;
            }
        }

        final String syncDisbursementParameterName = "syncDisbursementWithMeeting";
        if (command.isChangeInBooleanParameterNamed(syncDisbursementParameterName, isSyncDisbursementWithMeeting())) {
            final Boolean valueAsInput = command.booleanObjectValueOfParameterNamed(syncDisbursementParameterName);
            actualChanges.put(syncDisbursementParameterName, valueAsInput);
            this.syncDisbursementWithMeeting = valueAsInput;
        }

        final String interestChargedFromDateParamName = "interestChargedFromDate";
        if (command.isChangeInLocalDateParameterNamed(interestChargedFromDateParamName, getInterestChargedFromDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(interestChargedFromDateParamName);
            actualChanges.put(interestChargedFromDateParamName, valueAsInput);
            actualChanges.put("dateFormat", dateFormatAsInput);
            actualChanges.put("locale", localeAsInput);
            actualChanges.put("recalculateLoanSchedule", true);

            final LocalDate newValue = command.localDateValueOfParameterNamed(interestChargedFromDateParamName);
            if (newValue != null) {
                this.interestChargedFromDate = newValue.toDate();
            } else {
                this.interestChargedFromDate = null;
            }
        }

        // the comparison should be done with the tenant date
        // (DateUtils.getLocalDateOfTenant()) and not the server date (new
        // LocalDate())
        if (getSubmittedOnDate().isAfter(DateUtils.getLocalDateOfTenant())) {
            final String errorMessage = "The date on which a loan is submitted cannot be in the future.";
            throw new InvalidLoanStateTransitionException("submittal", "cannot.be.a.future.date", errorMessage, getSubmittedOnDate());
        }

        if (!(this.client == null)) {
            if (getSubmittedOnDate().isBefore(this.client.getActivationLocalDate())) {
                final String errorMessage = "The date on which a loan is submitted cannot be earlier than client's activation date.";
                throw new InvalidLoanStateTransitionException("submittal", "cannot.be.before.client.activation.date", errorMessage,
                        getSubmittedOnDate());
            }
        } else if (!(this.group == null)) {
            if (getSubmittedOnDate().isBefore(this.group.getActivationLocalDate())) {
                final String errorMessage = "The date on which a loan is submitted cannot be earlier than groups's activation date.";
                throw new InvalidLoanStateTransitionException("submittal", "cannot.be.before.group.activation.date", errorMessage,
                        getSubmittedOnDate());
            }
        }

        if (getSubmittedOnDate().isAfter(getExpectedDisbursedOnLocalDate())) {
            final String errorMessage = "The date on which a loan is submitted cannot be after its expected disbursement date: "
                    + getExpectedDisbursedOnLocalDate().toString();
            throw new InvalidLoanStateTransitionException("submittal", "cannot.be.after.expected.disbursement.date", errorMessage,
                    getSubmittedOnDate(), getExpectedDisbursedOnLocalDate());
        }

        final String chargesParamName = "charges";

        if (isChargesModified) {
            actualChanges.put(chargesParamName, getLoanCharges(possiblyModifedLoanCharges));
            actualChanges.put("recalculateLoanSchedule", true);
        }

        final String collateralParamName = "collateral";
        if (command.parameterExists(collateralParamName)) {

            if (!possiblyModifedLoanCollateralItems.equals(this.collateral)) {
                actualChanges.put(collateralParamName, listOfLoanCollateralData(possiblyModifedLoanCollateralItems));
            }
        }

        final String loanTermFrequencyParamName = "loanTermFrequency";
        if (command.isChangeInIntegerParameterNamed(loanTermFrequencyParamName, this.termFrequency)) {
            final Integer newValue = command.integerValueOfParameterNamed(loanTermFrequencyParamName);
            actualChanges.put(externalIdParamName, newValue);
            this.termFrequency = newValue;
        }

        final String loanTermFrequencyTypeParamName = "loanTermFrequencyType";
        if (command.isChangeInIntegerParameterNamed(loanTermFrequencyTypeParamName, this.termPeriodFrequencyType)) {
            final Integer newValue = command.integerValueOfParameterNamed(loanTermFrequencyTypeParamName);
            final PeriodFrequencyType newTermPeriodFrequencyType = PeriodFrequencyType.fromInt(newValue);
            actualChanges.put(loanTermFrequencyTypeParamName, newTermPeriodFrequencyType.getValue());
            this.termPeriodFrequencyType = newValue;
        }

        final String principalParamName = "principal";
        if (command.isChangeInBigDecimalParameterNamed(principalParamName, this.approvedPrincipal)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(principalParamName);
            this.approvedPrincipal = newValue;
        }

        if (command.isChangeInBigDecimalParameterNamed(principalParamName, this.proposedPrincipal)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(principalParamName);
            this.proposedPrincipal = newValue;
        }

        if (loanProduct.isMultiDisburseLoan()) {
            updateDisbursementDetails(command, actualChanges);
            if (command.isChangeInBigDecimalParameterNamed(LoanApiConstants.maxOutstandingBalanceParameterName,
                    this.maxOutstandingLoanBalance)) {
                this.maxOutstandingLoanBalance = command
                        .bigDecimalValueOfParameterNamed(LoanApiConstants.maxOutstandingBalanceParameterName);
            }
            final JsonArray disbursementDataArray = command.arrayOfParameterNamed(LoanApiConstants.disbursementDataParameterName);

            if (disbursementDataArray == null || disbursementDataArray.size() == 0) {
                final String errorMessage = "For this loan product, disbursement details must be provided";
                throw new MultiDisbursementDataRequiredException(LoanApiConstants.disbursementDataParameterName, errorMessage);
            }
            if (disbursementDataArray.size() > loanProduct.maxTrancheCount()) {
                final String errorMessage = "Number of tranche shouldn't be greter than " + loanProduct.maxTrancheCount();
                throw new ExceedingTrancheCountException(LoanApiConstants.disbursementDataParameterName, errorMessage,
                        loanProduct.maxTrancheCount(), disbursementDetails.size());
            }
        } else {
            this.disbursementDetails.clear();
        }

        if (loanProduct.isMultiDisburseLoan() || loanProduct.canDefineInstallmentAmount()) {
            if (command.isChangeInBigDecimalParameterNamed(LoanApiConstants.emiAmountParameterName, this.fixedEmiAmount)) {
                this.fixedEmiAmount = command.bigDecimalValueOfParameterNamed(LoanApiConstants.emiAmountParameterName);
                actualChanges.put(LoanApiConstants.emiAmountParameterName, this.fixedEmiAmount);
                actualChanges.put("recalculateLoanSchedule", true);
            }
        } else {
            this.fixedEmiAmount = null;
        }
        Long expectedDisbursalTypeId = null;
        if(this.getExpectedDisbursalPaymentType() != null){
            expectedDisbursalTypeId = this.getExpectedDisbursalPaymentType().getId();
        }
        
        Long expectedRepaymentPaymentTypeId = null;
        if(this.getExpectedRepaymentPaymentType() != null){
            expectedRepaymentPaymentTypeId = this.getExpectedRepaymentPaymentType().getId();   
        }
        
        if (command.isChangeInLongParameterNamed(LoanApiConstants.expectedDisbursalPaymentTypeParamName, expectedDisbursalTypeId)) {
            actualChanges.put(LoanApiConstants.expectedDisbursalPaymentTypeParamName,
                    command.integerValueOfParameterNamed(LoanApiConstants.expectedDisbursalPaymentTypeParamName));
        }
        if (command.isChangeInLongParameterNamed(LoanApiConstants.expectedRepaymentPaymentTypeParamName, expectedRepaymentPaymentTypeId)) {
            actualChanges.put(LoanApiConstants.expectedRepaymentPaymentTypeParamName,
                    command.integerValueOfParameterNamed(LoanApiConstants.expectedRepaymentPaymentTypeParamName));
        }
        
        if (command.isChangeInBigDecimalParameterNamed(LoanApiConstants.discountOnDisbursalAmountParameterName, this.discountOnDisbursalAmount)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(LoanApiConstants.discountOnDisbursalAmountParameterName);
            this.discountOnDisbursalAmount = newValue;
            actualChanges.put(LoanApiConstants.discountOnDisbursalAmountParameterName, newValue);
        }
        
        if (command.isChangeInBigDecimalParameterNamed(LoanApiConstants.amountForUpfrontCollectionParameterName, this.amountForUpfrontCollection)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(LoanApiConstants.amountForUpfrontCollectionParameterName);
            this.amountForUpfrontCollection = newValue;
            actualChanges.put(LoanApiConstants.amountForUpfrontCollectionParameterName, newValue);
        }
        
        return actualChanges;
    }

    public void recalculateAllCharges() {
        Set<LoanCharge> charges = this.charges();
        int penaltyWaitPeriod = 0;
        for (final LoanCharge loanCharge : charges) {
            recalculateLoanCharge(loanCharge, penaltyWaitPeriod);
        }
        updateSummaryWithTotalFeeChargesDueAtDisbursement(deriveSumTotalOfChargesDueAtDisbursement());
        updateNetAmountForTranches(charges);
    }

    public boolean isInterestRecalculationEnabledForProduct() {
        return this.loanProduct.isInterestRecalculationEnabled();
    }

    public boolean isMultiDisburmentLoan() {
        return this.loanProduct.isMultiDisburseLoan();
    }

    /**
     * Update interest recalculation settings if product configuration changes
     */

    private void updateOverdueScheduleInstallment(final LoanCharge loanCharge) {
        if (loanCharge.isOverdueInstallmentCharge() && loanCharge.isActive()) {
            LoanOverdueInstallmentCharge overdueInstallmentCharge = loanCharge.getOverdueInstallmentCharge();
            if (overdueInstallmentCharge != null) {
                Integer installmentNumber = overdueInstallmentCharge.getInstallment().getInstallmentNumber();
                LoanRepaymentScheduleInstallment installment = fetchRepaymentScheduleInstallment(installmentNumber);
                overdueInstallmentCharge.updateLoanRepaymentScheduleInstallment(installment);
            }
        }
    }
    
    private void recalculateDisbursementCharge(LoanDisbursementDetails loanDisbursementDetail, Collection<LoanCharge> charges) {
        // penaltyWaitPeriod not used for tranche charges
        if(this.trancheCharges.isEmpty()){
            return;
        }
        final int penaltyWaitPeriod = 0;
        for (final LoanCharge loanCharge : charges) {
            if (loanCharge.isActive() && loanCharge.isChargePending() && loanCharge.getTrancheDisbursementCharge() != null
                    && loanCharge.getTrancheDisbursementCharge().getloanDisbursementDetails().expectedDisbursementDateAsLocalDate()
                            .isEqual(loanDisbursementDetail.expectedDisbursementDateAsLocalDate())) {
                recalculateLoanCharge(loanCharge, penaltyWaitPeriod);
            }
        }
    }

    private void recalculateLoanCharge(final LoanCharge loanCharge, final int penaltyWaitPeriod) {
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal chargeAmt = BigDecimal.ZERO;
        BigDecimal totalChargeAmt = BigDecimal.ZERO;
        if (loanCharge.getChargeCalculation().isPercentageBased()) {
            if (loanCharge.isOverdueInstallmentCharge()) {
                amount = calculateOverdueAmountPercentageAppliedTo(loanCharge, penaltyWaitPeriod);
            } else {
                amount = calculateAmountPercentageAppliedTo(loanCharge);
            }
            chargeAmt = loanCharge.getPercentage();
            if (loanCharge.isInstalmentFee()) {
                totalChargeAmt = calculatePerInstallmentChargeAmount(loanCharge);
            }
        } else {
            chargeAmt = loanCharge.amountOrPercentage();
        }
        if (loanCharge.isActive()) {
            loanCharge.update(chargeAmt, loanCharge.getDueLocalDate(), amount, fetchNumberOfInstallmensAfterExceptions(), totalChargeAmt);
            validateChargeHasValidSpecifiedDateIfApplicable(loanCharge, getDisbursementDate(), getLastRepaymentPeriodDueDate(false));
        }

    }

    private BigDecimal calculateOverdueAmountPercentageAppliedTo(final LoanCharge loanCharge, final int penaltyWaitPeriod) {
        LoanRepaymentScheduleInstallment installment = loanCharge.getOverdueInstallmentCharge().getInstallment();
        LocalDate graceDate = DateUtils.getLocalDateOfTenant().minusDays(penaltyWaitPeriod);
        Money amount = Money.zero(getCurrency());
        if (graceDate.isAfter(installment.getDueDate())) {
            amount = calculateOverdueAmountPercentageAppliedTo(installment, loanCharge.getChargeCalculation());
            if (!amount.isGreaterThanZero()) {
                loanCharge.setActive(false);
            }
        } else {
            loanCharge.setActive(false);
        }
        return amount.getAmount();
    }

    private Money calculateOverdueAmountPercentageAppliedTo(LoanRepaymentScheduleInstallment installment,
            ChargeCalculationType calculationType) {
        Money amount = Money.zero(getCurrency());
        switch (calculationType) {
            case PERCENT_OF_AMOUNT:
                amount = installment.getPrincipalOutstanding(getCurrency());
            break;
            case PERCENT_OF_AMOUNT_AND_INTEREST:
                amount = installment.getPrincipalOutstanding(getCurrency()).plus(installment.getInterestOutstanding(getCurrency()));
            break;
            case PERCENT_OF_INTEREST:
                amount = installment.getInterestOutstanding(getCurrency());
            break;
            case PERCENT_OF_AMOUNT_INTEREST_AND_FEES:
            	amount = installment.getPrincipal(getCurrency()).plus(installment.getInterestCharged(getCurrency()))
                	.plus(installment.getFeeChargesCharged(getCurrency()));
            break;
            default:
            break;
        }
        return amount;
    }

    // This method returns date format and locale if present in the JsonCommand
    private Map<String, String> getDateFormatAndLocale(final JsonCommand jsonCommand) {
        Map<String, String> returnObject = new HashMap<>();
        JsonElement jsonElement = jsonCommand.parsedJson();
        if (jsonElement.isJsonObject()) {
            JsonObject topLevel = jsonElement.getAsJsonObject();
            if (topLevel.has(LoanApiConstants.dateFormatParameterName)
                    && topLevel.get(LoanApiConstants.dateFormatParameterName).isJsonPrimitive()) {
                final JsonPrimitive primitive = topLevel.get(LoanApiConstants.dateFormatParameterName).getAsJsonPrimitive();
                returnObject.put(LoanApiConstants.dateFormatParameterName, primitive.getAsString());
            }
            if (topLevel.has(LoanApiConstants.localeParameterName) && topLevel.get(LoanApiConstants.localeParameterName).isJsonPrimitive()) {
                final JsonPrimitive primitive = topLevel.get(LoanApiConstants.localeParameterName).getAsJsonPrimitive();
                String localeString = primitive.getAsString();
                returnObject.put(LoanApiConstants.localeParameterName, localeString);
            }
        }
        return returnObject;
    }

    private Map<String, Object> parseDisbursementDetails(final JsonObject jsonObject, String dateFormat, Locale locale) {
        Map<String, Object> returnObject = new HashMap<>();
        if (jsonObject.get(LoanApiConstants.disbursementDateParameterName) != null
                && jsonObject.get(LoanApiConstants.disbursementDateParameterName).isJsonPrimitive()) {
            final JsonPrimitive primitive = jsonObject.get(LoanApiConstants.disbursementDateParameterName).getAsJsonPrimitive();
            final String valueAsString = primitive.getAsString();
            if (StringUtils.isNotBlank(valueAsString)) {
                LocalDate date = JsonParserHelper.convertFrom(valueAsString, LoanApiConstants.disbursementDateParameterName, dateFormat,
                        locale);
                if (date != null) {
                    returnObject.put(LoanApiConstants.disbursementDateParameterName, date.toDate());
                }
            }
        }

        if (jsonObject.get(LoanApiConstants.disbursementPrincipalParameterName).isJsonPrimitive()
                && StringUtils.isNotBlank((jsonObject.get(LoanApiConstants.disbursementPrincipalParameterName).getAsString()))) {
            BigDecimal principal = jsonObject.getAsJsonPrimitive(LoanApiConstants.disbursementPrincipalParameterName).getAsBigDecimal();
            returnObject.put(LoanApiConstants.disbursementPrincipalParameterName, principal);
        }

        if (jsonObject.has(LoanApiConstants.disbursementIdParameterName)
                && jsonObject.get(LoanApiConstants.disbursementIdParameterName).isJsonPrimitive()
                && StringUtils.isNotBlank((jsonObject.get(LoanApiConstants.disbursementIdParameterName).getAsString()))) {
            Long id = jsonObject.getAsJsonPrimitive(LoanApiConstants.disbursementIdParameterName).getAsLong();
            returnObject.put(LoanApiConstants.disbursementIdParameterName, id);
        }

        if (jsonObject.has(LoanApiConstants.loanChargeIdParameterName)
                && jsonObject.get(LoanApiConstants.loanChargeIdParameterName).isJsonPrimitive()
                && StringUtils.isNotBlank((jsonObject.get(LoanApiConstants.loanChargeIdParameterName).getAsString()))) {
            returnObject.put(LoanApiConstants.loanChargeIdParameterName,
                    jsonObject.getAsJsonPrimitive(LoanApiConstants.loanChargeIdParameterName).getAsString());
        }
        return returnObject;
    }

    public LocalDate updateDisbursementDetails(final JsonCommand jsonCommand, final Map<String, Object> actualChanges) {

        // disbursementList is updated when tranche is removed
        List<Long> disbursementList = fetchDisbursementIds();
        List<Long> loanChargeIds = fetchLoanTrancheChargeIds();
        int chargeIdLength = loanChargeIds.size();
        LocalDate recalculateFromDate = null;
        String chargeIds = null;
        // From modify application page, if user removes all charges, we should
        // get empty array.
        // So we need to remove all charges applied for this loan
        boolean removeAllChages = false;
        if (jsonCommand.parameterExists(LoanApiConstants.chargesParameterName)) {
            JsonArray chargesArray = jsonCommand.arrayOfParameterNamed(LoanApiConstants.chargesParameterName);
            if (chargesArray.size() == 0) removeAllChages = true;
        }
        
        final List<LoanCharge> charges = new ArrayList<>();
        if (!this.trancheCharges.isEmpty()) {
            for (LoanCharge charge : this.getLoanCharges()) {
                if (charge.isActive() && charge.isTrancheDisbursementCharge()) {
                    charges.add(charge);
                }
            }
        }

        if (jsonCommand.parameterExists(LoanApiConstants.disbursementDataParameterName)) {
            final JsonArray disbursementDataArray = jsonCommand.arrayOfParameterNamed(LoanApiConstants.disbursementDataParameterName);
            if (disbursementDataArray != null && disbursementDataArray.size() > 0) {
                String dateFormat = null;
                Locale locale = null;
                // Gets date format and locate
                Map<String, String> dateAndLocale = getDateFormatAndLocale(jsonCommand);
                dateFormat = dateAndLocale.get(LoanApiConstants.dateFormatParameterName);
                if (dateAndLocale.containsKey(LoanApiConstants.localeParameterName)) {
                    locale = JsonParserHelper.localeFromString(dateAndLocale.get(LoanApiConstants.localeParameterName));
                }
                for (JsonElement jsonElement : disbursementDataArray) {
                    final JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Map<String, Object> parsedDisbursementData = parseDisbursementDetails(jsonObject, dateFormat, locale);
                    Date expectedDisbursementDate = (Date) parsedDisbursementData.get(LoanApiConstants.disbursementDateParameterName);
                    BigDecimal principal = (BigDecimal) parsedDisbursementData.get(LoanApiConstants.disbursementPrincipalParameterName);
                    chargeIds = (String) parsedDisbursementData.get(LoanApiConstants.loanChargeIdParameterName);
                    if (chargeIds != null) {
                        if (chargeIds.indexOf(",") != -1) {
                            String[] chargeId = chargeIds.split(",");
                            for (String loanChargeId : chargeId) {
                                loanChargeIds.remove(Long.parseLong(loanChargeId));
                            }
                        } else {
                            loanChargeIds.remove(Long.parseLong(chargeIds));
                        }
                    }
                    recalculateFromDate = createOrUpdateDisbursementDetails(actualChanges, expectedDisbursementDate, principal,
                            disbursementList, recalculateFromDate, charges);
                }
               
                recalculateFromDate = removeDisbursementAndAssociatedCharges(actualChanges, disbursementList, loanChargeIds, chargeIdLength, removeAllChages, recalculateFromDate);
            }
        }
        return recalculateFromDate;
    }

    private LocalDate removeDisbursementAndAssociatedCharges(final Map<String, Object> actualChanges, List<Long> disbursementList,
            List<Long> loanChargeIds, int chargeIdLength, boolean removeAllChages, LocalDate recalculateFromDate) {
        if (removeAllChages) {
            LoanCharge[] tempCharges = new LoanCharge[this.charges.size()];
            this.charges.toArray(tempCharges);
            for (LoanCharge loanCharge : tempCharges) {
                removeLoanCharge(loanCharge);
            }
            this.trancheCharges.clear();
        } else {
            if (loanChargeIds.size() > 0 && loanChargeIds.size() != chargeIdLength) {
                for (Long chargeId : loanChargeIds) {
                    LoanCharge deleteCharge = fetchLoanChargesById(chargeId);
                    if (this.charges.contains(deleteCharge)) {
                        removeLoanCharge(deleteCharge);
                    }
                }
            }
        }
        for (Long id : disbursementList) {
            removeChargesByDisbursementID(id);
            LoanDisbursementDetails loanDisbursementDetail = fetchLoanDisbursementsById(id);
            recalculateFromDate = getRecalculateFromDate(recalculateFromDate, loanDisbursementDetail.expectedDisbursementDateAsLocalDate());
            loanDisbursementDetail.setActive(false);
            actualChanges.put("recalculateLoanSchedule", true);
        }
        return recalculateFromDate;
    }

    private LocalDate createOrUpdateDisbursementDetails(final Map<String, Object> actualChanges,
            Date expectedDisbursementDate, BigDecimal principal, List<Long> existingDisbursementList, LocalDate recalculateFromDate, 
            Collection<LoanCharge> charges) {

        LoanDisbursementDetails loanDisbursementDetails = fetchLoanDisbursementByExpectedDisbursementDate(expectedDisbursementDate);
        if (loanDisbursementDetails != null) {
            LoanDisbursementDetails loanDisbursementDetail = fetchLoanDisbursementsById(loanDisbursementDetails.getId());
            existingDisbursementList.remove(loanDisbursementDetails.getId());
            if (loanDisbursementDetail.actualDisbursementDate() == null) {
                Date actualDisbursementDate = null;
                LoanDisbursementDetails disbursementDetails = new LoanDisbursementDetails(expectedDisbursementDate, actualDisbursementDate,
                        principal);
                loanDisbursementDetail.updateLoan(this);
                if (!loanDisbursementDetail.equals(disbursementDetails)) {
                    loanDisbursementDetail.copy(disbursementDetails);
                    actualChanges.put("disbursementDetailId", loanDisbursementDetails.getId());
                    actualChanges.put("recalculateLoanSchedule", true);
                    recalculateDisbursementCharge(loanDisbursementDetail, charges);
                }
            }
        } else {
            Date actualDisbursementDate = null;
            LoanDisbursementDetails disbursementDetails = new LoanDisbursementDetails(expectedDisbursementDate, actualDisbursementDate,
                    principal);
            disbursementDetails.updateLoan(this);
            LocalDate expectedDisbursementDateAsLocalDate = new LocalDate(expectedDisbursementDate);
            recalculateFromDate = getRecalculateFromDate(recalculateFromDate, expectedDisbursementDateAsLocalDate);
            this.disbursementDetails.add(disbursementDetails);
            for (LoanTrancheCharge trancheCharge : trancheCharges) {
                Charge chargeDefinition = trancheCharge.getCharge();
                BigDecimal amount = trancheCharge.getAmount();
                final LoanCharge loanCharge = LoanCharge.createNewWithoutLoan(chargeDefinition, principal, amount, null, null, new LocalDate(
                        expectedDisbursementDate), null, null);
                loanCharge.update(this);
                LoanTrancheDisbursementCharge loanTrancheDisbursementCharge = new LoanTrancheDisbursementCharge(loanCharge,
                        disbursementDetails);
                loanCharge.updateLoanTrancheDisbursementCharge(loanTrancheDisbursementCharge);
                addLoanCharge(loanCharge);
            }
            actualChanges.put(LoanApiConstants.disbursementDataParameterName, expectedDisbursementDate + "-" + principal);
            actualChanges.put("recalculateLoanSchedule", true);
        }
        updateNetAmountForTranches(charges);
        return recalculateFromDate;
    }
    
    public void updateNetAmountForTranches(final Collection<LoanCharge> charges) {
        if (isMultiDisburmentLoan()) {
            resetNetDisburseAmount();
            for (LoanCharge charge : charges) {
                if (charge.getTrancheDisbursementCharge() != null) {
                    BigDecimal pricipal = charge.getTrancheDisbursementCharge().getloanDisbursementDetails().getNetPrincipalDisbursed();
                    BigDecimal chargeAmount = charge.amount();
                    charge.getTrancheDisbursementCharge().getloanDisbursementDetails()
                            .setNetPrincipalDisbursed(pricipal.subtract(chargeAmount));
                }
            }
        }
    }
    
    private void resetNetDisburseAmount(){
        if (isMultiDisburmentLoan()) {
            for (LoanDisbursementDetails detail : this.disbursementDetails) {
            	if(detail.isActive()){
                detail.resetNetPrincipalDisbursed();
            	}
            }
        }
    }

    public LocalDate getRecalculateFromDate(LocalDate recalculateFromDate, LocalDate expectedDisbursementDateAsLocalDate) {
        if (recalculateFromDate == null) {
            recalculateFromDate = expectedDisbursementDateAsLocalDate;
        } else if (recalculateFromDate.isAfter(expectedDisbursementDateAsLocalDate)) {
            recalculateFromDate = expectedDisbursementDateAsLocalDate;
        }
        return recalculateFromDate;
    }

    private void removeChargesByDisbursementID(Long id) {
        List<LoanCharge> tempCharges = new ArrayList<>();
        for (LoanCharge charge : this.charges) {
            LoanTrancheDisbursementCharge transCharge = charge.getTrancheDisbursementCharge();
            if (transCharge != null && id.equals(transCharge.getloanDisbursementDetails().getId())) {
                tempCharges.add(charge);
            }
        }
        for (LoanCharge charge : tempCharges) {
            removeLoanCharge(charge);
        }
    }

    private List<Long> fetchLoanTrancheChargeIds() {
        List<Long> list = new ArrayList<>();
        for (LoanCharge charge : this.charges) {
            if (charge.isTrancheDisbursementCharge() && charge.isActive()) {
                list.add(charge.getId());
            }
        }
        return list;
    }

    public LoanDisbursementDetails fetchLoanDisbursementsById(Long id) {
        LoanDisbursementDetails loanDisbursementDetail = null;
        for (LoanDisbursementDetails disbursementDetail : this.disbursementDetails) {
            if (disbursementDetail.isActive() && id.equals(disbursementDetail.getId())) {
                loanDisbursementDetail = disbursementDetail;
                break;
            }
        }
        return loanDisbursementDetail;
    }
    
    public LoanDisbursementDetails fetchLoanDisbursementByExpectedDisbursementDate(Date date) {
        LoanDisbursementDetails loanDisbursementDetail = null;
        for (LoanDisbursementDetails disbursementDetail : this.disbursementDetails) {
            if (disbursementDetail.isActive() && date.equals(disbursementDetail.expectedDisbursementDate())) {
                loanDisbursementDetail = disbursementDetail;
                break;
            }
        }
        return loanDisbursementDetail;
    }

    private List<Long> fetchDisbursementIds() {
        List<Long> list = new ArrayList<>();
        for (LoanDisbursementDetails disbursementDetails : this.disbursementDetails) {
        	if(disbursementDetails.isActive()){
            list.add(disbursementDetails.getId());
        	}
        }
        return list;
    }

    private CollateralData[] listOfLoanCollateralData(final Set<LoanCollateral> setOfLoanCollateral) {

        CollateralData[] existingLoanCollateral = null;

        final List<CollateralData> loanCollateralList = new ArrayList<>();
        for (final LoanCollateral loanCollateral : setOfLoanCollateral) {

            final CollateralData data = loanCollateral.toData();

            loanCollateralList.add(data);
        }

        existingLoanCollateral = loanCollateralList.toArray(new CollateralData[loanCollateralList.size()]);

        return existingLoanCollateral;
    }

    private LoanChargeCommand[] getLoanCharges(final Set<LoanCharge> setOfLoanCharges) {

        LoanChargeCommand[] existingLoanCharges = null;

        final List<LoanChargeCommand> loanChargesList = new ArrayList<>();
        for (final LoanCharge loanCharge : setOfLoanCharges) {
            loanChargesList.add(loanCharge.toCommand());
        }

        existingLoanCharges = loanChargesList.toArray(new LoanChargeCommand[loanChargesList.size()]);

        return existingLoanCharges;
    }

    private void removeFirstDisbursementTransaction() {
        for (final LoanTransaction loanTransaction : this.loanTransactions) {
            if (loanTransaction.isDisbursement()) {
                this.loanTransactions.remove(loanTransaction);
                break;
            }
        }
    }

    public void loanApplicationSubmittal(final AppUser currentUser, final LoanScheduleModel loanSchedule,
            final LoanApplicationTerms loanApplicationTerms, final LoanLifecycleStateMachine lifecycleStateMachine,
            final LocalDate submittedOn, final String externalId, final boolean allowTransactionsOnHoliday, final List<Holiday> holidays,
            final WorkingDays workingDays, final boolean allowTransactionsOnNonWorkingDay, List<GroupLoanIndividualMonitoring> glimList) {

        updateLoanSchedule(loanSchedule, currentUser);

        LoanStatus from = null;
        if (this.loanStatus != null) {
            from = LoanStatus.fromInt(this.loanStatus);
        }

        final LoanStatus statusEnum = lifecycleStateMachine.transition(LoanEvent.LOAN_CREATED, from);
        this.loanStatus = statusEnum.getValue();

        this.externalId = externalId;
        this.termFrequency = loanApplicationTerms.getLoanTermFrequency();
        this.termPeriodFrequencyType = loanApplicationTerms.getLoanTermPeriodFrequencyType().getValue();
        this.submittedOnDate = submittedOn.toDate();
        this.submittedBy = currentUser;
        this.expectedDisbursementDate = loanApplicationTerms.getExpectedDisbursementDate().toDate();
        this.expectedFirstRepaymentOnDate = loanApplicationTerms.getRepaymentStartFromDate();
        this.interestChargedFromDate = loanApplicationTerms.getInterestChargedFromDateFromUser();

        updateLoanScheduleDependentDerivedFields();

        if (submittedOn.isAfter(DateUtils.getLocalDateOfTenant())) {
            final String errorMessage = "The date on which a loan is submitted cannot be in the future.";
            throw new InvalidLoanStateTransitionException("submittal", "cannot.be.a.future.date", errorMessage, submittedOn);
        }

        if (this.client != null && this.client.isActivatedAfter(submittedOn)) {
            final String errorMessage = "The date on which a loan is submitted cannot be earlier than client's activation date.";
            throw new InvalidLoanStateTransitionException("submittal", "cannot.be.before.client.activation.date", errorMessage, submittedOn);
        }

        validateActivityNotBeforeClientOrGroupTransferDate(LoanEvent.LOAN_CREATED, submittedOn);

        if (this.group != null && this.group.isActivatedAfter(submittedOn)) {
            final String errorMessage = "The date on which a loan is submitted cannot be earlier than groups's activation date.";
            throw new InvalidLoanStateTransitionException("submittal", "cannot.be.before.group.activation.date", errorMessage, submittedOn);
        }

        if (submittedOn.isAfter(getExpectedDisbursedOnLocalDate())) {
            final String errorMessage = "The date on which a loan is submitted cannot be after its expected disbursement date: "
                    + getExpectedDisbursedOnLocalDate().toString();
            throw new InvalidLoanStateTransitionException("submittal", "cannot.be.after.expected.disbursement.date", errorMessage,
                    submittedOn, getExpectedDisbursedOnLocalDate());
        }

        // charges are optional
        int penaltyWaitPeriod = 0;
        this.glimList = glimList;
        for (final LoanCharge loanCharge : charges()) {
            recalculateLoanCharge(loanCharge, penaltyWaitPeriod);
        }

        updateSummaryWithTotalFeeChargesDueAtDisbursement(deriveSumTotalOfChargesDueAtDisbursement());

        // validate if disbursement date is a holiday or a non-working day
        validateDisbursementDateIsOnNonWorkingDay(workingDays, allowTransactionsOnNonWorkingDay);
        validateDisbursementDateIsOnHoliday(allowTransactionsOnHoliday, holidays);

        /**
         * Copy interest recalculation settings if interest recalculation is
         * enabled
         */
        if (this.loanRepaymentScheduleDetail.isInterestRecalculationEnabled()) {

            this.loanInterestRecalculationDetails = LoanInterestRecalculationDetails.createFrom(this.loanProduct
                    .getProductInterestRecalculationDetails());
            this.loanInterestRecalculationDetails.updateLoan(this);
        }

    }

    private LocalDate determineExpectedMaturityDate() {
        final int numberOfInstallments = this.repaymentScheduleInstallments.size();
        LocalDate maturityDate = this.repaymentScheduleInstallments.get(numberOfInstallments - 1).getDueDate();
        ListIterator<LoanRepaymentScheduleInstallment> iterator = this.repaymentScheduleInstallments.listIterator(numberOfInstallments);
        while(iterator.hasPrevious()){
            LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment = iterator.previous();
            if(!loanRepaymentScheduleInstallment.isRecalculatedInterestComponent()){
                maturityDate = loanRepaymentScheduleInstallment.getDueDate();
                break;
            }
        }
        return maturityDate;
    }

    public Map<String, Object> loanApplicationRejection(final AppUser currentUser, final JsonCommand command,
            final LoanLifecycleStateMachine loanLifecycleStateMachine) {

        validateAccountStatus(LoanEvent.LOAN_REJECTED);

        final Map<String, Object> actualChanges = new LinkedHashMap<>();

        final LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_REJECTED, LoanStatus.fromInt(this.loanStatus));
        if (!statusEnum.hasStateOf(LoanStatus.fromInt(this.loanStatus))) {
            this.loanStatus = statusEnum.getValue();
            actualChanges.put("status", LoanEnumerations.status(this.loanStatus));

            final LocalDate rejectedOn = command.localDateValueOfParameterNamed("rejectedOnDate");

            final Locale locale = new Locale(command.locale());
            final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);

            this.rejectedOnDate = rejectedOn.toDate();
            this.rejectedBy = currentUser;
            this.closedOnDate = rejectedOn.toDate();
            this.closedBy = currentUser;

            actualChanges.put("locale", command.locale());
            actualChanges.put("dateFormat", command.dateFormat());
            actualChanges.put("rejectedOnDate", rejectedOn.toString(fmt));
            actualChanges.put("closedOnDate", rejectedOn.toString(fmt));

            if (rejectedOn.isBefore(getSubmittedOnDate())) {
                final String errorMessage = "The date on which a loan is rejected cannot be before its submittal date: "
                        + getSubmittedOnDate().toString();
                throw new InvalidLoanStateTransitionException("reject", "cannot.be.before.submittal.date", errorMessage, rejectedOn,
                        getSubmittedOnDate());
            }

            validateActivityNotBeforeClientOrGroupTransferDate(LoanEvent.LOAN_REJECTED, rejectedOn);

            if (rejectedOn.isAfter(DateUtils.getLocalDateOfTenant())) {
                final String errorMessage = "The date on which a loan is rejected cannot be in the future.";
                throw new InvalidLoanStateTransitionException("reject", "cannot.be.a.future.date", errorMessage, rejectedOn);
            }
        } else {
            final String errorMessage = "Only the loan applications with status 'Submitted and pending approval' are allowed to be rejected.";
            throw new InvalidLoanStateTransitionException("reject", "cannot.reject", errorMessage);
        }

        return actualChanges;
    }

    public Map<String, Object> loanApplicationWithdrawnByApplicant(final AppUser currentUser, final JsonCommand command,
            final LoanLifecycleStateMachine loanLifecycleStateMachine) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>();

        final LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_WITHDRAWN, LoanStatus.fromInt(this.loanStatus));
        if (!statusEnum.hasStateOf(LoanStatus.fromInt(this.loanStatus))) {
            this.loanStatus = statusEnum.getValue();
            actualChanges.put("status", LoanEnumerations.status(this.loanStatus));

            LocalDate withdrawnOn = command.localDateValueOfParameterNamed("withdrawnOnDate");
            if (withdrawnOn == null) {
                withdrawnOn = command.localDateValueOfParameterNamed("eventDate");
            }

            final Locale locale = new Locale(command.locale());
            final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);

            this.withdrawnOnDate = withdrawnOn.toDate();
            this.withdrawnBy = currentUser;
            this.closedOnDate = withdrawnOn.toDate();
            this.closedBy = currentUser;

            actualChanges.put("locale", command.locale());
            actualChanges.put("dateFormat", command.dateFormat());
            actualChanges.put("withdrawnOnDate", withdrawnOn.toString(fmt));
            actualChanges.put("closedOnDate", withdrawnOn.toString(fmt));

            if (withdrawnOn.isBefore(getSubmittedOnDate())) {
                final String errorMessage = "The date on which a loan is withdrawn cannot be before its submittal date: "
                        + getSubmittedOnDate().toString();
                throw new InvalidLoanStateTransitionException("withdraw", "cannot.be.before.submittal.date", errorMessage, command,
                        getSubmittedOnDate());
            }

            validateActivityNotBeforeClientOrGroupTransferDate(LoanEvent.LOAN_WITHDRAWN, withdrawnOn);

            if (withdrawnOn.isAfter(DateUtils.getLocalDateOfTenant())) {
                final String errorMessage = "The date on which a loan is withdrawn cannot be in the future.";
                throw new InvalidLoanStateTransitionException("withdraw", "cannot.be.a.future.date", errorMessage, command);
            }
        } else {
            final String errorMessage = "Only the loan applications with status 'Submitted and pending approval' are allowed to be withdrawn by applicant.";
            throw new InvalidLoanStateTransitionException("withdraw", "cannot.withdraw", errorMessage);
        }

        return actualChanges;
    }

    public Map<String, Object> loanApplicationApproval(final AppUser currentUser, final JsonCommand command,
            final JsonArray disbursementDataArray, final LoanLifecycleStateMachine loanLifecycleStateMachine) {

        validateAccountStatus(LoanEvent.LOAN_APPROVED);

        final Map<String, Object> actualChanges = new LinkedHashMap<>();

        /*
         * statusEnum is holding the possible new status derived from
         * loanLifecycleStateMachine.transition.
         */

        final LoanStatus newStatusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_APPROVED, LoanStatus.fromInt(this.loanStatus));

        /*
         * FIXME: There is no need to check below condition, if
         * loanLifecycleStateMachine.transition is doing it's responsibility
         * properly. Better implementation approach is, if code passes invalid
         * combination of states (fromState and toState), state machine should
         * return invalidate state and below if condition should check for not
         * equal to invalidateState, instead of check new value is same as
         * present value.
         */

        if (!newStatusEnum.hasStateOf(LoanStatus.fromInt(this.loanStatus))) {
            this.loanStatus = newStatusEnum.getValue();
            actualChanges.put("status", LoanEnumerations.status(this.loanStatus));

            // only do below if status has changed in the 'approval' case
            LocalDate approvedOn = command.localDateValueOfParameterNamed("approvedOnDate");
            String approvedOnDateChange = command.stringValueOfParameterNamed("approvedOnDate");
            if (approvedOn == null) {
                approvedOn = command.localDateValueOfParameterNamed("eventDate");
                approvedOnDateChange = command.stringValueOfParameterNamed("eventDate");
            }

            LocalDate expecteddisbursementDate = command.localDateValueOfParameterNamed("expectedDisbursementDate");

            BigDecimal approvedLoanAmount = command.bigDecimalValueOfParameterNamed(LoanApiConstants.approvedLoanAmountParameterName);

            if (approvedLoanAmount != null) {

                // Approved amount has to be less than or equal to principal
                // amount demanded

                if (approvedLoanAmount.compareTo(this.proposedPrincipal) == -1) {

                    this.approvedPrincipal = approvedLoanAmount;

                    /*
                     * All the calculations are done based on the principal
                     * amount, so it is necessary to set principal amount to
                     * approved amount
                     */

                    this.loanRepaymentScheduleDetail.setPrincipal(approvedLoanAmount);

                    actualChanges.put(LoanApiConstants.approvedLoanAmountParameterName, approvedLoanAmount);
                    actualChanges.put(LoanApiConstants.disbursementPrincipalParameterName, approvedLoanAmount);
                } else if (approvedLoanAmount.compareTo(this.proposedPrincipal) == 1) {
                    final String errorMessage = "Loan approved amount can't be greater than loan amount demanded.";
                    throw new InvalidLoanStateTransitionException("approval", "amount.can't.be.greater.than.loan.amount.demanded",
                            errorMessage, this.proposedPrincipal, approvedLoanAmount);
                }

                /* Update disbursement details */
                if (disbursementDataArray != null) {
                    updateDisbursementDetails(command, actualChanges);
                }
            }
            if (loanProduct.isMultiDisburseLoan()) {
                recalculateAllCharges();

                if (this.disbursementDetails.isEmpty()) {
                    final String errorMessage = "For this loan product, disbursement details must be provided";
                    throw new MultiDisbursementDataRequiredException(LoanApiConstants.disbursementDataParameterName, errorMessage);
                }
               
                if (getActiveTrancheCount()  > loanProduct.maxTrancheCount()) {
                    final String errorMessage = "Number of tranche shouldn't be greter than " + loanProduct.maxTrancheCount();
                    throw new ExceedingTrancheCountException(LoanApiConstants.disbursementDataParameterName, errorMessage,
                            loanProduct.maxTrancheCount(), disbursementDetails.size());
                }
            }
            this.approvedOnDate = approvedOn.toDate();
            this.approvedBy = currentUser;
            actualChanges.put("locale", command.locale());
            actualChanges.put("dateFormat", command.dateFormat());
            actualChanges.put("approvedOnDate", approvedOnDateChange);

            final LocalDate submittalDate = new LocalDate(this.submittedOnDate);
            if (approvedOn.isBefore(submittalDate)) {
                final String errorMessage = "The date on which a loan is approved cannot be before its submittal date: "
                        + submittalDate.toString();
                throw new InvalidLoanStateTransitionException("approval", "cannot.be.before.submittal.date", errorMessage,
                        getApprovedOnDate(), submittalDate);
            }

            if (expecteddisbursementDate != null) {
                this.expectedDisbursementDate = expecteddisbursementDate.toDate();
                actualChanges.put("expectedDisbursementDate", expectedDisbursementDate);

                if (expecteddisbursementDate.isBefore(approvedOn)) {
                    final String errorMessage = "The expected disbursement date should be either on or after the approval date: "
                            + approvedOn.toString();
                    throw new InvalidLoanStateTransitionException("expecteddisbursal", "should.be.on.or.after.approval.date", errorMessage,
                            getApprovedOnDate(), expecteddisbursementDate);
                }
            }

            validateActivityNotBeforeClientOrGroupTransferDate(LoanEvent.LOAN_APPROVED, approvedOn);

            if (approvedOn.isAfter(DateUtils.getLocalDateOfTenant())) {
                final String errorMessage = "The date on which a loan is approved cannot be in the future.";
                throw new InvalidLoanStateTransitionException("approval", "cannot.be.a.future.date", errorMessage, getApprovedOnDate());
            }

            if (this.loanOfficer != null) {
                final LoanOfficerAssignmentHistory loanOfficerAssignmentHistory = LoanOfficerAssignmentHistory.createNew(this,
                        this.loanOfficer, approvedOn);
                this.loanOfficerHistory.add(loanOfficerAssignmentHistory);
            }
        }

        return actualChanges;
    }

    public Map<String, Object> undoApproval(final LoanLifecycleStateMachine loanLifecycleStateMachine) {

        validateAccountStatus(LoanEvent.LOAN_APPROVAL_UNDO);
        final Map<String, Object> actualChanges = new LinkedHashMap<>();

        final LoanStatus currentStatus = LoanStatus.fromInt(this.loanStatus);
        final LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_APPROVAL_UNDO, currentStatus);
        if (!statusEnum.hasStateOf(currentStatus)) {
            this.loanStatus = statusEnum.getValue();
            actualChanges.put("status", LoanEnumerations.status(this.loanStatus));

            this.approvedOnDate = null;
            this.approvedBy = null;

            if (this.approvedPrincipal.compareTo(this.proposedPrincipal) != 0) {
                this.approvedPrincipal = this.proposedPrincipal;
                this.loanRepaymentScheduleDetail.setPrincipal(this.proposedPrincipal);

                actualChanges.put(LoanApiConstants.approvedLoanAmountParameterName, this.proposedPrincipal);
                actualChanges.put(LoanApiConstants.disbursementPrincipalParameterName, this.proposedPrincipal);

            }

            actualChanges.put("approvedOnDate", "");

            this.loanOfficerHistory.clear();
        }

        return actualChanges;
    }

    public Collection<Long> findExistingTransactionIds() {

        final Collection<Long> ids = new ArrayList<>();

        for (final LoanTransaction transaction : this.loanTransactions) {
            ids.add(transaction.getId());
        }

        return ids;
    }

    public Collection<Long> findExistingReversedTransactionIds() {

        final Collection<Long> ids = new ArrayList<>();

        for (final LoanTransaction transaction : this.loanTransactions) {
            if (transaction.isReversed()) {
                ids.add(transaction.getId());
            }
        }

        return ids;
    }

    public ChangedTransactionDetail disburse(final AppUser currentUser, final JsonCommand command, final Map<String, Object> actualChanges,
            final ScheduleGeneratorDTO scheduleGeneratorDTO, final PaymentDetail paymentDetail) {

        final LoanStatus statusEnum = this.loanLifecycleStateMachine.transition(LoanEvent.LOAN_DISBURSED,
                LoanStatus.fromInt(this.loanStatus));

        final LocalDate actualDisbursementDate = command.localDateValueOfParameterNamed("actualDisbursementDate");
        
        this.loanStatus = statusEnum.getValue();
        actualChanges.put("status", LoanEnumerations.status(this.loanStatus));

        this.disbursedBy = currentUser;
        updateLoanScheduleDependentDerivedFields();

        actualChanges.put("locale", command.locale());
        actualChanges.put("dateFormat", command.dateFormat());
        actualChanges.put("actualDisbursementDate", command.stringValueOfParameterNamed("actualDisbursementDate"));

        HolidayDetailDTO holidayDetailDTO = scheduleGeneratorDTO.getHolidayDetailDTO();

        // validate if disbursement date is a holiday or a non-working day
        validateDisbursementDateIsOnNonWorkingDay(holidayDetailDTO.getWorkingDays(), holidayDetailDTO.isAllowTransactionsOnNonWorkingDay());
        validateDisbursementDateIsOnHoliday(holidayDetailDTO.isAllowTransactionsOnHoliday(), holidayDetailDTO.getHolidays());

        updateSummaryWithTotalFeeChargesDueAtDisbursement(deriveSumTotalOfChargesDueAtDisbursement());
        updateLoanRepaymentPeriodsDerivedFields(actualDisbursementDate);
        handleDisbursementTransaction(actualDisbursementDate, paymentDetail);
        updateLoanSummaryDerivedFields();
        final Money interestApplied = Money.of(getCurrency(), this.summary.getTotalInterestCharged());

        /**
         * Add an interest applied transaction of the interest is accrued
         * upfront (Up front accrual), no accounting or cash based accounting is
         * selected
         **/

        if (isNoneOrCashOrUpfrontAccrualAccountingEnabledOnLoanProduct()
                && ((isMultiDisburmentLoan() && getDisbursedLoanDisbursementDetails().size() == 1) || !isMultiDisburmentLoan())) {
            final LoanTransaction interestAppliedTransaction = LoanTransaction.accrueInterest(getOffice(), this, interestApplied,
                    actualDisbursementDate);
            this.loanTransactions.add(interestAppliedTransaction);
        }
        
        if (this.repaymentScheduleDetail().getBrokenPeriodMethod().isPostInterest() && getBrokenPeriodInterest().isGreaterThanZero()) {
            Money interestPostedAmount = retriveBrokenPeriodPostedAmount();
            Money postInterest = getBrokenPeriodInterest().minus(interestPostedAmount);
            if (postInterest.isGreaterThanZero()) {
                LoanTransaction brokenPeriodInterest = LoanTransaction.brokenPeriodInterestPosting(getOffice(), postInterest,
                        actualDisbursementDate);
                brokenPeriodInterest.updateLoan(this);
                this.loanTransactions.add(brokenPeriodInterest);
            }
        }

        return reprocessTransactionForDisbursement();

    }
    
    private Money retriveBrokenPeriodPostedAmount(){
        Money interestPostedAmount = Money.zero(getCurrency()); 
        for(LoanTransaction transaction : this.getLoanTransactions()){
            if(transaction.getTypeOf().isBrokenPeriodInterestPosting() && transaction.isNotReversed()){
                interestPostedAmount = interestPostedAmount.plus(transaction.getAmount());
            }
        }
        return interestPostedAmount;
    }
    
    private List<LoanDisbursementDetails> getDisbursedLoanDisbursementDetails() {
        List<LoanDisbursementDetails> ret = new ArrayList<>();
        if (this.disbursementDetails != null && this.disbursementDetails.size() > 0) {
            for (LoanDisbursementDetails disbursementDetail : this.disbursementDetails) {
                if (disbursementDetail.isActive() && disbursementDetail.actualDisbursementDate() != null) {
                    ret.add(disbursementDetail);
                }
            }
        }
        return ret;
    }

    private void validateTrancheDisbursalDateMustBeUniqueAndNotBeforeLastTransactionDate(final LocalDate actualDisbursementDate) {
        if (this.isOpen()) {
        if (!this.loanTransactions.isEmpty() && actualDisbursementDate.isBefore(getLastUserTransactionDate())) {
            final String errorMessage = "The disbursement date cannot be before last transaction date.";
            throw new InvalidLoanStateTransitionException("transaction", "cannot.be.before.last.transaction.date", errorMessage, actualDisbursementDate);
        }
        
        if (!this.disbursementDetails.isEmpty()) {
            for (LoanDisbursementDetails disbursementDetail : this.disbursementDetails) {
                if (disbursementDetail.isActive() && disbursementDetail.actualDisbursementDate() != null
                        && disbursementDetail.actualDisbursementDate().equals(actualDisbursementDate.toDate())) {
                    final String errorMessage = "The Tranche disbursement date must be unique.";
                    throw new InvalidLoanStateTransitionException("transaction", "tranche.disbursement.date.must.be.unique", errorMessage, actualDisbursementDate);
                }
            }
        }
     }
        
 }

    public void regenerateScheduleOnDisbursement(final ScheduleGeneratorDTO scheduleGeneratorDTO, final boolean recalculateSchedule,
            final LocalDate actualDisbursementDate, BigDecimal emiAmount, final AppUser currentUser, LocalDate nextPossibleRepaymentDate,
            Date rescheduledRepaymentDate) {
        boolean isEmiAmountChanged = false;
        if ((this.loanProduct.isMultiDisburseLoan() || this.loanProduct.canDefineInstallmentAmount()) && emiAmount != null
                && emiAmount.compareTo(retriveLastEmiAmount()) != 0) {
            if (this.loanProduct.isMultiDisburseLoan()) {
                final Date dateValue = null;
                final boolean isSpecificToInstallment = false;
                final Boolean isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled = scheduleGeneratorDTO.isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled();
                Date effectiveDateFrom = actualDisbursementDate.toDate();
                if (!isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled) {
                    effectiveDateFrom = actualDisbursementDate.plusDays(1).toDate();
                }
                LoanTermVariations loanVariationTerms = new LoanTermVariations(LoanTermVariationType.EMI_AMOUNT.getValue(),
                        effectiveDateFrom, emiAmount, dateValue, isSpecificToInstallment, this, LoanStatus.ACTIVE.getValue());
                this.loanTermVariations.add(loanVariationTerms);
            } else {
                this.fixedEmiAmount = emiAmount;
            }
            isEmiAmountChanged = true;
            
        }
        if (rescheduledRepaymentDate != null) {
            final boolean isSpecificToInstallment = false;
            LoanTermVariations loanVariationTerms = new LoanTermVariations(LoanTermVariationType.DUE_DATE.getValue(),
                    nextPossibleRepaymentDate.toDate(), emiAmount, rescheduledRepaymentDate, isSpecificToInstallment, this,
                    LoanStatus.ACTIVE.getValue());
            this.loanTermVariations.add(loanVariationTerms);
        }
       
       

        if (isRepaymentScheduleRegenerationRequiredForDisbursement(actualDisbursementDate) || recalculateSchedule || isEmiAmountChanged
                || rescheduledRepaymentDate != null
                || fetchRepaymentScheduleInstallment(1).getDueDate().isBefore(DateUtils.getLocalDateOfTenant()) || isDisbursementMissed()) {
            boolean generateSchedule = isApproved() || (!isAllPaymentsDone() && getMaturityDate().isAfter(actualDisbursementDate));
            if (generateSchedule) {
            	 final boolean isDisburseInitiated = true;
                 recalculateSchedule(scheduleGeneratorDTO, currentUser, isDisburseInitiated);
                if(this.getFlatInterestRate() != null){
                    this.calculatedInstallmentAmount = scheduleGeneratorDTO.getCalculatedInstallmentAmount();
                }
            }
        }
    }

    private boolean isAllPaymentsDone() {
        boolean paymentsDone = false;
        if (this.loanProduct.isMultiDisburseLoan()) {
            BigDecimal plannedPrincipal = getPlannedDisbursalAmount();
            BigDecimal principalAccounted = this.summary.getAccountedPrincipal();
            if (plannedPrincipal.compareTo(principalAccounted) != 1) {
                paymentsDone = true;
            }
        }
        return paymentsDone;
    }

    public boolean canDisburse(final LocalDate actualDisbursementDate) {
        Date lastDusburseDate = this.actualDisbursementDate;
        final LoanStatus statusEnum = this.loanLifecycleStateMachine.transition(LoanEvent.LOAN_DISBURSED,
                LoanStatus.fromInt(this.loanStatus));
        
        // validate tranche disbursal dates must be unique and it cannot be before last transaction date
        validateTrancheDisbursalDateMustBeUniqueAndNotBeforeLastTransactionDate(actualDisbursementDate);

        boolean isMultiTrancheDisburse = false;
        if (LoanStatus.fromInt(this.loanStatus).isActive() && isAllTranchesNotDisbursed()) {
            LoanDisbursementDetails details = fetchLastDisburseDetail();

            if (details != null) {
                lastDusburseDate = details.actualDisbursementDate();
            }
            if (actualDisbursementDate.toDate().before(lastDusburseDate)) {
                final String errorMsg = "Loan can't be disbursed before " + lastDusburseDate;
                throw new LoanDisbursalException(errorMsg, "actualdisbursementdate.before.lastdusbursedate", lastDusburseDate,
                        actualDisbursementDate.toDate());
            }
            isMultiTrancheDisburse = true;
        }
        return (!statusEnum.hasStateOf(LoanStatus.fromInt(this.loanStatus)) || isMultiTrancheDisburse);
    }

    public Money adjustDisburseAmount(final JsonCommand command, final LocalDate actualDisbursementDate) {
        setDiscountOnDisbursalAmount(command);
        BigDecimal discountOnDisbursalAmount = BigDecimal.ZERO;
        if(getDiscountOnDisburseAmount() != null){
            discountOnDisbursalAmount = getDiscountOnDisburseAmount();
        }
        Money disburseAmount = this.loanRepaymentScheduleDetail.getPrincipal().zero();
        BigDecimal principalDisbursed = command.bigDecimalValueOfParameterNamed(LoanApiConstants.principalDisbursedParameterName);
        if (this.actualDisbursementDate == null) {
            this.actualDisbursementDate = actualDisbursementDate.toDate();
        }
        BigDecimal diff = BigDecimal.ZERO;
        Collection<LoanDisbursementDetails> details = fetchUndisbursedDetail();
        if (principalDisbursed == null) {
            disburseAmount = this.loanRepaymentScheduleDetail.getPrincipal();
            if (!details.isEmpty()) {
                disburseAmount = disburseAmount.zero();
                for (LoanDisbursementDetails disbursementDetails : details) {
                    disbursementDetails.updateActualDisbursementDate(actualDisbursementDate.toDate());
                    disburseAmount = disburseAmount.plus(disbursementDetails.principal());
                }
            }
        } else {
            if (this.loanProduct.isMultiDisburseLoan()) {
                disburseAmount = Money.of(getCurrency(), principalDisbursed);
            } else {
                disburseAmount = disburseAmount.plus(principalDisbursed);
            }

            if (details.isEmpty()) {
                diff = this.loanRepaymentScheduleDetail.getPrincipal().minus(principalDisbursed).getAmount();
            } else {
                for (LoanDisbursementDetails disbursementDetails : details) {
                    disbursementDetails.updateActualDisbursementDate(actualDisbursementDate.toDate());
                    disbursementDetails.updatePrincipal(principalDisbursed);
                    recalculateDisbursementCharge(disbursementDetails, getLoanCharges());
                }
            }
            if (this.loanProduct().isMultiDisburseLoan()) {
                Collection<LoanDisbursementDetails> loanDisburseDetails = this.getDisbursementDetails();
                BigDecimal setPrincipalAmount = BigDecimal.ZERO;
                BigDecimal totalAmount = BigDecimal.ZERO;
                for (LoanDisbursementDetails disbursementDetails : loanDisburseDetails) {
                    if (disbursementDetails.isActive()) {
                        if (disbursementDetails.actualDisbursementDate() != null) {
                            setPrincipalAmount = setPrincipalAmount.add(disbursementDetails.principal());
                        }
                        totalAmount = totalAmount.add(disbursementDetails.principal());
                    }
                }
                this.loanRepaymentScheduleDetail.setPrincipal(setPrincipalAmount);
                if (totalAmount.add(discountOnDisbursalAmount).compareTo(this.approvedPrincipal) == 1) {
                    final String errorMsg = "Loan can't be disbursed,disburse amount is exceeding approved principal ";
                    throw new LoanDisbursalException(errorMsg, "disburse.amount.must.be.less.than.approved.principal", principalDisbursed,
                            this.approvedPrincipal);
                }
            } else {
                this.loanRepaymentScheduleDetail.setPrincipal(this.loanRepaymentScheduleDetail.getPrincipal().minus(diff).getAmount());
            }
            if (!(this.loanProduct().isMultiDisburseLoan()) && diff.subtract(discountOnDisbursalAmount).compareTo(BigDecimal.ZERO) == -1) {
                final String errorMsg = "Loan can't be disbursed,disburse amount is exceeding approved amount ";
                throw new LoanDisbursalException(errorMsg, "disburse.amount.must.be.less.than.approved.amount", principalDisbursed,
                        this.loanRepaymentScheduleDetail.getPrincipal().getAmount());
            }
        }
        return disburseAmount;
    }

    private ChangedTransactionDetail reprocessTransactionForDisbursement() {
        ChangedTransactionDetail changedTransactionDetail = null;
        if (this.loanProduct.isMultiDisburseLoan()) {
            final List<LoanTransaction> allNonContraTransactionsPostDisbursement = retreiveListOfTransactionsPostDisbursement();
            if (!allNonContraTransactionsPostDisbursement.isEmpty()) {
                final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                        .determineProcessor(this.transactionProcessingStrategy);
                changedTransactionDetail = loanRepaymentScheduleTransactionProcessor.handleTransaction(getDisbursementDate(),
                        allNonContraTransactionsPostDisbursement, getCurrency(), this.repaymentScheduleInstallments, charges(), disbursementDetails);
                for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                    mapEntry.getValue().updateLoan(this);
                    this.loanTransactions.add(mapEntry.getValue());
                }

            }
            updateLoanSummaryAndStatus();
        }

        return changedTransactionDetail;
    }

    private Collection<LoanDisbursementDetails> fetchUndisbursedDetail() {
        Collection<LoanDisbursementDetails> disbursementDetails = new ArrayList<>();
        Date date = null;
        for (LoanDisbursementDetails disbursementDetail : this.disbursementDetails) {
            if (disbursementDetail.isActive() && disbursementDetail.actualDisbursementDate() == null) {
                if (date == null || disbursementDetail.expectedDisbursementDate().equals(date)) {
                    disbursementDetails.add(disbursementDetail);
                    date = disbursementDetail.expectedDisbursementDate();
                } else if (disbursementDetail.isActive() && disbursementDetail.expectedDisbursementDate().before(date)) {
                    disbursementDetails.clear();
                    disbursementDetails.add(disbursementDetail);
                    date = disbursementDetail.expectedDisbursementDate();
                }
            }
        }
        return disbursementDetails;
    }
    
    private LoanDisbursementDetails fetchLastDisburseDetail() {
        LoanDisbursementDetails details = null;
        Date date = this.actualDisbursementDate;
        if (date != null) {
            for (LoanDisbursementDetails disbursementDetail : this.disbursementDetails) {
                if (disbursementDetail.isActive() && disbursementDetail.actualDisbursementDate() != null) {
                    if (disbursementDetail.actualDisbursementDate().after(date) || disbursementDetail.actualDisbursementDate().equals(date)) {
                        date = disbursementDetail.actualDisbursementDate();
                        details = disbursementDetail;
                    }
                }
            }
        }
        return details;
    }

    private boolean isDisbursementMissed() {
        boolean isDisbursementMissed = false;
        for (LoanDisbursementDetails disbursementDetail : this.disbursementDetails) {
            if (disbursementDetail.isActive() && disbursementDetail.actualDisbursementDate() == null
                    && DateUtils.getLocalDateOfTenant().isAfter(disbursementDetail.expectedDisbursementDateAsLocalDate())) {
                isDisbursementMissed = true;
                break;
            }
        }
        return isDisbursementMissed;
    }

    public BigDecimal getDisbursedAmount() {
        BigDecimal principal = BigDecimal.ZERO;
        for (LoanDisbursementDetails disbursementDetail : this.disbursementDetails) {
            if (disbursementDetail.isActive() && disbursementDetail.actualDisbursementDate() != null) {
                principal = principal.add(disbursementDetail.principal());
            }
        }
        return principal;
    }

    public BigDecimal getPlannedDisbursalAmount() {
        BigDecimal principal = BigDecimal.ZERO;
        for (LoanDisbursementDetails disbursementDetail : this.disbursementDetails) {
            principal = principal.add(disbursementDetail.principal());
        }
        return principal;
    }
    
    private void removeDisbursementDetail() {
        Set<LoanDisbursementDetails> details = new HashSet<>(this.disbursementDetails);
        for (LoanDisbursementDetails disbursementDetail : details) {
            if (disbursementDetail.isActive() && disbursementDetail.actualDisbursementDate() == null) {
                this.disbursementDetails.remove(disbursementDetail);
            }
        }
    }

    private boolean isDisbursementAllowed() {
        boolean isAllowed = false;
        for (LoanDisbursementDetails disbursementDetail : this.disbursementDetails) {
            if (disbursementDetail.isActive() && disbursementDetail.actualDisbursementDate() == null) {
                isAllowed = true;
                break;
            }
        }
        return isAllowed;
    }

    private boolean atleastOnceDisbursed() {
        boolean isDisbursed = false;
        for (LoanDisbursementDetails disbursementDetail : this.disbursementDetails) {
            if (disbursementDetail.isActive() && disbursementDetail.actualDisbursementDate() != null) {
                isDisbursed = true;
                break;
            }
        }
        return isDisbursed;
    }

    private void updateLoanRepaymentPeriodsDerivedFields(final LocalDate actualDisbursementDate) {

        for (final LoanRepaymentScheduleInstallment repaymentPeriod : this.repaymentScheduleInstallments) {
            repaymentPeriod.updateDerivedFields(loanCurrency(), actualDisbursementDate);
        }
    }

    /*
     * Ability to regenerate the repayment schedule based on the loans current
     * details/state.
     */
    public void regenerateRepaymentSchedule(final ScheduleGeneratorDTO scheduleGeneratorDTO, AppUser currentUser) {

        final LoanScheduleModel loanSchedule = regenerateScheduleModel(scheduleGeneratorDTO);
        if (loanSchedule == null) { return; }
        updateLoanSchedule(loanSchedule, currentUser);
        Set<LoanCharge> charges = this.charges();
        LocalDate lastRepaymentDate = this.getLastRepaymentPeriodDueDate(true);
        for (LoanCharge loanCharge : charges) {
        	if(loanCharge.getDueLocalDate() == null || !lastRepaymentDate.isBefore(loanCharge.getDueLocalDate())){
        		if(!loanCharge.isWaived()){
        			recalculateLoanCharge(loanCharge, scheduleGeneratorDTO.getPenaltyWaitPeriod());
        			}	
        	}else{
        		loanCharge.setActive(false);
        	}
        }
        updateSummaryWithTotalFeeChargesDueAtDisbursement();
        if(!this.isOpen()){
            updateNetAmountForTranches(charges);
        }
    }

    public LoanScheduleModel regenerateScheduleModel(final ScheduleGeneratorDTO scheduleGeneratorDTO) {

        final RoundingMode roundingMode = MoneyHelper.getRoundingMode();
        final MathContext mc = new MathContext(8, roundingMode);

        final InterestMethod interestMethod = this.loanRepaymentScheduleDetail.getInterestMethod();
        //capitalization of charges
        this.setTotalCapitalizedCharges(LoanUtilService.getCapitalizedChargeAmount(this.getLoanCharges()));
        final LoanApplicationTerms loanApplicationTerms = constructLoanApplicationTerms(scheduleGeneratorDTO);
        updateInstallmentAmountForGlim(loanApplicationTerms);
        loanApplicationTerms.updateTotalInterestDueForGlim(this.glimList);        
        final LoanScheduleGenerator loanScheduleGenerator = scheduleGeneratorDTO.getLoanScheduleFactory().create(interestMethod);
        final BigDecimal firstFixedInstallmentEmiAmount = GroupLoanIndividualMonitoringAssembler.calculateGlimFirstInstallmentAmount(loanApplicationTerms);
    	loanApplicationTerms.setFirstFixedEmiAmount(firstFixedInstallmentEmiAmount);
        if (this.loanProduct.adjustFirstEMIAmount() && !this.isOpen()) {
            final BigDecimal firstInstallmentEmiAmount = loanScheduleGenerator.calculateFirstInstallmentAmount(mc, loanApplicationTerms,
                    charges(), scheduleGeneratorDTO.getHolidayDetailDTO());
            loanApplicationTerms.setFirstEmiAmount(firstInstallmentEmiAmount);
            this.firstEmiAmount = firstInstallmentEmiAmount;
            loanApplicationTerms.setAdjustLastInstallmentInterestForRounding(true);
        }else if(this.loanProduct.isAdjustInterestForRounding()){
        	loanApplicationTerms.setAdjustLastInstallmentInterestForRounding(true);
        }
        final LoanScheduleModel loanSchedule = loanScheduleGenerator.generate(mc, loanApplicationTerms, charges(),
                scheduleGeneratorDTO.getHolidayDetailDTO());
        this.calculatedInstallmentAmount = loanApplicationTerms.getFixedEmiAmount();
        return loanSchedule;
    }

    private BigDecimal constructFloatingInterestRates(final BigDecimal annualNominalInterestRate, final FloatingRateDTO floatingRateDTO,
            final List<LoanTermVariationsData> loanTermVariations) {
        final LocalDate dateValue = null;
        final boolean isSpecificToInstallment = false;
        BigDecimal interestRate = annualNominalInterestRate;
        if (loanProduct.isLinkedToFloatingInterestRate()) {
            floatingRateDTO.resetInterestRateDiff();
            Collection<FloatingRatePeriodData> applicableRates = loanProduct.fetchInterestRates(floatingRateDTO);
            LocalDate interestRateStartDate = DateUtils.getLocalDateOfTenant();
            for (FloatingRatePeriodData periodData : applicableRates) {
                LoanTermVariationsData loanTermVariation = new LoanTermVariationsData(
                        LoanEnumerations.loanvariationType(LoanTermVariationType.INTEREST_RATE), periodData.getFromDateAsLocalDate(),
                        periodData.getInterestRate(), dateValue, isSpecificToInstallment);
                if (!interestRateStartDate.isBefore(periodData.getFromDateAsLocalDate())) {
                    interestRateStartDate = periodData.getFromDateAsLocalDate();
                    interestRate = periodData.getInterestRate();
                }
                loanTermVariations.add(loanTermVariation);
            }
        }
        return interestRate;
    }

    private void handleDisbursementTransaction(final LocalDate disbursedOn, final PaymentDetail paymentDetail) {

        // add repayment transaction to track incoming money from client to mfi
        // for (charges due at time of disbursement)

        /***
         * TODO Vishwas: do we need to be able to pass in payment type details
         * for repayments at disbursements too?
         ***/

        final Money totalFeeChargesDueAtDisbursement = this.summary.getTotalFeeChargesDueAtDisbursement(loanCurrency());
        /**
         * all Charges repaid at disbursal is marked as repaid and
         * "APPLY Charge" transactions are created for all other fees ( which
         * are created during disbursal but not repaid)
         **/

        Money disbursentMoney = Money.zero(getCurrency());
        final LoanTransaction chargesPayment = LoanTransaction.repaymentAtDisbursement(getOffice(), disbursentMoney, paymentDetail, disbursedOn,
                null);
        final Integer installmentNumber = null;
        for (final LoanCharge charge : charges()) {
            Date actualDisbursementDate = getActualDisbursementDate(charge);
            // update tax details for loan charge with actual disbursement date
            if(charge.getTaxGroup() != null) {
                charge.updateLoanChargeTaxDetails(disbursedOn, charge.amount());
            }
            if ((charge.getCharge().getChargeTimeType().equals(ChargeTimeType.DISBURSEMENT.getValue())
            		&& disbursedOn.equals(new LocalDate(actualDisbursementDate)) && actualDisbursementDate != null
                    && !charge.isWaived() && !charge.isFullyPaid())
                    || (charge.getCharge().getChargeTimeType().equals(ChargeTimeType.TRANCHE_DISBURSEMENT.getValue())
                            && disbursedOn.equals(new LocalDate(actualDisbursementDate)) && actualDisbursementDate != null
                            && !charge.isWaived() && !charge.isFullyPaid())) {
                if (totalFeeChargesDueAtDisbursement.isGreaterThanZero() && !charge.getChargePaymentMode().isPaymentModeAccountTransfer()) {
                    charge.markAsFullyPaid();
                    // Add "Loan Charge Paid By" details to this transaction
                    final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(chargesPayment, charge, charge.amount(),
                            installmentNumber);
                    chargesPayment.getLoanChargesPaid().add(loanChargePaidBy);
                    disbursentMoney = disbursentMoney.plus(charge.amount());
                }
            }
            
            if (disbursedOn.equals(new LocalDate(this.actualDisbursementDate))) {
                /**
                 * create a Charge applied transaction if Up front Accrual, None
                 * or Cash based accounting is enabled
                 **/
                if (isNoneOrCashOrUpfrontAccrualAccountingEnabledOnLoanProduct()) {
                    handleChargeAppliedTransaction(charge, disbursedOn);
                } else if (charge.isCapitalized() && isPeriodicAccrualAccountingEnabledOnLoanProduct()) {
                    /**
                     * Total capitalized charge Amount Accrual transaction
                     */
                    handleCapitalizedChargeAppliedTransaction(charge, disbursedOn);
                }
            }
        }
        
        if (disbursentMoney.isGreaterThanZero()) {
            final Money zero = Money.zero(getCurrency());
            chargesPayment.updateComponentsAndTotal(zero, zero, disbursentMoney, zero);
            chargesPayment.updateLoan(this);
            this.loanTransactions.add(chargesPayment);
            updateLoanOutstandingBalaces();
        }

        if (getApprovedOnDate() != null && disbursedOn.isBefore(getApprovedOnDate())) {
            final String errorMessage = "The date on which a loan is disbursed cannot be before its approval date: "
                    + getApprovedOnDate().toString();
            throw new InvalidLoanStateTransitionException("disbursal", "cannot.be.before.approval.date", errorMessage, disbursedOn,
                    getApprovedOnDate());
        }

        if (getExpectedFirstRepaymentOnDate() != null
                && (disbursedOn.isAfter(this.fetchRepaymentScheduleInstallment(1).getDueDate()) || disbursedOn
                        .isAfter(getExpectedFirstRepaymentOnDate())) && disbursedOn.toDate().equals(this.actualDisbursementDate)) {
            final String errorMessage = "submittedOnDate cannot be after the loans  expectedFirstRepaymentOnDate: "
                    + getExpectedFirstRepaymentOnDate().toString();
            throw new InvalidLoanStateTransitionException("disbursal", "cannot.be.after.expected.first.repayment.date", errorMessage,
                    disbursedOn, getExpectedFirstRepaymentOnDate());
        }

        validateActivityNotBeforeClientOrGroupTransferDate(LoanEvent.LOAN_DISBURSED, disbursedOn);

        if (disbursedOn.isAfter(DateUtils.getLocalDateOfTenant())) {
            final String errorMessage = "The date on which a loan with identifier : " + this.accountNumber
                    + " is disbursed cannot be in the future.";
            throw new InvalidLoanStateTransitionException("disbursal", "cannot.be.a.future.date", errorMessage, disbursedOn);
        }

    }
    
    private void handleCapitalizedChargeAppliedTransaction(final LoanCharge loanCharge, final LocalDate transactionDate) {
        final Money chargeAmount = loanCharge.getAmount(getCurrency());
        Money feeCharges = chargeAmount;
        Money penaltyCharges = Money.zero(loanCurrency());
        final LoanTransaction applyLoanChargeTransaction = LoanTransaction.accrueLoanCharge(this, getOffice(), chargeAmount,
                transactionDate, feeCharges, penaltyCharges);
        for (final LoanInstallmentCharge loanInstallmentCharge : loanCharge.installmentCharges()) {
            final Money perInstallmentChargeAmount = loanInstallmentCharge.getAmount(getCurrency());
            final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(applyLoanChargeTransaction, loanCharge, Money.of(getCurrency(),
                    perInstallmentChargeAmount.getAmount()).getAmount(), loanInstallmentCharge.getRepaymentInstallment()
                    .getInstallmentNumber());
            applyLoanChargeTransaction.getLoanChargesPaid().add(loanChargePaidBy);
            loanInstallmentCharge.getRepaymentInstallment().setFeeAccrualPortion(perInstallmentChargeAmount);
        }
        this.loanTransactions.add(applyLoanChargeTransaction);
    }

    public LoanTransaction handlePayDisbursementTransaction(final Long chargeId, final LoanTransaction chargesPayment,
            final List<Long> existingTransactionIds, final List<Long> existingReversedTransactionIds) {
        existingTransactionIds.addAll(findExistingTransactionIds());
        existingReversedTransactionIds.addAll(findExistingReversedTransactionIds());
        LoanCharge charge = null;
        for (final LoanCharge loanCharge : this.charges) {
            if (loanCharge.isActive() && chargeId.equals(loanCharge.getId())) {
                charge = loanCharge;
            }
        }
        final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(chargesPayment, charge, charge.amount(), null);
        chargesPayment.getLoanChargesPaid().add(loanChargePaidBy);
        final Money zero = Money.zero(getCurrency());
        chargesPayment.updateComponents(zero, zero, charge.getAmount(getCurrency()), zero);
        chargesPayment.updateLoan(this);
        this.loanTransactions.add(chargesPayment);
        updateLoanOutstandingBalaces();
        charge.markAsFullyPaid();
        return chargesPayment;
    }

    public Map<String, Object> undoDisbursal(final ScheduleGeneratorDTO scheduleGeneratorDTO, final List<Long> existingTransactionIds,
            final List<Long> existingReversedTransactionIds, AppUser currentUser) {

        validateAccountStatus(LoanEvent.LOAN_DISBURSAL_UNDO);
        existingTransactionIds.addAll(findExistingTransactionIds());
        existingReversedTransactionIds.addAll(findExistingReversedTransactionIds());
        final Map<String, Object> actualChanges = new LinkedHashMap<>();
        final LoanStatus currentStatus = LoanStatus.fromInt(this.loanStatus);
        final LoanStatus statusEnum = this.loanLifecycleStateMachine.transition(LoanEvent.LOAN_DISBURSAL_UNDO, currentStatus);
        validateActivityNotBeforeClientOrGroupTransferDate(LoanEvent.LOAN_DISBURSAL_UNDO, getDisbursementDate());
        existingTransactionIds.addAll(findExistingTransactionIds());
        existingReversedTransactionIds.addAll(findExistingReversedTransactionIds());
        if (!statusEnum.hasStateOf(currentStatus)) {
            this.loanStatus = statusEnum.getValue();
            actualChanges.put("status", LoanEnumerations.status(this.loanStatus));

            final LocalDate actualDisbursementDate = getDisbursementDate();
            final boolean isScheduleRegenerateRequired = isRepaymentScheduleRegenerationRequiredForDisbursement(actualDisbursementDate);
            this.actualDisbursementDate = null;
            this.disbursedBy = null;
            this.lastRepaymentDate = null;
            boolean isDisbursedAmountChanged = !this.approvedPrincipal.equals(this.loanRepaymentScheduleDetail.getPrincipal());
            this.loanRepaymentScheduleDetail.setPrincipal(this.approvedPrincipal);
            if (this.loanProduct.isMultiDisburseLoan()) {
                for (final LoanDisbursementDetails details : this.disbursementDetails) {
                	if(details.isActive()){
                    details.updateActualDisbursementDate(null);
                	}
                }
            }
            boolean isEmiAmountChanged = this.loanTermVariations.size() > 0;
            
            updateLoanToPreDisbursalState();
            if (isScheduleRegenerateRequired || isDisbursedAmountChanged || isEmiAmountChanged
                    || this.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
                // clear off actual disbusrement date so schedule regeneration
                // uses expected date.

                regenerateRepaymentSchedule(scheduleGeneratorDTO, currentUser);
                if (isDisbursedAmountChanged) {
                    updateSummaryWithTotalFeeChargesDueAtDisbursement(deriveSumTotalOfChargesDueAtDisbursement());
                }
            }else if(isPeriodicAccrualAccountingEnabledOnLoanProduct()){
                for (final LoanRepaymentScheduleInstallment period : getRepaymentScheduleInstallments()) {
                    period.resetAccrualComponents();
                }
            }

            if(this.isTopup){
                this.loanTopupDetails.setAccountTransferDetails(null);
                this.loanTopupDetails.setTopupAmount(null);
            }

            actualChanges.put("actualDisbursementDate", "");
            this.accruedTill = null;
            reverseExistingTransactions();
            updateLoanSummaryDerivedFields();

        }

        return actualChanges;
    }

    private final void reverseExistingTransactions() {
        Collection<LoanTransaction> retainTransactions = new ArrayList<>();
        for (final LoanTransaction transaction : this.loanTransactions) {
            transaction.reverse();
            if(transaction.getId() != null){
                retainTransactions.add(transaction);
            }
        }
        this.loanTransactions.retainAll(retainTransactions);
    }

    private void updateLoanToPreDisbursalState() {
        this.actualDisbursementDate = null;
        this.accruedTill = null;
        reverseExistingTransactions();

        for (final LoanCharge charge : charges()) {
            if (charge.isOverdueInstallmentCharge()) {
                charge.setActive(false);
            } else {
                charge.resetToOriginal(loanCurrency());
            }
        }

        for (final LoanRepaymentScheduleInstallment currentInstallment : this.repaymentScheduleInstallments) {
            currentInstallment.resetDerivedComponents();
        }
        for (LoanTermVariations variations : this.loanTermVariations) {
            if (variations.getOnLoanStatus().equals(LoanStatus.ACTIVE.getValue())) {
                variations.markAsInactive();
            }
        }
        final LoanRepaymentScheduleProcessingWrapper wrapper = new LoanRepaymentScheduleProcessingWrapper();
        wrapper.reprocess(getCurrency(), getDisbursementDate(), this.repaymentScheduleInstallments, charges(), disbursementDetails);
        this.calculatedInstallmentAmount = null;
        updateLoanSummaryDerivedFields();
    }

    public ChangedTransactionDetail waiveInterest(final LoanTransaction waiveInterestTransaction,
            final LoanLifecycleStateMachine loanLifecycleStateMachine, final List<Long> existingTransactionIds,
            final List<Long> existingReversedTransactionIds, final ScheduleGeneratorDTO scheduleGeneratorDTO, final AppUser currentUser) {

        validateAccountStatus(LoanEvent.WAIVER);

        validateActivityNotBeforeClientOrGroupTransferDate(LoanEvent.LOAN_REPAYMENT_OR_WAIVER,
                waiveInterestTransaction.getTransactionDate());
        validateActivityNotBeforeLastTransactionDate(LoanEvent.LOAN_REPAYMENT_OR_WAIVER, waiveInterestTransaction.getTransactionDate());

        existingTransactionIds.addAll(findExistingTransactionIds());
        existingReversedTransactionIds.addAll(findExistingReversedTransactionIds());

        final ChangedTransactionDetail changedTransactionDetail = handleRepaymentOrRecoveryOrWaiverTransaction(waiveInterestTransaction,
                loanLifecycleStateMachine, null, scheduleGeneratorDTO, currentUser);

        return changedTransactionDetail;
    }

    public ChangedTransactionDetail makeRepayment(final LoanTransaction repaymentTransaction,
            final LoanLifecycleStateMachine loanLifecycleStateMachine, boolean isRecoveryRepayment,
            final ScheduleGeneratorDTO scheduleGeneratorDTO, final AppUser currentUser, Boolean isHolidayValidationDone) {
        HolidayDetailDTO holidayDetailDTO = null;
        LoanEvent event = null;
        
        if (isRecoveryRepayment) {
            event = LoanEvent.LOAN_RECOVERY_PAYMENT;
        } else {
            event = LoanEvent.LOAN_REPAYMENT_OR_WAIVER;
        }
        if (!isHolidayValidationDone) {
            holidayDetailDTO = scheduleGeneratorDTO.getHolidayDetailDTO();
        }        
        if(!(this.isGLIMLoan() && isRecoveryRepayment)){
			if (isRecoveryRepayment) {
				validateAccountStatus(event);
			} else {
				if (repaymentTransaction.getTypeOf().getValue().equals(LoanTransactionType.REPAYMENT.getValue())) {
					validateAccountStatus(LoanEvent.LOAN_REPAYMENT);
				} else {
					validateAccountStatus(LoanEvent.WAIVER);
				}
			}
        }        
        validateActivityNotBeforeClientOrGroupTransferDate(event, repaymentTransaction.getTransactionDate());
        validateActivityNotBeforeLastTransactionDate(event, repaymentTransaction.getTransactionDate());
        if(repaymentTransaction.getTransactionSubTye().isPrePayment()){
            validatePrepayment(repaymentTransaction);
        }
        if (!isHolidayValidationDone) {
            validateRepaymentDateIsOnHoliday(repaymentTransaction.getTransactionDate(), holidayDetailDTO.isAllowTransactionsOnHoliday(),
                    holidayDetailDTO.getHolidays());
            validateRepaymentDateIsOnNonWorkingDay(repaymentTransaction.getTransactionDate(), holidayDetailDTO.getWorkingDays(),
                    holidayDetailDTO.isAllowTransactionsOnNonWorkingDay());
        }

        final ChangedTransactionDetail changedTransactionDetail = handleRepaymentOrRecoveryOrWaiverTransaction(repaymentTransaction,
                loanLifecycleStateMachine, null, scheduleGeneratorDTO, currentUser);

        return changedTransactionDetail;
    }
    
    public ChangedTransactionDetail addOrRevokeSubsidyTransaction(final LoanTransaction subsidyTransaction,
            final ScheduleGeneratorDTO scheduleGeneratorDTO, final AppUser currentUser, LoanEvent event) {

        validateActivityNotBeforeLastTransactionDate(event, subsidyTransaction.getTransactionDate());
        loanTransactions.add(subsidyTransaction);

        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();

        ChangedTransactionDetail changedTransactionDetail = recalculateScheduleFromLastTransaction(scheduleGeneratorDTO,
                existingTransactionIds, existingReversedTransactionIds, currentUser);

        return changedTransactionDetail;
    }

    public void makeChargePayment(final Long chargeId, final LoanLifecycleStateMachine loanLifecycleStateMachine,
            final List<Long> existingTransactionIds, final List<Long> existingReversedTransactionIds,
            final HolidayDetailDTO holidayDetailDTO, final LoanTransaction paymentTransaction, final Integer installmentNumber) {

        validateAccountStatus(LoanEvent.LOAN_CHARGE_PAYMENT);
        validateActivityNotBeforeClientOrGroupTransferDate(LoanEvent.LOAN_CHARGE_PAYMENT, paymentTransaction.getTransactionDate());
        validateActivityNotBeforeLastTransactionDate(LoanEvent.LOAN_CHARGE_PAYMENT, paymentTransaction.getTransactionDate());
        validateRepaymentDateIsOnHoliday(paymentTransaction.getTransactionDate(), holidayDetailDTO.isAllowTransactionsOnHoliday(),
                holidayDetailDTO.getHolidays());
        validateRepaymentDateIsOnNonWorkingDay(paymentTransaction.getTransactionDate(), holidayDetailDTO.getWorkingDays(),
                holidayDetailDTO.isAllowTransactionsOnNonWorkingDay());

        if (paymentTransaction.getTransactionDate().isAfter(DateUtils.getLocalDateOfTenant())) {
            final String errorMessage = "The date on which a loan charge paid cannot be in the future.";
            throw new InvalidLoanStateTransitionException("charge.payment", "cannot.be.a.future.date", errorMessage,
                    paymentTransaction.getTransactionDate());
        }
        existingTransactionIds.addAll(findExistingTransactionIds());
        existingReversedTransactionIds.addAll(findExistingReversedTransactionIds());
        LoanCharge charge = null;
        for (final LoanCharge loanCharge : this.charges) {
            if (loanCharge.isActive() && chargeId.equals(loanCharge.getId())) {
                charge = loanCharge;
            }
        }
        handleChargePaidTransaction(charge, paymentTransaction, loanLifecycleStateMachine, installmentNumber);
    }

    public void makeRefund(final LoanTransaction loanTransaction, final LoanLifecycleStateMachine loanLifecycleStateMachine,
            final List<Long> existingTransactionIds, final List<Long> existingReversedTransactionIds,
            final boolean allowTransactionsOnHoliday, final List<Holiday> holidays, final WorkingDays workingDays,
            final boolean allowTransactionsOnNonWorkingDay) {

        validateRepaymentDateIsOnHoliday(loanTransaction.getTransactionDate(), allowTransactionsOnHoliday, holidays);
        validateRepaymentDateIsOnNonWorkingDay(loanTransaction.getTransactionDate(), workingDays, allowTransactionsOnNonWorkingDay);

        existingTransactionIds.addAll(findExistingTransactionIds());
        existingReversedTransactionIds.addAll(findExistingReversedTransactionIds());

        if (status().isOverpaid()) {
            if (this.totalOverpaid.compareTo(loanTransaction.getAmount(getCurrency()).getAmount()) == -1) {
                final String errorMessage = "The refund amount must be less than or equal to overpaid amount ";
                throw new InvalidLoanStateTransitionException("transaction", "is.exceeding.overpaid.amount", errorMessage,
                        this.totalOverpaid, loanTransaction.getAmount(getCurrency()).getAmount());
            } else if (!isAfterLatRepayment(loanTransaction, this.loanTransactions)) {
                final String errorMessage = "Transfer funds is allowed only after last repayment date";
                throw new InvalidLoanStateTransitionException("transaction", "is.not.after.repayment.date", errorMessage);
            }
        } else {
            final String errorMessage = "Transfer funds is allowed only for loan accounts with overpaid status ";
            throw new InvalidLoanStateTransitionException("transaction", "is.not.a.overpaid.loan", errorMessage);
        }
        loanTransaction.updateLoan(this);

        if (loanTransaction.isNotZero(loanCurrency())) {
            this.loanTransactions.add(loanTransaction);
        }
        updateLoanSummaryDerivedFields();
        doPostLoanTransactionChecks(loanTransaction.getTransactionDate(), loanLifecycleStateMachine);
    }

    private ChangedTransactionDetail handleRepaymentOrRecoveryOrWaiverTransaction(final LoanTransaction loanTransaction,
            final LoanLifecycleStateMachine loanLifecycleStateMachine, final LoanTransaction adjustedTransaction,
            final ScheduleGeneratorDTO scheduleGeneratorDTO, final AppUser currentUser) {
        
        reverseExistingUnaccountableRefundTransactions();
        ChangedTransactionDetail changedTransactionDetail = null;

        LoanStatus statusEnum = null;

        LocalDate recalculateFrom = loanTransaction.getTransactionDate();
        if (adjustedTransaction != null && adjustedTransaction.getTransactionDate().isBefore(recalculateFrom)) {
            recalculateFrom = adjustedTransaction.getTransactionDate();
        }

        if (loanTransaction.isRecoveryRepayment()) {
            statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_RECOVERY_PAYMENT, LoanStatus.fromInt(this.loanStatus));
        } else {
            statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_REPAYMENT_OR_WAIVER, LoanStatus.fromInt(this.loanStatus));
        }
        if(!(this.isGLIMLoan() && loanTransaction.isRecoveryRepayment())){
        	this.loanStatus = statusEnum.getValue();
        }        

        loanTransaction.updateLoan(this);

        final boolean isTransactionChronologicallyLatest = isChronologicallyLatestRepaymentOrWaiver(loanTransaction, this.loanTransactions);

        if (loanTransaction.isNotZero(loanCurrency())) {
            this.loanTransactions.add(loanTransaction);
        }

        if (loanTransaction.isNotRepayment() && loanTransaction.isNotWaiver() && loanTransaction.isNotRecoveryRepayment()) {
            final String errorMessage = "A transaction of type repayment or recovery repayment or waiver was expected but not received.";
            throw new InvalidLoanTransactionTypeException("transaction", "is.not.a.repayment.or.waiver.or.recovery.transaction",
                    errorMessage);
        }

        final LocalDate loanTransactionDate = loanTransaction.getTransactionDate();
        if (loanTransactionDate.isBefore(getDisbursementDate())) {
            final String errorMessage = "The transaction date cannot be before the loan disbursement date: "
                    + getApprovedOnDate().toString();
            throw new InvalidLoanStateTransitionException("transaction", "cannot.be.before.disbursement.date", errorMessage,
                    loanTransactionDate, getDisbursementDate());
        }

        if (loanTransactionDate.isAfter(DateUtils.getLocalDateOfTenant())) {
            final String errorMessage = "The transaction date cannot be in the future.";
            throw new InvalidLoanStateTransitionException("transaction", "cannot.be.a.future.date", errorMessage, loanTransactionDate);
        }

        if (loanTransaction.isInterestWaiver()) {
            Money totalInterestOutstandingOnLoan = getTotalInterestOutstandingOnLoan();
            if (adjustedTransaction != null) {
                totalInterestOutstandingOnLoan = totalInterestOutstandingOnLoan.plus(adjustedTransaction.getAmount(loanCurrency()));
            }
            if (loanTransaction.getAmount(loanCurrency()).isGreaterThan(totalInterestOutstandingOnLoan)) {
                final String errorMessage = "The amount of interest to waive cannot be greater than total interest outstanding on loan.";
                throw new InvalidLoanStateTransitionException("waive.interest", "amount.exceeds.total.outstanding.interest", errorMessage,
                        loanTransaction.getAmount(loanCurrency()), totalInterestOutstandingOnLoan.getAmount());
            }
        }

        if (this.loanProduct.isMultiDisburseLoan() && adjustedTransaction == null) {
            BigDecimal totalDisbursed = getDisbursedAmount();
            if (totalDisbursed.compareTo(this.summary.getTotalPrincipalRepaid()) < 0 && !this.loanProduct.allowNegativeLoanBalances()) {
                final String errorMessage = "The transaction cannot be done before the loan disbursement: "
                        + getApprovedOnDate().toString();
                throw new InvalidLoanStateTransitionException("transaction", "cannot.be.done.before.disbursement", errorMessage);
            }
        }

        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                .determineProcessor(this.transactionProcessingStrategy);

        final LoanRepaymentScheduleInstallment currentInstallment = fetchLoanRepaymentScheduleInstallment(loanTransaction
                .getTransactionDate());
        boolean reprocess = true;

        if (isTransactionChronologicallyLatest && adjustedTransaction == null && !isFullProcessingRequired(loanTransaction)
                && loanTransaction.getTransactionDate().isEqual(DateUtils.getLocalDateOfTenant()) && currentInstallment != null
                && currentInstallment.getTotalOutstanding(getCurrency()).isEqualTo(loanTransaction.getAmount(getCurrency()))) {
            reprocess = false;
        }

        if (isTransactionChronologicallyLatest && adjustedTransaction == null && !isFullProcessingRequired(loanTransaction)
                && (!reprocess || !this.repaymentScheduleDetail().isInterestRecalculationEnabled())) {
            loanRepaymentScheduleTransactionProcessor.handleTransaction(loanTransaction, getCurrency(), this.repaymentScheduleInstallments,
                    charges());
            reprocess = false;
            if (this.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
                if (currentInstallment == null || currentInstallment.isNotFullyPaidOff()) {
                    reprocess = true;
                } else {
                    final LoanRepaymentScheduleInstallment nextInstallment = fetchRepaymentScheduleInstallment(currentInstallment
                            .getInstallmentNumber() + 1);
                    if (nextInstallment != null && nextInstallment.getTotalPaidInAdvance(getCurrency()).isGreaterThanZero()) {
                        reprocess = true;
                    }
                }
            }
        }
        if (this.isGLIMLoan() && adjustedTransaction != null) {
            reprocess = adjustGlimTransactions(loanTransaction, adjustedTransaction);
        } 
        
        if (reprocess) {
            if (adjustedTransaction != null) {
                for (LoanChargePaidBy chargesPaidBy : adjustedTransaction.getLoanChargesPaid()) {
                    if (chargesPaidBy.getLoanCharge().isInstalmentFee() && chargesPaidBy.getLoanCharge().isWaived()) {
                        chargesPaidBy.getLoanCharge().undoWaive();
                    }
                }
            }
            if (!loanTransaction.isRecoveryRepayment() && this.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
                regenerateRepaymentScheduleWithInterestRecalculation(scheduleGeneratorDTO, currentUser);
            }
            final List<LoanTransaction> allNonContraTransactionsPostDisbursement = retreiveListOfTransactionsPostDisbursement();
            changedTransactionDetail = loanRepaymentScheduleTransactionProcessor.handleTransaction(getDisbursementDate(),
                    allNonContraTransactionsPostDisbursement, getCurrency(), this.repaymentScheduleInstallments, charges(), disbursementDetails);
            for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                mapEntry.getValue().updateLoan(this);
            }
            /***
             * Commented since throwing exception if external id present for one
             * of the transactions. for this need to save the reversed
             * transactions first and then new transactions.
             */
            this.loanTransactions.addAll(changedTransactionDetail.getNewTransactionMappings().values());
        }

        updateLoanSummaryDerivedFields();
        
        updateLoanStatusBasedOnProductConfig(loanTransactionDate);
        
        this.lastRepaymentDate = getLastRepaymentDate().toDate();
      
        /**
         * FIXME: Vishwas, skipping post loan transaction checks for Loan
         * recoveries
         **/
        if(this.isGLIMLoan() && loanTransaction.getOverPaymentPortion()!= null && loanTransaction.getOverPaymentPortion().compareTo(BigDecimal.ZERO)>0){
            this.processIncomeAccrualTransactionOnLoanClosure(loanTransaction.getTransactionDate());
        }else if (loanTransaction.isNotRecoveryRepayment()) {
            doPostLoanTransactionChecks(loanTransaction.getTransactionDate(), loanLifecycleStateMachine);
        }
        

        if (this.loanProduct.isMultiDisburseLoan()) {
            BigDecimal totalDisbursed = getDisbursedAmount();
            if (totalDisbursed.compareTo(this.summary.getTotalPrincipalRepaid()) < 0
                    && this.repaymentScheduleDetail().getPrincipal().minus(totalDisbursed).isGreaterThanZero()) {
                final String errorMessage = "The transaction cannot be done before the loan disbursement: "
                        + getApprovedOnDate().toString();
                throw new InvalidLoanStateTransitionException("transaction", "cannot.be.done.before.disbursement", errorMessage);
            }
        }

        if (changedTransactionDetail != null) {
            this.loanTransactions.removeAll(changedTransactionDetail.getNewTransactionMappings().values());
        }
        return changedTransactionDetail;
    }
/*
 * 
 * adjust glim transactions
 */
    private boolean adjustGlimTransactions(final LoanTransaction loanTransaction, final LoanTransaction adjustedTransaction) {
        boolean reprocess;
        List<LoanRepaymentScheduleInstallment> updatePartlyPaidOrPaidInstallments = new ArrayList<>();
        Set<GroupLoanIndividualMonitoringTransaction> removeGlimTransactions = new HashSet<>();
        Set<GroupLoanIndividualMonitoringTransaction> glimTransactions = adjustedTransaction.getGlimTransaction();
        for (LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
            if (installment.isPartlyPaid() || installment.isObligationsMet()) {
                updatePartlyPaidOrPaidInstallments.add(installment);
            }
        }
        Map<Long, BigDecimal> chargeAmountMap = new HashMap<>();
        for (LoanCharge loanCharge : charges()) {
            chargeAmountMap.put(loanCharge.getCharge().getId(), BigDecimal.ZERO);
        }
        //each glim  transaction have one glim member's transaction details
        for (GroupLoanIndividualMonitoringTransaction glimTransaction : glimTransactions) {
            
            if (MathUtility.isGreaterThanZero(glimTransaction.getTotalAmount())) {
                GroupLoanIndividualMonitoring glimMember = glimTransaction.getGroupLoanIndividualMonitoring();                  
                BigDecimal feeAmount = glimTransaction.getFeePortion();
                BigDecimal interestAmount = glimTransaction.getInterestPortion();
                BigDecimal principalAmount = glimTransaction.getPrincipalPortion();
                BigDecimal overPaidAmount = glimTransaction.getOverpaidAmount();
                BigDecimal totalPaidAmount = MathUtility.add(glimMember.getPaidPrincipalAmount(), glimMember.getPaidInterestAmount(),
                        glimMember.getPaidChargeAmount(),glimMember.getOverpaidAmount());
                BigDecimal totalWaivedAmount = MathUtility
                        .add(glimMember.getWaivedInterestAmount(), glimMember.getWaivedChargeAmount());
                BigDecimal totalWrittenOffAmount = MathUtility.add(glimMember.getPrincipalWrittenOffAmount(),
                        glimMember.getInterestWrittenOffAmount(), glimMember.getChargeWrittenOffAmount());
                BigDecimal totalAmount = MathUtility.add(totalPaidAmount, totalWaivedAmount, totalWrittenOffAmount);
                BigDecimal totalDueAmountPerClient = MathUtility.add(glimMember.getDisbursedAmount(), glimMember.getInterestAmount(),
                        glimMember.getChargeAmount());
                Money transactionAmountPerClient = Money.of(getCurrency(), glimTransaction.getTotalAmount());
                Map<String, BigDecimal> processedTransactionMap = new HashMap<>();
                processedTransactionMap.put("processedCharge", BigDecimal.ZERO);
                processedTransactionMap.put("processedInterest", BigDecimal.ZERO);
                processedTransactionMap.put("processedPrincipal", BigDecimal.ZERO);
                processedTransactionMap.put("processedOverPaidAmount", BigDecimal.ZERO);
                processedTransactionMap.put("processedinstallmentTransactionAmount", BigDecimal.ZERO);

                Map<String, BigDecimal> installmentPaidMap = new HashMap<>();
                installmentPaidMap.put("unpaidCharge", BigDecimal.ZERO);
                installmentPaidMap.put("unpaidInterest", BigDecimal.ZERO);
                installmentPaidMap.put("unpaidPrincipal", BigDecimal.ZERO);
                installmentPaidMap.put("unprocessedOverPaidAmount", BigDecimal.ZERO);
                installmentPaidMap.put("installmentTransactionAmount", BigDecimal.ZERO);
                Map<Long, BigDecimal> glimChargeAmountMap = new HashMap<>();
                for (GroupLoanIndividualMonitoringCharge glimloanCharge : glimMember.getGroupLoanIndividualMonitoringCharges()) {
                    glimChargeAmountMap.put(glimloanCharge.getCharge().getId(), BigDecimal.ZERO);
                }
                //update adjusted repayment schedule
                for (LoanRepaymentScheduleInstallment updatePartlyPaidOrPaidInstallment : updatePartlyPaidOrPaidInstallments) {

                    if (transactionAmountPerClient.isGreaterThanZero()) {

                    Map<String, BigDecimal> paidInstallmentMap = GroupLoanIndividualMonitoringTransactionAssembler.getSplit(glimMember,
                            transactionAmountPerClient.getAmount(), this, updatePartlyPaidOrPaidInstallment.getInstallmentNumber(),
                            installmentPaidMap, adjustedTransaction, glimTransaction);

                        if (!(MathUtility.isZero(paidInstallmentMap.get("installmentTransactionAmount")))) {
                            Map<String, BigDecimal> splitMap = GroupLoanIndividualMonitoringTransactionAssembler.getSplit(glimMember,
                                    transactionAmountPerClient.getAmount(), this,
                                    updatePartlyPaidOrPaidInstallment.getInstallmentNumber(), installmentPaidMap, adjustedTransaction,
                                    glimTransaction);
                            Money feePortion = Money.of(getCurrency(), splitMap.get("unpaidCharge"));
                            Money interestPortion = Money.of(getCurrency(), splitMap.get("unpaidInterest"));
                            Money principalPortion = Money.of(getCurrency(), splitMap.get("unpaidPrincipal"));
                            Money overPaidPortion = Money.of(getCurrency(), splitMap.get("unprocessedOverPaidAmount"));
                            Money totalAmountForCurrentInstallment = Money.of(getCurrency(),
                                    splitMap.get("installmentTransactionAmount"));

                            processedTransactionMap.put("processedCharge",
                                    processedTransactionMap.get("processedCharge").add(feePortion.getAmount()));
                            processedTransactionMap.put("processedInterest",
                                    processedTransactionMap.get("processedInterest").add(interestPortion.getAmount()));
                            processedTransactionMap.put("processedPrincipal",
                                    processedTransactionMap.get("processedPrincipal").add(principalPortion.getAmount()));
                            processedTransactionMap.put("processedOverPaidAmount",
                                    processedTransactionMap.get("processedOverPaidAmount").add(overPaidPortion.getAmount()));
                            processedTransactionMap.put(
                                    "processedinstallmentTransactionAmount",
                                    processedTransactionMap.get("processedinstallmentTransactionAmount").add(
                                            totalAmountForCurrentInstallment.getAmount()));

                            transactionAmountPerClient = transactionAmountPerClient.minus(totalAmountForCurrentInstallment);

                            installmentPaidMap.put("unpaidCharge", processedTransactionMap.get("processedCharge"));
                            installmentPaidMap.put("unpaidInterest", processedTransactionMap.get("processedInterest"));
                            installmentPaidMap.put("unpaidPrincipal", processedTransactionMap.get("processedPrincipal"));
                            installmentPaidMap.put("unprocessedOverPaidAmount", processedTransactionMap.get("processedOverPaidAmount"));
                            installmentPaidMap.put("installmentTransactionAmount",
                                    processedTransactionMap.get("processedinstallmentTransactionAmount"));

                            updatePartlyPaidOrPaidInstallment.updateFeeChargesPaid(feePortion.getAmount());
                            updatePartlyPaidOrPaidInstallment.updateInterestPaid(interestPortion.getAmount());
                            updatePartlyPaidOrPaidInstallment.updatePrincipalCompleted(principalPortion.getAmount());
                            updatePartlyPaidOrPaidInstallment.markAsIncomplete();
                            updatePartlyPaidOrPaidInstallment.resetTotalPaidAmount();
                            //update charge for per installment
                            if (MathUtility.isNullOrZero(glimMember.getWaivedChargeAmount())) {
                                updateGlimInstallmentCharge(glimMember, glimChargeAmountMap, updatePartlyPaidOrPaidInstallment, feePortion);
                            }
                            if (transactionAmountPerClient.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                                break;
                            }
                        }

                    }

                }
                // calculate total glim charges
                for (Long chargeId : glimChargeAmountMap.keySet()) {
                    for (GroupLoanIndividualMonitoringCharge glimloanCharge : glimMember.getGroupLoanIndividualMonitoringCharges()) {
                        if (chargeId == glimloanCharge.getCharge().getId()) {
                            BigDecimal amount = MathUtility.subtract(
                                    MathUtility.add(chargeAmountMap.get(chargeId), glimChargeAmountMap.get(chargeId)));
                            chargeAmountMap.put(chargeId, amount);
                        }
                    }
                }
                // undo prepayment and mark glim member as active
                if (totalDueAmountPerClient.compareTo(totalAmount) == 0) {
                    glimMember.setIsActive(true);
                }
                //update glim member adjusted amounts
                updateAdjustedGlimMember(glimTransaction, glimMember, feeAmount, interestAmount, principalAmount, overPaidAmount);

                removeGlimTransactions.add(glimTransaction);
        	}
        }
        
        undoGlimLoanChargesPaid(chargeAmountMap, charges(), null);

        if (!removeGlimTransactions.isEmpty()) {
            glimTransactions.removeAll(removeGlimTransactions);
        }
        if (MathUtility.isZero(loanTransaction.getAmount(getCurrency()).getAmount())) {
            reprocess = false;
        } else {
            reprocess = true;
        }
        return reprocess;
    }

    private void updateAdjustedGlimMember(GroupLoanIndividualMonitoringTransaction glimTransaction,
            GroupLoanIndividualMonitoring glimMember, BigDecimal feeAmount, BigDecimal interestAmount, BigDecimal principalAmount, BigDecimal overPaidAmount) {
        glimMember.setPaidChargeAmount(MathUtility.subtract(glimMember.getPaidChargeAmount(),feeAmount));
        glimMember.setPaidInterestAmount(MathUtility.subtract(glimMember.getPaidInterestAmount(),interestAmount));
        glimMember.setPaidPrincipalAmount(MathUtility.subtract(glimMember.getPaidPrincipalAmount(),principalAmount));
        glimMember.setPaidAmount(MathUtility.subtract(glimMember.getTotalPaidAmount(),glimTransaction.getTotalAmount()));
        glimMember.setOverpaidAmount(MathUtility.subtract(glimMember.getOverpaidAmount(), overPaidAmount));
    }
    
    public BigDecimal getEarlierOverPaidAmount(Long glimId){
        for (GroupLoanIndividualMonitoring glim : this.defaultGlimMembers) {
            if (glimId.equals(glim.getId())) {
                return MathUtility.zeroIfNull(glim.getOverpaidAmount());
            }
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * update glim installment charge
     * @param glimMember
     * @param glimChargeAmountMap
     * @param updatePartlyPaidOrPaidInstallment
     * @param feePortion
     */
    private void updateGlimInstallmentCharge(GroupLoanIndividualMonitoring glimMember, Map<Long, BigDecimal> glimChargeAmountMap,
            LoanRepaymentScheduleInstallment updatePartlyPaidOrPaidInstallment, Money feePortion) {
        for (LoanCharge loanCharge : charges()) {
            for (GroupLoanIndividualMonitoringCharge glimCharge : glimMember
                    .getGroupLoanIndividualMonitoringCharges()) {
                if (loanCharge.getCharge().getId() == glimCharge.getCharge().getId()) {
                    int currentInstallmentNumber = updatePartlyPaidOrPaidInstallment.getInstallmentNumber();
                    BigDecimal amount = glimCharge.getCharge().isEmiRoundingGoalSeek() ? glimCharge
                            .getRevisedFeeAmount() : glimCharge.getFeeAmount();
                    BigDecimal installmentAmount = MathUtility.getInstallmentAmount(amount,
                            this.fetchNumberOfInstallmensAfterExceptions(), getCurrency(),
                            currentInstallmentNumber);
                    BigDecimal amountToBePaidInCurrentInstallment = BigDecimal.ZERO;
                    if (ChargeTimeType.fromInt(glimCharge.getCharge().getChargeTimeType().intValue())
                            .isUpfrontFee()
                            && currentInstallmentNumber == ChargesApiConstants.applyUpfrontFeeOnFirstInstallment) {
                        amountToBePaidInCurrentInstallment = amount;
                    } else {
                        if (currentInstallmentNumber != this.fetchNumberOfInstallmensAfterExceptions()) {
                            amountToBePaidInCurrentInstallment = installmentAmount;
                        } else {
                            BigDecimal defaultInstallmentAmount = MathUtility.getInstallmentAmount(amount,
                                    this.fetchNumberOfInstallmensAfterExceptions(), getCurrency(), 1);
                            amountToBePaidInCurrentInstallment = MathUtility.subtract(amount, MathUtility
                                    .multiply(defaultInstallmentAmount, currentInstallmentNumber - 1));

                        }
                    }

                    if (MathUtility.isGreaterThanZero(amountToBePaidInCurrentInstallment)) {
                        if (MathUtility.isEqualOrGreater(feePortion.getAmount(), amountToBePaidInCurrentInstallment)) {
                            glimCharge.setPaidCharge(MathUtility.subtract(glimCharge.getPaidCharge(), amountToBePaidInCurrentInstallment));
                            glimChargeAmountMap.put(glimCharge.getCharge().getId(), MathUtility
                                    .add(glimChargeAmountMap.get(glimCharge.getCharge().getId()).add(
                                            amountToBePaidInCurrentInstallment)));
                            feePortion = Money.of(getCurrency(),
                                    feePortion.getAmount().subtract(amountToBePaidInCurrentInstallment));
                        } else {
                            glimCharge.setPaidCharge(MathUtility.subtract(glimCharge.getPaidCharge()
                                    ,feePortion.getAmount()));
                            glimChargeAmountMap.put(glimCharge.getCharge().getId(), MathUtility
                                    .add(glimChargeAmountMap.get(glimCharge.getCharge().getId()).add(
                                            feePortion.getAmount())));
                            feePortion = Money.of(getCurrency(),
                                    MathUtility.subtract(feePortion.getAmount(),feePortion.getAmount()));
                        }
                    }
                }
            }
        }
    }
    
    private boolean isFullProcessingRequired(final LoanTransaction loanTransaction){
        return isForeclosure() || loanTransaction.getTransactionSubTye().isPrePayment();
    }

    private void undoGlimLoanChargesPaid(Map<Long, BigDecimal> chargeAmountMap, final Set<LoanCharge> charges,
            final Integer installmentNumber) {
        for (LoanCharge loanCharge : charges) {
            for (Long chargeId : chargeAmountMap.keySet()) {
                if (chargeId == loanCharge.getCharge().getId()) {
                    Money amountRemaining = Money.of(getCurrency(), chargeAmountMap.get(chargeId));
                    Set<LoanCharge> chargeSet = new HashSet<>();
                    chargeSet.add(loanCharge);
                    while (amountRemaining.isGreaterThanZero()) {
                        final LoanCharge unpaidCharge = findLatestPaidChargeFromUnOrderedSet(chargeSet, getCurrency());
                        Money feeAmount = Money.zero(getCurrency());
                        if (unpaidCharge == null) break;
                        final Money amountDeductedTowardsCharge = unpaidCharge.undoGlimPaidOrPartiallyAmountBy(amountRemaining,
                                installmentNumber, feeAmount);
                        amountRemaining = amountRemaining.minus(amountDeductedTowardsCharge);
                    }
                }
            }
        }
    }
    
    private LoanCharge findLatestPaidChargeFromUnOrderedSet(final Set<LoanCharge> charges, MonetaryCurrency currency) {
        LoanCharge latestPaidCharge = null;
        LoanCharge installemntCharge = null;
        LoanInstallmentCharge chargePerInstallment = null;
        for (final LoanCharge loanCharge : charges) {
            boolean isPaidOrPartiallyPaid = loanCharge.isPaidOrPartiallyPaid(currency);
            if (isPaidOrPartiallyPaid && !loanCharge.isDueAtDisbursement()) {
                if (loanCharge.isInstalmentFee()) {
                    LoanInstallmentCharge paidLoanChargePerInstallment = loanCharge
                            .getLastPaidOrPartiallyPaidInstallmentLoanChargeForGlim(currency);
                    if (chargePerInstallment == null
                            || (paidLoanChargePerInstallment != null && chargePerInstallment.getRepaymentInstallment().getDueDate()
                                    .isBefore(paidLoanChargePerInstallment.getRepaymentInstallment().getDueDate()))) {
                        installemntCharge = loanCharge;
                        chargePerInstallment = paidLoanChargePerInstallment;
                    }
                } else if (latestPaidCharge == null || (loanCharge.isPaidOrPartiallyPaid(currency))
                        && loanCharge.getDueLocalDate().isAfter(latestPaidCharge.getDueLocalDate())) {
                    latestPaidCharge = loanCharge;
                }
            }
        }
        if (latestPaidCharge == null
                || (chargePerInstallment != null && latestPaidCharge.getDueLocalDate().isAfter(
                        chargePerInstallment.getRepaymentInstallment().getDueDate()))) {
            latestPaidCharge = installemntCharge;
        }

        return latestPaidCharge;
    }

    private LoanRepaymentScheduleInstallment fetchLoanRepaymentScheduleInstallment(LocalDate dueDate) {
        LoanRepaymentScheduleInstallment installment = null;
        for (LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment : this.repaymentScheduleInstallments) {
            if (dueDate.equals(loanRepaymentScheduleInstallment.getDueDate())) {
                installment = loanRepaymentScheduleInstallment;
                break;
            }
        }
        return installment;
    }

    private List<LoanTransaction> retreiveListOfIncomePostingTransactions() {
        final List<LoanTransaction> incomePostTransactions = new ArrayList<>();
        for (final LoanTransaction transaction : this.loanTransactions) {
            if (transaction.isNotReversed() && transaction.isIncomePosting()) {
                incomePostTransactions.add(transaction);
            }
        }
        final LoanTransactionComparator transactionComparator = new LoanTransactionComparator();
        Collections.sort(incomePostTransactions, transactionComparator);
        return incomePostTransactions;
    }

    private List<LoanTransaction> retreiveListOfTransactionsPostDisbursement() {
        final List<LoanTransaction> repaymentsOrWaivers = new ArrayList<>();
        for (final LoanTransaction transaction : this.loanTransactions) {
            if (transaction.isNotReversed()
                    && !(transaction.isDisbursement() || transaction.isNonMonetaryTransaction() || transaction
                            .isBrokenPeriodInterestPosting())) {
                repaymentsOrWaivers.add(transaction);
            }
        }
        final LoanTransactionComparator transactionComparator = new LoanTransactionComparator();
        Collections.sort(repaymentsOrWaivers, transactionComparator);
        return repaymentsOrWaivers;
    }

    public List<LoanTransaction> retreiveListOfTransactionsPostDisbursementExcludeAccruals() {
        final List<LoanTransaction> repaymentsOrWaivers = new ArrayList<>();
        for (final LoanTransaction transaction : this.loanTransactions) {
            if (transaction.isNotReversed()
                    && !(transaction.isDisbursement() || transaction.isAccrual() || transaction.isRepaymentAtDisbursement()
                            || transaction.isNonMonetaryTransaction() || transaction.isIncomePosting() || transaction
                                .isBrokenPeriodInterestPosting())) {
                repaymentsOrWaivers.add(transaction);
            }
        }
        final LoanTransactionComparator transactionComparator = new LoanTransactionComparator();
        Collections.sort(repaymentsOrWaivers, transactionComparator);
        return repaymentsOrWaivers;
    }
    
    public List<LoanTransaction> retreiveCopyOfTransactionsPostDisbursementExcludeAccruals() {
        final List<LoanTransaction> repaymentsOrWaivers = new ArrayList<>();
        for (final LoanTransaction transaction : this.loanTransactions) {
            if (transaction.isNotReversed()
                    && !(transaction.isDisbursement() || transaction.isAccrual() || transaction.isRepaymentAtDisbursement()
                            || transaction.isNonMonetaryTransaction() || transaction.isIncomePosting() || transaction
                                .isBrokenPeriodInterestPosting())) {
                repaymentsOrWaivers.add(LoanTransaction.copyTransactionProperties(transaction));
            }
        }
        final LoanTransactionComparator transactionComparator = new LoanTransactionComparator();
        Collections.sort(repaymentsOrWaivers, transactionComparator);
        return repaymentsOrWaivers;
    }

    private List<LoanTransaction> retreiveListOfTransactionsExcludeAccruals() {
        final List<LoanTransaction> repaymentsOrWaivers = new ArrayList<>();
        for (final LoanTransaction transaction : this.loanTransactions) {
            if (transaction.isNotReversed()
                    && !(transaction.isAccrual() || transaction.isNonMonetaryTransaction() || transaction.isAddSubsidy() || transaction
                            .isRevokeSubsidy())) {
                repaymentsOrWaivers.add(transaction);
            }
        }
        final LoanTransactionComparator transactionComparator = new LoanTransactionComparator();
        Collections.sort(repaymentsOrWaivers, transactionComparator);
        return repaymentsOrWaivers;
    }

    private List<LoanTransaction> retreiveListOfAccrualTransactions() {
        final List<LoanTransaction> transactions = new ArrayList<>();
        for (final LoanTransaction transaction : this.loanTransactions) {
            if (transaction.isNotReversed() && transaction.isAccrual()) {
                transactions.add(transaction);
            }
        }
        final LoanTransactionComparator transactionComparator = new LoanTransactionComparator();
        Collections.sort(transactions, transactionComparator);
        return transactions;
    }

    private void doPostLoanTransactionChecks(final LocalDate transactionDate, final LoanLifecycleStateMachine loanLifecycleStateMachine) {

        boolean canChnageStatus = (!this.loanProduct().allowNegativeLoanBalances() || getPlannedDisbursalAmount().compareTo(
                this.summary.getTotalPrincipalDisbursed()) == 0);
        if (isOverPaid() && canChnageStatus) {
            // FIXME - kw - update account balance to negative amount.
            handleLoanOverpayment(loanLifecycleStateMachine);
            this.processIncomeAccrualTransactionOnLoanClosure(transactionDate);
        } else if (this.summary.isRepaidInFull(loanCurrency()) && canChnageStatus) {
            handleLoanRepaymentInFull(transactionDate, loanLifecycleStateMachine);
        }
    }
    
    public Money getTotalSubsidyAmount() {
        Money subsidyAmount = Money.zero(loanCurrency());
        for (LoanTransaction transaction : this.loanTransactions) {
            if (transaction.isNotReversed()) {
                if (transaction.getTypeOf().equals(LoanTransactionType.ADD_SUBSIDY)) {
                    subsidyAmount = subsidyAmount.plus(transaction.getAmount(loanCurrency()));
                } else if (transaction.getTypeOf().equals(LoanTransactionType.REVOKE_SUBSIDY)) {
                    if (subsidyAmount.isGreaterThan(Money.zero(loanCurrency()))) {
                        subsidyAmount = subsidyAmount.minus(transaction.getAmount(loanCurrency()));
                    }
                }
            }
        }
        return subsidyAmount;
    }
    
    public Money getTotalTransactionAmountPaid() {
        Money totalPaidAmount = Money.zero(loanCurrency());
        for (LoanTransaction transaction : this.loanTransactions) {
            if (transaction.isRepayment()) {
                totalPaidAmount = totalPaidAmount.plus(transaction.getAmount(loanCurrency()));
            }
        }
        return totalPaidAmount;
    }
    
    public Money getTotalInterestAmountTillDate(final LocalDate transactionDate) {
        Money totalInterestAmount = Money.zero(loanCurrency());
        for (LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
            if (transactionDate.isAfter(installment.getDueDate()) || transactionDate.isEqual(installment.getDueDate())) {
                totalInterestAmount = totalInterestAmount.plus(installment.getInterestCharged(loanCurrency()));
            }
        }
        return totalInterestAmount;
    }

    private void handleLoanRepaymentInFull(final LocalDate transactionDate, final LoanLifecycleStateMachine loanLifecycleStateMachine) {

        boolean isAllChargesPaid = true;
        for (final LoanCharge loanCharge : this.charges) {
            if (loanCharge.isActive() && !(loanCharge.isPaid() || loanCharge.isWaived())) {
                if (loanCharge.isTrancheDisbursementCharge()) {
                    LoanDisbursementDetails disbursementDetails = loanCharge.getTrancheDisbursementCharge().getloanDisbursementDetails();
                    if (loanCharge.isNotFullyPaid() && disbursementDetails.actualDisbursementDate() != null) {
                        isAllChargesPaid = false;
                        break;
                    }
                } else {
                    isAllChargesPaid = false;
                    break;
                }
            }
        }
        if (isAllChargesPaid) {
            final LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.REPAID_IN_FULL,
                    LoanStatus.fromInt(this.loanStatus));
            this.loanStatus = statusEnum.getValue();
            LocalDate closureDate = getLastUserTransactionDate();
            if(closureDate.isBefore(transactionDate)){
                closureDate = transactionDate;
            }
            this.closedOnDate = closureDate.toDate();
            this.actualMaturityDate = closureDate.toDate();
        } else if (LoanStatus.fromInt(this.loanStatus).isOverpaid()) {
            final LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_REPAYMENT_OR_WAIVER,
                    LoanStatus.fromInt(this.loanStatus));
            this.loanStatus = statusEnum.getValue();
        }
        processIncomeAccrualTransactionOnLoanClosure(transactionDate);
    }

    private void processIncomeAccrualTransactionOnLoanClosure(LocalDate transactionDate) {
        if (this.loanInterestRecalculationDetails != null && this.loanInterestRecalculationDetails.isCompoundingToBePostedAsTransaction()
                && (this.status().isClosedObligationsMet() || this.status().isOverpaid())) {
            reverseTransactionsOnOrAfter(retreiveListOfIncomePostingTransactions(), transactionDate.toDate());
            reverseTransactionsOnOrAfter(retreiveListOfAccrualTransactions(), transactionDate.toDate());
            HashMap<String, BigDecimal> cumulativeIncomeFromInstallments = new HashMap<>();
            determineCumulativeIncomeFromInstallments(cumulativeIncomeFromInstallments);
            HashMap<String, BigDecimal> cumulativeIncomeFromIncomePosting = new HashMap<>();
            determineCumulativeIncomeDetails(retreiveListOfIncomePostingTransactions(), cumulativeIncomeFromIncomePosting);
            BigDecimal interestToPost = cumulativeIncomeFromInstallments.get("interest").subtract(
                    cumulativeIncomeFromIncomePosting.get("interest"));
            BigDecimal feeToPost = cumulativeIncomeFromInstallments.get("fee").subtract(cumulativeIncomeFromIncomePosting.get("fee"));
            BigDecimal penaltyToPost = cumulativeIncomeFromInstallments.get("penalty").subtract(
                    cumulativeIncomeFromIncomePosting.get("penalty"));
            BigDecimal amountToPost = interestToPost.add(feeToPost).add(penaltyToPost);

            LoanTransaction finalIncomeTransaction = LoanTransaction.incomePosting(this, this.getOffice(), transactionDate.toDate(), amountToPost,
                    interestToPost, feeToPost, penaltyToPost);

            this.loanTransactions.add(finalIncomeTransaction);
            if (isPeriodicAccrualAccountingEnabledOnLoanProduct()) {
                List<LoanTransaction> updatedAccrualTransactions = retreiveListOfAccrualTransactions();
                LocalDate lastAccruedDate = this.getDisbursementDate();
                if (updatedAccrualTransactions != null && updatedAccrualTransactions.size() > 0) {
                    lastAccruedDate = updatedAccrualTransactions.get(updatedAccrualTransactions.size() - 1).getTransactionDate();
                }
                HashMap<String, Object> feeDetails = new HashMap<>();
                determineFeeDetails(lastAccruedDate, transactionDate, feeDetails);
                LoanTransaction finalAccrual = LoanTransaction.accrueTransaction(this, this.getOffice(), transactionDate, amountToPost,
                        interestToPost, feeToPost, penaltyToPost);
                updateLoanChargesPaidBy(finalAccrual, feeDetails, null);
                this.loanTransactions.add(finalAccrual);
            }
        }
        updateLoanOutstandingBalaces();
    }

    private void determineCumulativeIncomeFromInstallments(HashMap<String, BigDecimal> cumulativeIncomeFromInstallments) {
        BigDecimal interest = BigDecimal.ZERO;
        BigDecimal fee = BigDecimal.ZERO;
        BigDecimal penalty = BigDecimal.ZERO;
        for (LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
            interest = interest.add(installment.getInterestCharged(getCurrency()).getAmount());
            fee = fee.add(installment.getFeeChargesCharged(getCurrency()).getAmount());
            penalty = penalty.add(installment.getPenaltyChargesCharged(getCurrency()).getAmount());
        }
        cumulativeIncomeFromInstallments.put("interest", interest);
        cumulativeIncomeFromInstallments.put("fee", fee);
        cumulativeIncomeFromInstallments.put("penalty", penalty);
    }

    private void determineCumulativeIncomeDetails(Collection<LoanTransaction> transactions, HashMap<String, BigDecimal> incomeDetailsMap) {
        BigDecimal interest = BigDecimal.ZERO;
        BigDecimal fee = BigDecimal.ZERO;
        BigDecimal penalty = BigDecimal.ZERO;
        for (LoanTransaction transaction : transactions) {
            interest = interest.add(transaction.getInterestPortion(getCurrency()).getAmount());
            fee = fee.add(transaction.getFeeChargesPortion(getCurrency()).getAmount());
            penalty = penalty.add(transaction.getPenaltyChargesPortion(getCurrency()).getAmount());
        }
        incomeDetailsMap.put("interest", interest);
        incomeDetailsMap.put("fee", fee);
        incomeDetailsMap.put("penalty", penalty);
    }

    private void handleLoanOverpayment(final LoanLifecycleStateMachine loanLifecycleStateMachine) {

        final LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_OVERPAYMENT, LoanStatus.fromInt(this.loanStatus));
        this.loanStatus = statusEnum.getValue();

        this.closedOnDate = null;
        this.actualMaturityDate = null;
    }

    private boolean isChronologicallyLatestRepaymentOrWaiver(final LoanTransaction loanTransaction,
            final List<LoanTransaction> loanTransactions) {

        boolean isChronologicallyLatestRepaymentOrWaiver = true;

        final LocalDate currentTransactionDate = loanTransaction.getTransactionDate();
        for (final LoanTransaction previousTransaction : loanTransactions) {
            if (!previousTransaction.isDisbursement() && previousTransaction.isNotReversed()) {
                if (currentTransactionDate.isBefore(previousTransaction.getTransactionDate())
                        || currentTransactionDate.isEqual(previousTransaction.getTransactionDate())) {
                    isChronologicallyLatestRepaymentOrWaiver = false;
                    break;
                }
            }
        }

        return isChronologicallyLatestRepaymentOrWaiver;
    }

    private boolean isAfterLatRepayment(final LoanTransaction loanTransaction, final List<LoanTransaction> loanTransactions) {

        boolean isAfterLatRepayment = true;

        final LocalDate currentTransactionDate = loanTransaction.getTransactionDate();
        for (final LoanTransaction previousTransaction : loanTransactions) {
            if (previousTransaction.isRepayment() && previousTransaction.isNotReversed()) {
                if (currentTransactionDate.isBefore(previousTransaction.getTransactionDate())) {
                    isAfterLatRepayment = false;
                    break;
                }
            }
        }
        return isAfterLatRepayment;
    }

    private boolean isChronologicallyLatestTransaction(final LoanTransaction loanTransaction, final List<LoanTransaction> loanTransactions) {

        boolean isChronologicallyLatestRepaymentOrWaiver = true;

        final LocalDate currentTransactionDate = loanTransaction.getTransactionDate();
        for (final LoanTransaction previousTransaction : loanTransactions) {
            if (previousTransaction.isNotReversed()) {
                if (currentTransactionDate.isBefore(previousTransaction.getTransactionDate())
                        || currentTransactionDate.isEqual(previousTransaction.getTransactionDate())) {
                    isChronologicallyLatestRepaymentOrWaiver = false;
                    break;
                }
            }
        }

        return isChronologicallyLatestRepaymentOrWaiver;
    }

    public LocalDate possibleNextRepaymentDate() {
        LocalDate earliestUnpaidInstallmentDate = DateUtils.getLocalDateOfTenant();
        for (final LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
            if (installment.isNotFullyPaidOff()) {
                earliestUnpaidInstallmentDate = installment.getDueDate();
                break;
            }
        }

        LocalDate lastTransactionDate = null;
        for (final LoanTransaction transaction : this.loanTransactions) {
            if (transaction.isRepayment() && transaction.isNonZero()) {
                lastTransactionDate = transaction.getTransactionDate();
            }
        }

        LocalDate possibleNextRepaymentDate = earliestUnpaidInstallmentDate;
        if (lastTransactionDate != null && lastTransactionDate.isAfter(earliestUnpaidInstallmentDate)) {
            possibleNextRepaymentDate = lastTransactionDate;
        }

        return possibleNextRepaymentDate;
    }

    public LoanRepaymentScheduleInstallment possibleNextRepaymentInstallment() {
        LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment = null;

        for (final LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
            if (installment.isNotFullyPaidOff()) {
                loanRepaymentScheduleInstallment = installment;
                break;
            }
        }

        return loanRepaymentScheduleInstallment;
    }

    public ChangedTransactionDetail adjustExistingTransaction(final LoanTransaction newTransactionDetail,
            final LoanLifecycleStateMachine loanLifecycleStateMachine, final LoanTransaction transactionForAdjustment,
            final List<Long> existingTransactionIds, final List<Long> existingReversedTransactionIds,
            final ScheduleGeneratorDTO scheduleGeneratorDTO, final AppUser currentUser) {

        HolidayDetailDTO holidayDetailDTO = scheduleGeneratorDTO.getHolidayDetailDTO();
        validateActivityNotBeforeLastTransactionDate(LoanEvent.LOAN_REPAYMENT_OR_WAIVER, transactionForAdjustment.getTransactionDate());
        
        /**
         * RM-4327 : While undo transaction we should not validate holiday and non working day functionalities.
         */
        if (newTransactionDetail != null && newTransactionDetail.getAmount() != null
                && newTransactionDetail.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            validateRepaymentDateIsOnHoliday(newTransactionDetail.getTransactionDate(), holidayDetailDTO.isAllowTransactionsOnHoliday(),
                    holidayDetailDTO.getHolidays());

            validateRepaymentDateIsOnNonWorkingDay(newTransactionDetail.getTransactionDate(), holidayDetailDTO.getWorkingDays(),
                    holidayDetailDTO.isAllowTransactionsOnNonWorkingDay());
        }
        ChangedTransactionDetail changedTransactionDetail = null;

        existingTransactionIds.addAll(findExistingTransactionIds());
        existingReversedTransactionIds.addAll(findExistingReversedTransactionIds());

        validateActivityNotBeforeClientOrGroupTransferDate(LoanEvent.LOAN_REPAYMENT_OR_WAIVER,
                transactionForAdjustment.getTransactionDate());

        if (transactionForAdjustment.isNotRepayment() && transactionForAdjustment.isNotWaiver()
                && !transactionForAdjustment.isRecoveryRepaymentTransaction()) {
            final String errorMessage = "Only transactions of type repayment or waiver can be adjusted.";
            throw new InvalidLoanTransactionTypeException("transaction", "adjustment.is.only.allowed.to.repayment.or.waiver.transaction",
                    errorMessage);
        }
        
        if (this.isGLIMLoan()) {
            validateModifiedGlimTransaction(newTransactionDetail, transactionForAdjustment);
        }
        
        handleAccrualsForClosedLoanTransactionReversal();
        
        transactionForAdjustment.reverse();
        transactionForAdjustment.manuallyAdjustedOrReversed();
        ArrayList<Integer> transactionTypeForReversal = new ArrayList<>();
        
        if (status().isOverpaid() || status().isClosedObligationsMet() || status().isClosed()) {
            if (isSubsidyApplicable() && getTotalSubsidyAmount().isGreaterThanZero()) {
                final LoanTransaction realizationLoanTransaction = findRealizationTransaction();
                if (realizationLoanTransaction == null) {
                    final String errorMessage = "Only transactions of type realization cannot be adjusted.";
                    throw new InvalidLoanTransactionTypeException("transaction","adjustment.is.only.allowed.to.repayment.or.waiver.transaction", errorMessage);
                }
                realizationLoanTransaction.reverse();
            }
            if (this.loanInterestRecalculationDetails != null && this.loanInterestRecalculationDetails.isCompoundingToBePostedAsTransaction()) {
                Calendar calendar = scheduleGeneratorDTO.getCompoundingCalendarInstance().getCalendar();
                if (!CalendarUtils.isValidRedurringDate(calendar.getRecurrence(), calendar.getStartDateLocalDate(),transactionForAdjustment.getTransactionDate())) {
                    transactionTypeForReversal.add(LoanTransactionType.INCOME_POSTING.getValue());
                    transactionTypeForReversal.add(LoanTransactionType.ACCRUAL.getValue());
                }
            }
            if (!transactionTypeForReversal.isEmpty()) {
                findAndReverseTransactionsOfType(transactionTypeForReversal, transactionForAdjustment.getTransactionDate());
            }
        }
        
        if (isClosedWrittenOff() && !transactionForAdjustment.isRecoveryRepaymentTransaction()) {
            // find write off transaction and reverse it
            final LoanTransaction writeOffTransaction = findWriteOffTransaction();
            writeOffTransaction.reverse();
        }
        
        if (isClosedObligationsMet() || (isClosedWrittenOff() && !transactionForAdjustment.isRecoveryRepaymentTransaction())
                || isClosedWithOutsandingAmountMarkedForReschedule()) {
            this.loanStatus = LoanStatus.ACTIVE.getValue();
            this.closedOnDate = null;
            this.actualMaturityDate = this.expectedMaturityDate;
            if (this.isGLIMLoan()) {
                Set<GroupLoanIndividualMonitoringTransaction> glimTransactions = transactionForAdjustment.getGlimTransaction();
                for (GroupLoanIndividualMonitoringTransaction glimTransaction : glimTransactions) {
                    GroupLoanIndividualMonitoring glimMember = glimTransaction.getGroupLoanIndividualMonitoring();
                    glimMember.setIsActive(true);
                }
            }
        }
        
        if (newTransactionDetail.isRepayment() || newTransactionDetail.isInterestWaiver()
                || transactionForAdjustment.isRecoveryRepaymentTransaction()) {
            changedTransactionDetail = handleRepaymentOrRecoveryOrWaiverTransaction(newTransactionDetail, loanLifecycleStateMachine,
                    transactionForAdjustment, scheduleGeneratorDTO, currentUser);
        }
        
        return changedTransactionDetail;
    }

    private void findAndReverseTransactionsOfType(ArrayList<Integer> transactionType, LocalDate transactionDate) {
        for (LoanTransaction loanTransaction : this.getLoanTransactions()) {
            if (loanTransaction.isNotReversed() && loanTransaction.getTransactionDate().isEqual(transactionDate)
                    && transactionType.contains(loanTransaction.getTypeOf().getValue())) {
                loanTransaction.reverse();
            }
        }
    }

    private void validateModifiedGlimTransaction(final LoanTransaction newTransactionDetail, final LoanTransaction transactionForAdjustment) {
        if (transactionForAdjustment.isWaiver()) {
            final String errorMessage = "Only transactions of type repayment can be adjusted for GLIM loan.";
            throw new InvalidLoanTransactionTypeException("transaction", "adjustment.is.only.allowed.to.repayment.for.glim.loan",
                    errorMessage);
        }
        LoanTransaction currentTransaction = transactionForAdjustment;
        if (MathUtility.isGreaterThanZero(newTransactionDetail.getAmount())) {
            currentTransaction = newTransactionDetail;
        }
        List<Long> excludedTransactionIds = new ArrayList<>();
        excludedTransactionIds.add(transactionForAdjustment.getId());
        LoanTransaction lastGlimLoanTransaction = getLastUserTransaction(excludedTransactionIds);
        final int comparsion = currentTransaction.getTransactionDate().compareTo(lastGlimLoanTransaction.getTransactionDate());
        if (comparsion == -1
                || (comparsion == 0 && (lastGlimLoanTransaction.getCreatedDate().isAfter(transactionForAdjustment.getCreatedDate())))) {
            final String errorMessage = "undo or edit before last transaction is not allowed for GLIM loan.";
            throw new InvalidLoanTransactionTypeException("transaction",
                    "undo.or.edit.before.last.transaction.is.not.allowed.for.glim.loan", errorMessage);
        }

    }
    
    private void handleAccrualsForClosedLoanTransactionReversal() {
        if (isPeriodicAccrualAccountingEnabledOnLoanProduct() && !isInterestRecalculationEnabledForProduct()
                && (status().isOverpaid() || status().isClosedObligationsMet())) {
            LocalDate lastUserTransactionDate = getLastUserTransactionDate();
            LocalDate lastScheduleDate = determineExpectedMaturityDate();
            if (lastScheduleDate.isAfter(DateUtils.getLocalDateOfTenant())) {
                
                final List<LoanTransaction> transactions = new ArrayList<>();
                for (final LoanTransaction loanTransaction : getLoanTransactions()) {
                    if (loanTransaction.isAccrual()) {
                        if (loanTransaction.getTransactionDate().isEqual(lastUserTransactionDate)) {
                            loanTransaction.reverse();
                        } else {
                            transactions.add(loanTransaction);
                        }
                    }
                }
                final LoanTransactionComparator transactionComparator = new LoanTransactionComparator();
                Collections.sort(transactions, transactionComparator);
                applyPeriodicAccruals(transactions);
            }

        }
    }

    public ChangedTransactionDetail undoWrittenOff(final List<Long> existingTransactionIds,
            final List<Long> existingReversedTransactionIds, final ScheduleGeneratorDTO scheduleGeneratorDTO, final AppUser currentUser) {

        validateAccountStatus(LoanEvent.WRITE_OFF_OUTSTANDING_UNDO);
        existingTransactionIds.addAll(findExistingTransactionIds());
        existingReversedTransactionIds.addAll(findExistingReversedTransactionIds());
        final LoanTransaction writeOffTransaction = findWriteOffTransaction();
        writeOffTransaction.reverse();
        this.loanStatus = LoanStatus.ACTIVE.getValue();
        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                .determineProcessor(this.transactionProcessingStrategy);
        final List<LoanTransaction> allNonContraTransactionsPostDisbursement = retreiveListOfTransactionsPostDisbursement();
        if (this.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            regenerateRepaymentScheduleWithInterestRecalculation(scheduleGeneratorDTO, currentUser);
        }
        ChangedTransactionDetail changedTransactionDetail = loanRepaymentScheduleTransactionProcessor.handleTransaction(
                getDisbursementDate(), allNonContraTransactionsPostDisbursement, getCurrency(), this.repaymentScheduleInstallments,
                charges(), disbursementDetails);
        updateLoanSummaryDerivedFields();
        return changedTransactionDetail;
    }

    public LoanTransaction findWriteOffTransaction() {

        LoanTransaction writeOff = null;
        for (final LoanTransaction transaction : this.loanTransactions) {
            if (!transaction.isReversed() && transaction.isWriteOff()) {
                writeOff = transaction;
            }
        }

        return writeOff;
    }
    
    public LoanTransaction findRealizationTransaction() {
        LoanTransaction loanRealizationTransaction = null;
        for (LoanTransaction loanTransaction : this.getLoanTransactions()) {
            if (loanTransaction.isRealizationLoanSubsidyRepayment() && loanTransaction.isNotReversed()) {
                loanRealizationTransaction = loanTransaction;
            }
        }
        return loanRealizationTransaction;
    }

    private boolean isOverPaid() {
    	
        return calculateTotalOverpayment().isGreaterThanZero() ;
    }

    private Money calculateTotalOverpayment() {

        Money totalPaidInRepayments = getTotalPaidInRepayments();

        final MonetaryCurrency currency = loanCurrency();
        Money cumulativeTotalPaidOnInstallments = Money.zero(currency);
        Money cumulativeTotalWaivedOnInstallments = Money.zero(currency);

        for (final LoanRepaymentScheduleInstallment scheduledRepayment : this.repaymentScheduleInstallments) {

            cumulativeTotalPaidOnInstallments = cumulativeTotalPaidOnInstallments
                    .plus(scheduledRepayment.getPrincipalCompleted(currency).plus(scheduledRepayment.getInterestPaid(currency)))
                    .plus(scheduledRepayment.getFeeChargesPaid(currency)).plus(scheduledRepayment.getPenaltyChargesPaid(currency));

            cumulativeTotalWaivedOnInstallments = cumulativeTotalWaivedOnInstallments.plus(scheduledRepayment.getInterestWaived(currency));
        }

        for (final LoanTransaction loanTransaction : this.loanTransactions) {
            if ((loanTransaction.isRefund() || loanTransaction.isRefundForActiveLoan()) && !loanTransaction.isReversed()) {
                totalPaidInRepayments = totalPaidInRepayments.minus(loanTransaction.getAmount(currency));
            }
        }
        
        return totalPaidInRepayments.minus(cumulativeTotalPaidOnInstallments);
    }

    public Money calculateTotalRecoveredPayments() {
        Money totalRecoveredPayments = getTotalRecoveredPayments();
        // in case logic for reversing recovered payment is implemented handle
        // subtraction from totalRecoveredPayments
        return totalRecoveredPayments;
    }

    private MonetaryCurrency loanCurrency() {
        return this.loanRepaymentScheduleDetail.getCurrency();
    }

    public ChangedTransactionDetail closeAsWrittenOff(final JsonCommand command, final LoanLifecycleStateMachine loanLifecycleStateMachine,
            final Map<String, Object> changes, final List<Long> existingTransactionIds, final List<Long> existingReversedTransactionIds,
            final AppUser currentUser, final ScheduleGeneratorDTO scheduleGeneratorDTO) {

        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                .determineProcessor(this.transactionProcessingStrategy);
        ChangedTransactionDetail changedTransactionDetail = closeDisbursements(scheduleGeneratorDTO,
                currentUser);

        validateAccountStatus(LoanEvent.WRITE_OFF_OUTSTANDING);

        final LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.WRITE_OFF_OUTSTANDING,
                LoanStatus.fromInt(this.loanStatus));

        LoanTransaction loanTransaction = null;
        if (!statusEnum.hasStateOf(LoanStatus.fromInt(this.loanStatus))) {
            this.loanStatus = statusEnum.getValue();
            changes.put("status", LoanEnumerations.status(this.loanStatus));

            existingTransactionIds.addAll(findExistingTransactionIds());
            existingReversedTransactionIds.addAll(findExistingReversedTransactionIds());

            final LocalDate writtenOffOnLocalDate = command.localDateValueOfParameterNamed("transactionDate");
            
            final HolidayDetailDTO holidayDetailDTO = scheduleGeneratorDTO.getHolidayDetailDTO();
            String errorMessage = "Written off date should not be a holiday.";
            String errorCode = "writtenoff.date.on.holiday";
            validateDateIsOnHoliday(writtenOffOnLocalDate, holidayDetailDTO.isAllowTransactionsOnHoliday(), holidayDetailDTO.getHolidays(),
                    errorMessage, errorCode);
            errorMessage = "Written off date should not be a non working day.";
            errorCode = "writtenoff.date.on.non.working.day";
            validateDateIsOnNonWorkingDay(writtenOffOnLocalDate, holidayDetailDTO.getWorkingDays(),
                    holidayDetailDTO.isAllowTransactionsOnNonWorkingDay(), errorMessage, errorCode);
            
            final String txnExternalId = command.stringValueOfParameterNamedAllowingNull("externalId");

            this.closedOnDate = writtenOffOnLocalDate.toDate();
            this.writtenOffOnDate = writtenOffOnLocalDate.toDate();
            this.closedBy = currentUser;
            changes.put("closedOnDate", command.stringValueOfParameterNamed("transactionDate"));
            changes.put("writtenOffOnDate", command.stringValueOfParameterNamed("transactionDate"));

            if (writtenOffOnLocalDate.isBefore(getDisbursementDate())) {
                errorMessage = "The date on which a loan is written off cannot be before the loan disbursement date: "
                        + getDisbursementDate().toString();
                throw new InvalidLoanStateTransitionException("writeoff", "cannot.be.before.submittal.date", errorMessage,
                        writtenOffOnLocalDate, getDisbursementDate());
            }

            validateActivityNotBeforeClientOrGroupTransferDate(LoanEvent.WRITE_OFF_OUTSTANDING, writtenOffOnLocalDate);

            if (writtenOffOnLocalDate.isAfter(DateUtils.getLocalDateOfTenant())) {
                errorMessage = "The date on which a loan is written off cannot be in the future.";
                throw new InvalidLoanStateTransitionException("writeoff", "cannot.be.a.future.date", errorMessage, writtenOffOnLocalDate);
            }
            loanTransaction = LoanTransaction.writeoff(this, getOffice(), writtenOffOnLocalDate, txnExternalId);
            LocalDate lastTransactionDate = getLastUserTransactionDate();
            if (lastTransactionDate.isAfter(writtenOffOnLocalDate)) {
                errorMessage = "The date of the writeoff transaction must occur on or before previous transactions.";
                throw new InvalidLoanStateTransitionException("writeoff", "must.occur.on.or.after.other.transaction.dates", errorMessage,
                        writtenOffOnLocalDate);
            }

            this.loanTransactions.add(loanTransaction);

            loanRepaymentScheduleTransactionProcessor.handleWriteOff(loanTransaction, loanCurrency(), this.repaymentScheduleInstallments);

            updateLoanSummaryDerivedFields();
        }
        if (changedTransactionDetail == null) {
            changedTransactionDetail = new ChangedTransactionDetail();
        }
        changedTransactionDetail.getNewTransactionMappings().put(0L, loanTransaction);
        return changedTransactionDetail;
    }

    private ChangedTransactionDetail closeDisbursements(final ScheduleGeneratorDTO scheduleGeneratorDTO,
            final AppUser currentUser) {
        ChangedTransactionDetail changedTransactionDetail = null;
        if (isDisbursementAllowed() && atleastOnceDisbursed()) {
            this.loanRepaymentScheduleDetail.setPrincipal(getDisbursedAmount());
            removeDisbursementDetail();
            changedTransactionDetail =recalculateScheduleFromLastTransaction(scheduleGeneratorDTO, currentUser);
            LoanTransaction loanTransaction = findlatestTransaction();
            doPostLoanTransactionChecks(loanTransaction.getTransactionDate(), loanLifecycleStateMachine);
        }
        return changedTransactionDetail;
    }

    private LoanTransaction findlatestTransaction() {
        LoanTransaction transaction = null;
        for (LoanTransaction loanTransaction : this.loanTransactions) {
            if (!loanTransaction.isReversed()
                    && (transaction == null || transaction.getTransactionDate().isBefore(loanTransaction.getTransactionDate()))) {
                transaction = loanTransaction;
            }
        }
        return transaction;
    }

    public ChangedTransactionDetail close(final JsonCommand command, final LoanLifecycleStateMachine loanLifecycleStateMachine,
            final Map<String, Object> changes, final List<Long> existingTransactionIds, final List<Long> existingReversedTransactionIds,
            final ScheduleGeneratorDTO scheduleGeneratorDTO, final AppUser currentUser) {

        validateAccountStatus(LoanEvent.LOAN_CLOSED);

        existingTransactionIds.addAll(findExistingTransactionIds());
        existingReversedTransactionIds.addAll(findExistingReversedTransactionIds());

        final LocalDate closureDate = command.localDateValueOfParameterNamed("transactionDate");
        final String txnExternalId = command.stringValueOfParameterNamedAllowingNull("externalId");

        this.closedOnDate = closureDate.toDate();
        changes.put("closedOnDate", command.stringValueOfParameterNamed("transactionDate"));

        validateActivityNotBeforeClientOrGroupTransferDate(LoanEvent.REPAID_IN_FULL, closureDate);
        if (closureDate.isBefore(getDisbursementDate())) {
            final String errorMessage = "The date on which a loan is closed cannot be before the loan disbursement date: "
                    + getDisbursementDate().toString();
            throw new InvalidLoanStateTransitionException("close", "cannot.be.before.submittal.date", errorMessage, closureDate,
                    getDisbursementDate());
        }

        if (closureDate.isAfter(DateUtils.getLocalDateOfTenant())) {
            final String errorMessage = "The date on which a loan is closed cannot be in the future.";
            throw new InvalidLoanStateTransitionException("close", "cannot.be.a.future.date", errorMessage, closureDate);
        }
        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                .determineProcessor(this.transactionProcessingStrategy);
        ChangedTransactionDetail changedTransactionDetail = closeDisbursements(scheduleGeneratorDTO,
                currentUser);

        LoanTransaction loanTransaction = null;
        if (isOpen()) {
            final Money totalOutstanding = this.summary.getTotalOutstanding(loanCurrency());
            if (totalOutstanding.isGreaterThanZero() && getInArrearsTolerance().isGreaterThanOrEqualTo(totalOutstanding)) {

                final LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.REPAID_IN_FULL,
                        LoanStatus.fromInt(this.loanStatus));
                if (!statusEnum.hasStateOf(LoanStatus.fromInt(this.loanStatus))) {
                    this.loanStatus = statusEnum.getValue();
                    changes.put("status", LoanEnumerations.status(this.loanStatus));
                }
                this.closedOnDate = closureDate.toDate();
                loanTransaction = LoanTransaction.writeoff(this, getOffice(), closureDate, txnExternalId);
                final boolean isLastTransaction = isChronologicallyLatestTransaction(loanTransaction, this.loanTransactions);
                if (!isLastTransaction) {
                    final String errorMessage = "The closing date of the loan must be on or after latest transaction date.";
                    throw new InvalidLoanStateTransitionException("close.loan", "must.occur.on.or.after.latest.transaction.date",
                            errorMessage, closureDate);
                }

                this.loanTransactions.add(loanTransaction);

                loanRepaymentScheduleTransactionProcessor.handleWriteOff(loanTransaction, loanCurrency(),
                        this.repaymentScheduleInstallments);

                updateLoanSummaryDerivedFields();
            } else if (totalOutstanding.isGreaterThanZero()) {
                final String errorMessage = "A loan with money outstanding cannot be closed";
                throw new InvalidLoanStateTransitionException("close", "loan.has.money.outstanding", errorMessage,
                        totalOutstanding.toString());
            }
        }

        if (isOverPaid()) {
            final Money totalLoanOverpayment = calculateTotalOverpayment();
            if (totalLoanOverpayment.isGreaterThanZero() && getInArrearsTolerance().isGreaterThanOrEqualTo(totalLoanOverpayment)) {
                // TODO - KW - technically should set somewhere that this loan
                // has
                // 'overpaid' amount
                final LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.REPAID_IN_FULL,
                        LoanStatus.fromInt(this.loanStatus));
                if (!statusEnum.hasStateOf(LoanStatus.fromInt(this.loanStatus))) {
                    this.loanStatus = statusEnum.getValue();
                    changes.put("status", LoanEnumerations.status(this.loanStatus));
                }
                this.closedOnDate = closureDate.toDate();
            } else if (totalLoanOverpayment.isGreaterThanZero()) {
                final String errorMessage = "The loan is marked as 'Overpaid' and cannot be moved to 'Closed (obligations met).";
                throw new InvalidLoanStateTransitionException("close", "loan.is.overpaid", errorMessage, totalLoanOverpayment.toString());
            }
        }

        if (changedTransactionDetail == null) {
            changedTransactionDetail = new ChangedTransactionDetail();
        }
        changedTransactionDetail.getNewTransactionMappings().put(0L, loanTransaction);
        return changedTransactionDetail;
    }

    /**
     * Behaviour added to comply with capability of previous mifos product to
     * support easier transition to fineract platform.
     */
    public void closeAsMarkedForReschedule(final JsonCommand command, final LoanLifecycleStateMachine loanLifecycleStateMachine,
            final Map<String, Object> changes) {

        final LocalDate rescheduledOn = command.localDateValueOfParameterNamed("transactionDate");

        final LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_RESCHEDULE, LoanStatus.fromInt(this.loanStatus));
        if (!statusEnum.hasStateOf(LoanStatus.fromInt(this.loanStatus))) {
            this.loanStatus = statusEnum.getValue();
            changes.put("status", LoanEnumerations.status(this.loanStatus));
        }

        this.closedOnDate = rescheduledOn.toDate();
        this.rescheduledOnDate = rescheduledOn.toDate();
        changes.put("closedOnDate", command.stringValueOfParameterNamed("transactionDate"));
        changes.put("rescheduledOnDate", command.stringValueOfParameterNamed("transactionDate"));

        final LocalDate rescheduledOnLocalDate = new LocalDate(this.rescheduledOnDate);
        if (rescheduledOnLocalDate.isBefore(getDisbursementDate())) {
            final String errorMessage = "The date on which a loan is rescheduled cannot be before the loan disbursement date: "
                    + getDisbursementDate().toString();
            throw new InvalidLoanStateTransitionException("close.reschedule", "cannot.be.before.submittal.date", errorMessage,
                    rescheduledOnLocalDate, getDisbursementDate());
        }

        if (rescheduledOnLocalDate.isAfter(DateUtils.getLocalDateOfTenant())) {
            final String errorMessage = "The date on which a loan is rescheduled cannot be in the future.";
            throw new InvalidLoanStateTransitionException("close.reschedule", "cannot.be.a.future.date", errorMessage,
                    rescheduledOnLocalDate);
        }
    }

    public boolean isNotSubmittedAndPendingApproval() {
        return !isSubmittedAndPendingApproval();
    }

    public LoanStatus status() {
        return LoanStatus.fromInt(this.loanStatus);
    }

    public boolean isSubmittedAndPendingApproval() {
        return status().isSubmittedAndPendingApproval();
    }

    public boolean isApproved() {
        return status().isApproved();
    }

    private boolean isNotDisbursed() {
        return !isDisbursed();
    }

    public boolean isChargesAdditionAllowed() {
        boolean isDisbursed = false;
        if (this.loanProduct.isMultiDisburseLoan()) {
            isDisbursed = !isDisbursementAllowed();
        } else {
            isDisbursed = hasDisbursementTransaction();
        }
        return isDisbursed;
    }

    public boolean isDisbursed() {
        return hasDisbursementTransaction();
    }

    public boolean isClosed() {
        return status().isClosed() || isCancelled();
    }
    
    private boolean isClosedObligationsMet() {
        return status().isClosedObligationsMet();
    }

    public boolean isClosedWrittenOff() {
        return status().isClosedWrittenOff();
    }

    private boolean isClosedWithOutsandingAmountMarkedForReschedule() {
        return status().isClosedWithOutsandingAmountMarkedForReschedule();
    }

    private boolean isCancelled() {
        return isRejected() || isWithdrawn();
    }

    private boolean isWithdrawn() {
        return status().isWithdrawnByClient();
    }

    private boolean isRejected() {
        return status().isRejected();
    }

    public boolean isOpen() {
        return status().isActive();
    }

    private boolean isAllTranchesNotDisbursed() {
        return this.loanProduct.isMultiDisburseLoan()
                && (LoanStatus.fromInt(this.loanStatus).isActive() || LoanStatus.fromInt(this.loanStatus).isApproved())
                && isDisbursementAllowed();
    }

    private boolean hasDisbursementTransaction() {
        boolean hasRepaymentTransaction = false;
        for (final LoanTransaction loanTransaction : this.loanTransactions) {
            if (loanTransaction.isDisbursement() && loanTransaction.isNotReversed()) {
                hasRepaymentTransaction = true;
                break;
            }
        }
        return hasRepaymentTransaction;
    }

    public boolean isSubmittedOnDateAfter(final LocalDate compareDate) {
        return this.submittedOnDate == null ? false : new LocalDate(this.submittedOnDate).isAfter(compareDate);
    }

    public LocalDate getSubmittedOnDate() {
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.submittedOnDate), null);
    }

    public LocalDate getApprovedOnDate() {
        LocalDate date = null;
        if (this.approvedOnDate != null) {
            date = new LocalDate(this.approvedOnDate);
        }
        return date;
    }

    public LocalDate getExpectedDisbursedOnLocalDate() {
        LocalDate expectedDisbursementDate = null;
        if (this.expectedDisbursementDate != null) {
            expectedDisbursementDate = new LocalDate(this.expectedDisbursementDate);
        }
        return expectedDisbursementDate;
    }

    public LocalDate getDisbursementDate() {
        LocalDate disbursementDate = getExpectedDisbursedOnLocalDate();
        if (this.actualDisbursementDate != null) {
            disbursementDate = new LocalDate(this.actualDisbursementDate);
        }
        return disbursementDate;
    }

    public LocalDate getWrittenOffDate() {
        LocalDate writtenOffDate = null;
        if (this.writtenOffOnDate != null) {
            writtenOffDate = new LocalDate(this.writtenOffOnDate);
        }
        return writtenOffDate;
    }

    public LocalDate getExpectedDisbursedOnLocalDateForTemplate() {

        LocalDate expectedDisbursementDate = null;
        if (this.expectedDisbursementDate != null) {
            expectedDisbursementDate = new LocalDate(this.expectedDisbursementDate);
        }

        Collection<LoanDisbursementDetails> details = fetchUndisbursedDetail();
        if (!details.isEmpty()) {
            for (LoanDisbursementDetails disbursementDetails : details) {
                expectedDisbursementDate = new LocalDate(disbursementDetails.expectedDisbursementDate());
            }
        }
        return expectedDisbursementDate;
    }

    /*
     * Reason for derving
     */

    public BigDecimal getDisburseAmountForTemplate() {
        BigDecimal principal = this.loanRepaymentScheduleDetail.getPrincipal().getAmount();
        Collection<LoanDisbursementDetails> details = fetchUndisbursedDetail();
        if (!details.isEmpty()) {
            principal = BigDecimal.ZERO;
            for (LoanDisbursementDetails disbursementDetails : details) {
                principal = principal.add(disbursementDetails.principal());
            }
        }
        return principal;
    }

    public LocalDate getExpectedFirstRepaymentOnDate() {
        LocalDate firstRepaymentDate = null;
        if (this.expectedFirstRepaymentOnDate != null) {
            firstRepaymentDate = new LocalDate(this.expectedFirstRepaymentOnDate);
        }
        return firstRepaymentDate;
    }

    private void addRepaymentScheduleInstallment(final LoanRepaymentScheduleInstallment installment) {
        installment.updateLoan(this);
        this.repaymentScheduleInstallments.add(installment);
    }

    private boolean isActualDisbursedOnDateEarlierOrLaterThanExpected(final LocalDate actualDisbursedOnDate) {
        boolean isRegenerationRequired = false;
        if (this.loanProduct.isMultiDisburseLoan()) {
            LoanDisbursementDetails details = fetchLastDisburseDetail();
            if (details != null && !(details.expectedDisbursementDate().equals(details.actualDisbursementDate()))) {
                isRegenerationRequired = true;
            }
        }
        return !new LocalDate(this.expectedDisbursementDate).isEqual(actualDisbursedOnDate) || isRegenerationRequired;
    }

    private boolean isRepaymentScheduleRegenerationRequiredForDisbursement(final LocalDate actualDisbursementDate) {
        return isActualDisbursedOnDateEarlierOrLaterThanExpected(actualDisbursementDate);
    }

    private Money getTotalPaidInRepayments() {
        Money cumulativePaid = Money.zero(loanCurrency());

        for (final LoanTransaction repayment : this.loanTransactions) {
            if (repayment.isRepayment() && !repayment.isReversed()) {
                cumulativePaid = cumulativePaid.plus(repayment.getAmount(loanCurrency()));
            }
        }

        return cumulativePaid;
    }

    public Money getTotalRecoveredPayments() {
        Money cumulativePaid = Money.zero(loanCurrency());

        for (final LoanTransaction recoveredPayment : this.loanTransactions) {
            if (recoveredPayment.isRecoveryRepayment()) {
                cumulativePaid = cumulativePaid.plus(recoveredPayment.getAmount(loanCurrency()));
            }
        }
        return cumulativePaid;
    }

    private Money getTotalInterestOutstandingOnLoan() {
        Money cumulativeInterest = Money.zero(loanCurrency());

        for (final LoanRepaymentScheduleInstallment scheduledRepayment : this.repaymentScheduleInstallments) {
            cumulativeInterest = cumulativeInterest.plus(scheduledRepayment.getInterestOutstanding(loanCurrency()));
        }

        return cumulativeInterest;
    }

    @SuppressWarnings("unused")
    private Money getTotalInterestOverdueOnLoan() {
        Money cumulativeInterestOverdue = Money.zero(this.loanRepaymentScheduleDetail.getPrincipal().getCurrency());

        for (final LoanRepaymentScheduleInstallment scheduledRepayment : this.repaymentScheduleInstallments) {

            final Money interestOutstandingForPeriod = scheduledRepayment.getInterestOutstanding(loanCurrency());
            if (scheduledRepayment.isOverdueOn(DateUtils.getLocalDateOfTenant())) {
                cumulativeInterestOverdue = cumulativeInterestOverdue.plus(interestOutstandingForPeriod);
            }
        }

        return cumulativeInterestOverdue;
    }

    private Money getInArrearsTolerance() {
        return this.loanRepaymentScheduleDetail.getInArrearsTolerance();
    }

    public boolean hasIdentifyOf(final Long loanId) {
        return loanId.equals(getId());
    }

    public boolean hasLoanOfficer(final Staff fromLoanOfficer) {

        boolean matchesCurrentLoanOfficer = false;
        if (this.loanOfficer != null) {
            matchesCurrentLoanOfficer = this.loanOfficer.identifiedBy(fromLoanOfficer);
        } else {
            matchesCurrentLoanOfficer = fromLoanOfficer == null;
        }

        return matchesCurrentLoanOfficer;
    }

    public LocalDate getInterestChargedFromDate() {
        LocalDate interestChargedFrom = null;
        if (this.interestChargedFromDate != null) {
            interestChargedFrom = new LocalDate(this.interestChargedFromDate);
        }
        return interestChargedFrom;
    }

    public Money getPrincpal() {
        return this.loanRepaymentScheduleDetail.getPrincipal();
    }

    public boolean hasCurrencyCodeOf(final String matchingCurrencyCode) {
        return getCurrencyCode().equalsIgnoreCase(matchingCurrencyCode);
    }

    public String getCurrencyCode() {
        return this.loanRepaymentScheduleDetail.getPrincipal().getCurrencyCode();
    }

    public MonetaryCurrency getCurrency() {
        return this.loanRepaymentScheduleDetail.getCurrency();
    }

    public void reassignLoanOfficer(final Staff newLoanOfficer, final LocalDate assignmentDate) {

        final LoanOfficerAssignmentHistory latestHistoryRecord = findLatestIncompleteHistoryRecord();
        final LoanOfficerAssignmentHistory lastAssignmentRecord = findLastAssignmentHistoryRecord(newLoanOfficer);

        // assignment date should not be less than loan submitted date
        if (isSubmittedOnDateAfter(assignmentDate)) {

            final String errorMessage = "The Loan Officer assignment date (" + assignmentDate.toString()
                    + ") cannot be before loan submitted date (" + getSubmittedOnDate().toString() + ").";

            throw new LoanOfficerAssignmentDateException("cannot.be.before.loan.submittal.date", errorMessage, assignmentDate,
                    getSubmittedOnDate());

        } else if (lastAssignmentRecord != null && lastAssignmentRecord.isEndDateAfter(assignmentDate)) {

            final String errorMessage = "The Loan Officer assignment date (" + assignmentDate
                    + ") cannot be before previous Loan Officer unassigned date (" + lastAssignmentRecord.getEndDate() + ").";

            throw new LoanOfficerAssignmentDateException("cannot.be.before.previous.unassignement.date", errorMessage, assignmentDate,
                    lastAssignmentRecord.getEndDate());

        } else if (DateUtils.getLocalDateOfTenant().isBefore(assignmentDate)) {

            final String errorMessage = "The Loan Officer assignment date (" + assignmentDate + ") cannot be in the future.";

            throw new LoanOfficerAssignmentDateException("cannot.be.a.future.date", errorMessage, assignmentDate);

        } else if (latestHistoryRecord != null && this.loanOfficer.identifiedBy(newLoanOfficer)) {
            latestHistoryRecord.updateStartDate(assignmentDate);
        } else if (latestHistoryRecord != null && latestHistoryRecord.matchesStartDateOf(assignmentDate)) {
            latestHistoryRecord.updateLoanOfficer(newLoanOfficer);
            this.loanOfficer = newLoanOfficer;
        } else if (latestHistoryRecord != null && latestHistoryRecord.hasStartDateBefore(assignmentDate)) {
            final String errorMessage = "Loan with identifier " + getId() + " was already assigned before date " + assignmentDate;
            throw new LoanOfficerAssignmentDateException("is.before.last.assignment.date", errorMessage, getId(), assignmentDate);
        } else {
            if (latestHistoryRecord != null) {
                // loan officer correctly changed from previous loan officer to
                // new loan officer
                latestHistoryRecord.updateEndDate(assignmentDate);
            }

            this.loanOfficer = newLoanOfficer;
            if (isNotSubmittedAndPendingApproval()) {
                final LoanOfficerAssignmentHistory loanOfficerAssignmentHistory = LoanOfficerAssignmentHistory.createNew(this,
                        this.loanOfficer, assignmentDate);
                this.loanOfficerHistory.add(loanOfficerAssignmentHistory);
            }
        }
    }

    public void removeLoanOfficer(final LocalDate unassignDate) {

        final LoanOfficerAssignmentHistory latestHistoryRecord = findLatestIncompleteHistoryRecord();

        if (latestHistoryRecord != null) {
            validateUnassignDate(latestHistoryRecord, unassignDate);
            latestHistoryRecord.updateEndDate(unassignDate);
        }

        this.loanOfficer = null;
    }

    private void validateUnassignDate(final LoanOfficerAssignmentHistory latestHistoryRecord, final LocalDate unassignDate) {

        final LocalDate today = DateUtils.getLocalDateOfTenant();

        if (latestHistoryRecord.getStartDate().isAfter(unassignDate)) {

            final String errorMessage = "The Loan officer Unassign date(" + unassignDate + ") cannot be before its assignment date ("
                    + latestHistoryRecord.getStartDate() + ").";

            throw new LoanOfficerUnassignmentDateException("cannot.be.before.assignment.date", errorMessage, getId(), getLoanOfficer()
                    .getId(), latestHistoryRecord.getStartDate(), unassignDate);

        } else if (unassignDate.isAfter(today)) {

            final String errorMessage = "The Loan Officer Unassign date (" + unassignDate + ") cannot be in the future.";

            throw new LoanOfficerUnassignmentDateException("cannot.be.a.future.date", errorMessage, unassignDate);
        }
    }

    private LoanOfficerAssignmentHistory findLatestIncompleteHistoryRecord() {

        LoanOfficerAssignmentHistory latestRecordWithNoEndDate = null;
        for (final LoanOfficerAssignmentHistory historyRecord : this.loanOfficerHistory) {
            if (historyRecord.isCurrentRecord()) {
                latestRecordWithNoEndDate = historyRecord;
                break;
            }
        }
        return latestRecordWithNoEndDate;
    }

    private LoanOfficerAssignmentHistory findLastAssignmentHistoryRecord(final Staff newLoanOfficer) {

        LoanOfficerAssignmentHistory lastAssignmentRecordLatestEndDate = null;
        for (final LoanOfficerAssignmentHistory historyRecord : this.loanOfficerHistory) {

            if (historyRecord.isCurrentRecord() && !historyRecord.isSameLoanOfficer(newLoanOfficer)) {
                lastAssignmentRecordLatestEndDate = historyRecord;
                break;
            }

            if (lastAssignmentRecordLatestEndDate == null) {
                lastAssignmentRecordLatestEndDate = historyRecord;
            } else if (historyRecord.isEndDateAfter(lastAssignmentRecordLatestEndDate.getEndDate())
                    && !historyRecord.isSameLoanOfficer(newLoanOfficer)) {
                lastAssignmentRecordLatestEndDate = historyRecord;
            }
        }
        return lastAssignmentRecordLatestEndDate;
    }

    public Long getClientId() {
        Long clientId = null;
        if (this.client != null) {
            clientId = this.client.getId();
        }
        return clientId;
    }

    public Long getGroupId() {
        Long groupId = null;
        if (this.group != null) {
            groupId = this.group.getId();
        }
        return groupId;
    }

    public Long getOfficeId() {
        Long officeId = null;
        if (this.client != null) {
            officeId = this.client.officeId();
        } else {
            officeId = this.group.officeId();
        }
        return officeId;
    }

    public Office getOffice() {
        Office office = null;
        if (this.client != null) {
            office = this.client.getOffice();
        } else {
            office = this.group.getOffice();
        }
        return office;
    }
    
    public Integer getRepaymentHistoryVersion() {
       if(this.repaymentHistoryVersion == null){return 0; }
        return this.repaymentHistoryVersion;
    }
       
    public void setRepaymentHistoryVersion(Integer repaymenthistoryversion) {
         this.repaymentHistoryVersion = repaymenthistoryversion;
    }

    private Boolean isCashBasedAccountingEnabledOnLoanProduct() {
        return this.loanProduct.isCashBasedAccountingEnabled();
    }

    public Boolean isUpfrontAccrualAccountingEnabledOnLoanProduct() {
        return this.loanProduct.isUpfrontAccrualAccountingEnabled();
    }

    public Boolean isAccountingDisabledOnLoanProduct() {
        return this.loanProduct.isAccountingDisabled();
    }

    public Boolean isNoneOrCashOrUpfrontAccrualAccountingEnabledOnLoanProduct() {
        return isCashBasedAccountingEnabledOnLoanProduct() || isUpfrontAccrualAccountingEnabledOnLoanProduct()
                || isAccountingDisabledOnLoanProduct();
    }

    public Boolean isPeriodicAccrualAccountingEnabledOnLoanProduct() {
        return this.loanProduct.isPeriodicAccrualAccountingEnabled();
    }

    public Long productId() {
        return this.loanProduct.getId();
    }

    public Staff getLoanOfficer() {
        return this.loanOfficer;
    }

    public LoanSummary getSummary() {
        return this.summary;
    }

    public Set<LoanCollateral> getCollateral() {
        return this.collateral;
    }

    public BigDecimal getProposedPrincipal() {
        return this.proposedPrincipal;
    }
    
    public void updateProposedPrincipal(final BigDecimal proposedPrincipal) {
        this.proposedPrincipal = proposedPrincipal;
    }

    public void updateApprovedPrincipal(final BigDecimal approvedPrincipal) {
        this.approvedPrincipal = approvedPrincipal;
    }
    
	public Integer getLoanType() {
		return this.loanType;
	}

    public Map<String, Object> deriveAccountingBridgeData(final CurrencyData currencyData, final List<Long> existingTransactionIds,
            final List<Long> existingReversedTransactionIds, boolean isAccountTransfer) {

        final Map<String, Object> accountingBridgeData = new LinkedHashMap<>();
        accountingBridgeData.put("loanId", getId());
        accountingBridgeData.put("loanProductId", productId());
        accountingBridgeData.put("officeId", getOfficeId());
        accountingBridgeData.put("currency", currencyData);
        accountingBridgeData.put("calculatedInterest", this.summary.getTotalInterestCharged());
        accountingBridgeData.put("cashBasedAccountingEnabled", isCashBasedAccountingEnabledOnLoanProduct());
        accountingBridgeData.put("upfrontAccrualBasedAccountingEnabled", isUpfrontAccrualAccountingEnabledOnLoanProduct());
        accountingBridgeData.put("periodicAccrualBasedAccountingEnabled", isPeriodicAccrualAccountingEnabledOnLoanProduct());
        accountingBridgeData.put("isAccountTransfer", isAccountTransfer);
        if(this.writeOffReason != null){
        	accountingBridgeData.put("writeOffReasonId", this.writeOffReason.getId());
        }else{
        	accountingBridgeData.put("writeOffReasonId", null);
        }

        final List<Map<String, Object>> newLoanTransactions = new ArrayList<>();
        for (final LoanTransaction transaction : this.loanTransactions) {
            if (transaction.isReversed() && existingTransactionIds.contains(transaction.getId())
                    && !existingReversedTransactionIds.contains(transaction.getId())) {
                if (transaction.getSubTypeOf() == null
                        || !(transaction.getSubTypeOf().equals(LoanTransactionSubType.UNACCOUTABLE.getValue()))) {
                    newLoanTransactions.add(transaction.toMapData(currencyData));
                }
            } else if (!existingTransactionIds.contains(transaction.getId())) {
                if (transaction.getSubTypeOf() == null
                        || !(transaction.getSubTypeOf().equals(LoanTransactionSubType.UNACCOUTABLE.getValue()))) {
                    newLoanTransactions.add(transaction.toMapData(currencyData));
                }
            }
        }

        accountingBridgeData.put("newLoanTransactions", newLoanTransactions);
        return accountingBridgeData;
    }

    public Money getReceivableInterest(final LocalDate tillDate) {
        Money receivableInterest = Money.zero(getCurrency());
        for (final LoanTransaction transaction : this.loanTransactions) {
            if (transaction.isNotReversed() && !transaction.isRepaymentAtDisbursement() && !transaction.isDisbursement()
                    && !transaction.isBrokenPeriodInterestPosting() && !transaction.getTransactionDate().isAfter(tillDate)) {
                if (transaction.isAccrual()) {
                    receivableInterest = receivableInterest.plus(transaction.getInterestPortion(getCurrency()));
                } else if (transaction.isRepayment() || transaction.isInterestWaiver()) {
                    receivableInterest = receivableInterest.minus(transaction.getInterestPortion(getCurrency()));
                }
            }
            if (receivableInterest.isLessThanZero()) {
                receivableInterest = receivableInterest.zero();
            }
            /*
             * if (transaction.getTransactionDate().isAfter(tillDate) &&
             * transaction.isAccrual()) { final String errorMessage =
             * "The date on which a loan is interest waived cannot be in after accrual transactions."
             * ; throw new InvalidLoanStateTransitionException("waive",
             * "cannot.be.after.accrual.date", errorMessage, tillDate); }
             */
        }
        return receivableInterest;
    }

    public void setHelpers(final LoanLifecycleStateMachine loanLifecycleStateMachine, final LoanSummaryWrapper loanSummaryWrapper,
            final LoanRepaymentScheduleTransactionProcessorFactory transactionProcessorFactory) {
        this.loanLifecycleStateMachine = loanLifecycleStateMachine;
        this.loanSummaryWrapper = loanSummaryWrapper;
        this.transactionProcessorFactory = transactionProcessorFactory;
    }

    public boolean isSyncDisbursementWithMeeting() {
        return this.syncDisbursementWithMeeting == null ? false : this.syncDisbursementWithMeeting;
    }

    public Date getClosedOnDate() {
        return this.closedOnDate;
    }

    public void updateLoanRepaymentScheduleDates(final String recuringRule, final boolean isHolidayEnabled,
            final ScheduleGeneratorDTO scheduleGeneratorDTO, final WorkingDays workingDays, final LocalDate presentMeetingDate,
            final LocalDate newMeetingDate, final boolean isSkipRepaymentonfirstdayofmonth, final Integer numberofDays) {

        // first repayment's from date is same as disbursement date.
        /*
         * meetingStartDate is used as seedDate Capture the seedDate from user
         * and use the seedDate as meetingStart date
         */

        LocalDate tmpFromDate = getDisbursementDate();
        final PeriodFrequencyType repaymentPeriodFrequencyType = this.loanRepaymentScheduleDetail.getRepaymentPeriodFrequencyType();
        final Integer loanRepaymentInterval = this.loanRepaymentScheduleDetail.getRepayEvery();
        final String frequency = CalendarUtils.getMeetingFrequencyFromPeriodFrequencyType(repaymentPeriodFrequencyType);

        LocalDate newRepaymentDate = null;
        Boolean isFirstTime = true;
        LocalDate latestRepaymentDate = null;
        LoanApplicationTerms loanApplicationTerms = constructLoanApplicationTerms(scheduleGeneratorDTO);
        for (final LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment : this.repaymentScheduleInstallments) {

            LocalDate oldDueDate = loanRepaymentScheduleInstallment.getDueDate();

            if (oldDueDate.isEqual(presentMeetingDate) || oldDueDate.isAfter(presentMeetingDate)) {

                if (isFirstTime) {

                    isFirstTime = false;
                    newRepaymentDate = newMeetingDate;

                } else {
                    // tmpFromDate.plusDays(1) is done to make sure
                    // getNewRepaymentMeetingDate method returns next meeting
                    // date and not the same as tmpFromDate
                    newRepaymentDate = CalendarUtils.getNextRepaymentMeetingDate(recuringRule, tmpFromDate, tmpFromDate,
                            loanRepaymentInterval, frequency, workingDays, isSkipRepaymentonfirstdayofmonth, numberofDays);
                }

                if (isHolidayEnabled) {
                	ScheduledDateGenerator scheduledDateGenerator = new DefaultScheduledDateGenerator();
                	newRepaymentDate = scheduledDateGenerator.adjustRepaymentDate(newRepaymentDate, loanApplicationTerms,
                			scheduleGeneratorDTO.getHolidayDetailDTO()).getChangedScheduleDate();
                }
                if (latestRepaymentDate == null || latestRepaymentDate.isBefore(newRepaymentDate)) {
                    latestRepaymentDate = newRepaymentDate;
                }
                loanRepaymentScheduleInstallment.updateDueDate(newRepaymentDate);
                // reset from date to get actual daysInPeriod

                if (!isFirstTime) {
                    loanRepaymentScheduleInstallment.updateFromDate(tmpFromDate);
                }

                tmpFromDate = newRepaymentDate;// update with new repayment
                // date
            } else {
                tmpFromDate = oldDueDate;
            }
        }
        if (latestRepaymentDate != null) {
            this.expectedMaturityDate = latestRepaymentDate.toDate();
        }
    }

    public void updateLoanRepaymentScheduleDates(final LocalDate meetingStartDate, final String recuringRule,
    		final ScheduleGeneratorDTO scheduleGeneratorDTO, final boolean isHolidayEnabled, final WorkingDays workingDays,
            final boolean isSkipRepaymentonfirstdayofmonth, final Integer numberofDays) {

        // first repayment's from date is same as disbursement date.
        LocalDate tmpFromDate = getDisbursementDate();
        final PeriodFrequencyType repaymentPeriodFrequencyType = this.loanRepaymentScheduleDetail.getRepaymentPeriodFrequencyType();
        final Integer loanRepaymentInterval = this.loanRepaymentScheduleDetail.getRepayEvery();
        final String frequency = CalendarUtils.getMeetingFrequencyFromPeriodFrequencyType(repaymentPeriodFrequencyType);

        LocalDate newRepaymentDate = null;
        LocalDate seedDate = meetingStartDate;
        LocalDate latestRepaymentDate = null;
        LoanApplicationTerms loanApplicationTerms = constructLoanApplicationTerms(scheduleGeneratorDTO);
        for (final LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment : this.repaymentScheduleInstallments) {

            LocalDate oldDueDate = loanRepaymentScheduleInstallment.getDueDate();

            // FIXME: AA this won't update repayment dates before current date.

            if (oldDueDate.isAfter(seedDate) && oldDueDate.isAfter(DateUtils.getLocalDateOfTenant())) {

                newRepaymentDate = CalendarUtils.getNewRepaymentMeetingDate(recuringRule, seedDate, oldDueDate, loanRepaymentInterval,
                        frequency, workingDays, isSkipRepaymentonfirstdayofmonth, numberofDays);

                final LocalDate maxDateLimitForNewRepayment = getMaxDateLimitForNewRepayment(repaymentPeriodFrequencyType,
                        loanRepaymentInterval, tmpFromDate);

                if (newRepaymentDate.isAfter(maxDateLimitForNewRepayment)) {
                    newRepaymentDate = CalendarUtils.getNextRepaymentMeetingDate(recuringRule, seedDate, tmpFromDate,
                            loanRepaymentInterval, frequency, workingDays, isSkipRepaymentonfirstdayofmonth, numberofDays);
                }

                if (isHolidayEnabled) {
                	ScheduledDateGenerator scheduledDateGenerator = new DefaultScheduledDateGenerator();
                	newRepaymentDate = scheduledDateGenerator.adjustRepaymentDate(newRepaymentDate, loanApplicationTerms, scheduleGeneratorDTO.getHolidayDetailDTO())
                			.getChangedScheduleDate();
                }
                if (latestRepaymentDate == null || latestRepaymentDate.isBefore(newRepaymentDate)) {
                    latestRepaymentDate = newRepaymentDate;
                }

                loanRepaymentScheduleInstallment.updateDueDate(newRepaymentDate);
                // reset from date to get actual daysInPeriod
                loanRepaymentScheduleInstallment.updateFromDate(tmpFromDate);
                tmpFromDate = newRepaymentDate;// update with new repayment
                // date
            } else {
                tmpFromDate = oldDueDate;
            }
        }
        if (latestRepaymentDate != null) {
            this.expectedMaturityDate = latestRepaymentDate.toDate();
        }
    }
    
    private LocalDate getMaxDateLimitForNewRepayment(final PeriodFrequencyType periodFrequencyType, final Integer loanRepaymentInterval,
            final LocalDate startDate) {
        LocalDate dueRepaymentPeriodDate = startDate;
        final Integer repaidEvery = 2 * loanRepaymentInterval;
        switch (periodFrequencyType) {
            case DAYS:
                dueRepaymentPeriodDate = startDate.plusDays(repaidEvery);
            break;
            case WEEKS:
                dueRepaymentPeriodDate = startDate.plusWeeks(repaidEvery);
            break;
            case MONTHS:
                dueRepaymentPeriodDate = startDate.plusMonths(repaidEvery);
            break;
            case YEARS:
                dueRepaymentPeriodDate = startDate.plusYears(repaidEvery);
            break;
            case INVALID:
            break;
        }
        return dueRepaymentPeriodDate.minusDays(1);// get 2n-1 range date from
                                                   // startDate
    }

    public void applyHolidayToRepaymentScheduleDates(final Holiday holiday) {
        // first repayment's from date is same as disbursement date.
        LocalDate tmpFromDate = getDisbursementDate();
        // Loop through all loanRepayments
        for (final LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment : this.repaymentScheduleInstallments) {
            final LocalDate oldDueDate = loanRepaymentScheduleInstallment.getDueDate();

            // update from date if it's not same as previous installament's due
            // date.
            if (!loanRepaymentScheduleInstallment.getFromDate().isEqual(tmpFromDate)) {
                loanRepaymentScheduleInstallment.updateFromDate(tmpFromDate);
            }
            if (oldDueDate.isAfter(holiday.getToDateLocalDate())) {
                break;
            }

            if (oldDueDate.equals(holiday.getFromDateLocalDate()) || oldDueDate.equals(holiday.getToDateLocalDate())
                    || oldDueDate.isAfter(holiday.getFromDateLocalDate()) && oldDueDate.isBefore(holiday.getToDateLocalDate())) {
                // FIXME: AA do we need to apply non-working days.
                // Assuming holiday's repayment reschedule to date cannot be
                // created on a non-working day.
                final LocalDate newRepaymentDate = holiday.getRepaymentsRescheduledToLocalDate();
                loanRepaymentScheduleInstallment.updateDueDate(newRepaymentDate);
            }
            tmpFromDate = loanRepaymentScheduleInstallment.getDueDate();
        }
    }

    private void validateDisbursementDateIsOnNonWorkingDay(final WorkingDays workingDays, final boolean allowTransactionsOnNonWorkingDay) {
        if (!allowTransactionsOnNonWorkingDay) {
            if (this.isMultiDisburmentLoan()) {
                for (LoanDisbursementDetails disbursementDetail : this.disbursementDetails) {
                    if (disbursementDetail.isActive() && !WorkingDaysUtil.isWorkingDay(workingDays, disbursementDetail.getDisbursementDateAsLocalDate())) {
                        final String errorMessage = "Expected disbursement date cannot be on a non working day";
                        throw new LoanApplicationDateException("disbursement.date.on.non.working.day", errorMessage,
                                disbursementDetail.getDisbursementDateAsLocalDate());
                    }
                }
            } else if (!WorkingDaysUtil.isWorkingDay(workingDays, getDisbursementDate())) {
                final String errorMessage = "Expected disbursement date cannot be on a non working day";
                throw new LoanApplicationDateException("disbursement.date.on.non.working.day", errorMessage,
                        getExpectedDisbursedOnLocalDate());
            }
        }
    }

    private void validateDisbursementDateIsOnHoliday(final boolean allowTransactionsOnHoliday, final List<Holiday> holidays) {
        if (!allowTransactionsOnHoliday) {
            if (this.isMultiDisburmentLoan()) {
                for (LoanDisbursementDetails disbursementDetail : this.disbursementDetails) {
                    if (disbursementDetail.isActive() && HolidayUtil.isHoliday(disbursementDetail.getDisbursementDateAsLocalDate(), holidays)) {
                        final String errorMessage = "Expected disbursement date cannot be on a holiday";
                        throw new LoanApplicationDateException("disbursement.date.on.holiday", errorMessage,
                                disbursementDetail.getDisbursementDateAsLocalDate());
                    }
                }
            } else if (HolidayUtil.isHoliday(getDisbursementDate(), holidays)) {
                final String errorMessage = "Expected disbursement date cannot be on a holiday";
                throw new LoanApplicationDateException("disbursement.date.on.holiday", errorMessage, getExpectedDisbursedOnLocalDate());
            }
        }
    }

    public void validateRepaymentDateIsOnNonWorkingDay(final LocalDate repaymentDate, final WorkingDays workingDays,
            final boolean allowTransactionsOnNonWorkingDay) {
        final String errorMessage = "Repayment date cannot be on a non working day";
        final String errorCode = "repayment.date.on.non.working.day";
        validateDateIsOnNonWorkingDay(repaymentDate, workingDays, allowTransactionsOnNonWorkingDay, errorMessage, errorCode);
    }

    public void validateRepaymentDateIsOnHoliday(final LocalDate repaymentDate, final boolean allowTransactionsOnHoliday,
            final List<Holiday> holidays) {
        final String errorMessage = "Repayment date cannot be on a holiday";
        final String errorCode = "repayment.date.on.holiday";
        validateDateIsOnHoliday(repaymentDate, allowTransactionsOnHoliday, holidays, errorMessage, errorCode);
    }

    public Group group() {
        return this.group;
    }

    public void updateGroup(final Group newGroup) {
        this.group = newGroup;
    }

    public Integer getCurrentLoanCounter() {
        return this.loanCounter;
    }

    public Integer getLoanProductLoanCounter() {
        if (this.loanProductCounter == null) { return 0; }
        return this.loanProductCounter;
    }

    public void updateClientLoanCounter(final Integer newLoanCounter) {
        this.loanCounter = newLoanCounter;
    }

    public void updateLoanProductLoanCounter(final Integer newLoanProductLoanCounter) {
        this.loanProductCounter = newLoanProductLoanCounter;
    }

    public boolean isGroupLoan() {
        return AccountType.fromInt(this.loanType).isGroupAccount();
    }

    public boolean isJLGLoan() {
        return AccountType.fromInt(this.loanType).isJLGAccount();
    }
    
    public boolean isGLIMLoan() {
        if (isGlimPaymentAsGroup()) { return (AccountType.fromInt(this.loanType).isGLIMAccount() && LoanApiConstants.EXCLUDED_STATUS_FOR_GLIM_PAYMENT_AS_GROUP
                .contains(this.status().getValue())); }
        return (AccountType.fromInt(this.loanType).isGLIMAccount());
    }

    public void updateInterestRateFrequencyType() {
        this.loanRepaymentScheduleDetail.updatenterestPeriodFrequencyType(this.loanProduct.getInterestPeriodFrequencyType());
    }

    public Integer getTermFrequency() {
        return this.termFrequency;
    }

    public Integer getTermPeriodFrequencyType() {
        return this.termPeriodFrequencyType;
    }

    public List<LoanTransaction> getLoanTransactions() {
        return this.loanTransactions;
    }
    
    
    public void addLoanTransaction(final LoanTransaction loanTransaction) {
        this.loanTransactions.add(loanTransaction) ;
    }

    public void setLoanStatus(final Integer loanStatus) {
        this.loanStatus = loanStatus;
    }
    
    public void validateExpectedDisbursementForHolidayAndNonWorkingDay(final WorkingDays workingDays,
            final boolean allowTransactionsOnHoliday, final List<Holiday> holidays, final boolean allowTransactionsOnNonWorkingDay) {
        // validate if disbursement date is a holiday or a non-working day
        validateDisbursementDateIsOnNonWorkingDay(workingDays, allowTransactionsOnNonWorkingDay);
        validateDisbursementDateIsOnHoliday(allowTransactionsOnHoliday, holidays);

    }

    private void validateActivityNotBeforeClientOrGroupTransferDate(final LoanEvent event, final LocalDate activityDate) {
        if (this.client != null && this.client.getOfficeJoiningLocalDate() != null) {
            final LocalDate clientOfficeJoiningDate = this.client.getOfficeJoiningLocalDate();
            if (activityDate.isBefore(clientOfficeJoiningDate)) {
                String errorMessage = null;
                String action = null;
                String postfix = null;
                switch (event) {
                    case LOAN_CREATED:
                        errorMessage = "The date on which a loan is submitted cannot be earlier than client's transfer date to this office";
                        action = "submittal";
                        postfix = "cannot.be.before.client.transfer.date";
                    break;
                    case LOAN_APPROVED:
                        errorMessage = "The date on which a loan is approved cannot be earlier than client's transfer date to this office";
                        action = "approval";
                        postfix = "cannot.be.before.client.transfer.date";
                    break;
                    case LOAN_APPROVAL_UNDO:
                        errorMessage = "The date on which a loan is approved cannot be earlier than client's transfer date to this office";
                        action = "approval";
                        postfix = "cannot.be.undone.before.client.transfer.date";
                    break;
                    case LOAN_DISBURSED:
                        errorMessage = "The date on which a loan is disbursed cannot be earlier than client's transfer date to this office";
                        action = "disbursal";
                        postfix = "cannot.be.before.client.transfer.date";
                    break;
                    case LOAN_DISBURSAL_UNDO:
                        errorMessage = "Cannot undo a disbursal done in another branch";
                        action = "disbursal";
                        postfix = "cannot.be.undone.before.client.transfer.date";
                    break;
                    case LOAN_REPAYMENT_OR_WAIVER:
                        errorMessage = "The date on which a repayment or waiver is made cannot be earlier than client's transfer date to this office";
                        action = "repayment.or.waiver";
                        postfix = "cannot.be.made.before.client.transfer.date";
                    break;
                    case LOAN_REJECTED:
                        errorMessage = "The date on which a loan is rejected cannot be earlier than client's transfer date to this office";
                        action = "reject";
                        postfix = "cannot.be.before.client.transfer.date";
                    break;
                    case LOAN_WITHDRAWN:
                        errorMessage = "The date on which a loan is withdrawn cannot be earlier than client's transfer date to this office";
                        action = "withdraw";
                        postfix = "cannot.be.before.client.transfer.date";
                    break;
                    case WRITE_OFF_OUTSTANDING:
                        errorMessage = "The date on which a write off is made cannot be earlier than client's transfer date to this office";
                        action = "writeoff";
                        postfix = "cannot.be.undone.before.client.transfer.date";
                    break;
                    case REPAID_IN_FULL:
                        errorMessage = "The date on which the loan is repaid in full cannot be earlier than client's transfer date to this office";
                        action = "close";
                        postfix = "cannot.be.undone.before.client.transfer.date";
                    break;
                    case LOAN_CHARGE_PAYMENT:
                        errorMessage = "The date on which a charge payment is made cannot be earlier than client's transfer date to this office";
                        action = "charge.payment";
                        postfix = "cannot.be.made.before.client.transfer.date";
                    break;
                    case LOAN_REFUND:
                        errorMessage = "The date on which a refund is made cannot be earlier than client's transfer date to this office";
                        action = "refund";
                        postfix = "cannot.be.made.before.client.transfer.date";
                    break;
                    case LOAN_DISBURSAL_UNDO_LAST:
                        errorMessage = "Cannot undo a last disbursal in another branch";
                        action = "disbursal";
                        postfix = "cannot.be.undone.before.client.transfer.date";
                    break;
                    default:
                    break;
                }
                throw new InvalidLoanStateTransitionException(action, postfix, errorMessage, clientOfficeJoiningDate);
            }
        }
    }

    private void validateActivityNotBeforeLastTransactionDate(final LoanEvent event, final LocalDate activityDate) {
        if (!(this.repaymentScheduleDetail().isInterestRecalculationEnabled() || this.loanProduct().isHoldGuaranteeFundsEnabled())) { return; }
        LocalDate lastTransactionDate = getLastUserTransactionDate();
        final LocalDate clientOfficeJoiningDate = this.client.getOfficeJoiningLocalDate();
        if (lastTransactionDate.isAfter(activityDate)) {
            String errorMessage = null;
            String action = null;
            String postfix = null;
            switch (event) {
                case LOAN_REPAYMENT_OR_WAIVER:
                    errorMessage = "The date on which a repayment or waiver is made cannot be earlier than last transaction date";
                    action = "repayment.or.waiver";
                    postfix = "cannot.be.made.before.last.transaction.date";
                break;
                case WRITE_OFF_OUTSTANDING:
                    errorMessage = "The date on which a write off is made cannot be earlier than last transaction date";
                    action = "writeoff";
                    postfix = "cannot.be.made.before.last.transaction.date";
                break;
                case LOAN_CHARGE_PAYMENT:
                    errorMessage = "The date on which a charge payment is made cannot be earlier than last transaction date";
                    action = "charge.payment";
                    postfix = "cannot.be.made.before.last.transaction.date";
                break;
                case LOAN_ADD_SUBSIDY:
                    errorMessage = "The date on which a subsidy payment is made cannot be earlier than last transaction date";
                    action = "add.subsidy";
                    postfix = "cannot.be.made.before.last.transaction.date";
                break;
                case LOAN_REVOKE_SUBSIDY:
                    errorMessage = "The date on which a subsidy is revoked cannot be earlier than last transaction date";
                    action = "revoke.subsidy";
                    postfix = "cannot.be.made.before.last.transaction.date";
                break;
                default:
                break;
            }
            throw new InvalidLoanStateTransitionException(action, postfix, errorMessage, clientOfficeJoiningDate);
        }
    }
    
    private void validatePrepayment(final LoanTransaction loanTransaction) {
        if(!this.isInterestRecalculationEnabledForProduct()){
            String action = "prepayment";
            String errorMessage = "Must be interest reclculation product for prepayment";
            String postfix = "must.be.interest.recalution.product";
            throw new InvalidLoanStateTransitionException(action, postfix, errorMessage);
        }
        
        Money totalOutstanding = Money.zero(getCurrency()); 
        for (LoanRepaymentScheduleInstallment installment : getRepaymentScheduleInstallments()) {
            if (!loanTransaction.getTransactionDate().isBefore(installment.getDueDate())) {
                if (installment.isNotFullyPaidOff()) {
                    String action = "prepayment";
                    String errorMessage = "Must clear dues before prepayment";
                    String postfix = "cannot.be.made.before.dues.cleared";
                    throw new InvalidLoanStateTransitionException(action, postfix, errorMessage);
                }
            } else {
                totalOutstanding = totalOutstanding.plus(installment.getPrincipalOutstanding(getCurrency()));
            }
        }
        if(loanTransaction.getAmount(getCurrency()).isGreaterThan(totalOutstanding)){
            String action = "prepayment";
            String errorMessage = "Must be equal or less than principal outstanding";
            String postfix = "cannot.be.more.than.principal.due";
            throw new InvalidLoanStateTransitionException(action, postfix, errorMessage);
        }
        
    }
    
    public List<LoanTransaction> getOrderedLoanTransactions() {
        final LoanTransactionComparator transactionComparator = new LoanTransactionComparator();
        Collections.sort(this.loanTransactions, transactionComparator);
        return this.loanTransactions;
    }

    public LocalDate getLastUserTransactionDate() {
        Collection<Integer> transactionType = new ArrayList<>();
        transactionType.add(LoanTransactionType.ACCRUAL.getValue());
        transactionType.add(LoanTransactionType.INCOME_POSTING.getValue());
        boolean excludeTypes = true;
        return getLastTransactioDateOfType(transactionType, excludeTypes);
    }
    
    private LocalDate getLastTransactioDateOfType(Collection<Integer> transactionType, boolean excludeTypes) {
        int size = this.getLoanTransactions().size();
        LocalDate lastTansactionDate = getDisbursementDate();
        List<LoanTransaction> transactions = this.getOrderedLoanTransactions();
        for (int i = size - 1; i >= 0; i--) {
            LoanTransaction loanTransaction = transactions.get(i);
            if (loanTransaction.isNotReversed() && (transactionType.contains(loanTransaction.getTypeOf().getValue()) ^ excludeTypes)) {
                lastTansactionDate = loanTransaction.getTransactionDate(); 
                break;
            }
        }
        return lastTansactionDate;
    }

    public LocalDate getLastRepaymentDate() {
        Collection<Integer> transactionType = new ArrayList<>();
        transactionType.add(LoanTransactionType.REPAYMENT.getValue());
        boolean excludeTypes = false;
        return getLastTransactioDateOfType(transactionType, excludeTypes);
    }

    public LocalDate getLastUserTransactionForChargeCalc() {
        LocalDate lastTransaction = getDisbursementDate();
        if (this.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            lastTransaction = getLastUserTransactionDate();
        }
        return lastTransaction;
    }

    public Set<LoanCharge> charges() {
        Set<LoanCharge> loanCharges = new HashSet<>();
        if (this.charges != null) {
            for (LoanCharge charge : this.charges) {
                if (charge.isActive()) {
                    loanCharges.add(charge);
                }
            }
        }
        return loanCharges;
    }
    
    public Set<LoanCharge> chargesCopy() {
        Set<LoanCharge> loanCharges = new HashSet<>();
        if (this.charges != null) {
            for (LoanCharge charge : this.charges) {
                if (charge.isActive()) {
                    loanCharges.add(LoanCharge.copyCharge(charge));
                }
            }
        }
        return loanCharges;
    }

    public Set<LoanTrancheCharge> trancheCharges() {
        Set<LoanTrancheCharge> loanCharges = new HashSet<>();
        if (this.trancheCharges != null) {
            for (LoanTrancheCharge charge : this.trancheCharges) {
                loanCharges.add(charge);
            }
        }
        return loanCharges;
    }

    public List<LoanInstallmentCharge> generateInstallmentLoanCharges(final LoanCharge loanCharge) {
    	final List<LoanInstallmentCharge> loanChargePerInstallments = new ArrayList<>();
    	BigDecimal totalCapitalizAmount = BigDecimal.ZERO;
        if (loanCharge.isInstalmentFee()) {
            for (final LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
            	if(installment.isRecalculatedInterestComponent()){
            		continue;
            	}
                BigDecimal amount = BigDecimal.ZERO;
                if (loanCharge.getChargeCalculation().isFlat()) {
                    amount = loanCharge.amountOrPercentage();
                } else if (loanCharge.isInstalmentFee() && loanCharge.getChargeCalculation().isPercentageOfDisbursementAmount()) {
                    BigDecimal chargeAmountPerInstallment = BigDecimal.ZERO;
                    for(GroupLoanIndividualMonitoring glimMember : this.glimList) {
                        Set<GroupLoanIndividualMonitoringCharge> glimCharges = glimMember.getGroupLoanIndividualMonitoringCharges();
                        for(GroupLoanIndividualMonitoringCharge glimCharge : glimCharges) {
                            if(glimCharge.getCharge().getId().equals(loanCharge.getCharge().getId())) {
                                BigDecimal chargeAmount  = MathUtility.isNull(glimCharge.getRevisedFeeAmount())?glimCharge.getFeeAmount():glimCharge.getRevisedFeeAmount();
                                BigDecimal charge = Money.of(getCurrency(), BigDecimal.valueOf(chargeAmount.doubleValue()/this.fetchNumberOfInstallmensAfterExceptions())).getAmount();
                                if(this.fetchNumberOfInstallmensAfterExceptions() != installment.getInstallmentNumber()){
                                    chargeAmountPerInstallment = MathUtility.add(chargeAmountPerInstallment,charge);
                                }else{
                                    BigDecimal chargeBeforeLastInstallment =  charge.multiply(BigDecimal.valueOf(this.fetchNumberOfInstallmensAfterExceptions()-1));
                                    chargeAmountPerInstallment = chargeAmountPerInstallment.add(chargeAmount.subtract(chargeBeforeLastInstallment));
                                }
                            }
                        }
                    }
                    amount = chargeAmountPerInstallment;
                } else if (loanCharge.isInstalmentFee() && loanCharge.getChargeCalculation().isSlabBased()) {
                    if (loanCharge.isCapitalized()) {
                        amount = getCapitalizedInstallmentChargeAmount(loanCharge, totalCapitalizAmount, installment);
                        totalCapitalizAmount = totalCapitalizAmount.add(amount);
                    } else {
                        amount = MathUtility.getInstallmentAmount(loanCharge.amount(), this.fetchNumberOfInstallmensAfterExceptions(),
                                this.getCurrency(), installment.getInstallmentNumber());
                    }
                } else {
                    amount = calculateInstallmentChargeAmount(loanCharge.getChargeCalculation(), loanCharge.getPercentage(), installment)
                            .getAmount();
                }
                
                final LoanInstallmentCharge loanInstallmentCharge = new LoanInstallmentCharge(amount, loanCharge, installment);
                loanChargePerInstallments.add(loanInstallmentCharge);
            }
        }
        return loanChargePerInstallments;
    }

    private BigDecimal getCapitalizedInstallmentChargeAmount(final LoanCharge loanCharge, BigDecimal totalCapitalizAmount,
            final LoanRepaymentScheduleInstallment installment) {
        BigDecimal amount;
        if(installment.getInstallmentNumber()!=this.fetchNumberOfInstallmensAfterExceptions()){
        	amount = this.calculatedInstallmentAmount.subtract(installment.getPrincipal(this.getCurrency()).getAmount()).subtract(installment.getInterestCharged(this.getCurrency()).getAmount());
        	
        }else{
        	amount =loanCharge.getAmount(this.getCurrency()).getAmount().subtract(totalCapitalizAmount);
        }
        return amount;
    }

    public void validateAccountStatus(final LoanEvent event) {

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        switch (event) {
            case LOAN_CREATED:
            break;
            case LOAN_APPROVED:
                if (!isSubmittedAndPendingApproval()) {
                    final String defaultUserMessage = "Loan Account Approval is not allowed. Loan Account is not in submitted and pending approval state.";
                    final ApiParameterError error = ApiParameterError.generalError(
                            "error.msg.loan.approve.account.is.not.submitted.and.pending.state", defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case LOAN_APPROVAL_UNDO:
                if (!isApproved()) {
                    final String defaultUserMessage = "Loan Account Undo Approval is not allowed. Loan Account is not in approved state.";
                    final ApiParameterError error = ApiParameterError.generalError("error.msg.loan.undo.approval.account.is.not.approved",
                            defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case LOAN_DISBURSED:
                if ((!(isApproved() && isNotDisbursed()) && !this.loanProduct.isMultiDisburseLoan())
                        || (this.loanProduct.isMultiDisburseLoan() && !isAllTranchesNotDisbursed())) {
                    final String defaultUserMessage = "Loan Disbursal is not allowed. Loan Account is not in approved and not disbursed state.";
                    final ApiParameterError error = ApiParameterError.generalError(
                            "error.msg.loan.disbursal.account.is.not.approve.not.disbursed.state", defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case LOAN_DISBURSAL_UNDO:
                if (!isOpen()) {
                    final String defaultUserMessage = "Loan Undo disbursal is not allowed. Loan Account is not active.";
                    final ApiParameterError error = ApiParameterError.generalError("error.msg.loan.undo.disbursal.account.is.not.active",
                            defaultUserMessage);
                    dataValidationErrors.add(error);
                }
                if(isOpen() && this.isTopup()){
                    final String defaultUserMessage = "Loan Undo disbursal is not allowed on Topup Loans";
                    final ApiParameterError error = ApiParameterError.generalError("error.msg.loan.undo.disbursal.not.allowed.on.topup.loan",
                            defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case WAIVER:
                if (!isOpen()) {
                    final String defaultUserMessage = "Loan Waiver is not allowed. Loan Account is not active.";
                    final ApiParameterError error = ApiParameterError.generalError(
                            "error.msg.loan.waiver.account.is.not.active", defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case LOAN_REPAYMENT:
                if (!LoanApiConstants.loanStatusAllowedForPayment.contains(this.status())) {
                    final String defaultUserMessage = "Loan Repayment is not allowed. Loan Account is not active.";
                    final ApiParameterError error = ApiParameterError.generalError(
                            "error.msg.loan.repayment.account.is.not.active", defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case LOAN_REJECTED:
                if (!isSubmittedAndPendingApproval()) {
                    final String defaultUserMessage = "Loan application cannot be rejected. Loan Account is not in Submitted and Pending approval state.";
                    final ApiParameterError error = ApiParameterError.generalError(
                            "error.msg.loan.reject.account.is.not.submitted.pending.approval.state", defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case LOAN_WITHDRAWN:
                if (!isSubmittedAndPendingApproval()) {
                    final String defaultUserMessage = "Loan application cannot be withdrawn. Loan Account is not in Submitted and Pending approval state.";
                    final ApiParameterError error = ApiParameterError.generalError(
                            "error.msg.loan.withdrawn.account.is.not.submitted.pending.approval.state", defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case WRITE_OFF_OUTSTANDING:
                if (!isOpen()) {
                    final String defaultUserMessage = "Loan Written off is not allowed. Loan Account is not active.";
                    final ApiParameterError error = ApiParameterError.generalError("error.msg.loan.writtenoff.account.is.not.active",
                            defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case WRITE_OFF_OUTSTANDING_UNDO:
                if (!isClosedWrittenOff()) {
                    final String defaultUserMessage = "Loan Undo Written off is not allowed. Loan Account is not Written off.";
                    final ApiParameterError error = ApiParameterError.generalError(
                            "error.msg.loan.undo.writtenoff.account.is.not.written.off", defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case REPAID_IN_FULL:
            break;
            case LOAN_CHARGE_PAYMENT:
                if (!isOpen()) {
                    final String defaultUserMessage = "Charge payment is not allowed. Loan Account is not Active.";
                    final ApiParameterError error = ApiParameterError.generalError("error.msg.loan.charge.payment.account.is.not.active",
                            defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case LOAN_CLOSED:
                if (!isOpen()) {
                    final String defaultUserMessage = "Closing Loan Account is not allowed. Loan Account is not Active.";
                    final ApiParameterError error = ApiParameterError.generalError("error.msg.loan.close.account.is.not.active",
                            defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case LOAN_EDIT_MULTI_DISBURSE_DATE:
                if (isClosed()) {
                    final String defaultUserMessage = "Edit disbursement is not allowed. Loan Account is not active.";
                    final ApiParameterError error = ApiParameterError.generalError(
                            "error.msg.loan.edit.disbursement.account.is.not.active", defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case LOAN_RECOVERY_PAYMENT:
                if (!isClosedWrittenOff()) {
                    final String defaultUserMessage = "Recovery repayments may only be made on loans which are written off";
                    final ApiParameterError error = ApiParameterError.generalError("error.msg.loan.account.is.not.written.off",
                            defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case LOAN_REFUND:
                if (!isOpen()) {
                    final String defaultUserMessage = "Loan Refund is not allowed. Loan Account is not active.";
                    final ApiParameterError error = ApiParameterError.generalError("error.msg.loan.refund.account.is.not.active",
                            defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case LOAN_DISBURSAL_UNDO_LAST:
                if (!isOpen()) {
                    final String defaultUserMessage = "Loan Undo last disbursal is not allowed. Loan Account is not active.";
                    final ApiParameterError error = ApiParameterError.generalError(
                            "error.msg.loan.undo.last.disbursal.account.is.not.active", defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            case LOAN_FORECLOSURE:
                if (!isOpen()) {
                    final String defaultUserMessage = "Loan foreclosure is not allowed. Loan Account is not active.";
                    final ApiParameterError error = ApiParameterError.generalError(
                            "error.msg.loan.foreclosure.account.is.not.active", defaultUserMessage);
                    dataValidationErrors.add(error);
                }
            break;
            default:
            break;
        }

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

    }

    public LoanCharge fetchLoanChargesById(Long id) {
        LoanCharge charge = null;
        for (LoanCharge loanCharge : this.charges) {
            if (id.equals(loanCharge.getId())) {
                charge = loanCharge;
                break;
            }
        }
        return charge;
    }

    private List<Long> fetchAllLoanChargeIds() {
        List<Long> list = new ArrayList<>();
        for (LoanCharge loanCharge : this.charges) {
            if (loanCharge.getId() != null) {
                list.add(loanCharge.getId());
            }
        }
        return list;
    }

    public List<LoanDisbursementDetails> getDisbursementDetails() {
        return this.disbursementDetails;
    }
    
    public boolean hasUndisbursedTranches() {
        boolean hasActiveUndisbursedTranches = false;
        for(LoanDisbursementDetails loanDisbursementDetails : this.getDisbursementDetails()){
            if (loanDisbursementDetails.actualDisbursementDate() == null) {
                hasActiveUndisbursedTranches = true;
                break;
            }
        }
        return hasActiveUndisbursedTranches;
    }

    public ChangedTransactionDetail updateDisbursementDateAndAmountForTranche(final LoanDisbursementDetails disbursementDetails,
            final JsonCommand command, final Map<String, Object> actualChanges, final ScheduleGeneratorDTO scheduleGeneratorDTO,
            final AppUser currentUser) {
        final Locale locale = command.extractLocale();
        validateAccountStatus(LoanEvent.LOAN_EDIT_MULTI_DISBURSE_DATE);
        final BigDecimal principal = command.bigDecimalValueOfParameterNamed(LoanApiConstants.updatedDisbursementPrincipalParameterName,
                locale);
        final LocalDate expectedDisbursementDate = command
                .localDateValueOfParameterNamed(LoanApiConstants.updatedDisbursementDateParameterName);
        disbursementDetails.updateExpectedDisbursementDateAndAmount(expectedDisbursementDate.toDate(), principal);
        recalculateDisbursementCharge(disbursementDetails, charges);
        actualChanges.put(LoanApiConstants.disbursementDateParameterName,
                command.stringValueOfParameterNamed(LoanApiConstants.disbursementDateParameterName));
        actualChanges.put(LoanApiConstants.disbursementIdParameterName,
                command.stringValueOfParameterNamed(LoanApiConstants.disbursementIdParameterName));
        actualChanges.put(LoanApiConstants.disbursementPrincipalParameterName,
                command.bigDecimalValueOfParameterNamed(LoanApiConstants.disbursementPrincipalParameterName, locale));

        Collection<LoanDisbursementDetails> loanDisburseDetails = this.getDisbursementDetails();
        BigDecimal setPrincipalAmount = BigDecimal.ZERO;
        for (LoanDisbursementDetails details : loanDisburseDetails) {
            if (details.actualDisbursementDate() != null) {
                setPrincipalAmount = setPrincipalAmount.add(details.principal());
            }
        }

        this.loanRepaymentScheduleDetail.setPrincipal(setPrincipalAmount);
        ChangedTransactionDetail changedTransactionDetail =  recalculateScheduleFromLastTransaction(scheduleGeneratorDTO, currentUser);
     
        for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
            mapEntry.getValue().updateLoan(this);
            this.loanTransactions.add(mapEntry.getValue());
        }

        return changedTransactionDetail;
    }

    public BigDecimal retriveLastEmiAmount() {
        BigDecimal emiAmount = this.fixedEmiAmount;
        Date startDate = this.getDisbursementDate().toDate();
        for (LoanTermVariations loanTermVariations : this.loanTermVariations) {
            if (loanTermVariations.isActive() && loanTermVariations.getTermType().isEMIAmountVariation()
                    && !startDate.after(loanTermVariations.getTermApplicableFrom())) {
                startDate = loanTermVariations.getTermApplicableFrom();
                emiAmount = loanTermVariations.getTermValue();
            }
        }
        return emiAmount;
    }

    public LoanRepaymentScheduleInstallment fetchRepaymentScheduleInstallment(final Integer installmentNumber) {
        LoanRepaymentScheduleInstallment installment = null;
        if (installmentNumber == null) { return installment; }
        for (final LoanRepaymentScheduleInstallment scheduleInstallment : this.repaymentScheduleInstallments) {
            if (scheduleInstallment.getInstallmentNumber().equals(installmentNumber)) {
                installment = scheduleInstallment;
                break;
            }
        }
        return installment;
    }

    public BigDecimal getApprovedPrincipal() {
        return this.approvedPrincipal;
    }

    public BigDecimal getTotalOverpaid() {
        return this.totalOverpaid;
    }

    public void updateIsInterestRecalculationEnabled() {
        this.loanRepaymentScheduleDetail.updateIsInterestRecalculationEnabled(isInterestRecalculationEnabledForProduct());
    }

    public LoanInterestRecalculationDetails loanInterestRecalculationDetails() {
        return this.loanInterestRecalculationDetails;
    }

    public Long loanInterestRecalculationDetailId() {
        if (loanInterestRecalculationDetails() != null) { return this.loanInterestRecalculationDetails.getId(); }
        return null;
    }

    public LocalDate getExpectedMaturityDate() {
        LocalDate expectedMaturityDate = null;
        if (this.expectedMaturityDate != null) {
            expectedMaturityDate = new LocalDate(this.expectedMaturityDate);
        }
        return expectedMaturityDate;
    }

    public LocalDate getMaturityDate() {
        LocalDate maturityDate = getExpectedMaturityDate();
        if (this.actualMaturityDate != null) {
            maturityDate = new LocalDate(this.actualMaturityDate);
        }
        return maturityDate;
    }

    public ChangedTransactionDetail recalculateScheduleFromLastTransaction(final ScheduleGeneratorDTO generatorDTO,
            final List<Long> existingTransactionIds, final List<Long> existingReversedTransactionIds, final AppUser currentUser) {
        existingTransactionIds.addAll(findExistingTransactionIds());
        existingReversedTransactionIds.addAll(findExistingReversedTransactionIds());
        /*
         * LocalDate recalculateFrom = null; List<LoanTransaction>
         * loanTransactions =
         * this.retreiveListOfTransactionsPostDisbursementExcludeAccruals(); for
         * (LoanTransaction loanTransaction : loanTransactions) { if
         * (recalculateFrom == null ||
         * loanTransaction.getTransactionDate().isAfter(recalculateFrom)) {
         * recalculateFrom = loanTransaction.getTransactionDate(); } }
         * generatorDTO.setRecalculateFrom(recalculateFrom);
         */
        return recalculateScheduleFromLastTransaction(generatorDTO, currentUser);

    }
    
    private void recalculateSchedule(final ScheduleGeneratorDTO generatorDTO, final AppUser currentUser, final boolean isDisburseInitiated) {
        if ((isDisburseInitiated || this.isOpen()) && this.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            regenerateRepaymentScheduleWithInterestRecalculation(generatorDTO, currentUser);
            updateSummaryWithTotalFeeChargesDueAtDisbursement();
        } else {
            regenerateRepaymentSchedule(generatorDTO, currentUser);
        }
    }

    private ChangedTransactionDetail recalculateScheduleFromLastTransaction(final ScheduleGeneratorDTO generatorDTO,
            final AppUser currentUser) {
        final boolean isDisburseInitiated = false;
        recalculateSchedule(generatorDTO, currentUser, isDisburseInitiated);
        return processTransactions();
    }

    public ChangedTransactionDetail handleRegenerateRepaymentScheduleWithInterestRecalculation(final ScheduleGeneratorDTO generatorDTO,
            final AppUser currentUser) {
        regenerateRepaymentScheduleWithInterestRecalculation(generatorDTO, currentUser);
        return processTransactions();

    }

    public ChangedTransactionDetail processTransactions() {
        if(!this.isOpen()){
            return new ChangedTransactionDetail();
        }
        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                .determineProcessor(this.transactionProcessingStrategy);
        final List<LoanTransaction> allNonContraTransactionsPostDisbursement = retreiveListOfTransactionsPostDisbursement();
        ChangedTransactionDetail changedTransactionDetail = loanRepaymentScheduleTransactionProcessor.handleTransaction(
                getDisbursementDate(), allNonContraTransactionsPostDisbursement, getCurrency(), this.repaymentScheduleInstallments,
                charges(), disbursementDetails);
        for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
            mapEntry.getValue().updateLoan(this);
        }
        /***
         * Commented since throwing exception if external id present for one of
         * the transactions. for this need to save the reversed transactions
         * first and then new transactions.
         */
        this.loanTransactions.addAll(changedTransactionDetail.getNewTransactionMappings().values());

        updateLoanSummaryAndStatus();

        this.loanTransactions.removeAll(changedTransactionDetail.getNewTransactionMappings().values());

        return changedTransactionDetail;
    }

    public void regenerateRepaymentScheduleWithInterestRecalculation(final ScheduleGeneratorDTO generatorDTO, final AppUser currentUser) {

        LocalDate lastTransactionDate = getLastUserTransactionDate();
        final LoanScheduleDTO loanSchedule = getRecalculatedSchedule(generatorDTO);
        if (loanSchedule == null) { return; }
        updateLoanSchedule(loanSchedule.getInstallments(), currentUser);
        this.interestRecalculatedOn = DateUtils.getDateOfTenant();
        LocalDate lastRepaymentDate = this.getLastRepaymentPeriodDueDate(true);
        Set<LoanCharge> charges = this.charges();
        for (LoanCharge loanCharge : charges) {
            if (!loanCharge.isDueAtDisbursement()) {
                updateOverdueScheduleInstallment(loanCharge);
                if (loanCharge.getDueLocalDate() == null || (!lastRepaymentDate.isBefore(loanCharge.getDueLocalDate()))) {
                    if (!loanCharge.isWaived()
                            && (loanCharge.getDueLocalDate() == null || !lastTransactionDate.isAfter(loanCharge.getDueLocalDate()))) {
                        recalculateLoanCharge(loanCharge, generatorDTO.getPenaltyWaitPeriod());
                        loanCharge.updateWaivedAmount(getCurrency());
                    }
                } else {
                    loanCharge.setActive(false);
                }
            }
        }

        processPostDisbursementTransactions();
        processIncomeTransactions(currentUser);
    }

    private void updateLoanChargesPaidBy(LoanTransaction accrual, HashMap<String, Object> feeDetails,
            LoanRepaymentScheduleInstallment installment) {
        @SuppressWarnings("unchecked")
        List<LoanCharge> loanCharges = (List<LoanCharge>) feeDetails.get("loanCharges");
        @SuppressWarnings("unchecked")
        List<LoanInstallmentCharge> loanInstallmentCharges = (List<LoanInstallmentCharge>) feeDetails.get("loanInstallmentCharges");
        if (loanCharges != null) {
            for (LoanCharge loanCharge : loanCharges) {
                Integer installmentNumber = null == installment ? null : installment.getInstallmentNumber();
                final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(accrual, loanCharge, loanCharge.getAmount(getCurrency())
                        .getAmount(), installmentNumber);
                accrual.getLoanChargesPaid().add(loanChargePaidBy);
            }
        }
        if (loanInstallmentCharges != null) {
            for (LoanInstallmentCharge loanInstallmentCharge : loanInstallmentCharges) {
                Integer installmentNumber = null == loanInstallmentCharge.getInstallment() ? null : loanInstallmentCharge.getInstallment()
                        .getInstallmentNumber();
                final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(accrual, loanInstallmentCharge.getLoancharge(),
                        loanInstallmentCharge.getAmount(getCurrency()).getAmount(), installmentNumber);
                accrual.getLoanChargesPaid().add(loanChargePaidBy);
            }
        }
    }

    public void processIncomeTransactions(AppUser currentUser) {
        if (!this.isNpa() && this.loanInterestRecalculationDetails != null && this.loanInterestRecalculationDetails.isCompoundingToBePostedAsTransaction()) {
            LocalDate lastCompoundingDate = this.getDisbursementDate();
            List<LoanInterestRecalcualtionAdditionalDetails> compoundingDetails = extractInterestRecalculationAdditionalDetails();
            List<LoanTransaction> incomeTransactions = retreiveListOfIncomePostingTransactions();
            List<LoanTransaction> accrualTransactions = retreiveListOfAccrualTransactions();
            for (LoanInterestRecalcualtionAdditionalDetails compoundingDetail : compoundingDetails) {
                if (!compoundingDetail.getEffectiveDate().isBefore(DateUtils.getLocalDateOfTenant())) {
                    break;
                }
                LoanTransaction incomeTransaction = getTransactionForDate(incomeTransactions, compoundingDetail.getEffectiveDate());
                LoanTransaction accrualTransaction = getTransactionForDate(accrualTransactions, compoundingDetail.getEffectiveDate());
                addUpdateIncomeAndAccrualTransaction(compoundingDetail, lastCompoundingDate, incomeTransaction, accrualTransaction);
                lastCompoundingDate = compoundingDetail.getEffectiveDate();
            }
            LoanRepaymentScheduleInstallment lastInstallment = this.repaymentScheduleInstallments.get(this.repaymentScheduleInstallments
                    .size() - 1);
            reverseTransactionsPostEffectiveDate(incomeTransactions, lastInstallment.getDueDate());
            reverseTransactionsPostEffectiveDate(accrualTransactions, lastInstallment.getDueDate());
            applyAccurals(currentUser);
        }
    }

    private void reverseTransactionsOnOrAfter(List<LoanTransaction> transactions, Date date) {
        LocalDate refDate = new LocalDate(date);
        for (LoanTransaction loanTransaction : transactions) {
            if (!loanTransaction.getTransactionDate().isBefore(refDate)) {
                loanTransaction.reverse();
            }
        }
    }

    private void addUpdateIncomeAndAccrualTransaction(LoanInterestRecalcualtionAdditionalDetails compoundingDetail,
            LocalDate lastCompoundingDate, LoanTransaction existingIncomeTransaction, LoanTransaction existingAccrualTransaction) {
        BigDecimal interest = BigDecimal.ZERO;
        BigDecimal fee = BigDecimal.ZERO;
        BigDecimal penalties = BigDecimal.ZERO;
        HashMap<String, Object> feeDetails = new HashMap<>();

        if (this.loanInterestRecalculationDetails.getInterestRecalculationCompoundingMethod().equals(
                InterestRecalculationCompoundingMethod.INTEREST)) {
            interest = compoundingDetail.getAmount();
        } else if (this.loanInterestRecalculationDetails.getInterestRecalculationCompoundingMethod().equals(
                InterestRecalculationCompoundingMethod.FEE)) {
            determineFeeDetails(lastCompoundingDate, compoundingDetail.getEffectiveDate(), feeDetails);
            fee = (BigDecimal) feeDetails.get("fee");
            penalties = (BigDecimal) feeDetails.get("penalties");
        } else if (this.loanInterestRecalculationDetails.getInterestRecalculationCompoundingMethod().equals(
                InterestRecalculationCompoundingMethod.INTEREST_AND_FEE)) {
            determineFeeDetails(lastCompoundingDate, compoundingDetail.getEffectiveDate(), feeDetails);
            fee = (BigDecimal) feeDetails.get("fee");
            penalties = (BigDecimal) feeDetails.get("penalties");
            interest = compoundingDetail.getAmount().subtract(fee).subtract(penalties);
        }

        if (existingIncomeTransaction == null) {
            LoanTransaction transaction = LoanTransaction.incomePosting(this, this.getOffice(), compoundingDetail.getEffectiveDate()
                    .toDate(), compoundingDetail.getAmount(), interest, fee, penalties);
            this.loanTransactions.add(transaction);
        } else if (existingIncomeTransaction.getAmount(getCurrency()).getAmount().compareTo(compoundingDetail.getAmount()) != 0) {
            existingIncomeTransaction.reverse();
            LoanTransaction transaction = LoanTransaction.incomePosting(this, this.getOffice(), compoundingDetail.getEffectiveDate()
                    .toDate(), compoundingDetail.getAmount(), interest, fee, penalties);
            this.loanTransactions.add(transaction);
        }

        if (isPeriodicAccrualAccountingEnabledOnLoanProduct()) {
            if (existingAccrualTransaction == null) {
                LoanTransaction accrual = LoanTransaction.accrueTransaction(this, this.getOffice(), compoundingDetail.getEffectiveDate(),
                        compoundingDetail.getAmount(), interest, fee, penalties);
                updateLoanChargesPaidBy(accrual, feeDetails, null);
                this.loanTransactions.add(accrual);
            } else if (existingAccrualTransaction.getAmount(getCurrency()).getAmount().compareTo(compoundingDetail.getAmount()) != 0) {
                existingAccrualTransaction.reverse();
                LoanTransaction accrual = LoanTransaction.accrueTransaction(this, this.getOffice(), compoundingDetail.getEffectiveDate(),
                        compoundingDetail.getAmount(), interest, fee, penalties);
                updateLoanChargesPaidBy(accrual, feeDetails, null);
                this.loanTransactions.add(accrual);
            }
        }
        updateLoanOutstandingBalaces();
    }

    private void determineFeeDetails(LocalDate fromDate, LocalDate toDate, HashMap<String, Object> feeDetails) {
        BigDecimal fee = BigDecimal.ZERO;
        BigDecimal penalties = BigDecimal.ZERO;
        
        List<Integer> installments = new ArrayList<>();
        for (LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment : this.repaymentScheduleInstallments) {
            if (loanRepaymentScheduleInstallment.getDueDate().isAfter(fromDate)
                    && !loanRepaymentScheduleInstallment.getDueDate().isAfter(toDate)) {
                installments.add(loanRepaymentScheduleInstallment.getInstallmentNumber());
            }
        }

        List<LoanCharge> loanCharges = new ArrayList<>();
        List<LoanInstallmentCharge> loanInstallmentCharges = new ArrayList<>();
        for (LoanCharge loanCharge : this.charges()) {
            if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(fromDate, toDate)) {
                if (loanCharge.isPenaltyCharge() && !loanCharge.isInstalmentFee()) {
                    penalties = penalties.add(loanCharge.amount());
                    loanCharges.add(loanCharge);
                } else if (!loanCharge.isInstalmentFee()) {
                    fee = fee.add(loanCharge.amount());
                    loanCharges.add(loanCharge);
                }
            } else if (loanCharge.isInstalmentFee()) {
                for (LoanInstallmentCharge installmentCharge : loanCharge.installmentCharges()) {
                    if (installments.contains(installmentCharge.getRepaymentInstallment().getInstallmentNumber())) {
                        fee = fee.add(installmentCharge.getAmount());
                        loanInstallmentCharges.add(installmentCharge);
                    }
                }
            }
        }

        feeDetails.put("fee", fee);
        feeDetails.put("penalties", penalties);
        feeDetails.put("loanCharges", loanCharges);
        feeDetails.put("loanInstallmentCharges", loanInstallmentCharges);
    }

    private LoanTransaction getTransactionForDate(List<LoanTransaction> transactions, LocalDate effectiveDate) {
        for (LoanTransaction loanTransaction : transactions) {
            if (loanTransaction.getTransactionDate().isEqual(effectiveDate)) { return loanTransaction; }
        }
        return null;
    }

    private void reverseTransactionsPostEffectiveDate(List<LoanTransaction> transactions, LocalDate effectiveDate) {
        for (LoanTransaction loanTransaction : transactions) {
            if (loanTransaction.getTransactionDate().isAfter(effectiveDate)) {
                loanTransaction.reverse();
            }
        }
    }

    private List<LoanInterestRecalcualtionAdditionalDetails> extractInterestRecalculationAdditionalDetails() {
        List<LoanInterestRecalcualtionAdditionalDetails> retDetails = new ArrayList<>();
        if (null != this.repaymentScheduleInstallments && this.repaymentScheduleInstallments.size() > 0) {
            Iterator<LoanRepaymentScheduleInstallment> installmentsItr = this.repaymentScheduleInstallments.iterator();
            while (installmentsItr.hasNext()) {
                LoanRepaymentScheduleInstallment installment = installmentsItr.next();
                if (null != installment.getLoanCompoundingDetails()) {
                    retDetails.addAll(installment.getLoanCompoundingDetails());
                }
            }
        }
        Collections.sort(retDetails, new Comparator<LoanInterestRecalcualtionAdditionalDetails>() {

            @Override
            public int compare(LoanInterestRecalcualtionAdditionalDetails first, LoanInterestRecalcualtionAdditionalDetails second) {
                return first.getEffectiveDate().compareTo(second.getEffectiveDate());
            }
        });
        return retDetails;
    }

    public void processPostDisbursementTransactions() {
        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                .determineProcessor(this.transactionProcessingStrategy);
        final List<LoanTransaction> allNonContraTransactionsPostDisbursement = retreiveListOfTransactionsPostDisbursement();
        final List<LoanTransaction> copyTransactions = new ArrayList<>();
        if (allNonContraTransactionsPostDisbursement.size() > 0) {
            for (LoanTransaction loanTransaction : allNonContraTransactionsPostDisbursement) {
                copyTransactions.add(LoanTransaction.copyTransactionProperties(loanTransaction));
            }
            loanRepaymentScheduleTransactionProcessor.handleTransaction(getDisbursementDate(), copyTransactions, getCurrency(),
                    this.repaymentScheduleInstallments, charges(), disbursementDetails);

            updateLoanSummaryDerivedFields();
        }
    }

    private LoanScheduleDTO getRecalculatedSchedule(final ScheduleGeneratorDTO generatorDTO) {

        if (!this.repaymentScheduleDetail().isInterestRecalculationEnabled()) { return null; }
        final InterestMethod interestMethod = this.loanRepaymentScheduleDetail.getInterestMethod();
        final LoanScheduleGenerator loanScheduleGenerator = generatorDTO.getLoanScheduleFactory().create(interestMethod);

        final RoundingMode roundingMode = MoneyHelper.getRoundingMode();
        final MathContext mc = new MathContext(8, roundingMode);
        final LoanApplicationTerms loanApplicationTerms = constructLoanApplicationTerms(generatorDTO);
        updateInstallmentAmountForGlim(loanApplicationTerms);
        loanApplicationTerms.updateTotalInterestDueForGlim(this.glimList);
        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                .determineProcessor(this.transactionProcessingStrategy);
        final BigDecimal firstFixedInstallmentEmiAmount = GroupLoanIndividualMonitoringAssembler.calculateGlimFirstInstallmentAmount(loanApplicationTerms);
    	loanApplicationTerms.setFirstFixedEmiAmount(firstFixedInstallmentEmiAmount);
        if (this.loanProduct.adjustFirstEMIAmount() && !this.isOpen()) {
            final BigDecimal firstInstallmentEmiAmount = loanScheduleGenerator.calculateFirstInstallmentAmount(mc, loanApplicationTerms,
                    charges(), generatorDTO.getHolidayDetailDTO());
            loanApplicationTerms.setFirstEmiAmount(firstInstallmentEmiAmount);
            this.firstEmiAmount = firstInstallmentEmiAmount;
        }
        if (!this.getDisbursementDate().isBefore(DateUtils.getLocalDateOfTenant()) && this.isInterestRecalculationEnabledForProduct()
                && this.loanProduct().isAdjustInterestForRounding()) {
            loanApplicationTerms.setAdjustLastInstallmentInterestForRounding(true);
        }
        return loanScheduleGenerator.rescheduleNextInstallments(mc, loanApplicationTerms, this, generatorDTO.getHolidayDetailDTO(),
                retreiveListOfTransactionsPostDisbursementExcludeAccruals(),loanRepaymentScheduleTransactionProcessor, generatorDTO.getRecalculateFrom());
    }

    public LoanRepaymentScheduleInstallment fetchPrepaymentDetail(final ScheduleGeneratorDTO scheduleGeneratorDTO, final LocalDate onDate,
            final boolean calcualteInterestTillDate) {
        LoanRepaymentScheduleInstallment installment = null;

        if (this.loanRepaymentScheduleDetail.isInterestRecalculationEnabled()) {
            final RoundingMode roundingMode = MoneyHelper.getRoundingMode();
            final MathContext mc = new MathContext(8, roundingMode);

            final InterestMethod interestMethod = this.loanRepaymentScheduleDetail.getInterestMethod();
            final LoanApplicationTerms loanApplicationTerms = constructLoanApplicationTerms(scheduleGeneratorDTO);

            final LoanScheduleGenerator loanScheduleGenerator = scheduleGeneratorDTO.getLoanScheduleFactory().create(interestMethod);
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                    .determineProcessor(this.transactionProcessingStrategy);
            installment = loanScheduleGenerator.calculatePrepaymentAmount(getCurrency(), onDate, loanApplicationTerms, mc, this,
                    scheduleGeneratorDTO.getHolidayDetailDTO(), loanRepaymentScheduleTransactionProcessor);
        }else if(calcualteInterestTillDate){
            installment = fetchLoanForeclosureDetail(onDate);
        } else {
            installment = this.getTotalOutstandingOnLoan();
        }
        return installment;
    }

    public LoanApplicationTerms constructLoanApplicationTerms(final ScheduleGeneratorDTO scheduleGeneratorDTO) {
        final Integer loanTermFrequency = this.termFrequency;
        final PeriodFrequencyType loanTermPeriodFrequencyType = PeriodFrequencyType.fromInt(this.termPeriodFrequencyType);
        NthDayType nthDayType = null;
        DayOfWeekType dayOfWeekType = null;
        boolean reduceDiscountFromPricipal = this.discountOnDisbursalAmount != null
                && !(this.isOpen() || (scheduleGeneratorDTO.getBusinessEvent() != null && scheduleGeneratorDTO.getBusinessEvent().equals(
                        BUSINESS_EVENTS.LOAN_DISBURSAL)));
        final List<DisbursementData> disbursementData = new ArrayList<>();
        for (LoanDisbursementDetails disbursementDetails : this.disbursementDetails) {
            if (disbursementDetails.isActive()) {
                DisbursementData data = disbursementDetails.toData();
                if (reduceDiscountFromPricipal
                        && disbursementDetails.getDisbursementDateAsLocalDate().isEqual(getDisbursementDate())) {
                    data.reducePrincipal(this.discountOnDisbursalAmount);
                }
                disbursementData.add(data);
            }
        }

        Calendar calendar = scheduleGeneratorDTO.getCalendar();
        if (calendar != null) {
            nthDayType = CalendarUtils.getRepeatsOnNthDayOfMonth(calendar.getRecurrence());
            dayOfWeekType = DayOfWeekType.fromInt(CalendarUtils.getRepeatsOnDay(calendar.getRecurrence()).getValue());
        }
        HolidayDetailDTO holidayDetailDTO = scheduleGeneratorDTO.getHolidayDetailDTO();
        CalendarInstance restCalendarInstance = null;
        CalendarInstance compoundingCalendarInstance = null;
        RecalculationFrequencyType recalculationFrequencyType = null;
        InterestRecalculationCompoundingMethod compoundingMethod = null;
        RecalculationFrequencyType compoundingFrequencyType = null;
        LoanRescheduleStrategyMethod rescheduleStrategyMethod = null;
        CalendarHistoryDataWrapper calendarHistoryDataWrapper = null;
        boolean allowCompoundingOnEod = false;
        boolean isSubsidyApplicable = false;
        if (this.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            restCalendarInstance = scheduleGeneratorDTO.getCalendarInstanceForInterestRecalculation();
            compoundingCalendarInstance = scheduleGeneratorDTO.getCompoundingCalendarInstance();
            recalculationFrequencyType = this.loanInterestRecalculationDetails.getRestFrequencyType();
            compoundingMethod = this.loanInterestRecalculationDetails.getInterestRecalculationCompoundingMethod();
            compoundingFrequencyType = this.loanInterestRecalculationDetails.getCompoundingFrequencyType();
            rescheduleStrategyMethod = this.loanInterestRecalculationDetails.getRescheduleStrategyMethod();
            allowCompoundingOnEod = this.loanInterestRecalculationDetails.allowCompoundingOnEod();
            calendarHistoryDataWrapper = scheduleGeneratorDTO.getCalendarHistoryDataWrapper();
            isSubsidyApplicable = this.loanInterestRecalculationDetails.isSubsidyApplicable();
        }
        calendar = scheduleGeneratorDTO.getCalendar();
        calendarHistoryDataWrapper = scheduleGeneratorDTO.getCalendarHistoryDataWrapper();

        BigDecimal annualNominalInterestRate = this.loanRepaymentScheduleDetail.getAnnualNominalInterestRate();
        FloatingRateDTO floatingRateDTO = scheduleGeneratorDTO.getFloatingRateDTO();
        List<LoanTermVariationsData> loanTermVariations = new ArrayList<>();
        annualNominalInterestRate = constructLoanTermVariations(floatingRateDTO, annualNominalInterestRate, loanTermVariations);
        LocalDate interestChargedFromDate = getInterestChargedFromDate();
        if (interestChargedFromDate == null && scheduleGeneratorDTO.isInterestChargedFromDateAsDisbursementDateEnabled()) {
            interestChargedFromDate = getDisbursementDate();
        }

        final LoanApplicationTerms loanApplicationTerms = LoanApplicationTerms.assembleFrom(scheduleGeneratorDTO.getApplicationCurrency(),
                loanTermFrequency, loanTermPeriodFrequencyType, nthDayType, dayOfWeekType, getDisbursementDate(),
                getExpectedFirstRepaymentOnDate(), scheduleGeneratorDTO.getCalculatedRepaymentsStartingFromDate(), getInArrearsTolerance(),
                this.loanRepaymentScheduleDetail, this.loanProduct.isMultiDisburseLoan(), this.fixedEmiAmount, disbursementData,
                this.maxOutstandingLoanBalance, interestChargedFromDate, this.loanProduct.getPrincipalThresholdForLastInstallment(),
                this.loanProduct.getInstallmentAmountInMultiplesOf(), recalculationFrequencyType, restCalendarInstance, compoundingMethod,
                compoundingCalendarInstance, compoundingFrequencyType, this.loanProduct.preCloseInterestCalculationStrategy(),
                rescheduleStrategyMethod, calendar, getApprovedPrincipal(), annualNominalInterestRate, loanTermVariations,
                calendarHistoryDataWrapper, scheduleGeneratorDTO.getNumberOfdays(),
                scheduleGeneratorDTO.isSkipRepaymentOnFirstDayofMonth(), holidayDetailDTO, allowCompoundingOnEod, isSubsidyApplicable,
                firstEmiAmount, this.loanProduct.getAdjustedInstallmentInMultiplesOf(), this.loanProduct.adjustFirstEMIAmount(),
                scheduleGeneratorDTO.isConsiderFutureDisbursmentsInSchedule(), scheduleGeneratorDTO.isConsiderAllDisbursmentsInSchedule(),
                this.loanProduct.isAdjustInterestForRounding(), this.loanProduct.allowNegativeLoanBalances(),
                this.amountForUpfrontCollection, this.discountOnDisbursalAmount, reduceDiscountFromPricipal);
        loanApplicationTerms.setCapitalizedCharges(LoanUtilService.getCapitalizedCharges(this.getLoanCharges()));
        updateInterestRate(loanApplicationTerms,scheduleGeneratorDTO);
        if (this.isSubmittedAndPendingApproval()
                || this.isApproved()
                || (this.isMultiDisburmentLoan() && getLastUserTransactionDate().isBefore(
                        loanApplicationTerms.getCalculatedRepaymentsStartingFromLocalDate()))) {
            final Money interestPosted = null;
            Money brokenPeriodInterest = LoanUtilService.calculateBrokenPeriodInterest(loanApplicationTerms, interestPosted);
            setBrokenPeriodInterest(brokenPeriodInterest.getAmount());
        } else {
            LoanUtilService.calculateBrokenPeriodInterest(loanApplicationTerms, getBrokenPeriodInterest());
        }
        
        return loanApplicationTerms;
    }
    
    private void updateInterestRate(final LoanApplicationTerms loanApplicationTerms, final ScheduleGeneratorDTO scheduleGeneratorDTO) {
        if (getFlatInterestRate() != null) {
            loanApplicationTerms.setFlatInterestRate(getFlatInterestRate());
            final RoundingMode roundingMode = MoneyHelper.getRoundingMode();
            final MathContext mc = new MathContext(8, roundingMode);
            Money emi = loanApplicationTerms.calculateEmiWithFlatInterestRate(mc);

            Money interest = null;
            if (this.isOpen()) {
                interest = Money.of(getCurrency(), this.summary.getTotalInterestCharged());
            } else if (loanApplicationTerms.isAdjustInterestForRounding()) {
                Money totalPayment = emi.multipliedBy(loanApplicationTerms.getNumberOfRepayments());
                totalPayment = totalPayment.plus(loanApplicationTerms.getAmountForUpfrontCollection());
                Money principal = loanApplicationTerms.getPrincipalToBeScheduled();
                interest = totalPayment.minus(principal);
            } else {
                interest = loanApplicationTerms.calculateInterestWithFlatInterestRate(mc);
            }
            loanApplicationTerms.setTotalInterestForSchedule(interest);
            loanApplicationTerms.setTotalPrincipalForSchedule(loanApplicationTerms.getPrincipalToBeScheduled());
            if (loanApplicationTerms.getFixedEmiAmount() == null) {
                BigDecimal emiAmount = emi.getAmount();
                if (this.calculatedInstallmentAmount != null && this.isOpen()) {
                    emiAmount = this.calculatedInstallmentAmount;
                }
                loanApplicationTerms.setFixedEmiAmount(emiAmount);
                scheduleGeneratorDTO.setCalculatedInstallmentAmount(emiAmount);
            }
            if (!this.isOpen()) {
                double annualIrr = IRRCalculator.calculateIrr(loanApplicationTerms);
                if(Double.isNaN(annualIrr)){
                    throw new IRRCalculationException();
                }
                loanApplicationTerms.updateAnnualNominalInterestRate(BigDecimal.valueOf(annualIrr));
                if (loanApplicationTerms.getInterestRatePeriodFrequencyType().isMonthly()) {
                    double monthlyRate = annualIrr / 12;
                    loanApplicationTerms.updateInterestRatePerPeriod(BigDecimal.valueOf(monthlyRate));
                } else {
                    loanApplicationTerms.updateInterestRatePerPeriod(BigDecimal.valueOf(annualIrr));
                }
                this.getLoanRepaymentScheduleDetail().updateInterestRate(loanApplicationTerms.getAnnualNominalInterestRate());
            }
        }
    }

    public BigDecimal constructLoanTermVariations(FloatingRateDTO floatingRateDTO, BigDecimal annualNominalInterestRate,
            List<LoanTermVariationsData> loanTermVariations) {
        for (LoanTermVariations variationTerms : this.loanTermVariations) {
            if(variationTerms.isActive()) {
                loanTermVariations.add(variationTerms.toData());
            }
        }
        annualNominalInterestRate = constructFloatingInterestRates(annualNominalInterestRate, floatingRateDTO, loanTermVariations);
        return annualNominalInterestRate;
    }

    private LoanRepaymentScheduleInstallment getTotalOutstandingOnLoan() {
        Money feeCharges = Money.zero(loanCurrency());
        Money penaltyCharges = Money.zero(loanCurrency());
        Money totalPrincipal = Money.zero(loanCurrency());
        Money totalInterest = Money.zero(loanCurrency());
        final List<LoanInterestRecalcualtionAdditionalDetails> compoundingDetails = null;
        for (final LoanRepaymentScheduleInstallment scheduledRepayment : this.repaymentScheduleInstallments) {
            totalPrincipal = totalPrincipal.plus(scheduledRepayment.getPrincipalOutstanding(loanCurrency()));
            totalInterest = totalInterest.plus(scheduledRepayment.getInterestOutstanding(loanCurrency()));
            feeCharges = feeCharges.plus(scheduledRepayment.getFeeChargesOutstanding(loanCurrency()));
            penaltyCharges = penaltyCharges.plus(scheduledRepayment.getPenaltyChargesOutstanding(loanCurrency()));
        }
        return new LoanRepaymentScheduleInstallment(null, 0, DateUtils.getLocalDateOfTenant(), DateUtils.getLocalDateOfTenant(),
                totalPrincipal.getAmount(), totalInterest.getAmount(), feeCharges.getAmount(), penaltyCharges.getAmount(), false,
                compoundingDetails);
    }

    public List<LoanRepaymentScheduleInstallment> fetchRepaymentScheduleInstallments() {
        return this.repaymentScheduleInstallments;
    }

    public LocalDate getAccruedTill() {
        LocalDate accruedTill = null;
        if (this.accruedTill != null) {
            accruedTill = new LocalDate(this.accruedTill);
        }
        return accruedTill;
    }

    public LocalDate fetchInterestRecalculateFromDate() {
        LocalDate interestRecalculatedOn = null;
        if (this.interestRecalculatedOn == null) {
            interestRecalculatedOn = getDisbursementDate();
        } else {
            interestRecalculatedOn = new LocalDate(this.interestRecalculatedOn);
        }
        
        // ignoring overdue check for interest recalculation job when picking up loans from delete me table
        // for chaitanya
        final Boolean ignoreOverdue = ThreadLocalContextUtil.getIgnoreOverdue();
        if (ignoreOverdue == null || !ignoreOverdue) {
            //Fix to check in case the Overdue date is after the interest recalculatedOn Date then that needs to be taken
            //as the interest recalculated Date 
            LocalDate overdueSince = this.loanSummaryWrapper.determineOverdueSince(getRepaymentScheduleInstallments());
            if(overdueSince != null && overdueSince.isAfter(interestRecalculatedOn)){
                interestRecalculatedOn = overdueSince;
            }
        }
        

        return interestRecalculatedOn;
    }

    private void updateLoanOutstandingBalaces() {
        Money outstanding = Money.zero(getCurrency());
        List<LoanTransaction> loanTransactions = retreiveListOfTransactionsExcludeAccruals();
        for (LoanTransaction loanTransaction : loanTransactions) {
            if (loanTransaction.isDisbursement() || loanTransaction.isIncomePosting() || loanTransaction.isBrokenPeriodInterestPosting()) {
                outstanding = outstanding.plus(loanTransaction.getAmount(getCurrency()));
                loanTransaction.updateOutstandingLoanBalance(outstanding.getAmount());
            } else if(!loanTransaction.isRecoveryRepayment()) {
                if (this.loanInterestRecalculationDetails != null
                        && this.loanInterestRecalculationDetails.isCompoundingToBePostedAsTransaction()
                        && !loanTransaction.isRepaymentAtDisbursement()) {
                    if (this.loanInterestRecalculationDetails.getInterestRecalculationCompoundingMethod().isInterestCompoundingEnabled()) {
                        outstanding = outstanding.minus(loanTransaction.getInterestPortion());
                    }
                    if (this.loanInterestRecalculationDetails.getInterestRecalculationCompoundingMethod().isFeeCompoundingEnabled()) {
                        outstanding = outstanding.minus(loanTransaction.getFeeChargesPortion())
                                .minus(loanTransaction.getPenaltyChargesPortion(getCurrency()));
                    }
                    outstanding = outstanding.minus(loanTransaction.getAmount(getCurrency()));
                }
                outstanding = outstanding.minus(loanTransaction.getPrincipalPortion(getCurrency()));

                loanTransaction.updateOutstandingLoanBalance(outstanding.getAmount());
            }
        }
    }

    public LoanTransactionProcessingStrategy transactionProcessingStrategy() {
        return this.transactionProcessingStrategy;
    }

    public boolean isNpa() {
        return this.isNpa;
    }

    /**
     * @return List of loan repayments schedule objects
     **/
    public List<LoanRepaymentScheduleInstallment> getRepaymentScheduleInstallments() {
        return this.repaymentScheduleInstallments;
    }

    /**
     * @return Loan product minimum repayments schedule related detail
     **/
    public LoanProductRelatedDetail getLoanRepaymentScheduleDetail() {
        return this.loanRepaymentScheduleDetail;
    }

    /**
     * @return Loan Fixed Emi amount
     **/
    public BigDecimal getFixedEmiAmount() {
        return this.fixedEmiAmount;
    }

    /**
     * @return maximum outstanding loan balance
     **/
    public BigDecimal getMaxOutstandingLoanBalance() {
        return this.maxOutstandingLoanBalance;
    }

    /**
     * @param dueDate
     *            the due date of the installment
     * @return a schedule installment with similar due date to the one provided
     **/
    public LoanRepaymentScheduleInstallment getRepaymentScheduleInstallment(LocalDate dueDate) {
        LoanRepaymentScheduleInstallment installment = null;

        if (dueDate != null) {
            for (LoanRepaymentScheduleInstallment repaymentScheduleInstallment : this.repaymentScheduleInstallments) {

                if (repaymentScheduleInstallment.getDueDate().isEqual(dueDate)) {
                    installment = repaymentScheduleInstallment;

                    break;
                }
            }
        }

        return installment;
    }

    /**
     * @return loan disbursement data
     **/
    public List<DisbursementData> getDisbursmentData() {
        Iterator<LoanDisbursementDetails> iterator = this.getDisbursementDetails().iterator();
        List<DisbursementData> disbursementData = new ArrayList<>();

        while (iterator.hasNext()) {
            LoanDisbursementDetails loanDisbursementDetails = iterator.next();

            LocalDate expectedDisbursementDate = null;
            LocalDate actualDisbursementDate = null;

            if (loanDisbursementDetails.expectedDisbursementDate() != null) {
                expectedDisbursementDate = new LocalDate(loanDisbursementDetails.expectedDisbursementDate());
            }

            if (loanDisbursementDetails.actualDisbursementDate() != null) {
                actualDisbursementDate = new LocalDate(loanDisbursementDetails.actualDisbursementDate());
            }

            disbursementData.add(new DisbursementData(loanDisbursementDetails.getId(), expectedDisbursementDate, actualDisbursementDate,
                    loanDisbursementDetails.principal(), null, null));
        }

        return disbursementData;
    }

    /**
     * @return Loan summary embedded object
     **/
    public LoanSummary getLoanSummary() {
        return this.summary;
    }

    public void updateRescheduledByUser(AppUser rescheduledByUser) {
        this.rescheduledByUser = rescheduledByUser;
    }

    public LoanProductRelatedDetail getLoanProductRelatedDetail() {
        return this.loanRepaymentScheduleDetail;
    }

    public void updateNumberOfRepayments(Integer numberOfRepayments) {
        this.loanRepaymentScheduleDetail.updateNumberOfRepayments(numberOfRepayments);
    }

    public void updateRescheduledOnDate(LocalDate rescheduledOnDate) {

        if (rescheduledOnDate != null) {
            this.rescheduledOnDate = rescheduledOnDate.toDate();
        }
    }

    public void updateTermFrequency(Integer termFrequency) {

        if (termFrequency != null) {
            this.termFrequency = termFrequency;
        }
    }

    public boolean isFeeCompoundingEnabledForInterestRecalculation() {
        boolean isEnabled = false;
        if (this.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            isEnabled = this.loanInterestRecalculationDetails.getInterestRecalculationCompoundingMethod().isFeeCompoundingEnabled();
        }
        return isEnabled;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public Client getClient() {
        return this.client;
    }

    public Boolean shouldCreateStandingInstructionAtDisbursement() {
        return (this.createStandingInstructionAtDisbursement != null) && this.createStandingInstructionAtDisbursement;
    }

    public Collection<LoanCharge> getLoanCharges(LocalDate dueDate) {
        Collection<LoanCharge> loanCharges = new ArrayList<>();

        for (LoanCharge loanCharge : charges) {

            if ((loanCharge.getDueLocalDate() != null) && loanCharge.getDueLocalDate().equals(dueDate)) {
                loanCharges.add(loanCharge);
            }
        }

        return loanCharges;
    }

    public void setGuaranteeAmount(BigDecimal guaranteeAmountDerived) {
        this.guaranteeAmountDerived = guaranteeAmountDerived;
    }

    public void updateGuaranteeAmount(BigDecimal guaranteeAmount) {
        this.guaranteeAmountDerived = getGuaranteeAmount().add(guaranteeAmount);
    }

    public BigDecimal getGuaranteeAmount() {
        return this.guaranteeAmountDerived == null ? BigDecimal.ZERO : this.guaranteeAmountDerived;
    }

    public ChangedTransactionDetail makeRefundForActiveLoan(final LoanTransaction loanTransaction,
            final LoanLifecycleStateMachine loanLifecycleStateMachine, final List<Long> existingTransactionIds,
            final List<Long> existingReversedTransactionIds, final boolean allowTransactionsOnHoliday, final List<Holiday> holidays,
            final WorkingDays workingDays, final boolean allowTransactionsOnNonWorkingDay) {

        validateAccountStatus(LoanEvent.LOAN_REFUND);
        validateActivityNotBeforeClientOrGroupTransferDate(LoanEvent.LOAN_REFUND, loanTransaction.getTransactionDate());

        validateRefundDateIsAfterLastRepayment(loanTransaction.getTransactionDate());

        validateRepaymentDateIsOnHoliday(loanTransaction.getTransactionDate(), allowTransactionsOnHoliday, holidays);
        validateRepaymentDateIsOnNonWorkingDay(loanTransaction.getTransactionDate(), workingDays, allowTransactionsOnNonWorkingDay);

        existingTransactionIds.addAll(findExistingTransactionIds());
        existingReversedTransactionIds.addAll(findExistingReversedTransactionIds());

        final ChangedTransactionDetail changedTransactionDetail = handleRefundTransaction(loanTransaction, loanLifecycleStateMachine, null);

        return changedTransactionDetail;

    }

    private void validateRefundDateIsAfterLastRepayment(final LocalDate refundTransactionDate) {
        final LocalDate possibleNextRefundDate = possibleNextRefundDate();

		if (possibleNextRefundDate == null || refundTransactionDate.isBefore(possibleNextRefundDate)) {
			final String defaultUserMessage = "The refund date`" + refundTransactionDate + "`"
					+ "` cannot be before the smallest repayment transaction date";
			final String errorMsg = "error.msg.loan.refund.failed";
			throw new InvalidRefundDateException(errorMsg, refundTransactionDate.toString(), defaultUserMessage);
		}

    }

    private ChangedTransactionDetail handleRefundTransaction(final LoanTransaction loanTransaction,
            final LoanLifecycleStateMachine loanLifecycleStateMachine, final LoanTransaction adjustedTransaction) {

        ChangedTransactionDetail changedTransactionDetail = null;

        final LoanStatus statusEnum = loanLifecycleStateMachine.transition(LoanEvent.LOAN_REFUND, LoanStatus.fromInt(this.loanStatus));
        this.loanStatus = statusEnum.getValue();

        loanTransaction.updateLoan(this);

        // final boolean isTransactionChronologicallyLatest =
        // isChronologicallyLatestRefund(loanTransaction,
        // this.loanTransactions);

        if (status().isOverpaid() || status().isClosed()) {

            final String errorMessage = "This refund option is only for active loans ";
            throw new InvalidLoanStateTransitionException("transaction", "is.exceeding.overpaid.amount", errorMessage, this.totalOverpaid,
                    loanTransaction.getAmount(getCurrency()).getAmount());

        } else if (this.getTotalPaidInRepayments().isZero()) {
            final String errorMessage = "Cannot refund when no payment has been made";
            throw new InvalidLoanStateTransitionException("transaction", "no.payment.yet.made.for.loan", errorMessage);
        }

        if (loanTransaction.isNotZero(loanCurrency())) {
            this.loanTransactions.add(loanTransaction);
        }

        if (loanTransaction.isNotRefundForActiveLoan()) {
            final String errorMessage = "A transaction of type refund was expected but not received.";
            throw new InvalidLoanTransactionTypeException("transaction", "is.not.a.refund.transaction", errorMessage);
        }

        final LocalDate loanTransactionDate = loanTransaction.getTransactionDate();
        if (loanTransactionDate.isBefore(getDisbursementDate())) {
            final String errorMessage = "The transaction date cannot be before the loan disbursement date: "
                    + getApprovedOnDate().toString();
            throw new InvalidLoanStateTransitionException("transaction", "cannot.be.before.disbursement.date", errorMessage,
                    loanTransactionDate, getDisbursementDate());
        }

        if (loanTransactionDate.isAfter(DateUtils.getLocalDateOfTenant())) {
            final String errorMessage = "The transaction date cannot be in the future.";
            throw new InvalidLoanStateTransitionException("transaction", "cannot.be.a.future.date", errorMessage, loanTransactionDate);
        }

        if (this.loanProduct.isMultiDisburseLoan() && adjustedTransaction == null) {
            BigDecimal totalDisbursed = getDisbursedAmount();
            if (totalDisbursed.compareTo(this.summary.getTotalPrincipalRepaid()) < 0) {
                final String errorMessage = "The transaction cannot be done before the loan disbursement: "
                        + getApprovedOnDate().toString();
                throw new InvalidLoanStateTransitionException("transaction", "cannot.be.done.before.disbursement", errorMessage);
            }
        }

        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessorFactory
                .determineProcessor(this.transactionProcessingStrategy);

        // If is a refund
        if (adjustedTransaction == null) {
            loanRepaymentScheduleTransactionProcessor.handleRefund(loanTransaction, getCurrency(), this.repaymentScheduleInstallments,
                    charges());
        } else {
            final List<LoanTransaction> allNonContraTransactionsPostDisbursement = retreiveListOfTransactionsPostDisbursement();
            changedTransactionDetail = loanRepaymentScheduleTransactionProcessor.handleTransaction(getDisbursementDate(),
                    allNonContraTransactionsPostDisbursement, getCurrency(), this.repaymentScheduleInstallments, charges(), disbursementDetails);
            for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                mapEntry.getValue().updateLoan(this);
            }

        }

        updateLoanSummaryDerivedFields();

        doPostLoanTransactionChecks(loanTransaction.getTransactionDate(), loanLifecycleStateMachine);

        return changedTransactionDetail;
    }

    public LocalDate possibleNextRefundDate() {

        final LocalDate now = DateUtils.getLocalDateOfTenant();

        LocalDate lastTransactionDate = null;
        for (final LoanTransaction transaction : this.loanTransactions) {
            if ((transaction.isRepayment() || transaction.isRefundForActiveLoan()) && transaction.isNonZero()) {
                lastTransactionDate = transaction.getTransactionDate();
            }
        }

        return lastTransactionDate == null ? now : lastTransactionDate;
    }

    private Date getActualDisbursementDate(final LoanCharge loanCharge) {
        Date actualDisbursementDate = this.actualDisbursementDate;
        if (loanCharge.isDueAtDisbursement() && loanCharge.isActive()) {
            LoanTrancheDisbursementCharge trancheDisbursementCharge = loanCharge.getTrancheDisbursementCharge();
            if (trancheDisbursementCharge != null) {
                LoanDisbursementDetails details = trancheDisbursementCharge.getloanDisbursementDetails();
                actualDisbursementDate = details.actualDisbursementDate();
            }
        }
        return actualDisbursementDate;
    }

    public void addTrancheLoanCharge(Charge charge, BigDecimal amount) {
        List<Charge> appliedCharges = new ArrayList<>(); 
        for(LoanTrancheCharge loanTrancheCharge: trancheCharges){
            appliedCharges.add(loanTrancheCharge.getCharge());
        }
        if (!appliedCharges.contains(charge)) {
            trancheCharges.add(new LoanTrancheCharge(charge, this, amount));
        }
    }

    public ChangedTransactionDetail undoLastDisbursal(ScheduleGeneratorDTO scheduleGeneratorDTO, AppUser currentUser, Map<String, Object> actualChanges, List<Long> rescheduleVariations) {

        validateAccountStatus(LoanEvent.LOAN_DISBURSAL_UNDO_LAST);
        validateActivityNotBeforeClientOrGroupTransferDate(LoanEvent.LOAN_DISBURSAL_UNDO_LAST, getDisbursementDate());
        LocalDate actualDisbursementDate = null;
        LocalDate lastTransactionDate = getDisbursementDate();
        List<LoanTransaction> loanTransactions = retreiveListOfTransactionsExcludeAccruals();
        boolean isLastTranscheDisbursementFound = false; 
        Collections.reverse(loanTransactions);
        final Map<LoanCharge,LoanTransaction> waivedDisburseCharges = new HashMap<>();
        for (final LoanTransaction previousTransaction : loanTransactions) {
            if (lastTransactionDate.isBefore(previousTransaction.getTransactionDate())) {
                if (previousTransaction.isRepayment() || previousTransaction.isWaiver() || previousTransaction.isChargePayment()) { throw new UndoLastTrancheDisbursementException(
                        previousTransaction.getId()); }
            }
            if (previousTransaction.isChargesWaiver()) {
                Set<LoanChargePaidBy> paidBy = previousTransaction.getLoanChargesPaid();
                for (LoanChargePaidBy chargePaidBy : paidBy) {
                    if (chargePaidBy.getLoanCharge().isDueAtDisbursement()) {
                        waivedDisburseCharges.put(chargePaidBy.getLoanCharge(), previousTransaction);
                    }
                }
            }
            if (!isLastTranscheDisbursementFound && previousTransaction.isDisbursement()) {
                lastTransactionDate = previousTransaction.getTransactionDate();
                isLastTranscheDisbursementFound = true;
            }
            
        }
        actualDisbursementDate = lastTransactionDate;
        updateLoanToLastDisbursalState(actualDisbursementDate, waivedDisburseCharges);
        for (Iterator<LoanTermVariations> iterator = this.loanTermVariations.iterator(); iterator.hasNext();) {
            LoanTermVariations loanTermVariations = iterator.next();
            if (!rescheduleVariations.contains(loanTermVariations.getId())
                    && loanTermVariations.getOnLoanStatus().equals(LoanStatus.ACTIVE.getValue())
                    && ((loanTermVariations.getTermType().isDueDateVariation() && loanTermVariations.fetchDateValue().isAfter(
                            actualDisbursementDate))
                            || (loanTermVariations.getTermType().isEMIAmountVariation() && loanTermVariations.getTermApplicableFrom()
                                    .equals(actualDisbursementDate.toDate())) || loanTermVariations.getTermApplicableFrom().after(
                            actualDisbursementDate.toDate()))) {
                loanTermVariations.markAsInactive();
            }
        }
        reverseExistingTransactionsTillLastDisbursal(actualDisbursementDate);
        ChangedTransactionDetail changedTransactionDetail = recalculateScheduleFromLastTransaction(scheduleGeneratorDTO, currentUser);
        actualChanges.put("undolastdisbursal", "true");
        actualChanges.put("disbursedAmount", this.getDisbursedAmount());
        updateLoanSummaryDerivedFields();

        return changedTransactionDetail;
    }

    /**
     * Reverse only disbursement, accruals, and repayments at disbursal
     * transactions
     * 
     * @param actualDisbursementDate
     * @return
     */
    public List<LoanTransaction> reverseExistingTransactionsTillLastDisbursal(LocalDate actualDisbursementDate) {
        final List<LoanTransaction> reversedTransactions = new ArrayList<>();
        for (final LoanTransaction transaction : this.loanTransactions) {
            if ((actualDisbursementDate.equals(transaction.getTransactionDate()) || actualDisbursementDate.isBefore(transaction
                    .getTransactionDate())) && transaction.isAllowTypeTransactionAtTheTimeOfLastUndo()) {
                reversedTransactions.add(transaction);
                transaction.reverse();
            }
        }
        return reversedTransactions;
    }

    private void updateLoanToLastDisbursalState(LocalDate actualDisbursementDate, final Map<LoanCharge,LoanTransaction> waivedDisburseCharges) {

        boolean isDisbursedAmountChanged = false;
        Collection<LoanCharge> trancheCharges = new ArrayList<>(); 
        for (final LoanCharge charge : charges()) {
            if (charge.isOverdueInstallmentCharge()) {
                charge.setActive(false);
            } else if (charge.isTrancheDisbursementCharge()
                    && actualDisbursementDate.equals(new LocalDate(charge.getTrancheDisbursementCharge().getloanDisbursementDetails()
                            .actualDisbursementDate()))) {
                charge.resetToOriginal(loanCurrency());
                if(waivedDisburseCharges.containsKey(charge)){
                    waivedDisburseCharges.get(charge).reverse();
                }
                isDisbursedAmountChanged = true;
                trancheCharges.add(charge);
            }
        }
        for (final LoanDisbursementDetails details : this.disbursementDetails) {
            if (details.isActive() && actualDisbursementDate.equals(new LocalDate(details.actualDisbursementDate()))) {
                this.loanRepaymentScheduleDetail.setPrincipal(getDisbursedAmount().subtract(details.principal()));
                details.updateActualDisbursementDate(null);
                recalculateDisbursementCharge(details, trancheCharges);
            }
        }
        if (isDisbursedAmountChanged) {
            updateSummaryWithTotalFeeChargesDueAtDisbursement(deriveSumTotalOfChargesDueAtDisbursement());
        }
        updateLoanSummaryDerivedFields();
    }

    public Boolean getIsFloatingInterestRate() {
        return this.isFloatingInterestRate;
    }

    public BigDecimal getInterestRateDifferential() {
        return this.interestRateDifferential;
    }

    public void setIsFloatingInterestRate(Boolean isFloatingInterestRate) {
        this.isFloatingInterestRate = isFloatingInterestRate;
    }

    public void setInterestRateDifferential(BigDecimal interestRateDifferential) {
        this.interestRateDifferential = interestRateDifferential;
    }

    public List<LoanTermVariations> getLoanTermVariations() {
        return this.loanTermVariations;
    }
    
	public boolean isAnyActiveLoanVariation() {
		if (!this.loanTermVariations.isEmpty() && this.loanTermVariations != null) {
			for (LoanTermVariations loanTermVariation : this.loanTermVariations) {
				if (loanTermVariation.isActive()) {
					return true;
				}
			}
		}
		return false;
	}

    private int adjustNumberOfRepayments() {
        int repaymetsForAdjust = 0;
        for (LoanTermVariations loanTermVariations : this.loanTermVariations) {
            if (loanTermVariations.isActive()) {
                if (loanTermVariations.getTermType().isInsertInstallment()) {
                    repaymetsForAdjust++;
                } else if (loanTermVariations.getTermType().isDeleteInstallment()) {
                    repaymetsForAdjust--;
                }
            }
        }
        return repaymetsForAdjust;
    }

    public int fetchNumberOfInstallmensAfterExceptions() {
        if (this.repaymentScheduleInstallments.size() > 0) {
            int numberOfInstallments = 0;
            for (final LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
                if (!installment.isRecalculatedInterestComponent()) {
                    numberOfInstallments++;
                }
            }
            return numberOfInstallments;
        }
        return this.repaymentScheduleDetail().getNumberOfRepayments() + adjustNumberOfRepayments();
    }

    public void setExpectedFirstRepaymentOnDate(Date expectedFirstRepaymentOnDate) {
        this.expectedFirstRepaymentOnDate = expectedFirstRepaymentOnDate;
    }

    /*
     * get the next repayment date for rescheduling at the time of disbursement
     */
    public LocalDate getNextPossibleRepaymentDateForRescheduling(Boolean isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled, final LocalDate actualDisbursementDate) {
        List<LoanDisbursementDetails> loanDisbursementDetails = this.disbursementDetails;
        LocalDate nextRepaymentDate = DateUtils.getLocalDateOfTenant();
        if(this.isMultiDisburmentLoan()){
            for (LoanDisbursementDetails loanDisbursementDetail : loanDisbursementDetails) {
                if (loanDisbursementDetail.isActive() && loanDisbursementDetail.actualDisbursementDate() == null
                        || loanDisbursementDetail.isActive() && (actualDisbursementDate != null && loanDisbursementDetail.actualDisbursementDateAsLocalDate().isEqual(actualDisbursementDate))) {
                    for (final LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
                        if (installment.getDueDate().isAfter(loanDisbursementDetail.expectedDisbursementDateAsLocalDate())) {
                            nextRepaymentDate = installment.getDueDate();
                            break;
                        }
                    }
                    break;
                }
            }
        }else {
                for (final LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
                    if ((isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled && installment.getDueDate().isEqual(this.getExpectedDisbursedOnLocalDate()))
                            || installment.getDueDate().isAfter(this.getExpectedDisbursedOnLocalDate())
                            && installment.isNotFullyPaidOff()) {
                        nextRepaymentDate = installment.getDueDate();
                        break;
                    }
                }
        }
           
        return nextRepaymentDate;
    }
    
    public BigDecimal getDerivedAmountForCharge(LoanCharge loanCharge) {
        BigDecimal amount = BigDecimal.ZERO;
        if (isMultiDisburmentLoan() && (loanCharge.getCharge().getChargeTimeType() == ChargeTimeType.DISBURSEMENT.getValue())) {
            amount = getApprovedPrincipal();
        } else {
            amount = getPrincpal().getAmount();
        }
        return amount;
    }

    public void updateWriteOffReason(CodeValue writeOffReason) {
        this.writeOffReason = writeOffReason;
    }

    public void updateGlim(final List<GroupLoanIndividualMonitoring> glimList) {
        this.glimList.addAll(glimList);
    }
    
    public List<GroupLoanIndividualMonitoring> getGroupLoanIndividualMonitoringList() {
        return this.glimList;
    }
    
    public void updateInstallmentAmountForGlim(LoanApplicationTerms loanApplicationTerms) {
        BigDecimal totalInstallmentAmount = BigDecimal.ZERO;
        BigDecimal installmentAmountWithoutFee = BigDecimal.ZERO;
        Integer numberOfInstallments = loanApplicationTerms.getNumberOfRepayments();
        MonetaryCurrency currency = loanApplicationTerms.getCurrency();
        BigDecimal feeAmount = BigDecimal.ZERO;
        if (this.glimList != null && !this.glimList.isEmpty()) {
            for (GroupLoanIndividualMonitoring glim : this.glimList) {
                if (glim.isClientSelected()) {
                    totalInstallmentAmount = totalInstallmentAmount.add(glim.getInstallmentAmount());
                    Set<GroupLoanIndividualMonitoringCharge> glimmCharges = glim.getGroupLoanIndividualMonitoringCharges();
                    for (GroupLoanIndividualMonitoringCharge glimCharge : glimmCharges) {
                        if(!ChargeTimeType.fromInt(glimCharge.getCharge().getChargeTimeType().intValue()).isUpfrontFee()){
                            BigDecimal chargeAmount = MathUtility.isNull(glimCharge.getRevisedFeeAmount()) ? glimCharge.getFeeAmount()
                                    : glimCharge.getRevisedFeeAmount();
                            feeAmount = MathUtility.add(feeAmount, MathUtility.divide(chargeAmount, numberOfInstallments, currency));
                        }
                    }
                }
            }
            installmentAmountWithoutFee = totalInstallmentAmount.subtract(feeAmount);
            if (installmentAmountWithoutFee.compareTo(BigDecimal.ZERO) == 1) {
                loanApplicationTerms.setFixedEmiAmount(installmentAmountWithoutFee);
            }
        }
    }   
    
    public BigDecimal calculateInstallmentAmount(BigDecimal installmentAmount, final BigDecimal totalFeeCharges, final Integer numberOfRepayments) {
        BigDecimal feePerInstallment = BigDecimal.ZERO;
        if(installmentAmount != null){
            feePerInstallment = BigDecimal.valueOf(totalFeeCharges.doubleValue() / numberOfRepayments.doubleValue());
            installmentAmount = installmentAmount.subtract(feePerInstallment);
        }
        return installmentAmount;
    }

    public Boolean isSubsidyApplicable() {
        boolean isSubsidyApplicable = false;
        if (this.isInterestRecalculationEnabledForProduct()) {
            isSubsidyApplicable = this.loanInterestRecalculationDetails.isSubsidyApplicable();
        }
        return isSubsidyApplicable;
    }

    private void updateLoanStatusBasedOnProductConfig(final LocalDate loanTransactionDate) {
        if (loanProduct.isCloseLoanOnOverpayment() && isOverPaid()) {
            final Integer transactionSubType = LoanTransactionSubType.UNACCOUTABLE.getValue();
            PaymentDetail paymentDetails = null;
            String externalId = null;
            LoanTransaction refundTransaction = LoanTransaction.refundWithLoanTransactionSubType(getOffice(),
                    Money.of(getCurrency(), this.getTotalOverpaid()), paymentDetails, loanTransactionDate, externalId, transactionSubType);
            refundTransaction.updateLoan(this);
            this.loanTransactions.add(refundTransaction);
            updateLoanSummaryDerivedFields();
        }
    }

    private void reverseExistingUnaccountableRefundTransactions() {
        for (LoanTransaction existingTransation : this.loanTransactions) {
            if (existingTransation.isRefund() && existingTransation.getSubTypeOf() != null
                    && existingTransation.getSubTypeOf().equals(LoanTransactionSubType.UNACCOUTABLE.getValue())) {
                existingTransation.reverse();
                break;
            }
        }
    }

    public Group getGroup() {
        return group;
    }

    public LoanProduct getLoanProduct() {
        return loanProduct;
    }
	
    public LoanRepaymentScheduleInstallment fetchLoanForeclosureDetail(final LocalDate closureDate) {        
        Money[] receivables = retriveIncomeOutstandingTillDate(closureDate);
        Money totalPrincipal = (Money.of(getCurrency(), this.getSummary().getTotalPrincipalOutstanding()));
        totalPrincipal = totalPrincipal.minus(receivables[3]);
        final List<LoanInterestRecalcualtionAdditionalDetails> compoundingDetails = null;
        final LocalDate currentDate = DateUtils.getLocalDateOfTenant();
        return new LoanRepaymentScheduleInstallment(null, 0, currentDate, currentDate, totalPrincipal.getAmount(),
                receivables[0].getAmount(), receivables[1].getAmount(), receivables[2].getAmount(), false, compoundingDetails);
    }

    public Money[] retriveIncomeOutstandingTillDate(final LocalDate paymentDate) {
        Money[] balances = new Money[4];         
        final MonetaryCurrency currency = getCurrency();      
        Money interest = Money.zero(currency);
        Money paidFromFutureInstallments = Money.zero(currency); 
        Money fee = Money.zero(currency);
        Money penalty = Money.zero(currency);         
        for (final LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
            if (!installment.getDueDate().isAfter(paymentDate)) {
                interest = interest.plus(installment.getInterestOutstanding(currency));
                penalty = penalty.plus(installment.getPenaltyChargesOutstanding(currency));
                fee = fee.plus(installment.getFeeChargesOutstanding(currency));
                if(installment.getDueDate().isEqual(paymentDate)){
                    for (LoanCharge loanCharge : this.charges) {
                        if (loanCharge.isActive()
                                && loanCharge.isOverdueInstallmentCharge()
                                && loanCharge.getOverdueInstallmentCharge().getInstallment().getInstallmentNumber()
                                        .equals(installment.getInstallmentNumber())) {
                            penalty = penalty.minus(loanCharge.getAmount(currency));
                        }

                    }
                }
            } else if (installment.getFromDate().isBefore(paymentDate)) {
                Money[] balancesForCurrentPeroid = fetchInterestFeeAndPenaltyTillDate(paymentDate, currency, installment);                        
                if (balancesForCurrentPeroid[0].isGreaterThan(balancesForCurrentPeroid[5])) {
                    interest = interest.plus(balancesForCurrentPeroid[0])
                            .minus(balancesForCurrentPeroid[5]);
                } else {
                    Money interestPayable = balancesForCurrentPeroid[0].minus(installment.getInterestWaived(currency));
                    if (interestPayable.isGreaterThanZero()) {
                        paidFromFutureInstallments = paidFromFutureInstallments.plus(installment.getInterestPaid(currency))
                                .minus(interestPayable);
                    } else {
                        paidFromFutureInstallments = paidFromFutureInstallments.plus(installment.getInterestPaid(currency));
                    }
                }
                if (balancesForCurrentPeroid[1].isGreaterThan(balancesForCurrentPeroid[3])) {
                    fee = fee.plus(balancesForCurrentPeroid[1].minus(balancesForCurrentPeroid[3]));
                } else {
                    Money feePayable = balancesForCurrentPeroid[1].minus(installment.getFeeChargesWaived(currency));
                    if (feePayable.isGreaterThanZero()) {
                        paidFromFutureInstallments = paidFromFutureInstallments.plus(installment.getFeeChargesPaid(currency))
                                .minus(feePayable);
                    } else {
                        paidFromFutureInstallments = paidFromFutureInstallments.plus(installment.getFeeChargesPaid(currency));
                    }
                }
                if (balancesForCurrentPeroid[2].isGreaterThan(balancesForCurrentPeroid[4])) {
                    penalty = penalty.plus(balancesForCurrentPeroid[2].minus(balancesForCurrentPeroid[4]));
                } else {
                    Money penaltyPayable = balancesForCurrentPeroid[2].minus(installment.getPenaltyChargesWaived(currency));
                    if (penaltyPayable.isGreaterThanZero()) {
                        paidFromFutureInstallments = paidFromFutureInstallments.plus(installment.getPenaltyChargesPaid(currency))
                                .minus(penaltyPayable);
                    } else {
                        paidFromFutureInstallments = paidFromFutureInstallments.plus(installment.getPenaltyChargesPaid(currency));
                    }
                }
            } else if (installment.getDueDate().isAfter(paymentDate)) {
                paidFromFutureInstallments = paidFromFutureInstallments.plus(installment.getInterestPaid(currency))
                        .plus(installment.getPenaltyChargesPaid(currency)).plus(installment.getFeeChargesPaid(currency));
            }

        }
        balances[0] = interest;
        balances[1] = fee;
        balances[2] = penalty;
        balances[3] = paidFromFutureInstallments;
        return balances;
    }

    private Money[] fetchInterestFeeAndPenaltyTillDate(final LocalDate paymentDate, final MonetaryCurrency currency, final LoanRepaymentScheduleInstallment installment) {
        Money penaltyForCurrentPeriod = Money.zero(getCurrency());
        Money penaltyAccoutedForCurrentPeriod = Money.zero(getCurrency());
        Money feeForCurrentPeriod = Money.zero(getCurrency());
        Money feeAccountedForCurrentPeriod = Money.zero(getCurrency());
        Money interestForCurrentPeriod=Money.zero(getCurrency());
        Money interestAccountedForCurrentPeriod=Money.zero(getCurrency());
        int totalPeriodDays = Days.daysBetween(installment.getFromDate(), installment.getDueDate()).getDays();
        int tillDays = Days.daysBetween(installment.getFromDate(), paymentDate).getDays(); 
        interestForCurrentPeriod = Money.of(getCurrency(),BigDecimal.valueOf(calculateInterestForDays(totalPeriodDays, installment.getInterestCharged(getCurrency())
                .getAmount(), tillDays)));
        interestAccountedForCurrentPeriod = installment.getInterestWaived(getCurrency()).plus(installment.getInterestPaid(getCurrency()));
		for (LoanCharge loanCharge : this.charges) {
			if (loanCharge.isActive() && !loanCharge.isDueAtDisbursement()) {
				if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(installment.getFromDate(), paymentDate)) {
					if (loanCharge.isPenaltyCharge()) {
						penaltyForCurrentPeriod = penaltyForCurrentPeriod.plus(loanCharge.getAmount(getCurrency()));
						penaltyAccoutedForCurrentPeriod = penaltyAccoutedForCurrentPeriod.plus(loanCharge
								.getAmountWaived(getCurrency()).plus(loanCharge.getAmountPaid(getCurrency())));
					} else {
						feeForCurrentPeriod = feeForCurrentPeriod.plus(loanCharge.getAmount(currency));
						feeAccountedForCurrentPeriod = feeAccountedForCurrentPeriod
								.plus(loanCharge.getAmountWaived(getCurrency()).plus(

										loanCharge.getAmountPaid(getCurrency())));
					}
				} else if (loanCharge.isInstalmentFee()) {
					LoanInstallmentCharge loanInstallmentCharge = loanCharge
							.getInstallmentLoanCharge(installment.getInstallmentNumber());
					if (loanCharge.isPenaltyCharge()) {
						penaltyAccoutedForCurrentPeriod = penaltyAccoutedForCurrentPeriod
								.plus(loanInstallmentCharge.getAmountPaid(currency));
					} else {
						feeAccountedForCurrentPeriod = feeAccountedForCurrentPeriod
								.plus(loanInstallmentCharge.getAmountPaid(currency));
					}
				}
			}
		}
        
        Money[] balances = new Money[6];
        balances[0] = interestForCurrentPeriod;
        balances[1] = feeForCurrentPeriod;
        balances[2] = penaltyForCurrentPeriod;
        balances[3] = feeAccountedForCurrentPeriod;
        balances[4] = penaltyAccoutedForCurrentPeriod;
        balances[5] = interestAccountedForCurrentPeriod;
        return balances;
    }
    
    
    public Money[] retriveIncomeForOverlappingPeriod(final LocalDate paymentDate) {
        Money[] balances = new Money[3];
        final MonetaryCurrency currency = getCurrency();
        balances[0] = balances[1] = balances[2] = Money.zero(currency);        
        for (final LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
            if (installment.getDueDate().isEqual(paymentDate)){
                Money interest = installment.getInterestCharged(currency);
                Money fee = installment.getFeeChargesCharged(currency);
                Money penalty = installment.getPenaltyChargesCharged(currency);
                balances[0] = interest;
                balances[1] = fee;
                balances[2] = penalty;
                break;
            }else if(installment.getDueDate().isAfter(paymentDate) && installment.getFromDate().isBefore(paymentDate)){
                balances = fetchInterestFeeAndPenaltyTillDate(paymentDate, currency, installment);
                break;
            }
        }
       
        return balances;
    }
    private double calculateInterestForDays(int daysInPeriod, BigDecimal interest, int days) {
        if (interest.doubleValue() == 0) { return 0; }
        return ((interest.doubleValue()) / daysInPeriod) * days;
    }

    public Money[] getReceivableIncome(final LocalDate tillDate) {
        MonetaryCurrency currency = getCurrency();
        Money receivableInterest = Money.zero(currency);
        Money receivableFee = Money.zero(currency);
        Money receivablePenalty = Money.zero(currency);
        Money[] receivables = new Money[3];
        for (final LoanTransaction transaction : this.loanTransactions) {
            if (transaction.isNotReversed() && !transaction.isRepaymentAtDisbursement() && !transaction.isDisbursement()
                    && !transaction.isBrokenPeriodInterestPosting() && !transaction.getTransactionDate().isAfter(tillDate)) {
                if (transaction.isAccrual()) {
                    receivableInterest = receivableInterest.plus(transaction.getInterestPortion(currency));
                    receivableFee = receivableFee.plus(transaction.getFeeChargesPortion(currency));
                    receivablePenalty = receivablePenalty.plus(transaction.getPenaltyChargesPortion(currency));
                } else if (transaction.isRepayment() || transaction.isChargePayment()) {
                    receivableInterest = receivableInterest.minus(transaction.getInterestPortion(currency));
                    receivableFee = receivableFee.minus(transaction.getFeeChargesPortion(currency));
                    receivablePenalty = receivablePenalty.minus(transaction.getPenaltyChargesPortion(currency));
                }
            }
            if (receivableInterest.isLessThanZero()) {
                receivableInterest = receivableInterest.zero();
            }
            if (receivableFee.isLessThanZero()) {
                receivableFee = receivableFee.zero();
            }
            if (receivablePenalty.isLessThanZero()) {
                receivablePenalty = receivablePenalty.zero();
            }
        }
        receivables[0] = receivableInterest;
        receivables[1] = receivableFee;
        receivables[2] = receivablePenalty;
        return receivables;
    }
    
    public void reverseAccrualsAfter(final LocalDate tillDate) {
        for (final LoanTransaction transaction : this.loanTransactions) {
            if (transaction.isAccrual() && transaction.getTransactionDate().isAfter(tillDate)) {
                transaction.reverse();
            }
        }
    }

    public ChangedTransactionDetail handleForeClosureTransactions(final LoanTransaction repaymentTransaction,
            final LoanLifecycleStateMachine loanLifecycleStateMachine, final ScheduleGeneratorDTO scheduleGeneratorDTO,
            final AppUser appUser) {

        LoanEvent event = LoanEvent.LOAN_FORECLOSURE;
        validateAccountStatus(event);
        validateForForeclosure(repaymentTransaction.getTransactionDate());
        this.loanSubStatus = LoanSubStatus.FORECLOSED.getValue();
        applyAccurals(appUser);
        return handleRepaymentOrRecoveryOrWaiverTransaction(repaymentTransaction, loanLifecycleStateMachine, null, scheduleGeneratorDTO,
                appUser);
    }

    public Money retrieveAccruedAmountAfterDate(final LocalDate tillDate) {
        Money totalAmountAccrued = Money.zero(getCurrency());
        Money actualAmountTobeAccrued = Money.zero(getCurrency());
        for (final LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
            totalAmountAccrued = totalAmountAccrued.plus(installment.getInterestAccrued(getCurrency()));

            if (tillDate.isAfter(installment.getFromDate()) && tillDate.isBefore(installment.getDueDate())) {
                int daysInPeriod = Days.daysBetween(installment.getFromDate(), installment.getDueDate()).getDays();
                int tillDays = Days.daysBetween(installment.getFromDate(), tillDate).getDays();
                double interest = calculateInterestForDays(daysInPeriod, installment.getInterestCharged(getCurrency()).getAmount(),
                        tillDays);
                actualAmountTobeAccrued = actualAmountTobeAccrued.plus(interest);
            } else if ((tillDate.isAfter(installment.getFromDate()) && tillDate.isEqual(installment.getDueDate()))
                    || (tillDate.isEqual(installment.getFromDate()) && tillDate.isEqual(installment.getDueDate()))
                    || (tillDate.isAfter(installment.getFromDate()) && tillDate.isAfter(installment.getDueDate()))) {
                actualAmountTobeAccrued = actualAmountTobeAccrued.plus(installment.getInterestAccrued(getCurrency()));
            }
        }
        Money accredAmountAfterDate = totalAmountAccrued.minus(actualAmountTobeAccrued);
        if (accredAmountAfterDate.isLessThanZero()) {
            accredAmountAfterDate = Money.zero(getCurrency());
        }
        return accredAmountAfterDate;
    }

    public void validateForForeclosure(final LocalDate transactionDate) {

        if (isInterestRecalculationEnabledForProduct()) {
            final String defaultUserMessage = "The loan with interest recalculation enabled cannot be foreclosed.";
            throw new LoanForeclosureException("loan.with.interest.recalculation.enabled.cannot.be.foreclosured", defaultUserMessage,
                    getId());
        }

        LocalDate lastUserTransactionDate = getLastUserTransactionDate();

        if (DateUtils.isDateInTheFuture(transactionDate)) {
            final String defaultUserMessage = "The transactionDate cannot be in the future.";
            throw new LoanForeclosureException("loan.foreclosure.transaction.date.is.in.future", defaultUserMessage, transactionDate);
        }

        if (lastUserTransactionDate.isAfter(transactionDate)) {
            final String defaultUserMessage = "The transactionDate cannot be in the future.";
            throw new LoanForeclosureException("loan.foreclosure.transaction.date.cannot.before.the.last.transaction.date",
                    defaultUserMessage, transactionDate);
        }
    }

    public void updateInstallmentsPostDate(LocalDate transactionDate) {
        List<LoanRepaymentScheduleInstallment> newInstallments = new ArrayList<>(this.repaymentScheduleInstallments);
        final MonetaryCurrency currency = getCurrency();
        Money totalPrincipal = Money.zero(currency);
        Money [] balances = retriveIncomeForOverlappingPeriod(transactionDate);
        boolean isInterestComponent = true;
        int installmentNumberForRemovingCharges = this.repaymentScheduleInstallments.size();
        for (final LoanRepaymentScheduleInstallment installment : this.repaymentScheduleInstallments) {
            if (!installment.getDueDate().isBefore(transactionDate)) {
                    totalPrincipal = totalPrincipal.plus(installment.getPrincipal(currency));
                    newInstallments.remove(installment);
                    if(installment.getDueDate().isEqual(transactionDate)){
                        isInterestComponent = false;
                    }
                if (installmentNumberForRemovingCharges > installment.getInstallmentNumber()) {
                    installmentNumberForRemovingCharges = installment.getInstallmentNumber();
                }
            }   
            
        }
        
        for(LoanDisbursementDetails loanDisbursementDetails : getDisbursementDetails()){
            if(loanDisbursementDetails.actualDisbursementDate() == null){
                 totalPrincipal = Money.of(currency, totalPrincipal.getAmount().subtract(loanDisbursementDetails.principal()));
            }
        }
        
        LocalDate installmentStartDate = getDisbursementDate();

        if (newInstallments.size() > 0) {
            installmentStartDate = newInstallments.get((newInstallments.size() - 1)).getDueDate();
        }        
        int installmentNumber = newInstallments.size();
       
        if (!isInterestComponent) {
            installmentNumber++;
        }

        LoanRepaymentScheduleInstallment newInstallment = new LoanRepaymentScheduleInstallment(null, newInstallments.size() + 1,
                installmentStartDate, transactionDate, totalPrincipal.getAmount(), balances[0].getAmount(), balances[1].getAmount(),
                balances[2].getAmount(), isInterestComponent, null);
        newInstallment.updateInstallmentNumber(newInstallments.size() + 1);
        newInstallments.add(newInstallment);

        updateLoanScheduleOnForeclosure(newInstallments);

        Set<LoanCharge> charges = this.charges();
        int penaltyWaitPeriod = 0;
        for (LoanCharge loanCharge : charges) {
            if (loanCharge.getDueLocalDate() != null
                    && (loanCharge.getDueLocalDate().isAfter(transactionDate))) {
                loanCharge.setActive(false);
            } else if (loanCharge.getDueLocalDate() == null) {
                recalculateLoanCharge(loanCharge, penaltyWaitPeriod);
                loanCharge.updateWaivedAmount(currency);
            } else if (loanCharge.isOverdueInstallmentCharge()
                    && loanCharge.getOverdueInstallmentCharge().getInstallment().getInstallmentNumber() >= installmentNumberForRemovingCharges){
                loanCharge.setActive(false);
            }
        }
        
        for(LoanTransaction loanTransaction:getLoanTransactions()){
            if(loanTransaction.isChargesWaiver()){
                for(LoanChargePaidBy chargePaidBy: loanTransaction.getLoanChargesPaid()){
                    if ((chargePaidBy.getLoanCharge().isDueDateCharge() && chargePaidBy.getLoanCharge().getDueLocalDate()
                            .isAfter(transactionDate))
                            || (chargePaidBy.getLoanCharge().isInstalmentFee() && (chargePaidBy.getInstallmentNumber() != null && chargePaidBy
                                    .getInstallmentNumber() > installmentNumber))){
                        loanTransaction.reverse();
                    }
                }
            }
        }
    }

    public void updateLoanScheduleOnForeclosure(final Collection<LoanRepaymentScheduleInstallment> installments) {
        this.repaymentScheduleInstallments.clear();
        for (final LoanRepaymentScheduleInstallment installment : installments) {
            addRepaymentScheduleInstallment(installment);
        }
    }
    
    public Integer getLoanSubStatus() {
        return this.loanSubStatus;
    }
    
    private boolean isForeclosure(){
        boolean isForeClosure = false;
        if(this.loanSubStatus != null){
            isForeClosure = LoanSubStatus.fromInt(loanSubStatus).isForeclosed();
        }
        
        return isForeClosure;
    }

    public Set<LoanTermVariations> getActiveLoanTermVariations() {
        Set<LoanTermVariations> retData = new HashSet<>();
        if (this.loanTermVariations != null && this.loanTermVariations.size() > 0) {
            for (LoanTermVariations loanTermVariations : this.loanTermVariations) {
                if (loanTermVariations.isActive()) {
                    retData.add(loanTermVariations);
                }
            }
        }
        return retData.size() > 0 ? retData : null;
    }

    public void setIsTopup(final boolean isTopup) {
        this.isTopup = isTopup;
    }

    public boolean isTopup() {
        return this.isTopup;
    }

    public BigDecimal getFirstDisbursalAmount() {
        BigDecimal firstDisbursalAmount;

        if(this.isMultiDisburmentLoan()){
            List<DisbursementData> disbursementData = getDisbursmentData();
            Collections.sort(disbursementData);
            firstDisbursalAmount = disbursementData.get(disbursementData.size()-1).amount();
        }else{
            firstDisbursalAmount = this.getLoanRepaymentScheduleDetail().getPrincipal().getAmount();
        }
        return firstDisbursalAmount;
    }

    public void setTopupLoanDetails(LoanTopupDetails topupLoanDetails) {
        this.loanTopupDetails = topupLoanDetails;
    }

    public LoanTopupDetails getTopupLoanDetails() {
        return this.loanTopupDetails;
    }
    
    public Collection<LoanCharge> getLoanCharges() {
        return this.charges;
    }

    
    public LoanPurpose getLoanPurpose() {
        return this.loanPurpose;
    }

    public Set<LoanOfficerAssignmentHistory> getLoanOfficerHistory() {
        return this.loanOfficerHistory;
    }
    
    public Staff loanOfficer(){
        return this.loanOfficer;
    }
    
    /**
     * this method gives write off amount portions for glim 
     * @param command
     * @param writtenOffOnLocalDate
     * @param loanTransaction
     * @param changes
     * @param existingTransactionIds
     * @param existingReversedTransactionIds
     * @param currentUser
     * @return
     */
    public ChangedTransactionDetail GlimLoanCloseAsWrittenOff(JsonCommand command, LocalDate writtenOffOnLocalDate,
            LoanTransaction loanTransaction, Map<String, Object> changes, List<Long> existingTransactionIds,
            List<Long> existingReversedTransactionIds, AppUser currentUser) {

        validateAccountStatus(LoanEvent.WRITE_OFF_OUTSTANDING);

        ChangedTransactionDetail changedTransactionDetail = new ChangedTransactionDetail();

        Map<Integer, Map<String, BigDecimal>> installmentWriteOffMap = new HashMap<>();
        BigDecimal totalPrincipalToBeWriteOff = BigDecimal.ZERO;
        BigDecimal totalInterestToBeWriteOff = BigDecimal.ZERO;
        BigDecimal totalChargeToBeWriteOff = BigDecimal.ZERO;
        
        existingTransactionIds.addAll(findExistingTransactionIds());
        existingReversedTransactionIds.addAll(findExistingReversedTransactionIds());

        List<GroupLoanIndividualMonitoring> glimMembers = this.glimList;
        for (GroupLoanIndividualMonitoring glimMember : glimMembers) {

            if (!glimMember.isWrittenOff()) {

                this.closedOnDate = writtenOffOnLocalDate.toDate();
                this.writtenOffOnDate = writtenOffOnLocalDate.toDate();
                this.closedBy = currentUser;
                changes.put("closedOnDate", command.stringValueOfParameterNamed("transactionDate"));
                changes.put("writtenOffOnDate", command.stringValueOfParameterNamed("transactionDate"));

                validateGlimWriteOff(writtenOffOnLocalDate);

                Money transactionAmountPerClient = Money.of(getCurrency(), glimMember.getTransactionAmount());
                Loan loan = loanTransaction.getLoan();

                Map<String, BigDecimal> installmentPaidMap = new HashMap<>();
                installmentPaidMap.put("unpaidCharge", BigDecimal.ZERO);
                installmentPaidMap.put("unpaidInterest", BigDecimal.ZERO);
                installmentPaidMap.put("unpaidPrincipal", BigDecimal.ZERO);
                installmentPaidMap.put("unprocessedOverPaidAmount", BigDecimal.ZERO);
                installmentPaidMap.put("installmentTransactionAmount", BigDecimal.ZERO);

                Map<Long, BigDecimal> feeChargesWriteOffPerInstallments = new HashMap<>();

                // determine how much is written off in total and breakdown for
                // principal, interest and charges
                for (final LoanRepaymentScheduleInstallment currentInstallment : this.repaymentScheduleInstallments) {
                    if (transactionAmountPerClient.isGreaterThanZero()) {
                        Map<String, BigDecimal> paidInstallmentMap = GroupLoanIndividualMonitoringTransactionAssembler.getSplit(glimMember,
                                transactionAmountPerClient.getAmount(), loan, currentInstallment.getInstallmentNumber(),
                                installmentPaidMap, loanTransaction, null);

                        if (!(MathUtility.isZero(paidInstallmentMap.get("installmentTransactionAmount")) && MathUtility
                                .isGreaterThanZero(glimMember.getTotalPaidAmount()))) {
                            if (currentInstallment.isNotFullyPaidOff()) {

                                Map<String, BigDecimal> splitMap = GroupLoanIndividualMonitoringTransactionAssembler.getSplit(glimMember,
                                        transactionAmountPerClient.getAmount(), loan, currentInstallment.getInstallmentNumber(),
                                        installmentPaidMap, loanTransaction, null);

                                Money feePortionForCurrentInstallment = Money.of(getCurrency(), splitMap.get("unpaidCharge"));
                                Money interestPortionForCurrentInstallment = Money.of(getCurrency(), splitMap.get("unpaidInterest"));
                                Money principalPortionForCurrentInstallment = Money.of(getCurrency(), splitMap.get("unpaidPrincipal"));
                                Money totalAmountForCurrentInstallment = Money.of(getCurrency(),
                                        splitMap.get("installmentTransactionAmount"));
                                totalPrincipalToBeWriteOff = totalPrincipalToBeWriteOff.add(principalPortionForCurrentInstallment
                                        .getAmount());
                                totalInterestToBeWriteOff = totalInterestToBeWriteOff.add(interestPortionForCurrentInstallment.getAmount());
                                totalChargeToBeWriteOff = totalChargeToBeWriteOff.add(feePortionForCurrentInstallment.getAmount());
                                if (installmentWriteOffMap.containsKey(currentInstallment.getInstallmentNumber())) {
                                    Map<String, BigDecimal> installmentBreakUpMap = installmentWriteOffMap.get(currentInstallment
                                            .getInstallmentNumber());
                                    installmentBreakUpMap.put("principal",
                                            installmentBreakUpMap.get("principal").add(principalPortionForCurrentInstallment.getAmount()));
                                    installmentBreakUpMap.put("interest",
                                            installmentBreakUpMap.get("interest").add(interestPortionForCurrentInstallment.getAmount()));
                                    installmentBreakUpMap.put("charge",
                                            installmentBreakUpMap.get("charge").add(feePortionForCurrentInstallment.getAmount()));
                                    installmentWriteOffMap.put(currentInstallment.getInstallmentNumber(), installmentBreakUpMap);
                                } else {
                                    Map<String, BigDecimal> installmentBreakUpMap = new HashMap<>();
                                    installmentBreakUpMap.put("principal", principalPortionForCurrentInstallment.getAmount());
                                    installmentBreakUpMap.put("interest", interestPortionForCurrentInstallment.getAmount());
                                    installmentBreakUpMap.put("charge", feePortionForCurrentInstallment.getAmount());
                                    installmentWriteOffMap.put(currentInstallment.getInstallmentNumber(), installmentBreakUpMap);
                                }

                                installmentPaidMap.put("unpaidCharge",
                                        installmentPaidMap.get("unpaidCharge").add(feePortionForCurrentInstallment.getAmount()));
                                installmentPaidMap.put("unpaidInterest",
                                        installmentPaidMap.get("unpaidInterest").add(interestPortionForCurrentInstallment.getAmount()));
                                installmentPaidMap.put("unpaidPrincipal",
                                        installmentPaidMap.get("unpaidPrincipal").add(principalPortionForCurrentInstallment.getAmount()));
                                installmentPaidMap.put("unprocessedOverPaidAmount",BigDecimal.ZERO);
                                installmentPaidMap.put(
                                        "installmentTransactionAmount",
                                        installmentPaidMap.get("installmentTransactionAmount").add(
                                                totalAmountForCurrentInstallment.getAmount()));
                                transactionAmountPerClient = transactionAmountPerClient.minus(totalAmountForCurrentInstallment);

                                feeChargesWriteOffPerInstallments = writeOffLoanChargeForGlim(currentInstallment,
                                        feePortionForCurrentInstallment.getAmount(), glimMember, feeChargesWriteOffPerInstallments);

                            }
                        }
                    }
                }

                BigDecimal writeOfAmount = MathUtility.add(glimMember.getPrincipalWrittenOffAmount(),
                        glimMember.getInterestWrittenOffAmount(), glimMember.getChargeWrittenOffAmount());
                if (MathUtility.isGreaterThanZero(writeOfAmount) && MathUtility.isGreaterThanZero(glimMember.getTransactionAmount())
                        && !loanTransaction.getTypeOf().isRecoveryRepayment()) { throw new ClientAlreadyWriteOffException(); }

            }
        }
        /**
         * for each installment update the write off portions
         */
        final LocalDate transactionDate = loanTransaction.getTransactionDate();
        updateWriteOffAmountForEachInstallment(installmentWriteOffMap, transactionDate);
        loanTransaction.updateComponentsAndTotal(Money.of(getCurrency(), totalPrincipalToBeWriteOff),
                Money.of(getCurrency(), totalInterestToBeWriteOff), Money.of(getCurrency(), totalChargeToBeWriteOff),
                Money.zero(getCurrency()));

        updateLoanSummaryDerivedFields();
        BigDecimal loanPrincipalOutstandingBalance = this.getLoanSummary().getTotalPrincipalOutstanding();
        loanTransaction.updateOutstandingLoanBalance(loanPrincipalOutstandingBalance);
        changedTransactionDetail.getNewTransactionMappings().put(loanTransaction.getId(), loanTransaction);

        return changedTransactionDetail;
    }

    private void updateWriteOffAmountForEachInstallment(Map<Integer, Map<String, BigDecimal>> installmentWriteOffMap,
            final LocalDate transactionDate) {
        for (final LoanRepaymentScheduleInstallment currentInstallment : this.repaymentScheduleInstallments) {
            Map<String, BigDecimal> installmentBreakUpMap = installmentWriteOffMap.get(currentInstallment.getInstallmentNumber());
            if (installmentBreakUpMap != null) {
                currentInstallment.writeOffOutstandingPrincipalForGlim(transactionDate, getCurrency(),
                        Money.of(getCurrency(), MathUtility.zeroIfNull(installmentBreakUpMap.get("principal"))));
                currentInstallment.writeOffOutstandingInterestForGlim(transactionDate, getCurrency(),
                        Money.of(getCurrency(), MathUtility.zeroIfNull(installmentBreakUpMap.get("interest"))));
                currentInstallment.writeOffOutstandingFeeChargeForGlim(transactionDate, getCurrency(),
                        Money.of(getCurrency(), MathUtility.zeroIfNull(installmentBreakUpMap.get("charge"))));
            }

        }
    }

    private void validateGlimWriteOff(LocalDate writtenOffOnLocalDate) {
        if (writtenOffOnLocalDate.isBefore(getDisbursementDate())) {
            final String errorMessage = "The date on which a loan is written off cannot be before the loan disbursement date: "
                    + getDisbursementDate().toString();
            throw new InvalidLoanStateTransitionException("writeoff", "cannot.be.before.submittal.date", errorMessage,
                    writtenOffOnLocalDate, getDisbursementDate());
        }

        validateActivityNotBeforeClientOrGroupTransferDate(LoanEvent.WRITE_OFF_OUTSTANDING, writtenOffOnLocalDate);

        if (writtenOffOnLocalDate.isAfter(DateUtils.getLocalDateOfTenant())) {
            final String errorMessage = "The date on which a loan is written off cannot be in the future.";
            throw new InvalidLoanStateTransitionException("writeoff", "cannot.be.a.future.date", errorMessage, writtenOffOnLocalDate);
        }

        LocalDate lastTransactionDate = getLastUserTransactionDate();
        if (lastTransactionDate.isAfter(writtenOffOnLocalDate)) {
            final String errorMessage = "The date of the writeoff transaction must occur on or before previous transactions.";
            throw new InvalidLoanStateTransitionException("writeoff", "must.occur.on.or.after.other.transaction.dates", errorMessage,
                    writtenOffOnLocalDate);
        }
    }

    /**
     * write off loan charge amount for each installment
     * 
     * @param currentInstallment
     * @param amount
     * @param glimMember
     * @param feeChargesWriteOffPerInstallments
     * @return
     */
    private Map<Long, BigDecimal> writeOffLoanChargeForGlim(LoanRepaymentScheduleInstallment currentInstallment, BigDecimal amount,
            GroupLoanIndividualMonitoring glimMember, Map<Long, BigDecimal> feeChargesWriteOffPerInstallments) {

        if (MathUtility.isGreaterThanZero(amount)) {
            Set<GroupLoanIndividualMonitoringCharge> glimCharges = glimMember.getGroupLoanIndividualMonitoringCharges();
            for (GroupLoanIndividualMonitoringCharge glimCharge : glimCharges) {
                Long chargeId = glimCharge.getCharge().getId();
                BigDecimal chargeAmount = MathUtility.isNull(glimCharge.getRevisedFeeAmount()) ? glimCharge.getFeeAmount() : glimCharge
                        .getRevisedFeeAmount();
                for (LoanCharge loanCharge : charges()) {
                    if (loanCharge.getCharge().getId().equals(chargeId)) {
                        BigDecimal writeOffAmount = BigDecimal.ZERO;
                        if (ChargeTimeType.fromInt(loanCharge.getCharge().getChargeTimeType().intValue()).isUpfrontFee()) {
                            if (currentInstallment.getInstallmentNumber().equals(ChargesApiConstants.applyUpfrontFeeOnFirstInstallment)) {
                                writeOffAmount = amount;
                            }
                        } else {
                            writeOffAmount = MathUtility.getInstallmentAmount(chargeAmount, this.fetchNumberOfInstallmensAfterExceptions(),
                                    this.getCurrency(), currentInstallment.getInstallmentNumber());
                        }
                        loanCharge.updateWriteOffAmount(getCurrency(), Money.of(getCurrency(), writeOffAmount),
                                currentInstallment.getInstallmentNumber());

                    }
                }
            }
        }

        return feeChargesWriteOffPerInstallments;
    }
    /**
     * waive loan charges glim charges
     * @param currentInstallment
     * @param amount
     * @param glimMember
     * @param feeChargesWriteOffPerInstallments
     * @param existingTransactionIds
     * @param existingReversedTransactionIds
     * @param accruedCharge
     * @param currentUser
     * @param scheduleGeneratorDTO
     * @param changes
     * @return
     */
    public Map<Long, BigDecimal> waiveLoanChargeForGlim(LoanRepaymentScheduleInstallment currentInstallment, BigDecimal amount,
            GroupLoanIndividualMonitoring glimMember, Map<Long, BigDecimal> feeChargesWriteOffPerInstallments,
            List<Long> existingTransactionIds, List<Long> existingReversedTransactionIds, Money accruedCharge, AppUser currentUser,
            ScheduleGeneratorDTO scheduleGeneratorDTO) {

        if (MathUtility.isGreaterThanZero(amount)) {
            Set<GroupLoanIndividualMonitoringCharge> glimCharges = glimMember.getGroupLoanIndividualMonitoringCharges();
            for (LoanCharge loanCharge : charges()) {
                for (GroupLoanIndividualMonitoringCharge glimCharge : glimCharges) {
                    Long chargeId = glimCharge.getCharge().getId();
                    BigDecimal chargeAmount = MathUtility.isNull(glimCharge.getRevisedFeeAmount()) ? glimCharge.getFeeAmount() : glimCharge
                            .getRevisedFeeAmount();
                    if (loanCharge.getCharge().getId().equals(chargeId)) {

                        BigDecimal chargeAmountToBeWaived = BigDecimal.ZERO;
                        if (ChargeTimeType.fromInt(loanCharge.getCharge().getChargeTimeType().intValue()).isUpfrontFee()) {
                            if (currentInstallment.getInstallmentNumber().equals(ChargesApiConstants.applyUpfrontFeeOnFirstInstallment)) {
                                chargeAmountToBeWaived = amount;
                            }
                        } else {
                            chargeAmountToBeWaived = MathUtility.getInstallmentAmount(chargeAmount,
                                    this.fetchNumberOfInstallmensAfterExceptions(), this.getCurrency(),
                                    currentInstallment.getInstallmentNumber());
                        }

                        waiveGlimLoanCharge(loanCharge, existingTransactionIds, existingReversedTransactionIds,
                                currentInstallment.getInstallmentNumber(), scheduleGeneratorDTO, accruedCharge, currentUser,
                                Money.of(getCurrency(), chargeAmountToBeWaived));

                        if (feeChargesWriteOffPerInstallments.containsKey(chargeId)) {
                            feeChargesWriteOffPerInstallments.put(chargeId,
                                    MathUtility.add(chargeAmountToBeWaived, feeChargesWriteOffPerInstallments.get(chargeId)));
                        } else {
                            feeChargesWriteOffPerInstallments.put(chargeId, chargeAmountToBeWaived);
                        }

                        glimCharge.setWaivedChargeAmount(feeChargesWriteOffPerInstallments.get(chargeId));
                    }
                }
            }
        }

        return feeChargesWriteOffPerInstallments;
    }

    public void updateDefautGlimMembers(List<GroupLoanIndividualMonitoring> defaultGlimMembers) {
        this.defaultGlimMembers.addAll(defaultGlimMembers);
    }
    
    public List<GroupLoanIndividualMonitoring> getDefautGlimMembers() {
        return this.defaultGlimMembers;
    }

    
    public BigDecimal getTotalCapitalizedCharges() {
        return totalCapitalizedCharges;
    }

    
    public void setTotalCapitalizedCharges(BigDecimal totalCapitalizedCharges) {
        this.totalCapitalizedCharges = totalCapitalizedCharges;
    } 
    
    public void activateLoan(){
    	this.loanStatus = LoanStatus.ACTIVE.getValue();
    }

    public PaymentType getExpectedDisbursalPaymentType() {
        return this.expectedDisbursalPaymentType;
    }

    public void setExpectedDisbursalPaymentType(PaymentType expectedDisbursalPaymentType) {
        this.expectedDisbursalPaymentType = expectedDisbursalPaymentType;
    }

    public PaymentType getExpectedRepaymentPaymentType() {
        return this.expectedRepaymentPaymentType;
    }

    public void setExpectedRepaymentPaymentType(PaymentType expectedRepaymentPaymentType) {
        this.expectedRepaymentPaymentType = expectedRepaymentPaymentType;
    }

    public LoanTransaction getLastUserTransaction(List<Long> excludedTransactionIds) {
        LoanTransaction lastUserTransaction = null;
        for (int i = this.loanTransactions.size() - 1; i >= 0; i--) {
            LoanTransaction transaction = this.getLoanTransactions().get(i);
            if (!(transaction.isReversed() || transaction.isAccrual() || transaction.isIncomePosting())) {
                if (excludedTransactionIds == null || !excludedTransactionIds.contains(transaction.getId())) {
                    lastUserTransaction = transaction;
                    break;
                }
            }
        }
        return lastUserTransaction;
    }

    
    public LocalDate getActualDisbursementDate() {
        LocalDate disbursementDate = null;
        if (this.actualDisbursementDate != null) {
            disbursementDate = new LocalDate(this.actualDisbursementDate);
        }
        return disbursementDate;
    }

    
    public Fund getFund() {
        return this.fund;
    }

    
    public BigDecimal getFlatInterestRate() {
        return this.flatInterestRate;
    }

    
    public void setFlatInterestRate(BigDecimal flatInterestRate) {
        this.flatInterestRate = flatInterestRate;
    }

    
    public void setFixedEmiAmount(BigDecimal fixedEmiAmount) {
        this.fixedEmiAmount = fixedEmiAmount;
    }
    
    public Money getPrincipalWithBrokenPeriodInterest() {
        Money principal = this.loanRepaymentScheduleDetail.getPrincipal();
        switch (this.loanRepaymentScheduleDetail.getBrokenPeriodMethod()) {
            case POST_INTEREST:
                principal = principal.plus(this.brokenPeriodInterest);
            break;

            default:
            break;
        }
        return principal;
    }

    
    public Money getBrokenPeriodInterest() {
        return Money.of(getCurrency(), this.brokenPeriodInterest);
    }

    public void setBrokenPeriodInterest(BigDecimal brokenPeriodInterest) {
        this.brokenPeriodInterest = brokenPeriodInterest;
    }

    public BigDecimal getCalculatedInstallmentAmount() {
        return this.calculatedInstallmentAmount;
    }

    public void setCalculatedInstallmentAmount(BigDecimal calculatedInstallmentAmount) {
        this.calculatedInstallmentAmount = calculatedInstallmentAmount;
    }

    public BigDecimal getDiscountOnDisburseAmount() {
        return this.discountOnDisbursalAmount;
    }

    public void setDiscountOnDisbursalAmount(final JsonCommand command) {
        if (command.hasParameter(LoanApiConstants.discountOnDisbursalAmountParameterName)) {
            if (this.isApproved() && this.loanProduct().isFlatInterestRate()) {
                BigDecimal disbursalDiscountAmount = command
                        .bigDecimalValueOfParameterNamed(LoanApiConstants.discountOnDisbursalAmountParameterName);
                this.discountOnDisbursalAmount = disbursalDiscountAmount;
            } else {
                final List<String> unsupportedParameters = new ArrayList<>(1);
                unsupportedParameters.add(LoanApiConstants.discountOnDisbursalAmountParameterName);
                throw new UnsupportedParameterException(unsupportedParameters);
            }
        } else {
            this.discountOnDisbursalAmount = null;
        }
    }
    
    public void setDiscountOnDisbursalAmount(final BigDecimal discountOnDisbursalAmount) {
        this.discountOnDisbursalAmount = discountOnDisbursalAmount;
    }

    public int getActiveTrancheCount() {
        int size = 0;
        for (LoanDisbursementDetails details : getDisbursementDetails()) {
            if (details.isActive()) {
                size++;
            }
        }
        return size;
    }


    public boolean isGlimPaymentAsGroup() {
        return this.isGlimPaymentAsGroup;
    }

    public void setGlimPaymentAsGroup(boolean isGlimPaymentAsGroup) {
        this.isGlimPaymentAsGroup = isGlimPaymentAsGroup;
    }
    
    public void validateDateIsOnNonWorkingDay(final LocalDate repaymentDate, final WorkingDays workingDays,
            final boolean allowTransactionsOnNonWorkingDay, final String errorMessage, final String errorCode) {
        if (!allowTransactionsOnNonWorkingDay) {
            if (!WorkingDaysUtil.isWorkingDay(workingDays, repaymentDate)) { throw new LoanApplicationDateException(errorCode,
                    errorMessage, repaymentDate); }
        }
    }

    public void validateDateIsOnHoliday(final LocalDate repaymentDate, final boolean allowTransactionsOnHoliday,
            final List<Holiday> holidays, final String errorMessage, final String errorCode) {
        if (!allowTransactionsOnHoliday) {
            if (HolidayUtil.isHoliday(repaymentDate, holidays)) { throw new LoanApplicationDateException(errorCode, errorMessage,
                    repaymentDate); }
        }
    }
}
