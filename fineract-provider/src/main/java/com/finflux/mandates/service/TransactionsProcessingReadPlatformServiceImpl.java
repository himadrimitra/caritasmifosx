package com.finflux.mandates.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.mandates.data.MandateTransactionsData;
import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.data.MandatesSummaryData;
import com.finflux.mandates.domain.MandateProcessStatusEnum;
import com.finflux.portfolio.loan.mandate.domain.AccountTypeEnum;

@Service
public class TransactionsProcessingReadPlatformServiceImpl implements  TransactionsProcessingReadPlatformService{

        private final JdbcTemplate jdbcTemplate;
        private final PlatformSecurityContext context;
        private final SimpleDateFormat yyyyMMddFormat = new SimpleDateFormat("yyyy-MM-dd");
        private final PaginationHelper<MandateTransactionsData> mandateTransactionsDataPaginationHelper = new PaginationHelper<>();
        private final ConfigurationDomainService configurationDomainService;
        private final Integer DEFAULT_NUMBER_OF_DAYS = 5 ;
        
        @Autowired
        public TransactionsProcessingReadPlatformServiceImpl(final PlatformSecurityContext context,
                final RoutingDataSource dataSource, final ConfigurationDomainService configurationDomainService){

                this.context = context;
                this.jdbcTemplate = new JdbcTemplate(dataSource);
                this.configurationDomainService = configurationDomainService;

        }

        @Override
        public Collection<MandateTransactionsData> retrieveRecentFailedTransactions() {
                try{
                        AppUser user = this.context.authenticatedUser();
                        final String hierarchy = user.getOffice().getHierarchy();
                        final String hierarchySearchString = hierarchy + "%";
                        final Date curDate = new Date();
                        int totalNumberOfDays = DEFAULT_NUMBER_OF_DAYS ;
                        
                        if(this.configurationDomainService.retrieveNumberOfDays()!=null) {
                        	totalNumberOfDays = this.configurationDomainService.retrieveNumberOfDays() ;
                        }
                        final String startDate	= yyyyMMddFormat.format(new Date(curDate.getTime() - (totalNumberOfDays*24*60*60*1000)));
                        final String endDate = yyyyMMddFormat.format(new Date(curDate.getTime() - (1*24*60*60*1000)));

                        MandatesTransactionsDataMapper mapper = new MandatesTransactionsDataMapper();

                        return this.jdbcTemplate.query(mapper.schemaForFailedTransactions(),
                                new Object[]{startDate, endDate, hierarchySearchString}, mapper);
                }catch (EmptyResultDataAccessException e){
                        return new ArrayList<>();
                }
        }

        @Override
        public Collection<MandateTransactionsData> retrieveRequestStatusTransactions(final MandatesProcessData processData) {
                try{
                        AppUser user = this.context.authenticatedUser();
                        final String hierarchy = user.getOffice().getHierarchy();
                        final String hierarchySearchString = hierarchy + "%";

                        final String startDate = yyyyMMddFormat.format(processData.getPaymentDueStartDate());
                        final String endDate = yyyyMMddFormat.format(processData.getPaymentDueEndDate());

                        MandatesTransactionsDataMapper mapper = new MandatesTransactionsDataMapper();
                        String sql = mapper.schemaForRequestStatusTransactions();
                        if(processData.includeChildOffices()) {
                                sql += " and o.hierarchy like concat((select off.hierarchy from m_office as off where off.id = ?),'%') ";
                        }else{
                                sql += " and o.id = ? ";
                        }

                        return this.jdbcTemplate.query(sql,
                                new Object[]{startDate, endDate, hierarchySearchString, processData.getOfficeId()}, mapper);
                }catch (EmptyResultDataAccessException e){
                        return new ArrayList<>();
                }
        }

        @Override
        public MandateTransactionsData findOneByLoanAccountNoAndInprocessStatus(final String reference) {
                try{
                        MandatesTransactionsDataMapper mapper = new MandatesTransactionsDataMapper();
                        return this.jdbcTemplate.queryForObject(mapper.schemaForFindByLoanAccNoAndInProcessStatus(),
                                new Object[]{reference}, mapper);
                }catch (EmptyResultDataAccessException e){
                        return null;
                }
        }

        @Override
        public Collection<MandatesSummaryData> retrieveTransactionSummary(final Long officeId, final Boolean includeChildOffices,
                final Date fromDate, final Date toDate) {
                try{
                        MandateSummaryDataMapper mapper = new MandateSummaryDataMapper();
                        String sql = mapper.schema();
                        sql += " where mt.request_date between ? and ? ";
                        if(includeChildOffices){
                                sql += " and o.hierarchy like concat((select off.hierarchy from m_office as off where off.id = ?),'%') ";
                        }else{
                                sql += " and o.id = ? ";
                        }
                        sql += " group by mt.`status` ";
                        return jdbcTemplate.query(sql,
                                new Object[]{yyyyMMddFormat.format(fromDate), yyyyMMddFormat.format(toDate), officeId}, mapper);
                }catch (EmptyResultDataAccessException ex){
                        return new ArrayList<>();
                }
        }

