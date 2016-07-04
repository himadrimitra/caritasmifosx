package com.finflux.reconcilation.bankstatement.service;

import java.io.IOException;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.sun.jersey.multipart.FormDataMultiPart;

public interface BankStatementWritePlatformService {

    public CommandProcessingResult deleteBankStatement(final Long bankStatementId);

    Long createBankStatement(final FormDataMultiPart formParams) throws InvalidFormatException, IOException;

    public Long updateBankStatement(final FormDataMultiPart formParams);

    public Long deleteBankStatementDetails(final Long bankStatementDetailsId);

    public CommandProcessingResult reconcileBankStatementDetails(JsonCommand command);

    public CommandProcessingResult reconcileBankStatement(JsonCommand command);

    String createJournalEntries(final Long bankStatementId, String apiRequestBodyAsJson);

}
