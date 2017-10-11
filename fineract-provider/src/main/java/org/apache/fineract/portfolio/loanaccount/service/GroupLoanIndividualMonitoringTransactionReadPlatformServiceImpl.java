package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class GroupLoanIndividualMonitoringTransactionReadPlatformServiceImpl implements GroupLoanIndividualMonitoringTransactionReadPlatformService{

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    
    @Autowired
    public GroupLoanIndividualMonitoringTransactionReadPlatformServiceImpl(final RoutingDataSource dataSource, final PlatformSecurityContext context) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class GroupLoanIndividualMonitoringTransactionMapper implements RowMapper<GroupLoanIndividualMonitoringTransactionData> {
        
        final String schema;
        
        public GroupLoanIndividualMonitoringTransactionMapper() {
            final StringBuilder sql = new StringBuilder(400);
            sql.append("glimTransaction.id as id, mc.client_id as clientId, mcc.display_name as clientName, ");
            sql.append("glimTransaction.glim_id as glimId, glimTransaction.loan_transaction_id as loanTransactionId, ");
            sql.append("glimTransaction.transaction_type_enum as transactionType,  ");
            sql.append("glimTransaction.principal_portion as principalPortion, ");
            sql.append("glimTransaction.interest_portion as interestPortion, ");
            sql.append("glimTransaction.fee_portion as feePortion, ");
            sql.append("glimTransaction.penalty_portion as penaltyPortion, ");
            sql.append("glimTransaction.total_amount as totalAmount ");
            sql.append(" from m_loan_glim_transaction glimTransaction ");
            sql.append(" left join m_loan_glim mc on mc.id = glimTransaction.glim_id ");
            sql.append(" left join m_client mcc on mcc.id = mc.client_id ");
            this.schema = sql.toString();
            
        }
        
        public String schema() {
            return this.schema;
        }

        @Override
        public GroupLoanIndividualMonitoringTransactionData mapRow(ResultSet rs, int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long glimId = JdbcSupport.getLong(rs, "glimId");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final String clientName = rs.getString("clientName");
            final Long loanTransactionId = JdbcSupport.getLong(rs, "loanTransactionId");
            final int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
            final LoanTransactionEnumData transactionType = LoanEnumerations.transactionType(transactionTypeInt);
            final BigDecimal principalPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalPortion");
            final BigDecimal interestPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestPortion");
            final BigDecimal feePortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feePortion");
            final BigDecimal penaltyPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyPortion");
            final BigDecimal transactionAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "totalAmount");
            return GroupLoanIndividualMonitoringTransactionData.createNew(id, glimId, clientId, clientName, loanTransactionId, transactionType, principalPortion,
                    interestPortion, feePortion, penaltyPortion, transactionAmount);
        }
        
    }


    @Override
    public List<GroupLoanIndividualMonitoringTransactionData> retriveGlimTransaction(Long transactionId) {

        GroupLoanIndividualMonitoringTransactionMapper rm = new GroupLoanIndividualMonitoringTransactionMapper();

        String sql = "select " + rm.schema() + " where glimTransaction.loan_transaction_id = ? group by mc.client_id";

        return this.jdbcTemplate.query(sql, rm, new Object[] { transactionId });
    }


    @Override
    public GroupLoanIndividualMonitoringTransactionData retriveGlimTransaction(Long transactionId, Long glimId) {
        GroupLoanIndividualMonitoringTransactionMapper rm = new GroupLoanIndividualMonitoringTransactionMapper();

        String sql = "select " + rm.schema() + " where glimTransaction.loan_transaction_id = ? and glimTransaction.glim_id = ? group by mc.client_id";

        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { transactionId , glimId});
    }

}
