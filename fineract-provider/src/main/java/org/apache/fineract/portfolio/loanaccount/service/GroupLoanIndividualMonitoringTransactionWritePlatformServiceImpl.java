/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.exception.ClientNotActiveException;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.exception.GroupNotActiveException;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringDataChanges;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.data.ScheduleGeneratorDTO;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringRepository;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringTransactionRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallmentRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionRepository;
import org.apache.fineract.portfolio.loanaccount.exception.InvalidLoanTransactionTypeException;
import org.apache.fineract.portfolio.loanaccount.serialization.LoanEventApiJsonValidator;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailWritePlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;

@Service
public class GroupLoanIndividualMonitoringTransactionWritePlatformServiceImpl implements
        GroupLoanIndividualMonitoringTransactionWritePlatformService {

    private final GroupLoanIndividualMonitoringTransactionRepositoryWrapper groupLoanIndividualMonitoringTransactionRepositoryWrapper;
    private final LoanAccountDomainService loanAccountDomainService;
    private final LoanEventApiJsonValidator loanEventApiJsonValidator;
    private final LoanAssembler loanAssembler;
    private final PaymentDetailWritePlatformService paymentDetailWritePlatformService;
    private final GroupLoanIndividualMonitoringTransactionAssembler glimTransactionAssembler;
    private final GroupLoanIndividualMonitoringAssembler glimAssembler;
    private final GroupLoanIndividualMonitoringRepository glimRepository;
    private final LoanUtilService loanUtilService;
    private final LoanTransactionRepository loanTransactionRepository;
    private final LoanRepository loanRepository;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository;
    private final JournalEntryWritePlatformService journalEntryWritePlatformService;
    private final PlatformSecurityContext context;
    private final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final BusinessEventNotifierService businessEventNotifierService;

    @Autowired
    public GroupLoanIndividualMonitoringTransactionWritePlatformServiceImpl(
            final GroupLoanIndividualMonitoringTransactionRepositoryWrapper groupLoanIndividualMonitoringTransactionRepositoryWrapper,
            final LoanAccountDomainService loanAccountDomainService, final LoanEventApiJsonValidator loanEventApiJsonValidator,
            final LoanAssembler loanAssembler, final PaymentDetailWritePlatformService paymentDetailWritePlatformService,
            final GroupLoanIndividualMonitoringTransactionAssembler glimTransactionAssembler,
            final GroupLoanIndividualMonitoringAssembler glimAssembler, final GroupLoanIndividualMonitoringRepository glimRepository,
            final LoanUtilService loanUtilService, final LoanTransactionRepository loanTransactionRepository,
            final LoanRepository loanRepository, final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository,
            final JournalEntryWritePlatformService journalEntryWritePlatformService, final PlatformSecurityContext context,
            final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository,
            final CodeValueRepositoryWrapper codeValueRepository, final BusinessEventNotifierService businessEventNotifierService) {
        this.groupLoanIndividualMonitoringTransactionRepositoryWrapper = groupLoanIndividualMonitoringTransactionRepositoryWrapper;
        this.loanAccountDomainService = loanAccountDomainService;
        this.loanEventApiJsonValidator = loanEventApiJsonValidator;
        this.loanAssembler = loanAssembler;
        this.paymentDetailWritePlatformService = paymentDetailWritePlatformService;
        this.glimTransactionAssembler = glimTransactionAssembler;
        this.glimAssembler = glimAssembler;
        this.glimRepository = glimRepository;
        this.loanUtilService = loanUtilService;
        this.loanTransactionRepository = loanTransactionRepository;
        this.loanRepository = loanRepository;
        this.applicationCurrencyRepository = applicationCurrencyRepository;
        this.journalEntryWritePlatformService = journalEntryWritePlatformService;
        this.context = context;
        this.repaymentScheduleInstallmentRepository = repaymentScheduleInstallmentRepository;
        this.codeValueRepository = codeValueRepository;
        this.businessEventNotifierService = businessEventNotifierService;
    }

    @Transactional
    @Override
    public CommandProcessingResult repayGLIM(Long loanId, JsonCommand command, boolean isRecoveryRepayment) {

        this.loanEventApiJsonValidator.validateNewRepaymentTransaction(command.json());

        this.glimTransactionAssembler.validateGlimTransactionAmount(command, isRecoveryRepayment);

        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");
        final String txnExternalId = command.stringValueOfParameterNamedAllowingNull("externalId");

        final Map<String, Object> changes = new LinkedHashMap<>();
        final Collection<GroupLoanIndividualMonitoringDataChanges> clientMembers = new ArrayList<>();
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
        List<GroupLoanIndividualMonitoring> defaultGlimMembers = this.glimRepository.findByLoanIdAndIsClientSelected(loanId, true);
        loan.updateDefautGlimMembers(defaultGlimMembers);
        List<GroupLoanIndividualMonitoring> glimMembers = this.glimAssembler.assembleGlimFromJson(command, isRecoveryRepayment);
        loan.updateGlim(glimMembers);
        final PaymentDetail paymentDetail = this.paymentDetailWritePlatformService.createAndPersistPaymentDetail(command, changes);
        final Boolean isHolidayValidationDone = false;
        final HolidayDetailDTO holidayDetailDto = null;
        boolean isAccountTransfer = false;
        // validate for current transaction date should be before last user transaction for glim loans
        validateGlimTransaction(transactionDate, loan);
        final CommandProcessingResultBuilder commandProcessingResultBuilder = new CommandProcessingResultBuilder();
        LoanTransaction loanTransaction = this.loanAccountDomainService.makeRepayment(loan, commandProcessingResultBuilder,
                transactionDate, transactionAmount, paymentDetail, noteText, txnExternalId, isRecoveryRepayment, isAccountTransfer,
                holidayDetailDto, isHolidayValidationDone);
        // FinanceLib.pmt(r, n, p, f, t);
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = this.glimTransactionAssembler.assembleGLIMTransactions(
                command, loanTransaction, clientMembers);
        changes.put("clientMembers", clientMembers);
        this.glimAssembler.updateGLIMAfterRepayment(glimTransactions, isRecoveryRepayment);
        this.groupLoanIndividualMonitoringTransactionRepositoryWrapper.saveAsList(glimTransactions);
        this.glimTransactionAssembler.updateLoanStatusForGLIM(loan);
        
        return commandProcessingResultBuilder.withCommandId(command.commandId()) //
                .withTransactionId(loanTransaction.getId().toString()) //
                .withLoanId(loanId) //
                .with(changes) //
                .build();
    }

    private void validateGlimTransaction(LocalDate transactionDate, Loan loan) {
        LocalDate lastUserTransactionDate = loan.getLastUserTransactionDate();
        if (transactionDate.isBefore(lastUserTransactionDate)) {
            final String errorMessage = "repayment before last transaction is not allowed for GLIM loan.";
            throw new InvalidLoanTransactionTypeException("transaction", "repayment.before.last.transaction.is.not.allowed.for.glim.loan",
                    errorMessage);
        }
    }

    @Override
    public CommandProcessingResult waiveCharge(Long loanId, JsonCommand command) {

        final AppUser currentUser = getAppUserIfPresent();
        final Loan loan = this.loanAssembler.assembleFrom(loanId);

        checkClientOrGroupActive(loan);
        this.loanEventApiJsonValidator.validateGLIMWaiveChargeTransaction(command.json());
        MonetaryCurrency currency = loan.getCurrency();
        final Map<String, Object> changes = new LinkedHashMap<>();
        final Collection<GroupLoanIndividualMonitoringDataChanges> clientMembers = new ArrayList<>();
        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();
        LocalDate recalculateFrom = null;
        ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);
        List<GroupLoanIndividualMonitoring> defaultGlimMembers = this.glimRepository.findByLoanIdAndIsClientSelected(loanId, true);
        loan.updateDefautGlimMembers(defaultGlimMembers);
        boolean isRecoveryPayment = false;
        List<GroupLoanIndividualMonitoring> glimMembers = this.glimAssembler.assembleGlimFromJson(command, isRecoveryPayment);
        loan.updateGlim(glimMembers);
        final JsonArray glimList = command.arrayOfParameterNamed("clientMembers");
        BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");
        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        validateGlimTransaction(transactionDate, loan);
        changes.put("transactionDate", transactionDate);
        changes.put("transactionAmount", transactionAmount);
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());

        if (glimList != null) {
            for (GroupLoanIndividualMonitoring glim : glimMembers) {
                Money transactionAmountPerClient = Money.of(currency, glim.getTransactionAmount());
                Map<String, BigDecimal> installmentPaidMap = new HashMap<>();
                installmentPaidMap.put("unpaidCharge", BigDecimal.ZERO);
                installmentPaidMap.put("unpaidInterest", BigDecimal.ZERO);
                installmentPaidMap.put("unpaidPrincipal", BigDecimal.ZERO);
                installmentPaidMap.put("installmentTransactionAmount", BigDecimal.ZERO);
                clientMembers.add(GroupLoanIndividualMonitoringDataChanges.createNew(glim.getId(),
                        transactionAmountPerClient.getAmount()));

                Money accruedCharge = Money.zero(loan.getCurrency());
                Map<Long, BigDecimal> feeChargesWaiveOffPerInstallments = new HashMap<>();

                final LoanTransaction waiveLoanChargeTransaction = LoanTransaction.waiveGlimCharge(loan, loan.getOffice(),
                        DateUtils.getLocalDateOfTenant(), null);

                // determine how much is written off in total and breakdown
                // for
                // principal, interest and charges
                for (final LoanRepaymentScheduleInstallment currentInstallment : loan.getRepaymentScheduleInstallments()) {
                    if (transactionAmountPerClient.isGreaterThanZero()) {
                        Map<String, BigDecimal> paidInstallmentMap = GroupLoanIndividualMonitoringTransactionAssembler.getSplit(glim,
                                transactionAmountPerClient.getAmount(), loan, currentInstallment.getInstallmentNumber(),
                                installmentPaidMap, waiveLoanChargeTransaction, null);

                        if (!(paidInstallmentMap.get("installmentTransactionAmount").compareTo(BigDecimal.ZERO) == 0 && glim
                                .getTotalPaidAmount().compareTo(BigDecimal.ZERO) > 0)) {
                            if (currentInstallment.isNotFullyPaidOff()) {

                                Map<String, BigDecimal> splitMap = GroupLoanIndividualMonitoringTransactionAssembler.getSplit(glim,
                                        transactionAmountPerClient.getAmount(), loan, currentInstallment.getInstallmentNumber(),
                                        installmentPaidMap, waiveLoanChargeTransaction, null);

                                Money feePortionForCurrentInstallment = Money.of(currency, splitMap.get("unpaidCharge"));
                                Money interestPortionForCurrentInstallment = Money.of(currency, splitMap.get("unpaidInterest"));
                                Money principalPortionForCurrentInstallment = Money.of(currency, splitMap.get("unpaidPrincipal"));
                                Money totalAmountForCurrentInstallment = Money.of(currency, splitMap.get("installmentTransactionAmount"));
                                transactionAmountPerClient = transactionAmountPerClient.minus(totalAmountForCurrentInstallment);

                                installmentPaidMap.put("unpaidCharge",
                                        installmentPaidMap.get("unpaidCharge").add(feePortionForCurrentInstallment.getAmount()));
                                installmentPaidMap.put("unpaidInterest",
                                        installmentPaidMap.get("unpaidInterest").add(interestPortionForCurrentInstallment.getAmount()));
                                installmentPaidMap.put("unpaidPrincipal",
                                        installmentPaidMap.get("unpaidPrincipal").add(principalPortionForCurrentInstallment.getAmount()));
                                installmentPaidMap.put(
                                        "installmentTransactionAmount",
                                        installmentPaidMap.get("installmentTransactionAmount").add(
                                                totalAmountForCurrentInstallment.getAmount()));

                                feeChargesWaiveOffPerInstallments = loan.waiveLoanChargeForGlim(currentInstallment,
                                        feePortionForCurrentInstallment.getAmount(), glim, feeChargesWaiveOffPerInstallments,
                                        existingTransactionIds, existingReversedTransactionIds, accruedCharge, currentUser,
                                        scheduleGeneratorDTO, changes);

                            }
                        }
                    }
                }
                Money totalChargeWaived = Money.zero(loan.getCurrency());
                for (BigDecimal amountWaived : feeChargesWaiveOffPerInstallments.values()) {
                    totalChargeWaived = totalChargeWaived.plus(amountWaived);
                }

                glim.setWaivedChargeAmount(totalChargeWaived.getAmount().add(MathUtility.zeroIfNull(glim.getWaivedChargeAmount())));
                this.glimRepository.save(glim);

            }
            final LoanTransaction waiveLoanChargeTransaction = LoanTransaction.waiveLoanCharge(loan, loan.getOffice(),
                    Money.of(loan.getCurrency(), transactionAmount), transactionDate,
                    Money.of(loan.getCurrency(), transactionAmount), Money.zero(loan.getCurrency()), Money.zero(loan.getCurrency()));
            loan.getLoanTransactions().add(waiveLoanChargeTransaction);

            for (GroupLoanIndividualMonitoring glim : glimMembers) {
                BigDecimal waivedAmount = glim.getWaivedChargeAmount();
                GroupLoanIndividualMonitoringTransaction glimTransaction = GroupLoanIndividualMonitoringTransaction.waiveCharges(glim,
                        waiveLoanChargeTransaction, waivedAmount, waiveLoanChargeTransaction.getTypeOf().getValue());
                this.groupLoanIndividualMonitoringTransactionRepositoryWrapper.save(glimTransaction);
            }

            loan.updateLoanSummarAndStatus();
            this.loanTransactionRepository.save(waiveLoanChargeTransaction);
            saveLoanWithDataIntegrityViolationChecks(loan);

            postJournalEntries(loan, existingTransactionIds, existingReversedTransactionIds);
            
            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.LOAN_WAIVE_CHARGE,
                    constructEntityMap(BUSINESS_ENTITY.LOAN_TRANSACTION, waiveLoanChargeTransaction));
            changes.put("clientMembers", clientMembers);
            
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withOfficeId(loan.getOfficeId()) //
                .withClientId(loan.getClientId()) //
                .withGroupId(loan.getGroupId()) //
                .with(changes) //
                .withLoanId(loanId).build();
    }

    private Map<BUSINESS_ENTITY, Object> constructEntityMap(final BUSINESS_ENTITY entityEvent, Object entity) {
        Map<BUSINESS_ENTITY, Object> map = new HashMap<>(1);
        map.put(entityEvent, entity);
        return map;
    }

    private AppUser getAppUserIfPresent() {
        AppUser user = null;
        if (this.context != null) {
            user = this.context.getAuthenticatedUserIfPresent();
        }
        return user;
    }

    private void postJournalEntries(final Loan loan, final List<Long> existingTransactionIds,
            final List<Long> existingReversedTransactionIds) {

        final MonetaryCurrency currency = loan.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);
        boolean isAccountTransfer = false;
        final Map<String, Object> accountingBridgeData = loan.deriveAccountingBridgeData(applicationCurrency.toData(),
                existingTransactionIds, existingReversedTransactionIds, isAccountTransfer);
        this.journalEntryWritePlatformService.createJournalEntriesForLoan(accountingBridgeData);
    }

    private void saveLoanWithDataIntegrityViolationChecks(final Loan loan) {
        try {
            List<LoanRepaymentScheduleInstallment> installments = loan.fetchRepaymentScheduleInstallments();
            for (LoanRepaymentScheduleInstallment installment : installments) {
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

    @Transactional
    @Override
    public CommandProcessingResult waiveInterest(final Long loanId, final JsonCommand command) {

        this.loanEventApiJsonValidator.validateTransaction(command.json());

        this.loanEventApiJsonValidator.validateGlimForWaiveInterest(command.json());

        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");

        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();

        final Map<String, Object> changes = new LinkedHashMap<>();
        final Collection<GroupLoanIndividualMonitoringDataChanges> clientMembers = new ArrayList<>();
        changes.put("transactionDate", command.stringValueOfParameterNamed("transactionDate"));
        changes.put("transactionAmount", command.stringValueOfParameterNamed("transactionAmount"));
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        validateGlimTransaction(transactionDate, loan);
        List<GroupLoanIndividualMonitoring> defaultGlimMembers = this.glimRepository.findByLoanIdAndIsClientSelected(loanId, true);
        loan.updateDefautGlimMembers(defaultGlimMembers);
        boolean isRecoveryPayment = false;
        List<GroupLoanIndividualMonitoring> glimMembers = this.glimAssembler.assembleGlimFromJson(command, isRecoveryPayment);
        loan.updateGlim(glimMembers);
        final String noteText = command.stringValueOfParameterNamed("note");
        final CommandProcessingResultBuilder builderResult = new CommandProcessingResultBuilder();
        LoanTransaction loanTransaction = this.loanAccountDomainService.waiveInterest(loan, builderResult, transactionDate,
                transactionAmount, noteText, changes, existingTransactionIds, existingReversedTransactionIds);
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = this.glimTransactionAssembler.waiveInterestForClients(
                command, loanTransaction, clientMembers);
        changes.put("clientMembers", clientMembers);
        this.groupLoanIndividualMonitoringTransactionRepositoryWrapper.saveAsList(glimTransactions);

        return builderResult.withCommandId(command.commandId()) //
                .withTransactionId(loanTransaction.getId().toString()) //
                .with(changes) //
                .build();
    }

    @Override
    public CommandProcessingResult writeOff(Long loanId, JsonCommand command) {

        this.loanEventApiJsonValidator.validateGlimForWriteOff(command.json());

        final Map<String, Object> changes = new LinkedHashMap<>();
        final Collection<GroupLoanIndividualMonitoringDataChanges> clientMembers = new ArrayList<>();
        changes.put("transactionDate", command.stringValueOfParameterNamed("transactionDate"));
        changes.put("locale", command.locale());
        changes.put("dateFormat", command.dateFormat());
        LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        CodeValue writeoffReason = null;
        if (command.hasParameter("writeoffReasonId")) {
            Long writeoffReasonId = command.longValueOfParameterNamed("writeoffReasonId");
            writeoffReason = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(LoanApiConstants.WRITEOFFREASONS,
                    writeoffReasonId);
            changes.put("writeoffReasonId", writeoffReasonId);
            loan.updateWriteOffReason(writeoffReason);
        }
        validateGlimTransaction(transactionDate, loan);
        final List<Long> existingTransactionIds = new ArrayList<>();
        final List<Long> existingReversedTransactionIds = new ArrayList<>();
        final String noteText = command.stringValueOfParameterNamed("note");
        final CommandProcessingResultBuilder builderResult = new CommandProcessingResultBuilder();
        List<GroupLoanIndividualMonitoring> defaultGlimMembers = this.glimRepository.findByLoanIdAndIsClientSelected(loanId, true);
        loan.updateDefautGlimMembers(defaultGlimMembers);
        boolean isRecoveryPayment = false;
        List<GroupLoanIndividualMonitoring> glimMembers = this.glimAssembler.assembleGlimFromJson(command, isRecoveryPayment);
        loan.updateGlim(glimMembers);
        LoanTransaction loanTransaction = this.loanAccountDomainService.writeOffForGlimLoan(command, loan, builderResult, noteText,
                changes, existingTransactionIds, existingReversedTransactionIds);
        Collection<GroupLoanIndividualMonitoringTransaction> glimTransactions = this.glimTransactionAssembler.writeOffForClients(
                loanTransaction, glimMembers, writeoffReason, clientMembers);
        changes.put("clientMembers", clientMembers);
        this.groupLoanIndividualMonitoringTransactionRepositoryWrapper.saveAsList(glimTransactions);
        this.glimTransactionAssembler.updateLoanStatusForGLIM(loan);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withTransactionId(loanTransaction.getId().toString()) //
                .with(changes) //
                .build();
    }

}
