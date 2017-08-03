
package com.finflux.infrastructure.external.authentication.aadhar.service;

import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Random;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.aadhaarconnect.bridge.capture.model.auth.AuthCaptureData;
import com.aadhaarconnect.bridge.capture.model.common.request.CertificateType;
import com.aadhaarconnect.bridge.gateway.model.AuthResponse;
import com.aadhaarconnect.bridge.gateway.model.OtpResponse;
import com.finflux.common.security.service.PlatformCryptoService;
import com.finflux.infrastructure.common.httpaccess.ExternalHttpConnectivity;
import com.finflux.infrastructure.external.authentication.aadhar.api.AadhaarApiConstants;
import com.finflux.infrastructure.external.authentication.aadhar.data.AadhaarServerDetails;
import com.finflux.infrastructure.external.authentication.aadhar.data.AadhaarServiceDataAssembler;
import com.finflux.infrastructure.external.authentication.aadhar.domain.AadhaarDataValidator;
import com.finflux.infrastructure.external.authentication.aadhar.domain.AadhaarOutBoundRequestDetails;
import com.finflux.infrastructure.external.authentication.aadhar.domain.AadhaarOutBoundRequestDetailsRepositoryWrapper;
import com.finflux.infrastructure.external.authentication.aadhar.domain.AadhaarRequestPurposeTypeEnum;
import com.finflux.infrastructure.external.authentication.aadhar.domain.AadhaarRequestStatusTypeEnum;
import com.finflux.infrastructure.external.authentication.aadhar.exception.AadhaarServiceResponseParserException;
import com.finflux.infrastructure.external.authentication.aadhar.exception.AuthRequestDataBuldFailedException;
import com.finflux.infrastructure.external.authentication.aadhar.exception.ConnectionFailedException;
import com.finflux.infrastructure.external.authentication.aadhar.exception.UnableToGetKycDetails;
import com.finflux.infrastructure.external.authentication.aadhar.exception.UnableToSendOtpException;
import com.finflux.organisation.transaction.authentication.api.TransactionAuthenticationApiConstants;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

@Service
public class AadhaarBridgeProvidedServiceImpl implements AadhaarBridgeProvidedService {
	private final String generateOtpPath = "/otp";
	/* to authenticate user with raw json without encryption */
	private static final String authRaw = "/auth/raw";

	// if the json is sent as plain json without encrypting the auth data use
	// the api path /kyc/raw

	/*
	 * to authenticate user with encrypted raw json, by passing this raw json to
	 * AadhaarBridgeUtil.buildAuthRequest(json) provided by Aadhaar bridge jar
	 *
	 **/
	private static final String auth = "/auth";
	private static final String kyc = "/kyc";
	private static final String INIT = "_init";
	private static final String FORM = "_form/";
	private final AadhaarServiceDataAssembler aadharServiceDataAssembler;

	private String endpoint;

	private final JdbcTemplate jdbcTemplate;
	private final ExternalHttpConnectivity httpConnectivity;
	private final FromJsonHelper fromJsonHelper;
	private final AadhaarDataValidator aadhaarDataValidator;
	private final AadhaarOutBoundRequestDetailsRepositoryWrapper aadhaarServicesRepositoryWrapper;
	private final PlatformCryptoService platformCryptoService;

	private AadhaarServerDetails aadhaarServerDetails;
	private CertificateType certificateType;
	private final FromJsonHelper fromApiJsonHelper;
	
	private void initalize() {
		this.aadhaarServerDetails = getAadhaarServerDetails();
		setCertificateType();
		setEndpoint();
	}

	private void setEndpoint() {
		String url = this.aadhaarServerDetails.getHost();
		String port = this.aadhaarServerDetails.getPort();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("http://");
		stringBuilder.append(url);
		stringBuilder.append(':');
		stringBuilder.append(port);
		this.endpoint = stringBuilder.toString();
	}

	private void setCertificateType() {
		if (aadhaarServerDetails.getCertificateType().equals(AadhaarServiceConstants.PROD)) {
			this.certificateType = CertificateType.prod;
		} else {
			this.certificateType = CertificateType.preprod;
		}
	}

