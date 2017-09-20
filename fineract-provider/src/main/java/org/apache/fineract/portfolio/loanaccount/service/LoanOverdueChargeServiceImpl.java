package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import javax.transaction.Transactional;

import org.apache.fineract.accounting.closure.domain.GLClosure;
import org.apache.fineract.accounting.closure.domain.GLClosureRepository;
import org.apache.fineract.accounting.closure.exception.GLClosureForOfficeException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.charge.service.ChargeEnumerations;
import org.apache.fineract.portfolio.charge.service.ChargeUtils;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargeData;
import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueCalculationDTO;
import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueChargeData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanInterestRecalcualtionAdditionalDetails;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRecurringCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleProcessingWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleTransactionProcessorFactory;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PenaltyPeriod;
import org.apache.fineract.portfolio.loanaccount.loanschedule.service.LoanScheduleHistoryReadPlatformService;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import edu.emory.mathcs.backport.java.util.Collections;

@Service
public class LoanOverdueChargeServiceImpl implements LoanOverdueChargeService {

    private final ChargeRepositoryWrapper chargeRepository;
    private final LoanScheduleHistoryReadPlatformService loanScheduleHistoryReadPlatformService;
    private final LoanRepaymentScheduleTransactionProcessorFactory transactionProcessingStrategy;
    private final LoanReadPlatformService loanReadPlatformService;
    private final LoanAssembler loanAssembler;
    private final JdbcTemplate jdbcTemplate;
    private final GLClosureRepository closureRepository;
    private final LoanRepository loanRepository;

    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    final String LOAN_CHARGE_INSERT_STATEMENT = "INSERT INTO `m_loan_charge` (`loan_id`, `charge_id`, `is_penalty`, `charge_time_enum`, `due_for_collection_as_of_date`, `charge_calculation_enum`, `calculation_percentage`, `charge_amount_or_percentage`, `amount`, `amount_paid_derived`, `amount_outstanding_derived`) VALUES (";
    final String LOAN_SCHEDULE_UPDATE_STATEMENT = "UPDATE m_loan_repayment_schedule lrs SET lrs.penalty_charges_amount= IFNULL(lrs.penalty_charges_amount,0) + ";
    final String LOAN_UPDATE_STATEMENT = "UPDATE m_loan ml  SET ml.penalty_charges_charged_derived= IFNULL(ml.penalty_charges_charged_derived, 0) + :amount , ml.penalty_charges_outstanding_derived= IFNULL(ml.penalty_charges_outstanding_derived, 0) + :amount , ml.total_expected_repayment_derived= IFNULL(ml.total_expected_repayment_derived, 0) + :amount ,ml.total_expected_costofloan_derived = IFNULL(ml.total_expected_costofloan_derived,0) + :amount , ml.total_outstanding_derived = IFNULL(ml.total_outstanding_derived,0) + :amount ";
    final String LOAN_SCHEDULE_INSERT_STATEMENT = "INSERT INTO `m_loan_repayment_schedule` (`loan_id`, `fromdate`, `duedate`, `installment`, `penalty_charges_amount`, `completed_derived`, `recalculated_interest_component`) VALUES (";
    final String LOAN_SCHEDULE_UPDATE_STATEMENT_AFTER_MATURITY = "UPDATE m_loan_repayment_schedule rs SET rs.duedate='";
    final String LOAN_OVERDUE_CHARGE_UPDATE = "UPDATE f_loan_overdue_charge_detail ocd join f_loan_recurring_charge rc on rc.id = ocd.recurrence_charge_id SET ocd.last_applied_on_date='";

