package com.finflux.transaction.execution.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

import com.finflux.transaction.execution.domain.BankAccountTransaction;

public interface BankTransactionWriteService {

    CommandProcessingResult initiateTransaction(Long transactionId);
    
    CommandProcessingResult rejectTransaction(Long transactionId);

    CommandProcessingResult submitTransaction(Long transactionId, JsonCommand command);

    Long createTransactionEntry(BankAccountTransaction bankAccountTransaction);
    
    CommandProcessingResult closeTransaction(Long transactionId);

}
