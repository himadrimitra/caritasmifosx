package com.finflux.risk.creditbureau.summary.service;

import com.finflux.risk.creditbureau.summary.data.CreditBureauSummaryData;

public interface CreditBureauSummaryReadPlatformService {

	public CreditBureauSummaryData retrieveCreditSummary(Long clientId, Long loanApplicationId, Long loanId,
			Long trancheDisbursalId); 
	
}
