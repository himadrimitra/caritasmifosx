package com.finflux.transaction.execution.service;

import com.finflux.transaction.execution.domain.BankAccountTransaction;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface BankTransactionWriteService {

    CommandProcessingResult initiateTransaction(Long transactionId);

    CommandProcessingResult submitTransaction(Long transactionId);

    Long createTransactionEntry(BankAccountTransaction bankAccountTransaction);

}
