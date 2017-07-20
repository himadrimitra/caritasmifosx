package com.finflux.task.data;

import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;

import com.finflux.loanapplicationreference.domain.LoanApplicationReference;
import com.finflux.loanapplicationreference.domain.LoanCoApplicant;

public class WorkflowDTO 
{
	private final Client client;
	private final LoanApplicationReference loanApplicationReference;
	private final LoanProduct loanProduct;
	private final LoanCoApplicant loanCoApplicant;

    public WorkflowDTO(final Client client)
	{
		this.loanApplicationReference=null;
		this.loanProduct=null;
		this.client=client;
		this.loanCoApplicant=null;
	}
	public WorkflowDTO(final Client client,final LoanCoApplicant loanCoApplicant,final LoanApplicationReference loanApplicationReference,final LoanProduct loanProduct)
	{
		this.loanApplicationReference=loanApplicationReference;
		this.loanProduct=loanProduct;
		this.client=client;
		this.loanCoApplicant=loanCoApplicant;
	}
	public WorkflowDTO(final LoanApplicationReference loanApplicationReference,final LoanProduct loanProduct)
	{
		this.loanApplicationReference=loanApplicationReference;
		this.loanProduct=loanProduct;
		this.client=null;
		this.loanCoApplicant=null;
	}

	public Client getClient() {
		return this.client;
	}

	public LoanApplicationReference getLoanApplicationReference() {
		return this.loanApplicationReference;
	}

	public LoanProduct getLoanProduct() {
		return this.loanProduct;
	}

	public LoanCoApplicant getLoanCoApplicant() {
		return this.loanCoApplicant;
	}
	

	
	
}
