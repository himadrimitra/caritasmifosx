package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.charge.service.ChargeEnumerations;
import org.apache.fineract.portfolio.charge.service.ChargeUtils;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargeData;
import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueCalculationDTO;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRecurringCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleTransactionProcessorFactory;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PenaltyPeriod;
import org.apache.fineract.portfolio.loanaccount.loanschedule.service.LoanScheduleHistoryReadPlatformService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.emory.mathcs.backport.java.util.Collections;

@Service
public class LoanOverdueChargeServiceImpl implements LoanOverdueChargeService {

    private final ChargeRepositoryWrapper chargeRepository;
    private final LoanScheduleHistoryReadPlatformService loanScheduleHistoryReadPlatformService;
    private final LoanRepaymentScheduleTransactionProcessorFactory transactionProcessingStrategy;

    @Autowired
    public LoanOverdueChargeServiceImpl(final ChargeRepositoryWrapper chargeRepository,
            final LoanScheduleHistoryReadPlatformService loanScheduleHistoryReadPlatformService,
            final LoanRepaymentScheduleTransactionProcessorFactory transactionProcessingStrategy) {
        this.chargeRepository = chargeRepository;
        this.loanScheduleHistoryReadPlatformService = loanScheduleHistoryReadPlatformService;
        this.transactionProcessingStrategy = transactionProcessingStrategy;
    }

