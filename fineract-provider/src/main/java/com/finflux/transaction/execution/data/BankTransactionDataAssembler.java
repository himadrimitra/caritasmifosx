package com.finflux.transaction.execution.data;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.transaction.execution.api.BankTransactionApiConstants;
import com.finflux.transaction.execution.domain.BankAccountTransaction;
import com.google.gson.JsonElement;

@Service
public class BankTransactionDataAssembler {

    private final FromJsonHelper fromApiJsonHelper;


    @Autowired
    public BankTransactionDataAssembler(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void assembleSubmitBankTransction(BankAccountTransaction bankAccountTransaction,  JsonCommand command) {
        final JsonElement element = command.parsedJson();
        final String transferTypeStr = this.fromApiJsonHelper.extractStringNamed(BankTransactionApiConstants.transferType,
                element);

        TransferType transferType = TransferType.fromString(transferTypeStr);
        if(transferType!=null){
            bankAccountTransaction.setTransferType(transferType.getValue());
        }
    }
}