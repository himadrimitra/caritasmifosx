package com.finflux.transaction.execution.service.provider.rbl;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.transaction.execution.service.provider.BankTransferService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.finflux.transaction.execution.data.*;
import com.finflux.transaction.execution.service.provider.rbl.request.RBLFundTransferRequest;
import com.finflux.transaction.execution.service.provider.rbl.request.RBLFundTransferStatusRequest;
import com.finflux.transaction.execution.service.provider.rbl.request.RBLSinglePaymentRequest;
import com.finflux.transaction.execution.service.provider.rbl.request.RBLSinglePaymentStatusRequest;
import com.finflux.transaction.execution.service.provider.rbl.response.RBLFundTransferResponse;
import com.finflux.transaction.execution.service.provider.rbl.response.RBLFundTransferStatusResponse;
import com.finflux.transaction.execution.service.provider.rbl.response.RBLSinglePaymentResponse;
import com.finflux.transaction.execution.service.provider.rbl.response.RBLSinglePaymentStatusResponse;

/**
 * Created by dhirendra on 23/11/16.
 */
public class RBLBankTransferService implements BankTransferService {

	private final static Logger logger = LoggerFactory
			.getLogger(RBLBankTransferService.class);

	private static final List<TransferType> SUPPORTED_TRANSFERS = Arrays
			.asList(new TransferType[]{TransferType.FT, TransferType.IMPS,
					TransferType.NEFT, TransferType.RTGS});
	private static final NumberFormat formatter = new DecimalFormat("#0.00");
	private static final String STATUS_SUCCESS = "success";
	private static final String STATUS_FAILED = "failed";
	private static final String STATUS_INITIATED = "initiated";
	private static final String KEY_CLIENT_SECRET = "client_secret";
	private static final String KEY_CLIENT_ID = "client_id";
	private static final String KEY_USER = "user";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_KEYSTORE_PATH = "keystore_path";
	private static final String KEY_KEYSTORE_PASSWORD = "keystore_password";
	private static final String KEY_RBL_END_POINT = "rbl_end_point";
	private static final String KEY_DO_SINGLE_TXN_RESOURCE = "do_single_txn_resource";
	private static final String KEY_GET_SINGLE_TXN_STATUS_RESOURCE = "get_single_txn_status_resource";
	private static final String KEY_RPT_CODE = "rpt_code";
	private static final String KEY_CORPORATE_ID = "corporate_id";
	private final String rptCode;
	private final String corporateId;
	private final String rblEndPoint;
	private final String doSingleTxnResource;
	private final String doSingleTxnStatusResource;
	private final String authorizationCode;
	private final String clientId;
	private final String clientSecret;
	private String validationResource;
	private RestTemplate restTemplate;
	private Boolean isConfigured = false;

	// String rblEndPoint = "https://apideveloper.rblbank.com";
	// String doSingleTxnResource = "/test/sb/rbl/v1/payments/corp/payment";
	// String clientId = "fd27b97f-7f62-4dd9-b27c-c5a02a6d7d00";
	// String clientSecret =
	// "jK0aC3pQ3tG7mG1lY7eF1tV0nI5sO8vE1rN6xB2tT8vN7uG2lH";

