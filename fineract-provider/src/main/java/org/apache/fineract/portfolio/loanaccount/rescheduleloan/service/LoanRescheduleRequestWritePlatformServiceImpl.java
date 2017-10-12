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
package org.apache.fineract.portfolio.loanaccount.rescheduleloan.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.MoneyHelper;
import org.apache.fineract.portfolio.account.service.AccountTransfersWritePlatformService;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarHistory;
import org.apache.fineract.portfolio.calendar.domain.CalendarHistoryRepository;
import org.apache.fineract.portfolio.common.domain.DayOfWeekType;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.data.LoanTermVariationsData;
import org.apache.fineract.portfolio.loanaccount.data.ScheduleGeneratorDTO;
import org.apache.fineract.portfolio.loanaccount.domain.ChangedTransactionDetail;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanLifecycleStateMachine;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallmentRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleTransactionProcessorFactory;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRescheduleRequestToTermVariationMapping;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanaccount.domain.LoanSummaryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTermVariationType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTermVariations;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRepository;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanScheduleDTO;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.DefaultScheduledDateGenerator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanApplicationTerms;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanRepaymentScheduleHistory;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanRepaymentScheduleHistoryRepository;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleGenerator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleGeneratorFactory;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModel;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModelPeriod;
import org.apache.fineract.portfolio.loanaccount.loanschedule.service.LoanScheduleHistoryWritePlatformService;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.RescheduleLoansApiConstants;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.data.LoanRescheduleRequestDataValidator;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleRequest;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleRequestRepository;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.exception.LoanRescheduleRequestNotFoundException;
import org.apache.fineract.portfolio.loanaccount.service.LoanAssembler;
import org.apache.fineract.portfolio.loanaccount.service.LoanOverdueChargeService;
import org.apache.fineract.portfolio.loanaccount.service.LoanUtilService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanRescheduleRequestWritePlatformServiceImpl implements LoanRescheduleRequestWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(LoanRescheduleRequestWritePlatformServiceImpl.class);

    private final CodeValueRepositoryWrapper codeValueRepositoryWrapper;
    private final PlatformSecurityContext platformSecurityContext;
    private final LoanRescheduleRequestDataValidator loanRescheduleRequestDataValidator;
    private final LoanRescheduleRequestRepository loanRescheduleRequestRepository;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository;
    private final LoanRepaymentScheduleHistoryRepository loanRepaymentScheduleHistoryRepository;
    private final LoanScheduleHistoryWritePlatformService loanScheduleHistoryWritePlatformService;
    private final LoanTransactionRepository loanTransactionRepository;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final LoanRepository loanRepository;
    private final LoanAssembler loanAssembler;
    private final LoanUtilService loanUtilService;
    private final LoanRepaymentScheduleTransactionProcessorFactory loanRepaymentScheduleTransactionProcessorFactory;
    private final LoanScheduleGeneratorFactory loanScheduleFactory;
    private final LoanSummaryWrapper loanSummaryWrapper;
    private final AccountTransfersWritePlatformService accountTransfersWritePlatformService;
    private final DefaultScheduledDateGenerator scheduledDateGenerator = new DefaultScheduledDateGenerator();
    private final LoanAccountDomainService loanAccountDomainService;
    private final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository;
    private final CalendarHistoryRepository calendarHistoryRepository;
    private final GlimLoanRescheduleService glimLoanRescheduleService;
    private final LoanOverdueChargeService loanOverdueChargeService;

    /**
     * LoanRescheduleRequestWritePlatformServiceImpl constructor
     * 
     * @return void
     **/
    @Autowired
    public LoanRescheduleRequestWritePlatformServiceImpl(final CodeValueRepositoryWrapper codeValueRepositoryWrapper,
            final PlatformSecurityContext platformSecurityContext,
            final LoanRescheduleRequestDataValidator loanRescheduleRequestDataValidator,
            final LoanRescheduleRequestRepository loanRescheduleRequestRepository,
            final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository,
            final LoanRepaymentScheduleHistoryRepository loanRepaymentScheduleHistoryRepository,
            final LoanScheduleHistoryWritePlatformService loanScheduleHistoryWritePlatformService,
            final LoanTransactionRepository loanTransactionRepository,
            final JournalEntryWritePlatformService journalEntryWritePlatformService, final LoanRepository loanRepository,
            final LoanAssembler loanAssembler, final LoanUtilService loanUtilService,
            final LoanRepaymentScheduleTransactionProcessorFactory loanRepaymentScheduleTransactionProcessorFactory,
            final LoanScheduleGeneratorFactory loanScheduleFactory, final LoanSummaryWrapper loanSummaryWrapper,
            final AccountTransfersWritePlatformService accountTransfersWritePlatformService,
            final LoanAccountDomainService loanAccountDomainService,
            final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository,
            final CalendarHistoryRepository calendarHistoryRepository, final GlimLoanRescheduleService glimLoanRescheduleService,
            final LoanOverdueChargeService loanOverdueChargeService) {
        this.codeValueRepositoryWrapper = codeValueRepositoryWrapper;
        this.platformSecurityContext = platformSecurityContext;
        this.loanRescheduleRequestDataValidator = loanRescheduleRequestDataValidator;
        this.loanRescheduleRequestRepository = loanRescheduleRequestRepository;
        this.applicationCurrencyRepository = applicationCurrencyRepository;
        this.loanRepaymentScheduleHistoryRepository = loanRepaymentScheduleHistoryRepository;
        this.loanScheduleHistoryWritePlatformService = loanScheduleHistoryWritePlatformService;
        this.loanTransactionRepository = loanTransactionRepository;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
        this.loanRepository = loanRepository;
        this.loanAssembler = loanAssembler;
        this.loanUtilService = loanUtilService;
        this.loanRepaymentScheduleTransactionProcessorFactory = loanRepaymentScheduleTransactionProcessorFactory;
        this.loanScheduleFactory = loanScheduleFactory;
        this.loanSummaryWrapper = loanSummaryWrapper;
        this.accountTransfersWritePlatformService = accountTransfersWritePlatformService;
        this.loanAccountDomainService = loanAccountDomainService;
        this.repaymentScheduleInstallmentRepository = repaymentScheduleInstallmentRepository;
        this.calendarHistoryRepository = calendarHistoryRepository;
        this.glimLoanRescheduleService = glimLoanRescheduleService;
        this.loanOverdueChargeService = loanOverdueChargeService;
    }

    /**
     * create a new instance of the LoanRescheduleRequest object from the
     * JsonCommand object and persist
     * 
     * @return CommandProcessingResult object
     **/
    @Override
    @Transactional
    public CommandProcessingResult create(JsonCommand jsonCommand) {

        try {
            // get the loan id from the JsonCommand object
            final Long loanId = jsonCommand.longValueOfParameterNamed(RescheduleLoansApiConstants.loanIdParamName);

            // use the loan id to get a Loan entity object
            final Loan loan = this.loanAssembler.assembleFrom(loanId);

            // validate the request in the JsonCommand object passed as
            // parameter
            boolean isBulkCreateAndApprove = false;
            this.loanRescheduleRequestDataValidator.validateForCreateAction(jsonCommand, loan, isBulkCreateAndApprove);

            // get the reschedule reason code value id from the JsonCommand
            // object
            final Long rescheduleReasonId = jsonCommand.longValueOfParameterNamed(RescheduleLoansApiConstants.rescheduleReasonIdParamName);

            // use the reschedule reason code value id to get a CodeValue entity
            // object
            final CodeValue rescheduleReasonCodeValue = this.codeValueRepositoryWrapper.findOneWithNotFoundDetection(rescheduleReasonId);

            // get the grace on principal integer value from the JsonCommand
            // object
            final Integer graceOnPrincipal = jsonCommand
                    .integerValueOfParameterNamed(RescheduleLoansApiConstants.graceOnPrincipalParamName);

            // get the grace on interest integer value from the JsonCommand
            // object
            final Integer graceOnInterest = jsonCommand.integerValueOfParameterNamed(RescheduleLoansApiConstants.graceOnInterestParamName);

            // get the extra terms to be added at the end of the new schedule
            // from the JsonCommand object
            final Integer extraTerms = jsonCommand.integerValueOfParameterNamed(RescheduleLoansApiConstants.extraTermsParamName);

            // get the new interest rate that would be applied to the new loan
            // schedule
            final BigDecimal interestRate = jsonCommand
                    .bigDecimalValueOfParameterNamed(RescheduleLoansApiConstants.newInterestRateParamName);

            // get the reschedule reason comment text from the JsonCommand
            // object
            final String rescheduleReasonComment = jsonCommand
                    .stringValueOfParameterNamed(RescheduleLoansApiConstants.rescheduleReasonCommentParamName);

            // get the recalculate interest option
            final Boolean recalculateInterest = jsonCommand
                    .booleanObjectValueOfParameterNamed(RescheduleLoansApiConstants.recalculateInterestParamName);

            final BigDecimal installmentAmount = jsonCommand
                    .bigDecimalValueOfParameterNamed(RescheduleLoansApiConstants.newInstallmentAmountParamName);

            // initialize set the value to null
            Date submittedOnDate = null;

            
            // check if the parameter is in the JsonCommand object
            if (jsonCommand.hasParameter(RescheduleLoansApiConstants.submittedOnDateParamName)) {
                // create a LocalDate object from the "submittedOnDate" Date
                // string
                LocalDate localDate = jsonCommand.localDateValueOfParameterNamed(RescheduleLoansApiConstants.submittedOnDateParamName);
                
                if (localDate != null) {
                    // update the value of the "submittedOnDate" variable
                    submittedOnDate = localDate.toDate();
                }
            }

            // initially set the value to null
            Date rescheduleFromDate = null;

            // start point of the rescheduling exercise
            Integer rescheduleFromInstallment = null;

            // initially set the value to null
            Date adjustedDueDate = null;
            

            // check if the parameter is in the JsonCommand object
            if (jsonCommand.hasParameter(RescheduleLoansApiConstants.rescheduleFromDateParamName)) {
                // create a LocalDate object from the "rescheduleFromDate" Date
                // string
                LocalDate localDate = jsonCommand.localDateValueOfParameterNamed(RescheduleLoansApiConstants.rescheduleFromDateParamName);

                if (localDate != null) {
                    // get installment by due date
                    LoanRepaymentScheduleInstallment installment = loan.getRepaymentScheduleInstallment(localDate);
                    rescheduleFromInstallment = installment.getInstallmentNumber();

                    // update the value of the "rescheduleFromDate" variable
                    rescheduleFromDate = localDate.toDate();
                }
            }

            if (jsonCommand.hasParameter(RescheduleLoansApiConstants.adjustedDueDateParamName)) {
                // create a LocalDate object from the "adjustedDueDate" Date
                // string
                LocalDate localDate = jsonCommand.localDateValueOfParameterNamed(RescheduleLoansApiConstants.adjustedDueDateParamName);

                if (localDate != null) {
                    /**
                     * validate Holiday and Non working day.
                     */
                    final HolidayDetailDTO holidayDetailDTO = this.loanUtilService.constructHolidayDTO(loan);
                    loan.validateRepaymentDateIsOnHoliday(localDate, holidayDetailDTO.isAllowTransactionsOnHoliday(),
                            holidayDetailDTO.getHolidays());
                    loan.validateRepaymentDateIsOnNonWorkingDay(localDate, holidayDetailDTO.getWorkingDays(),
                            holidayDetailDTO.isAllowTransactionsOnNonWorkingDay());
                    // update the value of the "adjustedDueDate"variable
                    adjustedDueDate = localDate.toDate();
                }
            }
            
            //If isSpecificToInstallement is checked then reschedule only that particular re-payment
            boolean isSpecificToInstallment = jsonCommand
                    .booleanPrimitiveValueOfParameterNamed(RescheduleLoansApiConstants.isSpecificToInstallment);
                        
            final LoanRescheduleRequest loanRescheduleRequest = LoanRescheduleRequest.instance(loan,
                    LoanStatus.SUBMITTED_AND_PENDING_APPROVAL.getValue(), rescheduleFromInstallment, rescheduleFromDate,
                    recalculateInterest, rescheduleReasonCodeValue, rescheduleReasonComment, submittedOnDate,
                    this.platformSecurityContext.authenticatedUser(), null, null, null, null);

            // update reschedule request to term variations mapping
            List<LoanRescheduleRequestToTermVariationMapping> loanRescheduleRequestToTermVariationMappings = new ArrayList<>();
            final Boolean isActive = false;
            BigDecimal decimalValue = null;
            Date dueDate = null;
            // create term variations for flat and declining balance loans
            createLoanTermVariationsForRegularLoans(loan, graceOnPrincipal, graceOnInterest, extraTerms, interestRate, rescheduleFromDate,
                    adjustedDueDate, loanRescheduleRequest, loanRescheduleRequestToTermVariationMappings, isActive,
                    isSpecificToInstallment, decimalValue, dueDate, installmentAmount);

            // create a new entry in the m_loan_reschedule_request table
            this.loanRescheduleRequestRepository.save(loanRescheduleRequest);
            this.loanRepository.save(loan);

            return new CommandProcessingResultBuilder().withCommandId(jsonCommand.commandId()).withEntityId(loanRescheduleRequest.getId())
                    .withLoanId(loan.getId()).build();
        }

        catch (final DataIntegrityViolationException dve) {
            // handle the data integrity violation
            handleDataIntegrityViolation(dve);

            // return an empty command processing result object
            return CommandProcessingResult.empty();
        }
    }

    private void createLoanTermVariationsForRegularLoans(final Loan loan, final Integer graceOnPrincipal, final Integer graceOnInterest,
            final Integer extraTerms, final BigDecimal interestRate, Date rescheduleFromDate, Date adjustedDueDate,
            final LoanRescheduleRequest loanRescheduleRequest,
            List<LoanRescheduleRequestToTermVariationMapping> loanRescheduleRequestToTermVariationMappings, final Boolean isActive,
            final boolean isSpecificToInstallment, BigDecimal decimalValue, Date dueDate, BigDecimal installmentAmount) {

        if (rescheduleFromDate != null && adjustedDueDate != null) {
            LoanTermVariations parent = null;
            final Integer termType = LoanTermVariationType.DUE_DATE.getValue();
            createLoanTermVariations(termType, loan, rescheduleFromDate, adjustedDueDate, loanRescheduleRequestToTermVariationMappings,
                    isActive, isSpecificToInstallment, decimalValue, parent);
        }

        if (rescheduleFromDate != null && interestRate != null) {
            LoanTermVariations parent = null;
            final Integer termType = LoanTermVariationType.INTEREST_RATE_FROM_INSTALLMENT.getValue();
            createLoanTermVariations(termType, loan, rescheduleFromDate, dueDate, loanRescheduleRequestToTermVariationMappings, isActive,
                    isSpecificToInstallment, interestRate, parent);
        }

        if (rescheduleFromDate != null && graceOnPrincipal != null) {
            final Integer termType = LoanTermVariationType.GRACE_ON_PRINCIPAL.getValue();
            LoanTermVariations parent = null;
            parent = createLoanTermVariations(termType, loan, rescheduleFromDate, dueDate, loanRescheduleRequestToTermVariationMappings,
                    isActive, isSpecificToInstallment, BigDecimal.valueOf(graceOnPrincipal), parent);

            BigDecimal extraTermsBasedOnGracePeriods = BigDecimal.valueOf(graceOnPrincipal);
            createLoanTermVariations(LoanTermVariationType.EXTEND_REPAYMENT_PERIOD.getValue(), loan, rescheduleFromDate, dueDate,
                    loanRescheduleRequestToTermVariationMappings, isActive, isSpecificToInstallment, extraTermsBasedOnGracePeriods, parent);

        }

        if (rescheduleFromDate != null && graceOnInterest != null) {
            LoanTermVariations parent = null;
            final Integer termType = LoanTermVariationType.GRACE_ON_INTEREST.getValue();
            createLoanTermVariations(termType, loan, rescheduleFromDate, dueDate, loanRescheduleRequestToTermVariationMappings, isActive,
                    isSpecificToInstallment, BigDecimal.valueOf(graceOnInterest), parent);
        }

        if (rescheduleFromDate != null && extraTerms != null) {
            LoanTermVariations parent = null;
            final Integer termType = LoanTermVariationType.EXTEND_REPAYMENT_PERIOD.getValue();
            createLoanTermVariations(termType, loan, rescheduleFromDate, dueDate, loanRescheduleRequestToTermVariationMappings, isActive,
                    isSpecificToInstallment, BigDecimal.valueOf(extraTerms), parent);
        }

        if (rescheduleFromDate != null && installmentAmount != null) {
            LoanTermVariations parent = null;
            final Integer termType = LoanTermVariationType.EMI_AMOUNT.getValue();
            createLoanTermVariations(termType, loan, rescheduleFromDate, dueDate, loanRescheduleRequestToTermVariationMappings, isActive,
                    isSpecificToInstallment, installmentAmount, parent);
        }
        loanRescheduleRequest.updateLoanRescheduleRequestToTermVariationMappings(loanRescheduleRequestToTermVariationMappings);
    }

    private LoanTermVariations createLoanTermVariations(final Integer termType, final Loan loan, Date rescheduleFromDate,
            Date adjustedDueDate, List<LoanRescheduleRequestToTermVariationMapping> loanRescheduleRequestToTermVariationMappings,
            final Boolean isActive, final boolean isSpecificToInstallment, final BigDecimal decimalValue, LoanTermVariations parent) {
        LoanTermVariations loanTermVariation = new LoanTermVariations(termType, rescheduleFromDate, decimalValue, adjustedDueDate,
                isSpecificToInstallment, loan, loan.status().getValue(), isActive, parent);
        loanRescheduleRequestToTermVariationMappings.add(LoanRescheduleRequestToTermVariationMapping.createNew(loanTermVariation));
        return loanTermVariation;
    }

    @Override
    @Transactional
    public CommandProcessingResult approve(JsonCommand jsonCommand) {

        try {
            final Long loanRescheduleRequestId = jsonCommand.entityId();

            final LoanRescheduleRequest loanRescheduleRequest = this.loanRescheduleRequestRepository.findOne(loanRescheduleRequestId);

            if (loanRescheduleRequest == null) { throw new LoanRescheduleRequestNotFoundException(loanRescheduleRequestId); }

            // validate the request in the JsonCommand object passed as
            // parameter
            this.loanRescheduleRequestDataValidator.validateForApproveAction(jsonCommand, loanRescheduleRequest);

            final AppUser appUser = this.platformSecurityContext.authenticatedUser();
            final Map<String, Object> changes = new LinkedHashMap<>();

            LocalDate approvedOnDate = jsonCommand.localDateValueOfParameterNamed("approvedOnDate");
            final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(jsonCommand.dateFormat()).withLocale(
                    jsonCommand.extractLocale());

            changes.put("locale", jsonCommand.locale());
            changes.put("dateFormat", jsonCommand.dateFormat());
            changes.put("approvedOnDate", approvedOnDate.toString(dateTimeFormatter));
            changes.put("approvedByUserId", appUser.getId());

            Loan loan = loanRescheduleRequest.getLoan();
            final List<Long> existingTransactionIds = new ArrayList<>(loan.findExistingTransactionIds());
            final List<Long> existingReversedTransactionIds = new ArrayList<>(loan.findExistingReversedTransactionIds());

            ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan,
                    loanRescheduleRequest.getRescheduleFromDate());

            Collection<LoanRepaymentScheduleHistory> loanRepaymentScheduleHistoryList = this.loanScheduleHistoryWritePlatformService
                    .createLoanScheduleArchive(loan.getRepaymentScheduleInstallments(), loan, loanRescheduleRequest);

            final LoanApplicationTerms loanApplicationTerms = loan.constructLoanApplicationTerms(scheduleGeneratorDTO);

            LocalDate rescheduleFromDate = null;
            rescheduleFromDate = updateLoanTermVariations(loanRescheduleRequest, loan, loanApplicationTerms, rescheduleFromDate);
            for (LoanRescheduleRequestToTermVariationMapping mapping : loanRescheduleRequest
                    .getLoanRescheduleRequestToTermVariationMappings()) {
                mapping.getLoanTermVariations().updateIsActive(true);
            }
            BigDecimal annualNominalInterestRate = null;
            List<LoanTermVariationsData> loanTermVariations = new ArrayList<>();
            loan.constructLoanTermVariations(scheduleGeneratorDTO.getFloatingRateDTO(), annualNominalInterestRate, loanTermVariations);
            loanApplicationTerms.getLoanTermVariations().setExceptionData(loanTermVariations);
            processApproveRequest(loanRescheduleRequest, appUser, approvedOnDate, loan, existingTransactionIds,
                    existingReversedTransactionIds, scheduleGeneratorDTO, loanRepaymentScheduleHistoryList, loanApplicationTerms,
                    rescheduleFromDate);
            if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
                createAndSaveLoanScheduleArchive(loan, scheduleGeneratorDTO, loanRescheduleRequest);
            }

            return new CommandProcessingResultBuilder().withCommandId(jsonCommand.commandId()).withEntityId(loanRescheduleRequestId)
                    .withLoanId(loanRescheduleRequest.getLoan().getId()).with(changes).build();
        }

        catch (final DataIntegrityViolationException dve) {
            // handle the data integrity violation
            handleDataIntegrityViolation(dve);

            // return an empty command processing result object
            return CommandProcessingResult.empty();
        }
    }

    private void processApproveRequest(final LoanRescheduleRequest loanRescheduleRequest, final AppUser appUser, LocalDate approvedOnDate,
            Loan loan, final List<Long> existingTransactionIds, final List<Long> existingReversedTransactionIds,
            ScheduleGeneratorDTO scheduleGeneratorDTO, Collection<LoanRepaymentScheduleHistory> loanRepaymentScheduleHistoryList,
            final LoanApplicationTerms loanApplicationTerms, LocalDate rescheduleFromDate) {
        final RoundingMode roundingMode = MoneyHelper.getRoundingMode();
        final MathContext mathContext = new MathContext(8, roundingMode);
        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.loanRepaymentScheduleTransactionProcessorFactory
                .determineProcessor(loan.transactionProcessingStrategy());
        final LoanScheduleGenerator loanScheduleGenerator = this.loanScheduleFactory.create(loanApplicationTerms.getInterestMethod());
        final LoanLifecycleStateMachine loanLifecycleStateMachine = null;
        loan.setHelpers(loanLifecycleStateMachine, this.loanSummaryWrapper, this.loanRepaymentScheduleTransactionProcessorFactory);
        if (loan.isGLIMLoan()) {
            this.glimLoanRescheduleService.approveGlimRescheduleRequest(loan, loanRescheduleRequest, loanRepaymentScheduleHistoryList,
                    scheduleGeneratorDTO, loanApplicationTerms, rescheduleFromDate, appUser, approvedOnDate);

        } else {
            approveLoanRescheduleRequest(loanRescheduleRequest, appUser, approvedOnDate, loan, existingTransactionIds,
                    existingReversedTransactionIds, loanRepaymentScheduleHistoryList, loanApplicationTerms, rescheduleFromDate,
                    mathContext, loanRepaymentScheduleTransactionProcessor, loanScheduleGenerator);

        }
    }

    private LocalDate updateLoanTermVariations(final LoanRescheduleRequest loanRescheduleRequest, Loan loan,
            final LoanApplicationTerms loanApplicationTerms, LocalDate rescheduleFromDate) {
        Set<LoanTermVariations> activeLoanTermVariations = loan.getActiveLoanTermVariations();
        LoanTermVariations dueDateVariationInCurrentRequest = loanRescheduleRequest.getDueDateTermVariationIfExists();
        LocalDate previouslyAdjustedDate = null;
        if (dueDateVariationInCurrentRequest != null && activeLoanTermVariations != null) {
            LocalDate fromScheduleDate = dueDateVariationInCurrentRequest.fetchTermApplicaDate();
            LocalDate currentScheduleDate = fromScheduleDate;
            LocalDate modifiedScheduleDate = dueDateVariationInCurrentRequest.fetchDateValue();
            Map<LocalDate, LocalDate> changeMap = new HashMap<>();
            changeMap.put(currentScheduleDate, modifiedScheduleDate);
            for (LoanTermVariations activeLoanTermVariation : activeLoanTermVariations) {
                if (activeLoanTermVariation.getTermType().isDueDateVariation()
                        && activeLoanTermVariation.fetchDateValue().equals(dueDateVariationInCurrentRequest.fetchTermApplicaDate())) {
                    activeLoanTermVariation.markAsInactive();
                    rescheduleFromDate = activeLoanTermVariation.fetchTermApplicaDate();
                    previouslyAdjustedDate = activeLoanTermVariation.fetchDateValue();
                    dueDateVariationInCurrentRequest.setTermApplicableFrom(rescheduleFromDate.toDate());
                } else if (!activeLoanTermVariation.fetchTermApplicaDate().isBefore(fromScheduleDate)) {
                    while (currentScheduleDate.isBefore(activeLoanTermVariation.fetchTermApplicaDate())) {
                        currentScheduleDate = this.scheduledDateGenerator.generateNextRepaymentDate(currentScheduleDate,
                                loanApplicationTerms, false);
                        modifiedScheduleDate = this.scheduledDateGenerator.generateNextRepaymentDate(modifiedScheduleDate,
                                loanApplicationTerms, false);
                        changeMap.put(currentScheduleDate, modifiedScheduleDate);
                    }
                    if (changeMap.containsKey(activeLoanTermVariation.fetchTermApplicaDate())) {
                        activeLoanTermVariation.setTermApplicableFrom(changeMap.get(activeLoanTermVariation.fetchTermApplicaDate())
                                .toDate());
                    }
                }
            }
        }
        if (rescheduleFromDate == null) {
            rescheduleFromDate = loanRescheduleRequest.getRescheduleFromDate();
        }
        if (previouslyAdjustedDate != null) {
            rescheduleFromDate = rescheduleFromDate.isAfter(previouslyAdjustedDate) ? previouslyAdjustedDate : rescheduleFromDate;
        }
        if (dueDateVariationInCurrentRequest != null) {
            if (!(loanApplicationTerms.getNthDay() == null || loanApplicationTerms.getWeekDayType() == null || loanApplicationTerms
                    .getWeekDayType() == DayOfWeekType.INVALID) && loanApplicationTerms.getRepaymentPeriodFrequencyType().isMonthly()) {
                Calendar loanCalendar = loanApplicationTerms.getLoanCalendar();
                // create calendar history before updating calendar
                final CalendarHistory calendarHistory = new CalendarHistory(loanCalendar, loanCalendar.getStartDate());
                Date endDate = dueDateVariationInCurrentRequest.fetchDateValue().minusDays(1).toDate();
                calendarHistory.updateEndDate(endDate);
                this.calendarHistoryRepository.save(calendarHistory);
                loanApplicationTerms.getCalendarHistoryDataWrapper().getCalendarHistoryList().add(calendarHistory);
                boolean isMeetingAttached = this.loanUtilService.isMeetingAttached(loan);
                if (loan.isGLIMLoan()) {
                    loanCalendar.updateStartDateAndNthDayAndDayOfWeek(dueDateVariationInCurrentRequest.fetchDateValue(), isMeetingAttached);
                } else {
                    loanCalendar.updateStartDateAndNthDayAndDayOfWeekType(dueDateVariationInCurrentRequest.fetchDateValue(), isMeetingAttached);
                }
            }
        }
        return rescheduleFromDate;
    }

    private void approveLoanRescheduleRequest(final LoanRescheduleRequest loanRescheduleRequest, final AppUser appUser,
            LocalDate approvedOnDate, Loan loan, final List<Long> existingTransactionIds, final List<Long> existingReversedTransactionIds,
            Collection<LoanRepaymentScheduleHistory> loanRepaymentScheduleHistoryList, final LoanApplicationTerms loanApplicationTerms,
            LocalDate rescheduleFromDate, final MathContext mathContext,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor,
            final LoanScheduleGenerator loanScheduleGenerator) {
        if (!loan.getLoanRecurringCharges().isEmpty() && rescheduleFromDate.isBefore(DateUtils.getLocalDateOfTenant())) {
            this.loanOverdueChargeService.updateOverdueChargesAsOnDate(loan, rescheduleFromDate);
        }
        LoanScheduleDTO loanSchedule = loanScheduleGenerator.rescheduleNextInstallments(mathContext, loanApplicationTerms, loan,
                loanApplicationTerms.getHolidayDetailDTO(), loan.getLoanTransactions(), loanRepaymentScheduleTransactionProcessor,
                rescheduleFromDate);
        loan.updateLoanSchedule(loanSchedule.getInstallments(), appUser);
        loan.recalculateAllCharges();
        ChangedTransactionDetail changedTransactionDetail = loan.processTransactions();

        for (LoanRepaymentScheduleHistory loanRepaymentScheduleHistory : loanRepaymentScheduleHistoryList) {
            this.loanRepaymentScheduleHistoryRepository.save(loanRepaymentScheduleHistory);
        }

        loan.updateRescheduledByUser(appUser);
        loan.updateRescheduledOnDate(DateUtils.getLocalDateOfTenant());

        // update the status of the request
        loanRescheduleRequest.approve(appUser, approvedOnDate);

        // update the loan object
        saveAndFlushLoanWithDataIntegrityViolationChecks(loan);

        if (changedTransactionDetail != null) {
            for (final Map.Entry<Long, LoanTransaction> mapEntry : changedTransactionDetail.getNewTransactionMappings().entrySet()) {
                this.loanTransactionRepository.save(mapEntry.getValue());
                // update loan with references to the newly created
                // transactions
                loan.addLoanTransaction(mapEntry.getValue());
                this.accountTransfersWritePlatformService.updateLoanTransaction(mapEntry.getKey(), mapEntry.getValue());
            }
        }
        postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);

        this.loanAccountDomainService.recalculateAccruals(loan, true);
    }

    private void saveAndFlushLoanWithDataIntegrityViolationChecks(final Loan loan) {
        try {
            List<LoanRepaymentScheduleInstallment> installments = loan.getRepaymentScheduleInstallments();
            for (LoanRepaymentScheduleInstallment installment : installments) {
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

    private void postJournalEntries(Loan loan, List<Long> existingTransactionIds, List<Long> existingReversedTransactionIds) {
        final MonetaryCurrency currency = loan.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);
        boolean isAccountTransfer = false;
        final Map<String, Object> accountingBridgeData = loan.deriveAccountingBridgeData(applicationCurrency.toData(),
                existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        this.journalEntryWritePlatformService.createJournalEntriesForLoan(accountingBridgeData);
    }

    @Override
    @Transactional
    public CommandProcessingResult reject(JsonCommand jsonCommand) {

        try {
            final Long loanRescheduleRequestId = jsonCommand.entityId();

            final LoanRescheduleRequest loanRescheduleRequest = loanRescheduleRequestRepository.findOne(loanRescheduleRequestId);

            if (loanRescheduleRequest == null) { throw new LoanRescheduleRequestNotFoundException(loanRescheduleRequestId); }

            // validate the request in the JsonCommand object passed as
            // parameter
            this.loanRescheduleRequestDataValidator.validateForRejectAction(jsonCommand, loanRescheduleRequest);

            final AppUser appUser = this.platformSecurityContext.authenticatedUser();
            final Map<String, Object> changes = new LinkedHashMap<>();

            LocalDate rejectedOnDate = jsonCommand.localDateValueOfParameterNamed("rejectedOnDate");
            final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(jsonCommand.dateFormat()).withLocale(
                    jsonCommand.extractLocale());

            changes.put("locale", jsonCommand.locale());
            changes.put("dateFormat", jsonCommand.dateFormat());
            changes.put("rejectedOnDate", rejectedOnDate.toString(dateTimeFormatter));
            changes.put("rejectedByUserId", appUser.getId());

            if (!changes.isEmpty()) {
                loanRescheduleRequest.reject(appUser, rejectedOnDate);
                Set<LoanRescheduleRequestToTermVariationMapping> loanRescheduleRequestToTermVariationMappings = loanRescheduleRequest
                        .getLoanRescheduleRequestToTermVariationMappings();
                for (LoanRescheduleRequestToTermVariationMapping loanRescheduleRequestToTermVariationMapping : loanRescheduleRequestToTermVariationMappings) {
                    loanRescheduleRequestToTermVariationMapping.getLoanTermVariations().markAsInactive();
                }
            }

            return new CommandProcessingResultBuilder().withCommandId(jsonCommand.commandId()).withEntityId(loanRescheduleRequestId)
                    .withLoanId(loanRescheduleRequest.getLoan().getId()).with(changes).build();
        }

        catch (final DataIntegrityViolationException dve) {
            // handle the data integrity violation
            handleDataIntegrityViolation(dve);

            // return an empty command processing result object
            return CommandProcessingResult.empty();
        }
    }

    /**
     * handles the data integrity violation exception for loan reschedule write
     * services
     *
     * @param dve
     *            data integrity violation exception
     * @return void
     **/
    private void handleDataIntegrityViolation(final DataIntegrityViolationException dve) {

        logger.error(dve.getMessage(), dve);

        throw new PlatformDataIntegrityException("error.msg.loan.reschedule.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    @Override
    public CommandProcessingResult createAndApprove(JsonCommand jsonCommand) {
        try {

            boolean isBulkCreateAndApprove = true;
            Loan loan = null;
            this.loanRescheduleRequestDataValidator.validateForCreateAction(jsonCommand, loan, isBulkCreateAndApprove);
            // get the reschedule reason code value id from the JsonCommand
            // object
            final Long rescheduleReasonId = jsonCommand.longValueOfParameterNamed(RescheduleLoansApiConstants.rescheduleReasonIdParamName);

            // use the reschedule reason code value id to get a CodeValue entity
            // object
            final CodeValue rescheduleReasonCodeValue = this.codeValueRepositoryWrapper.findOneWithNotFoundDetection(rescheduleReasonId);

            // get the grace on principal integer value from the JsonCommand
            // object
            final Integer graceOnPrincipal = jsonCommand
                    .integerValueOfParameterNamed(RescheduleLoansApiConstants.graceOnPrincipalParamName);

            // get the grace on interest integer value from the JsonCommand
            // object
            final Integer graceOnInterest = jsonCommand.integerValueOfParameterNamed(RescheduleLoansApiConstants.graceOnInterestParamName);

            // get the extra terms to be added at the end of the new schedule
            // from the JsonCommand object
            final Integer extraTerms = jsonCommand.integerValueOfParameterNamed(RescheduleLoansApiConstants.extraTermsParamName);

            // get the new interest rate that would be applied to the new loan
            // schedule
            final BigDecimal interestRate = jsonCommand
                    .bigDecimalValueOfParameterNamed(RescheduleLoansApiConstants.newInterestRateParamName);

            // get the reschedule reason comment text from the JsonCommand
            // object
            final String rescheduleReasonComment = jsonCommand
                    .stringValueOfParameterNamed(RescheduleLoansApiConstants.rescheduleReasonCommentParamName);

            // get the recalculate interest option
            final Boolean recalculateInterest = jsonCommand
                    .booleanObjectValueOfParameterNamed(RescheduleLoansApiConstants.recalculateInterestParamName);

            final BigDecimal installmentAmount = jsonCommand
                    .bigDecimalValueOfParameterNamed(RescheduleLoansApiConstants.newInstallmentAmountParamName);

            // initialize set the value to null
            Date submittedOnDate = null;

            // check if the parameter is in the JsonCommand object
            if (jsonCommand.hasParameter(RescheduleLoansApiConstants.submittedOnDateParamName)) {
                // create a LocalDate object from the "submittedOnDate" Date
                // string
                LocalDate localDate = jsonCommand.localDateValueOfParameterNamed(RescheduleLoansApiConstants.submittedOnDateParamName);

                if (localDate != null) {
                    // update the value of the "submittedOnDate" variable
                    submittedOnDate = localDate.toDate();
                }
            }

            // initially set the value to null
            Date rescheduleFromDate = null;

            // start point of the rescheduling exercise
            Integer rescheduleFromInstallment = null;

            // initially set the value to null
            Date adjustedDueDate = null;

            if (jsonCommand.hasParameter(RescheduleLoansApiConstants.adjustedDueDateParamName)) {
                // create a LocalDate object from the "adjustedDueDate" Date
                // string
                LocalDate localDate = jsonCommand.localDateValueOfParameterNamed(RescheduleLoansApiConstants.adjustedDueDateParamName);

                if (localDate != null) {
                    // update the value of the "adjustedDueDate"variable
                    adjustedDueDate = localDate.toDate();
                }
            }
            // check if the parameter is in the JsonCommand object
            if (jsonCommand.hasParameter(RescheduleLoansApiConstants.rescheduleFromDateParamName)) {
                // create a LocalDate object from the "rescheduleFromDate" Date
                // string
                LocalDate localDate = jsonCommand.localDateValueOfParameterNamed(RescheduleLoansApiConstants.rescheduleFromDateParamName);

                if (localDate != null) {
                    // update the value of the "rescheduleFromDate" variable
                    rescheduleFromDate = localDate.toDate();
                }
            }

            // initialize specificToInstallment to false
            boolean isSpecificToInstallment = jsonCommand
                    .booleanPrimitiveValueOfParameterNamed(RescheduleLoansApiConstants.isSpecificToInstallment);

            String[] loans = jsonCommand.arrayValueOfParameterNamed(RescheduleLoansApiConstants.loansParamName);
            final AppUser appUser = this.platformSecurityContext.authenticatedUser();
            final Map<String, Object> changes = new LinkedHashMap<>();

            LocalDate approvedOnDate = new LocalDate(submittedOnDate);
            final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(jsonCommand.dateFormat()).withLocale(
                    jsonCommand.extractLocale());
            changes.put("locale", jsonCommand.locale());
            changes.put("dateFormat", jsonCommand.dateFormat());
            changes.put("approvedOnDate", approvedOnDate.toString(dateTimeFormatter));
            changes.put("approvedByUserId", appUser.getId());

            List<HashMap<String, Object>> rescheduleRequestChanges = new ArrayList<>();
            for (String loanId : loans) {
                HashMap<String, Object> change = bulkCreateAndApproveRescheduleRequest(Long.parseLong(loanId), rescheduleReasonCodeValue,
                        graceOnPrincipal, graceOnInterest, extraTerms, interestRate, rescheduleReasonComment, recalculateInterest,
                        installmentAmount, submittedOnDate, rescheduleFromDate, rescheduleFromInstallment, adjustedDueDate,
                        isSpecificToInstallment, appUser);
                rescheduleRequestChanges.add(change);

            }
            changes.put("rescheduleRequestChanges", rescheduleRequestChanges);
            // use the loan id to get a Loan entity object

            return new CommandProcessingResultBuilder().withCommandId(jsonCommand.commandId()).with(changes).build();
        } catch (final DataIntegrityViolationException dve) {
            // handle the data integrity violation
            handleDataIntegrityViolation(dve);
            // return an empty command processing result object
            return CommandProcessingResult.empty();
        }

    }

    private HashMap<String, Object> bulkCreateAndApproveRescheduleRequest(final Long loanId, final CodeValue rescheduleReasonCodeValue,
            final Integer graceOnPrincipal, final Integer graceOnInterest, final Integer extraTerms, final BigDecimal interestRate,
            final String rescheduleReasonComment, final Boolean recalculateInterest, final BigDecimal installmentAmount,
            Date submittedOnDate, Date rescheduleFromDate, Integer rescheduleFromInstallment, Date adjustedDueDate,
            boolean isSpecificToInstallment, final AppUser appUser) {

        HashMap<String, Object> changes = new HashMap<>();
        Loan loan = this.loanAssembler.assembleFrom(loanId);

        // loan specific validation
        this.loanRescheduleRequestDataValidator.validateForBulkCreateAndApproveAction(loan, new LocalDate(rescheduleFromDate),
                new LocalDate(submittedOnDate), new LocalDate(adjustedDueDate), installmentAmount);
        if (rescheduleFromDate != null) {
            LoanRepaymentScheduleInstallment installment = loan.getRepaymentScheduleInstallment(new LocalDate(rescheduleFromDate));
            rescheduleFromInstallment = installment.getInstallmentNumber();
        }
        LoanRescheduleRequest loanRescheduleRequest = LoanRescheduleRequest.instance(loan,
                LoanStatus.SUBMITTED_AND_PENDING_APPROVAL.getValue(), rescheduleFromInstallment, rescheduleFromDate, recalculateInterest,
                rescheduleReasonCodeValue, rescheduleReasonComment, submittedOnDate, appUser, null, null, null, null);

        // update reschedule request to term variations mapping
        List<LoanRescheduleRequestToTermVariationMapping> loanRescheduleRequestToTermVariationMappings = new ArrayList<>();
        final Boolean isActive = false;
        BigDecimal decimalValue = null;
        Date dueDate = null;
        // create term variations for flat and declining balance loans
        createLoanTermVariationsForRegularLoans(loan, graceOnPrincipal, graceOnInterest, extraTerms, interestRate, rescheduleFromDate,
                adjustedDueDate, loanRescheduleRequest, loanRescheduleRequestToTermVariationMappings, isActive, isSpecificToInstallment,
                decimalValue, dueDate, installmentAmount);

        this.loanRescheduleRequestRepository.save(loanRescheduleRequest);

        // approve functionality
        LocalDate approvedOnDate = new LocalDate(submittedOnDate);

        final List<Long> existingTransactionIds = new ArrayList<>(loan.findExistingTransactionIds());
        final List<Long> existingReversedTransactionIds = new ArrayList<>(loan.findExistingReversedTransactionIds());

        ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, new LocalDate(rescheduleFromDate));

        Collection<LoanRepaymentScheduleHistory> loanRepaymentScheduleHistoryList = this.loanScheduleHistoryWritePlatformService
                .createLoanScheduleArchive(loan.getRepaymentScheduleInstallments(), loan, loanRescheduleRequest);

        final LoanApplicationTerms loanApplicationTerms = loan.constructLoanApplicationTerms(scheduleGeneratorDTO);

        rescheduleFromDate = updateLoanTermVariations(loanRescheduleRequest, loan, loanApplicationTerms, new LocalDate(rescheduleFromDate))
                .toDate();
        for (LoanRescheduleRequestToTermVariationMapping mapping : loanRescheduleRequestToTermVariationMappings) {
            mapping.getLoanTermVariations().updateIsActive(true);
            loan.getLoanTermVariations().add(mapping.getLoanTermVariations());
        }
        BigDecimal annualNominalInterestRate = null;
        List<LoanTermVariationsData> loanTermVariations = new ArrayList<>();
        loan.constructLoanTermVariations(scheduleGeneratorDTO.getFloatingRateDTO(), annualNominalInterestRate, loanTermVariations);

        loanApplicationTerms.getLoanTermVariations().setExceptionData(loanTermVariations);
        processApproveRequest(loanRescheduleRequest, appUser, approvedOnDate, loan, existingTransactionIds, existingReversedTransactionIds,
                scheduleGeneratorDTO, loanRepaymentScheduleHistoryList, loanApplicationTerms, new LocalDate(rescheduleFromDate));
        this.loanRepository.save(loan);
        changes.put("loanId", loanId);
        changes.put("rescheduleRequestId", loanRescheduleRequest.getId());
        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            createAndSaveLoanScheduleArchive(loan, scheduleGeneratorDTO, loanRescheduleRequest);
        }
        return changes;
    }
    
    private void createAndSaveLoanScheduleArchive(final Loan loan, ScheduleGeneratorDTO scheduleGeneratorDTO, final LoanRescheduleRequest loanRescheduleRequest) {
        LoanScheduleModel loanScheduleModel = loan.regenerateScheduleModel(scheduleGeneratorDTO);
        List<LoanRepaymentScheduleInstallment> installments = retrieveRepaymentScheduleFromModel(loanScheduleModel);
        this.loanScheduleHistoryWritePlatformService.createAndSaveLoanScheduleArchive(installments, loan, loanRescheduleRequest);
    }
    
    private List<LoanRepaymentScheduleInstallment> retrieveRepaymentScheduleFromModel(LoanScheduleModel model) {
        final List<LoanRepaymentScheduleInstallment> installments = new ArrayList<>();
        for (final LoanScheduleModelPeriod scheduledLoanInstallment : model.getPeriods()) {
            if (scheduledLoanInstallment.isRepaymentPeriod()) {
                final LoanRepaymentScheduleInstallment installment = new LoanRepaymentScheduleInstallment(null,
                        scheduledLoanInstallment.periodNumber(), scheduledLoanInstallment.periodFromDate(),
                        scheduledLoanInstallment.periodDueDate(), scheduledLoanInstallment.principalDue(),
                        scheduledLoanInstallment.interestDue(), scheduledLoanInstallment.feeChargesDue(),
                        scheduledLoanInstallment.penaltyChargesDue(), scheduledLoanInstallment.isRecalculatedInterestComponent(),
                        scheduledLoanInstallment.getLoanCompoundingDetails());
                installments.add(installment);
            }
        }
        return installments;
    }

}
