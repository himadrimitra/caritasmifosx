package com.finflux.infrastructure.external.authentication.aadhar.domain;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aadhaarconnect.bridge.capture.model.auth.AuthCaptureData;
import com.aadhaarconnect.bridge.capture.model.common.ConsentType;
import com.aadhaarconnect.bridge.capture.model.common.response.Modalities;
import com.aadhaarconnect.bridge.capture.model.common.response.Pid;
import com.aadhaarconnect.bridge.capture.model.common.response.PidType;
import com.aadhaarconnect.bridge.capture.model.common.response.SessionKey;
import com.finflux.infrastructure.external.authentication.aadhar.api.AadhaarApiConstants;
import com.finflux.infrastructure.external.authentication.aadhar.exception.AuthRequestDataBuldFailedException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class AadhaarDataValidator {

	private final FromJsonHelper fromJsonHelper;

	@Autowired
	public AadhaarDataValidator(final FromJsonHelper fromJsonHelper) {
		this.fromJsonHelper = fromJsonHelper;
	}

	public void validateJsonForGenerateOtp(final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();

		this.fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, AadhaarApiConstants.OTP_REQUEST_DATA);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder dataValidatorBuilder = new DataValidatorBuilder(dataValidationErrors)
				.resource(AadhaarApiConstants.AADHAAR_SERIVICE_RESOURCE_NAME);

		JsonElement element = this.fromJsonHelper.parse(json);

		final String aadhaarNumber = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.AADHAAR_NUMBER,
				element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.AADHAAR_NUMBER).value(aadhaarNumber).notNull()
				.notExceedingLengthOf(12);

		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

	public void validateJsonForGetEKyc(final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();

		this.fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, AadhaarApiConstants.OTP_REQUEST_EKYC_DATA);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder dataValidatorBuilder = new DataValidatorBuilder(dataValidationErrors)
				.resource(AadhaarApiConstants.AADHAAR_SERIVICE_RESOURCE_NAME);

		JsonElement element = this.fromJsonHelper.parse(json);

		final String requestId = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.REQUESTID, element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.REQUESTID).value(requestId).notNull()
				.notExceedingLengthOf(20);

		final String uuId = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.AADHAARUUID, element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.AADHAARUUID).value(uuId).notNull();

		final String hash = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.HASH, element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.HASH).value(hash).notNull();

		final String status = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.REQUESTSTATUS, element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.REQUESTSTATUS).value(status).notNull();

		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) {
			//
			throw new PlatformApiDataValidationException(dataValidationErrors);
		}
	}

	public void validateReceivedHash(final String receivedHash, final String saltKey, final String requestId,
			final String aadhaarNumber, final String saCode) {
		String calculatedHash = generateHashKey(saCode, aadhaarNumber, requestId, saltKey);
		if (receivedHash != null && calculatedHash != null) {
			if (!receivedHash.equals(calculatedHash)) {
				throw new PlatformDataIntegrityException("error.msg.invalidHash.hash.key.mismatch.issue",
						"Invalid hash key.");
			}
		}
	}

	public String generateHashKey(final String saCode, final String aadhaarNumber, final String requestId,
			final String saltKey) {
		/**
		 * construct the hash key
		 **/
		StringBuilder builder = new StringBuilder();
		builder.append(saltKey);
		builder.append("|");
		builder.append(requestId);
		builder.append("|");
		builder.append(aadhaarNumber);
		builder.append("|");
		builder.append(saCode);
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new AuthRequestDataBuldFailedException("OTP");
		}
		return new String(Hex.encode(digest.digest(builder.toString().getBytes())));
	}

	public AuthCaptureData validateAndCreateAuthCaptureData(final String json) {
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
		pid.setType(PidType.valueOf(pidType));
		final String authData = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.PID_VALUE, pidData);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.PID_VALUE).value(authData).notNull().notBlank();
		pid.setValue(authData);
		authCaptureData.setPid(pid);
		final String hmac = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.HMAC, element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.HMAC).value(hmac).notNull().notBlank();
		authCaptureData.setHmac(hmac);
		final JsonElement sessionData = parentObject.get(AadhaarApiConstants.SESSION_KEY);
		SessionKey sessionKey = new SessionKey();
		final String certificateId = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.CERTIFICATE_ID,
				sessionData);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.CERTIFICATE_ID).value(certificateId).notNull()
				.notBlank();
		final String keyValue = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.CERTIFICATE_VALUE,
				sessionData);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.CERTIFICATE_VALUE).value(sessionKey).notNull()
				.notBlank();
		sessionKey.setCertificateId(certificateId);
		sessionKey.setValue(keyValue);
		authCaptureData.setSessionKey(sessionKey);
		final String uniqueDeviceCode = this.fromJsonHelper.extractStringNamed(AadhaarApiConstants.UNIQUE_DEVICE_CODE,
				element);
		dataValidatorBuilder.reset().parameter(AadhaarApiConstants.UNIQUE_DEVICE_CODE).value(uniqueDeviceCode).notNull()
				.notBlank();
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

}