        @Override
        public Page<MandateTransactionsData> retrieveAllTransactions(final Long officeId, final Boolean includeChildOffices,
                final Date fromDate, final Date toDate, final Integer offset, final Integer limit) {
                MandatesTransactionsDataMapper mapper = new MandatesTransactionsDataMapper();
                String sql = " select SQL_CALC_FOUND_ROWS  " + mapper.schema();
                sql += " where mt.request_date between ? and ? ";
                if(includeChildOffices){
                        sql += " and o.hierarchy like concat((select off.hierarchy from m_office as off where off.id = ?),'%') ";
                }else{
                        sql += " and o.id = ? ";
                }
                sql += " order by mt.request_date desc ";
                sql += " limit " + limit;
                sql += " offset " + offset;

                final String sqlCountRows = "SELECT FOUND_ROWS()";
                return this.mandateTransactionsDataPaginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sql,
                        new Object[]{yyyyMMddFormat.format(fromDate), yyyyMMddFormat.format(toDate), officeId}, mapper);
        }

        private static class MandatesTransactionsDataMapper implements RowMapper<MandateTransactionsData> {
                public String schemaForFailedTransactions(){
                        StringBuilder sql = new StringBuilder("select mt.id as id, mt.mandate_id as mandateId,  ")
                                .append("  mt.loan_id as loanId, mt.payment_due_amount as paymentDueAmount,  ")
                                .append("  mt.payment_due_date as paymentDueDate, mt.request_date as requestDate, ")
                                .append("  mt.`status` as status, mt.return_process_date as returnProcessDate,  ")
                                .append("  mt.return_process_reference_id as returnProcessReferenceId, mt.return_reason as returnReason,  ")
                                .append("  lm.umrn as umrn, lm.bank_account_holder_name as bankAccountHolderName, ")
                                .append("  lm.bank_account_number as bankAccountNumber, lm.bank_name as bankName,  ")
                                .append("  lm.branch_name as branchName, lm.micr as micr,  ")
                                .append("  lm.ifsc as ifsc, lm.account_type_enum as accountType, l.account_no as loanAccountNo ")
                                .append(" from f_mandate_transactions as mt " + "left join f_loan_mandates as lm on mt.mandate_id = lm.id ")
                                .append(" left join m_loan as l on mt.loan_id = l.id " + "left join m_client as cl on l.client_id = cl.id ")
                                .append(" left join m_office as o on cl.office_id = o.id " + "where mt.`status` = 4 ")
                                .append(" and mt.request_date between ? and ? " + "and o.hierarchy like ? ");
                        return sql.toString();
                }

                public String schemaForRequestStatusTransactions(){
                        StringBuilder sql = new StringBuilder("select mt.id as id, mt.mandate_id as mandateId,   ")
                                .append("  mt.loan_id as loanId, mt.payment_due_amount as paymentDueAmount,   ")
                                .append("  mt.payment_due_date as paymentDueDate, mt.request_date as requestDate,  ")
                                .append("  mt.`status` as status, mt.return_process_date as returnProcessDate,   ")
                                .append("  mt.return_process_reference_id as returnProcessReferenceId, mt.return_reason as returnReason,   ")
                                .append("  lm.umrn as umrn, lm.bank_account_holder_name as bankAccountHolderName,  ")
                                .append("  lm.bank_account_number as bankAccountNumber, lm.bank_name as bankName,   ")
                                .append("  lm.branch_name as branchName, lm.micr as micr,   ")
                                .append("  lm.ifsc as ifsc, lm.account_type_enum as accountType, l.account_no as loanAccountNo  ")
                                .append(" from f_mandate_transactions as mt  ")
                                .append(" left join f_loan_mandates as lm on mt.mandate_id = lm.id  ")
                                .append(" left join m_loan as l on mt.loan_id = l.id  ")
                                .append(" left join m_client as cl on l.client_id = cl.id  ")
                                .append(" left join m_office as o on cl.office_id = o.id  ")
                                .append(" where mt.`status` = 1  ")
                                .append(" and mt.payment_due_date between ? and ?  ")
                                .append(" and o.hierarchy like ?");
                        return sql.toString();
                }

                public String schemaForFindByLoanAccNoAndInProcessStatus(){
                        StringBuilder sql = new StringBuilder("select mt.id as id, mt.mandate_id as mandateId, ")
                                .append(" mt.loan_id as loanId, mt.payment_due_amount as paymentDueAmount, ")
                                .append(" mt.payment_due_date as paymentDueDate, mt.request_date as requestDate,")
                                .append(" mt.`status` as status, mt.return_process_date as returnProcessDate, ")
                                .append(" mt.return_process_reference_id as returnProcessReferenceId, mt.return_reason as returnReason, ")
                                .append(" lm.umrn as umrn, lm.bank_account_holder_name as bankAccountHolderName,")
                                .append(" lm.bank_account_number as bankAccountNumber, lm.bank_name as bankName, ")
                                .append(" lm.branch_name as branchName, lm.micr as micr, ")
                                .append(" lm.ifsc as ifsc, lm.account_type_enum as accountType, l.account_no as loanAccountNo ")
                                .append(" from f_mandate_transactions as mt ")
                                .append(" left join f_loan_mandates as lm on mt.mandate_id = lm.id ")
                                .append(" left join m_loan as l on mt.loan_id = l.id ")
                                .append(" where mt.`status` = 2 ")
                                .append(" and l.account_no = ? ");
                        return sql.toString();
                }

                public String schema(){
                        StringBuilder sql = new StringBuilder(" mt.id as id, mt.mandate_id as mandateId,   ")
                                .append("  mt.loan_id as loanId, mt.payment_due_amount as paymentDueAmount,   ")
                                .append("  mt.payment_due_date as paymentDueDate, mt.request_date as requestDate,  ")
                                .append("  mt.`status` as status, mt.return_process_date as returnProcessDate,   ")
                                .append("  mt.return_process_reference_id as returnProcessReferenceId, mt.return_reason as returnReason,   ")
                                .append("  lm.umrn as umrn, lm.bank_account_holder_name as bankAccountHolderName,  ")
                                .append("  lm.bank_account_number as bankAccountNumber, lm.bank_name as bankName,   ")
                                .append("  lm.branch_name as branchName, lm.micr as micr,   ")
                                .append("  lm.ifsc as ifsc, lm.account_type_enum as accountType, l.account_no as loanAccountNo  ")
                                .append(" from f_mandate_transactions as mt  ")
                                .append(" left join f_loan_mandates as lm on mt.mandate_id = lm.id  ")
                                .append(" left join m_loan as l on mt.loan_id = l.id  ")
                                .append(" left join m_client as cl on l.client_id = cl.id  ")
                                .append(" left join m_office as o on cl.office_id = o.id  ");
                        return sql.toString();
                }

                @Override
                public MandateTransactionsData mapRow(ResultSet rs, int rowNum) throws SQLException {
                        final Long id = JdbcSupport.getLong(rs, "id");
                        final Long mandateId = JdbcSupport.getLong(rs, "mandateId");
                        final Long loanId = JdbcSupport.getLong(rs, "loanId");
                        final BigDecimal paymentDueAmount = rs.getBigDecimal("paymentDueAmount");
                        final Date paymentDueDate = rs.getDate("paymentDueDate");
                        final Date requestDate = rs.getDate("requestDate");
                        final Integer statusEnum = rs.getInt("status");
                        final String status = MandateProcessStatusEnum.fromInt(statusEnum).getStatus();
                        final Date returnProcessDate = rs.getDate("returnProcessDate");
                        final String returnProcessReferenceId = rs.getString("returnProcessReferenceId");
                        final String returnReason = rs.getString("returnReason");
                        final String umrn = rs.getString("umrn");
                        final String bankAccountHolderName = rs.getString("bankAccountHolderName");
                        final String bankAccountNumber = rs.getString("bankAccountNumber");
                        final String bankName = rs.getString("bankName");
                        final String branchName = rs.getString("branchName");
                        final String micr = rs.getString("micr");
                        final String ifsc = rs.getString("ifsc");
                        final Integer accountTypeEnum = rs.getInt("accountType");
                        final EnumOptionData accountType = AccountTypeEnum.enumOptionDataFrom(accountTypeEnum);
                        final String loanAccountNo = rs.getString("loanAccountNo");

                        return new MandateTransactionsData(id, mandateId, loanId, paymentDueAmount, paymentDueDate, requestDate,
                                status, returnProcessDate, returnProcessReferenceId, returnReason, umrn, bankAccountHolderName,
                                bankAccountNumber, bankName, branchName, micr, ifsc, accountType, loanAccountNo);
                }
        }

        private static class MandateSummaryDataMapper implements RowMapper<MandatesSummaryData> {
                public String schema(){
                        StringBuilder sql = new StringBuilder("select mt.`status` as status, ")
                                .append(" count(*) as count, sum(mt.payment_due_amount) as amount ")
                                .append(" from f_mandate_transactions as mt  ")
                                .append(" left join f_loan_mandates as lm on mt.mandate_id = lm.id  ")
                                .append(" left join m_loan as l on mt.loan_id = l.id  ")
                                .append(" left join m_client as cl on l.client_id = cl.id  ")
                                .append(" left join m_office as o on cl.office_id = o.id  ");
                        return sql.toString();
                }

                @Override
                public MandatesSummaryData mapRow(ResultSet rs, int rowNum) throws SQLException {
                        final Integer statusEnum = rs.getInt("status");
                        final String statusValue = MandateProcessStatusEnum.fromInt(statusEnum).getStatus();
                        final EnumOptionData status = new EnumOptionData(statusEnum.longValue(), statusValue, statusValue);
                        final Integer count = rs.getInt("count");
                        final BigDecimal amount = rs.getBigDecimal("amount");

                        return new MandatesSummaryData(status, count, amount);
                }
        }
}
