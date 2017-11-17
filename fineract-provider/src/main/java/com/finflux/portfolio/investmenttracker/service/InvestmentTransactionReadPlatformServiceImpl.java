package com.finflux.portfolio.investmenttracker.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.useradministration.data.AppUserData;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.investmenttracker.Exception.InvestmentAccountNotFoundException;
import com.finflux.portfolio.investmenttracker.data.InvestmentTransactionData;
import com.finflux.portfolio.investmenttracker.data.InvestmentTransactionEnumData;
import com.finflux.portfolio.investmenttracker.domain.InvestmentTransactionRepositoryWrapper;

@Service
public class InvestmentTransactionReadPlatformServiceImpl implements InvestmentTransactionReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final InvestmentTransactionRepositoryWrapper investmentTransactionRepositoryWrapper;

    @Autowired
    public InvestmentTransactionReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final InvestmentTransactionRepositoryWrapper investmentTransactionRepositoryWrapper) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.investmentTransactionRepositoryWrapper = investmentTransactionRepositoryWrapper;
    }

    private static final class InvestmentTransactionTransactionsMapper implements RowMapper<InvestmentTransactionData> {

        private final String schemaSql;

        public InvestmentTransactionTransactionsMapper() {

            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("tr.id as transactionId, tr.transaction_type_enum as transactionType, ");
            sqlBuilder.append("tr.transaction_date as transactionDate, tr.amount as transactionAmount,");
            sqlBuilder.append("tr.created_date as createdDate,");
            sqlBuilder.append("tr.running_balance as runningBalance, tr.is_reversed as reversed,");
            sqlBuilder.append("tr.investment_account_id as investmentAccountId ,");
            sqlBuilder.append("o.id as officeId , o.name as officeName, ");
            sqlBuilder.append("user.id as appUserId , user.username as userName, ");
            sqlBuilder.append("from f_investment_account_transaction tr ");
            sqlBuilder.append("left join m_office o on o.id = tr.office_id ");
            sqlBuilder.append("left join m_appuser user on user.id = tr.appuser_id ");

            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public InvestmentTransactionData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("transactionId");
            final int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
            final InvestmentTransactionEnumData transactionType = InvestmentEnumerations.transactionType(transactionTypeInt);

            final LocalDate dateOf = JdbcSupport.getLocalDate(rs, "transactionDate");
            final LocalDate createdDate = JdbcSupport.getLocalDate(rs, "createdDate");
            final BigDecimal amount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "transactionAmount");
            final BigDecimal runningBalance = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "runningBalance");
            final Boolean reversed = rs.getBoolean("reversed");

            final Long investmentAccountId = rs.getLong("investmentAccountId");

            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final OfficeData office = OfficeData.lookup(officeId, officeName);
            final Long appUserId = rs.getLong("appUserId");
            final String userName = rs.getString("userName");
            final AppUserData appUser = AppUserData.dropdown(appUserId, userName);

            return InvestmentTransactionData.create(id, investmentAccountId, office, amount, runningBalance, dateOf, transactionType,
                    reversed, createdDate, appUser);
        }
    }

    @Override
    public List<InvestmentTransactionData> findByAccountId(Long accountId) {
       try {
            this.context.authenticatedUser();
            InvestmentTransactionTransactionsMapper itm = new InvestmentTransactionTransactionsMapper();

            final StringBuilder sqlBuilder = new StringBuilder("select " + itm.schema());
            sqlBuilder.append(" where tr.investment_account_id = ? ");

            final Object[] queryParameters = new Object[] { accountId };
            return this.jdbcTemplate.query(sqlBuilder.toString(), itm, queryParameters);
        } catch (final EmptyResultDataAccessException e) {
            throw new InvestmentAccountNotFoundException(accountId);
        }

    }

}
