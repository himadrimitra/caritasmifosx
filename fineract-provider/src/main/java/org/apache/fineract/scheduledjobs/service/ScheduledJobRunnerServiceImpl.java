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
package org.apache.fineract.scheduledjobs.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.exception.ExceptionHelper;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSourceServiceFactory;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.hooks.domain.HookConfigurationRepository;
import org.apache.fineract.infrastructure.hooks.domain.HookRepository;
import org.apache.fineract.infrastructure.hooks.service.HookReadPlatformServiceImpl;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.holiday.domain.HolidayRepositoryWrapper;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.workingdays.data.AdjustedDateDetailsDTO;
import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.apache.fineract.portfolio.calendar.domain.CalendarFrequencyType;
import org.apache.fineract.portfolio.calendar.service.CalendarReadPlatformService;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.calendar.service.CalendarWritePlatformService;
import org.apache.fineract.portfolio.charge.data.ChargeData;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.service.ChargeEnumerations;
import org.apache.fineract.portfolio.charge.service.ChargeReadPlatformService;
import org.apache.fineract.portfolio.client.data.ClientChargeData;
import org.apache.fineract.portfolio.client.data.ClientRecurringChargeData;
import org.apache.fineract.portfolio.client.service.ClientChargeWritePlatformService;
import org.apache.fineract.portfolio.client.service.ClientRecurringChargeReadPlatformService;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.investment.data.InvestmentBatchJobData;
import org.apache.fineract.portfolio.investment.exception.NoAnyInvestmentFoundForDistributionException;
import org.apache.fineract.portfolio.investment.service.InvestmentBatchJobReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanSchedularService;
import org.apache.fineract.portfolio.loanaccount.service.LoanUtilService;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetailRepository;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.api.SavingsAccountTransactionsApiResource;
import org.apache.fineract.portfolio.savings.data.DepositAccountData;
import org.apache.fineract.portfolio.savings.data.SavingIdListData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountAnnualFeeData;
import org.apache.fineract.portfolio.savings.data.SavingsIdOfChargeData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.apache.fineract.portfolio.savings.service.DepositAccountReadPlatformService;
import org.apache.fineract.portfolio.savings.service.DepositAccountWritePlatformService;
import org.apache.fineract.portfolio.savings.service.RecurringDepositSchedularService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountChargeReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountDividendReadPlatformService;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountSchedularService;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.finflux.common.util.ScheduleDateGeneratorUtil;
import com.finflux.common.util.WorkingDaysAndHolidaysUtil;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.data.TaskExecutionData;
import com.finflux.task.data.TaskMakerCheckerData;
import com.finflux.task.service.TaskPlatformReadService;
import com.finflux.transaction.execution.data.BankTransactionDetail;
import com.finflux.transaction.execution.data.BankTransactionResponse;
import com.finflux.transaction.execution.data.TransactionStatus;
import com.finflux.transaction.execution.data.TransferType;
import com.finflux.transaction.execution.domain.BankAccountTransaction;
import com.finflux.transaction.execution.domain.BankAccountTransactionRepository;
import com.finflux.transaction.execution.provider.BankTransferService;
import com.finflux.transaction.execution.service.BankTransactionService;
import com.google.gson.JsonElement;

@SuppressWarnings("deprecation")
@Service(value = "scheduledJobRunnerService")
public class ScheduledJobRunnerServiceImpl implements ScheduledJobRunnerService {

    private final static Logger logger = LoggerFactory.getLogger(ScheduledJobRunnerServiceImpl.class);
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private final DateTimeFormatter formatterWithTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private final RoutingDataSourceServiceFactory dataSourceServiceFactory;
    private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService;
    private final DepositAccountReadPlatformService depositAccountReadPlatformService;
    private final DepositAccountWritePlatformService depositAccountWritePlatformService;

    private final ChargeReadPlatformService chargeReadPlatformService;
    private final ToApiJsonSerializer<CommandProcessingResult> toApiResultJsonSerializer;
    private final HookReadPlatformServiceImpl hookReadPlatformServiceImpl;
    private final HookRepository hookRepository;
    private final HookConfigurationRepository hookConfigurationRepository;
    private final LoanReadPlatformService loanReadPlatformService;
    private final InvestmentBatchJobReadPlatformService investmentBatchJobReadPlatformService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final SavingsAccountDomainService savingsAccountDomainService;
    private final SavingsAccountAssembler savingAccountAssembler;
    private final PaymentTypeRepositoryWrapper paymentTyperepositoryWrapper;
    private final PaymentDetailRepository paymentDetailRepository;
    private final SavingsAccountTransactionsApiResource savingsAccountTransactionsApiResource;
    private final PlatformSecurityContext context;
    private final FromJsonHelper fromApiJsonHelper;
    private final SavingsAccountRepository savingAccount;

    private final ShareAccountDividendReadPlatformService shareAccountDividendReadPlatformService;
    private final ShareAccountSchedularService shareAccountSchedularService;
    private final ClientRecurringChargeReadPlatformService clientRecurringChargeReadPlatformService;
    private final BankTransactionService bankTransactionService;
    private final BankAccountTransactionRepository bankAccountTransactionRepository;
    private final TaskPlatformReadService taskPlatformReadService;
    private final ConfigurationDomainService configurationDomainService;
    private final HolidayRepositoryWrapper holidayRepository;
    private final LoanSchedularService loanSchedularService;
    private final LoanUtilService loanUtilService;
    private final RecurringDepositSchedularService recurringDepositSchedularService;
    private final ClientChargeWritePlatformService clientChargeWritePlatformService;
    private final CalendarReadPlatformService calanderReadPlatformService;
    private final CalendarWritePlatformService calendarWritePlatformService;

    @Autowired
    public ScheduledJobRunnerServiceImpl(final RoutingDataSourceServiceFactory dataSourceServiceFactory,
            final SavingsAccountWritePlatformService savingsAccountWritePlatformService,
            final SavingsAccountChargeReadPlatformService savingsAccountChargeReadPlatformService,
            final DepositAccountReadPlatformService depositAccountReadPlatformService,
            final DepositAccountWritePlatformService depositAccountWritePlatformService,
            final ChargeReadPlatformService chargeReadPlatformService,
            final ToApiJsonSerializer<CommandProcessingResult> toApiResultJsonSerializer,
            final HookReadPlatformServiceImpl hookReadPlatformServiceImpl, final HookRepository hookRepository,
            final HookConfigurationRepository hookConfigurationRepository, final LoanReadPlatformService loanReadPlatformService,
            final InvestmentBatchJobReadPlatformService investmentBatchJobReadPlatformService,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final SavingsAccountDomainService savingsAccountDomainService, final SavingsAccountAssembler savingAccountAssembler,
            final PaymentTypeRepositoryWrapper paymentTyperepositoryWrapper, final PaymentDetailRepository paymentDetailRepository,
            final SavingsAccountTransactionsApiResource savingsAccountTransactionsApiResource, final PlatformSecurityContext context,
            final FromJsonHelper fromApiJsonHelper, final SavingsAccountRepository savingAccount,
            final ShareAccountDividendReadPlatformService shareAccountDividendReadPlatformService,
            final ShareAccountSchedularService shareAccountSchedularService,
            final ClientRecurringChargeReadPlatformService clientRecurringChargeReadPlatformService,
            final BankTransactionService bankTransactionService, final BankAccountTransactionRepository bankAccountTransactionRepository,
            final TaskPlatformReadService taskPlatformReadService, final ConfigurationDomainService configurationDomainService,
            final HolidayRepositoryWrapper holidayRepository, final LoanSchedularService loanSchedularService,
            final LoanUtilService loanUtilService, final RecurringDepositSchedularService recurringDepositSchedularService,
            final ClientChargeWritePlatformService clientChargeWritePlatformService,
            final CalendarReadPlatformService calanderReadPlatformService,
            final CalendarWritePlatformService calendarWritePlatformService) {
        this.dataSourceServiceFactory = dataSourceServiceFactory;
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
        this.savingsAccountChargeReadPlatformService = savingsAccountChargeReadPlatformService;
        this.depositAccountReadPlatformService = depositAccountReadPlatformService;
        this.depositAccountWritePlatformService = depositAccountWritePlatformService;
        this.chargeReadPlatformService = chargeReadPlatformService;
        this.toApiResultJsonSerializer = toApiResultJsonSerializer;
        this.hookReadPlatformServiceImpl = hookReadPlatformServiceImpl;
        this.hookRepository = hookRepository;
        this.hookConfigurationRepository = hookConfigurationRepository;
        this.loanReadPlatformService = loanReadPlatformService;
        this.investmentBatchJobReadPlatformService = investmentBatchJobReadPlatformService;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.savingsAccountDomainService = savingsAccountDomainService;
        this.savingAccountAssembler = savingAccountAssembler;
        this.paymentTyperepositoryWrapper = paymentTyperepositoryWrapper;
        this.paymentDetailRepository = paymentDetailRepository;
        this.savingsAccountTransactionsApiResource = savingsAccountTransactionsApiResource;
        this.context = context;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.savingAccount = savingAccount;
        this.shareAccountDividendReadPlatformService = shareAccountDividendReadPlatformService;
        this.shareAccountSchedularService = shareAccountSchedularService;
        this.clientRecurringChargeReadPlatformService = clientRecurringChargeReadPlatformService;
        this.bankTransactionService = bankTransactionService;
        this.bankAccountTransactionRepository = bankAccountTransactionRepository;
        this.taskPlatformReadService = taskPlatformReadService;
        this.configurationDomainService = configurationDomainService;
        this.holidayRepository = holidayRepository;
        this.loanSchedularService = loanSchedularService;
        this.loanUtilService = loanUtilService;
        this.recurringDepositSchedularService = recurringDepositSchedularService;
        this.clientChargeWritePlatformService = clientChargeWritePlatformService;
        this.calanderReadPlatformService = calanderReadPlatformService;
        this.calendarWritePlatformService = calendarWritePlatformService;
    }

    @Transactional
    @Override
    @CronTarget(jobName = JobName.UPDATE_CLIENT_SUB_STATUS)
    public void updateClientSubStatus() {

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

        final int result = jdbcTemplate.update("call doClientSubStatusUpdates()");

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Results affected by update: " + result);
    }

