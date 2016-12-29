package com.finflux.transaction.execution.service;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.finflux.portfolio.external.data.ExternalServicePropertyData;
import com.finflux.portfolio.external.data.ExternalServicesData;
import com.finflux.portfolio.external.data.ExternalServicesReadService;
import com.finflux.transaction.execution.data.BankTransactionDetail;
import com.finflux.transaction.execution.data.BankTransactionEntityType;
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


	private static final String SECRET_KEY_1 = "B245072A69BC04DE34BB5D7272856191";
	private static final String SECRET_IV = "9A870F3B721ACE3B3098D3FBD26D3B8C";

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
		List<ExternalServicePropertyData> extProperties =  externalServicesReadService.findPropertiesForExternalServices(externalServiceId);
		Map<String,String> propertiesMap = getDecryptedMap(extProperties);
		return bankTransferProviderFactory.getBankTransferService( externalServiceData.getName(),propertiesMap);
	}

	private Map<String, String> getDecryptedMap(List<ExternalServicePropertyData> extProperties) {
		Map<String,String> propertiesMap = new HashMap<>();
		try {
			IvParameterSpec ivParameterSpec = new IvParameterSpec(Hex.decodeHex(SECRET_IV.toCharArray()));
			SecretKeySpec secretKeySpec = new SecretKeySpec(Hex.decodeHex(SECRET_KEY_1.toCharArray()), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

			for(ExternalServicePropertyData propertyData: extProperties){
				if(!propertyData.getEncrypted()) {
					propertiesMap.put(propertyData.getName(), propertyData.getValue());
				}else{
					//decrypt value
					try {
						String decryptedValue = decrypt(cipher,secretKeySpec,ivParameterSpec,propertyData.getValue());
						propertiesMap.put(propertyData.getName(), decryptedValue);
					} catch (InvalidAlgorithmParameterException e) {
						e.printStackTrace();
					} catch (InvalidKeyException e) {
						e.printStackTrace();
					} catch (BadPaddingException e) {
						e.printStackTrace();
					} catch (IllegalBlockSizeException e) {
						e.printStackTrace();
					}
				}
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		return  propertiesMap;
	}


	private String decrypt(Cipher cipher, SecretKeySpec secretKeySpec, IvParameterSpec ivParameterSpec,
						   String encrypted) throws InvalidAlgorithmParameterException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException {
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] decryptedBytes = cipher.doFinal(Base64.decodeBase64(encrypted));
		return new String(decryptedBytes);
	}

}
