package com.finflux.risk.creditbureau.provider.service;

import java.util.List;

import com.finflux.risk.creditbureau.provider.data.*;

public interface CreditBureauEnquiryReadService {

    List<LoanEnquiryData> getEnquiryRequestDataForAll();

    LoanEnquiryData getEnquiryRequestDataForLoan(Long loanId);

    LoanEnquiryReferenceData getLatestCreditBureauEnquiryForLoan(Long loanId, Long creditBureauProductId);

    EnquiryReferenceData getCreditBureauEnquiryBasicData(Long enquiryId);

    List<ReportRequestData> getReportRequestData();

    List<EnquiryAddressData> getClientAddressData(Long clientId);

    List<EnquiryDocumentData> getClientDocumentData(Long clientId);

    LoanEnquiryData getEnquiryRequestDataForLoanApplication(final Long loanApplicationId);

    LoanEnquiryReferenceData getLatestCreditBureauEnquiryDetails(final Long loanApplicationId, final Long creditBureauProductId,
            final Long loanId, final Long trancheDisbursalId);

    void inActivePreviousLoanApplicationCreditbureauEnquiries(final Long loanApplicationId);

    List<EnquiryClientRelationshipData> getClientRelationshipData(final Long clientId);
}