    @Override
    public void applyOverdueChargesForLoan(final Loan loan, final LocalDate runOndate) {
        MonetaryCurrency currency = loan.getCurrency();
        List<LoanRecurringCharge> recurringCharges = loan.getLoanRecurringCharges();
        boolean incomepostingEnabled = loan.isInterestRecalculationEnabled() && loan.isCompoundingToBePostedAsTransaction();
        if (!recurringCharges.isEmpty()) {
            List<LoanTransaction> paymentTransactions = loan.retrivePaymentTransactions();
            boolean isReverseOrder = false;
            List<LoanTransaction> incomeTransactions = loan.retreiveListOfIncomePostingTransactions(isReverseOrder);
            boolean isBasedOnOriginalSchedule = false;
            boolean isBasedOnCurrentSchedule = false;
            boolean isOriginalScheduleWithPostingInterest = false;
            boolean isPenaltyOnlyBasedOriginalSchedule = false;
            boolean isOverdueChargePresent = false;
            for (LoanRecurringCharge recurringCharge : recurringCharges) {
                if (recurringCharge.getChargeOverueDetail() == null) {
                    continue;
                }
                isOverdueChargePresent = true;
                isBasedOnOriginalSchedule = isBasedOnOriginalSchedule
                        || recurringCharge.getChargeOverueDetail().isBasedOnOriginalSchedule();
                isBasedOnCurrentSchedule = isBasedOnCurrentSchedule || !recurringCharge.getChargeOverueDetail().isBasedOnOriginalSchedule();
                boolean considerOnlyPostedInterest = incomepostingEnabled
                        || recurringCharge.getChargeOverueDetail().isConsiderOnlyPostedInterest();
                isOriginalScheduleWithPostingInterest = isOriginalScheduleWithPostingInterest || considerOnlyPostedInterest;
                isPenaltyOnlyBasedOriginalSchedule = isPenaltyOnlyBasedOriginalSchedule
                        || (recurringCharge.getChargeOverueDetail().isBasedOnOriginalSchedule() && !considerOnlyPostedInterest);
            }
            if (!isOverdueChargePresent) { return; }
            List<LoanRepaymentScheduleInstallment> currentInstallments = loan.getRepaymentScheduleInstallments();
            List<LoanRepaymentScheduleInstallment> originalInstallments = new ArrayList<>();
            List<LoanRepaymentScheduleInstallment> originalInstallmentsWithPostedInterest = new ArrayList<>();
            List<LoanTransaction> paymentTransactionsForOriginalSchedule = new ArrayList<>();
            List<LoanTransaction> paymentTransactionsForOriginalScheduleWithPostedInterest = new ArrayList<>();

            if (isBasedOnOriginalSchedule) {
                originalInstallments = constructDataForOverdueCalculationBasedOnOriginalSchedule(loan, currency, paymentTransactions,
                        incomeTransactions, isOriginalScheduleWithPostingInterest, isPenaltyOnlyBasedOriginalSchedule,
                        originalInstallmentsWithPostedInterest, paymentTransactionsForOriginalSchedule,
                        paymentTransactionsForOriginalScheduleWithPostedInterest);

            }

            Map<Long, Collection<LoanChargeData>> overdueChargeData = new HashMap<>(1);
            for (LoanRecurringCharge recurringCharge : recurringCharges) {
                List<LoanRepaymentScheduleInstallment> installments = null;
                List<LoanTransaction> transactions = null;
                boolean considerOnlyPostedInterest = incomepostingEnabled
                        || recurringCharge.getChargeOverueDetail().isConsiderOnlyPostedInterest();

                if (recurringCharge.getChargeOverueDetail().isBasedOnOriginalSchedule()) {
                    if (considerOnlyPostedInterest) {
                        installments = originalInstallmentsWithPostedInterest;
                        transactions = paymentTransactionsForOriginalScheduleWithPostedInterest;
                    } else {
                        installments = originalInstallments;
                        transactions = paymentTransactionsForOriginalSchedule;
                    }
                } else {
                    installments = currentInstallments;
                    transactions = paymentTransactions;
                }
                LoanOverdueCalculationDTO overdueCalculationDetail = new LoanOverdueCalculationDTO(loan.getId(), runOndate, currency,
                        transactions, installments);

                LoanChargeData chargeData = applyOverdueChargesForLoan(recurringCharge, overdueCalculationDetail);
                if (chargeData != null && chargeData.getAmount().compareTo(BigDecimal.ZERO) == 1) {
                    if (overdueChargeData.containsKey(chargeData.getChargeId())) {
                        overdueChargeData.get(chargeData.getChargeId()).add(chargeData);
                    } else {
                        Collection<LoanChargeData> chargeDataList = new ArrayList<>();
                        chargeDataList.add(chargeData);
                        overdueChargeData.put(chargeData.getChargeId(), chargeDataList);
                    }
                }
            }

            for (Map.Entry<Long, Collection<LoanChargeData>> mapEntry : overdueChargeData.entrySet()) {
                final Charge charge = this.chargeRepository.findOneWithNotFoundDetection(mapEntry.getKey());
                for (LoanChargeData chargeData : mapEntry.getValue()) {
                    LoanCharge loanCharge = new LoanCharge(loan, charge, chargeData.getAmount(), chargeData.getPercentage(),
                            chargeData.getChargeTimeType(), chargeData.getChargeCalculationType(), chargeData.getDueDate(),
                            chargeData.getChargePaymentMode(), chargeData.isPenalty());
                    loan.getLoanCharges().add(loanCharge);
                }
            }

        }

    }

