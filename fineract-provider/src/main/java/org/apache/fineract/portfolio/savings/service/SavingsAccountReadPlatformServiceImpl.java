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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.organisation.staff.service.StaffReadPlatformService;
import org.apache.fineract.portfolio.account.data.AccountTransferData;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.ClientStatus;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.common.domain.DayOfWeekType;
import org.apache.fineract.portfolio.common.domain.NthDayType;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.common.service.CommonEnumerations;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.group.service.GroupReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.guarantor.data.GuarantorData;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.SavingsCompoundingInterestPeriodType;
import org.apache.fineract.portfolio.savings.SavingsDpLimitCalculationType;
import org.apache.fineract.portfolio.savings.SavingsInterestCalculationDaysInYearType;
import org.apache.fineract.portfolio.savings.SavingsInterestCalculationType;
import org.apache.fineract.portfolio.savings.SavingsPeriodFrequencyType;
import org.apache.fineract.portfolio.savings.SavingsPostingInterestPeriodType;
import org.apache.fineract.portfolio.savings.data.SavingsAccountApplicationTimelineData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountChargeData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountDpDetailsData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountStatusEnumData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountSubStatusEnumData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountSummaryData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionEnumData;
import org.apache.fineract.portfolio.savings.data.SavingsProductData;
import org.apache.fineract.portfolio.savings.data.SavingsProductDrawingPowerDetailsData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountStatusType;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountSubStatusEnum;
import org.apache.fineract.portfolio.savings.exception.SavingsAccountNotFoundException;
import org.apache.fineract.portfolio.savings.exception.SavingsAccountTransactionNotFoundException;
import org.apache.fineract.portfolio.tax.data.TaxGroupData;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.finflux.common.constant.CommonConstants;

