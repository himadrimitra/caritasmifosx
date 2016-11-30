package com.finflux.transaction.execution.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.transaction.execution.service.provider.BankTransferProvider;
import com.finflux.transaction.execution.service.provider.BankTransferService;

/**
 * Created by dhirendra on 23/08/16.
 */
@Component
@Scope("singleton")
public class BankTransferProviderFactory {

    Map<String, BankTransferProvider> bankTransferProviderMap = new HashMap();
    Map<String, BankTransferService> bankTransferServiceMap = new HashMap();

    @Autowired
    public BankTransferProviderFactory(Set<BankTransferProvider> bankTransferProviders) {
        for (BankTransferProvider bankTransferProvider : bankTransferProviders) {
            bankTransferProviderMap.put(bankTransferProvider.getKey(), bankTransferProvider);
        }
        //get Unique PaymentSource
        //get ALL Config
        String refId = "1";
        String impl_key = "india.bank.rbl.txn";

        Map<String, String> keyValueMap = new HashMap<>();
        keyValueMap.put("client_id","fd27b97f-7f62-4dd9-b27c-c5a02a6d7d00");
        keyValueMap.put("client_secret","jK0aC3pQ3tG7mG1lY7eF1tV0nI5sO8vE1rN6xB2tT8vN7uG2lH");
        keyValueMap.put("user","CHAITANYA");
        keyValueMap.put("password","pass@123");
        keyValueMap.put("keystore_path","/Users/dhirendra/workspace/conflux-git/clients/rbl/client1.p12");
        keyValueMap.put("keyStore_password","pass@123");
        keyValueMap.put("rbl_end_point","/test/sb/rbl/v1/payments/corp/payment");
        keyValueMap.put("do_single_txn_resource","/test/sb/rbl/v1/payments/corp/payment");
        keyValueMap.put("do_single_txn_status_resource","/test/sb/rbl/v1/payments/corp/payment/query");
        keyValueMap.put("rpt_code","HSBA");
        keyValueMap.put("corporate_id","CHAITANYA");

        bankTransferServiceMap.put(refId,bankTransferProviderMap.get(impl_key).getBankTransferService(keyValueMap));
    }

    public BankTransferService getBankTransferService(String refId) {
        return bankTransferServiceMap.get(refId);
    }
}
