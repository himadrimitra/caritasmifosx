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
package org.apache.fineract.portfolio.loanaccount.guarantor.domain;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.savings.domain.DepositAccountOnHoldTransaction;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_guarantor_transaction")
public class GuarantorFundingTransaction extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "guarantor_fund_detail_id", nullable = false)
    private GuarantorFundingDetails guarantorFundingDetails;

    @ManyToOne
    @JoinColumn(name = "loan_transaction_id", nullable = true)
    private LoanTransaction loanTransaction;
    
    @ManyToOne
    @JoinColumn(name = "saving_transaction_id", nullable = true)
    private SavingsAccountTransaction savingTransaction;

	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "deposit_on_hold_transaction_id", nullable = false)
    private DepositAccountOnHoldTransaction depositAccountOnHoldTransaction;

    @Column(name = "is_reversed", nullable = false)
    private boolean reversed;

    protected GuarantorFundingTransaction() {}

    public GuarantorFundingTransaction(final GuarantorFundingDetails guarantorFundingDetails, final LoanTransaction loanTransaction,
            final DepositAccountOnHoldTransaction depositAccountOnHoldTransaction, final SavingsAccountTransaction savingTransaction ) {
        this.depositAccountOnHoldTransaction = depositAccountOnHoldTransaction;
        this.guarantorFundingDetails = guarantorFundingDetails;
        this.loanTransaction = loanTransaction;
        this.reversed = false;
        this.savingTransaction = savingTransaction;
    }

    public void reverseTransaction() {
        if (!this.reversed) {
            this.reversed = true;
            BigDecimal amountForReverse = this.depositAccountOnHoldTransaction.getAmount();
            this.depositAccountOnHoldTransaction.reverseTransaction();
            if (this.depositAccountOnHoldTransaction.getTransactionType().isRelease()) {
                this.guarantorFundingDetails.undoReleaseFunds(amountForReverse);
            }
        }
    }
    
  //following code change for if undo the deposit amount then release guarantor amount has to be undo (other guarantor)
    public void reverseTransactionIfDepositUndoTxn(){
    	   if(!this.reversed){
    			   this.reversed = true;
    			   BigDecimal amountForReverse = this.depositAccountOnHoldTransaction.getAmount();
    			   this.depositAccountOnHoldTransaction.reverseTxnIfUndoDepositTxn(amountForReverse);
    			   if(this.depositAccountOnHoldTransaction.getTransactionType().isRelease()){
    				   this.guarantorFundingDetails.undoReleaseFunds(amountForReverse);
    			   }
    		   }
       }
    
    //following code change if undo the self saving on hold amount transaction then on hold amount has to reduce to last txn amount.(self guarantor)
    public void undoDepositSavingAccTxnThenUndoOnhold(BigDecimal undoTxnAmount){
    	if(!this.reversed){
    		this.reversed = true;
    		BigDecimal selfRemainingAmount = this.guarantorFundingDetails.getAmountRemaining();
    		BigDecimal newSelfRemainingAmount = selfRemainingAmount.subtract(undoTxnAmount);
    		if(newSelfRemainingAmount.longValue()>0){
    			this.guarantorFundingDetails.setAmountRemaining(newSelfRemainingAmount);
    		}else{
    			this.guarantorFundingDetails.setAmountRemaining(BigDecimal.ZERO);
    		}
    		this.depositAccountOnHoldTransaction.removedOnholdsFundsWithTxnAmount(undoTxnAmount);
    	}
    }
  
   
	public DepositAccountOnHoldTransaction getDepositAccountOnHoldTransaction() {
		return this.depositAccountOnHoldTransaction;
	}

	public void setDepositAccountOnHoldTransaction(
			DepositAccountOnHoldTransaction depositAccountOnHoldTransaction) {
		this.depositAccountOnHoldTransaction = depositAccountOnHoldTransaction;
	}

	public GuarantorFundingDetails getGuarantorFundingDetails() {
		return this.guarantorFundingDetails;
	}

	public void setGuarantorFundingDetails(
			GuarantorFundingDetails guarantorFundingDetails) {
		this.guarantorFundingDetails = guarantorFundingDetails;
	}


}
