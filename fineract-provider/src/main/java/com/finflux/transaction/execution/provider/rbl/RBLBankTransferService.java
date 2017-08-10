package com.finflux.transaction.execution.provider.rbl;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finflux.transaction.execution.provider.rbl.response.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.finflux.infrastructure.external.requestreponse.data.ThirdPartyRequestEntityType;
import com.finflux.infrastructure.external.requestreponse.service.ThirdPartyRequestResponseWritePlatformService;
import com.finflux.portfolio.bank.data.BankAccountDetailData;
import com.finflux.transaction.execution.data.BankTransactionResponse;
import com.finflux.transaction.execution.data.BasicHttpResponse;
import com.finflux.transaction.execution.data.TransactionStatus;
import com.finflux.transaction.execution.data.TransferType;
import com.finflux.transaction.execution.provider.BankTransferService;
import com.finflux.transaction.execution.provider.rbl.request.RBLFundTransferRequest;
import com.finflux.transaction.execution.provider.rbl.request.RBLFundTransferStatusRequest;
import com.finflux.transaction.execution.provider.rbl.request.RBLPaymentRequestValidator;
import com.finflux.transaction.execution.provider.rbl.request.RBLSinglePaymentRequest;
import com.finflux.transaction.execution.provider.rbl.request.RBLSinglePaymentStatusRequest;
import com.google.gson.Gson;

/**
 * Created by dhirendra on 23/11/16.
 */
public class RBLBankTransferService implements BankTransferService {

	private final static Logger logger = LoggerFactory
			.getLogger(RBLBankTransferService.class);

	private static final NumberFormat formatter = new DecimalFormat("#0.00");
	private static final String STATUS_SUCCESS = "success";
	private static final String RESPONSECODE_SUCCESS = "00";
	private static final String STATUS_FAILED = "failed";
	private static final String STATUS_INPROGRESS = "IN PROGRESS";
	private static final String STATUS_FAILURE = "failure";
	private static final String STATUS_INITIATED = "initiated";
	private static final String IMPS_STATUS_SUCCESS = "2";
	private static final String IMPS_STATUS_FAILURE = "6";
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

	private final ObjectMapper jacksonMapper = new ObjectMapper();
	private final Gson gson = new Gson();
	private String validationResource;
	private RestTemplate restTemplate;
	private Boolean isConfigured = false;
	private final ThirdPartyRequestResponseWritePlatformService requestResponseLogWriter;
	private DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

	// String rblEndPoint = "https://apideveloper.rblbank.com";
	// String doSingleTxnResource = "/test/sb/rbl/v1/payments/corp/payment";
	// String clientId = "fd27b97f-7f62-4dd9-b27c-c5a02a6d7d00";
	// String clientSecret =
	// "jK0aC3pQ3tG7mG1lY7eF1tV0nI5sO8vE1rN6xB2tT8vN7uG2lH";

