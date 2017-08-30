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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.exception.ExceptionHelper;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSourceServiceFactory;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
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
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.service.ChargeEnumerations;
import org.apache.fineract.portfolio.client.data.ClientChargeData;
import org.apache.fineract.portfolio.client.data.ClientRecurringChargeData;
import org.apache.fineract.portfolio.client.service.ClientChargeWritePlatformService;
import org.apache.fineract.portfolio.client.service.ClientRecurringChargeReadPlatformService;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.service.LoanSchedularService;
import org.apache.fineract.portfolio.loanaccount.service.LoanUtilService;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.data.DepositAccountData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountAnnualFeeData;
import org.apache.fineract.portfolio.savings.service.DepositAccountReadPlatformService;
import org.apache.fineract.portfolio.savings.service.DepositAccountWritePlatformService;
import org.apache.fineract.portfolio.savings.service.RecurringDepositSchedularService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountChargeReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountDividendReadPlatformService;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountSchedularService;
import org.joda.time.LocalDate;
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
            final ShareAccountDividendReadPlatformService shareAccountDividendReadPlatformService,
            final ShareAccountSchedularService shareAccountSchedularService,
            final ClientRecurringChargeReadPlatformService clientRecurringChargeReadPlatformService,
            final BankTransactionService bankTransactionService, final BankAccountTransactionRepository bankAccountTransactionRepository,
            final TaskPlatformReadService taskPlatformReadService, final ConfigurationDomainService configurationDomainService,
            final HolidayRepositoryWrapper holidayRepository, final LoanSchedularService loanSchedularService,
            final LoanUtilService loanUtilService, final RecurringDepositSchedularService recurringDepositSchedularService,
            final ClientChargeWritePlatformService clientChargeWritePlatformService,
            final CalendarReadPlatformService calanderReadPlatformService, final CalendarWritePlatformService calendarWritePlatformService) {
        this.dataSourceServiceFactory = dataSourceServiceFactory;
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
        this.savingsAccountChargeReadPlatformService = savingsAccountChargeReadPlatformService;
        this.depositAccountReadPlatformService = depositAccountReadPlatformService;
        this.depositAccountWritePlatformService = depositAccountWritePlatformService;
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
	@CronTarget(jobName = JobName.UPDATE_LOAN_SUMMARY)
	public void updateLoanSummaryDetails() {

		final JdbcTemplate jdbcTemplate = new JdbcTemplate(
				this.dataSourceServiceFactory.determineDataSourceService()
						.retrieveDataSource());

		final StringBuilder updateSqlBuilder = new StringBuilder(900);
		updateSqlBuilder.append("update m_loan ");
		updateSqlBuilder.append("join (");
		updateSqlBuilder.append("SELECT ml.id AS loanId,");
		updateSqlBuilder
				.append(" if(ml.broken_period_method_enum   = 2,  ml.principal_amount + IFNULL(ml.broken_period_interest,0),ml.principal_amount) as principal_disbursed_derived, ");
		updateSqlBuilder
				.append("SUM(IFNULL(mr.principal_completed_derived,0)) as principal_repaid_derived, ");
		updateSqlBuilder
				.append("SUM(IFNULL(mr.principal_writtenoff_derived,0)) as principal_writtenoff_derived,");
		updateSqlBuilder
				.append("SUM(IFNULL(mr.interest_amount,0)) as interest_charged_derived,");
		updateSqlBuilder
				.append("SUM(IFNULL(mr.interest_completed_derived,0)) as interest_repaid_derived,");
		updateSqlBuilder
				.append("SUM(IFNULL(mr.interest_waived_derived,0)) as interest_waived_derived,");
		updateSqlBuilder
				.append("SUM(IFNULL(mr.interest_writtenoff_derived,0)) as interest_writtenoff_derived,");
		updateSqlBuilder
				.append("SUM(IFNULL(mr.fee_charges_amount,0)) + IFNULL((select SUM(lc.amount) from  m_loan_charge lc where lc.loan_id=ml.id and lc.is_active=1 and lc.charge_time_enum=1),0) as fee_charges_charged_derived,");
		updateSqlBuilder
				.append("SUM(IFNULL(mr.fee_charges_completed_derived,0)) + IFNULL((select SUM(lc.amount_paid_derived) from  m_loan_charge lc where lc.loan_id=ml.id and lc.is_active=1 and lc.charge_time_enum=1),0) as fee_charges_repaid_derived,");
		updateSqlBuilder
				.append("SUM(IFNULL(mr.fee_charges_waived_derived,0)) as fee_charges_waived_derived,");
		updateSqlBuilder
				.append("SUM(IFNULL(mr.fee_charges_writtenoff_derived,0)) as fee_charges_writtenoff_derived,");
		updateSqlBuilder
				.append("SUM(IFNULL(mr.penalty_charges_amount,0)) as penalty_charges_charged_derived,");
		updateSqlBuilder
				.append("SUM(IFNULL(mr.penalty_charges_completed_derived,0)) as penalty_charges_repaid_derived,");
		updateSqlBuilder
				.append("SUM(IFNULL(mr.penalty_charges_waived_derived,0)) as penalty_charges_waived_derived,");
		updateSqlBuilder
				.append("SUM(IFNULL(mr.penalty_charges_writtenoff_derived,0)) as penalty_charges_writtenoff_derived ");
		updateSqlBuilder.append(" FROM m_loan ml ");
		updateSqlBuilder
				.append("INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id ");
		updateSqlBuilder.append("WHERE ml.disbursedon_date is not null ");
		updateSqlBuilder.append("GROUP BY ml.id ");
		updateSqlBuilder.append(") x on x.loanId = m_loan.id ");

		updateSqlBuilder
				.append("SET m_loan.principal_disbursed_derived = x.principal_disbursed_derived,");
		updateSqlBuilder
				.append("m_loan.principal_repaid_derived = x.principal_repaid_derived,");
		updateSqlBuilder
				.append("m_loan.principal_writtenoff_derived = x.principal_writtenoff_derived,");
		updateSqlBuilder
				.append("m_loan.principal_outstanding_derived = (x.principal_disbursed_derived - (x.principal_repaid_derived + x.principal_writtenoff_derived)),");
		updateSqlBuilder
				.append("m_loan.interest_charged_derived = x.interest_charged_derived,");
		updateSqlBuilder
				.append("m_loan.interest_repaid_derived = x.interest_repaid_derived,");
		updateSqlBuilder
				.append("m_loan.interest_waived_derived = x.interest_waived_derived,");
		updateSqlBuilder
				.append("m_loan.interest_writtenoff_derived = x.interest_writtenoff_derived,");
		updateSqlBuilder
				.append("m_loan.interest_outstanding_derived = (x.interest_charged_derived - (x.interest_repaid_derived + x.interest_waived_derived + x.interest_writtenoff_derived)),");
		updateSqlBuilder
				.append("m_loan.fee_charges_charged_derived = x.fee_charges_charged_derived,");
		updateSqlBuilder
				.append("m_loan.fee_charges_repaid_derived = x.fee_charges_repaid_derived,");
		updateSqlBuilder
				.append("m_loan.fee_charges_waived_derived = x.fee_charges_waived_derived,");
		updateSqlBuilder
				.append("m_loan.fee_charges_writtenoff_derived = x.fee_charges_writtenoff_derived,");
		updateSqlBuilder
				.append("m_loan.fee_charges_outstanding_derived = (x.fee_charges_charged_derived - (x.fee_charges_repaid_derived + x.fee_charges_waived_derived + x.fee_charges_writtenoff_derived)),");
		updateSqlBuilder
				.append("m_loan.penalty_charges_charged_derived = x.penalty_charges_charged_derived,");
		updateSqlBuilder
				.append("m_loan.penalty_charges_repaid_derived = x.penalty_charges_repaid_derived,");
		updateSqlBuilder
				.append("m_loan.penalty_charges_waived_derived = x.penalty_charges_waived_derived,");
		updateSqlBuilder
				.append("m_loan.penalty_charges_writtenoff_derived = x.penalty_charges_writtenoff_derived,");
		updateSqlBuilder
				.append("m_loan.penalty_charges_outstanding_derived = (x.penalty_charges_charged_derived - (x.penalty_charges_repaid_derived + x.penalty_charges_waived_derived + x.penalty_charges_writtenoff_derived)),");
		updateSqlBuilder
				.append("m_loan.total_expected_repayment_derived = (x.principal_disbursed_derived + x.interest_charged_derived + x.fee_charges_charged_derived + x.penalty_charges_charged_derived),");
		updateSqlBuilder
				.append("m_loan.total_repayment_derived = (x.principal_repaid_derived + x.interest_repaid_derived + x.fee_charges_repaid_derived + x.penalty_charges_repaid_derived),");
		updateSqlBuilder
				.append("m_loan.total_expected_costofloan_derived = (x.interest_charged_derived + x.fee_charges_charged_derived + x.penalty_charges_charged_derived),");
		updateSqlBuilder
				.append("m_loan.total_costofloan_derived = (x.interest_repaid_derived + x.fee_charges_repaid_derived + x.penalty_charges_repaid_derived),");
		updateSqlBuilder
				.append("m_loan.total_waived_derived = (x.interest_waived_derived + x.fee_charges_waived_derived + x.penalty_charges_waived_derived),");
		updateSqlBuilder
				.append("m_loan.total_writtenoff_derived = (x.interest_writtenoff_derived +  x.fee_charges_writtenoff_derived + x.penalty_charges_writtenoff_derived),");
		updateSqlBuilder.append("m_loan.total_outstanding_derived=");
		updateSqlBuilder
				.append(" (x.principal_disbursed_derived - (x.principal_repaid_derived + x.principal_writtenoff_derived)) + ");
		updateSqlBuilder
				.append(" (x.interest_charged_derived - (x.interest_repaid_derived + x.interest_waived_derived + x.interest_writtenoff_derived)) +");
		updateSqlBuilder
				.append(" (x.fee_charges_charged_derived - (x.fee_charges_repaid_derived + x.fee_charges_waived_derived + x.fee_charges_writtenoff_derived)) +");
		updateSqlBuilder
				.append(" (x.penalty_charges_charged_derived - (x.penalty_charges_repaid_derived + x.penalty_charges_waived_derived + x.penalty_charges_writtenoff_derived))");

		final int result = jdbcTemplate.update(updateSqlBuilder.toString());

		logger.info(ThreadLocalContextUtil.getTenant().getName()
				+ ": Results affected by update: " + result);
	}

	@Transactional
	@Override
	@CronTarget(jobName = JobName.UPDATE_LOAN_PAID_IN_ADVANCE)
	public void updateLoanPaidInAdvance() {
        String currentdate = formatter.print(DateUtils.getLocalDateOfTenant());
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(
				this.dataSourceServiceFactory.determineDataSourceService()
						.retrieveDataSource());

		jdbcTemplate.execute("truncate table m_loan_paid_in_advance");

		final StringBuilder updateSqlBuilder = new StringBuilder(900);

		updateSqlBuilder
				.append("INSERT INTO m_loan_paid_in_advance(loan_id, principal_in_advance_derived, interest_in_advance_derived, fee_charges_in_advance_derived, penalty_charges_in_advance_derived, total_in_advance_derived)");
		updateSqlBuilder.append(" select ml.id as loanId,");
		updateSqlBuilder
				.append(" SUM(ifnull(mr.principal_completed_derived, 0)) as principal_in_advance_derived,");
		updateSqlBuilder
				.append(" SUM(ifnull(mr.interest_completed_derived, 0)) as interest_in_advance_derived,");
		updateSqlBuilder
				.append(" SUM(ifnull(mr.fee_charges_completed_derived, 0)) as fee_charges_in_advance_derived,");
		updateSqlBuilder
				.append(" SUM(ifnull(mr.penalty_charges_completed_derived, 0)) as penalty_charges_in_advance_derived,");
		updateSqlBuilder
				.append(" (SUM(ifnull(mr.principal_completed_derived, 0)) + SUM(ifnull(mr.interest_completed_derived, 0)) + SUM(ifnull(mr.fee_charges_completed_derived, 0)) + SUM(ifnull(mr.penalty_charges_completed_derived, 0))) as total_in_advance_derived");
		updateSqlBuilder.append(" FROM m_loan ml ");
		updateSqlBuilder
				.append(" INNER JOIN m_loan_repayment_schedule mr on mr.loan_id = ml.id ");
		updateSqlBuilder.append(" WHERE ml.loan_status_id = 300 ");
		updateSqlBuilder.append(" and mr.duedate >= ? ");
		updateSqlBuilder.append(" GROUP BY ml.id");
		updateSqlBuilder
				.append(" HAVING (SUM(ifnull(mr.principal_completed_derived, 0)) + SUM(ifnull(mr.interest_completed_derived, 0)) +");
		updateSqlBuilder
				.append(" SUM(ifnull(mr.fee_charges_completed_derived, 0)) + SUM(ifnull(mr.penalty_charges_completed_derived, 0))) > 0.0");

		final int result = jdbcTemplate.update(updateSqlBuilder.toString(),currentdate);

		logger.info(ThreadLocalContextUtil.getTenant().getName()
				+ ": Results affected by update: " + result);
	}

	@Override
	@CronTarget(jobName = JobName.APPLY_ANNUAL_FEE_FOR_SAVINGS)
	public void applyAnnualFeeForSavings() {

		final Collection<SavingsAccountAnnualFeeData> annualFeeData = this.savingsAccountChargeReadPlatformService
				.retrieveChargesWithAnnualFeeDue();

		for (final SavingsAccountAnnualFeeData savingsAccountReference : annualFeeData) {
			try {
				this.savingsAccountWritePlatformService.applyAnnualFee(
						savingsAccountReference.getId(),
						savingsAccountReference.getAccountId());
			} catch (final PlatformApiDataValidationException e) {
				final List<ApiParameterError> errors = e.getErrors();
				for (final ApiParameterError error : errors) {
					logger.error("Apply annual fee failed for account:"
							+ savingsAccountReference.getAccountNo()
							+ " with message " + error.getDeveloperMessage());
				}
			} catch (final Exception ex) {
				// need to handle this scenario
			}
		}

		logger.info(ThreadLocalContextUtil.getTenant().getName()
				+ ": Savings accounts affected by update: "
				+ annualFeeData.size());
	}

	@Override
	@CronTarget(jobName = JobName.PAY_DUE_SAVINGS_CHARGES)
	public void applyDueChargesForSavings() throws JobExecutionException {
		final Collection<SavingsAccountAnnualFeeData> chargesDueData = this.savingsAccountChargeReadPlatformService
				.retrieveChargesWithDue();
		final StringBuilder errorMsg = new StringBuilder();

		for (final SavingsAccountAnnualFeeData savingsAccountReference : chargesDueData) {
			try {
				this.savingsAccountWritePlatformService.applyChargeDue(
						savingsAccountReference.getId(),
						savingsAccountReference.getAccountId());
			} catch (final PlatformApiDataValidationException e) {
				final List<ApiParameterError> errors = e.getErrors();
				for (final ApiParameterError error : errors) {
					logger.error("Apply Charges due for savings failed for account:"
							+ savingsAccountReference.getAccountNo()
							+ " with message " + error.getDeveloperMessage());
					errorMsg.append(
							"Apply Charges due for savings failed for account:")
							.append(savingsAccountReference.getAccountNo())
							.append(" with message ")
							.append(error.getDeveloperMessage());
				}
			}
		}

		logger.info(ThreadLocalContextUtil.getTenant().getName()
				+ ": Savings accounts affected by update: "
				+ chargesDueData.size());

		/*
		 * throw exception if any charge payment fails.
		 */
		if (errorMsg.length() > 0) {
			throw new JobExecutionException(errorMsg.toString());
		}
	}

        @Override
        @CronTarget(jobName = JobName.UPDATE_NPA)
        public void updateNPA() throws JobExecutionException {
            StringBuilder sb = new StringBuilder();
            this.loanSchedularService.updateNPAForNonAccrualBasedProducts(sb);
            this.loanSchedularService.updateNPAForAccrualBasedProducts(sb);
            if (sb.length() > 0) { throw new JobExecutionException(sb.toString()); }
        }

	@Override
	@CronTarget(jobName = JobName.UPDATE_DEPOSITS_ACCOUNT_MATURITY_DETAILS)
	public void updateMaturityDetailsOfDepositAccounts() {

		final Collection<DepositAccountData> depositAccounts = this.depositAccountReadPlatformService
				.retrieveForMaturityUpdate();

		for (final DepositAccountData depositAccount : depositAccounts) {
			try {
				final DepositAccountType depositAccountType = DepositAccountType
						.fromInt(depositAccount.depositType().getId()
								.intValue());
				this.depositAccountWritePlatformService.updateMaturityDetails(
						depositAccount.id(), depositAccountType);
			} catch (final PlatformApiDataValidationException e) {
				final List<ApiParameterError> errors = e.getErrors();
				for (final ApiParameterError error : errors) {
					logger.error("Update maturity details failed for account:"
							+ depositAccount.accountNo() + " with message "
							+ error.getDeveloperMessage());
				}
			} catch (final Exception ex) {
				// need to handle this scenario
			}
		}

		logger.info(ThreadLocalContextUtil.getTenant().getName()
				+ ": Deposit accounts affected by update: "
				+ depositAccounts.size());
	}

    @Override
    @CronTarget(jobName = JobName.GENERATE_RD_SCEHDULE)
    public void generateRDSchedule() {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource());
        final Collection<Map<String, Object>> scheduleDetails = this.depositAccountReadPlatformService.retriveDataForRDScheduleCreation();
        final String insertSql = "INSERT INTO `m_mandatory_savings_schedule` (`savings_account_id`, `actualduedate`, `duedate`, `installment`, `deposit_amount`, `completed_derived`, `created_date`, `lastmodified_date`) VALUES ";
        StringBuilder sb = new StringBuilder();
        String currentDate = formatterWithTime.print(DateUtils.getLocalDateTimeOfTenant());
        int iterations = 0;
        for (Map<String, Object> details : scheduleDetails) {
            Long count = (Long) details.get("futureInstallemts");
            if (count == null) {
                count = 0l;
            }
            final Long savingsId = (Long) details.get("savingsId");
            Long officeId = (Long) details.get("clientOfficeId");
            if(officeId == null){
                officeId = (Long) details.get("groupOfficeId");
            }
            final BigDecimal amount = (BigDecimal) details.get("amount");
            final String recurrence = (String) details.get("recurrence");
            CalendarFrequencyType calendarFrequencyType = CalendarUtils.getFrequency(recurrence);
            final PeriodFrequencyType frequency = CalendarFrequencyType.from(calendarFrequencyType);
            final Integer recurringEvery = CalendarUtils.getInterval(recurrence);
            final Date actualDueDate = (Date) details.get("actualDueDate");
            LocalDate lastDepositDate = new LocalDate(actualDueDate);
            final List<Holiday> holidays = this.holidayRepository.findByOfficeIdAndGreaterThanDate(officeId,
                    lastDepositDate.toDate());
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
                sb.append(formatter.print(adjustedDateDetailsDTO.getChangedActualRepaymentDate()));
                sb.append(",'");
                sb.append(formatter.print(adjustedDateDetailsDTO.getChangedScheduleDate()));
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
                    jdbcTemplate.update(insertSql + sb.toString(), currentDate);
                    sb = new StringBuilder();
                }
            }
        }
        if (sb.length() > 0) {
            jdbcTemplate.update(insertSql + sb.toString(), currentDate);
        }
    }

	@Override
	@CronTarget(jobName = JobName.HIGHMARK_ENQUIRY)
	public void highmarkEnquiry() {

		// clientCreditRequestService.sendAndSaveClientCreditRequest();
	}

	@Override
	@CronTarget(jobName = JobName.POST_DIVIDENTS_FOR_SHARES)
	public void postDividends() throws JobExecutionException {
		List<Map<String, Object>> dividendDetails = this.shareAccountDividendReadPlatformService
				.retriveDividendDetailsForPostDividents();
		StringBuilder errorMsg = new StringBuilder();
		for (Map<String, Object> dividendMap : dividendDetails) {
			final Long id = ((Long) dividendMap.get("id"));
			final Long savingsId = ((Long) dividendMap.get("savingsAccountId"));
			try {
				this.shareAccountSchedularService.postDividend(id, savingsId);
			} catch (final PlatformApiDataValidationException e) {
				final List<ApiParameterError> errors = e.getErrors();
				for (final ApiParameterError error : errors) {
					logger.error("Post Dividends to savings failed for Divident detail Id:"
							+ id
							+ " and savings Id: "
							+ savingsId
							+ " with message " + error.getDeveloperMessage());
					errorMsg.append(
							"Post Dividends to savings failed for Divident detail Id:")
							.append(id).append(" and savings Id:")
							.append(savingsId).append(" with message ")
							.append(error.getDeveloperMessage());
				}
			} catch (final Exception e) {
				logger.error("Post Dividends to savings failed for Divident detail Id:"
						+ id
						+ " and savings Id: "
						+ savingsId
						+ " with message " + e.getLocalizedMessage());
				errorMsg.append(
						"Post Dividends to savings failed for Divident detail Id:")
						.append(id).append(" and savings Id:")
						.append(savingsId).append(" with message ")
						.append(e.getLocalizedMessage());
			}
		}

		if (errorMsg.length() > 0) {
			throw new JobExecutionException(errorMsg.toString());
		}
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
        for (ClientRecurringChargeData clientRecurringChargeData : clientRecurringcharges) {
            this.clientChargeWritePlatformService.applyClientRecurringCharge(currentDate, clientRecurringChargeData, sb);
        }
        if (sb.length() > 0) { throw new JobExecutionException(sb.toString()); }
    }

	@Override
	@CronTarget(jobName = JobName.INITIATE_BANK_TRANSACTION)
	public void initiateBankTransactions() throws JobExecutionException {

		StringBuilder errorMsg = new StringBuilder();
		MultiValueMap<Long,BankAccountTransaction> transactionMap = new LinkedMultiValueMap<>();
		List<BankAccountTransaction> transactions = bankAccountTransactionRepository.findByStatusOrderByExternalServiceIdAsc(TransactionStatus.INITIATED.getValue());
		for(BankAccountTransaction transaction:transactions ){
			transactionMap.add(transaction.getExternalServiceId(),transaction);
		}

		for(Map.Entry<Long, List<BankAccountTransaction>> entry:transactionMap.entrySet()){
			BankTransferService bankTransferService = bankTransactionService.getBankTransferService(entry.getKey());

			if(bankTransferService!=null && entry.getValue()!=null && !entry.getValue().isEmpty()){
				for(BankAccountTransaction transaction:entry.getValue()){
					try{
						BankTransactionDetail txnDetail = bankTransactionService.getTransactionDetail(transaction.getId());
						Long makerId = null;
						Long checkerId = null;
						Long approverId = null;
						TaskExecutionData taskExecutionData = taskPlatformReadService.getTaskDetailsByEntityTypeAndEntityId(TaskEntityType.BANK_TRANSACTION,
								txnDetail.getTransactionId());
						if(taskExecutionData!=null){
							TaskMakerCheckerData makerCheckerData = taskPlatformReadService.getMakerCheckerData(taskExecutionData.getId());
							makerId = makerCheckerData.getMakerUserId();
							checkerId = makerCheckerData.getCheckerUserId();
							approverId = makerCheckerData.getApproverUserId();
						}
						BankTransactionResponse response=bankTransferService.doTransaction(
							txnDetail.getTransactionId(),transaction.getInternalReferenceId(),txnDetail.getAmount(),transaction.getReason(),
							txnDetail.getDebiter(), txnDetail.getBeneficiary(),
							TransferType.fromInt(transaction.getTransferType()),""+transaction.getId(),
								transaction.getReason(),""+transaction.getId(),transaction.getReason(),
								makerId,checkerId,approverId);
						if(!response.getSuccess()){
							logger.warn("Initiate Transaction failed for transaction:"+ transaction.getExternalServiceId());
						}
						transaction.setErrorCode(response.getErrorCode());
						transaction.setErrorMessage(response.getErrorMessage());
						transaction.setReferenceNumber(response.getReferenceNumber());
						transaction.setUtrNumber(response.getUtrNumber());
						transaction.setPoNumber(response.getPoNumber());
						if(response.getTransactionTime()!=null) {
							transaction.setTransactionDate(response.getTransactionTime().toDate());
						}
						transaction.setStatus(response.getTransactionStatus().getValue());
						bankAccountTransactionRepository.save(transaction);
						if (response.getValidationErrors() != null) { 
                                                    throw new PlatformApiDataValidationException(response.getValidationErrors()); 
                                                }
					}catch (PlatformApiDataValidationException e){
                                            logger.error("Initiation failed for transaction "
                                                    + transaction.getId()
                                                    + " with message " + getErrorParam(e));
                                            errorMsg.append(
                                                    "Initiation failed for transaction Id:")
                                                    .append(transaction.getId())
                                                    .append(getErrorParam(e));
                                        }catch (Exception e){
						logger.error("Initiation failed for transaction "
							+ transaction.getId()
							+ " with message " + e.getMessage(),e);
						errorMsg.append(
							"Initiation failed for transaction Id:")
							.append(transaction.getId())
							.append(e.getLocalizedMessage());
					}
				}
			}

		}
		if (errorMsg.length() > 0) {
			throw new JobExecutionException(errorMsg.toString());
		}
	}
	
	private String getErrorParam(PlatformApiDataValidationException e){
	    List<ApiParameterError> errorList=e.getErrors();
	    String errorMessage=e.getDefaultUserMessage();
	    for(ApiParameterError error:errorList){
	        errorMessage=errorMessage+error.getParameterName()+",";
	    }
	    return errorMessage.substring(0,errorMessage.length()-1);
	    
	}

	@Override
	@CronTarget(jobName = JobName.UPDATE_BANK_TRANSACTION_STATUS)
	public void updateBankTransactionsStatus() throws JobExecutionException {
		StringBuilder errorMsg = new StringBuilder();
		MultiValueMap<Long,BankAccountTransaction> transactionMap = new LinkedMultiValueMap<>();
		List<BankAccountTransaction> transactions = bankAccountTransactionRepository.findByStatusOrderByExternalServiceIdAsc	(TransactionStatus.PENDING.getValue());
		for(BankAccountTransaction transaction:transactions ){
			transactionMap.add(transaction.getExternalServiceId(),transaction);
		}

		List<BankAccountTransaction> errorTransactions = bankAccountTransactionRepository.findByStatusOrderByExternalServiceIdAsc	(TransactionStatus.ERROR.getValue());
		for(BankAccountTransaction transaction:errorTransactions ){
			transactionMap.add(transaction.getExternalServiceId(),transaction);
		}

		for(Map.Entry<Long, List<BankAccountTransaction>> entry:transactionMap.entrySet()){
			BankTransferService bankTransferService = bankTransactionService.getBankTransferService(entry.getKey());

			if(bankTransferService!=null && entry.getValue()!=null && !entry.getValue().isEmpty()){
				for(BankAccountTransaction transaction:entry.getValue()){
					try{
						Long makerId = null;
						Long checkerId = null;
						Long approverId = null;
						BankTransactionDetail txnDetail = bankTransactionService.getTransactionDetail(transaction.getId());
						TaskExecutionData taskExecutionData = taskPlatformReadService.getTaskDetailsByEntityTypeAndEntityId(TaskEntityType.BANK_TRANSACTION,
								txnDetail.getTransactionId());
						if(taskExecutionData!=null){
							TaskMakerCheckerData makerCheckerData = taskPlatformReadService.getMakerCheckerData(taskExecutionData.getId());
							makerId = makerCheckerData.getMakerUserId();
							checkerId = makerCheckerData.getCheckerUserId();
							approverId = makerCheckerData.getApproverUserId();
						}
						BankTransactionResponse response=bankTransferService.getTransactionStatus(
							transaction.getId(),transaction.getInternalReferenceId(),transaction.getReferenceNumber(),makerId,checkerId,approverId);
						if(!response.getSuccess()){
							logger.warn("Status update failed for transaction:"+ transaction.getExternalServiceId());
						}
						transaction.setErrorCode(response.getErrorCode());
						transaction.setErrorMessage(response.getErrorMessage());
						if(transaction.getReferenceNumber()==null){
							transaction.setReferenceNumber(response.getReferenceNumber());
						}
						if(response.getUtrNumber()!=null){
							transaction.setUtrNumber(response.getUtrNumber());
						}
						if(response.getPoNumber()!=null){
							transaction.setPoNumber(response.getPoNumber());
						}
						if(response.getTransactionTime()!=null){
							transaction.setTransactionDate(response.getTransactionTime().toDate());
						}
						transaction.setStatus(response.getTransactionStatus().getValue());
						bankAccountTransactionRepository.save(transaction);
					}catch (Exception e){
						logger.error("Status update failed for transaction Id:"
							+ transaction.getId()
							+ " with message " + e.getMessage(),e);
						errorMsg.append(
							"| Status update failed for transaction Id: ")
							.append(transaction.getId()).append(" with error:")
							.append(e.getMessage());
					}
				}
			}

		}
		if (errorMsg.length() > 0) {
			throw new JobExecutionException(errorMsg.toString());
		}
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
            } catch (Exception e) {
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
                    List<Holiday> holidaylist = new ArrayList<>();
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
        Collection<CalendarData> calendars = calanderReadPlatformService.retrieveAllCalendarsForNextRecurringDate();
        if (!calendars.isEmpty()) {
            for (CalendarData calendar : calendars) {
                String recurringRule = calendar.getRecurrence();
                LocalDate seedDate = calendar.getStartDate();
                LocalDate startDate = DateUtils.getLocalDateOfTenant();
                Long calendarId = calendar.getId();
                LocalDate nextRecurringDate = CalendarUtils.getNextRecurringDate(recurringRule, seedDate, startDate);
                this.calendarWritePlatformService.updateCalendarNextRecurringDate(calendarId, nextRecurringDate);
            }
        }
    }

}