@Service
public class SavingsAccountReadPlatformServiceImpl implements SavingsAccountReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final ClientReadPlatformService clientReadPlatformService;
    private final GroupReadPlatformService groupReadPlatformService;
    private final SavingsProductReadPlatformService savingsProductReadPlatformService;
    private final StaffReadPlatformService staffReadPlatformService;
    private final SavingsDropdownReadPlatformService dropdownReadPlatformService;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    // mappers
    private final SavingsAccountTransactionTemplateMapper transactionTemplateMapper;
    private final SavingsAccountTransactionsMapper transactionsMapper;
    private final SavingAccountMapper savingAccountMapper;
    // private final SavingsAccountAnnualFeeMapper annualFeeMapper;

    // pagination
    private final PaginationHelper<SavingsAccountData> paginationHelper = new PaginationHelper<>();
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final PaginationHelper<Long> paginationHelperForLong = new PaginationHelper<>();
    
    @Autowired
    public SavingsAccountReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final ClientReadPlatformService clientReadPlatformService, final GroupReadPlatformService groupReadPlatformService,
            final SavingsProductReadPlatformService savingProductReadPlatformService,
            final StaffReadPlatformService staffReadPlatformService, final SavingsDropdownReadPlatformService dropdownReadPlatformService,
            final ChargeReadPlatformService chargeReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.clientReadPlatformService = clientReadPlatformService;
        this.groupReadPlatformService = groupReadPlatformService;
        this.savingsProductReadPlatformService = savingProductReadPlatformService;
        this.staffReadPlatformService = staffReadPlatformService;
        this.dropdownReadPlatformService = dropdownReadPlatformService;
        this.transactionTemplateMapper = new SavingsAccountTransactionTemplateMapper();
        this.transactionsMapper = new SavingsAccountTransactionsMapper();
        this.savingAccountMapper = new SavingAccountMapper();
        // this.annualFeeMapper = new SavingsAccountAnnualFeeMapper();
        this.chargeReadPlatformService = chargeReadPlatformService;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Collection<SavingsAccountData> retrieveAllForLookup(final Long clientId) {

        final StringBuilder sqlBuilder = new StringBuilder("select " + this.savingAccountMapper.schema());
        sqlBuilder.append(" where sa.client_id = ? and sa.status_enum = 300 ");

        final Object[] queryParameters = new Object[] { clientId };
        return this.jdbcTemplate.query(sqlBuilder.toString(), this.savingAccountMapper, queryParameters);
    }

    @Override
    public Collection<SavingsAccountData> retrieveActiveForLookup(final Long clientId, final DepositAccountType depositAccountType) {

        final StringBuilder sqlBuilder = new StringBuilder("select " + this.savingAccountMapper.schema());
        sqlBuilder.append(" where sa.client_id = ? and sa.status_enum = 300 and sa.deposit_type_enum = ? ");

        final Object[] queryParameters = new Object[] { clientId, depositAccountType.getValue() };
        return this.jdbcTemplate.query(sqlBuilder.toString(), this.savingAccountMapper, queryParameters);
    }

    @Override
    public Collection<SavingsAccountData> retrieveActiveForLookup(final Long clientId, final DepositAccountType depositAccountType,
            final String currencyCode) {
        final StringBuilder sqlBuilder = new StringBuilder("select " + this.savingAccountMapper.schema());
        sqlBuilder.append(" where sa.client_id = ? and sa.status_enum = 300 and sa.deposit_type_enum = ? and sa.currency_code = ? ");

        final Object[] queryParameters = new Object[] { clientId, depositAccountType.getValue(), currencyCode };
        return this.jdbcTemplate.query(sqlBuilder.toString(), this.savingAccountMapper, queryParameters);
    }

    @Override
    public Page<SavingsAccountData> retrieveAll(final SearchParameters searchParameters) {

        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";

        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        sqlBuilder.append(this.savingAccountMapper.schema());

        sqlBuilder.append(" join m_office o on o.id = c.office_id");
        sqlBuilder.append(" where o.hierarchy like ?");

        final Object[] objectArray = new Object[2];
        objectArray[0] = hierarchySearchString;
        int arrayPos = 1;

        final Map<String, String> searchConditions = searchParameters.getSearchConditions();
        searchConditions.forEach((key, value) -> {
            switch (key) {
                case CommonConstants.SAVINGS_ACCOUNT_NO:
                    sqlBuilder.append(" and ( sa.account_no = '").append(value).append("' ) ");
                break;
                default:
                break;
            }
        });

        if (StringUtils.isNotBlank(searchParameters.getExternalId())) {
            sqlBuilder.append(" and sa.external_id = ?");
            objectArray[arrayPos] = searchParameters.getExternalId();
            arrayPos = arrayPos + 1;
        }

        if (searchParameters.isOfficeIdPassed()) {
            sqlBuilder.append(" and c.office_id = ?");
            objectArray[arrayPos] = searchParameters.getOfficeId();
            arrayPos = arrayPos + 1;
        }

        if (searchParameters.isOrderByRequested()) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy());

            if (searchParameters.isSortOrderProvided()) {
                sqlBuilder.append(' ').append(searchParameters.getSortOrder());
            }
        }

        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getOffset());
            }
        }

        final Object[] finalObjectArray = Arrays.copyOf(objectArray, arrayPos);
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(), finalObjectArray,
                this.savingAccountMapper);
    }

    @Override
    public SavingsAccountData retrieveOne(final Long accountId) {

        try {
            final String sql = "select " + this.savingAccountMapper.schema() + " where sa.id = ?";

            return this.jdbcTemplate.queryForObject(sql, this.savingAccountMapper, new Object[] { accountId });
        } catch (final EmptyResultDataAccessException e) {
            throw new SavingsAccountNotFoundException(accountId);
        }
    }

    @Override
    public Long getIsReleaseGuarantor(final Long savingId) {

        final String sql = " select msa.release_guarantor from m_savings_account msa where msa.id = " + savingId;
        return this.jdbcTemplate.queryForObject(sql, Long.class);
    }

    private static final class SavingAccountMapper implements RowMapper<SavingsAccountData> {

        private final String schemaSql;

        public SavingAccountMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("sa.id as id, sa.account_no as accountNo, sa.external_id as externalId, ");
            sqlBuilder.append("sa.deposit_type_enum as depositType, ");
            sqlBuilder.append("c.id as clientId, c.display_name as clientName, ");
            sqlBuilder.append("g.id as groupId, g.display_name as groupName, ");
            sqlBuilder.append("sp.id as productId, sp.name as productName, ");
            sqlBuilder.append("s.id fieldOfficerId, s.display_name as fieldOfficerName, ");
            sqlBuilder.append("sa.status_enum as statusEnum, ");
            sqlBuilder.append("sa.sub_status_enum as subStatusEnum, ");
            sqlBuilder.append("sa.submittedon_date as submittedOnDate,");
            sqlBuilder.append("sbu.username as submittedByUsername,");
            sqlBuilder.append("sbu.firstname as submittedByFirstname, sbu.lastname as submittedByLastname,");

            sqlBuilder.append("sa.rejectedon_date as rejectedOnDate,");
            sqlBuilder.append("rbu.username as rejectedByUsername,");
            sqlBuilder.append("rbu.firstname as rejectedByFirstname, rbu.lastname as rejectedByLastname,");

            sqlBuilder.append("sa.withdrawnon_date as withdrawnOnDate,");
            sqlBuilder.append("wbu.username as withdrawnByUsername,");
            sqlBuilder.append("wbu.firstname as withdrawnByFirstname, wbu.lastname as withdrawnByLastname,");

            sqlBuilder.append("sa.approvedon_date as approvedOnDate,");
            sqlBuilder.append("abu.username as approvedByUsername,");
            sqlBuilder.append("abu.firstname as approvedByFirstname, abu.lastname as approvedByLastname,");

            sqlBuilder.append("sa.activatedon_date as activatedOnDate,");
            sqlBuilder.append("avbu.username as activatedByUsername,");
            sqlBuilder.append("avbu.firstname as activatedByFirstname, avbu.lastname as activatedByLastname,");

            sqlBuilder.append("sa.closedon_date as closedOnDate,");
            sqlBuilder.append("cbu.username as closedByUsername,");
            sqlBuilder.append("cbu.firstname as closedByFirstname, cbu.lastname as closedByLastname,");

            sqlBuilder.append(
                    "sa.currency_code as currencyCode, sa.currency_digits as currencyDigits, sa.currency_multiplesof as inMultiplesOf, ");
            sqlBuilder.append("curr.name as currencyName, curr.internationalized_name_code as currencyNameCode, ");
            sqlBuilder.append("curr.display_symbol as currencyDisplaySymbol, ");

            sqlBuilder.append("sa.nominal_annual_interest_rate as nominalAnnualInterestRate, ");
            sqlBuilder.append("sa.interest_compounding_period_enum as interestCompoundingPeriodType, ");
            sqlBuilder.append("sa.interest_posting_period_enum as interestPostingPeriodType, ");
            sqlBuilder.append("sa.interest_calculation_type_enum as interestCalculationType, ");
            sqlBuilder.append("sa.interest_calculation_days_in_year_type_enum as interestCalculationDaysInYearType, ");
            sqlBuilder.append("sa.min_required_opening_balance as minRequiredOpeningBalance, ");
            sqlBuilder.append("sa.lockin_period_frequency as lockinPeriodFrequency,");
            sqlBuilder.append("sa.lockin_period_frequency_enum as lockinPeriodFrequencyType, ");
            // sqlBuilder.append("sa.withdrawal_fee_amount as
            // withdrawalFeeAmount,");
            // sqlBuilder.append("sa.withdrawal_fee_type_enum as
            // withdrawalFeeTypeEnum, ");
            sqlBuilder.append("sa.withdrawal_fee_for_transfer as withdrawalFeeForTransfers, ");
            sqlBuilder.append("sa.allow_overdraft as allowOverdraft, ");
            sqlBuilder.append("sa.overdraft_limit as overdraftLimit, ");
            sqlBuilder.append("sa.release_guarantor as releaseguarantor, ");
            sqlBuilder.append("sa.nominal_annual_interest_rate_overdraft as nominalAnnualInterestRateOverdraft, ");
            sqlBuilder.append("sa.min_overdraft_for_interest_calculation as minOverdraftForInterestCalculation, ");
            // sqlBuilder.append("sa.annual_fee_amount as annualFeeAmount,");
            // sqlBuilder.append("sa.annual_fee_on_month as annualFeeOnMonth,
            // ");
            // sqlBuilder.append("sa.annual_fee_on_day as annualFeeOnDay, ");
            // sqlBuilder.append("sa.annual_fee_next_due_date as
            // annualFeeNextDueDate, ");
            sqlBuilder.append("sa.total_deposits_derived as totalDeposits, ");
            sqlBuilder.append("sa.total_withdrawals_derived as totalWithdrawals, ");
            sqlBuilder.append("sa.total_withdrawal_fees_derived as totalWithdrawalFees, ");
            sqlBuilder.append("sa.total_annual_fees_derived as totalAnnualFees, ");
            sqlBuilder.append("sa.total_interest_earned_derived as totalInterestEarned, ");
            sqlBuilder.append("sa.total_interest_posted_derived as totalInterestPosted, ");
            sqlBuilder.append("sa.total_overdraft_interest_derived as totalOverdraftInterestDerived, ");
            sqlBuilder.append("sa.account_balance_derived as accountBalance, ");
            sqlBuilder.append("sa.total_fees_charge_derived as totalFeeCharge, ");
            sqlBuilder.append("sa.total_penalty_charge_derived as totalPenaltyCharge, ");
            sqlBuilder.append("sa.min_balance_for_interest_calculation as minBalanceForInterestCalculation,");
            sqlBuilder.append("sa.min_required_balance as minRequiredBalance, ");
            sqlBuilder.append("sa.enforce_min_required_balance as enforceMinRequiredBalance, ");
            sqlBuilder.append("sa.on_hold_funds_derived as onHoldFunds, ");
            sqlBuilder.append("sa.withhold_tax as withHoldTax, ");
            sqlBuilder.append("sa.total_withhold_tax_derived as totalWithholdTax, ");
            sqlBuilder.append("sa.last_interest_calculation_date as lastInterestCalculationDate, ");
            sqlBuilder.append("sa.total_savings_amount_on_hold as onHoldAmount, ");
            sqlBuilder.append("tg.id as taxGroupId, tg.name as taxGroupName, ");
            sqlBuilder.append("(select IFNULL(max(sat.transaction_date),sa.activatedon_date) ");
            sqlBuilder.append("from m_savings_account_transaction as sat ");
            sqlBuilder.append("where sat.is_reversed = 0 ");
            sqlBuilder.append("and sat.transaction_type_enum in (1,2) ");
            sqlBuilder.append("and sat.savings_account_id = sa.id) as lastActiveTransactionDate, ");
            sqlBuilder.append("sp.is_dormancy_tracking_active as isDormancyTrackingActive, ");
            sqlBuilder.append("sp.days_to_inactive as daysToInactive, ");
            sqlBuilder.append("sp.days_to_dormancy as daysToDormancy, ");
            sqlBuilder.append("sp.days_to_escheat as daysToEscheat ");
            sqlBuilder.append("from m_savings_account sa ");
            sqlBuilder.append("join m_savings_product sp ON sa.product_id = sp.id ");
            sqlBuilder.append("join m_currency curr on curr.code = sa.currency_code ");
            sqlBuilder.append("left join m_client c ON c.id = sa.client_id ");
            sqlBuilder.append("left join m_group g ON g.id = sa.group_id ");
            sqlBuilder.append("left join m_staff s ON s.id = sa.field_officer_id ");
            sqlBuilder.append("left join m_appuser sbu on sbu.id = sa.submittedon_userid ");
            sqlBuilder.append("left join m_appuser rbu on rbu.id = sa.rejectedon_userid ");
            sqlBuilder.append("left join m_appuser wbu on wbu.id = sa.withdrawnon_userid ");
            sqlBuilder.append("left join m_appuser abu on abu.id = sa.approvedon_userid ");
            sqlBuilder.append("left join m_appuser avbu on avbu.id = sa.activatedon_userid ");
            sqlBuilder.append("left join m_appuser cbu on cbu.id = sa.closedon_userid ");
            sqlBuilder.append("left join m_tax_group tg on tg.id = sa.tax_group_id  ");

            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public SavingsAccountData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String accountNo = rs.getString("accountNo");
            final String externalId = rs.getString("externalId");
            final Integer depositTypeId = rs.getInt("depositType");
            final EnumOptionData depositType = SavingsEnumerations.depositType(depositTypeId);

            final Long groupId = JdbcSupport.getLong(rs, "groupId");
            final String groupName = rs.getString("groupName");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final String clientName = rs.getString("clientName");

            final Long productId = rs.getLong("productId");
            final String productName = rs.getString("productName");

            final Long fieldOfficerId = rs.getLong("fieldOfficerId");
            final String fieldOfficerName = rs.getString("fieldOfficerName");

            final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
            final SavingsAccountStatusEnumData status = SavingsEnumerations.status(statusEnum);

            final Integer subStatusEnum = JdbcSupport.getInteger(rs, "subStatusEnum");
            final SavingsAccountSubStatusEnumData subStatus = SavingsEnumerations.subStatus(subStatusEnum);

            final LocalDate lastActiveTransactionDate = JdbcSupport.getLocalDate(rs, "lastActiveTransactionDate");
            final boolean isDormancyTrackingActive = rs.getBoolean("isDormancyTrackingActive");
            final Integer numDaysToInactive = JdbcSupport.getInteger(rs, "daysToInactive");
            final Integer numDaysToDormancy = JdbcSupport.getInteger(rs, "daysToDormancy");
            final Integer numDaysToEscheat = JdbcSupport.getInteger(rs, "daysToEscheat");
            Integer daysToInactive = null;
            Integer daysToDormancy = null;
            Integer daysToEscheat = null;

            final LocalDate localTenantDate = DateUtils.getLocalDateOfTenant();
            if (isDormancyTrackingActive && statusEnum.equals(SavingsAccountStatusType.ACTIVE.getValue())) {
                if (subStatusEnum < SavingsAccountSubStatusEnum.ESCHEAT.getValue()) {
                    daysToEscheat = Days.daysBetween(localTenantDate, lastActiveTransactionDate.plusDays(numDaysToEscheat)).getDays();
                }
                if (subStatusEnum < SavingsAccountSubStatusEnum.DORMANT.getValue()) {
                    daysToDormancy = Days.daysBetween(localTenantDate, lastActiveTransactionDate.plusDays(numDaysToDormancy)).getDays();
                }
                if (subStatusEnum < SavingsAccountSubStatusEnum.INACTIVE.getValue()) {
                    daysToInactive = Days.daysBetween(localTenantDate, lastActiveTransactionDate.plusDays(numDaysToInactive)).getDays();
                }
            }

            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final String submittedByUsername = rs.getString("submittedByUsername");
            final String submittedByFirstname = rs.getString("submittedByFirstname");
            final String submittedByLastname = rs.getString("submittedByLastname");

            final LocalDate rejectedOnDate = JdbcSupport.getLocalDate(rs, "rejectedOnDate");
            final String rejectedByUsername = rs.getString("rejectedByUsername");
            final String rejectedByFirstname = rs.getString("rejectedByFirstname");
            final String rejectedByLastname = rs.getString("rejectedByLastname");

            final LocalDate withdrawnOnDate = JdbcSupport.getLocalDate(rs, "withdrawnOnDate");
            final String withdrawnByUsername = rs.getString("withdrawnByUsername");
            final String withdrawnByFirstname = rs.getString("withdrawnByFirstname");
            final String withdrawnByLastname = rs.getString("withdrawnByLastname");

            final LocalDate approvedOnDate = JdbcSupport.getLocalDate(rs, "approvedOnDate");
            final String approvedByUsername = rs.getString("approvedByUsername");
            final String approvedByFirstname = rs.getString("approvedByFirstname");
            final String approvedByLastname = rs.getString("approvedByLastname");

            final LocalDate activatedOnDate = JdbcSupport.getLocalDate(rs, "activatedOnDate");
            final String activatedByUsername = rs.getString("activatedByUsername");
            final String activatedByFirstname = rs.getString("activatedByFirstname");
            final String activatedByLastname = rs.getString("activatedByLastname");

            final LocalDate closedOnDate = JdbcSupport.getLocalDate(rs, "closedOnDate");
            final String closedByUsername = rs.getString("closedByUsername");
            final String closedByFirstname = rs.getString("closedByFirstname");
            final String closedByLastname = rs.getString("closedByLastname");

            final SavingsAccountApplicationTimelineData timeline = new SavingsAccountApplicationTimelineData(submittedOnDate,
                    submittedByUsername, submittedByFirstname, submittedByLastname, rejectedOnDate, rejectedByUsername, rejectedByFirstname,
                    rejectedByLastname, withdrawnOnDate, withdrawnByUsername, withdrawnByFirstname, withdrawnByLastname, approvedOnDate,
                    approvedByUsername, approvedByFirstname, approvedByLastname, activatedOnDate, activatedByUsername, activatedByFirstname,
                    activatedByLastname, closedOnDate, closedByUsername, closedByFirstname, closedByLastname);

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf, currencyDisplaySymbol,
                    currencyNameCode);

            final BigDecimal nominalAnnualInterestRate = rs.getBigDecimal("nominalAnnualInterestRate");

            final EnumOptionData interestCompoundingPeriodType = SavingsEnumerations.compoundingInterestPeriodType(
                    SavingsCompoundingInterestPeriodType.fromInt(JdbcSupport.getInteger(rs, "interestCompoundingPeriodType")));

            final EnumOptionData interestPostingPeriodType = SavingsEnumerations.interestPostingPeriodType(
                    SavingsPostingInterestPeriodType.fromInt(JdbcSupport.getInteger(rs, "interestPostingPeriodType")));

            final EnumOptionData interestCalculationType = SavingsEnumerations
                    .interestCalculationType(SavingsInterestCalculationType.fromInt(JdbcSupport.getInteger(rs, "interestCalculationType")));

            final EnumOptionData interestCalculationDaysInYearType = SavingsEnumerations.interestCalculationDaysInYearType(
                    SavingsInterestCalculationDaysInYearType.fromInt(JdbcSupport.getInteger(rs, "interestCalculationDaysInYearType")));

            final BigDecimal minRequiredOpeningBalance = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "minRequiredOpeningBalance");

            final Integer lockinPeriodFrequency = JdbcSupport.getInteger(rs, "lockinPeriodFrequency");
            EnumOptionData lockinPeriodFrequencyType = null;
            final Integer lockinPeriodFrequencyTypeValue = JdbcSupport.getInteger(rs, "lockinPeriodFrequencyType");
            if (lockinPeriodFrequencyTypeValue != null) {
                final SavingsPeriodFrequencyType lockinPeriodType = SavingsPeriodFrequencyType.fromInt(lockinPeriodFrequencyTypeValue);
                lockinPeriodFrequencyType = SavingsEnumerations.lockinPeriodFrequencyType(lockinPeriodType);
            }

            final boolean withdrawalFeeForTransfers = rs.getBoolean("withdrawalFeeForTransfers");

            final boolean allowOverdraft = rs.getBoolean("allowOverdraft");
            final BigDecimal overdraftLimit = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "overdraftLimit");
            final BigDecimal nominalAnnualInterestRateOverdraft = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs,
                    "nominalAnnualInterestRateOverdraft");
            final BigDecimal minOverdraftForInterestCalculation = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs,
                    "minOverdraftForInterestCalculation");

            final boolean releaseguarantor = rs.getBoolean("releaseguarantor");
            final BigDecimal minRequiredBalance = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "minRequiredBalance");
            final boolean enforceMinRequiredBalance = rs.getBoolean("enforceMinRequiredBalance");

            final BigDecimal totalDeposits = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalDeposits");
            final BigDecimal totalWithdrawals = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalWithdrawals");
            final BigDecimal totalWithdrawalFees = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalWithdrawalFees");
            final BigDecimal totalAnnualFees = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalAnnualFees");

            final BigDecimal totalInterestEarned = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalInterestEarned");
            final BigDecimal totalInterestPosted = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalInterestPosted");
            final BigDecimal accountBalance = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "accountBalance");
            final BigDecimal totalFeeCharge = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalFeeCharge");
            final BigDecimal totalPenaltyCharge = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalPenaltyCharge");
            final BigDecimal totalOverdraftInterestDerived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs,
                    "totalOverdraftInterestDerived");
            final BigDecimal totalWithholdTax = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "totalWithholdTax");

            final BigDecimal minBalanceForInterestCalculation = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs,
                    "minBalanceForInterestCalculation");
            final BigDecimal onHoldFunds = rs.getBigDecimal("onHoldFunds");

            final BigDecimal onHoldAmount = rs.getBigDecimal("onHoldAmount");

            BigDecimal availableBalance = accountBalance;
            if (availableBalance != null && onHoldFunds != null) {

                availableBalance = availableBalance.subtract(onHoldFunds);
            }

            if (availableBalance != null && onHoldAmount != null) {

                availableBalance = availableBalance.subtract(onHoldAmount);
            }

            BigDecimal interestNotPosted = BigDecimal.ZERO;
            LocalDate lastInterestCalculationDate = null;
            if (totalInterestEarned != null) {
                interestNotPosted = totalInterestEarned.subtract(totalInterestPosted).add(totalOverdraftInterestDerived);
                lastInterestCalculationDate = JdbcSupport.getLocalDate(rs, "lastInterestCalculationDate");
            }

            final SavingsAccountSummaryData summary = new SavingsAccountSummaryData(currency, totalDeposits, totalWithdrawals,
                    totalWithdrawalFees, totalAnnualFees, totalInterestEarned, totalInterestPosted, accountBalance, totalFeeCharge,
                    totalPenaltyCharge, totalOverdraftInterestDerived, totalWithholdTax, interestNotPosted, lastInterestCalculationDate,
                    availableBalance);

            final boolean withHoldTax = rs.getBoolean("withHoldTax");
            final Long taxGroupId = JdbcSupport.getLong(rs, "taxGroupId");
            final String taxGroupName = rs.getString("taxGroupName");
            TaxGroupData taxGroupData = null;
            if (taxGroupId != null) {
                taxGroupData = TaxGroupData.lookup(taxGroupId, taxGroupName);
            }
            final SavingsAccountDpDetailsData savingsAccountDpDetailsData = null;
            return SavingsAccountData.instance(id, accountNo, depositType, externalId, groupId, groupName, clientId, clientName, productId,
                    productName, fieldOfficerId, fieldOfficerName, status, subStatus, timeline, currency, nominalAnnualInterestRate,
                    interestCompoundingPeriodType, interestPostingPeriodType, interestCalculationType, interestCalculationDaysInYearType,
                    minRequiredOpeningBalance, lockinPeriodFrequency, lockinPeriodFrequencyType, withdrawalFeeForTransfers, summary,
                    allowOverdraft, overdraftLimit, minRequiredBalance, enforceMinRequiredBalance, minBalanceForInterestCalculation,
                    onHoldFunds, releaseguarantor, nominalAnnualInterestRateOverdraft, minOverdraftForInterestCalculation, withHoldTax,
                    taxGroupData, lastActiveTransactionDate, isDormancyTrackingActive, daysToInactive, daysToDormancy, daysToEscheat,
                    savingsAccountDpDetailsData, onHoldAmount);
        }
    }

    private static final class SavingAccountMapperForLookup implements RowMapper<SavingsAccountData> {

        private final String schemaSql;

        public SavingAccountMapperForLookup() {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("sa.id as id, sa.account_no as accountNo, ");
            sqlBuilder.append("sa.deposit_type_enum as depositType, ");
            sqlBuilder.append("sp.id as productId, sp.name as productName, ");
            sqlBuilder.append("sa.status_enum as statusEnum ");

            sqlBuilder.append("from m_savings_account sa ");
            sqlBuilder.append("join m_savings_product sp ON sa.product_id = sp.id ");

            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public SavingsAccountData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String accountNo = rs.getString("accountNo");
            final Integer depositTypeId = rs.getInt("depositType");
            final EnumOptionData depositType = SavingsEnumerations.depositType(depositTypeId);

            final Long productId = rs.getLong("productId");
            final String productName = rs.getString("productName");

            final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
            final SavingsAccountStatusEnumData status = SavingsEnumerations.status(statusEnum);

            return SavingsAccountData.lookupWithProductDetails(id, accountNo, depositType, productId, productName, status);
        }
    }

    @Override
    public SavingsAccountData retrieveTemplate(final Long clientId, final Long groupId, final Long productId,
            final boolean staffInSelectedOfficeOnly, SavingsAccountDpDetailsData savingsAccountDpDetailsData) {

        final AppUser loggedInUser = this.context.authenticatedUser();
        Long officeId = loggedInUser.getOffice().getId();

        ClientData client = null;
        if (clientId != null) {
            client = this.clientReadPlatformService.retrieveOne(clientId);
            officeId = client.officeId();
        }

        GroupGeneralData group = null;
        if (groupId != null) {
            group = this.groupReadPlatformService.retrieveOne(groupId);
            officeId = group.officeId();
        }

        final Collection<SavingsProductData> productOptions = this.savingsProductReadPlatformService.retrieveAllForLookup();
        SavingsAccountData template = null;
        if (productId != null) {

            final SavingAccountTemplateMapper mapper = new SavingAccountTemplateMapper(client, group);

            final String sql = "select " + mapper.schema() + " where sp.id = ?";
            template = this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { productId });

            final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions = this.dropdownReadPlatformService
                    .retrieveCompoundingInterestPeriodTypeOptions();

            final Collection<EnumOptionData> interestPostingPeriodTypeOptions = this.dropdownReadPlatformService
                    .retrieveInterestPostingPeriodTypeOptions();

            final Collection<EnumOptionData> interestCalculationTypeOptions = this.dropdownReadPlatformService
                    .retrieveInterestCalculationTypeOptions();

            final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions = this.dropdownReadPlatformService
                    .retrieveInterestCalculationDaysInYearTypeOptions();

            final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions = this.dropdownReadPlatformService
                    .retrieveLockinPeriodFrequencyTypeOptions();

            final Collection<EnumOptionData> withdrawalFeeTypeOptions = this.dropdownReadPlatformService.retrievewithdrawalFeeTypeOptions();

            final Collection<GuarantorData> guarantors = null;

            final Collection<EnumOptionData> savingsDpLimitCalculationTypeOptions = this.dropdownReadPlatformService
                    .retrieveSavingsDpLimitCalculationTypeOptions();

            final Collection<SavingsAccountTransactionData> transactions = null;
            final Collection<ChargeData> productCharges = this.chargeReadPlatformService.retrieveSavingsProductCharges(productId);
            // update charges from Product charges
            final Collection<SavingsAccountChargeData> charges = fromChargesToSavingsCharges(productCharges);

            final boolean feeChargesOnly = false;
            final Collection<ChargeData> chargeOptions = this.chargeReadPlatformService
                    .retrieveSavingsProductApplicableCharges(feeChargesOnly);

            Collection<StaffData> fieldOfficerOptions = null;

            if (officeId != null) {

                if (staffInSelectedOfficeOnly) {
                    // only bring back loan officers in selected branch/office
                    final Collection<StaffData> fieldOfficersInBranch = this.staffReadPlatformService
                            .retrieveAllLoanOfficersInOfficeById(officeId);

                    if (!CollectionUtils.isEmpty(fieldOfficersInBranch)) {
                        fieldOfficerOptions = new ArrayList<>(fieldOfficersInBranch);
                    }
                } else {
                    // by default bring back all officers in selected
                    // branch/office as well as officers in office above
                    // this office
                    final boolean restrictToLoanOfficersOnly = true;
                    final Collection<StaffData> loanOfficersInHierarchy = this.staffReadPlatformService
                            .retrieveAllStaffInOfficeAndItsParentOfficeHierarchy(officeId, restrictToLoanOfficersOnly);

                    if (!CollectionUtils.isEmpty(loanOfficersInHierarchy)) {
                        fieldOfficerOptions = new ArrayList<>(loanOfficersInHierarchy);
                    }
                }
            }
            if (savingsAccountDpDetailsData == null) {
                savingsAccountDpDetailsData = template.getSavingsAccountDpDetailsData();
            }

            template = SavingsAccountData.withTemplateOptions(template, productOptions, fieldOfficerOptions,
                    interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions, interestCalculationTypeOptions,
                    interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions, withdrawalFeeTypeOptions, transactions,
                    charges, chargeOptions, savingsDpLimitCalculationTypeOptions, savingsAccountDpDetailsData, guarantors);
        } else {

            String clientName = null;
            if (client != null) {
                clientName = client.displayName();
            }

            String groupName = null;
            if (group != null) {
                groupName = group.getName();
            }

            template = SavingsAccountData.withClientTemplate(clientId, clientName, groupId, groupName);

            final Collection<StaffData> fieldOfficerOptions = null;
            final Collection<EnumOptionData> interestCompoundingPeriodTypeOptions = null;
            final Collection<EnumOptionData> interestPostingPeriodTypeOptions = null;
            final Collection<EnumOptionData> interestCalculationTypeOptions = null;
            final Collection<EnumOptionData> interestCalculationDaysInYearTypeOptions = null;
            final Collection<EnumOptionData> lockinPeriodFrequencyTypeOptions = null;
            final Collection<EnumOptionData> withdrawalFeeTypeOptions = null;

            final Collection<SavingsAccountTransactionData> transactions = null;
            final Collection<SavingsAccountChargeData> charges = null;
            final Collection<GuarantorData> guarantors = null;

            final Collection<EnumOptionData> savingsDpLimitCalculationTypeOptions = null;

            final boolean feeChargesOnly = false;
            final Collection<ChargeData> chargeOptions = this.chargeReadPlatformService
                    .retrieveSavingsProductApplicableCharges(feeChargesOnly);

            template = SavingsAccountData.withTemplateOptions(template, productOptions, fieldOfficerOptions,
                    interestCompoundingPeriodTypeOptions, interestPostingPeriodTypeOptions, interestCalculationTypeOptions,
                    interestCalculationDaysInYearTypeOptions, lockinPeriodFrequencyTypeOptions, withdrawalFeeTypeOptions, transactions,
                    charges, chargeOptions, savingsDpLimitCalculationTypeOptions, savingsAccountDpDetailsData, guarantors);
        }

        return template;
    }

    private Collection<SavingsAccountChargeData> fromChargesToSavingsCharges(final Collection<ChargeData> productCharges) {
        final Collection<SavingsAccountChargeData> savingsCharges = new ArrayList<>();
        for (final ChargeData chargeData : productCharges) {
            final SavingsAccountChargeData savingsCharge = chargeData.toSavingsAccountChargeData();
            savingsCharges.add(savingsCharge);
        }
        return savingsCharges;
    }

    @Override
    public SavingsAccountTransactionData retrieveDepositTransactionTemplate(final Long savingsId,
            final DepositAccountType depositAccountType) {

        try {
            final String sql = "select " + this.transactionTemplateMapper.schema() + " where sa.id = ? and sa.deposit_type_enum = ?";

            return this.jdbcTemplate.queryForObject(sql, this.transactionTemplateMapper,
                    new Object[] { savingsId, depositAccountType.getValue() });
        } catch (final EmptyResultDataAccessException e) {
            throw new SavingsAccountNotFoundException(savingsId);
        }
    }

    @Override
    public Collection<SavingsAccountTransactionData> retrieveAllTransactions(final Long savingsId,
            final DepositAccountType depositAccountType) {

        final String sql = "select " + this.transactionsMapper.schema()
                + " where sa.id = ? and sa.deposit_type_enum = ? order by tr.transaction_date DESC, tr.created_date DESC, tr.id DESC";

        return this.jdbcTemplate.query(sql, this.transactionsMapper, new Object[] { savingsId, depositAccountType.getValue() });
    }

    @Override
    public SavingsAccountTransactionData retrieveSavingsTransaction(final Long savingsId, final Long transactionId,
            final DepositAccountType depositAccountType) {

        final String sql = "select " + this.transactionsMapper.schema() + " where sa.id = ? and sa.deposit_type_enum = ? and tr.id= ?";

        return this.jdbcTemplate.queryForObject(sql, this.transactionsMapper,
                new Object[] { savingsId, depositAccountType.getValue(), transactionId });
    }

    @Override
    public Collection<SavingsAccountTransactionData> retrieveAllTransactions(final Long savingsId,
            @SuppressWarnings("unused") final DepositAccountType depositAccountType, final SearchParameters searchParameters) {
        final StringBuilder builder = new StringBuilder(400);
        final SavingsAccountTransactionsMapper mapper = new SavingsAccountTransactionsMapper();
        final List<Object> params = new ArrayList<>();
        builder.append("select ");
        builder.append(mapper.schema());
        builder.append(" where sa.id = ?");
        params.add(savingsId);

        final Map<String, String> searchConditions = searchParameters.getSearchConditions();
        searchConditions.forEach((key, value) -> {
            switch (key) {
                case CommonConstants.SAVINGS_ACCOUNT_NO:
                    builder.append(" and ( sa.account_no = '").append(value).append("' ) ");
                break;
                default:
                break;
            }
        });

        if (searchParameters.getStartDate() != null) {
            final LocalDate startDate = new LocalDate(searchParameters.getStartDate());
            LocalDate endDate = DateUtils.getLocalDateOfTenant();
            if (searchParameters.getEndDate() != null) {
                endDate = new LocalDate(searchParameters.getEndDate());
            }
            builder.append(" and tr.transaction_date between ? and ? ");
            params.add(this.formatter.print(startDate));
            params.add(this.formatter.print(endDate));
        }

        if (searchParameters.getTransactionsCount() == null) {
            if (searchParameters.isOrderByRequested()) {
                builder.append(" order by ").append(searchParameters.getOrderBy());

                if (searchParameters.isSortOrderProvided()) {
                    builder.append(' ').append(searchParameters.getSortOrder());
                }
            }

            if (searchParameters.isLimited()) {
                builder.append(" limit ").append(searchParameters.getLimit());
                if (searchParameters.isOffset()) {
                    builder.append(" offset ").append(searchParameters.getOffset());
                }
            }
        } else {

            builder.append(" order by tr.transaction_date DESC, tr.created_date DESC,tr.id DESC limit ? ");
            params.add(searchParameters.getTransactionsCount());
        }
        final Object[] paramArray = params.toArray();

        return this.jdbcTemplate.query(builder.toString(), mapper, paramArray);
    }

    private static final class SavingsAccountTransactionsMapper implements RowMapper<SavingsAccountTransactionData> {

        private final String schemaSql;

        public SavingsAccountTransactionsMapper() {

            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("tr.id as transactionId, tr.transaction_type_enum as transactionType, ");
            sqlBuilder.append("tr.transaction_date as transactionDate, tr.amount as transactionAmount,");
            sqlBuilder.append("tr.created_date as submittedOnDate,");
            sqlBuilder.append("tr.running_balance_derived as runningBalance, tr.is_reversed as reversed,");
            sqlBuilder.append("fromtran.id as fromTransferId, fromtran.is_reversed as fromTransferReversed,");
            sqlBuilder.append("fromtran.transaction_date as fromTransferDate, fromtran.amount as fromTransferAmount,");
            sqlBuilder.append("fromtran.description as fromTransferDescription,");
            sqlBuilder.append("totran.id as toTransferId, totran.is_reversed as toTransferReversed,");
            sqlBuilder.append("totran.transaction_date as toTransferDate, totran.amount as toTransferAmount,");
            sqlBuilder.append("totran.description as toTransferDescription,");
            sqlBuilder.append("sa.id as savingsId, sa.account_no as accountNo,");
            sqlBuilder.append("pd.payment_type_id as paymentType,pd.account_number as accountNumber,pd.check_number as checkNumber, ");
            sqlBuilder.append("pd.receipt_number as receiptNumber, pd.bank_number as bankNumber,pd.routing_code as routingCode, ");
            sqlBuilder.append(
                    "sa.currency_code as currencyCode, sa.currency_digits as currencyDigits, sa.currency_multiplesof as inMultiplesOf, ");
            sqlBuilder.append("curr.name as currencyName, curr.internationalized_name_code as currencyNameCode, ");
            sqlBuilder.append("curr.display_symbol as currencyDisplaySymbol, ");
            sqlBuilder.append("pt.value as paymentTypeName, ");
            sqlBuilder.append("tr.is_manual as postInterestAsOn ");
            sqlBuilder.append("from m_savings_account sa ");
            sqlBuilder.append("join m_savings_account_transaction tr on tr.savings_account_id = sa.id ");
            sqlBuilder.append("join m_currency curr on curr.code = sa.currency_code ");
            sqlBuilder.append("left join m_account_transfer_transaction fromtran on fromtran.from_savings_transaction_id = tr.id ");
            sqlBuilder.append("left join m_account_transfer_transaction totran on totran.to_savings_transaction_id = tr.id ");
            sqlBuilder.append("left join m_payment_detail pd on tr.payment_detail_id = pd.id ");
            sqlBuilder.append("left join m_payment_type pt on pd.payment_type_id = pt.id ");

            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public SavingsAccountTransactionData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("transactionId");
            final int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
            final SavingsAccountTransactionEnumData transactionType = SavingsEnumerations.transactionType(transactionTypeInt);

            final LocalDate date = JdbcSupport.getLocalDate(rs, "transactionDate");
            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final BigDecimal amount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "transactionAmount");
            final BigDecimal runningBalance = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "runningBalance");
            final boolean reversed = rs.getBoolean("reversed");

            final Long savingsId = rs.getLong("savingsId");
            final String accountNo = rs.getString("accountNo");
            final boolean postInterestAsOn = rs.getBoolean("postInterestAsOn");

            PaymentDetailData paymentDetailData = null;
            if (transactionType.isDepositOrWithdrawal()) {
                final Long paymentTypeId = JdbcSupport.getLong(rs, "paymentType");
                if (paymentTypeId != null) {
                    final String typeName = rs.getString("paymentTypeName");
                    final PaymentTypeData paymentType = PaymentTypeData.instance(paymentTypeId, typeName);
                    final String accountNumber = rs.getString("accountNumber");
                    final String checkNumber = rs.getString("checkNumber");
                    final String routingCode = rs.getString("routingCode");
                    final String receiptNumber = rs.getString("receiptNumber");
                    final String bankNumber = rs.getString("bankNumber");
                    final String branchName = null;
                    final Date paymentDate = null;
                    paymentDetailData = new PaymentDetailData(id, paymentType, accountNumber, checkNumber, routingCode, receiptNumber,
                            bankNumber, branchName, paymentDate);
                }
            }

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf, currencyDisplaySymbol,
                    currencyNameCode);

            AccountTransferData transfer = null;
            final Long fromTransferId = JdbcSupport.getLong(rs, "fromTransferId");
            final Long toTransferId = JdbcSupport.getLong(rs, "toTransferId");
            if (fromTransferId != null) {
                final LocalDate fromTransferDate = JdbcSupport.getLocalDate(rs, "fromTransferDate");
                final BigDecimal fromTransferAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "fromTransferAmount");
                final boolean fromTransferReversed = rs.getBoolean("fromTransferReversed");
                final String fromTransferDescription = rs.getString("fromTransferDescription");

                transfer = AccountTransferData.transferBasicDetails(fromTransferId, currency, fromTransferAmount, fromTransferDate,
                        fromTransferDescription, fromTransferReversed);
            } else if (toTransferId != null) {
                final LocalDate toTransferDate = JdbcSupport.getLocalDate(rs, "toTransferDate");
                final BigDecimal toTransferAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "toTransferAmount");
                final boolean toTransferReversed = rs.getBoolean("toTransferReversed");
                final String toTransferDescription = rs.getString("toTransferDescription");

                transfer = AccountTransferData.transferBasicDetails(toTransferId, currency, toTransferAmount, toTransferDate,
                        toTransferDescription, toTransferReversed);
            }

            return SavingsAccountTransactionData.create(id, transactionType, paymentDetailData, savingsId, accountNo, date, currency,
                    amount, runningBalance, reversed, transfer, submittedOnDate, postInterestAsOn);
        }
    }

    private static final class SavingsAccountTransactionTemplateMapper implements RowMapper<SavingsAccountTransactionData> {

        private final String schemaSql;

        public SavingsAccountTransactionTemplateMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("sa.id as id, sa.account_no as accountNo, ");
            sqlBuilder.append(
                    "sa.currency_code as currencyCode, sa.currency_digits as currencyDigits, sa.currency_multiplesof as inMultiplesOf, ");
            sqlBuilder.append("curr.name as currencyName, curr.internationalized_name_code as currencyNameCode, ");
            sqlBuilder.append("curr.display_symbol as currencyDisplaySymbol, ");
            sqlBuilder.append("sa.min_required_opening_balance as minRequiredOpeningBalance ");
            sqlBuilder.append("from m_savings_account sa ");
            sqlBuilder.append("join m_currency curr on curr.code = sa.currency_code ");

            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public SavingsAccountTransactionData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long savingsId = rs.getLong("id");
            final String accountNo = rs.getString("accountNo");

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf, currencyDisplaySymbol,
                    currencyNameCode);

            return SavingsAccountTransactionData.template(savingsId, accountNo, DateUtils.getLocalDateOfTenant(), currency);
        }
    }

    private static final class SavingAccountTemplateMapper implements RowMapper<SavingsAccountData> {

        private final ClientData client;
        private final GroupGeneralData group;

        private final String schemaSql;

        public SavingAccountTemplateMapper(final ClientData client, final GroupGeneralData group) {
            this.client = client;
            this.group = group;

            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("sp.id as productId, sp.name as productName, ");
            sqlBuilder.append(
                    "sp.currency_code as currencyCode, sp.currency_digits as currencyDigits, sp.currency_multiplesof as inMultiplesOf, ");
            sqlBuilder.append("curr.name as currencyName, curr.internationalized_name_code as currencyNameCode, ");
            sqlBuilder.append("curr.display_symbol as currencyDisplaySymbol, ");
            sqlBuilder.append("sp.nominal_annual_interest_rate as nominalAnnualIterestRate, ");
            sqlBuilder.append("sp.interest_compounding_period_enum as interestCompoundingPeriodType, ");
            sqlBuilder.append("sp.interest_posting_period_enum as interestPostingPeriodType, ");
            sqlBuilder.append("sp.interest_calculation_type_enum as interestCalculationType, ");
            sqlBuilder.append("sp.interest_calculation_days_in_year_type_enum as interestCalculationDaysInYearType, ");
            sqlBuilder.append("sp.min_required_opening_balance as minRequiredOpeningBalance, ");
            sqlBuilder.append("sp.lockin_period_frequency as lockinPeriodFrequency,");
            sqlBuilder.append("sp.lockin_period_frequency_enum as lockinPeriodFrequencyType, ");
            sqlBuilder.append("sp.withdrawal_fee_for_transfer as withdrawalFeeForTransfers, ");
            sqlBuilder.append("sp.min_balance_for_interest_calculation as minBalanceForInterestCalculation, ");
            sqlBuilder.append("sp.allow_overdraft as allowOverdraft, ");
            sqlBuilder.append("sp.overdraft_limit as overdraftLimit, ");
            sqlBuilder.append("sp.release_guarantor as releaseguarantor, ");
            sqlBuilder.append("sp.nominal_annual_interest_rate_overdraft as nominalAnnualInterestRateOverdraft, ");
            sqlBuilder.append("sp.min_overdraft_for_interest_calculation as minOverdraftForInterestCalculation, ");
            sqlBuilder.append("sp.withhold_tax as withHoldTax,");
            sqlBuilder.append("tg.id as taxGroupId, tg.name as taxGroupName, ");
            sqlBuilder.append("sp.min_required_balance as minRequiredBalance, ");
            sqlBuilder.append("sp.enforce_min_required_balance as enforceMinRequiredBalance ");
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
        public SavingsAccountData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long productId = rs.getLong("productId");
            final String productName = rs.getString("productName");

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf, currencyDisplaySymbol,
                    currencyNameCode);

            final BigDecimal nominalAnnualIterestRate = rs.getBigDecimal("nominalAnnualIterestRate");

            final EnumOptionData interestCompoundingPeriodType = SavingsEnumerations.compoundingInterestPeriodType(
                    SavingsCompoundingInterestPeriodType.fromInt(JdbcSupport.getInteger(rs, "interestCompoundingPeriodType")));

            final EnumOptionData interestPostingPeriodType = SavingsEnumerations.interestPostingPeriodType(
                    SavingsPostingInterestPeriodType.fromInt(JdbcSupport.getInteger(rs, "interestPostingPeriodType")));

            final EnumOptionData interestCalculationType = SavingsEnumerations
                    .interestCalculationType(SavingsInterestCalculationType.fromInt(JdbcSupport.getInteger(rs, "interestCalculationType")));

            final EnumOptionData interestCalculationDaysInYearType = SavingsEnumerations.interestCalculationDaysInYearType(
                    SavingsInterestCalculationDaysInYearType.fromInt(JdbcSupport.getInteger(rs, "interestCalculationDaysInYearType")));

            final BigDecimal minRequiredOpeningBalance = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "minRequiredOpeningBalance");

            final Integer lockinPeriodFrequency = JdbcSupport.getInteger(rs, "lockinPeriodFrequency");
            EnumOptionData lockinPeriodFrequencyType = null;
            final Integer lockinPeriodFrequencyTypeValue = JdbcSupport.getInteger(rs, "lockinPeriodFrequencyType");
            if (lockinPeriodFrequencyTypeValue != null) {
                final SavingsPeriodFrequencyType lockinPeriodType = SavingsPeriodFrequencyType.fromInt(lockinPeriodFrequencyTypeValue);
                lockinPeriodFrequencyType = SavingsEnumerations.lockinPeriodFrequencyType(lockinPeriodType);
            }

            final boolean withdrawalFeeForTransfers = rs.getBoolean("withdrawalFeeForTransfers");

            final boolean allowOverdraft = rs.getBoolean("allowOverdraft");
            final boolean releaseguarantor = rs.getBoolean("releaseguarantor");
            final BigDecimal overdraftLimit = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "overdraftLimit");
            final BigDecimal nominalAnnualInterestRateOverdraft = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs,
                    "nominalAnnualInterestRateOverdraft");
            final BigDecimal minOverdraftForInterestCalculation = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs,
                    "minOverdraftForInterestCalculation");

            final BigDecimal minRequiredBalance = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "minRequiredBalance");
            final boolean enforceMinRequiredBalance = rs.getBoolean("enforceMinRequiredBalance");
            final BigDecimal minBalanceForInterestCalculation = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs,
                    "minBalanceForInterestCalculation");

            final boolean withHoldTax = rs.getBoolean("withHoldTax");
            final Long taxGroupId = JdbcSupport.getLong(rs, "taxGroupId");
            final String taxGroupName = rs.getString("taxGroupName");
            TaxGroupData taxGroupData = null;
            if (taxGroupId != null) {
                taxGroupData = TaxGroupData.lookup(taxGroupId, taxGroupName);
            }

            Long clientId = null;
            String clientName = null;
            if (this.client != null) {
                clientId = this.client.id();
                clientName = this.client.displayName();
            }

            Long groupId = null;
            String groupName = null;
            if (this.group != null) {
                groupId = this.group.getId();
                groupName = this.group.getName();
            }

            final Long fieldOfficerId = null;
            final String fieldOfficerName = null;
            final SavingsAccountStatusEnumData status = null;
            // final LocalDate annualFeeNextDueDate = null;
            final SavingsAccountSummaryData summary = null;
            final BigDecimal onHoldFunds = null;
            final BigDecimal savingsAmountOnHold = null;

            final SavingsAccountSubStatusEnumData subStatus = null;
            final LocalDate lastActiveTransactionDate = null;
            final boolean isDormancyTrackingActive = false;
            final Integer daysToInactive = null;
            final Integer daysToDormancy = null;
            final Integer daysToEscheat = null;

            final SavingsAccountApplicationTimelineData timeline = SavingsAccountApplicationTimelineData.templateDefault();
            final EnumOptionData depositType = null;

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

            /**
             * {@link SavingsAccountDpDetailsData}
             */
            final Long id = null;
            final Long savingsAccountId = null;
            final Integer duration = null;
            final BigDecimal amount = null;
            final BigDecimal dpAmount = null;
            final EnumOptionData calculationType = null;
            final BigDecimal amountOrPercentage = null;
            final Date savingsActivatedonDate = null;
            final Date startDate = null;

            /**
             * {@link SavingsProductDrawingPowerDetailsData}
             */
            final SavingsProductDrawingPowerDetailsData savingsProductDrawingPowerDetailsData = SavingsProductDrawingPowerDetailsData
                    .createNew(frequencyType, frequencyInterval, frequencyNthDay, frequencyDayOfWeekType, frequencyOnDay);

            final SavingsAccountDpDetailsData savingsAccountDpDetailsData = SavingsAccountDpDetailsData.createNew(id, savingsAccountId,
                    duration, amount, dpAmount, calculationType, amountOrPercentage, savingsActivatedonDate, startDate,
                    savingsProductDrawingPowerDetailsData);

            return SavingsAccountData.instance(null, null, depositType, null, groupId, groupName, clientId, clientName, productId,
                    productName, fieldOfficerId, fieldOfficerName, status, subStatus, timeline, currency, nominalAnnualIterestRate,
                    interestCompoundingPeriodType, interestPostingPeriodType, interestCalculationType, interestCalculationDaysInYearType,
                    minRequiredOpeningBalance, lockinPeriodFrequency, lockinPeriodFrequencyType, withdrawalFeeForTransfers, summary,
                    allowOverdraft, overdraftLimit, minRequiredBalance, enforceMinRequiredBalance, minBalanceForInterestCalculation,
                    onHoldFunds, releaseguarantor, nominalAnnualInterestRateOverdraft, minOverdraftForInterestCalculation, withHoldTax,
                    taxGroupData, lastActiveTransactionDate, isDormancyTrackingActive, daysToInactive, daysToDormancy, daysToEscheat,
                    savingsAccountDpDetailsData, savingsAmountOnHold);
        }
    }

    @Override
    public Collection<SavingsAccountData> retrieveForLookup(final Long clientId, final Boolean overdraft) {

        final SavingAccountMapperForLookup accountMapperForLookup = new SavingAccountMapperForLookup();
        final StringBuilder sqlBuilder = new StringBuilder("select " + accountMapperForLookup.schema());
        sqlBuilder.append(" where sa.client_id = ? and sa.status_enum = 300");
        Object[] queryParameters = null;
        if (overdraft == null) {
            queryParameters = new Object[] { clientId };
        } else {
            sqlBuilder.append(" and sa.allow_overdraft = ?");
            queryParameters = new Object[] { clientId, overdraft };
        }
        return this.jdbcTemplate.query(sqlBuilder.toString(), accountMapperForLookup, queryParameters);

    }

    @Override
    public List<Long> retrieveSavingsIdsPendingInactive(final LocalDate tenantLocalDate) {
        List<Long> ret = null;
        final StringBuilder sql = new StringBuilder("select sa.id ");
        sql.append(" from m_savings_account as sa ");
        sql.append(" inner join m_savings_product as sp on (sa.product_id = sp.id and sp.is_dormancy_tracking_active = 1) ");
        sql.append(" where sa.status_enum = 300 ");
        sql.append(" and sa.sub_status_enum = 0 ");
        sql.append(" and DATEDIFF(?,(select IFNULL(max(sat.transaction_date),sa.activatedon_date) ");
        sql.append(" from m_savings_account_transaction as sat ");
        sql.append(" where sat.is_reversed = 0 ");
        sql.append(" and sat.transaction_type_enum in (1,2) ");
        sql.append(" and sat.savings_account_id = sa.id)) >= sp.days_to_inactive ");

        try {
            ret = this.jdbcTemplate.queryForList(sql.toString(), Long.class, new Object[] { this.formatter.print(tenantLocalDate) });
        } catch (final EmptyResultDataAccessException e) {
            // ignore empty result scenario
        } catch (final DataAccessException e) {
            throw e;
        }

        return ret;
    }

    @Override
    public List<Long> retrieveSavingsIdsPendingDormant(final LocalDate tenantLocalDate) {
        List<Long> ret = null;
        final StringBuilder sql = new StringBuilder("select sa.id ");
        sql.append(" from m_savings_account as sa ");
        sql.append(" inner join m_savings_product as sp on (sa.product_id = sp.id and sp.is_dormancy_tracking_active = 1) ");
        sql.append(" where sa.status_enum = 300 ");
        sql.append(" and sa.sub_status_enum = 100 ");
        sql.append(" and DATEDIFF(?,(select IFNULL(max(sat.transaction_date),sa.activatedon_date) ");
        sql.append(" from m_savings_account_transaction as sat ");
        sql.append(" where sat.is_reversed = 0 ");
        sql.append(" and sat.transaction_type_enum in (1,2) ");
        sql.append(" and sat.savings_account_id = sa.id)) >= sp.days_to_dormancy ");

        try {
            ret = this.jdbcTemplate.queryForList(sql.toString(), Long.class, new Object[] { this.formatter.print(tenantLocalDate) });
        } catch (final EmptyResultDataAccessException e) {
            // ignore empty result scenario
        } catch (final DataAccessException e) {
            throw e;
        }

        return ret;
    }

    @Override
    public List<Long> retrieveSavingsIdsPendingEscheat(final LocalDate tenantLocalDate) {
        List<Long> ret = null;
        final StringBuilder sql = new StringBuilder("select sa.id ");
        sql.append(" from m_savings_account as sa ");
        sql.append(" inner join m_savings_product as sp on (sa.product_id = sp.id and sp.is_dormancy_tracking_active = 1) ");
        sql.append(" where sa.status_enum = 300 ");
        sql.append(" and sa.sub_status_enum = 200 ");
        sql.append(" and DATEDIFF(?,(select IFNULL(max(sat.transaction_date),sa.activatedon_date) ");
        sql.append(" from m_savings_account_transaction as sat ");
        sql.append(" where sat.is_reversed = 0 ");
        sql.append(" and sat.transaction_type_enum in (1,2) ");
        sql.append(" and sat.savings_account_id = sa.id)) >= sp.days_to_escheat ");

        try {
            ret = this.jdbcTemplate.queryForList(sql.toString(), Long.class, new Object[] { this.formatter.print(tenantLocalDate) });
        } catch (final EmptyResultDataAccessException e) {
            // ignore empty result scenario
        } catch (final DataAccessException e) {
            throw e;
        }

        return ret;
    }

    @Override
    public boolean isAccountBelongsToClient(final Long clientId, final Long accountId, final DepositAccountType depositAccountType,
            final String currencyCode) {
        try {
            final StringBuffer buff = new StringBuffer("select count(*) from m_savings_account sa ");
            buff.append(
                    " where sa.id = ? and sa.client_id = ? and sa.deposit_type_enum = ? and sa.currency_code = ? and sa.status_enum = 300");
            return this.jdbcTemplate.queryForObject(buff.toString(),
                    new Object[] { accountId, clientId, depositAccountType.getValue(), currencyCode }, Integer.class) > 0;
        } catch (final EmptyResultDataAccessException e) {
            throw new SavingsAccountNotFoundException(accountId);
        }
    }

    @Override
    public Collection<SavingsAccountDpDetailsData> retriveSavingsAccountDpDetailsDatas() {

        final SavingAccountDpDetailsDataMapper rm = new SavingAccountDpDetailsDataMapper();
        final String sql = "SELECT " + rm.schema() + " where sa.overdraft_limit > 0 and sa.status_enum = 300 ";
        return this.jdbcTemplate.query(sql, new Object[] {}, rm);
    }

    @Override
    public Long retrivePaymentDetailsIdWithSavingsAccountNumberAndTransactioId(final long transactionId,
            final String savingsAccountNumber) {
        try {
            final StringBuilder sql = new StringBuilder();
            sql.append("select sat.payment_detail_id  from m_savings_account sa ");
            sql.append("join m_savings_account_transaction sat on sat.savings_account_id = sa.id ");
            sql.append("where sa.account_no = ? and sat.id= ?");

            return this.jdbcTemplate.queryForObject(sql.toString(), new Object[] { savingsAccountNumber, transactionId }, Long.class);
        } catch (final EmptyResultDataAccessException e) {
            throw new SavingsAccountTransactionNotFoundException(savingsAccountNumber, transactionId);
        }
    }

    private static final class SavingAccountDpDetailsDataMapper implements RowMapper<SavingsAccountDpDetailsData> {

        private final String schemaSql;

        public SavingAccountDpDetailsDataMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("sdp.id as id, sa.id as savingsAccountId, ");
            sqlBuilder.append("sdp.duration as duration, sdp.amount as amount, sdp.dp_amount as dpAmount, ");
            sqlBuilder.append("sdp.calculation_type as calculationTypeId, ");
            sqlBuilder.append("sdp.amount_or_percentage as amountOrPercentage, sa.activatedon_date as savingsActivatedonDate ");
            sqlBuilder.append(",sdp.start_date as startDate ");
            sqlBuilder.append(",spdpd.frequency_type_enum as frequencyType ");
            sqlBuilder.append(",spdpd.frequency_interval as frequencyInterval ");
            sqlBuilder.append(",spdpd.frequency_nth_day_enum as frequencyNthDay ");
            sqlBuilder.append(",spdpd.frequency_day_of_week_type_enum as frequencyDayOfWeekType ");
            sqlBuilder.append(",spdpd.frequency_on_day as frequencyOnDay ");
            sqlBuilder.append("from f_savings_account_dp_details sdp ");
            sqlBuilder.append("join m_savings_account sa ON sdp.savings_id = sa.id ");
            sqlBuilder.append("join m_savings_product sp ON sa.product_id = sp.id ");
            sqlBuilder.append("left join f_savings_product_drawing_power_details spdpd on spdpd.product_id = sp.id ");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public SavingsAccountDpDetailsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
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
            final Long id = rs.getLong("id");

            final Long savingsAccountId = rs.getLong("savingsAccountId");
            final Integer duration = JdbcSupport.getInteger(rs, "duration");
            final BigDecimal amount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amount");
            final BigDecimal dpAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "dpAmount");
            final EnumOptionData calculationType = SavingsDpLimitCalculationType
                    .savingsDpLimitCalculationType(SavingsDpLimitCalculationType.fromInt(JdbcSupport.getInteger(rs, "calculationTypeId")));
            final BigDecimal amountOrPercentage = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amountOrPercentage");
            final Date savingsActivatedonDate = rs.getDate("savingsActivatedonDate");
            final Date startDate = rs.getDate("startDate");
            return SavingsAccountDpDetailsData.createNew(id, savingsAccountId, duration, amount, dpAmount, calculationType,
                    amountOrPercentage, savingsActivatedonDate, startDate, savingsProductDrawingPowerDetailsData);
        }
    }

    @Override
    public void updateSavingsAccountDpLimit(final BigDecimal dpLimitAmount, final Long savingsAccountId) {
        final String updateSql = "UPDATE m_savings_account sa SET sa.overdraft_limit = " + dpLimitAmount + " WHERE sa.id = "
                + savingsAccountId + "";
        this.jdbcTemplate.execute(updateSql);

    }

    @Override
    public SavingsAccountDpDetailsData retrieveSavingsDpDetailsBySavingsId(final Long accountId) {
        SavingsAccountDpDetailsData savingsAccountDpDetailsData = null;
        final SavingAccountDpDetailsDataMapper rm = new SavingAccountDpDetailsDataMapper();
        final String sql = "SELECT " + rm.schema() + " WHERE sdp.savings_id = ? ";
        final List<SavingsAccountDpDetailsData> savingsAccountDpDetailsDatas = this.jdbcTemplate.query(sql, new Object[] { accountId }, rm);
        if (savingsAccountDpDetailsDatas.size() > 0) {
            savingsAccountDpDetailsData = savingsAccountDpDetailsDatas.get(0);
        }
        return savingsAccountDpDetailsData;
    }

    @Override
    public Collection<Long> retrieveRecurringDepositsIdByOfficesAndHoliday(final Long officeId, final List<Holiday> holidays,
            final Collection<Integer> status, final LocalDate recalculateFrom) {
        final StringBuilder sql = new StringBuilder();

        sql.append("SELECT DISTINCT(sa.id) ");
        sql.append("FROM m_office mo ");
        sql.append("JOIN m_client mc ON mc.office_id = mo.id ");
        sql.append("JOIN m_savings_account sa ON  sa.group_id is null and sa.client_id = mc.id and sa.status_enum in (:status) ");
        sql.append("JOIN m_deposit_account_term_and_preclosure datp ON  datp.savings_account_id = sa.id and datp.maturity_date >= :date ");
        sql.append("JOIN m_mandatory_savings_schedule ss on ss.completed_derived != 1 and ss.savings_account_id = sa.id and (");

        generateConditionBasedOnHoliday(holidays, sql);
        sql.append(") ");
        sql.append("WHERE mo.id = :officeId  ");

        sql.append(" union ");

        sql.append("SELECT DISTINCT(sa.id) ");
        sql.append("FROM m_office mo ");
        sql.append("JOIN m_group mg ON mg.office_id = mo.id ");
        sql.append("JOIN m_savings_account sa ON  sa.group_id = mg.id and sa.status_enum in (:status) ");
        sql.append("JOIN m_deposit_account_term_and_preclosure datp ON  datp.savings_account_id = sa.id and datp.maturity_date >= :date ");
        sql.append("JOIN m_mandatory_savings_schedule ss on ss.completed_derived != 1 and ss.savings_account_id = sa.id and (");

        generateConditionBasedOnHoliday(holidays, sql);
        sql.append(") ");
        sql.append("WHERE mo.id = :officeId  ");

        final Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("date", this.formatter.print(recalculateFrom));
        paramMap.put("status", status);
        paramMap.put("officeId", officeId);
        return this.namedParameterJdbcTemplate.queryForList(sql.toString(), paramMap, Long.class);
    }
    
    @Override
    public List<Long> retrieveAllActiveSavingsIdsForActiveClients() {
        StringBuilder sqlBuilder = new StringBuilder("select sa.id as id");
        sqlBuilder.append(" from m_savings_account as sa ");
        sqlBuilder.append(" join m_client mc on mc.id = sa.client_id and mc.status_enum = ?");
        sqlBuilder.append(" where sa.status_enum = ? ");

        return this.jdbcTemplate.queryForList(sqlBuilder.toString(), Long.class, ClientStatus.ACTIVE.getValue(),
                SavingsAccountStatusType.ACTIVE.getValue());
    }
    
    class SavingsIdMapper implements RowMapper<Long> {

        @Override
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong("id");
        }

    }
    
    private void generateConditionBasedOnHoliday(final List<Holiday> holidays, final StringBuilder sql) {
        boolean isFirstTime = true;
        for (final Holiday holiday : holidays) {
            if (!isFirstTime) {
                sql.append(" or ");
            }
            sql.append("ss.duedate BETWEEN '");
            sql.append(this.formatter.print(holiday.getFromDateLocalDate()));
            sql.append("' and '");
            sql.append(this.formatter.print(holiday.getToDateLocalDate()));
            sql.append("'");
            isFirstTime = false;
        }
    }
}