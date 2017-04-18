/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.accounting.journalentry.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.accounting.common.AccountingEnumerations;
import org.apache.fineract.accounting.financialactivityaccount.domain.FinancialActivityAccount;
import org.apache.fineract.accounting.financialactivityaccount.domain.FinancialActivityAccountRepositoryWrapper;
import org.apache.fineract.accounting.glaccount.data.GLAccountData;
import org.apache.fineract.accounting.glaccount.domain.GLAccountType;
import org.apache.fineract.accounting.glaccount.service.GLAccountReadPlatformService;
import org.apache.fineract.accounting.journalentry.data.JournalEntryAssociationParametersData;
import org.apache.fineract.accounting.journalentry.data.JournalEntryData;
import org.apache.fineract.accounting.journalentry.data.JournalEntryDetailData;
import org.apache.fineract.accounting.journalentry.data.OfficeOpeningBalancesData;
import org.apache.fineract.accounting.journalentry.data.TransactionDetailData;
import org.apache.fineract.accounting.journalentry.data.TransactionTypeEnumData;
import org.apache.fineract.accounting.journalentry.exception.JournalEntriesNotFoundException;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.portfolio.account.PortfolioAccountType;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionEnumData;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.note.data.NoteData;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionEnumData;
import org.apache.fineract.portfolio.savings.service.SavingsEnumerations;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class JournalEntryReadPlatformServiceImpl implements JournalEntryReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final GLAccountReadPlatformService glAccountReadPlatformService;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final FinancialActivityAccountRepositoryWrapper financialActivityAccountRepositoryWrapper;

    private final PaginationHelper<JournalEntryData> paginationHelper = new PaginationHelper<>();

    @Autowired
    public JournalEntryReadPlatformServiceImpl(final RoutingDataSource dataSource,
            final GLAccountReadPlatformService glAccountReadPlatformService, final OfficeReadPlatformService officeReadPlatformService,
            final FinancialActivityAccountRepositoryWrapper financialActivityAccountRepositoryWrapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.glAccountReadPlatformService = glAccountReadPlatformService;
        this.officeReadPlatformService = officeReadPlatformService;
        this.financialActivityAccountRepositoryWrapper = financialActivityAccountRepositoryWrapper;
    }

    private static final class GLJournalEntryMapper implements RowMapper<JournalEntryData> {

        private final JournalEntryAssociationParametersData associationParametersData;
        private final JournalEntryDetailDataMapper journalEntryDetailDataMapper = new JournalEntryDetailDataMapper();

        public GLJournalEntryMapper(final JournalEntryAssociationParametersData associationParametersData) {
            if (associationParametersData == null) {
                this.associationParametersData = new JournalEntryAssociationParametersData();
            } else {
                this.associationParametersData = associationParametersData;
            }
        }

        public String schema() {
            StringBuilder sb = new StringBuilder();
            sb.append(" journalEntry.id as id, glAccount.classification_enum as classification ,")
                    .append("journalEntry.transaction_identifier as transactionId,")
                    .append(" glAccount.name as glAccountName, glAccount.gl_code as glAccountCode,glAccount.id as glAccountId, ")
                    .append(" journalEntry.office_id as officeId, office.name as officeName, journalEntry.ref_num as referenceNumber, ")
                    .append(" journalEntry.manual_entry as manualEntry,journalEntry.entry_date as transactionDate, ")
                    .append("journalEntry.value_date as valueDate,journalEntry.effective_date as effectiveDate,")
                    .append(" journalEntryDetail.type_enum as entryType,journalEntryDetail.amount as amount, ")
                    .append(" journalEntry.entity_type_enum as entityType, journalEntry.entity_id as entityId, ")
                    .append(" journalEntry.entity_transaction_id as entityTransactionId,")
                     .append("creatingUser.id as createdByUserId, ")
                    .append(" creatingUser.username as createdByUserName, journalEntry.description as comments, ")
                    .append(" journalEntry.created_date as createdDate, journalEntry.reversed as reversed, ")
                    .append(" journalEntry.currency_code as currencyCode, curr.name as currencyName, curr.internationalized_name_code as currencyNameCode, ")
                    .append(" curr.display_symbol as currencyDisplaySymbol, curr.decimal_places as currencyDigits, curr.currency_multiplesof as inMultiplesOf ");
            if (associationParametersData.isTransactionDetailsRequired()) {
                sb.append(" ,pd.receipt_number as receiptNumber, ").append(" pd.check_number as checkNumber, ")
                        .append(" pd.account_number as accountNumber, ").append(" pt.value as paymentTypeName, ")
                        .append(" pd.payment_type_id as paymentTypeId,").append(" pd.bank_number as bankNumber, ")
                        .append(" pd.routing_code as routingCode, ").append(" note.id as noteId, ")
                        .append(" note.note as transactionNote, ").append(" lt.transaction_type_enum as loanTransactionType, ")
                        .append(" st.transaction_type_enum as savingsTransactionType ");
            }
            sb.append(" from f_journal_entry as journalEntry ")
                    .append(" join f_journal_entry_detail as journalEntryDetail on journalEntryDetail.journal_entry_id = journalEntry.id")
                    .append(" left join acc_gl_account as glAccount on glAccount.id = journalEntryDetail.account_id")
                    .append(" left join m_office as office on office.id = journalEntry.office_id")
                    .append(" left join m_appuser as creatingUser on creatingUser.id = journalEntry.createdby_id ")
                    .append(" join m_currency curr on curr.code = journalEntry.currency_code ");
            if (associationParametersData.isTransactionDetailsRequired()) {
                sb.append(" left join m_loan_transaction as lt on journalEntry.entity_type_enum = ? and  journalEntry.entity_transaction_id = lt.id ")
                        .append(" left join m_savings_account_transaction as st on journalEntry.entity_type_enum = ? and journalEntry.entity_transaction_id = st.id ")
                        .append(" left join m_payment_detail as pd on lt.payment_detail_id = pd.id or st.payment_detail_id = pd.id or journalEntry.payment_details_id = pd.id")
                        .append(" left join m_payment_type as pt on pt.id = pd.payment_type_id ")
                        .append(" left join m_note as note on lt.id = note.loan_transaction_id or st.id = note.savings_account_transaction_id ");
            }
            return sb.toString();

        }

        @Override
        public JournalEntryData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");

            final LocalDate transactionDate = JdbcSupport.getLocalDate(rs, "transactionDate");
            final LocalDate valueDate = JdbcSupport.getLocalDate(rs, "valueDate");
            final LocalDate effectiveDate = JdbcSupport.getLocalDate(rs, "effectiveDate");

            final Boolean manualEntry = rs.getBoolean("manualEntry");

            final String transactionId = rs.getString("transactionId");
            final Integer entityTypeId = JdbcSupport.getInteger(rs, "entityType");
            EnumOptionData entityType = null;
            if (entityTypeId != null) {
                entityType = AccountingEnumerations.portfolioProductType(entityTypeId);

            }

            final Long entityId = JdbcSupport.getLong(rs, "entityId");
            final Long entityTransactionId = JdbcSupport.getLong(rs, "entityTransactionId");
            final Long createdByUserId = rs.getLong("createdByUserId");
            final LocalDate createdDate = JdbcSupport.getLocalDate(rs, "createdDate");
            final String createdByUserName = rs.getString("createdByUserName");
            final String comments = rs.getString("comments");
            final Boolean reversed = rs.getBoolean("reversed");
            final String referenceNumber = rs.getString("referenceNumber");

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);

            TransactionDetailData transactionDetailData = null;

            if (associationParametersData.isTransactionDetailsRequired()) {
                PaymentDetailData paymentDetailData = null;
                final Long paymentTypeId = JdbcSupport.getLong(rs, "paymentTypeId");
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
                NoteData noteData = null;
                final Long noteId = JdbcSupport.getLong(rs, "noteId");
                if (noteId != null) {
                    final String note = rs.getString("transactionNote");
                    noteData = new NoteData(noteId, null, null, null, null, null, null, null, note, null, null, null, null, null, null, null, null);
                }
                Long transaction = null;
                if (entityType != null) {
                    transaction = Long.parseLong(transactionId.substring(1).trim());
                }

                TransactionTypeEnumData transactionTypeEnumData = null;

                if (PortfolioAccountType.fromInt(entityTypeId).isLoanAccount()) {
                    final LoanTransactionEnumData loanTransactionType = LoanEnumerations.transactionType(JdbcSupport.getInteger(rs,
                            "loanTransactionType"));
                    transactionTypeEnumData = new TransactionTypeEnumData(loanTransactionType.id(), loanTransactionType.getCode(),
                            loanTransactionType.getValue());
                } else if (PortfolioAccountType.fromInt(entityTypeId).isSavingsAccount()) {
                    final SavingsAccountTransactionEnumData savingsTransactionType = SavingsEnumerations.transactionType(JdbcSupport
                            .getInteger(rs, "savingsTransactionType"));
                    transactionTypeEnumData = new TransactionTypeEnumData(savingsTransactionType.getId(), savingsTransactionType.getCode(),
                            savingsTransactionType.getValue());
                }

                transactionDetailData = new TransactionDetailData(transaction, paymentDetailData, noteData, transactionTypeEnumData);
            }
            Collection<JournalEntryDetailData> journalEntryDetails = new ArrayList<>();

            JournalEntryDetailData journalEntryDetailData = this.journalEntryDetailDataMapper.mapRow(rs, rowNum);
            journalEntryDetails.add(journalEntryDetailData);
            while (rs.next()) {
                if (id.equals(rs.getLong("id"))) {
                    journalEntryDetails.add(this.journalEntryDetailDataMapper.mapRow(rs, rowNum));
                } else {
                    rs.previous();
                    break;
                }
            }

            return new JournalEntryData(id, officeId, officeName, transactionDate, valueDate, effectiveDate, transactionId, manualEntry,
                    entityType, entityId, entityTransactionId, createdByUserId, createdDate, createdByUserName, comments, reversed,
                    referenceNumber, transactionDetailData, currency, journalEntryDetails);
        }
    }
    
    
    private static final class GLAccountDataMapper implements RowMapper<GLAccountData> {

        @Override
        public GLAccountData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            final String glCode = rs.getString("glAccountCode");
            final String glAccountName = rs.getString("glAccountName");
            final Long glAccountId = rs.getLong("glAccountId");
            final int accountTypeId = JdbcSupport.getInteger(rs, "classification");
            final EnumOptionData accountType = AccountingEnumerations.gLAccountType(accountTypeId);
            return GLAccountData.createFrom(glAccountId, glAccountName, glCode, accountType);
        }
        
    }
    
    private static final class JournalEntryDetailDataMapper implements RowMapper<JournalEntryDetailData> {

        GLAccountDataMapper gLAccountDataMapper = new GLAccountDataMapper();

        @Override
        public JournalEntryDetailData mapRow(ResultSet rs, int rowNum) throws SQLException {
            GLAccountData accountData = this.gLAccountDataMapper.mapRow(rs, rowNum);
            final BigDecimal amount = rs.getBigDecimal("amount");
            final int entryTypeId = JdbcSupport.getInteger(rs, "entryType");
            final EnumOptionData entryType = AccountingEnumerations.journalEntryType(entryTypeId);
            return new JournalEntryDetailData(accountData, amount, entryType);
        }

    }

    @Override
    public Page<JournalEntryData> retrieveAll(final SearchParameters searchParameters, final Long glAccountId,
            final Boolean onlyManualEntries, final Date fromDate, final Date toDate, final String transactionId, final Integer entityType,
            final JournalEntryAssociationParametersData associationParametersData) {

        GLJournalEntryMapper rm = new GLJournalEntryMapper(associationParametersData);
        List<Object> paramList = new ArrayList<>();

        String sql = constructSqlForPaginatedJournalEntry(searchParameters, glAccountId, onlyManualEntries, fromDate, toDate,
                transactionId, entityType, associationParametersData, paramList);

        final Object[] finalObjectArray = paramList.toArray();
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sql, finalObjectArray, rm);
    }
    
    
    private String constructSqlForPaginatedJournalEntry(final SearchParameters searchParameters, final Long glAccountId,
            final Boolean onlyManualEntries, final Date fromDate, final Date toDate, final String transactionId, final Integer entityType,
            final JournalEntryAssociationParametersData associationParametersData, final List<Object> paramList) {

        StringBuilder sb = new StringBuilder();
        sb.append("select SQL_CALC_FOUND_ROWS ");
        sb.append(" journalEntry.id as id, glAccount.classification_enum as classification ,")
                .append("journalEntry.transaction_identifier as transactionId,")
                .append(" glAccount.name as glAccountName, glAccount.gl_code as glAccountCode,glAccount.id as glAccountId, ")
                .append(" journalEntry.office_id as officeId, office.name as officeName, journalEntry.ref_num as referenceNumber, ")
                .append(" journalEntry.manual_entry as manualEntry,journalEntry.entry_date as transactionDate, ")
                .append("journalEntry.value_date as valueDate,journalEntry.effective_date as effectiveDate,")
                .append(" journalEntryDetail.type_enum as entryType,journalEntryDetail.amount as amount, ")
                .append(" journalEntry.entity_type_enum as entityType, journalEntry.entity_id as entityId, ")
                .append(" journalEntry.entity_transaction_id as entityTransactionId,")
                .append("creatingUser.id as createdByUserId, ")
                .append(" creatingUser.username as createdByUserName, journalEntry.description as comments, ")
                .append(" journalEntry.created_date as createdDate, journalEntry.reversed as reversed, ")
                .append(" journalEntry.currency_code as currencyCode, curr.name as currencyName, curr.internationalized_name_code as currencyNameCode, ")
                .append(" curr.display_symbol as currencyDisplaySymbol, curr.decimal_places as currencyDigits, curr.currency_multiplesof as inMultiplesOf ");
        if (associationParametersData.isTransactionDetailsRequired()) {
            sb.append(" ,pd.receipt_number as receiptNumber, ").append(" pd.check_number as checkNumber, ")
                    .append(" pd.account_number as accountNumber, ").append(" pt.value as paymentTypeName, ")
                    .append(" pd.payment_type_id as paymentTypeId,").append(" pd.bank_number as bankNumber, ")
                    .append(" pd.routing_code as routingCode, ").append(" note.id as noteId, ").append(" note.note as transactionNote, ")
                    .append(" lt.transaction_type_enum as loanTransactionType, ")
                    .append(" st.transaction_type_enum as savingsTransactionType ");
        }

        sb.append(" from (select DISTINCT je.* from f_journal_entry je inner join f_journal_entry_detail jed on je.id = jed.journal_entry_id ");

        String whereClose = " where ";

        if (StringUtils.isNotBlank(transactionId)) {
            sb.append(whereClose + " je.transaction_identifier = ?");
            paramList.add(transactionId);
            whereClose = " and ";
        }

        if (entityType != null && entityType != 0 && (onlyManualEntries == null)) {
            sb.append(whereClose + " je.entity_type_enum = ?");
            paramList.add(entityType);
            whereClose = " and ";
        }

        if (searchParameters.isOfficeIdPassed()) {
            sb.append(whereClose + " je.office_id = ?");
            paramList.add(searchParameters.getOfficeId());
            whereClose = " and ";
        }

        if (searchParameters.isCurrencyCodePassed()) {
            sb.append(whereClose + " je.currency_code = ?");
            paramList.add(searchParameters.getCurrencyCode());
            whereClose = " and ";
        }

        if (glAccountId != null && glAccountId != 0) {
            sb.append(whereClose + " jed.account_id = ?");
            paramList.add(glAccountId);
            whereClose = " and ";
        }

        if (fromDate != null || toDate != null) {
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String fromDateString = null;
            String toDateString = null;
            if (fromDate != null && toDate != null) {
                sb.append(whereClose + " je.entry_date between ? and ? ");
                fromDateString = df.format(fromDate);
                toDateString = df.format(toDate);
                paramList.add(fromDateString);
                paramList.add(toDateString);
            } else if (fromDate != null) {
                sb.append(whereClose + " je.entry_date >= ? ");
                fromDateString = df.format(fromDate);
                paramList.add(fromDateString);

            } else if (toDate != null) {
                sb.append(whereClose + " je.entry_date <= ? ");
                toDateString = df.format(toDate);
                paramList.add(toDateString);
            }
            whereClose = " and ";

        }

        if (onlyManualEntries != null) {
            if (onlyManualEntries) {
                sb.append(whereClose + " je.manual_entry = 1");
                whereClose = " and ";
            }
        }

        if (searchParameters.isLoanIdPassed()) {
            sb.append(whereClose + " je.entity_transaction_id  in (select id from m_loan_transaction where loan_id = ?)");
            paramList.add(searchParameters.getLoanId());
            whereClose = " and ";
        }
        if (searchParameters.isSavingsIdPassed()) {
            sb.append(whereClose
                    + " je.entity_transaction_id in (select id from m_savings_account_transaction where savings_account_id = ?)");
            paramList.add(searchParameters.getSavingsId());
            whereClose = " and ";
        }

        if (searchParameters.isLimited()) {
            sb.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sb.append(" offset ").append(searchParameters.getOffset());
            }
        }

        sb.append(" ) as journalEntry ")
                .append(" join f_journal_entry_detail as journalEntryDetail on journalEntryDetail.journal_entry_id = journalEntry.id")
                .append(" left join acc_gl_account as glAccount on glAccount.id = journalEntryDetail.account_id")
                .append(" left join m_office as office on office.id = journalEntry.office_id")
                .append(" left join m_appuser as creatingUser on creatingUser.id = journalEntry.createdby_id ")
                .append(" join m_currency curr on curr.code = journalEntry.currency_code ");
        if (associationParametersData.isTransactionDetailsRequired()) {
            sb.append(
                    " left join m_loan_transaction as lt on journalEntry.entity_type_enum = ? and  journalEntry.entity_transaction_id = lt.id ")
                    .append(" left join m_savings_account_transaction as st on journalEntry.entity_type_enum = ? and journalEntry.entity_transaction_id = st.id ")
                    .append(" left join m_payment_detail as pd on lt.payment_detail_id = pd.id or st.payment_detail_id = pd.id or journalEntry.payment_details_id = pd.id")
                    .append(" left join m_payment_type as pt on pt.id = pd.payment_type_id ")
                    .append(" left join m_note as note on lt.id = note.loan_transaction_id or st.id = note.savings_account_transaction_id ");
        }

        if (associationParametersData.isTransactionDetailsRequired()) {
            paramList.add(PortfolioAccountType.LOAN.getValue());
            paramList.add(PortfolioAccountType.SAVINGS.getValue());
        }

        whereClose = " where ";
        if (glAccountId != null && glAccountId != 0) {
            sb.append(whereClose + " journalEntryDetail.account_id = ?");
            paramList.add(glAccountId);
            whereClose = " and ";
        }

        if (searchParameters.isOrderByRequested()) {
            sb.append(" order by ").append(searchParameters.getOrderBy());

            if (searchParameters.isSortOrderProvided()) {
                sb.append(' ').append(searchParameters.getSortOrder());
            }
        } else {
            sb.append(" order by journalEntry.entry_date, journalEntry.id");
        }

        return sb.toString();

    }

    @Override
    public JournalEntryData retrieveGLJournalEntryById(final long glJournalEntryId,
            JournalEntryAssociationParametersData associationParametersData) {
        try {

            final GLJournalEntryMapper rm = new GLJournalEntryMapper(associationParametersData);
            final String sql = "select " + rm.schema() + " where journalEntry.id = ?";

            final JournalEntryData glJournalEntryData = this.jdbcTemplate.queryForObject(sql, rm, new Object[] { glJournalEntryId });

            return glJournalEntryData;
        } catch (final EmptyResultDataAccessException e) {
            throw new JournalEntriesNotFoundException(glJournalEntryId);
        }
    }

    @Override
    public OfficeOpeningBalancesData retrieveOfficeOpeningBalances(final Long officeId, String currencyCode) {

        final FinancialActivityAccount financialActivityAccountId = this.financialActivityAccountRepositoryWrapper
                .findByFinancialActivityTypeWithNotFoundDetection(300);
        final Long contraId = financialActivityAccountId.getGlAccount().getId();
        if (contraId == null) { throw new GeneralPlatformDomainRuleException(
                "error.msg.financial.activity.mapping.opening.balance.contra.account.cannot.be.null",
                "office-opening-balances-contra-account value can not be null", "office-opening-balances-contra-account"); }

        final JournalEntryAssociationParametersData associationParametersData = new JournalEntryAssociationParametersData();
        final GLAccountData contraAccount = this.glAccountReadPlatformService.retrieveGLAccountById(contraId, associationParametersData);
        if (!GLAccountType.fromInt(contraAccount.getTypeId()).isEquityType()) { throw new GeneralPlatformDomainRuleException(
                "error.msg.configuration.opening.balance.contra.account.value.is.invalid.account.type",
                "Global configuration 'office-opening-balances-contra-account' value is not an equity type account", contraId); }

        final OfficeData officeData = this.officeReadPlatformService.retrieveOffice(officeId);
        final List<GLAccountData> allOpeningTransactions = populateAllTransactionsFromGLAccounts(contraId);
        final String contraTransactionId = retrieveContraAccountTransactionId(officeId, contraId, currencyCode);

        List<JournalEntryData> existingOpeningBalanceTransactions = new ArrayList<>();
        if (StringUtils.isNotBlank(contraTransactionId)) {
            existingOpeningBalanceTransactions = retrieveOfficeBalanceTransactions(officeId, contraTransactionId, currencyCode);
        }
        final List<JournalEntryDetailData> transactions = populateOpeningBalances(existingOpeningBalanceTransactions, allOpeningTransactions);
        final List<JournalEntryDetailData> assetAccountOpeningBalances = new ArrayList<>();
        final List<JournalEntryDetailData> liabityAccountOpeningBalances = new ArrayList<>();
        final List<JournalEntryDetailData> incomeAccountOpeningBalances = new ArrayList<>();
        final List<JournalEntryDetailData> equityAccountOpeningBalances = new ArrayList<>();
        final List<JournalEntryDetailData> expenseAccountOpeningBalances = new ArrayList<>();

        for (final JournalEntryDetailData journalEntryDetailData : transactions) {
            final GLAccountType type = GLAccountType.fromInt(journalEntryDetailData.getGlAccountData().getType().getId().intValue());
            if (type.isAssetType()) {
                assetAccountOpeningBalances.add(journalEntryDetailData);
            } else if (type.isLiabilityType()) {
                liabityAccountOpeningBalances.add(journalEntryDetailData);
            } else if (type.isEquityType()) {
                equityAccountOpeningBalances.add(journalEntryDetailData);
            } else if (type.isIncomeType()) {
                incomeAccountOpeningBalances.add(journalEntryDetailData);
            } else if (type.isExpenseType()) {
                expenseAccountOpeningBalances.add(journalEntryDetailData);
            }
        }

        final LocalDate transactionDate = DateUtils.getLocalDateOfTenant();

        final OfficeOpeningBalancesData officeOpeningBalancesData = OfficeOpeningBalancesData.createNew(officeId, officeData.name(),
                transactionDate, contraAccount, assetAccountOpeningBalances, liabityAccountOpeningBalances, incomeAccountOpeningBalances,
                equityAccountOpeningBalances, expenseAccountOpeningBalances);
        return officeOpeningBalancesData;
    }

    private List<JournalEntryDetailData> populateOpeningBalances(final List<JournalEntryData> existingOpeningBalanceTransactions,
            final List<GLAccountData> allOpeningTransactions) {
        final List<JournalEntryDetailData> allOpeningBalnceTransactions = new ArrayList<>(allOpeningTransactions.size());
        for (final GLAccountData glAccountData : allOpeningTransactions) {
            boolean isNewTransactionAddedToCollection = false;
            for (final JournalEntryData existingOpeningBalanceTransaction : existingOpeningBalanceTransactions) {
                for (JournalEntryDetailData journalEntryDetailData : existingOpeningBalanceTransaction.getJournalEntryDetails()) {
                    if (glAccountData.getId().equals(journalEntryDetailData.getGlAccountData().getId())) {
                        allOpeningBalnceTransactions.add(journalEntryDetailData);
                        isNewTransactionAddedToCollection = true;
                        break;
                    }
                }
            }
            if (!isNewTransactionAddedToCollection) {
                JournalEntryDetailData data = JournalEntryDetailData.createWithGlAccountData(glAccountData);
                allOpeningBalnceTransactions.add(data);
            }
        }
        return allOpeningBalnceTransactions;
    }

    private List<GLAccountData> populateAllTransactionsFromGLAccounts(final Long contraId) {
        final List<GLAccountData> glAccounts = this.glAccountReadPlatformService.retrieveAllEnabledDetailGLAccounts();
        final List<GLAccountData> nonContraglAccounts = new ArrayList<>();
        for (final GLAccountData glAccountData : glAccounts) {
            if (!contraId.equals(glAccountData.getId())) {
                nonContraglAccounts.add(glAccountData);
            }
        }
        return nonContraglAccounts;
    }

    private List<JournalEntryData> retrieveOfficeBalanceTransactions(final Long officeId, final String transactionId,
            final String currencyCode) {
        final Long contraId = null;
        return retrieveContraTransactions(officeId, contraId, transactionId, currencyCode).getPageItems();
    }

    private String retrieveContraAccountTransactionId(final Long officeId, final Long contraId, final String currencyCode) {
        final String transactionId = "";
        final Page<JournalEntryData> contraJournalEntries = retrieveContraTransactions(officeId, contraId, transactionId, currencyCode);
        if (!CollectionUtils.isEmpty(contraJournalEntries.getPageItems())) {
            final JournalEntryData contraTransaction = contraJournalEntries.getPageItems().get(
                    contraJournalEntries.getPageItems().size() - 1);
            return contraTransaction.getTransactionId();
        }
        return transactionId;
    }

    private Page<JournalEntryData> retrieveContraTransactions(final Long officeId, final Long contraId, final String transactionId,
            final String currencyCode) {
        final Integer offset = 0;
        final Integer limit = null;
        final String orderBy = "journalEntry.id";
        final String sortOrder = "ASC";
        final Integer entityType = null;
        final Boolean onlyManualEntries = null;
        final Date fromDate = null;
        final Date toDate = null;
        final JournalEntryAssociationParametersData associationParametersData = null;
        final Long loanId = null;
        final Long savingsId = null;

        final SearchParameters searchParameters = SearchParameters.forJournalEntries(officeId, offset, limit, orderBy, sortOrder, loanId,
                savingsId, currencyCode);
        return retrieveAll(searchParameters, contraId, onlyManualEntries, fromDate, toDate, transactionId, entityType,
                associationParametersData);

    }

    @Override
    public Page<JournalEntryData> retrieveJournalEntriesByEntityId(String transactionId, Long entityId, Integer entityType) {
        JournalEntryAssociationParametersData associationParametersData = new JournalEntryAssociationParametersData(true, true);
        try {
            final GLJournalEntryMapper rm = new GLJournalEntryMapper(associationParametersData);
            final String sql = "select " + rm.schema()
                    + " where journalEntry.transaction_identifier = ? and journalEntry.entity_id = ? and journalEntry.entity_type_enum = ?";
            final String sqlCountRows = "SELECT FOUND_ROWS()";
            Object[] data = { transactionId, entityId, entityType };
            return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sql, data, rm);
        } catch (final EmptyResultDataAccessException e) {
            throw new JournalEntriesNotFoundException(entityId);
        }
    }

    @Override
    public List<String> findNonReversedContraTansactionIds(final Long contraId, final Long officeId) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DISTINCT j.transaction_identifier");
        sql.append(" FROM f_journal_entry j");
        sql.append(" join f_journal_entry_detail je on j.id = je.journal_entry_id");
        sql.append(" left join f_journal_entry j2 on  j.id = j2.reversal_id");
        sql.append(" WHERE j.office_id = ? AND je.account_id = ? AND j.reversed IS FALSE AND j2.id is null");
        return this.jdbcTemplate.queryForList(sql.toString(), String.class, officeId, contraId);
    }
    
    @Override
    public List<String> findNonContraTansactionIds(final Long contraId, final Long officeId) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DISTINCT j.transaction_identifier");
        sql.append(" FROM f_journal_entry j");
        sql.append(" left join f_journal_entry_detail je on j.id = je.journal_entry_id and je.account_id = ?");
        sql.append(" WHERE j.reversed = FALSE AND j.office_id = ? and je.id is null");
        return this.jdbcTemplate.queryForList(sql.toString(), String.class, contraId, officeId);
    }
    
    @Override
    public boolean hasJournalEntryForAccount(final long glAccountId) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT COUNT(je.account_id) AS size");
        sql.append(" FROM f_journal_entry_detail je");
        sql.append(" WHERE je.account_id = ?");
        long size = this.jdbcTemplate.queryForObject(sql.toString(), Long.class,glAccountId);
        return size > 0;
    }
}
