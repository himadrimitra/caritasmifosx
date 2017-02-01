package com.finflux.transaction.execution.provider.rbl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.infrastructure.external.requestreponse.service.ThirdPartyRequestResponseWritePlatformService;
import com.finflux.transaction.execution.data.TransferType;
import com.finflux.transaction.execution.provider.BankTransferProvider;
import com.finflux.transaction.execution.provider.BankTransferService;

/**
 * Created by dhirendra on 23/11/16.
 */
@Component
@Scope("singleton")
public class RBLBankTransferProvider implements BankTransferProvider {

	private final static Logger logger = LoggerFactory
			.getLogger(RBLBankTransferProvider.class);

	private static final String KEY = "RBL";
	private static final List<TransferType> SUPPORTED_TRANSFERS = Arrays
			.asList(new TransferType[]{TransferType.IMPS, TransferType.NEFT});

	private final ThirdPartyRequestResponseWritePlatformService thirdPartyRequestResponseWritePlatformService;

	@Autowired
	private RBLBankTransferProvider(final ThirdPartyRequestResponseWritePlatformService thirdPartyRequestResponseWritePlatformService) {
		this.thirdPartyRequestResponseWritePlatformService = thirdPartyRequestResponseWritePlatformService;
	}

	@Override
	public BankTransferService getBankTransferService(Map<String, String> keyValueMap) {
		return RBLBankTransferService.getInstance(keyValueMap, thirdPartyRequestResponseWritePlatformService);
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public List<TransferType> getSupportedTransfers() {
		return SUPPORTED_TRANSFERS;
	}
}
