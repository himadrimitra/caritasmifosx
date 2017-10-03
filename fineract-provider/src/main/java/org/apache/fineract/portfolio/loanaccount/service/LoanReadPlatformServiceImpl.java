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

import static org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations.interestType;

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
import org.apache.fineract.accounting.common.AccountingRuleType;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.organisation.staff.service.StaffReadPlatformService;
import org.apache.fineract.portfolio.account.data.AccountTransferData;
import org.apache.fineract.portfolio.accountdetails.data.LoanAccountSummaryData;
import org.apache.fineract.portfolio.accountdetails.domain.AccountType;
import org.apache.fineract.portfolio.accountdetails.service.AccountDetailsReadPlatformService;
import org.apache.fineract.portfolio.accountdetails.service.AccountEnumerations;
import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.service.CalendarReadPlatformService;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.ClientEnumerations;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.collaterals.data.PledgeData;
import org.apache.fineract.portfolio.collaterals.service.PledgeReadPlatformService;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.common.service.CommonEnumerations;
import org.apache.fineract.portfolio.floatingrates.data.InterestRatePeriodData;
import org.apache.fineract.portfolio.floatingrates.service.FloatingRatesReadPlatformService;
import org.apache.fineract.portfolio.fund.api.FundApiConstants;
import org.apache.fineract.portfolio.fund.data.FundData;
import org.apache.fineract.portfolio.fund.service.FundReadPlatformService;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.group.data.GroupRoleData;
import org.apache.fineract.portfolio.group.service.GroupReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.data.DisbursementData;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.data.LoanApplicationTimelineData;
import org.apache.fineract.portfolio.loanaccount.data.LoanApprovalData;
import org.apache.fineract.portfolio.loanaccount.data.LoanInterestRecalculationData;
import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueChargeData;
import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueData;
import org.apache.fineract.portfolio.loanaccount.data.LoanScheduleAccrualData;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;
import org.apache.fineract.portfolio.loanaccount.data.LoanSummaryData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTermVariationsData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.apache.fineract.portfolio.loanaccount.data.PaidInAdvanceData;
import org.apache.fineract.portfolio.loanaccount.data.RepaymentScheduleRelatedLoanData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanChargeOverdueDetails;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRecurringCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanStatus;
import org.apache.fineract.portfolio.loanaccount.domain.LoanSubStatus;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTermVariationType;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanaccount.exception.LoanNotFoundException;
import org.apache.fineract.portfolio.loanaccount.exception.LoanTransactionNotFoundException;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanSchedulePeriodData;
import org.apache.fineract.portfolio.loanproduct.data.LoanProductData;
import org.apache.fineract.portfolio.loanproduct.data.TransactionProcessingStrategyData;
import org.apache.fineract.portfolio.loanproduct.domain.InterestMethod;
import org.apache.fineract.portfolio.loanproduct.domain.WeeksInYearType;
import org.apache.fineract.portfolio.loanproduct.service.LoanDropdownReadPlatformService;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.apache.fineract.useradministration.data.AppUserData;
import org.apache.fineract.useradministration.domain.AppUser;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.finflux.common.constant.CommonConstants;
import com.finflux.fingerprint.data.FingerPrintData;
import com.finflux.fingerprint.services.FingerPrintReadPlatformServices;
import com.finflux.infrastructure.external.authentication.data.ExternalAuthenticationServiceData;
import com.finflux.infrastructure.external.authentication.service.ExternalAuthenticationServicesReadPlatformService;
import com.finflux.organisation.transaction.authentication.data.TransactionAuthenticationData;
import com.finflux.organisation.transaction.authentication.domain.SupportedAuthenticaionTransactionTypes;
import com.finflux.organisation.transaction.authentication.domain.SupportedAuthenticationPortfolioTypes;
import com.finflux.organisation.transaction.authentication.service.TransactionAuthenticationReadPlatformService;
import com.finflux.portfolio.loan.purpose.data.LoanPurposeData;
import com.finflux.portfolio.loan.purpose.service.LoanPurposeGroupReadPlatformService;
import com.finflux.portfolio.loanemipacks.data.LoanEMIPackData;
import com.finflux.portfolio.loanemipacks.service.LoanEMIPacksReadPlatformService;

@Service
public class LoanReadPlatformServiceImpl implements LoanReadPlatformService {
	
    private final static Logger logger = LoggerFactory.getLogger(LoanReadPlatformServiceImpl.class);
    
    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final ClientReadPlatformService clientReadPlatformService;
    private final GroupReadPlatformService groupReadPlatformService;
    private final LoanDropdownReadPlatformService loanDropdownReadPlatformService;
    private final FundReadPlatformService fundReadPlatformService;
    private final ChargeReadPlatformService chargeReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final CalendarReadPlatformService calendarReadPlatformService;
    private final StaffReadPlatformService staffReadPlatformService;
    private final PaginationHelper<LoanAccountData> paginationHelper = new PaginationHelper<>();
    private LoanMapper loaanLoanMapper = null;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;
    
    private final FloatingRatesReadPlatformService floatingRatesReadPlatformService;
    
    private final PledgeReadPlatformService pledgeReadPlatformService;
    private final ConfigurationDomainService configurationDomainService;
    private final FingerPrintReadPlatformServices fingerPrintReadPlatformServices;
    private final ExternalAuthenticationServicesReadPlatformService externalAuthenticationServicesReadPlatformService;
    private final AccountDetailsReadPlatformService accountDetailsReadPlatformService;
    private final TransactionAuthenticationReadPlatformService transactionAuthenticationReadPlatformService;
    private final LoanPurposeGroupReadPlatformService loanPurposeGroupReadPlatformService;
    private final GroupLoanIndividualMonitoringTransactionReadPlatformService groupLoanIndividualMonitoringTransactionReadPlatformService;
    private final LoanEMIPacksReadPlatformService loanEMIPacksReadPlatformService;

    @Autowired
    public LoanReadPlatformServiceImpl(final PlatformSecurityContext context, 
            final LoanProductReadPlatformService loanProductReadPlatformService, final ClientReadPlatformService clientReadPlatformService,
            final GroupReadPlatformService groupReadPlatformService, final LoanDropdownReadPlatformService loanDropdownReadPlatformService,
            final FundReadPlatformService fundReadPlatformService, final ChargeReadPlatformService chargeReadPlatformService,
            final CodeValueReadPlatformService codeValueReadPlatformService, final RoutingDataSource dataSource,
            final CalendarReadPlatformService calendarReadPlatformService, final StaffReadPlatformService staffReadPlatformService,
            final PaymentTypeReadPlatformService paymentTypeReadPlatformService,
            final PledgeReadPlatformService pledgeReadPlatformService,
            final FloatingRatesReadPlatformService floatingRatesReadPlatformService,
            final ConfigurationDomainService configurationDomainService,
            final AccountDetailsReadPlatformService accountDetailsReadPlatformService,
            final TransactionAuthenticationReadPlatformService transactionAuthenticationReadPlatformService,
            final LoanPurposeGroupReadPlatformService loanPurposeGroupReadPlatformService,
            final FingerPrintReadPlatformServices fingerPrintReadPlatformServices,
            final ExternalAuthenticationServicesReadPlatformService externalAuthenticationServicesReadPlatformService,
            final GroupLoanIndividualMonitoringTransactionReadPlatformService groupLoanIndividualMonitoringTransactionReadPlatformService,
            final LoanEMIPacksReadPlatformService loanEMIPacksReadPlatformService) {
        this.context = context;
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.clientReadPlatformService = clientReadPlatformService;
        this.groupReadPlatformService = groupReadPlatformService;
        this.loanDropdownReadPlatformService = loanDropdownReadPlatformService;
        this.fundReadPlatformService = fundReadPlatformService;
        this.chargeReadPlatformService = chargeReadPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.calendarReadPlatformService = calendarReadPlatformService;
        this.staffReadPlatformService = staffReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.paymentTypeReadPlatformService = paymentTypeReadPlatformService;
        this.pledgeReadPlatformService = pledgeReadPlatformService;
        this.floatingRatesReadPlatformService = floatingRatesReadPlatformService;
        this.configurationDomainService = configurationDomainService;
        this.accountDetailsReadPlatformService = accountDetailsReadPlatformService;
        this.transactionAuthenticationReadPlatformService = transactionAuthenticationReadPlatformService;
        this.loanPurposeGroupReadPlatformService =loanPurposeGroupReadPlatformService;
        this.fingerPrintReadPlatformServices = fingerPrintReadPlatformServices;
        this.externalAuthenticationServicesReadPlatformService = externalAuthenticationServicesReadPlatformService;
        this.groupLoanIndividualMonitoringTransactionReadPlatformService = groupLoanIndividualMonitoringTransactionReadPlatformService;
        this.loanEMIPacksReadPlatformService = loanEMIPacksReadPlatformService;
    }

    @Override
    public LoanAccountData retrieveOne(final Long loanId) {
        try {
            final AppUser currentUser = this.context.authenticatedUser();
            final String hierarchy = currentUser.getOffice().getHierarchy();
            final String hierarchySearchString = hierarchy + "%";

            final LoanMapper rm = new LoanMapper();

            final StringBuilder sqlBuilder = new StringBuilder(rm.loanSchema().length()+200);
            sqlBuilder.append("select ");
            sqlBuilder.append(rm.loanSchema());
            sqlBuilder.append(" LEFT join m_office co on co.id = c.office_id ");
            sqlBuilder.append(" LEFT join m_office go on go.id = g.office_id ");
            sqlBuilder.append(" left join m_office transferToOffice on transferToOffice.id = c.transfer_to_office_id ");
            sqlBuilder.append(" where l.id=? and ( co.hierarchy like ? or go.hierarchy like ? or transferToOffice.hierarchy like ?)");

            return this.jdbcTemplate.queryForObject(sqlBuilder.toString(), rm, new Object[] { loanId, hierarchySearchString,
                    hierarchySearchString,hierarchySearchString });
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(loanId);
        }
    }
    
    @Override
    public LoanAccountData retrieveOneWithBasicDetails(final Long loanId) {

        try {
            final LoanBasicMapper rm = new LoanBasicMapper();
            final StringBuilder sqlBuilder = new StringBuilder(rm.loanSchema().length() + 200);
            sqlBuilder.append("select ");
            sqlBuilder.append(rm.loanSchema());
            sqlBuilder.append(" where l.id=?");

            return this.jdbcTemplate.queryForObject(sqlBuilder.toString(), rm, new Object[] { loanId });
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(loanId);
        }
    }
    
    @Override
    public void validateForLoanExistence(final Long loanId) {
        try {
            final String sql = "select l.id from m_loan l where l.id=?";
            this.jdbcTemplate.queryForObject(sql, Long.class, loanId);
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(loanId);
        }
    }

    @Override
    public LoanScheduleData retrieveRepaymentSchedule(final Long loanId,
            final RepaymentScheduleRelatedLoanData repaymentScheduleRelatedLoanData, Collection<DisbursementData> disbursementData,
            BigDecimal totalPaidFeeCharges) {

        try {
            this.context.authenticatedUser();

            final LoanScheduleResultSetExtractor fullResultsetExtractor = new LoanScheduleResultSetExtractor(
                    repaymentScheduleRelatedLoanData, disbursementData, totalPaidFeeCharges  );
            final String sql = "select " + fullResultsetExtractor.schema() + " where ls.loan_id = ? order by ls.loan_id, ls.installment";

            return this.jdbcTemplate.query(sql, fullResultsetExtractor, new Object[] { loanId });
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(loanId);
        }
    }

    @Override
    public Collection<LoanTransactionData> retrieveLoanTransactions(final Long loanId) {
        try {
            this.context.authenticatedUser();

            final LoanTransactionsMapper rm = new LoanTransactionsMapper();

            // retrieve all loan transactions that are not invalid and have not
            // been 'contra'ed by another transaction
            // repayments at time of disbursement (e.g. charges)

            /***
             * TODO Vishwas: Remove references to "Contra" from the codebase
             ***/
            final String sql = "select "
                    + rm.LoanPaymentsSchema()
                    + " where tr.loan_id = ? and tr.transaction_type_enum not in (0, 3) and  (tr.is_reversed=0 or tr.manually_adjusted_or_reversed = 1) order by tr.transaction_date ASC,id ";
            return this.jdbcTemplate.query(sql, rm, new Object[] { loanId });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Page<LoanAccountData> retrieveAll(final SearchParameters searchParameters, final boolean lookup) {

        final AppUser currentUser = this.context.authenticatedUser();
        final String hierarchy = currentUser.getOffice().getHierarchy();
        final String hierarchySearchString = hierarchy + "%";
        
        if (lookup) {
            this.loaanLoanMapper = new LoanLookupMapper();
        } else {
            this.loaanLoanMapper = new LoanMapper();
        }

        final StringBuilder sqlBuilder = new StringBuilder(this.loaanLoanMapper.loanSchema().length()+200);
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        sqlBuilder.append(this.loaanLoanMapper.loanSchema());

        // TODO - for time being this will data scope list of loans returned to
        // only loans that have a client associated.
        // to support senario where loan has group_id only OR client_id will
        // probably require a UNION query
        // but that at present is an edge case
        sqlBuilder.append(" join m_office o on o.id = c.office_id");
        sqlBuilder.append(" left join m_office transferToOffice on transferToOffice.id = c.transfer_to_office_id ");
        sqlBuilder.append(" where ( o.hierarchy like ? or transferToOffice.hierarchy like ?)");

        int arrayPos = 2;
        List<Object> extraCriterias = new ArrayList<>();
        extraCriterias.add(hierarchySearchString);
        extraCriterias.add(hierarchySearchString);

        final Map<String, String> searchConditions = searchParameters.getSearchConditions();
        searchConditions.forEach((key, value) -> {
            switch (key) {
                case CommonConstants.LOAN_ACCOUNT_NO:
                    sqlBuilder.append(" and ( l.account_no = '").append(value).append("' ) ");
                break;
                default:
                break;
            }
        });

        if (StringUtils.isNotBlank(searchParameters.getExternalId())) {
            sqlBuilder.append(" and l.external_id = ?");
            extraCriterias.add(searchParameters.getExternalId());
            arrayPos = arrayPos + 1;
        }

        if (StringUtils.isNotBlank(searchParameters.getAccountNo())) {
            sqlBuilder.append(" and l.account_no = ?");
            extraCriterias.add(searchParameters.getAccountNo());
            arrayPos = arrayPos + 1;
        }

        if (searchParameters.isOfficeIdPassed()) {
            sqlBuilder.append(" and c.office_id = ?");
            extraCriterias.add(searchParameters.getOfficeId());
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

        final Object[] objectArray = extraCriterias.toArray();
        final Object[] finalObjectArray = Arrays.copyOf(objectArray, arrayPos);
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(), finalObjectArray,
                this.loaanLoanMapper);
    }

    @Override
    public LoanAccountData retrieveTemplateWithClientAndProductDetails(final Long clientId, final Long productId) {

        this.context.authenticatedUser();

        final ClientData clientAccount = this.clientReadPlatformService.retrieveOne(clientId);
        final LocalDate expectedDisbursementDate = DateUtils.getLocalDateOfTenant();
        final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        LoanAccountData loanTemplateDetails = LoanAccountData.clientDefaults(clientAccount.id(), clientAccount.accountNo(),
                clientAccount.displayName(), clientAccount.officeId(), expectedDisbursementDate, paymentOptions);

        if (productId != null) {
            final LoanProductData selectedProduct = this.loanProductReadPlatformService.retrieveLoanProduct(productId);
            loanTemplateDetails = LoanAccountData.populateLoanProductDefaults(loanTemplateDetails, selectedProduct);
        }

        return loanTemplateDetails;
    }

    @Override
    public LoanAccountData retrieveTemplateWithGroupAndProductDetails(final Long groupId, final Long productId) {

        this.context.authenticatedUser();

        final GroupGeneralData groupAccount = this.groupReadPlatformService.retrieveOne(groupId);
        final LocalDate expectedDisbursementDate = DateUtils.getLocalDateOfTenant();
        LoanAccountData loanDetails = LoanAccountData.groupDefaults(groupAccount, expectedDisbursementDate);

        if (productId != null) {
            final LoanProductData selectedProduct = this.loanProductReadPlatformService.retrieveLoanProduct(productId);
            loanDetails = LoanAccountData.populateLoanProductDefaults(loanDetails, selectedProduct);
        }

        return loanDetails;
    }

    @Override
    public LoanAccountData retrieveTemplateWithCompleteGroupAndProductDetails(final Long groupId, final Long productId) {

        this.context.authenticatedUser();

        GroupGeneralData groupAccount = this.groupReadPlatformService.retrieveOne(groupId);
        // get group associations
        final Collection<ClientData> membersOfGroup = this.clientReadPlatformService.retrieveClientMembersOfGroup(groupId);
        if (!CollectionUtils.isEmpty(membersOfGroup)) {
            final Collection<ClientData> activeClientMembers = null;
            final Collection<CalendarData> calendarsData = null;
            final CalendarData collectionMeetingCalendar = null;
            final Collection<GroupRoleData> groupRoles = null;
            groupAccount = GroupGeneralData.withAssocations(groupAccount, membersOfGroup, activeClientMembers, groupRoles, calendarsData,
                    collectionMeetingCalendar);
        }

        final LocalDate expectedDisbursementDate = DateUtils.getLocalDateOfTenant();
        LoanAccountData loanDetails = LoanAccountData.groupDefaults(groupAccount, expectedDisbursementDate);

        if (productId != null) {
            final LoanProductData selectedProduct = this.loanProductReadPlatformService.retrieveLoanProduct(productId);
            loanDetails = LoanAccountData.populateLoanProductDefaults(loanDetails, selectedProduct);
        }

        return loanDetails;
    }

    @Override
    public LoanTransactionData retrieveLoanTransactionTemplate(final Long loanId) {

        this.context.authenticatedUser();
        try {
            RepaymentTransactionTemplateMapper mapper = new RepaymentTransactionTemplateMapper();
            String sql = "select " + mapper.schema() + " where l.id =?";
            LoanTransactionData loanTransactionData = this.jdbcTemplate.queryForObject(sql, mapper,
                    LoanTransactionType.REPAYMENT.getValue(), loanId, loanId);
            LoanOverdueData loanOverdueData = loanTransactionData.getLoanOverdueData();
            final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
            loanTransactionData = LoanTransactionData.templateOnTop(loanTransactionData, paymentOptions);
            loanTransactionData.setLoanOverdueData(loanOverdueData);

            if (loanOverdueData != null
                    && loanOverdueData.getLastOverdueDate().isBefore(DateUtils.getLocalDateOfTenant())) {
                boolean fetchApplicableChargesByState = true;
                List<LoanRecurringCharge> recurringCharges = this.retrieveLoanOverdueRecurringCharge(loanId, fetchApplicableChargesByState);
                LocalDate lastRunOnDate = null;
                LocalDate lastChargeAppliedOnDate = null;
                boolean canApplyBrokenPeriodChargeAsOnCurrentDate = false;
                if (!recurringCharges.isEmpty()) {
                    for (LoanRecurringCharge charge : recurringCharges) {
                        if (lastRunOnDate == null
                                || (charge.getChargeOverueDetail().getLastRunOnDate() != null && lastRunOnDate.isAfter(charge
                                        .getChargeOverueDetail().getLastRunOnDate()))) {
                            lastRunOnDate = charge.getChargeOverueDetail().getLastRunOnDate();
                        }
                        if (lastChargeAppliedOnDate == null
                                || (charge.getChargeOverueDetail().getLastAppliedOnDate() != null && lastChargeAppliedOnDate.isAfter(charge
                                        .getChargeOverueDetail().getLastAppliedOnDate()))) {
                            lastChargeAppliedOnDate = charge.getChargeOverueDetail().getLastAppliedOnDate();
                        }

                        if (charge.getChargeOverueDetail().isApplyChargeForBrokenPeriod()
                                && (lastChargeAppliedOnDate == null || (charge.getChargeOverueDetail().getLastRunOnDate() != null && DateUtils
                                        .getLocalDateOfTenant().isEqual(charge.getChargeOverueDetail().getLastRunOnDate())))) {
                            canApplyBrokenPeriodChargeAsOnCurrentDate = true;
                        }

                    }
                    LoanOverdueChargeData chargeData = new LoanOverdueChargeData(lastRunOnDate, lastChargeAppliedOnDate,
                            canApplyBrokenPeriodChargeAsOnCurrentDate);
                    loanTransactionData.setLoanOverdueChargeData(chargeData);
                }
            }
            return loanTransactionData;

        } catch (EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(loanId);
        }
    }

    
    @Override
    public LoanTransactionData retrieveWaiveInterestDetails(final Long loanId,final Boolean isTotalOutstandingInterest) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ls.duedate AS duedate, ");
        if (isTotalOutstandingInterest) {
            sb.append("ml.interest_outstanding_derived AS waiveramount,");
        } else {
            sb.append(
                    "IF((IFNULL(ls.interest_amount,0) - IFNULL(ls.interest_completed_derived,0) - IFNULL(ls.interest_writtenoff_derived,0) - IFNULL(ls.interest_waived_derived,0)) > 0,");
            sb.append(
                    "(IFNULL(ls.interest_amount,0) - IFNULL(ls.interest_completed_derived,0) - IFNULL(ls.interest_writtenoff_derived,0) - IFNULL(ls.interest_waived_derived,0)),");
            sb.append("ml.interest_outstanding_derived) AS waiveramount,");
        }
        sb.append(" ml.currency_code as currencyCode, ml.currency_digits as currencyDigits, ml.currency_multiplesof as inMultiplesOf, rc.`name` as currencyName, ");
        sb.append(" rc.display_symbol as currencyDisplaySymbol, rc.internationalized_name_code as currencyNameCode ");
        sb.append(" FROM m_loan ml");
        sb.append(" LEFT JOIN (");
        sb.append("SELECT mltemp.id AS loanId, MIN(lstemp.duedate) AS duedate");
        sb.append(" FROM m_loan mltemp");
        sb.append(" JOIN m_loan_repayment_schedule lstemp ON mltemp.id = lstemp.loan_id AND ((IFNULL(lstemp.interest_amount,0) - IFNULL(lstemp.interest_completed_derived,0) - IFNULL(lstemp.interest_writtenoff_derived,0) - IFNULL(lstemp.interest_waived_derived,0)) > 0)");
        sb.append(" WHERE mltemp.id = ?");
        sb.append(" GROUP BY mltemp.id) x ON x.loanId = ml.id");
        sb.append(" LEFT JOIN m_loan_repayment_schedule ls ON ml.id = ls.loan_id AND x.duedate = ls.duedate");
        sb.append(" join m_currency rc on rc.`code` = ml.currency_code ");
        sb.append(" WHERE ml.id = ?");

        try {

            Map<String, Object> data = this.jdbcTemplate.queryForMap(sb.toString(), loanId, loanId);
            final BigDecimal amount = (BigDecimal) data.get("waiveramount");
            LocalDate transactionDate = DateUtils.getLocalDateOfTenant();
            if (data.get("duedate") != null) {
                transactionDate = new LocalDate(data.get("duedate"));
            }

            final CurrencyData currencyData = fetchCurrencyData(data);

            final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.WAIVE_INTEREST);
            final BigDecimal outstandingLoanBalance = null;
            final BigDecimal unrecognizedIncomePortion = null;
            return new LoanTransactionData(null, null, null, transactionType, null, currencyData, transactionDate, amount, null, null,
                    null, null, null, null, null, null, outstandingLoanBalance, unrecognizedIncomePortion, false);
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(loanId);
        }

    }