    @Transactional
    @Override
    @CronTarget(jobName = JobName.UPDATE_LOAN_SUMMARY)
    public void updateLoanSummaryDetails() {

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

        final StringBuilder updateSqlBuilder = new StringBuilder(900);
        updateSqlBuilder.append("update m_loan ");
        updateSqlBuilder.append("join (");
        updateSqlBuilder.append("SELECT ml.id AS loanId,");
        updateSqlBuilder.append(
                " if(ml.broken_period_method_enum   = 2,  ml.principal_amount + IFNULL(ml.broken_period_interest,0),ml.principal_amount) as principal_disbursed_derived, ");
        updateSqlBuilder.append("SUM(IFNULL(mr.principal_completed_derived,0)) as principal_repaid_derived, ");
        updateSqlBuilder.append("SUM(IFNULL(mr.principal_writtenoff_derived,0)) as principal_writtenoff_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.interest_amount,0)) as interest_charged_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.interest_completed_derived,0)) as interest_repaid_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.interest_waived_derived,0)) as interest_waived_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.interest_writtenoff_derived,0)) as interest_writtenoff_derived,");
        updateSqlBuilder.append(
                "SUM(IFNULL(mr.fee_charges_amount,0)) + IFNULL((select SUM(lc.amount) from  m_loan_charge lc where lc.loan_id=ml.id and lc.is_active=1 and lc.charge_time_enum=1),0) as fee_charges_charged_derived,");
        updateSqlBuilder.append(
                "SUM(IFNULL(mr.fee_charges_completed_derived,0)) + IFNULL((select SUM(lc.amount_paid_derived) from  m_loan_charge lc where lc.loan_id=ml.id and lc.is_active=1 and lc.charge_time_enum=1),0) as fee_charges_repaid_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.fee_charges_waived_derived,0)) as fee_charges_waived_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.fee_charges_writtenoff_derived,0)) as fee_charges_writtenoff_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_amount,0)) as penalty_charges_charged_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_completed_derived,0)) as penalty_charges_repaid_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_waived_derived,0)) as penalty_charges_waived_derived,");
        updateSqlBuilder.append("SUM(IFNULL(mr.penalty_charges_writtenoff_derived,0)) as penalty_charges_writtenoff_derived ");
        updateSqlBuilder.append(" FROM m_loan ml ");
        updateSqlBuilder.append("INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id ");
        updateSqlBuilder.append("WHERE ml.disbursedon_date is not null ");
        updateSqlBuilder.append("GROUP BY ml.id ");
        updateSqlBuilder.append(") x on x.loanId = m_loan.id ");

        updateSqlBuilder.append("SET m_loan.principal_disbursed_derived = x.principal_disbursed_derived,");
        updateSqlBuilder.append("m_loan.principal_repaid_derived = x.principal_repaid_derived,");
        updateSqlBuilder.append("m_loan.principal_writtenoff_derived = x.principal_writtenoff_derived,");
        updateSqlBuilder.append(
                "m_loan.principal_outstanding_derived = (x.principal_disbursed_derived - (x.principal_repaid_derived + x.principal_writtenoff_derived)),");
        updateSqlBuilder.append("m_loan.interest_charged_derived = x.interest_charged_derived,");
        updateSqlBuilder.append("m_loan.interest_repaid_derived = x.interest_repaid_derived,");
        updateSqlBuilder.append("m_loan.interest_waived_derived = x.interest_waived_derived,");
        updateSqlBuilder.append("m_loan.interest_writtenoff_derived = x.interest_writtenoff_derived,");
        updateSqlBuilder.append(
                "m_loan.interest_outstanding_derived = (x.interest_charged_derived - (x.interest_repaid_derived + x.interest_waived_derived + x.interest_writtenoff_derived)),");
        updateSqlBuilder.append("m_loan.fee_charges_charged_derived = x.fee_charges_charged_derived,");
        updateSqlBuilder.append("m_loan.fee_charges_repaid_derived = x.fee_charges_repaid_derived,");
        updateSqlBuilder.append("m_loan.fee_charges_waived_derived = x.fee_charges_waived_derived,");
        updateSqlBuilder.append("m_loan.fee_charges_writtenoff_derived = x.fee_charges_writtenoff_derived,");
        updateSqlBuilder.append(
                "m_loan.fee_charges_outstanding_derived = (x.fee_charges_charged_derived - (x.fee_charges_repaid_derived + x.fee_charges_waived_derived + x.fee_charges_writtenoff_derived)),");
        updateSqlBuilder.append("m_loan.penalty_charges_charged_derived = x.penalty_charges_charged_derived,");
        updateSqlBuilder.append("m_loan.penalty_charges_repaid_derived = x.penalty_charges_repaid_derived,");
        updateSqlBuilder.append("m_loan.penalty_charges_waived_derived = x.penalty_charges_waived_derived,");
        updateSqlBuilder.append("m_loan.penalty_charges_writtenoff_derived = x.penalty_charges_writtenoff_derived,");
        updateSqlBuilder.append(
                "m_loan.penalty_charges_outstanding_derived = (x.penalty_charges_charged_derived - (x.penalty_charges_repaid_derived + x.penalty_charges_waived_derived + x.penalty_charges_writtenoff_derived)),");
        updateSqlBuilder.append(
                "m_loan.total_expected_repayment_derived = (x.principal_disbursed_derived + x.interest_charged_derived + x.fee_charges_charged_derived + x.penalty_charges_charged_derived),");
        updateSqlBuilder.append(
                "m_loan.total_repayment_derived = (x.principal_repaid_derived + x.interest_repaid_derived + x.fee_charges_repaid_derived + x.penalty_charges_repaid_derived),");
        updateSqlBuilder.append(
                "m_loan.total_expected_costofloan_derived = (x.interest_charged_derived + x.fee_charges_charged_derived + x.penalty_charges_charged_derived),");
        updateSqlBuilder.append(
                "m_loan.total_costofloan_derived = (x.interest_repaid_derived + x.fee_charges_repaid_derived + x.penalty_charges_repaid_derived),");
        updateSqlBuilder.append(
                "m_loan.total_waived_derived = (x.interest_waived_derived + x.fee_charges_waived_derived + x.penalty_charges_waived_derived),");
        updateSqlBuilder.append(
                "m_loan.total_writtenoff_derived = (x.interest_writtenoff_derived +  x.fee_charges_writtenoff_derived + x.penalty_charges_writtenoff_derived),");
        updateSqlBuilder.append("m_loan.total_outstanding_derived=");
        updateSqlBuilder.append(" (x.principal_disbursed_derived - (x.principal_repaid_derived + x.principal_writtenoff_derived)) + ");
        updateSqlBuilder.append(
                " (x.interest_charged_derived - (x.interest_repaid_derived + x.interest_waived_derived + x.interest_writtenoff_derived)) +");
        updateSqlBuilder.append(
                " (x.fee_charges_charged_derived - (x.fee_charges_repaid_derived + x.fee_charges_waived_derived + x.fee_charges_writtenoff_derived)) +");
        updateSqlBuilder.append(
                " (x.penalty_charges_charged_derived - (x.penalty_charges_repaid_derived + x.penalty_charges_waived_derived + x.penalty_charges_writtenoff_derived))");

        final int result = jdbcTemplate.update(updateSqlBuilder.toString());

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Results affected by update: " + result);
    }

    @Transactional
    @Override
    @CronTarget(jobName = JobName.UPDATE_LOAN_PAID_IN_ADVANCE)
    public void updateLoanPaidInAdvance() {
        final String currentdate = this.formatter.print(DateUtils.getLocalDateOfTenant());
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

        jdbcTemplate.execute("truncate table m_loan_paid_in_advance");

        final StringBuilder updateSqlBuilder = new StringBuilder(900);

        updateSqlBuilder.append(
                "INSERT INTO m_loan_paid_in_advance(loan_id, principal_in_advance_derived, interest_in_advance_derived, fee_charges_in_advance_derived, penalty_charges_in_advance_derived, total_in_advance_derived)");
        updateSqlBuilder.append(" select ml.id as loanId,");
        updateSqlBuilder.append(" SUM(ifnull(mr.principal_completed_derived, 0)) as principal_in_advance_derived,");
        updateSqlBuilder.append(" SUM(ifnull(mr.interest_completed_derived, 0)) as interest_in_advance_derived,");
        updateSqlBuilder.append(" SUM(ifnull(mr.fee_charges_completed_derived, 0)) as fee_charges_in_advance_derived,");
        updateSqlBuilder.append(" SUM(ifnull(mr.penalty_charges_completed_derived, 0)) as penalty_charges_in_advance_derived,");
        updateSqlBuilder.append(
                " (SUM(ifnull(mr.principal_completed_derived, 0)) + SUM(ifnull(mr.interest_completed_derived, 0)) + SUM(ifnull(mr.fee_charges_completed_derived, 0)) + SUM(ifnull(mr.penalty_charges_completed_derived, 0))) as total_in_advance_derived");
        updateSqlBuilder.append(" FROM m_loan ml ");
        updateSqlBuilder.append(" INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id ");
        updateSqlBuilder.append(" WHERE ml.loan_status_id = 300 ");
        updateSqlBuilder.append(" and mr.duedate >= ? ");
        updateSqlBuilder.append(" GROUP BY ml.id");
        updateSqlBuilder
                .append(" HAVING (SUM(ifnull(mr.principal_completed_derived, 0)) + SUM(ifnull(mr.interest_completed_derived, 0)) +");
        updateSqlBuilder
                .append(" SUM(ifnull(mr.fee_charges_completed_derived, 0)) + SUM(ifnull(mr.penalty_charges_completed_derived, 0))) > 0.0");

        final int result = jdbcTemplate.update(updateSqlBuilder.toString(), currentdate);

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Results affected by update: " + result);
    }

    @Override
    @CronTarget(jobName = JobName.APPLY_ANNUAL_FEE_FOR_SAVINGS)
    public void applyAnnualFeeForSavings() {

        final Collection<SavingsAccountAnnualFeeData> annualFeeData = this.savingsAccountChargeReadPlatformService
                .retrieveChargesWithAnnualFeeDue();

        for (final SavingsAccountAnnualFeeData savingsAccountReference : annualFeeData) {
            try {
                this.savingsAccountWritePlatformService.applyAnnualFee(savingsAccountReference.getId(),
                        savingsAccountReference.getAccountId());
            } catch (final PlatformApiDataValidationException e) {
                final List<ApiParameterError> errors = e.getErrors();
                for (final ApiParameterError error : errors) {
                    logger.error("Apply annual fee failed for account:" + savingsAccountReference.getAccountNo() + " with message "
                            + error.getDeveloperMessage());
                }
            } catch (final Exception ex) {
                // need to handle this scenario
            }
        }

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Savings accounts affected by update: " + annualFeeData.size());
    }

