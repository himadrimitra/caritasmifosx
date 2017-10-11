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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.data.DisbursementData;
import org.apache.fineract.portfolio.loanaccount.data.RepaymentScheduleRelatedLoanData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanInterestRecalcualtionAdditionalDetails;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanSchedulePeriodData;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class LoanScheduleHistoryReadPlatformServiceImpl implements LoanScheduleHistoryReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public LoanScheduleHistoryReadPlatformServiceImpl(final RoutingDataSource dataSource, final PlatformSecurityContext context) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public LoanScheduleData retrieveRepaymentArchiveSchedule(final Long loanId,
            final RepaymentScheduleRelatedLoanData repaymentScheduleRelatedLoanData, final Collection<DisbursementData> disbursementData) {

        try {
            this.context.authenticatedUser();
            final LoanScheduleArchiveResultSetExtractor fullResultsetExtractor = new LoanScheduleArchiveResultSetExtractor(
                    repaymentScheduleRelatedLoanData, disbursementData);
            final String sql = "select " + fullResultsetExtractor.schema() + " where ml.id = ? order by ls.loan_id, ls.installment";

            return this.jdbcTemplate.query(sql, fullResultsetExtractor, new Object[] { loanId });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    private static final class LoanScheduleArchiveResultSetExtractor implements ResultSetExtractor<LoanScheduleData> {

        private final CurrencyData currency;
        private final DisbursementData disbursement;
        private final BigDecimal totalFeeChargesDueAtDisbursement;
        private final Collection<DisbursementData> disbursementData;
        private LocalDate lastDueDate;
        private BigDecimal outstandingLoanPrincipalBalance;
        private final BigDecimal interestPosted;
        private final boolean considerFutureDisbursmentsInSchedule;
        private final boolean considerAllDisbursementsInSchedule;

        public LoanScheduleArchiveResultSetExtractor(final RepaymentScheduleRelatedLoanData repaymentScheduleRelatedLoanData,
                final Collection<DisbursementData> disbursementData) {
            this.currency = repaymentScheduleRelatedLoanData.getCurrency();
            this.disbursement = repaymentScheduleRelatedLoanData.disbursementData();
            this.totalFeeChargesDueAtDisbursement = repaymentScheduleRelatedLoanData.getTotalFeeChargesAtDisbursement();
            this.lastDueDate = this.disbursement.disbursementDate();
            this.outstandingLoanPrincipalBalance = this.disbursement.amount();
            this.disbursementData = disbursementData;
            this.interestPosted = repaymentScheduleRelatedLoanData.getInterestPostedAmount();
            this.considerFutureDisbursmentsInSchedule = repaymentScheduleRelatedLoanData.isConsiderFutureDisbursmentsInSchedule();
            this.considerAllDisbursementsInSchedule = repaymentScheduleRelatedLoanData.isConsiderAllDisbursementsInSchedule();
        }

        public String schema() {
            final StringBuilder stringBuilder = new StringBuilder(200);
            stringBuilder.append(" ls.installment as period, ls.fromdate as fromDate, ls.duedate as dueDate, ");
            stringBuilder.append(
                    "ls.principal_amount as principalDue, ls.interest_amount as interestDue, ls.fee_charges_amount as feeChargesDue, ls.penalty_charges_amount as penaltyChargesDue, ");
            stringBuilder.append(
                    " ls.recalculated_interest_component as recalculatedInterestComponent from m_loan ml inner join m_loan_repayment_schedule_history ls on ls.loan_id=ml.id and ls.version=ml.repayment_history_version ");
            return stringBuilder.toString();
        }

        @Override
        public LoanScheduleData extractData(final ResultSet rs) throws SQLException, DataAccessException {

            final LoanSchedulePeriodData disbursementPeriod = LoanSchedulePeriodData.disbursementOnlyPeriod(
                    this.disbursement.disbursementDate(), this.disbursement.amount(), this.totalFeeChargesDueAtDisbursement,
                    this.disbursement.isDisbursed());

            final Collection<LoanSchedulePeriodData> periods = new ArrayList<>();
            final MonetaryCurrency monCurrency = new MonetaryCurrency(this.currency.code(), this.currency.decimalPlaces(),
                    this.currency.currencyInMultiplesOf());
            BigDecimal totalPrincipalDisbursed = BigDecimal.ZERO;
            if (this.disbursementData == null || this.disbursementData.isEmpty()) {
                periods.add(disbursementPeriod);
                totalPrincipalDisbursed = Money.of(monCurrency, this.disbursement.amount()).getAmount();
            } else {
                this.outstandingLoanPrincipalBalance = BigDecimal.ZERO;
            }

            Money totalPrincipalExpected = Money.zero(monCurrency);
            Money totalInterestCharged = Money.zero(monCurrency);
            Money totalFeeChargesCharged = Money.zero(monCurrency);
            Money totalPenaltyChargesCharged = Money.zero(monCurrency);
            Money totalRepaymentExpected = Money.zero(monCurrency);

            // update totals with details of fees charged during disbursement
            totalFeeChargesCharged = totalFeeChargesCharged.plus(disbursementPeriod.feeChargesDue());
            totalRepaymentExpected = totalRepaymentExpected.plus(disbursementPeriod.feeChargesDue());

            Integer loanTermInDays = Integer.valueOf(0);
            while (rs.next()) {
                final Integer period = JdbcSupport.getInteger(rs, "period");
                LocalDate fromDate = JdbcSupport.getLocalDate(rs, "fromDate");
                final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");
                if (this.disbursementData != null) {
                    BigDecimal principal = BigDecimal.ZERO;
                    for (final DisbursementData data : this.disbursementData) {
                        if (periods.size() == 0) {
                            if (fromDate.equals(this.disbursement.disbursementDate()) && data.disbursementDate().equals(fromDate)) {
                                principal = principal.add(data.amount()).add(this.interestPosted);
                                final LoanSchedulePeriodData periodData = LoanSchedulePeriodData.disbursementOnlyPeriod(
                                        data.disbursementDate(), principal, this.totalFeeChargesDueAtDisbursement, data.isDisbursed());
                                periods.add(periodData);
                                this.outstandingLoanPrincipalBalance = this.outstandingLoanPrincipalBalance.add(principal);
                            }
                        } else if (data.isDueForDisbursement(fromDate, dueDate) && (data.isDisbursed()
                                || this.considerAllDisbursementsInSchedule || (this.considerFutureDisbursmentsInSchedule
                                        && !data.disbursementDate().isBefore(DateUtils.getLocalDateOfTenant())))) {
                            principal = principal.add(data.amount());
                            final LoanSchedulePeriodData periodData = LoanSchedulePeriodData.disbursementOnlyPeriod(data.disbursementDate(),
                                    data.amount(), BigDecimal.ZERO, data.isDisbursed());
                            periods.add(periodData);
                            this.outstandingLoanPrincipalBalance = this.outstandingLoanPrincipalBalance.add(data.amount());
                        }
                    }
                    totalPrincipalDisbursed = totalPrincipalDisbursed.add(principal);
                }

                Integer daysInPeriod = Integer.valueOf(0);
                if (fromDate != null) {
                    daysInPeriod = Days.daysBetween(fromDate, dueDate).getDays();
                    loanTermInDays = Integer.valueOf(loanTermInDays.intValue() + daysInPeriod.intValue());
                }

                final BigDecimal principalDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalDue");
                totalPrincipalExpected = totalPrincipalExpected.plus(principalDue);

                final BigDecimal interestExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestDue");
                totalInterestCharged = totalInterestCharged.plus(interestExpectedDue);

                final BigDecimal totalInstallmentAmount = totalPrincipalExpected.zero().plus(principalDue).plus(interestExpectedDue)
                        .getAmount();

                final BigDecimal feeChargesExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesDue");
                totalFeeChargesCharged = totalFeeChargesCharged.plus(feeChargesExpectedDue);

                final BigDecimal penaltyChargesExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesDue");
                totalPenaltyChargesCharged = totalPenaltyChargesCharged.plus(penaltyChargesExpectedDue);

                final BigDecimal totalExpectedCostOfLoanForPeriod = interestExpectedDue.add(feeChargesExpectedDue)
                        .add(penaltyChargesExpectedDue);

                final BigDecimal totalDueForPeriod = principalDue.add(totalExpectedCostOfLoanForPeriod);

                totalRepaymentExpected = totalRepaymentExpected.plus(totalDueForPeriod);

                if (fromDate == null) {
                    fromDate = this.lastDueDate;
                }
                final BigDecimal outstandingPrincipalBalanceOfLoan = this.outstandingLoanPrincipalBalance.subtract(principalDue);

                // update based on current period values
                this.lastDueDate = dueDate;
                this.outstandingLoanPrincipalBalance = this.outstandingLoanPrincipalBalance.subtract(principalDue);

                final Boolean recalculatedInterestComponent = rs.getBoolean("recalculatedInterestComponent");

                final LoanSchedulePeriodData periodData = LoanSchedulePeriodData.repaymentOnlyPeriod(period, fromDate, dueDate,
                        principalDue, outstandingPrincipalBalanceOfLoan, interestExpectedDue, feeChargesExpectedDue,
                        penaltyChargesExpectedDue, totalDueForPeriod, totalInstallmentAmount, recalculatedInterestComponent);

                periods.add(periodData);
            }

            return new LoanScheduleData(this.currency, periods, loanTermInDays, totalPrincipalDisbursed, totalPrincipalExpected.getAmount(),
                    totalInterestCharged.getAmount(), totalFeeChargesCharged.getAmount(), totalPenaltyChargesCharged.getAmount(),
                    totalRepaymentExpected.getAmount());
        }

    }

    @Override
    public List<LoanRepaymentScheduleInstallment> retrieveRepaymentArchiveAsInstallments(final Long loanId) {
        final LoanScheduleArchiveAsInstallmentsMapper mapper = new LoanScheduleArchiveAsInstallmentsMapper();
        final String sql = "select " + mapper.schema() + " where ml.id = ? order by ls.loan_id, ls.installment";

        return this.jdbcTemplate.query(sql, mapper, new Object[] { loanId });
    }

    private static final class LoanScheduleArchiveAsInstallmentsMapper implements RowMapper<LoanRepaymentScheduleInstallment> {

        public String schema() {
            final StringBuilder stringBuilder = new StringBuilder(200);
            stringBuilder.append(" ls.installment as period, ls.fromdate as fromDate, ls.duedate as dueDate, ");
            stringBuilder.append(
                    "ls.principal_amount as principalDue, ls.interest_amount as interestDue, ls.fee_charges_amount as feeChargesDue, ls.penalty_charges_amount as penaltyChargesDue, ");
            stringBuilder.append(
                    " ls.recalculated_interest_component as recalculatedInterestComponent from m_loan ml inner join m_loan_repayment_schedule_history ls on ls.loan_id=ml.id and ls.version=ml.repayment_history_version ");
            return stringBuilder.toString();
        }

        @Override
        public LoanRepaymentScheduleInstallment mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {

            final Integer installmentNumber = JdbcSupport.getInteger(rs, "period");
            final LocalDate fromDate = JdbcSupport.getLocalDate(rs, "fromDate");
            final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");
            final BigDecimal principal = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalDue");
            final BigDecimal interest = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestDue");
            final BigDecimal feeCharges = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesDue");
            final BigDecimal penaltyCharges = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesDue");
            final Boolean recalculatedInterestComponent = rs.getBoolean("recalculatedInterestComponent");
            final Loan loan = null;
            final List<LoanInterestRecalcualtionAdditionalDetails> compoundingDetails = null;
            return new LoanRepaymentScheduleInstallment(loan, installmentNumber, fromDate, dueDate, principal, interest, feeCharges,
                    penaltyCharges, recalculatedInterestComponent, compoundingDetails);
        }

    }
}
