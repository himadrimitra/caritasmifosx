package com.finflux.portfolio.loan.mandate.service;

import com.finflux.mandates.data.MandatesSummaryData;
import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.portfolio.bank.domain.BankAccountDetailEntityType;
import com.finflux.portfolio.bank.service.BankAccountDetailsReadService;
import com.finflux.portfolio.loan.mandate.data.MandateData;
import com.finflux.portfolio.loan.mandate.domain.AccountTypeEnum;
import com.finflux.portfolio.loan.mandate.domain.DebitFrequencyEnum;
import com.finflux.portfolio.loan.mandate.domain.DebitTypeEnum;
import com.finflux.portfolio.loan.mandate.domain.MandateStatusEnum;
import com.finflux.portfolio.loan.mandate.exception.CommandQueryParamExpectedException;
import com.finflux.portfolio.loan.mandate.exception.InvalidCommandQueryParamException;
import com.finflux.portfolio.loan.mandate.exception.NoActiveMandateFoundException;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.documentmanagement.data.DocumentData;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentReadPlatformService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Service
public class MandateReadPlatformServiceImpl implements MandateReadPlatformService {

        private final JdbcTemplate jdbcTemplate;
        private final DocumentReadPlatformService documentReadPlatformService;
        private final LoanReadPlatformService loanReadPlatformService;
        private final SimpleDateFormat yyyyMMddFormat = new SimpleDateFormat("yyyy-MM-dd");
        private final PaginationHelper<MandateData> mandateDataPaginationHelper = new PaginationHelper<>();
        private final BankAccountDetailsReadService bankAccountDetailsReadPlatformService ;
        @Autowired
        public MandateReadPlatformServiceImpl(final RoutingDataSource dataSource,
                final DocumentReadPlatformService documentReadPlatformService,
                final LoanReadPlatformService loanReadPlatformService,
                final BankAccountDetailsReadService bankAccountDetailsReadPlatformService){
                
                this.jdbcTemplate = new JdbcTemplate(dataSource);
                this.documentReadPlatformService = documentReadPlatformService;
                this.loanReadPlatformService = loanReadPlatformService;
                this.bankAccountDetailsReadPlatformService = bankAccountDetailsReadPlatformService ;

        }

    @Override
    public MandateData retrieveTemplate(final Long loanId, final String commandParam, final Boolean showEMIBalance) {
                MandateData ret = null;
                if(commandParam == null){
                        throw new CommandQueryParamExpectedException();
                }
                switch (commandParam.trim().toUpperCase()){
                        case "CREATE":
                                LoanAccountData loan = this.loanReadPlatformService.retrieveOne(loanId);
                                ret = retrieveCreateTemplate(loan,showEMIBalance);
                                break;
                        case "UPDATE":
                                ret = retrieveUpdateTemplate(loanId);
                                break;
                        case "CANCEL":
                                ret = retrieveCancelTemplate(loanId);
                                break;
                        default:
                                throw new InvalidCommandQueryParamException(commandParam);
                }
                return ret;
        }

    private MandateData retrieveCreateTemplate(final LoanAccountData loan, final Boolean showEMIBalance) {
            BankAccountDetailData bankAccountDetailsData = this.bankAccountDetailsReadPlatformService.retrieveOneBy(BankAccountDetailEntityType.CLIENTS, loan.clientId()) ;
        return MandateData.createTemplate(getDocumentEnumOptionData(loan.getId()), loan, bankAccountDetailsData, showEMIBalance);
        }

    private MandateData retrieveUpdateTemplate(final Long loanId) {
        final MandateData data = retrieveActiveMandate(loanId);
        if (null != data) { return MandateData.createTemplateFrom(data, getDocumentEnumOptionData(loanId)); }
        throw new NoActiveMandateFoundException();
    }

        private MandateData retrieveCancelTemplate(final Long loanId) {
                final MandateData data = retrieveActiveMandate(loanId);
                if(null != data){
                        return MandateData.createTemplateFrom(data, getDocumentEnumOptionData(loanId));
                }
                throw new NoActiveMandateFoundException();
        }

    private Collection<EnumOptionData> getDocumentEnumOptionData(final Long loanId) {
        Collection<DocumentData> documents = this.documentReadPlatformService.retrieveAllDocuments("loans", loanId);

        if (null == documents || documents.size() < 1) { return null; }
        Collection<EnumOptionData> ret = new ArrayList<>();
        for (DocumentData document : documents) {
            ret.add(new EnumOptionData(document.getId(), document.getName(), document.getName()));
        }
        return ret;
    }

