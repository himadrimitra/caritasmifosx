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
package org.apache.fineract.portfolio.charge.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.accounting.common.AccountingDropdownReadPlatformService;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.entityaccess.domain.FineractEntityType;
import org.apache.fineract.infrastructure.entityaccess.service.FineractEntityAccessUtil;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.service.CurrencyReadPlatformService;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.data.ChargeOverdueData;
import org.apache.fineract.portfolio.charge.data.ChargeSlabData;
import org.apache.fineract.portfolio.charge.domain.ChargeAppliesTo;
import org.apache.fineract.portfolio.charge.domain.ChargePercentagePeriodType;
import org.apache.fineract.portfolio.charge.domain.ChargePercentageType;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.domain.PenaltyGraceType;
import org.apache.fineract.portfolio.charge.domain.SlabChargeType;
import org.apache.fineract.portfolio.charge.exception.ChargeNotFoundException;
import org.apache.fineract.portfolio.common.domain.LoanPeriodFrequencyType;
import org.apache.fineract.portfolio.common.service.DropdownReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.tax.data.TaxGroupData;
import org.apache.fineract.portfolio.tax.service.TaxReadPlatformService;
import org.joda.time.MonthDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author vishwas
 *
 */
@Service
public class ChargeReadPlatformServiceImpl implements ChargeReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final CurrencyReadPlatformService currencyReadPlatformService;
    private final ChargeDropdownReadPlatformService chargeDropdownReadPlatformService;
    private final DropdownReadPlatformService dropdownReadPlatformService;
    private final FineractEntityAccessUtil fineractEntityAccessUtil;
    private final AccountingDropdownReadPlatformService accountingDropdownReadPlatformService;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final TaxReadPlatformService taxReadPlatformService;
    final ChargeSlabReadPlatformService chargeSlabReadPlatformService;

    @Autowired
    public ChargeReadPlatformServiceImpl(final CurrencyReadPlatformService currencyReadPlatformService,
            final ChargeDropdownReadPlatformService chargeDropdownReadPlatformService, final RoutingDataSource dataSource,
            final DropdownReadPlatformService dropdownReadPlatformService, final FineractEntityAccessUtil fineractEntityAccessUtil,
            final AccountingDropdownReadPlatformService accountingDropdownReadPlatformService,
            final TaxReadPlatformService taxReadPlatformService, final ChargeSlabReadPlatformService chargeSlabReadPlatformService) {
        this.chargeDropdownReadPlatformService = chargeDropdownReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.currencyReadPlatformService = currencyReadPlatformService;
        this.dropdownReadPlatformService = dropdownReadPlatformService;
        this.fineractEntityAccessUtil = fineractEntityAccessUtil;
        this.accountingDropdownReadPlatformService = accountingDropdownReadPlatformService;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.taxReadPlatformService = taxReadPlatformService;
        this.chargeSlabReadPlatformService = chargeSlabReadPlatformService;
    }

    @Override
    // @Cacheable(value = "charges", key =
    // "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('ch')")
    public Collection<ChargeData> retrieveAllCharges() {
        final ChargeMapperDataExtractor rm = new ChargeMapperDataExtractor(this.chargeSlabReadPlatformService);
        String sql = "select " + rm.schema() + " where c.is_deleted=0 ";
        sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();
        sql += " order by c.name ";
        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    private static final class ChargeMapperDataExtractor implements ResultSetExtractor<Collection<ChargeData>> {

        final ChargeMapper pm = new ChargeMapper();
        final ChargeSlabMapper cm = new ChargeSlabMapper();
        final ChargeSlabReadPlatformService chargeSlabReadPlatformService;

        public ChargeMapperDataExtractor(final ChargeSlabReadPlatformService chargeSlabReadPlatformService) {
            this.chargeSlabReadPlatformService = chargeSlabReadPlatformService;
        }

        public String schema() {
            return this.pm.chargeSchemaWithChargeSlabs();
        }

        @Override
        public Collection<ChargeData> extractData(final ResultSet rs) throws SQLException, DataAccessException {
            final List<ChargeData> chargeDataList = new ArrayList<>();
            ChargeData chargeData = null;
            Long chargeDataId = null;
            final int rowNum = 0;// index
            while (rs.next()) {
                final Long tempcId = rs.getLong("id");
                if (chargeData == null || (chargeDataId != null && !chargeDataId.equals(tempcId))) {
                    chargeDataId = tempcId;
                    chargeData = this.pm.mapRow(rs, rowNum);
                    chargeDataList.add(chargeData);
                }
                final ChargeSlabData chargeSlabData = this.cm.mapRow(rs, rowNum);
                if (chargeSlabData != null) {
                    chargeSlabData.setSubSlabs(
                            this.chargeSlabReadPlatformService.retrieveAllChargeSubSlabsBySlabChargeId(chargeSlabData.getId()));
                    chargeData.addChargeSlabData(chargeSlabData);
                }

            }
            return chargeDataList;
        }
    }

    @Override
    public Collection<ChargeData> retrieveAllChargesForCurrency(final String currencyCode) {
        final ChargeMapper rm = new ChargeMapper();

        String sql = "select " + rm.chargeSchema() + " where c.is_deleted=0 and c.currency_code='" + currencyCode + "' ";

        sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();

        sql += " order by c.name ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    public ChargeData retrieveCharge(final Long chargeId) {
        try {
            final ChargeMapperDataExtractor rm = new ChargeMapperDataExtractor(this.chargeSlabReadPlatformService);
            String sql = "select " + rm.schema() + " where c.id = ? and c.is_deleted=0 ";
            sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();
            final Collection<ChargeData> chargeDataList = this.jdbcTemplate.query(sql, rm, new Object[] { chargeId });

            if (!chargeDataList.isEmpty()) { return chargeDataList.iterator().next(); }
            throw new ChargeNotFoundException(chargeId);
        } catch (final EmptyResultDataAccessException e) {
            throw new ChargeNotFoundException(chargeId);
        }
    }

    @Override
    public ChargeData retrieveNewChargeDetails() {

        final Collection<CurrencyData> currencyOptions = this.currencyReadPlatformService.retrieveAllowedCurrencies();
        final List<EnumOptionData> allowedChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService.retrieveCalculationTypes();
        final List<EnumOptionData> allowedChargeAppliesToOptions = this.chargeDropdownReadPlatformService.retrieveApplicableToTypes();
        final List<EnumOptionData> allowedChargeTimeOptions = this.chargeDropdownReadPlatformService.retrieveCollectionTimeTypes();
        final List<EnumOptionData> chargePaymentOptions = this.chargeDropdownReadPlatformService.retrivePaymentModes();
        final List<EnumOptionData> slabChargeTypeOptions = this.chargeDropdownReadPlatformService.retrieveSlabChargeTypes();
        final List<EnumOptionData> loansChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveLoanCalculationTypes();
        final List<EnumOptionData> loansChargeTimeTypeOptions = this.chargeDropdownReadPlatformService.retrieveLoanCollectionTimeTypes();
        final List<EnumOptionData> savingsChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveSavingsCalculationTypes();
        final List<EnumOptionData> savingsChargeTimeTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveSavingsCollectionTimeTypes();
        final List<EnumOptionData> clientChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveClientCalculationTypes();
        final List<EnumOptionData> clientChargeTimeTypeOptions = this.chargeDropdownReadPlatformService.retrieveClientCollectionTimeTypes();
        final Collection<EnumOptionData> feeFrequencyOptions = this.dropdownReadPlatformService.retrieveLoanPeriodFrequencyTypeOptions();
        final Map<String, List<GLAccountData>> incomeOrLiabilityAccountOptions = this.accountingDropdownReadPlatformService
                .retrieveAccountMappingOptionsForCharges();
        final List<EnumOptionData> shareChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveSharesCalculationTypes();
        final List<EnumOptionData> shareChargeTimeTypeOptions = this.chargeDropdownReadPlatformService.retrieveSharesCollectionTimeTypes();
        final Collection<TaxGroupData> taxGroupOptions = this.taxReadPlatformService.retrieveTaxGroupsForLookUp();
        final List<EnumOptionData> glimChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveGlimChargeCalculationTypes();
        final Collection<EnumOptionData> percentageTypeOptions = this.chargeDropdownReadPlatformService.retriveChargePercentageTypes();
        final Collection<EnumOptionData> percentagePeriodTypeOptions = this.chargeDropdownReadPlatformService
                .retriveChargePercentagePeriodTypes();
        final Collection<EnumOptionData> penaltyGraceTypeOptions = this.chargeDropdownReadPlatformService.retrivePenaltyGraceTypes();

        return ChargeData.template(currencyOptions, allowedChargeCalculationTypeOptions, allowedChargeAppliesToOptions,
                allowedChargeTimeOptions, chargePaymentOptions, loansChargeCalculationTypeOptions, loansChargeTimeTypeOptions,
                savingsChargeCalculationTypeOptions, savingsChargeTimeTypeOptions, clientChargeCalculationTypeOptions,
                clientChargeTimeTypeOptions, feeFrequencyOptions, incomeOrLiabilityAccountOptions, taxGroupOptions,
                shareChargeCalculationTypeOptions, shareChargeTimeTypeOptions, glimChargeCalculationTypeOptions, slabChargeTypeOptions,
                percentageTypeOptions, percentagePeriodTypeOptions, penaltyGraceTypeOptions);
    }

    @Override
    public Collection<ChargeData> retrieveLoanProductCharges(final Long loanProductId) {
        final ChargeMapper rm = new ChargeMapper();

        String sql = "select " + rm.loanProductChargeSchema() + " where c.is_deleted=0 and c.is_active=1 and plc.product_loan_id=? ";

        sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();

        return this.jdbcTemplate.query(sql, rm, new Object[] { loanProductId });
    }

    @Override
    public Collection<ChargeData> retrieveLoanProductCharges(final Long loanProductId, final ChargeTimeType chargeTime) {

        final ChargeMapper rm = new ChargeMapper();

        String sql = "select " + rm.loanProductChargeSchema()
                + " where c.is_deleted=0 and c.is_active=1 and plc.product_loan_id=? and c.charge_time_enum=? ";
        sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();

        return this.jdbcTemplate.query(sql, rm, new Object[] { loanProductId, chargeTime.getValue() });
    }

    @Override
    public Collection<ChargeData> retrieveLoanApplicableFees() {
        final ChargeMapper rm = new ChargeMapper();
        final Object[] params = new Object[] { ChargeAppliesTo.LOAN.getValue() };
        String sql = "select " + rm.chargeSchema()
                + " where c.is_deleted=0 and c.is_active=1 and c.is_penalty=0 and c.charge_applies_to_enum=? ";
        sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();
        sql += " order by c.name ";

        return this.jdbcTemplate.query(sql, rm, params);
    }

    @Override
    public Collection<ChargeData> retrieveLoanAccountApplicableCharges(final Long loanId, final ChargeTimeType[] excludeChargeTimes) {
        final ChargeMapper rm = new ChargeMapper();
        final StringBuilder excludeClause = new StringBuilder("");
        final Collection<Integer> chargeType = new ArrayList<>();
        chargeType.add(ChargeTimeType.DISBURSEMENT.getValue());
        chargeType.add(ChargeTimeType.TRANCHE_DISBURSEMENT.getValue());
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("loanId", loanId);
        paramMap.put("chargeAppliesTo", ChargeAppliesTo.LOAN.getValue());
        paramMap.put("status", LoanStatus.ACTIVE.getValue());
        paramMap.put("chargeType", chargeType);
        processChargeExclusionsForLoans(excludeChargeTimes, excludeClause);
        String sql = "select " + rm.chargeSchema() + " join m_loan la on la.currency_code = c.currency_code" + " where la.id=:loanId"
                + " and c.is_deleted=0 and c.is_active=1 and c.charge_applies_to_enum=:chargeAppliesTo" + excludeClause + " "
                + " and (la.loan_status_id != :status or c.charge_time_enum NOT In(:chargeType))";
        sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();
        sql += " order by c.name ";
        return this.namedParameterJdbcTemplate.query(sql, paramMap, rm);
    }

    /**
     * @param excludeChargeTimes
     * @param excludeClause
     * @param params
     * @return
     */
    private void processChargeExclusionsForLoans(final ChargeTimeType[] excludeChargeTimes, StringBuilder excludeClause) {
        if (excludeChargeTimes != null && excludeChargeTimes.length > 0) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < excludeChargeTimes.length; i++) {
                if (i != 0) {
                    sb.append(",");
                }
                sb.append(excludeChargeTimes[i].getValue());
            }
            excludeClause = excludeClause.append(" and c.charge_time_enum not in(" + sb.toString() + ") ");
        }
    }

    @Override
    public Collection<ChargeData> retrieveLoanProductApplicableCharges(final Long loanProductId,
            final ChargeTimeType[] excludeChargeTimes) {
        final ChargeMapper rm = new ChargeMapper();
        final StringBuilder excludeClause = new StringBuilder("");
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("productId", loanProductId);
        paramMap.put("chargeAppliesTo", ChargeAppliesTo.LOAN.getValue());
        processChargeExclusionsForLoans(excludeChargeTimes, excludeClause);
        String sql = "select " + rm.chargeSchema() + " join m_product_loan lp on lp.currency_code = c.currency_code"
                + " where lp.id=:productId" + " and c.is_deleted=0 and c.is_active=1 and c.charge_applies_to_enum=:chargeAppliesTo"
                + excludeClause + " ";
        sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();
        sql += " order by c.name ";

        return this.namedParameterJdbcTemplate.query(sql, paramMap, rm);
    }

    @Override
    public Collection<ChargeData> retrieveLoanApplicablePenalties() {
        final ChargeMapper rm = new ChargeMapper();

        String sql = "select " + rm.chargeSchema()
                + " where c.is_deleted=0 and c.is_active=1 and c.is_penalty=1 and c.charge_applies_to_enum=? ";
        sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();
        sql += " order by c.name ";
        return this.jdbcTemplate.query(sql, rm, new Object[] { ChargeAppliesTo.LOAN.getValue() });
    }

    private String addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled() {

        String sql = "";

        // Check if branch specific products are enabled. If yes, fetch only
        // charges mapped to current user's office
        final String inClause = this.fineractEntityAccessUtil
                .getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled(FineractEntityType.CHARGE);
        if ((inClause != null) && (!(inClause.trim().isEmpty()))) {
            sql += " and c.id in ( " + inClause + " ) ";
        }

        return sql;
    }

    private static final class ChargeMapper implements RowMapper<ChargeData> {

        private final ChargeOverdueDetailMapper chargeOverdueDetailMapper = new ChargeOverdueDetailMapper();

        public String chargeSchemaWithChargeSlabs() {
            return "c.id as id, c.name as name, c.amount as amount, c.currency_code as currencyCode, "
                    + "c.charge_applies_to_enum as chargeAppliesTo, c.charge_time_enum as chargeTime, "
                    + "c.charge_payment_mode_enum as chargePaymentMode, c.emi_rounding_goalseek as emiRoundingGoalSeek, c.glim_charge_calculation_enum as glimChargeCalculation, "
                    + "c.is_glim_charge as isGlimCharge, c.charge_calculation_enum as chargeCalculation, c.is_penalty as penalty, "
                    + "cs.id as csid, cs.from_loan_amount as min, cs.to_loan_amount as max, cs.type as type, cs.amount as chargeAmount, c.is_capitalized as isCapitalized, "
                    + "c.is_active as active, oc.name as currencyName, oc.decimal_places as currencyDecimalPlaces, "
                    + "oc.currency_multiplesof as inMultiplesOf, oc.display_symbol as currencyDisplaySymbol, "
                    + "oc.internationalized_name_code as currencyNameCode, c.fee_on_day as feeOnDay, c.fee_on_month as feeOnMonth, "
                    + "c.fee_interval as feeInterval, c.fee_frequency as feeFrequency,c.min_cap as minCap,c.max_cap as maxCap, "
                    + "c.income_or_liability_account_id as glAccountId , acc.name as glAccountName, acc.gl_code as glCode, "
                    + "c.charge_percentage_type as percentageType, c.charge_percentage_period_type as percentagePeriodType,"
                    + "cod.id as overdueDetailId,cod.grace_period as penaltyGracePeriod, cod.penalty_free_period as penaltyFreePeriod, "
                    + "cod.grace_type_enum as penaltyGraceType,cod.apply_charge_for_broken_period as applyPenaltyForBrokenPeriod, "
                    + "cod.is_based_on_original_schedule as penaltyBasedOnOriginalSchedule, cod.consider_only_posted_interest as penaltyOnPostedInterestOnly,"
                    + "cod.calculate_charge_on_current_overdue as penaltyOnCurrentOverdue,cod.min_overdue_amount_required as minOverdueAmountRequired,"
                    + "cod.stop_charge_on_npa as stopChargeOnNPA," + "tg.id as taxGroupId, tg.name as taxGroupName " + "from m_charge c "
                    + "join m_organisation_currency oc on c.currency_code = oc.code "
                    + " LEFT JOIN acc_gl_account acc on acc.id = c.income_or_liability_account_id "
                    + " LEFT JOIN m_tax_group tg on tg.id = c.tax_group_id " + " LEFT JOIN f_charge_slab cs on cs.charge_id = c.id "
                    + " LEFT JOIN f_charge_overdue_detail cod on cod.charge_id = c.id ";
        }

        public String chargeSchema() {
            return "c.id as id, c.name as name, c.amount as amount, c.currency_code as currencyCode, "
                    + "c.charge_applies_to_enum as chargeAppliesTo, c.charge_time_enum as chargeTime, "
                    + "c.charge_payment_mode_enum as chargePaymentMode, c.emi_rounding_goalseek as emiRoundingGoalSeek, c.glim_charge_calculation_enum as glimChargeCalculation, "
                    + "c.is_glim_charge as isGlimCharge, c.charge_calculation_enum as chargeCalculation, c.is_penalty as penalty, c.is_capitalized as isCapitalized, "
                    + "c.is_active as active, oc.name as currencyName, oc.decimal_places as currencyDecimalPlaces, "
                    + "oc.currency_multiplesof as inMultiplesOf, oc.display_symbol as currencyDisplaySymbol, "
                    + "oc.internationalized_name_code as currencyNameCode, c.fee_on_day as feeOnDay, c.fee_on_month as feeOnMonth, "
                    + "c.fee_interval as feeInterval, c.fee_frequency as feeFrequency,c.min_cap as minCap,c.max_cap as maxCap, "
                    + "c.income_or_liability_account_id as glAccountId , acc.name as glAccountName, acc.gl_code as glCode, "
                    + "c.charge_percentage_type as percentageType, c.charge_percentage_period_type as percentagePeriodType,"
                    + "cod.id as overdueDetailId,cod.grace_period as penaltyGracePeriod, cod.penalty_free_period as penaltyFreePeriod, "
                    + "cod.grace_type_enum as penaltyGraceType,cod.apply_charge_for_broken_period as applyPenaltyForBrokenPeriod, "
                    + "cod.is_based_on_original_schedule as penaltyBasedOnOriginalSchedule, cod.consider_only_posted_interest as penaltyOnPostedInterestOnly,"
                    + "cod.calculate_charge_on_current_overdue as penaltyOnCurrentOverdue, cod.min_overdue_amount_required as minOverdueAmountRequired, "
                    + "cod.stop_charge_on_npa as stopChargeOnNPA," + "tg.id as taxGroupId, tg.name as taxGroupName " + "from m_charge c "
                    + "join m_organisation_currency oc on c.currency_code = oc.code "
                    + " LEFT JOIN acc_gl_account acc on acc.id = c.income_or_liability_account_id "
                    + " LEFT JOIN m_tax_group tg on tg.id = c.tax_group_id "
                    + " LEFT JOIN f_charge_overdue_detail cod on cod.charge_id = c.id ";
        }

        public String loanProductChargeSchema() {
            return chargeSchema() + " join m_product_loan_charge plc on plc.charge_id = c.id";
        }

        public String savingsProductChargeSchema() {
            return chargeSchema() + " join m_savings_product_charge spc on spc.charge_id = c.id";
        }

        public String shareProductChargeSchema() {
            return chargeSchema() + " join m_share_product_charge mspc on mspc.charge_id = c.id";
        }

        @Override
        public ChargeData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final BigDecimal amount = rs.getBigDecimal("amount");

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDecimalPlaces = JdbcSupport.getInteger(rs, "currencyDecimalPlaces");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");

            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDecimalPlaces, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);

            final int chargeAppliesTo = rs.getInt("chargeAppliesTo");
            final EnumOptionData chargeAppliesToType = ChargeEnumerations.chargeAppliesTo(chargeAppliesTo);

            final int chargeTime = rs.getInt("chargeTime");
            final ChargeTimeType chargeTimeTypeEnum = ChargeTimeType.fromInt(chargeTime);
            final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(chargeTimeTypeEnum);

            final int chargeCalculation = rs.getInt("chargeCalculation");
            final EnumOptionData chargeCalculationType = ChargeEnumerations.chargeCalculationType(chargeCalculation);

            final int paymentMode = rs.getInt("chargePaymentMode");
            final EnumOptionData chargePaymentMode = ChargeEnumerations.chargePaymentMode(paymentMode);

            final boolean penalty = rs.getBoolean("penalty");
            final boolean active = rs.getBoolean("active");

            final Integer feeInterval = JdbcSupport.getInteger(rs, "feeInterval");
            EnumOptionData feeFrequencyType = null;
            final Integer feeFrequency = JdbcSupport.getInteger(rs, "feeFrequency");
            if (feeFrequency != null) {
                feeFrequencyType = LoanPeriodFrequencyType.overduePeriodFrequencyType(feeFrequency);
            }
            MonthDay feeOnMonthDay = null;
            final Integer feeOnMonth = JdbcSupport.getInteger(rs, "feeOnMonth");
            final Integer feeOnDay = JdbcSupport.getInteger(rs, "feeOnDay");
            if (feeOnDay != null && feeOnMonth != null) {
                feeOnMonthDay = new MonthDay(feeOnMonth, feeOnDay);
            }
            final BigDecimal minCap = rs.getBigDecimal("minCap");
            final BigDecimal maxCap = rs.getBigDecimal("maxCap");

            // extract GL Account
            final Long glAccountId = JdbcSupport.getLong(rs, "glAccountId");
            final String glAccountName = rs.getString("glAccountName");
            final String glCode = rs.getString("glCode");
            GLAccountData glAccountData = null;
            if (glAccountId != null) {
                glAccountData = new GLAccountData(glAccountId, glAccountName, glCode);
            }

            final Long taxGroupId = JdbcSupport.getLong(rs, "taxGroupId");
            final String taxGroupName = rs.getString("taxGroupName");
            TaxGroupData taxGroupData = null;
            if (taxGroupId != null) {
                taxGroupData = TaxGroupData.lookup(taxGroupId, taxGroupName);
            }
            final boolean emiRoundingGoalSeek = rs.getBoolean("emiRoundingGoalSeek");
            final boolean isGlimCharge = rs.getBoolean("isGlimCharge");
            final int glimCalculation = rs.getInt("glimChargeCalculation");
            final EnumOptionData glimChargeCalculation = ChargeEnumerations.glimChargeCalculationType(glimCalculation);
            final boolean isCapitalized = rs.getBoolean("isCapitalized");

            final int percentageType = rs.getInt("percentageType");
            final EnumOptionData percentageTypeOptionData = ChargePercentageType.chargePercentageType(percentageType);

            final int percentagePeriodType = rs.getInt("percentagePeriodType");
            final EnumOptionData percentagePeriodTypeOptionData = ChargePercentagePeriodType
                    .chargePercentagePeriodType(percentagePeriodType);

            ChargeOverdueData chargeOverdueData = null;
            if (chargeTimeTypeEnum.isOverdueInstallment()) {
                chargeOverdueData = this.chargeOverdueDetailMapper.mapRow(rs, rowNum);
            }

            return ChargeData.instance(id, name, amount, currency, chargeTimeType, chargeAppliesToType, chargeCalculationType,
                    chargePaymentMode, feeOnMonthDay, feeInterval, penalty, active, minCap, maxCap, feeFrequencyType, glAccountData,
                    taxGroupData, emiRoundingGoalSeek, isGlimCharge, glimChargeCalculation, isCapitalized, percentageTypeOptionData,
                    percentagePeriodTypeOptionData, chargeOverdueData);
        }
    }

    private static final class ChargeOverdueDetailMapper implements RowMapper<ChargeOverdueData> {

        @Override
        public ChargeOverdueData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "overdueDetailId");
            final Integer gracePeriod = JdbcSupport.getInteger(rs, "penaltyGracePeriod");
            final Integer penaltyFreePeriod = JdbcSupport.getInteger(rs, "penaltyFreePeriod");
            final Integer penaltyGraceType = JdbcSupport.getInteger(rs, "penaltyGraceType");
            final EnumOptionData graceType = PenaltyGraceType.penaltyGraceType(penaltyGraceType);
            final boolean applyChargeForBrokenPeriod = rs.getBoolean("applyPenaltyForBrokenPeriod");
            final boolean isBasedOnOriginalSchedule = rs.getBoolean("penaltyBasedOnOriginalSchedule");
            final boolean considerOnlyPostedInterest = rs.getBoolean("penaltyOnPostedInterestOnly");
            final boolean calculateChargeOnCurrentOverdue = rs.getBoolean("penaltyOnCurrentOverdue");
            final boolean stopChargeOnNPA = rs.getBoolean("stopChargeOnNPA");
            final BigDecimal minOverdueAmountRequired = rs.getBigDecimal("minOverdueAmountRequired");

            return new ChargeOverdueData(id, gracePeriod, penaltyFreePeriod, graceType, applyChargeForBrokenPeriod,
                    isBasedOnOriginalSchedule, considerOnlyPostedInterest, calculateChargeOnCurrentOverdue, stopChargeOnNPA,
                    minOverdueAmountRequired);
        }

    }

    private static final class ChargeSlabMapper implements RowMapper<ChargeSlabData> {

        /*
         * public String chargeSlabSchema() { return
         * "cs.id as csid, cs.from_loan_amount as min, cs.to_loan_amount as max, cs.type as type, cs.amount as chargeAmount, "
         * + "c.is_capitalized as isCapitalized from f_charge_slab cs " +
         * " LEFT JOIN m_charge c on cs.charge_id = c.id "; }
         */

        @Override
        public ChargeSlabData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("csid");
            final BigDecimal minValue = rs.getBigDecimal("min");
            final BigDecimal maxValue = rs.getBigDecimal("max");
            final BigDecimal amount = rs.getBigDecimal("chargeAmount");
            final EnumOptionData type = SlabChargeType.fromInt(rs.getInt("type"));
            return ChargeSlabData.createChargeSlabData(id, minValue, maxValue, amount, type);
        }
    }

    @Override
    public Collection<ChargeData> retrieveSavingsProductApplicableCharges(final boolean feeChargesOnly) {
        final ChargeMapper rm = new ChargeMapper();

        String sql = "select " + rm.chargeSchema() + " where c.is_deleted=0 and c.is_active=1 and c.charge_applies_to_enum=? ";
        if (feeChargesOnly) {
            sql = "select " + rm.chargeSchema()
                    + " where c.is_deleted=0 and c.is_active=1 and c.is_penalty=0 and c.charge_applies_to_enum=? ";
        }
        sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();
        sql += " order by c.name ";

        return this.jdbcTemplate.query(sql, rm, new Object[] { ChargeAppliesTo.SAVINGS.getValue() });
    }

    @Override
    public Collection<ChargeData> retrieveSavingsApplicablePenalties() {
        final ChargeMapper rm = new ChargeMapper();

        String sql = "select " + rm.chargeSchema()
                + " where c.is_deleted=0 and c.is_active=1 and c.is_penalty=1 and c.charge_applies_to_enum=? ";
        sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();
        sql += " order by c.name ";
        return this.jdbcTemplate.query(sql, rm, new Object[] { ChargeAppliesTo.SAVINGS.getValue() });
    }

    @Override
    public Collection<ChargeData> retrieveSavingsProductCharges(final Long savingsProductId) {
        final ChargeMapper rm = new ChargeMapper();

        String sql = "select " + rm.savingsProductChargeSchema() + " where c.is_deleted=0 and c.is_active=1 and spc.savings_product_id=? ";
        sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();

        return this.jdbcTemplate.query(sql, rm, new Object[] { savingsProductId });
    }

    @Override
    public Collection<ChargeData> retrieveShareProductCharges(final Long shareProductId) {
        final ChargeMapper rm = new ChargeMapper();

        String sql = "select " + rm.shareProductChargeSchema() + " where c.is_deleted=0 and c.is_active=1 and mspc.product_id=? ";
        sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();

        return this.jdbcTemplate.query(sql, rm, new Object[] { shareProductId });
    }

    @Override
    public Collection<ChargeData> retrieveSavingsAccountApplicableCharges(final Long savingsAccountId) {

        final ChargeMapper rm = new ChargeMapper();

        String sql = "select " + rm.chargeSchema() + " join m_savings_account sa on sa.currency_code = c.currency_code"
                + " where c.is_deleted=0 and c.is_active=1 and c.charge_applies_to_enum=? " + " and sa.id = ?";
        sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();

        return this.jdbcTemplate.query(sql, rm, new Object[] { ChargeAppliesTo.SAVINGS.getValue(), savingsAccountId });

    }

    @Override
    public Collection<ChargeData> retriveAllChargeOfSavingLateFee() {

        final ChargeMapper rm = new ChargeMapper();

        final String sql = "select " + rm.chargeSchema() + " where c.charge_time_enum =12 and c.is_deleted=0 and c.is_active=1 ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    public Collection<ChargeData> retrieveAllChargesApplicableToClients() {
        final ChargeMapper rm = new ChargeMapper();
        String sql = "select " + rm.chargeSchema() + " where c.is_deleted=0 and c.is_active=1 and c.charge_applies_to_enum=? ";
        sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();
        sql += " order by c.name ";

        return this.jdbcTemplate.query(sql, rm, new Object[] { ChargeAppliesTo.CLIENT.getValue() });
    }

    @Override
    public Collection<ChargeData> retrieveSharesApplicableCharges() {
        final ChargeMapper rm = new ChargeMapper();
        String sql = "select " + rm.chargeSchema() + " where c.is_deleted=0 and c.is_active=1 and c.charge_applies_to_enum=? ";
        sql += addInClauseToSQL_toLimitChargesMappedToOffice_ifOfficeSpecificProductsEnabled();
        sql += " order by c.name ";

        return this.jdbcTemplate.query(sql, rm, new Object[] { ChargeAppliesTo.SHARES.getValue() });
    }
}