	@Autowired
	public AadhaarBridgeProvidedServiceImpl(final AadhaarServiceDataAssembler aadhaarServiceDataAssembler,
			final RoutingDataSource dataSource, final ExternalHttpConnectivity httpConnectivity,
			final FromJsonHelper fromJsonHelper, final AadhaarDataValidator aadhaarDataValidator, 
			final AadhaarOutBoundRequestDetailsRepositoryWrapper aadhaarServicesRepositoryWrapper, final PlatformCryptoService platformCryptoService,
			final FromJsonHelper fromApiJsonHelper) {
		this.aadharServiceDataAssembler = aadhaarServiceDataAssembler;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.httpConnectivity = httpConnectivity;
		this.fromJsonHelper = fromJsonHelper;
		this.aadhaarDataValidator = aadhaarDataValidator;
		this.aadhaarServicesRepositoryWrapper = aadhaarServicesRepositoryWrapper;
		this.platformCryptoService = platformCryptoService;
		this.fromApiJsonHelper = fromApiJsonHelper ;
	}

	private String urlBuilder(final String endpoint, final String path) {
		StringBuilder url = new StringBuilder();
		url.append(endpoint);
		url.append(path);
		return url.toString();
	}

	@Override
	public OtpResponse generateOtp(final String aadhaarNumber) {
		initalize();
		String otpCaptureRequestJson = this.aadharServiceDataAssembler.otpRequestDataAssembler(aadhaarNumber);
		String url = urlBuilder(endpoint, generateOtpPath);

		String response = this.httpConnectivity.performServerPost(url, otpCaptureRequestJson, String.class);
		if (response != null) {
			try {
				OtpResponse otpResponse = new Gson().fromJson(response, OtpResponse.class);
				return otpResponse;
			} catch (JsonSyntaxException jpe) {
				throw new AadhaarServiceResponseParserException();
			}
		} else {
			throw new ConnectionFailedException("Unable to communicate with Aadhaar server");
		}

	}

	@Override
	public AuthResponse authenticateUserByOtp(String aadhaarNumber, String otp) {
		initalize();
		final JsonElement element = this.fromApiJsonHelper.parse(otp);
		final String otpElement = this.fromApiJsonHelper.extractStringNamed("OtpPinNumber", element) ;
		JsonElement otpResponseElement = element.getAsJsonObject().get("otpResponse");
		final String transactionIdElement = this.fromApiJsonHelper.extractStringNamed("aadhaar-reference-code", otpResponseElement) ;
			String authenticateUserByOtpJson = this.aadharServiceDataAssembler
				.authenticateUserUsingOtpDataAssembler(aadhaarNumber, otpElement, this.certificateType, transactionIdElement);
		String url = urlBuilder(endpoint, authRaw);

		String response = this.httpConnectivity.performServerPost(url, authenticateUserByOtpJson, String.class);
		if (response != null) {
			try {
				AuthResponse authResponse = new Gson().fromJson(response, AuthResponse.class);
				return authResponse;
			} catch (JsonSyntaxException jpe) {
				throw new AadhaarServiceResponseParserException();
			}
		} else {
			throw new ConnectionFailedException("Unable to communicate with Aadhaar server");
		}
	}