	private RBLBankTransferService(String clientId, String clientSecret,
			String user, String password, String keystorePath,
			String keyStorePassword, String rblEndPoint,
			String doSingleTxnResource, String doSingleTxnStatusResource,
			String rptCode, String corporateId) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		String authUser = user + ":" + password;
		byte[] encodedBytes = Base64.encodeBase64(authUser.getBytes());
		this.authorizationCode = new String(encodedBytes);
		this.rblEndPoint = rblEndPoint;
		this.doSingleTxnResource = doSingleTxnResource;
		this.doSingleTxnStatusResource = doSingleTxnStatusResource;
		this.rptCode = rptCode;
		this.corporateId = corporateId;
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(new FileInputStream(new File(keystorePath)),
					keyStorePassword.toCharArray());
			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
					new SSLContextBuilder()
							.loadTrustMaterial(null,
									new TrustSelfSignedStrategy())
							.loadKeyMaterial(keyStore,
									keyStorePassword.toCharArray()).build(),
					NoopHostnameVerifier.INSTANCE);

			HttpClient httpClient = HttpClients.custom()
					.setSSLSocketFactory(socketFactory).build();

			ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
					httpClient);
			restTemplate = new RestTemplate(requestFactory);
			isConfigured = true;
		} catch (Exception e) {
			isConfigured = false;
		}
	}

	public static RBLBankTransferService getInstance(Map<String, String> keyValueMap) {
		return new RBLBankTransferService(keyValueMap.get(KEY_CLIENT_ID),
				keyValueMap.get(KEY_CLIENT_SECRET), keyValueMap.get(KEY_USER),
				keyValueMap.get(KEY_PASSWORD),
				keyValueMap.get(KEY_KEYSTORE_PATH),
				keyValueMap.get(KEY_KEYSTORE_PASSWORD),
				keyValueMap.get(KEY_RBL_END_POINT),
				keyValueMap.get(KEY_DO_SINGLE_TXN_RESOURCE),
				keyValueMap.get(KEY_GET_SINGLE_TXN_STATUS_RESOURCE),
				keyValueMap.get(KEY_RPT_CODE),
				keyValueMap.get(KEY_CORPORATE_ID));
	}

	@Override
	public AccountTransactionResponse doTransaction(String internalTxnId,
			Double amount, String reason, BankAccountDetailData debitAccount,
		BankAccountDetailData beneficiaryAccount, TransferType transferType,
			String debitParticulars, String debitremarks,
			String beneficiaryParticulars, String beneficiaryRemarks) {

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Authorization", "Basic " + authorizationCode);
		headers.add("Content-Type", "application/json");

		RBLSinglePaymentRequest.Header header = new RBLSinglePaymentRequest.Header(
				internalTxnId, corporateId, null, null, null);

		RBLSinglePaymentRequest.Body body = new RBLSinglePaymentRequest.Body(
				formatter.format(amount), debitAccount.getAccountNumber(),
				debitAccount.getName(), debitAccount.getIfscCode(),
				debitAccount.getMobileNumber(), debitParticulars, debitremarks,
				beneficiaryAccount.getIfscCode(),
				beneficiaryAccount.getAccountNumber(),
				beneficiaryAccount.getName(), null, null,
				beneficiaryAccount.getEmail(),
				beneficiaryAccount.getMobileNumber(), beneficiaryParticulars,
				beneficiaryRemarks, transferType.toString(), rptCode, reason);

		String signatureStr = "Finflux";

		RBLSinglePaymentRequest.Signature signature = new RBLSinglePaymentRequest.Signature(
				signatureStr);

		RBLSinglePaymentRequest singlePaymentRequest = new RBLSinglePaymentRequest(
				header, body, signature);
		RBLFundTransferRequest rblFundTransferRequest = new RBLFundTransferRequest(
				singlePaymentRequest);
		// Gson gson = new Gson();
		// String requestData = gson.toJson(rblFundTransferRequest);
		// System.out.println(requestData);
		HttpEntity<RBLFundTransferRequest> request = new HttpEntity<>(
				rblFundTransferRequest, headers);

		UriComponentsBuilder builder = UriComponentsBuilder
				.fromUriString(doSingleTxnResource)
				.queryParam("client_id", clientId)
				.queryParam("client_secret", clientSecret);

		BasicHttpResponse basicHttpResponse = new BasicHttpResponse();
		TransactionStatus txnStatus = TransactionStatus.FAILED;
		String referenceNumber = null;
		try {
			ResponseEntity<RBLFundTransferResponse> response = restTemplate
					.postForEntity(rblEndPoint + builder.build().toUriString(),
							request, RBLFundTransferResponse.class);

			if (response != null & response.getBody() != null) {
				basicHttpResponse.setHttpStatusCode(response.getStatusCode()
						.toString());
				RBLSinglePaymentResponse paymentResponse = response.getBody()
						.getPaymentResponse();

				String requestStatus = null;

				if (paymentResponse != null) {
					if (paymentResponse.getHeader() != null) {
						requestStatus = paymentResponse.getHeader().getStatus();
						basicHttpResponse.setErrorCode(paymentResponse
								.getHeader().getErrorCode());
						basicHttpResponse.setErrorMessage(paymentResponse
								.getHeader().getErrorDescription());
					}
					if (paymentResponse.getBody() != null) {
						referenceNumber = paymentResponse.getBody()
								.getReferenceNumber();
					}
				}

				if (StringUtils.isNotEmpty(referenceNumber)
						&& !STATUS_FAILED.equalsIgnoreCase(requestStatus)) {
					basicHttpResponse.setSuccess(true);
					if (STATUS_INITIATED.equalsIgnoreCase(requestStatus)) {
						txnStatus = TransactionStatus.PENDING;
					} else if (STATUS_SUCCESS.equalsIgnoreCase(requestStatus)) {
						txnStatus = TransactionStatus.SUCCESS;
					}
				} else {
					basicHttpResponse.setSuccess(false);
				}

			} else {
				basicHttpResponse.setErrorMessage("Empty Response");
			}

		} catch (RestClientException e) {
			basicHttpResponse.setErrorMessage(e.getMessage());
			logger.warn("RBL DO Transaction Exception", e);
		}

		return new AccountTransactionResponse(basicHttpResponse, internalTxnId,
				referenceNumber, txnStatus);
	}

	@Override
	public AccountTransactionResponse getTransactionStatus(String internalTxnId,
			String referenceNumber, String makerId, String checkerId,
			String approverId) {

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Authorization", "Basic " + authorizationCode);
		headers.add("Content-Type", "application/json");

		RBLSinglePaymentStatusRequest.Header header = new RBLSinglePaymentStatusRequest.Header(
				internalTxnId, corporateId, makerId, checkerId, approverId);

		RBLSinglePaymentStatusRequest.Body body = new RBLSinglePaymentStatusRequest.Body(
				referenceNumber);

		String signatureStr = "Finflux";

		RBLSinglePaymentStatusRequest.Signature signature = new RBLSinglePaymentStatusRequest.Signature(
				signatureStr);

		RBLSinglePaymentStatusRequest singlePaymentStatusRequest = new RBLSinglePaymentStatusRequest(
				header, body, signature);
		RBLFundTransferStatusRequest rblFundTransferStatusRequest = new RBLFundTransferStatusRequest(
				singlePaymentStatusRequest);
		// Gson gson = new Gson();
		// String requestData = gson.toJson(rblFundTransferRequest);
		// System.out.println(requestData);
		HttpEntity<RBLFundTransferStatusRequest> request = new HttpEntity<>(
				rblFundTransferStatusRequest, headers);

		UriComponentsBuilder builder = UriComponentsBuilder
				.fromUriString(doSingleTxnStatusResource)
				.queryParam("client_id", clientId)
				.queryParam("client_secret", clientSecret);

		BasicHttpResponse basicHttpResponse = new BasicHttpResponse();
		TransactionStatus txnStatus = null;

		try {
			ResponseEntity<RBLFundTransferStatusResponse> response = restTemplate
					.postForEntity(rblEndPoint + builder.build().toUriString(),
							request, RBLFundTransferStatusResponse.class);

			if (response != null & response.getBody() != null) {
				basicHttpResponse.setHttpStatusCode(response.getStatusCode()
						.toString());
				RBLSinglePaymentStatusResponse paymentResponse = response
						.getBody().getpaymentStatusResponse();

				String requestStatus = null;
				String txnStatusStr = null;

				if (paymentResponse != null) {
					if (paymentResponse.getHeader() != null) {
						requestStatus = paymentResponse.getHeader().getStatus();
						basicHttpResponse.setErrorCode(paymentResponse
								.getHeader().getErrorCode());
						basicHttpResponse.setErrorMessage(paymentResponse
								.getHeader().getErrorDescription());
					}
					if (paymentResponse.getBody() != null) {
						txnStatusStr = paymentResponse.getBody()
								.getTransactionStatus();
					}
				}

				if (STATUS_SUCCESS.equalsIgnoreCase(requestStatus)) {
					basicHttpResponse.setSuccess(true);
					if (STATUS_INITIATED.equalsIgnoreCase(txnStatusStr)) {
						txnStatus = TransactionStatus.PENDING;
					} else if (STATUS_SUCCESS.equalsIgnoreCase(txnStatusStr)) {
						txnStatus = TransactionStatus.SUCCESS;
					} else if (STATUS_FAILED.equalsIgnoreCase(txnStatusStr)) {
						txnStatus = TransactionStatus.FAILED;
					}
				} else {
					basicHttpResponse.setSuccess(false);
				}

			} else {
				basicHttpResponse.setErrorMessage("Empty Response");
			}

		} catch (RestClientException e) {
			basicHttpResponse.setErrorMessage(e.getMessage());
			logger.warn("RBL Get Transaction Status Exception", e);
		}

		return new AccountTransactionResponse(basicHttpResponse, internalTxnId,
				referenceNumber, txnStatus);
	}

	@Override
	public void getStatus(String externalTxnId) {

	}
}
