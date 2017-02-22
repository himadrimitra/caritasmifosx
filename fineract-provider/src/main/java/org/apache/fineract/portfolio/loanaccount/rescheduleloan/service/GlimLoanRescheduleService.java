/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.rescheduleloan.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.data.ScheduleGeneratorDTO;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallmentRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanSummary;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanApplicationTerms;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanRepaymentScheduleHistory;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanRepaymentScheduleHistoryRepository;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModel;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModelDisbursementPeriod;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModelPeriod;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModelRepaymentPeriod;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleRequest;
import org.apache.fineract.portfolio.loanaccount.service.LoanWritePlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class GlimLoanRescheduleService {

    private final LoanRepaymentScheduleHistoryRepository loanRepaymentScheduleHistoryRepository;
    private final LoanRepository loanRepository;
    private final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository;
    private final LoanWritePlatformService loanWritePlatformService;

    @Autowired
    public GlimLoanRescheduleService(final LoanRepaymentScheduleHistoryRepository loanRepaymentScheduleHistoryRepository,
            final LoanRepository loanRepository, final LoanRepaymentScheduleInstallmentRepository repaymentScheduleInstallmentRepository,
            final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository, final LoanWritePlatformService loanWritePlatformService) {
        this.loanRepaymentScheduleHistoryRepository = loanRepaymentScheduleHistoryRepository;
        this.loanRepository = loanRepository;
        this.repaymentScheduleInstallmentRepository = repaymentScheduleInstallmentRepository;
        this.applicationCurrencyRepository = applicationCurrencyRepository;
        this.loanWritePlatformService = loanWritePlatformService;
    }

    public void approveGlimRescheduleRequest(Loan loan, final LoanRescheduleRequest loanRescheduleRequest,
            Collection<LoanRepaymentScheduleHistory> loanRepaymentScheduleHistoryList, ScheduleGeneratorDTO scheduleGeneratorDTO,
            LoanApplicationTerms loanApplicationTerms, LocalDate recalculateFrom, final AppUser appUser, LocalDate approvedOnDate) {
        this.loanWritePlatformService.updateScheduleDates(loan, scheduleGeneratorDTO, loanApplicationTerms, recalculateFrom);
        loan.updateRescheduledByUser(appUser);
        loan.updateRescheduledOnDate(DateUtils.getLocalDateOfTenant());
        loan.updateLoanScheduleDependentDerivedFields();
        loanRescheduleRequest.approve(appUser, approvedOnDate);
        saveAndFlushLoanWithDataIntegrityViolationChecks(loan);
        for (LoanRepaymentScheduleHistory loanRepaymentScheduleHistory : loanRepaymentScheduleHistoryList) {
            this.loanRepaymentScheduleHistoryRepository.save(loanRepaymentScheduleHistory);
        }
    }

    public LoanScheduleModel getGlimLoanScheduleModels(Loan loan, ScheduleGeneratorDTO scheduleGeneratorDTO,
            LoanApplicationTerms loanApplicationTerms, LocalDate recalculateFrom) {
        this.loanWritePlatformService.updateScheduleDates(loan, scheduleGeneratorDTO, loanApplicationTerms, recalculateFrom);
        return constructGlimScheduleModel(loan);
    }

    public LoanScheduleModel constructGlimScheduleModel(Loan loan) {
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(loan.getCurrency());
        Collection<LoanScheduleModelPeriod> periods = getPeriods(loan);
        LoanSummary summary = loan.getSummary();
        final int loanTermInDays = Days.daysBetween(loan.getExpectedMaturityDate(), loan.getDisbursementDate()).getDays();
        LoanScheduleModel loanScheduleModel = LoanScheduleModel.from(periods, applicationCurrency, loanTermInDays,
                Money.of(loan.getCurrency(), loan.getDisbursedAmount()), summary.getTotalPrincipalDisbursed(),
                summary.getTotalPrincipalRepaid(), summary.getTotalInterestCharged(), summary.getTotalFeeChargesCharged(),
                summary.getTotalPenaltyChargesCharged(), summary.getTotalExpectedRepayment(), summary.getTotalOutstanding());
        return loanScheduleModel;
    }

    public Collection<LoanScheduleModelPeriod> getPeriods(Loan loan) {
        Collection<LoanScheduleModelPeriod> periods = new ArrayList<>();
        Money outstandingBalance = Money.of(loan.getCurrency(), loan.getSummary().getTotalPrincipalDisbursed());
        final BigDecimal chargesDueAtTimeOfDisbursement = deriveTotalChargesDueAtTimeOfDisbursement(loan.charges());
        final LoanScheduleModelDisbursementPeriod disbursementPeriod = LoanScheduleModelDisbursementPeriod.disbursement(
                loan.getDisbursementDate(), outstandingBalance, chargesDueAtTimeOfDisbursement);
        periods.add(disbursementPeriod);
        for (LoanRepaymentScheduleInstallment installment : loan.getRepaymentScheduleInstallments()) {
            outstandingBalance = outstandingBalance.minus(installment.getPrincipal(loan.getCurrency()));
            final LoanScheduleModelPeriod loanScheduleModelPeriod = createLoanScheduleModelPeriod(installment, outstandingBalance);
            periods.add(loanScheduleModelPeriod);
        }
        return periods;
    }

    private LoanScheduleModelPeriod createLoanScheduleModelPeriod(final LoanRepaymentScheduleInstallment installment,
            final Money outstandingPrincipal) {
        final MonetaryCurrency currency = outstandingPrincipal.getCurrency();
        LoanScheduleModelPeriod scheduledLoanInstallment = LoanScheduleModelRepaymentPeriod.repayment(installment.getInstallmentNumber(),
                installment.getFromDate(), installment.getDueDate(), installment.getPrincipal(currency), outstandingPrincipal,
                installment.getInterestCharged(currency), installment.getFeeChargesCharged(currency),
                installment.getPenaltyChargesCharged(currency), installment.getDue(currency),
                installment.isRecalculatedInterestComponent());
        return scheduledLoanInstallment;
    }

    private BigDecimal deriveTotalChargesDueAtTimeOfDisbursement(final Set<LoanCharge> loanCharges) {
        BigDecimal chargesDueAtTimeOfDisbursement = BigDecimal.ZERO;
        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isDueAtDisbursement()) {
                chargesDueAtTimeOfDisbursement = chargesDueAtTimeOfDisbursement.add(loanCharge.amount());
            }
        }
        return chargesDueAtTimeOfDisbursement;
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
}
