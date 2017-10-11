package org.apache.fineract.portfolio.client.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.client.domain.ClientAccountLimitsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ClientAccountLimitsReadPlatformServiceImpl implements ClientAccountLimitsReadPlatformService {

    private final AccountLimitsMapper accountLimitsMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ClientAccountLimitsReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.accountLimitsMapper = new AccountLimitsMapper();
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public ClientAccountLimitsData retrieveOne(final Long clientId) {
        try {
            final String sql = "select  " + this.accountLimitsMapper.schema() + " where cal.client_id = ?";
            return this.jdbcTemplate.queryForObject(sql, this.accountLimitsMapper, new Object[] { clientId });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }

    private static final class AccountLimitsMapper implements RowMapper<ClientAccountLimitsData> {

        private final String schema;

        public AccountLimitsMapper() {
            final StringBuilder builder = new StringBuilder(400);
            builder.append("cal.id as id, cal.client_id as clientId,cal.total_disbursement_amount_limit as limitOnTotalDisbursementAmount, ");
            builder.append("cal.total_loan_outstanding_amount_limit as limitOnTotalLoanOutstandingAmount,cal.daily_withdrawal_amount_limit as dailyWithdrawalLimit,cal.daily_transfer_amount_limit as dailyTransferLimit, ");
            builder.append("cal.total_overdraft_amount_limit as limitOnTotalOverdraftAmount ");
            builder.append("from m_client_account_limits cal ");
            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public ClientAccountLimitsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final BigDecimal limitOnTotalDisbursementAmount = rs.getBigDecimal("limitOnTotalDisbursementAmount");
            final BigDecimal limitOnTotalLoanOutstandingAmount = rs.getBigDecimal("limitOnTotalLoanOutstandingAmount");
            final BigDecimal dailyWithdrawalLimit = rs.getBigDecimal("dailyWithdrawalLimit");
            final BigDecimal dailyTransferLimit = rs.getBigDecimal("dailyTransferLimit");
            final BigDecimal limitOnTotalOverdraftAmount = rs.getBigDecimal("limitOnTotalOverdraftAmount");

            return new ClientAccountLimitsData(id, limitOnTotalDisbursementAmount, limitOnTotalLoanOutstandingAmount, dailyWithdrawalLimit,
                    dailyTransferLimit, limitOnTotalOverdraftAmount, clientId);

        }
    }
}