	@Override
	public AuthResponse authenticateUserByFingerPrintUsingAadhaarService(final String aadharNumber, final String authData) {
		initalize();
		String authenticateUserByFingerprintJson = this.aadharServiceDataAssembler
				.authenticateUserUsingFingerprintDataAssembler(aadharNumber,authData );
		String url = urlBuilder(endpoint, auth);

		String response = this.httpConnectivity.performServerPost(url, authenticateUserByFingerprintJson, String.class);

		if (response != null) {
			try {
				AuthResponse authResponse = new Gson().fromJson(response, AuthResponse.class);
				return authResponse;
			} catch (JsonSyntaxException jpe) {
				throw new AadhaarServiceResponseParserException();
			}
		} else {
			throw new ConnectionFailedException("Unable to communicate with Aadhaar server");
		}
	
	}

	
	public String obtainEKycUsingAadhaarService(final String aadhaarNumber, final String authData,
			final String transactionId, final String authType) {
        initalize();
        String kycByFingerprintJson = this.aadharServiceDataAssembler.getKycCaptureData(aadhaarNumber, authData,
                        certificateType, transactionId, authType);
        String url = urlBuilder(endpoint, kyc);

        String response = this.httpConnectivity.performServerPost(url, kycByFingerprintJson, String.class);

        if (response != null) {
                JsonElement element = this.fromJsonHelper.parse(response);
                boolean isSuccess = this.fromJsonHelper.extractBooleanNamed(AadhaarApiConstants.SUCCESS, element);
                if (isSuccess) {
                        return response;
                } 
                throw new UnableToGetKycDetails(aadhaarNumber, authType);

        }
        throw new ConnectionFailedException("Unable to communicate with Aadhaar server");
}

    private AadhaarServerDetails getAadhaarServerDetails() {
        final ResultSetExtractor<AadhaarServerDetails> resultSetExtractor = new AadhaarServerDetailsMapper();
        final String sql = "SELECT esp.name, esp.value FROM c_external_service_properties esp inner join c_external_service es on esp.external_service_id = es.id where es.name = '"
                + AadhaarServiceConstants.AADHAAR_SERVICE_NAME + "'";
        final AadhaarServerDetails aadhaarServerDetails = this.jdbcTemplate.query(sql, resultSetExtractor, new Object[] {});
        return aadhaarServerDetails;
    }

    private final class AadhaarServerDetailsMapper implements ResultSetExtractor<AadhaarServerDetails> {