    private CurrencyData fetchCurrencyData(Map<String, Object> data) {
        final String currencyCode = (String) data.get("currencyCode");
        final String currencyName = (String) data.get("currencyName");
        final String currencyNameCode = (String) data.get("currencyNameCode");
        final String currencyDisplaySymbol = (String) data.get("currencyDisplaySymbol");
        final Integer currencyDigits = (Integer) data.get("currencyDigits");
        final Integer inMultiplesOf = (Integer) data.get("inMultiplesOf");
        final CurrencyData currencyData = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf,
                currencyDisplaySymbol, currencyNameCode);
        return currencyData;
    }

    @Override
    public LoanTransactionData retrieveNewClosureDetails() {

        this.context.authenticatedUser();
        final BigDecimal outstandingLoanBalance = null;
        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.WRITEOFF);
        final BigDecimal unrecognizedIncomePortion = null;
        return new LoanTransactionData(null, null, null, transactionType, null, null, DateUtils.getLocalDateOfTenant(), null, null, null,
                null, null, null, null, null, null, outstandingLoanBalance, unrecognizedIncomePortion, false);

    }

    @Override
    public LoanApprovalData retrieveApprovalTemplate(final Long loanId) {

        String sql = "select l.principal_amount_proposed from m_loan l where l.id = ?";
        try {
            final BigDecimal proposedPrincipal = this.jdbcTemplate.queryForObject(sql, BigDecimal.class, loanId);
            return new LoanApprovalData(proposedPrincipal, DateUtils.getLocalDateOfTenant());
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(loanId);
        }

    }

    @Override
    public LoanTransactionData retrieveDisbursalTemplate(final Long loanId, boolean paymentDetailsRequired) {


        try {
            final Map<String, Object> data = retrieveDisbursalDataMap(loanId);
            
            final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.DISBURSEMENT);
            Collection<PaymentTypeData> paymentOptions = null;
            if (paymentDetailsRequired) {
                paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
            }
            Integer status = (Integer) data.get("status");
            LoanStatus loanStatus = LoanStatus.fromInt(status);
            LocalDate expectedDisbursementDate = new LocalDate(data.get("expectedDisbursementDate"));
            LocalDate nextDueDate = new LocalDate(data.get("nextDueDate"));
            BigDecimal principal = (BigDecimal) data.get("principal");
            BigDecimal discountOnDisbursalAmount = null;
            if (data.get("discountOnDisbursalAmount") != null) {
                discountOnDisbursalAmount = (BigDecimal) data.get("discountOnDisbursalAmount");
                if (data.get("trancheDisbursalId") == null) {
                    principal = principal.subtract(discountOnDisbursalAmount);
                }
            }
            BigDecimal fixedEmiAmount = null;
            if (data.get("fixedEmiAmount") != null) {
                fixedEmiAmount = (BigDecimal) data.get("fixedEmiAmount");
            }
            BigDecimal netDisburseAmount = principal;
           
            BigDecimal totalNetDisbursal = (BigDecimal) data.get("netDisbursalDerived");
            BigDecimal trancheNetDisburseAmount = (BigDecimal) data.get("trancheNetDisburseAmount");
            if (discountOnDisbursalAmount != null) {
                if (totalNetDisbursal != null) {
                    totalNetDisbursal = totalNetDisbursal.subtract(discountOnDisbursalAmount);
                }
                if (trancheNetDisburseAmount != null) {
                    trancheNetDisburseAmount = trancheNetDisburseAmount.subtract(discountOnDisbursalAmount);
                }
            }
            
           
            if (loanStatus.isActive()) {
                netDisburseAmount = trancheNetDisburseAmount;
            } else if ((trancheNetDisburseAmount != null && trancheNetDisburseAmount.compareTo(principal) != 0) || principal.compareTo(totalNetDisbursal) == -1) {
                final String sqlForDisbursementCharges = "select sum(lc.amount) as chargeAmount from m_loan_charge lc where lc.loan_id = ? and  lc.charge_time_enum = ? ";
                final BigDecimal disbursementCharge = this.jdbcTemplate.queryForObject(sqlForDisbursementCharges, BigDecimal.class, loanId,
                        ChargeTimeType.DISBURSEMENT.getValue());
                if (disbursementCharge != null) {
                    netDisburseAmount = netDisburseAmount.subtract(disbursementCharge);
                }
                netDisburseAmount = netDisburseAmount.subtract(principal.subtract(trancheNetDisburseAmount));
            } else {
                netDisburseAmount = totalNetDisbursal;
            }
            
            
            
            Collection<FingerPrintData> fingerPrintData = null;
            Long clientId = (Long) data.get("clientId");
            BigDecimal approvedPrincipal = (BigDecimal) data.get("approvedPrincipal");
            Long productId = (Long) data.get("productId");
            Collection<TransactionAuthenticationData> transactionAuthenticationOptions = null; 
            if (clientId != null) {
                transactionAuthenticationOptions = this.transactionAuthenticationReadPlatformService
                        .retiveTransactionAuthenticationDetailsForTemplate(SupportedAuthenticationPortfolioTypes.LOANS.getValue(),
                                SupportedAuthenticaionTransactionTypes.DISBURSEMENT.getValue(), approvedPrincipal, loanId, productId);
                final Collection<ExternalAuthenticationServiceData> externalServices = this.externalAuthenticationServicesReadPlatformService
                        .getOnlyActiveExternalAuthenticationServices();
                if (externalServices.size() > 0 && !externalServices.isEmpty()) {
                    for (ExternalAuthenticationServiceData services : externalServices) {
                        if (services.getName().contains("Fingerprint Auth")) {
                            if (services.isActive()) {
                                fingerPrintData = this.fingerPrintReadPlatformServices.retriveFingerPrintData(clientId);
                            }
                        }
                    }
                }
            }
            LoanTransactionData disburseTemplate = LoanTransactionData.LoanTransactionDataForDisbursalTemplate(transactionType, expectedDisbursementDate, principal,
                    paymentOptions, fixedEmiAmount, nextDueDate, transactionAuthenticationOptions,fingerPrintData);
            disburseTemplate.setNetDisbursalAmount(netDisburseAmount);
            disburseTemplate.setDiscountOnDisbursalAmount(discountOnDisbursalAmount);
            if(!loanStatus.isActive() && data.get("expectedFirstRepaymentOnDate") != null){
                disburseTemplate.setExpectedFirstRepaymentOnDate(new LocalDate(data.get("expectedFirstRepaymentOnDate")));
            }
             return disburseTemplate;
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(loanId);
        }
    }

    public Map<String, Object> retrieveDisbursalDataMap(final Long loanId) {
        final StringBuilder sql = new StringBuilder(200);
        sql.append("SELECT dd.id AS trancheDisbursalId, IFNULL( dd.principal,l.principal_amount)  AS principal, IFNULL(dd.expected_disburse_date,l.expected_disbursedon_date) AS expectedDisbursementDate, ifnull(tv.decimal_value,l.fixed_emi_amount) as fixedEmiAmount, min(rs.duedate) as nextDueDate, l.approved_principal as approvedPrincipal ");
        sql.append(" , l.product_id as productId, l.client_id as clientId, l.expected_firstrepaymenton_date as expectedFirstRepaymentOnDate,");
        sql.append(" l.principal_net_disbursed_derived as netDisbursalDerived , dd.principal_net_disbursed as trancheNetDisburseAmount,l.loan_status_id as status, IF(dd.id IS NOT NULL,dd.discount_on_disbursal_amount, l.discount_on_disbursal_amount) as discountOnDisbursalAmount ");
        sql.append("FROM m_loan l");
        sql.append(" left join (select ltemp.id loanId, MIN(ddtemp.expected_disburse_date) as minDisburseDate from m_loan ltemp join m_loan_disbursement_detail  ddtemp on ltemp.id = ddtemp.loan_id and ddtemp.disbursedon_date is null where ltemp.id = :loanId  group by ltemp.id ) x on x.loanId = l.id");
        sql.append(" left join m_loan_disbursement_detail dd on dd.loan_id = l.id and dd.expected_disburse_date =  x.minDisburseDate");
        sql.append(" left join (select ltemp.id loanId, Max(temptv.applicable_date) as maxemidate  from m_loan ltemp join m_loan_term_variations temptv on temptv.loan_id = ltemp.id and temptv.is_active = 1 and temptv.is_specific_to_installment = 0 and temptv.term_type = :termtype where ltemp.id = :loanId  group by ltemp.id) y on y.loanId = l.id");
        sql.append(" left join m_loan_term_variations tv on tv.loan_id = l.id and tv.is_active = 1 and tv.is_specific_to_installment = 0 and tv.term_type = :termtype and tv.applicable_date = y.maxemidate");
        sql.append(" join m_loan_repayment_schedule rs on rs.loan_id = l.id and rs.duedate >=  if(:changeEmi, IFNULL(dd.expected_disburse_date,l.expected_disbursedon_date),DATE_ADD(IFNULL(dd.expected_disburse_date,l.expected_disbursedon_date), INTERVAL 1 DAY))");
        sql.append(" WHERE l.id = :loanId group by l.id");

        final Boolean isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled = this.configurationDomainService
                .isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled();
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("loanId", loanId);
        paramMap.put("termtype", LoanTermVariationType.EMI_AMOUNT.getValue());
        paramMap.put("changeEmi", isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled);
        return this.namedParameterJdbcTemplate.queryForMap(sql.toString(), paramMap);
    }

    @Override
    public LoanTransactionData retrieveLoanTransaction(final Long loanId, final Long transactionId) {
        this.context.authenticatedUser();
        try {
            final LoanTransactionsMapper rm = new LoanTransactionsMapper();
            final String sql = "select " + rm.LoanPaymentsSchema() + " where l.id = ? and tr.id = ? ";
            LoanTransactionData loanTransactionData = this.jdbcTemplate.queryForObject(sql, rm, new Object[] { loanId, transactionId });
            List<GroupLoanIndividualMonitoringTransactionData> glimTransactions = this.groupLoanIndividualMonitoringTransactionReadPlatformService
                    .retriveGlimTransaction(transactionId);
            loanTransactionData.updateGlimTransactions(glimTransactions);
            return loanTransactionData;
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanTransactionNotFoundException(transactionId);
        }
    }

    private static class LoanMapper implements RowMapper<LoanAccountData> {
        
        private CurrencyMapper currencyMapper = new CurrencyMapper();
        final String loanSql;

        LoanMapper() {
            final StringBuilder sb = new StringBuilder(10000);
            sb.append("l.id as id, l.account_no as accountNo, l.external_id as externalId, l.fund_id as fundId, f.name as fundName,");
            sb.append(" l.loan_type_enum as loanType, l.loan_purpose_id as loanPurposeId, l.weeks_in_year_enum as weeksInYearType, flp.name as loanPurposeName,");
            sb.append(" lp.id as loanProductId, lp.name as loanProductName, lp.description as loanProductDescription,");
            sb.append(" lp.is_linked_to_floating_interest_rates as isLoanProductLinkedToFloatingRate, ");
            sb.append(" lp.allow_variabe_installments as isvariableInstallmentsAllowed, ");
            sb.append(" lp.allow_multiple_disbursals as multiDisburseLoan,");
            sb.append(" lp.can_define_fixed_emi_amount as canDefineInstallmentAmount, lp.allow_upfront_collection as allowUpfrontCollection,");
            sb.append(" lp.consider_future_disbursements_in_schedule as considerFutureDisbursmentsInSchedule, lp.consider_all_disbursements_in_schedule as considerAllDisbursementsInSchedule,");
            sb.append(" c.id as clientId, c.account_no as clientAccountNo, c.display_name as clientName, IFNULL(c.mobile_no,null) as mobileNo, c.office_id as clientOfficeId,");
            sb.append(" g.id as groupId, g.account_no as groupAccountNo, g.display_name as groupName,g.level_id as groupLevel,");
            sb.append(" g.office_id as groupOfficeId, g.staff_id As groupStaffId , g.parent_id as groupParentId, (select mg.display_name from m_group mg where mg.id = g.parent_id) as centerName, ");
            sb.append(" g.hierarchy As groupHierarchy , g.level_id as groupLevel, g.external_id As groupExternalId, ");
            sb.append(" g.status_enum as statusEnum, g.activation_date as activationDate, ");
            sb.append(" l.submittedon_date as submittedOnDate, sbu.username as submittedByUsername, sbu.firstname as submittedByFirstname, sbu.lastname as submittedByLastname,");
            sb.append(" l.rejectedon_date as rejectedOnDate, rbu.username as rejectedByUsername, rbu.firstname as rejectedByFirstname, rbu.lastname as rejectedByLastname,");
            sb.append(" l.withdrawnon_date as withdrawnOnDate, wbu.username as withdrawnByUsername, wbu.firstname as withdrawnByFirstname, wbu.lastname as withdrawnByLastname,");
            sb.append(" l.approvedon_date as approvedOnDate, abu.username as approvedByUsername, abu.firstname as approvedByFirstname, abu.lastname as approvedByLastname,");
            sb.append(" l.expected_disbursedon_date as expectedDisbursementDate, l.disbursedon_date as actualDisbursementDate, dbu.username as disbursedByUsername, dbu.firstname as disbursedByFirstname, dbu.lastname as disbursedByLastname,");
            sb.append(" l.closedon_date as closedOnDate, cbu.username as closedByUsername, cbu.firstname as closedByFirstname, cbu.lastname as closedByLastname, l.writtenoffon_date as writtenOffOnDate, ");
            sb.append(" l.expected_firstrepaymenton_date as expectedFirstRepaymentOnDate, l.interest_calculated_from_date as interestChargedFromDate, l.expected_maturedon_date as expectedMaturityDate, ");
            sb.append(" l.principal_amount_proposed as proposedPrincipal, l.principal_amount as principal, l.approved_principal as approvedPrincipal, l.arrearstolerance_amount as inArrearsTolerance, l.number_of_repayments as numberOfRepayments, l.repay_every as repaymentEvery,");
            sb.append(" l.grace_on_principal_periods as graceOnPrincipalPayment, l.recurring_moratorium_principal_periods as recurringMoratoriumOnPrincipalPeriods, l.grace_on_interest_periods as graceOnInterestPayment, l.grace_interest_free_periods as graceOnInterestCharged,l.grace_on_arrears_ageing as graceOnArrearsAgeing,");
            sb.append(" l.nominal_interest_rate_per_period as interestRatePerPeriod, l.annual_nominal_interest_rate as annualInterestRate, ");
            sb.append(" l.repayment_period_frequency_enum as repaymentFrequencyType, l.interest_period_frequency_enum as interestRateFrequencyType, ");
            sb.append(" l.term_frequency as termFrequency, l.term_period_frequency_enum as termPeriodFrequencyType, ");
            sb.append(" l.amortization_method_enum as amortizationType, l.interest_method_enum as interestType, l.interest_calculated_in_period_enum as interestCalculationPeriodType,");
            sb.append(" l.allow_partial_period_interest_calcualtion as allowPartialPeriodInterestCalcualtion,");
            sb.append(" l.loan_status_id as lifeCycleStatusId, l.loan_transaction_strategy_id as transactionStrategyId, ");
            sb.append(" lps.name as transactionStrategyName, lps.code as transactionStrategyCode,");
            sb.append(" l.calculated_installment_amount as calculatedEmiAmount, ");
            sb.append(" l.currency_code as currencyCode, l.currency_digits as currencyDigits, l.currency_multiplesof as inMultiplesOf, rc.`name` as currencyName, rc.display_symbol as currencyDisplaySymbol, rc.internationalized_name_code as currencyNameCode, ");
            sb.append(" l.loan_officer_id as loanOfficerId, s.display_name as loanOfficerName, ");
            sb.append(" l.principal_disbursed_derived as principalDisbursed,");
            sb.append(" l.principal_net_disbursed_derived as principalNetDisbursed,");
            sb.append(" l.principal_repaid_derived as principalPaid,");
            sb.append(" l.principal_writtenoff_derived as principalWrittenOff,");
            sb.append(" l.principal_outstanding_derived as principalOutstanding,");
            sb.append(" l.interest_charged_derived as interestCharged,");
            sb.append(" l.interest_repaid_derived as interestPaid,");
            sb.append(" l.interest_waived_derived as interestWaived,");
            sb.append(" l.interest_writtenoff_derived as interestWrittenOff,");
            sb.append(" l.interest_outstanding_derived as interestOutstanding,");
            sb.append(" l.fee_charges_charged_derived as feeChargesCharged,");
            sb.append(" l.total_charges_due_at_disbursement_derived as feeChargesDueAtDisbursementCharged,");
            sb.append(" l.fee_charges_repaid_derived as feeChargesPaid,");
            sb.append(" l.fee_charges_waived_derived as feeChargesWaived,");
            sb.append(" l.fee_charges_writtenoff_derived as feeChargesWrittenOff,");
            sb.append(" l.fee_charges_outstanding_derived as feeChargesOutstanding,");
            sb.append(" l.penalty_charges_charged_derived as penaltyChargesCharged,");
            sb.append(" l.penalty_charges_repaid_derived as penaltyChargesPaid,");
            sb.append(" l.penalty_charges_waived_derived as penaltyChargesWaived,");
            sb.append(" l.penalty_charges_writtenoff_derived as penaltyChargesWrittenOff,");
            sb.append(" l.penalty_charges_outstanding_derived as penaltyChargesOutstanding,");
            sb.append(" l.total_expected_repayment_derived as totalExpectedRepayment,");
            sb.append(" l.total_repayment_derived as totalRepayment,");
            sb.append(" l.total_expected_costofloan_derived as totalExpectedCostOfLoan,");
            sb.append(" l.total_costofloan_derived as totalCostOfLoan,");
            sb.append(" l.total_waived_derived as totalWaived,");
            sb.append(" l.total_writtenoff_derived as totalWrittenOff,");
            sb.append(" l.writeoff_reason_cv_id as writeoffReasonId,");
            sb.append(" l.flat_interest_rate as flatInterestRate,");
            sb.append(" l.discount_on_disbursal_amount as discountOnDisbursalAmount, l.amount_for_upfront_collection as amountForUpfrontCollection,");
            sb.append(" codev.code_value as writeoffReason,");
            sb.append(" l.total_outstanding_derived as totalOutstanding,");
            sb.append(" l.total_overpaid_derived as totalOverpaid,");
            sb.append(" l.fixed_emi_amount as fixedEmiAmount,");
            sb.append(" l.max_outstanding_loan_balance as outstandingLoanBalance,");
            sb.append(" l.loan_sub_status_id as loanSubStatusId,");
            sb.append(" l.broken_period_method_enum as brokenPeriodMethodType, l.broken_period_interest as brokenPeriodInterest,");
            sb.append(" la.principal_overdue_derived as principalOverdue,");
            sb.append(" la.interest_overdue_derived as interestOverdue,");
            sb.append(" la.fee_charges_overdue_derived as feeChargesOverdue,");
            sb.append(" la.penalty_charges_overdue_derived as penaltyChargesOverdue,");
            sb.append(" la.total_overdue_derived as totalOverdue,");
            sb.append(" la.overdue_since_date_derived as overdueSinceDate,");
            sb.append(" l.sync_disbursement_with_meeting as syncDisbursementWithMeeting,");
            sb.append(" l.loan_counter as loanCounter, l.loan_product_counter as loanProductCounter,");
            sb.append(" l.is_npa as isNPA, l.days_in_month_enum as daysInMonth, l.days_in_year_enum as daysInYear, ");
            sb.append(" l.interest_recalculation_enabled as isInterestRecalculationEnabled, ");
            sb.append(" lir.id as lirId, lir.loan_id as loanId, lir.compound_type_enum as compoundType, lir.reschedule_strategy_enum as rescheduleStrategy, ");
            sb.append(" lir.rest_frequency_type_enum as restFrequencyEnum, lir.rest_frequency_interval as restFrequencyInterval, ");
            sb.append(" lir.rest_frequency_nth_day_enum as restFrequencyNthDayEnum, ");
            sb.append(" lir.rest_frequency_weekday_enum as restFrequencyWeekDayEnum, ");
            sb.append(" lir.rest_frequency_on_day as restFrequencyOnDay, ");
            sb.append(" lir.compounding_frequency_type_enum as compoundingFrequencyEnum, lir.compounding_frequency_interval as compoundingInterval, ");
            sb.append(" lir.compounding_frequency_nth_day_enum as compoundingFrequencyNthDayEnum, ");
            sb.append(" lir.compounding_frequency_weekday_enum as compoundingFrequencyWeekDayEnum, ");
            sb.append(" lir.compounding_frequency_on_day as compoundingFrequencyOnDay, ");
            sb.append(" lir.is_compounding_to_be_posted_as_transaction as isCompoundingToBePostedAsTransaction, ");
            sb.append(" lir.allow_compounding_on_eod as allowCompoundingOnEod, ");
            sb.append(" lir.rest_frequency_start_date as recalculationRestFrequencyStartDate, ");
            sb.append(" lir.compounding_frequency_start_date as recalculationCompoundingFrequencyStartDate, ");
            sb.append(" l.is_floating_interest_rate as isFloatingInterestRate, ");
            sb.append("l.expected_disbursal_payment_type_id as expectedDisbursalPaymentTypeId,pt_disburse.value as disbursementPaymentTypeName, ");
            sb.append("l.expected_repayment_payment_type_id as expectedRepaymentPaymentTypeId, pt_repayment.value as repaymenPaymentTypeName, ");
            sb.append(" l.interest_rate_differential as interestRateDifferential, ");
            sb.append(" l.create_standing_instruction_at_disbursement as createStandingInstructionAtDisbursement, ");
            sb.append(" lpvi.minimum_gap as minimuminstallmentgap, lpvi.maximum_gap as maximuminstallmentgap , ");
            sb.append(" lir.is_subsidy_applicable as isSubsidyApplicable,");
            sb.append(" lp.can_use_for_topup as canUseForTopup, ");
            sb.append(" l.is_topup as isTopup, ");
            sb.append(" topup.closure_loan_id as closureLoanId, ");
            sb.append(" topuploan.account_no as closureLoanAccountNo, ");
            sb.append(" topup.topup_amount as topupAmount ");
            sb.append(",l.is_locked as isLocked ");
            sb.append(" from m_loan l");
            sb.append(" join m_product_loan lp on lp.id = l.product_id");
            sb.append(" left join m_loan_recalculation_details lir on lir.loan_id = l.id ");
            sb.append(" join m_currency rc on rc.`code` = l.currency_code");
            sb.append(" left join m_client c on c.id = l.client_id");
            sb.append(" left join m_group g on g.id = l.group_id");
            sb.append(" left join m_loan_arrears_aging la on la.loan_id = l.id");
            sb.append(" left join m_fund f on f.id = l.fund_id");
            sb.append(" left join m_staff s on s.id = l.loan_officer_id");
            sb.append(" left join m_appuser sbu on sbu.id = l.submittedon_userid");
            sb.append(" left join m_appuser rbu on rbu.id = l.rejectedon_userid");
            sb.append(" left join m_appuser wbu on wbu.id = l.withdrawnon_userid");
            sb.append(" left join m_appuser abu on abu.id = l.approvedon_userid");
            sb.append(" left join m_appuser dbu on dbu.id = l.disbursedon_userid");
            sb.append(" left join m_appuser cbu on cbu.id = l.closedon_userid");
            sb.append(" left join m_code_value codev on codev.id = l.writeoff_reason_cv_id");
            sb.append(" left join f_loan_purpose flp on flp.id = l.loan_purpose_id");
            sb.append(" left join ref_loan_transaction_processing_strategy lps on lps.id = l.loan_transaction_strategy_id");
            sb.append(" left join m_product_loan_variable_installment_config lpvi on lpvi.loan_product_id = l.product_id");
            sb.append(" left join m_loan_topup as topup on l.id = topup.loan_id");
            sb.append(" left join m_loan as topuploan on topuploan.id = topup.closure_loan_id");
            sb.append(" LEFT JOIN m_payment_type pt_disburse ON pt_disburse.id = l.expected_disbursal_payment_type_id ");
            sb.append(" LEFT JOIN m_payment_type pt_repayment ON pt_repayment.id = l.expected_repayment_payment_type_id ");
            this.loanSql = sb.toString();
        }

        public String loanSchema() {
            return this.loanSql;
        }

        @Override
        public LoanAccountData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            final CurrencyData currencyData = this.currencyMapper.mapRow(rs, rowNum);

            final Long id = rs.getLong("id");
            final String accountNo = rs.getString("accountNo");
            final String externalId = rs.getString("externalId");

            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final String clientAccountNo = rs.getString("clientAccountNo");
            final Long clientOfficeId = JdbcSupport.getLong(rs, "clientOfficeId");
            final String clientName = rs.getString("clientName");
            final String mobileNo = rs.getString("mobileNo");

            final Long groupId = JdbcSupport.getLong(rs, "groupId");
            final String groupName = rs.getString("groupName");
            final String groupAccountNo = rs.getString("groupAccountNo");
            final String groupExternalId = rs.getString("groupExternalId");
            final Long groupOfficeId = JdbcSupport.getLong(rs, "groupOfficeId");
            final Long groupStaffId = JdbcSupport.getLong(rs, "groupStaffId");
            final Long groupParentId = JdbcSupport.getLong(rs, "groupParentId");
            final String centerName = rs.getString("centerName");
            final String groupHierarchy = rs.getString("groupHierarchy");
            final String groupLevel = rs.getString("groupLevel");

            final Integer loanTypeId = JdbcSupport.getInteger(rs, "loanType");
            final EnumOptionData loanType = AccountEnumerations.loanType(loanTypeId);

            final Long fundId = JdbcSupport.getLong(rs, "fundId");
            final String fundName = rs.getString("fundName");

            final Long loanOfficerId = JdbcSupport.getLong(rs, "loanOfficerId");
            final String loanOfficerName = rs.getString("loanOfficerName");

            final Long loanPurposeId = JdbcSupport.getLong(rs, "loanPurposeId");
            final String loanPurposeName = rs.getString("loanPurposeName");
            
            final Integer weeksInYearTypeInteger = JdbcSupport.getInteger(rs, "weeksInYearType");
            final EnumOptionData weeksInYearType = LoanEnumerations.weeksInYearType(WeeksInYearType.fromInt(weeksInYearTypeInteger));

            final Long loanProductId = JdbcSupport.getLong(rs, "loanProductId");
            final String loanProductName = rs.getString("loanProductName");
            final String loanProductDescription = rs.getString("loanProductDescription");
            final boolean isLoanProductLinkedToFloatingRate = rs.getBoolean("isLoanProductLinkedToFloatingRate");
            final Boolean multiDisburseLoan = rs.getBoolean("multiDisburseLoan");
            final Boolean canDefineInstallmentAmount = rs.getBoolean("canDefineInstallmentAmount");
            final BigDecimal outstandingLoanBalance = rs.getBigDecimal("outstandingLoanBalance");

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

            final LocalDate expectedDisbursementDate = JdbcSupport.getLocalDate(rs, "expectedDisbursementDate");
            final LocalDate actualDisbursementDate = JdbcSupport.getLocalDate(rs, "actualDisbursementDate");
            final String disbursedByUsername = rs.getString("disbursedByUsername");
            final String disbursedByFirstname = rs.getString("disbursedByFirstname");
            final String disbursedByLastname = rs.getString("disbursedByLastname");

            final LocalDate closedOnDate = JdbcSupport.getLocalDate(rs, "closedOnDate");
            final String closedByUsername = rs.getString("closedByUsername");
            final String closedByFirstname = rs.getString("closedByFirstname");
            final String closedByLastname = rs.getString("closedByLastname");

            final LocalDate writtenOffOnDate = JdbcSupport.getLocalDate(rs, "writtenOffOnDate");
            final Long writeoffReasonId = JdbcSupport.getLong(rs, "writeoffReasonId");
            final String writeoffReason = rs.getString("writeoffReason");
            final LocalDate expectedMaturityDate = JdbcSupport.getLocalDate(rs, "expectedMaturityDate");

            final Boolean isvariableInstallmentsAllowed = rs.getBoolean("isvariableInstallmentsAllowed");
            final Integer minimumGap = rs.getInt("minimuminstallmentgap");
            final Integer maximumGap = rs.getInt("maximuminstallmentgap");
            
            final LoanApplicationTimelineData timeline = new LoanApplicationTimelineData(submittedOnDate, submittedByUsername,
                    submittedByFirstname, submittedByLastname, rejectedOnDate, rejectedByUsername, rejectedByFirstname, rejectedByLastname,
                    withdrawnOnDate, withdrawnByUsername, withdrawnByFirstname, withdrawnByLastname, approvedOnDate, approvedByUsername,
                    approvedByFirstname, approvedByLastname, expectedDisbursementDate, actualDisbursementDate, disbursedByUsername,
                    disbursedByFirstname, disbursedByLastname, closedOnDate, closedByUsername, closedByFirstname, closedByLastname,
                    expectedMaturityDate, writtenOffOnDate, closedByUsername, closedByFirstname, closedByLastname);

            final BigDecimal principal = rs.getBigDecimal("principal");
            final BigDecimal approvedPrincipal = rs.getBigDecimal("approvedPrincipal");
            final BigDecimal proposedPrincipal = rs.getBigDecimal("proposedPrincipal");
            final BigDecimal totalOverpaid = rs.getBigDecimal("totalOverpaid");
            final BigDecimal inArrearsTolerance = rs.getBigDecimal("inArrearsTolerance");

            final Integer numberOfRepayments = JdbcSupport.getInteger(rs, "numberOfRepayments");
            final Integer repaymentEvery = JdbcSupport.getInteger(rs, "repaymentEvery");
            final BigDecimal interestRatePerPeriod = rs.getBigDecimal("interestRatePerPeriod");
            final BigDecimal annualInterestRate = rs.getBigDecimal("annualInterestRate");
            final BigDecimal interestRateDifferential = rs.getBigDecimal("interestRateDifferential");
            final boolean isFloatingInterestRate = rs.getBoolean("isFloatingInterestRate");

            final Integer graceOnPrincipalPayment = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnPrincipalPayment");
            final Integer recurringMoratoriumOnPrincipalPeriods = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "recurringMoratoriumOnPrincipalPeriods");
            final Integer graceOnInterestPayment = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnInterestPayment");
            final Integer graceOnInterestCharged = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnInterestCharged");
            final Integer graceOnArrearsAgeing = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnArrearsAgeing");

            final Integer termFrequency = JdbcSupport.getInteger(rs, "termFrequency");
            final Integer termPeriodFrequencyTypeInt = JdbcSupport.getInteger(rs, "termPeriodFrequencyType");
            final EnumOptionData termPeriodFrequencyType = LoanEnumerations.termFrequencyType(termPeriodFrequencyTypeInt);

            final int repaymentFrequencyTypeInt = JdbcSupport.getInteger(rs, "repaymentFrequencyType");
            final EnumOptionData repaymentFrequencyType = LoanEnumerations.repaymentFrequencyType(repaymentFrequencyTypeInt);

            final int interestRateFrequencyTypeInt = JdbcSupport.getInteger(rs, "interestRateFrequencyType");
            final EnumOptionData interestRateFrequencyType = LoanEnumerations.interestRateFrequencyType(interestRateFrequencyTypeInt);

            final Long transactionStrategyId = JdbcSupport.getLong(rs, "transactionStrategyId");
            final String transactionStrategyName = rs.getString("transactionStrategyName");
            final String transactionStrategyCode = rs.getString("transactionStrategyCode");

            final int amortizationTypeInt = JdbcSupport.getInteger(rs, "amortizationType");
            final int interestTypeInt = JdbcSupport.getInteger(rs, "interestType");
            final int interestCalculationPeriodTypeInt = JdbcSupport.getInteger(rs, "interestCalculationPeriodType");

            final EnumOptionData amortizationType = LoanEnumerations.amortizationType(amortizationTypeInt);
            final EnumOptionData interestType = LoanEnumerations.interestType(interestTypeInt);
            final EnumOptionData interestCalculationPeriodType = LoanEnumerations
                    .interestCalculationPeriodType(interestCalculationPeriodTypeInt);
            final Boolean allowPartialPeriodInterestCalcualtion = rs.getBoolean("allowPartialPeriodInterestCalcualtion");

            final Integer lifeCycleStatusId = JdbcSupport.getInteger(rs, "lifeCycleStatusId");
            final LoanStatusEnumData status = LoanEnumerations.status(lifeCycleStatusId);

            final Integer loanSubStatusId = JdbcSupport.getInteger(rs, "loanSubStatusId");
            EnumOptionData loanSubStatus = null;
            if (loanSubStatusId != null) {
                loanSubStatus = LoanSubStatus.loanSubStatus(loanSubStatusId);
            }

            // settings
            final LocalDate expectedFirstRepaymentOnDate = JdbcSupport.getLocalDate(rs, "expectedFirstRepaymentOnDate");
            final LocalDate interestChargedFromDate = JdbcSupport.getLocalDate(rs, "interestChargedFromDate");

            final Boolean syncDisbursementWithMeeting = rs.getBoolean("syncDisbursementWithMeeting");

            final BigDecimal feeChargesDueAtDisbursementCharged = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs,
                    "feeChargesDueAtDisbursementCharged");
            LoanSummaryData loanSummary = null;
            Boolean inArrears = false;
            if (status.id().intValue() >= 300) {

                // loan summary
                final BigDecimal principalDisbursed = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalDisbursed");
                final BigDecimal principalPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalPaid");
                final BigDecimal principalWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalWrittenOff");
                final BigDecimal principalOutstanding = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalOutstanding");
                final BigDecimal principalOverdue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalOverdue");
                final BigDecimal principalNetDisbursed = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalNetDisbursed");

                final BigDecimal interestCharged = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestCharged");
                final BigDecimal interestPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestPaid");
                final BigDecimal interestWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWaived");
                final BigDecimal interestWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWrittenOff");
                final BigDecimal interestOutstanding = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestOutstanding");
                final BigDecimal interestOverdue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestOverdue");

                final BigDecimal feeChargesCharged = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesCharged");
                final BigDecimal feeChargesPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesPaid");
                final BigDecimal feeChargesWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesWaived");
                final BigDecimal feeChargesWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesWrittenOff");
                final BigDecimal feeChargesOutstanding = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesOutstanding");
                final BigDecimal feeChargesOverdue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesOverdue");

                final BigDecimal penaltyChargesCharged = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesCharged");
                final BigDecimal penaltyChargesPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesPaid");
                final BigDecimal penaltyChargesWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesWaived");
                final BigDecimal penaltyChargesWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesWrittenOff");
                final BigDecimal penaltyChargesOutstanding = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesOutstanding");
                final BigDecimal penaltyChargesOverdue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesOverdue");

                final BigDecimal totalExpectedRepayment = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalExpectedRepayment");
                final BigDecimal totalRepayment = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalRepayment");
                final BigDecimal totalExpectedCostOfLoan = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalExpectedCostOfLoan");
                final BigDecimal totalCostOfLoan = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalCostOfLoan");
                final BigDecimal totalWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalWaived");
                final BigDecimal totalWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalWrittenOff");
                final BigDecimal totalOutstanding = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalOutstanding");
                final BigDecimal totalOverdue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalOverdue");

                final LocalDate overdueSinceDate = JdbcSupport.getLocalDate(rs, "overdueSinceDate");
                if (overdueSinceDate != null) {
                    inArrears = true;
                }

                loanSummary = new LoanSummaryData(currencyData, principalDisbursed, principalPaid, principalWrittenOff,
                        principalOutstanding, principalOverdue, principalNetDisbursed, interestCharged, interestPaid, interestWaived,
                        interestWrittenOff, interestOutstanding, interestOverdue, feeChargesCharged, feeChargesDueAtDisbursementCharged,
                        feeChargesPaid, feeChargesWaived, feeChargesWrittenOff, feeChargesOutstanding, feeChargesOverdue,
                        penaltyChargesCharged, penaltyChargesPaid, penaltyChargesWaived, penaltyChargesWrittenOff,
                        penaltyChargesOutstanding, penaltyChargesOverdue, totalExpectedRepayment, totalRepayment, totalExpectedCostOfLoan,
                        totalCostOfLoan, totalWaived, totalWrittenOff, totalOutstanding, totalOverdue,overdueSinceDate, writeoffReasonId, writeoffReason);
            }

            GroupGeneralData groupData = null;
            if (groupId != null) {
                final Integer groupStatusEnum = JdbcSupport.getInteger(rs, "statusEnum");
                final EnumOptionData groupStatus = ClientEnumerations.status(groupStatusEnum);
                final LocalDate activationDate = JdbcSupport.getLocalDate(rs, "activationDate");
                groupData = GroupGeneralData.instance(groupId, groupAccountNo, groupName, groupExternalId, groupStatus, activationDate,
                        groupOfficeId, null, groupParentId, centerName, groupStaffId, null, groupHierarchy, groupLevel, null);
            }

            final Integer loanCounter = JdbcSupport.getInteger(rs, "loanCounter");
            final Integer loanProductCounter = JdbcSupport.getInteger(rs, "loanProductCounter");
            final BigDecimal fixedEmiAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "fixedEmiAmount");
            final Boolean isNPA = rs.getBoolean("isNPA");

            final int daysInMonth = JdbcSupport.getInteger(rs, "daysInMonth");
            final EnumOptionData daysInMonthType = CommonEnumerations.daysInMonthType(daysInMonth);
            final int daysInYear = JdbcSupport.getInteger(rs, "daysInYear");
            final EnumOptionData daysInYearType = CommonEnumerations.daysInYearType(daysInYear);
            final boolean isInterestRecalculationEnabled = rs.getBoolean("isInterestRecalculationEnabled");
            final Boolean createStandingInstructionAtDisbursement = rs.getBoolean("createStandingInstructionAtDisbursement");
            
            Integer brokenPeriodTypeId = JdbcSupport.getInteger(rs, "brokenPeriodMethodType");
            EnumOptionData brokenPeriodMethodType = null;
            if (brokenPeriodTypeId != null) {
                brokenPeriodMethodType = LoanEnumerations.brokenPeriodMethodType(brokenPeriodTypeId);
            }
            final BigDecimal brokenPeriodInterest = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "brokenPeriodInterest");
            final Boolean considerFutureDisbursmentsInSchedule = rs.getBoolean("considerFutureDisbursmentsInSchedule");
            final Boolean considerAllDisbursementsInSchedule = rs.getBoolean("considerAllDisbursementsInSchedule");

            LoanInterestRecalculationData interestRecalculationData = null;
            if (isInterestRecalculationEnabled) {
                final String codePrefix = "interestRecalculationCompounding.";
                final Long lprId = JdbcSupport.getLong(rs, "lirId");
                final Long productId = JdbcSupport.getLong(rs, "loanId");
                final int compoundTypeEnumValue = JdbcSupport.getInteger(rs, "compoundType");
                final EnumOptionData interestRecalculationCompoundingType = LoanEnumerations
                        .interestRecalculationCompoundingType(compoundTypeEnumValue);
                final int rescheduleStrategyEnumValue = JdbcSupport.getInteger(rs, "rescheduleStrategy");
                final EnumOptionData rescheduleStrategyType = LoanEnumerations.rescheduleStrategyType(rescheduleStrategyEnumValue);
                final CalendarData calendarData = null;
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
                final CalendarData compoundingCalendarData = null;
                final Integer compoundingFrequencyEnumValue = JdbcSupport.getInteger(rs, "compoundingFrequencyEnum");
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
                
                final Boolean isSubsidyApplicable = rs.getBoolean("isSubsidyApplicable");

                final Boolean isCompoundingToBePostedAsTransaction = rs.getBoolean("isCompoundingToBePostedAsTransaction");
                final Boolean allowCompoundingOnEod = rs.getBoolean("allowCompoundingOnEod");
                final LocalDate recalculationRestFrequencyStartDate = JdbcSupport.getLocalDate(rs, "recalculationRestFrequencyStartDate");
                final LocalDate recalculationCompoundingFrequencyStartDate = JdbcSupport.getLocalDate(rs, "recalculationCompoundingFrequencyStartDate");
                
                interestRecalculationData = new LoanInterestRecalculationData(lprId, productId, interestRecalculationCompoundingType,
                        rescheduleStrategyType, calendarData, restFrequencyType, restFrequencyInterval, restFrequencyNthDayEnum,
                        restFrequencyWeekDayEnum, restFrequencyOnDay, compoundingCalendarData, compoundingFrequencyType,
                        compoundingInterval, compoundingFrequencyNthDayEnum, compoundingFrequencyWeekDayEnum, compoundingFrequencyOnDay,
                        isCompoundingToBePostedAsTransaction, allowCompoundingOnEod, isSubsidyApplicable,
                        recalculationRestFrequencyStartDate, recalculationCompoundingFrequencyStartDate);
            }

            final boolean canUseForTopup = rs.getBoolean("canUseForTopup");
            final boolean isTopup = rs.getBoolean("isTopup");
            final Long closureLoanId = rs.getLong("closureLoanId");
            final String closureLoanAccountNo = rs.getString("closureLoanAccountNo");
            final BigDecimal topupAmount = rs.getBigDecimal("topupAmount");
            PaymentTypeData expectedDisbursalPaymentType = null;
            final Integer expectedDisbursalPaymentTypeId = JdbcSupport.getInteger(rs,"expectedDisbursalPaymentTypeId");
            if(expectedDisbursalPaymentTypeId != null){
                final String disbursementPaymentTypeName = rs.getString("disbursementPaymentTypeName");
                expectedDisbursalPaymentType = PaymentTypeData.instance(expectedDisbursalPaymentTypeId.longValue(), disbursementPaymentTypeName);            	
            }
            PaymentTypeData expectedRepaymentPaymentType = null;
            final Integer expectedRepaymentPaymentTypeId = JdbcSupport.getInteger(rs,"expectedRepaymentPaymentTypeId");
            if(expectedRepaymentPaymentTypeId != null){
            	final String repaymenPaymentTypeName = rs.getString("repaymenPaymentTypeName");
            	expectedRepaymentPaymentType = PaymentTypeData.instance(expectedRepaymentPaymentTypeId.longValue(), repaymenPaymentTypeName);            	
            } 
            final BigDecimal flatInterestRate = rs.getBigDecimal("flatInterestRate");
            final BigDecimal discountOnDisbursalAmount = rs.getBigDecimal("discountOnDisbursalAmount");
            final Boolean allowUpfrontCollection = rs.getBoolean("allowUpfrontCollection");
            final BigDecimal amountForUpfrontCollection = rs.getBigDecimal("amountForUpfrontCollection");
            final BigDecimal calculatedEmiAmount = rs.getBigDecimal("calculatedEmiAmount") ;
            final boolean isLocked = rs.getBoolean("isLocked");

            return LoanAccountData.basicLoanDetails(id, accountNo, status, externalId, clientId, clientAccountNo, clientName, mobileNo,
                    clientOfficeId, groupData, loanType, loanProductId, loanProductName, loanProductDescription,
                    isLoanProductLinkedToFloatingRate, fundId, fundName, loanPurposeId, loanPurposeName, loanOfficerId, loanOfficerName,
                    currencyData, proposedPrincipal, principal, approvedPrincipal, totalOverpaid, inArrearsTolerance, termFrequency,
                    termPeriodFrequencyType, numberOfRepayments, repaymentEvery, repaymentFrequencyType, null, null, transactionStrategyId,
                    transactionStrategyCode, transactionStrategyName, amortizationType, interestRatePerPeriod, interestRateFrequencyType,
                    annualInterestRate, interestType, isFloatingInterestRate, interestRateDifferential, interestCalculationPeriodType,
                    allowPartialPeriodInterestCalcualtion, expectedFirstRepaymentOnDate, graceOnPrincipalPayment,
                    recurringMoratoriumOnPrincipalPeriods, graceOnInterestPayment, graceOnInterestCharged, interestChargedFromDate,
                    timeline, loanSummary, feeChargesDueAtDisbursementCharged, syncDisbursementWithMeeting, loanCounter,
                    loanProductCounter, multiDisburseLoan, canDefineInstallmentAmount, fixedEmiAmount, outstandingLoanBalance, inArrears,
                    graceOnArrearsAgeing, isNPA, daysInMonthType, daysInYearType, isInterestRecalculationEnabled,
                    interestRecalculationData, createStandingInstructionAtDisbursement, isvariableInstallmentsAllowed, minimumGap,
                    maximumGap, loanSubStatus, canUseForTopup, isTopup, closureLoanId, closureLoanAccountNo, topupAmount, weeksInYearType,
                    expectedDisbursalPaymentType, expectedRepaymentPaymentType, brokenPeriodMethodType, flatInterestRate,
                    brokenPeriodInterest, considerFutureDisbursmentsInSchedule, considerAllDisbursementsInSchedule,
                    discountOnDisbursalAmount, allowUpfrontCollection, amountForUpfrontCollection, calculatedEmiAmount, isLocked);
        }
    }
    
    private static final class LoanLookupMapper extends LoanMapper implements RowMapper<LoanAccountData> {

        public String loanSchema() {

            return " l.id AS id, l.account_no AS accountNo, c.id AS clientId, c.display_name AS clientName, lp.name AS loanProductName, "
                    + "l.expected_disbursedon_date AS expectedDisbursementDate, l.principal_amount AS principal, l.expected_disbursal_payment_type_id as expectedDisbursalPaymentTypeId,"
            		+" pt_disburse.value as disbursementPaymentTypeName,l.expected_repayment_payment_type_id as expectedRepaymentPaymentTypeId, pt_repayment.value as repaymenPaymentTypeName, l.loan_status_id AS lifeCycleStatusId "
                    + " FROM m_loan l " + "JOIN m_product_loan lp ON lp.id = l.product_id " + "LEFT JOIN m_client c ON c.id = l.client_id "
                    + "LEFT JOIN m_group g ON g.id = l.group_id " + "LEFT JOIN m_staff s ON s.id = l.loan_officer_id "
                    + "LEFT JOIN m_payment_type pt_disburse ON pt_disburse.id = l.expected_disbursal_payment_type_id "
                    + "LEFT JOIN m_payment_type pt_repayment ON pt_repayment.id = l.expected_repayment_payment_type_id ";

        }
        @Override
        public LoanAccountData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String accountNo = rs.getString("accountNo");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final String clientName = rs.getString("clientName");
            final String loanProductName = rs.getString("loanProductName");

            final LocalDate expectedDisbursementDate = JdbcSupport.getLocalDate(rs, "expectedDisbursementDate");
            final LoanApplicationTimelineData timeline = LoanApplicationTimelineData.templateDefault(expectedDisbursementDate);

            final BigDecimal principal = rs.getBigDecimal("principal");

            final Integer lifeCycleStatusId = JdbcSupport.getInteger(rs, "lifeCycleStatusId");
            final LoanStatusEnumData status = LoanEnumerations.status(lifeCycleStatusId);
            final Integer expectedDisbursalPaymentTypeId = rs.getInt("expectedDisbursalPaymentTypeId");
            PaymentTypeData expectedDisbursalPaymentType = null;
            if(expectedDisbursalPaymentTypeId != null){
                final String disbursementPaymentTypeName = rs.getString("disbursementPaymentTypeName");
                expectedDisbursalPaymentType = PaymentTypeData.instance(expectedDisbursalPaymentTypeId.longValue(), disbursementPaymentTypeName);            	
            }
            PaymentTypeData expectedRepaymentPaymentType = null;
            final Integer expectedRepaymentPaymentTypeId = rs.getInt("expectedRepaymentPaymentTypeId");
            if(expectedRepaymentPaymentTypeId != null){
            	final String repaymenPaymentTypeName = rs.getString("repaymenPaymentTypeName");
            	expectedRepaymentPaymentType = PaymentTypeData.instance(expectedRepaymentPaymentTypeId.longValue(), repaymenPaymentTypeName);            	
            }           

            return LoanAccountData.basicLoanDetailsForDataLookup(id, accountNo, status, clientId, clientName, loanProductName,
                    principal, timeline, expectedDisbursalPaymentType, expectedRepaymentPaymentType);
        }
    }
    @Override
    public Collection<LoanAccountData> retrieveAllForTaskLookupBySearchParameters(final SearchParameters searchParameters) {
        final AppUser currentUser = this.context.authenticatedUser();
        String hierarchy = currentUser.getOffice().getHierarchy() + "%";
        final StringBuilder builderSelectQuery = new StringBuilder(400);
        /**
         * This is used for to build the multiple where conditions.
         */
        final StringBuilder builderWhereConditions = new StringBuilder(100);
        final LoanTaskLookupMapper mapper = new LoanTaskLookupMapper();
        List<Object> params = new ArrayList<>();
        builderSelectQuery.append("select ");
        builderSelectQuery.append("ml.id as id, ml.account_no AS accountNo,ml.loan_status_id AS lifeCycleStatusId, ");
        builderSelectQuery.append("g.id as groupId, ");
        builderSelectQuery.append("g.account_no as groupAccountNo, ");
        builderSelectQuery.append("g.display_name as groupName, ");
        builderSelectQuery.append("g.level_id as groupLevel, ");
        builderSelectQuery.append("c.id as clientId, c.account_no AS clientAccountNo,c.display_name AS clientName, ");
        builderSelectQuery
                .append("mp.id as loanProductId,  mp.name as loanProductName, ml.loan_purpose_id as loanPurposeId, flp.name as loanPurposeName, ");
        builderSelectQuery.append(" ml.loan_officer_id AS loanOfficerId, s.display_name AS loanOfficerName,  ");
        builderSelectQuery.append("ml.principal_amount as principal, dd.principal as firstTrancheAmount, ml.loan_type_enum as loanType, ");
        builderSelectQuery.append("pt.value as paymentTypeName, ml.expected_disbursal_payment_type_id as expectedDisbursalPaymentTypeId ");
        builderSelectQuery.append("from m_loan ml ");
        builderSelectQuery.append("join m_product_loan mp on mp.id = ml.product_id ");
        builderSelectQuery.append("LEFT JOIN m_staff s ON s.id = ml.loan_officer_id ");
        builderSelectQuery
                .append(" left join m_loan_disbursement_detail dd on dd.loan_id = ml.id and dd.expected_disburse_date = ml.expected_disbursedon_date ");
        builderSelectQuery.append(" left join f_loan_purpose flp on flp.id = ml.loan_purpose_id  ");
        builderSelectQuery.append(" left join m_client c on ml.client_id = c.id ");
        builderSelectQuery.append(" left JOIN m_group g ON g.id = ml.group_id ");
        builderSelectQuery.append(" JOIN m_office o ON (o.id = g.office_id or o.id = c.office_id) AND o.hierarchy LIKE ? ");
        builderSelectQuery.append(" left JOIN m_payment_type pt ON ml.expected_disbursal_payment_type_id = pt.id ");
        params.add(hierarchy);
        if (searchParameters.getOfficeId() != null) {
            builderWhereConditions.append(" and o.id = ? ");
            params.add(searchParameters.getOfficeId());
        }
        if (searchParameters.getStaffId() != null) {
            builderWhereConditions.append(" and s.id = ?");
            params.add(searchParameters.getStaffId());
        }
        if (searchParameters.getGroupId() != null) {
            builderWhereConditions.append(" and g.id = ?");
            params.add(searchParameters.getGroupId());
        }
        if (searchParameters.getCenterId() != null) {
            builderWhereConditions.append(" and g.parent_id = ?");
            params.add(searchParameters.getCenterId());
        }
        if (searchParameters.getPaymentType() != null) {
            builderWhereConditions.append(" and ml.expected_disbursal_payment_type_id = ? ");
            params.add(searchParameters.getPaymentType());
        }
        
        final Map<String, String> searchConditions = searchParameters.getSearchConditions();
        searchConditions.forEach((key, value) -> {
            switch (key) {
                case CommonConstants.LOAN_STATUS:
                    builderWhereConditions.append(" and ( ml.loan_status_id = ").append(value).append(" ) ");
                break;
                case CommonConstants.LOAN_EXPECTED_DISBURSEDON_DATE:
                    builderWhereConditions.append(" and ( ml.expected_disbursedon_date = '").append(value).append("' ) ");
                break;
                default:
                break;
            }
        });
        
        builderSelectQuery.append(builderWhereConditions.toString().replaceFirst("(?i)and", " where "));
        return this.jdbcTemplate.query(builderSelectQuery.toString(), mapper, params.toArray());
    }
    
    private static final class LoanTaskLookupMapper implements RowMapper<LoanAccountData> {
        @Override
        public LoanAccountData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
           
            final Long id = rs.getLong("id");
            final String accountNo = rs.getString("accountNo");
            final Integer lifeCycleStatusId = JdbcSupport.getInteger(rs, "lifeCycleStatusId");
            final LoanStatusEnumData status = LoanEnumerations.status(lifeCycleStatusId);
            final Long groupId = rs.getLong("groupId");
            final String groupName = rs.getString("groupName");
            final String groupAccountNo = rs.getString("groupAccountNo");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final String clientAccountNo = rs.getString("clientAccountNo");
            final String clientName = rs.getString("clientName");
            final Long loanProductId = JdbcSupport.getLong(rs, "loanProductId");
            final String loanProductName = rs.getString("loanProductName");
            final Long loanPurposeId = JdbcSupport.getLong(rs, "loanPurposeId");
            final String loanPurposeName = rs.getString("loanPurposeName");
            final Long loanOfficerId = JdbcSupport.getLong(rs, "loanOfficerId");
            final String loanOfficerName = rs.getString("loanOfficerName");
            final Integer loanTypeId = JdbcSupport.getInteger(rs, "loanType");
            final EnumOptionData loanType = AccountEnumerations.loanType(loanTypeId);
            BigDecimal principal = rs.getBigDecimal("principal");
            final BigDecimal firstTrancheAmount = rs.getBigDecimal("firstTrancheAmount");
            if(firstTrancheAmount !=null && LoanStatus.fromInt(lifeCycleStatusId).isApproved()){
                principal = firstTrancheAmount;
            }
            final Long expectedDisbursalPaymentTypeId = JdbcSupport.getLong(rs, "expectedDisbursalPaymentTypeId");
            final String paymentTypeName = rs.getString("paymentTypeName");
            final String groupLevel=rs.getString("groupLevel");
            final GroupGeneralData group = GroupGeneralData.lookup(groupId, groupAccountNo, groupName,groupLevel);
            PaymentTypeData expectedDisbursalPaymentTypeData = PaymentTypeData.lookUp(expectedDisbursalPaymentTypeId, paymentTypeName);
            return LoanAccountData.loanDetailsForTaskLookup(id,accountNo,status,clientId, clientAccountNo, clientName, loanProductId,
                    loanProductName, loanPurposeId, loanPurposeName, loanOfficerId, loanOfficerName, loanType,
                     principal, expectedDisbursalPaymentTypeData,group);
        }
    }

    private static final class LoanScheduleResultSetExtractor implements ResultSetExtractor<LoanScheduleData> {

        private final CurrencyData currency;
        private final DisbursementData disbursement;
        private final BigDecimal totalFeeChargesDueAtDisbursement;
        private final Collection<DisbursementData> disbursementData;
        private LocalDate lastDueDate;
        private BigDecimal outstandingLoanPrincipalBalance;
        private BigDecimal totalPaidFeeCharges;
        private final BigDecimal interestPosted;
        private final boolean considerFutureDisbursmentsInSchedule;
        private final boolean considerAllDisbursementsInSchedule;
        private final BigDecimal discountOnDisbursalAmount;

        public LoanScheduleResultSetExtractor(final RepaymentScheduleRelatedLoanData repaymentScheduleRelatedLoanData,
                Collection<DisbursementData> disbursementData, BigDecimal totalPaidFeeCharges) {
            this.currency = repaymentScheduleRelatedLoanData.getCurrency();
            this.disbursement = repaymentScheduleRelatedLoanData.disbursementData();
            this.totalFeeChargesDueAtDisbursement = repaymentScheduleRelatedLoanData.getTotalFeeChargesAtDisbursement();
            this.lastDueDate = this.disbursement.disbursementDate();
            this.outstandingLoanPrincipalBalance = this.disbursement.amount().subtract(repaymentScheduleRelatedLoanData.getDiscountedFromPrincipal());
            this.disbursementData = disbursementData;
            this.totalPaidFeeCharges = totalPaidFeeCharges;
            this.interestPosted = repaymentScheduleRelatedLoanData.getInterestPostedAmount();
            this.considerFutureDisbursmentsInSchedule = repaymentScheduleRelatedLoanData.isConsiderFutureDisbursmentsInSchedule();
            this.considerAllDisbursementsInSchedule = repaymentScheduleRelatedLoanData.isConsiderAllDisbursementsInSchedule();
            this.discountOnDisbursalAmount = repaymentScheduleRelatedLoanData.getDiscountedFromPrincipal();
        }

        public String schema() {

            return " ls.loan_id as loanId, ls.installment as period, ls.fromdate as fromDate, ls.duedate as dueDate, ls.obligations_met_on_date as obligationsMetOnDate, ls.completed_derived as complete,"
                    + " ls.principal_amount as principalDue, ls.principal_completed_derived as principalPaid, ls.principal_writtenoff_derived as principalWrittenOff, "
                    + " ls.interest_amount as interestDue, ls.interest_completed_derived as interestPaid, ls.interest_waived_derived as interestWaived, ls.interest_writtenoff_derived as interestWrittenOff, "
                    + " ls.fee_charges_amount as feeChargesDue, ls.fee_charges_completed_derived as feeChargesPaid, ls.fee_charges_waived_derived as feeChargesWaived, ls.fee_charges_writtenoff_derived as feeChargesWrittenOff, "
                    + " ls.penalty_charges_amount as penaltyChargesDue, ls.penalty_charges_completed_derived as penaltyChargesPaid, ls.penalty_charges_waived_derived as penaltyChargesWaived, ls.penalty_charges_writtenoff_derived as penaltyChargesWrittenOff, "
                    + " ls.total_paid_in_advance_derived as totalPaidInAdvanceForPeriod, ls.total_paid_late_derived as totalPaidLateForPeriod, ls.advance_payment_amount as advancePaymentAmount "
                    + " from m_loan_repayment_schedule ls ";
        }

        @Override
        public LoanScheduleData extractData(final ResultSet rs) throws SQLException, DataAccessException {

            final LoanSchedulePeriodData disbursementPeriod = LoanSchedulePeriodData.disbursementOnlyPeriod(
                    this.disbursement.disbursementDate(), this.disbursement.amount().subtract(discountOnDisbursalAmount), this.totalFeeChargesDueAtDisbursement,
                    this.disbursement.isDisbursed());

            boolean incluedeAllDisbursements = true;
            
            final Collection<LoanSchedulePeriodData> periods = new ArrayList<>();
            final MonetaryCurrency monCurrency = new MonetaryCurrency(this.currency.code(), this.currency.decimalPlaces(),
                    this.currency.currencyInMultiplesOf());
            BigDecimal totalPrincipalDisbursed = BigDecimal.ZERO;
            BigDecimal disbursementChargeAmount = this.totalFeeChargesDueAtDisbursement;
            if (disbursementData == null || disbursementData.isEmpty()) {
                periods.add(disbursementPeriod);
                totalPrincipalDisbursed = Money.of(monCurrency, this.disbursement.amount()).minus(discountOnDisbursalAmount).getAmount();
            } else {
                if (this.disbursement.isDisbursed()) {
                    incluedeAllDisbursements = considerAllDisbursementsInSchedule;
                }
                for (DisbursementData data : disbursementData) {
                    if (data.getChargeAmount() != null) {
                        if (!this.disbursement.isDisbursed() || data.isDisbursed()) {
                            disbursementChargeAmount = disbursementChargeAmount.subtract(data.getChargeAmount());
                        }
                    }
                }
                this.outstandingLoanPrincipalBalance = BigDecimal.ZERO;
            }

            Money totalPrincipalExpected = Money.zero(monCurrency);
            Money totalPrincipalPaid = Money.zero(monCurrency);
            Money totalInterestCharged = Money.zero(monCurrency);
            Money totalFeeChargesCharged = Money.zero(monCurrency);
            Money totalPenaltyChargesCharged = Money.zero(monCurrency);
            Money totalWaived = Money.zero(monCurrency);
            Money totalWrittenOff = Money.zero(monCurrency);
            Money totalRepaymentExpected = Money.zero(monCurrency);
            Money totalRepayment = Money.zero(monCurrency);
            Money totalPaidInAdvance = Money.zero(monCurrency);
            Money totalAdvancePayment = Money.zero(monCurrency);
            Money totalPaidLate = Money.zero(monCurrency);
            Money totalOutstanding = Money.zero(monCurrency);

            // update totals with details of fees charged during disbursement
            totalFeeChargesCharged = totalFeeChargesCharged.plus(disbursementPeriod.feeChargesDue());
            totalRepaymentExpected = totalRepaymentExpected.plus(disbursementPeriod.feeChargesDue());
            totalRepayment = totalRepayment.plus(disbursementPeriod.feeChargesPaid());
            totalOutstanding = totalOutstanding.plus(disbursementPeriod.feeChargesDue()).minus(disbursementPeriod.feeChargesPaid());

            Integer loanTermInDays = Integer.valueOf(0);
            while (rs.next()) {

                final Long loanId = rs.getLong("loanId");
                final Integer period = JdbcSupport.getInteger(rs, "period");
                LocalDate fromDate = JdbcSupport.getLocalDate(rs, "fromDate");
                final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");
                final LocalDate obligationsMetOnDate = JdbcSupport.getLocalDate(rs, "obligationsMetOnDate");
                final boolean complete = rs.getBoolean("complete");
                if (disbursementData != null) {
                    BigDecimal principal = BigDecimal.ZERO;
                    for (DisbursementData data : disbursementData) {
                        if (fromDate.equals(this.disbursement.disbursementDate()) && data.disbursementDate().equals(fromDate)) {
                            if (periods.size() == 0) {
                                principal = principal.add(data.amount()).add(interestPosted);
                                if (data.getChargeAmount() == null) {
                                    final LoanSchedulePeriodData periodData = LoanSchedulePeriodData.disbursementOnlyPeriod(
                                            data.disbursementDate(), principal, disbursementChargeAmount, data.isDisbursed());
                                    periods.add(periodData);
                                } else {
                                    final LoanSchedulePeriodData periodData = LoanSchedulePeriodData.disbursementOnlyPeriod(
                                            data.disbursementDate(), principal, disbursementChargeAmount.add(data.getChargeAmount()), data.isDisbursed());
                                    periods.add(periodData);
                                }
                                this.outstandingLoanPrincipalBalance = this.outstandingLoanPrincipalBalance.add(principal);
                            }
                        } else if (data.isDueForDisbursement(fromDate, dueDate)
                                && (incluedeAllDisbursements || data.isDisbursed() || (considerFutureDisbursmentsInSchedule && !data
                                        .disbursementDate().isBefore(DateUtils.getLocalDateOfTenant())))) {
                            principal = principal.add(data.amount());
                            if (data.getChargeAmount() == null) {
                                final LoanSchedulePeriodData periodData = LoanSchedulePeriodData.disbursementOnlyPeriod(
                                        data.disbursementDate(), data.amount(), BigDecimal.ZERO, data.isDisbursed());
                                periods.add(periodData);
                            } else {
                                final LoanSchedulePeriodData periodData = LoanSchedulePeriodData.disbursementOnlyPeriod(
                                        data.disbursementDate(), data.amount(), data.getChargeAmount(), data.isDisbursed());
                                periods.add(periodData);
                            }
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
                final BigDecimal principalPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalPaid");
                totalPrincipalPaid = totalPrincipalPaid.plus(principalPaid);
                final BigDecimal principalWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalWrittenOff");

                final BigDecimal principalOutstanding = principalDue.subtract(principalPaid).subtract(principalWrittenOff);

                final BigDecimal interestExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestDue");
                totalInterestCharged = totalInterestCharged.plus(interestExpectedDue);
                final BigDecimal interestPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestPaid");
                final BigDecimal interestWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWaived");
                final BigDecimal interestWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWrittenOff");
                final BigDecimal totalInstallmentAmount = totalPrincipalPaid.zero().plus(principalDue).plus(interestExpectedDue)
                        .getAmount();

                final BigDecimal interestActualDue = interestExpectedDue.subtract(interestWaived).subtract(interestWrittenOff);
                final BigDecimal interestOutstanding = interestActualDue.subtract(interestPaid);

                final BigDecimal feeChargesExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesDue");
                totalFeeChargesCharged = totalFeeChargesCharged.plus(feeChargesExpectedDue);
                final BigDecimal feeChargesPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesPaid");
                final BigDecimal feeChargesWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesWaived");
                final BigDecimal feeChargesWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesWrittenOff");

                final BigDecimal feeChargesActualDue = feeChargesExpectedDue.subtract(feeChargesWaived).subtract(feeChargesWrittenOff);
                final BigDecimal feeChargesOutstanding = feeChargesActualDue.subtract(feeChargesPaid);

                final BigDecimal penaltyChargesExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesDue");
                totalPenaltyChargesCharged = totalPenaltyChargesCharged.plus(penaltyChargesExpectedDue);
                final BigDecimal penaltyChargesPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesPaid");
                final BigDecimal penaltyChargesWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesWaived");
                final BigDecimal penaltyChargesWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesWrittenOff");

                final BigDecimal totalPaidInAdvanceForPeriod = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs,
                        "totalPaidInAdvanceForPeriod");
                final BigDecimal advancedPayment = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs,
                        "advancePaymentAmount");
                final BigDecimal totalPaidLateForPeriod = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalPaidLateForPeriod");

                final BigDecimal penaltyChargesActualDue = penaltyChargesExpectedDue.subtract(penaltyChargesWaived).subtract(
                        penaltyChargesWrittenOff);
                final BigDecimal penaltyChargesOutstanding = penaltyChargesActualDue.subtract(penaltyChargesPaid);

                final BigDecimal totalExpectedCostOfLoanForPeriod = interestExpectedDue.add(feeChargesExpectedDue).add(
                        penaltyChargesExpectedDue);

                final BigDecimal totalDueForPeriod = principalDue.add(totalExpectedCostOfLoanForPeriod);
                final BigDecimal totalPaidForPeriod = principalPaid.add(interestPaid).add(feeChargesPaid).add(penaltyChargesPaid);
                final BigDecimal totalWaivedForPeriod = interestWaived.add(feeChargesWaived).add(penaltyChargesWaived);
                totalWaived = totalWaived.plus(totalWaivedForPeriod);
                final BigDecimal totalWrittenOffForPeriod = principalWrittenOff.add(interestWrittenOff).add(feeChargesWrittenOff)
                        .add(penaltyChargesWrittenOff);
                totalWrittenOff = totalWrittenOff.plus(totalWrittenOffForPeriod);
                final BigDecimal totalOutstandingForPeriod = principalOutstanding.add(interestOutstanding).add(feeChargesOutstanding)
                        .add(penaltyChargesOutstanding);

                final BigDecimal totalActualCostOfLoanForPeriod = interestActualDue.add(feeChargesActualDue).add(penaltyChargesActualDue);

                totalRepaymentExpected = totalRepaymentExpected.plus(totalDueForPeriod);
                totalRepayment = totalRepayment.plus(totalPaidForPeriod);
                totalPaidInAdvance = totalPaidInAdvance.plus(totalPaidInAdvanceForPeriod);
                totalAdvancePayment = totalAdvancePayment.plus(advancedPayment);
                totalPaidLate = totalPaidLate.plus(totalPaidLateForPeriod);
                totalOutstanding = totalOutstanding.plus(totalOutstandingForPeriod);

                if (fromDate == null) {
                    fromDate = this.lastDueDate;
                }
                final BigDecimal outstandingPrincipalBalanceOfLoan = this.outstandingLoanPrincipalBalance.subtract(principalDue);

                // update based on current period values
                this.lastDueDate = dueDate;
                this.outstandingLoanPrincipalBalance = this.outstandingLoanPrincipalBalance.subtract(principalDue);

                final LoanSchedulePeriodData periodData = LoanSchedulePeriodData.repaymentPeriodWithPayments(loanId, period, fromDate,
                        dueDate, obligationsMetOnDate, complete, principalDue, principalPaid, principalWrittenOff, principalOutstanding,
                        outstandingPrincipalBalanceOfLoan, interestExpectedDue, interestPaid, interestWaived, interestWrittenOff,
                        interestOutstanding, feeChargesExpectedDue, feeChargesPaid, feeChargesWaived, feeChargesWrittenOff,
                        feeChargesOutstanding, penaltyChargesExpectedDue, penaltyChargesPaid, penaltyChargesWaived,
                        penaltyChargesWrittenOff, penaltyChargesOutstanding, totalDueForPeriod, totalPaidForPeriod,
                        totalPaidInAdvanceForPeriod, totalPaidLateForPeriod, totalWaivedForPeriod, totalWrittenOffForPeriod,
                        totalOutstandingForPeriod, totalActualCostOfLoanForPeriod, totalInstallmentAmount, advancedPayment);

                periods.add(periodData);
            }

            return new LoanScheduleData(this.currency, periods, loanTermInDays, totalPrincipalDisbursed,
                    totalPrincipalExpected.getAmount(), totalPrincipalPaid.getAmount(), totalInterestCharged.getAmount(),
                    totalFeeChargesCharged.getAmount(), totalPenaltyChargesCharged.getAmount(), totalWaived.getAmount(),
                    totalWrittenOff.getAmount(), totalRepaymentExpected.getAmount(), totalRepayment.getAmount(),
                    totalPaidInAdvance.getAmount(), totalPaidLate.getAmount(), totalOutstanding.getAmount(),
                    totalAdvancePayment.getAmount());
        }

    }
    
    
 

    private static final class LoanTransactionsMapper implements RowMapper<LoanTransactionData> {

        public String LoanPaymentsSchema() {

            return " tr.id as id, tr.transaction_type_enum as transactionType, tr.transaction_date as `date`, tr.amount as total, "
                    + " tr.principal_portion_derived as principal, tr.interest_portion_derived as interest, "
                    + " tr.fee_charges_portion_derived as fees, tr.penalty_charges_portion_derived as penalties, "
                    + " tr.overpayment_portion_derived as overpayment, tr.outstanding_loan_balance_derived as outstandingLoanBalance, "
                    + " tr.unrecognized_income_portion as unrecognizedIncome,"
                    + " tr.submitted_on_date as submittedOnDate, "
                    + " tr.created_date as createdDate, crb.username as createdByUserName, crb.id as createdById, crb.firstname as createdByFirstName, crb.lastname as createdByLastName, "
					+ " tr.lastmodified_date as updatedDate, lmb.username as updatedByUserName, lmb.id as updatedById, lmb.firstname as updatedByFirstName, lmb.lastname as updatedByLastName, "
                    + " tr.manually_adjusted_or_reversed as manuallyReversed, "
                    + " pd.payment_type_id as paymentType,pd.account_number as accountNumber,pd.check_number as checkNumber, "
                    + " pd.receipt_number as receiptNumber, pd.bank_number as bankNumber,pd.routing_code as routingCode, "
                    + " l.currency_code as currencyCode, l.currency_digits as currencyDigits, l.currency_multiplesof as inMultiplesOf, rc.`name` as currencyName, "
                    + " rc.display_symbol as currencyDisplaySymbol, rc.internationalized_name_code as currencyNameCode, "
                    + " pt.value as paymentTypeName, tr.external_id as externalId, tr.office_id as officeId, office.name as officeName, "
                    + " fromtran.id as fromTransferId, fromtran.is_reversed as fromTransferReversed,"
                    + " fromtran.transaction_date as fromTransferDate, fromtran.amount as fromTransferAmount,"
                    + " fromtran.description as fromTransferDescription,"
                    + " totran.id as toTransferId, totran.is_reversed as toTransferReversed,"
                    + " totran.transaction_date as toTransferDate, totran.amount as toTransferAmount,"
                    + " totran.description as toTransferDescription " + " from m_loan l join m_loan_transaction tr on tr.loan_id = l.id"
                    + " join m_currency rc on rc.`code` = l.currency_code "
                    + " left JOIN m_payment_detail pd ON tr.payment_detail_id = pd.id"
                    + " left join m_payment_type pt on pd.payment_type_id = pt.id" + " left join m_office office on office.id=tr.office_id"
                    + " left join m_account_transfer_transaction fromtran on fromtran.from_loan_transaction_id = tr.id "
                    + " left join m_account_transfer_transaction totran on totran.to_loan_transaction_id = tr.id "
                    + " left join m_appuser crb on crb.id = tr.createdby_id"
					+ " left join m_appuser lmb on lmb.id = tr.lastmodifiedby_id";
        }

        @Override
        public LoanTransactionData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currencyData = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);

            final Long id = rs.getLong("id");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
            final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(transactionTypeInt);
            final boolean manuallyReversed = rs.getBoolean("manuallyReversed");

            PaymentDetailData paymentDetailData = null;

            if (transactionType.isPaymentOrReceipt()) {
                final Long paymentTypeId = JdbcSupport.getLong(rs, "paymentType");
                if (paymentTypeId != null) {
                    final String typeName = rs.getString("paymentTypeName");
                    final PaymentTypeData paymentType = PaymentTypeData.instance(paymentTypeId, typeName);
                    final String accountNumber = rs.getString("accountNumber");
                    final String checkNumber = rs.getString("checkNumber");
                    final String routingCode = rs.getString("routingCode");
                    final String receiptNumber = rs.getString("receiptNumber");
                    final String bankNumber = rs.getString("bankNumber");
                    final String branchName = null ;
                    final Date paymentDate = null ;
                    paymentDetailData = new PaymentDetailData(id, paymentType, accountNumber, checkNumber, routingCode, receiptNumber,
                            bankNumber, branchName, paymentDate);
                }
            }
            final DateTime createdDateTime = JdbcSupport.getDateTime(rs, "createdDate");
            final LocalDateTime createdDate = createdDateTime == null ? null: createdDateTime.toLocalDateTime();
            final DateTime updatedDateTime = JdbcSupport.getDateTime(rs, "updatedDate");
            final LocalDateTime updatedDate = updatedDateTime == null ? null:JdbcSupport.getDateTime(rs, "updatedDate").toLocalDateTime();
            final LocalDate date = JdbcSupport.getLocalDate(rs, "date");
            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final BigDecimal totalAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "total");
            final BigDecimal principalPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principal");
            final BigDecimal interestPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interest");
            final BigDecimal feeChargesPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "fees");
            final BigDecimal penaltyChargesPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penalties");
            final BigDecimal overPaymentPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "overpayment");
            final BigDecimal unrecognizedIncomePortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "unrecognizedIncome");
            final BigDecimal outstandingLoanBalance = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "outstandingLoanBalance");
            final String externalId = rs.getString("externalId");
			final String createdByUserName = rs.getString("createdByUserName");
			final String updatedByUserName = rs.getString("updatedByUserName");
			final String createdByFirstName = rs.getString("createdByFirstName");
			final String createdByLastName = rs.getString("createdByLastName");
			final String updatedByFirstName = rs.getString("updatedByFirstName");
			final String updatedByLastName = rs.getString("updatedByLastName");
			final long createdById = rs.getLong("createdById");
			final long updatedById = rs.getLong("updatedById");


            AccountTransferData transfer = null;
            final Long fromTransferId = JdbcSupport.getLong(rs, "fromTransferId");
            final Long toTransferId = JdbcSupport.getLong(rs, "toTransferId");
            if (fromTransferId != null) {
                final LocalDate fromTransferDate = JdbcSupport.getLocalDate(rs, "fromTransferDate");
                final BigDecimal fromTransferAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "fromTransferAmount");
                final boolean fromTransferReversed = rs.getBoolean("fromTransferReversed");
                final String fromTransferDescription = rs.getString("fromTransferDescription");

                transfer = AccountTransferData.transferBasicDetails(fromTransferId, currencyData, fromTransferAmount, fromTransferDate,
                        fromTransferDescription, fromTransferReversed);
            } else if (toTransferId != null) {
                final LocalDate toTransferDate = JdbcSupport.getLocalDate(rs, "toTransferDate");
                final BigDecimal toTransferAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "toTransferAmount");
                final boolean toTransferReversed = rs.getBoolean("toTransferReversed");
                final String toTransferDescription = rs.getString("toTransferDescription");

                transfer = AccountTransferData.transferBasicDetails(toTransferId, currencyData, toTransferAmount, toTransferDate,
                        toTransferDescription, toTransferReversed);
            }
            final AppUserData createdBy = AppUserData.auditdetails(createdById, createdByUserName, createdByFirstName, createdByLastName);
			final AppUserData updatedBy = AppUserData.auditdetails(updatedById, updatedByUserName, updatedByFirstName, updatedByLastName);
            return new LoanTransactionData(id, officeId, officeName, transactionType, paymentDetailData, currencyData, date, totalAmount, principalPortion, interestPortion,
                    feeChargesPortion, penaltyChargesPortion, overPaymentPortion, unrecognizedIncomePortion, null, externalId, transfer,
                    null, outstandingLoanBalance, submittedOnDate, manuallyReversed, createdDate, updatedDate, createdBy, updatedBy, null);
        }

    }

    @Override
    public LoanAccountData retrieveLoanProductDetailsTemplate(final Long productId, final Long clientId, final Long groupId) {

        this.context.authenticatedUser();

        final LoanProductData loanProduct = this.loanProductReadPlatformService.retrieveLoanProduct(productId);
        final Collection<EnumOptionData> loanTermFrequencyTypeOptions = this.loanDropdownReadPlatformService
                .retrieveLoanTermFrequencyTypeOptions();
        final Collection<EnumOptionData> repaymentFrequencyTypeOptions = this.loanDropdownReadPlatformService
                .retrieveRepaymentFrequencyTypeOptions();
        final Collection<EnumOptionData> repaymentFrequencyNthDayTypeOptions = this.loanDropdownReadPlatformService
                .retrieveRepaymentFrequencyOptionsForNthDayOfMonth();
        final Collection<EnumOptionData> repaymentFrequencyDaysOfWeekTypeOptions = this.loanDropdownReadPlatformService
                .retrieveRepaymentFrequencyOptionsForDaysOfWeek();
        final Collection<EnumOptionData> interestRateFrequencyTypeOptions = this.loanDropdownReadPlatformService
                .retrieveInterestRateFrequencyTypeOptions();
        final Collection<EnumOptionData> amortizationTypeOptions = this.loanDropdownReadPlatformService
                .retrieveLoanAmortizationTypeOptions();
        Collection<EnumOptionData> interestTypeOptions = null;
        if (loanProduct.isLinkedToFloatingInterestRates()) {
            interestTypeOptions = Arrays.asList(interestType(InterestMethod.DECLINING_BALANCE));
        } else {
            interestTypeOptions = this.loanDropdownReadPlatformService.retrieveLoanInterestTypeOptions();
        }
        final Collection<EnumOptionData> interestCalculationPeriodTypeOptions = this.loanDropdownReadPlatformService
                .retrieveLoanInterestRateCalculatedInPeriodOptions();
        final Collection<FundData> fundOptions = this.fundReadPlatformService.retrieveAllFunds(FundApiConstants.activeParamName);
        final Collection<TransactionProcessingStrategyData> repaymentStrategyOptions = this.loanDropdownReadPlatformService
                .retreiveTransactionProcessingStrategies();
        final Collection<LoanPurposeData> loanPurposeOptions = this.loanPurposeGroupReadPlatformService.retrieveAllLoanPurposes(null, null,
                true);
        final Collection<CodeValueData> loanCollateralOptions = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode("LoanCollateral");
        Long loanOfficerId = null;
        if(clientId!= null){
        loanOfficerId = this.clientReadPlatformService.fetchDefaultLoanOfficerFromGroup(clientId);
        }
        final Collection<PledgeData> loanProductCollateralPledgesOptions = this.pledgeReadPlatformService.retrievePledgesByClientIdAndProductId(clientId, productId, null);
        Collection<ChargeData> chargeOptions = null;
        if (loanProduct.getMultiDisburseLoan()) {
            chargeOptions = this.chargeReadPlatformService.retrieveLoanProductApplicableCharges(productId,
                    new ChargeTimeType[] { ChargeTimeType.OVERDUE_INSTALLMENT });
        } else {
            chargeOptions = this.chargeReadPlatformService.retrieveLoanProductApplicableCharges(productId, new ChargeTimeType[] {
                    ChargeTimeType.OVERDUE_INSTALLMENT, ChargeTimeType.TRANCHE_DISBURSEMENT });
        }

        Integer loanCycleCounter = null;
        if (loanProduct.useBorrowerCycle()) {
            if (clientId == null) {
                loanCycleCounter = retriveLoanCounter(groupId, AccountType.GROUP.getValue(), loanProduct.getId());
            } else {
                loanCycleCounter = retriveLoanCounter(clientId, loanProduct.getId());
            }
        }

        Collection<LoanAccountSummaryData> clientActiveLoanOptions = null;
        if(loanProduct.canUseForTopup() && clientId != null){
            clientActiveLoanOptions = this.accountDetailsReadPlatformService.retrieveClientActiveLoanAccountSummary(clientId);
        }
        final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService
				.retrieveAllPaymentTypes();
        Collection<LoanEMIPackData> loanEMIPacks = this.loanEMIPacksReadPlatformService.retrieveEMIPackDetails(productId);
        if(loanEMIPacks == null || loanEMIPacks.size() == 0){
            loanEMIPacks = null;
        }
        final List<EnumOptionData> brokenPeriodMethodTypeOptions = this.loanDropdownReadPlatformService.retrieveBrokenPeriodMethodTypeOptions();
        return LoanAccountData.loanProductWithTemplateDefaults(loanProduct, loanTermFrequencyTypeOptions, repaymentFrequencyTypeOptions,
                repaymentFrequencyNthDayTypeOptions, repaymentFrequencyDaysOfWeekTypeOptions, repaymentStrategyOptions,
                interestRateFrequencyTypeOptions, amortizationTypeOptions, interestTypeOptions, interestCalculationPeriodTypeOptions,
                fundOptions, chargeOptions, loanPurposeOptions, loanCollateralOptions, loanCycleCounter, loanProductCollateralPledgesOptions,
                clientActiveLoanOptions, paymentOptions, loanOfficerId, loanEMIPacks,brokenPeriodMethodTypeOptions);
    }

    @Override
    public LoanAccountData retrieveClientDetailsTemplate(final Long clientId) {

        this.context.authenticatedUser();

        final ClientData clientAccount = this.clientReadPlatformService.retrieveOne(clientId);
        final LocalDate expectedDisbursementDate = DateUtils.getLocalDateOfTenant();
        final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        return LoanAccountData.clientDefaults(clientAccount.id(), clientAccount.accountNo(), clientAccount.displayName(),
                clientAccount.officeId(), expectedDisbursementDate, paymentOptions);
    }

    @Override
    public LoanAccountData retrieveGroupDetailsTemplate(final Long groupId) {
        this.context.authenticatedUser();
        final GroupGeneralData groupAccount = this.groupReadPlatformService.retrieveOne(groupId);
        List<ClientData> clientMember = (List<ClientData>) this.clientReadPlatformService.retrieveActiveClientMembersOfGroup(groupId);
        groupAccount.updateClientMembers(clientMember);
        final LocalDate expectedDisbursementDate = DateUtils.getLocalDateOfTenant();
        return LoanAccountData.groupDefaults(groupAccount, expectedDisbursementDate);
    }

    @Override
    public LoanAccountData retrieveGroupAndMembersDetailsTemplate(final Long groupId) {
        GroupGeneralData groupAccount = this.groupReadPlatformService.retrieveOne(groupId);
        final LocalDate expectedDisbursementDate = DateUtils.getLocalDateOfTenant();

        // get group associations
        final Collection<ClientData> membersOfGroup = this.clientReadPlatformService.retrieveActiveClientMembersOfGroup(groupId);
        if (!CollectionUtils.isEmpty(membersOfGroup)) {
            final Collection<ClientData> activeClientMembers = null;
            final Collection<CalendarData> calendarsData = null;
            final CalendarData collectionMeetingCalendar = null;
            final Collection<GroupRoleData> groupRoles = null;
            groupAccount = GroupGeneralData.withAssocations(groupAccount, membersOfGroup, activeClientMembers, groupRoles, calendarsData,
                    collectionMeetingCalendar);
        }

        return LoanAccountData.groupDefaults(groupAccount, expectedDisbursementDate);
    }

    @Override
    public Collection<CalendarData> retrieveCalendars(final Long groupId) {
        Collection<CalendarData> calendarsData = new ArrayList<>();
        calendarsData.addAll(this.calendarReadPlatformService.retrieveParentCalendarsByEntity(groupId,
                CalendarEntityType.GROUPS.getValue(), null));
        calendarsData
                .addAll(this.calendarReadPlatformService.retrieveCalendarsByEntity(groupId, CalendarEntityType.GROUPS.getValue(), null));
        calendarsData = this.calendarReadPlatformService.updateWithRecurringDates(calendarsData);
        return calendarsData;
    }

    @Override
    public Collection<StaffData> retrieveAllowedLoanOfficers(final Long selectedOfficeId, final boolean staffInSelectedOfficeOnly) {
        if (selectedOfficeId == null) { return null; }

        Collection<StaffData> allowedLoanOfficers = null;

        if (staffInSelectedOfficeOnly) {
            // only bring back loan officers in selected branch/office
            allowedLoanOfficers = this.staffReadPlatformService.retrieveAllLoanOfficersInOfficeById(selectedOfficeId);
        } else {
            // by default bring back all loan officers in selected
            // branch/office as well as loan officers in officer above
            // this office
            final boolean restrictToLoanOfficersOnly = true;
            allowedLoanOfficers = this.staffReadPlatformService.retrieveAllStaffInOfficeAndItsParentOfficeHierarchy(selectedOfficeId,
                    restrictToLoanOfficersOnly);
        }

        return allowedLoanOfficers;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Integer retriveLoanCounter(final Long groupId, final Integer loanType, Long productId) {
        final String sql = "Select MAX(l.loan_product_counter) from m_loan l where l.group_id = ?  and l.loan_type_enum = ? and l.product_id=?";
        return this.jdbcTemplate.queryForInt(sql, groupId, loanType, productId);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Integer retriveLoanCounter(final Long clientId, Long productId) {
        final String sql = "Select MAX(l.loan_product_counter) from m_loan l where l.client_id = ? and l.product_id=?";
        return this.jdbcTemplate.queryForInt(sql, clientId, productId);
    }
    
    @Override
    public Collection<Long> retrieveAllActiveSubmittedAprrovedGroupLoanIds(final Long groupId){
    	final String sql = "select loan.id from m_loan loan where loan.loan_status_id in(?, ?, ?) AND loan.group_id=?";
    	 return this.jdbcTemplate.queryForList(sql, Long.class, new Object[] { LoanStatus.ACTIVE.getValue(),
    			 LoanStatus.APPROVED.getValue(), LoanStatus.SUBMITTED_AND_PENDING_APPROVAL.getValue(), groupId });
    }
    
    @Override
    public Collection<DisbursementData> retrieveLoanDisbursementDetails(final Long loanId) {
        final LoanDisbursementDetailMapper rm = new LoanDisbursementDetailMapper();
        final String sql = "select " + rm.schema() + " where dd.loan_id=? and dd.is_active = 1 group by dd.id order by dd.expected_disburse_date";
        return this.jdbcTemplate.query(sql, rm, new Object[] { loanId });
    }

    private static final class LoanDisbursementDetailMapper implements RowMapper<DisbursementData> {

        public String schema() {
            return "dd.id as id,dd.expected_disburse_date as expectedDisbursementdate, dd.disbursedon_date as actualDisbursementdate,dd.principal as principal,sum(lc.amount) chargeAmount, "
                    + "lc.amount_waived_derived waivedAmount,group_concat(lc.id) loanChargeId, dd.discount_on_disbursal_amount as discountOnDisbursalAmount "
                    + "from m_loan l inner join m_loan_disbursement_detail dd on dd.loan_id = l.id left join m_loan_tranche_disbursement_charge tdc on tdc.disbursement_detail_id=dd.id "
                    + "left join m_loan_charge lc on  lc.id=tdc.loan_charge_id and lc.is_active=1";
        }

        @Override
        public DisbursementData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final LocalDate expectedDisbursementdate = JdbcSupport.getLocalDate(rs, "expectedDisbursementdate");
            final LocalDate actualDisbursementdate = JdbcSupport.getLocalDate(rs, "actualDisbursementdate");
            final BigDecimal principal = rs.getBigDecimal("principal");
            final BigDecimal discountOnDisbursalAmount = rs.getBigDecimal("discountOnDisbursalAmount");
            final String loanChargeId = rs.getString("loanChargeId");
            BigDecimal chargeAmount = rs.getBigDecimal("chargeAmount");
            final BigDecimal waivedAmount = rs.getBigDecimal("waivedAmount");
            if (chargeAmount != null && waivedAmount != null) chargeAmount = chargeAmount.subtract(waivedAmount);
            final DisbursementData disbursementData = new DisbursementData(id, expectedDisbursementdate, actualDisbursementdate, principal,
                    loanChargeId, chargeAmount, discountOnDisbursalAmount);
            return disbursementData;
        }

    }

    @Override
    public DisbursementData retrieveLoanDisbursementDetail(Long loanId, Long disbursementId) {
        final LoanDisbursementDetailMapper rm = new LoanDisbursementDetailMapper();
        final String sql = "select " + rm.schema() + " where dd.loan_id=? and dd.id=?";
        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { loanId, disbursementId });
    }
    
    @Override
    public LoanTransactionData refundTemplate(Long loanId) {
        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.REFUND);
        final LocalDate refundDate = DateUtils.getLocalDateOfTenant();
        final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        final String sql = "select total_overpaid_derived from m_loan ml where ml.id=?";
        final BigDecimal overPaidAmount = this.jdbcTemplate.queryForObject(sql, BigDecimal.class, loanId);
        return LoanTransactionData.LoanTransactionRefundData(transactionType, refundDate, overPaidAmount, paymentOptions);
    }

    @Override
    public Collection<LoanTermVariationsData> retrieveLoanTermVariations(Long loanId, Integer termType) {
        final LoanTermVariationsMapper rm = new LoanTermVariationsMapper();
        final String sql = "select " + rm.schema() + " where tv.loan_id=? and tv.term_type=?";
        return this.jdbcTemplate.query(sql, rm, new Object[] { loanId, termType });
    }

    private static final class LoanTermVariationsMapper implements RowMapper<LoanTermVariationsData> {

        public String schema() {
            return "tv.id as id,tv.applicable_date as variationApplicableFrom,tv.decimal_value as decimalValue, tv.date_value as dateValue, tv.is_specific_to_installment as isSpecificToInstallment "
                    + "from m_loan_term_variations tv";
        }

        @Override
        public LoanTermVariationsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final LocalDate variationApplicableFrom = JdbcSupport.getLocalDate(rs, "variationApplicableFrom");
            final BigDecimal decimalValue = rs.getBigDecimal("decimalValue");
            final LocalDate dateValue = JdbcSupport.getLocalDate(rs, "dateValue");
            final boolean isSpecificToInstallment = rs.getBoolean("isSpecificToInstallment");

            final LoanTermVariationsData loanTermVariationsData = new LoanTermVariationsData(id,
                    LoanEnumerations.loanvariationType(LoanTermVariationType.EMI_AMOUNT), variationApplicableFrom, decimalValue, dateValue,
                    isSpecificToInstallment);
            return loanTermVariationsData;
        }

    }

    @Override
    public Collection<LoanScheduleAccrualData> retriveScheduleAccrualData() {

        LoanScheduleAccrualMapper mapper = new LoanScheduleAccrualMapper();
        Date organisationStartDate = this.configurationDomainService.retrieveOrganisationStartDate();
        final StringBuilder sqlBuilder = new StringBuilder(400);
        sqlBuilder
                .append("select ")
                .append(mapper.schema())
                .append(" where (recaldet.is_compounding_to_be_posted_as_transaction is null or recaldet.is_compounding_to_be_posted_as_transaction = 0) ")
                .append(" and (((ls.fee_charges_amount <> if(ls.accrual_fee_charges_derived is null,0, ls.accrual_fee_charges_derived))")
                .append(" or ( ls.penalty_charges_amount <> if(ls.accrual_penalty_charges_derived is null,0,ls.accrual_penalty_charges_derived))")
                .append(" or ( ls.interest_amount <> if(ls.accrual_interest_derived is null,0,ls.accrual_interest_derived)))")
                .append(" and loan.loan_status_id=:active and mpl.accounting_type=:type and ls.duedate <= :currentdate ")
                 .append(" and (loan.loan_recalcualated_on is null or ls.duedate >=  loan.loan_recalcualated_on))");
        if(organisationStartDate != null){
            sqlBuilder.append(" and ls.duedate > :organisationstartdate ");
        }
            sqlBuilder.append(" order by loan.id,ls.duedate ");
        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("currentdate",formatter.print(DateUtils.getLocalDateOfTenant()));
        paramMap.put("active", LoanStatus.ACTIVE.getValue());
        paramMap.put("type", AccountingRuleType.ACCRUAL_PERIODIC.getValue());
        paramMap.put("organisationstartdate", formatter.print(new LocalDate(organisationStartDate)));
        
        return this.namedParameterJdbcTemplate.query(sqlBuilder.toString(), paramMap, mapper);       
    }

    @Override
    public Collection<LoanScheduleAccrualData> retrivePeriodicAccrualData(final LocalDate tillDate, List<Long> loanList) {

        LoanSchedulePeriodicAccrualMapper mapper = new LoanSchedulePeriodicAccrualMapper();
        Date organisationStartDate = this.configurationDomainService.retrieveOrganisationStartDate();
        final StringBuilder sqlBuilder = new StringBuilder(400);
        sqlBuilder
                .append("select ")
                .append(mapper.schema())
                .append(" where  (recaldet.is_compounding_to_be_posted_as_transaction is null or recaldet.is_compounding_to_be_posted_as_transaction = 0) ")
                .append(" and (((ls.fee_charges_amount <> if(ls.accrual_fee_charges_derived is null,0, ls.accrual_fee_charges_derived))")
                .append(" or (ls.penalty_charges_amount <> if(ls.accrual_penalty_charges_derived is null,0,ls.accrual_penalty_charges_derived))")
                .append(" or (ls.interest_amount <> if(ls.accrual_interest_derived is null,0,ls.accrual_interest_derived)))")
                .append(" and loan.loan_status_id=:active and mpl.accounting_type=:type and (loan.closedon_date <= :tilldate or loan.closedon_date is null)")
                .append(" and (ls.duedate <= :tilldate or (ls.duedate > :tilldate and ls.fromdate < :tilldate)) ")
                .append(" and (loan.loan_recalcualated_on is null or ls.duedate >=  loan.loan_recalcualated_on))");
        if(organisationStartDate != null){
            sqlBuilder.append(" and ls.duedate > :organisationstartdate ");
        }
        
        if(loanList != null && !loanList.isEmpty()){
        	sqlBuilder.append(" and loan.id in (:loanid) ");       
        }
            sqlBuilder.append(" order by loan.id,ls.duedate ");
        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("active", LoanStatus.ACTIVE.getValue());
        paramMap.put("type", AccountingRuleType.ACCRUAL_PERIODIC.getValue());
        paramMap.put("tilldate", formatter.print(tillDate));
        paramMap.put("organisationstartdate", formatter.print(new LocalDate(organisationStartDate)));
        if(loanList != null && !loanList.isEmpty()){
        	paramMap.put("loanid", loanList);
        }
        logger.info(sqlBuilder.toString());
        

        return this.namedParameterJdbcTemplate.query(sqlBuilder.toString(), paramMap, mapper);
    }

    private static final class LoanSchedulePeriodicAccrualMapper implements RowMapper<LoanScheduleAccrualData> {

        public String schema() {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder
                    .append("loan.id as loanId ,if(loan.client_id is null,mg.office_id,mc.office_id) as officeId,")
                    .append("loan.accrued_till as accruedTill, loan.repayment_period_frequency_enum as frequencyEnum, ")
                    .append("loan.interest_calculated_from_date as interestCalculatedFrom, ")
                    .append("loan.repay_every as repayEvery, loan.is_npa as npa,")
                    .append("ls.installment as installmentNumber, ")
                    .append("ls.duedate as duedate,ls.fromdate as fromdate ,ls.id as scheduleId,loan.product_id as productId,")
                    .append("ls.interest_amount as interest, ls.interest_waived_derived as interestWaived,")
                    .append("ls.penalty_charges_amount as penalty, ")
                    .append("ls.fee_charges_amount as charges, ")
                    .append("ls.accrual_interest_derived as accinterest,ls.accrual_fee_charges_derived as accfeecharege,ls.accrual_penalty_charges_derived as accpenalty,")
                    .append(" loan.currency_code as currencyCode,loan.currency_digits as currencyDigits,loan.currency_multiplesof as inMultiplesOf,")
                    .append("curr.display_symbol as currencyDisplaySymbol,curr.name as currencyName,curr.internationalized_name_code as currencyNameCode")
                    .append(" from m_loan_repayment_schedule ls ").append(" left join m_loan loan on loan.id=ls.loan_id ")
                    .append(" left join m_product_loan mpl on mpl.id = loan.product_id")
                    .append(" left join m_client mc on mc.id = loan.client_id ").append(" left join m_group mg on mg.id = loan.group_id")
                    .append(" left join m_currency curr on curr.code = loan.currency_code")
                    .append(" left join m_loan_recalculation_details as recaldet on loan.id = recaldet.loan_id ");
            return sqlBuilder.toString();
        }

        @Override
        public LoanScheduleAccrualData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {

            final Long loanId = rs.getLong("loanId");
            final Long officeId = rs.getLong("officeId");
            final LocalDate accruedTill = JdbcSupport.getLocalDate(rs, "accruedTill");
            final LocalDate interestCalculatedFrom = JdbcSupport.getLocalDate(rs, "interestCalculatedFrom");
            final Integer installmentNumber = JdbcSupport.getInteger(rs, "installmentNumber");

            final Integer frequencyEnum = JdbcSupport.getInteger(rs, "frequencyEnum");
            final Integer repayEvery = JdbcSupport.getInteger(rs, "repayEvery");
            final PeriodFrequencyType frequency = PeriodFrequencyType.fromInt(frequencyEnum);
            final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "duedate");
            final LocalDate fromDate = JdbcSupport.getLocalDate(rs, "fromdate");
            final Long repaymentScheduleId = rs.getLong("scheduleId");
            final Long loanProductId = rs.getLong("productId");
            final BigDecimal interestIncome = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "interest");
            final BigDecimal feeIncome = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "charges");
            final BigDecimal penaltyIncome = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "penalty");
            final BigDecimal interestIncomeWaived = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "interestWaived");
            final BigDecimal accruedInterestIncome = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "accinterest");
            final BigDecimal accruedFeeIncome = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "accfeecharege");
            final BigDecimal accruedPenaltyIncome = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "accpenalty");
            final boolean isNpa = rs.getBoolean("npa");

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currencyData = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);

            return new LoanScheduleAccrualData(loanId, officeId, installmentNumber, accruedTill, frequency, repayEvery, dueDate, fromDate,
                    repaymentScheduleId, loanProductId, interestIncome, feeIncome, penaltyIncome, accruedInterestIncome, accruedFeeIncome,
                    accruedPenaltyIncome, currencyData, interestCalculatedFrom, interestIncomeWaived, isNpa);
        }

    }

    private static final class LoanScheduleAccrualMapper implements RowMapper<LoanScheduleAccrualData> {

        public String schema() {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder
                    .append("loan.id as loanId , loan.is_npa as npa, if(loan.client_id is null,mg.office_id,mc.office_id) as officeId,")
                    .append("ls.duedate as duedate,ls.fromdate as fromdate,ls.id as scheduleId,loan.product_id as productId,")
                    .append("ls.installment as installmentNumber, ")
                    .append("ls.interest_amount as interest, ls.interest_waived_derived as interestWaived,")
                    .append("ls.penalty_charges_amount as penalty, ")
                    .append("ls.fee_charges_amount as charges, ")
                    .append("ls.accrual_interest_derived as accinterest,ls.accrual_fee_charges_derived as accfeecharege,ls.accrual_penalty_charges_derived as accpenalty,")
                    .append(" loan.currency_code as currencyCode,loan.currency_digits as currencyDigits,loan.currency_multiplesof as inMultiplesOf,")
                    .append("curr.display_symbol as currencyDisplaySymbol,curr.name as currencyName,curr.internationalized_name_code as currencyNameCode")
                    .append(" from m_loan_repayment_schedule ls ").append(" left join m_loan loan on loan.id=ls.loan_id ")
                    .append(" left join m_product_loan mpl on mpl.id = loan.product_id")
                    .append(" left join m_client mc on mc.id = loan.client_id ").append(" left join m_group mg on mg.id = loan.group_id")
                    .append(" left join m_currency curr on curr.code = loan.currency_code")
                    .append(" left join m_loan_recalculation_details as recaldet on loan.id = recaldet.loan_id ");
            return sqlBuilder.toString();
        }

        @Override
        public LoanScheduleAccrualData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {

            final Long loanId = rs.getLong("loanId");
            final Long officeId = rs.getLong("officeId");
            final Integer installmentNumber = JdbcSupport.getInteger(rs, "installmentNumber");
            final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "duedate");
            final LocalDate fromdate = JdbcSupport.getLocalDate(rs, "fromdate");
            final Long repaymentScheduleId = rs.getLong("scheduleId");
            final Long loanProductId = rs.getLong("productId");
            final BigDecimal interestIncome = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "interest");
            final BigDecimal feeIncome = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "charges");
            final BigDecimal penaltyIncome = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "penalty");
            final BigDecimal interestIncomeWaived = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "interestWaived");
            final BigDecimal accruedInterestIncome = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "accinterest");
            final BigDecimal accruedFeeIncome = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "accfeecharege");
            final BigDecimal accruedPenaltyIncome = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "accpenalty");
            final boolean isNpa = rs.getBoolean("npa");

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currencyData = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);
            final LocalDate accruedTill = null;
            final PeriodFrequencyType frequency = null;
            final Integer repayEvery = null;
            final LocalDate interestCalculatedFrom = null;
            return new LoanScheduleAccrualData(loanId, officeId, installmentNumber, accruedTill, frequency, repayEvery, dueDate, fromdate,
                    repaymentScheduleId, loanProductId, interestIncome, feeIncome, penaltyIncome, accruedInterestIncome, accruedFeeIncome,
                    accruedPenaltyIncome, currencyData, interestCalculatedFrom, interestIncomeWaived, isNpa);
        }
    }

    @Override
    public LoanTransactionData retrieveRecoveryPaymentTemplate(Long loanId) {
        try {
            String sql = "select ml.total_writtenoff_derived from m_loan ml where ml.id = ?";
            final BigDecimal writtenoffamount = this.jdbcTemplate.queryForObject(sql, BigDecimal.class, loanId);
            final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.RECOVERY_REPAYMENT);
            final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
            BigDecimal outstandingLoanBalance = null;
            final BigDecimal unrecognizedIncomePortion = null;
            return new LoanTransactionData(null, null, null, transactionType, null, null, null, writtenoffamount, null, null, null, null,
                    null, unrecognizedIncomePortion, paymentOptions, null, null, null, outstandingLoanBalance, false);
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(loanId);
        }
    }

    @Override
    public LoanTransactionData retrieveLoanWriteoffTemplate(final Long loanId) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ml.total_outstanding_derived- IFNULL(SUM(CASE lt.transaction_type_enum WHEN :addsubsidy THEN lt.amount WHEN :revokesubsidy THEN lt.amount * -1 END),0) AS outstanding, ");
        sb.append("ml.currency_code AS currencyCode, ml.currency_digits AS currencyDigits, ml.currency_multiplesof AS inMultiplesOf, ");
        sb.append("rc.`name` AS currencyName,rc.display_symbol AS currencyDisplaySymbol, rc.internationalized_name_code AS currencyNameCode");
        sb.append(" FROM m_loan ml");
        sb.append(" LEFT JOIN m_loan_transaction lt ON lt.loan_id = ml.id AND lt.is_reversed = 0 AND (lt.transaction_type_enum = :addsubsidy OR lt.transaction_type_enum = :revokesubsidy)");
        sb.append(" JOIN m_currency rc ON rc.`code` = ml.currency_code");
        sb.append(" WHERE ml.id = :loanId");
        sb.append(" GROUP BY ml.id");

        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("loanId", loanId);
        paramMap.put("addsubsidy", LoanTransactionType.ADD_SUBSIDY.getValue());
        paramMap.put("revokesubsidy", LoanTransactionType.REVOKE_SUBSIDY.getValue());
        try {
            Map<String, Object> data = this.namedParameterJdbcTemplate.queryForMap(sb.toString(), paramMap);

            BigDecimal principal = (BigDecimal) data.get("outstanding");
            final CurrencyData currencyData = fetchCurrencyData(data);
            final BigDecimal outstandingLoanBalance = null;
            final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.WRITEOFF);
            final BigDecimal unrecognizedIncomePortion = null;
            final List<CodeValueData> writeOffReasonOptions = new ArrayList<>(
                    this.codeValueReadPlatformService.retrieveCodeValuesByCode(LoanApiConstants.WRITEOFFREASONS));

            LoanTransactionData loanTransactionData = new LoanTransactionData(null, null, null, transactionType, null, currencyData,
                    DateUtils.getLocalDateOfTenant(), principal, null, null, null, null, null, null, null, null, outstandingLoanBalance,
                    unrecognizedIncomePortion, false);
            loanTransactionData.setWriteOffReasonOptions(writeOffReasonOptions);
            return loanTransactionData;
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(loanId);
        }
    }

    @Override
    public List<Long> fetchLoansForInterestRecalculation() {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder sql = new StringBuilder();
        List<Long> loanIds = null;
        sql.append("SELECT loan_id from reprocess_loans ");
        loanIds = this.jdbcTemplate.queryForList(sql.toString(), Long.class);
        if (!loanIds.isEmpty()) {
            this.jdbcTemplate.update("truncate reprocess_loans");
        }
        sqlBuilder.append("SELECT ml.id FROM m_loan ml ");
        sqlBuilder.append(" INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id ");
        sqlBuilder.append(" LEFT JOIN m_loan_disbursement_detail dd on dd.loan_id=ml.id and dd.disbursedon_date is null ");
        // For Floating rate changes
        sqlBuilder
                .append(" left join m_product_loan_floating_rates pfr on ml.product_id = pfr.loan_product_id and ml.is_floating_interest_rate = 1");
        sqlBuilder.append(" left join m_floating_rates fr on  pfr.floating_rates_id = fr.id");
        sqlBuilder.append(" left join m_floating_rates_periods frp on fr.id = frp.floating_rates_id ");
        sqlBuilder.append(" left join m_loan_reschedule_request lrr on lrr.loan_id = ml.id");
        // this is to identify the applicable rates when base rate is changed
        sqlBuilder.append(" left join  m_floating_rates bfr on  bfr.is_base_lending_rate = 1");
        sqlBuilder.append(" left join  m_floating_rates_periods bfrp on  bfr.id = bfrp.floating_rates_id and bfrp.created_date >= ?");
        sqlBuilder.append(" WHERE ml.loan_status_id = ? ");
        sqlBuilder.append(" and ((");
        sqlBuilder.append("ml.interest_recalculation_enabled = 1 ");
        sqlBuilder.append(" and (ml.interest_recalcualated_on is null or ml.interest_recalcualated_on <> ?)");
        sqlBuilder.append(" and ((");
        sqlBuilder.append(" mr.completed_derived is false ");
        sqlBuilder.append(" and mr.duedate < ? )");
        sqlBuilder.append(" or dd.expected_disburse_date < ? )) ");
        sqlBuilder.append(" or (");
        sqlBuilder.append(" fr.is_active = 1 and  frp.is_active = 1");
        sqlBuilder.append(" and (frp.created_date >= ?  or ");
        sqlBuilder.append("(bfrp.id is not null and frp.is_differential_to_base_lending_rate = 1 and frp.from_date >= bfrp.from_date)) ");
        sqlBuilder.append("and lrr.loan_id is null");
        sqlBuilder.append(" ))");
        sqlBuilder.append(" group by ml.id");
        if (loanIds != null && loanIds.size() > 0) {
            ThreadLocalContextUtil.setIgnoreOverdue(true);
            return loanIds;
    	} 
        try {
            String currentdate = formatter.print(DateUtils.getLocalDateOfTenant());
            // will look only for yesterday modified rates
            String yesterday = formatter.print(DateUtils.getLocalDateOfTenant().minusDays(1));
            return this.jdbcTemplate.queryForList(sqlBuilder.toString(), Long.class, new Object[] { yesterday,
                    LoanStatus.ACTIVE.getValue(), currentdate, currentdate, currentdate, yesterday });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Collection<LoanTransactionData> retrieveWaiverLoanTransactions(final Long loanId) {
        try {

            final LoanTransactionDerivedComponentMapper rm = new LoanTransactionDerivedComponentMapper();

            final String sql = "select " + rm.schema()
                    + " where tr.loan_id = ? and tr.transaction_type_enum = ? and tr.is_reversed=0 order by tr.transaction_date ASC,id ";
            return this.jdbcTemplate.query(sql, rm, new Object[] { loanId, LoanTransactionType.WAIVE_INTEREST.getValue() });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean isGuaranteeRequired(final Long loanId) {
        final String sql = "select pl.hold_guarantee_funds from m_loan ml inner join m_product_loan pl on pl.id = ml.product_id where ml.id=?";
        return this.jdbcTemplate.queryForObject(sql, Boolean.class, loanId);
    }
    
    @Override
    public boolean isAnyActiveJLGLoanForClient(final Long clientid, final Long groupId) {
        final String sql = "SELECT COUNT(loan.id) from m_loan loan where loan.client_id = ? and loan.group_id= ? "
                + "and (loan.loan_status_id = ?)";
        Integer activeLoanCount = this.jdbcTemplate.queryForInt(sql, clientid, groupId, LoanStatus.ACTIVE.getValue());
        if (activeLoanCount > 0) { return true; }
        return false;
    }
     

    private static final class LoanTransactionDerivedComponentMapper implements RowMapper<LoanTransactionData> {

        public String schema() {

            return " tr.id as id, tr.transaction_type_enum as transactionType, tr.transaction_date as `date`, tr.amount as total, "
                    + " tr.principal_portion_derived as principal, tr.interest_portion_derived as interest, "
                    + " tr.fee_charges_portion_derived as fees, tr.penalty_charges_portion_derived as penalties, "
                    + " tr.overpayment_portion_derived as overpayment, tr.outstanding_loan_balance_derived as outstandingLoanBalance, "
                    + " tr.unrecognized_income_portion as unrecognizedIncome " + " from m_loan_transaction tr ";
        }

        @Override
        public LoanTransactionData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
            final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(transactionTypeInt);

            final LocalDate date = JdbcSupport.getLocalDate(rs, "date");
            final BigDecimal totalAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "total");
            final BigDecimal principalPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principal");
            final BigDecimal interestPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interest");
            final BigDecimal feeChargesPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "fees");
            final BigDecimal penaltyChargesPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penalties");
            final BigDecimal overPaymentPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "overpayment");
            final BigDecimal unrecognizedIncomePortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "unrecognizedIncome");
            final BigDecimal outstandingLoanBalance = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "outstandingLoanBalance");

            return new LoanTransactionData(id, transactionType, date, totalAmount, principalPortion, interestPortion, feeChargesPortion,
                    penaltyChargesPortion, overPaymentPortion, unrecognizedIncomePortion, outstandingLoanBalance, false);
        }
    }

    @Override
    public Collection<LoanSchedulePeriodData> fetchWaiverInterestRepaymentData(final Long loanId) {
        try {

            final LoanRepaymentWaiverMapper rm = new LoanRepaymentWaiverMapper();

            final String sql = "select " + rm.getSchema()
                    + " where lrs.loan_id = ? and lrs.interest_waived_derived is not null order by lrs.installment ASC ";
            return this.jdbcTemplate.query(sql, rm, new Object[] { loanId });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }

    }

    private static final class LoanRepaymentWaiverMapper implements RowMapper<LoanSchedulePeriodData> {

        private final String sqlSchema;

        public String getSchema() {
            return this.sqlSchema;
        }

        public LoanRepaymentWaiverMapper() {
            StringBuilder sb = new StringBuilder();
            sb.append("lrs.duedate as dueDate,lrs.interest_waived_derived interestWaived, lrs.installment as installment");
            sb.append(" from m_loan_repayment_schedule lrs ");
            sqlSchema = sb.toString();
        }

        @Override
        public LoanSchedulePeriodData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {

            final Integer period = JdbcSupport.getInteger(rs, "installment");
            final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");
            final BigDecimal interestWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWaived");

            final LocalDate fromDate = null;
            final LocalDate obligationsMetOnDate = null;
            final Boolean complete = false;
            final BigDecimal principalOriginalDue = null;
            final BigDecimal principalPaid = null;
            final BigDecimal principalWrittenOff = null;
            final BigDecimal principalOutstanding = null;
            final BigDecimal interestPaid = null;
            final BigDecimal interestWrittenOff = null;
            final BigDecimal interestOutstanding = null;
            final BigDecimal feeChargesDue = null;
            final BigDecimal feeChargesPaid = null;
            final BigDecimal feeChargesWaived = null;
            final BigDecimal feeChargesWrittenOff = null;
            final BigDecimal feeChargesOutstanding = null;
            final BigDecimal penaltyChargesDue = null;
            final BigDecimal penaltyChargesPaid = null;
            final BigDecimal penaltyChargesWaived = null;
            final BigDecimal penaltyChargesWrittenOff = null;
            final BigDecimal penaltyChargesOutstanding = null;

            final BigDecimal totalDueForPeriod = null;
            final BigDecimal totalPaidInAdvanceForPeriod = null;
            final BigDecimal totalPaidLateForPeriod = null;
            final BigDecimal totalActualCostOfLoanForPeriod = null;
            final BigDecimal outstandingPrincipalBalanceOfLoan = null;
            final BigDecimal interestDueOnPrincipalOutstanding = null;
            Long loanId = null;
            final BigDecimal totalWaived = null;
            final BigDecimal totalWrittenOff = null;
            final BigDecimal totalOutstanding = null;
            final BigDecimal totalPaid = null;
            final BigDecimal totalInstallmentAmount = null;
            final BigDecimal advancePaymentAmount = null;

            return LoanSchedulePeriodData.repaymentPeriodWithPayments(loanId, period, fromDate, dueDate, obligationsMetOnDate, complete,
                    principalOriginalDue, principalPaid, principalWrittenOff, principalOutstanding, outstandingPrincipalBalanceOfLoan,
                    interestDueOnPrincipalOutstanding, interestPaid, interestWaived, interestWrittenOff, interestOutstanding,
                    feeChargesDue, feeChargesPaid, feeChargesWaived, feeChargesWrittenOff, feeChargesOutstanding, penaltyChargesDue,
                    penaltyChargesPaid, penaltyChargesWaived, penaltyChargesWrittenOff, penaltyChargesOutstanding, totalDueForPeriod,
                    totalPaid, totalPaidInAdvanceForPeriod, totalPaidLateForPeriod, totalWaived, totalWrittenOff, totalOutstanding,
                    totalActualCostOfLoanForPeriod, totalInstallmentAmount, advancePaymentAmount);
        }
    }

    @Override
    public Date retrieveMinimumDateOfRepaymentTransaction(Long loanId) {
        // TODO Auto-generated method stub
        Date date = this.jdbcTemplate.queryForObject(
                "select min(transaction_date) from m_loan_transaction where loan_id=? and transaction_type_enum=2",
                new Object[] { loanId }, Date.class);

        return date;
    }

    @Override
    public PaidInAdvanceData retrieveTotalPaidInAdvance(Long loanId) {
        // TODO Auto-generated method stub
    	
        try {
            String currentdate = formatter.print(DateUtils.getLocalDateOfTenant());
            final String sql = "  select (SUM(ifnull(mr.principal_completed_derived, 0)) +"
                    + " + SUM(ifnull(mr.interest_completed_derived, 0)) " + " + SUM(ifnull(mr.fee_charges_completed_derived, 0)) "
                    + " + SUM(ifnull(mr.penalty_charges_completed_derived, 0))) as total_in_advance_derived "
                    + " from m_loan ml INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id "
                    + " where ml.id=? and  mr.duedate >= ? group by ml.id having "
                    + " (SUM(ifnull(mr.principal_completed_derived, 0))  " + " + SUM(ifnull(mr.interest_completed_derived, 0)) "
                    + " + SUM(ifnull(mr.fee_charges_completed_derived, 0)) "
                    + "+  SUM(ifnull(mr.penalty_charges_completed_derived, 0))) > 0";
            BigDecimal bigDecimal = this.jdbcTemplate.queryForObject(sql, BigDecimal.class, loanId,currentdate);
            return new PaidInAdvanceData(bigDecimal);
        } catch (DataAccessException e) {
            // TODO Auto-generated catch block
            return new PaidInAdvanceData(new BigDecimal(0));
        }
    }

    @Override
    public LoanTransactionData retrieveRefundByCashTemplate(Long loanId) {
        // TODO Auto-generated method stub
        this.context.authenticatedUser();

        CurrencyData currencyData = null;
        try {
            String sql = "SELECT ml.currency_code AS currencyCode, ml.currency_digits AS currencyDigits, ml.currency_multiplesof AS inMultiplesOf, "
                    + "rc.`name` AS currencyName,rc.display_symbol AS currencyDisplaySymbol, rc.internationalized_name_code AS currencyNameCode "
                    + "FROM m_loan ml JOIN m_currency rc ON rc.`code` = ml.currency_code WHERE ml.id = ?";
            Map<String, Object> data = this.jdbcTemplate.queryForMap(sql, loanId);
            currencyData = fetchCurrencyData(data);
        }catch(EmptyResultDataAccessException e){
            throw new LoanNotFoundException(loanId);
        }
        final LocalDate earliestUnpaidInstallmentDate = DateUtils.getLocalDateOfTenant();

        final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.REFUND_FOR_ACTIVE_LOAN);
        final Collection<PaymentTypeData> paymentOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        return new LoanTransactionData(null, null, null, transactionType, null, currencyData, earliestUnpaidInstallmentDate,
                retrieveTotalPaidInAdvance(loanId).getPaidInAdvance(), null, null, null, null, null, null, paymentOptions, null,
                null, null, null, false);
    }

    @Override
    public Collection<InterestRatePeriodData> retrieveLoanInterestRatePeriodData(LoanAccountData loanData) {
        this.context.authenticatedUser();

        if (loanData.isLoanProductLinkedToFloatingRate()) {
            final Collection<InterestRatePeriodData> intRatePeriodData = new ArrayList<>();
            final Collection<InterestRatePeriodData> intRates = this.floatingRatesReadPlatformService.retrieveInterestRatePeriods(loanData
                    .loanProductId());
            for (final InterestRatePeriodData rate : intRates) {
                if (rate.getFromDate().compareTo(loanData.getDisbursementDate().toDate()) > 0 && loanData.isFloatingInterestRate()) {
                    updateInterestRatePeriodData(rate, loanData);
                    intRatePeriodData.add(rate);
                } else if (rate.getFromDate().compareTo(loanData.getDisbursementDate().toDate()) <= 0) {
                    updateInterestRatePeriodData(rate, loanData);
                    intRatePeriodData.add(rate);
                    break;
                }
            }

            return intRatePeriodData;
        }
        return null;
    }

    private void updateInterestRatePeriodData(InterestRatePeriodData rate, LoanAccountData loan) {
        LoanProductData loanProductData = loanProductReadPlatformService.retrieveLoanProductFloatingDetails(loan.loanProductId());
        rate.setLoanProductDifferentialInterestRate(loanProductData.getInterestRateDifferential());
        rate.setLoanDifferentialInterestRate(loan.getInterestRateDifferential());

        BigDecimal effectiveInterestRate = BigDecimal.ZERO;
        effectiveInterestRate = effectiveInterestRate.add(rate.getLoanDifferentialInterestRate());
        effectiveInterestRate = effectiveInterestRate.add(rate.getLoanProductDifferentialInterestRate());
        effectiveInterestRate = effectiveInterestRate.add(rate.getInterestRate());
        if (rate.getBlrInterestRate() != null && rate.isDifferentialToBLR()) {
            effectiveInterestRate = effectiveInterestRate.add(rate.getBlrInterestRate());
        }
        rate.setEffectiveInterestRate(effectiveInterestRate);

        if (rate.getFromDate().compareTo(loan.getDisbursementDate().toDate()) < 0) {
            rate.setFromDate(loan.getDisbursementDate().toDate());
        }
    }

    @Override
    public Collection<Long> retrieveLoanIdsWithPendingIncomePostingTransactions() {
        StringBuilder sqlBuilder = new StringBuilder()
            .append(" select distinct loan.id ")
            .append(" from m_loan as loan ")
            .append(" inner join m_loan_recalculation_details as recdet on (recdet.loan_id = loan.id and recdet.is_compounding_to_be_posted_as_transaction is not null and recdet.is_compounding_to_be_posted_as_transaction = 1) ")
            .append(" inner join m_loan_repayment_schedule as repsch on repsch.loan_id = loan.id ")
            .append(" inner join m_loan_interest_recalculation_additional_details as adddet on adddet.loan_repayment_schedule_id = repsch.id ")
            .append(" left join m_loan_transaction as trans on (trans.is_reversed <> 1 and trans.transaction_type_enum = 19 and trans.loan_id = loan.id and trans.transaction_date = adddet.effective_date) ")
            .append(" where loan.loan_status_id = 300 ")
            .append(" and loan.is_npa = 0 ")
            .append(" and adddet.effective_date is not null ")
            .append(" and trans.transaction_date is null ")
            .append(" and adddet.effective_date < ? ");
        try {
            String currentdate = formatter.print(DateUtils.getLocalDateOfTenant());
            return this.jdbcTemplate.queryForList(sqlBuilder.toString(), Long.class, new Object[] { currentdate });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    private static final class CurrencyMapper implements RowMapper<CurrencyData> {

        @Override
        public CurrencyData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            return new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf, currencyDisplaySymbol, currencyNameCode);
        }

    }
    
    private static final class RepaymentTransactionTemplateMapper implements RowMapper<LoanTransactionData> {

        private CurrencyMapper currencyMapper = new CurrencyMapper();

        public String schema() {
            StringBuilder sqlBuilder = new StringBuilder();

            sqlBuilder.append("if(max(tr.transaction_date)>ls.dueDate,max(tr.transaction_date),ls.dueDate) as transactionDate,");
            sqlBuilder.append("ls.dueDate as lastUnpaidInstallmentDate,");
            sqlBuilder
                    .append("ls.principal_amount - IFNULL(ls.principal_writtenoff_derived,0) - IFNULL(ls.principal_completed_derived,0) as principalDue,");
            sqlBuilder
                    .append("ls.interest_amount - IFNULL(ls.interest_completed_derived,0) - IFNULL(ls.interest_waived_derived,0) - IFNULL(ls.interest_writtenoff_derived,0) as interestDue,");
            sqlBuilder
                    .append("ls.fee_charges_amount - IFNULL(ls.fee_charges_completed_derived,0) - IFNULL(ls.fee_charges_writtenoff_derived,0) - IFNULL(ls.fee_charges_waived_derived,0) as feeDue,");
            sqlBuilder
                    .append("ls.penalty_charges_amount - IFNULL(ls.penalty_charges_completed_derived,0) - IFNULL(ls.penalty_charges_writtenoff_derived,0) - IFNULL(ls.penalty_charges_waived_derived,0) as penaltyDue,");
            sqlBuilder
                    .append(" l.currency_code as currencyCode, l.currency_digits as currencyDigits, l.currency_multiplesof as inMultiplesOf, rc.`name` as currencyName, ");
            sqlBuilder.append(" rc.display_symbol as currencyDisplaySymbol, rc.internationalized_name_code as currencyNameCode ");
            sqlBuilder.append(" FROM m_loan l");
            sqlBuilder.append(" LEFT JOIN m_loan_transaction tr ON tr.loan_id = l.id AND tr.transaction_type_enum = ? and tr.is_reversed = 0");
            sqlBuilder.append(" join m_currency rc on rc.`code` = l.currency_code ");
            sqlBuilder.append(" JOIN m_loan_repayment_schedule ls ON ls.loan_id = l.id AND ls.completed_derived = 0 ");
            sqlBuilder.append(" LEFT join( ");
            sqlBuilder.append(" (select min(ls.duedate) datedue,ls.loan_id from m_loan_repayment_schedule ls  ");
            sqlBuilder.append(" where ls.loan_id = ? and  ls.completed_derived = 0)");
            sqlBuilder.append(" )asq on asq.loan_id = ls.loan_id and asq.datedue = ls.duedate");
            return sqlBuilder.toString();

        }

        @Override
        public LoanTransactionData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(LoanTransactionType.REPAYMENT);
            final CurrencyData currencyData = this.currencyMapper.mapRow(rs, rowNum);
            final LocalDate date = (JdbcSupport.getLocalDate(rs, "transactionDate") == null) ? DateUtils.getLocalDateOfTenant() : JdbcSupport.getLocalDate(
                    rs, "transactionDate");
            final BigDecimal principalPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalDue");
            final BigDecimal interestDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestDue");
            final BigDecimal feeDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeDue");
            final BigDecimal penaltyDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyDue");
            final BigDecimal totalDue = principalPortion.add(interestDue).add(feeDue).add(penaltyDue);
            final BigDecimal outstandingLoanBalance = null;
            final BigDecimal unrecognizedIncomePortion = null;
            final BigDecimal overPaymentPortion = null;
            final Long id = null;
            final Long officeId = null;
            final String officeName = null;
            boolean manuallyReversed = false;
            final PaymentDetailData paymentDetailData = null;
            final String externalId = null;
            final AccountTransferData transfer = null;
            final BigDecimal fixedEmiAmount = null;
            LoanTransactionData transaction =  new LoanTransactionData(id, officeId, officeName, transactionType, paymentDetailData, currencyData, date, totalDue,
                    principalPortion, interestDue, feeDue, penaltyDue, overPaymentPortion, externalId, transfer, fixedEmiAmount,
                    outstandingLoanBalance, unrecognizedIncomePortion, manuallyReversed);
            final LocalDate lastOverdueDate = (JdbcSupport.getLocalDate(rs, "lastUnpaidInstallmentDate") == null) ? DateUtils.getLocalDateOfTenant() : JdbcSupport.getLocalDate(
                    rs, "lastUnpaidInstallmentDate");
            LoanOverdueData loanOverdueData = new LoanOverdueData(lastOverdueDate);
            transaction.setLoanOverdueData(loanOverdueData);
            return transaction;
        }

    }
    

    @SuppressWarnings("deprecation")
    @Override
    public Long retrieveLoanApplicationReferenceId(final LoanAccountData loanBasicDetails) {
        Long loanApplicationReferenceId = null;
        try {
            loanApplicationReferenceId = this.jdbcTemplate
                    .queryForLong("SELECT lar.id FROM f_loan_application_reference lar WHERE lar.loan_id = " + loanBasicDetails.getId());
        } catch (EmptyResultDataAccessException ex) {

        }
        return loanApplicationReferenceId;
    }
    
    @Override
    public Collection<Long> retrieveLoansByOfficesAndHoliday(final Long officeId, final List<Holiday> holidays, final Collection<Integer> status, LocalDate recalculateFrom) {
        final StringBuilder sql = new StringBuilder();

        sql.append("SELECT DISTINCT(ml.id) ");
        sql.append("FROM m_office mo ");
        sql.append("JOIN m_client mc ON mc.office_id = mo.id ");
        sql.append("JOIN m_loan ml ON  ml.group_id is null and ml.client_id = mc.id  AND ml.maturedon_date >= :date AND ml.loan_status_id in (:status) ");
        sql.append("JOIN m_loan_repayment_schedule rs on rs.loan_id = ml.id and (");
        
        generateConditionBasedOnHoliday(holidays, sql);
        sql.append( ") ");
        sql.append("WHERE mo.id = :officeId  ");

        sql.append(" union ");

        sql.append("SELECT DISTINCT(ml.id) ");
        sql.append("FROM m_office mo ");
        sql.append("JOIN m_group mg ON mg.office_id = mo.id ");
        sql.append("JOIN m_loan ml ON ml.group_id = mg.id AND ml.maturedon_date >= :date AND ml.loan_status_id in (:status) ");
        sql.append("JOIN m_loan_repayment_schedule rs on rs.loan_id = ml.id and (");
        generateConditionBasedOnHoliday(holidays, sql);
        sql.append( ") ");
        sql.append("WHERE mo.id = :officeId  ");
      
        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("date", formatter.print(recalculateFrom));
        paramMap.put("status", status);
        paramMap.put("officeId", officeId);
        return this.namedParameterJdbcTemplate.queryForList(sql.toString(), paramMap, Long.class);
    }

    @Override
    public Long retrieveLoanProductIdByLoanId(Long loanId) {
        try {
            final String sql = "Select product_id from m_loan where id = ?";

            return this.jdbcTemplate.queryForObject(sql, new Object[] { loanId }, Long.class);

        } catch (final EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(loanId);
        }
    }
    private void generateConditionBasedOnHoliday(final List<Holiday> holidays, final StringBuilder sql) {
        boolean isFirstTime = true;
        for (Holiday holiday : holidays) {
            if (!isFirstTime) {
                sql.append(" or ");
            }
            sql.append("rs.duedate BETWEEN '");
            sql.append(formatter.print(holiday.getFromDateLocalDate()));
            sql.append("' and '");
            sql.append(formatter.print(holiday.getToDateLocalDate()));
            sql.append("'");
            isFirstTime = false;
        }
    }

    private static class LoanBasicMapper implements RowMapper<LoanAccountData> {

        final String loanSql;

        LoanBasicMapper() {
            final StringBuilder sb = new StringBuilder(10000);
            sb.append("l.id as id, l.account_no as accountNo, l.external_id as externalId, l.fund_id as fundId, ");
            sb.append(" l.loan_type_enum as loanType, l.loan_purpose_id as loanPurposeId, l.weeks_in_year_enum as weeksInYearType, ");
            sb.append(" l.product_id as loanProductId, ");
            sb.append(" l.client_id as clientId, ");
            sb.append(" l.group_id as groupId, ");
            sb.append(" l.submittedon_date as submittedOnDate, l.rejectedon_date as rejectedOnDate, l.withdrawnon_date as withdrawnOnDate, ");
            sb.append(" l.approvedon_date as approvedOnDate, l.expected_disbursedon_date as expectedDisbursementDate, l.disbursedon_date as actualDisbursementDate, ");
            sb.append(" l.closedon_date as closedOnDate, l.writtenoffon_date as writtenOffOnDate, ");
            sb.append(" l.expected_firstrepaymenton_date as expectedFirstRepaymentOnDate, l.interest_calculated_from_date as interestChargedFromDate, l.expected_maturedon_date as expectedMaturityDate, ");
            sb.append(" l.principal_amount_proposed as proposedPrincipal, l.principal_amount as principal, l.approved_principal as approvedPrincipal, l.arrearstolerance_amount as inArrearsTolerance, l.number_of_repayments as numberOfRepayments, l.repay_every as repaymentEvery,");
            sb.append(" l.grace_on_principal_periods as graceOnPrincipalPayment, l.recurring_moratorium_principal_periods as recurringMoratoriumOnPrincipalPeriods, l.grace_on_interest_periods as graceOnInterestPayment, l.grace_interest_free_periods as graceOnInterestCharged,l.grace_on_arrears_ageing as graceOnArrearsAgeing,");
            sb.append(" l.nominal_interest_rate_per_period as interestRatePerPeriod, l.annual_nominal_interest_rate as annualInterestRate, ");
            sb.append(" l.repayment_period_frequency_enum as repaymentFrequencyType, l.interest_period_frequency_enum as interestRateFrequencyType, ");
            sb.append(" l.term_frequency as termFrequency, l.term_period_frequency_enum as termPeriodFrequencyType, ");
            sb.append(" l.amortization_method_enum as amortizationType, l.interest_method_enum as interestType, l.interest_calculated_in_period_enum as interestCalculationPeriodType,");
            sb.append(" l.allow_partial_period_interest_calcualtion as allowPartialPeriodInterestCalcualtion,");
            sb.append(" l.loan_status_id as lifeCycleStatusId, l.loan_transaction_strategy_id as transactionStrategyId, ");
            sb.append(" l.currency_code as currencyCode, l.currency_digits as currencyDigits, l.currency_multiplesof as inMultiplesOf, ");
            sb.append(" l.loan_officer_id as loanOfficerId, ");
            sb.append(" l.principal_disbursed_derived as principalDisbursed,");
            sb.append(" l.principal_repaid_derived as principalPaid,");
            sb.append(" l.principal_writtenoff_derived as principalWrittenOff,");
            sb.append(" l.principal_outstanding_derived as principalOutstanding,");
            sb.append(" l.principal_net_disbursed_derived as principalNetDisbursed,");
            sb.append(" l.interest_charged_derived as interestCharged,");
            sb.append(" l.interest_repaid_derived as interestPaid,");
            sb.append(" l.interest_waived_derived as interestWaived,");
            sb.append(" l.interest_writtenoff_derived as interestWrittenOff,");
            sb.append(" l.interest_outstanding_derived as interestOutstanding,");
            sb.append(" l.fee_charges_charged_derived as feeChargesCharged,");
            sb.append(" l.total_charges_due_at_disbursement_derived as feeChargesDueAtDisbursementCharged,");
            sb.append(" l.fee_charges_repaid_derived as feeChargesPaid,");
            sb.append(" l.fee_charges_waived_derived as feeChargesWaived,");
            sb.append(" l.fee_charges_writtenoff_derived as feeChargesWrittenOff,");
            sb.append(" l.fee_charges_outstanding_derived as feeChargesOutstanding,");
            sb.append(" l.penalty_charges_charged_derived as penaltyChargesCharged,");
            sb.append(" l.penalty_charges_repaid_derived as penaltyChargesPaid,");
            sb.append(" l.penalty_charges_waived_derived as penaltyChargesWaived,");
            sb.append(" l.penalty_charges_writtenoff_derived as penaltyChargesWrittenOff,");
            sb.append(" l.penalty_charges_outstanding_derived as penaltyChargesOutstanding,");
            sb.append(" l.total_expected_repayment_derived as totalExpectedRepayment,");
            sb.append(" l.total_repayment_derived as totalRepayment,");
            sb.append(" l.total_expected_costofloan_derived as totalExpectedCostOfLoan,");
            sb.append(" l.total_costofloan_derived as totalCostOfLoan,");
            sb.append(" l.total_waived_derived as totalWaived,");
            sb.append(" l.total_writtenoff_derived as totalWrittenOff,");
            sb.append(" l.writeoff_reason_cv_id as writeoffReasonId,");
            sb.append(" l.total_outstanding_derived as totalOutstanding,");
            sb.append(" l.total_overpaid_derived as totalOverpaid,");
            sb.append(" l.fixed_emi_amount as fixedEmiAmount,");
            sb.append(" l.max_outstanding_loan_balance as outstandingLoanBalance,");
            sb.append(" l.loan_sub_status_id as loanSubStatusId,");
            sb.append(" l.sync_disbursement_with_meeting as syncDisbursementWithMeeting,");
            sb.append(" l.loan_counter as loanCounter, l.loan_product_counter as loanProductCounter,");
            sb.append(" l.is_npa as isNPA, l.days_in_month_enum as daysInMonth, l.days_in_year_enum as daysInYear, ");
            sb.append(" l.interest_recalculation_enabled as isInterestRecalculationEnabled, ");
            sb.append(" l.is_floating_interest_rate as isFloatingInterestRate, ");
            sb.append("l.expected_disbursal_payment_type_id as expectedDisbursalPaymentTypeId, ");
            sb.append("l.expected_repayment_payment_type_id as expectedRepaymentPaymentTypeId, ");
            sb.append(" l.interest_rate_differential as interestRateDifferential, ");
            sb.append(" l.create_standing_instruction_at_disbursement as createStandingInstructionAtDisbursement, ");
            sb.append(" l.broken_period_method_enum as brokenPeriodMethodType,");
            sb.append(" l.flat_interest_rate as flatInterestRate,");
            sb.append(" l.broken_period_interest as brokenPeriodInterest,");
            sb.append(" l.discount_on_disbursal_amount as discountOnDisbursalAmount, l.amount_for_upfront_collection as amountForUpfrontCollection,");
            sb.append(" l.is_topup as isTopup ");
            sb.append(" from m_loan l");
            this.loanSql = sb.toString();
        }

        public String loanSchema() {
            return this.loanSql;
        }

        @Override
        public LoanAccountData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = "";
            final String currencyNameCode = "";
            final String currencyDisplaySymbol = "";
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currencyData = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);

            final Long id = rs.getLong("id");
            final String accountNo = rs.getString("accountNo");
            final String externalId = rs.getString("externalId");

            final Long clientId = JdbcSupport.getLong(rs, "clientId");

            final Long groupId = JdbcSupport.getLong(rs, "groupId");

            final Integer loanTypeId = JdbcSupport.getInteger(rs, "loanType");
            final EnumOptionData loanType = AccountEnumerations.loanType(loanTypeId);

            final Long fundId = JdbcSupport.getLong(rs, "fundId");

            final Long loanOfficerId = JdbcSupport.getLong(rs, "loanOfficerId");

            final Long loanPurposeId = JdbcSupport.getLong(rs, "loanPurposeId");

            final Integer weeksInYearTypeInteger = JdbcSupport.getInteger(rs, "weeksInYearType");
            final EnumOptionData weeksInYearType = LoanEnumerations.weeksInYearType(WeeksInYearType.fromInt(weeksInYearTypeInteger));

            final Long loanProductId = JdbcSupport.getLong(rs, "loanProductId");

            final BigDecimal outstandingLoanBalance = rs.getBigDecimal("outstandingLoanBalance");

            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final LocalDate rejectedOnDate = JdbcSupport.getLocalDate(rs, "rejectedOnDate");
            final LocalDate withdrawnOnDate = JdbcSupport.getLocalDate(rs, "withdrawnOnDate");
            final LocalDate approvedOnDate = JdbcSupport.getLocalDate(rs, "approvedOnDate");
            final LocalDate expectedDisbursementDate = JdbcSupport.getLocalDate(rs, "expectedDisbursementDate");
            final LocalDate actualDisbursementDate = JdbcSupport.getLocalDate(rs, "actualDisbursementDate");
            final LocalDate closedOnDate = JdbcSupport.getLocalDate(rs, "closedOnDate");
            final LocalDate writtenOffOnDate = JdbcSupport.getLocalDate(rs, "writtenOffOnDate");
            final Long writeoffReasonId = JdbcSupport.getLong(rs, "writeoffReasonId");
            final String writeoffReason = null;
            final LocalDate expectedMaturityDate = JdbcSupport.getLocalDate(rs, "expectedMaturityDate");

            final LoanApplicationTimelineData timeline = new LoanApplicationTimelineData(submittedOnDate, rejectedOnDate, withdrawnOnDate,
                    approvedOnDate, expectedDisbursementDate, actualDisbursementDate, closedOnDate, expectedMaturityDate, writtenOffOnDate);

            final BigDecimal principal = rs.getBigDecimal("principal");
            final BigDecimal approvedPrincipal = rs.getBigDecimal("approvedPrincipal");
            final BigDecimal proposedPrincipal = rs.getBigDecimal("proposedPrincipal");
            final BigDecimal totalOverpaid = rs.getBigDecimal("totalOverpaid");
            final BigDecimal inArrearsTolerance = rs.getBigDecimal("inArrearsTolerance");

            final Integer numberOfRepayments = JdbcSupport.getInteger(rs, "numberOfRepayments");
            final Integer repaymentEvery = JdbcSupport.getInteger(rs, "repaymentEvery");
            final BigDecimal interestRatePerPeriod = rs.getBigDecimal("interestRatePerPeriod");
            final BigDecimal annualInterestRate = rs.getBigDecimal("annualInterestRate");
            final BigDecimal interestRateDifferential = rs.getBigDecimal("interestRateDifferential");
            final boolean isFloatingInterestRate = rs.getBoolean("isFloatingInterestRate");

            final Integer graceOnPrincipalPayment = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnPrincipalPayment");
            final Integer recurringMoratoriumOnPrincipalPeriods = JdbcSupport.getIntegerDefaultToNullIfZero(rs,
                    "recurringMoratoriumOnPrincipalPeriods");
            final Integer graceOnInterestPayment = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnInterestPayment");
            final Integer graceOnInterestCharged = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnInterestCharged");
            final Integer graceOnArrearsAgeing = JdbcSupport.getIntegerDefaultToNullIfZero(rs, "graceOnArrearsAgeing");

            final Integer termFrequency = JdbcSupport.getInteger(rs, "termFrequency");
            final Integer termPeriodFrequencyTypeInt = JdbcSupport.getInteger(rs, "termPeriodFrequencyType");
            final EnumOptionData termPeriodFrequencyType = LoanEnumerations.termFrequencyType(termPeriodFrequencyTypeInt);

            final int repaymentFrequencyTypeInt = JdbcSupport.getInteger(rs, "repaymentFrequencyType");
            final EnumOptionData repaymentFrequencyType = LoanEnumerations.repaymentFrequencyType(repaymentFrequencyTypeInt);

            final int interestRateFrequencyTypeInt = JdbcSupport.getInteger(rs, "interestRateFrequencyType");
            final EnumOptionData interestRateFrequencyType = LoanEnumerations.interestRateFrequencyType(interestRateFrequencyTypeInt);

            final Long transactionStrategyId = JdbcSupport.getLong(rs, "transactionStrategyId");

            final int amortizationTypeInt = JdbcSupport.getInteger(rs, "amortizationType");
            final int interestTypeInt = JdbcSupport.getInteger(rs, "interestType");
            final int interestCalculationPeriodTypeInt = JdbcSupport.getInteger(rs, "interestCalculationPeriodType");

            final EnumOptionData amortizationType = LoanEnumerations.amortizationType(amortizationTypeInt);
            final EnumOptionData interestType = LoanEnumerations.interestType(interestTypeInt);
            final EnumOptionData interestCalculationPeriodType = LoanEnumerations
                    .interestCalculationPeriodType(interestCalculationPeriodTypeInt);
            final Boolean allowPartialPeriodInterestCalcualtion = rs.getBoolean("allowPartialPeriodInterestCalcualtion");

            final Integer lifeCycleStatusId = JdbcSupport.getInteger(rs, "lifeCycleStatusId");
            final LoanStatusEnumData status = LoanEnumerations.status(lifeCycleStatusId);

            final Integer loanSubStatusId = JdbcSupport.getInteger(rs, "loanSubStatusId");
            EnumOptionData loanSubStatus = null;
            if (loanSubStatusId != null) {
                loanSubStatus = LoanSubStatus.loanSubStatus(loanSubStatusId);
            }

            // settings
            final LocalDate expectedFirstRepaymentOnDate = JdbcSupport.getLocalDate(rs, "expectedFirstRepaymentOnDate");
            final LocalDate interestChargedFromDate = JdbcSupport.getLocalDate(rs, "interestChargedFromDate");

            final Boolean syncDisbursementWithMeeting = rs.getBoolean("syncDisbursementWithMeeting");

            final BigDecimal feeChargesDueAtDisbursementCharged = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs,
                    "feeChargesDueAtDisbursementCharged");
            LoanSummaryData loanSummary = null;
            Boolean inArrears = false;
            if (status.id().intValue() >= 300) {

                // loan summary
                final BigDecimal principalDisbursed = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalDisbursed");
                final BigDecimal principalPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalPaid");
                final BigDecimal principalWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalWrittenOff");
                final BigDecimal principalOutstanding = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalOutstanding");
                final BigDecimal principalOverdue = BigDecimal.ZERO;
                final BigDecimal principalNetDisbursed = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalNetDisbursed");

                final BigDecimal interestCharged = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestCharged");
                final BigDecimal interestPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestPaid");
                final BigDecimal interestWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWaived");
                final BigDecimal interestWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWrittenOff");
                final BigDecimal interestOutstanding = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestOutstanding");
                final BigDecimal interestOverdue = BigDecimal.ZERO;

                final BigDecimal feeChargesCharged = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesCharged");
                final BigDecimal feeChargesPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesPaid");
                final BigDecimal feeChargesWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesWaived");
                final BigDecimal feeChargesWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesWrittenOff");
                final BigDecimal feeChargesOutstanding = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesOutstanding");
                final BigDecimal feeChargesOverdue = BigDecimal.ZERO;

                final BigDecimal penaltyChargesCharged = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesCharged");
                final BigDecimal penaltyChargesPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesPaid");
                final BigDecimal penaltyChargesWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesWaived");
                final BigDecimal penaltyChargesWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesWrittenOff");
                final BigDecimal penaltyChargesOutstanding = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesOutstanding");
                final BigDecimal penaltyChargesOverdue = BigDecimal.ZERO;

                final BigDecimal totalExpectedRepayment = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalExpectedRepayment");
                final BigDecimal totalRepayment = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalRepayment");
                final BigDecimal totalExpectedCostOfLoan = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalExpectedCostOfLoan");
                final BigDecimal totalCostOfLoan = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalCostOfLoan");
                final BigDecimal totalWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalWaived");
                final BigDecimal totalWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalWrittenOff");
                final BigDecimal totalOutstanding = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalOutstanding");
                final BigDecimal totalOverdue = BigDecimal.ZERO;

                final LocalDate overdueSinceDate = null;

                loanSummary = new LoanSummaryData(currencyData, principalDisbursed, principalPaid, principalWrittenOff,
                        principalOutstanding, principalOverdue, principalNetDisbursed, interestCharged, interestPaid, interestWaived,
                        interestWrittenOff, interestOutstanding, interestOverdue, feeChargesCharged, feeChargesDueAtDisbursementCharged,
                        feeChargesPaid, feeChargesWaived, feeChargesWrittenOff, feeChargesOutstanding, feeChargesOverdue,
                        penaltyChargesCharged, penaltyChargesPaid, penaltyChargesWaived, penaltyChargesWrittenOff,
                        penaltyChargesOutstanding, penaltyChargesOverdue, totalExpectedRepayment, totalRepayment, totalExpectedCostOfLoan,
                        totalCostOfLoan, totalWaived, totalWrittenOff, totalOutstanding, totalOverdue, overdueSinceDate, writeoffReasonId, writeoffReason);
            }

            GroupGeneralData groupData = null;
            if (groupId != null) {
                final String groupName = null;
                groupData = GroupGeneralData.formGroupData(groupId, groupName);
            }

            final Integer loanCounter = JdbcSupport.getInteger(rs, "loanCounter");
            final Integer loanProductCounter = JdbcSupport.getInteger(rs, "loanProductCounter");
            final BigDecimal fixedEmiAmount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "fixedEmiAmount");
            final Boolean isNPA = rs.getBoolean("isNPA");

            final int daysInMonth = JdbcSupport.getInteger(rs, "daysInMonth");
            final EnumOptionData daysInMonthType = CommonEnumerations.daysInMonthType(daysInMonth);
            final int daysInYear = JdbcSupport.getInteger(rs, "daysInYear");
            final EnumOptionData daysInYearType = CommonEnumerations.daysInYearType(daysInYear);
            final boolean isInterestRecalculationEnabled = rs.getBoolean("isInterestRecalculationEnabled");
            final Boolean createStandingInstructionAtDisbursement = rs.getBoolean("createStandingInstructionAtDisbursement");

            LoanInterestRecalculationData interestRecalculationData = null;

            final boolean isTopup = rs.getBoolean("isTopup");
            PaymentTypeData expectedDisbursalPaymentType = null;
            final Integer expectedDisbursalPaymentTypeId = JdbcSupport.getInteger(rs, "expectedDisbursalPaymentTypeId");
            PaymentTypeData expectedRepaymentPaymentType = null;
            if (expectedDisbursalPaymentTypeId != null) {
                final String disbursementPaymentTypeName = null;
                expectedDisbursalPaymentType = PaymentTypeData.instance(expectedDisbursalPaymentTypeId.longValue(),
                        disbursementPaymentTypeName);
            }
            final Integer expectedRepaymentPaymentTypeId = JdbcSupport.getInteger(rs, "expectedRepaymentPaymentTypeId");
            if (expectedRepaymentPaymentTypeId != null) {
                final String repaymenPaymentTypeName = null;
                expectedRepaymentPaymentType = PaymentTypeData
                        .instance(expectedRepaymentPaymentTypeId.longValue(), repaymenPaymentTypeName);
            }
            
            Integer brokenPeriodTypeId = JdbcSupport.getInteger(rs, "brokenPeriodMethodType");
            EnumOptionData brokenPeriodMethodType = null;
            if (brokenPeriodTypeId != null) {
                brokenPeriodMethodType = LoanEnumerations.brokenPeriodMethodType(brokenPeriodTypeId);
            }
            final BigDecimal flatInterestRate = rs.getBigDecimal("flatInterestRate");
            final BigDecimal brokenPeriodInterest = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "brokenPeriodInterest");
            final BigDecimal discountOnDisbursalAmount = rs.getBigDecimal("discountOnDisbursalAmount");
            final BigDecimal amountForUpfrontCollection = rs.getBigDecimal("amountForUpfrontCollection");
            
            return LoanAccountData.basicLoanDetails(id, accountNo, status, externalId, clientId, groupData, loanType, loanProductId,
                    fundId, loanPurposeId, loanOfficerId, currencyData, proposedPrincipal, principal, approvedPrincipal, totalOverpaid,
                    inArrearsTolerance, termFrequency, termPeriodFrequencyType, numberOfRepayments, repaymentEvery, repaymentFrequencyType,
                    transactionStrategyId, amortizationType, interestRatePerPeriod, interestRateFrequencyType, annualInterestRate,
                    interestType, isFloatingInterestRate, interestRateDifferential, interestCalculationPeriodType,
                    allowPartialPeriodInterestCalcualtion, expectedFirstRepaymentOnDate, graceOnPrincipalPayment,
                    recurringMoratoriumOnPrincipalPeriods, graceOnInterestPayment, graceOnInterestCharged, interestChargedFromDate,
                    timeline, loanSummary, feeChargesDueAtDisbursementCharged, syncDisbursementWithMeeting, loanCounter,
                    loanProductCounter, fixedEmiAmount, outstandingLoanBalance, inArrears, graceOnArrearsAgeing, isNPA, daysInMonthType,
                    daysInYearType, isInterestRecalculationEnabled, interestRecalculationData, createStandingInstructionAtDisbursement,
                    loanSubStatus, isTopup, weeksInYearType, expectedDisbursalPaymentType, expectedRepaymentPaymentType, brokenPeriodMethodType, 
                    flatInterestRate, brokenPeriodInterest, discountOnDisbursalAmount, amountForUpfrontCollection);
        }
    }

    @Override
    public Map<String, Object> retrieveLoanProductIdApprovedAmountClientId(Long loanId) {
        final StringBuilder sql = new StringBuilder(200);
        sql.append("Select product_id as productId, approved_principal as apprivedPrincipal, client_id as clientId ");
        sql.append("from m_loan where id = :loanId ");
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("loanId", loanId);

        return this.namedParameterJdbcTemplate.queryForMap(sql.toString(), paramMap);
    }
    
    @Override
    public LoanTransactionData retrieveLoanInstallmentDetails(Long id) {
        LoanTransactionData loanTransactionData = null;
        try {

            final LoanTransactionDataMapper loanTransactionDataMapper = new LoanTransactionDataMapper();
            final String sql = loanTransactionDataMapper.installmentDetailsSchema();

            Date currentDate = DateUtils.getDateOfTenant();
            loanTransactionData = this.jdbcTemplate.queryForObject(sql, loanTransactionDataMapper,
                    new Object[] { currentDate, currentDate, currentDate, id,LoanStatus.ACTIVE.getValue() });

        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
        return loanTransactionData;
    }
    
    @Override
    public Long retrivePaymentDetailsIdWithLoanAccountNumberAndLoanTransactioId(final long loanTransactionId,
            final String loanAccountNumber) {
        try {
            final StringBuilder sql = new StringBuilder();
            sql.append("select mlt.payment_detail_id from m_loan loan ");
            sql.append("JOIN m_loan_transaction mlt on mlt.loan_id = loan.id ");
            sql.append("where loan.account_no = ? and mlt.id= ?");

            return this.jdbcTemplate.queryForObject(sql.toString(), new Object[] { loanAccountNumber, loanTransactionId }, Long.class);
        } catch (final EmptyResultDataAccessException e) {
            throw new LoanTransactionNotFoundException(loanTransactionId, loanAccountNumber); 
        }
    }
    
    @Override
    public Collection<Long> retriveLoansForMarkingAsNonNPAWithPeriodicAccounding() {
        String currentdate = formatter.print(DateUtils.getLocalDateOfTenant());
        final StringBuilder sql = new StringBuilder(900);
        sql.append("select loan.id from  m_loan loan ");
        sql.append("left join m_loan_arrears_aging laa on laa.loan_id = loan.id ");
        sql.append("inner join m_product_loan mpl on mpl.id = loan.product_id and mpl.overdue_days_for_npa is not null ");
        sql.append("and mpl.accounting_type = ? ");
        sql.append("where  loan.loan_status_id = 300 and loan.is_npa = 1 and ( ");
        sql.append("laa.overdue_since_date_derived is null or (mpl.account_moves_out_of_npa_only_on_arrears_completion = 0 and ");
        sql.append("laa.overdue_since_date_derived >= SUBDATE(?,INTERVAL  ifnull(mpl.overdue_days_for_npa,0) day))) ");

        return this.jdbcTemplate.queryForList(sql.toString(), Long.class, AccountingRuleType.ACCRUAL_PERIODIC.getValue(), currentdate);

    }
    
    @Override
    public Collection<Long> retriveLoansForMarkingAsNPAWithPeriodicAccounding() {
        final StringBuilder sql = new StringBuilder(900);
        String currentdate = formatter.print(DateUtils.getLocalDateOfTenant());
        sql.append(" select loan.id ");
        sql.append("from m_loan_arrears_aging laa");
        sql.append(" INNER JOIN  m_loan loan on laa.loan_id = loan.id ");
        sql.append(" INNER JOIN m_product_loan mpl on mpl.id = loan.product_id AND mpl.overdue_days_for_npa is not null  and mpl.accounting_type = ? ");
        sql.append("WHERE loan.loan_status_id = 300 and loan.is_npa = 0 and ");
        sql.append("laa.overdue_since_date_derived < SUBDATE(?,INTERVAL  ifnull(mpl.overdue_days_for_npa,0) day) ");
        sql.append("group by loan.id ");
        return this.jdbcTemplate.queryForList(sql.toString(), Long.class, AccountingRuleType.ACCRUAL_PERIODIC.getValue(), currentdate);
    }
    
    private static final class LoanTransactionDataMapper implements RowMapper<LoanTransactionData> {

        public String installmentDetailsSchema() {
            return "SELECT loan.id,(IFNULL(repayment.principal_amount,0) + IFNULL(repayment.interest_amount,0) + IFNULL(repayment.fee_charges_amount,0)+ IFNULL(repayment.penalty_charges_amount,0)- IFNULL(repayment.total_paid_in_advance_derived,0)) AS EMI," 
                    + " repayment.dueDate AS next_EMI_Date, loan.total_outstanding_derived AS Total_Outstanding, IFNULL(loan_arre.total_overdue_derived,0) AS Total_Overdue, CASE WHEN (loan.maturedon_date < ?) THEN 0 ELSE (IFNULL(loan_arre.total_overdue_derived,0)+ IFNULL(repayment.principal_amount,0)+ IFNULL(repayment.interest_amount,0)+ IFNULL(repayment.fee_charges_amount,0)+ IFNULL(repayment.penalty_charges_amount,0)- IFNULL(repayment.total_paid_in_advance_derived,0)) END  AS Overdue_with_next_emi "
                    + " FROM m_loan loan LEFT JOIN m_loan_repayment_schedule repayment ON loan.id = repayment.loan_id AND CASE WHEN (loan.maturedon_date >= ? ) THEN repayment.duedate >= ? END "
                    + " LEFT JOIN m_loan_arrears_aging loan_arre ON loan.id = loan_arre.loan_id  WHERE loan.id = ? AND loan.loan_status_id = ? LIMIT 1" ;
        }

        @Override
        public LoanTransactionData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException,DataAccessException {

            final Long id = rs.getLong("id");
            final BigDecimal emi = rs.getBigDecimal("EMI");
            final LocalDate nextEMIDate = JdbcSupport.getLocalDate(rs, "next_EMI_Date");
            final BigDecimal totalOutstanding = rs.getBigDecimal("Total_Outstanding");
            final BigDecimal totalOverdue = rs.getBigDecimal("Total_Overdue");

            final BigDecimal OverdueWithNextEMI = rs.getBigDecimal("Overdue_with_next_emi");

            return new LoanTransactionData(id, emi, nextEMIDate, totalOutstanding, totalOverdue, OverdueWithNextEMI);
        }
    }
    
    @Override
    public Collection<LoanSchedulePeriodData> lookUpLoanSchedulePeriodsByPeriodNumberAndDueDateAndDueAmounts(final Long loanId,
            final boolean excludeLoanScheduleMappedToPDC) {
        final LoanSchedulePeriodDataMapper loanSchedulePeriodDataMapper = new LoanSchedulePeriodDataMapper(excludeLoanScheduleMappedToPDC);
        String sql = "select " + loanSchedulePeriodDataMapper.schema() + " where l.id = ? ";
        if (excludeLoanScheduleMappedToPDC) {
            sql += " and pdcm.due_date is null ";
        }
        return this.jdbcTemplate.query(sql, loanSchedulePeriodDataMapper, new Object[] { loanId });
    }

    private static final class LoanSchedulePeriodDataMapper implements RowMapper<LoanSchedulePeriodData> {

        private final String schema;

        public LoanSchedulePeriodDataMapper(final boolean excludeLoanScheduleMappedToPDC) {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("ls.installment as periodNumber ");
            sqlBuilder.append(",ls.duedate as dueDate ");
            sqlBuilder
                    .append(",ls.principal_amount - IFNULL(ls.principal_writtenoff_derived,0) - IFNULL(ls.principal_completed_derived,0) as principalDue ");
            sqlBuilder
                    .append(",ls.interest_amount - IFNULL(ls.interest_completed_derived,0) - IFNULL(ls.interest_waived_derived,0) - IFNULL(ls.interest_writtenoff_derived,0) as interestDue ");
            sqlBuilder
                    .append(",ls.fee_charges_amount - IFNULL(ls.fee_charges_completed_derived,0) - IFNULL(ls.fee_charges_writtenoff_derived,0) - IFNULL(ls.fee_charges_waived_derived,0) as feeDue ");
            sqlBuilder
                    .append(",ls.penalty_charges_amount - IFNULL(ls.penalty_charges_completed_derived,0) - IFNULL(ls.penalty_charges_writtenoff_derived,0) - IFNULL(ls.penalty_charges_waived_derived,0) as penaltyDue ");
            sqlBuilder.append("from m_loan l ");
            sqlBuilder.append("join m_loan_repayment_schedule ls on ls.loan_id = l.id and ls.completed_derived = 0 ");
            if (excludeLoanScheduleMappedToPDC) {
                sqlBuilder.append("left join f_pdc_cheque_detail_mapping pdcm on pdcm.entity_type = ").append(EntityType.LOAN.getValue());
                sqlBuilder.append(" and pdcm.is_deleted = 0 and pdcm.entity_id = ls.loan_id and pdcm.due_date = ls.duedate ");
            }
            this.schema = sqlBuilder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public LoanSchedulePeriodData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Integer periodNumber = rs.getInt("periodNumber");
            final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");
            final BigDecimal principalDue = rs.getBigDecimal("principalDue");
            final BigDecimal interestDue = rs.getBigDecimal("interestDue");
            final BigDecimal feeDue = rs.getBigDecimal("feeDue");
            final BigDecimal penaltyDue = rs.getBigDecimal("penaltyDue");
            final BigDecimal totalDueForPeriod = MathUtility.add(principalDue, interestDue, feeDue, penaltyDue);
            return LoanSchedulePeriodData.lookUpByPeriodNumberAndDueDateAndDueAmounts(periodNumber, dueDate, totalDueForPeriod);
        }
    }
    
    @Override
    public List<LoanRepaymentScheduleInstallment> retrieveLoanRepaymentScheduleInstallments(final Long loanId) {
        LoanRepaymentScheduleEntityMapper mapper = new LoanRepaymentScheduleEntityMapper();
        String sql = "select " + mapper.schema() + " where ls.loan_id = ? order by ls.installment";
        return this.jdbcTemplate.query(sql, mapper, loanId);
    }
    
    @Override
    public List<LoanTransaction> retrieveLoanTransactions(final Long loanId, final Integer... types) {
        LoanTransactionEntityMapper mapper = new LoanTransactionEntityMapper();
        String sql = "select " + mapper.LoanPaymentsSchema()
                + " where tr.loan_id = :loanId and tr.is_reversed = 0 and tr.transaction_type_enum in (:types)";
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("loanId", loanId);
        paramMap.put("types", types);
        return this.namedParameterJdbcTemplate.query(sql, paramMap, mapper);
    }
    
    @Override
    public List<LoanRecurringCharge> retrieveLoanOverdueRecurringCharge(final Long loanId, final boolean fetchApplicableChargesByState){
        RecurreringChargeMapper mapper = new RecurreringChargeMapper();
        String sql = "select " + mapper.schema() + " where c.loan_id = ? and c.charge_time_enum = ?";
        if (fetchApplicableChargesByState) {
            sql = sql +" and (cod.stop_charge_on_npa = 0 or ml.is_npa = 0)";
        }
        return this.jdbcTemplate.query(sql, mapper, loanId,ChargeTimeType.OVERDUE_INSTALLMENT.getValue());
    }
    
    @Override
    public MonetaryCurrency retrieveLoanCurrency(Long loanId) {
        MonetaryCurrencyMapper mapper = new MonetaryCurrencyMapper();
         String sql = mapper.schema() +  " where l.id = ?";
        return this.jdbcTemplate.queryForObject(sql, mapper, loanId);
    }
    
    @Override
    public List<Long> fetchLoanIdsForOverdueCharge(boolean isRunForBrokenPeriod, boolean isInterestRecalculationLoans) {
        StringBuilder sb = new StringBuilder();
        String currentdate = formatter.print(DateUtils.getLocalDateOfTenant());

        sb.append("SELECT if( DATE_SUB(:currentdate ,INTERVAL MIN(od.grace_period) DAY) > MIN(ls.duedate) , ml.id, 0) as loanId ");
        sb.append(" FROM m_loan ml");
        sb.append(" JOIN f_loan_recurring_charge rc ON rc.loan_id = ml.id");
        sb.append(" JOIN f_loan_overdue_charge_detail od ON od.recurrence_charge_id = rc.id AND (od.last_run_on_date <> :currentdate OR od.last_run_on_date IS NULL)");
        if (isRunForBrokenPeriod) {
            sb.append("or (od.apply_charge_for_broken_period = 1 and (od.last_applied_on_date IS NULL or  od.last_run_on_date <> :currentdate))");
        }
        sb.append(" JOIN m_loan_repayment_schedule ls ON ml.id = ls.loan_id");
        sb.append(" WHERE  ml.loan_status_id = 300 and ");
        if (isInterestRecalculationLoans) {
            sb.append("ml.interest_recalculation_enabled = 1 ");
        } else {
            sb.append("ml.interest_recalculation_enabled = 0 ");
        }
        sb.append("AND :currentdate > ls.duedate AND ls.recalculated_interest_component <> 1 AND ls.completed_derived <> 1");
        sb.append(" GROUP BY ml.id");
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("currentdate", currentdate);
        return this.namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, Long.class);
    }
    
    private static final class LoanRepaymentScheduleEntityMapper implements RowMapper<LoanRepaymentScheduleInstallment> {

        public String schema() {
            StringBuilder sb = new StringBuilder();
            sb.append(" ls.id as id, ls.loan_id as loanId, ls.installment as period, ls.fromdate as fromDate, ls.duedate as dueDate, ls.obligations_met_on_date as obligationsMetOnDate, ls.completed_derived as complete,");
            sb.append(" ls.principal_amount as principalDue, ls.principal_completed_derived as principalPaid, ls.principal_writtenoff_derived as principalWrittenOff, ");
            sb.append(" ls.interest_amount as interestDue, ls.interest_completed_derived as interestPaid, ls.interest_waived_derived as interestWaived, ls.interest_writtenoff_derived as interestWrittenOff, ");
            sb.append(" ls.fee_charges_amount as feeChargesDue, ls.fee_charges_completed_derived as feeChargesPaid, ls.fee_charges_waived_derived as feeChargesWaived, ls.fee_charges_writtenoff_derived as feeChargesWrittenOff, ");
            sb.append(" ls.penalty_charges_amount as penaltyChargesDue, ls.penalty_charges_completed_derived as penaltyChargesPaid, ls.penalty_charges_waived_derived as penaltyChargesWaived, ls.penalty_charges_writtenoff_derived as penaltyChargesWrittenOff, ");
            sb.append(" ls.total_paid_in_advance_derived as totalPaidInAdvanceForPeriod, ls.total_paid_late_derived as totalPaidLateForPeriod, ls.advance_payment_amount as advancePaymentAmount, ");
            sb.append(" ls.recalculated_interest_component as recalculatedInterestComponent, ls.capitalized_charge_amount as  capitalizedCharePortion ");
            sb.append(" from  m_loan_repayment_schedule ls ");
            return sb.toString();
        }

        @Override
        public LoanRepaymentScheduleInstallment mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final Integer period = JdbcSupport.getInteger(rs, "period");
            final Long id = JdbcSupport.getLong(rs, "id");
            LocalDate fromDate = JdbcSupport.getLocalDate(rs, "fromDate");
            final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");
            final LocalDate obligationsMetOnDate = JdbcSupport.getLocalDate(rs, "obligationsMetOnDate");
            final boolean complete = rs.getBoolean("complete");
            final boolean recalculatedInterestComponent = rs.getBoolean("recalculatedInterestComponent");

            final BigDecimal principalDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalDue");
            final BigDecimal principalPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalPaid");
            final BigDecimal principalWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalWrittenOff");

            final BigDecimal interestExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestDue");
            final BigDecimal interestPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestPaid");
            final BigDecimal interestWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWaived");
            final BigDecimal interestWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWrittenOff");

            final BigDecimal feeChargesExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesDue");
            final BigDecimal feeChargesPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesPaid");
            final BigDecimal feeChargesWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesWaived");
            final BigDecimal feeChargesWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesWrittenOff");

            final BigDecimal penaltyChargesExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesDue");
            final BigDecimal penaltyChargesPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesPaid");
            final BigDecimal penaltyChargesWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesWaived");
            final BigDecimal penaltyChargesWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesWrittenOff");

            final BigDecimal capitalizedCharePortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "capitalizedCharePortion");

            return new LoanRepaymentScheduleInstallment(id,period, fromDate, dueDate, principalDue, principalPaid, principalWrittenOff,
                    interestExpectedDue, interestPaid, interestWaived, interestWrittenOff, feeChargesExpectedDue, feeChargesPaid,
                    feeChargesWaived, feeChargesWrittenOff, penaltyChargesExpectedDue, recalculatedInterestComponent, penaltyChargesPaid,
                    penaltyChargesWaived, penaltyChargesWrittenOff, complete, obligationsMetOnDate, capitalizedCharePortion);
        }

    }
    
    
    private static final class LoanTransactionEntityMapper implements RowMapper<LoanTransaction> {

        public String LoanPaymentsSchema() {
            StringBuilder sb = new StringBuilder();
            sb.append(" tr.id as id, tr.transaction_type_enum as transactionType, tr.transaction_sub_type_enum as transactionSubType, ");
            sb.append("tr.transaction_date as `date`, tr.amount as total, ");
            sb.append(" tr.principal_portion_derived as principal, tr.interest_portion_derived as interest, ");
            sb.append(" tr.fee_charges_portion_derived as fees, tr.penalty_charges_portion_derived as penalties, ");
            sb.append(" tr.overpayment_portion_derived as overpayment,  ");
            sb.append(" tr.unrecognized_income_portion as unrecognizedIncome,");
            sb.append(" tr.submitted_on_date as submittedOnDate, ");
            sb.append(" tr.created_date as createdDate  ");
            sb.append(" from m_loan_transaction tr ");
            return sb.toString();
        }

        @Override
        public LoanTransaction mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final int transactionType = JdbcSupport.getInteger(rs, "transactionType");
            final int transactionSubType = JdbcSupport.getInteger(rs, "transactionSubType");

            final DateTime createdDateTime = JdbcSupport.getDateTime(rs, "createdDate");
            final Date createdDate = createdDateTime == null ? null : createdDateTime.toDate();
            final Date date = rs.getDate("date");
            final Date submittedOnDate = rs.getDate("submittedOnDate");
            final BigDecimal totalAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "total");
            final BigDecimal principalPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principal");
            final BigDecimal interestPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interest");
            final BigDecimal feeChargesPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "fees");
            final BigDecimal penaltyChargesPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penalties");
            final BigDecimal overPaymentPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "overpayment");
            final BigDecimal unrecognizedIncomePortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "unrecognizedIncome");
            return new LoanTransaction(id, transactionType, transactionSubType, date, totalAmount, principalPortion,
                    interestPortion, feeChargesPortion, penaltyChargesPortion, unrecognizedIncomePortion, overPaymentPortion, createdDate,
                    submittedOnDate);
        }

    }
    
    
    private static final class RecurreringChargeMapper implements RowMapper<LoanRecurringCharge> {

        final LoanChargeOverdueDetailMapper chargeOverdueDetailMapper = new LoanChargeOverdueDetailMapper();
        final String schema;
        
        public RecurreringChargeMapper() {

            StringBuilder sb = new StringBuilder();
            sb.append("c.charge_id as chargeId, c.amount as amount, ");
            sb.append("c.charge_time_enum as chargeTime, ");
            sb.append("c.charge_payment_mode_enum as chargePaymentMode, ");
            sb.append("c.charge_calculation_enum as chargeCalculation, c.is_penalty as penalty, ");
            sb.append("c.fee_interval as feeInterval, c.fee_frequency as feeFrequency, ");
            sb.append("c.charge_percentage_type as percentageType, c.charge_percentage_period_type as percentagePeriodType,");
            sb.append("cod.id as overdueDetailId,cod.grace_period as penaltyGracePeriod, cod.penalty_free_period as penaltyFreePeriod, ");
            sb.append("cod.grace_type_enum as penaltyGraceType,cod.apply_charge_for_broken_period as applyPenaltyForBrokenPeriod, ");
            sb.append("cod.is_based_on_original_schedule as penaltyBasedOnOriginalSchedule, cod.consider_only_posted_interest as penaltyOnPostedInterestOnly,");
            sb.append("cod.calculate_charge_on_current_overdue as penaltyOnCurrentOverdue, cod.min_overdue_amount_required as minOverdueAmountRequired, ");
            sb.append("cod.last_applied_on_date as lastAppliedOnDate, cod.last_run_on_date as lastRunOnDate,cod.stop_charge_on_npa as stopChargeOnNPA,");
            sb.append("c.tax_group_id as taxGroupId ");
            sb.append(" from m_loan ml");
            sb.append(" JOIN f_loan_recurring_charge c on ml.id = c.loan_id");
            sb.append(" LEFT JOIN f_loan_overdue_charge_detail cod on cod.recurrence_charge_id = c.id ");
            this.schema = sb.toString();

        }

        public String schema() {
            return this.schema;
        }

        @Override
        public LoanRecurringCharge mapRow(ResultSet rs, int rowNum) throws SQLException {

            final Long chargeId = rs.getLong("chargeId");
            final BigDecimal amount = rs.getBigDecimal("amount");
            final int chargeTime = rs.getInt("chargeTime");
            ChargeTimeType chargeTimeTypeEnum = ChargeTimeType.fromInt(chargeTime);
            final int chargeCalculation = rs.getInt("chargeCalculation");
            final int paymentMode = rs.getInt("chargePaymentMode");
            final boolean penalty = rs.getBoolean("penalty");
            final Integer feeInterval = JdbcSupport.getInteger(rs, "feeInterval");
            final Integer feeFrequency = JdbcSupport.getInteger(rs, "feeFrequency");
            final Long taxGroupId = JdbcSupport.getLong(rs, "taxGroupId");
            final int percentageType = rs.getInt("percentageType");
            final int percentagePeriodType = rs.getInt("percentagePeriodType");
            LoanChargeOverdueDetails chargeOverdueData = null;
            if (chargeTimeTypeEnum.isOverdueInstallment()) {
                chargeOverdueData = this.chargeOverdueDetailMapper.mapRow(rs, rowNum);
            }

            return new LoanRecurringCharge(chargeId, amount, chargeTime, chargeCalculation, paymentMode, feeInterval, penalty,
                    feeFrequency, percentageType, percentagePeriodType, taxGroupId, chargeOverdueData);
        }

    }
    
    private static final class LoanChargeOverdueDetailMapper implements RowMapper<LoanChargeOverdueDetails> {

        @Override
        public LoanChargeOverdueDetails mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {

            final Integer gracePeriod = JdbcSupport.getInteger(rs, "penaltyGracePeriod");
            final Integer penaltyFreePeriod = JdbcSupport.getInteger(rs, "penaltyFreePeriod");
            final Integer penaltyGraceType = JdbcSupport.getInteger(rs, "penaltyGraceType");
            final boolean applyChargeForBrokenPeriod = rs.getBoolean("applyPenaltyForBrokenPeriod");
            final boolean isBasedOnOriginalSchedule = rs.getBoolean("penaltyBasedOnOriginalSchedule");
            final boolean considerOnlyPostedInterest = rs.getBoolean("penaltyOnPostedInterestOnly");
            final boolean calculateChargeOnCurrentOverdue = rs.getBoolean("penaltyOnCurrentOverdue");
            final boolean stopChargeOnNPA = rs.getBoolean("stopChargeOnNPA");
            final BigDecimal minOverdueAmountRequired = rs.getBigDecimal("minOverdueAmountRequired");
            final Date lastAppliedOnDate = rs.getDate("lastAppliedOnDate");
            final Date lastRunOnDate = rs.getDate("lastRunOnDate");

            return new LoanChargeOverdueDetails(gracePeriod, penaltyFreePeriod, penaltyGraceType, applyChargeForBrokenPeriod,
                    isBasedOnOriginalSchedule, considerOnlyPostedInterest, calculateChargeOnCurrentOverdue, stopChargeOnNPA,
                    minOverdueAmountRequired, lastAppliedOnDate, lastRunOnDate);
        }

    }
    
    private static final class MonetaryCurrencyMapper implements RowMapper<MonetaryCurrency> {

        public String schema() {
            return "select l.currency_code as code, l.currency_digits as digitsAfterDecimal, l.currency_multiplesof as inMultiplesOf  from m_loan l ";
        }

        @Override
        public MonetaryCurrency mapRow(ResultSet rs, int rowNum) throws SQLException {
            String code = rs.getString("code");
            Integer digitsAfterDecimal = JdbcSupport.getInteger(rs, "digitsAfterDecimal");
            Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            return new MonetaryCurrency(code, digitsAfterDecimal, inMultiplesOf);
        }

    }
    
}
