/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.reconcilation.bankstatement.data.BankStatementDetailsData;
import com.finflux.reconcilation.bankstatement.domain.BankStatement;
import com.finflux.reconcilation.bankstatement.domain.BankStatementDetailType;
import com.finflux.reconcilation.bankstatement.domain.BankStatementRepositoryWrapper;
import com.google.gson.Gson;

@Service
public class BankStatementDetailsReadPlatformServiceImpl implements BankStatementDetailsReadPlatformService{
	
	private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final BankLoanTransactionsReadPlatformService bankLoanTransactionsReadPlatformService;
    private final BankStatementRepositoryWrapper bankStatementReadPlatformService;

    @Autowired
    public BankStatementDetailsReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
    		final BankLoanTransactionsReadPlatformService bankLoanTransactionsReadPlatformService,
    		final BankStatementRepositoryWrapper bankStatementReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.bankLoanTransactionsReadPlatformService = bankLoanTransactionsReadPlatformService;
        this.bankStatementReadPlatformService = bankStatementReadPlatformService;
    }
    

    private static final class BankStatementDetailsNonPortfolioMapper implements RowMapper<BankStatementDetailsData> {
    	
        public String schema() {
            return " bsd.id as id, bsd.bank_statement_id as bankStatementId, "+
            		" bsd.transaction_id as transactionId, bsd.amount as amount, "+
            		" bsd.transaction_date as transactionDate, bsd.accounting_type as accountingType, "+
            		" bsd.gl_code as glCode, bsd.is_reconciled as isReconciled, office.name as branchName, office.id as branch, bsd.branch_external_id as branchExternalId, "+
            		" gl.name as glAccount "+
            		" from f_bank_statement_details bsd "+
            		" join f_bank_statement bs on (bs.id=bsd.bank_statement_id and bsd.bank_statement_id = ? and bsd.bank_statement_detail_type = "+BankStatementDetailType.NONPORTFOLIO.getValue()+" )"+
            		" left join f_bank b on b.id=bs.bank "+
            		" left join m_office office on office.external_id=bsd.branch_external_id"+
            		" left join acc_gl_account gl on (gl.gl_code=bsd.gl_code )";
        }

        @Override
        public BankStatementDetailsData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String transactionId = rs.getString("transactionId");
            final Long bankStatementId = rs.getLong("bankStatementId");
            final BigDecimal amount = rs.getBigDecimal("amount");
            final Date transactionDate = rs.getDate("transactionDate");
            final Long branch = JdbcSupport.getLong(rs, "branch");
            final String glAccount = rs.getString("glAccount");
            final String accountingType = rs.getString("accountingType");
            final String branchName = rs.getString("branchName");
            final String glCode = rs.getString("glCode");
            final String branchExternalId = rs.getString("branchExternalId");
            final boolean isReconciled = rs.getBoolean("isReconciled");
            return new BankStatementDetailsData(id, bankStatementId, transactionId, transactionDate,
        			amount, branchExternalId, branch,glAccount,branchName,accountingType,glCode, isReconciled);

        }
    }
    
    private static final class BankStatementDetailsGeneratePortfolioMapper implements RowMapper<BankStatementDetailsData> {
        
        public String schema() {
            return " bsd.id as id, bsd.bank_statement_id as bankStatementId, "+
                        " bsd.transaction_id as transactionId, bsd.amount as amount, "+
                        " bsd.transaction_date as transactionDate, bsd.is_error as isError, bsd.receipt_number as receiptNumber , bsd.description as description, bsd.transaction_type as transactionType, bsd.loan_account_number as loanAccountNumber "+
                        " from f_bank_statement_details bsd "+
                        " where bsd.bank_statement_id = ? ";
        }

        @Override
        public BankStatementDetailsData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long bankStatementId = rs.getLong("bankStatementId");
            final String transactionId = rs.getString("transactionId");
            final BigDecimal amount = rs.getBigDecimal("amount");
            final Date transactionDate = rs.getDate("transactionDate");
            final String description = rs.getString("description");
            final String transactionType = rs.getString("transactionType");
            final String loanAccountNumber = rs.getString("loanAccountNumber");
            final String receiptNumber = rs.getString("receiptNumber");
            final Boolean isError = rs.getBoolean("isError");
            return BankStatementDetailsData.generatePortfolioTransactions(id, bankStatementId, transactionId, transactionDate, amount, description, transactionType, loanAccountNumber, receiptNumber, isError);

        }
    }
    
    
    private static final class BankStatementDetailsDataMapper implements RowMapper<BankStatementDetailsData> {

   	 public String schema() {
       	
       return	" bsd.id as id, bsd.bank_statement_id as bankStatementId, bsd.description as "+
       		" description, bsd.mobile_number as mobileNumber, bsd.is_manual_reconciled as isManualReconciled, "+
       		" bsd.amount as amount, bsd.transaction_date as transactionDate,bsd.transaction_id as transactionId, "+
       		" bsd.transaction_type as transactionType, bsd.is_reconciled as isReconciled , "+
       		" bsd.loan_account_number as loanAccountNumber, bsd.accounting_type as accountingType,"+
       		" bsd.group_external_id as groupExternalId,bsd.loan_transaction as loanTransactionId "+
       		" from f_bank_statement_details bsd "+
       		" where bsd.bank_statement_id = ? ";
       }

       @Override
       public BankStatementDetailsData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

           final Long id = JdbcSupport.getLong(rs, "id");
           final String description = rs.getString("description");
           final String loanAccountNumber = rs.getString("loanAccountNumber");
           final String transactionType = rs.getString("transactionType");
           final String accountingType = rs.getString("accountingType");
           final String mobileNumber = rs.getString("mobileNumber");
           final Long bankStatementId = rs.getLong("bankStatementId");
           final BigDecimal amount = rs.getBigDecimal("amount");
           final Date transactionDate = rs.getDate("transactionDate");
           final Long loanTransactionId = rs.getLong("loanTransactionId");
           final String groupExternalId = rs.getString("groupExternalId");
           final boolean isReconciled = rs.getBoolean("isReconciled");
           final boolean isManualReconciled = rs.getBoolean("isManualReconciled");
           final String transactionId = rs.getString("transactionId");
           return BankStatementDetailsData.reconciledData(id, bankStatementId, transactionDate, description,
           		amount, mobileNumber, loanAccountNumber, transactionType, groupExternalId, loanTransactionId,
           		isReconciled, accountingType, isManualReconciled, transactionId);

       }
   }
   

    private static final class BankStatementDetailsRevertMapper implements RowMapper<BankStatementDetailsData> {
    	
        public String schema() {
            return " bsd.id as id, bsd.bank_statement_id as bankStatementId,bsd.is_manual_reconciled as isManualReconciled, "+
            		" bsd.transaction_id as transactionId, bsd.loan_transaction as loanTransaction, "+
            		" bsd.bank_statement_detail_type as bankStatementDetailType, bsd.loan_account_number as loanAccountNumber from f_bank_statement_details bsd "+
            		" where bsd.bank_statement_id = ? and  bsd.is_reconciled = 1 ";
        }

        @Override
        public BankStatementDetailsData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long bankStatementId = rs.getLong("bankStatementId");
            final String transactionId = rs.getString("transactionId");
            final Long loanTransaction = JdbcSupport.getLong(rs, "loanTransaction");
            final Integer bankStatementDetailType = JdbcSupport.getInteger(rs, "bankStatementDetailType");
            final String loanAccountNumber = rs.getString("loanAccountNumber");
            final boolean isManualReconciled = rs.getBoolean("isManualReconciled");
            return new BankStatementDetailsData(id, bankStatementId, transactionId, loanTransaction, 
            		bankStatementDetailType, loanAccountNumber, isManualReconciled);

        }
    } 
     
    
	@Override
	public List<BankStatementDetailsData> changedBankStatementDetailsData(
			Long bankStatementId) {
		
		this.context.authenticatedUser();

        final BankStatementDetailsRevertMapper rm = new BankStatementDetailsRevertMapper();

        final String sql = "SELECT " + rm.schema() ;
        
        return  this.jdbcTemplate.query(sql, rm, new Object[] { bankStatementId });
        
	}

	@Override
	public List<BankStatementDetailsData> retrieveBankStatementDetailsReconciledData(Long bankStatementId) {
		
		this.context.authenticatedUser();

        final BankStatementDetailsDataMapper rm = new BankStatementDetailsDataMapper();

        final String sql = "SELECT " + rm.schema() +"  and bsd.bank_statement_detail_type = "+ BankStatementDetailType.PORTFOLIO.getValue()+" and bsd.is_reconciled = 1 order by bsd.updated_date desc ";
        
        List<BankStatementDetailsData> bankStatementDataList = this.jdbcTemplate.query(sql, rm, new Object[] { bankStatementId });
        for (BankStatementDetailsData bankStatementData : bankStatementDataList) {
        	bankStatementData.setLoanTransactionData(this.bankLoanTransactionsReadPlatformService.getReconciledLoanTransaction(bankStatementData.getLoanTransactionId()));
		}
        return bankStatementDataList;
	}

    @Override
    public List<BankStatementDetailsData> retrieveBankStatementDetailsDataForReconcile(Long bankStatementId) {

        this.context.authenticatedUser();

        final BankStatementDetailsDataMapper rm = new BankStatementDetailsDataMapper();

        final String sql = "SELECT " + rm.schema() + "  and bsd.bank_statement_detail_type = "
                + BankStatementDetailType.PORTFOLIO.getValue()
                + " and bsd.is_reconciled = 0 and bsd.loan_account_number IS NULL order by bsd.transaction_date, bsd.amount ";

        List<BankStatementDetailsData> bankStatementDataList = this.jdbcTemplate.query(sql, rm, new Object[] { bankStatementId });
        for (BankStatementDetailsData bankStatementData : bankStatementDataList) {
            List<LoanTransactionData> loanTxnData = this.bankLoanTransactionsReadPlatformService
                    .getLoanTransactionOptions(bankStatementData);
            if (loanTxnData != null) {
                bankStatementData.setOptionsLength(loanTxnData.size());
                if (loanTxnData.size() == 1) {
                    bankStatementData.setLoanTransactionData(loanTxnData.get(0));
                    ;
                } else {
                    bankStatementData.setLoanTransactionOptions(loanTxnData);
                }
            }
        }
        return bankStatementDataList;
    }

	@Override
	public List<BankStatementDetailsData> retrieveBankStatementNonPortfolioData(
			Long bankStatementId) {
		
		this.context.authenticatedUser();

        final BankStatementDetailsNonPortfolioMapper rm = new BankStatementDetailsNonPortfolioMapper();

        final String sql = "SELECT " + rm.schema();
        
        return this.jdbcTemplate.query(sql, rm, new Object[] { bankStatementId });
        
	}

	@Override
	public List<BankStatementDetailsData> retrieveBankStatementMiscellaneousData(
			Long bankStatementId) {
		
		this.context.authenticatedUser();

        final BankStatementDetailsDataMapper rm = new BankStatementDetailsDataMapper();

        final String sql = "SELECT " + rm.schema() +"  and bsd.bank_statement_detail_type = "+ BankStatementDetailType.MISCELLANEOUS.getValue()+" ";
        
        return this.jdbcTemplate.query(sql, rm, new Object[] { bankStatementId });
        
	}

	@Override
	public List<BankStatementDetailsData> retrieveAllBankStatementData(
			Long bankStatementId) {
		
		this.context.authenticatedUser();

        final BankStatementDetailsDataMapper rm = new BankStatementDetailsDataMapper();

        final String sql = "SELECT " + rm.schema();
        
        return this.jdbcTemplate.query(sql, rm, new Object[] { bankStatementId });
	}

    @Override
    public List<BankStatementDetailsData> retrieveGeneratePortfolioData(Long bankStatementId, String searchCriteria) {
        
        final BankStatementDetailsGeneratePortfolioMapper rm = new BankStatementDetailsGeneratePortfolioMapper();

        final String sql = "SELECT " + rm.schema() +" and bsd.loan_account_number IS NOT NULL "+searchCriteria;
        
        return this.jdbcTemplate.query(sql, rm, new Object[] { bankStatementId });
        
    }

	@Override
	public String getBankStatementDetails(
			List<BankStatementDetailsData> bankStatementDetailData, final Long bankStatementId) {
		HashMap<String, Object> responseData = new HashMap<>();
        Gson gson = new Gson();     
        BankStatement bankStatement = this.bankStatementReadPlatformService.findOneWithNotFoundDetection(bankStatementId);
        final String sql = "SELECT count(*) from f_bank_statement_details bsd where bsd.bank_statement_id = ? and bsd.loan_account_number IS NULL and bsd.bank_statement_detail_type = "+ BankStatementDetailType.PORTFOLIO.getValue();
        int totalTransactions = this.jdbcTemplate.queryForObject(
                sql, new Object[] { bankStatementId }, Integer.class);
        responseData.put("totalTransactions", totalTransactions);
        responseData.put("bankName", bankStatement.getBank().getName());
        responseData.put("bankStatementName", bankStatement.getName());
        responseData.put("bankStatementDetails", bankStatementDetailData);
        return gson.toJson(responseData);
	}

}
