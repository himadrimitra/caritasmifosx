package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.charge.service.ChargeEnumerations;
import org.apache.fineract.portfolio.charge.service.ChargeUtils;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargeData;
import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueCalculationDTO;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRecurringCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleTransactionProcessorFactory;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
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
    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanAssembler loanAssembler;

    @Autowired
    public LoanOverdueChargeServiceImpl(final ChargeRepositoryWrapper chargeRepository,
            final LoanScheduleHistoryReadPlatformService loanScheduleHistoryReadPlatformService,
            final LoanRepaymentScheduleTransactionProcessorFactory transactionProcessingStrategy,
            final LoanReadPlatformService loanReadPlatformService, final LoanAssembler loanAssembler) {
        this.chargeRepository = chargeRepository;
        this.loanScheduleHistoryReadPlatformService = loanScheduleHistoryReadPlatformService;
        this.transactionProcessingStrategy = transactionProcessingStrategy;
        this.loanReadPlatformService = loanReadPlatformService;
        this.loanAssembler = loanAssembler;
    }

    @Override
    public boolean applyOverdueChargesForLoan(final Loan loan, final LocalDate runOndate) {

        List<LoanRecurringCharge> recurringCharges = loan.getLoanRecurringCharges();

        Map<Long, Collection<LoanChargeData>> overdueChargeData = new HashMap<>(1);
        LocalDate brokenPeriodOnDate = null;
        if (!recurringCharges.isEmpty()) {
            calculateOverdueCharges(loan, runOndate, recurringCharges, overdueChargeData, brokenPeriodOnDate);
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
        return !overdueChargeData.isEmpty();

    }

    public boolean applyOverdueChargesForLoan(final Loan loan, final LocalDate runOndate, final LocalDate brokenPeriodOnDate) {
        List<LoanRecurringCharge> recurringCharges = loan.getLoanRecurringCharges();

        Map<Long, Collection<LoanChargeData>> overdueChargeData = new HashMap<>(1);
        if (!recurringCharges.isEmpty()) {
            calculateOverdueCharges(loan, runOndate, recurringCharges, overdueChargeData, brokenPeriodOnDate);
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
        return !overdueChargeData.isEmpty();
    }

    @Override
    public boolean updateOverdueChargesOnPayment(final Loan loan, final LocalDate transactionDate) {
        boolean isChargeChanged = false;
        List<LoanRecurringCharge> recurringCharges = loan.getLoanRecurringCharges();
        if (!recurringCharges.isEmpty()) {
            boolean isOverduePresentBeforeTranasctionDate = false;
            boolean isOverduePresent = false;
            boolean isOverdueChargePresent = false;
            Map<Long, LoanRecurringCharge> chargeApplicableFromDates = new HashMap<>();
            for (LoanRecurringCharge recurringCharge : recurringCharges) {
                if (recurringCharge.getChargeOverueDetail() == null) {
                    continue;
                }
                isOverdueChargePresent = true;
                chargeApplicableFromDates.put(recurringCharge.getChargeId(), recurringCharge);
            }
            if (!isOverdueChargePresent) { return isChargeChanged; }
            LocalDate currentDate = DateUtils.getLocalDateOfTenant();
            for (LoanRepaymentScheduleInstallment installment : loan.getRepaymentScheduleInstallments()) {
                if (installment.isNotFullyPaidOff()) {
                    if (!installment.getDueDate().isAfter(transactionDate)) {
                        isOverduePresentBeforeTranasctionDate = true;
                        isOverduePresent = true;
                        for (final Long chargeId : chargeApplicableFromDates.keySet()) {
                            chargeApplicableFromDates.get(chargeId).getChargeOverueDetail()
                                    .setLastAppliedOnDate(installment.getDueDate().toDate());
                        }

                    } else if (!installment.getDueDate().isAfter(currentDate)) {
                        isOverduePresent = true;
                    }

                    break;
                }
                if (installment.getDueDate().isAfter(currentDate)) {
                    break;
                }
            }
            if (isOverduePresent) {
                Collection<LoanCharge> charges = loan.getLoanCharges();
                for (LoanCharge loanCharge : charges) {
                    if (loanCharge.isActive() && loanCharge.isOverdueInstallmentCharge()) {
                        if (loanCharge.getDueLocalDate().isAfter(transactionDate)) {
                            loanCharge.setActive(false);
                            isChargeChanged = true;
                        } else if (loanCharge.getDueLocalDate().isAfter(
                                chargeApplicableFromDates.get(loanCharge.getCharge().getId()).getChargeOverueDetail()
                                        .getLastAppliedOnDate())) {
                            chargeApplicableFromDates.get(loanCharge.getCharge().getId()).getChargeOverueDetail()
                                    .setLastAppliedOnDate(loanCharge.getDueLocalDate().toDate());
                        }
                    }
                }
            }

            if (isOverduePresentBeforeTranasctionDate) {
                isChargeChanged = isChargeChanged || applyOverdueChargesForLoan(loan, transactionDate, transactionDate);
            } else {
                for (final Long chargeId : chargeApplicableFromDates.keySet()) {
                    chargeApplicableFromDates.get(chargeId).getChargeOverueDetail().setLastAppliedOnDate(null);
                    chargeApplicableFromDates.get(chargeId).getChargeOverueDetail().setLastRunOnDate(transactionDate.toDate());
                }
            }
        }
        return isChargeChanged;

    }

    public BigDecimal calculateOverdueChargesForLoan(final Long loanId, final LocalDate runOndate, final LocalDate brokenPeriodOnDate) {

        List<LoanRecurringCharge> recurringCharges = this.loanReadPlatformService.retrieveLoanOverdueRecurringCharge(loanId);
        Map<Long, Collection<LoanChargeData>> overdueChargeData = new HashMap<>(1);
        if (!recurringCharges.isEmpty()) {
            boolean isBasedOnOriginalSchedule = false;
            boolean isBasedOnCurrentSchedule = false;
            boolean isOriginalScheduleWithPostingInterest = false;
            boolean isPenaltyOnlyBasedOriginalSchedule = false;
            boolean isOverdueChargePresent = false;
            boolean considerOnlyPostedInterest = false;
            for (LoanRecurringCharge recurringCharge : recurringCharges) {
                if (recurringCharge.getChargeOverueDetail() == null) {
                    continue;
                }
                isOverdueChargePresent = true;
                isBasedOnOriginalSchedule = isBasedOnOriginalSchedule
                        || recurringCharge.getChargeOverueDetail().isBasedOnOriginalSchedule();
                isBasedOnCurrentSchedule = isBasedOnCurrentSchedule || !recurringCharge.getChargeOverueDetail().isBasedOnOriginalSchedule();
                considerOnlyPostedInterest = considerOnlyPostedInterest
                        || recurringCharge.getChargeOverueDetail().isConsiderOnlyPostedInterest();
                isOriginalScheduleWithPostingInterest = isOriginalScheduleWithPostingInterest || considerOnlyPostedInterest;
                isPenaltyOnlyBasedOriginalSchedule = isPenaltyOnlyBasedOriginalSchedule
                        || (recurringCharge.getChargeOverueDetail().isBasedOnOriginalSchedule() && !considerOnlyPostedInterest);
            }
            if (isOverdueChargePresent && (isBasedOnOriginalSchedule || considerOnlyPostedInterest)) {
                final Loan loan = this.loanAssembler.assembleFrom(loanId);
                calculateOverdueCharges(loan, runOndate, recurringCharges, overdueChargeData, brokenPeriodOnDate);
            } else {
                calculateOverdueCharges(loanId, runOndate, recurringCharges, overdueChargeData, brokenPeriodOnDate);
            }

        }
        BigDecimal chargesToBeApplied = BigDecimal.ZERO;
        for (Map.Entry<Long, Collection<LoanChargeData>> mapEntry : overdueChargeData.entrySet()) {
            for (LoanChargeData charge : mapEntry.getValue()) {
                chargesToBeApplied = MathUtility.add(chargesToBeApplied, charge.getAmount());
            }
        }
        return chargesToBeApplied;
    }

    private void calculateOverdueCharges(final Loan loan, final LocalDate runOndate, List<LoanRecurringCharge> recurringCharges,
            Map<Long, Collection<LoanChargeData>> overdueChargeData, LocalDate brokenPeriodOnDate) {
        MonetaryCurrency currency = loan.getCurrency();
        boolean incomepostingEnabled = loan.isInterestRecalculationEnabled() && loan.isCompoundingToBePostedAsTransaction();
        List<LoanTransaction> paymentTransactions = loan.retrivePaymentTransactions();
        boolean isReverseOrder = false;
        List<LoanTransaction> incomeTransactions = loan.retreiveListOfIncomePostingTransactions(isReverseOrder);
        boolean isBasedOnOriginalSchedule = false;
        boolean isOriginalScheduleWithPostingInterest = false;
        boolean isCurrentScheduleWithPostingInterest = false;
        boolean isPenaltyOnlyBasedOriginalSchedule = false;
        boolean isOverdueChargePresent = false;
        for (LoanRecurringCharge recurringCharge : recurringCharges) {
            if (recurringCharge.getChargeOverueDetail() == null) {
                continue;
            }
            isOverdueChargePresent = true;
            isBasedOnOriginalSchedule = isBasedOnOriginalSchedule || recurringCharge.getChargeOverueDetail().isBasedOnOriginalSchedule();
            boolean considerOnlyPostedInterest = incomepostingEnabled
                    || recurringCharge.getChargeOverueDetail().isConsiderOnlyPostedInterest();
            isOriginalScheduleWithPostingInterest = isOriginalScheduleWithPostingInterest
                    || (considerOnlyPostedInterest && recurringCharge.getChargeOverueDetail().isBasedOnOriginalSchedule());
            isPenaltyOnlyBasedOriginalSchedule = isPenaltyOnlyBasedOriginalSchedule
                    || (recurringCharge.getChargeOverueDetail().isBasedOnOriginalSchedule() && !considerOnlyPostedInterest);
            isCurrentScheduleWithPostingInterest = isCurrentScheduleWithPostingInterest
                    || (considerOnlyPostedInterest && !recurringCharge.getChargeOverueDetail().isBasedOnOriginalSchedule());
        }
        if (!isOverdueChargePresent) { return; }
        List<LoanRepaymentScheduleInstallment> currentInstallments = loan.getRepaymentScheduleInstallments();
        List<LoanRepaymentScheduleInstallment> currentInstallmentsWithPostedInterest = new ArrayList<>(1);
        List<LoanRepaymentScheduleInstallment> originalInstallments = new ArrayList<>();
        List<LoanRepaymentScheduleInstallment> originalInstallmentsWithPostedInterest = new ArrayList<>();
        List<LoanTransaction> paymentTransactionsForOriginalSchedule = new ArrayList<>();
        List<LoanTransaction> paymentTransactionsForOriginalScheduleWithPostedInterest = new ArrayList<>();
        List<LoanTransaction> paymentTransactionsForCurrentScheduleWithPostedInterest = new ArrayList<>(1);
        if (isPenaltyOnlyBasedOriginalSchedule || isOriginalScheduleWithPostingInterest || isCurrentScheduleWithPostingInterest) {
            for (LoanTransaction transaction : paymentTransactions) {
                if (isPenaltyOnlyBasedOriginalSchedule) {
                    paymentTransactionsForOriginalSchedule.add(LoanTransaction.copyTransactionProperties(transaction));
                }
                if (isOriginalScheduleWithPostingInterest) {
                    paymentTransactionsForOriginalScheduleWithPostedInterest.add(LoanTransaction.copyTransactionProperties(transaction));
                }
                if (isCurrentScheduleWithPostingInterest) {
                    paymentTransactionsForCurrentScheduleWithPostedInterest.add(LoanTransaction.copyTransactionProperties(transaction));
                }
            }
        }

        if (isBasedOnOriginalSchedule) {
            originalInstallments = constructDataForOverdueCalculationBasedOnOriginalSchedule(loan, currency, incomeTransactions,
                    isOriginalScheduleWithPostingInterest, isPenaltyOnlyBasedOriginalSchedule, originalInstallmentsWithPostedInterest,
                    paymentTransactionsForOriginalSchedule, paymentTransactionsForOriginalScheduleWithPostedInterest);
        }

        if (isCurrentScheduleWithPostingInterest) {
            constructDataForOverdueCalculationBasedOnPostedInterest(loan, currency, incomeTransactions, currentInstallments,
                    currentInstallmentsWithPostedInterest, paymentTransactionsForCurrentScheduleWithPostedInterest);
        }

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
                    transactions, installments, brokenPeriodOnDate);

            applyOverdueChargesForLoan(recurringCharge, overdueCalculationDetail, overdueChargeData);
        }
    }

    private void addChargeDataToCollection(Map<Long, Collection<LoanChargeData>> overdueChargeData, LoanChargeData chargeData) {
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

    private void constructDataForOverdueCalculationBasedOnPostedInterest(final Loan loan, MonetaryCurrency currency,
            List<LoanTransaction> incomeTransactions, List<LoanRepaymentScheduleInstallment> currentInstallments,
            List<LoanRepaymentScheduleInstallment> currentInstallmentsWithPostedInterest,
            List<LoanTransaction> paymentTransactionsForCurrentScheduleWithPostedInterest) {
        ListIterator<LoanTransaction> incomeTransactionIterator = incomeTransactions.listIterator();
        for (LoanRepaymentScheduleInstallment installment : currentInstallments) {
            if (!installment.isRecalculatedInterestComponent() && installment.getPrincipal(currency).isZero()) {
                Money interest = Money.zero(currency);
                Money fee = Money.zero(currency);
                Money penalty = Money.zero(currency);
                while (incomeTransactionIterator.hasNext()) {
                    LoanTransaction transaction = incomeTransactionIterator.next();
                    if (!transaction.getTransactionDate().isAfter(installment.getDueDate())
                            && transaction.getTransactionDate().isAfter(installment.getFromDate())) {
                        interest = interest.plus(transaction.getInterestPortion());
                        fee = fee.plus(transaction.getFeeChargesPortion());
                        penalty = penalty.plus(transaction.getPenaltyChargesPortion());
                    } else if (transaction.getTransactionDate().isAfter(installment.getDueDate())) {
                        incomeTransactionIterator.previous();
                        break;
                    }
                }
                LoanRepaymentScheduleInstallment withPOstedInterest = new LoanRepaymentScheduleInstallment(installment.getLoan(),
                        installment.getInstallmentNumber(), installment.getFromDate(), installment.getDueDate(), installment.getPrincipal(
                                currency).getAmount(), interest.getAmount(), fee.getAmount(), penalty.getAmount(),
                        installment.isRecalculatedInterestComponent(), installment.getLoanCompoundingDetails());
                currentInstallmentsWithPostedInterest.add(withPOstedInterest);
            } else {
                currentInstallmentsWithPostedInterest.add(new LoanRepaymentScheduleInstallment(installment));
            }
        }

        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessingStrategy
                .determineProcessor(loan.transactionProcessingStrategy());
        loanRepaymentScheduleTransactionProcessor.handleTransaction(loan.getDisbursementDate(),
                paymentTransactionsForCurrentScheduleWithPostedInterest, currency, currentInstallmentsWithPostedInterest,
                loan.chargesCopy(), loan.getDisbursementDetails(), loan.isPeriodicAccrualAccountingEnabledOnLoanProduct());
    }

    private List<LoanRepaymentScheduleInstallment> constructDataForOverdueCalculationBasedOnOriginalSchedule(final Loan loan,
            MonetaryCurrency currency, List<LoanTransaction> incomeTransactions, boolean isOriginalScheduleWithPostingInterest,
            boolean isPenaltyOnlyBasedOriginalSchedule, List<LoanRepaymentScheduleInstallment> originalInstallmentsWithPostedInterest,
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
                        if (!transaction.getTransactionDate().isAfter(installment.getDueDate())
                                && transaction.getTransactionDate().isAfter(installment.getFromDate())) {
                            interest = interest.plus(transaction.getInterestPortion());
                            fee = fee.plus(transaction.getFeeChargesPortion());
                            penalty = penalty.plus(transaction.getPenaltyChargesPortion());
                        } else if (transaction.getTransactionDate().isAfter(installment.getDueDate())) {
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

    private void applyOverdueChargesForLoan(final LoanRecurringCharge recurringCharge,
            final LoanOverdueCalculationDTO overdueCalculationDetail, Map<Long, Collection<LoanChargeData>> overdueChargeData) {

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
            } else if (installment.getDueDate().isAfter(considerForRunAsOnDate)
                    && !installment.getDueDate().isAfter(DateUtils.getLocalDateOfTenant())) {
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
        final boolean includeBrokenPeriodDate = isBrokenPeriodApplyDateAvilable(recurringCharge, overdueCalculationDetail);
        if (lastAppliedOnDate != null && !lastAppliedOnDate.isBefore(applyChargeFromDate)) {
            applyChargeFromDate = lastAppliedOnDate;
            isChargeAppliedOnce = true;
        }
        overdueCalculationDetail.setApplyChargeFromDate(applyChargeFromDate);
        overdueCalculationDetail.createDatesForOverdueChange(includeBrokenPeriodDate);
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

        Map<LocalDate, PenaltyPeriod> penaltyPeriods = new TreeMap<>();
        LocalDate lastChargeAppliedDate = null;
        if (!recurrenceDates.isEmpty()) {
            Collections.sort(recurrenceDates, Collections.reverseOrder());
            LocalDate endDate = recurrenceDates.get(0);
            if (includeBrokenPeriodDate && endDate.isBefore(overdueCalculationDetail.getBrokenPeriodOnDate())) {
                endDate = overdueCalculationDetail.getBrokenPeriodOnDate();
            } else {
                recurrenceDates.remove(0);
            }
            if (isChargeAppliedOnce && endDate.isEqual(applyChargeFromDate)) { return; }
            lastChargeAppliedDate = endDate;
            for (LocalDate date : overdueCalculationDetail.getDatesForOverdueAmountChange()) {
                if (date.isAfter(endDate)) {
                    chargePeriodGenerator.handleReversalOfTransactionForOverdueCalculation(overdueCalculationDetail, date);
                    chargePeriodGenerator.handleReversalOfOverdueInstallment(overdueCalculationDetail, date);
                } else {
                    break;
                }
            }
            if (recurrenceDates.isEmpty()) {
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
        Money totalCahrgeApplied = chargeAmount.zero();
        for (Map.Entry<LocalDate, PenaltyPeriod> penaltyPeriod : penaltyPeriods.entrySet()) {
            chargeAmount = chargeAmount.plus(chargeCalculator.calculateCharge(penaltyPeriod.getValue()));
            if (includeBrokenPeriodDate && penaltyPeriod.getKey().isEqual(overdueCalculationDetail.getBrokenPeriodOnDate())) {
                LoanChargeData chargeData = LoanChargeData.newLoanOverdueCharge(recurringCharge.getChargeId(),
                        overdueCalculationDetail.getLoanId(), overdueCalculationDetail.getRunOnDate(), chargeAmount.getAmount(),
                        recurringCharge.getAmount(), ChargeEnumerations.chargeTimeType(recurringCharge.getChargeTimeType()),
                        ChargeEnumerations.chargeCalculationType(recurringCharge.getChargeCalculation()),
                        ChargeEnumerations.chargePaymentMode(recurringCharge.getChargePaymentMode()), recurringCharge.getTaxGroupId());
                addChargeDataToCollection(overdueChargeData, chargeData);
                totalCahrgeApplied = totalCahrgeApplied.plus(chargeAmount);
                chargeAmount = chargeAmount.zero();
            }
        }
        totalCahrgeApplied = totalCahrgeApplied.plus(chargeAmount);
        if (totalCahrgeApplied.isGreaterThanZero() && lastChargeAppliedDate != null) {
            recurringCharge.getChargeOverueDetail().setLastAppliedOnDate(lastChargeAppliedDate.toDate());
        }
        recurringCharge.getChargeOverueDetail().setLastRunOnDate(overdueCalculationDetail.getRunOnDate().toDate());
        LoanChargeData chargeData = LoanChargeData.newLoanOverdueCharge(recurringCharge.getChargeId(),
                overdueCalculationDetail.getLoanId(), overdueCalculationDetail.getRunOnDate(), chargeAmount.getAmount(),
                recurringCharge.getAmount(), ChargeEnumerations.chargeTimeType(recurringCharge.getChargeTimeType()),
                ChargeEnumerations.chargeCalculationType(recurringCharge.getChargeCalculation()),
                ChargeEnumerations.chargePaymentMode(recurringCharge.getChargePaymentMode()), recurringCharge.getTaxGroupId());
        addChargeDataToCollection(overdueChargeData, chargeData);
    }

    private boolean isBrokenPeriodApplyDateAvilable(final LoanRecurringCharge recurringCharge,
            final LoanOverdueCalculationDTO overdueCalculationDetail) {
        return recurringCharge.getChargeOverueDetail().isApplyChargeForBrokenPeriod()
                && overdueCalculationDetail.getBrokenPeriodOnDate() != null;
    }

    private void calculateOverdueCharges(final Long loanId, final LocalDate runOndate, List<LoanRecurringCharge> recurringCharges,
            Map<Long, Collection<LoanChargeData>> overdueChargeData, final LocalDate brokenPeriodOnDate) {

        List<LoanTransaction> paymentTransactions = this.loanReadPlatformService.retrieveLoanTransactions(loanId,
                LoanTransactionType.REPAYMENT.getValue(), LoanTransactionType.WAIVE_CHARGES.getValue(),
                LoanTransactionType.WAIVE_INTEREST.getValue(), LoanTransactionType.CHARGE_PAYMENT.getValue(),
                LoanTransactionType.REFUND.getValue(), LoanTransactionType.REFUND_FOR_ACTIVE_LOAN.getValue());
        List<LoanRepaymentScheduleInstallment> currentInstallments = this.loanReadPlatformService
                .retrieveLoanRepaymentScheduleInstallments(loanId);
        MonetaryCurrency currency = this.loanReadPlatformService.retrieveLoanCurrency(loanId);
        for (LoanRecurringCharge recurringCharge : recurringCharges) {
            LoanOverdueCalculationDTO overdueCalculationDetail = new LoanOverdueCalculationDTO(loanId, runOndate, currency,
                    paymentTransactions, currentInstallments, brokenPeriodOnDate);
            applyOverdueChargesForLoan(recurringCharge, overdueCalculationDetail, overdueChargeData);
        }
    }
}
