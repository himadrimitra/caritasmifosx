package com.finflux.transaction.execution.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.finflux.transaction.execution.data.BankTransactionEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.external.data.ExternalServicesData;
import com.finflux.portfolio.external.data.ExternalServicesReadService;
import com.finflux.transaction.execution.data.BankTransactionDetail;
import com.finflux.transaction.execution.data.BankTransactionRequest;
import com.finflux.transaction.execution.data.TransactionStatus;
import com.finflux.transaction.execution.domain.BankAccountTransaction;
import com.finflux.transaction.execution.provider.BankTransferService;

/**
 * Created by dhirendra on 22/09/16.
 */
@Service
@Scope("singleton")
public class BankTransactionServiceImpl implements BankTransactionService {

	private final BankTransferProviderFactory bankTransferProviderFactory;
	private final ExternalServicesReadService externalServicesReadService;
	private final BankTransactionReadPlatformService readPlatformService;
	private final BankTransactionWriteService writePlatformService;

	private final String defaultPaymentKey = "RBL";
	private final Map<String,String> defaultConfigMap = new HashMap<>();

	@Autowired
	public BankTransactionServiceImpl(final BankTransferProviderFactory bankTransferProviderFactory,
									  final ExternalServicesReadService externalServicesReadService,
									  final BankTransactionReadPlatformService readPlatformService,
									  final BankTransactionWriteService writePlatformService) {
		this.bankTransferProviderFactory = bankTransferProviderFactory;
		this.externalServicesReadService = externalServicesReadService;
		this.readPlatformService = readPlatformService;
		this.writePlatformService = writePlatformService;

		defaultConfigMap.put("client_id","fd27b97f-7f62-4dd9-b27c-c5a02a6d7d00");
		defaultConfigMap.put("client_secret","jK0aC3pQ3tG7mG1lY7eF1tV0nI5sO8vE1rN6xB2tT8vN7uG2lH");
		defaultConfigMap.put("user","CHAITANYA");
		defaultConfigMap.put("password","pass@123");
		defaultConfigMap.put("keystore_path","/Users/dhirendra/workspace/conflux-git/clients/rbl/client1.p12");
		defaultConfigMap.put("keystore_password","finflux");
		defaultConfigMap.put("rbl_end_point","https://apideveloper.rblbank.com");
		defaultConfigMap.put("do_single_txn_resource","/test/sb/rbl/v1/payments/corp/payment");
		defaultConfigMap.put("get_single_txn_status_resource","/test/sb/rbl/v1/payments/corp/payment/query");
		defaultConfigMap.put("rpt_code","HSBA");
		defaultConfigMap.put("corporate_id","CHAITANYA");
	}

	@Override
	public boolean validateAccount(Long beneficiary) {
		return false;
	}

	@Override
	public BankTransactionDetail transactionEntry(
			Long externalPaymentServiceId,
			BankTransactionRequest transactionRequest) {
		ExternalServicesData externalService = externalServicesReadService.findOneWithNotFoundException(externalPaymentServiceId);
		BankAccountTransaction bankAccountTransaction = new BankAccountTransaction(
				transactionRequest.getEntityTypeId(), transactionRequest.getEntityId(),
				transactionRequest.getEntityTxnId(), TransactionStatus.DRAFTED.getValue(),
				transactionRequest.getDebiter().getId(), transactionRequest.getBeneficiary().getId(),
				transactionRequest.getAmount(), transactionRequest.getTransferType().getValue(),
				externalPaymentServiceId, transactionRequest.getReason());
		Long txnId = writePlatformService.createTransactionEntry(bankAccountTransaction);
		return getTransactionDetail(txnId);
	}

	@Override public List<BankTransactionDetail> getAllTransaction(BankTransactionEntityType entityType, Long entityId) {
		return readPlatformService.getAccountTransactionsByEntity(entityType,entityId);
	}

	@Override public BankTransactionDetail getTransactionDetail(Long transactionId) {
		return readPlatformService.getAccountTransactionDetails(transactionId);
	}

	@Override
	public BankTransferService getBankTransferService(Long externalServiceId) {
		ExternalServicesData externalServiceData = externalServicesReadService.findOneWithNotFoundException(externalServiceId);
		return bankTransferProviderFactory.getBankTransferService( externalServiceData.getName(),defaultConfigMap);
	}

}
