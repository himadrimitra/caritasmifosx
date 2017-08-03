package com.finflux.infrastructure.external.authentication.aadhar.data;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.aadhaarconnect.bridge.capture.model.auth.AuthCaptureData;
import com.aadhaarconnect.bridge.capture.model.auth.AuthCaptureRequest;
import com.aadhaarconnect.bridge.capture.model.common.ConsentType;
import com.aadhaarconnect.bridge.capture.model.common.Location;
import com.aadhaarconnect.bridge.capture.model.common.LocationType;
import com.aadhaarconnect.bridge.capture.model.common.request.CertificateType;
import com.aadhaarconnect.bridge.capture.model.common.request.Modality;
import com.aadhaarconnect.bridge.capture.model.common.request.ModalityType;
import com.aadhaarconnect.bridge.capture.model.common.response.Modalities;
import com.aadhaarconnect.bridge.capture.model.common.response.Pid;
import com.aadhaarconnect.bridge.capture.model.common.response.PidType;
import com.aadhaarconnect.bridge.capture.model.common.response.SessionKey;
import com.aadhaarconnect.bridge.capture.model.kyc.KycCaptureData;
import com.aadhaarconnect.bridge.capture.model.otp.AMType;
import com.aadhaarconnect.bridge.capture.model.otp.OtpCaptureRequest;
import com.aadhaarconnect.bridge.capture.model.otp.OtpChannel;
import com.finflux.infrastructure.external.authentication.aadhar.api.AadhaarApiConstants;
import com.finflux.infrastructure.external.authentication.aadhar.exception.AuthRequestDataBuldFailedException;
import com.finflux.kyc.address.service.AddressReadPlatformService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Service
public class AadhaarServiceDataAssembler {

	private final DefaultToApiJsonSerializer jsonSerializer;
	private final FromJsonHelper fromJsonHelper;
	
	@Autowired
    public AadhaarServiceDataAssembler(final DefaultToApiJsonSerializer jsonSerializer, final FromJsonHelper fromJsonHelper) {
        this.jsonSerializer = jsonSerializer;
        this.fromJsonHelper = fromJsonHelper;
    }
	
    public String otpRequestDataAssembler(final String aadhaarNumber) {
        OtpCaptureRequest otpCaptureRequest = new OtpCaptureRequest();
        otpCaptureRequest.setAadhaar(aadhaarNumber);
        otpCaptureRequest.setChannel(OtpChannel.SMS);
        otpCaptureRequest.setType(AMType.A);
        String json = new Gson().toJson(otpCaptureRequest);
        return json;
    }

    private Location setLocation(final LocationType locationType, final String pincode, final String latitude, final String longitude) {
        Location location = new Location();
        location.setType(locationType);
        if (locationType == LocationType.pincode) {
            location.setPincode(pincode);
        } else if (locationType == LocationType.gps) {
            location.setLatitude(latitude);
            location.setLongitude(longitude);
        }
        return location;
    }

    public String authenticateUserUsingOtpDataAssembler(String aadhaarNumber, String otp, CertificateType certificateType,
            final String transactionId) {
        AuthCaptureRequest authCaptureRequest = assembleAuthRequestUsingOtp(aadhaarNumber, otp, certificateType,transactionId);
        String json = new Gson().toJson(authCaptureRequest);
        return json;
    }

    private AuthCaptureRequest assembleAuthRequestUsingOtp(String aadhaarNumber, final String otp, CertificateType certificateType,
            final String transactionId) {
        AuthCaptureRequest authCaptureRequest = new AuthCaptureRequest();
        authCaptureRequest.setAadhaar(aadhaarNumber);
        authCaptureRequest.setConsent(ConsentType.Y);
        authCaptureRequest.setCertificateType(certificateType);
        authCaptureRequest.setTxnId(transactionId);
        authCaptureRequest.setModality(Modality.otp);
        authCaptureRequest.setOtp(otp);
        return authCaptureRequest;
    }

    public String authenticateUserUsingFingerprintDataAssembler(final String json) {
        final AuthCaptureData authCaptureData = assembleAndGetAuthCaptureDataForFingerPrint(json);
       return this.jsonSerializer.serialize(authCaptureData);
    }