        @Override
        public MandateData retrieveActiveMandate(final Long loanId) {
                try{
                        MandateDataMapper mapper = new MandateDataMapper();
                        String sql = "select " + mapper.schema() + " where m.mandate_status_enum = ? and l.id = ? ";
                        return jdbcTemplate.queryForObject(sql, new Object[]{MandateStatusEnum.ACTIVE.getValue(), loanId}, mapper);
                }catch (EmptyResultDataAccessException ex){
                        return null;
                }
        }

        @Override
        public MandateData retrieveMandate(final Long loanId, final Long mandateId) {
                try{
                        MandateDataMapper mapper = new MandateDataMapper();
                        String sql = "select " + mapper.schema() + " where l.id = ? and m.id = ?";
                        MandateData data = jdbcTemplate.queryForObject(sql, new Object[]{loanId, mandateId}, mapper);
                        data.setEnumOptions(AccountTypeEnum.getAccountTypeOptionData(),
                                                DebitTypeEnum.getDebitTypeOptionData(),
                                                DebitFrequencyEnum.getDebitFrequencyOptionData(),
                                                getDocumentEnumOptionData(loanId));
                        return data;
                }catch (EmptyResultDataAccessException ex){
                        return null;
                }
        }

        @Override
        public Collection<MandatesSummaryData> retrieveMandateSummary(final Long officeId, final Boolean includeChildOffices,
                final Date fromDate, final Date toDate) {
                try{
                        MandateSummaryDataMapper mapper = new MandateSummaryDataMapper();
                        String sql = mapper.schema();
                        sql += " where m.request_date between ? and ? ";
                        if(includeChildOffices){
                                sql += " and o.hierarchy like concat((select off.hierarchy from m_office as off where off.id = ?),'%') ";
                        }else{
                                sql += " and o.id = ? ";
                        }
                        sql += " group by m.mandate_status_enum ";
                        return jdbcTemplate.query(sql,
                                new Object[]{yyyyMMddFormat.format(fromDate), yyyyMMddFormat.format(toDate), officeId}, mapper);
                }catch (EmptyResultDataAccessException ex){
                        return new ArrayList<>();
                }
        }

