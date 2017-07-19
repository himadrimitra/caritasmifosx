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

import org.apache.fineract.accounting.common.AccountingEnumerations;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.entityaccess.domain.FineractEntityType;
import org.apache.fineract.infrastructure.entityaccess.service.FineractEntityAccessUtil;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.common.domain.DayOfWeekType;
import org.apache.fineract.portfolio.common.domain.NthDayType;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.common.service.CommonEnumerations;
import org.apache.fineract.portfolio.interestratechart.service.FloatingInterestRateChartReadPlatformService;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;
import org.apache.fineract.portfolio.savings.data.SavingsProductDrawingPowerDetailsData;
import org.apache.fineract.portfolio.savings.exception.SavingsProductNotFoundException;
import org.apache.fineract.portfolio.tax.data.TaxGroupData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SavingsProductReadPlatformServiceImpl implements SavingsProductReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final SavingProductMapper savingsProductRowMapper = new SavingProductMapper();
    private final SavingProductLookupMapper savingsProductLookupsRowMapper = new SavingProductLookupMapper();
    private final FineractEntityAccessUtil fineractEntityAccessUtil;
    private final FloatingInterestRateChartReadPlatformService floatingInterestRateChartReadPlatformService;
    @Autowired
    public SavingsProductReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final FineractEntityAccessUtil fineractEntityAccessUtil, final FloatingInterestRateChartReadPlatformService floatingInterestRateChartReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.fineractEntityAccessUtil = fineractEntityAccessUtil;
        this.floatingInterestRateChartReadPlatformService = floatingInterestRateChartReadPlatformService;
    }

    @Override
    public Collection<SavingsProductData> retrieveAll() {

        this.context.authenticatedUser();

        String sql = "select " + this.savingsProductRowMapper.schema() + "where sp.deposit_type_enum = ?";

        // Check if branch specific products are enabled. If yes, fetch only
        // products mapped to current user's office
        String inClause = fineractEntityAccessUtil
                .getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled(FineractEntityType.SAVINGS_PRODUCT);
        if ((inClause != null) && (!(inClause.trim().isEmpty()))) {
            sql += " and sp.id in ( " + inClause + " ) ";
        }

        return this.jdbcTemplate.query(sql, this.savingsProductRowMapper, new Object[] { DepositAccountType.SAVINGS_DEPOSIT.getValue() });
    }

    @Override
    public Collection<SavingsProductData> retrieveAllForLookup() {

        String sql = "select " + this.savingsProductLookupsRowMapper.schema() + " where sp.deposit_type_enum = ? ";

        // Check if branch specific products are enabled. If yes, fetch only
        // products mapped to current user's office
        String inClause = fineractEntityAccessUtil
                .getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled(FineractEntityType.SAVINGS_PRODUCT);
        if ((inClause != null) && (!(inClause.trim().isEmpty()))) {
            sql += " and id in ( " + inClause + " ) ";
        }

        return this.jdbcTemplate.query(sql, this.savingsProductLookupsRowMapper,
                new Object[] { DepositAccountType.SAVINGS_DEPOSIT.getValue() });
    }

    @Override
    public SavingsProductData retrieveOne(final Long savingProductId) {
        try {
            this.context.authenticatedUser();
            final String sql = "select " + this.savingsProductRowMapper.schema() + " where sp.id = ? and sp.deposit_type_enum = ?";
            SavingsProductData savingsProductData = this.jdbcTemplate.queryForObject(sql, this.savingsProductRowMapper,
                    new Object[] { savingProductId, DepositAccountType.SAVINGS_DEPOSIT.getValue() });
            savingsProductData
                    .updateFloatingInterestRateChartData(floatingInterestRateChartReadPlatformService.retrieveByProductId(savingProductId));
            return savingsProductData;
        } catch (final EmptyResultDataAccessException e) {
            throw new SavingsProductNotFoundException(savingProductId);
        }
    }

    private static final class SavingProductMapper implements RowMapper<SavingsProductData> {

        private final String schemaSql;

        public SavingProductMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("sp.id as id, sp.name as name, sp.short_name as shortName, sp.description as description, sp.external_id as externalId, ");
            sqlBuilder
                    .append("sp.currency_code as currencyCode, sp.currency_digits as currencyDigits, sp.currency_multiplesof as inMultiplesOf, ");
            sqlBuilder.append("curr.name as currencyName, curr.internationalized_name_code as currencyNameCode, ");
            sqlBuilder.append("curr.display_symbol as currencyDisplaySymbol, ");
            sqlBuilder.append("sp.nominal_annual_interest_rate as nominalAnnualInterestRate, ");
            sqlBuilder.append("sp.interest_compounding_period_enum as compoundingInterestPeriodType, ");
            sqlBuilder.append("sp.interest_posting_period_enum as interestPostingPeriodType, ");
            sqlBuilder.append("sp.interest_calculation_type_enum as interestCalculationType, ");
            sqlBuilder.append("sp.interest_calculation_days_in_year_type_enum as interestCalculationDaysInYearType, ");
            sqlBuilder.append("sp.min_required_opening_balance as minRequiredOpeningBalance, ");
            sqlBuilder.append("sp.lockin_period_frequency as lockinPeriodFrequency,");
            sqlBuilder.append("sp.lockin_period_frequency_enum as lockinPeriodFrequencyType, ");
            sqlBuilder.append("sp.withdrawal_fee_for_transfer as withdrawalFeeForTransfers, ");
            sqlBuilder.append("sp.allow_overdraft as allowOverdraft, ");
            sqlBuilder.append("sp.overdraft_limit as overdraftLimit, ");
            sqlBuilder.append("sp.nominal_annual_interest_rate_overdraft as nominalAnnualInterestRateOverdraft, ");
            sqlBuilder.append("sp.min_overdraft_for_interest_calculation as minOverdraftForInterestCalculation, ");
            sqlBuilder.append("sp.min_required_balance as minRequiredBalance, ");
            sqlBuilder.append("sp.enforce_min_required_balance as enforceMinRequiredBalance, ");
            sqlBuilder.append("sp.min_balance_for_interest_calculation as minBalanceForInterestCalculation,");
            sqlBuilder.append("sp.accounting_type as accountingType, ");
            sqlBuilder.append("sp.withhold_tax as withHoldTax,");
            sqlBuilder.append("tg.id as taxGroupId, tg.name as taxGroupName, ");
            sqlBuilder.append("sp.is_dormancy_tracking_active as isDormancyTrackingActive,");
            sqlBuilder.append("sp.days_to_inactive as daysToInactive,");
            sqlBuilder.append("sp.days_to_dormancy as daysToDormancy,");
            sqlBuilder.append("sp.days_to_escheat as daysToEscheat ");
            sqlBuilder.append(",spdpd.frequency_type_enum as frequencyType ");
            sqlBuilder.append(",spdpd.frequency_interval as frequencyInterval ");
            sqlBuilder.append(",spdpd.frequency_nth_day_enum as frequencyNthDay ");
            sqlBuilder.append(",spdpd.frequency_day_of_week_type_enum as frequencyDayOfWeekType ");
            sqlBuilder.append(",spdpd.frequency_on_day as frequencyOnDay ");
            sqlBuilder.append("from m_savings_product sp ");
            sqlBuilder.append("left join f_savings_product_drawing_power_details spdpd on spdpd.product_id = sp.id ");
            sqlBuilder.append("join m_currency curr on curr.code = sp.currency_code ");
            sqlBuilder.append("left join m_tax_group tg on tg.id = sp.tax_group_id  ");

            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public SavingsProductData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String shortName = rs.getString("shortName");
            final String description = rs.getString("description");
            final String externalId = rs.getString("externalId");

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);
            final BigDecimal nominalAnnualInterestRate = rs.getBigDecimal("nominalAnnualInterestRate");

            final Integer compoundingInterestPeriodTypeValue = JdbcSupport.getInteger(rs, "compoundingInterestPeriodType");
            final EnumOptionData compoundingInterestPeriodType = SavingsEnumerations
                    .compoundingInterestPeriodType(compoundingInterestPeriodTypeValue);

            final Integer interestPostingPeriodTypeValue = JdbcSupport.getInteger(rs, "interestPostingPeriodType");
            final EnumOptionData interestPostingPeriodType = SavingsEnumerations.interestPostingPeriodType(interestPostingPeriodTypeValue);

            final Integer interestCalculationTypeValue = JdbcSupport.getInteger(rs, "interestCalculationType");
            final EnumOptionData interestCalculationType = SavingsEnumerations.interestCalculationType(interestCalculationTypeValue);

            EnumOptionData interestCalculationDaysInYearType = null;
            final Integer interestCalculationDaysInYearTypeValue = JdbcSupport.getInteger(rs, "interestCalculationDaysInYearType");
            if (interestCalculationDaysInYearTypeValue != null) {
                interestCalculationDaysInYearType = SavingsEnumerations
                        .interestCalculationDaysInYearType(interestCalculationDaysInYearTypeValue);
            }

            final Integer accountingRuleId = JdbcSupport.getInteger(rs, "accountingType");
            final EnumOptionData accountingRuleType = AccountingEnumerations.accountingRuleType(accountingRuleId);

            final BigDecimal minRequiredOpeningBalance = rs.getBigDecimal("minRequiredOpeningBalance");

            final Integer lockinPeriodFrequency = JdbcSupport.getInteger(rs, "lockinPeriodFrequency");
            EnumOptionData lockinPeriodFrequencyType = null;
            final Integer lockinPeriodFrequencyTypeValue = JdbcSupport.getInteger(rs, "lockinPeriodFrequencyType");
            if (lockinPeriodFrequencyTypeValue != null) {
                lockinPeriodFrequencyType = SavingsEnumerations.lockinPeriodFrequencyType(lockinPeriodFrequencyTypeValue);
            }

            final boolean withdrawalFeeForTransfers = rs.getBoolean("withdrawalFeeForTransfers");
            final boolean allowOverdraft = rs.getBoolean("allowOverdraft");
            final BigDecimal overdraftLimit = rs.getBigDecimal("overdraftLimit");
            final BigDecimal nominalAnnualInterestRateOverdraft = rs.getBigDecimal("nominalAnnualInterestRateOverdraft");
            final BigDecimal minOverdraftForInterestCalculation = rs.getBigDecimal("minOverdraftForInterestCalculation");

            final BigDecimal minRequiredBalance = rs.getBigDecimal("minRequiredBalance");
            final boolean enforceMinRequiredBalance = rs.getBoolean("enforceMinRequiredBalance");
            final BigDecimal minBalanceForInterestCalculation = rs.getBigDecimal("minBalanceForInterestCalculation");

            final boolean withHoldTax = rs.getBoolean("withHoldTax");
            final Long taxGroupId = JdbcSupport.getLong(rs, "taxGroupId");
            final String taxGroupName = rs.getString("taxGroupName");
            TaxGroupData taxGroupData = null;
            if (taxGroupId != null) {
                taxGroupData = TaxGroupData.lookup(taxGroupId, taxGroupName);
            }
            
            final Boolean isDormancyTrackingActive = rs.getBoolean("isDormancyTrackingActive");
            final Long daysToInactive = JdbcSupport.getLong(rs, "daysToInactive");
            final Long daysToDormancy = JdbcSupport.getLong(rs, "daysToDormancy");
            final Long daysToEscheat = JdbcSupport.getLong(rs, "daysToEscheat");
            
            final String codePrefix = "savingsproductdrawingpower.";

            final Integer frequencyTypeValue = JdbcSupport.getInteger(rs, "frequencyType");
            EnumOptionData frequencyType = null;
            if (frequencyTypeValue != null) {
                frequencyType = PeriodFrequencyType.periodFrequencyType(frequencyTypeValue);
            }

            final Integer frequencyInterval = JdbcSupport.getInteger(rs, "frequencyInterval");

            final Integer frequencyNthDayValue = JdbcSupport.getInteger(rs, "frequencyNthDay");
            EnumOptionData frequencyNthDay = null;
            if (frequencyNthDayValue != null) {
                frequencyNthDay = CommonEnumerations.nthDayType(NthDayType.fromInt(frequencyNthDayValue), codePrefix);
            }

            final Integer frequencyDayOfWeekTypeValue = JdbcSupport.getInteger(rs, "frequencyDayOfWeekType");
            EnumOptionData frequencyDayOfWeekType = null;
            if (frequencyDayOfWeekTypeValue != null) {
                frequencyDayOfWeekType = CommonEnumerations.dayOfWeekType(DayOfWeekType.fromInt(frequencyDayOfWeekTypeValue), codePrefix);
            }

            final Integer frequencyOnDay = JdbcSupport.getInteger(rs, "frequencyOnDay");

            final SavingsProductDrawingPowerDetailsData savingsProductDrawingPowerDetailsData = SavingsProductDrawingPowerDetailsData
                    .createNew(frequencyType, frequencyInterval, frequencyNthDay, frequencyDayOfWeekType, frequencyOnDay);

            return SavingsProductData.instance(id, name, shortName, description, currency, nominalAnnualInterestRate,
                    compoundingInterestPeriodType, interestPostingPeriodType, interestCalculationType, interestCalculationDaysInYearType,
                    minRequiredOpeningBalance, lockinPeriodFrequency, lockinPeriodFrequencyType, withdrawalFeeForTransfers,
                    accountingRuleType, allowOverdraft, overdraftLimit, minRequiredBalance, enforceMinRequiredBalance,
                    minBalanceForInterestCalculation, nominalAnnualInterestRateOverdraft, minOverdraftForInterestCalculation, withHoldTax,
                    taxGroupData, isDormancyTrackingActive, daysToInactive, daysToDormancy, daysToEscheat, externalId,
                    savingsProductDrawingPowerDetailsData);
        }
    }

    private static final class SavingProductLookupMapper implements RowMapper<SavingsProductData> {

        public String schema() {
            return " sp.id as id, sp.name as name from m_savings_product sp";
        }

        @Override
        public SavingsProductData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");

            return SavingsProductData.lookup(id, name);
        }
    }

    @Override
    public Collection<SavingsProductData> retrieveAllForLookupByType(Boolean isOverdraftType) {
        String sql = "select " + this.savingsProductLookupsRowMapper.schema();

        boolean inClauseAdded = false;

        // Check if branch specific products are enabled. If yes, fetch only
        // products mapped to current user's office
        String inClause = fineractEntityAccessUtil
                .getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled(FineractEntityType.SAVINGS_PRODUCT);
        if ((inClause != null) && (!(inClause.trim().isEmpty()))) {
            sql += " where id in ( " + inClause + " ) ";
            inClauseAdded = true;
        }

        if (isOverdraftType != null) {
            if (inClauseAdded) {
                sql += " and sp.allow_overdraft=?";
            } else {
                sql += " where sp.allow_overdraft=?";
            }
            return this.jdbcTemplate.query(sql, this.savingsProductLookupsRowMapper, isOverdraftType);
        }

        return this.jdbcTemplate.query(sql, this.savingsProductLookupsRowMapper);

    }

    @Override
    public Collection<SavingsProductData> retrieveAllForCurrency(String currencyCode) {

        this.context.authenticatedUser();

        String sql = "select " + this.savingsProductRowMapper.schema() + " where sp.currency_code='" + currencyCode + "'";

        // Check if branch specific products are enabled. If yes, fetch only
        // products mapped to current user's office
        String inClause = fineractEntityAccessUtil
                .getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled(FineractEntityType.SAVINGS_PRODUCT);
        if ((inClause != null) && (!(inClause.trim().isEmpty()))) {
            sql += " and id in ( " + inClause + " ) ";
        }

        return this.jdbcTemplate.query(sql, this.savingsProductRowMapper);
    }
}