package com.finflux.risk.existingloans.service;

import java.util.List;

import com.finflux.risk.existingloans.data.ExistingLoanData;
import com.finflux.risk.existingloans.data.ExistingLoanTemplateData;

public interface ExistingLoanReadPlatformService {

    ExistingLoanTemplateData retriveTemplate();

    List<ExistingLoanData> retriveAll(final Long clientId, final Long loanApplicationId, final Long loanId, final Long trancheDisbursalId);

    ExistingLoanData retrieveOne(final Long clientId, final Long existingLoanId);
}