    private List<LoanRepaymentScheduleInstallment> constructDataForOverdueCalculationBasedOnOriginalSchedule(final Loan loan,
            MonetaryCurrency currency, List<LoanTransaction> paymentTransactions, List<LoanTransaction> incomeTransactions,
            boolean isOriginalScheduleWithPostingInterest, boolean isPenaltyOnlyBasedOriginalSchedule,
            List<LoanRepaymentScheduleInstallment> originalInstallmentsWithPostedInterest,
            List<LoanTransaction> paymentTransactionsForOriginalSchedule,
            List<LoanTransaction> paymentTransactionsForOriginalScheduleWithPostedInterest) {
        List<LoanRepaymentScheduleInstallment> originalInstallments;
        ListIterator<LoanTransaction> incomeTransactionIterator = incomeTransactions.listIterator();
        originalInstallments = this.loanScheduleHistoryReadPlatformService.retrieveRepaymentArchiveAsInstallments(loan.getId());
        if (isOriginalScheduleWithPostingInterest) {
            for (LoanRepaymentScheduleInstallment installment : originalInstallments) {
                if (!installment.isRecalculatedInterestComponent() && installment.getPrincipal(currency).isZero()) {
                    Money interest = Money.zero(currency);
                    Money fee = Money.zero(currency);
                    Money penalty = Money.zero(currency);
                    while (incomeTransactionIterator.hasNext()) {
                        LoanTransaction transaction = incomeTransactionIterator.next();
                        if (!transaction.getTransactionDate().isAfter(installment.getDueDate()) && transaction.getTransactionDate().isAfter(installment.getFromDate())) {
                            interest = interest.plus(transaction.getInterestPortion());
                            fee = fee.plus(transaction.getFeeChargesPortion());
                            penalty = penalty.plus(transaction.getPenaltyChargesPortion());
                        } else if(transaction.getTransactionDate().isAfter(installment.getDueDate())){
                            incomeTransactionIterator.previous();
                            break;
                        }
                    }
                    LoanRepaymentScheduleInstallment withPOstedInterest = new LoanRepaymentScheduleInstallment(installment.getLoan(),
                            installment.getInstallmentNumber(), installment.getFromDate(), installment.getDueDate(), installment
                                    .getPrincipal(currency).getAmount(), interest.getAmount(), fee.getAmount(), penalty.getAmount(),
                            installment.isRecalculatedInterestComponent(), installment.getLoanCompoundingDetails());
                    originalInstallmentsWithPostedInterest.add(withPOstedInterest);
                } else {
                    originalInstallmentsWithPostedInterest.add(new LoanRepaymentScheduleInstallment(installment));
                }
            }
        }
        for (LoanTransaction transaction : paymentTransactions) {
            if (isPenaltyOnlyBasedOriginalSchedule) {
                paymentTransactionsForOriginalSchedule.add(LoanTransaction.copyTransactionProperties(transaction));
            }
            if (isOriginalScheduleWithPostingInterest) {
                paymentTransactionsForOriginalScheduleWithPostedInterest.add(LoanTransaction.copyTransactionProperties(transaction));
            }
        }
        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessingStrategy
                .determineProcessor(loan.transactionProcessingStrategy());

        if (isPenaltyOnlyBasedOriginalSchedule) {
            loanRepaymentScheduleTransactionProcessor.handleTransaction(loan.getDisbursementDate(), paymentTransactionsForOriginalSchedule,
                    currency, originalInstallments, loan.chargesCopy(), loan.getDisbursementDetails(),
                    loan.isPeriodicAccrualAccountingEnabledOnLoanProduct());
        }

        if (isOriginalScheduleWithPostingInterest) {
            loanRepaymentScheduleTransactionProcessor.handleTransaction(loan.getDisbursementDate(),
                    paymentTransactionsForOriginalScheduleWithPostedInterest, currency, originalInstallmentsWithPostedInterest,
                    loan.chargesCopy(), loan.getDisbursementDetails(), loan.isPeriodicAccrualAccountingEnabledOnLoanProduct());
        }
        return originalInstallments;
    }

