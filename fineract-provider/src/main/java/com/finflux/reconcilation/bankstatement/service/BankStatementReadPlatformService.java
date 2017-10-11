/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.service;

import java.io.File;
import java.util.List;

import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;

import com.finflux.reconcilation.bankstatement.data.BankStatementData;

public interface BankStatementReadPlatformService {

    public File retrieveFile(Long documentId);

    public List<BankStatementData> retrieveAllBankStatements(Integer statementType, Boolean isProcessed);
    
    public BankStatementData getBankStatementSummary(final Long bankStatementId);
    
    public Page<BankStatementData> retrieveAllBankStatements(Integer statementType, Boolean isProcessed, SearchParameters searchParameters);

    public BankStatementData getBankStatement(final Long bankStatementId);

    public List<BankStatementData> retrieveBankStatementsByAssociatedBank(final Long bankId);
    
}
