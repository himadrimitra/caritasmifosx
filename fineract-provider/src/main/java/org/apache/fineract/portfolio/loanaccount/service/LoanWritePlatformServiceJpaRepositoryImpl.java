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
package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.accountnumberformat.domain.EntityAccountType;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformServiceUnavailableException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.entityaccess.domain.FineractEntityAccessType;
import org.apache.fineract.infrastructure.entityaccess.service.FineractEntityAccessUtil;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.holiday.domain.HolidayRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.monetary.domain.MoneyHelper;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.organisation.workingdays.data.AdjustedDateDetailsDTO;
import org.apache.fineract.organisation.workingdays.data.WorkingDayExemptionsData;
import org.apache.fineract.organisation.workingdays.domain.WorkingDays;
import org.apache.fineract.organisation.workingdays.domain.WorkingDaysRepositoryWrapper;
import org.apache.fineract.organisation.workingdays.service.WorkingDayExemptionsReadPlatformService;
import org.apache.fineract.portfolio.account.PortfolioAccountType;
import org.apache.fineract.portfolio.account.data.AccountTransferDTO;
import org.apache.fineract.portfolio.account.data.PortfolioAccountData;
import org.apache.fineract.portfolio.account.domain.AccountAssociationType;
import org.apache.fineract.portfolio.account.domain.AccountAssociations;
import org.apache.fineract.portfolio.account.domain.AccountAssociationsRepository;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetailRepository;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetails;
import org.apache.fineract.portfolio.account.domain.AccountTransferRecurrenceType;
import org.apache.fineract.portfolio.account.domain.AccountTransferStandingInstruction;
import org.apache.fineract.portfolio.account.domain.AccountTransferType;
import org.apache.fineract.portfolio.account.domain.StandingInstructionPriority;
import org.apache.fineract.portfolio.account.domain.StandingInstructionStatus;
import org.apache.fineract.portfolio.account.domain.StandingInstructionType;
import org.apache.fineract.portfolio.account.service.AccountAssociationsReadPlatformService;
import org.apache.fineract.portfolio.account.service.AccountTransfersReadPlatformService;
import org.apache.fineract.portfolio.account.service.AccountTransfersWritePlatformService;
import org.apache.fineract.portfolio.account.service.StandingInstructionWritePlatformService;
import org.apache.fineract.portfolio.accountdetails.domain.AccountType;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstanceRepository;
import org.apache.fineract.portfolio.calendar.domain.CalendarRepository;
import org.apache.fineract.portfolio.calendar.domain.CalendarType;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargePaymentMode;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.charge.exception.ChargeCannotBeUpdatedException;
import org.apache.fineract.portfolio.charge.exception.LoanChargeCannotBeAddedException;
import org.apache.fineract.portfolio.charge.exception.LoanChargeCannotBeDeletedException;
import org.apache.fineract.portfolio.charge.exception.LoanChargeCannotBeDeletedException.LOAN_CHARGE_CANNOT_BE_DELETED_REASON;
import org.apache.fineract.portfolio.charge.exception.LoanChargeCannotBePayedException;
import org.apache.fineract.portfolio.charge.exception.LoanChargeCannotBePayedException.LOAN_CHARGE_CANNOT_BE_PAYED_REASON;
import org.apache.fineract.portfolio.charge.exception.LoanChargeCannotBeUpdatedException;
import org.apache.fineract.portfolio.charge.exception.LoanChargeCannotBeUpdatedException.LOAN_CHARGE_CANNOT_BE_UPDATED_REASON;
import org.apache.fineract.portfolio.charge.exception.LoanChargeCannotBeWaivedException;
import org.apache.fineract.portfolio.charge.exception.LoanChargeCannotBeWaivedException.LOAN_CHARGE_CANNOT_BE_WAIVED_REASON;
import org.apache.fineract.portfolio.charge.exception.LoanChargeNotFoundException;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.exception.ClientNotActiveException;
import org.apache.fineract.portfolio.client.service.ClientWritePlatformService;
import org.apache.fineract.portfolio.collectionsheet.command.CollectionSheetBulkDisbursalCommand;
import org.apache.fineract.portfolio.collectionsheet.command.CollectionSheetBulkRepaymentCommand;
import org.apache.fineract.portfolio.collectionsheet.command.SingleDisbursalCommand;
import org.apache.fineract.portfolio.collectionsheet.command.SingleRepaymentCommand;
import org.apache.fineract.portfolio.collectionsheet.domain.CollectionSheetTransactionDetails;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.exception.CenterNotFoundException;
import org.apache.fineract.portfolio.group.exception.GroupNotActiveException;
import org.apache.fineract.portfolio.group.exception.UpdateStaffHierarchyException;
import org.apache.fineract.portfolio.group.service.CenterReadPlatformService;
import org.apache.fineract.portfolio.group.service.GroupingTypesWritePlatformService;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.command.LoanUpdateCommand;
import org.apache.fineract.portfolio.loanaccount.data.AdjustedLoanTransactionDetails;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringDataChanges;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringDataValidator;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargeData;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargePaidByData;
import org.apache.fineract.portfolio.loanaccount.data.LoanInstallmentChargeData;
import org.apache.fineract.portfolio.loanaccount.data.LoanOfficerAssignmentHistoryData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTermVariationsData;
import org.apache.fineract.portfolio.loanaccount.data.ScheduleGeneratorDTO;
import org.apache.fineract.portfolio.loanaccount.domain.ChangedTransactionDetail;
import org.apache.fineract.portfolio.loanaccount.domain.DefaultLoanLifecycleStateMachine;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepository;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanChargeRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDisbursementDetails;
import org.apache.fineract.portfolio.loanaccount.domain.LoanEvent;
import org.apache.fineract.portfolio.loanaccount.domain.LoanGlimRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanGlimRepaymentScheduleInstallmentRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanInstallmentCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanLifecycleStateMachine;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallmentRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleTransactionProcessorFactory;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanaccount.domain.LoanSummaryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTrancheDisbursementCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanaccount.exception.DateMismatchException;
import org.apache.fineract.portfolio.loanaccount.exception.ExceedingTrancheCountException;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidPaidInAdvanceAmountException;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidRefundDateException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanDisbursalException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanDisbursementDateException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanMultiDisbursementException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanOfficerAssignmentDateException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanOfficerAssignmentException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanOfficerUnassignmentException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanWriteOffException;
import org.apache.fineract.portfolio.loanaccount.exception.MultiDisbursementDataRequiredException;
import org.apache.fineract.portfolio.loanaccount.exception.SubsidyAmountExceedsPrincipalOutstandingException;
import org.apache.fineract.portfolio.loanaccount.exception.SubsidyNotApplicableException;
import org.apache.fineract.portfolio.loanaccount.exception.SubsidyNotAppliedException;
import org.apache.fineract.portfolio.loanaccount.guarantor.service.GuarantorDomainService;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.DefaultScheduledDateGenerator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanApplicationTerms;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.ScheduledDateGenerator;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.service.LoanRescheduleRequestReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.serialization.LoanApplicationCommandFromApiJsonHelper;
import org.apache.fineract.portfolio.loanaccount.serialization.LoanEventApiJsonValidator;
import org.apache.fineract.portfolio.loanaccount.serialization.LoanUpdateCommandFromApiJsonDeserializer;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductBusinessRuleValidator;
import org.apache.fineract.portfolio.loanproduct.exception.InvalidCurrencyException;
import org.apache.fineract.portfolio.loanproduct.exception.LinkedAccountRequiredException;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.exception.InsufficientAccountBalanceException;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class LoanWritePlatformServiceJpaRepositoryImpl implements LoanWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(LoanWritePlatformServiceJpaRepositoryImpl.class);
    private final ScheduledDateGenerator scheduledDateGenerator = new DefaultScheduledDateGenerator();

    private final PlatformSecurityContext context;
    private final LoanEventApiJsonValidator loanEventApiJsonValidator;
    private final LoanUpdateCommandFromApiJsonDeserializer loanUpdateCommandFromApiJsonDeserializer;
    private final LoanRepository loanRepository;
    private final LoanAccountDomainService loanAccountDomainService;
    private final NoteRepository noteRepository;
    private final LoanTransactionRepository loanTransactionRepository;
    private final LoanAssembler loanAssembler;
    private final ChargeRepositoryWrapper chargeRepository;
    private final LoanChargeRepository loanChargeRepository;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final CalendarInstanceRepository calendarInstanceRepository;
    private final PaymentDetailWritePlatformService paymentDetailWritePlatformService;
    private final HolidayRepositoryWrapper holidayRepository;
    private final ConfigurationDomainService configurationDomainService;
    private final WorkingDaysRepositoryWrapper workingDaysRepository;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final AccountTransfersWritePlatformService accountTransfersWritePlatformService;
    private final AccountTransfersReadPlatformService accountTransfersReadPlatformService;
    private final AccountAssociationsReadPlatformService accountAssociationsReadPlatformService;
    private final LoanChargeReadPlatformService loanChargeReadPlatformService;
    private final LoanReadPlatformService loanReadPlatformService;
    private final FromJsonHelper fromApiJsonHelper;
    private final CalendarRepository calendarRepository;
    private final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository;
    private final LoanApplicationCommandFromApiJsonHelper loanApplicationCommandFromApiJsonHelper;
    private final AccountAssociationsRepository accountAssociationRepository;
    private final AccountTransferDetailRepository accountTransferDetailRepository;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final GuarantorDomainService guarantorDomainService;
    private final LoanUtilService loanUtilService;
    private final LoanSummaryWrapper loanSummaryWrapper;
    private final LoanRepaymentScheduleTransactionProcessorFactory transactionProcessingStrategy;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final StandingInstructionWritePlatformService standingInstructionWritePlatformService;
    private final LoanProductBusinessRuleValidator loanProductBusinessRuleValidator;
    private final WorkingDayExemptionsReadPlatformService workingDayExcumptionsReadPlatformService;
    private final GroupLoanIndividualMonitoringRepository glimRepository;
    private final GroupLoanIndividualMonitoringAssembler glimAssembler;
    private final LoanGlimRepaymentScheduleInstallmentRepository loanGlimRepaymentScheduleInstallmentRepository;
    private final GroupLoanIndividualMonitoringTransactionAssembler glimTransactionAssembler;
    private final LoanScheduleValidator loanScheduleValidator;
    private final FineractEntityAccessUtil fineractEntityAccessUtil;
    private final LoanRescheduleRequestReadPlatformService loanRescheduleRequestReadPlatformService;
    private final GroupingTypesWritePlatformService groupingTypesWritePlatformService;
    private final LoanCalculationReadService loanCalculationReadService;
    private final BulkLoansReadPlatformService bulkLoansReadPlatformService;
    private final JdbcTemplate jdbcTemplate;
    private final LoanOfficerAssignmentHistoryWriteService loanOfficerAssignmentHistoryWriteService;
    private final ClientWritePlatformService clientWritePlatformService;
    private final CenterReadPlatformService centerReadPlatformService;
    private final LoanOverdueChargeService loanOverdueChargeService;

    @Autowired
    public LoanWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final LoanEventApiJsonValidator loanEventApiJsonValidator,
            final LoanUpdateCommandFromApiJsonDeserializer loanUpdateCommandFromApiJsonDeserializer, final LoanAssembler loanAssembler,
            final LoanRepository loanRepository, final LoanAccountDomainService loanAccountDomainService,
            final LoanTransactionRepository loanTransactionRepository, final NoteRepository noteRepository,
            final ChargeRepositoryWrapper chargeRepository, final LoanChargeRepository loanChargeRepository,
            final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository,
            final JournalEntryWritePlatformService journalEntryWritePlatformService,
            final CalendarInstanceRepository calendarInstanceRepository,
            final PaymentDetailWritePlatformService paymentDetailWritePlatformService, final HolidayRepositoryWrapper holidayRepository,
            final ConfigurationDomainService configurationDomainService, final WorkingDaysRepositoryWrapper workingDaysRepository,
            final LoanProductReadPlatformService loanProductReadPlatformService,
            final AccountTransfersWritePlatformService accountTransfersWritePlatformService,
            final AccountTransfersReadPlatformService accountTransfersReadPlatformService,
            final AccountAssociationsReadPlatformService accountAssociationsReadPlatformService,
            final LoanChargeReadPlatformService loanChargeReadPlatformService, final LoanReadPlatformService loanReadPlatformService,
            final FromJsonHelper fromApiJsonHelper, final CalendarRepository calendarRepository,
            final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository,
            final LoanApplicationCommandFromApiJsonHelper loanApplicationCommandFromApiJsonHelper,
            final AccountAssociationsRepository accountAssociationRepository,
            final AccountTransferDetailRepository accountTransferDetailRepository,
            final BusinessEventNotifierService businessEventNotifierService, final GuarantorDomainService guarantorDomainService,
            final LoanUtilService loanUtilService, final LoanSummaryWrapper loanSummaryWrapper,
            final LoanRepaymentScheduleTransactionProcessorFactory transactionProcessingStrategy,
            final CodeValueRepositoryWrapper codeValueRepository,
            final StandingInstructionWritePlatformService standingInstructionWritePlatformService,
            final LoanProductBusinessRuleValidator loanProductBusinessRuleValidator,
            final WorkingDayExemptionsReadPlatformService workingDayExcumptionsReadPlatformService,
            final GroupLoanIndividualMonitoringRepository glimRepository, final GroupLoanIndividualMonitoringAssembler glimAssembler,
            final LoanGlimRepaymentScheduleInstallmentRepository loanGlimRepaymentScheduleInstallmentRepository,
            final GroupLoanIndividualMonitoringTransactionAssembler glimTransactionAssembler,
            final LoanScheduleValidator loanScheduleValidator, final FineractEntityAccessUtil fineractEntityAccessUtil,
            final LoanRescheduleRequestReadPlatformService loanRescheduleRequestReadPlatformService,
            final GroupingTypesWritePlatformService groupingTypesWritePlatformService,
            final BulkLoansReadPlatformService bulkLoansReadPlatformService, final RoutingDataSource dataSource,
            final LoanOfficerAssignmentHistoryWriteService loanOfficerAssignmentHistoryWriteService,
            final ClientWritePlatformService clientWritePlatformService, final CenterReadPlatformService centerReadPlatformService,
            final LoanOverdueChargeService loanOverdueChargeService, final LoanCalculationReadService loanCalculationReadService) {

        this.context = context;
        this.loanEventApiJsonValidator = loanEventApiJsonValidator;
        this.loanAssembler = loanAssembler;
        this.loanRepository = loanRepository;
        this.loanAccountDomainService = loanAccountDomainService;
        this.loanTransactionRepository = loanTransactionRepository;
        this.noteRepository = noteRepository;
        this.chargeRepository = chargeRepository;
        this.loanChargeRepository = loanChargeRepository;
        this.applicationCurrencyRepository = applicationCurrencyRepository;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
        this.loanUpdateCommandFromApiJsonDeserializer = loanUpdateCommandFromApiJsonDeserializer;
        this.calendarInstanceRepository = calendarInstanceRepository;
        this.paymentDetailWritePlatformService = paymentDetailWritePlatformService;
        this.holidayRepository = holidayRepository;
        this.configurationDomainService = configurationDomainService;
        this.workingDaysRepository = workingDaysRepository;
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.accountTransfersWritePlatformService = accountTransfersWritePlatformService;
        this.accountTransfersReadPlatformService = accountTransfersReadPlatformService;
        this.accountAssociationsReadPlatformService = accountAssociationsReadPlatformService;
        this.loanChargeReadPlatformService = loanChargeReadPlatformService;
        this.loanReadPlatformService = loanReadPlatformService;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.calendarRepository = calendarRepository;
        this.repaymentScheduleInstallmentRepository = repaymentScheduleInstallmentRepository;
        this.loanApplicationCommandFromApiJsonHelper = loanApplicationCommandFromApiJsonHelper;
        this.accountAssociationRepository = accountAssociationRepository;
        this.accountTransferDetailRepository = accountTransferDetailRepository;
        this.businessEventNotifierService = businessEventNotifierService;
        this.guarantorDomainService = guarantorDomainService;
        this.loanUtilService = loanUtilService;
        this.loanSummaryWrapper = loanSummaryWrapper;
        this.transactionProcessingStrategy = transactionProcessingStrategy;
        this.codeValueRepository = codeValueRepository;
        this.standingInstructionWritePlatformService = standingInstructionWritePlatformService;
        this.loanProductBusinessRuleValidator = loanProductBusinessRuleValidator;
        this.workingDayExcumptionsReadPlatformService = workingDayExcumptionsReadPlatformService;
        this.glimRepository = glimRepository;
        this.glimAssembler = glimAssembler;
        this.loanGlimRepaymentScheduleInstallmentRepository = loanGlimRepaymentScheduleInstallmentRepository;
        this.glimTransactionAssembler = glimTransactionAssembler;
        this.loanScheduleValidator = loanScheduleValidator;
        this.fineractEntityAccessUtil = fineractEntityAccessUtil;
        this.loanRescheduleRequestReadPlatformService = loanRescheduleRequestReadPlatformService;
        this.groupingTypesWritePlatformService = groupingTypesWritePlatformService;
        this.bulkLoansReadPlatformService = bulkLoansReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.loanOfficerAssignmentHistoryWriteService = loanOfficerAssignmentHistoryWriteService;
        this.clientWritePlatformService = clientWritePlatformService;
        this.centerReadPlatformService = centerReadPlatformService;
        this.loanOverdueChargeService = loanOverdueChargeService;
        this.loanCalculationReadService = loanCalculationReadService;
    }

    private LoanLifecycleStateMachine defaultLoanLifecycleStateMachine() {
        final List<LoanStatus> allowedLoanStatuses = Arrays.asList(LoanStatus.values());
        return new DefaultLoanLifecycleStateMachine(allowedLoanStatuses);
    }

    @Transactional
    @Override
    public CommandProcessingResult disburseLoan(final Long loanId, final JsonCommand command, final Boolean isAccountTransfer) {

        final AppUser currentUser = getAppUserIfPresent();

        this.loanEventApiJsonValidator.validateDisbursement(loanId, command.json(), isAccountTransfer);

        final Loan loan = this.loanAssembler.assembleFromWithInitializeLazy(loanId);
        final LocalDate actualDisbursementDate = command.localDateValueOfParameterNamed("actualDisbursementDate");

        final LocalDate repaymentsStartingFromDate = command
                .localDateValueOfParameterNamed(LoanApiConstants.repaymentsStartingFromDateParameterName);
        if (repaymentsStartingFromDate != null) {
            loan.setExpectedFirstRepaymentOnDate(repaymentsStartingFromDate.toDate());
        }
        // validate ActualDisbursement Date Against Expected Disbursement Date
        final LoanProduct loanProduct = loan.loanProduct();
        if (loanProduct.isSyncExpectedWithDisbursementDate()) {
            syncExpectedDateWithActualDisbursementDate(loan, actualDisbursementDate);
        }

        checkClientOrGroupActive(loan);

        final Boolean isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled = this.configurationDomainService
                .isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled();
        final Date rescheduledRepaymentDate = command.DateValueOfParameterNamed("adjustRepaymentDate");

        // check for product mix validations
        checkForProductMixRestrictions(loan);

        // validate for glim application
        if (command.hasParameter(LoanApiConstants.clientMembersParamName)) {
            final List<GroupLoanIndividualMonitoring> glimList = this.glimRepository.findByLoanId(loanId);
            final boolean validateForApprovalLimits = false;
            GroupLoanIndividualMonitoringDataValidator.validateForGroupLoanIndividualMonitoringTransaction(command,
                    LoanApiConstants.principalDisbursedParameterName, currentUser, loan.getCurrencyCode(), validateForApprovalLimits,
                    glimList);
        }

        LocalDate recalculateFrom = null;
        if (loan.isOpen() && loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            recalculateFrom = actualDisbursementDate;
            loan.setConsiderFutureAccrualsBefore(recalculateFrom);
        }
        final boolean considerFutureDisbursmentsInSchedule = loan.loanProduct().considerFutureDisbursementsInSchedule();
        final boolean considerAllDisbursmentsInSchedule = loan.loanProduct().considerAllDisbursementsInSchedule();

        final Map<BUSINESS_ENTITY, Object> entityMap = constructEntityMap(loan, command);
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_DISBURSAL, entityMap);

        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();

        final Map<String, Object> changes = new LinkedHashMap<>();

        final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);

        final Boolean isPaymnetypeApplicableforDisbursementCharge = this.configurationDomainService
                .isPaymnetypeApplicableforDisbursementCharge();

        updateLoanCounters(loan, actualDisbursementDate);
        final Money amountBeforeAdjust = loan.getPrincpal();
        loan.validateAccountStatus(LoanEvent.LOAN_DISBURSED);
        final boolean canDisburse = loan.canDisburse(actualDisbursementDate);
        ChangedTransactionDetail changedTransactionDetail = null;
        LoanTransaction disbursementTransaction = null;
        if (canDisburse) {
            final Money disburseAmount = loan.adjustDisburseAmount(command, actualDisbursementDate);
            final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom,
                    considerFutureDisbursmentsInSchedule, considerAllDisbursmentsInSchedule);
            scheduleGeneratorDTO.setBusinessEvent(BUSINESS_EVENTS.LOAN_DISBURSAL);
            Money amountToDisburse = disburseAmount.copy();
            final LocalDate nextPossibleRepaymentDate = validateAndFetchNextRepaymentDate(loan, actualDisbursementDate, loanProduct,
                    isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled, rescheduledRepaymentDate, scheduleGeneratorDTO);
            final boolean recalculateSchedule = amountBeforeAdjust.isNotEqualTo(loan.getPrincpal());
            final String txnExternalId = command.stringValueOfParameterNamedAllowingNull("externalId");

            if (loan.isApproved() && loan.isTopup() && loan.getClientId() != null) {
                final Long loanIdToClose = loan.getTopupLoanDetails().getLoanIdToClose();
                final Loan loanToClose = this.loanRepository.findNonClosedLoanThatBelongsToClient(loanIdToClose, loan.getClientId());
                if (loanToClose == null) { throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.to.be.closed.with.topup.is.not.active", "Loan to be closed with this topup is not active."); }
                final LocalDate lastUserTransactionOnLoanToClose = loanToClose.getLastUserTransactionDate();
                if (loan.getDisbursementDate()
                        .isBefore(lastUserTransactionOnLoanToClose)) { throw new GeneralPlatformDomainRuleException(
                                "error.msg.loan.disbursal.date.should.be.after.last.transaction.date.of.loan.to.be.closed",
                                "Disbursal date of this loan application " + loan.getDisbursementDate()
                                        + " should be after last transaction date of loan to be closed "
                                        + lastUserTransactionOnLoanToClose); }
                final boolean calcualteInterestTillDate = true;
                final BigDecimal loanOutstanding = this.loanCalculationReadService
                        .retrieveLoanPrePaymentTemplate(actualDisbursementDate, calcualteInterestTillDate, loanToClose).getAmount();
                final BigDecimal firstDisbursalAmount = loan.getFirstDisbursalAmount();
                if (loanOutstanding.compareTo(firstDisbursalAmount) > 0) { throw new GeneralPlatformDomainRuleException(
                        "error.msg.loan.amount.less.than.outstanding.of.loan.to.be.closed",
                        "Topup loan amount should be greater than outstanding amount of loan to be closed."); }

                amountToDisburse = disburseAmount.minus(loanOutstanding);

                disburseLoanToLoan(loan, loanToClose, command, loanOutstanding);
            }
            disbursementTransaction = LoanTransaction.disbursement(loan.getOffice(), amountToDisburse, paymentDetail,
                    actualDisbursementDate, txnExternalId);
            if (isAccountTransfer) {
                disburseLoanToSavings(loan, command, amountToDisburse, paymentDetail);
                existingTransactionIds.addAll(loan.findExistingTransactionIds());
                existingReversedTransactionIds.addAll(loan.findExistingReversedTransactionIds());
            } else {
                existingTransactionIds.addAll(loan.findExistingTransactionIds());
                existingReversedTransactionIds.addAll(loan.findExistingReversedTransactionIds());
                disbursementTransaction.updateLoan(loan);
                if (!loan.status().isActive() && loan.loanProduct().getPercentageOfDisbursementToBeTransferred() != null) {
                    disbursePartialAmountToSavings(command, loan, existingTransactionIds, existingReversedTransactionIds, paymentDetail,
                            disbursementTransaction, disburseAmount);
                }
                loan.getLoanTransactions().add(disbursementTransaction);
            }

            // update disbursed amount for each client in glim
            updateGlimMembers(command, loan, changes);

            regenerateScheduleOnDisbursement(command, loan, recalculateSchedule, scheduleGeneratorDTO, nextPossibleRepaymentDate,
                    rescheduledRepaymentDate);
            if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
                this.loanAccountDomainService.createAndSaveLoanScheduleArchive(loan, scheduleGeneratorDTO);
            }
            if (isPaymnetypeApplicableforDisbursementCharge) {
                changedTransactionDetail = loan.disburse(currentUser, command, changes, scheduleGeneratorDTO, paymentDetail);
            } else {
                changedTransactionDetail = loan.disburse(currentUser, command, changes, scheduleGeneratorDTO, null);
            }
        }
        Map<BUSINESS_ENTITY, Object> notifyParamMap = new HashMap<>();
        if (!changes.isEmpty()) {
            if (changedTransactionDetail != null) {
                for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                    this.loanTransactionRepository.save(mapEntry.getValue());
                    this.accountTransfersWritePlatformService.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
                }
            }

            saveAndFlushLoanWithDataIntegrityViolationChecks(loan);

            final String noteText = command.stringValueOfParameterNamed("note");
            if (StringUtils.isNotBlank(noteText)) {
                final Note note = Note.loanNote(loan, noteText);
                this.noteRepository.save(note);
            }

            if (changedTransactionDetail != null) {
                for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                    this.loanTransactionRepository.save(mapEntry.getValue());
                    this.accountTransfersWritePlatformService.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
                }
                notifyParamMap = constructEntityMap(BUSINESS_ENTITY.CHANGED_TRANSACTION_DETAIL, changedTransactionDetail);
            }

            // auto create standing instruction
            createStandingInstruction(loan);

            postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);

        }

        final Set<LoanCharge> loanCharges = loan.charges();
        final Map<Long, BigDecimal> disBuLoanCharges = new HashMap<>();
        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isDueAtDisbursement() && loanCharge.getChargePaymentMode().isPaymentModeAccountTransfer()
                    && loanCharge.isChargePending()) {
                disBuLoanCharges.put(loanCharge.getId(), loanCharge.amountOutstanding());
            }
        }

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
        for (final Map.Entry<Long, BigDecimal> entrySet : disBuLoanCharges.entrySet()) {
            final PortfolioAccountData savingAccountData = this.accountAssociationsReadPlatformService.retriveLoanLinkedAssociation(loanId);
            final SavingsAccount fromSavingsAccount = null;
            final boolean isRegularTransaction = true;
            final boolean isExceptionForBalanceCheck = false;
            final AccountTransferDTO accountTransferDTO = new AccountTransferDTO(actualDisbursementDate, entrySet.getValue(),
                    PortfolioAccountType.SAVINGS, PortfolioAccountType.LOAN, savingAccountData.accountId(), loanId, "Loan Charge Payment",
                    locale, fmt, null, null, LoanTransactionType.REPAYMENT_AT_DISBURSEMENT.getValue(), entrySet.getKey(), null,
                    AccountTransferType.CHARGE_PAYMENT.getValue(), null, null, null, null, null, fromSavingsAccount, isRegularTransaction,
                    isExceptionForBalanceCheck);
            this.accountTransfersWritePlatformService.transferFunds(accountTransferDTO);
        }

        updateRecurringCalendarDatesForInterestRecalculation(loan);
        this.loanAccountDomainService.recalculateAccruals(loan);
        final Map<BUSINESS_ENTITY, Object> map = constructEntityMap(BUSINESS_ENTITY.LOAN, loan);
        if (disbursementTransaction != null) {
            map.put(BUSINESS_ENTITY.LOAN_TRANSACTION, disbursementTransaction);
        }
        map.putAll(notifyParamMap);

        this.loanUtilService.validateOfficetoProductAccess(loan);

        this.loanUtilService.validateOfficetoChargeAccess(loan);

        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_DISBURSAL, map);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loan.getId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .with(changes) //
                .build();
    }

    private void updateGlimMembers(final JsonCommand command, final Loan loan, final Map<String, Object> changes) {
        if (loan.isGLIMLoan()) {
            List<GroupLoanIndividualMonitoring> glimList = null;
            if (command.hasParameter(LoanApiConstants.clientMembersParamName)) {
                final Collection<GroupLoanIndividualMonitoringDataChanges> clientMembers = new ArrayList<>();
                glimList = this.glimAssembler.updateFromJson(command.parsedJson(), "disbursedAmount", loan,
                        loan.fetchNumberOfInstallmensAfterExceptions(), loan.getLoanProductRelatedDetail().getAnnualNominalInterestRate(),
                        clientMembers);
                changes.put("clientMembers", clientMembers);
            } else {
                glimList = this.glimRepository.findByLoanId(loan.getId());
                for (final GroupLoanIndividualMonitoring glim : glimList) {
                    glim.setDisbursedAmount(glim.getApprovedAmount());
                }
            }
            loan.updateGlim(glimList);
            loan.updateDefautGlimMembers(glimList);
            this.glimAssembler.adjustRoundOffValuesToApplicableCharges(loan.charges(), loan.fetchNumberOfInstallmensAfterExceptions(),
                    glimList);

        }
    }

    private void disbursePartialAmountToSavings(final JsonCommand command, final Loan loan, final List<Long> existingTransactionIds,
            final List<Long> existingReversedTransactionIds, final PaymentDetail paymentDetail,
            final LoanTransaction disbursementTransaction, final Money disburseAmount) {
        final BigDecimal transferPercentage = loan.loanProduct().getPercentageOfDisbursementToBeTransferred();
        final BigDecimal divisor = BigDecimal.valueOf(Double.valueOf("100.0"));
        final RoundingMode roundingMode = MoneyHelper.getRoundingMode();
        final MathContext mc = new MathContext(8, roundingMode);
        final BigDecimal totalAmountForTransfer = disburseAmount.getAmount().multiply(transferPercentage).divide(divisor, mc);
        disbursementTransaction.setAmount(disburseAmount.minus(totalAmountForTransfer).getAmount());
        disburseLoanToSavings(loan, command, Money.of(loan.getCurrency(), totalAmountForTransfer), paymentDetail);
        existingTransactionIds.addAll(loan.findExistingTransactionIds());
        existingReversedTransactionIds.addAll(loan.findExistingReversedTransactionIds());
    }

    private LocalDate validateAndFetchNextRepaymentDate(final Loan loan, final LocalDate actualDisbursementDate,
            final LoanProduct loanProduct, final Boolean isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled,
            final Date rescheduledRepaymentDate, final ScheduleGeneratorDTO scheduleGeneratorDTO) {
        LocalDate nextPossibleRepaymentDate = null;
        final boolean isActiveLoan = loan.isOpen();
        if (isActiveLoan) {
            nextPossibleRepaymentDate = loan.getNextPossibleRepaymentDateForRescheduling(
                    isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled, actualDisbursementDate);
        } else {
            nextPossibleRepaymentDate = scheduleGeneratorDTO.getCalculatedRepaymentsStartingFromDate();
        }
        if (!isActiveLoan || loanProduct.isMinDurationApplicableForAllDisbursements()) {
            LocalDate expectedFirstRepaymentDate = null;
            if (rescheduledRepaymentDate != null) {
                expectedFirstRepaymentDate = new LocalDate(rescheduledRepaymentDate);
            } else {
                expectedFirstRepaymentDate = nextPossibleRepaymentDate;
            }
            final Integer minimumDaysBetweenDisbursalAndFirstRepayment = this.loanUtilService
                    .calculateMinimumDaysBetweenDisbursalAndFirstRepayment(actualDisbursementDate, loanProduct,
                            loan.getLoanRepaymentScheduleDetail().getRepaymentPeriodFrequencyType(),
                            loan.getLoanProductRelatedDetail().getRepayEvery());

            this.loanScheduleValidator.validateRepaymentAndDisbursementDateWithMeetingDateAndMinimumDaysBetweenDisbursalAndFirstRepayment(
                    expectedFirstRepaymentDate, actualDisbursementDate, scheduleGeneratorDTO.getCalendar(),
                    minimumDaysBetweenDisbursalAndFirstRepayment, AccountType.fromInt(loan.getLoanType()),
                    loan.isSyncDisbursementWithMeeting());
        }
        return nextPossibleRepaymentDate;
    }

    /**
     * create standing instruction for disbursed loan
     *
     * @param loan
     *            the disbursed loan
     * @return void
     **/
    private void createStandingInstruction(final Loan loan) {

        if (loan.shouldCreateStandingInstructionAtDisbursement()) {
            final AccountAssociations accountAssociations = this.accountAssociationRepository.findByLoanIdAndType(loan.getId(),
                    AccountAssociationType.LINKED_ACCOUNT_ASSOCIATION.getValue());

            if (accountAssociations != null) {

                final SavingsAccount linkedSavingsAccount = accountAssociations.linkedSavingsAccount();

                // name is auto-generated
                final String name = "To loan " + loan.getAccountNumber() + " from savings " + linkedSavingsAccount.getAccountNumber();
                final Office fromOffice = loan.getOffice();
                final Client fromClient = loan.getClient();
                final Office toOffice = loan.getOffice();
                final Client toClient = loan.getClient();
                final Integer priority = StandingInstructionPriority.MEDIUM.getValue();
                final Integer transferType = AccountTransferType.LOAN_REPAYMENT.getValue();
                final Integer instructionType = StandingInstructionType.DUES.getValue();
                final Integer status = StandingInstructionStatus.ACTIVE.getValue();
                final Integer recurrenceType = AccountTransferRecurrenceType.AS_PER_DUES.getValue();
                final LocalDate validFrom = DateUtils.getLocalDateOfTenant();

                final AccountTransferDetails accountTransferDetails = AccountTransferDetails.savingsToLoanTransfer(fromOffice, fromClient,
                        linkedSavingsAccount, toOffice, toClient, loan, transferType);

                final AccountTransferStandingInstruction accountTransferStandingInstruction = AccountTransferStandingInstruction.create(
                        accountTransferDetails, name, priority, instructionType, status, null, validFrom, null, recurrenceType, null, null,
                        null);
                accountTransferDetails.updateAccountTransferStandingInstruction(accountTransferStandingInstruction);

                this.accountTransferDetailRepository.save(accountTransferDetails);
            }
        }
    }

    private void updateRecurringCalendarDatesForInterestRecalculation(final Loan loan) {

        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()
                && loan.loanInterestRecalculationDetails().getRestFrequencyType().isSameAsRepayment()) {
            final CalendarInstance calendarInstanceForInterestRecalculation = this.calendarInstanceRepository
                    .findByEntityIdAndEntityTypeIdAndCalendarTypeId(loan.loanInterestRecalculationDetailId(),
                            CalendarEntityType.LOAN_RECALCULATION_REST_DETAIL.getValue(), CalendarType.COLLECTION.getValue());

            final Calendar calendarForInterestRecalculation = calendarInstanceForInterestRecalculation.getCalendar();
            calendarForInterestRecalculation.updateStartAndEndDate(loan.getDisbursementDate(), loan.getMaturityDate());
            this.calendarRepository.save(calendarForInterestRecalculation);
        }

    }

    private void saveAndFlushLoanWithDataIntegrityViolationChecks(final Loan loan) {
        try {
            final List<LoanRepaymentScheduleInstallment> installments = loan.fetchRepaymentScheduleInstallments();
            for (final LoanRepaymentScheduleInstallment installment : installments) {
                if (installment.getId() == null) {
                    this.repaymentScheduleInstallmentRepository.save(installment);
                }
            }
            this.loanRepository.saveAndFlush(loan);
        } catch (final DataIntegrityViolationException e) {
            final Throwable realCause = e.getCause();
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");
            if (realCause.getMessage().toLowerCase().contains("external_id_unique")) {
                baseDataValidator.reset().parameter("externalId").failWithCode("value.must.be.unique");
            }
            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                    "Validation errors exist.", dataValidationErrors); }
        }
    }

    private void saveLoanWithDataIntegrityViolationChecks(final Loan loan) {
        try {
            final List<LoanRepaymentScheduleInstallment> installments = loan.fetchRepaymentScheduleInstallments();
            for (final LoanRepaymentScheduleInstallment installment : installments) {
                if (installment.getId() == null) {
                    this.repaymentScheduleInstallmentRepository.save(installment);
                }
            }
            this.loanRepository.save(loan);
        } catch (final DataIntegrityViolationException e) {
            final Throwable realCause = e.getCause();
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.transaction");
            if (realCause.getMessage().toLowerCase().contains("external_id_unique")) {
                baseDataValidator.reset().parameter("externalId").failWithCode("value.must.be.unique");
            }
            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                    "Validation errors exist.", dataValidationErrors); }
        }
    }

    /****
     * TODO Vishwas: Pair with Ashok and re-factor collection sheet code-base
     *
     * May of the changes made to disburseLoan aren't being made here, should
     * refactor to reuse disburseLoan ASAP
     *****/
    @Transactional
    @Override
    public Map<String, Object> bulkLoanDisbursal(final JsonCommand command, final CollectionSheetBulkDisbursalCommand bulkDisbursalCommand,
            final Boolean isAccountTransfer, final List<CollectionSheetTransactionDetails> collectionSheetTransactionDetailsList) {
        final AppUser currentUser = getAppUserIfPresent();

        final SingleDisbursalCommand[] disbursalCommand = bulkDisbursalCommand.getDisburseTransactions();
        final Map<String, Object> changes = new LinkedHashMap<>();
        if (disbursalCommand == null) { return changes; }

        final Date rescheduledRepaymentDate = null;
        final Boolean isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled = this.configurationDomainService
                .isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled();

        for (int i = 0; i < disbursalCommand.length; i++) {
            final SingleDisbursalCommand singleLoanDisbursalCommand = disbursalCommand[i];

            final Loan loan = this.loanAssembler.assembleFromWithInitializeLazy(singleLoanDisbursalCommand.getLoanId());
            final LocalDate actualDisbursementDate = command.localDateValueOfParameterNamed("actualDisbursementDate");

            // validate ActualDisbursement Date Against Expected Disbursement
            // Date
            final LoanProduct loanProduct = loan.loanProduct();
            if (loanProduct.isSyncExpectedWithDisbursementDate()) {
                syncExpectedDateWithActualDisbursementDate(loan, actualDisbursementDate);
            }

            checkClientOrGroupActive(loan);
            this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_DISBURSAL,
                    constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

            final List<Long> existingTransactionIds = new ArrayList<>();
            final List<Long> existingReversedTransactionIds = new ArrayList<>();

            final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);

            // Bulk disbursement should happen on meeting date (mostly from
            // collection sheet).
            // FIXME: AA - this should be first meeting date based on
            // disbursement date and next available meeting dates
            // assuming repayment schedule won't regenerate because expected
            // disbursement and actual disbursement happens on same date
            loan.validateAccountStatus(LoanEvent.LOAN_DISBURSED);
            updateLoanCounters(loan, actualDisbursementDate);
            final boolean canDisburse = loan.canDisburse(actualDisbursementDate);
            ChangedTransactionDetail changedTransactionDetail = null;
            LoanTransaction disbursementTransaction = null;
            if (canDisburse) {
                final Money amountBeforeAdjust = loan.getPrincpal();
                final Money disburseAmount = loan.adjustDisburseAmount(command, actualDisbursementDate);
                final boolean recalculateSchedule = amountBeforeAdjust.isNotEqualTo(loan.getPrincpal());
                final String txnExternalId = command.stringValueOfParameterNamedAllowingNull("externalId");
                disbursementTransaction = LoanTransaction.disbursement(loan.getOffice(), disburseAmount, paymentDetail,
                        actualDisbursementDate, txnExternalId);
                if (isAccountTransfer) {
                    disburseLoanToSavings(loan, command, disburseAmount, paymentDetail);
                    existingTransactionIds.addAll(loan.findExistingTransactionIds());
                    existingReversedTransactionIds.addAll(loan.findExistingReversedTransactionIds());
                } else {
                    existingTransactionIds.addAll(loan.findExistingTransactionIds());
                    existingReversedTransactionIds.addAll(loan.findExistingReversedTransactionIds());
                    disbursementTransaction.updateLoan(loan);
                    if (!loan.status().isActive() && loan.loanProduct().getPercentageOfDisbursementToBeTransferred() != null) {
                        disbursePartialAmountToSavings(command, loan, existingTransactionIds, existingReversedTransactionIds, paymentDetail,
                                disbursementTransaction, disburseAmount);
                    }
                    loan.getLoanTransactions().add(disbursementTransaction);
                }
                final LocalDate recalculateFrom = null;
                final boolean considerFutureDisbursmentsInSchedule = loan.loanProduct().considerFutureDisbursementsInSchedule();
                final boolean considerAllDisbursmentsInSchedule = loan.loanProduct().considerAllDisbursementsInSchedule();

                final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom,
                        considerFutureDisbursmentsInSchedule, considerAllDisbursmentsInSchedule);
                scheduleGeneratorDTO.setBusinessEvent(BUSINESS_EVENTS.LOAN_DISBURSAL);
                final LocalDate nextPossibleRepaymentDate = validateAndFetchNextRepaymentDate(loan, actualDisbursementDate, loanProduct,
                        isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled, rescheduledRepaymentDate, scheduleGeneratorDTO);
                regenerateScheduleOnDisbursement(command, loan, recalculateSchedule, scheduleGeneratorDTO, nextPossibleRepaymentDate,
                        rescheduledRepaymentDate);
                if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
                    this.loanAccountDomainService.createAndSaveLoanScheduleArchive(loan, scheduleGeneratorDTO);
                }
                if (this.configurationDomainService.isPaymnetypeApplicableforDisbursementCharge()) {
                    changedTransactionDetail = loan.disburse(currentUser, command, changes, scheduleGeneratorDTO, paymentDetail);
                } else {
                    changedTransactionDetail = loan.disburse(currentUser, command, changes, scheduleGeneratorDTO, null);
                }
                final boolean transactionStatus = true;
                final String errorMessage = null;
                final CollectionSheetTransactionDetails collectionSheetTransactionDetails = CollectionSheetTransactionDetails
                        .formCollectionSheetTransactionDetails(loan.getId(), disbursementTransaction.getId(), transactionStatus,
                                errorMessage, EntityType.LOAN.getValue());
                collectionSheetTransactionDetailsList.add(collectionSheetTransactionDetails);
            }
            if (!changes.isEmpty()) {
                if (changedTransactionDetail != null) {
                    for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings()
                            .entrySet()) {
                        this.loanTransactionRepository.save(mapEntry.getValue());
                        this.accountTransfersWritePlatformService.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
                    }
                }

                saveAndFlushLoanWithDataIntegrityViolationChecks(loan);

                final String noteText = command.stringValueOfParameterNamed("note");
                if (StringUtils.isNotBlank(noteText)) {
                    final Note note = Note.loanNote(loan, noteText);
                    this.noteRepository.save(note);
                }
                if (changedTransactionDetail != null) {
                    for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings()
                            .entrySet()) {
                        this.loanTransactionRepository.save(mapEntry.getValue());
                        this.accountTransfersWritePlatformService.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
                    }
                    final Map<BUSINESS_ENTITY, Object> changedTransactionEntityMap = constructEntityMap(
                            BUSINESS_ENTITY.CHANGED_TRANSACTION_DETAIL, changedTransactionDetail);
                    this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_DISBURSAL,
                            changedTransactionEntityMap);
                }
                postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);
            }
            final Set<LoanCharge> loanCharges = loan.charges();
            final Map<Long, BigDecimal> disBuLoanCharges = new HashMap<>();
            for (final LoanCharge loanCharge : loanCharges) {
                if (loanCharge.isDueAtDisbursement() && loanCharge.getChargePaymentMode().isPaymentModeAccountTransfer()
                        && loanCharge.isChargePending()) {
                    disBuLoanCharges.put(loanCharge.getId(), loanCharge.amountOutstanding());
                }
            }
            final Locale locale = command.extractLocale();
            final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
            for (final Map.Entry<Long, BigDecimal> entrySet : disBuLoanCharges.entrySet()) {
                final PortfolioAccountData savingAccountData = this.accountAssociationsReadPlatformService
                        .retriveLoanLinkedAssociation(loan.getId());
                final SavingsAccount fromSavingsAccount = null;
                final boolean isRegularTransaction = true;
                final boolean isExceptionForBalanceCheck = false;
                final AccountTransferDTO accountTransferDTO = new AccountTransferDTO(actualDisbursementDate, entrySet.getValue(),
                        PortfolioAccountType.SAVINGS, PortfolioAccountType.LOAN, savingAccountData.accountId(), loan.getId(),
                        "Loan Charge Payment", locale, fmt, null, null, LoanTransactionType.REPAYMENT_AT_DISBURSEMENT.getValue(),
                        entrySet.getKey(), null, AccountTransferType.CHARGE_PAYMENT.getValue(), null, null, null, null, null,
                        fromSavingsAccount, isRegularTransaction, isExceptionForBalanceCheck);
                this.accountTransfersWritePlatformService.transferFunds(accountTransferDTO);
            }
            updateRecurringCalendarDatesForInterestRecalculation(loan);
            this.loanAccountDomainService.recalculateAccruals(loan);
            final Map<BUSINESS_ENTITY, Object> map = constructEntityMap(BUSINESS_ENTITY.LOAN, loan);
            if (disbursementTransaction != null) {
                map.put(BUSINESS_ENTITY.LOAN_TRANSACTION, disbursementTransaction);
            }
            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_DISBURSAL, map);
        }
        return changes;
    }

    @Transactional
    @Override
    public CommandProcessingResult undoLoanDisbursal(final Long loanId, final JsonCommand command) {

        final AppUser currentUser = getAppUserIfPresent();

        final Loan loan = this.loanAssembler.assembleFromWithInitializeLazy(loanId);
        checkClientOrGroupActive(loan);

        final Map<BUSINESS_ENTITY, Object> eventDetailMap = constructEntityMap(BUSINESS_ENTITY.LOAN, loan);
        eventDetailMap.put(BUSINESS_ENTITY.ENTITY_LOCK_STATUS, loan.isLocked());
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_UNDO_DISBURSAL, eventDetailMap);

        removeLoanCycle(loan);

        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();
        //
        final MonetaryCurrency currency = loan.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);

        final LocalDate recalculateFrom = null;
        final boolean considerFutureDisbursmentsInSchedule = true;
        final boolean considerAllDisbursmentsInSchedule = true;
        final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom,
                considerFutureDisbursmentsInSchedule, considerAllDisbursmentsInSchedule);
        final LocalDate startDate = this.loanUtilService.calculateRepaymentStartingFromDate(loan.getExpectedDisbursedOnLocalDate(), loan,
                scheduleGeneratorDTO.getCalendar(), scheduleGeneratorDTO.getCalendarHistoryDataWrapper());
        scheduleGeneratorDTO.setCalculatedRepaymentsStartingFromDate(startDate);
        if (loan.isGLIMLoan() || AccountType.fromInt(loan.getLoanType()).isGLIMAccount()) {
            updateGlimMemberOnUndoLoanDisbursal(loan);

        }
        final LocalDate actualDisbursementDate = loan.getActualDisbursementDate();
        final Map<String, Object> changes = loan.undoDisbursal(scheduleGeneratorDTO, existingTransactionIds, existingReversedTransactionIds,
                currentUser);

        if (!changes.isEmpty()) {
            saveAndFlushLoanWithDataIntegrityViolationChecks(loan);
            final Collection<Long> transactionsIdsWithTransfer = this.accountTransfersReadPlatformService
                    .retrivePortfolioTransactionIds(loanId, PortfolioAccountType.LOAN);
            if (!transactionsIdsWithTransfer.isEmpty()) {
                this.accountTransfersWritePlatformService.reverseAllTransactions(loanId, PortfolioAccountType.LOAN);
            }
            final Collection<Long> deletedIds = this.standingInstructionWritePlatformService.delete(loanId, PortfolioAccountType.LOAN);
            changes.put("deletedInstructions", deletedIds);
            String noteText = null;
            if (command.hasParameter("note")) {
                noteText = command.stringValueOfParameterNamed("note");
                if (StringUtils.isNotBlank(noteText)) {
                    final Note note = Note.loanNote(loan, noteText);
                    this.noteRepository.save(note);
                }
            }
            existingReversedTransactionIds.addAll(transactionsIdsWithTransfer);
            boolean isAccountTransfer = false;
            final Map<String, Object> accountingBridgeData = loan.deriveAccountingBridgeData(applicationCurrency.toData(),
                    existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
            this.journalEntryWritePlatformService.createJournalEntriesForLoan(accountingBridgeData);

            if (!transactionsIdsWithTransfer.isEmpty()) {
                isAccountTransfer = true;
                boolean isLoanToLoanTransfer = false;
                Long topupTransactionId = null;
                if (loan.isTopup()) {
                    for (final LoanTransaction transaction : loan.getLoanTransactions()) {
                        if (transaction.isDisbursementIncludeReversal() && transactionsIdsWithTransfer.contains(transaction.getId())
                                && transaction.getTransactionDate().isEqual(actualDisbursementDate)) {
                            topupTransactionId = transaction.getId();
                            isLoanToLoanTransfer = true;
                            transactionsIdsWithTransfer.remove(topupTransactionId);
                            break;
                        }
                    }
                }
                existingReversedTransactionIds.clear();
                existingReversedTransactionIds.addAll(loan.findExistingReversedTransactionIds());
                if (!transactionsIdsWithTransfer.isEmpty()) {
                    existingReversedTransactionIds.removeAll(transactionsIdsWithTransfer);
                    final Map<String, Object> accountingBridgeDataForTransfers = loan.deriveAccountingBridgeData(
                            applicationCurrency.toData(), existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
                    this.journalEntryWritePlatformService.createJournalEntriesForLoan(accountingBridgeDataForTransfers);
                }
                if (isLoanToLoanTransfer) {
                    existingReversedTransactionIds.addAll(transactionsIdsWithTransfer);
                    existingReversedTransactionIds.remove(topupTransactionId);
                    final Map<String, Object> accountingBridgeDataForTopup = loan.deriveAccountingBridgeData(applicationCurrency.toData(),
                            existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
                    accountingBridgeDataForTopup.put("isLoanToLoanTransfer", isLoanToLoanTransfer);
                    this.journalEntryWritePlatformService.createJournalEntriesForLoan(accountingBridgeDataForTopup);
                }

            }

            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_UNDO_DISBURSAL,
                    constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loan.getId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .with(changes) //
                .build();
    }

    /**
     * update glim member, charges and reayment schedules for glim
     */
    private void updateGlimMemberOnUndoLoanDisbursal(final Loan loan) {
        final List<GroupLoanIndividualMonitoring> defaultGlimMembers = this.glimRepository.findByLoanIdAndIsClientSelected(loan.getId(),
                true);
        loan.updateDefautGlimMembers(defaultGlimMembers);
        final List<GroupLoanIndividualMonitoring> glimList = this.glimRepository.findByLoanId(loan.getId());
        final List<Long> glimIds = new ArrayList<>();
        final List<GroupLoanIndividualMonitoring> approvedGlimMembers = new ArrayList<>();
        final HashMap<Long, BigDecimal> chargesMap = new HashMap<>();
        // reset glim property for undo loan disbursal
        for (final GroupLoanIndividualMonitoring glim : glimList) {
            final BigDecimal approvedAmount = glim.getApprovedAmount();
            if (MathUtility.isGreaterThanZero(approvedAmount)) {
                glimIds.add(glim.getId());
                approvedGlimMembers.add(glim);
                glim.setIsClientSelected(true);
                glim.undoGlimTransaction();
                glim.resetDerievedComponents();
                this.glimAssembler.recalculateTotalFeeCharges(loan, chargesMap, approvedAmount,
                        glim.getGroupLoanIndividualMonitoringCharges());
            }
        }
        // get glim repayment schedules
        final List<LoanGlimRepaymentScheduleInstallment> loanGlimRepaymentScheduleInstallments = this.loanGlimRepaymentScheduleInstallmentRepository
                .getLoanGlimRepaymentScheduleInstallmentByGlimIds(glimIds);
        // delete existing glim repayment schedule
        this.loanGlimRepaymentScheduleInstallmentRepository.deleteInBatch(loanGlimRepaymentScheduleInstallments);
        // update the loan charges
        this.glimAssembler.updateLoanChargesForGlim(loan, chargesMap);
        loan.updateGlim(approvedGlimMembers);
        this.glimAssembler.adjustRoundOffValuesToApplicableCharges(loan.charges(), loan.fetchNumberOfInstallmensAfterExceptions(),
                approvedGlimMembers);
    }

    @Transactional
    @Override
    public CommandProcessingResult makeLoanRepayment(final Long loanId, final JsonCommand command, final boolean isRecoveryRepayment) {

        this.loanEventApiJsonValidator.validateNewRepaymentTransaction(command.json());

        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");
        final String txnExternalId = command.stringValueOfParameterNamedAllowingNull("externalId");

        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put("transactionDate", command.stringValueOfParameterNamed("transactionDate"));
        changes.put("transactionAmount", command.stringValueOfParameterNamed("transactionAmount"));
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());
        changes.put("paymentTypeId", command.stringValueOfParameterNamed("paymentTypeId"));

        final String noteText = command.stringValueOfParameterNamed("note");
        if (StringUtils.isNotBlank(noteText)) {
            changes.put("note", noteText);
        }
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        if (isRecoveryRepayment && loan.isClosedWrittenOff() && transactionDate.isBefore(loan.getWrittenOffDate())) {
            final String defaultUserMessage = "The refund date`" + loan.getWrittenOffDate() + "`"
                    + "` cannot be before the writen off date";
            final String errorMsg = "error.msg.loan.recovery.failed";
            throw new InvalidRefundDateException(errorMsg, loan.getWrittenOffDate().toString(), defaultUserMessage);
        }
        final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);
        final Boolean isHolidayValidationDone = false;
        final HolidayDetailDTO holidayDetailDto = null;
        final boolean isAccountTransfer = false;
        final CommandProcessingResultBuilder commandProcessingResultBuilder = new CommandProcessingResultBuilder();

        final LoanTransaction loanTransaction = this.loanAccountDomainService.makeRepayment(loan, commandProcessingResultBuilder,
                transactionDate, transactionAmount, paymentDetail, noteText, txnExternalId, isRecoveryRepayment, isAccountTransfer,
                holidayDetailDto, isHolidayValidationDone);

        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_MAKE_REPAYMENT,
                constructEntityMap(BUSINESS_ENTITY.LOAN_TRANSACTION, loanTransaction));

        return commandProcessingResultBuilder.withCommandId(command.commandId()) //
                .withLoanId(loanId) //
                .with(changes) //
                .withTransactionId(loanTransaction.getId().toString()) //
                .build();
    }

    @Override
    public CommandProcessingResult makeLoanPreRepayment(final Long loanId, final JsonCommand command) {

        this.loanEventApiJsonValidator.validateNewRepaymentTransaction(command.json());

        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");
        final String txnExternalId = command.stringValueOfParameterNamedAllowingNull("externalId");

        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put("transactionDate", command.stringValueOfParameterNamed("transactionDate"));
        changes.put("transactionAmount", command.stringValueOfParameterNamed("transactionAmount"));
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());
        changes.put("paymentTypeId", command.stringValueOfParameterNamed("paymentTypeId"));

        final String noteText = command.stringValueOfParameterNamed("note");
        if (StringUtils.isNotBlank(noteText)) {
            changes.put("note", noteText);
        }
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);
        final Boolean isHolidayValidationDone = false;
        final HolidayDetailDTO holidayDetailDto = null;
        final boolean isAccountTransfer = false;
        final CommandProcessingResultBuilder commandProcessingResultBuilder = new CommandProcessingResultBuilder();
        final boolean isLoanToLoanTransfer = false;
        final boolean isPrepayment = true;
        final boolean isRecoveryRepayment = false;
        final LoanTransaction loanTransaction = this.loanAccountDomainService.makeRepayment(loan, commandProcessingResultBuilder,
                transactionDate, transactionAmount, paymentDetail, noteText, txnExternalId, isRecoveryRepayment, isAccountTransfer,
                holidayDetailDto, isHolidayValidationDone, isLoanToLoanTransfer, isPrepayment);

        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_MAKE_REPAYMENT,
                constructEntityMap(BUSINESS_ENTITY.LOAN_TRANSACTION, loanTransaction));

        return commandProcessingResultBuilder.withCommandId(command.commandId()) //
                .withLoanId(loanId) //
                .with(changes) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult refundOverPaidLoan(final Long loanId, final JsonCommand command) {
        this.loanEventApiJsonValidator.validateRefundTransaction(command.json());
        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put("transactionDate", command.stringValueOfParameterNamed("transactionDate"));
        changes.put("locale", command.locale());
        changes.put("paymentTypeId", command.stringValueOfParameterNamed("paymentTypeId"));
        final String noteText = command.stringValueOfParameterNamed("note");
        if (StringUtils.isNotBlank(noteText)) {
            changes.put("note", noteText);
        }
        final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);
        final boolean isAccountTransfer = false;
        final CommandProcessingResultBuilder commandProcessingResultBuilder = new CommandProcessingResultBuilder();
        final BigDecimal transactionAmount = null;
        this.loanAccountDomainService.makeRefund(loanId, commandProcessingResultBuilder, transactionDate, transactionAmount, paymentDetail,
                noteText, null, isAccountTransfer);
        return commandProcessingResultBuilder.withCommandId(command.commandId()) //
                .withLoanId(loanId) //
                .with(changes) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult addLoanSubsidy(final Long loanId, final JsonCommand command) {

        this.loanEventApiJsonValidator.validateNewAddSubsidyTransaction(command.json());

        final LocalDate transactionDate = command.localDateValueOfParameterNamed("subsidyReleaseDate");
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("subsidyAmountReleased");
        final String txnExternalId = command.stringValueOfParameterNamedAllowingNull("externalId");

        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put("subsidyReleaseDate", command.stringValueOfParameterNamed("subsidyReleaseDate"));
        changes.put("subsidyAmountReleased", command.stringValueOfParameterNamed("subsidyAmountReleased"));
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());
        changes.put("paymentTypeId", command.stringValueOfParameterNamed("paymentTypeId"));

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        if (!loan.isSubsidyApplicable()) { throw new SubsidyNotApplicableException(loan.getId()); }
        final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);
        final HolidayDetailDTO holidayDetailDto = null;
        final boolean isAccountTransfer = false;
        final CommandProcessingResultBuilder commandProcessingResultBuilder = new CommandProcessingResultBuilder();

        final Money subsidyAmount = loan.getTotalSubsidyAmount().plus(transactionAmount);
        if (Money.of(loan.getCurrency(), loan.getLoanSummary().getTotalPrincipalOutstanding()).isGreaterThanOrEqualTo(subsidyAmount)) {
            this.loanAccountDomainService.addOrRevokeLoanSubsidy(loan, commandProcessingResultBuilder, transactionDate,
                    Money.of(loan.getCurrency(), transactionAmount), paymentDetail, txnExternalId, isAccountTransfer, holidayDetailDto,
                    LoanTransactionType.ADD_SUBSIDY);
        } else {
            throw new SubsidyAmountExceedsPrincipalOutstandingException(loanId, loan.getCurrency().getCode(),
                    subsidyAmount.minus(loan.getLoanSummary().getTotalPrincipalOutstanding()).getAmount());
        }

        return commandProcessingResultBuilder.withCommandId(command.commandId()) //
                .withLoanId(loanId) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult revokeLoanSubsidy(final Long loanId, final JsonCommand command) {

        this.loanEventApiJsonValidator.validateNewRevokeSubsidyTransaction(command.json());

        final LocalDate transactionDate = command.localDateValueOfParameterNamed("subsidyRevokeDate");
        final String txnExternalId = command.stringValueOfParameterNamedAllowingNull("externalId");

        final Loan loan = this.loanAssembler.assembleFrom(loanId);

        if (!loan.isSubsidyApplicable()) { throw new SubsidyNotApplicableException(loan.getId()); }

        final PaymentDetail paymentDetail = null;
        final HolidayDetailDTO holidayDetailDto = null;
        final boolean isAccountTransfer = false;
        final CommandProcessingResultBuilder commandProcessingResultBuilder = new CommandProcessingResultBuilder();

        final Money subsidyAmount = loan.getTotalSubsidyAmount();
        if (subsidyAmount.isGreaterThanZero()) {
            this.loanAccountDomainService.addOrRevokeLoanSubsidy(loan, commandProcessingResultBuilder, transactionDate, subsidyAmount,
                    paymentDetail, txnExternalId, isAccountTransfer, holidayDetailDto, LoanTransactionType.REVOKE_SUBSIDY);
        } else {
            throw new SubsidyNotAppliedException(loanId);
        }

        return commandProcessingResultBuilder.withCommandId(command.commandId()) //
                .withLoanId(loanId) //
                .build();
    }

    @Transactional
    @Override
    public Map<String, Object> makeLoanBulkRepayment(final CollectionSheetBulkRepaymentCommand bulkRepaymentCommand,
            final List<CollectionSheetTransactionDetails> collectionSheetTransactionDetails) {

        final SingleRepaymentCommand[] repaymentCommand = bulkRepaymentCommand.getLoanTransactions();
        final Map<String, Object> changes = new LinkedHashMap<>();
        final boolean isRecoveryRepayment = false;

        if (repaymentCommand == null) { return changes; }
        final List<Long> transactionIds = new ArrayList<>();
        final boolean isAccountTransfer = false;
        HolidayDetailDTO holidayDetailDTO = null;
        Boolean isHolidayValidationDone = false;
        final boolean allowTransactionsOnHoliday = this.configurationDomainService.allowTransactionsOnHolidayEnabled();
        for (final SingleRepaymentCommand singleLoanRepaymentCommand : repaymentCommand) {
            if (singleLoanRepaymentCommand != null) {
                final Loan loans = this.loanRepository.findOne(singleLoanRepaymentCommand.getLoanId());
                final List<Holiday> holidays = this.holidayRepository.findByOfficeIdAndGreaterThanDate(loans.getOfficeId(),
                        singleLoanRepaymentCommand.getTransactionDate().toDate());
                final WorkingDays workingDays = this.workingDaysRepository.findOne();
                final boolean allowTransactionsOnNonWorkingDay = this.configurationDomainService.allowTransactionsOnNonWorkingDayEnabled();
                boolean isHolidayEnabled = false;
                isHolidayEnabled = this.configurationDomainService.isRescheduleRepaymentsOnHolidaysEnabled();
                final List<WorkingDayExemptionsData> workingDayExemptions = this.workingDayExcumptionsReadPlatformService
                        .getWorkingDayExemptionsForEntityType(EntityAccountType.LOAN.getValue());
                holidayDetailDTO = new HolidayDetailDTO(isHolidayEnabled, holidays, workingDays, allowTransactionsOnHoliday,
                        allowTransactionsOnNonWorkingDay, workingDayExemptions);
                loans.validateRepaymentDateIsOnHoliday(singleLoanRepaymentCommand.getTransactionDate(),
                        holidayDetailDTO.isAllowTransactionsOnHoliday(), holidayDetailDTO.getHolidays());
                loans.validateRepaymentDateIsOnNonWorkingDay(singleLoanRepaymentCommand.getTransactionDate(),
                        holidayDetailDTO.getWorkingDays(), holidayDetailDTO.isAllowTransactionsOnNonWorkingDay());
                isHolidayValidationDone = true;
                break;
            }

        }
        for (final SingleRepaymentCommand singleLoanRepaymentCommand : repaymentCommand) {
            if (singleLoanRepaymentCommand != null) {
                final Loan loan = this.loanAssembler.assembleFrom(singleLoanRepaymentCommand.getLoanId());
                final PaymentDetail paymentDetail = singleLoanRepaymentCommand.getPaymentDetail();
                if (paymentDetail != null && paymentDetail.getId() == null) {
                    this.paymentDetailWritePlatformService.persistPaymentDetail(paymentDetail);
                }
                final CommandProcessingResultBuilder commandProcessingResultBuilder = new CommandProcessingResultBuilder();
                final LoanTransaction loanTransaction = this.loanAccountDomainService.makeRepayment(loan, commandProcessingResultBuilder,
                        bulkRepaymentCommand.getTransactionDate(), singleLoanRepaymentCommand.getTransactionAmount(), paymentDetail,
                        bulkRepaymentCommand.getNote(), null, isRecoveryRepayment, isAccountTransfer, holidayDetailDTO,
                        isHolidayValidationDone);
                transactionIds.add(loanTransaction.getId());
                final String errorMessage = null;
                Boolean transactionStatus = false;
                if (loanTransaction.getId() != null) {
                    transactionStatus = true;
                }
                final CollectionSheetTransactionDetails singleCollectionSheetTransactionDetails = CollectionSheetTransactionDetails
                        .formCollectionSheetTransactionDetails(loan.getId(), loanTransaction.getId(), transactionStatus, errorMessage,
                                EntityType.LOAN.getValue());
                collectionSheetTransactionDetails.add(singleCollectionSheetTransactionDetails);
            }
        }
        changes.put("loanTransactions", transactionIds);
        return changes;
    }

    @Transactional
    @Override
    public CommandProcessingResult adjustLoanTransaction(final Long loanId, final Long transactionId, final JsonCommand command) {
        AdjustedLoanTransactionDetails changedLoanTransactionDetails = null;
        this.loanEventApiJsonValidator.validateTransaction(command.json());
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        final Map<String, Object> changes = new LinkedHashMap<>();
        final Collection<GroupLoanIndividualMonitoringDataChanges> clientMembers = new ArrayList<>();
        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");
        final String txnExternalId = command.stringValueOfParameterNamedAllowingNull("externalId");
        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
        if (loan.isGLIMLoan()) {
            final List<GroupLoanIndividualMonitoring> defaultGlimMembers = this.glimRepository.findByLoanIdAndIsClientSelected(loanId,
                    true);
            loan.updateDefautGlimMembers(defaultGlimMembers);
            final boolean isRecoveryPayment = false;
            final List<GroupLoanIndividualMonitoring> glimMembers = this.glimAssembler.assembleGlimFromJson(command, isRecoveryPayment);
            loan.updateGlim(glimMembers);
        }
        final String noteText = command.stringValueOfParameterNamed("note");
        final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createPaymentDetail(command, changes);
        final Long fromAccountId = null;
        final String description = null;
        final Integer fromTransferType = null;
        final Integer toTransferType = null;
        final Long chargeId = null;
        final Integer loanInstallmentNumber = null;
        final Integer transferType = null;
        final AccountTransferDetails accountTransferDetails = null;
        final SavingsAccount toSavingsAccount = null;
        final SavingsAccount fromSavingsAccount = null;
        final Boolean isRegularTransaction = false;
        final Boolean isExceptionForBalanceCheck = false;
        final boolean isRecoveryRepayment = false;

        if (this.accountTransfersReadPlatformService.isAccountTransfer(transactionId, PortfolioAccountType.LOAN)) {
            final AccountTransferDTO accountTransferDTO = new AccountTransferDTO(transactionDate, transactionAmount,
                    PortfolioAccountType.SAVINGS, PortfolioAccountType.LOAN, fromAccountId, loanId, description, locale, fmt, paymentDetail,
                    fromTransferType, toTransferType, chargeId, loanInstallmentNumber, transferType, accountTransferDetails, noteText,
                    txnExternalId, loan, toSavingsAccount, fromSavingsAccount, isRegularTransaction, isExceptionForBalanceCheck);
            changedLoanTransactionDetails = this.accountTransfersWritePlatformService.reverseTransaction(accountTransferDTO, transactionId,
                    PortfolioAccountType.LOAN);
        } else {
            final boolean isAccountTransfer = false;
            final boolean isLoanToLoanTransfer = false;
            changedLoanTransactionDetails = this.loanAccountDomainService.reverseLoanTransactions(loan, transactionId, transactionDate,
                    transactionAmount, txnExternalId, locale, fmt, noteText, paymentDetail, isAccountTransfer, isLoanToLoanTransfer);
        }
        final LoanTransaction newLoanTransaction = changedLoanTransactionDetails.getNewTransactionDetail();
        if (loan.isGLIMLoan() && MathUtility.isGreaterThanZero(newLoanTransaction.getAmount(loan.getCurrency()))) {
            this.glimTransactionAssembler.updateGlimTransactionsData(command, loan, changes, clientMembers, isRecoveryRepayment,
                    newLoanTransaction);
        }

        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_ADJUST_TRANSACTION,
                constructEntityMap(BUSINESS_ENTITY.LOAN_ADJUSTED_TRANSACTION, changedLoanTransactionDetails.getTransactionToAdjust()));
        final Map<BUSINESS_ENTITY, Object> entityMap = constructEntityMap(BUSINESS_ENTITY.LOAN_ADJUSTED_TRANSACTION,
                changedLoanTransactionDetails.getTransactionToAdjust());
        if (changedLoanTransactionDetails.getNewTransactionDetail().isRepayment()
                && changedLoanTransactionDetails.getNewTransactionDetail().isGreaterThanZero(loan.getPrincpal().getCurrency())) {
            entityMap.put(BUSINESS_ENTITY.LOAN_TRANSACTION, changedLoanTransactionDetails.getNewTransactionDetail());
        }
        final Map<BUSINESS_ENTITY, Object> changedTransactionEntityMap = constructEntityMap(BUSINESS_ENTITY.CHANGED_TRANSACTION_DETAIL,
                changedLoanTransactionDetails);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_ADJUST_TRANSACTION,
                changedTransactionEntityMap);

        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_ADJUST_TRANSACTION, entityMap);
        if (loan.isGLIMLoan()) {
            this.glimTransactionAssembler.updateLoanStatusForGLIM(loan);
        }
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(transactionId) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .with(changedLoanTransactionDetails.getChanges()) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult waiveInterestOnLoan(final Long loanId, final JsonCommand command) {

        this.loanEventApiJsonValidator.validateTransaction(command.json());

        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put("transactionDate", command.stringValueOfParameterNamed("transactionDate"));
        changes.put("transactionAmount", command.stringValueOfParameterNamed("transactionAmount"));
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());
        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        checkClientOrGroupActive(loan);

        final String noteText = command.stringValueOfParameterNamed("note");
        final CommandProcessingResultBuilder commandProcessingResultBuilder = new CommandProcessingResultBuilder();
        this.loanAccountDomainService.waiveInterest(loan, commandProcessingResultBuilder, transactionDate, transactionAmount, noteText,
                changes);
        return commandProcessingResultBuilder.withCommandId(command.commandId()) //
                .withLoanId(loanId) //
                .with(changes) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult writeOff(final Long loanId, final JsonCommand command) {
        final AppUser currentUser = getAppUserIfPresent();

        this.loanEventApiJsonValidator.validateTransactionWithNoAmount(command.json());

        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put("transactionDate", command.stringValueOfParameterNamed("transactionDate"));
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        if (command.hasParameter("writeoffReasonId")) {
            final Long writeoffReasonId = command.longValueOfParameterNamed("writeoffReasonId");
            final CodeValue writeoffReason = this.codeValueRepository
                    .findOneByCodeNameAndIdWithNotFoundDetection(LoanApiConstants.WRITEOFFREASONS, writeoffReasonId);
            changes.put("writeoffReasonId", writeoffReasonId);
            loan.updateWriteOffReason(writeoffReason);
        }

        checkClientOrGroupActive(loan);
        if (loan.getSummary().getUnaccountedPrincipal().compareTo(BigDecimal.ZERO) != 1) { throw new LoanWriteOffException(); }
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_WRITTEN_OFF,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));
        removeLoanCycle(loan);

        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();

        updateLoanCounters(loan, loan.getDisbursementDate());

        LocalDate recalculateFrom = null;
        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            recalculateFrom = command.localDateValueOfParameterNamed("transactionDate");
        }

        final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);
        scheduleGeneratorDTO.setBusinessEvent(BUSINESS_EVENTS.LOAN_WRITTEN_OFF);
        final ChangedTransactionDetail changedTransactionDetail = loan.closeAsWrittenOff(command, defaultLoanLifecycleStateMachine(),
                changes, existingTransactionIds, existingReversedTransactionIds, currentUser, scheduleGeneratorDTO);
        final LoanTransaction writeoff = changedTransactionDetail.getNewTransactionMappings().remove(0L);
        this.loanTransactionRepository.save(writeoff);
        final LoanTransaction accrualwriteoff = changedTransactionDetail.getNewTransactionMappings().remove(-1L);
        if (accrualwriteoff != null) {
            accrualwriteoff.setAssociatedTransactionId(writeoff.getId());
            this.loanTransactionRepository.save(accrualwriteoff);
        }

        for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
            this.loanTransactionRepository.save(mapEntry.getValue());
            this.accountTransfersWritePlatformService.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
        }
        loan.getLoanTransactions().addAll(changedTransactionDetail.getNewTransactionMappings().values());
        final Map<BUSINESS_ENTITY, Object> changedTransactionEntityMap = constructEntityMap(BUSINESS_ENTITY.CHANGED_TRANSACTION_DETAIL,
                changedTransactionDetail);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_WRITTEN_OFF, changedTransactionEntityMap);
        saveLoanWithDataIntegrityViolationChecks(loan);
        final String noteText = command.stringValueOfParameterNamed("note");
        if (StringUtils.isNotBlank(noteText)) {
            changes.put("note", noteText);
            final Note note = Note.loanTransactionNote(loan, writeoff, noteText);
            this.noteRepository.save(note);
        }

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);
        this.loanAccountDomainService.recalculateAccruals(loan);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_WRITTEN_OFF,
                constructEntityMap(BUSINESS_ENTITY.LOAN_TRANSACTION, writeoff));
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(writeoff.getId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .with(changes) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult closeLoan(final Long loanId, final JsonCommand command) {

        final AppUser currentUser = getAppUserIfPresent();

        this.loanEventApiJsonValidator.validateTransactionWithNoAmount(command.json());

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        checkClientOrGroupActive(loan);
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_CLOSE,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put("transactionDate", command.stringValueOfParameterNamed("transactionDate"));
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());

        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();

        updateLoanCounters(loan, loan.getDisbursementDate());

        LocalDate recalculateFrom = null;
        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            recalculateFrom = command.localDateValueOfParameterNamed("transactionDate");
        }

        final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);
        final ChangedTransactionDetail changedTransactionDetail = loan.close(command, defaultLoanLifecycleStateMachine(), changes,
                existingTransactionIds, existingReversedTransactionIds, scheduleGeneratorDTO, currentUser);
        final LoanTransaction possibleClosingTransaction = changedTransactionDetail.getNewTransactionMappings().remove(0L);
        if (possibleClosingTransaction != null) {
            this.loanTransactionRepository.save(possibleClosingTransaction);
            final LoanTransaction accrualwriteoff = changedTransactionDetail.getNewTransactionMappings().remove(-1L);
            if (accrualwriteoff != null) {
                accrualwriteoff.setAssociatedTransactionId(possibleClosingTransaction.getId());
                this.loanTransactionRepository.save(accrualwriteoff);
            }
        }

        for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
            this.loanTransactionRepository.save(mapEntry.getValue());
            this.accountTransfersWritePlatformService.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
        }
        loan.getLoanTransactions().addAll(changedTransactionDetail.getNewTransactionMappings().values());
        saveLoanWithDataIntegrityViolationChecks(loan);

        final String noteText = command.stringValueOfParameterNamed("note");
        if (StringUtils.isNotBlank(noteText)) {
            changes.put("note", noteText);
            final Note note = Note.loanNote(loan, noteText);
            this.noteRepository.save(note);
        }

        if (possibleClosingTransaction != null) {
            postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);
        }
        this.loanAccountDomainService.recalculateAccruals(loan);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_CLOSE,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

        // disable all active standing instructions linked to the loan
        this.loanAccountDomainService.disableStandingInstructionsLinkedToClosedLoan(loan);

        final Map<BUSINESS_ENTITY, Object> changedTransactionEntityMap = constructEntityMap(BUSINESS_ENTITY.CHANGED_TRANSACTION_DETAIL,
                changedTransactionDetail);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_CLOSE, changedTransactionEntityMap);
        CommandProcessingResult result = null;
        if (possibleClosingTransaction != null) {

            result = new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(possibleClosingTransaction.getId()) //
                    .withOfficeId(loan.getOfficeId()) //
                    .withClientId(loan.getClientId()) //
                    .withGroupId(loan.getGroupId()) //
                    .withLoanId(loanId) //
                    .with(changes) //
                    .build();
        } else {
            result = new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(loanId) //
                    .withOfficeId(loan.getOfficeId()) //
                    .withClientId(loan.getClientId()) //
                    .withGroupId(loan.getGroupId()) //
                    .withLoanId(loanId) //
                    .with(changes) //
                    .build();
        }

        return result;
    }

    @Transactional
    @Override
    public CommandProcessingResult closeAsRescheduled(final Long loanId, final JsonCommand command) {

        this.loanEventApiJsonValidator.validateTransactionWithNoAmount(command.json());

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        checkClientOrGroupActive(loan);
        removeLoanCycle(loan);
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_CLOSE_AS_RESCHEDULE,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put("transactionDate", command.stringValueOfParameterNamed("transactionDate"));
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());

        loan.closeAsMarkedForReschedule(command, defaultLoanLifecycleStateMachine(), changes);

        saveLoanWithDataIntegrityViolationChecks(loan);

        final String noteText = command.stringValueOfParameterNamed("note");
        if (StringUtils.isNotBlank(noteText)) {
            changes.put("note", noteText);
            final Note note = Note.loanNote(loan, noteText);
            this.noteRepository.save(note);
        }
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_CLOSE_AS_RESCHEDULE,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

        // disable all active standing instructions linked to the loan
        this.loanAccountDomainService.disableStandingInstructionsLinkedToClosedLoan(loan);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loanId) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .with(changes) //
                .build();
    }

    private void validateAddingNewChargeAllowed(@SuppressWarnings("unused") final List<LoanDisbursementDetails> loanDisburseDetails, final Loan loan,
            final Charge chargeDefinition) {
        if (loan.status().isActive() && chargeDefinition.isDisbursementCharge()) { throw new ChargeCannotBeUpdatedException(
                "error.msg.charge.cannot.be.applied.to.active.loan", "This charge cannot be applied to active loan"); }
    }

    @SuppressWarnings("null")
    @Transactional
    @Override
    public CommandProcessingResult addLoanCharge(final Long loanId, final JsonCommand command) {

        this.loanEventApiJsonValidator.validateAddLoanCharge(command.json());

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        checkClientOrGroupActive(loan);

        final List<LoanDisbursementDetails> loanDisburseDetails = loan.getDisbursementDetails();
        final Long chargeDefinitionId = command.longValueOfParameterNamed("chargeId");
        this.fineractEntityAccessUtil.checkConfigurationAndValidateProductOrChargeResrictionsForUserOffice(
                FineractEntityAccessType.OFFICE_ACCESS_TO_CHARGES, chargeDefinitionId);
        final Charge chargeDefinition = this.chargeRepository.findOneWithNotFoundDetection(chargeDefinitionId);

        if (loan.isDisbursed() && chargeDefinition.isDisbursementCharge()) {
            validateAddingNewChargeAllowed(loanDisburseDetails, loan, chargeDefinition); // validates
            // whether any
            // pending
            // disbursements
            // are
            // available to
            // apply this
            // charge
        }
        final List<Long> existingTransactionIds = new ArrayList<>(loan.findExistingTransactionIds());
        final List<Long> existingReversedTransactionIds = new ArrayList<>(loan.findExistingReversedTransactionIds());

        boolean isAppliedOnBackDate = false;
        LoanCharge loanCharge = null;
        LocalDate recalculateFrom = loan.fetchInterestRecalculateFromDate();
        final Collection<LoanCharge> createdCharges = new ArrayList<>();
        final Collection<LoanTransaction> chargeTransactions = new ArrayList<>();
        if (chargeDefinition.isTrancheDisbursement()) {
            LoanTrancheDisbursementCharge loanTrancheDisbursementCharge = null;
            final BigDecimal amount = command.bigDecimalValueOfParameterNamed("amount");
            for (final LoanDisbursementDetails disbursementDetail : loanDisburseDetails) {
                if (disbursementDetail.actualDisbursementDate() == null) {
                    loanCharge = LoanCharge.createNewWithoutLoan(chargeDefinition, disbursementDetail.principal(), amount, null, null,
                            disbursementDetail.expectedDisbursementDateAsLocalDate(), null, null);
                    loanTrancheDisbursementCharge = new LoanTrancheDisbursementCharge(loanCharge, disbursementDetail);
                    loanCharge.updateLoanTrancheDisbursementCharge(loanTrancheDisbursementCharge);
                    this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_ADD_CHARGE,
                            constructEntityMap(BUSINESS_ENTITY.LOAN_CHARGE, loanCharge));
                    validateAddLoanCharge(loan, chargeDefinition, loanCharge);
                    addCharge(loan, chargeDefinition, loanCharge, createdCharges, chargeTransactions);
                    isAppliedOnBackDate = true;
                    if (recalculateFrom.isAfter(disbursementDetail.expectedDisbursementDateAsLocalDate())) {
                        recalculateFrom = disbursementDetail.expectedDisbursementDateAsLocalDate();
                    }
                    if (loanCharge.getTaxGroup() != null) {
                        loanCharge.createLoanChargeTaxDetails(disbursementDetail.expectedDisbursementDateAsLocalDate(),
                                loanCharge.amount());
                    }
                }
            }
            loan.addTrancheLoanCharge(chargeDefinition, amount);
        } else {
            loanCharge = LoanCharge.createNewFromJson(loan, chargeDefinition, command);
            this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_ADD_CHARGE,
                    constructEntityMap(BUSINESS_ENTITY.LOAN_CHARGE, loanCharge));

            validateAddLoanCharge(loan, chargeDefinition, loanCharge);
            isAppliedOnBackDate = addCharge(loan, chargeDefinition, loanCharge, createdCharges, chargeTransactions);
            if (loanCharge.getDueLocalDate() == null || recalculateFrom.isAfter(loanCharge.getDueLocalDate())) {
                isAppliedOnBackDate = true;
                recalculateFrom = loanCharge.getDueLocalDate();
            }
            if (loanCharge.getTaxGroup() != null) {
                loanCharge.createLoanChargeTaxDetails(loan.getDisbursementDate(), loanCharge.amount());
            }
        }

        if (!createdCharges.isEmpty()) {
            this.loanChargeRepository.save(createdCharges);
            if (!chargeTransactions.isEmpty()) {
                this.loanTransactionRepository.save(chargeTransactions);
            }
        }

        boolean reprocessRequired = true;
        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            if (isAppliedOnBackDate && loan.isFeeCompoundingEnabledForInterestRecalculation()) {

                runScheduleRecalculation(loan, recalculateFrom);
                reprocessRequired = false;
            }
            updateOriginalSchedule(loan);
        }

        if (reprocessRequired) {
            final ChangedTransactionDetail changedTransactionDetail = loan.reprocessTransactions();
            if (changedTransactionDetail != null) {
                for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                    this.loanTransactionRepository.save(mapEntry.getValue());
                    // update loan with references to the newly created
                    // transactions
                    loan.getLoanTransactions().add(mapEntry.getValue());
                    this.accountTransfersWritePlatformService.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
                }
                final Map<BUSINESS_ENTITY, Object> changedTransactionEntityMap = constructEntityMap(
                        BUSINESS_ENTITY.CHANGED_TRANSACTION_DETAIL, changedTransactionDetail);
                this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_ADD_CHARGE,
                        changedTransactionEntityMap);
            }
        }
        saveLoanWithDataIntegrityViolationChecks(loan);

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);

        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled() && isAppliedOnBackDate
                && loan.isFeeCompoundingEnabledForInterestRecalculation()) {
            this.loanAccountDomainService.recalculateAccruals(loan);
        }
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_ADD_CHARGE,
                constructEntityMap(BUSINESS_ENTITY.LOAN_CHARGE, loanCharge));
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loanCharge.getId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .build();
    }

    private void validateAddLoanCharge(final Loan loan, final Charge chargeDefinition, final LoanCharge loanCharge) {
        if (chargeDefinition.isOverdueInstallment()) {
            final String defaultUserMessage = "Installment charge cannot be added to the loan.";
            throw new LoanChargeCannotBeAddedException("loanCharge", "overdue.charge", defaultUserMessage, null,
                    chargeDefinition.getName());
        } else if (loanCharge.getDueLocalDate() != null
                && loanCharge.getDueLocalDate().isBefore(loan.getLastUserTransactionForChargeCalc())) {
            final String defaultUserMessage = "charge with date before last transaction date can not be added to loan.";
            throw new LoanChargeCannotBeAddedException("loanCharge", "date.is.before.last.transaction.date", defaultUserMessage, null,
                    chargeDefinition.getName());
        } else if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {

            if (loanCharge.isInstalmentFee() && loan.status().isActive()) {
                final String defaultUserMessage = "installment charge addition not allowed after disbursement";
                throw new LoanChargeCannotBeAddedException("loanCharge", "installment.charge", defaultUserMessage, null,
                        chargeDefinition.getName());
            }
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final Set<LoanCharge> loanCharges = new HashSet<>(1);
            loanCharges.add(loanCharge);
            this.loanApplicationCommandFromApiJsonHelper.validateLoanCharges(loanCharges, dataValidationErrors,
                    loan.repaymentScheduleDetail().isInterestRecalculationEnabled());
            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
        } else if (loan.isMultiDisburmentLoan()) {
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final Set<LoanCharge> loanCharges = new HashSet<>(1);
            loanCharges.add(loanCharge);
            this.loanApplicationCommandFromApiJsonHelper.validateLoanCharges(loanCharges, dataValidationErrors,
                    loan.repaymentScheduleDetail().isInterestRecalculationEnabled());
            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
        } else if (loanCharge.isCapitalized()) {
            final String defaultUserMessage = "Capitalized charge cannot be added to the loan.";
            throw new LoanChargeCannotBeAddedException("loanCharge", "capitalized.charge", defaultUserMessage, null,
                    chargeDefinition.getName());
        }

    }

    public void runScheduleRecalculation(final Loan loan, final LocalDate recalculateFrom) {
        final AppUser currentUser = getAppUserIfPresent();
        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            final ScheduleGeneratorDTO generatorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);
            final ChangedTransactionDetail changedTransactionDetail = loan
                    .handleRegenerateRepaymentScheduleWithInterestRecalculation(generatorDTO, currentUser);
            saveLoanWithDataIntegrityViolationChecks(loan);
            if (changedTransactionDetail != null) {
                for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                    this.loanTransactionRepository.save(mapEntry.getValue());
                    // update loan with references to the newly created
                    // transactions
                    loan.getLoanTransactions().add(mapEntry.getValue());
                    this.accountTransfersWritePlatformService.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
                }
            }

        }
    }

    public void updateOriginalSchedule(final Loan loan) {
        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            final LocalDate recalculateFrom = null;
            final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);
            createLoanScheduleArchive(loan, scheduleGeneratorDTO);
        }

    }

    private boolean addCharge(final Loan loan, final Charge chargeDefinition, final LoanCharge loanCharge,
            final Collection<LoanCharge> createdCharges, final Collection<LoanTransaction> chargeTransactions) {

        if (!loan.hasCurrencyCodeOf(chargeDefinition.getCurrencyCode())) {
            final String errorMessage = "Charge and Loan must have the same currency.";
            throw new InvalidCurrencyException("loanCharge", "attach.to.loan", errorMessage);
        }

        if (loanCharge.getChargePaymentMode().isPaymentModeAccountTransfer()) {
            final PortfolioAccountData portfolioAccountData = this.accountAssociationsReadPlatformService
                    .retriveLoanLinkedAssociation(loan.getId());
            if (portfolioAccountData == null) {
                final String errorMessage = loanCharge.name() + "Charge  requires linked savings account for payment";
                throw new LinkedAccountRequiredException("loanCharge.add", errorMessage, loanCharge.name());
            }
        }

        loan.addLoanCharge(loanCharge);
        createdCharges.add(loanCharge);

        /**
         * we want to apply charge transactions only for those loans charges
         * that are applied when a loan is active and the loan product uses
         * Upfront Accruals
         **/
        if (loan.status().isActive() && loan.isNoneOrCashOrUpfrontAccrualAccountingEnabledOnLoanProduct()) {
            final LoanTransaction applyLoanChargeTransaction = loan.handleChargeAppliedTransaction(loanCharge, null);
            chargeTransactions.add(applyLoanChargeTransaction);
        }
        boolean isAppliedOnBackDate = false;
        if (loanCharge.getDueLocalDate() == null || DateUtils.getLocalDateOfTenant().isAfter(loanCharge.getDueLocalDate())) {
            isAppliedOnBackDate = true;
        }
        return isAppliedOnBackDate;
    }

    @Transactional
    @Override
    public CommandProcessingResult updateLoanCharge(final Long loanId, final Long loanChargeId, final JsonCommand command) {

        this.loanEventApiJsonValidator.validateUpdateOfLoanCharge(command.json());

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        checkClientOrGroupActive(loan);
        final LoanCharge loanCharge = retrieveLoanChargeBy(loanId, loanChargeId);
        // Charges may be edited only when the loan associated with them are
        // yet to be approved (are in submitted and pending status)
        validateForUpdate(loan, loanCharge);
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_UPDATE_CHARGE,
                constructEntityMap(BUSINESS_ENTITY.LOAN_CHARGE, loanCharge));

        final Map<String, Object> changes = loan.updateLoanCharge(loanCharge, command);
        loan.validateChargeHasValidSpecifiedDateIfApplicable(loanCharge, loan.getDisbursementDate(), loan.getMaturityDate());

        saveLoanWithDataIntegrityViolationChecks(loan);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_UPDATE_CHARGE,
                constructEntityMap(BUSINESS_ENTITY.LOAN_CHARGE, loanCharge));
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loanChargeId) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .with(changes) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult waiveLoanCharge(final Long loanId, final Long loanChargeId, final JsonCommand command) {

        final AppUser currentUser = getAppUserIfPresent();

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        checkClientOrGroupActive(loan);
        this.loanEventApiJsonValidator.validateInstallmentChargeTransaction(command.json());
        final LoanCharge loanCharge = retrieveLoanChargeBy(loanId, loanChargeId);

        // Charges may be waived only when the loan associated with them are
        // active
        if (!loan.status().isActive()) { throw new LoanChargeCannotBeWaivedException(LOAN_CHARGE_CANNOT_BE_WAIVED_REASON.LOAN_INACTIVE,
                loanCharge.getId()); }

        // validate loan charge is not already paid or waived
        if (loanCharge.isWaived()) {
            throw new LoanChargeCannotBeWaivedException(LOAN_CHARGE_CANNOT_BE_WAIVED_REASON.ALREADY_WAIVED, loanCharge.getId());
        } else if (loanCharge.isPaid()) { throw new LoanChargeCannotBeWaivedException(LOAN_CHARGE_CANNOT_BE_WAIVED_REASON.ALREADY_PAID,
                loanCharge.getId()); }
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_WAIVE_CHARGE,
                constructEntityMap(BUSINESS_ENTITY.LOAN_CHARGE, loanCharge));
        Integer loanInstallmentNumber = null;
        if (loanCharge.isInstalmentFee()) {
            LoanInstallmentCharge chargePerInstallment = null;
            if (!StringUtils.isBlank(command.json())) {
                final LocalDate dueDate = command.localDateValueOfParameterNamed("dueDate");
                final Integer installmentNumber = command.integerValueOfParameterNamed("installmentNumber");
                if (dueDate != null) {
                    chargePerInstallment = loanCharge.getInstallmentLoanCharge(dueDate);
                } else if (installmentNumber != null) {
                    chargePerInstallment = loanCharge.getInstallmentLoanCharge(installmentNumber);
                }
            }
            if (chargePerInstallment == null) {
                chargePerInstallment = loanCharge.getUnpaidInstallmentLoanCharge();
            }
            if (chargePerInstallment.isWaived()) {
                throw new LoanChargeCannotBePayedException(LOAN_CHARGE_CANNOT_BE_PAYED_REASON.ALREADY_WAIVED, loanCharge.getId());
            } else if (chargePerInstallment
                    .isPaid()) { throw new LoanChargeCannotBePayedException(LOAN_CHARGE_CANNOT_BE_PAYED_REASON.ALREADY_PAID,
                            loanCharge.getId()); }
            loanInstallmentNumber = chargePerInstallment.getRepaymentInstallment().getInstallmentNumber();
        }

        final Map<String, Object> changes = new LinkedHashMap<>(3);

        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();
        final LocalDate recalculateFrom = null;
        final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);

        Money accruedCharge = Money.zero(loan.getCurrency());
        if (loan.isPeriodicAccrualAccountingEnabledOnLoanProduct()) {
            final Collection<LoanChargePaidByData> chargePaidByDatas = this.loanChargeReadPlatformService
                    .retriveLoanChargesPaidBy(loanCharge.getId(), LoanTransactionType.ACCRUAL, loanInstallmentNumber);
            for (final LoanChargePaidByData chargePaidByData : chargePaidByDatas) {
                accruedCharge = accruedCharge.plus(chargePaidByData.getAmount());
            }
        }

        final LoanTransaction waiveTransaction = loan.waiveLoanCharge(loanCharge, defaultLoanLifecycleStateMachine(), changes,
                existingTransactionIds, existingReversedTransactionIds, loanInstallmentNumber, scheduleGeneratorDTO, accruedCharge,
                currentUser);

        this.loanTransactionRepository.save(waiveTransaction);
        this.loanAccountDomainService.handleWaiverForAccrualSuspense(loan, waiveTransaction);
        saveLoanWithDataIntegrityViolationChecks(loan);

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);

        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_WAIVE_CHARGE,
                constructEntityMap(BUSINESS_ENTITY.LOAN_CHARGE, loanCharge));

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loanChargeId) //
                .withTransactionId(waiveTransaction.getId().toString()).withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .with(changes) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteLoanCharge(final Long loanId, final Long loanChargeId, final JsonCommand command) {

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        checkClientOrGroupActive(loan);
        final LoanCharge loanCharge = retrieveLoanChargeBy(loanId, loanChargeId);
        final Long productId = loan.productId();
        final Boolean isPenalty = false;
        final List<Map<String, Object>> chargeIdList = this.loanProductReadPlatformService.getLoanProductMandatoryCharges(productId,
                isPenalty);
        this.loanProductBusinessRuleValidator.validateLoanProductChargeMandatoryOrNot(chargeIdList, loanCharge.getCharge().getId());

        if (loan.isGLIMLoan()) {
            throw new LoanChargeCannotBeDeletedException(LOAN_CHARGE_CANNOT_BE_DELETED_REASON.GLIM_LOAN_CHARGE, loanCharge.getId());
        } else if (loanCharge
                .isCapitalized()) { throw new LoanChargeCannotBeDeletedException(LOAN_CHARGE_CANNOT_BE_DELETED_REASON.CAPITALIZED_CHARGE,
                        loanCharge.getId()); }

        // Charges may be deleted only when the loan associated with them are
        // yet to be approved (are in submitted and pending status)
        if (!loan.status().isSubmittedAndPendingApproval()) { throw new LoanChargeCannotBeDeletedException(
                LOAN_CHARGE_CANNOT_BE_DELETED_REASON.LOAN_NOT_IN_SUBMITTED_AND_PENDING_APPROVAL_STAGE, loanCharge.getId()); }
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_DELETE_CHARGE,
                constructEntityMap(BUSINESS_ENTITY.LOAN_CHARGE, loanCharge));

        loan.removeLoanCharge(loanCharge);
        saveLoanWithDataIntegrityViolationChecks(loan);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_DELETE_CHARGE,
                constructEntityMap(BUSINESS_ENTITY.LOAN_CHARGE, loanCharge));
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loanChargeId) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .build();
    }

    @Override
    @Transactional
    public CommandProcessingResult payLoanCharge(final Long loanId, Long loanChargeId, final JsonCommand command,
            final boolean isChargeIdIncludedInJson) {

        this.loanEventApiJsonValidator.validateChargePaymentTransaction(command.json(), isChargeIdIncludedInJson);
        if (isChargeIdIncludedInJson) {
            loanChargeId = command.longValueOfParameterNamed("chargeId");
        }
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        checkClientOrGroupActive(loan);
        final LoanCharge loanCharge = retrieveLoanChargeBy(loanId, loanChargeId);

        // Charges may be waived only when the loan associated with them are
        // active
        if (!loan.status().isActive()) { throw new LoanChargeCannotBePayedException(LOAN_CHARGE_CANNOT_BE_PAYED_REASON.LOAN_INACTIVE,
                loanCharge.getId()); }

        // validate loan charge is not already paid or waived
        if (loanCharge.isWaived()) {
            throw new LoanChargeCannotBePayedException(LOAN_CHARGE_CANNOT_BE_PAYED_REASON.ALREADY_WAIVED, loanCharge.getId());
        } else if (loanCharge.isPaid()) { throw new LoanChargeCannotBePayedException(LOAN_CHARGE_CANNOT_BE_PAYED_REASON.ALREADY_PAID,
                loanCharge.getId()); }

        if (!loanCharge.getChargePaymentMode().isPaymentModeAccountTransfer()) { throw new LoanChargeCannotBePayedException(
                LOAN_CHARGE_CANNOT_BE_PAYED_REASON.CHARGE_NOT_ACCOUNT_TRANSFER, loanCharge.getId()); }

        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
        Integer loanInstallmentNumber = null;
        BigDecimal amount = loanCharge.amountOutstanding();
        if (loanCharge.isInstalmentFee()) {
            LoanInstallmentCharge chargePerInstallment = null;
            final LocalDate dueDate = command.localDateValueOfParameterNamed("dueDate");
            final Integer installmentNumber = command.integerValueOfParameterNamed("installmentNumber");
            if (dueDate != null) {
                chargePerInstallment = loanCharge.getInstallmentLoanCharge(dueDate);
            } else if (installmentNumber != null) {
                chargePerInstallment = loanCharge.getInstallmentLoanCharge(installmentNumber);
            }
            if (chargePerInstallment == null) {
                chargePerInstallment = loanCharge.getUnpaidInstallmentLoanCharge();
            }
            if (chargePerInstallment.isWaived()) {
                throw new LoanChargeCannotBePayedException(LOAN_CHARGE_CANNOT_BE_PAYED_REASON.ALREADY_WAIVED, loanCharge.getId());
            } else if (chargePerInstallment
                    .isPaid()) { throw new LoanChargeCannotBePayedException(LOAN_CHARGE_CANNOT_BE_PAYED_REASON.ALREADY_PAID,
                            loanCharge.getId()); }
            loanInstallmentNumber = chargePerInstallment.getRepaymentInstallment().getInstallmentNumber();
            amount = chargePerInstallment.getAmountOutstanding();
        }

        final PortfolioAccountData portfolioAccountData = this.accountAssociationsReadPlatformService.retriveLoanLinkedAssociation(loanId);
        if (portfolioAccountData == null) {
            final String errorMessage = "Charge with id:" + loanChargeId + " requires linked savings account for payment";
            throw new LinkedAccountRequiredException("loanCharge.pay", errorMessage, loanChargeId);
        }
        final SavingsAccount fromSavingsAccount = null;
        final boolean isRegularTransaction = true;
        final boolean isExceptionForBalanceCheck = false;
        final AccountTransferDTO accountTransferDTO = new AccountTransferDTO(transactionDate, amount, PortfolioAccountType.SAVINGS,
                PortfolioAccountType.LOAN, portfolioAccountData.accountId(), loanId, "Loan Charge Payment", locale, fmt, null, null,
                LoanTransactionType.CHARGE_PAYMENT.getValue(), loanChargeId, loanInstallmentNumber,
                AccountTransferType.CHARGE_PAYMENT.getValue(), null, null, null, null, null, fromSavingsAccount, isRegularTransaction,
                isExceptionForBalanceCheck);
        this.accountTransfersWritePlatformService.transferFunds(accountTransferDTO);
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loanChargeId) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .withSavingsId(portfolioAccountData.accountId()).build();
    }

    public void disburseLoanToLoan(final Loan loan, final Loan loanToClose, final JsonCommand command, final BigDecimal amount) {

        final LocalDate transactionDate = command.localDateValueOfParameterNamed("actualDisbursementDate");
        final String txnExternalId = command.stringValueOfParameterNamedAllowingNull("externalId");

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
        final AccountTransferDTO accountTransferDTO = new AccountTransferDTO(transactionDate, amount, PortfolioAccountType.LOAN,
                PortfolioAccountType.LOAN, loan.getId(), loanToClose.getId(), "Loan Topup", locale, fmt,
                LoanTransactionType.DISBURSEMENT.getValue(), LoanTransactionType.REPAYMENT.getValue(), txnExternalId, loan, loanToClose);
        final AccountTransferDetails accountTransferDetails = this.accountTransfersWritePlatformService
                .repayLoanWithTopup(accountTransferDTO);
        loan.getTopupLoanDetails().setAccountTransferDetails(accountTransferDetails.getId());
        loan.getTopupLoanDetails().setTopupAmount(amount);
    }

    public void disburseLoanToSavings(final Loan loan, final JsonCommand command, final Money amount, final PaymentDetail paymentDetail) {

        final LocalDate transactionDate = command.localDateValueOfParameterNamed("actualDisbursementDate");
        final String txnExternalId = command.stringValueOfParameterNamedAllowingNull("externalId");

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
        final PortfolioAccountData portfolioAccountData = this.accountAssociationsReadPlatformService
                .retriveLoanLinkedAssociation(loan.getId());
        if (portfolioAccountData == null) {
            final String errorMessage = "Disburse Loan with id:" + loan.getId() + " requires linked savings account for payment";
            throw new LinkedAccountRequiredException("loan.disburse.to.savings", errorMessage, loan.getId());
        }
        final SavingsAccount fromSavingsAccount = null;
        final boolean isExceptionForBalanceCheck = false;
        final boolean isRegularTransaction = true;
        final AccountTransferDTO accountTransferDTO = new AccountTransferDTO(transactionDate, amount.getAmount(), PortfolioAccountType.LOAN,
                PortfolioAccountType.SAVINGS, loan.getId(), portfolioAccountData.accountId(), "Loan Disbursement", locale, fmt,
                paymentDetail, LoanTransactionType.DISBURSEMENT.getValue(), null, null, null,
                AccountTransferType.ACCOUNT_TRANSFER.getValue(), null, null, txnExternalId, loan, null, fromSavingsAccount,
                isRegularTransaction, isExceptionForBalanceCheck);
        this.accountTransfersWritePlatformService.transferFunds(accountTransferDTO);

    }

    @Override
    @CronTarget(jobName = JobName.TRANSFER_FEE_CHARGE_FOR_LOANS)
    public void transferFeeCharges() throws JobExecutionException {
        final Collection<LoanChargeData> chargeDatas = this.loanChargeReadPlatformService
                .retrieveLoanChargesForFeePayment(ChargePaymentMode.ACCOUNT_TRANSFER.getValue(), LoanStatus.ACTIVE.getValue());
        final boolean isRegularTransaction = true;
        final StringBuilder sb = new StringBuilder();
        if (chargeDatas != null) {
            for (final LoanChargeData chargeData : chargeDatas) {
                if (chargeData.isInstallmentFee()) {
                    final Collection<LoanInstallmentChargeData> chargePerInstallments = this.loanChargeReadPlatformService
                            .retrieveInstallmentLoanCharges(chargeData.getId(), true);
                    PortfolioAccountData portfolioAccountData = null;
                    for (final LoanInstallmentChargeData installmentChargeData : chargePerInstallments) {
                        if (!installmentChargeData.getDueDate().isAfter(DateUtils.getLocalDateOfTenant())) {
                            if (portfolioAccountData == null) {
                                portfolioAccountData = this.accountAssociationsReadPlatformService
                                        .retriveLoanLinkedAssociation(chargeData.getLoanId());
                            }
                            final SavingsAccount fromSavingsAccount = null;
                            final boolean isExceptionForBalanceCheck = false;
                            final AccountTransferDTO accountTransferDTO = new AccountTransferDTO(DateUtils.getLocalDateOfTenant(),
                                    installmentChargeData.getAmountOutstanding(), PortfolioAccountType.SAVINGS, PortfolioAccountType.LOAN,
                                    portfolioAccountData.accountId(), chargeData.getLoanId(), "Loan Charge Payment", null, null, null, null,
                                    LoanTransactionType.CHARGE_PAYMENT.getValue(), chargeData.getId(),
                                    installmentChargeData.getInstallmentNumber(), AccountTransferType.CHARGE_PAYMENT.getValue(), null, null,
                                    null, null, null, fromSavingsAccount, isRegularTransaction, isExceptionForBalanceCheck);
                            transferFeeCharge(sb, accountTransferDTO);
                        }
                    }
                } else if (chargeData.getDueDate() != null && !chargeData.getDueDate().isAfter(DateUtils.getLocalDateOfTenant())) {
                    final PortfolioAccountData portfolioAccountData = this.accountAssociationsReadPlatformService
                            .retriveLoanLinkedAssociation(chargeData.getLoanId());
                    final SavingsAccount fromSavingsAccount = null;
                    final boolean isExceptionForBalanceCheck = false;
                    final AccountTransferDTO accountTransferDTO = new AccountTransferDTO(DateUtils.getLocalDateOfTenant(),
                            chargeData.getAmountOutstanding(), PortfolioAccountType.SAVINGS, PortfolioAccountType.LOAN,
                            portfolioAccountData.accountId(), chargeData.getLoanId(), "Loan Charge Payment", null, null, null, null,
                            LoanTransactionType.CHARGE_PAYMENT.getValue(), chargeData.getId(), null,
                            AccountTransferType.CHARGE_PAYMENT.getValue(), null, null, null, null, null, fromSavingsAccount,
                            isRegularTransaction, isExceptionForBalanceCheck);
                    transferFeeCharge(sb, accountTransferDTO);
                }
            }
        }
        if (sb.length() > 0) { throw new JobExecutionException(sb.toString()); }
    }

    /**
     * @param sb
     * @param accountTransferDTO
     */
    private void transferFeeCharge(final StringBuilder sb, final AccountTransferDTO accountTransferDTO) {
        try {
            this.accountTransfersWritePlatformService.transferFunds(accountTransferDTO);
        } catch (final PlatformApiDataValidationException e) {
            sb.append("Validation exception while paying charge ").append(accountTransferDTO.getChargeId()).append(" for loan id:")
                    .append(accountTransferDTO.getToAccountId()).append("--------");
        } catch (final InsufficientAccountBalanceException e) {
            sb.append("InsufficientAccountBalance Exception while paying charge ").append(accountTransferDTO.getChargeId())
                    .append("for loan id:").append(accountTransferDTO.getToAccountId()).append("--------");

        }
    }

    private LoanCharge retrieveLoanChargeBy(final Long loanId, final Long loanChargeId) {
        final LoanCharge loanCharge = this.loanChargeRepository.findOne(loanChargeId);
        if (loanCharge == null) { throw new LoanChargeNotFoundException(loanChargeId); }

        if (loanCharge.hasNotLoanIdentifiedBy(loanId)) { throw new LoanChargeNotFoundException(loanChargeId, loanId); }
        return loanCharge;
    }

    @Transactional
    @Override
    public LoanTransaction initiateLoanTransfer(final Long accountId, final LocalDate transferDate) {

        final Loan loan = this.loanAssembler.assembleFrom(accountId);
        checkClientOrGroupActive(loan);
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_INITIATE_TRANSFER,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

        final List<Long> existingTransactionIds = new ArrayList<>(loan.findExistingTransactionIds());
        final List<Long> existingReversedTransactionIds = new ArrayList<>(loan.findExistingReversedTransactionIds());
        final LoanTransaction newTransferTransaction = LoanTransaction.initiateTransfer(loan.getOffice(), loan, transferDate);
        loan.getLoanTransactions().add(newTransferTransaction);
        loan.setLoanStatus(LoanStatus.TRANSFER_IN_PROGRESS.getValue());

        this.loanTransactionRepository.save(newTransferTransaction);
        saveLoanWithDataIntegrityViolationChecks(loan);

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_INITIATE_TRANSFER,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));
        return newTransferTransaction;
    }

    @Transactional
    @Override
    public LoanTransaction acceptLoanTransfer(final Long accountId, final LocalDate transferDate, final Office acceptedInOffice,
            final Staff loanOfficer) {

        final Loan loan = this.loanAssembler.assembleFromWithInitializeLazy(accountId);
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_ACCEPT_TRANSFER,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));
        final List<Long> existingTransactionIds = new ArrayList<>(loan.findExistingTransactionIds());
        final List<Long> existingReversedTransactionIds = new ArrayList<>(loan.findExistingReversedTransactionIds());
        final LoanTransaction newTransferAcceptanceTransaction = LoanTransaction.approveTransfer(acceptedInOffice, loan, transferDate);
        loan.getLoanTransactions().add(newTransferAcceptanceTransaction);
        if (loan.getTotalOverpaid() != null) {
            loan.setLoanStatus(LoanStatus.OVERPAID.getValue());
        } else {
            loan.setLoanStatus(LoanStatus.ACTIVE.getValue());
        }
        if (loanOfficer != null) {
            loan.reassignLoanOfficer(loanOfficer, transferDate);
        }

        this.loanTransactionRepository.save(newTransferAcceptanceTransaction);
        saveLoanWithDataIntegrityViolationChecks(loan);

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_ACCEPT_TRANSFER,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

        return newTransferAcceptanceTransaction;
    }

    @Transactional
    @Override
    public LoanTransaction withdrawLoanTransfer(final Long accountId, final LocalDate transferDate) {

        final Loan loan = this.loanAssembler.assembleFrom(accountId);
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_WITHDRAW_TRANSFER,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

        final List<Long> existingTransactionIds = new ArrayList<>(loan.findExistingTransactionIds());
        final List<Long> existingReversedTransactionIds = new ArrayList<>(loan.findExistingReversedTransactionIds());
        final LoanTransaction newTransferAcceptanceTransaction = LoanTransaction.withdrawTransfer(loan.getOffice(), loan, transferDate);
        loan.getLoanTransactions().add(newTransferAcceptanceTransaction);
        loan.setLoanStatus(LoanStatus.ACTIVE.getValue());

        this.loanTransactionRepository.save(newTransferAcceptanceTransaction);
        saveLoanWithDataIntegrityViolationChecks(loan);

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_WITHDRAW_TRANSFER,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

        return newTransferAcceptanceTransaction;
    }

    @Transactional
    @Override
    public void rejectLoanTransfer(final Long accountId) {
        final Loan loan = this.loanAssembler.assembleFrom(accountId);
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_REJECT_TRANSFER,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));
        loan.setLoanStatus(LoanStatus.TRANSFER_ON_HOLD.getValue());
        saveLoanWithDataIntegrityViolationChecks(loan);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_REJECT_TRANSFER,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));
    }

    @Transactional
    @Override
    public CommandProcessingResult loanReassignment(final Long loanId, final JsonCommand command) {

        this.loanEventApiJsonValidator.validateUpdateOfLoanOfficer(command.json());
        final Long fromLoanOfficerId = command.longValueOfParameterNamed("fromLoanOfficerId");
        final Long toLoanOfficerId = command.longValueOfParameterNamed("toLoanOfficerId");
        if (this.configurationDomainService
                .isLoanOfficerToCenterHierarchyEnabled()) { throw new UpdateStaffHierarchyException(toLoanOfficerId); }
        final Staff fromLoanOfficer = this.loanAssembler.findLoanOfficerByIdIfProvided(fromLoanOfficerId);
        final Staff toLoanOfficer = this.loanAssembler.findLoanOfficerByIdIfProvided(toLoanOfficerId);
        final LocalDate dateOfLoanOfficerAssignment = command.localDateValueOfParameterNamed("assignmentDate");

        final Loan loan = this.loanAssembler.assembleFromWithInitializeLazy(loanId);
        checkClientOrGroupActive(loan);
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_REASSIGN_OFFICER,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));
        if (!loan.hasLoanOfficer(fromLoanOfficer)) { throw new LoanOfficerAssignmentException(loanId, fromLoanOfficerId); }

        loan.reassignLoanOfficer(toLoanOfficer, dateOfLoanOfficerAssignment);

        saveLoanWithDataIntegrityViolationChecks(loan);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_REASSIGN_OFFICER,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loanId) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .build();
    }

    @SuppressWarnings("null")
    @Transactional
    @Override
    public CommandProcessingResult bulkLoanReassignment(final JsonCommand command) {

        this.loanEventApiJsonValidator.validateForBulkLoanReassignment(command.json());

        final Long fromLoanOfficerId = command.longValueOfParameterNamed("fromLoanOfficerId");
        final Long toLoanOfficerId = command.longValueOfParameterNamed("toLoanOfficerId");
        final Set<String> loanIds = new HashSet<>(Arrays.asList(command.arrayValueOfParameterNamed("loans")));
        final LocalDate dateOfLoanOfficerAssignment = command.localDateValueOfParameterNamed("assignmentDate");
        final Set<String> centerIds = new HashSet<>(Arrays.asList(command.arrayValueOfParameterNamed("centers")));
        final Set<String> groupIds = new HashSet<>(Arrays.asList(command.arrayValueOfParameterNamed("groups")));
        final Set<String> clientIds = new HashSet<>(Arrays.asList(command.arrayValueOfParameterNamed("clients")));
        final Staff toLoanOfficer = this.loanAssembler.findLoanOfficerByIdIfProvided(toLoanOfficerId);
        if (this.configurationDomainService.isLoanOfficerToCenterHierarchyEnabled()) {
            if (centerIds != null) {
                for (final String centerIdString : centerIds) {
                    final Long centerId = Long.valueOf(centerIdString);
                    if (!this.centerReadPlatformService.isCenter(centerId)) { throw new CenterNotFoundException(centerId); }
                    this.groupingTypesWritePlatformService.updateGroupOrCenterStaff(centerId, toLoanOfficer.getId());
                    this.groupingTypesWritePlatformService.createStaffAssignmentHistory(centerId, toLoanOfficer.getId(),
                            dateOfLoanOfficerAssignment);
                }
            }
            if (groupIds != null) {
                for (final String groupIdString : groupIds) {
                    final Long groupId = Long.valueOf(groupIdString);
                    this.groupingTypesWritePlatformService.updateGroupOrCenterStaff(groupId, toLoanOfficer.getId());
                }
            }
            if (clientIds != null) {
                for (final String clientIdString : clientIds) {
                    final Long clientId = Long.valueOf(clientIdString);
                    this.clientWritePlatformService.updateClientStaff(clientId, toLoanOfficer.getId());
                }
            }

        }
        for (final String loanIdString : loanIds) {
            final Long loanId = Long.valueOf(loanIdString);
            final LoanOfficerAssignmentHistoryData loanOfficerAssignmentHistory = this.bulkLoansReadPlatformService
                    .retrieveLoanOfficerAssignmentHistoryByLoanId(loanId);

            validateForReasignLoanOfficerForLoans(dateOfLoanOfficerAssignment, loanOfficerAssignmentHistory, loanId, fromLoanOfficerId);
            final LoanStatus loanStatus = loanOfficerAssignmentHistory.getStatus();
            final Long loanOfficerAssignmentHistoryId = loanOfficerAssignmentHistory.getLatestHistoryRecordId();
            final LocalDate latestHistoryRecordStartdate = loanOfficerAssignmentHistory.getLatestHistoryRecordStartdate();
            if (latestHistoryRecordStartdate != null && loanOfficerAssignmentHistory.loanOfficerIdentifiedBy(toLoanOfficer.getId())) {
                this.loanOfficerAssignmentHistoryWriteService.updateStartDate(loanOfficerAssignmentHistoryId, dateOfLoanOfficerAssignment);
            } else if (latestHistoryRecordStartdate != null
                    && loanOfficerAssignmentHistory.matchesStartDateOfLatestHistory(dateOfLoanOfficerAssignment)) {
                this.loanOfficerAssignmentHistoryWriteService.updateLoanOfficer(toLoanOfficer.getId(), loanOfficerAssignmentHistoryId);
                updateLoanOfficer(loanId, toLoanOfficer.getId());
            } else {
                if (latestHistoryRecordStartdate != null) {
                    // loan officer correctly changed from previous loan officer
                    // to
                    // new loan officer
                    this.loanOfficerAssignmentHistoryWriteService.updateEndDate(loanOfficerAssignmentHistoryId,
                            dateOfLoanOfficerAssignment);
                }

                updateLoanOfficer(loanId, toLoanOfficer.getId());
                if (!loanStatus.isSubmittedAndPendingApproval()) {
                    this.loanOfficerAssignmentHistoryWriteService.createLoanOfficerAssignmentHistory(toLoanOfficer.getId(), loanId,
                            dateOfLoanOfficerAssignment);
                }
            }

        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult removeLoanOfficer(final Long loanId, final JsonCommand command) {

        final LoanUpdateCommand loanUpdateCommand = this.loanUpdateCommandFromApiJsonDeserializer.commandFromApiJson(command.json());

        loanUpdateCommand.validate();
        if (this.configurationDomainService.isLoanOfficerToCenterHierarchyEnabled()) { throw new UpdateStaffHierarchyException(loanId); }
        final LocalDate dateOfLoanOfficerunAssigned = command.localDateValueOfParameterNamed("unassignedDate");

        final Loan loan = this.loanAssembler.assembleFromWithInitializeLazy(loanId);
        checkClientOrGroupActive(loan);
        if (loan.getLoanOfficer() == null) { throw new LoanOfficerUnassignmentException(loanId); }
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_REMOVE_OFFICER,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

        loan.removeLoanOfficer(dateOfLoanOfficerunAssigned);

        saveLoanWithDataIntegrityViolationChecks(loan);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_REMOVE_OFFICER,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loanId) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .build();
    }

    private void postJournalEntries(final Loan loan, final List<Long> existingTransactionIds,
            final List<Long> existingReversedTransactionIds) {

        final MonetaryCurrency currency = loan.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);
        final boolean isAccountTransfer = false;
        final Map<String, Object> accountingBridgeData = loan.deriveAccountingBridgeData(applicationCurrency.toData(),
                existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        this.journalEntryWritePlatformService.createJournalEntriesForLoan(accountingBridgeData);
    }

    @Transactional
    @Override
    public void applyMeetingDateChanges(final Calendar calendar, final Collection<CalendarInstance> loanCalendarInstances) {

        final Boolean reschedulebasedOnMeetingDates = false;
        final LocalDate presentMeetingDate = DateUtils.getLocalDateOfTenant();
        final LocalDate newMeetingDate = DateUtils.getLocalDateOfTenant();

        applyMeetingDateChanges(calendar, loanCalendarInstances, reschedulebasedOnMeetingDates, presentMeetingDate, newMeetingDate);

    }

    @Transactional
    @Override
    public void applyMeetingDateChanges(final Calendar calendar, final Collection<CalendarInstance> loanCalendarInstances,
            final Boolean reschedulebasedOnMeetingDates, final LocalDate presentMeetingDate, final LocalDate newMeetingDate) {

        final WorkingDays workingDays = this.workingDaysRepository.findOne();
        final AppUser currentUser = getAppUserIfPresent();
        final Collection<Integer> loanStatuses = new ArrayList<>(Arrays.asList(LoanStatus.SUBMITTED_AND_PENDING_APPROVAL.getValue(),
                LoanStatus.APPROVED.getValue(), LoanStatus.ACTIVE.getValue()));
        final Collection<Integer> loanTypes = new ArrayList<>(Arrays.asList(AccountType.GROUP.getValue(), AccountType.JLG.getValue()));
        final Collection<Long> loanIds = new ArrayList<>(loanCalendarInstances.size());
        // loop through loanCalendarInstances to get loan ids
        for (final CalendarInstance calendarInstance : loanCalendarInstances) {
            loanIds.add(calendarInstance.getEntityId());
        }

        final List<Loan> loans = this.loanRepository.findByIdsAndLoanStatusAndLoanType(loanIds, loanStatuses, loanTypes);
        this.loanEventApiJsonValidator.validateGroupMeetingDateHasActiveLoans(loans, reschedulebasedOnMeetingDates, presentMeetingDate);
        LocalDate recalculateFrom = presentMeetingDate;

        // checking with back dated future meeting date
        if (presentMeetingDate.isAfter(newMeetingDate)) {
            recalculateFrom = newMeetingDate;
        }
        // loop through each loan to reschedule the repayment dates
        for (final Loan loan : loans) {
            if (loan != null) {
                final List<Long> existingTransactionIds = new ArrayList<>();
                final List<Long> existingReversedTransactionIds = new ArrayList<>();
                Boolean runJournalEntries = false;
                if (reschedulebasedOnMeetingDates && loan.getExpectedFirstRepaymentOnDate() != null
                        && loan.getExpectedFirstRepaymentOnDate().equals(presentMeetingDate)) {
                    loan.setExpectedFirstRepaymentOnDate(newMeetingDate.toDate());
                }

                Boolean isSkipRepaymentOnFirstMonth = false;
                Integer numberOfDays = 0;
                final boolean isSkipRepaymentOnFirstMonthEnabled = this.configurationDomainService
                        .isSkippingMeetingOnFirstDayOfMonthEnabled();
                final boolean isHolidayEnabled = this.configurationDomainService.isRescheduleRepaymentsOnHolidaysEnabled();
                if (isSkipRepaymentOnFirstMonthEnabled) {
                    isSkipRepaymentOnFirstMonth = this.loanUtilService.isLoanRepaymentsSyncWithMeeting(loan.group(), calendar);
                    if (isSkipRepaymentOnFirstMonth) {
                        numberOfDays = this.configurationDomainService.retreivePeroidInNumberOfDaysForSkipMeetingDate().intValue();
                    }
                }

                final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);
                if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
                    loan.setHelpers(null, this.loanSummaryWrapper, this.transactionProcessingStrategy);
                    final ChangedTransactionDetail changedTransactionDetail = loan.recalculateScheduleFromLastTransaction(
                            scheduleGeneratorDTO, existingTransactionIds, existingReversedTransactionIds, currentUser);
                    if (changedTransactionDetail != null) {
                        runJournalEntries = true;
                        for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings()
                                .entrySet()) {
                            this.loanTransactionRepository.save(mapEntry.getValue());
                            // update loan with references to the newly created
                            // transactions
                            loan.getLoanTransactions().add(mapEntry.getValue());
                            this.accountTransfersWritePlatformService.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
                        }
                    }
                    this.loanAccountDomainService.createAndSaveLoanScheduleArchive(loan, scheduleGeneratorDTO);
                } else if (reschedulebasedOnMeetingDates) {
                    loan.updateLoanRepaymentScheduleDates(calendar.getRecurrence(), isHolidayEnabled, scheduleGeneratorDTO, workingDays,
                            presentMeetingDate, newMeetingDate, isSkipRepaymentOnFirstMonth, numberOfDays);
                } else {
                    loan.updateLoanRepaymentScheduleDates(calendar.getStartDateLocalDate(), calendar.getRecurrence(), scheduleGeneratorDTO,
                            isHolidayEnabled, workingDays, isSkipRepaymentOnFirstMonth, numberOfDays);
                }

                saveLoanWithDataIntegrityViolationChecks(loan);
                if (runJournalEntries) {
                    postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);
                }
            }
        }
    }

    private void removeLoanCycle(final Loan loan) {
        final List<Loan> loansToUpdate;
        if (loan.isGroupLoan()) {
            if (loan.loanProduct().isIncludeInBorrowerCycle()) {
                loansToUpdate = this.loanRepository.getGroupLoansToUpdateLoanCounter(loan.getCurrentLoanCounter(), loan.getGroupId(),
                        AccountType.GROUP.getValue());
            } else {
                loansToUpdate = this.loanRepository.getGroupLoansToUpdateLoanProductCounter(loan.getLoanProductLoanCounter(),
                        loan.getGroupId(), AccountType.GROUP.getValue());
            }

        } else {
            if (loan.loanProduct().isIncludeInBorrowerCycle()) {
                loansToUpdate = this.loanRepository.getClientOrJLGLoansToUpdateLoanCounter(loan.getCurrentLoanCounter(),
                        loan.getClientId());
            } else {
                loansToUpdate = this.loanRepository.getClientLoansToUpdateLoanProductCounter(loan.getLoanProductLoanCounter(),
                        loan.getClientId());
            }

        }
        if (loansToUpdate != null) {
            updateLoanCycleCounter(loansToUpdate, loan);
        }
        loan.updateClientLoanCounter(null);
        loan.updateLoanProductLoanCounter(null);

    }

    private void updateLoanCounters(final Loan loan, final LocalDate actualDisbursementDate) {

        if (loan.isGroupLoan()) {
            final List<Loan> loansToUpdateForLoanCounter = this.loanRepository.getGroupLoansDisbursedAfter(actualDisbursementDate.toDate(),
                    loan.getGroupId(), AccountType.GROUP.getValue());
            final Integer newLoanCounter = getNewGroupLoanCounter(loan);
            final Integer newLoanProductCounter = getNewGroupLoanProductCounter(loan);
            updateLoanCounter(loan, loansToUpdateForLoanCounter, newLoanCounter, newLoanProductCounter);
        } else {
            final List<Loan> loansToUpdateForLoanCounter = this.loanRepository
                    .getClientOrJLGLoansDisbursedAfter(actualDisbursementDate.toDate(), loan.getClientId());
            final Integer newLoanCounter = getNewClientOrJLGLoanCounter(loan);
            final Integer newLoanProductCounter = getNewClientOrJLGLoanProductCounter(loan);
            updateLoanCounter(loan, loansToUpdateForLoanCounter, newLoanCounter, newLoanProductCounter);
        }
    }

    private Integer getNewGroupLoanCounter(final Loan loan) {

        Integer maxClientLoanCounter = this.loanRepository.getMaxGroupLoanCounter(loan.getGroupId(), AccountType.GROUP.getValue());
        if (maxClientLoanCounter == null) {
            maxClientLoanCounter = 1;
        } else {
            maxClientLoanCounter = maxClientLoanCounter + 1;
        }
        return maxClientLoanCounter;
    }

    private Integer getNewGroupLoanProductCounter(final Loan loan) {

        Integer maxLoanProductLoanCounter = this.loanRepository.getMaxGroupLoanProductCounter(loan.loanProduct().getId(), loan.getGroupId(),
                AccountType.GROUP.getValue());
        if (maxLoanProductLoanCounter == null) {
            maxLoanProductLoanCounter = 1;
        } else {
            maxLoanProductLoanCounter = maxLoanProductLoanCounter + 1;
        }
        return maxLoanProductLoanCounter;
    }

    private void updateLoanCounter(final Loan loan, final List<Loan> loansToUpdateForLoanCounter, Integer newLoanCounter,
            Integer newLoanProductCounter) {

        final boolean includeInBorrowerCycle = loan.loanProduct().isIncludeInBorrowerCycle();
        for (final Loan loanToUpdate : loansToUpdateForLoanCounter) {
            // Update client loan counter if loan product includeInBorrowerCycle
            // is true
            if (loanToUpdate.loanProduct().isIncludeInBorrowerCycle()) {
                Integer currentLoanCounter = loanToUpdate.getCurrentLoanCounter() == null ? 1 : loanToUpdate.getCurrentLoanCounter();
                if (newLoanCounter > currentLoanCounter) {
                    newLoanCounter = currentLoanCounter;
                }
                loanToUpdate.updateClientLoanCounter(++currentLoanCounter);
            }

            if (loanToUpdate.loanProduct().getId().equals(loan.loanProduct().getId())) {
                Integer loanProductLoanCounter = loanToUpdate.getLoanProductLoanCounter();
                if (newLoanProductCounter > loanProductLoanCounter) {
                    newLoanProductCounter = loanProductLoanCounter;
                }
                loanToUpdate.updateLoanProductLoanCounter(++loanProductLoanCounter);
            }
        }

        if (includeInBorrowerCycle) {
            loan.updateClientLoanCounter(newLoanCounter);
        } else {
            loan.updateClientLoanCounter(null);
        }
        loan.updateLoanProductLoanCounter(newLoanProductCounter);
        this.loanRepository.save(loansToUpdateForLoanCounter);
    }

    private Integer getNewClientOrJLGLoanCounter(final Loan loan) {

        Integer maxClientLoanCounter = this.loanRepository.getMaxClientOrJLGLoanCounter(loan.getClientId());
        if (maxClientLoanCounter == null) {
            maxClientLoanCounter = 1;
        } else {
            maxClientLoanCounter = maxClientLoanCounter + 1;
        }
        return maxClientLoanCounter;
    }

    private Integer getNewClientOrJLGLoanProductCounter(final Loan loan) {

        Integer maxLoanProductLoanCounter = this.loanRepository.getMaxClientOrJLGLoanProductCounter(loan.loanProduct().getId(),
                loan.getClientId());
        if (maxLoanProductLoanCounter == null) {
            maxLoanProductLoanCounter = 1;
        } else {
            maxLoanProductLoanCounter = maxLoanProductLoanCounter + 1;
        }
        return maxLoanProductLoanCounter;
    }

    private void updateLoanCycleCounter(final List<Loan> loansToUpdate, final Loan loan) {

        final Integer currentLoancounter = loan.getCurrentLoanCounter();
        final Integer currentLoanProductCounter = loan.getLoanProductLoanCounter();

        for (final Loan loanToUpdate : loansToUpdate) {
            if (loan.loanProduct().isIncludeInBorrowerCycle()) {
                Integer runningLoancounter = loanToUpdate.getCurrentLoanCounter();
                if (runningLoancounter > currentLoancounter) {
                    loanToUpdate.updateClientLoanCounter(--runningLoancounter);
                }
            }
            if (loan.loanProduct().getId().equals(loanToUpdate.loanProduct().getId())) {
                Integer runningLoanProductCounter = loanToUpdate.getLoanProductLoanCounter();
                if (runningLoanProductCounter > currentLoanProductCounter) {
                    loanToUpdate.updateLoanProductLoanCounter(--runningLoanProductCounter);
                }
            }
        }
        this.loanRepository.save(loansToUpdate);
    }

    private void validateForUpdate(final Loan loan, final LoanCharge loanCharge) {
        if (!loan.status().isSubmittedAndPendingApproval()) { throw new LoanChargeCannotBeUpdatedException(
                LOAN_CHARGE_CANNOT_BE_UPDATED_REASON.LOAN_NOT_IN_SUBMITTED_AND_PENDING_APPROVAL_STAGE, loanCharge.getId()); }
        if (loan.isGLIMLoan() && !loanCharge.isUpfrontFee()) { throw new LoanChargeCannotBeUpdatedException(
                LOAN_CHARGE_CANNOT_BE_UPDATED_REASON.GLIM_LOAN, loanCharge.getId()); }
    }

    @Override
    @Transactional
    public void updateScheduleDates(final Long loanId, HolidayDetailDTO holidayDetailDTO, final LocalDate recalculateFrom) {
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        final List<Holiday> holidaysToBeProcessForLoan = this.holidayRepository.findByOfficeIdAndGreaterThanDate(loan.getOfficeId(),
                loan.getDisbursementDate().toDate());
        holidayDetailDTO = new HolidayDetailDTO(holidayDetailDTO, holidaysToBeProcessForLoan);
        final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom,
                holidayDetailDTO);
        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();
        final AppUser currentUser = getAppUserIfPresent();
        Boolean runJournalEntries = false;
        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            loan.setHelpers(null, this.loanSummaryWrapper, this.transactionProcessingStrategy);
            final ChangedTransactionDetail changedTransactionDetail = loan.recalculateScheduleFromLastTransaction(scheduleGeneratorDTO,
                    existingTransactionIds, existingReversedTransactionIds, currentUser);
            if (changedTransactionDetail != null) {
                runJournalEntries = true;
                for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                    this.loanTransactionRepository.save(mapEntry.getValue());
                    // update loan with references to the newly created
                    // transactions
                    loan.getLoanTransactions().add(mapEntry.getValue());
                    this.accountTransfersWritePlatformService.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
                }
            }
            if (loan.isOpen()) {
                this.loanAccountDomainService.createAndSaveLoanScheduleArchive(loan, scheduleGeneratorDTO);
            }
        } else {
            updateScheduleDates(loan, scheduleGeneratorDTO);
        }
        saveLoanWithDataIntegrityViolationChecks(loan);
        if (runJournalEntries) {
            postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);
        }
    }

    private void updateScheduleDates(final Loan loan, final ScheduleGeneratorDTO scheduleGeneratorDTO) {
        final LoanApplicationTerms loanApplicationTerms = loan.constructLoanApplicationTerms(scheduleGeneratorDTO);
        final LocalDate currentDate = DateUtils.getLocalDateOfTenant();
        updateScheduleDates(loan, scheduleGeneratorDTO, loanApplicationTerms, currentDate);
    }

    @Override
    public void updateScheduleDates(final Loan loan, final ScheduleGeneratorDTO scheduleGeneratorDTO,
            final LoanApplicationTerms loanApplicationTerms, final LocalDate currentDate) {
        LocalDate actualRepaymentDate = loanApplicationTerms.getExpectedDisbursementDate();
        LocalDate lastActualRepaymentDate = actualRepaymentDate;
        LocalDate lastScheduledDate = actualRepaymentDate;
        boolean isFirstRepayment = true;
        for (final LoanRepaymentScheduleInstallment installment : loan.getRepaymentScheduleInstallments()) {
            // this will generate the next schedule due date and allows to
            // process the installment only if recalculate from date is
            // greater than due date

            isFirstRepayment = installment.getInstallmentNumber() == 1 ? true : false;
            final LocalDate previousRepaymentDate = lastActualRepaymentDate;
            actualRepaymentDate = this.scheduledDateGenerator.generateNextRepaymentDate(previousRepaymentDate, loanApplicationTerms,
                    isFirstRepayment);
            AdjustedDateDetailsDTO adjustedDateDetailsDTO = this.scheduledDateGenerator.adjustRepaymentDate(actualRepaymentDate,
                    loanApplicationTerms, scheduleGeneratorDTO.getHolidayDetailDTO());
            lastActualRepaymentDate = adjustedDateDetailsDTO.getChangedActualRepaymentDate();
            LocalDate dueDate = adjustedDateDetailsDTO.getChangedScheduleDate();
            while (loanApplicationTerms.getLoanTermVariations().hasDueDateVariation(dueDate)) {
                final LoanTermVariationsData variation = loanApplicationTerms.getLoanTermVariations().nextDueDateVariation();
                if (!variation.isSpecificToInstallment()) {
                    lastActualRepaymentDate = variation.getDateValue();
                }
                dueDate = variation.getDateValue();
                adjustedDateDetailsDTO = this.scheduledDateGenerator.adjustRepaymentDate(dueDate, loanApplicationTerms,
                        scheduleGeneratorDTO.getHolidayDetailDTO());
                if (!adjustedDateDetailsDTO.getChangedActualRepaymentDate().isEqual(dueDate)) {
                    lastActualRepaymentDate = adjustedDateDetailsDTO.getChangedActualRepaymentDate();
                }
                dueDate = adjustedDateDetailsDTO.getChangedScheduleDate();
            }
            if (installment.getDueDate().isBefore(currentDate)) {
                lastScheduledDate = dueDate;
            } else {
                installment.updateFromDate(lastScheduledDate);
                lastScheduledDate = dueDate;
                installment.updateDueDate(lastScheduledDate);
            }
        }
    }

    private void checkForProductMixRestrictions(final Loan loan) {

        final List<Long> activeLoansLoanProductIds;
        final Long productId = loan.loanProduct().getId();

        if (loan.isGroupLoan()) {
            activeLoansLoanProductIds = this.loanRepository.findActiveLoansLoanProductIdsByGroup(loan.getGroupId(),
                    LoanStatus.ACTIVE.getValue());
        } else {
            activeLoansLoanProductIds = this.loanRepository.findActiveLoansLoanProductIdsByClient(loan.getClientId(),
                    LoanStatus.ACTIVE.getValue());
        }
        checkForProductMixRestrictions(activeLoansLoanProductIds, productId, loan.loanProduct().productName());
    }

    private void checkForProductMixRestrictions(final List<Long> activeLoansLoanProductIds, final Long productId,
            final String productName) {

        if (!CollectionUtils.isEmpty(activeLoansLoanProductIds)) {
            final Collection<LoanProductData> restrictedPrdouctsList = this.loanProductReadPlatformService
                    .retrieveRestrictedProductsForMix(productId);
            for (final LoanProductData restrictedProduct : restrictedPrdouctsList) {
                if (activeLoansLoanProductIds.contains(
                        restrictedProduct.getId())) { throw new LoanDisbursalException(productName, restrictedProduct.getName()); }
            }
        }
    }

    private void checkClientOrGroupActive(final Loan loan) {
        final Client client = loan.client();
        if (client != null) {
            if (client.isNotActive()) { throw new ClientNotActiveException(client.getId()); }
        }
        final Group group = loan.group();
        if (group != null) {
            if (group.isNotActive()) { throw new GroupNotActiveException(group.getId()); }
        }
    }

    @Override
    public CommandProcessingResult undoWriteOff(final Long loanId) {
        final AppUser currentUser = getAppUserIfPresent();

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        checkClientOrGroupActive(loan);
        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();
        if (!loan.isClosedWrittenOff()) { throw new PlatformServiceUnavailableException(
                "error.msg.loan.status.not.written.off.update.not.allowed",
                "Loan :" + loanId + " update not allowed as loan status is not written off", loanId); }
        final LocalDate recalculateFrom = null;
        final LoanTransaction writeOffTransaction = loan.findWriteOffTransaction();
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_UNDO_WRITTEN_OFF,
                constructEntityMap(BUSINESS_ENTITY.LOAN_TRANSACTION, writeOffTransaction));

        final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);

        final ChangedTransactionDetail changedTransactionDetail = loan.undoWrittenOff(existingTransactionIds,
                existingReversedTransactionIds, scheduleGeneratorDTO, currentUser);
        if (changedTransactionDetail != null) {
            for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                this.loanTransactionRepository.save(mapEntry.getValue());
                this.accountTransfersWritePlatformService.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
            }
            final Map<BUSINESS_ENTITY, Object> changedTransactionEntityMap = constructEntityMap(BUSINESS_ENTITY.CHANGED_TRANSACTION_DETAIL,
                    changedTransactionDetail);
            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_UNDO_WRITTEN_OFF,
                    changedTransactionEntityMap);
        }
        saveLoanWithDataIntegrityViolationChecks(loan);

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);
        this.loanAccountDomainService.recalculateAccruals(loan);
        if (writeOffTransaction != null) {
            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_UNDO_WRITTEN_OFF,
                    constructEntityMap(BUSINESS_ENTITY.LOAN_TRANSACTION, writeOffTransaction));
        }
        return new CommandProcessingResultBuilder() //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .build();
    }

    private void validateMultiDisbursementData(final JsonCommand command, final LocalDate expectedDisbursementDate) {
        final String json = command.json();
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan");
        final JsonArray disbursementDataArray = command.arrayOfParameterNamed(LoanApiConstants.disbursementDataParameterName);
        if (disbursementDataArray == null || disbursementDataArray.size() == 0) {
            final String errorMessage = "For this loan product, disbursement details must be provided";
            throw new MultiDisbursementDataRequiredException(LoanApiConstants.disbursementDataParameterName, errorMessage);
        }
        final BigDecimal principal = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed("approvedLoanAmount", element);

        this.loanApplicationCommandFromApiJsonHelper.validateLoanMultiDisbursementdate(element, baseDataValidator, expectedDisbursementDate,
                principal);
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    private void validateForAddAndDeleteTranche(final Loan loan) {

        BigDecimal totalDisbursedAmount = BigDecimal.ZERO;
        final Collection<LoanDisbursementDetails> loanDisburseDetails = loan.getDisbursementDetails();
        for (final LoanDisbursementDetails disbursementDetails : loanDisburseDetails) {
            if (disbursementDetails.actualDisbursementDate() != null) {
                totalDisbursedAmount = totalDisbursedAmount.add(disbursementDetails.getAccountedPrincipal());
            }
        }
        if (totalDisbursedAmount.compareTo(loan.getApprovedPrincipal()) == 0) {
            final String errorMessage = "loan.disbursement.cannot.be.a.edited";
            throw new LoanMultiDisbursementException(errorMessage);
        }
    }

    @Override
    @Transactional
    public CommandProcessingResult addAndDeleteLoanDisburseDetails(final Long loanId, final JsonCommand command) {

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        checkClientOrGroupActive(loan);
        final Map<String, Object> actualChanges = new LinkedHashMap<>();
        final LocalDate expectedDisbursementDate = loan.getExpectedDisbursedOnLocalDate();
        if (!loan.loanProduct().isMultiDisburseLoan()) {
            final String errorMessage = "loan.product.does.not.support.multiple.disbursals";
            throw new LoanMultiDisbursementException(errorMessage);
        }
        if (loan.isSubmittedAndPendingApproval() || loan.isClosed() || loan.isClosedWrittenOff() || loan.status().isClosedObligationsMet()
                || loan.status().isOverpaid()) {
            final String errorMessage = "cannot.modify.tranches.if.loan.is.pendingapproval.closed.overpaid.writtenoff";
            throw new LoanMultiDisbursementException(errorMessage);
        }
        validateMultiDisbursementData(command, expectedDisbursementDate);

        validateForAddAndDeleteTranche(loan);

        final LocalDate recalculateFromDate = loan.updateDisbursementDetails(command, actualChanges);

        if (loan.getDisbursementDetails().isEmpty()) {
            final String errorMessage = "For this loan product, disbursement details must be provided";
            throw new MultiDisbursementDataRequiredException(LoanApiConstants.disbursementDataParameterName, errorMessage);
        }
        if (loan.getActiveTrancheCount() > loan.loanProduct().maxTrancheCount()) {
            final String errorMessage = "Number of tranche shouldn't be greter than " + loan.loanProduct().maxTrancheCount();
            throw new ExceedingTrancheCountException(LoanApiConstants.disbursementDataParameterName, errorMessage,
                    loan.loanProduct().maxTrancheCount(), loan.getActiveTrancheCount());
        }
        final LoanDisbursementDetails updateDetails = null;
        return processLoanDisbursementDetail(loan, loanId, command, updateDetails, recalculateFromDate);

    }

    private CommandProcessingResult processLoanDisbursementDetail(final Loan loan, final Long loanId, final JsonCommand command,
            final LoanDisbursementDetails loanDisbursementDetails, final LocalDate recalculateFromDate) {
        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();
        existingTransactionIds.addAll(loan.findExistingTransactionIds());
        existingReversedTransactionIds.addAll(loan.findExistingReversedTransactionIds());
        final Map<String, Object> changes = new LinkedHashMap<>();

        LocalDate recalculateFrom = null;
        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            recalculateFrom = recalculateFromDate;
        }
        final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);

        ChangedTransactionDetail changedTransactionDetail = null;
        final AppUser currentUser = getAppUserIfPresent();

        if (command.entityId() != null) {

            changedTransactionDetail = loan.updateDisbursementDateAndAmountForTranche(loanDisbursementDetails, command, changes,
                    scheduleGeneratorDTO, currentUser);
        } else {
            // BigDecimal setAmount = loan.getApprovedPrincipal();
            final Collection<LoanDisbursementDetails> loanDisburseDetails = loan.getDisbursementDetails();
            BigDecimal setAmount = BigDecimal.ZERO;
            for (final LoanDisbursementDetails details : loanDisburseDetails) {
                if (details.actualDisbursementDate() != null) {
                    setAmount = setAmount.add(details.principal());
                }
            }

            loan.repaymentScheduleDetail().setPrincipal(setAmount);

            if (loan.isOpen() && loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
                changedTransactionDetail = loan.handleRegenerateRepaymentScheduleWithInterestRecalculation(scheduleGeneratorDTO,
                        currentUser);
            } else {
                loan.regenerateRepaymentSchedule(scheduleGeneratorDTO, currentUser);
                changedTransactionDetail = loan.processTransactions();
            }
        }

        saveAndFlushLoanWithDataIntegrityViolationChecks(loan);

        if (changedTransactionDetail != null) {
            for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                this.loanTransactionRepository.save(mapEntry.getValue());
                // update loan with references to the newly created
                // transactions
                loan.getLoanTransactions().add(mapEntry.getValue());
                this.accountTransfersWritePlatformService.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
            }
            final Map<BUSINESS_ENTITY, Object> changedTransactionEntityMap = constructEntityMap(BUSINESS_ENTITY.CHANGED_TRANSACTION_DETAIL,
                    changedTransactionDetail);
            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_DISBURSAL, changedTransactionEntityMap);
        }
        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            createLoanScheduleArchive(loan, scheduleGeneratorDTO);
        }
        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);
        this.loanAccountDomainService.recalculateAccruals(loan);
        return new CommandProcessingResultBuilder() //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .with(changes).build();
    }

    @Override
    @Transactional
    public CommandProcessingResult updateDisbursementDateAndAmountForTranche(final Long loanId, final Long disbursementId,
            final JsonCommand command) {

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        checkClientOrGroupActive(loan);
        checkOtherDisbursementDetailsWithDate(loan, disbursementId,
                command.DateValueOfParameterNamed(LoanApiConstants.updatedDisbursementDateParameterName));
        final LoanDisbursementDetails loanDisbursementDetails = loan.fetchLoanDisbursementsById(disbursementId);
        this.loanEventApiJsonValidator.validateUpdateDisbursementDateAndAmount(command.json(), loanDisbursementDetails);
        LocalDate recalculateFromDate = null;
        /*
         * picking up recalculateFrom date based on adding new tranches or
         * deleting existing tranches or updating the tranche date to back dated
         * or feature date In any case get the least expected tranche date.
         */
        final LocalDate updatedExpectedDisbursementDate = command
                .localDateValueOfParameterNamed(LoanApiConstants.updatedDisbursementDateParameterName);
        if (updatedExpectedDisbursementDate.isBefore(loanDisbursementDetails.expectedDisbursementDateAsLocalDate())) {
            recalculateFromDate = updatedExpectedDisbursementDate;
        } else {
            recalculateFromDate = loanDisbursementDetails.expectedDisbursementDateAsLocalDate();
        }

        return processLoanDisbursementDetail(loan, loanId, command, loanDisbursementDetails, recalculateFromDate);

    }

    public LoanTransaction disburseLoanAmountToSavings(final Long loanId, Long loanChargeId, final JsonCommand command,
            final boolean isChargeIdIncludedInJson) {

        final LoanTransaction transaction = null;

        this.loanEventApiJsonValidator.validateChargePaymentTransaction(command.json(), isChargeIdIncludedInJson);
        if (isChargeIdIncludedInJson) {
            loanChargeId = command.longValueOfParameterNamed("chargeId");
        }
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        checkClientOrGroupActive(loan);
        final LoanCharge loanCharge = retrieveLoanChargeBy(loanId, loanChargeId);

        // Charges may be waived only when the loan associated with them are
        // active
        if (!loan.status().isActive()) { throw new LoanChargeCannotBePayedException(LOAN_CHARGE_CANNOT_BE_PAYED_REASON.LOAN_INACTIVE,
                loanCharge.getId()); }

        // validate loan charge is not already paid or waived
        if (loanCharge.isWaived()) {
            throw new LoanChargeCannotBePayedException(LOAN_CHARGE_CANNOT_BE_PAYED_REASON.ALREADY_WAIVED, loanCharge.getId());
        } else if (loanCharge.isPaid()) { throw new LoanChargeCannotBePayedException(LOAN_CHARGE_CANNOT_BE_PAYED_REASON.ALREADY_PAID,
                loanCharge.getId()); }

        if (!loanCharge.getChargePaymentMode().isPaymentModeAccountTransfer()) { throw new LoanChargeCannotBePayedException(
                LOAN_CHARGE_CANNOT_BE_PAYED_REASON.CHARGE_NOT_ACCOUNT_TRANSFER, loanCharge.getId()); }

        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
        Integer loanInstallmentNumber = null;
        BigDecimal amount = loanCharge.amountOutstanding();
        if (loanCharge.isInstalmentFee()) {
            LoanInstallmentCharge chargePerInstallment = null;
            final LocalDate dueDate = command.localDateValueOfParameterNamed("dueDate");
            final Integer installmentNumber = command.integerValueOfParameterNamed("installmentNumber");
            if (dueDate != null) {
                chargePerInstallment = loanCharge.getInstallmentLoanCharge(dueDate);
            } else if (installmentNumber != null) {
                chargePerInstallment = loanCharge.getInstallmentLoanCharge(installmentNumber);
            }
            if (chargePerInstallment == null) {
                chargePerInstallment = loanCharge.getUnpaidInstallmentLoanCharge();
            }
            if (chargePerInstallment.isWaived()) {
                throw new LoanChargeCannotBePayedException(LOAN_CHARGE_CANNOT_BE_PAYED_REASON.ALREADY_WAIVED, loanCharge.getId());
            } else if (chargePerInstallment
                    .isPaid()) { throw new LoanChargeCannotBePayedException(LOAN_CHARGE_CANNOT_BE_PAYED_REASON.ALREADY_PAID,
                            loanCharge.getId()); }
            loanInstallmentNumber = chargePerInstallment.getRepaymentInstallment().getInstallmentNumber();
            amount = chargePerInstallment.getAmountOutstanding();
        }

        final PortfolioAccountData portfolioAccountData = this.accountAssociationsReadPlatformService.retriveLoanLinkedAssociation(loanId);
        if (portfolioAccountData == null) {
            final String errorMessage = "Charge with id:" + loanChargeId + " requires linked savings account for payment";
            throw new LinkedAccountRequiredException("loanCharge.pay", errorMessage, loanChargeId);
        }
        final SavingsAccount fromSavingsAccount = null;
        final boolean isRegularTransaction = true;
        final boolean isExceptionForBalanceCheck = false;
        final AccountTransferDTO accountTransferDTO = new AccountTransferDTO(transactionDate, amount, PortfolioAccountType.SAVINGS,
                PortfolioAccountType.LOAN, portfolioAccountData.accountId(), loanId, "Loan Charge Payment", locale, fmt, null, null,
                LoanTransactionType.CHARGE_PAYMENT.getValue(), loanChargeId, loanInstallmentNumber,
                AccountTransferType.CHARGE_PAYMENT.getValue(), null, null, null, null, null, fromSavingsAccount, isRegularTransaction,
                isExceptionForBalanceCheck);
        this.accountTransfersWritePlatformService.transferFunds(accountTransferDTO);

        return transaction;
    }

    @Transactional
    @Override
    public void recalculateInterest(final long loanId, final LocalDate penaltiesRunOnDate, final LocalDate penaltiesBrokenPeriodOnDate) {
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        final LocalDate recalculateFrom = loan.fetchInterestRecalculateFromDate();
        final AppUser currentUser = getAppUserIfPresent();
        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_INTEREST_RECALCULATION,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));
        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();

        final ScheduleGeneratorDTO generatorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);

        final boolean isPenaltiesApplied = this.loanOverdueChargeService.updateAndApplyOverdueChargesForLoan(loan, penaltiesRunOnDate,
                penaltiesBrokenPeriodOnDate);

        final ChangedTransactionDetail changedTransactionDetail = loan.recalculateScheduleFromLastTransaction(generatorDTO,
                existingTransactionIds, existingReversedTransactionIds, currentUser);

        // creating original schedule while picking up loans from delete me
        // table
        final Boolean ignoreOverdue = ThreadLocalContextUtil.getIgnoreOverdue();
        if (isPenaltiesApplied || (ignoreOverdue != null && ignoreOverdue)) {
            this.loanAccountDomainService.createAndSaveLoanScheduleArchive(loan, generatorDTO);
        }

        saveLoanWithDataIntegrityViolationChecks(loan);

        if (changedTransactionDetail != null) {
            for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                this.loanTransactionRepository.save(mapEntry.getValue());
                // update loan with references to the newly created
                // transactions
                loan.getLoanTransactions().add(mapEntry.getValue());
                this.accountTransfersWritePlatformService.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
            }
            final Map<BUSINESS_ENTITY, Object> changedTransactionEntityMap = constructEntityMap(BUSINESS_ENTITY.CHANGED_TRANSACTION_DETAIL,
                    changedTransactionDetail);
            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_INTEREST_RECALCULATION,
                    changedTransactionEntityMap);
        }
        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);
        // Commented the recalculate Accruals it needs to be taken care by the
        // Periodic Accrual Job.
        // this.loanAccountDomainService.recalculateAccruals(loan);
        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_INTEREST_RECALCULATION,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));
    }

    @Override
    public CommandProcessingResult recoverFromGuarantor(final Long loanId, final LocalDate guarantorRecoveryDate) {
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        this.guarantorDomainService.transaferFundsFromGuarantor(loan, guarantorRecoveryDate);
        return new CommandProcessingResultBuilder().withLoanId(loanId).build();
    }

    private void createLoanScheduleArchive(final Loan loan, final ScheduleGeneratorDTO scheduleGeneratorDTO) {
        this.loanAccountDomainService.createAndSaveLoanScheduleArchive(loan, scheduleGeneratorDTO);

    }

    private void regenerateScheduleOnDisbursement(final JsonCommand command, final Loan loan, final boolean recalculateSchedule,
            final ScheduleGeneratorDTO scheduleGeneratorDTO, final LocalDate nextPossibleRepaymentDate,
            final Date rescheduledRepaymentDate) {
        final AppUser currentUser = getAppUserIfPresent();
        final LocalDate actualDisbursementDate = command.localDateValueOfParameterNamed("actualDisbursementDate");
        final BigDecimal emiAmount = command.bigDecimalValueOfParameterNamed(LoanApiConstants.emiAmountParameterName);
        loan.regenerateScheduleOnDisbursement(scheduleGeneratorDTO, recalculateSchedule, actualDisbursementDate, emiAmount, currentUser,
                nextPossibleRepaymentDate, rescheduledRepaymentDate);
    }

    @Override
    @Transactional
    public CommandProcessingResult makeLoanRefund(final Long loanId, final JsonCommand command) {
        // TODO Auto-generated method stub

        this.loanEventApiJsonValidator.validateNewRefundTransaction(command.json());

        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");

        // checkRefundDateIsAfterAtLeastOneRepayment(loanId, transactionDate);

        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");
        checkIfLoanIsPaidInAdvance(loanId, transactionAmount);

        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put("transactionDate", command.stringValueOfParameterNamed("transactionDate"));
        changes.put("transactionAmount", command.stringValueOfParameterNamed("transactionAmount"));
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());

        final String noteText = command.stringValueOfParameterNamed("note");
        if (StringUtils.isNotBlank(noteText)) {
            changes.put("note", noteText);
        }

        final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);

        final CommandProcessingResultBuilder commandProcessingResultBuilder = new CommandProcessingResultBuilder();

        this.loanAccountDomainService.makeRefundForActiveLoan(loanId, commandProcessingResultBuilder, transactionDate, transactionAmount,
                paymentDetail, noteText, null);

        return commandProcessingResultBuilder.withCommandId(command.commandId()) //
                .withLoanId(loanId) //
                .with(changes) //
                .build();

    }

    private void checkIfLoanIsPaidInAdvance(final Long loanId, final BigDecimal transactionAmount) {
        BigDecimal overpaid = this.loanReadPlatformService.retrieveTotalPaidInAdvance(loanId).getPaidInAdvance();

        if (overpaid == null || overpaid.equals(new BigDecimal(0)) || transactionAmount.floatValue() > overpaid.floatValue()) {
            if (overpaid == null) {
                overpaid = BigDecimal.ZERO;
            }
            throw new InvalidPaidInAdvanceAmountException(overpaid.toPlainString());
        }
    }

    private AppUser getAppUserIfPresent() {
        AppUser user = null;
        if (this.context != null) {
            user = this.context.getAuthenticatedUserIfPresent();
        }
        return user;
    }

    private Map<BUSINESS_ENTITY, Object> constructEntityMap(final BUSINESS_ENTITY entityEvent, final Object entity) {
        final Map<BUSINESS_ENTITY, Object> map = new HashMap<>(1);
        map.put(entityEvent, entity);
        return map;
    }

    private Map<BUSINESS_ENTITY, Object> constructEntityMap(final Object entity, final JsonCommand command) {
        final Map<BUSINESS_ENTITY, Object> map = new HashMap<>(2);
        map.put(BUSINESS_ENTITY.LOAN, entity);
        map.put(BUSINESS_ENTITY.JSON_COMMAND, command);
        return map;
    }

    @Override
    @Transactional
    public CommandProcessingResult undoLastLoanDisbursal(final Long loanId, final JsonCommand command) {
        final AppUser currentUser = getAppUserIfPresent();

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        final LocalDate recalculateFromDate = loan.getLastRepaymentDate();
        validateIsMultiDisbursalLoanAndDisbursedMoreThanOneTranche(loan);
        checkClientOrGroupActive(loan);
        final List<Long> existingTransactionIds = new ArrayList<>(loan.findExistingTransactionIds());
        final List<Long> existingReversedTransactionIds = new ArrayList<>(loan.findExistingReversedTransactionIds());
        final Map<String, Object> changes = new LinkedHashMap<>();

        this.businessEventNotifierService.notifyBusinessEventToBeExecuted(BUSINESS_EVENTS.LOAN_UNDO_LASTDISBURSAL,
                constructEntityMap(BUSINESS_ENTITY.LOAN, loan));

        final MonetaryCurrency currency = loan.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);

        final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFromDate);
        final List<Long> rescheduleVariations = this.loanRescheduleRequestReadPlatformService
                .retriveActiveLoanRescheduleRequestVariations(loanId);

        final ChangedTransactionDetail changedTransactionDetail = loan.undoLastDisbursal(scheduleGeneratorDTO, currentUser, changes,
                rescheduleVariations);
        if (!changes.isEmpty()) {
            saveAndFlushLoanWithDataIntegrityViolationChecks(loan);
            String noteText = null;
            if (command.hasParameter("note")) {
                noteText = command.stringValueOfParameterNamed("note");
                if (StringUtils.isNotBlank(noteText)) {
                    final Note note = Note.loanNote(loan, noteText);
                    this.noteRepository.save(note);
                }
            }
            if (changedTransactionDetail != null) {
                for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                    this.loanTransactionRepository.save(mapEntry.getValue());
                    // update loan with references to the newly created
                    // transactions
                    loan.getLoanTransactions().add(mapEntry.getValue());
                    this.accountTransfersWritePlatformService.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
                }
            }
            final boolean isAccountTransfer = false;
            final Map<String, Object> accountingBridgeData = loan.deriveAccountingBridgeData(applicationCurrency.toData(),
                    existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
            this.journalEntryWritePlatformService.createJournalEntriesForLoan(accountingBridgeData);
            this.loanAccountDomainService.recalculateAccruals(loan);
            if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
                this.loanAccountDomainService.createAndSaveLoanScheduleArchive(loan, scheduleGeneratorDTO);
            }
            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_UNDO_LASTDISBURSAL,
                    constructEntityMap(BUSINESS_ENTITY.LOAN, loan));
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(loan.getId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loanId) //
                .with(changes) //
                .build();
    }

    @Override
    @Transactional
    public CommandProcessingResult forecloseLoan(final Long loanId, final JsonCommand command) {
        final String json = command.json();
        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        final LocalDate transactionDate = this.fromApiJsonHelper.extractLocalDateNamed(LoanApiConstants.transactionDateParamName, element);
        this.loanEventApiJsonValidator.validateLoanForeclosure(command.json());
        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put("transactionDate", transactionDate);

        final String noteText = this.fromApiJsonHelper.extractStringNamed(LoanApiConstants.noteParamName, element);
        final boolean isAccountTransfer = false;
        final boolean isLoanToLoanTransfer = false;
        this.loanAccountDomainService.foreCloseLoan(loan, transactionDate, noteText, isAccountTransfer, isLoanToLoanTransfer, changes);

        final CommandProcessingResultBuilder commandProcessingResultBuilder = new CommandProcessingResultBuilder();
        return commandProcessingResultBuilder.withLoanId(loanId) //
                .with(changes) //
                .build();
    }

    @Transactional
    @Override
    public void updateLoanAsNPA(final Long loanId) {

        final Collection<Integer> typeForAddReceivale = new ArrayList<>();
        typeForAddReceivale.add(LoanTransactionType.ACCRUAL.getValue());
        typeForAddReceivale.add(LoanTransactionType.REFUND_FOR_ACTIVE_LOAN.getValue());

        final Collection<Integer> typeForSubtractReceivale = new ArrayList<>();
        typeForSubtractReceivale.add(LoanTransactionType.REPAYMENT.getValue());
        typeForSubtractReceivale.add(LoanTransactionType.WAIVE_INTEREST.getValue());
        typeForSubtractReceivale.add(LoanTransactionType.WAIVE_CHARGES.getValue());
        typeForSubtractReceivale.add(LoanTransactionType.CHARGE_PAYMENT.getValue());

        final boolean npaStatus = true;
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        loan.setNpa(npaStatus);
        final MonetaryCurrency currency = loan.getCurrency();
        final List<Long> existingTransactionIds = new ArrayList<>(loan.findExistingTransactionIds());
        final List<Long> existingReversedTransactionIds = new ArrayList<>(loan.findExistingReversedTransactionIds());
        final LoanTransaction accrualTransaction = LoanTransaction.accrualSuspense(loan, loan.getOffice(), Money.zero(currency),
                Money.zero(currency), Money.zero(currency), Money.zero(currency));
        final boolean addToTransactions = true;
        loan.updateTransactionDetails(typeForAddReceivale, typeForSubtractReceivale, accrualTransaction, addToTransactions);

        if (accrualTransaction != null && accrualTransaction.getAmount(currency).isGreaterThanZero()) {
            this.loanTransactionRepository.save(accrualTransaction);
        }
        this.loanRepository.save(loan);

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);

    }

    @Transactional
    @Override
    public void updateLoanAsNonNPA(final Long loanId) {

        final Collection<Integer> typeForAddReceivale = new ArrayList<>();
        typeForAddReceivale.add(LoanTransactionType.ACCRUAL_SUSPENSE.getValue());

        final Collection<Integer> typeForSubtractReceivale = new ArrayList<>();
        typeForSubtractReceivale.add(LoanTransactionType.ACCRUAL_SUSPENSE_REVERSE.getValue());
        typeForSubtractReceivale.add(LoanTransactionType.ACCRUAL_WRITEOFF.getValue());

        final boolean npaStatus = false;
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        loan.setNpa(npaStatus);
        final MonetaryCurrency currency = loan.getCurrency();
        final List<Long> existingTransactionIds = new ArrayList<>(loan.findExistingTransactionIds());
        final List<Long> existingReversedTransactionIds = new ArrayList<>(loan.findExistingReversedTransactionIds());
        final LoanTransaction accrualTransaction = LoanTransaction.accrualSuspenseReverse(loan, loan.getOffice(), Money.zero(currency),
                Money.zero(currency), Money.zero(currency), Money.zero(currency), DateUtils.getLocalDateOfTenant());
        final boolean addToTransactions = true;
        loan.updateTransactionDetails(typeForAddReceivale, typeForSubtractReceivale, accrualTransaction, addToTransactions);

        if (accrualTransaction != null && accrualTransaction.getAmount(currency).isGreaterThanZero()) {
            this.loanTransactionRepository.save(accrualTransaction);
        }
        this.loanRepository.save(loan);

        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);

    }

    private void validateIsMultiDisbursalLoanAndDisbursedMoreThanOneTranche(final Loan loan) {
        if (!loan.isMultiDisburmentLoan()) {
            final String errorMessage = "loan.product.does.not.support.multiple.disbursals.cannot.undo.last.disbursal";
            throw new LoanMultiDisbursementException(errorMessage);
        }
        Integer trancheDisbursedCount = 0;
        for (final LoanDisbursementDetails disbursementDetails : loan.getDisbursementDetails()) {
            if (disbursementDetails.actualDisbursementDate() != null) {
                trancheDisbursedCount++;
            }
        }
        if (trancheDisbursedCount <= 1) {
            final String errorMessage = "tranches.should.be.disbursed.more.than.one.to.undo.last.disbursal";
            throw new LoanMultiDisbursementException(errorMessage);
        }

    }

    private void syncExpectedDateWithActualDisbursementDate(final Loan loan, final LocalDate actualDisbursementDate) {
        LocalDate expectedDisbursementDate = null;
        if (loan.isMultiDisburmentLoan()) {
            for (final LoanDisbursementDetails loanDisbursementDetails : loan.getDisbursementDetails()) {
                if (loanDisbursementDetails.actualDisbursementDate() == null) {
                    expectedDisbursementDate = new LocalDate(loanDisbursementDetails.expectedDisbursementDate());
                    if ((!expectedDisbursementDate.isEqual(actualDisbursementDate))) {
                        final String erroMessage = "error.msg.tranche.actual.disbursement.date.does.not.match.with.expected.disbursal.date.of.tranche";
                        final String userMessage = "Actual disbursement date  (" + actualDisbursementDate + ") "
                                + "should be equal to Expected disbursal date (" + expectedDisbursementDate + ") for this tranche";
                        final Object defaultUserMessageArgs = null;
                        throw new DateMismatchException(actualDisbursementDate, expectedDisbursementDate, erroMessage, userMessage,
                                defaultUserMessageArgs);
                    }
                    break;
                }
            }

        } else {
            expectedDisbursementDate = loan.getExpectedDisbursedOnLocalDate();
            if (expectedDisbursementDate != null && (!expectedDisbursementDate.isEqual(actualDisbursementDate))) {
                final String erroMessage = "error.msg.actual.disbursement.date.does.not.match.with.expected.disbursal.date";
                final String userMessage = "Actual disbursement date  (" + actualDisbursementDate + ") "
                        + "should be equal to Expected disbursal date (" + expectedDisbursementDate + ")";
                final Object defaultUserMessageArgs = null;
                throw new DateMismatchException(actualDisbursementDate, expectedDisbursementDate, erroMessage, userMessage,
                        defaultUserMessageArgs);
            }
        }

    }

    private void checkOtherDisbursementDetailsWithDate(final Loan loan, final Long disbursementId, final Date updatedDisbursementDate) {
        for (final LoanDisbursementDetails loanDisbursementDetails : loan.getDisbursementDetails()) {
            if (loanDisbursementDetails.expectedDisbursementDate().equals(updatedDisbursementDate) && !loanDisbursementDetails.getId()
                    .equals(disbursementId)) { throw new LoanDisbursementDateException("Loan disbursement details with date - "
                            + updatedDisbursementDate + " for loan - " + loan.getId() + " already exists.", loan.getId(),
                            updatedDisbursementDate.toString()); }
        }
    }

    private void updateLoanOfficer(final Long loanId, final Long loanOfficerId) {
        final String sql = "update m_loan SET m_loan.loan_officer_id = '" + loanOfficerId + "' where m_loan.id = " + loanId;
        this.jdbcTemplate.execute(sql);
    }

    private void validateForReasignLoanOfficerForLoans(final LocalDate dateOfLoanOfficerAssignment,
            final LoanOfficerAssignmentHistoryData loanOfficerAssignmentHistory, final Long loanId, final Long fromLoanOfficerId) {
        final LocalDate loanSubmitedOnDate = loanOfficerAssignmentHistory.getLoanSubmittedOnDate();

        if (!loanOfficerAssignmentHistory
                .hasLoanOfficer(fromLoanOfficerId)) { throw new LoanOfficerAssignmentException(loanId, fromLoanOfficerId); }
        if (loanSubmitedOnDate.isAfter(dateOfLoanOfficerAssignment)) {
            final String errorMessage = "The Loan Officer assignment date (" + dateOfLoanOfficerAssignment.toString()
                    + ") cannot be before loan submitted date (" + loanSubmitedOnDate.toString() + ").";

            throw new LoanOfficerAssignmentDateException("cannot.be.before.loan.submittal.date", errorMessage, dateOfLoanOfficerAssignment,
                    loanSubmitedOnDate);
        } else if (DateUtils.getLocalDateOfTenant().isBefore(dateOfLoanOfficerAssignment)) {

            final String errorMessage = "The Loan Officer assignment date (" + dateOfLoanOfficerAssignment + ") cannot be in the future.";

            throw new LoanOfficerAssignmentDateException("cannot.be.a.future.date", errorMessage, dateOfLoanOfficerAssignment);
        } else if (loanOfficerAssignmentHistory.getLatestHistoryRecordStartdate() != null
                && loanOfficerAssignmentHistory.hasHistoryStartDateBefore(dateOfLoanOfficerAssignment)) {
            final String errorMessage = "Loan with identifier " + loanId + " was already assigned before date "
                    + dateOfLoanOfficerAssignment;
            throw new LoanOfficerAssignmentDateException("is.before.last.assignment.date", errorMessage, loanId,
                    dateOfLoanOfficerAssignment);
        }

    }
}