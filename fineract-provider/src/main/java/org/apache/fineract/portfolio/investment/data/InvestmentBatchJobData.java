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
package org.apache.fineract.portfolio.investment.data;

import java.math.BigDecimal;
import java.util.Date;

public class InvestmentBatchJobData {

	Long investmentId;
	int loanStatusId;
	Long savingId;
	Date investmentStartDate;
    BigDecimal investmetAmount;
    Date investmentCloseDate;
    Date loanMaturityDate;
 
    BigDecimal caritasPercentage;
    BigDecimal groupPercentage;
   
	Date interestDate;
    Date loanCloseDate;
    BigDecimal totalInterest;
    
	Long loanId;
    
    String earningStatus;
    BigDecimal totalInvestedAmount;
    Date loanStartDate;
    
    BigDecimal sumOfLoanCharge;
    
  
 

	public InvestmentBatchJobData(int loanStatusId, Date loanMaturityDate){
    	this.loanStatusId = loanStatusId;
    	this.loanMaturityDate = loanMaturityDate;
    }

	public Date getLoanMaturityDate() {
		return this.loanMaturityDate;
	}

	public void setLoanMaturityDate(Date loanMaturityDate) {
		this.loanMaturityDate = loanMaturityDate;
	}

	public InvestmentBatchJobData(Long loanId, String earningStatus){
    	this.loanId = loanId;
    	this.earningStatus = earningStatus;  	
    }
    
  
    public String getEarningStatus() {
		return this.earningStatus;
	}


	public void setEarningStatus(String earningStatus) {
		this.earningStatus = earningStatus;
	}


	public InvestmentBatchJobData(Date loanCloseDate, BigDecimal totalInterest, BigDecimal totalInvestedAmount, Date loanStartDate){
    	this.loanCloseDate = loanCloseDate;
    	this.totalInterest = totalInterest;
    	this.totalInvestedAmount = totalInvestedAmount;
    	this.loanStartDate = loanStartDate;
    }
    
    public InvestmentBatchJobData(BigDecimal caritasPercentage, BigDecimal groupPercentage,  Date interestDate){
    	this.caritasPercentage = caritasPercentage;
    	this.groupPercentage = groupPercentage;
    	this.interestDate = interestDate;
    }
	
	public InvestmentBatchJobData(Long savingId, Date investmentStartDate,Date investmentCloseDate, BigDecimal investmentAmount, Long investmentId){
		this.savingId = savingId;
		this.investmentStartDate = investmentStartDate;
		this.investmetAmount = investmentAmount;
		this.investmentCloseDate = investmentCloseDate;
		this.investmentId = investmentId;
	}
	
	public InvestmentBatchJobData (Long investmentId){
		this.investmentId = investmentId;
	}

	
	public Long getSavingId() {
		return this.savingId;
	}
	public void setSavingId(Long savingId) {
		this.savingId = savingId;
	}
	public Date getInvestmentStartDate() {
		return this.investmentStartDate;
	}
	public void setInvestmentStartDate(Date investmentStartDate) {
		this.investmentStartDate = investmentStartDate;
	}
	public BigDecimal getInvestmetAmount() {
		return this.investmetAmount;
	}
	public void setInvestmetAmount(BigDecimal investmetAmount) {
		this.investmetAmount = investmetAmount;
	}
	public Date getInvestmentCloseDate() {
		return this.investmentCloseDate;
	}
	public void setInvestmentCloseDate(Date investmentCloseDate) {
		this.investmentCloseDate = investmentCloseDate;
	}
	
	public Long getInvestmentId() {
		return this.investmentId;
	}
	
	   public BigDecimal getCaritasPercentage() {
			return this.caritasPercentage;
		}

		public void setCaritasPercentage(BigDecimal caritasPercentage) {
			this.caritasPercentage = caritasPercentage;
		}

		public BigDecimal getGroupPercentage() {
			return this.groupPercentage;
		}

		public void setGroupPercentage(BigDecimal groupPercentage) {
			this.groupPercentage = groupPercentage;
		}

		public Date getInterestDate() {
			return this.interestDate;
		}

		public void setInterestDate(Date interestDate) {
			this.interestDate = interestDate;
		}

		public void setInvestmentId(Long investmentId) {
			this.investmentId = investmentId;
		}

		 public Date getLoanCloseDate() {
				return this.loanCloseDate;
			}

			public void setLoanCloseDate(Date loanCloseDate) {
				this.loanCloseDate = loanCloseDate;
			}
			
			public Long getLoanId() {
				return this.loanId;
		}


		public void setLoanId(Long loanId) {
			this.loanId = loanId;
		}

		public int getLoanStatusId() {
			return this.loanStatusId;
		}

		public void setLoanStatusId(int loanStatusId) {
			this.loanStatusId = loanStatusId;
		}
		
	    Long paymentType;
	    public BigDecimal getTotalInvestedAmount() {
			return this.totalInvestedAmount;
		}

		public void setTotalInvestedAmount(BigDecimal totalInvestedAmount) {
			this.totalInvestedAmount = totalInvestedAmount;
		}
		

		public BigDecimal getTotalInterest() {
			return this.totalInterest;
		}

		public void setTotalInterest(BigDecimal totalInterest) {
			this.totalInterest = totalInterest;
		}

		   public Date getLoanStartDate() {
				return this.loanStartDate;
			}

			public void setLoanStartDate(Date loanStartDate) {
				this.loanStartDate = loanStartDate;
			}
			
			
			  
		    public BigDecimal getSumOfLoanCharge() {
				return this.sumOfLoanCharge;
			}



			public void setSumOfLoanCharge(BigDecimal sumOfLoanCharge) {
				this.sumOfLoanCharge = sumOfLoanCharge;
			}



			public Long getPaymentType() {
				return this.paymentType;
			}



			public void setPaymentType(Long paymentType) {
				this.paymentType = paymentType;
			}



			public InvestmentBatchJobData(BigDecimal sumOfLoanCharge){
		    	this.sumOfLoanCharge = sumOfLoanCharge;
		    }
		   		
}
