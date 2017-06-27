/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.service;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.infrastructure.documentmanagement.data.DocumentData;
import org.apache.fineract.infrastructure.documentmanagement.exception.DocumentNotFoundException;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentReadPlatformService;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
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
    private final BankStatementDetailsReadPlatformService bankStatementDetailsReadPlatformService;
    private final PaginationHelper<BankStatementData> paginationHelper = new PaginationHelper<>();

    @Autowired
    public BankStatementReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final DocumentReadPlatformService documentReadPlatformService, 
            final BankStatementDetailsReadPlatformService bankStatementDetailsReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.documentReadPlatformService = documentReadPlatformService;
        this.bankStatementDetailsReadPlatformService = bankStatementDetailsReadPlatformService;
    }

    private static final class BankStatementMapper implements RowMapper<BankStatementData> {

        public String schema() {

            return " bs.id as id, bs.name as name,bs.description as description, bs.cif_key_document_id as cifKeyDocumentId,"
                    + " bs.org_statement_key_document_id as orgStatementKeyDocumentId, bs.createdby_id as createdById, bs.created_date as createdDate,"
                    + " bs.lastmodifiedby_id as lastModifiedById, bs.is_reconciled as isReconciled, m1.username as lastModifiedByName, bs.lastmodified_date as lastModifiedDate, m.username as createdByName ,"
                    + " d.file_name as cpifFileName , d1.file_name as orgFileName, "
                    + " b.id as bank, b.name as bankName, b.support_simplified_statement as supportSimplifiedStatement, b.gl_account as glAccount, gl.gl_code as glCode " 
                    + " from f_bank_statement  bs "
                    + " join m_appuser m on m.id=bs.createdby_id " + " left join m_document d on d.id = bs.cif_key_document_id "
                    + " left join m_appuser m1 on m1.id = bs.lastmodifiedby_id " + " left join f_bank b on b.id = bs.bank "
                    + " left join acc_gl_account gl on gl.id = b.gl_account "
                    + " left join m_document d1 on d1.id = bs.org_statement_key_document_id ";

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
            final Boolean supportSimplifiedStatement = rs.getBoolean("supportSimplifiedStatement");
            BankData bankData = BankData.instance(bank, bankName, glAccount, glCode, supportSimplifiedStatement);
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

    @Override
    public List<BankStatementData> retrieveAllBankStatements(Integer statementType, Boolean isProcessed) {

        this.context.authenticatedUser();

        final BankStatementMapper rm = new BankStatementMapper();
        List<Object> params = new ArrayList<>();
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(rm.schema());
        sb.append(" where bs.statement_type = ?");
        params.add(statementType);
        if (isProcessed != null) {
            sb.append(" and bs.is_reconciled = ?");
            params.add(isProcessed);
        }
        sb.append(" ORDER BY is_reconciled, created_date DESC ");

        return this.jdbcTemplate.query(sb.toString(), rm, params.toArray());
    }
    
    @Override
    public Page<BankStatementData> retrieveAllBankStatements(Integer statementType, Boolean isProcessed,
            SearchParameters searchParameters) {

        this.context.authenticatedUser();

        final BankStatementMapper rm = new BankStatementMapper();
        List<Object> params = new ArrayList<>();
        final StringBuilder sb = new StringBuilder();
        sb.append("select SQL_CALC_FOUND_ROWS ");
        sb.append(rm.schema());
        sb.append(" where bs.statement_type = ?");
        params.add(statementType);
        if (isProcessed != null) {
            sb.append(" and bs.is_reconciled = ?");
            params.add(isProcessed);
        }

        if (searchParameters.isOrderByRequested()) {
            sb.append(" order by ").append(searchParameters.getOrderBy());

            if (searchParameters.isSortOrderProvided()) {
                sb.append(' ').append(searchParameters.getSortOrder());
            }
        } else {
            sb.append(" ORDER BY is_reconciled, created_date DESC ");
        }

        if (searchParameters.isLimited()) {
            sb.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sb.append(" offset ").append(searchParameters.getOffset());
            }
        }
        final String sqlCountRows = "SELECT FOUND_ROWS()";

        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sb.toString(), params.toArray(), rm);
    }

    @Override
    public BankStatementData getBankStatement(final Long bankStatementId) {
        this.context.authenticatedUser();

        final BankStatementMapper rm = new BankStatementMapper();

        final String sql = "SELECT " + rm.schema() + " WHERE bs.id = ? ";

        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { bankStatementId });
    }
    
    @Override
    public List<BankStatementData> retrieveBankStatementsByAssociatedBank(final Long bankId) {

        this.context.authenticatedUser();

        final BankStatementMapper rm = new BankStatementMapper();

        final String sql = "SELECT " + rm.schema() + " where bs.bank = ? order by bs.id desc ";

        return this.jdbcTemplate.query(sql, rm, new Object[] { bankId });
    }

	@Override
	public BankStatementData getBankStatementSummary(Long bankStatementId) {
		
		final BankStatementData bankStatementData = getBankStatement(bankStatementId);
		List<BankStatementDetailsData> bankStatementDetailsDataList = this.bankStatementDetailsReadPlatformService.retrieveAllBankStatementData(bankStatementId);
		
		BigDecimal portFolioReconciledInflowAmount = BigDecimal.ZERO;
	    BigDecimal portFolioReconciledOutflowAmount = BigDecimal.ZERO;
	    BigDecimal portFolioUnReconciledInflowAmount = BigDecimal.ZERO;
	    BigDecimal portFolioUnReconciledOutflowAmount = BigDecimal.ZERO;
	    
	    BigDecimal nonPortFolioReconciledInflowAmount = BigDecimal.ZERO;
	    BigDecimal nonPortFolioReconciledOutflowAmount = BigDecimal.ZERO;
	    BigDecimal nonPortFolioUnReconciledInflowAmount = BigDecimal.ZERO;
	    BigDecimal nonPortFolioUnReconciledOutflowAmount = BigDecimal.ZERO;
	    
	    BigDecimal miscellaneousReconciledInflowAmount = BigDecimal.ZERO;
	    BigDecimal miscellaneousUnReconciledInflowAmount = BigDecimal.ZERO;
	    BigDecimal miscellaneousReconciledOutflowAmount = BigDecimal.ZERO;
	    BigDecimal miscellaneousUnReconciledOutflowAmount = BigDecimal.ZERO;
	    
		for (BankStatementDetailsData bankStatementDetailsData : bankStatementDetailsDataList) {
			Boolean isReconciled = bankStatementDetailsData.getIsReconciled();
			String transactionType = bankStatementDetailsData.getTransactionType();
			String accountingType = bankStatementDetailsData.getAccountingType();
			BigDecimal amount = bankStatementDetailsData.getAmount();
			
			if(isReconciled){
				if(transactionType.equalsIgnoreCase(ReconciliationApiConstants.OTHER)){
					if(accountingType.equalsIgnoreCase(ReconciliationApiConstants.DEBIT)){
						nonPortFolioReconciledOutflowAmount = nonPortFolioReconciledOutflowAmount.add(amount);											
					}else{
						nonPortFolioReconciledInflowAmount = nonPortFolioReconciledInflowAmount.add(amount);
					}
				}else if(transactionType.equalsIgnoreCase(ReconciliationApiConstants.ERROR)){
					if(MathUtility.isEqualOrGreater(amount, BigDecimal.ZERO)){
						miscellaneousReconciledInflowAmount = miscellaneousReconciledInflowAmount.add(amount);
					}else{
						miscellaneousReconciledOutflowAmount = miscellaneousReconciledOutflowAmount.add(amount);
					}
				}else{
					if(transactionType.equalsIgnoreCase(ReconciliationApiConstants.DISBURSAL)){
						portFolioReconciledOutflowAmount = portFolioReconciledOutflowAmount.add(amount);
					}else{
						portFolioReconciledInflowAmount = portFolioReconciledInflowAmount.add(amount);
					}
				}
			}else{
				if(transactionType.equalsIgnoreCase(ReconciliationApiConstants.OTHER)){
					if(accountingType.equalsIgnoreCase(ReconciliationApiConstants.DEBIT)){
						nonPortFolioUnReconciledOutflowAmount = nonPortFolioUnReconciledOutflowAmount.add(amount);
					}else{						
						nonPortFolioUnReconciledInflowAmount = nonPortFolioUnReconciledInflowAmount.add(amount);
					}
				}else if(transactionType.equalsIgnoreCase(ReconciliationApiConstants.ERROR)){
					if(MathUtility.isEqualOrGreater(amount, BigDecimal.ZERO)){
						miscellaneousUnReconciledInflowAmount = miscellaneousUnReconciledInflowAmount.add(amount);
					}else{
						miscellaneousUnReconciledOutflowAmount = miscellaneousUnReconciledOutflowAmount.add(amount);
					}
				}else{
					if(transactionType.equalsIgnoreCase(ReconciliationApiConstants.DISBURSAL)){
						portFolioUnReconciledOutflowAmount = portFolioUnReconciledOutflowAmount.add(amount);
					}else{
						portFolioUnReconciledInflowAmount = portFolioUnReconciledInflowAmount.add(amount);
					}
				}
			}
		}
		bankStatementData.setPortFolioReconciledInflowAmount(portFolioReconciledInflowAmount);
		bankStatementData.setPortFolioReconciledOutflowAmount(portFolioReconciledOutflowAmount);
		bankStatementData.setPortFolioUnReconciledInflowAmount(portFolioUnReconciledInflowAmount);
		bankStatementData.setPortFolioUnReconciledOutflowAmount(portFolioUnReconciledOutflowAmount);
		
		bankStatementData.setNonPortFolioReconciledInflowAmount(nonPortFolioReconciledInflowAmount);
		bankStatementData.setNonPortFolioReconciledOutflowAmount(nonPortFolioReconciledOutflowAmount);
		bankStatementData.setNonPortFolioUnReconciledInflowAmount(nonPortFolioUnReconciledInflowAmount);
		bankStatementData.setNonPortFolioUnReconciledOutflowAmount(nonPortFolioUnReconciledOutflowAmount);
		
		bankStatementData.setMiscellaneousReconciledInflowAmount(miscellaneousReconciledInflowAmount);
		bankStatementData.setMiscellaneousReconciledOutflowAmount(miscellaneousReconciledOutflowAmount);
		bankStatementData.setMiscellaneousUnReconciledInflowAmount(miscellaneousUnReconciledInflowAmount);
		bankStatementData.setMiscellaneousUnReconciledOutflowAmount(miscellaneousUnReconciledOutflowAmount);
		return bankStatementData;
	}


}
