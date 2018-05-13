package com.finflux.portfolio.investmenttracker.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.portfolio.account.data.AccountTransferData;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionEnumData;
import org.apache.fineract.portfolio.savings.service.SavingsEnumerations;
import org.apache.fineract.useradministration.data.AppUserData;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.investmenttracker.Exception.InvestmentAccountNotFoundException;
import com.finflux.portfolio.investmenttracker.data.InvestmentSavingsTransactionData;
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
            sqlBuilder.append("user.id as appUserId , user.username as userName  ");
            sqlBuilder.append("from f_investment_transaction tr ");
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
    

    private static final class InvestmentSavingsTransactionDataMapper implements RowMapper<InvestmentSavingsTransactionData> {

        private final String schemaSql;

        public InvestmentSavingsTransactionDataMapper() {

            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("isat.id as id, isat.investment_id as investmentId, isat.description as description, ");
            sqlBuilder.append("tr.id as transactionId, tr.transaction_type_enum as transactionType, ");
            sqlBuilder.append("tr.transaction_date as transactionDate, tr.amount as transactionAmount, ");
            sqlBuilder.append("o.id as officeId, o.name as officeName ");
            sqlBuilder.append("from f_investment_saving_account_transaction isat ");
            sqlBuilder.append("join  m_savings_account_transaction tr on tr.id = isat.transaction_id ");
            sqlBuilder.append("join  m_office o on o.id = tr.office_id ");
          
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public InvestmentSavingsTransactionData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final Long investmentId = rs.getLong("investmentId");
            final String description = rs.getString("description");
            
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            
            OfficeData officeData = OfficeData.lookup(officeId, officeName);
            
            final Long transactionId = rs.getLong("transactionId");
            final int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
            final SavingsAccountTransactionEnumData transactionType = SavingsEnumerations.transactionType(transactionTypeInt);
            final LocalDate transactionDate = JdbcSupport.getLocalDate(rs, "transactionDate");
            final BigDecimal amount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "transactionAmount");
            
            SavingsAccountTransactionData savingsAccountTransactionData = SavingsAccountTransactionData.create(transactionId, transactionType, transactionDate, amount);
            
            return InvestmentSavingsTransactionData.create(id, savingsAccountTransactionData, investmentId, description, officeData);
        }
    }


    @Override
    public List<InvestmentSavingsTransactionData> findByInvestmentIdAndSavingsId(Long accountId, Long savingsId) {
        try{
        InvestmentSavingsTransactionDataMapper mapper = new InvestmentSavingsTransactionDataMapper();
        String sql = "select "+mapper.schema()+" where isat.investment_id = ? and isat.savings_id = ?  ";
        final Object[] queryParameters = new Object[] { accountId ,savingsId };
        return this.jdbcTemplate.query(sql, mapper, queryParameters);
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }

    }
    
    @Override
    public InvestmentSavingsTransactionData findBySavingsTransactionId(Long savingTransactionId) {
        try{
        InvestmentSavingsTransactionDataMapper mapper = new InvestmentSavingsTransactionDataMapper();
        String sql = "select "+mapper.schema()+" where isat.transaction_id = ? ";
        final Object[] queryParameters = new Object[] { savingTransactionId };
        return this.jdbcTemplate.queryForObject(sql, mapper, queryParameters);
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }

    }

}