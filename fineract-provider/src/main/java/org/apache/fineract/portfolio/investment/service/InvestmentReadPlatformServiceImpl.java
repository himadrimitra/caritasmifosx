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
package org.apache.fineract.portfolio.investment.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.investment.data.LoanInvestmentData;
import org.apache.fineract.portfolio.investment.data.SavingInvestmentData;
import org.apache.fineract.portfolio.investment.exception.InvestmentAlreadyClosedException;
import org.apache.fineract.portfolio.investment.exception.InvestmentIsNotClosedException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class InvestmentReadPlatformServiceImpl implements
		InvestmentReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	public static RoutingDataSource dataSource;

	@Autowired
	public InvestmentReadPlatformServiceImpl(RoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<SavingInvestmentData> retriveLoanAccountsBySavingId(
			Long savingId) throws SQLException {

		final SavingInvestmentDataMapper mapper = new SavingInvestmentDataMapper();
		final String schema = " select " + mapper.savingAccountsSchema()
				+ " where ms.saving_id =" + savingId;

		List<SavingInvestmentData> data = this.jdbcTemplate.query(schema,
				mapper);

		return data;

	}

	@Override
	public List<LoanInvestmentData> retriveSavingAccountsByLoanId(Long loanId) {
		// TODO Auto-generated method stub
		final LoanInvestmentDataMapper mapper = new LoanInvestmentDataMapper();
		final String schema = " select " + mapper.loanAccountSchema()
				+ " where msi.loan_id = " + loanId;

		List<LoanInvestmentData> data = this.jdbcTemplate.query(schema, mapper);
		return data;
	}

	@Override
	public Long retriveSavingInvestmentId(Long savingId, Long loanId, LocalDate startDate) {
		try{

	   	
		final String schema = "select ms.id from m_investment ms "
				+ " where ms.saving_id = " + savingId + " and ms.loan_id = " + loanId +
				" and ms.start_date = '" + startDate + "'"
		        + " and ms.close_date is null ";

		Integer data = this.jdbcTemplate.queryForObject(schema,new Object[]{}, Integer.class);

		Long resultData = new Long(data);
		return resultData;
		
		
		}catch(Exception e){
			
	    	throw new InvestmentIsNotClosedException();	   
		}
	}
	
	@Override
	public Long retriveSavingInvestmentIdForUpdate(Long savingId, Long loanId, String startDate){
		try{
			final String schema = "select ms.id from m_investment ms"
					+ " where ms.saving_id = " + savingId + " and ms.loan_id = " + loanId +
					" and ms.start_date = '" + startDate + "'"
					+ " and ms.close_date is null ";
			Integer data = this.jdbcTemplate.queryForObject(schema, new Object[]{}, Integer.class);
			
			Long resultData = new Long(data);					
			return resultData;
		}catch(Exception e){
			throw new InvestmentAlreadyClosedException();	  
		}
		
	}
	
	@Override
	public Integer retriveSavingInvestmentIdForClose(Long savingId, Long loanId,
			String startDate) {
		try{
		final String schema = "select ms.id from m_investment ms "
				+ " where ms.saving_id = " + savingId + " and ms.loan_id = " + loanId
				+ " and ms.start_date = '" + startDate + "'"
		        + " and ms.close_date is not null ";
		Integer data =  this.jdbcTemplate.queryForObject(schema,new Object[]{},Integer.class);
		return data;
		}catch(Exception e){
			throw new InvestmentIsNotClosedException();
		}

	}

	@Override
	public Long retriveLoanInvestmentId(Long loanId, Long savingId, String startDate) {
		try{

		final String schema = "select ms.id from m_investment ms "
				+ " where ms.loan_id = " + loanId + " and ms.saving_id = "
				+ savingId +
				" and ms.start_date = '" + startDate + "'"
		        + " and ms.close_date is not null ";;

		Integer  data = this.jdbcTemplate.queryForObject(schema, new Object[]{}, Integer.class);
		Long resultData = new Long(data);
		return resultData;
		}catch(Exception e){
			throw new InvestmentIsNotClosedException();
		}

	}
	
	
	@Override
	public Long retriveLoanInvestmentIdForUpdate(Long loanId, Long savingId, String startDate){
		
		try{
			
			final String schema = "select ms.id from m_investment ms "
					+ " where ms.loan_id = " + loanId + " and ms.saving_id = "
					+ savingId +
					" and ms.start_date = '" + startDate + "'"
			        + " and ms.close_date is null ";;

			Integer  data = this.jdbcTemplate.queryForObject(schema, new Object[]{}, Integer.class);
			Long resultData = new Long(data);
			
			return resultData;
			
		}catch(Exception e){
			throw new InvestmentAlreadyClosedException();	
		}
		
	}

	@Override
	public List<Long> retriveLoanIdBySavingId(Long savingId) {

		final SavingInvestmentDataMapper mapp = new SavingInvestmentDataMapper();
		final String schema = "select ms.loan_id from m_investment ms"
				+ " where ms.saving_id = " + savingId;

		List<Long> data = this.jdbcTemplate.queryForList(schema, null,
				Long.class);
		return data;
	}
	
	@Override
	public List<Long>  retriveInvestedAmountBySavingId(Long savingId){
		final String schema = "select ms.invested_amount from m_investment ms"
				+ " where ms.saving_id = " + savingId;

		List<Long> data = this.jdbcTemplate.queryForList(schema, null,
				Long.class);
		return data;	
	}

	@Override
	public List<Long> retriveSavingIdByLoanId(Long loanId) {

		final String schema = " select ms.saving_id from m_investment ms "
				+ " where ms.loan_id = " + loanId;
		List<Long> data = this.jdbcTemplate.queryForList(schema, null,
				Long.class);

		return data;
	}
	
	@Override
	public List<Long>  retriveInvestedAmountByLoanId(Long loanId){
		final String schema = "select ms.invested_amount from m_investment ms"
				+ " where ms.loan_id = " + loanId;

		List<Long> data = this.jdbcTemplate.queryForList(schema, null,
				Long.class);
		return data;	
	}

	
	

	private static final class SavingInvestmentDataMapper implements
			RowMapper<SavingInvestmentData> {

		public String savingAccountsSchema() {
			return   " ml.id as loan_id,cl.id as client_id, ml.account_no as accountno, cl.display_name as name," 
                   + " ml.approved_principal as loanammount, mpl.name as productname, ms.invested_amount as investedAmount,"
                   + " ms.start_date as start_date, "
                   + " ms.close_date as close_date "
                   + " from m_investment ms " 					
                   + " left join m_loan ml on ms.loan_id = ml.id "
                   + " left join m_client cl on ml.client_id = cl.id " 
                   + " left join m_product_loan mpl on ml.product_id = mpl.id ";
		}

		/*
		 * public String loanAccountSchema(){ return
		 * "ms.loan_id from m_saving_investment ms"; }
		 */

		@Override
		public SavingInvestmentData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			final Long loan_id = rs.getLong("loan_id");
			final Long client_id = rs.getLong("client_id");
			final String accountno = rs.getString("accountno");
			final String name = rs.getString("name");
			final Long loanammount = rs.getLong("loanammount");
			final String productname = rs.getString("productname");
			final Long investedAmount = rs.getLong("investedAmount");
			
			final LocalDate closeDate = JdbcSupport.getLocalDate(rs, "close_date");
			final LocalDate startDate = JdbcSupport.getLocalDate(rs, "start_date");


			List<SavingInvestmentData> savingInvestmentData = null;
			final SavingInvestmentData data = SavingInvestmentData.instance(
					loan_id,client_id, accountno, name, loanammount, productname, null,
					null, investedAmount, startDate, closeDate);
			// TODO Auto-generated method stub
			return data;
		}

	}

	private static final class LoanInvestmentDataMapper implements
			RowMapper<LoanInvestmentData> {

		public String loanAccountSchema() {

			return    "msi.saving_id as saving_id,mp.id as group_id , mp.display_name as name, "
					+ " msp.name as productname, msa.account_no as accountno, msa.account_balance_derived as savingammount,"
					+ " msi.start_date as startDate, "
					+ " msi.close_date as closeDate, "
					+ " msi.invested_amount as investedamount  from m_investment msi "
					+ " left join m_savings_account msa on msi.saving_id = msa.id "
					+ " left join m_savings_product msp on msa.product_id = msp.id left join m_group mp on msa.group_id = mp.id";
		}

		@Override
		public LoanInvestmentData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			final Long saving_id = rs.getLong("saving_id");
			final Long group_id = rs.getLong("group_id");
			final String accountno = rs.getString("accountno");
			final String name = rs.getString("name");
			final Long savingammount = rs.getLong("savingammount");
			final String productname = rs.getString("productname");
			final Long investedAmount = rs.getLong("investedamount");
            final LocalDate startDate = JdbcSupport.getLocalDate(rs, "startDate");
            final LocalDate closeDate = JdbcSupport.getLocalDate(rs, "closeDate");
            
			final LoanInvestmentData data = LoanInvestmentData.intance(
					saving_id,group_id, name, accountno, savingammount, productname,
					investedAmount, startDate, closeDate);

			return data;
		}

	}

	@Override
	public boolean isSavingAccountLinkedWithInvestment(Long savingId) {
		 boolean hasLinkiedWithInvestment = false;
	  
		  String sql = " select id from m_investment mi "
				+ " where mi.saving_id = " + savingId + 
                  " and mi.close_date is null ";
		
		 List<Long>  data = this.jdbcTemplate.queryForList(sql,null,Long.class);
		 if(!(data.isEmpty())){
			 hasLinkiedWithInvestment = true;
		 }
		 
		return hasLinkiedWithInvestment;
	}

	@Override
	public boolean isSavingInvestmentAlreadyDoneWithSameDate(Long savingId,
			LocalDate investmentStartDate) {
		boolean isSavingInvestementAlreadyDoneWithSameDate = false;
		
		String sql = " select id from m_investment mi where mi.start_date in ('" + investmentStartDate + "')"
				+ " and mi.saving_id = " + savingId ;
		List<Long>  data = this.jdbcTemplate.queryForList(sql,null,Long.class);

		if(!(data.isEmpty())){
			isSavingInvestementAlreadyDoneWithSameDate = true;
		}
		// TODO Auto-generated method stub
		return isSavingInvestementAlreadyDoneWithSameDate;
	}

	@Override
	public boolean isLoanInvestmentAlreadyDoneOnSameDate(Long loanId,
			LocalDate investmentStartDate) {
		
		boolean isLoanInvestmentAlreadyDoneWithSameDate = false;
		
		String sql =  " select id from m_investment mi where mi.start_date in ('" + investmentStartDate + "')"
				+ " and mi.loan_id = " + loanId ;
		
		List<Long>  data = this.jdbcTemplate.queryForList(sql,null,Long.class);
		
		if(!(data.isEmpty())){
			isLoanInvestmentAlreadyDoneWithSameDate = true;
		}
		
		return isLoanInvestmentAlreadyDoneWithSameDate;
	}

	

}
