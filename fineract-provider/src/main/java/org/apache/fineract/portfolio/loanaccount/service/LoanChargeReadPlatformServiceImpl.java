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
package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.data.ChargeSlabData;
import org.apache.fineract.portfolio.charge.domain.SlabChargeType;
import org.apache.fineract.portfolio.charge.service.ChargeDropdownReadPlatformService;
import org.apache.fineract.portfolio.charge.service.ChargeEnumerations;
import org.apache.fineract.portfolio.charge.service.ChargeSlabReadPlatformService;
import org.apache.fineract.portfolio.common.service.DropdownReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargeData;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargePaidByData;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargeSummaryData;
import org.apache.fineract.portfolio.loanaccount.data.LoanInstallmentChargeData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.tax.data.TaxGroupData;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class LoanChargeReadPlatformServiceImpl implements LoanChargeReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final ChargeDropdownReadPlatformService chargeDropdownReadPlatformService;
    private final DropdownReadPlatformService dropdownReadPlatformService;
    final ChargeSlabReadPlatformService chargeSlabReadPlatformService;

    @Autowired
    public LoanChargeReadPlatformServiceImpl(final PlatformSecurityContext context,
            final ChargeDropdownReadPlatformService chargeDropdownReadPlatformService, final RoutingDataSource dataSource,
            final DropdownReadPlatformService dropdownReadPlatformService,
            final ChargeSlabReadPlatformService chargeSlabReadPlatformService) {
        this.context = context;
        this.chargeDropdownReadPlatformService = chargeDropdownReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dropdownReadPlatformService = dropdownReadPlatformService;
        this.chargeSlabReadPlatformService = chargeSlabReadPlatformService;
    }

    private static final class LoanChargeMapper implements RowMapper<LoanChargeData> {

        private final String sqlSchema;

        private LoanChargeMapper() {
            final StringBuilder sb = new StringBuilder(500);
            sb.append("lc.id as id, c.id as chargeId, c.name as name, ");
            sb.append("lc.amount as amountDue, lc.amount_paid_derived as amountPaid, lc.amount_waived_derived as amountWaived, ");
            sb.append("lc.amount_writtenoff_derived as amountWrittenOff, lc.amount_outstanding_derived as amountOutstanding, ");
            sb.append("lc.calculation_percentage as percentageOf, lc.calculation_on_amount as amountPercentageAppliedTo, ");
            sb.append("lc.charge_time_enum as chargeTime, lc.is_penalty as penalty, ");
            sb.append("lc.due_for_collection_as_of_date as dueAsOfDate, lc.charge_calculation_enum as chargeCalculation, ");
            sb.append("lc.charge_payment_mode_enum as chargePaymentMode, lc.is_paid_derived as paid, lc.waived as waied, ");
            sb.append("lc.min_cap as minCap, lc.max_cap as maxCap, ");
            sb.append("lc.charge_amount_or_percentage as amountOrPercentage, c.is_glim_charge isGlimCharge, ");
            sb.append("c.currency_code as currencyCode, oc.name as currencyName, ");
            sb.append("date(ifnull(dd.disbursedon_date,dd.expected_disburse_date)) as disbursementDate, ");
            sb.append(
                    "oc.decimal_places as currencyDecimalPlaces, oc.currency_multiplesof as inMultiplesOf, oc.display_symbol as currencyDisplaySymbol, ");
            sb.append(
                    "oc.internationalized_name_code as currencyNameCode,lc.is_capitalized as isCapitalized,lc.tax_group_id as taxGroupId,pc.is_mandatory as isMandatory ");
            sb.append("from  m_loan ml ");
            sb.append("join m_loan_charge lc on lc.loan_id = ml.id ");
            sb.append("join m_charge c on c.id = lc.charge_id ");
            sb.append("join m_organisation_currency oc on c.currency_code = oc.code ");
            sb.append("left join m_product_loan_charge pc on pc.product_loan_id = ml.product_id and pc.charge_id = c.id ");
            sb.append("LEFT JOIN m_loan_tranche_disbursement_charge dc ON dc.loan_charge_id = lc.id ");
            sb.append("LEFT JOIN m_loan_disbursement_detail dd ON dd.id = dc.disbursement_detail_id ");
            this.sqlSchema = sb.toString();
        }

        public String schema() {
            return this.sqlSchema;
        }

        public String loanChargeWithSlabsSchema() {
            final StringBuilder sb = new StringBuilder(500);
            sb.append("lc.id as id, c.id as chargeId, c.name as name, ");
            sb.append("lc.amount as amountDue, lc.amount_paid_derived as amountPaid, lc.amount_waived_derived as amountWaived, ");
            sb.append("lc.amount_writtenoff_derived as amountWrittenOff, lc.amount_outstanding_derived as amountOutstanding, ");
            sb.append("lc.calculation_percentage as percentageOf, lc.calculation_on_amount as amountPercentageAppliedTo, ");
            sb.append("lc.charge_time_enum as chargeTime, lc.is_penalty as penalty, ");
            sb.append("lc.due_for_collection_as_of_date as dueAsOfDate, lc.charge_calculation_enum as chargeCalculation, ");
            sb.append("lc.charge_payment_mode_enum as chargePaymentMode, lc.is_paid_derived as paid, lc.waived as waied, ");
            sb.append("lc.min_cap as minCap, lc.max_cap as maxCap, ");
            sb.append("cs.id as csid, cs.from_loan_amount as min, cs.to_loan_amount as max, cs.type as type, cs.amount as chargeAmount, ");
            sb.append("lc.charge_amount_or_percentage as amountOrPercentage, c.is_glim_charge isGlimCharge, ");
            sb.append("c.currency_code as currencyCode, oc.name as currencyName, ");
            sb.append("date(ifnull(dd.disbursedon_date,dd.expected_disburse_date)) as disbursementDate, ");
            sb.append(
                    "oc.decimal_places as currencyDecimalPlaces, oc.currency_multiplesof as inMultiplesOf, oc.display_symbol as currencyDisplaySymbol, ");
            sb.append(
                    "oc.internationalized_name_code as currencyNameCode,lc.is_capitalized as isCapitalized,lc.tax_group_id as taxGroupId,pc.is_mandatory as isMandatory ");
            sb.append("from  m_loan ml ");
            sb.append("join m_loan_charge lc on lc.loan_id = ml.id ");
            sb.append("join m_charge c on c.id = lc.charge_id ");
            sb.append("join m_organisation_currency oc on c.currency_code = oc.code ");
            sb.append("left join m_product_loan_charge pc on pc.product_loan_id = ml.product_id and pc.charge_id = c.id ");
            sb.append("left join f_charge_slab cs on cs.charge_id = c.id ");
            sb.append("LEFT JOIN m_loan_tranche_disbursement_charge dc ON dc.loan_charge_id = lc.id ");
            sb.append("LEFT JOIN m_loan_disbursement_detail dd ON dd.id = dc.disbursement_detail_id ");

            return sb.toString();
        }

        @Override
        public LoanChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long chargeId = rs.getLong("chargeId");
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

            LocalDate dueAsOfDate = JdbcSupport.getLocalDate(rs, "dueAsOfDate");

            final int chargeCalculation = rs.getInt("chargeCalculation");
            final EnumOptionData chargeCalculationType = ChargeEnumerations.chargeCalculationType(chargeCalculation);
            final boolean penalty = rs.getBoolean("penalty");

            final int chargePaymentMode = rs.getInt("chargePaymentMode");
            final EnumOptionData paymentMode = ChargeEnumerations.chargePaymentMode(chargePaymentMode);
            final boolean paid = rs.getBoolean("paid");
            final boolean waived = rs.getBoolean("waied");
            final BigDecimal minCap = rs.getBigDecimal("minCap");
            final BigDecimal maxCap = rs.getBigDecimal("maxCap");
            final BigDecimal amountOrPercentage = rs.getBigDecimal("amountOrPercentage");
            final LocalDate disbursementDate = JdbcSupport.getLocalDate(rs, "disbursementDate");
            final boolean isGlimCharge = rs.getBoolean("isGlimCharge");
            final boolean isCapitalized = rs.getBoolean("isCapitalized");
            final Long taxGroupId = rs.getLong("taxGroupId");
            final boolean isMandatory = rs.getBoolean("isMandatory");
            if (disbursementDate != null) {
                dueAsOfDate = disbursementDate;
            }

            return new LoanChargeData(id, chargeId, name, currency, amount, amountPaid, amountWaived, amountWrittenOff, amountOutstanding,
                    chargeTimeType, dueAsOfDate, chargeCalculationType, percentageOf, amountPercentageAppliedTo, penalty, paymentMode, paid,
                    waived, null, minCap, maxCap, amountOrPercentage, null, isGlimCharge, isCapitalized, taxGroupId, isMandatory);
        }
    }

    private static final class LoanChargeMapperDataExtractor implements ResultSetExtractor<Collection<LoanChargeData>> {

        final LoanChargeMapper loanChargeMapper = new LoanChargeMapper();
        final ChargeSlabMapper chargeSlabMapper = new ChargeSlabMapper();
        final ChargeSlabReadPlatformService chargeSlabReadPlatformService;

        private LoanChargeMapperDataExtractor(final ChargeSlabReadPlatformService chargeSlabReadPlatformService) {
            this.chargeSlabReadPlatformService = chargeSlabReadPlatformService;
        }

        public String schema() {
            return this.loanChargeMapper.loanChargeWithSlabsSchema();
        }

        @Override
        public Collection<LoanChargeData> extractData(final ResultSet rs) throws SQLException, DataAccessException {
            final List<LoanChargeData> loanChargeDataList = new ArrayList<>();
            LoanChargeData loanChargeData = null;
            Long loanChargeDataId = null;
            final int rowNum = 0;// index
            while (rs.next()) {
                final Long tempcId = rs.getLong("id");
                if (loanChargeData == null || (loanChargeDataId != null && !loanChargeDataId.equals(tempcId))) {
                    loanChargeDataId = tempcId;
                    loanChargeData = this.loanChargeMapper.mapRow(rs, rowNum);
                    loanChargeDataList.add(loanChargeData);
                }
                final ChargeSlabData chargeSlabData = this.chargeSlabMapper.mapRow(rs, rowNum);
                if (chargeSlabData != null) {
                    chargeSlabData.setSubSlabs(
                            this.chargeSlabReadPlatformService.retrieveAllChargeSubSlabsBySlabChargeId(chargeSlabData.getId()));
                    loanChargeData.addChargeSlabData(chargeSlabData);
                }

            }
            return loanChargeDataList;
        }
    }

    private static final class ChargeSlabMapper implements RowMapper<ChargeSlabData> {

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
    public ChargeData retrieveLoanChargeTemplate() {
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
        final List<EnumOptionData> clientChargeCalculationTypeOptions = null;
        final List<EnumOptionData> clientChargeTimeTypeOptions = null;

        final Collection<EnumOptionData> feeFrequencyOptions = this.dropdownReadPlatformService.retrieveLoanPeriodFrequencyTypeOptions();
        // this field is applicable only for client charges
        final Map<String, List<GLAccountData>> incomeOrLiabilityAccountOptions = null;
        final List<EnumOptionData> shareChargeCalculationTypeOptions = null;
        final List<EnumOptionData> shareChargeTimeTypeOptions = null;
        final Collection<TaxGroupData> taxGroupOptions = null;
        final List<EnumOptionData> glimChargeCalculationOptions = null;
        final List<EnumOptionData> slabChargeTypeOptions = null;
        final Collection<EnumOptionData> percentageTypeOptions = this.chargeDropdownReadPlatformService.retriveChargePercentageTypes();
        final Collection<EnumOptionData> percentagePeriodTypeOptions = this.chargeDropdownReadPlatformService
                .retriveChargePercentagePeriodTypes();
        final Collection<EnumOptionData> penaltyGraceTypeOptions = this.chargeDropdownReadPlatformService.retrivePenaltyGraceTypes();

        return ChargeData.template(null, allowedChargeCalculationTypeOptions, null, allowedChargeTimeOptions, null,
                loansChargeCalculationTypeOptions, loansChargeTimeTypeOptions, savingsChargeCalculationTypeOptions,
                savingsChargeTimeTypeOptions, clientChargeCalculationTypeOptions, clientChargeTimeTypeOptions, feeFrequencyOptions,
                incomeOrLiabilityAccountOptions, taxGroupOptions, shareChargeCalculationTypeOptions, shareChargeTimeTypeOptions,
                glimChargeCalculationOptions, slabChargeTypeOptions, percentageTypeOptions, percentagePeriodTypeOptions,
                penaltyGraceTypeOptions);
    }

    @Override
    public LoanChargeData retrieveLoanChargeDetails(final Long id, final Long loanId) {
        this.context.authenticatedUser();

        final LoanChargeMapper rm = new LoanChargeMapper();

        final String sql = "select " + rm.schema() + " where lc.id=? and lc.loan_id=? group by lc.id";

        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { id, loanId });
    }

    @Override
    public Collection<LoanChargeData> retrieveLoanCharges(final Long loanId) {
        this.context.authenticatedUser();

        final LoanChargeMapperDataExtractor rm = new LoanChargeMapperDataExtractor(this.chargeSlabReadPlatformService);

        final String sql = "select " + rm.schema() + " where lc.loan_id=? AND lc.is_active = 1 "
                + " order by ifnull(lc.due_for_collection_as_of_date,date(ifnull(dd.disbursedon_date,dd.expected_disburse_date))),lc.charge_time_enum ASC, lc.due_for_collection_as_of_date ASC, lc.is_penalty ASC,lc.id ASC ";

        return this.jdbcTemplate.query(sql, rm, new Object[] { loanId });
    }

    @Override
    public Collection<LoanChargeData> retrieveLoanChargesForFeePayment(final Integer paymentMode, final Integer loanStatus) {
        final LoanChargeMapperWithLoanId rm = new LoanChargeMapperWithLoanId();
        final String sql = "select " + rm.schema()
                + "where loan.loan_status_id= ? and lc.charge_payment_mode_enum=? and lc.waived =0 and lc.is_paid_derived=0 and lc.is_active = 1";
        return this.jdbcTemplate.query(sql, rm, new Object[] { loanStatus, paymentMode });
    }

    private static final class LoanChargeMapperWithLoanId implements RowMapper<LoanChargeData> {

        public String schema() {
            return "lc.id as id, lc.due_for_collection_as_of_date as dueAsOfDate, " + "lc.amount_outstanding_derived as amountOutstanding, "
                    + "lc.charge_time_enum as chargeTime, " + "loan.id as loanId " + "from  m_loan_charge lc "
                    + "join m_loan loan on loan.id = lc.loan_id ";
        }

        @Override
        public LoanChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final LocalDate dueAsOfDate = JdbcSupport.getLocalDate(rs, "dueAsOfDate");
            final Long loanId = rs.getLong("loanId");
            final BigDecimal amountOutstanding = rs.getBigDecimal("amountOutstanding");
            final int chargeTime = rs.getInt("chargeTime");
            final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(chargeTime);

            return new LoanChargeData(id, dueAsOfDate, amountOutstanding, chargeTimeType, loanId, null);
        }
    }

    @Override
    public Collection<LoanInstallmentChargeData> retrieveInstallmentLoanCharges(final Long loanChargeId,
            final boolean onlyPaymentPendingCharges) {
        final LoanInstallmentChargeMapper rm = new LoanInstallmentChargeMapper();
        String sql = "select " + rm.schema() + "where lic.loan_charge_id= ? ";
        if (onlyPaymentPendingCharges) {
            sql = sql + "and lic.waived =0 and lic.is_paid_derived=0";
        }
        sql = sql + " order by lsi.installment";
        return this.jdbcTemplate.query(sql, rm, new Object[] { loanChargeId });
    }

    private static final class LoanInstallmentChargeMapper implements RowMapper<LoanInstallmentChargeData> {

        private final String sqlSchema;

        LoanInstallmentChargeMapper() {
            final StringBuilder sb = new StringBuilder(100);
            sb.append(" lsi.installment as installmentNumber, lsi.duedate as dueAsOfDate, ");
            sb.append("lic.amount_outstanding_derived as amountOutstanding,");
            sb.append("lic.amount as  amount, lic.is_paid_derived as paid, ");
            sb.append("lic.amount_waived_derived as amountWaived, lic.waived as waied, ");
            sb.append("lic.amount_sans_tax as amountSansTax, lic.tax_amount as taxAmount ");
            sb.append("from  m_loan_installment_charge lic ");
            sb.append("join m_loan_repayment_schedule lsi on lsi.id = lic.loan_schedule_id ");
            this.sqlSchema = sb.toString();
        }

        public String schema() {
            return this.sqlSchema;
        }

        @Override
        public LoanInstallmentChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Integer installmentNumber = rs.getInt("installmentNumber");
            final LocalDate dueAsOfDate = JdbcSupport.getLocalDate(rs, "dueAsOfDate");
            final BigDecimal amountOutstanding = rs.getBigDecimal("amountOutstanding");
            final BigDecimal amount = rs.getBigDecimal("amount");
            final BigDecimal amountWaived = rs.getBigDecimal("amountWaived");
            final boolean paid = rs.getBoolean("paid");
            final boolean waived = rs.getBoolean("waied");
            final BigDecimal amountSansTax = rs.getBigDecimal("amountSansTax");
            final BigDecimal taxAmount = rs.getBigDecimal("taxAmount");
            return new LoanInstallmentChargeData(installmentNumber, dueAsOfDate, amount, amountOutstanding, amountWaived, paid, waived,
                    amountSansTax, taxAmount);
        }
    }

    @Override
    public Collection<Integer> retrieveOverdueInstallmentChargeFrequencyNumber(final Long loanId, final Long chargeId,
            final Integer periodNumber) {
        String sql = "select oic.frequency_number from m_loan_overdue_installment_charge oic  inner join m_loan_charge lc on lc.id=oic.loan_charge_id inner join m_loan_repayment_schedule rs on rs.id = oic.loan_schedule_id inner join m_loan loan on loan.id=rs.loan_id "
                + "where lc.is_active = 1 and loan.id = ? and rs.installment=?";
        Object[] params = { loanId, periodNumber };
        if (chargeId != null) {
            sql += " and lc.charge_id = ? ";
            params = new Object[] { loanId, periodNumber, chargeId };
        }
        return this.jdbcTemplate.queryForList(sql, Integer.class, params);
    }

    @Override
    public Collection<LoanChargeData> retrieveLoanChargesForAccural(final Long loanId) {

        final LoanChargeAccrualMapper rm = new LoanChargeAccrualMapper();

        final String sql = "select " + rm.schema() + " where lc.loan_id=? AND lc.is_active = 1 group by  lc.id "
                + " order by lc.charge_time_enum ASC, lc.due_for_collection_as_of_date ASC, lc.is_penalty ASC";

        Collection<LoanChargeData> charges = this.jdbcTemplate.query(sql, rm,
                new Object[] { LoanTransactionType.ACCRUAL.getValue(), loanId, loanId });
        charges = updateLoanChargesWithUnrecognizedIncome(loanId, charges);

        final Collection<LoanChargeData> removeCharges = new ArrayList<>();
        for (final LoanChargeData loanChargeData : charges) {
            if (loanChargeData.isInstallmentFee()) {
                removeCharges.add(loanChargeData);
            }
        }
        charges.removeAll(removeCharges);
        for (final LoanChargeData loanChargeData : removeCharges) {
            if (loanChargeData.isInstallmentFee()) {
                final Collection<LoanInstallmentChargeData> installmentChargeDatas = retrieveInstallmentLoanChargesForAccrual(
                        loanChargeData.getId());
                final LoanChargeData modifiedChargeData = new LoanChargeData(loanChargeData, installmentChargeDatas);
                charges.add(modifiedChargeData);
            }
        }

        return charges;
    }

    private static final class LoanChargeAccrualMapper implements RowMapper<LoanChargeData> {

        private final String schemaSql;

        public LoanChargeAccrualMapper() {
            final StringBuilder sb = new StringBuilder(50);
            sb.append("lc.id as id, lc.charge_id as chargeId, ");
            sb.append("lc.amount as amountDue, ");
            sb.append("lc.amount_waived_derived as amountWaived, ");
            sb.append("lc.charge_time_enum as chargeTime, ");
            sb.append(" sum(cp.amount) as amountAccrued, ");
            sb.append("lc.is_penalty as penalty, ");
            sb.append("lc.is_capitalized as isCapitalized, ");
            sb.append("lc.due_for_collection_as_of_date as dueAsOfDate, ");
            sb.append("lc.tax_group_id as taxGroupId ");
            sb.append("from m_loan_charge lc ");
            sb.append("left join (");
            sb.append("select lcp.loan_charge_id, lcp.amount ");
            sb.append("from m_loan_charge_paid_by lcp ");
            sb.append(
                    "inner join m_loan_transaction lt on lt.id = lcp.loan_transaction_id and lt.is_reversed = 0 and lt.transaction_type_enum = ? and lt.loan_id = ?");
            sb.append(") cp on  cp.loan_charge_id= lc.id  ");
            this.schemaSql = sb.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public LoanChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long chargeId = rs.getLong("chargeId");
            final BigDecimal amount = rs.getBigDecimal("amountDue");
            final BigDecimal amountAccrued = rs.getBigDecimal("amountAccrued");
            final BigDecimal amountWaived = rs.getBigDecimal("amountWaived");

            final int chargeTime = rs.getInt("chargeTime");
            final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(chargeTime);

            final LocalDate dueAsOfDate = JdbcSupport.getLocalDate(rs, "dueAsOfDate");
            final boolean penalty = rs.getBoolean("penalty");
            final boolean isCapitalized = rs.getBoolean("isCapitalized");
            final Long taxGroupId = rs.getLong("taxGroupId");
            final boolean isMandatory = false;
            return new LoanChargeData(id, chargeId, dueAsOfDate, chargeTimeType, amount, amountAccrued, amountWaived, penalty,
                    isCapitalized, taxGroupId, isMandatory);
        }
    }

    private Collection<LoanChargeData> updateLoanChargesWithUnrecognizedIncome(final Long loanId,
            final Collection<LoanChargeData> loanChargeDatas) {

        final LoanChargeUnRecognizedIncomeMapper rm = new LoanChargeUnRecognizedIncomeMapper(loanChargeDatas);

        final String sql = "select " + rm.schema() + " where lc.loan_id=? AND lc.is_active = 1 group by  lc.id "
                + " order by lc.charge_time_enum ASC, lc.due_for_collection_as_of_date ASC, lc.is_penalty ASC";

        return this.jdbcTemplate.query(sql, rm, new Object[] { LoanTransactionType.WAIVE_CHARGES.getValue(), loanId, loanId });

    }

    private static final class LoanChargeUnRecognizedIncomeMapper implements RowMapper<LoanChargeData> {

        private final String schemaSql;
        private final Map<Long, LoanChargeData> chargeDataMap;

        public LoanChargeUnRecognizedIncomeMapper(final Collection<LoanChargeData> datas) {
            this.chargeDataMap = new HashMap<>();
            for (final LoanChargeData chargeData : datas) {
                this.chargeDataMap.put(chargeData.getId(), chargeData);
            }

            final StringBuilder sb = new StringBuilder(50);
            sb.append("lc.id as id,  ");
            sb.append(" sum(wt.unrecognized_income_portion) as amountUnrecognized ");
            sb.append(" from m_loan_charge lc ");
            sb.append("left join (");
            sb.append("select cpb.loan_charge_id, lt.unrecognized_income_portion");
            sb.append(" from m_loan_charge_paid_by cpb ");
            sb.append(
                    "inner join m_loan_transaction lt on lt.id = cpb.loan_transaction_id and lt.is_reversed = 0 and lt.transaction_type_enum = ?  and lt.loan_id = ? ");
            sb.append(") wt on  wt.loan_charge_id= lc.id  ");

            this.schemaSql = sb.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public LoanChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final BigDecimal amountUnrecognized = rs.getBigDecimal("amountUnrecognized");

            final LoanChargeData chargeData = this.chargeDataMap.get(id);
            return new LoanChargeData(amountUnrecognized, chargeData);
        }
    }

    private Collection<LoanInstallmentChargeData> retrieveInstallmentLoanChargesForAccrual(final Long loanChargeId) {
        final LoanInstallmentChargeAccrualMapper rm = new LoanInstallmentChargeAccrualMapper();
        final String sql = "select " + rm.schema() + " where lic.loan_charge_id= ?  group by lsi.installment";
        Collection<LoanInstallmentChargeData> chargeDatas = this.jdbcTemplate.query(sql, rm,
                new Object[] { LoanTransactionType.ACCRUAL.getValue(), loanChargeId, loanChargeId });
        final Map<Integer, LoanInstallmentChargeData> installmentChargeDatas = new HashMap<>();
        for (final LoanInstallmentChargeData installmentChargeData : chargeDatas) {
            installmentChargeDatas.put(installmentChargeData.getInstallmentNumber(), installmentChargeData);
        }
        chargeDatas = updateInstallmentLoanChargesWithUnrecognizedIncome(loanChargeId, installmentChargeDatas);
        for (final LoanInstallmentChargeData installmentChargeData : chargeDatas) {
            installmentChargeDatas.put(installmentChargeData.getInstallmentNumber(), installmentChargeData);
        }
        return installmentChargeDatas.values();

    }

    private static final class LoanInstallmentChargeAccrualMapper implements RowMapper<LoanInstallmentChargeData> {

        private final String schemaSql;

        public LoanInstallmentChargeAccrualMapper() {
            final StringBuilder sb = new StringBuilder(50);
            sb.append(" lsi.installment as installmentNumber, lsi.duedate as dueAsOfDate, ");
            sb.append("lic.amount_outstanding_derived as amountOutstanding,");
            sb.append("lic.amount as  amount, ");
            sb.append("lic.is_paid_derived as paid, ");
            sb.append("lic.amount_waived_derived as amountWaived, ");
            sb.append(" sum(cp.amount) as amountAccrued, ");
            sb.append("lic.waived as waied, ");
            sb.append("lic.amount_sans_tax as amountSansTax, lic.tax_amount as taxAmount ");
            sb.append("from  m_loan_installment_charge lic ");
            sb.append("join m_loan_repayment_schedule lsi on lsi.id = lic.loan_schedule_id ");
            sb.append("left join (");
            sb.append("select lcp.loan_charge_id, lcp.amount as amount, lcp.installment_number ");
            sb.append(" from m_loan_charge_paid_by lcp ");
            sb.append(
                    "inner join m_loan_transaction lt on lt.id = lcp.loan_transaction_id and lt.is_reversed = 0 and lt.transaction_type_enum = ? and lcp.loan_charge_id = ? ");
            sb.append(") cp on  cp.loan_charge_id= lic.loan_charge_id and  cp.installment_number = lsi.installment ");
            this.schemaSql = sb.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public LoanInstallmentChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Integer installmentNumber = rs.getInt("installmentNumber");
            final LocalDate dueAsOfDate = JdbcSupport.getLocalDate(rs, "dueAsOfDate");
            final BigDecimal amountOutstanding = rs.getBigDecimal("amountOutstanding");
            final BigDecimal amount = rs.getBigDecimal("amount");
            final BigDecimal amountWaived = rs.getBigDecimal("amountWaived");
            final boolean paid = rs.getBoolean("paid");
            final boolean waived = rs.getBoolean("waied");
            final BigDecimal amountAccrued = rs.getBigDecimal("amountAccrued");
            final BigDecimal amountSansTax = rs.getBigDecimal("amountSansTax");
            final BigDecimal taxAmount = rs.getBigDecimal("taxAmount");
            return new LoanInstallmentChargeData(installmentNumber, dueAsOfDate, amount, amountOutstanding, amountWaived, paid, waived,
                    amountAccrued, amountSansTax, taxAmount);
        }
    }

    private Collection<LoanInstallmentChargeData> updateInstallmentLoanChargesWithUnrecognizedIncome(final Long loanChargeId,
            final Map<Integer, LoanInstallmentChargeData> installmentChargeDatas) {
        final LoanInstallmentChargeUnRecognizedIncomeMapper rm = new LoanInstallmentChargeUnRecognizedIncomeMapper(installmentChargeDatas);
        final String sql = "select " + rm.schema() + " where cpb.loan_charge_id = ? group by cpb.installment_number  ";
        return this.jdbcTemplate.query(sql, rm, new Object[] { LoanTransactionType.WAIVE_CHARGES.getValue(), loanChargeId });
    }

    private static final class LoanInstallmentChargeUnRecognizedIncomeMapper implements RowMapper<LoanInstallmentChargeData> {

        private final String schemaSql;
        private final Map<Integer, LoanInstallmentChargeData> installmentChargeDatas;

        public LoanInstallmentChargeUnRecognizedIncomeMapper(final Map<Integer, LoanInstallmentChargeData> installmentChargeDatas) {
            this.installmentChargeDatas = installmentChargeDatas;
            final StringBuilder sb = new StringBuilder(50);
            sb.append(" cpb.installment_number as installmentNumber, ");
            sb.append("  sum(lt.unrecognized_income_portion) as amountUnrecognized ");
            sb.append(" from m_loan_charge_paid_by cpb ");
            sb.append(
                    "inner join m_loan_transaction lt on lt.id = cpb.loan_transaction_id and lt.is_reversed = 0 and lt.transaction_type_enum = ?");
            this.schemaSql = sb.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public LoanInstallmentChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Integer installmentNumber = rs.getInt("installmentNumber");
            final BigDecimal amountUnrecognized = rs.getBigDecimal("amountUnrecognized");
            final LoanInstallmentChargeData installmentChargeData = this.installmentChargeDatas.get(installmentNumber);
            return new LoanInstallmentChargeData(installmentChargeData, amountUnrecognized);
        }
    }

    @Override
    public Collection<LoanChargePaidByData> retriveLoanChargesPaidBy(final Long chargeId, final LoanTransactionType transactionType,
            final Integer installmentNumber) {

        final LoanChargesPaidByMapper rm = new LoanChargesPaidByMapper();
        final StringBuilder sb = new StringBuilder(100);
        sb.append("select ");
        sb.append(rm.schema());
        sb.append(" where lcp.loan_charge_id = ?");
        final List<Object> args = new ArrayList<>(3);
        args.add(chargeId);
        if (transactionType != null) {
            sb.append(" and lt.transaction_type_enum = ?");
            args.add(transactionType.getValue());
        }
        if (installmentNumber != null) {
            sb.append(" and lcp.installment_number = ?");
            args.add(installmentNumber);
        }

        return this.jdbcTemplate.query(sb.toString(), rm, args.toArray());
    }

    private static final class LoanChargesPaidByMapper implements RowMapper<LoanChargePaidByData> {

        private final String schemaSql;

        public LoanChargesPaidByMapper() {
            final StringBuilder sb = new StringBuilder(100);
            sb.append("lcp.id as id, lcp.loan_charge_id as chargeId, ");
            sb.append("lcp.amount as amount, ");
            sb.append("lcp.loan_transaction_id as transactionId, ");
            sb.append("lcp.installment_number as installmentNumber ");
            sb.append(" from m_loan_charge_paid_by lcp ");
            sb.append(" join m_loan_transaction lt on lt.id = lcp.loan_transaction_id and lt.is_reversed=0");

            this.schemaSql = sb.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public LoanChargePaidByData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long chargeId = rs.getLong("chargeId");
            final BigDecimal amount = rs.getBigDecimal("amount");
            final Long transactionId = rs.getLong("transactionId");
            final Integer installmentNumber = rs.getInt("installmentNumber");

            return new LoanChargePaidByData(id, amount, installmentNumber, chargeId, transactionId);
        }
    }

    private static final class LoanChargeByClientIdMapper implements RowMapper<LoanChargeSummaryData> {

        private final String schemaSql;

        public LoanChargeByClientIdMapper() {
            final StringBuilder sb = new StringBuilder(100);
            sb.append("c.name as chargeName,mlc.amount_outstanding_derived as ChargeDue, ");
            sb.append("l.id as loanId ,l.account_no as accountNo,mlc.id as chargeId, ");
            sb.append("ifnull(due_for_collection_as_of_date,curdate())as charge_due_date ");
            sb.append("from m_client m inner join m_loan l on  l.client_id = m.id ");
            sb.append("inner join m_loan_charge mlc on l.id = mlc.loan_id ");
            sb.append("inner join m_charge c on c.id = mlc.charge_id ");
            this.schemaSql = sb.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public LoanChargeSummaryData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final String chargeName = rs.getString("chargeName");
            final BigDecimal chargeDue = rs.getBigDecimal("ChargeDue");
            final Long id = rs.getLong("loanId");
            final String accountNo = rs.getString("accountNo");
            final Long chargeId = rs.getLong("chargeId");
            final String date = rs.getString("charge_due_date");
            return new LoanChargeSummaryData(id, accountNo, chargeName, chargeDue, chargeId, date);
        }

    }

    @Override
    public Collection<LoanChargeSummaryData> retriveLoanCharge(final Long loanId, final String chargeonDate) {

        final LoanChargeByClientIdMapper rm = new LoanChargeByClientIdMapper();
        final StringBuilder sb = new StringBuilder(100);
        sb.append("select a.chargeName,a.ChargeDue,a.loanId,a.accountNo,a.chargeId,a.charge_due_date from (");
        sb.append("select ");
        sb.append(rm.schema());
        sb.append(" where mlc.loan_id = ? ");
        sb.append(" and mlc.is_paid_derived=0");
        sb.append(" and mlc.waived=0");
        sb.append(" and mlc.is_active=1)a");
        sb.append(" where a.charge_due_date <= ?");
        return this.jdbcTemplate.query(sb.toString(), rm, new Object[] { loanId, chargeonDate });
    }

}