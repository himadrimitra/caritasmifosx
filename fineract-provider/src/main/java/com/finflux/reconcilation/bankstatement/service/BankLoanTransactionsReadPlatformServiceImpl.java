/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bankstatement.data.BankStatementDetailsData;

@Service
public class BankLoanTransactionsReadPlatformServiceImpl implements BankLoanTransactionsReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public BankLoanTransactionsReadPlatformServiceImpl(final RoutingDataSource dataSource, final PlatformSecurityContext context) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    private static final class LoanTransactionDataMapper implements RowMapper<LoanTransactionData> {

   	 public String schema() {
       	
       return	" tr.id AS id, tr.transaction_type_enum AS transactionType, tr.transaction_date AS transactionDate,"+
       " tr.amount AS amount, office.id as officeId,office.name AS officeName,g.external_id AS groupExternalId,l.account_no AS loanAccountNumber"+
       " FROM m_loan l "+" JOIN m_loan_transaction tr ON tr.loan_id = l.id and tr.id = ? "+
       " LEFT JOIN m_office office ON office.id=tr.office_id "+
       " LEFT JOIN m_group g ON g.id=l.group_id ";

       }

     @Override
     public LoanTransactionData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

    	 final Long id = JdbcSupport.getLong(rs, "id");
           final String loanAccountNumber = rs.getString("loanAccountNumber");
           LoanTransactionEnumData transactionType = null;
           if (JdbcSupport.getInteger(rs, "transactionType") != null) {
               int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
               transactionType = LoanEnumerations.transactionType(transactionTypeInt);
           }
           final String officeName = rs.getString("officeName");
           final String groupExternalId = rs.getString("groupExternalId");            
           final BigDecimal amount = rs.getBigDecimal("amount");
           final LocalDate transactionDate = JdbcSupport.getLocalDate(rs, "transactionDate");
           final Long officeId = rs.getLong("officeId");
           
           return LoanTransactionData.LoanTransactionDataForReconciliationLoanTransactionData(id,
        		   officeId, officeName, transactionType, transactionDate, amount, groupExternalId, loanAccountNumber);

       }
   }
 
    private static final class LoanTransactionDataOptionsMapper implements RowMapper<LoanTransactionData> {

      	 public String schema() {
          	
          return	" tr.id AS id, tr.transaction_type_enum AS transactionType, tr.transaction_date AS transactionDate,"+
          " tr.amount AS amount, office.id as officeId,office.name AS officeName,g.external_id AS groupExternalId,l.account_no AS loanAccountNumber"+
          " FROM m_loan l "+" JOIN m_loan_transaction tr ON (tr.loan_id = l.id and tr.transaction_type_enum in (1,2,8) and tr.is_reversed = 0 and tr.is_reconciled = 0 ) "+
          " LEFT JOIN m_office office ON office.id=tr.office_id "+
          " LEFT JOIN m_group g ON g.id=l.group_id ";

          }

        @Override
        public LoanTransactionData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

       	 final Long id = JdbcSupport.getLong(rs, "id");
              final String loanAccountNumber = rs.getString("loanAccountNumber");
              LoanTransactionEnumData transactionType = null;
              if (JdbcSupport.getInteger(rs, "transactionType") != null) {
                  int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
                  transactionType = LoanEnumerations.transactionType(transactionTypeInt);
              }
              final String officeName = rs.getString("officeName");
              final String groupExternalId = rs.getString("groupExternalId");            
              final BigDecimal amount = rs.getBigDecimal("amount");
              final LocalDate transactionDate = JdbcSupport.getLocalDate(rs, "transactionDate");
              final Long officeId = rs.getLong("officeId");
              
              return LoanTransactionData.LoanTransactionDataForReconciliationLoanTransactionData(id,
           		   officeId, officeName, transactionType, transactionDate, amount, groupExternalId, loanAccountNumber);

          }
      }

	@Override
	public LoanTransactionData getReconciledLoanTransaction(
			Long loanTransactionId) {
		try {
            this.context.authenticatedUser();

            final LoanTransactionDataMapper rm = new LoanTransactionDataMapper();

            final String sql = "select " + rm.schema();

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { loanTransactionId });
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
	}

	@Override
	public List<LoanTransactionData> getLoanTransactionOptions(BankStatementDetailsData bankStatementDetailData) {
		try {
            this.context.authenticatedUser();

            final LoanTransactionDataOptionsMapper rm = new LoanTransactionDataOptionsMapper();

            String sql = "select " + rm.schema()+ " where tr.amount = ? and g.external_id = ? and tr.transaction_date = \'"+bankStatementDetailData.getTransactionDate()+"\' ";

            if(bankStatementDetailData.getTransactionType().equalsIgnoreCase(ReconciliationApiConstants.DISBURSAL)){
            	sql = sql + " and tr.transaction_type_enum = ? ";
            	return this.jdbcTemplate.query(sql, rm, new Object[] { bankStatementDetailData.getAmount(), bankStatementDetailData.getGroupExternalId(),
            			LoanTransactionType.DISBURSEMENT.getValue() });
            }
            sql = sql + " and tr.transaction_type_enum in (? , ?) ";
            return this.jdbcTemplate.query(sql, rm, new Object[] { bankStatementDetailData.getAmount(), bankStatementDetailData.getGroupExternalId(),
            			LoanTransactionType.REPAYMENT.getValue(),LoanTransactionType.RECOVERY_REPAYMENT.getValue() });

        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
	}
}