        @Override
        public AadhaarServerDetails extractData(ResultSet rs) throws SQLException, DataAccessException {
            String host = null;
            String port = null;
            String certificateType = null;
            String saltKey = null;
            String saCode = null;
            String initUrl = null;
            String kycUrl = null;
            while (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase(AadhaarServiceConstants.AADHAAR_HOST)) {
                    host = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(AadhaarServiceConstants.AADHAAR_PORT)) {
                    port = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(AadhaarServiceConstants.AADHAAR_SERVER_CERTIFICATE)) {
                    certificateType = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(AadhaarServiceConstants.SALTKEY)) {
                    saltKey = rs.getString("value");
                    if (saltKey != null) {
                        saltKey = platformCryptoService.decrypt(saltKey);
                    }
                } else if (rs.getString("name").equalsIgnoreCase(AadhaarServiceConstants.SACODE)) {
                    saCode = rs.getString("value");
                    if (saCode != null) {
                        saCode = platformCryptoService.decrypt(saCode);
                    }
                } else if (rs.getString("name").equalsIgnoreCase(AadhaarServiceConstants.INITURL)) {
                	initUrl = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(AadhaarServiceConstants.KYCURL)) {
                    kycUrl = rs.getString("value");
                }
            }
            return AadhaarServerDetails.instance(host, port, certificateType, saltKey, saCode, initUrl, kycUrl);
        }
    }

	@Override
	public OtpResponse processOtpRequest(final String json) {
		this.aadhaarDataValidator.validateJsonForGenerateOtp(json);
		final JsonElement element = this.fromJsonHelper.parse(json);
		final String aadhaarNumber = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.AADHAAR_NUMBER,
				element);
		final OtpResponse otpResponse = generateOtp(aadhaarNumber);
		if (otpResponse.isSuccess()) {
			return otpResponse;
		} else {
			throw new UnableToSendOtpException(aadhaarNumber);
		}
	}

	@Override
	public String processKycRequest(String json) {
		this.aadhaarDataValidator.validateJsonForEKyc(json);
		final JsonElement element = this.fromJsonHelper.parse(json);
		final String aadhaarNumber = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.AADHAAR_NUMBER,
				element);
		final String authType = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.AUTH_TYPE, element);
		final String authData = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.AUTH_DATA, element);
		JsonElement locationElement = element.getAsJsonObject().get("location");
		final String transactionId = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.TRANSACTIONID,element);
		String kycData = obtainEKycUsingAadhaarService(aadhaarNumber, authData, transactionId, authType) ;
		return kycData ;
	}

    @Override
    public String initiateOtpRequest(String json) {
        initalize();
        this.aadhaarDataValidator.validateJsonForGenerateOtp(json);
        final JsonElement element = this.fromJsonHelper.parse(json);
        final String aadhaarNumber = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.AADHAAR_NUMBER, element);
        /**
         * fetch the sacode and saltkey from external services
         **/
        String initUrl = this.aadhaarServerDetails.getInitUrl();
		initUrl = initUrl + this.INIT;
        final String saCode = this.aadhaarServerDetails.getSaCode();
        final String saltKey = this.aadhaarServerDetails.getSaltKey();
        final String redirectUrl = this.endpoint;
        String requestId = generateRandomRequestId(aadhaarNumber, 16);
        String uuId = null;
        final String hash = generateHashKey(saCode, aadhaarNumber, requestId, saltKey, uuId);
        final String status = AadhaarRequestStatusTypeEnum.enumTypeInitiate;
        AadhaarOutBoundRequestDetails aadhaarServices = AadhaarOutBoundRequestDetails.create(aadhaarNumber, requestId, status);
        this.aadhaarServicesRepositoryWrapper.save(aadhaarServices);
        final AadhaarRequestPurposeTypeEnum purposeTypeEnum = AadhaarRequestPurposeTypeEnum.fromInt(aadhaarServices.getPurpose());
        final EnumOptionData optionData = AadhaarRequestPurposeTypeEnum.aadhaarRequestEntity(purposeTypeEnum);
        String purpose = null;
        if (optionData != null) {
            purpose = optionData.getValue();
        }
        MultiValueMap<String, String> mapParams = this.aadharServiceDataAssembler.otpRequestDataAssembler(redirectUrl, aadhaarNumber, requestId, purpose,
                saCode, hash);
        URI response = this.httpConnectivity.performForLocation(initUrl, mapParams);
		String webUrl = this.aadhaarServerDetails.getInitUrl();
		webUrl = webUrl + this.FORM;
		if (response != null) {
			String uriStr = response.toString();
			String[] tokens = uriStr.split("/");
			String responseUrl = tokens[tokens.length - 1];

			if (responseUrl != null && responseUrl.length() > 15) {
				webUrl = webUrl + responseUrl;
			}
		}
		return webUrl;
    }

    /**
     * Generate the 20 digits random unique requestId per each aadhaar request.
     * 16 digits will be unique number and 4 digits will be last four digits of
     * aadhharNumber
     **/

    private String generateRandomRequestId(final String aadhaarNumber, final int length) {
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        if (aadhaarNumber.length() > 4)
            return (aadhaarNumber.substring(aadhaarNumber.length() - 4, aadhaarNumber.length()-1) + (new String(digits)));
        else
            return (new String(digits));

    }

    public String generateHashKey(final String saCode, final String aadhaarNumber, final String requestId, final String saltKey,
            final String uuId) {
        /**
         * construct the hash key
         **/
        StringBuilder builder = new StringBuilder();
        if (uuId != null && !uuId.isEmpty()) {
            builder.append(uuId);
            builder.append("|");
        }
        builder.append(saCode);
        builder.append("|");
        builder.append(aadhaarNumber);
        builder.append("|");
        builder.append(requestId);
        builder.append("|");
        builder.append(saltKey);
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new AuthRequestDataBuldFailedException("OTP");
        }
        return new String(Hex.encode(digest.digest(builder.toString().getBytes())));
    }

    @Override
    public String initiateKycRequest(String json) {
        this.aadhaarDataValidator.validateJsonForGetEKyc(json);
        initalize();
        final JsonElement element = this.fromJsonHelper.parse(json);
        String aadhaarNumber = null;
        final String requestId = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.REQUESTID, element);

        final String uuId = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.AADHAARUUID, element);
        if (requestId != null) {
            AadhaarOutBoundRequestDetails aadhaar = this.aadhaarServicesRepositoryWrapper.findRequestIdWithNotFoundDetection(requestId);
            if (aadhaar != null) {
                aadhaarNumber = aadhaar.getAadhaarNumber();
                final String status = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.REQUESTSTATUS, element);
                if (status != null) {
                    aadhaar = aadhaar.update(status);
                    this.aadhaarServicesRepositoryWrapper.save(aadhaar);
                }
            }
        }

        final String receivedHash = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.HASH, element);
        final String eKycUrl = this.aadhaarServerDetails.getKycUrl();
        final String saCode = this.aadhaarServerDetails.getSaCode();
        final String saltKey = this.aadhaarServerDetails.getSaltKey();
        final String hash = generateHashKey(saCode, aadhaarNumber, requestId, saltKey, uuId);
        this.aadhaarDataValidator.validateReceivedHash(receivedHash, saltKey, requestId, aadhaarNumber, saCode);
        String eKycByOtpJson = this.aadharServiceDataAssembler.getEkycCaptureData(aadhaarNumber, requestId, uuId, saCode, hash);

        String response = this.httpConnectivity.performServerPost(eKycUrl, eKycByOtpJson, String.class);

        if (response != null) {
            JsonElement elements = this.fromJsonHelper.parse(response);
            boolean isSuccess = this.fromJsonHelper.extractBooleanNamed(AadhaarApiConstants.SUCCESS, elements);
            if (isSuccess) {
                return response;
            } else {
                throw new UnableToGetKycDetails(aadhaarNumber, "Otp");
            }

        } else {
            throw new ConnectionFailedException("Unable to communicate with Aadhaar server");
        }
    }
    
	@Override
	public String initiateRequestWithBiometricData(String json) {
		AuthCaptureData capturedData = this.aadhaarDataValidator.validateAndCreateAuthCaptureData(json);
		initalize();
		final JsonElement element = this.fromJsonHelper.parse(json);
		final String aadhaarNumber = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.AADHAAR_ID, element);
		/**
		 * fetch the sacode and saltkey from external services
		 **/
		String initUrl = this.aadhaarServerDetails.getInitUrl();
		initUrl = initUrl + this.INIT;
		final String saCode = this.aadhaarServerDetails.getSaCode();
		final String saltKey = this.aadhaarServerDetails.getSaltKey();
		final String redirectUrl = this.endpoint;
		String requestId = generateRandomRequestId(aadhaarNumber, 16);
		String uuId = null;
		final String hash = generateHashKey(saCode, aadhaarNumber, requestId, saltKey, uuId);
		final String status = AadhaarRequestStatusTypeEnum.enumTypeInitiate;
		AadhaarOutBoundRequestDetails aadhaarServices = AadhaarOutBoundRequestDetails.create(aadhaarNumber, requestId,
				status);
		this.aadhaarServicesRepositoryWrapper.save(aadhaarServices);
		final AadhaarRequestPurposeTypeEnum purposeTypeEnum = AadhaarRequestPurposeTypeEnum
				.fromInt(aadhaarServices.getPurpose());
		final EnumOptionData optionData = AadhaarRequestPurposeTypeEnum.aadhaarRequestEntity(purposeTypeEnum);
		String purpose = null;
		if (optionData != null) {
			purpose = optionData.getValue();
		}
		MultiValueMap<String, String> mapParams = this.aadharServiceDataAssembler.initiateBiometricRequest(redirectUrl,
				aadhaarNumber, requestId, purpose, saCode, hash, capturedData);
		URI response = this.httpConnectivity.performForLocation(initUrl, mapParams);
		String webUrl = this.aadhaarServerDetails.getInitUrl();
		webUrl = webUrl+this.FORM;
		if (response != null) {
			String uriStr = response.toString();
			String[] tokens = uriStr.split("/");
			String responseUrl = tokens[tokens.length - 1];

			if (responseUrl != null && responseUrl.length() > 15) {
				webUrl = webUrl + responseUrl;
			}
		}
		return webUrl;
	}

}
