package com.finflux.risk.creditbureau.provider.service;

import com.finflux.risk.creditbureau.provider.data.CreditBureauFileContentData;
import com.finflux.risk.creditbureau.provider.data.OtherInstituteLoansSummaryData;

public interface CreditBureauCheckService {

    OtherInstituteLoansSummaryData getCreditBureauEnquiryData(final String entityType, final Long entityId);

    OtherInstituteLoansSummaryData getOtherInstituteLoansSummary(final String entityType, final Long entityId, final Long trancheDisbursalId);

    CreditBureauFileContentData getCreditBureauReportFileContent(final String entityType, final Long entityId);

}