    public String getKycCaptureData(String aadhaarNumber, String authData, CertificateType certificateType, final String transactionId,
            final String authType) {
        KycCaptureData kycCaptureData = new KycCaptureData();
        kycCaptureData.setConsent(ConsentType.Y);
        AuthCaptureData authCaptureData = null;

        switch (authType) {
            case AadhaarApiConstants.AUTHTYPE_FINGERPRINT:
                //authCaptureData = assembleAndGetAuthCaptureDataForFingerPrint(aadhaarNumber, authData, certificateType, location);
            break;
            case AadhaarApiConstants.AUTHTYPE_OTP:
                AuthCaptureRequest authCaptureRequest = assembleAuthRequestUsingOtp(aadhaarNumber, authData, certificateType, transactionId);
                try {
                    //authCaptureData = AadhaarBridgeUtil.buildAuthRequest(authCaptureRequest);
                } catch (Exception e) {
                    throw new AuthRequestDataBuldFailedException(authType);
                }
             break ;
            case AadhaarApiConstants.AUTHTYPE_IRIS:
                //authCaptureData = assembleAndGetAuthCaptureDataForIris(aadhaarNumber, authData, certificateType, location) ;
                break ;
        }
        kycCaptureData.setAuthCaptureData(authCaptureData); // refer
                                                            // authCaptureData
                                                            // for fp
       //kycCaptureData.setTs(ISO8601.now());
        String json = new Gson().toJson(kycCaptureData);
        return json;
    }

    public AuthCaptureData assembleAndGetAuthCaptureDataForFingerPrint(final String json) {
    	if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();

		this.fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, AadhaarApiConstants.IRIS_REQUEST_DATA);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder dataValidatorBuilder = new DataValidatorBuilder(dataValidationErrors)
				.resource(AadhaarApiConstants.AADHAAR_SERIVICE_RESOURCE_NAME);

