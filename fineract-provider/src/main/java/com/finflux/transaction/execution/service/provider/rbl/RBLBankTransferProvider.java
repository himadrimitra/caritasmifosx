package com.finflux.transaction.execution.service.provider.rbl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.finflux.transaction.execution.service.provider.BankTransferProvider;
import com.finflux.transaction.execution.service.provider.BankTransferService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by dhirendra on 23/11/16.
 */
@Component
@Scope("singleton")
public class RBLBankTransferProvider implements BankTransferProvider {

	private final static Logger logger = LoggerFactory
			.getLogger(RBLBankTransferProvider.class);

	private static final String KEY = "india.bank.rbl.txn";


	@Override
	public BankTransferService getBankTransferService(Map<String, String> keyValueMap) {
		return RBLBankTransferService.getInstance(keyValueMap);
	}

	@Override public String getKey() {
		return KEY;
	}
}
