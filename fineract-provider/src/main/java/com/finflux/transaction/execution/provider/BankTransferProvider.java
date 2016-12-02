package com.finflux.transaction.execution.provider;

import java.util.Map;

/**
 * Created by dhirendra on 23/08/16.
 */
public interface BankTransferProvider {
    BankTransferService getBankTransferService(Map<String,String> keyValueMap);

    String getKey();
}
