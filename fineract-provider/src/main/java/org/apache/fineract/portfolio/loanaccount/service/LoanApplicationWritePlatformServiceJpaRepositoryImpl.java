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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormat;
import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormatRepositoryWrapper;
import org.apache.fineract.infrastructure.accountnumberformat.domain.EntityAccountType;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.api.JsonQuery;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.entityaccess.FineractEntityAccessConstants;
import org.apache.fineract.infrastructure.entityaccess.domain.FineractEntityAccessType;
import org.apache.fineract.infrastructure.entityaccess.domain.FineractEntityRelation;
import org.apache.fineract.infrastructure.entityaccess.domain.FineractEntityRelationRepository;
import org.apache.fineract.infrastructure.entityaccess.domain.FineractEntityToEntityMapping;
import org.apache.fineract.infrastructure.entityaccess.domain.FineractEntityToEntityMappingRepository;
import org.apache.fineract.infrastructure.entityaccess.exception.NotOfficeSpecificProductException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.portfolio.account.domain.AccountAssociationType;
import org.apache.fineract.portfolio.account.domain.AccountAssociations;
import org.apache.fineract.portfolio.account.domain.AccountAssociationsRepository;
import org.apache.fineract.portfolio.accountdetails.domain.AccountType;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.domain.CalendarFrequencyType;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstanceRepository;
import org.apache.fineract.portfolio.calendar.domain.CalendarRepository;
import org.apache.fineract.portfolio.calendar.domain.CalendarType;
import org.apache.fineract.portfolio.calendar.exception.CalendarNotFoundException;
import org.apache.fineract.portfolio.calendar.service.CalendarReadPlatformService;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.domain.GroupLoanIndividualMonitoringChargeRepository;
import org.apache.fineract.portfolio.charge.exception.ChargeNotSupportedException;
import org.apache.fineract.portfolio.charge.exception.GlimLoanCannotHaveMoreThanOneGoalRoundSeekCharge;
import org.apache.fineract.portfolio.charge.exception.UpfrontChargeNotFoundException;
import org.apache.fineract.portfolio.client.domain.AccountNumberGenerator;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.exception.ClientNotActiveException;
import org.apache.fineract.portfolio.collateral.domain.LoanCollateral;
import org.apache.fineract.portfolio.collateral.service.CollateralAssembler;
import org.apache.fineract.portfolio.collaterals.domain.PledgeRepositoryWrapper;
import org.apache.fineract.portfolio.collaterals.domain.Pledges;
import org.apache.fineract.portfolio.collaterals.service.PledgeReadPlatformService;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.fund.domain.Fund;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.domain.GroupRepositoryWrapper;
import org.apache.fineract.portfolio.group.exception.GroupNotActiveException;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringDataValidator;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargeData;
import org.apache.fineract.portfolio.loanaccount.data.ScheduleGeneratorDTO;
import org.apache.fineract.portfolio.loanaccount.domain.DefaultLoanLifecycleStateMachine;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepository;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDisbursementDetails;
import org.apache.fineract.portfolio.loanaccount.domain.LoanLifecycleStateMachine;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallmentRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleTransactionProcessorFactory;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanaccount.domain.LoanSummaryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTopupDetails;
import org.apache.fineract.portfolio.loanaccount.exception.LoanApplicationDateException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanApplicationNotInSubmittedAndPendingApprovalStateCannotBeDeleted;
import org.apache.fineract.portfolio.loanaccount.exception.LoanApplicationNotInSubmittedAndPendingApprovalStateCannotBeModified;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.AprCalculator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanApplicationTerms;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModel;
import org.apache.fineract.portfolio.loanaccount.loanschedule.service.LoanScheduleAssembler;
import org.apache.fineract.portfolio.loanaccount.loanschedule.service.LoanScheduleCalculationPlatformService;
import org.apache.fineract.portfolio.loanaccount.serialization.LoanApplicationCommandFromApiJsonHelper;
import org.apache.fineract.portfolio.loanaccount.serialization.LoanApplicationTransitionApiJsonValidator;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductBusinessRuleValidator;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRelatedDetail;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRepository;
import org.apache.fineract.portfolio.loanproduct.domain.LoanTransactionProcessingStrategy;
import org.apache.fineract.portfolio.loanproduct.domain.RecalculationFrequencyType;
import org.apache.fineract.portfolio.loanproduct.exception.LinkedAccountRequiredException;
import org.apache.fineract.portfolio.loanproduct.exception.LoanProductNotFoundException;
import org.apache.fineract.portfolio.loanproduct.serialization.LoanProductDataValidator;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.portfolio.loan.purpose.domain.LoanPurpose;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class LoanApplicationWritePlatformServiceJpaRepositoryImpl implements LoanApplicationWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(LoanApplicationWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final FromJsonHelper fromJsonHelper;
    private final LoanApplicationTransitionApiJsonValidator loanApplicationTransitionApiJsonValidator;
    private final LoanProductDataValidator loanProductCommandFromApiJsonDeserializer;
    private final LoanApplicationCommandFromApiJsonHelper fromApiJsonDeserializer;
    private final LoanRepository loanRepository;
    private final NoteRepository noteRepository;
    private final LoanScheduleCalculationPlatformService calculationPlatformService;
    private final LoanAssembler loanAssembler;
    private final ClientRepositoryWrapper clientRepository;
    private final LoanProductRepository loanProductRepository;
    private final LoanChargeAssembler loanChargeAssembler;
    private final CollateralAssembler loanCollateralAssembler;
    private final AprCalculator aprCalculator;
    private final AccountNumberGenerator accountNumberGenerator;
    private final LoanSummaryWrapper loanSummaryWrapper;
    private final GroupRepositoryWrapper groupRepository;
    private final LoanRepaymentScheduleTransactionProcessorFactory loanRepaymentScheduleTransactionProcessorFactory;
    private final CalendarRepository calendarRepository;
    private final CalendarInstanceRepository calendarInstanceRepository;
    private final SavingsAccountAssembler savingsAccountAssembler;
    private final AccountAssociationsRepository accountAssociationsRepository;
    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository;
    private final AccountNumberFormatRepositoryWrapper accountNumberFormatRepository;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final ConfigurationDomainService configurationDomainService;
    private final LoanScheduleAssembler loanScheduleAssembler;
    private final PledgeRepositoryWrapper pledgeRepositoryWrapper;
    private final PledgeReadPlatformService pledgeReadPlatformService;
    private final LoanUtilService loanUtilService;
    private final CalendarReadPlatformService calendarReadPlatformService;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final LoanProductBusinessRuleValidator loanProductBusinessRuleValidator;
    private final FineractEntityToEntityMappingRepository repository;
    private final FineractEntityRelationRepository fineractEntityRelationRepository;
    private final LoanRepositoryWrapper  loanRepositoryWrapper;
    private final GroupLoanIndividualMonitoringRepository groupLoanIndividualMonitoringRepository;
    private final GroupLoanIndividualMonitoringAssembler groupLoanIndividualMonitoringAssembler;
    private final ChargeRepositoryWrapper chargeRepositoryWrapper;
    private final GroupLoanIndividualMonitoringChargeRepository groupLoanIndividualMonitoringChargeRepository;

    @Autowired
    public LoanApplicationWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context, final FromJsonHelper fromJsonHelper,
            final LoanApplicationTransitionApiJsonValidator loanApplicationTransitionApiJsonValidator,
            final LoanApplicationCommandFromApiJsonHelper fromApiJsonDeserializer,
            final LoanProductDataValidator loanProductCommandFromApiJsonDeserializer, final AprCalculator aprCalculator,
            final LoanAssembler loanAssembler, final LoanChargeAssembler loanChargeAssembler,
            final CollateralAssembler loanCollateralAssembler, final LoanRepository loanRepository, final NoteRepository noteRepository,
            final LoanScheduleCalculationPlatformService calculationPlatformService, final ClientRepositoryWrapper clientRepository,
            final LoanProductRepository loanProductRepository, final AccountNumberGenerator accountNumberGenerator,
            final LoanSummaryWrapper loanSummaryWrapper, final GroupRepositoryWrapper groupRepository,
            final LoanRepaymentScheduleTransactionProcessorFactory loanRepaymentScheduleTransactionProcessorFactory,
            final CalendarRepository calendarRepository, final CalendarInstanceRepository calendarInstanceRepository,
            final SavingsAccountAssembler savingsAccountAssembler, final AccountAssociationsRepository accountAssociationsRepository,
            final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository,
            final LoanReadPlatformService loanReadPlatformService,
            final AccountNumberFormatRepositoryWrapper accountNumberFormatRepository,
            final BusinessEventNotifierService businessEventNotifierService, final ConfigurationDomainService configurationDomainService,
            final LoanScheduleAssembler loanScheduleAssembler, final PledgeRepositoryWrapper pledgeRepositoryWrapper,
            final PledgeReadPlatformService pledgeReadPlatformService, final LoanUtilService loanUtilService,
            final CalendarReadPlatformService calendarReadPlatformService,
            final LoanProductReadPlatformService loanProductReadPlatformService,
            final LoanProductBusinessRuleValidator loanProductBusinessRuleValidator,
            final FineractEntityToEntityMappingRepository repository, final FineractEntityRelationRepository fineractEntityRelationRepository,
            final LoanRepositoryWrapper  loanRepositoryWrapper,final GroupLoanIndividualMonitoringRepository groupLoanIndividualMonitoringRepository,  
            final GroupLoanIndividualMonitoringAssembler groupLoanIndividualMonitoringAssembler, final ChargeRepositoryWrapper chargeRepositoryWrapper,
            final GroupLoanIndividualMonitoringChargeRepository groupLoanIndividualMonitoringChargeRepository) {
        this.context = context;
        this.fromJsonHelper = fromJsonHelper;
        this.loanApplicationTransitionApiJsonValidator = loanApplicationTransitionApiJsonValidator;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.loanProductCommandFromApiJsonDeserializer = loanProductCommandFromApiJsonDeserializer;
        this.aprCalculator = aprCalculator;
        this.loanAssembler = loanAssembler;
        this.loanChargeAssembler = loanChargeAssembler;
        this.loanCollateralAssembler = loanCollateralAssembler;
        this.loanRepository = loanRepository;
        this.noteRepository = noteRepository;
        this.calculationPlatformService = calculationPlatformService;
        this.clientRepository = clientRepository;
        this.loanProductRepository = loanProductRepository;
        this.accountNumberGenerator = accountNumberGenerator;
        this.loanSummaryWrapper = loanSummaryWrapper;
        this.groupRepository = groupRepository;
        this.loanRepaymentScheduleTransactionProcessorFactory = loanRepaymentScheduleTransactionProcessorFactory;
        this.calendarRepository = calendarRepository;
        this.calendarInstanceRepository = calendarInstanceRepository;
        this.savingsAccountAssembler = savingsAccountAssembler;
        this.accountAssociationsRepository = accountAssociationsRepository;
        this.repaymentScheduleInstallmentRepository = repaymentScheduleInstallmentRepository;
        this.loanReadPlatformService = loanReadPlatformService;
        this.accountNumberFormatRepository = accountNumberFormatRepository;
        this.businessEventNotifierService = businessEventNotifierService;
        this.configurationDomainService = configurationDomainService;
        this.loanScheduleAssembler = loanScheduleAssembler;
        this.pledgeRepositoryWrapper = pledgeRepositoryWrapper;
        this.pledgeReadPlatformService = pledgeReadPlatformService;
        this.loanUtilService = loanUtilService;
        this.calendarReadPlatformService = calendarReadPlatformService;
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.loanProductBusinessRuleValidator = loanProductBusinessRuleValidator;
        this.repository = repository;
        this.fineractEntityRelationRepository = fineractEntityRelationRepository;
        this.loanRepositoryWrapper = loanRepositoryWrapper;
        this.groupLoanIndividualMonitoringRepository = groupLoanIndividualMonitoringRepository;
        this.groupLoanIndividualMonitoringAssembler = groupLoanIndividualMonitoringAssembler;
        this.chargeRepositoryWrapper = chargeRepositoryWrapper;
        this.groupLoanIndividualMonitoringChargeRepository = groupLoanIndividualMonitoringChargeRepository;
    }

    private LoanLifecycleStateMachine defaultLoanLifecycleStateMachine() {
        final List<LoanStatus> allowedLoanStatuses = Arrays.asList(LoanStatus.values());
        return new DefaultLoanLifecycleStateMachine(allowedLoanStatuses);
    }

    @Transactional
    @Override
    public CommandProcessingResult submitApplication(final JsonCommand command) {
        try {
            final Long productId = this.fromJsonHelper.extractLongNamed("productId", command.parsedJson());
            final LoanProduct loanProduct = this.loanProductRepository.findOne(productId);
            if (loanProduct == null) { throw new LoanProductNotFoundException(productId); }
            final Boolean isPenalty = false;
            final List<Map<String, Object>> chargeIdList = this.loanProductReadPlatformService.getLoanProductMandatoryCharges(productId,
                    isPenalty);
            this.loanProductBusinessRuleValidator.validateLoanProductMandatoryCharges(chargeIdList, command.parsedJson());
            final Loan newLoanApplication = validateAndAssembleSubmitLoanApplication(loanProduct, command);
            
            final Long clientId = this.fromJsonHelper.extractLongNamed("clientId", command.parsedJson());
            if (clientId != null) {
                Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
                officeSpecificLoanProductValidation(productId, client.getOffice().getId());
            }
            final Long groupId = this.fromJsonHelper.extractLongNamed("groupId", command.parsedJson());
            if (groupId != null) {
                Group group = this.groupRepository.findOneWithNotFoundDetection(groupId);
                officeSpecificLoanProductValidation(productId, group.getOffice().getId());
            }
            
            if (!command.parameterExists("isonlyloanappcreate")) {
                this.loanRepository.save(newLoanApplication);
            }

            if(loanProduct.canUseForTopup() && clientId != null){
                final Boolean isTopup = command.booleanObjectValueOfParameterNamed(LoanApiConstants.isTopup);
                if(null == isTopup){
                    newLoanApplication.setIsTopup(false);
                }else{
                    newLoanApplication.setIsTopup(isTopup);
                }

                if(newLoanApplication.isTopup()){
                    final Long loanIdToClose = command.longValueOfParameterNamed(LoanApiConstants.loanIdToClose);
                    final Loan loanToClose = this.loanRepository.findNonClosedLoanThatBelongsToClient(loanIdToClose, clientId);
                    if(loanToClose == null){
                        throw new GeneralPlatformDomainRuleException("error.msg.loan.loanIdToClose.no.active.loan.associated.to.client.found",
                                "loanIdToClose is invalid, No Active Loan associated with the given Client ID found.");
                    }
                    if(loanToClose.isMultiDisburmentLoan() && !loanToClose.isInterestRecalculationEnabledForProduct()){
                        throw new GeneralPlatformDomainRuleException(
                                "error.msg.loan.topup.on.multi.tranche.loan.without.interest.recalculation.not.supported",
                                "Topup on loan with multi-tranche disbursal and without interest recalculation is not supported.");
                    }
                    final LocalDate disbursalDateOfLoanToClose = loanToClose.getDisbursementDate();
                    if(!newLoanApplication.getSubmittedOnDate().isAfter(disbursalDateOfLoanToClose)){
                        throw new GeneralPlatformDomainRuleException(
                                "error.msg.loan.submitted.date.should.be.after.topup.loan.disbursal.date",
                                "Submitted date of this loan application "+newLoanApplication.getSubmittedOnDate()
                                        +" should be after the disbursed date of loan to be closed "+ disbursalDateOfLoanToClose);
                    }
                    if(!loanToClose.getCurrencyCode().equals(newLoanApplication.getCurrencyCode())){
                        throw new GeneralPlatformDomainRuleException("error.msg.loan.to.be.closed.has.different.currency",
                                "loanIdToClose is invalid, Currency code is different.");
                    }
                    final LocalDate lastUserTransactionOnLoanToClose = loanToClose.getLastUserTransactionDate();
                    if(!newLoanApplication.getDisbursementDate().isAfter(lastUserTransactionOnLoanToClose)){
                        throw new GeneralPlatformDomainRuleException(
                                "error.msg.loan.disbursal.date.should.be.after.last.transaction.date.of.loan.to.be.closed",
                                "Disbursal date of this loan application "+newLoanApplication.getDisbursementDate()
                                        +" should be after last transaction date of loan to be closed "+ lastUserTransactionOnLoanToClose);
                    }
                    BigDecimal loanOutstanding = this.loanReadPlatformService.retrieveLoanPrePaymentTemplate(loanIdToClose,
                            newLoanApplication.getDisbursementDate()).getAmount();
                    final BigDecimal firstDisbursalAmount = newLoanApplication.getFirstDisbursalAmount();
                    if(loanOutstanding.compareTo(firstDisbursalAmount) > 0){
                        throw new GeneralPlatformDomainRuleException("error.msg.loan.amount.less.than.outstanding.of.loan.to.be.closed",
                                "Topup loan amount should be greater than outstanding amount of loan to be closed.");
                    }

                    final LoanTopupDetails topupDetails = new LoanTopupDetails(newLoanApplication, loanIdToClose);
                    newLoanApplication.setTopupLoanDetails(topupDetails);
                }
            }

            this.loanRepository.save(newLoanApplication);

            if (loanProduct.isInterestRecalculationEnabled()) {
                this.fromApiJsonDeserializer.validateLoanForInterestRecalculation(newLoanApplication);
                createAndPersistCalendarInstanceForInterestRecalculation(newLoanApplication);
            }

            if (newLoanApplication.isAccountNumberRequiresAutoGeneration()) {
                final AccountNumberFormat accountNumberFormat = this.accountNumberFormatRepository
                        .findByAccountType(EntityAccountType.LOAN);
                newLoanApplication.updateAccountNo(this.accountNumberGenerator.generate(newLoanApplication, accountNumberFormat));
                this.loanRepository.save(newLoanApplication);
            }

            final String submittedOnNote = command.stringValueOfParameterNamed("submittedOnNote");
            if (StringUtils.isNotBlank(submittedOnNote)) {
                final Note note = Note.loanNote(newLoanApplication, submittedOnNote);
                this.noteRepository.save(note);
            }

            // Save calendar instance
            final Long calendarId = command.longValueOfParameterNamed("calendarId");
            Calendar calendar = null;

            if (calendarId != null && calendarId != 0) {
                calendar = this.calendarRepository.findOne(calendarId);
                if (calendar == null) { throw new CalendarNotFoundException(calendarId); }

                final CalendarInstance calendarInstance = new CalendarInstance(calendar, newLoanApplication.getId(),
                        CalendarEntityType.LOANS.getValue());
                this.calendarInstanceRepository.save(calendarInstance);
            } else {
                final boolean considerAllDisbursmentsInSchedule = true;
                final LoanApplicationTerms loanApplicationTerms = this.loanScheduleAssembler.assembleLoanTerms(command.parsedJson(), considerAllDisbursmentsInSchedule);
                final Integer repaymentFrequencyNthDayType = command.integerValueOfParameterNamed("repaymentFrequencyNthDayType");
                if (loanApplicationTerms.getRepaymentPeriodFrequencyType() == PeriodFrequencyType.MONTHS
                        && repaymentFrequencyNthDayType != null) {
                    final String title = "loan_schedule_" + newLoanApplication.getId();
                    LocalDate calendarStartDate = loanApplicationTerms.getRepaymentsStartingFromLocalDate();
                    if (calendarStartDate == null) calendarStartDate = loanApplicationTerms.getExpectedDisbursementDate();
                    final CalendarFrequencyType calendarFrequencyType = CalendarFrequencyType.MONTHLY;
                    final Integer frequency = loanApplicationTerms.getRepaymentEvery();
                    final Integer repeatsOnDay = loanApplicationTerms.getWeekDayType().getValue();
                    final Integer repeatsOnNthDayOfMonth = loanApplicationTerms.getNthDay();
                    final Integer calendarEntityType = CalendarEntityType.LOANS.getValue();
                    final Calendar loanCalendar = Calendar.createRepeatingCalendar(title, calendarStartDate,
                            CalendarType.COLLECTION.getValue(), calendarFrequencyType, frequency, repeatsOnDay, repeatsOnNthDayOfMonth);
                    this.calendarRepository.save(loanCalendar);
                    final CalendarInstance calendarInstance = CalendarInstance.from(loanCalendar, newLoanApplication.getId(),
                            calendarEntityType);
                    this.calendarInstanceRepository.save(calendarInstance);
                }
            }

            // Save linked account information
            final Long savingsAccountId = command.longValueOfParameterNamed("linkAccountId");
            if (savingsAccountId != null) {
                final SavingsAccount savingsAccount = this.savingsAccountAssembler.assembleFrom(savingsAccountId);
                this.fromApiJsonDeserializer.validatelinkedSavingsAccount(savingsAccount, newLoanApplication);
                boolean isActive = true;
                final AccountAssociations accountAssociations = AccountAssociations.associateSavingsAccount(newLoanApplication,
                        savingsAccount, AccountAssociationType.LINKED_ACCOUNT_ASSOCIATION.getValue(), isActive);
                this.accountAssociationsRepository.save(accountAssociations);
            }
            
            // save GroupLoanIndividualMonitoring clients
            final List<GroupLoanIndividualMonitoring> glimList = newLoanApplication.getGroupLoanIndividualMonitoringList();
            this.groupLoanIndividualMonitoringRepository.save(glimList);
            
            attachLoanAccountToPledge(command, newLoanApplication);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(newLoanApplication.getId()) //
                    .withOfficeId(newLoanApplication.getOfficeId()) //
                    .withClientId(newLoanApplication.getClientId()) //
                    .withGroupId(newLoanApplication.getGroupId()) //
                    .withLoanId(newLoanApplication.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }


    private Loan validateAndAssembleSubmitLoanApplication(final LoanProduct loanProduct, final JsonCommand command) {

        final AppUser currentUser = getAppUserIfPresent();

        boolean isMeetingMandatoryForJLGLoans = configurationDomainService.isMeetingMandatoryForJLGLoans();
        this.fromApiJsonDeserializer.validateForCreate(command.json(), isMeetingMandatoryForJLGLoans, loanProduct);

        validateCollateralAmountWithPrincipal(command, loanProduct);

        // validate for glim application
        if (command.hasParameter(LoanApiConstants.clientMembersParamName)) {
            GroupLoanIndividualMonitoringDataValidator.validateForGroupLoanIndividualMonitoring(command,
                    LoanApiConstants.principalParamName);
        }

        // validate glim charges
        validateGlimCharges(command);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan");

        if (loanProduct.useBorrowerCycle()) {
            final Long clientId = this.fromJsonHelper.extractLongNamed("clientId", command.parsedJson());
            final Long groupId = this.fromJsonHelper.extractLongNamed("groupId", command.parsedJson());
            Integer cycleNumber = 0;
            if (clientId != null) {
                cycleNumber = this.loanReadPlatformService.retriveLoanCounter(clientId, loanProduct.getId());
            } else if (groupId != null) {
                cycleNumber = this.loanReadPlatformService.retriveLoanCounter(groupId, AccountType.GROUP.getValue(), loanProduct.getId());
            }
            this.loanProductCommandFromApiJsonDeserializer.validateMinMaxConstraints(command.parsedJson(), baseDataValidator, loanProduct,
                    cycleNumber);
        } else {
            this.loanProductCommandFromApiJsonDeserializer.validateMinMaxConstraints(command.parsedJson(), baseDataValidator, loanProduct);
        }
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

        final Loan newLoanApplication = this.loanAssembler.assembleFrom(command, currentUser);

        validateSubmittedOnDate(newLoanApplication.getSubmittedOnDate(), newLoanApplication.getExpectedFirstRepaymentOnDate(),
                newLoanApplication.loanProduct(), newLoanApplication.client());

        final LoanProductRelatedDetail productRelatedDetail = newLoanApplication.repaymentScheduleDetail();

        if (loanProduct.getLoanProductConfigurableAttributes() != null) {
            updateProductRelatedDetails(productRelatedDetail, newLoanApplication);
        }

        this.fromApiJsonDeserializer
                .validateLoanTermAndRepaidEveryValues(newLoanApplication.getTermFrequency(),
                        newLoanApplication.getTermPeriodFrequencyType(), productRelatedDetail.getNumberOfRepayments(),
                        productRelatedDetail.getRepayEvery(), productRelatedDetail.getRepaymentPeriodFrequencyType().getValue(),
                        newLoanApplication);

        return newLoanApplication;
    }
	
    private void validateGlimCharges(JsonCommand command) {
        final String loanTypeParameterName = "loanType";
        final String chargesParameterName = "charges";
        final String loanTypeStr = command.stringValueOfParameterNamed(loanTypeParameterName);
        BigDecimal totalUpfrontFeeAmount = BigDecimal.ZERO;
        boolean isUpfrontFee = false;
        if (command.hasParameter(chargesParameterName)) {
            JsonArray charges = command.arrayOfParameterNamed(LoanApiConstants.chargesParameterName);
            if (AccountType.fromName(loanTypeStr).isGLIMAccount()) {
            	int numberOfEmiRoundingGoalSeek = 0;
            	BigDecimal upfrontFeeAmount = BigDecimal.ZERO;
                for (JsonElement glimCharge : charges) {
                    JsonObject jsonObject = glimCharge.getAsJsonObject();
                    Long chargeId = jsonObject.get("chargeId").getAsLong();
                    BigDecimal chargeAmount = jsonObject.get("amount").getAsBigDecimal();
                    Charge charge = this.chargeRepositoryWrapper.findOneWithNotFoundDetection(chargeId);
                    if (charge.isEmiRoundingGoalSeek()) {
                        numberOfEmiRoundingGoalSeek++;
                    }
                    JsonArray upfrontCharges = this.fromJsonHelper.extractJsonArrayNamed(LoanApiConstants.upfrontChargesAmountParamName, glimCharge);
                    if (upfrontCharges != null && ChargeTimeType.fromInt(charge.getChargeTimeType().intValue()).isUpfrontFee()) {
                        isUpfrontFee = true;
                        upfrontFeeAmount = upfrontFeeAmount.add(chargeAmount);
                        for(JsonElement upfrontCharge : upfrontCharges) {
                            JsonObject jsonObj = upfrontCharge.getAsJsonObject();
                            totalUpfrontFeeAmount = totalUpfrontFeeAmount.add(jsonObj.get("upfrontChargeAmount").getAsBigDecimal());
                        }
                    }
                    
                    if (!charge.isGlimCharge()) {
                        final String entityName = " GLIM Loan";
                        final String userErrorMessage = "Charge can not be applied to GLIM Loan";
                        throw new ChargeNotSupportedException(entityName, charge.getId(), userErrorMessage);
                    }
                }
                if (numberOfEmiRoundingGoalSeek > 1) { throw new GlimLoanCannotHaveMoreThanOneGoalRoundSeekCharge(); }
                if (!(totalUpfrontFeeAmount.compareTo(upfrontFeeAmount) == 0) && isUpfrontFee) {
                    String entity = "GLIM Charge";
                    String errorMessage = "upfront.charges.is.not.equal.to.individual.glim.charges";
                    String defaultUserMessage = "Upfront Charges is not equal to individual glim charges";
                    throw new UpfrontChargeNotFoundException(entity, errorMessage, defaultUserMessage);
                }
            } else {
                for (JsonElement element : charges) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    Long chargeId = jsonObject.get("chargeId").getAsLong();
                    Charge charge = this.chargeRepositoryWrapper.findOneWithNotFoundDetection(chargeId);
                    performChargeTimeNCalculationTypeValidation(chargeId, charge.getChargeTimeType(), charge.getChargeCalculation());
                }
            }
            
        } 
        if (MathUtility.isGreaterThanZero(totalUpfrontFeeAmount) && !isUpfrontFee) {
            String entity = "GLIM Charge";
            String errorMessage = "upfront.charges.not.found.for.glim.loan";
            String defaultUserMessage = "Upfront Charges not found for glim loan";
            throw new UpfrontChargeNotFoundException(entity, errorMessage, defaultUserMessage);
        }
    }

    private void performChargeTimeNCalculationTypeValidation(Long chargeId, Integer chargeTimeType, Integer chargeCalculation) {
        if(chargeTimeType.equals(ChargeTimeType.INSTALMENT_FEE.getValue()) && 
                chargeCalculation.equals(ChargeCalculationType.PERCENT_OF_DISBURSEMENT_AMOUNT.getValue())) {
            final String entityName = "Loan";
            final String userErrorMessage = "Charge can be applied only to GLIM Loan";
            throw new ChargeNotSupportedException(entityName, chargeId, userErrorMessage);
        }
        
    }

    private void updateProductRelatedDetails(LoanProductRelatedDetail productRelatedDetail, Loan loan) {
        final Boolean amortization = loan.loanProduct().getLoanProductConfigurableAttributes().getAmortizationBoolean();
        final Boolean arrearsTolerance = loan.loanProduct().getLoanProductConfigurableAttributes().getArrearsToleranceBoolean();
        final Boolean graceOnArrearsAging = loan.loanProduct().getLoanProductConfigurableAttributes().getGraceOnArrearsAgingBoolean();
        final Boolean interestCalcPeriod = loan.loanProduct().getLoanProductConfigurableAttributes().getInterestCalcPeriodBoolean();
        final Boolean interestMethod = loan.loanProduct().getLoanProductConfigurableAttributes().getInterestMethodBoolean();
        final Boolean graceOnPrincipalAndInterestPayment = loan.loanProduct().getLoanProductConfigurableAttributes()
                .getGraceOnPrincipalAndInterestPaymentBoolean();
        final Boolean repaymentEvery = loan.loanProduct().getLoanProductConfigurableAttributes().getRepaymentEveryBoolean();
        final Boolean transactionProcessingStrategy = loan.loanProduct().getLoanProductConfigurableAttributes()
                .getTransactionProcessingStrategyBoolean();

        if (!amortization) {
            productRelatedDetail.setAmortizationMethod(loan.loanProduct().getLoanProductRelatedDetail().getAmortizationMethod());
        }
        if (!arrearsTolerance) {
            productRelatedDetail.setInArrearsTolerance(loan.loanProduct().getLoanProductRelatedDetail().getArrearsTolerance());
        }
        if (!graceOnArrearsAging) {
            productRelatedDetail.setGraceOnArrearsAgeing(loan.loanProduct().getLoanProductRelatedDetail().getGraceOnArrearsAgeing());
        }
        if (!interestCalcPeriod) {
            productRelatedDetail.setInterestCalculationPeriodMethod(loan.loanProduct().getLoanProductRelatedDetail()
                    .getInterestCalculationPeriodMethod());
        }
        if (!interestMethod) {
            productRelatedDetail.setInterestMethod(loan.loanProduct().getLoanProductRelatedDetail().getInterestMethod());
        }
        if (!graceOnPrincipalAndInterestPayment) {
            productRelatedDetail.setGraceOnInterestPayment(loan.loanProduct().getLoanProductRelatedDetail().getGraceOnInterestPayment());
            productRelatedDetail.setGraceOnPrincipalPayment(loan.loanProduct().getLoanProductRelatedDetail().getGraceOnPrincipalPayment());
        }
        if (!repaymentEvery) {
            productRelatedDetail.setRepayEvery(loan.loanProduct().getLoanProductRelatedDetail().getRepayEvery());
        }
        if (!transactionProcessingStrategy) {
            loan.updateTransactionProcessingStrategy(loan.loanProduct().getRepaymentStrategy());
        }
    }

    private void createAndPersistCalendarInstanceForInterestRecalculation(final Loan loan) {

        LocalDate calendarStartDate = loan.getExpectedDisbursedOnLocalDate();
        Integer repeatsOnDay = null;
        final RecalculationFrequencyType recalculationFrequencyType = loan.loanInterestRecalculationDetails().getRestFrequencyType();
        Integer recalculationFrequencyNthDay = loan.loanInterestRecalculationDetails().getRestFrequencyOnDay();
        if (recalculationFrequencyNthDay == null) {
            recalculationFrequencyNthDay = loan.loanInterestRecalculationDetails().getRestFrequencyNthDay();
            repeatsOnDay = loan.loanInterestRecalculationDetails().getRestFrequencyWeekday();
        }

        Integer frequency = loan.loanInterestRecalculationDetails().getRestInterval();
        CalendarEntityType calendarEntityType = CalendarEntityType.LOAN_RECALCULATION_REST_DETAIL;
        final String title = "loan_recalculation_detail_" + loan.loanInterestRecalculationDetails().getId();

        createCalendar(loan, calendarStartDate, recalculationFrequencyNthDay, repeatsOnDay, recalculationFrequencyType, frequency,
                calendarEntityType, title);

        if (loan.loanInterestRecalculationDetails().getInterestRecalculationCompoundingMethod().isCompoundingEnabled()) {
            LocalDate compoundingStartDate = loan.getExpectedDisbursedOnLocalDate();
            Integer compoundingRepeatsOnDay = null;
            final RecalculationFrequencyType recalculationCompoundingFrequencyType = loan.loanInterestRecalculationDetails()
                    .getCompoundingFrequencyType();
            Integer recalculationCompoundingFrequencyNthDay = loan.loanInterestRecalculationDetails().getCompoundingFrequencyOnDay();
            if (recalculationCompoundingFrequencyNthDay == null) {
                recalculationCompoundingFrequencyNthDay = loan.loanInterestRecalculationDetails().getCompoundingFrequencyNthDay();
                compoundingRepeatsOnDay = loan.loanInterestRecalculationDetails().getCompoundingFrequencyWeekday();
            }

            Integer compoundingFrequency = loan.loanInterestRecalculationDetails().getCompoundingInterval();
            CalendarEntityType compoundingCalendarEntityType = CalendarEntityType.LOAN_RECALCULATION_COMPOUNDING_DETAIL;
            final String compoundingCalendarTitle = "loan_recalculation_detail_compounding_frequency"
                    + loan.loanInterestRecalculationDetails().getId();

            createCalendar(loan, compoundingStartDate, recalculationCompoundingFrequencyNthDay, compoundingRepeatsOnDay,
                    recalculationCompoundingFrequencyType, compoundingFrequency, compoundingCalendarEntityType, compoundingCalendarTitle);
        }

    }

    private void createCalendar(final Loan loan, LocalDate calendarStartDate, Integer recalculationFrequencyNthDay,
            final Integer repeatsOnDay, final RecalculationFrequencyType recalculationFrequencyType, Integer frequency,
            CalendarEntityType calendarEntityType, final String title) {
        CalendarFrequencyType calendarFrequencyType = CalendarFrequencyType.INVALID;
        Integer updatedRepeatsOnDay = repeatsOnDay;
        switch (recalculationFrequencyType) {
            case DAILY:
                calendarFrequencyType = CalendarFrequencyType.DAILY;
            break;
            case MONTHLY:
                calendarFrequencyType = CalendarFrequencyType.MONTHLY;
            break;
            case SAME_AS_REPAYMENT_PERIOD:
                frequency = loan.repaymentScheduleDetail().getRepayEvery();
                calendarFrequencyType = CalendarFrequencyType.from(loan.repaymentScheduleDetail().getRepaymentPeriodFrequencyType());
                calendarStartDate = loan.getExpectedDisbursedOnLocalDate();
                if (updatedRepeatsOnDay == null) {
                    updatedRepeatsOnDay = calendarStartDate.getDayOfWeek();
                }
            break;
            case WEEKLY:
                calendarFrequencyType = CalendarFrequencyType.WEEKLY;
            break;
            default:
            break;
        }

        final Calendar calendar = Calendar.createRepeatingCalendar(title, calendarStartDate, CalendarType.COLLECTION.getValue(),
                calendarFrequencyType, frequency, updatedRepeatsOnDay, recalculationFrequencyNthDay);
        final CalendarInstance calendarInstance = CalendarInstance.from(calendar, loan.loanInterestRecalculationDetails().getId(),
                calendarEntityType.getValue());
        this.calendarInstanceRepository.save(calendarInstance);
    }

    @Transactional
    @Override
    public CommandProcessingResult modifyApplication(final Long loanId, final JsonCommand command) {

        try {
            AppUser currentUser = getAppUserIfPresent();
            final Loan existingLoanApplication = retrieveLoanBy(loanId);
            if (!existingLoanApplication.isSubmittedAndPendingApproval()) { throw new LoanApplicationNotInSubmittedAndPendingApprovalStateCannotBeModified(
                    loanId); }

            final String productIdParamName = "productId";
            LoanProduct newLoanProduct = null;
            if (command.isChangeInLongParameterNamed(productIdParamName, existingLoanApplication.loanProduct().getId())) {
                final Long productId = command.longValueOfParameterNamed(productIdParamName);
                newLoanProduct = this.loanProductRepository.findOne(productId);
                if (newLoanProduct == null) { throw new LoanProductNotFoundException(productId); }
            }

            LoanProduct loanProductForValidations = newLoanProduct == null ? existingLoanApplication.loanProduct() : newLoanProduct;
            final Boolean isPenalty = false;
            final List<Map<String, Object>> chargeIdList = this.loanProductReadPlatformService.getLoanProductMandatoryCharges(
                    loanProductForValidations.getId(), isPenalty);
            this.loanProductBusinessRuleValidator.validateLoanProductMandatoryCharges(chargeIdList, command.parsedJson());
            this.fromApiJsonDeserializer.validateForModify(command.json(), loanProductForValidations, existingLoanApplication);
            
            checkClientOrGroupActive(existingLoanApplication);
            LoanProduct product = existingLoanApplication.loanProduct();

            validateCollateralAmount(command, product);
            
            //validate for glim application
            if(command.hasParameter(LoanApiConstants.clientMembersParamName)){
            	GroupLoanIndividualMonitoringDataValidator.validateForGroupLoanIndividualMonitoring(command, LoanApiConstants.principalParamName);
            	// validate glim charges
                validateGlimCharges(command);
            }

            final Set<LoanCharge> existingCharges = existingLoanApplication.charges();
            Map<Long, LoanChargeData> chargesMap = new HashMap<>();
            for (LoanCharge charge : existingCharges) {
                LoanChargeData chargeData = new LoanChargeData(charge.getId(), charge.getDueLocalDate(), charge.amountOrPercentage());
                chargesMap.put(charge.getId(), chargeData);
            }
            Set<LoanDisbursementDetails> disbursementDetails = this.loanUtilService.fetchDisbursementData(command.parsedJson()
                    .getAsJsonObject());

            /**
             * Stores all charges which are passed in during modify loan
             * application
             **/
            final Set<LoanCharge> possiblyModifedLoanCharges = this.loanChargeAssembler.fromParsedJson(command.parsedJson(),
                    disbursementDetails);
            /** Boolean determines if any charge has been modified **/
            boolean isChargeModified = false;

            Set<Charge> newTrancheChages = this.loanChargeAssembler.getNewLoanTrancheCharges(command.parsedJson());
            for (Charge charge : newTrancheChages) {
                existingLoanApplication.addTrancheLoanCharge(charge);
            }

            /**
             * If there are any charges already present, which are now not
             * passed in as a part of the request, deem the charges as modified
             **/
            if (!possiblyModifedLoanCharges.isEmpty()) {
                if (!possiblyModifedLoanCharges.containsAll(existingCharges)) {
                    isChargeModified = true;
                }
            }

            /**
             * If any new charges are added or values of existing charges are
             * modified
             **/
            for (LoanCharge loanCharge : possiblyModifedLoanCharges) {
                if (loanCharge.getId() == null) {
                    isChargeModified = true;
                } else {
                    LoanChargeData chargeData = chargesMap.get(loanCharge.getId());
                    if (loanCharge.amountOrPercentage().compareTo(chargeData.amountOrPercentage()) != 0
                            || (loanCharge.isSpecifiedDueDate() && !loanCharge.getDueLocalDate().equals(chargeData.getDueDate()))) {
                        isChargeModified = true;
                    }
                }
            }

            final Set<LoanCollateral> possiblyModifedLoanCollateralItems = this.loanCollateralAssembler
                    .fromParsedJson(command.parsedJson());


            final Map<String, Object> changes = existingLoanApplication.loanApplicationModification(command, possiblyModifedLoanCharges,
                    possiblyModifedLoanCollateralItems, this.aprCalculator, isChargeModified);

            if (changes.containsKey("expectedDisbursementDate")) {
                this.loanAssembler.validateExpectedDisbursementForHolidayAndNonWorkingDay(existingLoanApplication);
            }

            final String clientIdParamName = "clientId";
            if (changes.containsKey(clientIdParamName)) {
                final Long clientId = command.longValueOfParameterNamed(clientIdParamName);
                final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
                if (client.isNotActive()) { throw new ClientNotActiveException(clientId); }

                existingLoanApplication.updateClient(client);
            }

            final String groupIdParamName = "groupId";
            if (changes.containsKey(groupIdParamName)) {
                final Long groupId = command.longValueOfParameterNamed(groupIdParamName);
                final Group group = this.groupRepository.findOneWithNotFoundDetection(groupId);
                if (group.isNotActive()) { throw new GroupNotActiveException(groupId); }

                existingLoanApplication.updateGroup(group);
            }

            if (newLoanProduct != null) {
                existingLoanApplication.updateLoanProduct(newLoanProduct);
                if (!changes.containsKey("interestRateFrequencyType")) {
                    existingLoanApplication.updateInterestRateFrequencyType();
                }
                final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
                final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan");
                if (newLoanProduct.useBorrowerCycle()) {
                    final Long clientId = this.fromJsonHelper.extractLongNamed("clientId", command.parsedJson());
                    final Long groupId = this.fromJsonHelper.extractLongNamed("groupId", command.parsedJson());
                    Integer cycleNumber = 0;
                    if (clientId != null) {
                        cycleNumber = this.loanReadPlatformService.retriveLoanCounter(clientId, newLoanProduct.getId());
                    } else if (groupId != null) {
                        cycleNumber = this.loanReadPlatformService.retriveLoanCounter(groupId, AccountType.GROUP.getValue(),
                                newLoanProduct.getId());
                    }
                    this.loanProductCommandFromApiJsonDeserializer.validateMinMaxConstraints(command.parsedJson(), baseDataValidator,
                            newLoanProduct, cycleNumber);
                } else {
                    this.loanProductCommandFromApiJsonDeserializer.validateMinMaxConstraints(command.parsedJson(), baseDataValidator,
                            newLoanProduct);
                }
                if (newLoanProduct.isLinkedToFloatingInterestRate()) {
                    existingLoanApplication.getLoanProductRelatedDetail().updateForFloatingInterestRates();
                } else {
                    existingLoanApplication.setInterestRateDifferential(null);
                    existingLoanApplication.setIsFloatingInterestRate(null);
                }
                if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
            }

            existingLoanApplication.updateIsInterestRecalculationEnabled();

            validateSubmittedOnDate(existingLoanApplication.getSubmittedOnDate(),
                    existingLoanApplication.getExpectedFirstRepaymentOnDate(), existingLoanApplication.loanProduct(),
                    existingLoanApplication.client());

            final LoanProductRelatedDetail productRelatedDetail = existingLoanApplication.repaymentScheduleDetail();
            if (existingLoanApplication.loanProduct().getLoanProductConfigurableAttributes() != null) {
                updateProductRelatedDetails(productRelatedDetail, existingLoanApplication);
            }

            if(existingLoanApplication.getLoanProduct().canUseForTopup() && existingLoanApplication.getClientId() != null){
                final Boolean isTopup = command.booleanObjectValueOfParameterNamed(LoanApiConstants.isTopup);
                if(command.isChangeInBooleanParameterNamed(LoanApiConstants.isTopup, existingLoanApplication.isTopup())){
                    existingLoanApplication.setIsTopup(isTopup);
                    changes.put(LoanApiConstants.isTopup, isTopup);
                }

                if(existingLoanApplication.isTopup()){
                    final Long loanIdToClose = command.longValueOfParameterNamed(LoanApiConstants.loanIdToClose);
                    LoanTopupDetails existingLoanTopupDetails = existingLoanApplication.getTopupLoanDetails();
                    if(existingLoanTopupDetails == null
                            || (existingLoanTopupDetails != null && existingLoanTopupDetails.getLoanIdToClose() != loanIdToClose)
                            || changes.containsKey("submittedOnDate")
                            || changes.containsKey("expectedDisbursementDate")
                            || changes.containsKey("principal")
                            || changes.containsKey(LoanApiConstants.disbursementDataParameterName)){
                        Long existingLoanIdToClose = null;
                        if(existingLoanTopupDetails != null){
                            existingLoanIdToClose = existingLoanTopupDetails.getLoanIdToClose();
                        }
                        final Loan loanToClose = this.loanRepository.findNonClosedLoanThatBelongsToClient(loanIdToClose, existingLoanApplication.getClientId());
                        if(loanToClose == null){
                            throw new GeneralPlatformDomainRuleException("error.msg.loan.loanIdToClose.no.active.loan.associated.to.client.found",
                                    "loanIdToClose is invalid, No Active Loan associated with the given Client ID found.");
                        }
                        if(loanToClose.isMultiDisburmentLoan() && !loanToClose.isInterestRecalculationEnabledForProduct()){
                            throw new GeneralPlatformDomainRuleException("error.msg.loan.topup.on.multi.tranche.loan.without.interest.recalculation.not.supported",
                                    "Topup on loan with multi-tranche disbursal and without interest recalculation is not supported.");
                        }
                        final LocalDate disbursalDateOfLoanToClose = loanToClose.getDisbursementDate();
                        if(!existingLoanApplication.getSubmittedOnDate().isAfter(disbursalDateOfLoanToClose)){
                            throw new GeneralPlatformDomainRuleException(
                                    "error.msg.loan.submitted.date.should.be.after.topup.loan.disbursal.date",
                                    "Submitted date of this loan application "+existingLoanApplication.getSubmittedOnDate()
                                            +" should be after the disbursed date of loan to be closed "+ disbursalDateOfLoanToClose);
                        }
                        if(!loanToClose.getCurrencyCode().equals(existingLoanApplication.getCurrencyCode())){
                            throw new GeneralPlatformDomainRuleException("error.msg.loan.to.be.closed.has.different.currency",
                                    "loanIdToClose is invalid, Currency code is different.");
                        }
                        final LocalDate lastUserTransactionOnLoanToClose = loanToClose.getLastUserTransactionDate();
                        if(!existingLoanApplication.getDisbursementDate().isAfter(lastUserTransactionOnLoanToClose)){
                            throw new GeneralPlatformDomainRuleException(
                                    "error.msg.loan.disbursal.date.should.be.after.last.transaction.date.of.loan.to.be.closed",
                                    "Disbursal date of this loan application "+existingLoanApplication.getDisbursementDate()
                                            +" should be after last transaction date of loan to be closed "+ lastUserTransactionOnLoanToClose);
                        }
                        BigDecimal loanOutstanding = this.loanReadPlatformService.retrieveLoanPrePaymentTemplate(loanIdToClose,
                                existingLoanApplication.getDisbursementDate()).getAmount();
                        final BigDecimal firstDisbursalAmount = existingLoanApplication.getFirstDisbursalAmount();
                        if(loanOutstanding.compareTo(firstDisbursalAmount) > 0){
                            throw new GeneralPlatformDomainRuleException("error.msg.loan.amount.less.than.outstanding.of.loan.to.be.closed",
                                    "Topup loan amount should be greater than outstanding amount of loan to be closed.");
                        }

                        if(existingLoanIdToClose != loanIdToClose){
                            final LoanTopupDetails topupDetails = new LoanTopupDetails(existingLoanApplication, loanIdToClose);
                            existingLoanApplication.setTopupLoanDetails(topupDetails);
                            changes.put(LoanApiConstants.loanIdToClose, loanIdToClose);
                        }
                    }
                }else{
                    existingLoanApplication.setTopupLoanDetails(null);
                }
            } else {
                if(existingLoanApplication.isTopup()){
                    existingLoanApplication.setIsTopup(false);
                    existingLoanApplication.setTopupLoanDetails(null);
                    changes.put(LoanApiConstants.isTopup, false);
                }
            }


            final String fundIdParamName = "fundId";
            if (changes.containsKey(fundIdParamName)) {
                final Long fundId = command.longValueOfParameterNamed(fundIdParamName);
                final Fund fund = this.loanAssembler.findFundByIdIfProvided(fundId);

                existingLoanApplication.updateFund(fund);
            }

            final String loanPurposeIdParamName = "loanPurposeId";
            if (changes.containsKey(loanPurposeIdParamName)) {
                final Long loanPurposeId = command.longValueOfParameterNamed(loanPurposeIdParamName);
                final LoanPurpose loanPurpose = this.loanAssembler.findLoanPurposeByIdIfProvided(loanPurposeId);
                existingLoanApplication.updateLoanPurpose(loanPurpose);
            }

            final String loanOfficerIdParamName = "loanOfficerId";
            if (changes.containsKey(loanOfficerIdParamName)) {
                final Long loanOfficerId = command.longValueOfParameterNamed(loanOfficerIdParamName);
                final Staff newValue = this.loanAssembler.findLoanOfficerByIdIfProvided(loanOfficerId);
                existingLoanApplication.updateLoanOfficerOnLoanApplication(newValue);
            }

            final String strategyIdParamName = "transactionProcessingStrategyId";
            if (changes.containsKey(strategyIdParamName)) {
                final Long strategyId = command.longValueOfParameterNamed(strategyIdParamName);
                final LoanTransactionProcessingStrategy strategy = this.loanAssembler.findStrategyByIdIfProvided(strategyId);

                existingLoanApplication.updateTransactionProcessingStrategy(strategy);
            }

            final String collateralParamName = "collateral";
            if (changes.containsKey(collateralParamName)) {
                final Set<LoanCollateral> loanCollateral = this.loanCollateralAssembler.fromParsedJson(command.parsedJson());
                existingLoanApplication.updateLoanCollateral(loanCollateral);
            }

            final String chargesParamName = "charges";
            if (changes.containsKey(chargesParamName)) {
                existingLoanApplication.updateLoanCharges(possiblyModifedLoanCharges);
            }
            
            final BigDecimal interestRate = existingLoanApplication.getLoanProductRelatedDetail().getAnnualNominalInterestRate();
            final Integer numberOfRepayments = existingLoanApplication.fetchNumberOfInstallmensAfterExceptions();
            List<GroupLoanIndividualMonitoring> glimList = new ArrayList<GroupLoanIndividualMonitoring>();
            // modify glim data            
            if(command.hasParameter(LoanApiConstants.clientMembersParamName)){
                glimList  = this.groupLoanIndividualMonitoringAssembler.createOrUpdateIndividualClientsAmountSplit(existingLoanApplication, command.parsedJson(), interestRate,
                        numberOfRepayments);
		
		        List<GroupLoanIndividualMonitoring> existingGlimList = this.groupLoanIndividualMonitoringRepository.findByLoanId(loanId);
		        if (!existingGlimList.isEmpty()) {
		            this.groupLoanIndividualMonitoringRepository.delete(existingGlimList);
		        }
            }       
            	        
	        if (changes.containsKey("recalculateLoanSchedule")) {
	            changes.remove("recalculateLoanSchedule");
	
	            final JsonElement parsedQuery = this.fromJsonHelper.parse(command.json());
	            final JsonQuery query = JsonQuery.from(command.json(), parsedQuery, this.fromJsonHelper);
	            existingLoanApplication.updateGlim(glimList);
	            final boolean considerAllDisbursmentsInSchedule = true;
	            final LoanScheduleModel loanSchedule = this.calculationPlatformService.calculateLoanSchedule(query, false, considerAllDisbursmentsInSchedule);
	            existingLoanApplication.updateLoanSchedule(loanSchedule, currentUser);
	            existingLoanApplication.recalculateAllCharges();
	            // save GroupLoanIndividualMonitoring clients
	            if(glimList.size()>0){
	            	this.groupLoanIndividualMonitoringRepository.save(glimList);
	            }
	            
	        }

            this.fromApiJsonDeserializer.validateLoanTermAndRepaidEveryValues(existingLoanApplication.getTermFrequency(),
                    existingLoanApplication.getTermPeriodFrequencyType(), productRelatedDetail.getNumberOfRepayments(),
                    productRelatedDetail.getRepayEvery(), productRelatedDetail.getRepaymentPeriodFrequencyType().getValue(),
                    existingLoanApplication);

            saveAndFlushLoanWithDataIntegrityViolationChecks(existingLoanApplication);

            final String submittedOnNote = command.stringValueOfParameterNamed("submittedOnNote");
            if (StringUtils.isNotBlank(submittedOnNote)) {
                final Note note = Note.loanNote(existingLoanApplication, submittedOnNote);
                this.noteRepository.save(note);
            }

            final Long calendarId = command.longValueOfParameterNamed("calendarId");
            Calendar calendar = null;
            if (calendarId != null && calendarId != 0) {
                calendar = this.calendarRepository.findOne(calendarId);
                if (calendar == null) { throw new CalendarNotFoundException(calendarId); }
            }

            final List<CalendarInstance> ciList = (List<CalendarInstance>) this.calendarInstanceRepository.findByEntityIdAndEntityTypeId(
                    loanId, CalendarEntityType.LOANS.getValue());
            if (calendar != null) {

                // For loans, allow to attach only one calendar instance per
                // loan
                if (ciList != null && !ciList.isEmpty()) {
                    final CalendarInstance calendarInstance = ciList.get(0);
                    final boolean isCalendarAssociatedWithEntity = this.calendarReadPlatformService.isCalendarAssociatedWithEntity(calendarInstance
                            .getEntityId(), calendarInstance.getCalendar().getId(), CalendarEntityType.LOANS.getValue().longValue());
                    if (isCalendarAssociatedWithEntity) {
                        this.calendarRepository.delete(calendarInstance.getCalendar());
                    }
                    if (calendarInstance.getCalendar().getId() != calendar.getId()) {
                        calendarInstance.updateCalendar(calendar);
                        this.calendarInstanceRepository.saveAndFlush(calendarInstance);
                    }
                } else {
                    // attaching new calendar
                    final CalendarInstance calendarInstance = new CalendarInstance(calendar, existingLoanApplication.getId(),
                            CalendarEntityType.LOANS.getValue());
                    this.calendarInstanceRepository.save(calendarInstance);
                }

            } else {
                boolean instanceDeleted = false;
                if (ciList != null && !ciList.isEmpty()) {
                    final CalendarInstance existingCalendarInstance = ciList.get(0);
                    final boolean isCalendarAssociatedWithEntity = this.loanUtilService.isLoanRepaymentsSyncWithMeeting(
                            existingLoanApplication.getGroup(), existingCalendarInstance.getCalendar());
                    if (isCalendarAssociatedWithEntity) {
                        this.calendarInstanceRepository.delete(existingCalendarInstance);
                        instanceDeleted = true;
                    }
                }
                if (changes.containsKey("repaymentFrequencyNthDayType") || changes.containsKey("repaymentFrequencyDayOfWeekType")) {
                    if (changes.get("repaymentFrequencyNthDayType") == null) {
                        if (ciList != null && !ciList.isEmpty() && !instanceDeleted) {
                            final CalendarInstance calendarInstance = ciList.get(0);
                            final boolean isCalendarAssociatedWithEntity = this.calendarReadPlatformService.isCalendarAssociatedWithEntity(
                                    calendarInstance.getEntityId(), calendarInstance.getCalendar().getId(), CalendarEntityType.LOANS.getValue().longValue());
                            if (isCalendarAssociatedWithEntity) {
                                this.calendarInstanceRepository.delete(calendarInstance);
                                this.calendarRepository.delete(calendarInstance.getCalendar());
                            }
                        }
                    } else {
                        Integer repaymentFrequencyTypeInt = command.integerValueOfParameterNamed("repaymentFrequencyType");
                        if (repaymentFrequencyTypeInt != null) {
                            if (PeriodFrequencyType.fromInt(repaymentFrequencyTypeInt) == PeriodFrequencyType.MONTHS) {
                                final String title = "loan_schedule_" + existingLoanApplication.getId();
                                final Integer typeId = CalendarType.COLLECTION.getValue();
                                final CalendarFrequencyType repaymentFrequencyType = CalendarFrequencyType.MONTHLY;
                                final Integer interval = command.integerValueOfParameterNamed("repaymentEvery");
                                LocalDate startDate = command.localDateValueOfParameterNamed("repaymentsStartingFromDate");
                                if (startDate == null) startDate = command.localDateValueOfParameterNamed("expectedDisbursementDate");
                                final Calendar newCalendar = Calendar.createRepeatingCalendar(title, startDate, typeId,
                                        repaymentFrequencyType, interval, (Integer) changes.get("repaymentFrequencyDayOfWeekType"),
                                        (Integer) changes.get("repaymentFrequencyNthDayType"));
                                if (ciList != null && !ciList.isEmpty()) {
                                    final CalendarInstance calendarInstance = ciList.get(0);
                                    final boolean isCalendarAssociatedWithEntity = this.calendarReadPlatformService.isCalendarAssociatedWithEntity(
                                            calendarInstance.getEntityId(), calendarInstance.getCalendar().getId(), CalendarEntityType.LOANS.getValue().longValue());
                                    if (isCalendarAssociatedWithEntity) {
                                        final Calendar existingCalendar = calendarInstance.getCalendar();
                                        if (existingCalendar != null) {
                                            String existingRecurrence = existingCalendar.getRecurrence();
                                            if (!existingRecurrence.equals(newCalendar.getRecurrence())) {
                                                existingCalendar.setRecurrence(newCalendar.getRecurrence());
                                                this.calendarRepository.save(existingCalendar);
                                            }
                                        }
                                    }
                                } else {
                                    this.calendarRepository.save(newCalendar);
                                    final Integer calendarEntityType = CalendarEntityType.LOANS.getValue();
                                    final CalendarInstance calendarInstance = new CalendarInstance(newCalendar,
                                            existingLoanApplication.getId(), calendarEntityType);
                                    this.calendarInstanceRepository.save(calendarInstance);
                                }
                            }
                        }
                    }
                }
            }

            // Save linked account information
            final String linkAccountIdParamName = "linkAccountId";
            final Long savingsAccountId = command.longValueOfParameterNamed(linkAccountIdParamName);
            AccountAssociations accountAssociations = this.accountAssociationsRepository.findByLoanIdAndType(loanId,
                    AccountAssociationType.LINKED_ACCOUNT_ASSOCIATION.getValue());
            boolean isLinkedAccPresent = false;
            if (savingsAccountId == null) {
                if (accountAssociations != null) {
                    if (this.fromJsonHelper.parameterExists(linkAccountIdParamName, command.parsedJson())) {
                        this.accountAssociationsRepository.delete(accountAssociations);
                        changes.put(linkAccountIdParamName, null);
                    } else {
                        isLinkedAccPresent = true;
                    }
                }
            } else {
                isLinkedAccPresent = true;
                boolean isModified = false;
                if (accountAssociations == null) {
                    isModified = true;
                } else {
                    final SavingsAccount savingsAccount = accountAssociations.linkedSavingsAccount();
                    if (savingsAccount == null || !savingsAccount.getId().equals(savingsAccountId)) {
                        isModified = true;
                    }
                }
                if (isModified) {
                    final SavingsAccount savingsAccount = this.savingsAccountAssembler.assembleFrom(savingsAccountId);
                    this.fromApiJsonDeserializer.validatelinkedSavingsAccount(savingsAccount, existingLoanApplication);
                    if (accountAssociations == null) {
                        boolean isActive = true;
                        accountAssociations = AccountAssociations.associateSavingsAccount(existingLoanApplication, savingsAccount,
                                AccountAssociationType.LINKED_ACCOUNT_ASSOCIATION.getValue(), isActive);
                    } else {
                        accountAssociations.updateLinkedSavingsAccount(savingsAccount);
                    }
                    changes.put(linkAccountIdParamName, savingsAccountId);
                    this.accountAssociationsRepository.save(accountAssociations);
                }
            }

            if (!isLinkedAccPresent) {
                final Set<LoanCharge> charges = existingLoanApplication.charges();
                for (final LoanCharge loanCharge : charges) {
                    if (loanCharge.getChargePaymentMode().isPaymentModeAccountTransfer()) {
                        final String errorMessage = "one of the charges requires linked savings account for payment";
                        throw new LinkedAccountRequiredException("loanCharge", errorMessage);
                    }
                }
            }
            
            if ((command.longValueOfParameterNamed(productIdParamName) != null)
                    || (command.longValueOfParameterNamed(clientIdParamName) != null)
                    || (command.longValueOfParameterNamed(groupIdParamName) != null)) {
                Long OfficeId = null;
                if (existingLoanApplication.getClient() != null) {
                    OfficeId = existingLoanApplication.getClient().getOffice().getId();
                } else if (existingLoanApplication.getGroup() != null) {
                    OfficeId = existingLoanApplication.getGroup().getOffice().getId();
                }
                officeSpecificLoanProductValidation(existingLoanApplication.getLoanProduct().getId(), OfficeId);
            }

            // updating loan interest recalculation details throwing null
            // pointer exception after saveAndFlush
            // http://stackoverflow.com/questions/17151757/hibernate-cascade-update-gives-null-pointer/17334374#17334374
            this.loanRepository.save(existingLoanApplication);

            validatePledgeForLoan(command, existingLoanApplication);

            if (productRelatedDetail.isInterestRecalculationEnabled()) {
                this.fromApiJsonDeserializer.validateLoanForInterestRecalculation(existingLoanApplication);
                if (changes.containsKey(LoanProductConstants.isInterestRecalculationEnabledParameterName)) {
                    createAndPersistCalendarInstanceForInterestRecalculation(existingLoanApplication);

                }

            }

            final AccountType loanType = AccountType.fromInt(existingLoanApplication.getLoanType());
            if ((loanType.isJLGAccount() || loanType.isGroupAccount()) && calendar != null) {
                final PeriodFrequencyType meetingPeriodFrequency = CalendarUtils.getMeetingPeriodFrequencyType(calendar.getRecurrence());
                final Integer repaymentFrequencyType = existingLoanApplication.getLoanProductRelatedDetail()
                        .getRepaymentPeriodFrequencyType().getValue();
                final Integer repaymentEvery = existingLoanApplication.getLoanProductRelatedDetail().getRepayEvery();
                this.loanScheduleAssembler.validateRepaymentFrequencyIsSameAsMeetingFrequency(meetingPeriodFrequency.getValue(),
                        repaymentFrequencyType, CalendarUtils.getInterval(calendar.getRecurrence()), repaymentEvery);
            }
            
            LocalDate repaymentsStartingFromDate = existingLoanApplication.getExpectedFirstRepaymentOnDate();
            if (repaymentsStartingFromDate != null) {
                LocalDate expectedDisbursementDate = existingLoanApplication.getExpectedDisbursedOnLocalDate();
                this.loanScheduleAssembler.validateMinimumDaysBetweenDisbursalAndFirstRepayment(repaymentsStartingFromDate,
                        existingLoanApplication, expectedDisbursementDate);
            }

            return new CommandProcessingResultBuilder() //
                    .withEntityId(loanId) //
                    .withOfficeId(existingLoanApplication.getOfficeId()) //
                    .withClientId(existingLoanApplication.getClientId()) //
                    .withGroupId(existingLoanApplication.getGroupId()) //
                    .withLoanId(existingLoanApplication.getId()) //
                    .with(changes).build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("loan_account_no_UNIQUE")) {

            final String accountNo = command.stringValueOfParameterNamed("accountNo");
            throw new PlatformDataIntegrityException("error.msg.loan.duplicate.accountNo", "Loan with accountNo `" + accountNo
                    + "` already exists", "accountNo", accountNo);
        } else if (realCause.getMessage().contains("loan_externalid_UNIQUE")) {

            final String externalId = command.stringValueOfParameterNamed("externalId");
            throw new PlatformDataIntegrityException("error.msg.loan.duplicate.externalId", "Loan with externalId `" + externalId
                    + "` already exists", "externalId", externalId);
        }

        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.unknown.data.integrity.issue", "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteApplication(final Long loanId) {

        final Loan loan = retrieveLoanBy(loanId);
        checkClientOrGroupActive(loan);

        if (loan.isNotSubmittedAndPendingApproval()) { throw new LoanApplicationNotInSubmittedAndPendingApprovalStateCannotBeDeleted(loanId); }
		if (loan.isGLIMLoan()) {
			List<GroupLoanIndividualMonitoring> glimMembers = this.groupLoanIndividualMonitoringRepository.findByLoanId(loan.getId());
			for(GroupLoanIndividualMonitoring glimMember : glimMembers) {
				this.groupLoanIndividualMonitoringChargeRepository.deleteInBatch(glimMember.getGroupLoanIndividualMonitoringCharges());
			}
			this.groupLoanIndividualMonitoringRepository.deleteInBatch(glimMembers);
		}

        final List<Note> relatedNotes = this.noteRepository.findByLoanId(loan.getId());
        this.noteRepository.deleteInBatch(relatedNotes);

        this.loanRepository.delete(loanId);

        return new CommandProcessingResultBuilder() //
                .withEntityId(loanId) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .withLoanId(loan.getId()) //
                .build();
    }

    public void validateMultiDisbursementData(final JsonCommand command, LocalDate expectedDisbursementDate) {
        final String json = command.json();
        final JsonElement element = this.fromJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan");
        final BigDecimal principal = this.fromJsonHelper.extractBigDecimalWithLocaleNamed("approvedLoanAmount", element);
        fromApiJsonDeserializer.validateLoanMultiDisbursementdate(element, baseDataValidator, expectedDisbursementDate, principal);
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    @Transactional
    @Override
    public CommandProcessingResult approveApplication(final Long loanId, final JsonCommand command) {

        final AppUser currentUser = getAppUserIfPresent();
        LocalDate expectedDisbursementDate = null;

        this.loanApplicationTransitionApiJsonValidator.validateApproval(command.json());

        final Loan loan = retrieveLoanBy(loanId);

        final JsonArray disbursementDataArray = command.arrayOfParameterNamed(LoanApiConstants.disbursementDataParameterName);

        expectedDisbursementDate = command.localDateValueOfParameterNamed(LoanApiConstants.disbursementDateParameterName);
        if (expectedDisbursementDate == null) {
            expectedDisbursementDate = loan.getExpectedDisbursedOnLocalDate();
        }
        if (loan.loanProduct().isMultiDisburseLoan()) {
            this.validateMultiDisbursementData(command, expectedDisbursementDate);
        }
        
        // validate for GLIM application
        if(command.hasParameter(LoanApiConstants.clientMembersParamName)){
        	GroupLoanIndividualMonitoringDataValidator.validateForGroupLoanIndividualMonitoring(command, LoanApiConstants.approvedLoanAmountParameterName);
        }

        checkClientOrGroupActive(loan);
        Boolean isSkipRepaymentOnFirstMonth = false;
        Integer numberOfDays = 0;
        // validate expected disbursement date against meeting date
        if (loan.isSyncDisbursementWithMeeting() && (loan.isGroupLoan() || loan.isJLGLoan())) {
            final CalendarInstance calendarInstance = this.calendarInstanceRepository.findCalendarInstaneByEntityId(loan.getId(),
                    CalendarEntityType.LOANS.getValue());
            final Calendar calendar = calendarInstance.getCalendar();
            boolean isSkipRepaymentOnFirstMonthEnabled = this.configurationDomainService.isSkippingMeetingOnFirstDayOfMonthEnabled();
            if (isSkipRepaymentOnFirstMonthEnabled) {
                isSkipRepaymentOnFirstMonth = this.loanUtilService.isLoanRepaymentsSyncWithMeeting(loan.group(), calendar);
                if (isSkipRepaymentOnFirstMonth) {
                    numberOfDays = configurationDomainService.retreivePeroidInNumberOfDaysForSkipMeetingDate().intValue();
                }
            }
            this.loanScheduleAssembler.validateDisbursementDateWithMeetingDates(expectedDisbursementDate, calendar,
                    isSkipRepaymentOnFirstMonth, numberOfDays);

        }

        final Map<String, Object> changes = loan.loanApplicationApproval(currentUser, command, disbursementDataArray,
                defaultLoanLifecycleStateMachine());

        if (!changes.isEmpty()) {
            
            // If loan approved amount less than loan demanded amount, then need
            // to recompute the schedule
            if (changes.containsKey(LoanApiConstants.approvedLoanAmountParameterName) || changes.containsKey("recalculateLoanSchedule")
                    || changes.containsKey("expectedDisbursementDate")) {
                if (loan.isGLIMLoan()) {
                    // update approved amount in glim
                    List<GroupLoanIndividualMonitoring> glimList = this.groupLoanIndividualMonitoringAssembler.updateFromJson(command
                            .parsedJson(), "approvedAmount", loan, loan.fetchNumberOfInstallmensAfterExceptions(), loan
                            .getLoanProductRelatedDetail().getAnnualNominalInterestRate());
                    loan.updateGlim(glimList);
                    loan.updateDefautGlimMembers(glimList);
                    this.groupLoanIndividualMonitoringAssembler.adjustRoundOffValuesToApplicableCharges(loan.charges(),
                            loan.fetchNumberOfInstallmensAfterExceptions(), glimList);
                }
                LocalDate recalculateFrom = null;
                ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);
                loan.regenerateRepaymentSchedule(scheduleGeneratorDTO, currentUser);
            }
            

            if(loan.isTopup() && loan.getClientId() != null){
                final Long loanIdToClose = loan.getTopupLoanDetails().getLoanIdToClose();
                final Loan loanToClose = this.loanRepository.findNonClosedLoanThatBelongsToClient(loanIdToClose, loan.getClientId());
                if(loanToClose == null){
                    throw new GeneralPlatformDomainRuleException("error.msg.loan.to.be.closed.with.topup.is.not.active",
                            "Loan to be closed with this topup is not active.");
                }

                final LocalDate lastUserTransactionOnLoanToClose = loanToClose.getLastUserTransactionDate();
                if(!loan.getDisbursementDate().isAfter(lastUserTransactionOnLoanToClose)){
                    throw new GeneralPlatformDomainRuleException(
                            "error.msg.loan.disbursal.date.should.be.after.last.transaction.date.of.loan.to.be.closed",
                            "Disbursal date of this loan application "+loan.getDisbursementDate()
                                    +" should be after last transaction date of loan to be closed "+ lastUserTransactionOnLoanToClose);
                }
                BigDecimal loanOutstanding = this.loanReadPlatformService.retrieveLoanPrePaymentTemplate(loanIdToClose,
                        expectedDisbursementDate).getAmount();
                final BigDecimal firstDisbursalAmount = loan.getFirstDisbursalAmount();
                if(loanOutstanding.compareTo(firstDisbursalAmount) > 0){
                    throw new GeneralPlatformDomainRuleException("error.msg.loan.amount.less.than.outstanding.of.loan.to.be.closed",
                            "Topup loan amount should be greater than outstanding amount of loan to be closed.");
                }
            }

            saveAndFlushLoanWithDataIntegrityViolationChecks(loan);

            final String noteText = command.stringValueOfParameterNamed("note");
            if (StringUtils.isNotBlank(noteText)) {
                final Note note = Note.loanNote(loan, noteText);
                changes.put("note", noteText);
                this.noteRepository.save(note);
            }

            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_APPROVED,
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

    @Transactional
    @Override
    public CommandProcessingResult undoApplicationApproval(final Long loanId, final JsonCommand command) {

        AppUser currentUser = getAppUserIfPresent();

        this.fromApiJsonDeserializer.validateForUndo(command.json());

        final Loan loan = retrieveLoanBy(loanId);
        checkClientOrGroupActive(loan);

        final Map<String, Object> changes = loan.undoApproval(defaultLoanLifecycleStateMachine());
        if (!changes.isEmpty()) {

            // If loan approved amount is not same as loan amount demanded, then
            // during undo, restore the demand amount to principal amount.
            if(loan.isGLIMLoan()){
                List<GroupLoanIndividualMonitoring> glimList  = this.groupLoanIndividualMonitoringRepository.findByLoanIdAndIsClientSelected(loanId, true);
                HashMap<Long, BigDecimal> chargesMap = new HashMap<>();
                for (GroupLoanIndividualMonitoring glim : glimList) {
                    final BigDecimal proposedAmount = glim.getProposedAmount();
                    if (proposedAmount != null) {
                        glim.setIsClientSelected(true);
                        glim.setApprovedAmount(null);
                        this.groupLoanIndividualMonitoringAssembler.recalculateTotalFeeCharges(loan, chargesMap, proposedAmount,
                                glim.getGroupLoanIndividualMonitoringCharges());
                    }
                }
                this.groupLoanIndividualMonitoringAssembler.updateLoanChargesForGlim(loan, chargesMap);
                loan.updateGlim(glimList);
                this.groupLoanIndividualMonitoringAssembler.adjustRoundOffValuesToApplicableCharges(loan.charges(),
                        loan.fetchNumberOfInstallmensAfterExceptions(), glimList);
            }

            if (changes.containsKey(LoanApiConstants.approvedLoanAmountParameterName)
                    || changes.containsKey(LoanApiConstants.disbursementPrincipalParameterName)) {
                
                LocalDate recalculateFrom = null;
                ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);
                loan.regenerateRepaymentSchedule(scheduleGeneratorDTO, currentUser);
            }

            saveAndFlushLoanWithDataIntegrityViolationChecks(loan);

            final String noteText = command.stringValueOfParameterNamed("note");
            if (StringUtils.isNotBlank(noteText)) {
                final Note note = Note.loanNote(loan, noteText);
                this.noteRepository.save(note);
            }
            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_UNDO_APPROVAL,
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

    @Transactional
    @Override
    public CommandProcessingResult rejectApplication(final Long loanId, final JsonCommand command) {

        final AppUser currentUser = getAppUserIfPresent();

        this.loanApplicationTransitionApiJsonValidator.validateRejection(command.json());

        final Loan loan = retrieveLoanBy(loanId);
        checkClientOrGroupActive(loan);

        final Map<String, Object> changes = loan.loanApplicationRejection(currentUser, command, defaultLoanLifecycleStateMachine());
        if (!changes.isEmpty()) {
            this.loanRepository.save(loan);

            final String noteText = command.stringValueOfParameterNamed("note");
            if (StringUtils.isNotBlank(noteText)) {
                final Note note = Note.loanNote(loan, noteText);
                this.noteRepository.save(note);
            }
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

    @Transactional
    @Override
    public CommandProcessingResult applicantWithdrawsFromApplication(final Long loanId, final JsonCommand command) {

        final AppUser currentUser = getAppUserIfPresent();

        this.loanApplicationTransitionApiJsonValidator.validateApplicantWithdrawal(command.json());

        final Loan loan = retrieveLoanBy(loanId);
        checkClientOrGroupActive(loan);

        final Map<String, Object> changes = loan.loanApplicationWithdrawnByApplicant(currentUser, command,
                defaultLoanLifecycleStateMachine());
        if (!changes.isEmpty()) {
            this.loanRepository.save(loan);

            final String noteText = command.stringValueOfParameterNamed("note");
            if (StringUtils.isNotBlank(noteText)) {
                final Note note = Note.loanNote(loan, noteText);
                this.noteRepository.save(note);
            }
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

    private Loan retrieveLoanBy(final Long loanId) {
        final Loan loan = this.loanRepositoryWrapper.findOneWithNotFoundDetectionAndLazyInitialize(loanId);
        loan.setHelpers(defaultLoanLifecycleStateMachine(), this.loanSummaryWrapper, this.loanRepaymentScheduleTransactionProcessorFactory);
        return loan;
    }

    public void validateSubmittedOnDate(final LocalDate submittedOnDate, final LocalDate expectedFirstRepaymentOnDate,
            final LoanProduct loanProduct, final Client client) {

        final LocalDate startDate = loanProduct.getStartDate();
        final LocalDate closeDate = loanProduct.getCloseDate();
        String defaultUserMessage = "";

        if (client != null) {
            final LocalDate activationDate = client.getActivationLocalDate();

            if (activationDate == null) {
                defaultUserMessage = "Client is not activated.";
                throw new LoanApplicationDateException("client.is.not.activated", defaultUserMessage);
            }

            if (submittedOnDate.isBefore(activationDate)) {
                defaultUserMessage = "Submitted on date cannot be before the client activation date.";
                throw new LoanApplicationDateException("submitted.on.date.cannot.be.before.the.client.activation.date", defaultUserMessage,
                        submittedOnDate.toString(), activationDate.toString());
            }
        }

        if (startDate != null && submittedOnDate.isBefore(startDate)) {
            defaultUserMessage = "submittedOnDate cannot be before the loan product startDate.";
            throw new LoanApplicationDateException("submitted.on.date.cannot.be.before.the.loan.product.start.date", defaultUserMessage,
                    submittedOnDate.toString(), startDate.toString());
        }

        if (closeDate != null && submittedOnDate.isAfter(closeDate)) {
            defaultUserMessage = "submittedOnDate cannot be after the loan product closeDate.";
            throw new LoanApplicationDateException("submitted.on.date.cannot.be.after.the.loan.product.close.date", defaultUserMessage,
                    submittedOnDate.toString(), closeDate.toString());
        }

        if (expectedFirstRepaymentOnDate != null && submittedOnDate.isAfter(expectedFirstRepaymentOnDate)) {
            defaultUserMessage = "submittedOnDate cannot be after the loans  expectedFirstRepaymentOnDate.";
            throw new LoanApplicationDateException("submitted.on.date.cannot.be.after.the.loan.expected.first.repayment.date",
                    defaultUserMessage, submittedOnDate.toString(), expectedFirstRepaymentOnDate.toString());
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

    private void saveAndFlushLoanWithDataIntegrityViolationChecks(final Loan loan) {
        try {
            List<LoanRepaymentScheduleInstallment> installments = loan.fetchRepaymentScheduleInstallments();
            for (LoanRepaymentScheduleInstallment installment : installments) {
                if (installment.getId() == null) {
                    this.repaymentScheduleInstallmentRepository.save(installment);
                }
            }
            this.loanRepository.saveAndFlush(loan);
        } catch (final DataIntegrityViolationException e) {
            final Throwable realCause = e.getCause();
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan.application");
            if (realCause.getMessage().toLowerCase().contains("external_id_unique")) {
                baseDataValidator.reset().parameter("externalId").failWithCode("value.must.be.unique");
            }
            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                    "Validation errors exist.", dataValidationErrors); }
        }
    }

    private AppUser getAppUserIfPresent() {
        AppUser user = null;
        if (this.context != null) {
            user = this.context.getAuthenticatedUserIfPresent();
        }
        return user;
    }

    private Map<BUSINESS_ENTITY, Object> constructEntityMap(final BUSINESS_ENTITY entityEvent, Object entity) {
        Map<BUSINESS_ENTITY, Object> map = new HashMap<>(1);
        map.put(entityEvent, entity);
        return map;
    }

    private void validatePledgeForLoan(JsonCommand command, Loan existingLoanApplication) {
        final Long existingPledgeId = this.pledgeReadPlatformService.retrievePledgesByloanId(existingLoanApplication.getId());
        if (existingPledgeId != null) {
            Pledges existingPledge = this.pledgeRepositoryWrapper.findOneWithNotFoundDetection(existingPledgeId);
            if (existingPledge != null) {
                existingPledge.updateLoanId(null);
                this.pledgeRepositoryWrapper.save(existingPledge);
            }
        }

        final Long pledgeId = command.longValueOfParameterNamed("pledgeId");
        if (pledgeId != null) {
            final Pledges pledge = this.pledgeRepositoryWrapper.findOneWithNotFoundDetection(pledgeId);
            pledge.updateLoanId(existingLoanApplication);
            this.pledgeRepositoryWrapper.save(pledge);
        }
    }

    private void validateCollateralAmount(JsonCommand command, LoanProduct product) {
        final String principalParameterName = "principal";
        final String collateralUserValueParameterName = "collateralUserValue";
        BigDecimal principal = command.bigDecimalValueOfParameterNamed(principalParameterName);
        BigDecimal collateralUserValue = command.bigDecimalValueOfParameterNamed(collateralUserValueParameterName);
        if (principal != null && collateralUserValue != null) {
            product.validateCollateralAmountShouldNotExceedPrincipleAmount(principal, collateralUserValue);
        }
    }

    private void validateCollateralAmountWithPrincipal(JsonCommand command, LoanProduct loanProduct) {
        final BigDecimal principal = this.fromJsonHelper.extractBigDecimalWithLocaleNamed("principal", command.parsedJson());
        final BigDecimal collateralUserValue = this.fromJsonHelper.extractBigDecimalWithLocaleNamed("collateralUserValue",
                command.parsedJson());
        if (principal != null && collateralUserValue != null) {
            loanProduct.validateCollateralAmountShouldNotExceedPrincipleAmount(principal, collateralUserValue);
        }
    }

    private void attachLoanAccountToPledge(JsonCommand command, Loan newLoanApplication) {
        // Save linked loan account in pledge
        final Long pledgeId = command.longValueOfParameterNamed("pledgeId");
        if (pledgeId != null) {
            final Pledges pledge = this.pledgeRepositoryWrapper.findOneWithNotFoundDetection(pledgeId);
            pledge.updateLoanId(newLoanApplication);
            pledge.updatePledgeStatus();
            this.pledgeRepositoryWrapper.save(pledge);
        }
    }

    private void officeSpecificLoanProductValidation(final Long productId, final Long officeId) {
		final GlobalConfigurationPropertyData restrictToUserOfficeProperty = configurationDomainService
				.getGlobalConfigurationPropertyData(FineractEntityAccessConstants.GLOBAL_CONFIG_FOR_OFFICE_SPECIFIC_PRODUCTS);
        if (restrictToUserOfficeProperty.isEnabled()) {
            FineractEntityRelation fineractEntityRelation = fineractEntityRelationRepository
                    .findOneByCodeName(FineractEntityAccessType.OFFICE_ACCESS_TO_LOAN_PRODUCTS.toStr());
            FineractEntityToEntityMapping officeToLoanProductMappingList = this.repository.findListByProductId(fineractEntityRelation,
                    productId, officeId);
            if (officeToLoanProductMappingList == null) { throw new NotOfficeSpecificProductException(productId, officeId); }

        }
    }
    
}