    @Override
    @CronTarget(jobName = JobName.PAY_DUE_SAVINGS_CHARGES)
    public void applyDueChargesForSavings() throws JobExecutionException {
        final Collection<SavingsAccountAnnualFeeData> chargesDueData = this.savingsAccountChargeReadPlatformService
                .retrieveChargesWithDue();
        final StringBuilder errorMsg = new StringBuilder();

        for (final SavingsAccountAnnualFeeData savingsAccountReference : chargesDueData) {
            try {
                this.savingsAccountWritePlatformService.applyChargeDue(savingsAccountReference.getId(),
                        savingsAccountReference.getAccountId());
            } catch (final PlatformApiDataValidationException e) {
                final List<ApiParameterError> errors = e.getErrors();
                for (final ApiParameterError error : errors) {
                    logger.error("Apply Charges due for savings failed for account:" + savingsAccountReference.getAccountNo()
                            + " with message " + error.getDeveloperMessage());
                    errorMsg.append("Apply Charges due for savings failed for account:").append(savingsAccountReference.getAccountNo())
                            .append(" with message ").append(error.getDeveloperMessage());
                }
            }
        }

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Savings accounts affected by update: " + chargesDueData.size());

        /*
         * throw exception if any charge payment fails.
         */
        if (errorMsg.length() > 0) { throw new JobExecutionException(errorMsg.toString()); }
    }

    @Override
    @CronTarget(jobName = JobName.UPDATE_NPA)
    public void updateNPA() throws JobExecutionException {
        final StringBuilder sb = new StringBuilder();
        this.loanSchedularService.updateNPAForNonAccrualBasedProducts(sb);
        this.loanSchedularService.updateNPAForAccrualBasedProducts(sb);
        if (sb.length() > 0) { throw new JobExecutionException(sb.toString()); }
    }

    @Override
    @CronTarget(jobName = JobName.UPDATE_DEPOSITS_ACCOUNT_MATURITY_DETAILS)
    public void updateMaturityDetailsOfDepositAccounts() {

        final Collection<DepositAccountData> depositAccounts = this.depositAccountReadPlatformService.retrieveForMaturityUpdate();

        for (final DepositAccountData depositAccount : depositAccounts) {
            try {
                final DepositAccountType depositAccountType = DepositAccountType.fromInt(depositAccount.depositType().getId().intValue());
                this.depositAccountWritePlatformService.updateMaturityDetails(depositAccount.id(), depositAccountType);
            } catch (final PlatformApiDataValidationException e) {
                final List<ApiParameterError> errors = e.getErrors();
                for (final ApiParameterError error : errors) {
                    logger.error("Update maturity details failed for account:" + depositAccount.accountNo() + " with message "
                            + error.getDeveloperMessage());
                }
            } catch (final Exception ex) {
                // need to handle this scenario
            }
        }

        logger.info(ThreadLocalContextUtil.getTenant().getName() + ": Deposit accounts affected by update: " + depositAccounts.size());
    }

