package com.finflux.reconcilation.bankstatement.service;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.documentmanagement.data.DocumentData;
import org.apache.fineract.infrastructure.documentmanagement.exception.DocumentNotFoundException;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentReadPlatformService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bank.data.BankData;
import com.finflux.reconcilation.bankstatement.data.BankStatementData;
import com.finflux.reconcilation.bankstatement.data.BankStatementDetailsData;

@Service
public class BankStatementReadPlatformServiceImpl implements BankStatementReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final DocumentReadPlatformService documentReadPlatformService;

    @Autowired
    public BankStatementReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final DocumentReadPlatformService documentReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.documentReadPlatformService = documentReadPlatformService;
    }

    private static final class BankStatementMapper implements RowMapper<BankStatementData> {

        public String schema() {

            return " bs.id as id, bs.name as name,bs.description as description, bs.cif_key_document_id as cifKeyDocumentId,"
                    + " bs.org_statement_key_document_id as orgStatementKeyDocumentId, bs.createdby_id as createdById, bs.created_date as createdDate,"
                    + " bs.lastmodifiedby_id as lastModifiedById, bs.is_reconciled as isReconciled, m1.username as lastModifiedByName, bs.lastmodified_date as lastModifiedDate, m.username as createdByName ,"
                    + " d.file_name as cpifFileName , d1.file_name as orgFileName, "
                    + " b.id as bank, b.name as bankName, b.gl_account as glAccount, gl.gl_code as glCode " + " from f_bank_statement  bs "
                    + " join m_appuser m on m.id=bs.createdby_id " + " left join m_document d on d.id = bs.cif_key_document_id "
                    + " left join m_appuser m1 on m1.id = bs.lastmodifiedby_id " + " left join f_bank b on b.id = bs.bank "
                    + " left join acc_gl_account gl on gl.id = b.gl_account "
                    + " left join m_document d1 on d1.id = bs.org_statement_key_document_id";

        }

        @Override
        public BankStatementData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String name = rs.getString("name");
            final String description = rs.getString("description");
            final String createdByName = rs.getString("createdByName");
            final Long createdById = rs.getLong("createdById");
            final Date createdDate = rs.getDate("createdDate");
            final Long lastModifiedById = JdbcSupport.getLongDefaultToNullIfZero(rs, "lastModifiedById");
            final String lastModifiedByName = rs.getString("lastModifiedByName");
            final Date lastModifiedDate = rs.getDate("lastModifiedDate");
            final boolean isReconciled = rs.getBoolean("isReconciled");
            final Long cifKeyDocumentId = rs.getLong("cifKeyDocumentId");
            final Long orgStatementKeyDocumentId = rs.getLong("orgStatementKeyDocumentId");
            final String cpifFileName = rs.getString("cpifFileName");
            final String orgFileName = rs.getString("orgFileName");
            final String bankName = rs.getString("bankName");
            final String glCode = rs.getString("glCode");
            final Long bank = JdbcSupport.getLong(rs, "bank");
            final Long glAccount = JdbcSupport.getLong(rs, "glAccount");
            BankData bankData = BankData.instance(bank, bankName, glAccount, glCode);
            return new BankStatementData(id, name, description, cifKeyDocumentId, orgStatementKeyDocumentId, createdById, createdDate,
                    lastModifiedById, lastModifiedDate, createdByName, lastModifiedByName, cpifFileName, orgFileName, isReconciled, bankData);
        }
    }

    @Override
    public File retrieveFile(final Long documentId) {
        try {
            final DocumentData documentData = this.documentReadPlatformService.retrieveDocument(ReconciliationApiConstants.entityName,
                    ReconciliationApiConstants.bankStatementFolder, documentId, false, false);
            final File file = new File(documentData.fileLocation());
            return file;
        } catch (final EmptyResultDataAccessException e) {
            throw new DocumentNotFoundException(ReconciliationApiConstants.entityName, ReconciliationApiConstants.bankStatementFolder,
                    documentId);
        }
    }

    private static final class BankStatementDetailsMapper implements RowMapper<BankStatementDetailsData> {

        public String schema() {
            return " bsd.id as id, bsd.bank_statement_id as bankStatementId, bsd.description as "
                    + " description,bsd.transaction_type as bankStatementTransactionType, bsd.transaction_id as transactionId, bsd.mobile_number as mobileNumber,"
                    + " bsd.amount as amount, bsd.transaction_date as transactionDate, bsd.client_account_number as "
                    + " clientAccountNumber, bsd.loan_transaction as loanTransaction, bsd.loan_account_number as loanAccountNumber,"
                    + " bsd.group_external_id as groupExternalId, bsd.is_reconciled as isReconciled, "
                    + " bankStatementDetailoffice.id as branch, gl.name as glAccount, bsd.gl_code as glCode, bankStatementDetailoffice.name as branchName, bankStatementDetailoffice.external_id as branchExternalId, "
                    + " bsd.accounting_type as accountingType, bsd.is_journal_entry as isJournalEntry, "
                    + " tr.id as loanTransactionId, tr.transaction_type_enum as transactionType, "
                    + " tr.transaction_date as `date`, tr.amount as transactionAmount, tr.office_id as "
                    + " officeId, tr.external_id as externalId, l.submittedon_date as submittedOnDate"
                    + " ,pt.value as paymentTypeName, pd.payment_type_id as paymentType, pd.account_number "
                    + " as accountNumber,pd.check_number as checkNumber,  pd.receipt_number as receiptNumber, "
                    + " b.id as bank, b.name as bankName, gl.gl_code as bankGLCode , bankGLCode.id as bankGLAccount, "
                    + " pd.bank_number as bankNumber,office.name as officeName,pd.routing_code as routingCode, l.account_no as loanAccountNo "
                    + " from f_bank_statement_details bsd " + " left join m_loan_transaction tr on tr.id = bsd.loan_transaction "
                    + " left join m_loan l on tr.loan_id = l.id " + " left JOIN m_payment_detail pd ON  tr.payment_detail_id = pd.id "
                    + " left join m_payment_type pt on pd.payment_type_id = pt.id "
                    + " left join m_office office on office.id=tr.office_id "
                    + " left join m_office bankStatementDetailoffice on bankStatementDetailoffice.external_id=bsd.branch_external_id "
                    + " left join f_bank_statement bs on bs.id=bsd.bank_statement_id " + " left join f_bank b on b.id=bs.bank "
                    + " left join acc_gl_account gl on gl.gl_code=bsd.gl_code "
                    + " left join acc_gl_account bankGLCode on bankGLCode.id = b.gl_account ";

        }

        @Override
        public BankStatementDetailsData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String clientAccountNumber = rs.getString("clientAccountNumber");
            final String description = rs.getString("description");
            final String loanAccountNumber = rs.getString("loanAccountNumber");
            final String transactionId = rs.getString("transactionId");
            final String bankStatementTransactionType = rs.getString("bankStatementTransactionType");
            final String mobileNumber = rs.getString("mobileNumber");
            final Long bankStatementId = rs.getLong("bankStatementId");
            final BigDecimal amount = rs.getBigDecimal("amount");
            final Date transactionDate = rs.getDate("transactionDate");
            final boolean isReconciled = rs.getBoolean("isReconciled");
            final Long loanTransaction = rs.getLong("loanTransaction");

            final Long loanTransactionId = rs.getLong("loanTransactionId");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            int transactionTypeInt = 0;
            LoanTransactionEnumData transactionType = null;
            PaymentDetailData paymentDetailData = null;

            if (JdbcSupport.getInteger(rs, "transactionType") != null) {
                transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
                transactionType = LoanEnumerations.transactionType(transactionTypeInt);
                if (transactionType.isPaymentOrReceipt()) {
                    final Long paymentTypeId = JdbcSupport.getLong(rs, "paymentType");
                    if (paymentTypeId != null) {
                        final String typeName = rs.getString("paymentTypeName");
                        final PaymentTypeData paymentType = PaymentTypeData.instance(paymentTypeId, typeName);
                        final String accountNumber = rs.getString("accountNumber");
                        final String checkNumber = rs.getString("checkNumber");
                        final String routingCode = rs.getString("routingCode");
                        final String receiptNumber = rs.getString("receiptNumber");
                        final String bankNumber = rs.getString("bankNumber");
                        paymentDetailData = new PaymentDetailData(id, paymentType, accountNumber, checkNumber, routingCode, receiptNumber,
                                bankNumber);
                    }
                }
            }
            final LocalDate date = JdbcSupport.getLocalDate(rs, "date");
            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final BigDecimal transactionAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "transactionAmount");
            final String externalId = rs.getString("externalId");
            final String loanAccountNo = rs.getString("loanAccountNo");
            final Long branch = JdbcSupport.getLong(rs, "branch");
            final String glAccount = rs.getString("glAccount");
            final String accountingType = rs.getString("accountingType");
            final String glCode = rs.getString("glCode");
            final Boolean isJournalEntry = rs.getBoolean("isJournalEntry");

            final Long bank = JdbcSupport.getLong(rs, "bank");
            final String bankName = rs.getString("bankName");
            final String bankGLCode = rs.getString("bankGLCode");
            final String branchName = rs.getString("branchName");
            final String branchExternalId = rs.getString("branchExternalId");
            final String groupExternalId = rs.getString("groupExternalId");
            final Long bankGLAccount = JdbcSupport.getLong(rs, "bankGLAccount");
            BankData bankData = BankData.instance(bank, bankName, bankGLAccount, bankGLCode);

            return new BankStatementDetailsData(id, bankStatementId, transactionId, transactionDate, description, amount, mobileNumber,
                    clientAccountNumber, loanAccountNumber, isReconciled, loanTransaction, loanTransactionId, officeId, officeName,
                    transactionType, date, paymentDetailData, transactionAmount, externalId, submittedOnDate, loanAccountNo, branch,
                    glAccount, accountingType, glCode, isJournalEntry, bankData, branchName, bankStatementTransactionType,
                    branchExternalId, groupExternalId);

        }
    }

    @Override
    public List<BankStatementData> retrieveAllBankStatements() {

        this.context.authenticatedUser();

        final BankStatementMapper rm = new BankStatementMapper();

        final String sql = "SELECT " + rm.schema() + " ORDER BY is_reconciled, created_date DESC ";

        return this.jdbcTemplate.query(sql, rm);
    }

    @Override
    public List<BankStatementDetailsData> retrieveBankStatementDetailsData(final Long bankStatementId, String command) {

        this.context.authenticatedUser();

        final BankStatementDetailsMapper rm = new BankStatementDetailsMapper();

        String appendedConditionString = "";

        if (!command.equalsIgnoreCase(ReconciliationApiConstants.JOURNAL_COMMAND_PARAMETER)) {
            appendedConditionString = " and bsd.is_journal_entry = false ORDER BY bsd.is_reconciled, bsd.transaction_date DESC ";
        } else {
            appendedConditionString = "and bsd.is_journal_entry = true  ORDER BY bsd.transaction_date DESC";
        }

        final String sql = "SELECT " + rm.schema() + " WHERE bsd.bank_statement_id = ? " + appendedConditionString;

        return this.jdbcTemplate.query(sql, rm, new Object[] { bankStatementId });
    }

    @Override
    public BankStatementData getBankStatement(final Long bankStatementId) {
        this.context.authenticatedUser();

        final BankStatementMapper rm = new BankStatementMapper();

        final String sql = "SELECT " + rm.schema() + " WHERE bs.id = ? ";

        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { bankStatementId });
    }

    @Override
    public List<BankStatementDetailsData> changedBankStatementDetailsData(Long bankStatementId) {

        this.context.authenticatedUser();

        final BankStatementDetailsMapper rm = new BankStatementDetailsMapper();

        final String sql = "SELECT "
                + rm.schema()
                + " WHERE bsd.bank_statement_id = ? and (bsd.is_reconciled = true or (bsd.is_journal_entry = true and bsd.transaction_id IS NOT NULL)) "
                + " ORDER BY bsd.transaction_date DESC ";

        return this.jdbcTemplate.query(sql, rm, new Object[] { bankStatementId });
    }

    @Override
    public List<BankStatementData> retrieveBankStatementsByAssociatedBank(final Long bankId) {

        this.context.authenticatedUser();

        final BankStatementMapper rm = new BankStatementMapper();

        final String sql = "SELECT " + rm.schema() + " where bs.bank = ? order by bs.id desc ";

        return this.jdbcTemplate.query(sql, rm, new Object[] { bankId });
    }

}
