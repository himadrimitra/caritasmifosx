/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.service;

import java.io.IOException;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.sun.jersey.multipart.FormDataMultiPart;

public interface BankStatementWritePlatformService {

    public CommandProcessingResult deleteBankStatement(JsonCommand command);

    Long createBankStatement(final FormDataMultiPart formParams) throws InvalidFormatException, IOException;

    public Long updateBankStatement(final FormDataMultiPart formParams);

    public Long deleteBankStatementDetails(final Long bankStatementDetailsId);

    public CommandProcessingResult reconcileBankStatementDetails(JsonCommand command);
    
    public CommandProcessingResult undoReconcileBankStatementDetails(JsonCommand command);

    public CommandProcessingResult reconcileBankStatement(JsonCommand command);

    String createJournalEntries(final Long bankStatementId, String apiRequestBodyAsJson);
    
    public CommandProcessingResult generatePortfolioTransactions(JsonCommand command);

    CommandProcessingResult completePortfolioTransactions(JsonCommand command);

}
