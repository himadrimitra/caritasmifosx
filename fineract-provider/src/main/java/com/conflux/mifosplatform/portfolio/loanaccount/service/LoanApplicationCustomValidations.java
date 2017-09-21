package com.conflux.mifosplatform.portfolio.loanaccount.service;


import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Component;

import com.conflux.mifosplatform.portfolio.loanaccount.exception.LoanApplicationSubStatusException;

@Component
public class LoanApplicationCustomValidations {
	
	private final BusinessEventNotifierService businessEventNotifierService;
	
	@Autowired
    public LoanApplicationCustomValidations (
            final BusinessEventNotifierService businessEventNotifierService
            ) {
        this.businessEventNotifierService = businessEventNotifierService;
    }
	
	@PostConstruct
    public void addListners() {
        this.businessEventNotifierService.addBusinessEventPreListners(
        		BUSINESS_EVENTS.LOAN_SUBMITTED, new CheckForSubStatusActiveGoodStanding());		
    }
	
	private class CheckForSubStatusActiveGoodStanding implements BusinessEventListner {

		private String ACTIVE_GOOD_STANDING = "Active in Good Standing";
		
        @Override
        public void businessEventToBeExecuted(@SuppressWarnings("unused") AbstractPersistable<Long> businessEventEntity) {
        	if (businessEventEntity instanceof Loan) {
                Loan loan = (Loan) businessEventEntity;
                if ( (loan.client() != null) &&
                		( loan.client().subStatus() != null)	&&
                		( loan.client().subStatus().label() != null) )
                if ( ! (loan.client().subStatus().label().equals(ACTIVE_GOOD_STANDING)) ) {
                	throw new LoanApplicationSubStatusException (
                			"clientsubstatus",
                			"Invalid Client Sub-Status for Loan Application.",
                			(Object[]) null);
                }
            }
        }

        @Override
        public void businessEventWasExecuted(AbstractPersistable<Long> businessEventEntity) {
            
        }

        @Override
        public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
            // TODO Auto-generated method stub
            
        }
    }

}
