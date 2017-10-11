package com.finflux.transaction.execution.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.transaction.execution.data.TransferType;
import com.finflux.transaction.execution.provider.BankTransferProvider;
import com.finflux.transaction.execution.provider.BankTransferService;

/**
 * Created by dhirendra on 23/08/16.
 */
@Component
@Scope("singleton")
public class BankTransferProviderFactory {

    private Map<String, BankTransferProvider> bankTransferProviderMap = new HashMap();
//    Map<String, BankTransferService> bankTransferServiceMap = new HashMap();

    @Autowired
    public BankTransferProviderFactory(Set<BankTransferProvider> bankTransferProviders) {
        for (BankTransferProvider bankTransferProvider : bankTransferProviders) {
            bankTransferProviderMap.put(bankTransferProvider.getKey(), bankTransferProvider);
        }
    }

    public BankTransferService getBankTransferService(String name, Map<String,String> configMap) {
        BankTransferProvider bankTransferProvider = bankTransferProviderMap.get(name);
        if(bankTransferProvider!=null){
            return bankTransferProvider.getBankTransferService(configMap);
        }
        return null;
    }

    public List<TransferType> getSupportedTransfers(String name) {
        BankTransferProvider bankTransferProvider = bankTransferProviderMap.get(name);
        if(bankTransferProvider!=null) {
            return bankTransferProvider.getSupportedTransfers();
        }
        return null;
    }
}
