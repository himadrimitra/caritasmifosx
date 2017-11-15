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
import java.util.Iterator;
import java.util.List;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonQuery;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.monetary.service.CurrencyReadPlatformService;
import org.apache.fineract.portfolio.accountdetails.domain.AccountType;
import org.apache.fineract.portfolio.loanaccount.data.ScheduleGeneratorDTO;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDisbursementDetails;
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
import org.apache.fineract.portfolio.loanaccount.service.LoanUtilService;
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
    private final LoanRepaymentScheduleTransactionProcessorFactory loanRepaymentScheduleTransactionProcessorFactory;
    private final ConfigurationDomainService configurationDomainService;
    private final CurrencyReadPlatformService currencyReadPlatformService;
    private final LoanUtilService loanUtilService;

    @Autowired
    public LoanScheduleCalculationPlatformServiceImpl(final CalculateLoanScheduleQueryFromApiJsonHelper fromApiJsonDeserializer,
            final LoanScheduleAssembler loanScheduleAssembler, final FromJsonHelper fromJsonHelper,
            final LoanProductRepository loanProductRepository, final LoanProductDataValidator loanProductCommandFromApiJsonDeserializer,
            final LoanReadPlatformService loanReadPlatformService, final LoanApplicationCommandFromApiJsonHelper loanApiJsonDeserializer,
            final LoanAssembler loanAssembler,
            final LoanRepaymentScheduleTransactionProcessorFactory loanRepaymentScheduleTransactionProcessorFactory,
            final ConfigurationDomainService configurationDomainService, final CurrencyReadPlatformService currencyReadPlatformService,
            final LoanUtilService loanUtilService) {
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.loanScheduleAssembler = loanScheduleAssembler;
        this.fromJsonHelper = fromJsonHelper;
        this.loanProductRepository = loanProductRepository;
        this.loanProductCommandFromApiJsonDeserializer = loanProductCommandFromApiJsonDeserializer;
        this.loanReadPlatformService = loanReadPlatformService;
        this.loanApiJsonDeserializer = loanApiJsonDeserializer;
        this.loanAssembler = loanAssembler;
        this.loanRepaymentScheduleTransactionProcessorFactory = loanRepaymentScheduleTransactionProcessorFactory;
        this.configurationDomainService = configurationDomainService;
        this.currencyReadPlatformService = currencyReadPlatformService;
        this.loanUtilService = loanUtilService;
    }

    @Override
    public LoanScheduleModel calculateLoanSchedule(final JsonQuery query, final Boolean validateParams,
            final boolean considerAllDisbursmentsInSchedule) {
        validateLoanParameters(query, validateParams);
        return this.loanScheduleAssembler.assembleLoanScheduleFrom(query.parsedJson(), considerAllDisbursmentsInSchedule);
    }

    private void validateLoanParameters(final JsonQuery query, final Boolean validateParams) {
        /***
         * TODO: Vishwas, this is probably not required, test and remove the
         * same
         **/
        final Long productId = this.fromJsonHelper.extractLongNamed("productId", query.parsedJson());
        final LoanProduct loanProduct = this.loanProductRepository.findOne(productId);
        if (loanProduct == null) { throw new LoanProductNotFoundException(productId); }

        if (validateParams) {
            final boolean isMeetingMandatoryForJLGLoans = this.configurationDomainService.isMeetingMandatoryForJLGLoans();
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
    }

    @Override
    public LoanScheduleModel calculateLoanSchedule(final JsonQuery query, final Boolean validateParams, final LoanApplicationTerms terms) {
        validateLoanParameters(query, validateParams);
        return this.loanScheduleAssembler.assembleLoanScheduleFrom(query.parsedJson(), terms);
    }

    @Override
    public void updateFutureSchedule(final LoanScheduleData loanScheduleData, final Long loanId) {

        final Loan loan = this.loanAssembler.assembleFrom(loanId);

        final LocalDate today = DateUtils.getLocalDateOfTenant();
        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.loanRepaymentScheduleTransactionProcessorFactory
                .determineProcessor(loan.transactionProcessingStrategy());

        if (!loan.repaymentScheduleDetail().isInterestRecalculationEnabled() || !loan.status().isActive()
                || !loanRepaymentScheduleTransactionProcessor.isInterestFirstRepaymentScheduleTransactionProcessor()) { return; }

        if (loan.loanProduct().isMultiDisburseLoan()) {
            final BigDecimal disbursedAmount = loan.getDisbursedAmount();
            final BigDecimal principalRepaid = loan.getLoanSummary().getTotalPrincipalRepaid();
            final BigDecimal principalWrittenOff = loan.getLoanSummary().getTotalPrincipalWrittenOff();
            if (disbursedAmount.subtract(principalWrittenOff).subtract(principalRepaid).compareTo(BigDecimal.ZERO) != 1) { return; }
        }
        final MonetaryCurrency currency = loan.getCurrency();
        Money totalPrincipal = Money.zero(currency);
        final List<LoanSchedulePeriodData> futureInstallments = new ArrayList<>();
        for (final LoanRepaymentScheduleInstallment currentInstallment : loan.fetchRepaymentScheduleInstallments()) {
            if (currentInstallment.isNotFullyPaidOff()) {
                if (!currentInstallment.getDueDate().isAfter(today)) {
                    totalPrincipal = totalPrincipal.plus(currentInstallment.getPrincipalOutstanding(currency));
                }
            }
        }
        final LoanApplicationTerms loanApplicationTerms = constructLoanApplicationTerms(loan);
        final LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment = this.loanScheduleAssembler.calculatePrepaymentAmount(
                currency, today, loanApplicationTerms, loan, loan.getOfficeId(), loanRepaymentScheduleTransactionProcessor);
        Money totalAmount = totalPrincipal.plus(loanRepaymentScheduleInstallment.getFeeChargesOutstanding(currency))
                .plus(loanRepaymentScheduleInstallment.getPenaltyChargesOutstanding(currency));
        Money interestDue = Money.zero(currency);
        if (loanRepaymentScheduleInstallment.isInterestDue(currency)) {
            interestDue = loanRepaymentScheduleInstallment.getInterestOutstanding(currency);
            totalAmount = totalAmount.plus(interestDue);
        }
        boolean isNewPaymentRequired = loanRepaymentScheduleInstallment.isInterestDue(currency) || totalPrincipal.isGreaterThanZero();
        final List<LoanTransaction> modifiedTransactions = new ArrayList<>();
        final List<LoanTransaction> transactions = loan.retreiveListOfTransactionsPostDisbursementExcludeAccruals();
        for (final LoanTransaction loanTransaction : transactions) {

            modifiedTransactions.add(LoanTransaction.copyTransactionProperties(loanTransaction));
        }
        if (isNewPaymentRequired) {
            final LoanTransaction ondayPaymentTransaction = LoanTransaction.repayment(null, totalAmount, null, today, null);
            modifiedTransactions.add(ondayPaymentTransaction);
        }

        final LoanScheduleModel model = this.loanScheduleAssembler.assembleForInterestRecalculation(loanApplicationTerms,
                loan.getOfficeId(), modifiedTransactions, loan, loanRepaymentScheduleTransactionProcessor,
                loan.fetchInterestRecalculateFromDate());
        final LoanScheduleData scheduleDate = model.toData();
        final Collection<LoanSchedulePeriodData> periodDatas = scheduleDate.getPeriods();
        for (final LoanSchedulePeriodData periodData : periodDatas) {
            if ((periodData.periodDueDate().isEqual(today) || periodData.periodDueDate().isAfter(today)) && isNewPaymentRequired) {
                final LoanSchedulePeriodData loanSchedulePeriodData = LoanSchedulePeriodData.repaymentOnlyPeriod(periodData.periodNumber(),
                        periodData.periodFromDate(), periodData.periodDueDate(), totalPrincipal.getAmount(),
                        periodData.principalLoanBalanceOutstanding(), interestDue.getAmount(),
                        loanRepaymentScheduleInstallment.getFeeChargesCharged(currency).getAmount(),
                        loanRepaymentScheduleInstallment.getPenaltyChargesCharged(currency).getAmount(), totalAmount.getAmount(),
                        totalPrincipal.plus(interestDue).getAmount(), periodData.getRecalculatedInterestComponent());
                futureInstallments.add(loanSchedulePeriodData);
                isNewPaymentRequired = false;
            } else if (periodData.periodDueDate().isAfter(today)) {
                futureInstallments.add(periodData);
            }

        }
        loanScheduleData.updateFuturePeriods(futureInstallments);
    }

    @Override
    public LoanScheduleData generateLoanScheduleForVariableInstallmentRequest(final Long loanId, final String json) {
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        this.loanScheduleAssembler.assempleVariableScheduleFrom(loan, json);
        return constructLoanScheduleData(loan);
    }

    @Override
    public LoanScheduleModel generateLoanScheduleWithCurrentStatus(final Long loanId) {
        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        final boolean considerFutureDisbursmentsInSchedule = true;
        final boolean considerAllDisbursmentsInSchedule = false;
        final LocalDate recalculateFrom = null;
        final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom,
                considerFutureDisbursmentsInSchedule, considerAllDisbursmentsInSchedule);
        return loan.regenerateScheduleModel(scheduleGeneratorDTO);
    }

    private LoanScheduleData constructLoanScheduleData(final Loan loan) {
        final Collection<LoanRepaymentScheduleInstallment> installments = loan.getRepaymentScheduleInstallments();
        final List<LoanSchedulePeriodData> installmentData = new ArrayList<>();
        final MonetaryCurrency currency = loan.getCurrency();
        Money outstanding = loan.getPrincpal();

        List<LoanDisbursementDetails> disbursementDetails = new ArrayList<>();
        if (loan.isMultiDisburmentLoan()) {
            disbursementDetails = loan.getDisbursementDetails();
            outstanding = outstanding.zero();
        }
        Money principal = outstanding;
        final Iterator<LoanDisbursementDetails> disbursementItr = disbursementDetails.iterator();
        LoanDisbursementDetails loanDisbursementDetails = null;
        if (disbursementItr.hasNext()) {
            loanDisbursementDetails = disbursementItr.next();
        }

        Money totalInterest = principal.zero();
        Money totalCharge = principal.zero();
        Money totalPenalty = principal.zero();

        for (final LoanRepaymentScheduleInstallment installment : installments) {
            if (loanDisbursementDetails != null
                    && !loanDisbursementDetails.expectedDisbursementDateAsLocalDate().isAfter(installment.getDueDate())) {
                outstanding = outstanding.plus(loanDisbursementDetails.principal());
                principal = principal.plus(loanDisbursementDetails.principal());
                if (disbursementItr.hasNext()) {
                    loanDisbursementDetails = disbursementItr.next();
                } else {
                    loanDisbursementDetails = null;
                }
            }
            outstanding = outstanding.minus(installment.getPrincipal(currency));
            final LoanSchedulePeriodData loanSchedulePeriodData = LoanSchedulePeriodData.repaymentOnlyPeriod(
                    installment.getInstallmentNumber(), installment.getFromDate(), installment.getDueDate(),
                    installment.getPrincipal(currency).getAmount(), outstanding.getAmount(),
                    installment.getInterestCharged(currency).getAmount(), installment.getFeeChargesCharged(currency).getAmount(),
                    installment.getPenaltyChargesCharged(currency).getAmount(), installment.getDue(currency).getAmount(),
                    installment.getTotalPrincipalAndInterest(currency).getAmount(), installment.isRecalculatedInterestComponent());
            installmentData.add(loanSchedulePeriodData);
            totalInterest = totalInterest.plus(installment.getInterestCharged(currency));
            totalCharge = totalCharge.plus(installment.getFeeChargesCharged(currency));
            totalPenalty = totalPenalty.plus(installment.getPenaltyChargesCharged(currency));
        }

        final CurrencyData currencyData = this.currencyReadPlatformService.retrieveCurrency(currency.getCode());

        final LoanScheduleData scheduleData = new LoanScheduleData(currencyData, installmentData,
                loan.getLoanRepaymentScheduleDetail().getNumberOfRepayments(), principal.getAmount(), principal.getAmount(),
                totalInterest.getAmount(), totalCharge.getAmount(), totalPenalty.getAmount(),
                principal.plus(totalCharge).plus(totalInterest).plus(totalPenalty).getAmount());

        return scheduleData;
    }

    private LoanApplicationTerms constructLoanApplicationTerms(final Loan loan) {
        final LocalDate recalculateFrom = null;
        final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);
        final LoanApplicationTerms loanApplicationTerms = loan.constructLoanApplicationTerms(scheduleGeneratorDTO);
        return loanApplicationTerms;
    }
}