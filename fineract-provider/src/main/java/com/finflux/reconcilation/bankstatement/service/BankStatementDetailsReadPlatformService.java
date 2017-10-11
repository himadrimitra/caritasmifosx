/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.service;

import java.util.List;



import com.finflux.reconcilation.bankstatement.data.BankStatementDetailsData;

public interface BankStatementDetailsReadPlatformService {
	
	public List<BankStatementDetailsData> changedBankStatementDetailsData(final Long bankStatementId);

    public List<BankStatementDetailsData> retrieveBankStatementDetailsReconciledData(final Long bankStatementId);
    
    public List<BankStatementDetailsData> retrieveBankStatementDetailsDataForReconcile(final Long bankStatementId);
    
    public List<BankStatementDetailsData> retrieveBankStatementNonPortfolioData(final Long bankStatementId);
    
    public List<BankStatementDetailsData> retrieveBankStatementMiscellaneousData(final Long bankStatementId);
    
    public List<BankStatementDetailsData> retrieveAllBankStatementData(final Long bankStatementId);
    
    public List<BankStatementDetailsData> retrieveGeneratePortfolioData(final Long bankStatementId, String searchCriteria);
    
    public String getBankStatementDetails(List<BankStatementDetailsData> bankStatementDetailData, final Long bankStatementId);
    
}
