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
package org.apache.fineract.portfolio.accountdetails.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.accountdetails.PaymentDetailCollectionData;
import org.apache.fineract.portfolio.accountdetails.SharesAccountBalanceCollectionData;
import org.apache.fineract.portfolio.accountdetails.data.AccountSummaryCollectionData;
import org.apache.fineract.portfolio.accountdetails.data.LoanAccountSummaryData;
import org.apache.fineract.portfolio.accountdetails.data.MpesaTransactionSummaryData;
import org.apache.fineract.portfolio.accountdetails.data.SavingsAccountSummaryData;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.group.service.GroupReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.data.LoanApplicationTimelineData;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargeSummaryData;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.service.LoanChargeReadPlatformServiceImpl;
import org.apache.fineract.portfolio.loanproduct.service.LoanEnumerations;
import org.apache.fineract.portfolio.paymentdetail.PaymentDetailConstants;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.paymenttype.service.PaymentTypeReadPlatformService;
import org.apache.fineract.portfolio.savings.data.SavingsAccountApplicationTimelineData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountStatusEnumData;
import org.apache.fineract.portfolio.savings.data.SavingsChargesSummaryData;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.apache.fineract.portfolio.savings.service.SavingsAccountChargeReadPlatformServiceImpl;
import org.apache.fineract.portfolio.savings.service.SavingsEnumerations;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class AccountDetailsReadPlatformServiceJpaRepositoryImpl implements AccountDetailsReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final ClientReadPlatformService clientReadPlatformService;
    private final GroupReadPlatformService groupReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final SavingsAccountRepository savingsAccountRepository;
    private final LoanRepository loanRepository;
    private final LoanChargeReadPlatformServiceImpl loanChargeReadPlatformServiceImpl;
    private final SavingsAccountChargeReadPlatformServiceImpl savingsAccountChargeReadPlatformServiceImpl;
    private final PaymentTypeReadPlatformService paymentTypeReadPlatformService;


    @Autowired
    public AccountDetailsReadPlatformServiceJpaRepositoryImpl(final ClientReadPlatformService clientReadPlatformService,
            final RoutingDataSource dataSource, final GroupReadPlatformService groupReadPlatformService,
            final CodeValueReadPlatformService codeValueReadPlatformService,
            final SavingsAccountRepository savingsAccountRepository,
            final LoanRepository loanRepository,
            final LoanChargeReadPlatformServiceImpl loanChargeReadPlatformServiceImpl,
            final SavingsAccountChargeReadPlatformServiceImpl savingsAccountChargeReadPlatformServiceImpl,
            final PaymentTypeReadPlatformService paymentTypeReadPlatformService) {
        this.clientReadPlatformService = clientReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.groupReadPlatformService = groupReadPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.savingsAccountRepository=savingsAccountRepository;
        this.loanRepository=loanRepository;
        this.loanChargeReadPlatformServiceImpl= loanChargeReadPlatformServiceImpl;
        this.savingsAccountChargeReadPlatformServiceImpl=savingsAccountChargeReadPlatformServiceImpl;
        this.paymentTypeReadPlatformService =paymentTypeReadPlatformService;
    }

    @Override
    public AccountSummaryCollectionData retrieveClientAccountDetails(final Long clientId) {
        // Check if client exists
        this.clientReadPlatformService.retrieveOne(clientId);
        final String loanwhereClause = " where l.client_id = ?  group  by l.account_no";
        final String savingswhereClause = " where sa.client_id = ?  group by sa.account_no order by sa.status_enum ASC, sa.account_no ASC ";
        final List<LoanAccountSummaryData> loanAccounts = retrieveLoanAccountDetails(loanwhereClause, new Object[] { clientId });
        final List<SavingsAccountSummaryData> savingsAccounts = retrieveAccountDetails(savingswhereClause, new Object[] { clientId });
        final Collection<PaymentTypeData> paymentTypeOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
        return new AccountSummaryCollectionData(loanAccounts, savingsAccounts, paymentTypeOptions);
    }

    @Override
    public AccountSummaryCollectionData retrieveGroupAccountDetails(final Long groupId) {
        // Check if group exists
        this.groupReadPlatformService.retrieveOne(groupId);
        final String loanWhereClauseForGroup = " where l.group_id = ? and l.client_id is null";
        final String loanWhereClauseForMembers = " where l.group_id = ? and l.client_id is not null";
        final String savingswhereClauseForGroup = " where sa.group_id = ? and sa.client_id is null order by sa.status_enum ASC, sa.account_no ASC";
        final String savingswhereClauseForMembers = " where sa.group_id = ? and sa.client_id is not null order by sa.status_enum ASC, sa.account_no ASC";
        final List<LoanAccountSummaryData> groupLoanAccounts = retrieveLoanAccountDetails(loanWhereClauseForGroup, new Object[] { groupId });
        final List<SavingsAccountSummaryData> groupSavingsAccounts = retrieveAccountDetails(savingswhereClauseForGroup,
                new Object[] { groupId });
        final List<LoanAccountSummaryData> memberLoanAccounts = retrieveLoanAccountDetails(loanWhereClauseForMembers,
                new Object[] { groupId });
        final List<SavingsAccountSummaryData> memberSavingsAccounts = retrieveAccountDetails(savingswhereClauseForMembers,
                new Object[] { groupId });
        return new AccountSummaryCollectionData(groupLoanAccounts, groupSavingsAccounts, memberLoanAccounts, memberSavingsAccounts, null);
    }

    @Override
    public Collection<LoanAccountSummaryData> retrieveClientLoanAccountsByLoanOfficerId(final Long clientId, final Long loanOfficerId) {
        // Check if client exists
        this.clientReadPlatformService.retrieveOne(clientId);
        final String loanWhereClause = " where l.client_id = ? and l.loan_officer_id = ?";
        return retrieveLoanAccountDetails(loanWhereClause, new Object[] { clientId, loanOfficerId });
    }

    @Override
    public Collection<LoanAccountSummaryData> retrieveGroupLoanAccountsByLoanOfficerId(final Long groupId, final Long loanOfficerId) {
        // Check if group exists
        this.groupReadPlatformService.retrieveOne(groupId);
        final String loanWhereClause = " where l.group_id = ? and l.client_id is null and l.loan_officer_id = ?";
        return retrieveLoanAccountDetails(loanWhereClause, new Object[] { groupId, loanOfficerId });
    }

    private List<LoanAccountSummaryData> retrieveLoanAccountDetails(final String loanwhereClause, final Object[] inputs) {
        final LoanAccountSummaryDataMapper rm = new LoanAccountSummaryDataMapper();
        final String sql = "select " + rm.loanAccountSummarySchema() + loanwhereClause;
        return this.jdbcTemplate.query(sql, rm, inputs);
    }

    /**
     * @param entityId
     * @return
     */
    private List<SavingsAccountSummaryData> retrieveAccountDetails(final String savingswhereClause, final Object[] inputs) {
        final SavingsAccountSummaryDataMapper savingsAccountSummaryDataMapper = new SavingsAccountSummaryDataMapper();
        final String savingsSql = "select " + savingsAccountSummaryDataMapper.schema() + savingswhereClause;
        return this.jdbcTemplate.query(savingsSql, savingsAccountSummaryDataMapper, inputs);
    }

    private Collection<PaymentDetailCollectionData> retrievePaymentDetails( final Object[] inputs) {
        final PaymentDetailDataMapper rm = new PaymentDetailDataMapper();
        final String sql = rm.schemaSql;
        return this.jdbcTemplate.query(sql, rm, inputs);
    }
    private static final class PaymentDetailDataMapper implements RowMapper<PaymentDetailCollectionData> {
      final String schemaSql;
      public PaymentDetailDataMapper(){
          final StringBuilder paymentdetail = new StringBuilder();
          paymentdetail.append("select date_format(c.transaction_date,'%d' '-%b' '-%y') as transaction_date , c. receipt_number,sum(c.amount) as amount,c.loan_id ,c.PaymentType1 from ( ");
          paymentdetail.append("select mlt.transaction_date ,IF (LENGTH(mpd.receipt_number) >0 , mpd.receipt_number , CONCAT('dummy_', mpd.id) )  receipt_number, ");
          paymentdetail.append(" sum(mlt.amount) as amount,mlt.transaction_type_enum,mlt.loan_id, ");
          paymentdetail.append("if(mlt.transaction_type_enum =1,'D','R')as paymentType, ");
          paymentdetail.append("if(mlt.transaction_type_enum=1,'DS','P') as PaymentType1 ");
          paymentdetail.append("from m_client mc ");
          paymentdetail.append("inner join m_loan l on mc.id=l.client_id ");
          paymentdetail.append("inner join m_loan_transaction mlt on mlt.loan_id=l.id ");
          paymentdetail.append(" and mlt.is_reversed=0 and mlt.transaction_type_enum in (2) ");
          paymentdetail.append("left outer join m_payment_detail mpd on mpd.id=mlt.payment_detail_id ");
          paymentdetail.append(" where mc.id=? ");
          paymentdetail.append("group by mpd.receipt_number,mlt.transaction_type_enum,mlt.transaction_date,mlt.loan_id ");
          paymentdetail.append("union ");
          paymentdetail.append("select mst.transaction_date ,IF (LENGTH( mpd.receipt_number) >0 , mpd.receipt_number , CONCAT('dummy_', mpd.id) )  receipt_number, ");
          paymentdetail.append("sum(amount) as amount,mst.transaction_type_enum,mst.savings_account_id, ");
          paymentdetail.append("if(mst.transaction_type_enum=1,'R','W')paymentType, ");
          paymentdetail.append("if (mst.transaction_type_enum=1,'DP','W') paymentType1 ");
          paymentdetail.append("from m_client mc  ");
          paymentdetail.append("inner join m_savings_account s on mc.id=s.client_id ");
          paymentdetail.append("inner join m_savings_account_transaction mst on mst.savings_account_id=s.id ");
          paymentdetail.append(" and mst.transaction_type_enum in(1,2) and  mst.is_reversed=0 ");
          paymentdetail.append("left outer join m_payment_detail mpd on mpd.id=mst.payment_detail_id ");
          paymentdetail.append("where mc.id=?  ");
          paymentdetail.append(" group by mpd.receipt_number,mst.transaction_type_enum,mst.transaction_date,mst.savings_account_id ");
          paymentdetail.append(" )c group by c.transaction_date,c.receipt_number ");
          paymentdetail.append("  order by c.transaction_date desc "); 
          paymentdetail.append(" LIMIT 3");
          this.schemaSql = paymentdetail.toString();
      }
      public String schema() {
          return this.schemaSql;
      }
	@Override
	public PaymentDetailCollectionData mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		final String date = rs.getString("transaction_date");
        final BigDecimal amount = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs,"amount");
        final String type=rs.getString("PaymentType1");
        final String receiptNumber = rs.getString("receipt_number");
        return new PaymentDetailCollectionData(amount, date, receiptNumber,type);                
       
	}
    	
    }
    private static final class SavingsAccountSummaryDataMapper implements RowMapper<SavingsAccountSummaryData> {

        final String schemaSql;

        public SavingsAccountSummaryDataMapper() {
            final StringBuilder accountsSummary = new StringBuilder();
            accountsSummary.append("sa.id as id, sa.account_no as accountNo, sa.external_id as externalId, sa.status_enum as statusEnum, ");
            accountsSummary.append("sa.account_type_enum as accountType, ");
            accountsSummary.append("sa.account_balance_derived as accountBalance, ");

            accountsSummary.append("sa.submittedon_date as submittedOnDate,");
            accountsSummary.append("sbu.username as submittedByUsername,");
            accountsSummary.append("sbu.firstname as submittedByFirstname, sbu.lastname as submittedByLastname,");

            accountsSummary.append("sa.rejectedon_date as rejectedOnDate,");
            accountsSummary.append("rbu.username as rejectedByUsername,");
            accountsSummary.append("rbu.firstname as rejectedByFirstname, rbu.lastname as rejectedByLastname,");

            accountsSummary.append("sa.withdrawnon_date as withdrawnOnDate,");
            accountsSummary.append("wbu.username as withdrawnByUsername,");
            accountsSummary.append("wbu.firstname as withdrawnByFirstname, wbu.lastname as withdrawnByLastname,");

            accountsSummary.append("sa.approvedon_date as approvedOnDate,");
            accountsSummary.append("abu.username as approvedByUsername,");
            accountsSummary.append("abu.firstname as approvedByFirstname, abu.lastname as approvedByLastname,");

            accountsSummary.append("sa.activatedon_date as activatedOnDate,");
            accountsSummary.append("avbu.username as activatedByUsername,");
            accountsSummary.append("avbu.firstname as activatedByFirstname, avbu.lastname as activatedByLastname,");

            accountsSummary.append("sa.closedon_date as closedOnDate,");
            accountsSummary.append("cbu.username as closedByUsername,");
            accountsSummary.append("cbu.firstname as closedByFirstname, cbu.lastname as closedByLastname,");
            
       //     accountsSummary.append(" sum(mac.amount_outstanding_derived) as charges,");


            accountsSummary
                    .append("sa.currency_code as currencyCode, sa.currency_digits as currencyDigits, sa.currency_multiplesof as inMultiplesOf, ");
            accountsSummary.append("curr.name as currencyName, curr.internationalized_name_code as currencyNameCode, ");
            accountsSummary.append("curr.display_symbol as currencyDisplaySymbol, ");
            accountsSummary.append("sa.product_id as productId, p.name as productName, p.short_name as shortProductName, ");
            accountsSummary.append("sa.deposit_type_enum as depositType ");
            accountsSummary.append("from m_savings_account sa ");
            accountsSummary.append("join m_savings_product as p on p.id = sa.product_id ");
            accountsSummary.append("join m_currency curr on curr.code = sa.currency_code ");
            accountsSummary.append("left join m_appuser sbu on sbu.id = sa.submittedon_userid ");
            accountsSummary.append("left join m_appuser rbu on rbu.id = sa.rejectedon_userid ");
            accountsSummary.append("left join m_appuser wbu on wbu.id = sa.withdrawnon_userid ");
            accountsSummary.append("left join m_appuser abu on abu.id = sa.approvedon_userid ");
            accountsSummary.append("left join m_appuser avbu on rbu.id = sa.activatedon_userid ");
            accountsSummary.append("left join m_appuser cbu on cbu.id = sa.closedon_userid ");
         //   accountsSummary.append("left join m_savings_account_charge mac on mac.savings_account_id = sa.id");


            this.schemaSql = accountsSummary.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public SavingsAccountSummaryData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String accountNo = rs.getString("accountNo");
            final String externalId = rs.getString("externalId");
            final Long productId = JdbcSupport.getLong(rs, "productId");
            final String productName = rs.getString("productName");
            final String shortProductName = rs.getString("shortProductName");
            final Integer statusId = JdbcSupport.getInteger(rs, "statusEnum");
            final BigDecimal accountBalance = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "accountBalance");
            final SavingsAccountStatusEnumData status = SavingsEnumerations.status(statusId);
            final Integer accountType = JdbcSupport.getInteger(rs, "accountType");
            final EnumOptionData accountTypeData = AccountEnumerations.loanType(accountType);
            final Integer depositTypeId = JdbcSupport.getInteger(rs, "depositType");
            final EnumOptionData depositTypeData = SavingsEnumerations.depositType(depositTypeId);
          //  final BigDecimal charges =   JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "charges");
            
            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);

            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final String submittedByUsername = rs.getString("submittedByUsername");
            final String submittedByFirstname = rs.getString("submittedByFirstname");
            final String submittedByLastname = rs.getString("submittedByLastname");

            final LocalDate rejectedOnDate = JdbcSupport.getLocalDate(rs, "rejectedOnDate");
            final String rejectedByUsername = rs.getString("rejectedByUsername");
            final String rejectedByFirstname = rs.getString("rejectedByFirstname");
            final String rejectedByLastname = rs.getString("rejectedByLastname");

            final LocalDate withdrawnOnDate = JdbcSupport.getLocalDate(rs, "withdrawnOnDate");
            final String withdrawnByUsername = rs.getString("withdrawnByUsername");
            final String withdrawnByFirstname = rs.getString("withdrawnByFirstname");
            final String withdrawnByLastname = rs.getString("withdrawnByLastname");

            final LocalDate approvedOnDate = JdbcSupport.getLocalDate(rs, "approvedOnDate");
            final String approvedByUsername = rs.getString("approvedByUsername");
            final String approvedByFirstname = rs.getString("approvedByFirstname");
            final String approvedByLastname = rs.getString("approvedByLastname");

            final LocalDate activatedOnDate = JdbcSupport.getLocalDate(rs, "activatedOnDate");
            final String activatedByUsername = rs.getString("activatedByUsername");
            final String activatedByFirstname = rs.getString("activatedByFirstname");
            final String activatedByLastname = rs.getString("activatedByLastname");

            final LocalDate closedOnDate = JdbcSupport.getLocalDate(rs, "closedOnDate");
            final String closedByUsername = rs.getString("closedByUsername");
            final String closedByFirstname = rs.getString("closedByFirstname");
            final String closedByLastname = rs.getString("closedByLastname");

            final SavingsAccountApplicationTimelineData timeline = new SavingsAccountApplicationTimelineData(submittedOnDate,
                    submittedByUsername, submittedByFirstname, submittedByLastname, rejectedOnDate, rejectedByUsername,
                    rejectedByFirstname, rejectedByLastname, withdrawnOnDate, withdrawnByUsername, withdrawnByFirstname,
                    withdrawnByLastname, approvedOnDate, approvedByUsername, approvedByFirstname, approvedByLastname, activatedOnDate,
                    activatedByUsername, activatedByFirstname, activatedByLastname, closedOnDate, closedByUsername, closedByFirstname,
                    closedByLastname);

            return new SavingsAccountSummaryData(id, accountNo, externalId, productId, productName, shortProductName, status, currency, accountBalance,
                    accountTypeData, timeline, depositTypeData);
        }
    }

    private static final class LoanAccountSummaryDataMapper implements RowMapper<LoanAccountSummaryData> {

        public String loanAccountSummarySchema() {

            final StringBuilder accountsSummary = new StringBuilder("l.id as id, l.account_no as accountNo, l.external_id as externalId,");
            accountsSummary
                    .append(" l.product_id as productId, lp.name as productName, lp.short_name as shortProductName, null as chargeDescription, ")
                    .append(" l.loan_status_id as statusId, l.loan_type_enum as loanType,")
                    
                    .append("l.principal_disbursed_derived as originalLoan,")
                    .append("l.principal_outstanding_derived as loanBalance,")
                    .append("l.total_repayment_derived as amountPaid,")
                    
                    .append(" l.loan_product_counter as loanCycle,")

                    .append(" l.submittedon_date as submittedOnDate,")
                    .append(" sbu.username as submittedByUsername, sbu.firstname as submittedByFirstname, sbu.lastname as submittedByLastname,")

                    .append(" l.rejectedon_date as rejectedOnDate,")
                    .append(" rbu.username as rejectedByUsername, rbu.firstname as rejectedByFirstname, rbu.lastname as rejectedByLastname,")

                    .append(" l.withdrawnon_date as withdrawnOnDate,")
                    .append(" wbu.username as withdrawnByUsername, wbu.firstname as withdrawnByFirstname, wbu.lastname as withdrawnByLastname,")

                    .append(" l.approvedon_date as approvedOnDate,")
                    .append(" abu.username as approvedByUsername, abu.firstname as approvedByFirstname, abu.lastname as approvedByLastname,")

                    .append(" l.expected_disbursedon_date as expectedDisbursementDate, l.disbursedon_date as actualDisbursementDate,")
                    .append(" dbu.username as disbursedByUsername, dbu.firstname as disbursedByFirstname, dbu.lastname as disbursedByLastname,")

                    .append(" l.closedon_date as closedOnDate,")
                    .append(" cbu.username as closedByUsername, cbu.firstname as closedByFirstname, cbu.lastname as closedByLastname,")
                    .append(" la.overdue_since_date_derived as overdueSinceDate,")
                    .append(" l.writtenoffon_date as writtenOffOnDate, l.expected_maturedon_date as expectedMaturityDate")
                  //  .append(" sum(mlc.amount_outstanding_derived) as charges")

                    .append(" from m_loan l ").append(" LEFT JOIN m_product_loan AS lp ON lp.id = l.product_id ")
                    .append(" left join m_appuser sbu on sbu.id = l.submittedon_userid")
                    .append(" left join m_appuser rbu on rbu.id = l.rejectedon_userid")
                    .append(" left join m_appuser wbu on wbu.id = l.withdrawnon_userid")
                    .append(" left join m_appuser abu on abu.id = l.approvedon_userid")
                    .append(" left join m_appuser dbu on dbu.id = l.disbursedon_userid")
                    .append(" left join m_appuser cbu on cbu.id = l.closedon_userid")
                    .append(" left join m_loan_arrears_aging la on la.loan_id = l.id");
                  //  .append(" left join m_loan_charge mlc on mlc.loan_id=l.id");
                    
                    

            return accountsSummary.toString();
        }

        @Override
        public LoanAccountSummaryData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String accountNo = rs.getString("accountNo");
            final String externalId = rs.getString("externalId");
            final Long productId = JdbcSupport.getLong(rs, "productId");
            final String loanProductName = rs.getString("productName");
            final String shortLoanProductName = rs.getString("shortProductName");
            final Integer loanStatusId = JdbcSupport.getInteger(rs, "statusId");
            final LoanStatusEnumData loanStatus = LoanEnumerations.status(loanStatusId);
            final Integer loanTypeId = JdbcSupport.getInteger(rs, "loanType");
            final EnumOptionData loanType = AccountEnumerations.loanType(loanTypeId);
            final Integer loanCycle = JdbcSupport.getInteger(rs, "loanCycle");

            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final String submittedByUsername = rs.getString("submittedByUsername");
            final String submittedByFirstname = rs.getString("submittedByFirstname");
            final String submittedByLastname = rs.getString("submittedByLastname");

            final LocalDate rejectedOnDate = JdbcSupport.getLocalDate(rs, "rejectedOnDate");
            final String rejectedByUsername = rs.getString("rejectedByUsername");
            final String rejectedByFirstname = rs.getString("rejectedByFirstname");
            final String rejectedByLastname = rs.getString("rejectedByLastname");

            final LocalDate withdrawnOnDate = JdbcSupport.getLocalDate(rs, "withdrawnOnDate");
            final String withdrawnByUsername = rs.getString("withdrawnByUsername");
            final String withdrawnByFirstname = rs.getString("withdrawnByFirstname");
            final String withdrawnByLastname = rs.getString("withdrawnByLastname");

            final LocalDate approvedOnDate = JdbcSupport.getLocalDate(rs, "approvedOnDate");
            final String approvedByUsername = rs.getString("approvedByUsername");
            final String approvedByFirstname = rs.getString("approvedByFirstname");
            final String approvedByLastname = rs.getString("approvedByLastname");

            final LocalDate expectedDisbursementDate = JdbcSupport.getLocalDate(rs, "expectedDisbursementDate");
            final LocalDate actualDisbursementDate = JdbcSupport.getLocalDate(rs, "actualDisbursementDate");
            final String disbursedByUsername = rs.getString("disbursedByUsername");
            final String disbursedByFirstname = rs.getString("disbursedByFirstname");
            final String disbursedByLastname = rs.getString("disbursedByLastname");

            final LocalDate closedOnDate = JdbcSupport.getLocalDate(rs, "closedOnDate");
            final String closedByUsername = rs.getString("closedByUsername");
            final String closedByFirstname = rs.getString("closedByFirstname");
            final String closedByLastname = rs.getString("closedByLastname");
            
            final BigDecimal originalLoan = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs,"originalLoan");
            final BigDecimal loanBalance = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs,"loanBalance");
            final BigDecimal amountPaid = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs,"amountPaid");
         //   final BigDecimal charges    = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs,"charges");

            final LocalDate writtenOffOnDate = JdbcSupport.getLocalDate(rs, "writtenOffOnDate");

            final LocalDate expectedMaturityDate = JdbcSupport.getLocalDate(rs, "expectedMaturityDate");

            final LocalDate overdueSinceDate = JdbcSupport.getLocalDate(rs, "overdueSinceDate");
            Boolean inArrears = true;
            if (overdueSinceDate == null) {
                inArrears = false;
            }

            final LoanApplicationTimelineData timeline = new LoanApplicationTimelineData(submittedOnDate, submittedByUsername,
                    submittedByFirstname, submittedByLastname, rejectedOnDate, rejectedByUsername, rejectedByFirstname, rejectedByLastname,
                    withdrawnOnDate, withdrawnByUsername, withdrawnByFirstname, withdrawnByLastname, approvedOnDate, approvedByUsername,
                    approvedByFirstname, approvedByLastname, expectedDisbursementDate, actualDisbursementDate, disbursedByUsername,
                    disbursedByFirstname, disbursedByLastname, closedOnDate, closedByUsername, closedByFirstname, closedByLastname,
                    expectedMaturityDate, writtenOffOnDate, closedByUsername, closedByFirstname, closedByLastname);

            return new LoanAccountSummaryData(id, accountNo, externalId, productId, loanProductName, shortLoanProductName, loanStatus, loanType, loanCycle,
                    timeline, inArrears,originalLoan,loanBalance,amountPaid);
        }
    }

	@Override
	public Collection<PaymentDetailCollectionData> retrivePaymentDetail(
			Long clientId) {
		this.clientReadPlatformService.retrieveOne(clientId);
		return retrievePaymentDetails(new Object[] {clientId,clientId});
        
   
	}
	private Collection<SharesAccountBalanceCollectionData> retrieveShareAccountBalance( final Object[] inputs) {
        final shareAccountBalanceDataaMapper rm = new shareAccountBalanceDataaMapper();
        final String sql = rm.schemaSql;
        return this.jdbcTemplate.query(sql, rm, inputs);
    }
	
	
	private static final class shareAccountBalanceDataaMapper implements RowMapper<SharesAccountBalanceCollectionData> {
	      final String schemaSql;
	      public shareAccountBalanceDataaMapper(){
	          final StringBuilder shareAccountBalance = new StringBuilder();
	          shareAccountBalance.append("select msa.id ,msa.account_balance_derived  from   ");
	          shareAccountBalance.append("m_client mc left join m_savings_account msa on mc.id=msa.client_id  ");
	          shareAccountBalance.append("where mc.id=? ");
	          shareAccountBalance.append(" and msa.id in (select  default_savings_account from m_client where mc.id=?) ");
	          shareAccountBalance.append(" and msa.status_enum=300 ");
	          this.schemaSql = shareAccountBalance.toString();
	      }
	      public String schema() {
	          return this.schemaSql;
	      }
		@Override
		public SharesAccountBalanceCollectionData mapRow(ResultSet rs,
				int rowNum) throws SQLException {
			final String accountNo = rs.getString("id");
			final BigDecimal accountBalance = JdbcSupport.getBigDecimalDefaultToNullIfZero(rs, "account_balance_derived");
	         return  new SharesAccountBalanceCollectionData(accountNo, accountBalance);                
	       
		}
	}
	@Override
	public Collection<SharesAccountBalanceCollectionData> retriveSharesBalance(
			Long clientId) {
		this.clientReadPlatformService.retrieveOne(clientId);
		return retrieveShareAccountBalance(new Object[] { clientId,clientId});
	}
	private Collection<MpesaTransactionSummaryData>retriveMpesaSummary(final Object[] inputs){
		final MpesaTransactionSummaryDataMapper rm = new MpesaTransactionSummaryDataMapper();
        final String sql = rm.schemaSql;
        return this.jdbcTemplate.query(sql, rm, inputs);
		
	}
	private static final class MpesaTransactionSummaryDataMapper implements RowMapper<MpesaTransactionSummaryData> {
	      final String schemaSql;
	      public MpesaTransactionSummaryDataMapper(){
	          final StringBuilder sb = new StringBuilder();
	          sb.append("(select mlt.id as transaction_id ");
	          sb.append(", concat('Loan-',mlt.loan_id) account_no ");
			  sb.append(" , mlt.transaction_date t_date ");
			  sb.append(" ,rev.enum_value payment_type ");
			  sb.append(" ,mpl.name product_name ");
			  sb.append(" ,mc.id client_id ");
			  sb.append(" ,mc.display_name client_name ");
			  sb.append(" ,(ifnull(mlt.fee_charges_portion_derived,0)+ifnull(mlt.penalty_charges_portion_derived,0))as loan_charges ");
			  sb.append("  ,(mlt.amount-ifnull(mlt.fee_charges_portion_derived,0)-ifnull(mlt.penalty_charges_portion_derived,0))as amount ");
			  sb.append("  from m_loan_transaction mlt ");
			  sb.append("  inner join m_appuser map on mlt.appuser_id = map.id ");
			  sb.append("  inner join m_loan ml on ml.id=mlt.loan_id ");
			  sb.append("  left join m_client mc on mc.id=ml.client_id ");
			  sb.append("  inner join m_product_loan mpl on mpl.id=ml.product_id ");
			  sb.append("  left join r_enum_value rev on rev.enum_id=mlt.transaction_type_enum and rev.enum_name='transaction_type_enum' ");
			  sb.append("  where mlt.payment_detail_id in (select mpd.id from m_payment_detail mpd  ");
			  sb.append("  where mpd.receipt_number=?) ");
			  sb.append("  and mc.id=? ");
			  sb.append("  and mlt.transaction_date =? ");
			  sb.append("  and mlt.is_reversed=0)  ");
			  sb.append("  union  ");
			  sb.append("  (select a.transaction_id,concat('Savings-',a.account_no)as account_no, a.t_date as t_date,a.payment_type,a.product_name,a.client_id,a.client_name,b.charge_amount,(a.amount-ifnull(b.charge_amount,0))as amount ");
			  sb.append("   from  ");
			  sb.append("  (select msat.id transaction_id ");
			  sb.append("  ,msat.savings_account_id as account_no ");
			  sb.append("  ,msat.transaction_date t_date  ");
			  sb.append("  ,msat.amount ");
			  sb.append("  ,rev.enum_value payment_type ");
			  sb.append("  ,msp.name product_name ");
			  sb.append("  ,mc.id client_id ");
			  sb.append("  ,mc.display_name client_name ");
			  sb.append("  from m_savings_account_transaction msat ");
			  sb.append("  inner join m_appuser map on msat.appuser_id = map.id ");
			  sb.append("  left join m_savings_account msa on msa.id=msat.savings_account_id ");
			  sb.append("  left join m_client mc on mc.id=msa.client_id ");
			  sb.append("  left join m_office mo on mc.office_id=mo.id ");
			  sb.append("  inner join m_savings_product msp on msp.id=msa.product_id ");
			  sb.append("  left join r_enum_value rev on rev.enum_id=msat.transaction_type_enum and rev.enum_name='savings_transaction_type_enum' ");
			  sb.append("  where msat.payment_detail_id in (select mpd.id from m_payment_detail mpd  ");
			  sb.append("  where mpd.receipt_number=?) ");
			  sb.append(" and mc.id=? ");
			  sb.append(" and msat.transaction_date=? ");
			  sb.append(" and msat.is_reversed=0 ");
			  sb.append(" and msat.transaction_type_enum=1)a ");
			  sb.append(" left join ");
			  sb.append(" (select msat.id transaction_id ");
			  sb.append("  ,msat.savings_account_id as account_no ");
			  sb.append("  ,msat.transaction_date t_date  ");
			  sb.append("  ,sum(msat.amount) charge_amount ");
			  sb.append("  ,rev.enum_value payment_type ");
			  sb.append("  ,msp.name product_name ");
			  sb.append("  ,mc.id client_id ");
			  sb.append("  ,mc.display_name client_name ");
			  sb.append("  from m_savings_account_transaction msat ");
			  sb.append("  left join m_savings_account msa on msa.id=msat.savings_account_id ");
			  sb.append("  left join m_client mc on mc.id=msa.client_id ");
			  sb.append("  inner join m_savings_product msp on msp.id=msa.product_id ");
			  sb.append("  left join r_enum_value rev on rev.enum_id=msat.transaction_type_enum and rev.enum_name='savings_transaction_type_enum' ");
			  sb.append("  where msat.payment_detail_id in (select mpd.id from m_payment_detail mpd  ");
			  sb.append("  where mpd.receipt_number=?) ");
			  sb.append("  and mc.id=? ");
			  sb.append("  and msat.transaction_date=? ");
			  sb.append("  and msat.is_reversed=0 ");
			  sb.append("  and msat.transaction_type_enum in(4,5,7) ");
			  sb.append("  group by msat.savings_account_id)b ");
			  sb.append("  on a.account_no = b.account_no)  ");
	  
			  schemaSql = sb.toString(); 
	      }
	      public String schema() {
	          return this.schemaSql;
	      }
		@Override
		public MpesaTransactionSummaryData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final String accountNo= rs.getString("account_no");
			final BigDecimal chargeAmount = rs.getBigDecimal("loan_charges");
			final BigDecimal amount = rs.getBigDecimal("amount");
			final String  TxnDate=rs.getString("t_date");
			final String clientName=rs.getString("client_name");
			return new  MpesaTransactionSummaryData(null,accountNo,amount,TxnDate,chargeAmount,clientName);

		}
		
	}
	
	
	@Override
	public Collection<MpesaTransactionSummaryData>retriveMpesaTransactionDetail(Long clientId,String TxnDate,String ReceiptNo){
	this.clientReadPlatformService.retrieveOne(clientId);
	return retriveMpesaSummary(new Object[]{ReceiptNo,clientId,TxnDate,ReceiptNo,clientId,TxnDate,ReceiptNo,clientId,TxnDate });
	}
	

	public AccountSummaryCollectionData retriveClientAccountAndChargeDetails(
			Long clientId, final String chargeonDate) {
		 this.clientReadPlatformService.retrieveOne(clientId);
	        final String loanwhereClause = " where l.client_id = ?  group  by l.account_no";
	        final String savingswhereClause = " where sa.client_id = ?  group by sa.account_no order by sa.status_enum ASC, sa.account_no ASC ";
	        final List<LoanAccountSummaryData> loanAccounts = retrieveLoanAccountDetails(loanwhereClause, new Object[] { clientId });
	        final List<SavingsAccountSummaryData> savingsAccounts = retrieveAccountDetails(savingswhereClause, new Object[] { clientId });
	        final List<Long>loanIdList= loanRepository.findLoanIdByClientId(clientId);
	        final List<Long>savingsAccountIdList=savingsAccountRepository.findSavingsIdByClientId(clientId);
	        List<LoanChargeSummaryData>loanCharges= new ArrayList<LoanChargeSummaryData>();
	        List<SavingsChargesSummaryData>savingsCharges= new ArrayList<SavingsChargesSummaryData>();
	        
	        for(Long loanId: loanIdList){
	        	//loanCharges = (List<LoanChargeSummaryData>) loanChargeReadPlatformServiceImpl.retriveLoanCharge(loanIdList.get(i));
	        	Collection<LoanChargeSummaryData> loanChargesList= loanChargeReadPlatformServiceImpl.retriveLoanCharge(loanId,chargeonDate);
	        	loanCharges.addAll(loanChargesList);
	            }
	        for(Long savingId : savingsAccountIdList){
	        	Collection<SavingsChargesSummaryData> savingChargesList = savingsAccountChargeReadPlatformServiceImpl.retriveCharge(savingId,chargeonDate);
	        	savingsCharges.addAll(savingChargesList);
	        }
	        final Collection<PaymentTypeData> paymentTypeOptions = this.paymentTypeReadPlatformService.retrieveAllPaymentTypes();
	        return new AccountSummaryCollectionData(loanAccounts, savingsAccounts, paymentTypeOptions,loanCharges,savingsCharges,null,null);

	}

}

