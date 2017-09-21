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
package org.apache.fineract.portfolio.loanaccount.loanschedule.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonQuery;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.accountdetails.domain.AccountType;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstanceRepository;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanAccountDomainService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleTransactionProcessorFactory;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanSchedulePeriodData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanApplicationTerms;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModel;
import org.apache.fineract.portfolio.loanaccount.serialization.CalculateLoanScheduleQueryFromApiJsonHelper;
import org.apache.fineract.portfolio.loanaccount.serialization.LoanApplicationCommandFromApiJsonHelper;
import org.apache.fineract.portfolio.loanaccount.service.LoanAssembler;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRepository;
import org.apache.fineract.portfolio.loanproduct.exception.LoanProductNotFoundException;
import org.apache.fineract.portfolio.loanproduct.serialization.LoanProductDataValidator;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanScheduleCalculationPlatformServiceImpl implements LoanScheduleCalculationPlatformService {

    private final CalculateLoanScheduleQueryFromApiJsonHelper fromApiJsonDeserializer;
    private final LoanScheduleAssembler loanScheduleAssembler;
    private final FromJsonHelper fromJsonHelper;
    private final LoanProductRepository loanProductRepository;
    private final LoanProductDataValidator loanProductCommandFromApiJsonDeserializer;
    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanApplicationCommandFromApiJsonHelper loanApiJsonDeserializer;
    private final LoanAssembler loanAssembler;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository;
    private final LoanAccountDomainService accountDomainService;
    private final CalendarInstanceRepository calendarInstanceRepository;
    private final LoanRepaymentScheduleTransactionProcessorFactory loanRepaymentScheduleTransactionProcessorFactory;
    private final ConfigurationDomainService configurationDomainService;

    @Autowired
    public LoanScheduleCalculationPlatformServiceImpl(final CalculateLoanScheduleQueryFromApiJsonHelper fromApiJsonDeserializer,
            final LoanScheduleAssembler loanScheduleAssembler, final FromJsonHelper fromJsonHelper,
            final LoanProductRepository loanProductRepository, final LoanProductDataValidator loanProductCommandFromApiJsonDeserializer,
            final LoanReadPlatformService loanReadPlatformService, final LoanApplicationCommandFromApiJsonHelper loanApiJsonDeserializer,
            final LoanAssembler loanAssembler, final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository,
            final LoanAccountDomainService accountDomainService, final CalendarInstanceRepository calendarInstanceRepository,
            final LoanRepaymentScheduleTransactionProcessorFactory loanRepaymentScheduleTransactionProcessorFactory,
            final ConfigurationDomainService configurationDomainService) {
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.loanScheduleAssembler = loanScheduleAssembler;
        this.fromJsonHelper = fromJsonHelper;
        this.loanProductRepository = loanProductRepository;
        this.loanProductCommandFromApiJsonDeserializer = loanProductCommandFromApiJsonDeserializer;
        this.loanReadPlatformService = loanReadPlatformService;
        this.loanApiJsonDeserializer = loanApiJsonDeserializer;
        this.loanAssembler = loanAssembler;
        this.applicationCurrencyRepository = applicationCurrencyRepository;
        this.accountDomainService = accountDomainService;
        this.calendarInstanceRepository = calendarInstanceRepository;
        this.loanRepaymentScheduleTransactionProcessorFactory = loanRepaymentScheduleTransactionProcessorFactory;
        this.configurationDomainService = configurationDomainService;
    }

    @Override
    public LoanScheduleModel calculateLoanSchedule(final JsonQuery query, Boolean validateParams) {

        /***
         * TODO: Vishwas, this is probably not required, test and remove the
         * same
         **/
        final Long productId = this.fromJsonHelper.extractLongNamed("productId", query.parsedJson());
        final LoanProduct loanProduct = this.loanProductRepository.findOne(productId);
        if (loanProduct == null) { throw new LoanProductNotFoundException(productId); }

        if (validateParams) {
            boolean isMeetingMandatoryForJLGLoans = configurationDomainService.isMeetingMandatoryForJLGLoans();
            this.loanApiJsonDeserializer.validateForCreate(query.json(), isMeetingMandatoryForJLGLoans, loanProduct);
        }
        this.fromApiJsonDeserializer.validate(query.json());

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("loan");

        if (loanProduct.useBorrowerCycle()) {
            final Long clientId = this.fromJsonHelper.extractLongNamed("clientId", query.parsedJson());
            final Long groupId = this.fromJsonHelper.extractLongNamed("groupId", query.parsedJson());
            Integer cycleNumber = 0;
            if (clientId != null) {
                cycleNumber = this.loanReadPlatformService.retriveLoanCounter(clientId, loanProduct.getId());
            } else if (groupId != null) {
                cycleNumber = this.loanReadPlatformService.retriveLoanCounter(groupId, AccountType.GROUP.getValue(), loanProduct.getId());
            }
            this.loanProductCommandFromApiJsonDeserializer.validateMinMaxConstraints(query.parsedJson(), baseDataValidator, loanProduct,
                    cycleNumber);
        } else {
            this.loanProductCommandFromApiJsonDeserializer.validateMinMaxConstraints(query.parsedJson(), baseDataValidator, loanProduct);
        }
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

        return this.loanScheduleAssembler.assembleLoanScheduleFrom(query.parsedJson());
    }

    @Override
    public void updateFutureSchedule(LoanScheduleData loanScheduleData, final Long loanId) {

        final Loan loan = this.loanAssembler.assembleFrom(loanId);

        LocalDate today = DateUtils.getLocalDateOfTenant();
        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = loanRepaymentScheduleTransactionProcessorFactory
                .determineProcessor(loan.transactionProcessingStrategy());

        if (!loan.repaymentScheduleDetail().isInterestRecalculationEnabled() || loan.isNpa() || !loan.status().isActive()
                || !loanRepaymentScheduleTransactionProcessor.isInterestFirstRepaymentScheduleTransactionProcessor()) { return; }

        if (loan.loanProduct().isMultiDisburseLoan()) {
            BigDecimal disbursedAmount = loan.getDisbursedAmount();
            BigDecimal principalRepaid = loan.getLoanSummary().getTotalPrincipalRepaid();
            BigDecimal principalWrittenOff = loan.getLoanSummary().getTotalPrincipalWrittenOff();
            if (disbursedAmount.subtract(principalWrittenOff).subtract(principalRepaid).compareTo(BigDecimal.ZERO) != 1) { return; }
        }
        MonetaryCurrency currency = loan.getCurrency();
        Money totalPrincipal = Money.zero(currency);
        final List<LoanSchedulePeriodData> futureInstallments = new ArrayList<>();
        for (final LoanRepaymentScheduleInstallment currentInstallment : loan.fetchRepaymentScheduleInstallments()) {
            if (currentInstallment.isNotFullyPaidOff()) {
                if (!currentInstallment.getDueDate().isAfter(today)) {
                    totalPrincipal = totalPrincipal.plus(currentInstallment.getPrincipalOutstanding(currency));
                }
            }
        }
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);
        final CalendarInstance calendarInstance = this.calendarInstanceRepository.findCalendarInstaneByEntityId(loan.getId(),
                CalendarEntityType.LOANS.getValue());
        final CalendarInstance restCalendarInstance = calendarInstanceRepository.findCalendarInstaneByEntityId(
                loan.loanInterestRecalculationDetailId(), CalendarEntityType.LOAN_RECALCULATION_REST_DETAIL.getValue());
        final CalendarInstance compoundingCalendarInstance = calendarInstanceRepository.findCalendarInstaneByEntityId(
                loan.loanInterestRecalculationDetailId(), CalendarEntityType.LOAN_RECALCULATION_COMPOUNDING_DETAIL.getValue());
        LocalDate calculatedRepaymentsStartingFromDate = accountDomainService.getCalculatedRepaymentsStartingFromDate(
                loan.getDisbursementDate(), loan, calendarInstance);
        LoanApplicationTerms loanApplicationTerms = loan.constructLoanApplicationTerms(applicationCurrency,
                calculatedRepaymentsStartingFromDate, restCalendarInstance, compoundingCalendarInstance);
        LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment = this.loanScheduleAssembler.calculatePrepaymentAmount(
                loan.fetchRepaymentScheduleInstallments(), currency, today, loanApplicationTerms, loan.charges(), loan.getOfficeId(),
                loan.retreiveListOfTransactionsPostDisbursementExcludeAccruals(), loanRepaymentScheduleTransactionProcessor);
        Money totalAmount = totalPrincipal.plus(loanRepaymentScheduleInstallment.getFeeChargesOutstanding(currency)).plus(
                loanRepaymentScheduleInstallment.getPenaltyChargesOutstanding(currency));
        Money interestDue = Money.zero(currency);
        if (loanRepaymentScheduleInstallment.isInterestDue(currency)) {
            interestDue = loanRepaymentScheduleInstallment.getInterestOutstanding(currency);
            totalAmount = totalAmount.plus(interestDue);
        }
        boolean isNewPaymentRequired = loanRepaymentScheduleInstallment.isInterestDue(currency) || totalPrincipal.isGreaterThanZero();
        final List<LoanTransaction> modifiedTransactions = new ArrayList<>();
        List<LoanTransaction> transactions = loan.retreiveListOfTransactionsPostDisbursementExcludeAccruals();
        for (LoanTransaction loanTransaction : transactions) {
            modifiedTransactions.add(LoanTransaction.copyTransactionProperties(loanTransaction));
        }
        if (isNewPaymentRequired) {
            LoanTransaction ondayPaymentTransaction = LoanTransaction.repayment(null, totalAmount, null, today, null,
                    DateUtils.getLocalDateTimeOfTenant(), null);
            modifiedTransactions.add(ondayPaymentTransaction);
        }

        LoanScheduleModel model = this.loanScheduleAssembler.assembleForInterestRecalculation(loanApplicationTerms, loan.getOfficeId(),
                modifiedTransactions, loan.charges(), loanRepaymentScheduleTransactionProcessor);
        LoanScheduleData scheduleDate = model.toData();
        Collection<LoanSchedulePeriodData> periodDatas = scheduleDate.getPeriods();
        for (LoanSchedulePeriodData periodData : periodDatas) {
            if ((periodData.periodDueDate().isEqual(today) || periodData.periodDueDate().isAfter(today)) && isNewPaymentRequired) {
                LoanSchedulePeriodData loanSchedulePeriodData = LoanSchedulePeriodData.repaymentOnlyPeriod(periodData.periodNumber(),
                        periodData.periodFromDate(), periodData.periodDueDate(), totalPrincipal.getAmount(), periodData
                                .principalLoanBalanceOutstanding(), interestDue.getAmount(), loanRepaymentScheduleInstallment
                                .getFeeChargesCharged(currency).getAmount(),
                        loanRepaymentScheduleInstallment.getPenaltyChargesCharged(currency).getAmount(), totalAmount.getAmount());
                futureInstallments.add(loanSchedulePeriodData);
                isNewPaymentRequired = false;
            } else if (periodData.periodDueDate().isAfter(today)) {
                futureInstallments.add(periodData);
            }

        }
        loanScheduleData.updateFuturePeriods(futureInstallments);
    }
}