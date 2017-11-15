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
package org.apache.fineract.portfolio.savings.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.exception.SavingsAccountChargeNotFoundException;
import org.apache.fineract.portfolio.charge.service.ChargeDropdownReadPlatformService;
import org.apache.fineract.portfolio.charge.service.ChargeEnumerations;
import org.apache.fineract.portfolio.common.service.DropdownReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingIdListData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountAnnualFeeData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountChargeData;
import org.apache.fineract.portfolio.savings.data.SavingsChargesSummaryData;
import org.apache.fineract.portfolio.savings.data.SavingsIdOfChargeData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountStatusType;
import org.apache.fineract.portfolio.tax.data.TaxGroupData;
import org.joda.time.LocalDate;
import org.joda.time.MonthDay;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SavingsAccountChargeReadPlatformServiceImpl implements SavingsAccountChargeReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final ChargeDropdownReadPlatformService chargeDropdownReadPlatformService;
    private final DropdownReadPlatformService dropdownReadPlatformService;
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    // mappers
    private final SavingsAccountChargeDueMapper chargeDueMapper;

    @Autowired
    public SavingsAccountChargeReadPlatformServiceImpl(final PlatformSecurityContext context,
            final ChargeDropdownReadPlatformService chargeDropdownReadPlatformService, final RoutingDataSource dataSource,
            final DropdownReadPlatformService dropdownReadPlatformService) {
        this.context = context;
        this.chargeDropdownReadPlatformService = chargeDropdownReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.chargeDueMapper = new SavingsAccountChargeDueMapper();
        this.dropdownReadPlatformService = dropdownReadPlatformService;
    }

    private static final class SavingIdListDataMapper implements RowMapper<SavingIdListData> {

        @Override
        public SavingIdListData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long savingId = rs.getLong("savingId");
            final LocalDate timePeriode = JdbcSupport.getLocalDate(rs, "timePeriode");
            final LocalDate startFeeChargeDate = JdbcSupport.getLocalDate(rs, "startFeeChargeDate");
            return SavingIdListData.instance(savingId, timePeriode, startFeeChargeDate);
        }
    }

    private static final class SavingIdListDataMapperForTxnDate implements RowMapper<SavingIdListData> {

        @Override
        public SavingIdListData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final LocalDate txnDate = JdbcSupport.getLocalDate(rs, "transactionDate");
            return SavingIdListData.instaceForTransactionDate(txnDate);
        }
    }

    private static final class SavingIdListForDepositeLateChargeDataMapper implements RowMapper<SavingIdListData> {

        @Override
        public SavingIdListData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long savingId = rs.getLong("savingId");
            final LocalDate activationOnDate = JdbcSupport.getLocalDate(rs, "activationOnDate");
            final LocalDate startFeeChargeDate = JdbcSupport.getLocalDate(rs, "startFeeChargeDate");
            return SavingIdListData.insatanceForAllSavingId(savingId, activationOnDate, startFeeChargeDate);
        }
    }

    private static final class SavingsIdOfChargeDataMapper implements RowMapper<SavingsIdOfChargeData> {

        @Override
        public SavingsIdOfChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long savingId = rs.getLong("savingId");
            return SavingsIdOfChargeData.instance(savingId);
        }
    }

    private static final class SavingsIdOfChargeDataWithDueDataMapper implements RowMapper<SavingsIdOfChargeData> {

        @Override
        public SavingsIdOfChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");
            return SavingsIdOfChargeData.instanceForDueDate(dueDate);
        }
    }

    private static final class SavingsAccountChargeMapper implements RowMapper<SavingsAccountChargeData> {

        public String schema() {
            return "sc.id as id, c.id as chargeId, sc.savings_account_id as accountId, c.name as name, " + "sc.amount as amountDue, "
                    + "sc.amount_paid_derived as amountPaid, " + "sc.amount_waived_derived as amountWaived, "
                    + "sc.amount_writtenoff_derived as amountWrittenOff, " + "sc.amount_outstanding_derived as amountOutstanding, "
                    + "sc.calculation_percentage as percentageOf, sc.calculation_on_amount as amountPercentageAppliedTo, "
                    + "sc.charge_time_enum as chargeTime, " + "sc.is_penalty as penalty, " + "sc.charge_due_date as dueAsOfDate, "
                    + "sc.fee_on_month as feeOnMonth, " + "sc.fee_on_day as feeOnDay, sc.fee_interval as feeInterval, "
                    + "sc.charge_calculation_enum as chargeCalculation, "
                    + "sc.is_active as isActive, sc.inactivated_on_date as inactivationDate, "
                    + "c.currency_code as currencyCode, oc.name as currencyName, "
                    + "oc.decimal_places as currencyDecimalPlaces, oc.currency_multiplesof as inMultiplesOf, oc.display_symbol as currencyDisplaySymbol, "
                    + "oc.internationalized_name_code as currencyNameCode from m_charge c "
                    + "join m_organisation_currency oc on c.currency_code = oc.code "
                    + "join m_savings_account_charge sc on sc.charge_id = c.id ";
        }

        @Override
        public SavingsAccountChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long chargeId = rs.getLong("chargeId");
            final Long accountId = rs.getLong("accountId");
            final String name = rs.getString("name");
            final BigDecimal amount = rs.getBigDecimal("amountDue");
            final BigDecimal amountPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amountPaid");
            final BigDecimal amountWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amountWaived");
            final BigDecimal amountWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amountWrittenOff");
            final BigDecimal amountOutstanding = rs.getBigDecimal("amountOutstanding");

            final BigDecimal percentageOf = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "percentageOf");
            final BigDecimal amountPercentageAppliedTo = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amountPercentageAppliedTo");

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDecimalPlaces = JdbcSupport.getInteger(rs, "currencyDecimalPlaces");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");

            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDecimalPlaces, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);

            final int chargeTime = rs.getInt("chargeTime");
            final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(chargeTime);

            final LocalDate dueAsOfDate = JdbcSupport.getLocalDate(rs, "dueAsOfDate");
            final Integer feeInterval = JdbcSupport.getInteger(rs, "feeInterval");
            MonthDay feeOnMonthDay = null;
            final Integer feeOnMonth = JdbcSupport.getInteger(rs, "feeOnMonth");
            final Integer feeOnDay = JdbcSupport.getInteger(rs, "feeOnDay");
            if (feeOnDay != null && feeOnMonth != null) {
                feeOnMonthDay = new MonthDay(feeOnMonth, feeOnDay);
            }

            final int chargeCalculation = rs.getInt("chargeCalculation");
            final EnumOptionData chargeCalculationType = ChargeEnumerations.chargeCalculationType(chargeCalculation);
            final boolean penalty = rs.getBoolean("penalty");
            final Boolean isActive = rs.getBoolean("isActive");
            final LocalDate inactivationDate = JdbcSupport.getLocalDate(rs, "inactivationDate");

            final Collection<ChargeData> chargeOptions = null;

            return SavingsAccountChargeData.instance(id, chargeId, accountId, name, currency, amount, amountPaid, amountWaived,
                    amountWrittenOff, amountOutstanding, chargeTimeType, dueAsOfDate, chargeCalculationType, percentageOf,
                    amountPercentageAppliedTo, chargeOptions, penalty, feeOnMonthDay, feeInterval, isActive, inactivationDate);
        }
    }

    @Override
    public ChargeData retrieveSavingsAccountChargeTemplate() {
        this.context.authenticatedUser();

        final List<EnumOptionData> allowedChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService.retrieveCalculationTypes();
        final List<EnumOptionData> allowedChargeTimeOptions = this.chargeDropdownReadPlatformService.retrieveCollectionTimeTypes();
        final List<EnumOptionData> loansChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveLoanCalculationTypes();
        final List<EnumOptionData> loansChargeTimeTypeOptions = this.chargeDropdownReadPlatformService.retrieveLoanCollectionTimeTypes();
        final List<EnumOptionData> savingsChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveSavingsCalculationTypes();
        final List<EnumOptionData> savingsChargeTimeTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveSavingsCollectionTimeTypes();

        final List<EnumOptionData> feeFrequencyOptions = this.dropdownReadPlatformService.retrievePeriodFrequencyTypeOptions();
        final List<EnumOptionData> clientChargeCalculationTypeOptions = null;
        final List<EnumOptionData> clientChargeTimeTypeOptions = null;

        // this field is applicable only for client charges
        final Map<String, List<GLAccountData>> incomeOrLiabilityAccountOptions = null;
        final List<EnumOptionData> shareChargeCalculationTypeOptions = null;
        final List<EnumOptionData> shareChargeTimeTypeOptions = null;
        final Collection<TaxGroupData> taxGroupOptions = null;
        // TODO AA : revisit for merge conflict - Not sure method signature
        final List<EnumOptionData> glimChargeCalculationOptions = null;
        final List<EnumOptionData> slabChargeTypeOptions = null;
        final Collection<EnumOptionData> percentageTypeOptions = null;
        final Collection<EnumOptionData> percentagePeriodTypeOptions = null;
        final Collection<EnumOptionData> penaltyGraceTypeOptions = null;
        return ChargeData.template(null, allowedChargeCalculationTypeOptions, null, allowedChargeTimeOptions, null,
                loansChargeCalculationTypeOptions, loansChargeTimeTypeOptions, savingsChargeCalculationTypeOptions,
                savingsChargeTimeTypeOptions, clientChargeCalculationTypeOptions, clientChargeTimeTypeOptions, feeFrequencyOptions,
                incomeOrLiabilityAccountOptions, taxGroupOptions, shareChargeCalculationTypeOptions, shareChargeTimeTypeOptions,
                glimChargeCalculationOptions, slabChargeTypeOptions, percentageTypeOptions, percentagePeriodTypeOptions,
                penaltyGraceTypeOptions);
    }

    @Override
    public SavingsAccountChargeData retrieveSavingsAccountChargeDetails(final Long id, final Long savingsAccountId) {
        try {
            this.context.authenticatedUser();

            final SavingsAccountChargeMapper rm = new SavingsAccountChargeMapper();

            final String sql = "select " + rm.schema() + " where sc.id=? and sc.savings_account_id=?";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { id, savingsAccountId });
        } catch (final EmptyResultDataAccessException e) {
            throw new SavingsAccountChargeNotFoundException(savingsAccountId);
        }
    }

    @Override
    public Collection<SavingsAccountChargeData> retrieveSavingsAccountCharges(final Long loanId, final String status) {
        this.context.authenticatedUser();

        final SavingsAccountChargeMapper rm = new SavingsAccountChargeMapper();
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select ").append(rm.schema()).append(" where sc.savings_account_id=? ");
        if (status.equalsIgnoreCase("active")) {
            sqlBuilder.append(" and sc.is_active = 1 ");
        } else if (status.equalsIgnoreCase("inactive")) {
            sqlBuilder.append(" and sc.is_active = 0 ");
        }
        sqlBuilder.append(" order by sc.charge_time_enum ASC, sc.charge_due_date ASC, sc.is_penalty ASC");

        return this.jdbcTemplate.query(sqlBuilder.toString(), rm, new Object[] { loanId });
    }

    private static final class SavingsAccountChargeDueMapper implements RowMapper<SavingsAccountAnnualFeeData> {

        private final String schemaSql;

        public SavingsAccountChargeDueMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("sac.id as id, ");
            sqlBuilder.append("sa.id as accountId, ");
            sqlBuilder.append("sa.account_no as accountNo, ");
            sqlBuilder.append("sac.charge_due_date as dueDate ");
            sqlBuilder.append("from m_savings_account_charge sac join m_savings_account sa on sac.savings_account_id = sa.id ");

            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public SavingsAccountAnnualFeeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long accountId = rs.getLong("accountId");
            final String accountNo = rs.getString("accountNo");
            final LocalDate annualFeeNextDueDate = JdbcSupport.getLocalDate(rs, "dueDate");

            return SavingsAccountAnnualFeeData.instance(id, accountId, accountNo, annualFeeNextDueDate);
        }
    }

    @Override
    public Collection<SavingsAccountAnnualFeeData> retrieveChargesWithAnnualFeeDue() {
        final String currentdate = this.formatter.print(DateUtils.getLocalDateOfTenant());
        final String sql = "select " + this.chargeDueMapper.schema() + " where sac.charge_due_date is not null and sac.charge_time_enum = "
                + ChargeTimeType.ANNUAL_FEE.getValue() + " and sac.charge_due_date <= ? and sa.status_enum = "
                + SavingsAccountStatusType.ACTIVE.getValue();

        return this.jdbcTemplate.query(sql, this.chargeDueMapper, new Object[] { currentdate });
    }

    @Override
    public Collection<SavingsAccountAnnualFeeData> retrieveChargesWithDue() {
        final String currentdate = this.formatter.print(DateUtils.getLocalDateOfTenant());
        final String sql = "select " + this.chargeDueMapper.schema()
                + " where sac.charge_due_date is not null and sac.charge_due_date <= ? and sac.waived = 0 and sac.is_paid_derived=0 and sac.is_active=1 and sa.status_enum = "
                + SavingsAccountStatusType.ACTIVE.getValue() + " order by sac.charge_due_date ";

        return this.jdbcTemplate.query(sql, this.chargeDueMapper, new Object[] { currentdate });

    }

    private static final class SavingsChargesSummaryMapper implements RowMapper<SavingsChargesSummaryData> {

        private final String schemaSql;

        public SavingsChargesSummaryMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append(" c.name as chargeName,mlc.amount_outstanding_derived as ChargeDue,mlc.id as chargeId, ");
            sqlBuilder.append("l.id as savingsId ,l.account_no as account_no, ");
            sqlBuilder.append("ifnull(mlc.charge_due_date,curdate())as charge_due_date ");
            sqlBuilder.append("from m_client m inner join m_savings_account l on  l.client_id=m.id ");
            sqlBuilder.append("inner join m_savings_account_charge mlc on l.id=mlc.savings_account_id ");
            sqlBuilder.append("inner join m_charge c on c.id=mlc.charge_id ");

            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public SavingsChargesSummaryData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final String chargeName = rs.getString("chargeName");
            final BigDecimal chargeDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "ChargeDue");
            final String accountNo = rs.getString("account_no");
            final Long id = rs.getLong("savingsId");
            final Long chargeId = rs.getLong("chargeId");
            final String date = rs.getString("charge_due_date");
            return new SavingsChargesSummaryData(id, accountNo, chargeName, chargeDue, chargeId, date);

        }

    }

    @Override
    public Collection<SavingsChargesSummaryData> retriveCharge(final Long savings_account_id, final String chargeonDate) {
        final SavingsChargesSummaryMapper rm = new SavingsChargesSummaryMapper();
        final StringBuilder sb = new StringBuilder(100);
        sb.append("select a.chargeName,a.ChargeDue,a.chargeId,a.savingsId,a.account_no,a.charge_due_date from(");
        sb.append("select ");
        sb.append(rm.schema());
        sb.append("where mlc.savings_account_id=? ");
        sb.append("and mlc.is_paid_derived=0");
        sb.append(" and  mlc.waived=0)a");
        sb.append(" where a.charge_due_date <= ?");
        return this.jdbcTemplate.query(sb.toString(), rm, new Object[] { savings_account_id, chargeonDate });
    }

    @Override
    public SavingsIdOfChargeData retriveOneWithMaxOfDueDate(final Long savingId) {

        final SavingsIdOfChargeDataWithDueDataMapper rm = new SavingsIdOfChargeDataWithDueDataMapper();
        try {
            final String sql = " select max(msach.charge_due_date) as dueDate "
                    + " from m_savings_account_charge msach where msach.charge_time_enum = 12 and " + "  msach.savings_account_id = "
                    + savingId;

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {});
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public SavingIdListData retriveMaxOfTransaction(final Long savingId) {
        final SavingIdListDataMapperForTxnDate rm = new SavingIdListDataMapperForTxnDate();
        try {
            final String sql = "select max(ms.transaction_date) as transactionDate "
                    + " from m_savings_account_transaction ms where ms.savings_account_id = " + savingId;

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {});
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Collection<SavingIdListData> retriveAllSavingIdForApplyDepositeLateCharge() {
        final SavingIdListForDepositeLateChargeDataMapper rm = new SavingIdListForDepositeLateChargeDataMapper();
        final String sql = " select msa.id as savingId, msa.activatedon_date as activationOnDate, msa.start_saving_deposite_late_fee_date as startFeeChargeDate "
                + " from m_savings_account msa " + " left join m_savings_product_charge mspc on msa.product_id = mspc.savings_product_id "
                + " left join m_charge mch on mspc.charge_id = mch.id " + " where mch.charge_time_enum = 12 "
                + " and msa.status_enum = 300 " + " and msa.product_id = mspc.savings_product_id ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    public Collection<SavingsIdOfChargeData> retriveAllSavingIdHavingDepositCharge(final String startDate) {
        final SavingsIdOfChargeDataMapper rm = new SavingsIdOfChargeDataMapper();
        final String sql = " select a.savingId from "
                + " (select msac.savings_account_id as savingId, max(month(msac.charge_due_date)) as days from m_savings_account_charge msac "
                + " where msac.charge_time_enum = 12 " + " and msac.is_active = 1 " + " group by msac.savings_account_id )a "
                + " where a.days between month('" + startDate + "') and month(now())";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});

    }

    @Override
    public Collection<SavingIdListData> retriveSavingAccountForApplySavingDepositeFee(final String startDate) {

        final SavingIdListDataMapper rm = new SavingIdListDataMapper();

        final String sql = "select a.savingId as savingId, a.Txn as timePeriode, a.startFeeChargeDate as startFeeChargeDate  from (select msa.id as savingId, MAX(month(mst.transaction_date)) as days,"
                + " max(mst.transaction_date) as Txn, msa.start_saving_deposite_late_fee_date as startFeeChargeDate from m_savings_product msp "
                + " left join m_savings_product_charge mspc on mspc.savings_product_id = msp.id "
                + " left join m_charge mch on mspc.charge_id = mch.id " + " left join m_savings_account msa on msp.id = msa.product_id "
                + " left join m_savings_account_transaction mst on mst.savings_account_id = msa.id "
                + " where mspc.savings_product_id = msa.product_id " + " and mst.transaction_type_enum = 1 " + " and msa.status_enum = 300 "
                + " group by msa.id ) a " + " where a.days NOT BETWEEN MONTH('" + startDate + "') AND MONTH(now())";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});

    }

}