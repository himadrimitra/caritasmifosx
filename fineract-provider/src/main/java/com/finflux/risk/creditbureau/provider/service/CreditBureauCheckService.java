package com.finflux.risk.creditbureau.provider.service;

import com.finflux.risk.creditbureau.provider.data.CreditBureauFileContentData;
import com.finflux.risk.creditbureau.provider.data.CreditBureauReportData;
import com.finflux.risk.creditbureau.provider.data.OtherInstituteLoansSummaryData;

public interface CreditBureauCheckService {

    CreditBureauReportData getCreditBureauDataForLoan(Long loanId);

    OtherInstituteLoansSummaryData getCreditBureauDataForLoanApplication(final Long loanApplicationId);

    OtherInstituteLoansSummaryData getOtherInstituteLoansSummary(final Long loanApplicationReferenceId, final Long sourceId);

    CreditBureauFileContentData getCreditBureauReportFileContent(final Long loanApplicationReferenceId);
}