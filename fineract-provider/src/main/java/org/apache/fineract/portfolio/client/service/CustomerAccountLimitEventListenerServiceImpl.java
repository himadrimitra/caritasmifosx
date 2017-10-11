package org.apache.fineract.portfolio.client.service;

import static org.apache.fineract.portfolio.account.api.AccountTransfersApiConstants.transferAmountParamName;
import static org.apache.fineract.portfolio.account.api.AccountTransfersApiConstants.transferDateParamName;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.savings.SavingsAccountTransactionType;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomerAccountLimitEventListenerServiceImpl implements CustomerAccountLimitEventListenerService {

    @SuppressWarnings("unused")
    private final static Logger logger = LoggerFactory.getLogger(CustomerAccountLimitEventListenerServiceImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CustomerAccountLimitEventListenerServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void validateLoanDisbursalAmountWithClientDisbursmentAmountLimit(final Long clientId, final BigDecimal principalAmount) {
        final BigDecimal totalDisbursementAmountLimit = getTotalDisbursementAmountLimit(clientId);
        if (totalDisbursementAmountLimit != null && MathUtility.isLesser(totalDisbursementAmountLimit, principalAmount)) {
            final String globalisationMessageCode = "error.msg.loan.amount.should.not.be.greater.than.client.disbursement.amount.limit";
            final String defaultUserMessage = "Loan amount " + principalAmount + " should not be greater than client disbursement amount "
                    + totalDisbursementAmountLimit + " limit";
            throwGeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage, totalDisbursementAmountLimit,
                    principalAmount);
        }
    }

    private BigDecimal getTotalDisbursementAmountLimit(final Long clientId) {
        final String sql = "select cal.total_disbursement_amount_limit as totalDisbursementAmountLimit from m_client_account_limits cal join m_client c on c.id = cal.client_id where cal.client_id = ?";
        return queryExecuteAndReturnBigDecimalValue(sql, clientId);
    }

    private BigDecimal queryExecuteAndReturnBigDecimalValue(final String sql, final Long clientId) {
        try {
            return this.jdbcTemplate.queryForObject(sql, new Object[] { clientId }, BigDecimal.class);
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    private void throwGeneralPlatformDomainRuleException(final String globalisationMessageCode, final String defaultUserMessage,
            final BigDecimal amountLimit, final BigDecimal transactionAmount) {
        throw new GeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage, amountLimit, transactionAmount);
    }

    @Override
    public void validateLoanDisbursalAmountWithClientCurrentOutstandingAmountLimit(final Loan loan) {
        final Long clientId = loan.getClientId();
        final BigDecimal totalLoanOutstandingAmountLimit = getTotalLoanOutstandingAmountLimit(clientId);
        if (totalLoanOutstandingAmountLimit != null) {
            final StringBuilder sb = new StringBuilder(100);
            sb.append("select SUM(ifnull(l.total_outstanding_derived, 0)) as totalCurrentLoansOutsatndingAmountLimit ");
            sb.append("from m_loan l join m_client c on c.id = l.client_id where ").append("l.id != ").append(loan.getId());
            sb.append(" and l.loan_status_id = 300 and l.client_id = ? ");
            BigDecimal totalCurrentLoansOutsatndingAmountLimit = queryExecuteAndReturnBigDecimalValue(sb.toString(), clientId);
            totalCurrentLoansOutsatndingAmountLimit = MathUtility.add(totalCurrentLoansOutsatndingAmountLimit, loan.getPrincipalAmount());
            if (MathUtility.isLesser(totalLoanOutstandingAmountLimit, totalCurrentLoansOutsatndingAmountLimit)) {
                final String globalisationMessageCode = "error.msg.all.loans.outstanding.amount.should.not.be.greater.than.client.total.loan.outstanding.amount.limit";
                final String defaultUserMessage = "All loans outstanding amount " + totalCurrentLoansOutsatndingAmountLimit
                        + " should not be greater than client total loan outstanding amount " + totalLoanOutstandingAmountLimit + " limit";
                throwGeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage, totalLoanOutstandingAmountLimit,
                        totalCurrentLoansOutsatndingAmountLimit);
            }
        }
    }

    private BigDecimal getTotalLoanOutstandingAmountLimit(final Long clientId) {
        final String sql = "select cal.total_loan_outstanding_amount_limit as totalLoanOutstandingAmountLimit from m_client_account_limits cal join m_client c on c.id = cal.client_id where cal.client_id = ?";
        return queryExecuteAndReturnBigDecimalValue(sql, clientId);
    }

    @Override
    public void validateSavingsAccountWithClientDailyWithdrawalAmountLimit(final SavingsAccount savingsAccount,
            final SavingsAccountTransaction withdrawal) {
        final BigDecimal clientTotalDailyWithdrawalAmountLimit = getClientTotalDailyWithdrawalAmountLimit(savingsAccount.clientId());
        if (clientTotalDailyWithdrawalAmountLimit != null) {

            final LocalDate transactionDate = withdrawal.getTransactionLocalDate();

            final StringBuilder sb = new StringBuilder(500);
            sb.append("SELECT SUM(IFNULL(sat.amount, 0)) as totalAmountWithdrawn ");
            sb.append("FROM m_savings_account sa ");
            sb.append("JOIN m_savings_account_transaction sat ON sat.savings_account_id = sa.id AND sat.transaction_type_enum = ").append(
                    SavingsAccountTransactionType.WITHDRAWAL.getValue());
            sb.append(" AND sat.is_reversed = 0 AND sat.transaction_date = '").append(transactionDate.toString()).append("' ");
            sb.append("LEFT JOIN m_account_transfer_transaction att ON att.from_savings_transaction_id = sat.id ");
            sb.append("WHERE sa.client_id = ? AND sa.status_enum = 300 AND att.id IS NULL ");

            final BigDecimal totalAmountWithdrawn = queryExecuteAndReturnBigDecimalValue(sb.toString(), savingsAccount.clientId());
            if (MathUtility.isLesser(clientTotalDailyWithdrawalAmountLimit, totalAmountWithdrawn)) {
                final String globalisationMessageCode = "error.msg.client.total.daily.withdrawal.amount.limit";
                final String defaultUserMessage = "Client total daily withdrawal amount limit " + clientTotalDailyWithdrawalAmountLimit;
                throwGeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage,
                        clientTotalDailyWithdrawalAmountLimit, withdrawal.getAmount());
            }
        }
        validateSavingsAccountWithClientTotalOverdraftAmountLimit(savingsAccount);
    }

    private BigDecimal getClientTotalDailyWithdrawalAmountLimit(final Long clientId) {
        final String sql = "select cal.daily_withdrawal_amount_limit as totalDailyWithdrawalAmountLimit from m_client_account_limits cal join m_client c on c.id = cal.client_id where cal.client_id = ?";
        return queryExecuteAndReturnBigDecimalValue(sql, clientId);
    }

    @Override
    public void validateSavingsAccountWithClientDailyTransferAmountLimit(final SavingsAccount savingsAccount, final JsonCommand jsonCommand) {
        final BigDecimal clientTotalDailyTransferAmountLimit = getClientTotalDailyTransferAmountLimit(savingsAccount.clientId());
        if (clientTotalDailyTransferAmountLimit != null) {

            final LocalDate transactionDate = jsonCommand.localDateValueOfParameterNamed(transferDateParamName);
            final BigDecimal transactionAmount = jsonCommand.bigDecimalValueOfParameterNamed(transferAmountParamName);

            final StringBuilder sb = new StringBuilder(500);
            sb.append("SELECT SUM(IFNULL(sat.amount, 0)) as totalAmountTransfered ");
            sb.append("FROM m_savings_account sa ");
            sb.append("JOIN m_savings_account_transaction sat ON sat.savings_account_id = sa.id AND sat.transaction_type_enum = ").append(
                    SavingsAccountTransactionType.WITHDRAWAL.getValue());
            sb.append(" AND sat.is_reversed = 0 AND sat.transaction_date = '").append(transactionDate.toString()).append("' ");
            sb.append("JOIN m_account_transfer_transaction att ON att.from_savings_transaction_id = sat.id ");
            sb.append("WHERE sa.client_id = ? AND sa.status_enum = 300 ");

            final BigDecimal totalAmountTransfered = queryExecuteAndReturnBigDecimalValue(sb.toString(), savingsAccount.clientId());

            if (clientTotalDailyTransferAmountLimit.compareTo(totalAmountTransfered) == -1) {
                final String globalisationMessageCode = "error.msg.client.total.daily.transfer.amount.limit";
                final String defaultUserMessage = "Client total daily transfer amount limit " + clientTotalDailyTransferAmountLimit;
                throwGeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage, clientTotalDailyTransferAmountLimit,
                        transactionAmount);
            }
        }

        validateSavingsAccountWithClientTotalOverdraftAmountLimit(savingsAccount);
    }

    private BigDecimal getClientTotalDailyTransferAmountLimit(final Long clientId) {
        final String sql = "select cal.daily_transfer_amount_limit as totalDailyTransferAmountLimit from m_client_account_limits cal join m_client c on c.id = cal.client_id where cal.client_id = ?";
        return queryExecuteAndReturnBigDecimalValue(sql, clientId);
    }

    @Override
    public void validateSavingsAccountWithClientTotalOverdraftAmountLimit(final SavingsAccount savingsAccount) {
        final BigDecimal clientTotalOverdraftAmountLimit = getClientTotalOverdraftAmountLimit(savingsAccount.clientId());
        if (clientTotalOverdraftAmountLimit != null) {
            final BigDecimal accountBalance = savingsAccount.getSummary().getAccountBalance();
            if (MathUtility.isLesser(accountBalance, BigDecimal.ZERO)) {
                if (MathUtility.isLesser(clientTotalOverdraftAmountLimit, accountBalance.abs())) {
                    final String globalisationMessageCode = "error.msg.client.total.overdraft.amount.limit";
                    final String defaultUserMessage = "Client total overdraft amount limit " + clientTotalOverdraftAmountLimit;
                    throwGeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage, clientTotalOverdraftAmountLimit,
                            accountBalance.abs());
                }
            }
        }
    }

    private BigDecimal getClientTotalOverdraftAmountLimit(final Long clientId) {
        final String sql = "select cal.total_overdraft_amount_limit as totalOverdraftAmountLimit from m_client_account_limits cal join m_client c on c.id = cal.client_id where cal.client_id = ?";
        return queryExecuteAndReturnBigDecimalValue(sql, clientId);
    }
}