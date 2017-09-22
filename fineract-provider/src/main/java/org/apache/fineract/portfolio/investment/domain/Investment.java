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
package org.apache.fineract.portfolio.investment.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.domain.ClientEnumerations;
import org.apache.fineract.portfolio.client.domain.ClientStatus;
import org.apache.fineract.portfolio.investment.api.InvestmentConstants;
import org.apache.fineract.portfolio.investment.data.SavingInvestmentData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_investment")
public class Investment extends AbstractPersistable<Long> {
    
    @Column(name = "saving_id", nullable = false)
    private Long savingId;
    
    @Column(name = "loan_id", nullable = false)
    private Long loanId;
    
    @Column(name = "invested_amount", scale = 6, precision = 19, nullable = true)
    private Long investedAmount;
    
    
    @Column(name = "start_date", nullable = true)
    private Date startDate;
    
    @Column(name = "close_date", nullable = true)
    private Date closeDate;
    
    
    protected Investment() {
        //
    }

   
    public Investment(Long savingId, Long loanId, Long investedAmount, Date startDate, Date closeDate) {
        // super();
        this.savingId = savingId;
        this.loanId = loanId;
        this.investedAmount = investedAmount;
        this.startDate = startDate;
        this.closeDate = closeDate;
    }

    
    public Long getInvestedAmount() {
		return this.investedAmount;
	}


	public Date getStartDate() {
		return this.startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public Date getCloseDate() {
		return this.closeDate;
	}


	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}


	public void setInvestedAmount(Long investedAmount) {
		this.investedAmount = investedAmount;
	}


	public Long getSavingId() {
        return this.savingId;
    }


    
    public void setSavingId(Long savingId) {
        this.savingId = savingId;
    }


    
    public Long getLoanId() {
        return this.loanId;
    }


    
    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }
    
    
    public void updateLoanId(Long loanId){
    	this.loanId = loanId;
    }
    
    public void updateSavingId(Long savingId){
    	this.savingId = savingId;
    }
    public void updateInvestedAmount(Long investedAmount){
    	this.investedAmount = investedAmount;
    }
    
    
    public Map<String, Object> update(final JsonCommand command) {
    	//Long loanId = command.longValueOfParameterNamed("loanId");
      //  Long ammount = command.longValueOfParameterNamed("investedAmounts");
       // Long savingsId = command.longValueOfParameterNamed("savingsId");
    	 final Map<String, Object> actualChanges = new LinkedHashMap<>(9);
    	 if (command.isChangeInStringParameterNamed(InvestmentConstants.loanIdParamName,Long.toString(this.loanId))){
    		 final Long newValue = command.longValueOfParameterNamed(InvestmentConstants.loanIdParamName);
             actualChanges.put(InvestmentConstants.loanIdParamName,newValue);
         }
    	 
    	 if (command.isChangeInStringParameterNamed(InvestmentConstants.savingIdParamName,Long.toString(this.savingId))){
    		 final Long newValue = command.longValueOfParameterNamed(InvestmentConstants.savingIdParamName);
             actualChanges.put(InvestmentConstants.savingIdParamName,newValue);
         }
    	 
    	 if (command.isChangeInStringParameterNamed(InvestmentConstants.SAVINGINVESTMENT_RESOURCE_NAME,Long.toString(this.investedAmount))){
    		 final Long newValue = command.longValueOfParameterNamed(InvestmentConstants.SAVINGINVESTMENT_RESOURCE_NAME);
             actualChanges.put(InvestmentConstants.SAVINGINVESTMENT_RESOURCE_NAME,newValue);
         }
    	 
    	 
		return actualChanges;
    
    }
       
}
    

    

