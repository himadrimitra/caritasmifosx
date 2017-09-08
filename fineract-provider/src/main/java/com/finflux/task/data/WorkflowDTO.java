package com.finflux.task.data;

import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.village.domain.Village;

import com.finflux.infrastructure.gis.district.domain.District;
import com.finflux.loanapplicationreference.domain.LoanApplicationReference;
import com.finflux.loanapplicationreference.domain.LoanCoApplicant;

public class WorkflowDTO 
{
	private final Client client;
	private final LoanApplicationReference loanApplicationReference;
	private final LoanProduct loanProduct;
	private final LoanCoApplicant loanCoApplicant;
	private final Village village;
	private final Office office;
	private final District district;
	
    public WorkflowDTO(final Client client)
	{
		this.loanApplicationReference=null;
		this.loanProduct=null;
		this.client=client;
		this.loanCoApplicant=null;
		this.village = null ;
		this.office = null;
		this.district = null;
	}
	public WorkflowDTO(final Client client,final LoanCoApplicant loanCoApplicant,final LoanApplicationReference loanApplicationReference,final LoanProduct loanProduct)
	{
		this.loanApplicationReference=loanApplicationReference;
		this.loanProduct=loanProduct;
		this.client=client;
		this.loanCoApplicant=loanCoApplicant;
		this.village = null ;
		this.office = null;
		this.district = null;
	}
	public WorkflowDTO(final LoanApplicationReference loanApplicationReference,final LoanProduct loanProduct)
	{
		this.loanApplicationReference=loanApplicationReference;
		this.loanProduct=loanProduct;
		this.client=null;
		this.loanCoApplicant=null;
		this.village = null ;
		this.office = null;
		this.district = null;
	}

	public WorkflowDTO(final Village village) {
	    this.loanApplicationReference= null;
            this.loanProduct=null;
            this.client=null;
            this.loanCoApplicant=null;
            this.village = village ;
            this.office = null;
            this.district = null;
	}
	
    public WorkflowDTO(final Office office) {
        this.loanApplicationReference = null;
        this.loanProduct = null;
        this.client = null;
        this.loanCoApplicant = null;
        this.village = null;
        this.office = office;
        this.district = null;
    }

    public WorkflowDTO(final District district) {
        this.loanApplicationReference = null;
        this.loanProduct = null;
        this.client = null;
        this.loanCoApplicant = null;
        this.village = null;
        this.office = null;
        this.district = district;
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
	
	public Village getVillage() {
	    return this.village ;
	}

    public Office getOffice() {
        return this.office;
    }

    public District getDistrict() {
        return this.district;
    }
}