    private LoanChargeData applyOverdueChargesForLoan(final LoanRecurringCharge recurringCharge,
            final LoanOverdueCalculationDTO overdueCalculationDetail) {

        MonetaryCurrency currency = overdueCalculationDetail.getCurrency();
        LocalDate considerForRunAsOnDate = overdueCalculationDetail.getRunOnDate().minusDays(
                recurringCharge.getChargeOverueDetail().getGracePeriod());
        Integer penaltyFreePeriod = recurringCharge.getChargeOverueDetail().getPenaltyFreePeriod();
        boolean graceApplicableForFirstOverdue = recurringCharge.getChargeOverueDetail().getGraceType().isApplyGraceForFirstOverdue();
        List<LocalDate> overdueScheduleDates = new ArrayList<>();
        for (LoanRepaymentScheduleInstallment installment : overdueCalculationDetail.getInstallments()) {
            if (installment.isNotFullyPaidOff() && !installment.getDueDate().isAfter(considerForRunAsOnDate)) {
                overdueCalculationDetail.plusPrincipalOutstingAsOnDate(installment.getPrincipalOutstanding(currency));
                overdueCalculationDetail.plusInterestOutstingAsOnDate(installment.getInterestOutstanding(currency));
                Money currentChargeOverdue = installment.getFeeChargesOutstanding(currency).plus(
                        installment.getPenaltyChargesOutstanding(currency));
                overdueCalculationDetail.plusChargeOutstingAsOnDate(currentChargeOverdue);
                overdueScheduleDates.add(installment.getDueDate());
                if (overdueCalculationDetail.getFirstOverdueDate().isAfter(installment.getDueDate())) {
                    overdueCalculationDetail.setFirstOverdueDate(installment.getDueDate());
                }
                overdueCalculationDetail.getOverdueInstallments().put(installment.getDueDate().plusDays(penaltyFreePeriod), installment);
                if (graceApplicableForFirstOverdue) {
                    considerForRunAsOnDate = considerForRunAsOnDate.plusDays(recurringCharge.getChargeOverueDetail().getGracePeriod());
                    penaltyFreePeriod = 0;
                }
            } else if (installment.getDueDate().isAfter(considerForRunAsOnDate) && !installment.getDueDate().isAfter(DateUtils.getLocalDateOfTenant())) {
                overdueCalculationDetail.plusPrincipalPaidAfterOnDate(installment.getPrincipalCompleted(currency).plus(
                        installment.getPrincipalWrittenOff(currency)));
                overdueCalculationDetail.plusInterestPaidAfterOnDate(installment.getInterestPaid(currency)
                        .plus(installment.getInterestWaived(currency)).plus(installment.getInterestWrittenOff(currency)));
                overdueCalculationDetail.plusChargePaidAfterOnDate((installment.getPenaltyChargesPaid(currency).plus(
                        installment.getPenaltyChargesWaived(currency)).plus(installment.getPenaltyChargesWrittenOff(currency)))
                        .plus(installment.getFeeChargesPaid(currency).plus(installment.getFeeChargesWaived(currency))
                                .plus(installment.getFeeChargesWrittenOff(currency))));
            } else if (installment.getDueDate().isAfter(DateUtils.getLocalDateOfTenant())) {
                break;
            }

        }
        LocalDate lastAppliedOnDate = recurringCharge.getChargeOverueDetail().getLastAppliedOnDate();
        LocalDate applyChargeFromDate = overdueCalculationDetail.getFirstOverdueDate().plusDays(
                recurringCharge.getChargeOverueDetail().getPenaltyFreePeriod());
        boolean isChargeAppliedOnce = false;
        if (lastAppliedOnDate != null && !lastAppliedOnDate.isBefore(applyChargeFromDate)) {
            applyChargeFromDate = lastAppliedOnDate;
            isChargeAppliedOnce = true;
        }
        overdueCalculationDetail.setApplyChargeFromDate(applyChargeFromDate);
        overdueCalculationDetail.createDatesForOverdueChange();
        boolean isOnCurrentOutStanding = recurringCharge.getChargeCalculation().isFlat()
                || recurringCharge.getChargeOverueDetail().isCalculateChargeOnCurrentOverdue();
        List<LocalDate> recurrenceDates = new ArrayList<>();
        if (recurringCharge.getFeeFrequency().isInvalid()) {
            isOnCurrentOutStanding = true;
            for (LocalDate scheduleDate : overdueCalculationDetail.getOverdueInstallments().keySet()) {
                if (((isChargeAppliedOnce && scheduleDate.isAfter(applyChargeFromDate)) || (!isChargeAppliedOnce && scheduleDate
                        .isEqual(applyChargeFromDate))) && scheduleDate.isBefore(overdueCalculationDetail.getRunOnDate())) {
                    recurrenceDates.add(scheduleDate);
                }
            }
        } else {
            recurrenceDates = ChargeUtils.retriveRecurrencePeriods(overdueCalculationDetail.getFirstOverdueDate(),
                    overdueCalculationDetail.getRunOnDate(), overdueScheduleDates,
                    recurringCharge.getChargeOverueDetail().getGracePeriod(), recurringCharge.getFeeFrequency(),
                    recurringCharge.getFeeInterval(), applyChargeFromDate);

        }

        PenaltyChargeGeneratorFactory penaltyChargeGeneratorFactory = new PenaltyChargeGeneratorFactory();
        PenaltyChargePeriodGenerator chargePeriodGenerator = penaltyChargeGeneratorFactory.findPenaltyChargePeriodGenerator(
                recurringCharge, isOnCurrentOutStanding);
        PenaltyChargeCalculator chargeCalculator = penaltyChargeGeneratorFactory.findPenaltyChargeCalculator(recurringCharge);

        Collection<PenaltyPeriod> penaltyPeriods = new ArrayList<>();
        LocalDate lastChargeAppliedDate = null;
        if (!recurrenceDates.isEmpty()) {
            Collections.sort(recurrenceDates, Collections.reverseOrder());
            LocalDate endDate = recurrenceDates.remove(0);
            if(isChargeAppliedOnce && endDate.isEqual(applyChargeFromDate)){
                return null;
            }
            lastChargeAppliedDate = endDate;
            for (LocalDate date : overdueCalculationDetail.getDatesForOverdueAmountChange()) {
                if(date.isAfter(endDate)){
                    chargePeriodGenerator.handleReversalOfTransactionForOverdueCalculation(overdueCalculationDetail, date);
                    chargePeriodGenerator.handleReversalOfOverdueInstallment(overdueCalculationDetail, date);
                }else{break;}
            }
            if (recurrenceDates.isEmpty()) {
                /*LocalDate chargeApplicableFromDate = overdueCalculationDetail.getFirstOverdueDate().plusDays(
                        recurringCharge.getChargeOverueDetail().getPenaltyFreePeriod());*/
                LocalDate recurrerDate = ChargeUtils.retrivePreviosRecurringDate(applyChargeFromDate, recurringCharge.getFeeFrequency(),
                        recurringCharge.getFeeInterval());
                chargePeriodGenerator.createPeriods(recurringCharge, overdueCalculationDetail, penaltyPeriods, endDate, recurrerDate,
                        recurrerDate);
            } else {
                if (isOnCurrentOutStanding) {
                    LocalDate minDate = recurrenceDates.get(recurrenceDates.size() - 1);
                    recurrenceDates.add(minDate);
                }
                for (LocalDate recurrerDate : recurrenceDates) {
                    LocalDate chargeApplicableFromDate = applyChargeFromDate.isAfter(recurrerDate) ? applyChargeFromDate : recurrerDate;
                    chargePeriodGenerator.createPeriods(recurringCharge, overdueCalculationDetail, penaltyPeriods, endDate, recurrerDate,
                            chargeApplicableFromDate);
                    if (applyChargeFromDate.isAfter(recurrerDate) && recurringCharge.getChargeCalculation().isPercentageBased()) {
                        break;
                    }
                    endDate = recurrerDate;

                }
            }
        }

        Money chargeAmount = Money.zero(currency);
        for (PenaltyPeriod penaltyPeriod : penaltyPeriods) {
            chargeAmount = chargeAmount.plus(chargeCalculator.calculateCharge(penaltyPeriod));
        }
        if(chargeAmount.isGreaterThanZero() && lastChargeAppliedDate != null){
            recurringCharge.getChargeOverueDetail().setLastAppliedOnDate(lastChargeAppliedDate.toDate());
        }
        return LoanChargeData.newLoanOverdueCharge(recurringCharge.getChargeId(), overdueCalculationDetail.getLoanId(),
                overdueCalculationDetail.getRunOnDate(), chargeAmount.getAmount(), recurringCharge.getAmount(),
                ChargeEnumerations.chargeTimeType(recurringCharge.getChargeTimeType()),
                ChargeEnumerations.chargeCalculationType(recurringCharge.getChargeCalculation()),
                ChargeEnumerations.chargePaymentMode(recurringCharge.getChargePaymentMode()), recurringCharge.getTaxGroupId());
    }

}