		AuthCaptureData authCaptureData = new AuthCaptureData();
		JsonElement element = this.fromJsonHelper.parse(json);
		authCaptureData.setConsent(ConsentType.Y);
		final String aadhaarNumber = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.AADHAAR_ID, element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.AADHAAR_NUMBER).value(aadhaarNumber).notNull();
		authCaptureData.setAadhaar(aadhaarNumber);
		final JsonObject parentObject = this.fromJsonHelper.parse(json).getAsJsonObject();
		final JsonElement modality = parentObject.get(AadhaarApiConstants.MODALITY);
		Modalities modalities = new Modalities();
		modalities.setIris(this.fromJsonHelper.extractBooleanNamed(AadhaarApiConstants.IRIS, modality));
		modalities.setOtp(this.fromJsonHelper.extractBooleanNamed(AadhaarApiConstants.AUTHTYPE_OTP, modality));
		modalities.setPin(this.fromJsonHelper.extractBooleanNamed(AadhaarApiConstants.PIN, modality));
		modalities.setDemographics(this.fromJsonHelper.extractBooleanNamed(AadhaarApiConstants.DEMOGRAPHIC, modality));
		modalities.setFpMinutae(this.fromJsonHelper.extractBooleanNamed(AadhaarApiConstants.FP_MINUTAE, modality));
		modalities.setFpImage(this.fromJsonHelper.extractBooleanNamed(AadhaarApiConstants.FP_IMAGE, modality));
		authCaptureData.setModalities(modalities);
		final JsonElement pidData = parentObject.get(AadhaarApiConstants.PID);
		Pid pid = new Pid();
		final String pidType = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.TYPE, pidData);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.TYPE).value(pidType).notNull().notBlank();
		;
		pid.setType(PidType.valueOf(pidType));
		final String authData = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.PID_VALUE, pidData);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.PID_VALUE).value(authData).notNull().notBlank();
		;
		pid.setValue(authData);
		authCaptureData.setPid(pid);
		final String hmac = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.HMAC, element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.HMAC).value(hmac).notNull().notBlank();
		;
		authCaptureData.setHmac(hmac);
		final JsonElement sessionData = parentObject.get(AadhaarApiConstants.SESSION_KEY);
		SessionKey sessionKey = new SessionKey();
		final String certificateId = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.CERTIFICATE_ID,
				sessionData);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.CERTIFICATE_ID).value(certificateId).notNull()
				.notBlank();
		;
		final String keyValue = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.CERTIFICATE_VALUE,
				sessionData);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.CERTIFICATE_VALUE).value(sessionKey).notNull()
				.notBlank();
		;
		sessionKey.setCertificateId(certificateId);
		sessionKey.setValue(keyValue);
		authCaptureData.setSessionKey(sessionKey);
		final String uniqueDeviceCode = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.UNIQUE_DEVICE_CODE,
				element);
		authCaptureData.setUniqueDeviceCode(uniqueDeviceCode);
		final String dpId = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.DPID, element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.DPID).value(dpId).notNull().notBlank();
		final String rdsId = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.RDSID, element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.RDSID).value(rdsId).notNull().notBlank();
		final String rdsVer = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.RDSVER, element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.RDSVER).value(rdsVer).notNull().notBlank();
		final String dc = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.DC, element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.DC).value(dc).notNull().notBlank();
		final String mi = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.MI, element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.MI).value(mi).notNull().notBlank();
		final String mc = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.MC, element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.MC).value(mc).notNull().notBlank();
		authCaptureData.setDpId(dpId);
		authCaptureData.setRdsId(rdsId);
		authCaptureData.setRdsVer(rdsVer);
		authCaptureData.setDc(dc);
		authCaptureData.setMc(mc);
		authCaptureData.setMi(mi);
		throwExceptionIfValidationWarningsExist(dataValidationErrors);
		return authCaptureData;
    }
    
    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            //
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }

    public MultiValueMap<String, String> otpRequestDataAssembler(final String redirectUrl, final String aadhaarNumber,
            final String requestId, final String purposeType, final String saCode, final String hash) {
        /**
         * constructing the url query parameters
         **/
        	MultiValueMap<String, String> urlParam = new LinkedMultiValueMap<>();
            urlParam.add(AadhaarApiConstants.SACODE, saCode);
            urlParam.add(AadhaarApiConstants.AADHAARID, aadhaarNumber);
            urlParam.add(AadhaarApiConstants.SUCCESSURL, redirectUrl);
            urlParam.add(AadhaarApiConstants.FAILUREURL, redirectUrl);
            urlParam.add(AadhaarApiConstants.REQUESTID, requestId);
            urlParam.add(AadhaarApiConstants.PURPOSE, purposeType);
            urlParam.add(AadhaarApiConstants.MODALITY, AadhaarApiConstants.AUTHTYPE_OTP);
            urlParam.add(AadhaarApiConstants.HASH, hash);
            urlParam.add(AadhaarApiConstants.CHANNEL,OtpChannel.SMS.toString());
            return urlParam;
    }

    public String getEkycCaptureData(final String aadhaarNumber, final String requestId, final String uuId, final String saCode,
            final String hash) {

        EkycRequestData kycRequestData = new EkycRequestData();
        kycRequestData.setSaCode(saCode);
        kycRequestData.setAadhaarId(aadhaarNumber);
        kycRequestData.setRequestId(requestId);
        kycRequestData.setHash(hash);
        kycRequestData.setUuid(uuId);
        String json = new Gson().toJson(kycRequestData);
        return json;
    }
    public MultiValueMap<String, String> initiateBiometricRequest(final String redirectUrl, final String aadhaarNumber,
            final String requestId, final String purposeType, final String saCode, final String hash,final AuthCaptureData authCaptureData) {
        /**
         * constructing the request body parameters
         **/
    	MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    	requestParams.add(AadhaarApiConstants.SACODE, saCode);
    	requestParams.add(AadhaarApiConstants.AADHAARID, aadhaarNumber);
    	requestParams.add(AadhaarApiConstants.SUCCESSURL, redirectUrl);
    	requestParams.add(AadhaarApiConstants.FAILUREURL, redirectUrl);
    	requestParams.add(AadhaarApiConstants.REQUESTID, requestId);
    	requestParams.add(AadhaarApiConstants.PURPOSE, purposeType);
    	requestParams.add(AadhaarApiConstants.MODALITY, AadhaarApiConstants.BIOMETRIC);
    	requestParams.add(AadhaarApiConstants.AUTH_CAPTURED_DATA, this.jsonSerializer.serialize(authCaptureData));
    	requestParams.add(AadhaarApiConstants.HASH, hash);
        return requestParams;
    }
    
}
