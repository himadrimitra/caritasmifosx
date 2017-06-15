package org.apache.fineract.portfolio.client.service;

import static org.apache.fineract.portfolio.account.api.AccountTransfersApiConstants.transferAmountParamName;
import static org.apache.fineract.portfolio.account.api.AccountTransfersApiConstants.transferDateParamName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.savings.SavingsAccountTransactionType;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        return this.jdbcTemplate.queryForObject(sql, new Object[] { clientId }, BigDecimal.class);
    }

    private void throwGeneralPlatformDomainRuleException(final String globalisationMessageCode, final String defaultUserMessage,
            final BigDecimal amountLimit, final BigDecimal transactionAmount) {
        throw new GeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage, amountLimit, transactionAmount);
    }

    @Override
    public void validateLoanDisbursalAmountWithClientCurrentOutstandingAmountLimit(final Long clientId, final BigDecimal principalAmount) {
        final BigDecimal totalLoanOutstandingAmountLimit = getTotalLoanOutstandingAmountLimit(clientId);
        if (totalLoanOutstandingAmountLimit != null) {
            final String sql = "select SUM(ifnull(l.principal_outstanding_derived, 0)) as totalCurrentLoansOutsatndingAmountLimit from m_loan l join m_client c on c.id = l.client_id where l.loan_status_id = 300 and l.client_id = ? ";
            BigDecimal totalCurrentLoansOutsatndingAmountLimit = this.jdbcTemplate.queryForObject(sql, new Object[] { clientId },
                    BigDecimal.class);
            totalCurrentLoansOutsatndingAmountLimit = MathUtility.add(totalCurrentLoansOutsatndingAmountLimit, principalAmount);
            if (MathUtility.isLesser(totalLoanOutstandingAmountLimit, totalCurrentLoansOutsatndingAmountLimit)) {
                final String globalisationMessageCode = "error.msg.all.loans.outstanding.amount.should.not.be.greater.than.client.total.loan.outstanding.amount.limit";
                final String defaultUserMessage = "All loans outstanding amount "
                        + totalCurrentLoansOutsatndingAmountLimit.add(principalAmount)
                        + " should not be greater than client total loan outstanding amount " + totalLoanOutstandingAmountLimit + " limit";
                throwGeneralPlatformDomainRuleException(globalisationMessageCode, defaultUserMessage, totalLoanOutstandingAmountLimit,
                        totalCurrentLoansOutsatndingAmountLimit.add(principalAmount));
            }
        }
    }

    private BigDecimal getTotalLoanOutstandingAmountLimit(final Long clientId) {
        final String sql = "select cal.total_loan_outstanding_amount_limit as totalLoanOutstandingAmountLimit from m_client_account_limits cal join m_client c on c.id = cal.client_id where cal.client_id = ?";
        return this.jdbcTemplate.queryForObject(sql, new Object[] { clientId }, BigDecimal.class);
    }

    @Override
    public void validateSavingsAccountWithClientDailyWithdrawalAmountLimit(final SavingsAccount savingsAccount,
            final SavingsAccountTransaction withdrawal) {
        final BigDecimal clientTotalDailyWithdrawalAmountLimit = getClientTotalDailyWithdrawalAmountLimit(savingsAccount.getClient()
                .getId());
        if (clientTotalDailyWithdrawalAmountLimit != null) {
            BigDecimal totalAmountWithdrawn = BigDecimal.ZERO;
            for (final SavingsAccountTransaction transaction : savingsAccount.getTransactions()) {
                if (!transaction.isReversed() && transaction.isWithdrawal()
                        && transaction.getTransactionLocalDate().isEqual(withdrawal.getTransactionLocalDate())) {
                    totalAmountWithdrawn = totalAmountWithdrawn.add(transaction.getAmount());
                }
            }
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
        return this.jdbcTemplate.queryForObject(sql, new Object[] { clientId }, BigDecimal.class);
    }

    @Override
    public void validateSavingsAccountWithClientDailyTransferAmountLimit(final SavingsAccount savingsAccount, final JsonCommand jsonCommand) {
        final BigDecimal clientTotalDailyTransferAmountLimit = getClientTotalDailyTransferAmountLimit(savingsAccount.getClient().getId());
        if (clientTotalDailyTransferAmountLimit != null) {
            BigDecimal totalAmountTransfered = BigDecimal.ZERO;
            final LocalDate transactionDate = jsonCommand.localDateValueOfParameterNamed(transferDateParamName);
            final BigDecimal transactionAmount = jsonCommand.bigDecimalValueOfParameterNamed(transferAmountParamName);
            final StringBuilder sqlBuilder = new StringBuilder(100);
            sqlBuilder.append("select st.amount as amount from m_savings_account_transaction st ");
            sqlBuilder.append("join m_account_transfer_transaction att on att.from_savings_transaction_id = st.id ");
            sqlBuilder.append("where st.savings_account_id = '").append(savingsAccount.getId()).append("'");
            sqlBuilder.append("and st.transaction_date = '").append(transactionDate.toString()).append("'");
            sqlBuilder.append("and st.transaction_type_enum = '").append(SavingsAccountTransactionType.WITHDRAWAL.getValue()).append("'");
            sqlBuilder.append("and st.is_reversed = 0 ");
            final List<Map<String, Object>> listOfMapForSavingAccountTransactionDatas = this.jdbcTemplate.queryForList(sqlBuilder
                    .toString());
            if (listOfMapForSavingAccountTransactionDatas != null && listOfMapForSavingAccountTransactionDatas.size() > 0) {
                for (final Map<String, Object> mapData : listOfMapForSavingAccountTransactionDatas) {
                    final BigDecimal amount = (BigDecimal) mapData.get("amount");
                    totalAmountTransfered = totalAmountTransfered.add(amount);
                }
            } else {
                totalAmountTransfered = totalAmountTransfered.add(transactionAmount);
            }
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
        return this.jdbcTemplate.queryForObject(sql, new Object[] { clientId }, BigDecimal.class);
    }

    @Override
    public void validateSavingsAccountWithClientTotalOverdraftAmountLimit(final SavingsAccount savingsAccount) {
        final BigDecimal clientTotalOverdraftAmountLimit = getClientTotalOverdraftAmountLimit(savingsAccount.getClient().getId());
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
        return this.jdbcTemplate.queryForObject(sql, new Object[] { clientId }, BigDecimal.class);
    }
}