        @Override
        public Page<MandateData> retrieveAllMandates(Long officeId, Boolean includeChildOffices, Date fromDate, Date toDate,
                Integer offset, Integer limit) {
                MandateDataMapper mapper = new MandateDataMapper();
                String sql = " select SQL_CALC_FOUND_ROWS  " + mapper.schemaWithOfficeJoin();
                sql += " where m.request_date between ? and ? ";
                if(includeChildOffices){
                        sql += " and o.hierarchy like concat((select off.hierarchy from m_office as off where off.id = ?),'%') ";
                }else{
                        sql += " and o.id = ? ";
                }
                sql += " order by m.request_date desc ";
                sql += " limit " + limit;
                sql += " offset " + offset;

                final String sqlCountRows = "SELECT FOUND_ROWS()";
                return this.mandateDataPaginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sql,
                        new Object[]{yyyyMMddFormat.format(fromDate), yyyyMMddFormat.format(toDate), officeId}, mapper);
        }

        @Override
        public Collection<MandateData> retrieveMandates(final Long loanId) {
                try{
                        MandateDataMapper mapper = new MandateDataMapper();
                        String sql = "select " + mapper.schema() + " where l.id = ? ";
                        return jdbcTemplate.query(sql, new Object[]{loanId}, mapper);
                }catch (EmptyResultDataAccessException ex){
                        return new ArrayList<>();
                }
        }

        @Override
        public Collection<MandateData> retrieveRequestedMandates(final Long officeId, final Boolean includeChildOffices) {
                try{
                        MandateDataMapper mapper = new MandateDataMapper();
                        String sql = "select " + mapper.schemaWithOfficeJoin();
                        sql += " where m.mandate_status_enum in (100, 200, 300) ";
                        if(includeChildOffices){
                                sql += " and o.hierarchy like concat((select off.hierarchy from m_office as off where off.id = ?),'%') ";
                        }else{
                                sql += " and o.id = ? ";
                        }
                        return jdbcTemplate.query(sql, new Object[]{officeId}, mapper);
                }catch (EmptyResultDataAccessException ex){
                        return new ArrayList<>();
                }
        }

        @Override
        public Collection<MandateData> retrieveMandatesWithStatus(final Long loanId, final Integer[] statuses) {
                try{
                        MandateDataMapper mapper = new MandateDataMapper();
                        String sql = "select " + mapper.schema() + " where l.id = ? and m.mandate_status_enum in (?) ";
                        return jdbcTemplate.query(sql, new Object[]{loanId, StringUtils.join(statuses, ',')}, mapper);
                }catch (EmptyResultDataAccessException ex){
                        return new ArrayList<>();
                }
        }

        private static class MandateDataMapper implements RowMapper<MandateData> {
                public String schema(){
                        StringBuilder sql = new StringBuilder(" m.id as id, ")
                                .append("m.loan_id as loanId, ")
                                .append("l.account_no as loanAccountNo, ")
                                .append("m.mandate_status_enum as mandateStatusEnum, ")
                                .append("m.request_date as requestDate, ")
                                .append("m.umrn as umrn, ")
                                .append("m.bank_account_holder_name as bankAccountHolderName, ")
                                .append("m.bank_name as bankName, ")
                                .append("m.branch_name as branchName, ")
                                .append("m.bank_account_number as bankAccountNumber, ")
                                .append("m.micr as micr, ")
                                .append("m.ifsc as ifsc, ")
                                .append("m.account_type_enum as accountTypeEnum, ")
                                .append("m.period_from_date as periodFromDate, ")
                                .append("m.period_to_date as periodToDate, ")
                                .append("m.period_until_cancelled as periodUntilCancelled, ")
                                .append("m.debit_type_enum as debitTypeEnum, ")
                                .append("m.amount as amount, ")
                                .append("m.debit_frequency_enum as debitFrequencyEnum, ")
                                .append("m.scanned_document_id as scannedDocumentId, ")
                                .append("d.name as scannedDocumentName, ")
                                .append("m.return_reason as returnReason, ")
                                .append("m.return_process_date as returnProcessDate, ")
                                .append("m.return_process_reference_id as returnProcessReferenceId, ")
                                .append("c.display_name as applicantName, ")
                                .append("c.mobile_no as applicantMobileNo, ")
                                .append("c.email_id as applicantEmail ")
                                .append("from f_loan_mandates as m ")
                                .append("left join m_loan as l on l.id = m.loan_id ")
                                .append("left join m_client as c on l.client_id = c.id ")
                                .append("left join m_document as d on d.id = m.scanned_document_id ");
                        return sql.toString();
                }

                public String schemaWithOfficeJoin(){
                        StringBuilder sql = new StringBuilder(" m.id as id, ")
                                .append("m.loan_id as loanId, ")
                                .append("l.account_no as loanAccountNo, ")
                                .append("m.mandate_status_enum as mandateStatusEnum, ")
                                .append("m.request_date as requestDate, ")
                                .append("m.umrn as umrn, ")
                                .append("m.bank_account_holder_name as bankAccountHolderName, ")
                                .append("m.bank_name as bankName, ")
                                .append("m.branch_name as branchName, ")
                                .append("m.bank_account_number as bankAccountNumber, ")
                                .append("m.micr as micr, ")
                                .append("m.ifsc as ifsc, ")
                                .append("m.account_type_enum as accountTypeEnum, ")
                                .append("m.period_from_date as periodFromDate, ")
                                .append("m.period_to_date as periodToDate, ")
                                .append("m.period_until_cancelled as periodUntilCancelled, ")
                                .append("m.debit_type_enum as debitTypeEnum, ")
                                .append("m.amount as amount, ")
                                .append("m.debit_frequency_enum as debitFrequencyEnum, ")
                                .append("m.scanned_document_id as scannedDocumentId, ")
                                .append("d.name as scannedDocumentName, ")
                                .append("m.return_reason as returnReason, ")
                                .append("m.return_process_date as returnProcessDate, ")
                                .append("m.return_process_reference_id as returnProcessReferenceId, ")
                                .append("c.display_name as applicantName, ")
                                .append("c.mobile_no as applicantMobileNo, ")
                                .append("c.email_id as applicantEmail ")
                                .append("from f_loan_mandates as m ")
                                .append("left join m_loan as l on l.id = m.loan_id ")
                                .append("left join m_document as d on d.id = m.scanned_document_id ")
                                .append("left join m_client as c on l.client_id = c.id ")
                                .append("left join m_office as o on c.office_id = o.id ");
                        return sql.toString();
                }

                @Override
                public MandateData mapRow(ResultSet rs, int rowNum) throws SQLException {
                        final Long id = JdbcSupport.getLong(rs, "id");
                        final Long loanId = JdbcSupport.getLong(rs, "loanId");
                        final String loanAccountNo = rs.getString("loanAccountNo");
                        final Integer mandateStatusEnum = rs.getInt("mandateStatusEnum");
                        final EnumOptionData mandateStatus = MandateStatusEnum.enumOptionDataFrom(mandateStatusEnum);
                        final Date requestDate = rs.getDate("requestDate");
                        final String umrn = rs.getString("umrn");
                        final String bankAccountHolderName = rs.getString("bankAccountHolderName");
                        final String bankName = rs.getString("bankName");
                        final String branchName = rs.getString("branchName");
                        final String bankAccountNumber = rs.getString("bankAccountNumber");
                        final String micr = rs.getString("micr");
                        final String ifsc = rs.getString("ifsc");
                        final Integer accountTypeEnum = rs.getInt("accountTypeEnum");
                        final EnumOptionData accountType = AccountTypeEnum.enumOptionDataFrom(accountTypeEnum);
                        final Date periodFromDate = rs.getDate("periodFromDate");
                        final Date periodToDate = rs.getDate("periodToDate");
                        final Boolean periodUntilCancelled = rs.getBoolean("periodUntilCancelled");
                        final Integer debitTypeEnum = rs.getInt("debitTypeEnum");
                        final EnumOptionData debitType = DebitTypeEnum.enumOptionDataFrom(debitTypeEnum);
                        final BigDecimal amount = rs.getBigDecimal("amount");
                        final Integer debitFrequencyEnum = rs.getInt("debitFrequencyEnum");
                        final EnumOptionData debitFrequency = DebitFrequencyEnum.enumOptionDataFrom(debitFrequencyEnum);
                        final Long scannedDocumentId = JdbcSupport.getLong(rs, "scannedDocumentId");
                        final String scannedDocumentName = rs.getString("scannedDocumentName");
                        final String returnReason = rs.getString("returnReason");
                        final Date returnProcessDate = rs.getDate("returnProcessDate");
                        final String returnProcessReferenceId = rs.getString("returnProcessReferenceId");
                        final String applicantName = rs.getString("applicantName") ;
                        final String applicantMobileNo = rs.getString("applicantMobileNo") ;
                        final String applicantEmail = rs.getString("applicantEmail") ;
                        
                        return MandateData.from(id, loanId, loanAccountNo, applicantName, applicantMobileNo, applicantEmail, mandateStatus, requestDate, umrn, bankAccountHolderName,
                                bankName, branchName, bankAccountNumber, micr, ifsc, accountType, periodFromDate, periodToDate,
                                periodUntilCancelled, debitType, amount, debitFrequency, scannedDocumentId, scannedDocumentName,
                                returnReason, returnProcessDate, returnProcessReferenceId);
                }
        }

        private static class MandateSummaryDataMapper implements RowMapper<MandatesSummaryData> {
                public String schema(){
                        StringBuilder sql = new StringBuilder(" select m.mandate_status_enum as mandateStatusEnum, ")
                                .append(" count(*) as count ")
                                .append(" from f_loan_mandates as m ")
                                .append(" left join m_loan as l on l.id = m.loan_id ")
                                .append(" left join m_document as d on d.id = m.scanned_document_id ")
                                .append(" left join m_client as c on l.client_id = c.id ")
                                .append(" left join m_office as o on c.office_id = o.id ");
                        return sql.toString();
                }

                @Override
                public MandatesSummaryData mapRow(ResultSet rs, int rowNum) throws SQLException {
                        final Integer mandateStatusEnum = rs.getInt("mandateStatusEnum");
                        final EnumOptionData mandateStatus = MandateStatusEnum.enumOptionDataFrom(mandateStatusEnum);
                        final Integer count = rs.getInt("count");
                        final BigDecimal amount = null;

                        return new MandatesSummaryData(mandateStatus, count, amount);
                }
        }
}
