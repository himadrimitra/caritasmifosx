package com.finflux.portfolio.loanproduct.creditbureau.data;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.organisation.office.data.OfficeData;

import com.finflux.risk.creditbureau.configuration.data.CreditBureauData;

@SuppressWarnings("unused")
public class CreditBureauLoanProductMappingData {

    private final Long id;
    private final CreditBureauData creditBureauData;
    private final Long loanProductId;
    private final String loanProductName;
    private final Boolean isCreditcheckMandatory;
    private final Boolean skipCreditcheckInFailure;
    private final Integer stalePeriod;
    private final Boolean isActive;
    private Collection<OfficeData> selectedOfficeList = new ArrayList<>();
    private Collection<OfficeData> availableOfficeList = new ArrayList<>();
    

    private CreditBureauLoanProductMappingData(final Long id, final CreditBureauData creditBureauData, final Long loanProductId,
            final String loanProductName, final Boolean isCreditcheckMandatory, final Boolean skipCreditcheckInFailure,
            final Integer stalePeriod, final Boolean isActive, Collection<OfficeData> availableOfficeList,
            final Collection<OfficeData> selectedOfficeList) {
        this.id = id;
        this.creditBureauData = creditBureauData;
        this.loanProductId = loanProductId;
        this.loanProductName = loanProductName;
        this.isCreditcheckMandatory = isCreditcheckMandatory;
        this.skipCreditcheckInFailure = skipCreditcheckInFailure;
        this.stalePeriod = stalePeriod;
        this.isActive = isActive;
        this.selectedOfficeList = selectedOfficeList;
        this.availableOfficeList = availableOfficeList;
    }

    public static CreditBureauLoanProductMappingData instance(final Long id, final CreditBureauData creditBureauData,
            final Long loanProductId, final String loanProductName, final Boolean isCreditcheckMandatory,
            final Boolean skipCreditcheckInFailure, final Integer stalePeriod, final Boolean isActive) {
        final Collection<OfficeData> selectedOfficeList = null;
        final Collection<OfficeData> availableOfficeList = null;
        return new CreditBureauLoanProductMappingData(id, creditBureauData, loanProductId, loanProductName, isCreditcheckMandatory,
                skipCreditcheckInFailure, stalePeriod, isActive, availableOfficeList, selectedOfficeList);
    }
    
    public static CreditBureauLoanProductMappingData create(final Long id, final CreditBureauData creditBureauData,
            final Long loanProductId, final String loanProductName, final Boolean isCreditcheckMandatory,
            final Boolean skipCreditcheckInFailure, final Integer stalePeriod, final Boolean isActive, final Collection<OfficeData> availableOfficeList, final Collection<OfficeData> selectedOfficeList) {
        return new CreditBureauLoanProductMappingData(id, creditBureauData, loanProductId, loanProductName, isCreditcheckMandatory,
                skipCreditcheckInFailure, stalePeriod, isActive, availableOfficeList, selectedOfficeList);
    }
    
    public Collection<OfficeData> getSelectedOfficeList() {
        return this.selectedOfficeList;
    }

    public void updateAvailableOfficeList(Collection<OfficeData> availableOfficeList) {
        this.availableOfficeList = availableOfficeList;
    }

}