    @Autowired
    public LoanOverdueChargeServiceImpl(final ChargeRepositoryWrapper chargeRepository,
            final LoanScheduleHistoryReadPlatformService loanScheduleHistoryReadPlatformService,
            final LoanRepaymentScheduleTransactionProcessorFactory transactionProcessingStrategy,
            final LoanReadPlatformService loanReadPlatformService, final LoanAssembler loanAssembler, final RoutingDataSource dataSource,
            final GLClosureRepository closureRepository, final LoanRepository loanRepository) {
        this.chargeRepository = chargeRepository;
        this.loanScheduleHistoryReadPlatformService = loanScheduleHistoryReadPlatformService;
        this.transactionProcessingStrategy = transactionProcessingStrategy;
        this.loanReadPlatformService = loanReadPlatformService;
        this.loanAssembler = loanAssembler;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.closureRepository = closureRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    public boolean updateAndApplyOverdueChargesForLoan(final Loan loan, final LocalDate runOndate, LocalDate brokenPeriodOnDate) {
        if (brokenPeriodOnDate == null) { return applyOverdueChargesForLoan(loan, runOndate, brokenPeriodOnDate); }
        validateLatestClosureByBranch(loan.getOfficeId(), brokenPeriodOnDate, loan.isPeriodicAccrualAccountingEnabledOnLoanProduct());
        return updateOverdueChargesOnPayment(loan, brokenPeriodOnDate);
    }

    @Override
    public boolean updateOverdueChargesOnPayment(final Loan loan, final LocalDate runOnDate) {
        LocalDate reverseLoanChargesBefore = runOnDate;
        List<LoanRecurringCharge> recurringCharges = loan.getLoanRecurringCharges();
        return updatePenaltiesOnPaymentTransaction(loan, runOnDate, reverseLoanChargesBefore, recurringCharges);

    }
  
    @Override
    public boolean updateOverdueChargesOnAdjustPayment(final Loan loan, final LoanTransaction previousTransaction,
            final LoanTransaction newTransaction) {
        boolean isChargeChanged = false;
        List<LoanRecurringCharge> recurringCharges = loan.getLoanRecurringCharges();
        if (!recurringCharges.isEmpty()) {
            LoanTransaction copyTransaction = LoanTransaction.copyTransactionProperties(previousTransaction);
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.transactionProcessingStrategy
                    .determineProcessor(loan.transactionProcessingStrategy());
            copyTransaction.resetDerivedComponents();
            loanRepaymentScheduleTransactionProcessor.handleRefund(copyTransaction, loan.getCurrency(),
                    loan.fetchRepaymentScheduleInstallments(), loan.chargesCopy());
            final Comparator<LoanRepaymentScheduleInstallment> byDate = new Comparator<LoanRepaymentScheduleInstallment>() {

                @Override
                public int compare(LoanRepaymentScheduleInstallment ord1, LoanRepaymentScheduleInstallment ord2) {
                    return ord1.getDueDate().compareTo(ord2.getDueDate());
                }
            };
            Collections.sort(loan.fetchRepaymentScheduleInstallments(), byDate);
            LocalDate runOnDate = newTransaction.getAmount(loan.getCurrency()).isGreaterThanZero() ? newTransaction.getTransactionDate()
                    : previousTransaction.getTransactionDate();
            LocalDate reverseLoanChargesBefore = previousTransaction.getTransactionDate().isBefore(newTransaction.getTransactionDate()) ? previousTransaction
                    .getTransactionDate() : newTransaction.getTransactionDate();
            isChargeChanged = updatePenaltiesOnPaymentTransaction(loan, runOnDate, reverseLoanChargesBefore, recurringCharges);
        }
        return isChargeChanged;
    }

    @Override
    @Transactional
    public void applyOverdueChargesForNonInterestRecalculationLoans(final Long loanId, final LocalDate runOndate,
            final LocalDate brokenPeriodOnDate) {
        List<LoanRecurringCharge> recurringCharges = this.loanReadPlatformService.retrieveLoanOverdueRecurringCharge(loanId);
        boolean isLastRunBeforeBrokenPeriodDate = false;
        if (brokenPeriodOnDate != null) {
            for (LoanRecurringCharge loanRecurringCharge : recurringCharges) {
                if (loanRecurringCharge.getChargeOverueDetail() != null
                        && loanRecurringCharge.getChargeOverueDetail().getLastRunOnDate().isAfter(brokenPeriodOnDate)) {
                    isLastRunBeforeBrokenPeriodDate = true;
                    break;
                }
            }
        }
        if (brokenPeriodOnDate == null || isLastRunBeforeBrokenPeriodDate) {
            applyOverdueChargesForNonInterestRecalculationLoans(loanId, runOndate, brokenPeriodOnDate, recurringCharges);
        } else {
            Loan loan = this.loanAssembler.assembleFrom(loanId);
            final boolean isChargeChanged = updateAndApplyOverdueChargesForLoan(loan, runOndate, brokenPeriodOnDate);
            if (isChargeChanged) {
                addInstallmentIfPenaltyAppliedAfterLastDueDate(loan, brokenPeriodOnDate);
                final LoanRepaymentScheduleProcessingWrapper wrapper = new LoanRepaymentScheduleProcessingWrapper();
                wrapper.reprocess(loan.getCurrency(), loan.getDisbursementDate(), loan.fetchRepaymentScheduleInstallments(),
                        loan.charges(), loan.getDisbursementDetails());
                loan.updateLoanSummaryAndStatus();
                this.loanRepository.save(loan);
            }
        }
    }

    @Override
    public LoanOverdueChargeData calculateOverdueChargesAsOnDate(final Loan loan, LocalDate runOnDate, LocalDate reverseLoanChargesBefore) {

        BigDecimal postedPenaltyChargeAmount = BigDecimal.ZERO;
        BigDecimal notPostedPenaltyChargeAmount = BigDecimal.ZERO;
        LoanOverdueChargeData loanOverdueChargeData = new LoanOverdueChargeData(postedPenaltyChargeAmount, notPostedPenaltyChargeAmount,
                runOnDate);
        if (!loan.getLoanRecurringCharges().isEmpty()) {
            List<LoanRecurringCharge> recurringCharges = new ArrayList<>(loan.getLoanRecurringCharges().size());
            LocalDate lastRunOnDate = null;
            LocalDate lastChargeAppliedOnDate = null;
            boolean canApplyBrokenPeriodChargeAsOnCurrentDate = false;
            for (LoanRecurringCharge charge : loan.getLoanRecurringCharges()) {
                recurringCharges.add(LoanRecurringCharge.copyFrom(charge));
                if (lastRunOnDate == null || lastRunOnDate.isAfter(charge.getChargeOverueDetail().getLastRunOnDate())) {
                    lastRunOnDate = charge.getChargeOverueDetail().getLastRunOnDate();
                }
                if (lastChargeAppliedOnDate == null || lastChargeAppliedOnDate.isAfter(charge.getChargeOverueDetail().getLastRunOnDate())) {
                    lastChargeAppliedOnDate = charge.getChargeOverueDetail().getLastRunOnDate();
                }

                if (charge.getChargeOverueDetail().isApplyChargeForBrokenPeriod()
                        && (lastChargeAppliedOnDate == null || DateUtils.getLocalDateOfTenant().isEqual(
                                charge.getChargeOverueDetail().getLastRunOnDate()))) {
                    canApplyBrokenPeriodChargeAsOnCurrentDate = true;
                }

            }
            loanOverdueChargeData.setCanApplyBrokenPeriodChargeAsOnCurrentDate(canApplyBrokenPeriodChargeAsOnCurrentDate);
            loanOverdueChargeData.setLastChargeAppliedOnDate(lastChargeAppliedOnDate);
            loanOverdueChargeData.setLastRunOnDate(lastRunOnDate);

            List<LoanRepaymentScheduleInstallment> installments = loan.getRepaymentScheduleInstallments();
            Collection<LoanCharge> charges = loan.chargesCopy();

            boolean isOverduePresentBeforeTranasctionDate = reprocessPenaltyChargesOnDate(runOnDate, reverseLoanChargesBefore,
                    loanOverdueChargeData, recurringCharges, installments, charges);
            if (isOverduePresentBeforeTranasctionDate) {
                Map<Long, Collection<LoanChargeData>> overdueChargeData = new HashMap<>(1);
                calculateOverdueCharges(loan, runOnDate, recurringCharges, overdueChargeData, runOnDate);
                for (Map.Entry<Long, Collection<LoanChargeData>> mapEntry : overdueChargeData.entrySet()) {
                    for (LoanChargeData loanChargeData : mapEntry.getValue()) {
                        loanOverdueChargeData.setPenaltyToBePostedAsOnDate(MathUtility.add(
                                loanOverdueChargeData.getPenaltyToBePostedAsOnDate(), loanChargeData.getAmount()));
                    }
                }
            }

        }
        return loanOverdueChargeData;
    }
    
    private boolean applyOverdueChargesForLoan(final Loan loan, final LocalDate runOndate, final LocalDate brokenPeriodOnDate) {
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
    
    private boolean updatePenaltiesOnPaymentTransaction(final Loan loan, LocalDate runOnDate, LocalDate reverseLoanChargesBefore,
            List<LoanRecurringCharge> recurringCharges) {
        boolean isChargeChanged = false;

        if (!recurringCharges.isEmpty()) {
            List<LoanRepaymentScheduleInstallment> installments = loan.getRepaymentScheduleInstallments();
            Collection<LoanCharge> charges = loan.getLoanCharges();

            boolean isOverduePresentBeforeTranasctionDate = false;
            LocalDate firstOverdueChargeDate = runOnDate;
            List<LocalDate> overdueScheduleDates = new ArrayList<>();
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
            for (LoanRepaymentScheduleInstallment installment : installments) {
                if (installment.isNotFullyPaidOff()) {
                    if (!installment.getDueDate().isAfter(runOnDate)) {
                        isOverduePresentBeforeTranasctionDate = true;
                        isOverduePresent = true;
                        for (final Long chargeId : chargeApplicableFromDates.keySet()) {
                            chargeApplicableFromDates.get(chargeId).getChargeOverueDetail()
                                    .setLastAppliedOnDate(installment.getDueDate().toDate());
                        }
                        overdueScheduleDates.add(installment.getDueDate());
                        if (firstOverdueChargeDate.isAfter(installment.getDueDate())) {
                            firstOverdueChargeDate = installment.getDueDate();
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
                for (LoanCharge loanCharge : charges) {
                    if (loanCharge.isActive() && loanCharge.isOverdueInstallmentCharge()) {
                        if (loanCharge.getDueLocalDate().isAfter(reverseLoanChargesBefore)) {
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
                for (final Long chargeId : chargeApplicableFromDates.keySet()) {
                    LoanRecurringCharge recurringCharge = chargeApplicableFromDates.get(chargeId);

                    if (!recurringCharge.getFeeFrequency().isInvalid()) {
                        List<LocalDate> recurrenceDates = ChargeUtils.retriveRecurrencePeriods(firstOverdueChargeDate, recurringCharge
                                .getChargeOverueDetail().getLastAppliedOnDate(), overdueScheduleDates, recurringCharge
                                .getChargeOverueDetail().getGracePeriod(), recurringCharge.getFeeFrequency(), recurringCharge
                                .getFeeInterval(), firstOverdueChargeDate, recurringCharge.getChargeCalculation().isFlat());
                        if (!recurrenceDates.isEmpty()) {
                            Collections.sort(recurrenceDates, Collections.reverseOrder());
                            LocalDate endDate = recurrenceDates.get(0);
                            recurringCharge.getChargeOverueDetail().setLastAppliedOnDate(endDate.toDate());
                        }

                    }
                    chargeApplicableFromDates.get(chargeId).getChargeOverueDetail().setLastRunOnDate(runOnDate.toDate());
                }
                isChargeChanged = isChargeChanged || applyOverdueChargesForLoan(loan, runOnDate, runOnDate);
            } else {
                for (final Long chargeId : chargeApplicableFromDates.keySet()) {
                    chargeApplicableFromDates.get(chargeId).getChargeOverueDetail().setLastAppliedOnDate(null);
                    chargeApplicableFromDates.get(chargeId).getChargeOverueDetail().setLastRunOnDate(runOnDate.toDate());
                }
            }
        }
        return isChargeChanged;
    }

    private void addInstallmentIfPenaltyAppliedAfterLastDueDate(Loan loan, LocalDate lastChargeDate) {
        if (lastChargeDate != null) {
            List<LoanRepaymentScheduleInstallment> installments = loan.fetchRepaymentScheduleInstallments();
            LoanRepaymentScheduleInstallment lastInstallment = loan.fetchRepaymentScheduleInstallment(installments.size());
            if (lastChargeDate.isAfter(lastInstallment.getDueDate())) {
                if (lastInstallment.isRecalculatedInterestComponent()) {
                    installments.remove(lastInstallment);
                    lastInstallment = loan.fetchRepaymentScheduleInstallment(installments.size());
                }
                boolean recalculatedInterestComponent = true;
                BigDecimal principal = BigDecimal.ZERO;
                BigDecimal interest = BigDecimal.ZERO;
                BigDecimal feeCharges = BigDecimal.ZERO;
                BigDecimal penaltyCharges = BigDecimal.ONE;
                final List<LoanInterestRecalcualtionAdditionalDetails> compoundingDetails = null;
                LoanRepaymentScheduleInstallment newEntry = new LoanRepaymentScheduleInstallment(loan, installments.size() + 1,
                        lastInstallment.getDueDate(), lastChargeDate, principal, interest, feeCharges, penaltyCharges,
                        recalculatedInterestComponent, compoundingDetails);
                installments.add(newEntry);
            }
        }
    }

    private void applyOverdueChargesForNonInterestRecalculationLoans(final Long loanId, final LocalDate runOndate,
            final LocalDate brokenPeriodOnDate, List<LoanRecurringCharge> recurringCharges) {
        Map<Long, Collection<LoanChargeData>> overdueChargeData = new HashMap<>(1);
        LoanRepaymentScheduleInstallment lastInstallment = calculateOverdueCharges(loanId, runOndate, recurringCharges, overdueChargeData,
                brokenPeriodOnDate);
        if (!overdueChargeData.isEmpty()) {
            List<String> sqlStatements = new ArrayList<>();
            Map<LocalDate, BigDecimal> chargeAmountMap = new HashMap<>();
            BigDecimal totalChargeAmount = BigDecimal.ZERO;
            for (Map.Entry<Long, Collection<LoanChargeData>> mapEntry : overdueChargeData.entrySet()) {
                for (LoanChargeData chargeData : mapEntry.getValue()) {
                    sqlStatements.add(constructLoanChargeInsert(chargeData));
                    totalChargeAmount = totalChargeAmount.add(chargeData.getAmount());
                    if (chargeAmountMap.containsKey(chargeData.getDueDate())) {
                        chargeAmountMap.get(chargeData.getDueDate()).add(chargeData.getAmount());
                    } else {
                        chargeAmountMap.put(chargeData.getDueDate(), chargeData.getAmount());
                    }
                }
            }
            if (totalChargeAmount.compareTo(BigDecimal.ZERO) == 1) {
                sqlStatements.add(constructLoanUpdateStatement(loanId, totalChargeAmount));
                for (Map.Entry<LocalDate, BigDecimal> mapEntry : chargeAmountMap.entrySet()) {
                    if (mapEntry.getKey().isAfter(lastInstallment.getDueDate())) {
                        if (lastInstallment.isRecalculatedInterestComponent()) {
                            sqlStatements.add(constructLoanScheduleUpdateStatementAfterMaturity(lastInstallment.getId(), mapEntry.getKey(),
                                    mapEntry.getValue()));
                        } else {
                            sqlStatements.add(constructLoanScheduleInsert(loanId, mapEntry.getKey(), lastInstallment.getDueDate(),
                                    lastInstallment.getInstallmentNumber(), mapEntry.getValue()));
                        }
                    } else {
                        sqlStatements.add(constructLoanScheduleUpdateStatement(loanId, mapEntry.getKey(), mapEntry.getValue()));
                    }
                }
            }

            if (!sqlStatements.isEmpty()) {
                for (LoanRecurringCharge recurringCharge : recurringCharges) {
                    sqlStatements.add(constructLoanRecurrChargeUpdateStatement(loanId, recurringCharge.getChargeId(), recurringCharge
                            .getChargeOverueDetail().getLastAppliedOnDate(), recurringCharge.getChargeOverueDetail().getLastRunOnDate()));
                }

                this.jdbcTemplate.batchUpdate(sqlStatements.toArray(new String[sqlStatements.size()]));
            }
        }
    }

    private boolean reprocessPenaltyChargesOnDate(LocalDate runOnDate, LocalDate reverseLoanChargesBefore,
            LoanOverdueChargeData loanOverdueChargeData, List<LoanRecurringCharge> recurringCharges,
            List<LoanRepaymentScheduleInstallment> installments, Collection<LoanCharge> charges) {
        boolean isOverduePresentBeforeTranasctionDate = false;
        LocalDate firstOverdueChargeDate = runOnDate;
        List<LocalDate> overdueScheduleDates = new ArrayList<>();
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
        if (!isOverdueChargePresent) { return isOverduePresentBeforeTranasctionDate; }
        LocalDate currentDate = DateUtils.getLocalDateOfTenant();
        for (LoanRepaymentScheduleInstallment installment : installments) {
            if (installment.isNotFullyPaidOff()) {
                if (!installment.getDueDate().isAfter(runOnDate)) {
                    isOverduePresentBeforeTranasctionDate = true;
                    isOverduePresent = true;
                    for (final Long chargeId : chargeApplicableFromDates.keySet()) {
                        chargeApplicableFromDates.get(chargeId).getChargeOverueDetail()
                                .setLastAppliedOnDate(installment.getDueDate().toDate());
                    }
                    overdueScheduleDates.add(installment.getDueDate());
                    if (firstOverdueChargeDate.isAfter(installment.getDueDate())) {
                        firstOverdueChargeDate = installment.getDueDate();
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
            for (LoanCharge loanCharge : charges) {
                if (loanCharge.isActive() && loanCharge.isOverdueInstallmentCharge()) {
                    if (!loanCharge.getDueLocalDate().isAfter(reverseLoanChargesBefore)) {
                        loanOverdueChargeData.setPenaltyPostedAsOnDate(MathUtility.add(loanOverdueChargeData.getPenaltyPostedAsOnDate(),
                                loanCharge.amountOutstanding()));
                        if (loanCharge.getDueLocalDate().isAfter(
                                chargeApplicableFromDates.get(loanCharge.getCharge().getId()).getChargeOverueDetail()
                                        .getLastAppliedOnDate())) {
                            chargeApplicableFromDates.get(loanCharge.getCharge().getId()).getChargeOverueDetail()
                                    .setLastAppliedOnDate(loanCharge.getDueLocalDate().toDate());
                        }
                    }
                }
            }

        }

        if (isOverduePresentBeforeTranasctionDate) {
            for (final Long chargeId : chargeApplicableFromDates.keySet()) {
                LoanRecurringCharge recurringCharge = chargeApplicableFromDates.get(chargeId);

                if (!recurringCharge.getFeeFrequency().isInvalid()) {
                    List<LocalDate> recurrenceDates = ChargeUtils.retriveRecurrencePeriods(firstOverdueChargeDate, recurringCharge
                            .getChargeOverueDetail().getLastAppliedOnDate(), overdueScheduleDates, recurringCharge.getChargeOverueDetail()
                            .getGracePeriod(), recurringCharge.getFeeFrequency(), recurringCharge.getFeeInterval(), firstOverdueChargeDate,
                            recurringCharge.getChargeCalculation().isFlat());
                    if (!recurrenceDates.isEmpty()) {
                        Collections.sort(recurrenceDates, Collections.reverseOrder());
                        LocalDate endDate = recurrenceDates.get(0);
                        recurringCharge.getChargeOverueDetail().setLastAppliedOnDate(endDate.toDate());
                    }

                }
            }

        }
        return isOverduePresentBeforeTranasctionDate;
    }

    private String constructLoanUpdateStatement(final Long loanId, final BigDecimal chargeAmount) {
        StringBuilder sb = new StringBuilder(LOAN_UPDATE_STATEMENT);
        sb.append(" WHERE ml.id = ");
        sb.append(loanId);
        String sqlStatement = sb.toString().replaceAll(":amount", chargeAmount.toString());
        return sqlStatement;
    }

    private String constructLoanScheduleUpdateStatement(final Long loanId, final LocalDate date, final BigDecimal chargeAmount) {
        String chargeAppliedOnDate = this.formatter.print(date);
        StringBuilder sb = new StringBuilder(LOAN_SCHEDULE_UPDATE_STATEMENT);
        sb.append(chargeAmount);
        sb.append(" WHERE lrs.loan_id = ");
        sb.append(loanId);
        sb.append(" and lrs.fromdate < '");
        sb.append(chargeAppliedOnDate);
        sb.append("' and lrs.duedate >= '");
        sb.append(chargeAppliedOnDate);
        sb.append("'");
        return sb.toString();
    }

    private String constructLoanChargeInsert(LoanChargeData chargeData) {
        StringBuilder sb = new StringBuilder(LOAN_CHARGE_INSERT_STATEMENT);
        sb.append(chargeData.getLoanId());
        sb.append(",");
        sb.append(chargeData.getChargeId());
        sb.append(",");
        sb.append(1);
        sb.append(",");
        sb.append(chargeData.getChargeTimeType().getValue());
        sb.append(",'");
        sb.append(this.formatter.print(chargeData.getDueDate()));
        sb.append("',");
        sb.append(chargeData.getChargeCalculationType().getValue());
        sb.append(",");
        sb.append(chargeData.getPercentage());
        sb.append(",");
        if (chargeData.getPercentage() == null) {
            sb.append(chargeData.getAmount());
        } else {
            sb.append(chargeData.getPercentage());
        }
        sb.append(",");
        sb.append(chargeData.getAmount());
        sb.append(",");
        sb.append(0.0);
        sb.append(",");
        sb.append(chargeData.getAmount());
        sb.append(")");
        return sb.toString();
    }

    private String constructLoanScheduleInsert(final Long loanId, final LocalDate toDate, LocalDate fromDate,
            final Integer installmentNumber, final BigDecimal chargeAmount) {
        StringBuilder sb = new StringBuilder(LOAN_SCHEDULE_INSERT_STATEMENT);
        sb.append(loanId);
        sb.append(",'");
        sb.append(this.formatter.print(fromDate));
        sb.append("','");
        sb.append(this.formatter.print(toDate));
        sb.append("',");
        sb.append(installmentNumber + 1);
        sb.append(",");
        sb.append(chargeAmount);
        sb.append(", b'0', 1)");
        return sb.toString();
    }

    private String constructLoanScheduleUpdateStatementAfterMaturity(final Long installmentId, final LocalDate date,
            final BigDecimal chargeAmount) {
        String chargeAppliedOnDate = this.formatter.print(date);
        StringBuilder sb = new StringBuilder(LOAN_SCHEDULE_UPDATE_STATEMENT_AFTER_MATURITY);
        sb.append(chargeAppliedOnDate);
        sb.append("',rs.penalty_charges_amount =IFNUll( rs.penalty_charges_amount,0) +");
        sb.append(chargeAmount);
        sb.append(" WHERE  rs.id = ");
        sb.append(installmentId);
        return sb.toString();
    }

    private String constructLoanRecurrChargeUpdateStatement(final Long loanId, final Long chargeId, final LocalDate lastApplyOnDate,
            final LocalDate lastRunOnDate) {
        StringBuilder sb = new StringBuilder(LOAN_OVERDUE_CHARGE_UPDATE);
        sb.append(this.formatter.print(lastApplyOnDate));
        sb.append("',ocd.last_run_on_date='");
        sb.append(this.formatter.print(lastRunOnDate));
        sb.append("' WHERE  rc.loan_id = ");
        sb.append(loanId);
        sb.append(" and rc.charge_id = ");
        sb.append(chargeId);
        return sb.toString();
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
                    recurringCharge.getFeeInterval(), applyChargeFromDate, recurringCharge.getChargeCalculation().isFlat());

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
        Money totalChargeApplied = chargeAmount.zero();
        for (Map.Entry<LocalDate, PenaltyPeriod> penaltyPeriod : penaltyPeriods.entrySet()) {
            chargeAmount = chargeAmount.plus(chargeCalculator.calculateCharge(penaltyPeriod.getValue()));
            if (includeBrokenPeriodDate && penaltyPeriod.getKey().isEqual(overdueCalculationDetail.getBrokenPeriodOnDate())) {
                LoanChargeData chargeData = LoanChargeData.newLoanOverdueCharge(recurringCharge.getChargeId(),
                        overdueCalculationDetail.getLoanId(), overdueCalculationDetail.getRunOnDate(), chargeAmount.getAmount(),
                        recurringCharge.getAmount(), ChargeEnumerations.chargeTimeType(recurringCharge.getChargeTimeType()),
                        ChargeEnumerations.chargeCalculationType(recurringCharge.getChargeCalculation()),
                        ChargeEnumerations.chargePaymentMode(recurringCharge.getChargePaymentMode()), recurringCharge.getTaxGroupId());
                addChargeDataToCollection(overdueChargeData, chargeData);
                totalChargeApplied = totalChargeApplied.plus(chargeAmount);
                chargeAmount = chargeAmount.zero();
            }
        }
        totalChargeApplied = totalChargeApplied.plus(chargeAmount);
        if (totalChargeApplied.isGreaterThanZero() && lastChargeAppliedDate != null) {
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

    private LoanRepaymentScheduleInstallment calculateOverdueCharges(final Long loanId, final LocalDate runOndate,
            List<LoanRecurringCharge> recurringCharges, Map<Long, Collection<LoanChargeData>> overdueChargeData,
            final LocalDate brokenPeriodOnDate) {

        List<LoanTransaction> paymentTransactions = this.loanReadPlatformService.retrieveLoanTransactions(loanId,
                LoanTransactionType.REPAYMENT.getValue(), LoanTransactionType.WAIVE_CHARGES.getValue(),
                LoanTransactionType.WAIVE_INTEREST.getValue(), LoanTransactionType.CHARGE_PAYMENT.getValue(),
                LoanTransactionType.REFUND.getValue(), LoanTransactionType.REFUND_FOR_ACTIVE_LOAN.getValue());
        List<LoanRepaymentScheduleInstallment> currentInstallments = this.loanReadPlatformService
                .retrieveLoanRepaymentScheduleInstallments(loanId);
        LoanRepaymentScheduleInstallment lastInstallment = null;
        if (!currentInstallments.isEmpty()) {
            lastInstallment = currentInstallments.get(currentInstallments.size() - 1);
        }
        MonetaryCurrency currency = this.loanReadPlatformService.retrieveLoanCurrency(loanId);
        for (LoanRecurringCharge recurringCharge : recurringCharges) {
            LoanOverdueCalculationDTO overdueCalculationDetail = new LoanOverdueCalculationDTO(loanId, runOndate, currency,
                    paymentTransactions, currentInstallments, brokenPeriodOnDate);
            applyOverdueChargesForLoan(recurringCharge, overdueCalculationDetail, overdueChargeData);
        }
        return lastInstallment;
    }

    private void validateLatestClosureByBranch(final long officeId, final LocalDate runOnDate, final boolean isPeriodicAccrualEnabled) {
        if (isPeriodicAccrualEnabled) {
            GLClosure closure = this.closureRepository.getLatestGLClosureByBranch(officeId);
            if (closure != null && !runOnDate.isAfter(closure.getClosingLocalDate())) { throw new GLClosureForOfficeException(officeId,
                    closure.getClosingDate()); }
        }
    }
}
