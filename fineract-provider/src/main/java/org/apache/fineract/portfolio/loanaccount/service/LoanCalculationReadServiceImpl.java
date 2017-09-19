package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueChargeData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.apache.fineract.portfolio.loanaccount.data.ScheduleGeneratorDTO;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleTransactionProcessorFactory;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanaccount.exception.LoanNotFoundException;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanCalculationReadServiceImpl implements LoanCalculationReadService {

    private final LoanAssembler loanAssembler;
    private final LoanRepaymentScheduleTransactionProcessorFactory loanRepaymentScheduleTransactionProcessorFactory;
    private final LoanUtilService loanUtilService;
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository;
    private final LoanOverdueChargeService loanOverdueChargeService;

    @Autowired
    public LoanCalculationReadServiceImpl(final LoanAssembler loanAssembler,
            final LoanRepaymentScheduleTransactionProcessorFactory loanRepaymentScheduleTransactionProcessorFactory,
            final LoanUtilService loanUtilService, final PaymentTypeReadPlatformService paymentTypeReadPlatformService,
            final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository,
            final LoanOverdueChargeService loanOverdueChargeService) {
        this.loanAssembler = loanAssembler;
        this.loanRepaymentScheduleTransactionProcessorFactory = loanRepaymentScheduleTransactionProcessorFactory;
        this.loanUtilService = loanUtilService;
        this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
        this.applicationCurrencyRepository = applicationCurrencyRepository;
        this.loanOverdueChargeService = loanOverdueChargeService;
    }

    @Override
    public LoanTransactionData retrieveLoanPrePaymentTemplate(final Long loanId, LocalDate onDate, boolean calcualteInterestTillDate) {

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        return retrieveLoanPrePaymentTemplate(onDate, calcualteInterestTillDate, loan);
    }

    @Override
    public LoanTransactionData retrieveLoanPrePaymentTemplate(final LocalDate onDate, boolean calcualteInterestTillDate, final Loan loan) {
        loan.setHelpers(LoanAssembler.defaultLoanLifecycleStateMachine(), null, loanRepaymentScheduleTransactionProcessorFactory);

        final MonetaryCurrency currency = loan.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);

        final CurrencyData currencyData = applicationCurrency.toData();

        final LocalDate earliestUnpaidInstallmentDate = DateUtils.getLocalDateOfTenant();
        final LocalDate recalculateFrom = null;
        final ScheduleGeneratorDTO scheduleGeneratorDTO = loanUtilService.buildScheduleGeneratorDTO(loan, recalculateFrom);

        final LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment = loan.fetchPrepaymentDetail(scheduleGeneratorDTO, onDate,
                calcualteInterestTillDate);
        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.REPAYMENT);
        final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        final BigDecimal outstandingLoanBalance = loanRepaymentScheduleInstallment.getPrincipalOutstanding(currency).getAmount();
        BigDecimal principalOustandingAmount = loanRepaymentScheduleInstallment.getPrincipalOutstanding(currency).getAmount();
        BigDecimal totalOutstandingLoanBalance = loanRepaymentScheduleInstallment.getTotalOutstanding(currency).getAmount();
        Money penaltyCharges = loanRepaymentScheduleInstallment.getPenaltyChargesOutstanding(currency);
        // this is to adjust the penalty charge amount based on pre-close date
        LoanOverdueChargeData overdueChargeData = this.loanOverdueChargeService.calculateOverdueChargesAsOnDate(loan, onDate, onDate);
        penaltyCharges = penaltyCharges.plus(overdueChargeData.getPenaltyToBePostedAsOnDate());
        totalOutstandingLoanBalance = totalOutstandingLoanBalance.add(overdueChargeData.getPenaltyToBePostedAsOnDate());
        final BigDecimal unrecognizedIncomePortion = null;
        if (loan.isSubsidyApplicable() && (loan.getTotalSubsidyAmount().isGreaterThanZero())) {
            principalOustandingAmount = principalOustandingAmount.subtract(loan.getTotalSubsidyAmount().getAmount());
            totalOutstandingLoanBalance = totalOutstandingLoanBalance.subtract(loan.getTotalSubsidyAmount().getAmount());
        }
        return new LoanTransactionData(null, null, null, transactionType, null, currencyData, earliestUnpaidInstallmentDate,
                totalOutstandingLoanBalance, principalOustandingAmount, loanRepaymentScheduleInstallment.getInterestOutstanding(currency)
                        .getAmount(), loanRepaymentScheduleInstallment.getFeeChargesOutstanding(currency).getAmount(),
                penaltyCharges.getAmount(), null, unrecognizedIncomePortion, paymentOptions, null, null, null, outstandingLoanBalance,
                false);
    }

    @Override
    public LoanTransactionData retrieveLoanForeclosureTemplate(final Long loanId, final LocalDate transactionDate) {

        final Loan loan = this.loanAssembler.assembleFrom(loanId);
        if (loan == null) { throw new LoanNotFoundException(loanId); }
        boolean validateForFutureDate = false;
        loan.validateForForeclosure(transactionDate, validateForFutureDate);
        final MonetaryCurrency currency = loan.getCurrency();
        final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);

        final CurrencyData currencyData = applicationCurrency.toData();

        final LocalDate earliestUnpaidInstallmentDate = DateUtils.getLocalDateOfTenant();

        final LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment = loan.fetchLoanForeclosureDetail(transactionDate);
        BigDecimal unrecognizedIncomePortion = null;
        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.REPAYMENT);
        final Collection<PaymentTypeData> paymentTypeOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        final BigDecimal outstandingLoanBalance = loanRepaymentScheduleInstallment.getPrincipalOutstanding(currency).getAmount();
        final Boolean isReversed = false;

        Money outStandingAmount = loanRepaymentScheduleInstallment.getTotalOutstanding(currency);
        Money penaltyCharges = loanRepaymentScheduleInstallment.getPenaltyChargesOutstanding(currency);
        // this is to adjust the penalty charge amount based on pre-close date
        LoanOverdueChargeData overdueChargeData = this.loanOverdueChargeService.calculateOverdueChargesAsOnDate(loan, transactionDate,
                transactionDate);
        penaltyCharges = penaltyCharges.plus(overdueChargeData.getPenaltyToBePostedAsOnDate());
        outStandingAmount = outStandingAmount.plus(overdueChargeData.getPenaltyToBePostedAsOnDate());
        return new LoanTransactionData(null, null, null, transactionType, null, currencyData, earliestUnpaidInstallmentDate,
                outStandingAmount.getAmount(), loanRepaymentScheduleInstallment.getPrincipalOutstanding(currency).getAmount(),
                loanRepaymentScheduleInstallment.getInterestOutstanding(currency).getAmount(), loanRepaymentScheduleInstallment
                        .getFeeChargesOutstanding(currency).getAmount(), penaltyCharges.getAmount(), null, unrecognizedIncomePortion,
                paymentTypeOptions, null, null, null, outstandingLoanBalance, isReversed);
    }

}