	private RBLBankTransferService(ThirdPartyRequestResponseWritePlatformService requestResponseLogWriter,
								   String clientId, String clientSecret,
			String user, String password, String keystorePath,
			String keyStorePassword, String rblEndPoint,
			String doSingleTxnResource, String doSingleTxnStatusResource,
			String rptCode, String corporateId) {
		this.requestResponseLogWriter = requestResponseLogWriter;
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
			logger.error("RBL bank transaction service init failed.",e);
			isConfigured = false;
		}
	}

	public static RBLBankTransferService getInstance(Map<String, String> keyValueMap,
													 ThirdPartyRequestResponseWritePlatformService thirdPartyRequestResponseWritePlatformService) {
		return new RBLBankTransferService(thirdPartyRequestResponseWritePlatformService, keyValueMap.get(KEY_CLIENT_ID),
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
    public BankTransactionResponse doTransaction(Long internalBankTransactionId, String internalTransactionReference, BigDecimal amount,
            String reason, BankAccountDetailData debitAccount, BankAccountDetailData beneficiaryAccount, TransferType transferType,
            String debitParticulars, String debitremarks, String beneficiaryParticulars, String beneficiaryRemarks, Long makerUserId,
            Long checkerUserId, Long approverUserId) {

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Basic " + authorizationCode);
        RBLSinglePaymentRequest.Header header = new RBLSinglePaymentRequest.Header(internalTransactionReference, corporateId, makerUserId,
                checkerUserId, approverUserId);

        RBLSinglePaymentRequest.Body body = new RBLSinglePaymentRequest.Body(formatter.format(amount), debitAccount.getAccountNumber(),
                debitAccount.getName(), debitAccount.getIfscCode(), debitAccount.getMobileNumber(), debitParticulars, debitremarks,
                beneficiaryAccount.getIfscCode(), beneficiaryAccount.getAccountNumber(), beneficiaryAccount.getName(),
                beneficiaryAccount.getBankCity(), beneficiaryAccount.getBankName(), beneficiaryAccount.getEmail(),
                beneficiaryAccount.getMobileNumber(), beneficiaryParticulars, beneficiaryRemarks, transferType.toString(), rptCode, reason);

        String signatureStr = "Finflux";

        RBLSinglePaymentRequest.Signature signature = new RBLSinglePaymentRequest.Signature(signatureStr);

        RBLSinglePaymentRequest singlePaymentRequest = new RBLSinglePaymentRequest(header, body, signature);
        RBLPaymentRequestValidator requestValidator = new RBLPaymentRequestValidator();
        requestValidator.validateNEFTSinglePaymentRequest(singlePaymentRequest);
        RBLFundTransferRequest rblFundTransferRequest = new RBLFundTransferRequest(singlePaymentRequest);

        // Gson gson = new Gson();
        // String requestData = gson.toJson(rblFundTransferRequest);
        // System.out.println(requestData);
        HttpEntity<RBLFundTransferRequest> request = new HttpEntity<>(rblFundTransferRequest, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(doSingleTxnResource).queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret);

        BasicHttpResponse basicHttpResponse = new BasicHttpResponse();
        TransactionStatus txnStatus = TransactionStatus.ERROR;
        String referenceNumber = null;
        String utrNumber = null;
        String poNumber = null;
        String responseBody = null;
        DateTime txnTime = null;
        HttpStatus responseHttpStatus = null;
        HttpHeaders responseHttpHeaders = null;
        StopWatch stopWatch = new StopWatch();
        String completeUrl = rblEndPoint + builder.build().toUriString();
        Long requestLogId = requestResponseLogWriter.registerRequest(ThirdPartyRequestEntityType.BANKTRANSACTION, internalBankTransactionId,
                HttpMethod.POST, rblEndPoint + doSingleTxnResource, convertToJsonStr(rblFundTransferRequest));
        stopWatch.start();
        try {
            ResponseEntity<RBLFundTransferResponse> response = restTemplate.exchange(completeUrl, HttpMethod.POST, request,
                    RBLFundTransferResponse.class);

            stopWatch.stop();
            responseBody = convertToJsonStr(response.getBody());
            responseHttpHeaders = response.getHeaders();
            responseHttpStatus = response.getStatusCode();

            basicHttpResponse.setHttpStatusCode(response.getStatusCode().toString());
            RBLSinglePaymentResponse paymentResponse = response.getBody().getPaymentResponse();

            String requestStatus = null;
            String responseCode = null;
            String paymentResponseStatus = null;
            String statusDescription = null;

            if (paymentResponse != null) {
                if (paymentResponse.getHeader() != null) {
                    requestStatus = paymentResponse.getHeader().getStatus();
                    responseCode = paymentResponse.getHeader().getResponseCode();
                    basicHttpResponse.setErrorCode(paymentResponse.getHeader().getErrorCode());
                    basicHttpResponse.setErrorMessage(paymentResponse.getHeader().getErrorDescription());
                }
                if (paymentResponse.getBody() != null) {
                    referenceNumber = paymentResponse.getBody().getReferenceNumber();
                    utrNumber = paymentResponse.getBody().getUtrNumber();
                    poNumber = paymentResponse.getBody().getPoNumber();
                    paymentResponseStatus = paymentResponse.getBody().getTransactionStatus();
                    statusDescription = paymentResponse.getBody().getStatusDescription();
                    if (paymentResponse.getBody().getTxnTime() != null) {
                        txnTime = DateTime.parse(paymentResponse.getBody().getTxnTime(), dateTimeFormatter);
                    }
                }
            }
            basicHttpResponse.setSuccess(true);
            if (STATUS_FAILED.equalsIgnoreCase(requestStatus) || STATUS_FAILURE.equalsIgnoreCase(requestStatus)) {
                txnStatus = TransactionStatus.FAILED;
            } else if (STATUS_SUCCESS.equalsIgnoreCase(requestStatus)) {
                if (paymentResponseStatus != null && StringUtils.isNotEmpty(paymentResponseStatus)) {
                    if (STATUS_SUCCESS.equalsIgnoreCase(paymentResponseStatus)) {
                        txnStatus = TransactionStatus.SUCCESS;
                    } else if (STATUS_FAILURE.equalsIgnoreCase(paymentResponseStatus)
                            || STATUS_FAILED.equalsIgnoreCase(paymentResponseStatus)) {
                        txnStatus = TransactionStatus.FAILED;
                        if (statusDescription != null && StringUtils.isNotBlank(statusDescription)) {
                            basicHttpResponse.setErrorMessage(statusDescription);
                        }
                    }
                } else if (StringUtils.isNotEmpty(responseCode)) {
                    if (!RESPONSECODE_SUCCESS.equalsIgnoreCase(responseCode)) {
                        txnStatus = TransactionStatus.PENDING;
                    }
                } else {
                    txnStatus = TransactionStatus.FAILED;
                }
            } else if (STATUS_INITIATED.equalsIgnoreCase(requestStatus)) {
                txnStatus = TransactionStatus.PENDING;
            } else {
                txnStatus = TransactionStatus.PENDING;
            }

        } catch (HttpStatusCodeException e) {
            stopWatch.stop();
            responseBody = e.getResponseBodyAsString();
            if (responseBody != null) {
                RBLHttpErrorResponse httpErrorResponse = gson.fromJson(responseBody, RBLHttpErrorResponse.class);
                if (httpErrorResponse != null) {
                    basicHttpResponse.setErrorMessage(httpErrorResponse.getMoreInformation());
                    basicHttpResponse.setErrorCode(httpErrorResponse.getHttpCode() + "-" + httpErrorResponse.getHttpMessage());
                }
            }
            responseHttpStatus = e.getStatusCode();
            responseHttpHeaders = e.getResponseHeaders();
            logger.warn("RBL Initiate Transaction  HttpStatusCodeException Exception", e);
            basicHttpResponse.setSuccess(false);
        } catch (RestClientException e) {
            stopWatch.stop();
            responseBody = e.getMessage();
            basicHttpResponse.setErrorMessage(e.getMessage());
            logger.warn("RBL Initiate Transaction RestClientException", e);
            basicHttpResponse.setSuccess(false);
        } catch (Exception e) {
            if (stopWatch.isStarted()) {
                stopWatch.stop();
            }
            if (responseBody == null) {
                responseBody = e.getMessage();
            }
            basicHttpResponse.setErrorMessage(e.getMessage());
            logger.warn("RBL Initiate Transaction unknown exception", e);
            basicHttpResponse.setSuccess(false);
        }
        final String loggableResponse = constructResponseString(responseHttpStatus, responseHttpHeaders, responseBody);
        requestResponseLogWriter.registerResponse(requestLogId, loggableResponse, stopWatch.getTime(),
                responseHttpStatus != null ? responseHttpStatus.value() : null);

        return new BankTransactionResponse(basicHttpResponse, internalTransactionReference, referenceNumber, txnStatus, utrNumber, poNumber,
                txnTime);
    }

	private String convertToJsonStr(Object object) {
		try {
			return jacksonMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			logger.warn("Json Parse Exception",e);
			return null;
		}
	}

	@Override
	public BankTransactionResponse getTransactionStatus(Long internalTxnId,
														String internalTransactionReference,
														String referenceNumber, Long makerId, Long checkerId,
														Long approverId) {

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Authorization", "Basic " + authorizationCode);
		headers.add("Content-Type", "application/json");

		RBLSinglePaymentStatusRequest.Header header = new RBLSinglePaymentStatusRequest.Header(
				internalTransactionReference, corporateId, makerId, checkerId, approverId);

		if(StringUtils.isEmpty(referenceNumber)){
			referenceNumber = "SP"+corporateId+internalTransactionReference;
		}
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
		TransactionStatus txnStatus = TransactionStatus.ERROR;

		String utrNumber = null;
		String poNumber = null;
		DateTime txnTime = null;

		String completeUrl = rblEndPoint + builder.build().toUriString();
		String responseBody = null;
		HttpStatus responseHttpStatus = null;
		HttpHeaders responseHttpHeaders = null;
		StopWatch stopWatch = new StopWatch();
		Long requestLogId = requestResponseLogWriter.registerRequest(ThirdPartyRequestEntityType.BANKTRANSACTION,internalTxnId,
				HttpMethod.POST,rblEndPoint+doSingleTxnResource,convertToJsonStr(rblFundTransferStatusRequest));
		stopWatch.start();
		try {
			ResponseEntity<RBLFundTransferStatusResponse> response = restTemplate
					.exchange(completeUrl,HttpMethod.POST,
							request, RBLFundTransferStatusResponse.class);

			stopWatch.stop();
			responseBody = convertToJsonStr(response.getBody());
			responseHttpHeaders = response.getHeaders();
			responseHttpStatus = response.getStatusCode();

			basicHttpResponse.setHttpStatusCode(response.getStatusCode()
					.toString());
			RBLSinglePaymentStatusResponse paymentResponse = response
					.getBody().getpaymentStatusResponse();

			String requestStatus = null;
			String txnStatusStr = null;
			String paymentStatus = null;
			String errorCode = null;
			String paymentResponseStatus = null;
			String statusDescription = null;

			if (paymentResponse != null) {
				if (paymentResponse.getHeader() != null) {
					requestStatus = paymentResponse.getHeader().getStatus();
					errorCode = paymentResponse
							.getHeader().getErrorCode();
					basicHttpResponse.setErrorCode(errorCode);
					basicHttpResponse.setErrorMessage(paymentResponse
							.getHeader().getErrorDescription());
				}
				if (paymentResponse.getBody() != null) {
					txnStatusStr = paymentResponse.getBody()
							.getTransactionStatus();
					paymentStatus = paymentResponse.getBody()
							.getPaymentStatus();
					utrNumber = paymentResponse.getBody().getUtrNumber();
					poNumber = paymentResponse.getBody().getPoNumber();
					paymentResponseStatus = paymentResponse.getBody().getTransactionStatus();
					statusDescription = paymentResponse.getBody().getStatusDescription();
					if(paymentResponse.getBody().getTxnTime()!=null){
						txnTime = DateTime.parse(paymentResponse.getBody().getTxnTime(),
								dateTimeFormatter);
					}
				}
			}

			basicHttpResponse.setSuccess(true);

			if (STATUS_SUCCESS.equalsIgnoreCase(requestStatus)) {
				if (StringUtils.isNotEmpty(txnStatusStr)) {
					if (STATUS_SUCCESS.equalsIgnoreCase(txnStatusStr)) {
					    if(paymentResponseStatus != null && StringUtils.isNotEmpty(paymentResponseStatus)){
					        if(STATUS_SUCCESS.equalsIgnoreCase(paymentResponseStatus)){
	                                                txnStatus = TransactionStatus.SUCCESS;
	                                            }else if(STATUS_FAILURE.equalsIgnoreCase(paymentResponseStatus) || STATUS_FAILED.equalsIgnoreCase(paymentResponseStatus)){
	                                                    txnStatus = TransactionStatus.FAILED;
	                                                    if (statusDescription != null && StringUtils.isNotBlank(statusDescription)) {
	                                                        basicHttpResponse.setErrorMessage(statusDescription);
	                                                    }
	                                            } 
					    }else{
					        txnStatus = TransactionStatus.FAILED;
					    }
					} else if (STATUS_FAILED.equalsIgnoreCase(txnStatusStr) || STATUS_FAILURE.equalsIgnoreCase(txnStatusStr)) {
						txnStatus = TransactionStatus.FAILED;
					} else if (STATUS_INPROGRESS.equalsIgnoreCase(txnStatusStr)) {
						txnStatus = TransactionStatus.PENDING;
					} else {
						txnStatus = TransactionStatus.PENDING;
					}
				} else if (StringUtils.isNotEmpty(paymentStatus)) {
					if (IMPS_STATUS_FAILURE.equalsIgnoreCase(paymentStatus)) {
						txnStatus = TransactionStatus.FAILED;
					} else if (IMPS_STATUS_SUCCESS.equalsIgnoreCase(paymentStatus)) {
						txnStatus = TransactionStatus.SUCCESS;
					} else {
						txnStatus = TransactionStatus.PENDING;
					}
				}
			} else if(STATUS_FAILED.equalsIgnoreCase(requestStatus)){
				if(RBLConstants.ERROR_CODE_ER009_RefNo_does_not_exist.equalsIgnoreCase(errorCode)){
					txnStatus = TransactionStatus.FAILED;
				}
			}

		} catch(HttpStatusCodeException e){
			stopWatch.stop();
			responseBody = e.getResponseBodyAsString();
			if(responseBody!=null){
				RBLHttpErrorResponse httpErrorResponse = gson.fromJson(responseBody,RBLHttpErrorResponse.class);
				if(httpErrorResponse!=null) {
					basicHttpResponse.setErrorMessage(httpErrorResponse.getMoreInformation());
					basicHttpResponse.setErrorCode(httpErrorResponse.getHttpCode() + "-" + httpErrorResponse.getHttpMessage());
				}
			}
			responseHttpStatus = e.getStatusCode();
			responseHttpHeaders = e.getResponseHeaders();
			logger.warn("RBL  Get Transaction Status HttpStatusCodeException Exception", e);
			basicHttpResponse.setSuccess(false);
		} catch (RestClientException e) {
			stopWatch.stop();
			responseBody = e.getMessage();
			basicHttpResponse.setErrorMessage(e.getMessage());
			logger.warn("RBL Get Transaction Status RestClientException", e);
			basicHttpResponse.setSuccess(false);
		} catch(Exception e){
			if(stopWatch.isStarted()){
				stopWatch.stop();
			}
			if(responseBody==null) {
				responseBody = e.getMessage();
			}
			basicHttpResponse.setErrorMessage(e.getMessage());
			logger.warn("RBL Get Transaction Status unknown exception", e);
			basicHttpResponse.setSuccess(false);
		}
		final String loggableResponse = constructResponseString(responseHttpStatus, responseHttpHeaders,responseBody);
		requestResponseLogWriter.registerResponse(requestLogId,loggableResponse,stopWatch.getTime(),
				responseHttpStatus!=null?responseHttpStatus.value():null);
		return new BankTransactionResponse(basicHttpResponse, internalTransactionReference,
				referenceNumber, txnStatus, utrNumber, poNumber, txnTime);
	}

	@Override
	public void getStatus(String externalTxnId) {

	}

	private String constructResponseString(final HttpStatus statusCode, final HttpHeaders headers,
										   final String bodyStr) {
		StringBuilder builder = new StringBuilder("<");
		if(statusCode!=null) {
			builder.append(statusCode.toString());
			builder.append(' ');
			builder.append(statusCode.getReasonPhrase());
			builder.append(',');
		}
		if (bodyStr != null) {
			builder.append(bodyStr);
			if (headers != null) {
				builder.append(',');
			}
		}
		if (headers != null) {
			builder.append(headers);
		}
		builder.append('>');
		return builder.toString();
	}
}
