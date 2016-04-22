package com.finflux.risk.existingloans.service;

import java.util.List;




import com.finflux.risk.existingloans.data.ExistingLoanData;

public interface ExistingLoanReadPlatformService {
	
	ExistingLoanData retrieveOne(Long clientId,Long existingLoanId);
	
	List<ExistingLoanData>retriveAll(Long clientId);
	
	ExistingLoanData retriveTemplate();
	
	


}
