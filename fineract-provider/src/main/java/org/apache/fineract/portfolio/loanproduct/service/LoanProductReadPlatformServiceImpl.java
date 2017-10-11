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
package org.apache.fineract.portfolio.loanproduct.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.fineract.accounting.common.AccountingEnumerations;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.entityaccess.domain.FineractEntityType;
import org.apache.fineract.infrastructure.entityaccess.service.FineractEntityAccessUtil;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.data.ChargeSlabData;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.apache.fineract.portfolio.charge.service.ChargeSlabReadPlatformService;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.LegalForm;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.portfolio.common.service.CommonEnumerations;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductBorrowerCycleVariationData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductEntityProfileMappingData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductGuaranteeData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductInterestRecalculationData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductTemplateData;
import org.apache.fineract.portfolio.loanproduct.data.ProductLoanChargeData;
import org.apache.fineract.portfolio.loanproduct.domain.ClientProfileType;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductApplicableForLoanType;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductConfigurableAttributes;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductParamType;
import org.apache.fineract.portfolio.loanproduct.domain.ValueEntityType;
import org.apache.fineract.portfolio.loanproduct.domain.WeeksInYearType;
import org.apache.fineract.portfolio.loanproduct.exception.LoanProductInactiveException;
import org.apache.fineract.portfolio.loanproduct.exception.LoanProductNotFoundException;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class LoanProductReadPlatformServiceImpl implements LoanProductReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final FineractEntityAccessUtil fineractEntityAccessUtil;
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private final ClientReadPlatformService clientReadPlatformService;
    final ChargeSlabReadPlatformService chargeSlabReadPlatformService;

    @Autowired
    public LoanProductReadPlatformServiceImpl(final PlatformSecurityContext context,
            final ChargeReadPlatformService chargeReadPlatformService, final RoutingDataSource dataSource,
            final FineractEntityAccessUtil fineractEntityAccessUtil, final ClientReadPlatformService clientReadPlatformService,
            final ChargeSlabReadPlatformService chargeSlabReadPlatformService) {
        this.context = context;
        this.chargeReadPlatformService = chargeReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.fineractEntityAccessUtil = fineractEntityAccessUtil;
        this.clientReadPlatformService = clientReadPlatformService;
        this.chargeSlabReadPlatformService = chargeSlabReadPlatformService;
    }

    @Override
    public LoanProductData retrieveLoanProduct(final Long loanProductId) {
        try {
            final Collection<ProductLoanChargeData> charges = retrieveProductLoanCharges(loanProductId);
            final Collection<LoanProductBorrowerCycleVariationData> borrowerCycleVariationDatas = retrieveLoanProductBorrowerCycleVariations(
                    loanProductId);
            final LoanProductMapper rm = new LoanProductMapper(charges, borrowerCycleVariationDatas);
            final String sql = "select " + rm.loanProductSchema() + " where lp.id = ?";
            final LoanProductData loanProductData = this.jdbcTemplate.queryForObject(sql, rm, new Object[] { loanProductId });
            final Collection<LoanProductEntityProfileMappingData> loanProductEntityProfileMappingDatas = retrieveLoanProductEntityProfileMappingData(
                    loanProductId);
            loanProductData.setLoanProductEntityProfileMappingDatas(loanProductEntityProfileMappingDatas);
            return loanProductData;
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanProductNotFoundException(loanProductId);
        }
    }

    private Collection<LoanProductEntityProfileMappingData> retrieveLoanProductEntityProfileMappingData(final Long loanProductId) {
        final LoanProductEntityProfileMappingDataMapper rm = new LoanProductEntityProfileMappingDataMapper();
        final String sql = "select " + rm.schema() + " where lpepm.loan_product_id = ? ";
        return this.jdbcTemplate.query(sql, rm, new Object[] { loanProductId });
    }

    private static final class LoanProductEntityProfileMappingDataMapper
            implements ResultSetExtractor<Collection<LoanProductEntityProfileMappingData>> {

        private final String schemaSql;

        public LoanProductEntityProfileMappingDataMapper() {
            final StringBuilder sb = new StringBuilder(100);
            sb.append(
                    "lpepm.id as id, lpepm.profile_type as profileType, lpepm.value as value, lpepm.value_entity_type as valueEntityType ");
            sb.append(",cv.code_value as valueName ");
            sb.append("from f_loan_product_entity_profile_mapping lpepm ");
            sb.append("left join m_code_value cv ON cv.id = lpepm.value and lpepm.value_entity_type = 2 ");
            this.schemaSql = sb.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public Collection<LoanProductEntityProfileMappingData> extractData(final ResultSet rs) throws SQLException, DataAccessException {
            final Collection<LoanProductEntityProfileMappingData> loanProductEntityProfileMappingDatas = new ArrayList<>();
            LoanProductEntityProfileMappingData loanProductEntityProfileMappingData = null;
            Integer profileTypeId = null;
            ClientProfileType clientProfileType = null;
            while (rs.next()) {
                final Long id = null;
                final Integer tempProfileTypeId = JdbcSupport.getInteger(rs, "profileType");
                final Long valueId = JdbcSupport.getLong(rs, "value");
                final Integer valueEntityTypeId = JdbcSupport.getInteger(rs, "valueEntityType");
                final ValueEntityType valueEntityType = ValueEntityType.fromInt(valueEntityTypeId);
                if (loanProductEntityProfileMappingData == null || (profileTypeId != null && !profileTypeId.equals(tempProfileTypeId))) {
                    profileTypeId = tempProfileTypeId;
                    clientProfileType = ClientProfileType.fromInt(profileTypeId);
                    final EnumOptionData profileType = ClientProfileType.type(clientProfileType);
                    final Collection<EnumOptionData> values = new ArrayList<>();
                    loanProductEntityProfileMappingData = LoanProductEntityProfileMappingData.instance(id, profileType, values);
                    loanProductEntityProfileMappingDatas.add(loanProductEntityProfileMappingData);
                }
                if (clientProfileType != null) {
                    EnumOptionData value = null;
                    if (valueEntityType.isEnumValue()) {
                        if (clientProfileType.isLegalForm()) {
                            value = LegalForm.legalFormType(valueId.intValue());
                        }
                    } else if (valueEntityType.isCodeValue()) {
                        final String valueName = rs.getString("valueName");
                        final String code = null;
                        value = new EnumOptionData(valueId, code, valueName);
                    }
                    if (value != null) {
                        loanProductEntityProfileMappingData.addValue(value);
                    }
                }
            }
            return loanProductEntityProfileMappingDatas;
        }
    }

    private Collection<ProductLoanChargeData> retrieveProductLoanCharges(final Long loanProductId) {
        final ProductLoanChargeMapper rm = new ProductLoanChargeMapper(this.chargeReadPlatformService, this.chargeSlabReadPlatformService);
        final String sql = "SELECT " + rm.schema() + " WHERE plc.product_loan_id=? ";

        return this.jdbcTemplate.query(sql, rm, new Object[] { loanProductId });
    }

    @Override
    public Collection<LoanProductBorrowerCycleVariationData> retrieveLoanProductBorrowerCycleVariations(final Long loanProductId) {
        final LoanProductBorrowerCycleMapper rm = new LoanProductBorrowerCycleMapper();
        final String sql = "select " + rm.schema() + " where bc.loan_product_id=?  order by bc.borrower_cycle_number,bc.value_condition";
        return this.jdbcTemplate.query(sql, rm, new Object[] { loanProductId });
    }

    @Override
    public Collection<LoanProductData> retrieveAllLoanProducts() {

        this.context.authenticatedUser();

        final LoanProductMapper rm = new LoanProductMapper(null, null);

        String sql = "select " + rm.loanProductSchema();

        // Check if branch specific products are enabled. If yes, fetch only
        // products mapped to current user's office
        final String inClause = this.fineractEntityAccessUtil
                .getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled(FineractEntityType.LOAN_PRODUCT);
        if ((inClause != null) && (!(inClause.trim().isEmpty()))) {
            sql += " where lp.id in ( " + inClause + " ) ";
        }

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    public Collection<LoanProductData> retrieveAllLoanProductsForLookup() {
        return retrieveAllLoanProductsForLookup(false);
    }

    @Override
    public Collection<LoanProductData> retrieveAllLoanProductsForLookup(final boolean activeOnly) {
        this.context.authenticatedUser();

        final LoanProductLookupMapper rm = new LoanProductLookupMapper();
        String sql = "select ";
        if (activeOnly) {
            sql += rm.activeOnlySchema();
        } else {
            sql += rm.schema();
        }
        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    public Collection<LoanProductData> retrieveAllLoanProductsForLookup(final String inClause) {

        this.context.authenticatedUser();

        final LoanProductLookupMapper rm = new LoanProductLookupMapper();

        String sql = "select " + rm.schema();

        if ((inClause != null) && (!(inClause.trim().isEmpty()))) {
            sql += " where lp.id in ( " + inClause + " ) ";
        }

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    public Collection<LoanProductData> retrieveAllLoanProductsForLookup(final Integer productApplicableForLoanType,
            final Integer entityType, final Long entityId) {
        final boolean activeOnly = false;
        return retrieveAllLoanProductsForLookup(activeOnly, productApplicableForLoanType, entityType, entityId);
    }

    @Override
    public Collection<LoanProductData> retrieveAllLoanProductsForLookup(final boolean activeOnly,
            final Integer productApplicableForLoanType, final Integer entityType, final Long entityId) {
        this.context.authenticatedUser();

        final StringJoiner profileType = new StringJoiner(",");
        final StringJoiner profileTypeEnumValues = new StringJoiner(",");
        final StringJoiner profileTypeCodeValues = new StringJoiner(",");
        if (productApplicableForLoanType != null && entityType != null && entityId != null) {
            final EntityType entityTypeEnum = EntityType.fromInt(entityType);
            if (entityTypeEnum.isClient()) {
                final ClientData clientData = this.clientReadPlatformService.retrieveOne(entityId);
                if (clientData.getLegalForm() != null) {
                    profileType.add(ClientProfileType.LEGAL_FORM.getValue().toString());
                    if (LegalForm.ENTITY.getValue().equals(clientData.getLegalForm().getId().intValue())) {
                        profileTypeEnumValues.add(LegalForm.ENTITY.getValue().toString());
                    } else if (LegalForm.PERSON.getValue().equals(clientData.getLegalForm().getId().intValue())) {
                        profileTypeEnumValues.add(LegalForm.PERSON.getValue().toString());
                    }
                }
                if (clientData.getClientType() != null && clientData.getClientType().getId() != null) {
                    profileType.add(ClientProfileType.CLIENT_TYPE.getValue().toString());
                    profileTypeCodeValues.add(clientData.getClientType().getId().toString());
                }
                if (clientData.getClientClassification() != null && clientData.getClientClassification().getId() != null) {
                    profileType.add(ClientProfileType.CLIENT_CLASSIFICATION.getValue().toString());
                    profileTypeCodeValues.add(clientData.getClientClassification().getId().toString());
                }
            }
        }

        final LoanProductLookupMapper rm = new LoanProductLookupMapper();
        final String currentdate = this.formatter.print(DateUtils.getLocalDateOfTenant());
        String sql = "select ";
        final List<Object> params = new ArrayList<>();
        sql += rm.schema();
        sql += constructSqlQueryBasedOnTheParametersForLoanProducts(productApplicableForLoanType, profileType, profileTypeEnumValues,
                profileTypeCodeValues);
        if (activeOnly) {
            params.add(currentdate);
            if (productApplicableForLoanType == null) {
                sql += " where (close_date is null or close_date >= ? )";
            } else {
                sql += " and (close_date is null or close_date >= ? )";
            }
        }

        // Check if branch specific products are enabled. If yes, fetch only
        // products mapped to current user's office
        final String inClause = this.fineractEntityAccessUtil
                .getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled(FineractEntityType.LOAN_PRODUCT);
        if ((inClause != null) && (!(inClause.trim().isEmpty()))) {
            if (activeOnly) {
                sql += " and lp.id in ( " + inClause + " )";
            } else {
                if (productApplicableForLoanType == null) {
                    sql += " where lp.id in ( " + inClause + " ) ";
                } else {
                    sql += " and lp.id in ( " + inClause + " ) ";
                }
            }
        }
        return this.jdbcTemplate.query(sql, rm, params.toArray());
    }

    private String constructSqlQueryBasedOnTheParametersForLoanProducts(final Integer productApplicableForLoanType,
            final StringJoiner profileType, final StringJoiner profileTypeEnumValues, final StringJoiner profileTypeCodeValues) {
        final StringBuilder sql = new StringBuilder(100);
        sql.append("");
        if (productApplicableForLoanType != null
                && productApplicableForLoanType.equals(LoanProductApplicableForLoanType.INDIVIDUAL_CLIENT.getValue())) {
            sql.append(" left join f_loan_product_entity_profile_mapping lpepm on lpepm.loan_product_id = lp.id ");
        }
        if (productApplicableForLoanType != null) {
            sql.append(" where lp.applicable_for_loan_type in ( " + LoanProductApplicableForLoanType.ALL_TYPES.getValue() + ","
                    + productApplicableForLoanType + " ) ");
            if (productApplicableForLoanType.equals(LoanProductApplicableForLoanType.INDIVIDUAL_CLIENT.getValue())) {
                if (profileType != null && profileType.toString().length() > 0) {
                    sql.append("and (lpepm.loan_product_id is null or lpepm.profile_type in ( " + profileType.toString() + " )) ");
                    if (profileTypeEnumValues != null && profileTypeEnumValues.toString().length() > 0 && profileTypeCodeValues != null
                            && profileTypeCodeValues.toString().length() > 0) {
                        sql.append("and ( ");
                        sql.append("lpepm.loan_product_id is null or ");
                        sql.append("( ");
                        sql.append("(lpepm.value in ( " + profileTypeEnumValues.toString() + " ) and lpepm.value_entity_type = "
                                + ValueEntityType.ENUM_VALUE.getValue() + ") ");
                        sql.append("or ");
                        sql.append("(lpepm.value in (" + profileTypeCodeValues.toString() + ") and lpepm.value_entity_type = "
                                + ValueEntityType.CODE_VALUE.getValue() + ") ");
                        sql.append(")");
                        sql.append(")");

                    } else if (profileTypeEnumValues != null && profileTypeEnumValues.toString().length() > 0) {
                        sql.append("and ( ");
                        sql.append("lpepm.loan_product_id is null or ");
                        sql.append("(lpepm.value in ( " + profileTypeEnumValues.toString() + " ) and lpepm.value_entity_type = "
                                + ValueEntityType.ENUM_VALUE.getValue() + ") ");
                        sql.append(")");
                    } else if (profileTypeCodeValues != null && profileTypeCodeValues.toString().length() > 0) {
                        sql.append("and ( ");
                        sql.append("lpepm.loan_product_id is null or ");
                        sql.append("(lpepm.value in ( " + profileTypeCodeValues.toString() + " ) and lpepm.value_entity_type = "
                                + ValueEntityType.CODE_VALUE.getValue() + ") ");
                        sql.append(")");
                    }
                } else {
                    sql.append(" and lpepm.loan_product_id is null ");
                }
            }
        }
        return sql.toString();
    }

    @Override
    public LoanProductData retrieveNewLoanProductDetails() {
        return LoanProductData.sensibleDefaultsForNewLoanProductCreation();
    }

    private static final class LoanProductMapper implements RowMapper<LoanProductData> {

        private final Collection<ProductLoanChargeData> charges;

        private final Collection<LoanProductBorrowerCycleVariationData> borrowerCycleVariationDatas;

        private final String schemaSql;

        public LoanProductMapper(final Collection<ProductLoanChargeData> charges,
                final Collection<LoanProductBorrowerCycleVariationData> borrowerCycleVariationDatas) {
            this.charges = charges;
            this.borrowerCycleVariationDatas = borrowerCycleVariationDatas;

            final StringBuilder sb = new StringBuilder(2000);
            sb.append(
                    "lp.id as id, lp.fund_id as fundId, f.name as fundName, lp.loan_transaction_strategy_id as transactionStrategyId, ltps.name as transactionStrategyName, ");
            sb.append("lp.name as name, lp.short_name as shortName, lp.description as description, ");
            sb.append(
                    "lp.principal_amount as principal, lp.min_principal_amount as minPrincipal, lp.max_principal_amount as maxPrincipal, lp.currency_code as currencyCode, lp.currency_digits as currencyDigits, lp.currency_multiplesof as inMultiplesOf, ");
            sb.append(
                    "lp.nominal_interest_rate_per_period as interestRatePerPeriod, lp.min_nominal_interest_rate_per_period as minInterestRatePerPeriod, lp.max_nominal_interest_rate_per_period as maxInterestRatePerPeriod, lp.interest_period_frequency_enum as interestRatePerPeriodFreq,lp.interest_rates_list_per_period as interestRatesListPerPeriod, ");
            sb.append(
                    "lp.annual_nominal_interest_rate as annualInterestRate, lp.interest_method_enum as interestMethod, lp.interest_calculated_in_period_enum as interestCalculationInPeriodMethod,lp.allow_partial_period_interest_calcualtion as allowPartialPeriodInterestCalcualtion, ");
            sb.append(
                    "lp.repay_every as repaidEvery, lp.repayment_period_frequency_enum as repaymentPeriodFrequency, lp.number_of_repayments as numberOfRepayments, lp.min_number_of_repayments as minNumberOfRepayments, lp.max_number_of_repayments as maxNumberOfRepayments, ");
            sb.append(
                    "lp.grace_on_principal_periods as graceOnPrincipalPayment, lp.recurring_moratorium_principal_periods as recurringMoratoriumOnPrincipalPeriods, lp.grace_on_interest_periods as graceOnInterestPayment, lp.grace_interest_free_periods as graceOnInterestCharged,lp.grace_on_arrears_ageing as graceOnArrearsAgeing,lp.overdue_days_for_npa as overdueDaysForNPA, ");
            sb.append(
                    "lp.min_days_between_disbursal_and_first_repayment As minimumDaysBetweenDisbursalAndFirstRepayment, lp.min_duration_applicable_for_all_disbursements as isMinDurationApplicableForAllDisbursements,");
            sb.append("lp.amortization_method_enum as amortizationMethod, lp.arrearstolerance_amount as tolerance, ");
            sb.append("lp.pmt_calculated_in_period_enum as installmentCalculationPeriodType,");
            sb.append(
                    "lp.accounting_type as accountingType, lp.include_in_borrower_cycle as includeInBorrowerCycle,lp.use_borrower_cycle as useBorrowerCycle, lp.start_date as startDate, lp.close_date as closeDate,  ");
            sb.append(
                    "lp.allow_multiple_disbursals as multiDisburseLoan, lp.max_disbursals as maxTrancheCount, lp.max_outstanding_loan_balance as outstandingLoanBalance, ");
            sb.append(
                    "lp.days_in_month_enum as daysInMonth,lp.weeks_in_year_enum as weeksInYearType , lp.days_in_year_enum as daysInYear, lp.interest_recalculation_enabled as isInterestRecalculationEnabled, ");
            sb.append(
                    "lp.can_define_fixed_emi_amount as canDefineInstallmentAmount, lp.instalment_amount_in_multiples_of as installmentAmountInMultiplesOf, ");
            sb.append("lp.is_flat_interest_rate as isFlatInterestRate,");
            sb.append("lpr.pre_close_interest_calculation_strategy as preCloseInterestCalculationStrategy, ");
            sb.append(
                    "lpr.id as lprId, lpr.product_id as productId, lpr.compound_type_enum as compoundType, lpr.reschedule_strategy_enum as rescheduleStrategy, ");
            sb.append("lpr.rest_frequency_type_enum as restFrequencyEnum, lpr.rest_frequency_interval as restFrequencyInterval, ");
            sb.append("lpr.rest_frequency_nth_day_enum as restFrequencyNthDayEnum, ");
            sb.append("lpr.rest_frequency_weekday_enum as restFrequencyWeekDayEnum, ");
            sb.append("lpr.rest_frequency_on_day as restFrequencyOnDay, ");
            sb.append("lpr.arrears_based_on_original_schedule as isArrearsBasedOnOriginalSchedule, ");
            sb.append(
                    "lpr.compounding_frequency_type_enum as compoundingFrequencyTypeEnum, lpr.compounding_frequency_interval as compoundingInterval, ");
            sb.append("lpr.compounding_frequency_nth_day_enum as compoundingFrequencyNthDayEnum, ");
            sb.append("lpr.compounding_frequency_weekday_enum as compoundingFrequencyWeekDayEnum, ");
            sb.append("lpr.compounding_frequency_on_day as compoundingFrequencyOnDay, ");
            sb.append("lpr.is_compounding_to_be_posted_as_transaction as isCompoundingToBePostedAsTransaction, ");
            sb.append("lpr.allow_compounding_on_eod as allowCompoundingOnEod, ");
            sb.append("lp.hold_guarantee_funds as holdGuaranteeFunds, ");
            sb.append("lp.principal_threshold_for_last_installment as principalThresholdForLastInstallment, ");
            sb.append("sync_expected_with_disbursement_date as syncExpectedWithDisbursementDate, ");
            sb.append("lp.min_periods_between_disbursal_and_first_repayment as minimumPeriodsBetweenDisbursalAndFirstRepayment, ");
            sb.append("lp.min_loan_term as minLoanTerm, lp.max_loan_term as maxLoanterm ,");
            sb.append("lp.loan_tenure_frequency_type as loanTenureFrequencyType , ");
            sb.append("lp.emi_based_on_disbursements as isEmiBasedOnDisbursements , ");
            sb.append("lpg.id as lpgId, lpg.mandatory_guarantee as mandatoryGuarantee, ");
            sb.append(
                    "lpg.minimum_guarantee_from_own_funds as minimumGuaranteeFromOwnFunds, lpg.minimum_guarantee_from_guarantor_funds as minimumGuaranteeFromGuarantor, ");
            sb.append("lp.account_moves_out_of_npa_only_on_arrears_completion as accountMovesOutOfNPAOnlyOnArrearsCompletion, ");
            sb.append(
                    "curr.name as currencyName, curr.internationalized_name_code as currencyNameCode, curr.display_symbol as currencyDisplaySymbol, lp.external_id as externalId, ");
            sb.append(
                    "lca.id as lcaId, lca.amortization_method_enum as amortizationBoolean, lca.interest_method_enum as interestMethodConfigBoolean, ");
            sb.append(
                    "lca.loan_transaction_strategy_id as transactionProcessingStrategyBoolean,lca.interest_calculated_in_period_enum as interestCalcPeriodBoolean, lca.arrearstolerance_amount as arrearsToleranceBoolean, ");
            sb.append(
                    "lca.repay_every as repaymentFrequencyBoolean, lca.moratorium as graceOnPrincipalAndInterestBoolean, lca.grace_on_arrears_ageing as graceOnArrearsAgingBoolean, ");
            sb.append("lp.is_linked_to_floating_interest_rates as isLinkedToFloatingInterestRates, ");
            sb.append(
                    "lp.broken_period_method_enum as brokenPeriodMethodType, lp.allow_negative_loan_balances as allowNegativeLoanBalance, ");
            sb.append(
                    "lp.consider_future_disbursements_in_schedule as considerFutureDisbursementsInSchedule, lp.consider_all_disbursements_in_schedule as considerAllDisbursementsInSchedule,");
            sb.append("lfr.floating_rates_id as floatingRateId, ");
            sb.append("fr.name as floatingRateName, ");
            sb.append("lfr.interest_rate_differential as interestRateDifferential, ");
            sb.append("lfr.min_differential_lending_rate as minDifferentialLendingRate, ");
            sb.append("lfr.default_differential_lending_rate as defaultDifferentialLendingRate, ");
            sb.append("lfr.max_differential_lending_rate as maxDifferentialLendingRate, ");
            sb.append("lfr.is_floating_interest_rate_calculation_allowed as isFloatingInterestRateCalculationAllowed, ");
            sb.append("lp.allow_variabe_installments as isVariableIntallmentsAllowed, ");
            sb.append("lvi.minimum_gap as minimumGap, ");
            sb.append(
                    "lvi.maximum_gap as maximumGap, lpr.is_subsidy_applicable AS isSubsidyApplicable, lp.close_loan_on_overpayment as closeLoanOnOverpayment, ");
            sb.append(
                    "lp.adjusted_instalment_in_multiples_of as adjustedInstallmentInMultiplesOf, lp.adjust_first_emi_amount as adjustFirstEMIAmount, ");
            sb.append("lp.can_use_for_topup as canUseForTopup ,lp.adjust_interest_for_rounding AS adjustInterestForRounding ,");
            sb.append(
                    "lp.allow_upfront_collection as allowUpfrontCollection, lp.percentage_of_disbursement_to_be_transferred as percentageOfDisbursementToBeTransferred");
            sb.append(",lp.applicable_for_loan_type as applicableForLoanType ");
            sb.append(" from m_product_loan lp ");
            sb.append(" left join m_fund f on f.id = lp.fund_id ");
            sb.append(" left join m_product_loan_recalculation_details lpr on lpr.product_id=lp.id ");
            sb.append(" left join m_product_loan_guarantee_details lpg on lpg.loan_product_id=lp.id ");
            sb.append(" left join ref_loan_transaction_processing_strategy ltps on ltps.id = lp.loan_transaction_strategy_id");
            sb.append(" left join m_product_loan_configurable_attributes lca on lca.loan_product_id = lp.id ");
            sb.append(" left join m_product_loan_floating_rates as lfr on lfr.loan_product_id = lp.id ");
            sb.append(" left join m_floating_rates as fr on lfr.floating_rates_id = fr.id ");
            sb.append(" left join m_product_loan_variable_installment_config as lvi on lvi.loan_product_id = lp.id ");
            sb.append(" join m_currency curr on curr.code = lp.currency_code");

            this.schemaSql = sb.toString();
        }

        public String loanProductSchema() {
            return this.schemaSql;
        }

        @Override
        public LoanProductData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String name = rs.getString("name");
            final String shortName = rs.getString("shortName");
            final String description = rs.getString("description");
            final Long fundId = JdbcSupport.getLong(rs, "fundId");
            final String fundName = rs.getString("fundName");
            final Long transactionStrategyId = JdbcSupport.getLong(rs, "transactionStrategyId");
            final String transactionStrategyName = rs.getString("transactionStrategyName");

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");

            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf, currencyDisplaySymbol,
                    currencyNameCode);

            final BigDecimal principal = rs.getBigDecimal("principal");
            final BigDecimal minPrincipal = rs.getBigDecimal("minPrincipal");
            final BigDecimal maxPrincipal = rs.getBigDecimal("maxPrincipal");
            final BigDecimal tolerance = rs.getBigDecimal("tolerance");

            final Integer numberOfRepayments = JdbcSupport.getInteger(rs, "numberOfRepayments");
            final Integer minNumberOfRepayments = JdbcSupport.getInteger(rs, "minNumberOfRepayments");
            final Integer maxNumberOfRepayments = JdbcSupport.getInteger(rs, "maxNumberOfRepayments");
            final Integer repaymentEvery = JdbcSupport.getInteger(rs, "repaidEvery");

            final Integer graceOnPrincipalPayment = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnPrincipalPayment");
            final Integer recurringMoratoriumOnPrincipalPeriods = JdbcSupport.getIntegerDefaultToNullIfZero(rs,
                    "recurringMoratoriumOnPrincipalPeriods");
            final Integer graceOnInterestPayment = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnInterestPayment");
            final Integer graceOnInterestCharged = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnInterestCharged");
            final Integer graceOnArrearsAgeing = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnArrearsAgeing");
            final Integer overdueDaysForNPA = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "overdueDaysForNPA");
            final Integer minimumDaysBetweenDisbursalAndFirstRepayment = JdbcSupport.getInteger(rs,
                    "minimumDaysBetweenDisbursalAndFirstRepayment");
            final Integer minimumPeriodsBetweenDisbursalAndFirstRepayment = JdbcSupport.getInteger(rs,
                    "minimumPeriodsBetweenDisbursalAndFirstRepayment");

            final Integer accountingRuleId = JdbcSupport.getInteger(rs, "accountingType");
            final EnumOptionData accountingRuleType = AccountingEnumerations.accountingRuleType(accountingRuleId);

            final BigDecimal interestRatePerPeriod = rs.getBigDecimal("interestRatePerPeriod");
            final BigDecimal minInterestRatePerPeriod = rs.getBigDecimal("minInterestRatePerPeriod");
            final BigDecimal maxInterestRatePerPeriod = rs.getBigDecimal("maxInterestRatePerPeriod");
            final BigDecimal annualInterestRate = rs.getBigDecimal("annualInterestRate");

            final String loanInterestRatesListPerPeriod = rs.getString("interestRatesListPerPeriod");
            final List<Float> interestRatesListPerPeriod = new ArrayList<>();
            if (loanInterestRatesListPerPeriod != null && !loanInterestRatesListPerPeriod.isEmpty()) {
                final List<String> interestRates = Arrays.asList(loanInterestRatesListPerPeriod.split(","));
                for (final String rate : interestRates) {
                    interestRatesListPerPeriod.add(Float.parseFloat(rate));
                }
            }

            final boolean isLinkedToFloatingInterestRates = rs.getBoolean("isLinkedToFloatingInterestRates");
            final Integer floatingRateId = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "floatingRateId");
            final String floatingRateName = rs.getString("floatingRateName");
            final BigDecimal interestRateDifferential = rs.getBigDecimal("interestRateDifferential");
            final BigDecimal minDifferentialLendingRate = rs.getBigDecimal("minDifferentialLendingRate");
            final BigDecimal defaultDifferentialLendingRate = rs.getBigDecimal("defaultDifferentialLendingRate");
            final BigDecimal maxDifferentialLendingRate = rs.getBigDecimal("maxDifferentialLendingRate");
            final boolean isFloatingInterestRateCalculationAllowed = rs.getBoolean("isFloatingInterestRateCalculationAllowed");

            final boolean isVariableIntallmentsAllowed = rs.getBoolean("isVariableIntallmentsAllowed");
            final Integer minimumGap = rs.getInt("minimumGap");
            final Integer maximumGap = rs.getInt("maximumGap");

            final int repaymentFrequencyTypeId = JdbcSupport.getInteger(rs, "repaymentPeriodFrequency");
            final EnumOptionData repaymentFrequencyType = LoanEnumerations.repaymentFrequencyType(repaymentFrequencyTypeId);

            final int amortizationTypeId = JdbcSupport.getInteger(rs, "amortizationMethod");
            final EnumOptionData amortizationType = LoanEnumerations.amortizationType(amortizationTypeId);

            final Integer interestRateFrequencyTypeId = JdbcSupport.getInteger(rs, "interestRatePerPeriodFreq");
            final EnumOptionData interestRateFrequencyType = LoanEnumerations.interestRateFrequencyType(interestRateFrequencyTypeId);

            final int interestTypeId = JdbcSupport.getInteger(rs, "interestMethod");
            final EnumOptionData interestType = LoanEnumerations.interestType(interestTypeId);

            final int interestCalculationPeriodTypeId = JdbcSupport.getInteger(rs, "interestCalculationInPeriodMethod");
            final Boolean allowPartialPeriodInterestCalcualtion = rs.getBoolean("allowPartialPeriodInterestCalcualtion");
            final EnumOptionData interestCalculationPeriodType = LoanEnumerations
                    .interestCalculationPeriodType(interestCalculationPeriodTypeId);

            final Integer installmentCalculationPeriodTypeId = JdbcSupport.getInteger(rs, "installmentCalculationPeriodType");
            EnumOptionData installmentCalculationPeriodType = null;
            if (installmentCalculationPeriodTypeId != null) {
                installmentCalculationPeriodType = LoanEnumerations.interestCalculationPeriodType(installmentCalculationPeriodTypeId);
            }

            final Integer brokenPeriodTypeId = JdbcSupport.getInteger(rs, "brokenPeriodMethodType");
            EnumOptionData brokenPeriodMethodType = null;
            if (brokenPeriodTypeId != null) {
                brokenPeriodMethodType = LoanEnumerations.brokenPeriodMethodType(brokenPeriodTypeId);
            }

            final boolean includeInBorrowerCycle = rs.getBoolean("includeInBorrowerCycle");
            final boolean useBorrowerCycle = rs.getBoolean("useBorrowerCycle");
            final LocalDate startDate = JdbcSupport.getLocalDate(rs, "startDate");
            final LocalDate closeDate = JdbcSupport.getLocalDate(rs, "closeDate");
            String status = "";
            if (closeDate != null && closeDate.isBefore(DateUtils.getLocalDateOfTenant())) {
                status = "loanProduct.inActive";
            } else {
                status = "loanProduct.active";
            }
            final String externalId = rs.getString("externalId");
            final Collection<LoanProductBorrowerCycleVariationData> principalVariationsForBorrowerCycle = new ArrayList<>();
            final Collection<LoanProductBorrowerCycleVariationData> interestRateVariationsForBorrowerCycle = new ArrayList<>();
            final Collection<LoanProductBorrowerCycleVariationData> numberOfRepaymentVariationsForBorrowerCycle = new ArrayList<>();
            if (this.borrowerCycleVariationDatas != null) {
                for (final LoanProductBorrowerCycleVariationData borrowerCycleVariationData : this.borrowerCycleVariationDatas) {
                    final LoanProductParamType loanProductParamType = borrowerCycleVariationData.getParamType();
                    if (loanProductParamType.isParamTypePrincipal()) {
                        principalVariationsForBorrowerCycle.add(borrowerCycleVariationData);
                    } else if (loanProductParamType.isParamTypeInterestTate()) {
                        interestRateVariationsForBorrowerCycle.add(borrowerCycleVariationData);
                    } else if (loanProductParamType.isParamTypeRepayment()) {
                        numberOfRepaymentVariationsForBorrowerCycle.add(borrowerCycleVariationData);
                    }
                }
            }

            final Boolean multiDisburseLoan = rs.getBoolean("multiDisburseLoan");
            final Boolean allowNegativeLoanBalance = rs.getBoolean(LoanProductConstants.allowNegativeLoanBalance);
            final Boolean considerFutureDisbursementsInSchedule = rs.getBoolean(LoanProductConstants.considerFutureDisbursementsInSchedule);
            final Boolean considerAllDisbursementsInSchedule = rs.getBoolean(LoanProductConstants.considerAllDisbursementsInSchedule);
            final Integer maxTrancheCount = rs.getInt("maxTrancheCount");
            final Boolean isEmiBasedOnDisbursements = rs.getBoolean("isEmiBasedOnDisbursements");
            final BigDecimal outstandingLoanBalance = rs.getBigDecimal("outstandingLoanBalance");

            final int daysInMonth = JdbcSupport.getInteger(rs, "daysInMonth");
            final EnumOptionData daysInMonthType = CommonEnumerations.daysInMonthType(daysInMonth);
            final int daysInYear = JdbcSupport.getInteger(rs, "daysInYear");
            final EnumOptionData daysInYearType = CommonEnumerations.daysInYearType(daysInYear);
            final Integer installmentAmountInMultiplesOf = JdbcSupport.getInteger(rs, "installmentAmountInMultiplesOf");
            final boolean canDefineInstallmentAmount = rs.getBoolean("canDefineInstallmentAmount");
            final boolean isInterestRecalculationEnabled = rs.getBoolean("isInterestRecalculationEnabled");

            LoanProductInterestRecalculationData interestRecalculationData = null;
            if (isInterestRecalculationEnabled) {
                final String codePrefix = "interestRecalculationCompounding.";
                final Long lprId = JdbcSupport.getLong(rs, "lprId");
                final Long productId = JdbcSupport.getLong(rs, "productId");
                final int compoundTypeEnumValue = JdbcSupport.getInteger(rs, "compoundType");
                final EnumOptionData interestRecalculationCompoundingType = LoanEnumerations
                        .interestRecalculationCompoundingType(compoundTypeEnumValue);
                final int rescheduleStrategyEnumValue = JdbcSupport.getInteger(rs, "rescheduleStrategy");
                final EnumOptionData rescheduleStrategyType = LoanEnumerations.rescheduleStrategyType(rescheduleStrategyEnumValue);
                final int restFrequencyEnumValue = JdbcSupport.getInteger(rs, "restFrequencyEnum");
                final EnumOptionData restFrequencyType = LoanEnumerations.interestRecalculationFrequencyType(restFrequencyEnumValue);
                final int restFrequencyInterval = JdbcSupport.getInteger(rs, "restFrequencyInterval");
                final Integer restFrequencyNthDayEnumValue = JdbcSupport.getInteger(rs, "restFrequencyNthDayEnum");
                EnumOptionData restFrequencyNthDayEnum = null;
                if (restFrequencyNthDayEnumValue != null) {
                    restFrequencyNthDayEnum = CommonEnumerations.nthDayType(restFrequencyNthDayEnumValue, codePrefix);
                }
                final Integer restFrequencyWeekDayEnumValue = JdbcSupport.getInteger(rs, "restFrequencyWeekDayEnum");
                EnumOptionData restFrequencyWeekDayEnum = null;
                if (restFrequencyWeekDayEnumValue != null) {
                    restFrequencyWeekDayEnum = CommonEnumerations.dayOfWeekType(restFrequencyWeekDayEnumValue, codePrefix);
                }
                final Integer restFrequencyOnDay = JdbcSupport.getInteger(rs, "restFrequencyOnDay");
                final Integer compoundingFrequencyEnumValue = JdbcSupport.getInteger(rs, "compoundingFrequencyTypeEnum");
                EnumOptionData compoundingFrequencyType = null;
                if (compoundingFrequencyEnumValue != null) {
                    compoundingFrequencyType = LoanEnumerations.interestRecalculationFrequencyType(compoundingFrequencyEnumValue);
                }
                final Integer compoundingInterval = JdbcSupport.getInteger(rs, "compoundingInterval");
                final Integer compoundingFrequencyNthDayEnumValue = JdbcSupport.getInteger(rs, "compoundingFrequencyNthDayEnum");
                EnumOptionData compoundingFrequencyNthDayEnum = null;
                if (compoundingFrequencyNthDayEnumValue != null) {
                    compoundingFrequencyNthDayEnum = CommonEnumerations.nthDayType(compoundingFrequencyNthDayEnumValue, codePrefix);
                }
                final Integer compoundingFrequencyWeekDayEnumValue = JdbcSupport.getInteger(rs, "compoundingFrequencyWeekDayEnum");
                EnumOptionData compoundingFrequencyWeekDayEnum = null;
                if (compoundingFrequencyWeekDayEnumValue != null) {
                    compoundingFrequencyWeekDayEnum = CommonEnumerations.dayOfWeekType(compoundingFrequencyWeekDayEnumValue, codePrefix);
                }
                final Integer compoundingFrequencyOnDay = JdbcSupport.getInteger(rs, "compoundingFrequencyOnDay");
                final boolean isArrearsBasedOnOriginalSchedule = rs.getBoolean("isArrearsBasedOnOriginalSchedule");
                final boolean isCompoundingToBePostedAsTransaction = rs.getBoolean("isCompoundingToBePostedAsTransaction");
                final int preCloseInterestCalculationStrategyEnumValue = JdbcSupport.getInteger(rs, "preCloseInterestCalculationStrategy");
                final EnumOptionData preCloseInterestCalculationStrategy = LoanEnumerations
                        .preCloseInterestCalculationStrategy(preCloseInterestCalculationStrategyEnumValue);
                final boolean allowCompoundingOnEod = rs.getBoolean("allowCompoundingOnEod");
                final boolean isSubsidyApplicable = rs.getBoolean("isSubsidyApplicable");

                interestRecalculationData = new LoanProductInterestRecalculationData(lprId, productId, interestRecalculationCompoundingType,
                        rescheduleStrategyType, restFrequencyType, restFrequencyInterval, restFrequencyNthDayEnum, restFrequencyWeekDayEnum,
                        restFrequencyOnDay, compoundingFrequencyType, compoundingInterval, compoundingFrequencyNthDayEnum,
                        compoundingFrequencyWeekDayEnum, compoundingFrequencyOnDay, isArrearsBasedOnOriginalSchedule,
                        isCompoundingToBePostedAsTransaction, preCloseInterestCalculationStrategy, allowCompoundingOnEod,
                        isSubsidyApplicable);
            }

            final boolean amortization = rs.getBoolean("amortizationBoolean");
            final boolean interestMethod = rs.getBoolean("interestMethodConfigBoolean");
            final boolean transactionProcessingStrategy = rs.getBoolean("transactionProcessingStrategyBoolean");
            final boolean interestCalcPeriod = rs.getBoolean("interestCalcPeriodBoolean");
            final boolean arrearsTolerance = rs.getBoolean("arrearsToleranceBoolean");
            final boolean repaymentFrequency = rs.getBoolean("repaymentFrequencyBoolean");
            final boolean graceOnPrincipalAndInterest = rs.getBoolean("graceOnPrincipalAndInterestBoolean");
            final boolean graceOnArrearsAging = rs.getBoolean("graceOnArrearsAgingBoolean");

            LoanProductConfigurableAttributes allowAttributeOverrides = null;

            allowAttributeOverrides = new LoanProductConfigurableAttributes(amortization, interestMethod, transactionProcessingStrategy,
                    interestCalcPeriod, arrearsTolerance, repaymentFrequency, graceOnPrincipalAndInterest, graceOnArrearsAging);

            final boolean holdGuaranteeFunds = rs.getBoolean("holdGuaranteeFunds");
            LoanProductGuaranteeData loanProductGuaranteeData = null;
            if (holdGuaranteeFunds) {
                final Long lpgId = JdbcSupport.getLong(rs, "lpgId");
                final BigDecimal mandatoryGuarantee = rs.getBigDecimal("mandatoryGuarantee");
                final BigDecimal minimumGuaranteeFromOwnFunds = rs.getBigDecimal("minimumGuaranteeFromOwnFunds");
                final BigDecimal minimumGuaranteeFromGuarantor = rs.getBigDecimal("minimumGuaranteeFromGuarantor");
                loanProductGuaranteeData = LoanProductGuaranteeData.instance(lpgId, id, mandatoryGuarantee, minimumGuaranteeFromOwnFunds,
                        minimumGuaranteeFromGuarantor);
            }

            final BigDecimal principalThresholdForLastInstallment = rs.getBigDecimal("principalThresholdForLastInstallment");
            final boolean accountMovesOutOfNPAOnlyOnArrearsCompletion = rs.getBoolean("accountMovesOutOfNPAOnlyOnArrearsCompletion");
            final Integer adjustedInstallmentInMultiplesOf = JdbcSupport.getInteger(rs, "adjustedInstallmentInMultiplesOf");
            final boolean adjustFirstEMIAmount = rs.getBoolean("adjustFirstEMIAmount");
            final boolean adjustInterestForRounding = rs.getBoolean("adjustInterestForRounding");
            final Boolean closeLoanOnOverpayment = rs.getBoolean("closeLoanOnOverpayment");
            final boolean syncExpectedWithDisbursementDate = rs.getBoolean("syncExpectedWithDisbursementDate");

            final Integer minLoanTerm = JdbcSupport.getInteger(rs, "minLoanTerm");
            final Integer maxLoanTerm = JdbcSupport.getInteger(rs, "maxLoanTerm");
            EnumOptionData loanTenureFrequencyType = null;
            final Integer loanTenureFrequencyTypeEnum = JdbcSupport.getInteger(rs, "loanTenureFrequencyType");
            if (loanTenureFrequencyTypeEnum != null) {
                loanTenureFrequencyType = LoanEnumerations.loanTenureFrequencyType(loanTenureFrequencyTypeEnum.intValue());
            }

            final boolean canUseForTopup = rs.getBoolean("canUseForTopup");
            final Integer weeksInYearTypeInteger = JdbcSupport.getInteger(rs, "weeksInYearType");
            final EnumOptionData weeksInYearType = LoanEnumerations.weeksInYearType(WeeksInYearType.fromInt(weeksInYearTypeInteger));
            final boolean isMinDurationApplicableForAllDisbursements = rs.getBoolean("isMinDurationApplicableForAllDisbursements");
            final boolean isFlatInterestRate = rs.getBoolean("isFlatInterestRate");
            final boolean allowUpfrontCollection = rs.getBoolean("allowUpfrontCollection");
            final BigDecimal percentageOfDisbursementToBeTransferred = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs,
                    "percentageOfDisbursementToBeTransferred");
            final LoanProductTemplateData loanProductTemplateData = null;
            final EnumOptionData applicableForLoanType = LoanProductApplicableForLoanType
                    .type(JdbcSupport.getInteger(rs, LoanProductConstants.applicableForLoanTypeParamName));
            return new LoanProductData(id, name, shortName, description, currency, principal, minPrincipal, maxPrincipal, tolerance,
                    numberOfRepayments, minNumberOfRepayments, maxNumberOfRepayments, repaymentEvery, interestRatePerPeriod,
                    minInterestRatePerPeriod, maxInterestRatePerPeriod, annualInterestRate, repaymentFrequencyType,
                    interestRateFrequencyType, amortizationType, interestType, interestCalculationPeriodType,
                    allowPartialPeriodInterestCalcualtion, fundId, fundName, transactionStrategyId, transactionStrategyName,
                    graceOnPrincipalPayment, recurringMoratoriumOnPrincipalPeriods, graceOnInterestPayment, graceOnInterestCharged,
                    this.charges, accountingRuleType, includeInBorrowerCycle, useBorrowerCycle, startDate, closeDate, status, externalId,
                    principalVariationsForBorrowerCycle, interestRateVariationsForBorrowerCycle,
                    numberOfRepaymentVariationsForBorrowerCycle, multiDisburseLoan, maxTrancheCount, outstandingLoanBalance,
                    graceOnArrearsAgeing, overdueDaysForNPA, daysInMonthType, daysInYearType, isInterestRecalculationEnabled,
                    interestRecalculationData, minimumDaysBetweenDisbursalAndFirstRepayment, holdGuaranteeFunds, loanProductGuaranteeData,
                    principalThresholdForLastInstallment, accountMovesOutOfNPAOnlyOnArrearsCompletion, canDefineInstallmentAmount,
                    installmentAmountInMultiplesOf, allowAttributeOverrides, isLinkedToFloatingInterestRates, floatingRateId,
                    floatingRateName, interestRateDifferential, minDifferentialLendingRate, defaultDifferentialLendingRate,
                    maxDifferentialLendingRate, isFloatingInterestRateCalculationAllowed, isVariableIntallmentsAllowed, minimumGap,
                    maximumGap, adjustedInstallmentInMultiplesOf, adjustFirstEMIAmount, closeLoanOnOverpayment,
                    syncExpectedWithDisbursementDate, minimumPeriodsBetweenDisbursalAndFirstRepayment, minLoanTerm, maxLoanTerm,
                    loanTenureFrequencyType, canUseForTopup, weeksInYearType, adjustInterestForRounding, isEmiBasedOnDisbursements,
                    installmentCalculationPeriodType, isMinDurationApplicableForAllDisbursements, brokenPeriodMethodType,
                    isFlatInterestRate, allowNegativeLoanBalance, considerFutureDisbursementsInSchedule, considerAllDisbursementsInSchedule,
                    allowUpfrontCollection, percentageOfDisbursementToBeTransferred, interestRatesListPerPeriod, loanProductTemplateData,
                    applicableForLoanType);
        }
    }

    private static final class LoanProductLookupMapper implements RowMapper<LoanProductData> {

        public String schema() {
            return "lp.id as id, lp.name as name from m_product_loan lp";
        }

        public String activeOnlySchema() {
            return schema() + " where (close_date is null or close_date >= CURDATE())";
        }

        public String productMixSchema() {
            return "lp.id as id, lp.name as name FROM m_product_loan lp left join m_product_mix pm on pm.product_id=lp.id where lp.id not IN("
                    + "select lp.id from m_product_loan lp inner join m_product_mix pm on pm.product_id=lp.id)";
        }

        public String restrictedProductsSchema() {
            return "pm.restricted_product_id as id, rp.name as name from m_product_mix pm join m_product_loan rp on rp.id = pm.restricted_product_id ";
        }

        public String derivedRestrictedProductsSchema() {
            return "pm.product_id as id, lp.name as name from m_product_mix pm join m_product_loan lp on lp.id=pm.product_id";
        }

        @Override
        public LoanProductData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");

            return LoanProductData.lookup(id, name);
        }
    }

    private static final class LoanProductBorrowerCycleMapper implements RowMapper<LoanProductBorrowerCycleVariationData> {

        public String schema() {
            return "bc.id as id,bc.borrower_cycle_number as cycleNumber,bc.value_condition as conditionType,bc.param_type as paramType,"
                    + "bc.default_value as defaultValue,bc.max_value as maxVal,bc.min_value as minVal, bc.interest_rates_list_per_cycle as interestRatesListPerCycle "
                    + "from m_product_loan_variations_borrower_cycle bc";
        }

        @Override
        public LoanProductBorrowerCycleVariationData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {
            final Long id = rs.getLong("id");
            final Integer cycleNumber = JdbcSupport.getInteger(rs, "cycleNumber");
            final Integer conditionType = JdbcSupport.getInteger(rs, "conditionType");
            final EnumOptionData conditionTypeData = LoanEnumerations.loanCycleValueConditionType(conditionType);
            final Integer paramType = JdbcSupport.getInteger(rs, "paramType");
            final EnumOptionData paramTypeData = LoanEnumerations.loanCycleParamType(paramType);
            final BigDecimal defaultValue = rs.getBigDecimal("defaultValue");
            final BigDecimal maxValue = rs.getBigDecimal("maxVal");
            final BigDecimal minValue = rs.getBigDecimal("minVal");
            final String loanInterestRatesListPerCycle = rs.getString("interestRatesListPerCycle");

            final List<Float> interestRatesListPerCycle = new ArrayList<>();
            if (loanInterestRatesListPerCycle != null && !loanInterestRatesListPerCycle.isEmpty()) {
                final List<String> interestRates = Arrays.asList(loanInterestRatesListPerCycle.split(","));
                for (final String rate : interestRates) {
                    interestRatesListPerCycle.add(Float.parseFloat(rate));
                }
            }

            final LoanProductBorrowerCycleVariationData borrowerCycleVariationData = new LoanProductBorrowerCycleVariationData(id,
                    cycleNumber, paramTypeData, conditionTypeData, defaultValue, minValue, maxValue, interestRatesListPerCycle);
            return borrowerCycleVariationData;
        }

    }

    private static final class ProductLoanChargeMapper implements RowMapper<ProductLoanChargeData> {

        final ChargeReadPlatformService chargeReadPlatformService;
        final ChargeSlabReadPlatformService chargeSlabReadPlatformService;

        private ProductLoanChargeMapper(final ChargeReadPlatformService chargeReadPlatformService,
                final ChargeSlabReadPlatformService chargeSlabReadPlatformService) {
            this.chargeReadPlatformService = chargeReadPlatformService;
            this.chargeSlabReadPlatformService = chargeSlabReadPlatformService;
        }

        public String schema() {
            return "plc.id AS id, plc.product_loan_id AS productLoanId, plc.charge_id AS chargeId, plc.is_mandatory AS isMandatory FROM m_product_loan_charge plc";
        }

        @Override
        public ProductLoanChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final Long productLoanId = rs.getLong("productLoanId");
            final Long chargeId = rs.getLong("chargeId");
            final ChargeData chargeData = this.chargeReadPlatformService.retrieveCharge(chargeId);
            if (chargeData.getChargeSlabs() != null && !chargeData.getChargeSlabs().isEmpty()) {
                for (final ChargeSlabData chargeSlabData : chargeData.getChargeSlabs()) {
                    this.chargeSlabReadPlatformService.retrieveAllChargeSubSlabsBySlabChargeId(chargeSlabData.getId());
                    chargeSlabData.setSubSlabs(
                            this.chargeSlabReadPlatformService.retrieveAllChargeSubSlabsBySlabChargeId(chargeSlabData.getId()));
                }
            }
            final Boolean isMandatory = rs.getBoolean("isMandatory");
            return ProductLoanChargeData.instance(id, productLoanId, chargeData, isMandatory);
        }

    }

    @Override
    public Collection<LoanProductData> retrieveAllLoanProductsForCurrency(final String currencyCode) {
        this.context.authenticatedUser();

        final LoanProductMapper rm = new LoanProductMapper(null, null);

        String sql = "select " + rm.loanProductSchema() + " where lp.currency_code='" + currencyCode + "'";

        // Check if branch specific products are enabled. If yes, fetch only
        // products mapped to current user's office
        final String inClause = this.fineractEntityAccessUtil
                .getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled(FineractEntityType.LOAN_PRODUCT);
        if ((inClause != null) && (!(inClause.trim().isEmpty()))) {
            sql += " and id lp.in ( " + inClause + " ) ";
        }

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    public Collection<LoanProductData> retrieveAvailableLoanProductsForMix() {

        this.context.authenticatedUser();

        final LoanProductLookupMapper rm = new LoanProductLookupMapper();

        String sql = "Select " + rm.productMixSchema();

        // Check if branch specific products are enabled. If yes, fetch only
        // products mapped to current user's office
        final String inClause = this.fineractEntityAccessUtil
                .getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled(FineractEntityType.LOAN_PRODUCT);
        if ((inClause != null) && (!(inClause.trim().isEmpty()))) {
            sql += " and lp.id in ( " + inClause + " ) ";
        }

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    public Collection<LoanProductData> retrieveRestrictedProductsForMix(final Long productId) {

        this.context.authenticatedUser();

        final LoanProductLookupMapper rm = new LoanProductLookupMapper();

        String sql = "Select " + rm.restrictedProductsSchema() + " where pm.product_id=? ";
        // Check if branch specific products are enabled. If yes, fetch only
        // products mapped to current user's office
        final String inClause1 = this.fineractEntityAccessUtil
                .getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled(FineractEntityType.LOAN_PRODUCT);
        if ((inClause1 != null) && (!(inClause1.trim().isEmpty()))) {
            sql += " and rp.id in ( " + inClause1 + " ) ";
        }

        sql += " UNION Select " + rm.derivedRestrictedProductsSchema() + " where pm.restricted_product_id=?";

        // Check if branch specific products are enabled. If yes, fetch only
        // products mapped to current user's office
        final String inClause2 = this.fineractEntityAccessUtil
                .getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled(FineractEntityType.LOAN_PRODUCT);
        if ((inClause2 != null) && (!(inClause2.trim().isEmpty()))) {
            sql += " and lp.id in ( " + inClause2 + " ) ";
        }

        return this.jdbcTemplate.query(sql, rm, new Object[] { productId, productId });
    }

    @Override
    public Collection<LoanProductData> retrieveAllowedProductsForMix(final Long productId) {

        this.context.authenticatedUser();

        final LoanProductLookupMapper rm = new LoanProductLookupMapper();
        String sql = "Select " + rm.schema() + " where ";

        // Check if branch specific products are enabled. If yes, fetch only
        // products mapped to current user's office
        final String inClause = this.fineractEntityAccessUtil
                .getSQLWhereClauseForProductIDsForUserOffice_ifGlobalConfigEnabled(FineractEntityType.LOAN_PRODUCT);
        if ((inClause != null) && (!(inClause.trim().isEmpty()))) {
            sql += " lp.id in ( " + inClause + " ) and ";
        }

        sql += "lp.id not in (" + "Select pm.restricted_product_id from m_product_mix pm where pm.product_id=? " + "UNION "
                + "Select pm.product_id from m_product_mix pm where pm.restricted_product_id=?)";

        return this.jdbcTemplate.query(sql, rm, new Object[] { productId, productId });
    }

    @Override
    public LoanProductData retrieveLoanProductFloatingDetails(final Long loanProductId) {

        try {
            final LoanProductFloatingRateMapper rm = new LoanProductFloatingRateMapper();
            final String sql = "select " + rm.schema() + " where lp.id = ?";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { loanProductId });

        } catch (final EmptyResultDataAccessException e) {
            throw new LoanProductNotFoundException(loanProductId);
        }
    }

    private static final class LoanProductFloatingRateMapper implements RowMapper<LoanProductData> {

        public LoanProductFloatingRateMapper() {}

        public String schema() {
            return "lp.id as id,  lp.name as name," + "lp.is_linked_to_floating_interest_rates as isLinkedToFloatingInterestRates, "
                    + "lfr.floating_rates_id as floatingRateId, " + "fr.name as floatingRateName, "
                    + "lfr.interest_rate_differential as interestRateDifferential, "
                    + "lfr.min_differential_lending_rate as minDifferentialLendingRate, "
                    + "lfr.default_differential_lending_rate as defaultDifferentialLendingRate, "
                    + "lfr.max_differential_lending_rate as maxDifferentialLendingRate, "
                    + "lfr.is_floating_interest_rate_calculation_allowed as isFloatingInterestRateCalculationAllowed "
                    + " from m_product_loan lp " + " left join m_product_loan_floating_rates as lfr on lfr.loan_product_id = lp.id "
                    + " left join m_floating_rates as fr on lfr.floating_rates_id = fr.id ";
        }

        @Override
        public LoanProductData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String name = rs.getString("name");

            final boolean isLinkedToFloatingInterestRates = rs.getBoolean("isLinkedToFloatingInterestRates");
            final Integer floatingRateId = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "floatingRateId");
            final String floatingRateName = rs.getString("floatingRateName");
            final BigDecimal interestRateDifferential = rs.getBigDecimal("interestRateDifferential");
            final BigDecimal minDifferentialLendingRate = rs.getBigDecimal("minDifferentialLendingRate");
            final BigDecimal defaultDifferentialLendingRate = rs.getBigDecimal("defaultDifferentialLendingRate");
            final BigDecimal maxDifferentialLendingRate = rs.getBigDecimal("maxDifferentialLendingRate");
            final boolean isFloatingInterestRateCalculationAllowed = rs.getBoolean("isFloatingInterestRateCalculationAllowed");

            return LoanProductData.loanProductWithFloatingRates(id, name, isLinkedToFloatingInterestRates, floatingRateId, floatingRateName,
                    interestRateDifferential, minDifferentialLendingRate, defaultDifferentialLendingRate, maxDifferentialLendingRate,
                    isFloatingInterestRateCalculationAllowed);
        }
    }

    @Override
    public List<Map<String, Object>> getLoanProductMandatoryCharges(final Long productId, final Boolean isPenalty) {
        final List<Map<String, Object>> chargeIdList = new LinkedList<>();
        String sql = "SELECT plc.charge_id FROM m_product_loan_charge plc JOIN m_charge mc ON mc.id = plc.charge_id ";
        if (isPenalty != null) {
            int v = 0;
            if (isPenalty) {
                v = 1;
            }
            sql += "AND mc.is_penalty = " + v + " ";
        }
        sql += "WHERE plc.product_loan_id = '" + productId + "' AND plc.is_mandatory = '1'";
        chargeIdList.addAll(this.jdbcTemplate.queryForList(sql));
        return chargeIdList;
    }

    @Override
    public void checkLoanProductByIdExists(final Long productId) {

        try {
            final String sql = "Select id from m_product_loan where id = ?";

            this.jdbcTemplate.queryForObject(sql, new Object[] { productId }, Long.class);

        } catch (final EmptyResultDataAccessException e) {
            throw new LoanProductNotFoundException(productId);
        }
    }

    @Override
    public void checkLoanProductByIdIsActive(final Long productId, final boolean activeOnly) {
        try {
            final String sql = "Select close_date from m_product_loan where id = ? ";

            final String closeDate = this.jdbcTemplate.queryForObject(sql, new Object[] { productId }, String.class);
            if (closeDate != null && activeOnly) {
                checkIfLoanProductIsClosed(closeDate, productId);
            }

        } catch (final EmptyResultDataAccessException e) {
            throw new LoanProductNotFoundException(productId);
        }
    }

    private void checkIfLoanProductIsClosed(final String closeDate, final Long productId) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            final Date closingDate = dateFormat.parse(closeDate);

            final LocalDate currentDate = DateUtils.getLocalDateOfTenant();
            final LocalDate closinDate = new LocalDate(closingDate);

            if (currentDate.isAfter(closinDate)) { throw new LoanProductInactiveException(productId); }
        } catch (final ParseException e) {

        }

    }

    @Override
    public LoanProductData retrieveLoanProductNameById(final Long productId) {
        try {
            final LoanProductLookupMapper rm = new LoanProductLookupMapper();
            final String sql = "select " + rm.schema() + " where lp.id = ?";
            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { productId });
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanProductNotFoundException(productId);
        }
    }

}