    @Override
    @CronTarget(jobName = JobName.GENERATE_RD_SCEHDULE)
    public void generateRDSchedule() {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());
        final Collection<Map<String, Object>> scheduleDetails = this.depositAccountReadPlatformService.retriveDataForRDScheduleCreation();
        final String insertSql = "INSERT INTO `m_mandatory_savings_schedule` (`savings_account_id`, `actualduedate`, `duedate`, `installment`, `deposit_amount`, `completed_derived`, `created_date`, `lastmodified_date`) VALUES ";
        StringBuilder sb = new StringBuilder();
        final String currentDate = this.formatterWithTime.print(DateUtils.getLocalDateTimeOfTenant());
        int iterations = 0;
        for (final Map<String, Object> details : scheduleDetails) {
            Long count = (Long) details.get("futureInstallemts");
            if (count == null) {
                count = 0l;
            }
            final Long savingsId = (Long) details.get("savingsId");
            Long officeId = (Long) details.get("clientOfficeId");
            if (officeId == null) {
                officeId = (Long) details.get("groupOfficeId");
            }
            final BigDecimal amount = (BigDecimal) details.get("amount");
            final String recurrence = (String) details.get("recurrence");
            final CalendarFrequencyType calendarFrequencyType = CalendarUtils.getFrequency(recurrence);
            final PeriodFrequencyType frequency = CalendarFrequencyType.from(calendarFrequencyType);
            final Integer recurringEvery = CalendarUtils.getInterval(recurrence);
            final Date actualDueDate = (Date) details.get("actualDueDate");
            LocalDate lastDepositDate = new LocalDate(actualDueDate);
            final List<Holiday> holidays = this.holidayRepository.findByOfficeIdAndGreaterThanDate(officeId, lastDepositDate.toDate());
            final HolidayDetailDTO holidayDetailDTO = this.loanUtilService.constructHolidayDTO(holidays);
            Integer installmentNumber = (Integer) details.get("installment");
            while (count < ScheduleDateGeneratorUtil.GENERATE_MINIMUM_NUMBER_OF_FUTURE_INSTALMENTS) {
                count++;
                installmentNumber++;
                lastDepositDate = ScheduleDateGeneratorUtil.generateNextScheduleDate(lastDepositDate, recurrence);
                final AdjustedDateDetailsDTO adjustedDateDetailsDTO = new AdjustedDateDetailsDTO(lastDepositDate, lastDepositDate,
                        lastDepositDate);
                final CalendarData calendarData = null;
                WorkingDaysAndHolidaysUtil.adjustInstallmentDateBasedOnWorkingDaysAndHolidays(adjustedDateDetailsDTO, holidayDetailDTO,
                        frequency, recurringEvery, calendarData);
                lastDepositDate = adjustedDateDetailsDTO.getChangedActualRepaymentDate();
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append("(");
                sb.append(savingsId);
                sb.append(",'");
                sb.append(this.formatter.print(adjustedDateDetailsDTO.getChangedActualRepaymentDate()));
                sb.append("'");
                sb.append(",'");
                sb.append(this.formatter.print(adjustedDateDetailsDTO.getChangedScheduleDate()));
                sb.append("',");
                sb.append(installmentNumber);
                sb.append(",");
                sb.append(amount);
                sb.append(", b'0','");
                sb.append(currentDate);
                sb.append("','");
                sb.append(currentDate);
                sb.append("')");
                iterations++;
                if (iterations > 200) {
                    jdbcTemplate.update(insertSql + sb.toString());
                    sb = new StringBuilder();
                }
            }
        }
        if (sb.length() > 0) {
            jdbcTemplate.update(insertSql + sb.toString());
        }
    }

    @Override
    @CronTarget(jobName = JobName.APPY_SAVING_DEPOSITE_LATE_FEE)
    public void doAppySavingLateFeeCharge() throws JobExecutionException {
        final PeriodFrequencyType frequencyType = null;

        String startingDate = new String();
        final SimpleDateFormat formateDate = new SimpleDateFormat("yyyy-MM-dd");

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

        final Collection<ChargeData> chargeData = this.chargeReadPlatformService.retriveAllChargeOfSavingLateFee();
        /**
         * above method retrun's all available charges for the saving late
         * deposit fee
         */

        for (final ChargeData oneCharge : chargeData) {
            final int interval = oneCharge.getFeeInterval();
            final int month = PeriodFrequencyType.MONTHS.getValue();

            final EnumOptionData data = oneCharge.getFeeFrequency();
            final Long frequency = data.getId();
            final Calendar aCalendar = Calendar.getInstance();

            if (month == frequency) {

                // added -interval for any previous month
                aCalendar.add(Calendar.MONTH, -interval);
                aCalendar.set(Calendar.DATE, 1);
                final Date startDate = aCalendar.getTime();
                startingDate = formateDate.format(startDate);
            }

            final Collection<SavingIdListData> savingIdList = this.savingsAccountChargeReadPlatformService
                    .retriveAllSavingIdForApplyDepositeLateCharge();
            final Collection<SavingsIdOfChargeData> savingIdsInCharge = this.savingsAccountChargeReadPlatformService
                    .retriveAllSavingIdHavingDepositCharge(startingDate);
            final Collection<SavingIdListData> savingIdsFromTransaction = this.savingsAccountChargeReadPlatformService
                    .retriveSavingAccountForApplySavingDepositeFee(startingDate);

            for (final SavingIdListData savingId : savingIdList) {

                final Long savingIdForGetMaxOfTxn = savingId.getSavingId();

                DateTime start = new DateTime();
                Date trasactionDate = new Date();
                Date startFeeCharge = new Date();
                LocalDate maxOfTransactionDate = new LocalDate();
                LocalDate maxOfchargeDueDate = new LocalDate();
                LocalDate startChargeDate = new LocalDate();
                boolean isInsert = true;
                boolean isValideForCharge = false;
                boolean isMaxOfChargeDue = false;
                boolean isPreviousTxn = false;
                boolean isAllowInsert = false;
                boolean isPriviousDueDate = false;
                boolean isSavingIdAvailable = false;
                int totalNoOfInsertion = 0;

                /**
                 * Following code will check if the start date of charge
                 * calculation is there or is saving account is active then it
                 * will return valid for apply charge
                 */

                if (savingId.getStartFeeChargeDate() != null) {

                    if (savingId.getStartFeeChargeDate().isAfter(savingId.getActivateOnDate())
                            || savingId.getStartFeeChargeDate().equals(savingId.getActivateOnDate())) {
                        isValideForCharge = true;
                    }
                } else if (savingId.getActivateOnDate() != null) {
                    isValideForCharge = true;
                    startFeeCharge = savingId.getActivateOnDate().toDate();
                }

                /**
                 * Following code will return the boolean value true if there is
                 * any previous charge on on saving id
                 */

                final SavingsIdOfChargeData maxOfChargeDueDate = this.savingsAccountChargeReadPlatformService
                        .retriveOneWithMaxOfDueDate(savingIdForGetMaxOfTxn);
                if (maxOfChargeDueDate.getDueDate() != null) {
                    maxOfchargeDueDate = new LocalDate(maxOfChargeDueDate.getDueDate());
                    isMaxOfChargeDue = true;
                    isPriviousDueDate = true;
                }

                /**
                 * if any savingAccount already having charge then isInsert
                 * become false else it will be true and it will allow to insert
                 * data
                 **/

                for (final SavingsIdOfChargeData savingData : savingIdsInCharge) {
                    if (savingId.getSavingId().equals(savingData.getSavingId())) {
                        isInsert = false;
                        break;
                    }
                }

                /**
                 * It checks the last transaction of saving Id is not in
                 * previous month those saving Id only eligible to apply charge
                 */

                for (final SavingIdListData savingIdListData : savingIdsFromTransaction) {
                    if (savingId.getSavingId().equals(savingIdListData.getSavingId())) {
                        isAllowInsert = true;
                    }
                }

                /**
                 * Following loop condition it will just adjust the dates based
                 * on some condition for the charge calculation
                 */

                for (final SavingIdListData savingIdListData : savingIdsFromTransaction) {

                    if (savingIdListData.getSavingId().equals(savingId.getSavingId())) {
                        isSavingIdAvailable = true;

                        final LocalDate dateOfTransaction = savingIdListData.getMaxTransactionDate();
                        if (dateOfTransaction.isAfter(maxOfchargeDueDate) || dateOfTransaction.equals(maxOfChargeDueDate)
                                || isMaxOfChargeDue == false) {
                            trasactionDate = dateOfTransaction.toDate();
                            maxOfTransactionDate = dateOfTransaction;
                            isPreviousTxn = true;

                        } else {
                            trasactionDate = maxOfchargeDueDate.toDate();
                            maxOfTransactionDate = maxOfchargeDueDate;
                            isPreviousTxn = true;
                        }

                        LocalDate startFeeChargeDate = savingId.getStartFeeChargeDate();
                        if (startFeeChargeDate == null) {
                            startFeeChargeDate = savingId.getActivateOnDate();
                        }

                        if (isMaxOfChargeDue == true) {
                            if (dateOfTransaction.isAfter(maxOfchargeDueDate) || dateOfTransaction.equals(maxOfChargeDueDate)) {
                                startFeeCharge = dateOfTransaction.toDate();
                                startChargeDate = dateOfTransaction;
                            } else {
                                startFeeCharge = maxOfchargeDueDate.toDate();
                                startChargeDate = maxOfchargeDueDate;
                            }
                        } else if (dateOfTransaction.isAfter(startFeeChargeDate)) {
                            startFeeCharge = dateOfTransaction.toDate();
                            startChargeDate = dateOfTransaction;
                        } else {
                            startFeeCharge = startFeeChargeDate.toDate();
                            startChargeDate = startFeeChargeDate;
                        }

                        break;
                    }

                }

                /**
                 * if there is no any single transaction of saving account then
                 * start date and number of insertion calculation done here
                 */

                final SavingIdListData maxTransactionDate = this.savingsAccountChargeReadPlatformService
                        .retriveMaxOfTransaction(savingIdForGetMaxOfTxn);
                if (maxTransactionDate.getMaxTransactionDate() == null) {
                    isAllowInsert = true;
                }

                if (isSavingIdAvailable == false && isInsert == true && isValideForCharge == true) {

                    LocalDate startFeeChargeDate = savingId.getStartFeeChargeDate();
                    if (startFeeChargeDate == null) {
                        startFeeChargeDate = savingId.getActivateOnDate();
                    }

                    if (maxOfchargeDueDate.isAfter(startFeeChargeDate) && isMaxOfChargeDue == true) {
                        startFeeCharge = maxOfchargeDueDate.toDate();
                    } else {
                        startFeeCharge = startFeeChargeDate.toDate();
                    }

                    final DateTime startFee = new DateTime(startFeeCharge);
                    start = new DateTime(startFee);
                    final Date endDateAsCurrDate = new Date();
                    final DateTime end = new DateTime(endDateAsCurrDate);

                    if (isMaxOfChargeDue == true) {
                        trasactionDate = maxOfchargeDueDate.toDate();
                        maxOfTransactionDate = maxOfchargeDueDate;
                        isPriviousDueDate = true;
                    } else {
                        trasactionDate = startFeeChargeDate.toDate();
                        maxOfTransactionDate = startFeeChargeDate;
                        isPriviousDueDate = false;
                    }

                    final Months diffMonth = Months.monthsBetween(start, end);
                    totalNoOfInsertion = (diffMonth.getMonths()) / interval;
                }

                /**
                 * If there is previous transaction of saving Id then here it
                 * will check how many charges will need be applied
                 *
                 */

                if (isSavingIdAvailable == true && isInsert == true && isValideForCharge == true) {

                    final DateTime transaction = new DateTime(trasactionDate);

                    final DateTime startFee = new DateTime(startFeeCharge);

                    final LocalDate startCharge = new LocalDate(startFeeCharge);
                    final Date endDateAsCurrDate = new Date();
                    final DateTime end = new DateTime(endDateAsCurrDate);

                    if (maxOfTransactionDate.isAfter(startCharge)
                            || maxOfTransactionDate.isEqual(startCharge) && isMaxOfChargeDue == true) {
                        start = new DateTime(transaction);
                        final Months diffMonth = Months.monthsBetween(start, end);
                        totalNoOfInsertion = (diffMonth.getMonths() - 1) / interval;

                    } else if (maxOfTransactionDate.isEqual(startCharge) && isMaxOfChargeDue == false) {
                        start = new DateTime(transaction);
                        final Months diffMonth = Months.monthsBetween(startFee, end);
                        totalNoOfInsertion = (diffMonth.getMonths() - 1) / interval;
                    } else if (maxOfTransactionDate.isAfter(maxOfchargeDueDate)
                            || maxOfTransactionDate.isEqual(maxOfchargeDueDate) && isMaxOfChargeDue == true) {

                        start = new DateTime(transaction);
                        final Months diffMonth = Months.monthsBetween(start, end);
                        totalNoOfInsertion = (diffMonth.getMonths()) / interval;

                    }

                }

                /**
                 * If all boolean values are true then it will insert the charge
                 * into the m_savings_account_charge
                 */

                if (isInsert == true && isValideForCharge == true && isAllowInsert == true) {

                    for (int i = 0; i < totalNoOfInsertion; i++) {

                        final String insertSql = " INSERT INTO `m_savings_account_charge` (`savings_account_id`, `charge_id`, `is_penalty`, `charge_time_enum`, "
                                + " `charge_due_date`, `fee_on_month`, `fee_on_day`, `fee_interval`, `charge_calculation_enum`, `calculation_percentage`, "
                                + " `calculation_on_amount`, `amount`, `amount_paid_derived`, `amount_waived_derived`, `amount_writtenoff_derived`, "
                                + " `amount_outstanding_derived`, `is_paid_derived`, `waived`, `is_active`, `inactivated_on_date`) VALUES ";

                        final StringBuilder sb = new StringBuilder();
                        final Long savingAccId = savingId.getSavingId();
                        final Long chargId = oneCharge.getId();
                        final BigDecimal amount = oneCharge.getAmount();
                        LocalDate chargeDueDate = new LocalDate();
                        if (i == 0) {
                            if (isPriviousDueDate == true) {

                                if (maxOfTransactionDate.isAfter(maxOfchargeDueDate) || isMaxOfChargeDue == false) {
                                    aCalendar.setTime(trasactionDate);

                                    aCalendar.add(Calendar.MONTH, interval + 1);
                                    aCalendar.set(Calendar.DATE, aCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));

                                    final Date nextMonthFirstDay = aCalendar.getTime();
                                    aCalendar.setTime(nextMonthFirstDay);
                                    chargeDueDate = new LocalDate(nextMonthFirstDay);

                                } else if (maxOfchargeDueDate.isAfter(maxOfTransactionDate) && isMaxOfChargeDue == true
                                        || maxOfchargeDueDate.equals(maxOfTransactionDate)) {

                                    Date chargeDue = new Date();
                                    chargeDue = maxOfchargeDueDate.toDate();
                                    aCalendar.setTime(chargeDue);
                                    aCalendar.add(Calendar.MONTH, interval);
                                    aCalendar.set(Calendar.DATE, aCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));

                                    final Date nextMonthFirstDay = aCalendar.getTime();
                                    aCalendar.setTime(nextMonthFirstDay);
                                    chargeDueDate = new LocalDate(nextMonthFirstDay);
                                }

                            }

                            // in this if there is no any previous due date then
                            // calendar is going to set on available date

                            else {

                                if (isPreviousTxn == true) {
                                    aCalendar.setTime(startFeeCharge);

                                    aCalendar.add(Calendar.MONTH, interval + 1);
                                    aCalendar.set(Calendar.DATE, aCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                                    final Date nextMonthFirstDay = aCalendar.getTime();
                                    aCalendar.setTime(nextMonthFirstDay);
                                    chargeDueDate = new LocalDate(nextMonthFirstDay);
                                }

                                else {

                                    aCalendar.setTime(startFeeCharge);

                                    aCalendar.add(Calendar.MONTH, interval);
                                    aCalendar.set(Calendar.DATE, aCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                                    final Date nextMonthFirstDay = aCalendar.getTime();
                                    aCalendar.setTime(nextMonthFirstDay);
                                    chargeDueDate = new LocalDate(nextMonthFirstDay);

                                }

                            }
                        }

                        else {

                            aCalendar.add(Calendar.MONTH, interval);
                            aCalendar.set(Calendar.DATE, aCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                            final Date nextMonthFirstDay = aCalendar.getTime();
                            aCalendar.setTime(nextMonthFirstDay);
                            chargeDueDate = new LocalDate(nextMonthFirstDay);

                        }
                        sb.append("(");
                        sb.append(savingAccId);
                        sb.append(",");
                        sb.append(chargId);
                        sb.append(",");
                        sb.append("1");
                        sb.append(",");
                        sb.append("12");
                        sb.append(",'");
                        sb.append(this.formatter.print(chargeDueDate));
                        sb.append("',");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append("1");
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append(amount);
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(",");
                        sb.append(amount);
                        sb.append(",");
                        sb.append("0");
                        sb.append(",");
                        sb.append("0");
                        sb.append(",");
                        sb.append("1");
                        sb.append(",");
                        sb.append("NULL");
                        sb.append(")");

                        if (sb.length() > 0) {
                            jdbcTemplate.update(insertSql + sb.toString());
                        }

                    }
                }
                // //
            }

        }

    }

    @Override
    @CronTarget(jobName = JobName.LOAN_REPAYMENT_SMS_REMINDER_TO_CLIENT)
    public void loanRepaymentSmsReminder() {
        final String payLoadUrl = "http://54.72.21.49:9191/modules/sms";
        final String apikey = this.hookRepository.retriveApiKey();
        final String tenantIdentifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(payLoadUrl);
        httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
        httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
        httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("X-Mifos-API-Key", apikey);
        StringEntity entity;
        try {
            final Date now = new Date();
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            final String date = df.format(now);
            final JSONObject jsonObj = new JSONObject("{\"reportName\":\"Loan Repayment Reminders\",\"date\":\"" + date + "\"}");
            entity = new StringEntity(jsonObj.toString());
            httpPost.setEntity(entity);
            httpClient.execute(httpPost);
        } catch (final UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final ClientProtocolException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final JSONException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    @CronTarget(jobName = JobName.LOAN_FIRST_OVERDUE_REPAYMENT_REMINDER_SMS)
    public void loanFirstOverdueRepaymentReminder() {
        final String payLoadUrl = "http://54.72.21.49:9191/modules/sms";
        final String apikey = this.hookRepository.retriveApiKey();
        final String tenantIdentifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(payLoadUrl);
        httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
        httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
        httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("X-Mifos-API-Key", apikey);
        StringEntity entity;
        try {
            final Date now = new Date();
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            final String date = df.format(now);
            final JSONObject jsonObj = new JSONObject(
                    "{\"reportName\":\"Loan First Overdue Repayment Reminder\",\"date\":\"" + date + "\"}");
            entity = new StringEntity(jsonObj.toString());
            httpPost.setEntity(entity);
            httpClient.execute(httpPost);
        } catch (final UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final ClientProtocolException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final JSONException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    @CronTarget(jobName = JobName.LOAN_SECOND_OVERDUE_REPAYMENT_REMINDER_SMS)
    public void loanSecondOverdueRepaymentReminder() {
        final String payLoadUrl = "http://54.72.21.49:9191/modules/sms";
        final String apikey = this.hookRepository.retriveApiKey();
        final String tenantIdentifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(payLoadUrl);
        httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
        httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
        httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("X-Mifos-API-Key", apikey);
        StringEntity entity;
        try {
            final Date now = new Date();
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            final String date = df.format(now);

            final JSONObject jsonObj = new JSONObject(
                    "{\"reportName\":\"Loan Second Overdue Repayment Reminder\",\"date\":\"" + date + "\"}");
            entity = new StringEntity(jsonObj.toString());
            httpPost.setEntity(entity);
            httpClient.execute(httpPost);
        } catch (final UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final ClientProtocolException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final JSONException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    @CronTarget(jobName = JobName.LOAN_THIRD_OVERDUE_REPAYMENT_REMINDER_SMS)
    public void loanThirdOverdueRepaymentReminder() {
        final String payLoadUrl = "http://54.72.21.49:9191/modules/sms";
        final String apikey = this.hookRepository.retriveApiKey();
        final String tenantIdentifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(payLoadUrl);
        httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
        httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
        httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("X-Mifos-API-Key", apikey);
        StringEntity entity;
        try {
            final Date now = new Date();
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            final String date = df.format(now);
            final JSONObject jsonObj = new JSONObject(
                    "{\"reportName\":\"Loan Third Overdue Repayment Reminder\",\"date\":\"" + date + "\"}");
            entity = new StringEntity(jsonObj.toString());
            httpPost.setEntity(entity);
            httpClient.execute(httpPost);
        } catch (final UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final ClientProtocolException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final JSONException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    @CronTarget(jobName = JobName.LOAN_FOURTH_OVERDUE_REPAYMENT_REMINDER_SMS)
    public void loanFourthOverdueRepaymentReminder() {
        final String payLoadUrl = "http://54.72.21.49:9191/modules/sms";
        final String apikey = this.hookRepository.retriveApiKey();
        final String tenantIdentifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(payLoadUrl);
        httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
        httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
        httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("X-Mifos-API-Key", apikey);
        StringEntity entity;
        try {
            final Date now = new Date();
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            final String date = df.format(now);
            final JSONObject jsonObj = new JSONObject(
                    "{\"reportName\":\"Loan Fourth Overdue Repayment Reminder\",\"date\":\"" + date + "\"}");
            entity = new StringEntity(jsonObj.toString());
            httpPost.setEntity(entity);
            httpClient.execute(httpPost);
        } catch (final UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final ClientProtocolException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final JSONException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    @CronTarget(jobName = JobName.DEFAULT_WARNING_SMS_TO_CLIENT)
    public void defaultWarningToClients() {
        final String payLoadUrl = "http://54.72.21.49:9191/modules/sms";
        final String apikey = this.hookRepository.retriveApiKey();
        final String tenantIdentifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(payLoadUrl);
        httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
        httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
        httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("X-Mifos-API-Key", apikey);
        StringEntity entity;
        try {
            final Date now = new Date();
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            final String date = df.format(now);
            final JSONObject jsonObj = new JSONObject("{\"reportName\":\"DefaultWarning - Clients\",\"date\":\"" + date + "\"}");
            entity = new StringEntity(jsonObj.toString());
            httpPost.setEntity(entity);
            httpClient.execute(httpPost);
        } catch (final UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final ClientProtocolException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final JSONException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    @CronTarget(jobName = JobName.DEFAULT_WARNING_SMS_TO_GURANTOR)
    public void defaultWarningToGuarantors() {
        final String payLoadUrl = "http://54.72.21.49:9191/modules/sms";
        final String apikey = this.hookRepository.retriveApiKey();
        final String tenantIdentifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(payLoadUrl);
        httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
        httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
        httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("X-Mifos-API-Key", apikey);
        StringEntity entity;
        try {
            final Date now = new Date();
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            final String date = df.format(now);
            final JSONObject jsonObj = new JSONObject("{\"reportName\":\"DefaultWarning -  guarantors\",\"date\":\"" + date + "\"}");
            entity = new StringEntity(jsonObj.toString());
            httpPost.setEntity(entity);
            httpClient.execute(httpPost);
        } catch (final UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final ClientProtocolException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final JSONException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    @CronTarget(jobName = JobName.DORMANCY_WARNING_SMS_TO_CLIENT)
    public void dormancyWarningToClients() {
        final String payLoadUrl = "http://54.72.21.49:9191/modules/sms";
        final String apikey = this.hookRepository.retriveApiKey();
        final String tenantIdentifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(payLoadUrl);
        httpPost.addHeader("X-Mifos-Action", "EXECUTEJOB");
        httpPost.addHeader("X-Mifos-Entity", "SCHEDULER");
        httpPost.addHeader("X-Mifos-Platform-TenantId", tenantIdentifier);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("X-Mifos-API-Key", apikey);
        StringEntity entity;
        try {
            final Date now = new Date();
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            final String date = df.format(now);
            final JSONObject jsonObj = new JSONObject("{\"reportName\":\"DormancyWarning - Clients\",\"date\":\"" + date + "\"}");
            entity = new StringEntity(jsonObj.toString());
            httpPost.setEntity(entity);
            httpClient.execute(httpPost);
        } catch (final UnsupportedEncodingException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final ClientProtocolException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (final JSONException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    @CronTarget(jobName = JobName.DO_INVESTMENT_DISTRIBUTION)
    public void distributeInvestmentEarning() {

        /*
         * this will call the distributeInvestment() method with passing null
         * values so it will work for all the data not for specific one
         *
         */
        final String[] productId = null;
        final String date = "";
        final String investmentId = "";

        distributeInvestment(productId, date, investmentId);

    }

    @Override
    @Transactional
    /**
     * the following method will call if user enter the manual data to run the
     * batch job
     */
    public CommandProcessingResult doInvestmentTracker(final JsonCommand command) {
        CommandProcessingResult result = null;
        final String[] productIds = command.arrayValueOfParameterNamed("productId");
        final String date = command.stringValueOfParameterNamed("date");
        final String investmentId = command.stringValueOfParameterNamed("investmentId");

        result = distributeInvestment(productIds, date, investmentId);
        if (result == null) { throw new NoAnyInvestmentFoundForDistributionException(); }
        return result;
    }

    /**
     * the following method is the one which is responsible for distribute the
     * investment and the same method we are calling for batch as well
     */

    public CommandProcessingResult distributeInvestment(final String[] productIds, final String date, final String investmentId) {

        final String distributionDate = date;
        final List<Long> investmentIdFromData = new ArrayList<>();
        CommandProcessingResult result = null;
        final Date today = new Date();
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        final DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMMM yyyy");
        final String statusDate = df.format(today);
        final StringBuilder sb = new StringBuilder();

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

        /**
         * In this code if any one of parameter is passed by user then following
         * method will return the specific result. In case user doesn't pass any
         * parameter then all data will be selected like all product and all
         * investmentId and date will be the current date
         **/

        final List<InvestmentBatchJobData> data = this.investmentBatchJobReadPlatformService.validateForInvestmentSplit(productIds,
                distributionDate, investmentId);
        if (data.isEmpty()) { throw new NoAnyInvestmentFoundForDistributionException(); }
        final List<Long> investmentIds = insertInvestmentStatus(data, statusDate);

        /**
         * Following method return's the investment data which having a matured
         * status and if we pass the investmentId then it will return the
         * specific record else it will return the all the data with matured
         * status
         */

        final InvestmentBatchJobData interestDetails = this.investmentBatchJobReadPlatformService.getInterestDetails();

        final BigDecimal groupPercentage = interestDetails.getGroupPercentage();
        final BigDecimal caritasPercentage = interestDetails.getCaritasPercentage();

        for (final Long loanId : investmentIds) {

            final List<InvestmentBatchJobData> maturedInvestmentData = this.investmentBatchJobReadPlatformService
                    .getAllInvestementDataWithMaturedStatus(loanId);
            for (final InvestmentBatchJobData investmentBatchJobData : maturedInvestmentData) {

                final Long savingId = investmentBatchJobData.getSavingId();
                final Long officeId = this.investmentBatchJobReadPlatformService.getOfficeIdOfSavingAccount(savingId);

                final Date investmentStartDate = investmentBatchJobData.getInvestmentStartDate();
                final Date investmentCloseDate = investmentBatchJobData.getInvestmentCloseDate();
                final BigDecimal investedAmountByGroup = investmentBatchJobData.getInvestmetAmount().setScale(5);
                final InvestmentBatchJobData loanData = this.investmentBatchJobReadPlatformService.getLoanClosedDate(loanId);
                final Date loanCloseOn = loanData.getLoanCloseDate();

                final LocalDate investmentStart = new LocalDate(investmentStartDate);
                final LocalDate investmentClose = new LocalDate(investmentCloseDate);
                final LocalDate loanClose = new LocalDate(loanCloseOn);
                final LocalDate transactionDate = new LocalDate();
                final StringBuilder dB = new StringBuilder();

                final DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                final String interestPostingDate = dateFormat.format(transactionDate.toDate());
                final String postingDateOfInvestment = df.format(transactionDate.toDate());

                // Calculate the total charge here itself
                final InvestmentBatchJobData getTotalLoanCharge = this.investmentBatchJobReadPlatformService
                        .getTotalLoanChargeAmount(loanId);
                final BigDecimal sumOfLoanChargeAmount = getTotalLoanCharge.getSumOfLoanCharge();

                final BigDecimal interestAmountOfLoan = loanData.getTotalInterest();

                // New Total Earning = total earning - total Charge
                final BigDecimal interestAmountAfterRemovedLoanCharges = interestAmountOfLoan.subtract(sumOfLoanChargeAmount);

                final BigDecimal totalInvestedAmount = loanData.getTotalInvestedAmount();

                final Date loanStartDate = loanData.getLoanStartDate();
                final LocalDate loanStart = new LocalDate(loanStartDate);

                LocalDate startInvestment = new LocalDate();
                LocalDate closeInvestment = new LocalDate();
                // if investment is not close until loan matured then at time of
                // investment distribution it will get close and update
                // min-enforce balace for that saving account.

                LocalDate closingOfInvestmentOnLoanMature = new LocalDate();

                int dayDiff = 0;
                if (!(investmentCloseDate == null)) {
                    if (investmentStart.isBefore(loanStart)) {
                        dayDiff = Days.daysBetween(loanStart, investmentClose).getDays();
                        startInvestment = loanStart;
                        closeInvestment = investmentClose;
                        closingOfInvestmentOnLoanMature = null;
                    } else {
                        dayDiff = Days.daysBetween(investmentStart, investmentClose).getDays();
                        startInvestment = investmentStart;
                        closeInvestment = investmentClose;
                        closingOfInvestmentOnLoanMature = null;
                    }

                } else {
                    if (investmentStart.isBefore(loanStart)) {
                        dayDiff = Days.daysBetween(loanStart, loanClose).getDays();
                        startInvestment = loanStart;
                        closeInvestment = loanClose;
                        closingOfInvestmentOnLoanMature = closeInvestment;
                    } else {
                        dayDiff = Days.daysBetween(investmentStart, loanClose).getDays();
                        startInvestment = investmentStart;
                        closeInvestment = loanClose;
                        closingOfInvestmentOnLoanMature = closeInvestment;
                    }
                }

                final BigDecimal numberOfDaysOfInvestment = new BigDecimal(dayDiff);

                final int totalDayDiff = Days.daysBetween(loanStart, loanClose).getDays();
                final BigDecimal totalNumberOfInvestment = new BigDecimal(totalDayDiff);
                final BigDecimal bigDecimalHundred = new BigDecimal(100);

                final BigDecimal ratioOfInvestmentAmount = investedAmountByGroup.divide(totalInvestedAmount, 10, RoundingMode.HALF_EVEN);
                final BigDecimal ratioOfDaysInvested = numberOfDaysOfInvestment.divide(totalNumberOfInvestment, 10, RoundingMode.HALF_EVEN);
                // BigDecimal interestEarn =
                // ratioOfInvestmentAmount.multiply(ratioOfDaysInvested).multiply(interestAmountAfterRemovedLoanCharges).setScale(05,
                // RoundingMode.HALF_EVEN);

                // BigDecimal transactionAmount =
                // interestEarn.multiply((groupPercentage.divide(bigDecimalHundred))).setScale(05,
                // RoundingMode.HALF_EVEN);

                // following for the group investment charge deduction = group
                // amount / total investment amount * charge amount

                final BigDecimal chargeAmountOfGroup = ratioOfInvestmentAmount.multiply(sumOfLoanChargeAmount);

                final BigDecimal totalInterestEarnFromIvestment = ratioOfInvestmentAmount.multiply(ratioOfDaysInvested)
                        .multiply(interestAmountOfLoan).setScale(05, RoundingMode.HALF_EVEN);
                final BigDecimal interestEarnAmountAfterChagreDeduction = totalInterestEarnFromIvestment.subtract(chargeAmountOfGroup);

                final BigDecimal groupInterestEarn = interestEarnAmountAfterChagreDeduction
                        .multiply((groupPercentage.divide(bigDecimalHundred))).setScale(05, RoundingMode.HALF_EVEN);
                BigDecimal caritasInterestEarn = BigDecimal.ZERO;

                caritasInterestEarn = interestEarnAmountAfterChagreDeduction
                        .multiply((caritasPercentage.divide(bigDecimalHundred)).setScale(05, RoundingMode.HALF_EVEN));

                // following code run if group invested money for limited time
                // e.g total investment is 91 days and group investment for 45
                // days.
                if (!(numberOfDaysOfInvestment.equals(totalNumberOfInvestment))) {
                    final BigDecimal cariatasInterestEarnInInvestment = caritasInterestEarn;
                    BigDecimal caritasInterestForRemainingDays = BigDecimal.ZERO;
                    caritasInterestEarn = BigDecimal.ZERO;
                    final BigDecimal oneDayInterest = totalInterestEarnFromIvestment
                            .divide(numberOfDaysOfInvestment, 10, RoundingMode.HALF_EVEN).setScale(05, RoundingMode.HALF_EVEN);
                    final BigDecimal numberOfDayDiffInInvestment = totalNumberOfInvestment.subtract(numberOfDaysOfInvestment).setScale(05,
                            RoundingMode.HALF_EVEN);
                    caritasInterestForRemainingDays = oneDayInterest.multiply(numberOfDayDiffInInvestment).setScale(05,
                            RoundingMode.HALF_EVEN);

                    caritasInterestEarn = caritasInterestForRemainingDays.add(cariatasInterestEarnInInvestment).setScale(05,
                            RoundingMode.HALF_EVEN);

                }

                final BigDecimal transactionAmount = groupInterestEarn;

                // following the method and constructor reused for getting the
                // long value result of
                final InvestmentBatchJobData paymentData = this.investmentBatchJobReadPlatformService.getPaymentType();
                final Long paymentTypeId = paymentData.getInvestmentId();

                final StringBuilder json = new StringBuilder();
                json.append("{ ");
                json.append("transactionDate:");
                json.append('"');
                json.append(interestPostingDate);
                json.append('"');
                json.append(", transactionAmount:");
                json.append(transactionAmount);
                json.append(",");
                json.append(" paymentTypeId: ");
                json.append(paymentTypeId);
                json.append(",");
                json.append("locale:en,");
                json.append("dateFormat : ");
                json.append('"');
                json.append("dd MMMM yyyy");
                json.append('"');
                json.append(", isFromBatchJob : true ");
                json.append("}");

                final String apiJson = json.toString();

                // this method is responsible for handling the deposit amount
                // into the specific saving account
                if (transactionAmount.compareTo(BigDecimal.ZERO) > 0) {

                    result = doDepositeTransaction(savingId, apiJson);

                    final String insertSqlStmt = "INSERT INTO `ct_posted_investment_earnings` (`loan_id`, `saving_id`, `office_id`, `number_of_days`, "
                            + " `invested_amount`, `gorup_interest_rate`, `gorup_interest_earned`, `group_charge_applied` , `caritas_interest_earned`, `interest_earned`, `date_of_interest_posting`, "
                            + " `investment_start_date`, `investment_close_date`) VALUES ";

                    dB.append("( ");
                    dB.append(loanId);
                    dB.append(",");
                    dB.append(savingId);
                    dB.append(",");
                    dB.append(officeId);
                    dB.append(",");
                    dB.append(dayDiff);
                    dB.append(",");
                    dB.append(investedAmountByGroup);
                    dB.append(",");
                    dB.append(groupPercentage);
                    dB.append(",");
                    dB.append(transactionAmount);
                    dB.append(",");
                    dB.append(chargeAmountOfGroup);
                    dB.append(",");
                    dB.append(caritasInterestEarn);
                    dB.append(",");
                    dB.append(totalInterestEarnFromIvestment);
                    dB.append(",'");
                    dB.append(postingDateOfInvestment);
                    dB.append("','");
                    dB.append(startInvestment);
                    dB.append("','");
                    dB.append(closeInvestment);
                    dB.append("')");

                    if (dB.length() > 0) {
                        jdbcTemplate.update(insertSqlStmt + dB.toString());
                    }

                    final String updateString = " update ct_investment_status cis set cis.earning_status = 'Distributed' ";
                    final StringBuilder update = new StringBuilder();
                    update.append(" where cis.loan_id = ");
                    update.append(loanId);
                    if (update.length() > 0) {
                        jdbcTemplate.update(updateString + update.toString());
                    }

                    if (closingOfInvestmentOnLoanMature != null) {
                        final StringBuilder updateCloseDate = new StringBuilder();
                        updateCloseDate.append(" update m_investment mi ");
                        updateCloseDate.append(" set mi.close_date =  ");
                        updateCloseDate.append("'");
                        updateCloseDate.append(closingOfInvestmentOnLoanMature);
                        updateCloseDate.append("'");
                        updateCloseDate.append(" where  mi.saving_id = ");
                        updateCloseDate.append(savingId);
                        updateCloseDate.append(" and mi.loan_id = ");
                        updateCloseDate.append(loanId);
                        updateCloseDate.append(" and mi.start_date = '");
                        updateCloseDate.append(startInvestment);
                        updateCloseDate.append("'");

                        if (update.length() > 0) {
                            jdbcTemplate.update(updateCloseDate.toString());
                        }

                        // following code for updating saving account minimum
                        // balance
                        final SavingsAccount account = this.savingAccount.findOne(savingId);
                        BigDecimal availableMinRequiredBal = account.getMinRequiredBalance();
                        if (availableMinRequiredBal == null) {
                            availableMinRequiredBal = BigDecimal.ZERO;
                        }
                        BigDecimal newMinBal = availableMinRequiredBal.subtract(investedAmountByGroup);
                        final Long minBal = newMinBal.longValue();

                        if (minBal >= 0) {
                            account.setMinRequiredBalance(newMinBal);
                            this.savingAccount.save(account);
                        } else {
                            newMinBal = null;
                            account.setMinRequiredBalance(newMinBal);
                            this.savingAccount.save(account);
                        }
                    }
                }
            }
        }

        return result;
    }

    // the following method will call when the money has to be deposited to a
    // specific account
    public CommandProcessingResult doDepositeTransaction(final Long savingId, final String apiJson) {

        CommandProcessingResult result = null;
        JsonCommand command = null;
        final JsonElement parsedCommand = this.fromApiJsonHelper.parse(apiJson);
        final CommandWrapper wrapper = new CommandWrapperBuilder().savingsAccountDeposit(savingId).withJson(apiJson).build();

        command = JsonCommand.from(apiJson, parsedCommand, this.fromApiJsonHelper, wrapper.getEntityName(), wrapper.getEntityId(),
                wrapper.getSubentityId(), wrapper.getGroupId(), wrapper.getClientId(), wrapper.getLoanId(), wrapper.getSavingsId(),
                wrapper.getTransactionId(), wrapper.getHref(), wrapper.getProductId(), wrapper.getEntityTypeId(),
                wrapper.getFormDataMultiPart());

        result = this.savingsAccountWritePlatformService.deposit(savingId, command);
        return result;
    }

    // this method for inserting the investment status is to the
    // ct_investment_status table
    @Transactional
    public List<Long> insertInvestmentStatus(final List<InvestmentBatchJobData> data, final String statusDate) {

        final List<Long> investmentId = new ArrayList<>();
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());

        for (final InvestmentBatchJobData oneElement : data) {
            final StringBuilder sb = new StringBuilder();
            final Long loanId = oneElement.getInvestmentId();
            final InvestmentBatchJobData getLoanStatus = this.investmentBatchJobReadPlatformService.getLoanIdStatus(loanId);

            final int loanStatusId = getLoanStatus.getLoanStatusId();
            final Date loanMaturityDate = getLoanStatus.getLoanMaturityDate();
            final Date investmentDistributionDate = new Date();
            final InvestmentBatchJobData earningStatus = this.investmentBatchJobReadPlatformService.getInvestmentStatus(loanId);

            if (earningStatus == null || earningStatus.getEarningStatus().isEmpty()) {

                final String insertSql = " INSERT INTO `ct_investment_status` (`loan_id`, `earning_status`, `status_date`) VALUES ";

                sb.append("( ");
                sb.append(loanId);
                sb.append(",");
                sb.append("'");
                if (loanStatusId == 600 && loanMaturityDate.before(investmentDistributionDate)) {
                    sb.append("Due For Realization");
                    investmentId.add(loanId);
                } else if (loanMaturityDate.after(investmentDistributionDate)) {
                    sb.append("Not Matured");
                } else if (loanStatusId == 300 && loanMaturityDate.before(investmentDistributionDate)) {
                    sb.append("Due For Realization");
                }
                sb.append("'");
                sb.append(",");
                sb.append("'");
                sb.append(statusDate);
                sb.append("'");
                sb.append(" )");

                final String instertIntoTableValues = insertSql + sb.toString();

                if (sb.length() > 0) {
                    jdbcTemplate.update(instertIntoTableValues);
                }

            } else {

                final String status = earningStatus.getEarningStatus();
                final StringBuilder update = new StringBuilder();
                if (loanStatusId == 600 && status.equalsIgnoreCase("Not Matured")) {

                    update.append(" update ct_investment_status cis set cis.earning_status = ");
                    update.append("'Due For Realization'");
                    update.append(" where ");
                    update.append("cis.loan_id = " + loanId);
                    investmentId.add(loanId);
                } else if (loanStatusId == 600 && status.equalsIgnoreCase("Due For Realization")) {
                    investmentId.add(loanId);
                }

                if (update.length() > 0) {
                    jdbcTemplate.update(update.toString());
                }

            }

        }

        return investmentId;

    }

    @Override
    @CronTarget(jobName = JobName.HIGHMARK_ENQUIRY)
    public void highmarkEnquiry() {

        // clientCreditRequestService.sendAndSaveClientCreditRequest();
    }

    @Override
    @CronTarget(jobName = JobName.POST_DIVIDENTS_FOR_SHARES)
    public void postDividends() throws JobExecutionException {
        final List<Map<String, Object>> dividendDetails = this.shareAccountDividendReadPlatformService
                .retriveDividendDetailsForPostDividents();
        final StringBuilder errorMsg = new StringBuilder();
        for (final Map<String, Object> dividendMap : dividendDetails) {
            final Long id = ((Long) dividendMap.get("id"));
            final Long savingsId = ((Long) dividendMap.get("savingsAccountId"));
            try {
                this.shareAccountSchedularService.postDividend(id, savingsId);
            } catch (final PlatformApiDataValidationException e) {
                final List<ApiParameterError> errors = e.getErrors();
                for (final ApiParameterError error : errors) {
                    logger.error("Post Dividends to savings failed for Divident detail Id:" + id + " and savings Id: " + savingsId
                            + " with message " + error.getDeveloperMessage());
                    errorMsg.append("Post Dividends to savings failed for Divident detail Id:").append(id).append(" and savings Id:")
                            .append(savingsId).append(" with message ").append(error.getDeveloperMessage());
                }
            } catch (final Exception e) {
                logger.error("Post Dividends to savings failed for Divident detail Id:" + id + " and savings Id: " + savingsId
                        + " with message " + e.getLocalizedMessage());
                errorMsg.append("Post Dividends to savings failed for Divident detail Id:").append(id).append(" and savings Id:")
                        .append(savingsId).append(" with message ").append(e.getLocalizedMessage());
            }
        }

        if (errorMsg.length() > 0) { throw new JobExecutionException(errorMsg.toString()); }
    }

    @Override
    @CronTarget(jobName = JobName.APPLY_RECURRING_CHARGE_ON_CLIENT)
    public void applyClientRecurringCharge() throws JobExecutionException {
        final StringBuilder sb = new StringBuilder();
        final LocalDate currentDate = DateUtils.getLocalDateOfTenant();
        final List<ClientRecurringChargeData> clientRecurringcharges = this.clientRecurringChargeReadPlatformService
                .retriveActiveRecurringChargesForJob(currentDate);
        /***
         * Get all Client recurring charges that are synced to a meeting and
         * create a Client charge for any meetings within the next 7 days
         ***/
        for (final ClientRecurringChargeData clientRecurringChargeData : clientRecurringcharges) {
            this.clientChargeWritePlatformService.applyClientRecurringCharge(currentDate, clientRecurringChargeData, sb);
        }
        if (sb.length() > 0) { throw new JobExecutionException(sb.toString()); }
    }

    @Override
    @CronTarget(jobName = JobName.INITIATE_BANK_TRANSACTION)
    public void initiateBankTransactions() throws JobExecutionException {

        final StringBuilder errorMsg = new StringBuilder();
        final MultiValueMap<Long, BankAccountTransaction> transactionMap = new LinkedMultiValueMap<>();
        final List<BankAccountTransaction> transactions = this.bankAccountTransactionRepository
                .findByStatusOrderByExternalServiceIdAsc(TransactionStatus.INITIATED.getValue());
        for (final BankAccountTransaction transaction : transactions) {
            transactionMap.add(transaction.getExternalServiceId(), transaction);
        }

        for (final Map.Entry<Long, List<BankAccountTransaction>> entry : transactionMap.entrySet()) {
            final BankTransferService bankTransferService = this.bankTransactionService.getBankTransferService(entry.getKey());

            if (bankTransferService != null && entry.getValue() != null && !entry.getValue().isEmpty()) {
                for (final BankAccountTransaction transaction : entry.getValue()) {
                    try {
                        final BankTransactionDetail txnDetail = this.bankTransactionService.getTransactionDetail(transaction.getId());
                        Long makerId = null;
                        Long checkerId = null;
                        Long approverId = null;
                        final TaskExecutionData taskExecutionData = this.taskPlatformReadService
                                .getTaskDetailsByEntityTypeAndEntityId(TaskEntityType.BANK_TRANSACTION, txnDetail.getTransactionId());
                        if (taskExecutionData != null) {
                            final TaskMakerCheckerData makerCheckerData = this.taskPlatformReadService
                                    .getMakerCheckerData(taskExecutionData.getId());
                            makerId = makerCheckerData.getMakerUserId();
                            checkerId = makerCheckerData.getCheckerUserId();
                            approverId = makerCheckerData.getApproverUserId();
                        }
                        final BankTransactionResponse response = bankTransferService.doTransaction(txnDetail.getTransactionId(),
                                transaction.getInternalReferenceId(), txnDetail.getAmount(), transaction.getReason(),
                                txnDetail.getDebiter(), txnDetail.getBeneficiary(), TransferType.fromInt(transaction.getTransferType()),
                                "" + transaction.getId(), transaction.getReason(), "" + transaction.getId(), transaction.getReason(),
                                makerId, checkerId, approverId);
                        if (!response.getSuccess()) {
                            logger.warn("Initiate Transaction failed for transaction:" + transaction.getExternalServiceId());
                        }
                        transaction.setErrorCode(response.getErrorCode());
                        transaction.setErrorMessage(response.getErrorMessage());
                        transaction.setReferenceNumber(response.getReferenceNumber());
                        transaction.setUtrNumber(response.getUtrNumber());
                        transaction.setPoNumber(response.getPoNumber());
                        if (response.getTransactionTime() != null) {
                            transaction.setTransactionDate(response.getTransactionTime().toDate());
                        }
                        transaction.setStatus(response.getTransactionStatus().getValue());
                        this.bankAccountTransactionRepository.save(transaction);
                        if (response.getValidationErrors() != null) { throw new PlatformApiDataValidationException(
                                response.getValidationErrors()); }
                    } catch (final PlatformApiDataValidationException e) {
                        logger.error("Initiation failed for transaction " + transaction.getId() + " with message " + getErrorParam(e));
                        errorMsg.append("Initiation failed for transaction Id:").append(transaction.getId()).append(getErrorParam(e));
                    } catch (final Exception e) {
                        logger.error("Initiation failed for transaction " + transaction.getId() + " with message " + e.getMessage(), e);
                        errorMsg.append("Initiation failed for transaction Id:").append(transaction.getId())
                                .append(e.getLocalizedMessage());
                    }
                }
            }

        }
        if (errorMsg.length() > 0) { throw new JobExecutionException(errorMsg.toString()); }
    }

    private String getErrorParam(final PlatformApiDataValidationException e) {
        final List<ApiParameterError> errorList = e.getErrors();
        String errorMessage = e.getDefaultUserMessage();
        for (final ApiParameterError error : errorList) {
            errorMessage = errorMessage + error.getParameterName() + ",";
        }
        return errorMessage.substring(0, errorMessage.length() - 1);

    }

    @Override
    @CronTarget(jobName = JobName.UPDATE_BANK_TRANSACTION_STATUS)
    public void updateBankTransactionsStatus() throws JobExecutionException {
        final StringBuilder errorMsg = new StringBuilder();
        final MultiValueMap<Long, BankAccountTransaction> transactionMap = new LinkedMultiValueMap<>();
        final List<BankAccountTransaction> transactions = this.bankAccountTransactionRepository
                .findByStatusOrderByExternalServiceIdAsc(TransactionStatus.PENDING.getValue());
        for (final BankAccountTransaction transaction : transactions) {
            transactionMap.add(transaction.getExternalServiceId(), transaction);
        }

        final List<BankAccountTransaction> errorTransactions = this.bankAccountTransactionRepository
                .findByStatusOrderByExternalServiceIdAsc(TransactionStatus.ERROR.getValue());
        for (final BankAccountTransaction transaction : errorTransactions) {
            transactionMap.add(transaction.getExternalServiceId(), transaction);
        }

        for (final Map.Entry<Long, List<BankAccountTransaction>> entry : transactionMap.entrySet()) {
            final BankTransferService bankTransferService = this.bankTransactionService.getBankTransferService(entry.getKey());

            if (bankTransferService != null && entry.getValue() != null && !entry.getValue().isEmpty()) {
                for (final BankAccountTransaction transaction : entry.getValue()) {
                    try {
                        Long makerId = null;
                        Long checkerId = null;
                        Long approverId = null;
                        final BankTransactionDetail txnDetail = this.bankTransactionService.getTransactionDetail(transaction.getId());
                        final TaskExecutionData taskExecutionData = this.taskPlatformReadService
                                .getTaskDetailsByEntityTypeAndEntityId(TaskEntityType.BANK_TRANSACTION, txnDetail.getTransactionId());
                        if (taskExecutionData != null) {
                            final TaskMakerCheckerData makerCheckerData = this.taskPlatformReadService
                                    .getMakerCheckerData(taskExecutionData.getId());
                            makerId = makerCheckerData.getMakerUserId();
                            checkerId = makerCheckerData.getCheckerUserId();
                            approverId = makerCheckerData.getApproverUserId();
                        }
                        final BankTransactionResponse response = bankTransferService.getTransactionStatus(transaction.getId(),
                                transaction.getInternalReferenceId(), transaction.getReferenceNumber(), makerId, checkerId, approverId);
                        if (!response.getSuccess()) {
                            logger.warn("Status update failed for transaction:" + transaction.getExternalServiceId());
                        }
                        transaction.setErrorCode(response.getErrorCode());
                        transaction.setErrorMessage(response.getErrorMessage());
                        if (transaction.getReferenceNumber() == null) {
                            transaction.setReferenceNumber(response.getReferenceNumber());
                        }
                        if (response.getUtrNumber() != null) {
                            transaction.setUtrNumber(response.getUtrNumber());
                        }
                        if (response.getPoNumber() != null) {
                            transaction.setPoNumber(response.getPoNumber());
                        }
                        if (response.getTransactionTime() != null) {
                            transaction.setTransactionDate(response.getTransactionTime().toDate());
                        }
                        transaction.setStatus(response.getTransactionStatus().getValue());
                        this.bankAccountTransactionRepository.save(transaction);
                    } catch (final Exception e) {
                        logger.error("Status update failed for transaction Id:" + transaction.getId() + " with message " + e.getMessage(),
                                e);
                        errorMsg.append("| Status update failed for transaction Id: ").append(transaction.getId()).append(" with error:")
                                .append(e.getMessage());
                    }
                }
            }

        }
        if (errorMsg.length() > 0) { throw new JobExecutionException(errorMsg.toString()); }
    }

    @Override
    @CronTarget(jobName = JobName.APPLY_HOLIDAYS)
    public void applyHolidays() throws JobExecutionException {
        final boolean isHolidayEnabled = this.configurationDomainService.isRescheduleRepaymentsOnHolidaysEnabled();
        if (!isHolidayEnabled) { return; }

        // Get all Holidays which are active/deleted and not processed
        final List<Holiday> holidays = this.holidayRepository.findUnprocessed();

        // Loop through all holidays to get the office Id's
        final Map<Long, List<Holiday>> officeIds = getMapWithEachOfficeHolidays(holidays);

        final StringBuilder sb = new StringBuilder();
        final Set<Long> failedForOffices = new HashSet<>();

        final List<Holiday> holidaysList = null;
        final HolidayDetailDTO holidayDetailDTO = this.loanUtilService.constructHolidayDTO(holidaysList);

        this.loanSchedularService.applyHolidaysToLoans(holidayDetailDTO, officeIds, failedForOffices, sb);

        this.recurringDepositSchedularService.applyHolidaysToRecurringDeposits(holidayDetailDTO, officeIds, failedForOffices, sb);

        applyHolidaysToClientRecurringCharge(holidayDetailDTO, officeIds, failedForOffices, sb);

        boolean holidayStatusChanged = false;
        for (final Holiday holiday : holidays) {
            if (failedForOffices.isEmpty()) {
                holiday.processed();
                holidayStatusChanged = true;
            } else {
                final Set<Office> offices = holiday.getOffices();
                boolean updateProcessedStatus = true;
                for (final Office office : offices) {
                    if (failedForOffices.contains(office.getId())) {
                        updateProcessedStatus = false;
                    }
                }
                if (updateProcessedStatus) {
                    holiday.processed();
                    holidayStatusChanged = true;
                }
            }
        }
        if (holidayStatusChanged) {
            this.holidayRepository.save(holidays);
        }
        if (sb.length() > 0) { throw new JobExecutionException(sb.toString()); }
    }

    private void applyHolidaysToClientRecurringCharge(HolidayDetailDTO holidayDetailDTO, final Map<Long, List<Holiday>> officeIds,
            final Set<Long> failedForOffices, final StringBuilder sb) {
        final LocalDate recalculateFrom = DateUtils.getLocalDateOfTenant();
        final List<Holiday> applicableAllHolidays = this.holidayRepository.findHolidaysFromDate(recalculateFrom);
        final Map<Long, List<Holiday>> applicableAllHolidaysWithOfficeIds = getMapWithEachOfficeHolidays(applicableAllHolidays);
        final Collection<Integer> chargeTimeTypes = new ArrayList<>(Arrays.asList(ChargeTimeType.WEEKLY_FEE.getValue(),
                ChargeTimeType.MONTHLY_FEE.getValue(), ChargeTimeType.ANNUAL_FEE.getValue()));
        for (final Map.Entry<Long, List<Holiday>> entry : officeIds.entrySet()) {
            try {
                final List<Holiday> holidays = entry.getValue();
                final List<Holiday> applicableHolidays = new ArrayList<>();
                final List<LocalDate> holidaysFromDate = new ArrayList<>();
                for (final Holiday holiday : holidays) {
                    if (!holiday.getFromDateLocalDate().isBefore(recalculateFrom)) {
                        applicableHolidays.add(holiday);
                        holidaysFromDate.add(holiday.getFromDateLocalDate());
                    }
                }
                if (!applicableHolidays.isEmpty()) {
                    for (final Map.Entry<Long, List<Holiday>> officeIdWithHolidays : applicableAllHolidaysWithOfficeIds.entrySet()) {
                        if (entry.getKey().equals(officeIdWithHolidays.getKey())) {
                            holidayDetailDTO = new HolidayDetailDTO(holidayDetailDTO, officeIdWithHolidays.getValue());
                            break;
                        }
                    }
                    final Collection<Map<String, Object>> clientRecurringChargeForProcess = this.clientRecurringChargeReadPlatformService
                            .retrieveClientRecurringChargeIdByOffice(entry.getKey(), chargeTimeTypes, recalculateFrom);
                    final Collection<ClientRecurringChargeData> clientRecurringChargeDatas = new ArrayList<>();
                    ClientRecurringChargeData clientRecurringChargeData = null;
                    Long previousClientRecurringChargeId = 0l;
                    for (final Map<String, Object> clientRecurringCharge : clientRecurringChargeForProcess) {
                        try {
                            final Long clientChargeId = (Long) clientRecurringCharge.get("clientChargeId");
                            final Long clientRecurringChargeId = (Long) clientRecurringCharge.get("clientRecurringChargeId");
                            final LocalDate actualDueDate = new LocalDate(clientRecurringCharge.get("actualDueDate"));
                            final LocalDate dueDate = new LocalDate(clientRecurringCharge.get("dueDate"));
                            final Integer chargeTimeTypeId = (Integer) clientRecurringCharge.get("chargeTimeTypeId");
                            final Boolean isSynchMeeting = (Boolean) clientRecurringCharge.get("isSynchMeeting");
                            final Integer feeInterval = (Integer) clientRecurringCharge.get("feeInterval");
                            if (!previousClientRecurringChargeId.equals(clientRecurringChargeId)) {
                                previousClientRecurringChargeId = clientRecurringChargeId;
                                final OfficeData officeData = null;
                                final LocalDate chargeDueDate = null;
                                final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(chargeTimeTypeId);
                                final Integer countOfExistingFutureInstallments = 0;
                                clientRecurringChargeData = new ClientRecurringChargeData(clientRecurringChargeId, officeData,
                                        chargeDueDate, chargeTimeType, feeInterval, isSynchMeeting, countOfExistingFutureInstallments);
                                clientRecurringChargeDatas.add(clientRecurringChargeData);
                            }
                            if (clientRecurringChargeData != null) {
                                final ClientChargeData clientChargeData = ClientChargeData.lookUp(clientChargeId, actualDueDate, dueDate);
                                clientRecurringChargeData.addClientChargeData(clientChargeData);
                            }
                        } catch (final Exception e) {

                        }
                    }
                    if (!clientRecurringChargeDatas.isEmpty()) {
                        for (final ClientRecurringChargeData clientRecurringCharge : clientRecurringChargeDatas) {
                            if (clientRecurringCharge.getClientChargeDatas() != null
                                    && !clientRecurringCharge.getClientChargeDatas().isEmpty()) {
                                for (final ClientChargeData clientChargeData : clientRecurringCharge.getClientChargeDatas()) {
                                    if (holidaysFromDate.contains(clientChargeData.getDueDate())) {
                                        this.clientChargeWritePlatformService.applyHolidaysToClientRecurringCharge(clientRecurringCharge,
                                                holidayDetailDTO, sb);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (final Exception e) {
                final String rootCause = ExceptionHelper.fetchExceptionMessage(e);
                logger.error("Apply Holidays for recurring deposit failed  with message " + rootCause);
                sb.append("Apply Holidays for recurring deposit failed with message ").append(rootCause);
                failedForOffices.add(entry.getKey());
            }

        }
    }

    private Map<Long, List<Holiday>> getMapWithEachOfficeHolidays(final List<Holiday> holidays) {
        final Map<Long, List<Holiday>> officeIds = new HashMap<>();
        for (final Holiday holiday : holidays) {
            // All offices to which holiday is applied
            final Set<Office> offices = holiday.getOffices();
            for (final Office office : offices) {
                if (officeIds.containsKey(office.getId())) {
                    officeIds.get(office.getId()).add(holiday);
                } else {
                    final List<Holiday> holidaylist = new ArrayList<>();
                    holidaylist.add(holiday);
                    officeIds.put(office.getId(), holidaylist);
                }
            }
        }
        return officeIds;
    }

    @Override
    @CronTarget(jobName = JobName.UPDATE_NEXT_RECURRING_DATE)
    public void generateNextRecurringDate() {
        final Collection<CalendarData> calendars = this.calanderReadPlatformService.retrieveAllCalendarsForNextRecurringDate();
        if (!calendars.isEmpty()) {
            for (final CalendarData calendar : calendars) {
                final String recurringRule = calendar.getRecurrence();
                final LocalDate seedDate = calendar.getStartDate();
                final LocalDate startDate = DateUtils.getLocalDateOfTenant();
                final Long calendarId = calendar.getId();
                final LocalDate nextRecurringDate = CalendarUtils.getNextRecurringDate(recurringRule, seedDate, startDate);
                this.calendarWritePlatformService.updateCalendarNextRecurringDate(calendarId, nextRecurringDate);
            }
        }
    }

}
