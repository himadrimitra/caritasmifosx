package com.finflux.transaction.execution.provider;

import java.util.List;
import java.util.Map;

import com.finflux.transaction.execution.data.TransferType;

/**
 * Created by dhirendra on 23/08/16.
 */
public interface BankTransferProvider {
    BankTransferService getBankTransferService(Map<String,String> keyValueMap);

    String getKey();

    List<TransferType> getSupportedTransfers();
}
