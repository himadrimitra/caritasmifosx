package com.finflux.transaction.execution.service;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.WordUtils;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.external.data.ExternalServicePropertyData;
import com.finflux.portfolio.external.data.ExternalServicesData;
import com.finflux.portfolio.external.service.ExternalServicesReadService;
import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.domain.TaskConfigEntityTypeMapping;
import com.finflux.task.domain.TaskConfigEntityTypeMappingRepository;
import com.finflux.task.service.TaskPlatformWriteService;
import com.finflux.transaction.execution.data.*;
import com.finflux.transaction.execution.domain.BankAccountTransaction;
import com.finflux.transaction.execution.domain.BankAccountTransactionRepositoryWrapper;
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
	private final ConfigurationDomainService configurationDomainService;
	private final TaskPlatformWriteService taskPlatformWriteService;
	private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;
	private final BankAccountTransactionRepositoryWrapper transactionRepositoryWrapper;


	private static final String SECRET_KEY_1 = "B245072A69BC04DE34BB5D7272856191";
	private static final String SECRET_IV = "9A870F3B721ACE3B3098D3FBD26D3B8C";

	private final String defaultPaymentKey = "RBL";
	private final Map<String,String> defaultConfigMap = new HashMap<>();

	@Autowired
	public BankTransactionServiceImpl(final BankTransferProviderFactory bankTransferProviderFactory,
									  final ExternalServicesReadService externalServicesReadService,
									  final BankTransactionReadPlatformService readPlatformService,
									  final BankTransactionWriteService writePlatformService,
									  final ConfigurationDomainService configurationDomainService,
									  final TaskPlatformWriteService taskPlatformWriteService,
									  final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository,
									  final BankAccountTransactionRepositoryWrapper transactionRepositoryWrapper) {
		this.bankTransferProviderFactory = bankTransferProviderFactory;
		this.externalServicesReadService = externalServicesReadService;
		this.readPlatformService = readPlatformService;
		this.writePlatformService = writePlatformService;
		this.configurationDomainService = configurationDomainService;
		this.taskPlatformWriteService = taskPlatformWriteService;
		this.taskConfigEntityTypeMappingRepository =taskConfigEntityTypeMappingRepository;
		this.transactionRepositoryWrapper = transactionRepositoryWrapper;

//		defaultConfigMap.put("client_id","fd27b97f-7f62-4dd9-b27c-c5a02a6d7d00");
//		defaultConfigMap.put("client_secret","jK0aC3pQ3tG7mG1lY7eF1tV0nI5sO8vE1rN6xB2tT8vN7uG2lH");
//		defaultConfigMap.put("user","CHAITANYA");
//		defaultConfigMap.put("password","pass@123");
//		defaultConfigMap.put("keystore_path","/Users/dhirendra/workspace/conflux-git/clients/rbl/client1.p12");
//		defaultConfigMap.put("keystore_password","finflux");
//		defaultConfigMap.put("rbl_end_point","https://apideveloper.rblbank.com");
//		defaultConfigMap.put("do_single_txn_resource","/test/sb/rbl/v1/payments/corp/payment");
//		defaultConfigMap.put("get_single_txn_status_resource","/test/sb/rbl/v1/payments/corp/payment/query");
//		defaultConfigMap.put("rpt_code","HSBA");
//		defaultConfigMap.put("corporate_id","CHAITANYA");
	}

	@Override
	public boolean validateAccount(Long beneficiary) {
		return false;
	}

	@Override
	public BankTransactionDetail transactionEntry(
			Long externalPaymentServiceId,
			BankTransactionRequest transactionRequest) {
		BankAccountTransaction bankAccountTransaction = new BankAccountTransaction(
				transactionRequest.getEntityTypeId(), transactionRequest.getEntityId(),
				transactionRequest.getEntityTxnId(), TransactionStatus.DRAFTED.getValue(),
				transactionRequest.getDebiter().getId(), transactionRequest.getBeneficiary().getId(),
				transactionRequest.getAmount(), transactionRequest.getTransferType().getValue(),
				externalPaymentServiceId, transactionRequest.getReason());
		Long txnId = writePlatformService.createTransactionEntry(bankAccountTransaction);

		//create workflow
		if (this.configurationDomainService.isWorkFlowEnabled()) {
			/**
			 * Checking is loan product mapped with task configuration
			 * entity type LOAN_PRODUCT
			 */
			final TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = this.taskConfigEntityTypeMappingRepository
					.findOneByEntityTypeAndEntityId(TaskConfigEntityType.BANKTRANSACTION.getValue(), -1L);
			if (taskConfigEntityTypeMapping != null) {
				final Client client = transactionRequest.getClient();
				final Map<TaskConfigKey, String> map = new HashMap<>();
				map.put(TaskConfigKey.CLIENT_ID, String.valueOf(client.getId()));
				map.put(TaskConfigKey.BANK_TRANSACTION_ID, String.valueOf(txnId));
				StringBuilder description = new StringBuilder();
				description.append("Bank txn #").append(txnId).append(" for amount ").append(transactionRequest.getAmount());
				if(BankTransactionEntityType.fromInt(transactionRequest.getEntityTypeId()).isLoan()){
					map.put(TaskConfigKey.LOAN_ID, String.valueOf(transactionRequest.getEntityId()));
					map.put(TaskConfigKey.LOAN_TRANSACTION_ID, String.valueOf(transactionRequest.getEntityTxnId()));
					description.append(" | Loan Id #").append(transactionRequest.getEntityId());
					description.append(" | Loan TxnId #").append(transactionRequest.getEntityTxnId());
				}
				description.append(" | Client: ").append(client.getDisplayName())
						.append("(" + client.getId()+") in  " + WordUtils.capitalizeFully(client.getOfficeName()));
				final AppUser assignedTo = null;
				final Date dueDate = null;
				this.taskPlatformWriteService.createTaskFromConfig(taskConfigEntityTypeMapping.getTaskConfigId(),
						TaskEntityType.BANK_TRANSACTION, txnId, client,assignedTo,dueDate,
						client.getOffice(), map, description.toString());
			}
		}

		return getTransactionDetail(txnId);
	}

	@Override public List<BankTransactionDetail> getAllTransaction(BankTransactionEntityType entityType, Long entityId) {
		return readPlatformService.getAccountTransactionsByEntity(entityType,entityId);
	}

	@Override public BankTransactionDetail getTransactionDetail(Long transactionId) {
		return  readPlatformService.getAccountTransactionDetails(transactionId);
	}

	@Override
	public BankTransferService getBankTransferService(Long externalServiceId) {
		ExternalServicesData externalServiceData = externalServicesReadService.findOneWithNotFoundException(externalServiceId);
		List<ExternalServicePropertyData> extProperties =  externalServicesReadService.findClearPropertiesForExternalServices(externalServiceId);
		Map<String,String> propertiesMap = convertToKeyValueMap(extProperties);
		return bankTransferProviderFactory.getBankTransferService( externalServiceData.getName(),propertiesMap);
	}

	@Override
	public List<EnumOptionData> geetSupportedTransfers(Long transactionId) {
		BankAccountTransaction bankAccountTransaction = transactionRepositoryWrapper.findOneWithNotFoundDetection(transactionId);
		ExternalServicesData externalServiceData = externalServicesReadService.findOneWithNotFoundException(bankAccountTransaction.getExternalServiceId());
		List<TransferType> transferTypes =  bankTransferProviderFactory.getSupportedTransfers(externalServiceData.getName());
		List<EnumOptionData> transferTypeEnums = new ArrayList<>();
		if(transferTypes!=null){
			for(TransferType transferType: transferTypes){
				transferTypeEnums.add(transferType.getEnumOptionData());
			}
		}
		return transferTypeEnums;
	}

	private Map<String, String> convertToKeyValueMap(List<ExternalServicePropertyData> extProperties) {
		Map<String,String> propertiesMap = new HashMap<>();
		for(ExternalServicePropertyData propertyData: extProperties) {
			propertiesMap.put(propertyData.getName(), propertyData.getValue());
		}
		return propertiesMap;

//		try {
//			IvParameterSpec ivParameterSpec = new IvParameterSpec(Hex.decodeHex(SECRET_IV.toCharArray()));
//			SecretKeySpec secretKeySpec = new SecretKeySpec(Hex.decodeHex(SECRET_KEY_1.toCharArray()), "AES");
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//
//			for(ExternalServicePropertyData propertyData: extProperties){
//				if(!propertyData.getEncrypted()) {
//					propertiesMap.put(propertyData.getName(), propertyData.getValue());
//				}else{
//					//decrypt value
//					try {
//						String decryptedValue = decrypt(cipher,secretKeySpec,ivParameterSpec,propertyData.getValue());
//						propertiesMap.put(propertyData.getName(), decryptedValue);
//					} catch (InvalidAlgorithmParameterException e) {
//						e.printStackTrace();
//					} catch (InvalidKeyException e) {
//						e.printStackTrace();
//					} catch (BadPaddingException e) {
//						e.printStackTrace();
//					} catch (IllegalBlockSizeException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (NoSuchPaddingException e) {
//			e.printStackTrace();
//		} catch (DecoderException e) {
//			e.printStackTrace();
//		}
//		return  propertiesMap;
	}


	private String decrypt(Cipher cipher, SecretKeySpec secretKeySpec, IvParameterSpec ivParameterSpec,
						   String encrypted) throws InvalidAlgorithmParameterException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException {
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] decryptedBytes = cipher.doFinal(Base64.decodeBase64(encrypted));
		return new String(decryptedBytes);
	}

}
