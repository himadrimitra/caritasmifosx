
package com.finflux.infrastructure.external.authentication.aadhar.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import com.aadhaarconnect.bridge.capture.model.common.Location;
import com.aadhaarconnect.bridge.capture.model.common.LocationType;
import com.aadhaarconnect.bridge.capture.model.common.request.CertificateType;
import com.aadhaarconnect.bridge.gateway.model.AuthResponse;
import com.aadhaarconnect.bridge.gateway.model.OtpResponse;
import com.finflux.infrastructure.common.httpaccess.ExternalHttpConnectivity;
import com.finflux.infrastructure.external.authentication.aadhar.api.AadhaarApiConstants;
import com.finflux.infrastructure.external.authentication.aadhar.data.AadhaarServerDetails;
import com.finflux.infrastructure.external.authentication.aadhar.data.AadhaarServiceDataAssembler;
import com.finflux.infrastructure.external.authentication.aadhar.domain.AadhaarDataValidator;
import com.finflux.infrastructure.external.authentication.aadhar.exception.AadhaarServiceResponseParserException;
import com.finflux.infrastructure.external.authentication.aadhar.exception.ConnectionFailedException;
import com.finflux.infrastructure.external.authentication.aadhar.exception.UnableToGetKycDetails;
import com.finflux.infrastructure.external.authentication.aadhar.exception.UnableToSendOtpException;
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
	private final AadhaarServiceDataAssembler aadharServiceDataAssembler;

	private String endpoint;

	private final JdbcTemplate jdbcTemplate;
	private final ExternalHttpConnectivity httpConnectivity;
	private final FromJsonHelper fromJsonHelper;
	private final AadhaarDataValidator aadhaarDataValidator;

	private AadhaarServerDetails aadhaarServerDetails;
	private CertificateType certificateType;

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
			final FromJsonHelper fromJsonHelper, final AadhaarDataValidator aadhaarDataValidator) {
		this.aadharServiceDataAssembler = aadhaarServiceDataAssembler;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.httpConnectivity = httpConnectivity;
		this.fromJsonHelper = fromJsonHelper;
		this.aadhaarDataValidator = aadhaarDataValidator;
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
		String otpCaptureRequestJson = this.aadharServiceDataAssembler.otpRequestDataAssembler(aadhaarNumber,
				this.certificateType);
		String url = urlBuilder(endpoint, generateOtpPath);
		try {
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
		} catch (Exception ce) {
			throw new ConnectionFailedException(
					"Unable to communicate with Aadhaar server. Please Conntact your Support team.");
		}
	}

	@Override
	public AuthResponse authenticateUserByOtp(String aadhaarNumber, String otp, final Location location) {
		initalize();
		String authenticateUserByOtpJson = this.aadharServiceDataAssembler
				.authenticateUserUsingOtpDataAssembler(aadhaarNumber, otp, this.certificateType, location);
		String url = urlBuilder(endpoint, authRaw);
		try {
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
		} catch (Exception ce) {
			throw new ConnectionFailedException(
					"Unable to communicate with Aadhaar server. Please Conntact your Support team.");
		}
	}

	@Override
	public AuthResponse authenticateUserByFingerPrintUsingAadhaarService(final String aadhaarNumber,
			final String fingerPrintData, final Location location) {
		initalize();
		String authenticateUserByFingerprintJson = this.aadharServiceDataAssembler
				.authenticateUserUsingFingerprintDataAssembler(aadhaarNumber, fingerPrintData, this.certificateType,
						location);
		String url = urlBuilder(endpoint, auth);

		try {
			String response = this.httpConnectivity.performServerPost(url, authenticateUserByFingerprintJson,
					String.class);

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
		} catch (Exception ce) {
			throw new ConnectionFailedException(
					"Unable to communicate with Aadhaar server. Please Conntact your Support team.");
		}
	}

	public String obtainEKycByOtpUsingAadhaarService(final String aadhaarNumber, final String otp,
			final Location location) {
		initalize();
		String kycByFingerprintJson = this.aadharServiceDataAssembler.getKycCaptureData(aadhaarNumber, otp,
				certificateType, location, AadhaarApiConstants.OTP);
		String url = urlBuilder(endpoint, kyc);

		try {
			String response = this.httpConnectivity.performServerPost(url, kycByFingerprintJson, String.class);

			if (response != null) {
				JsonElement element = this.fromJsonHelper.parse(response);
				boolean isSuccess = this.fromJsonHelper.extractBooleanNamed(AadhaarApiConstants.SUCCESS, element);
				if (isSuccess) {
					return response;
				} else {
					throw new UnableToGetKycDetails(aadhaarNumber, "Fingerprint");
				}

			} else {
				throw new ConnectionFailedException("Unable to communicate with Aadhaar server");
			}
		} catch (Exception ce) {
			throw new ConnectionFailedException(
					"Unable to communicate with Aadhaar server. Please Conntact your Support team.");
		}
	}

	public String obtainEKycByFingerprintUsingAadhaarService(final String aadhaarNumber, final String fingerprintData,
			final Location location) {
		initalize();
		String kycByFingerprintJson = this.aadharServiceDataAssembler.getKycCaptureData(aadhaarNumber, fingerprintData,
				certificateType, location, AadhaarApiConstants.FINGERPRINT);
		String url = urlBuilder(endpoint, kyc);

		try {
			String response = this.httpConnectivity.performServerPost(url, kycByFingerprintJson, String.class);

			if (response != null) {
				JsonElement element = this.fromJsonHelper.parse(response);
				boolean isSuccess = this.fromJsonHelper.extractBooleanNamed(AadhaarApiConstants.SUCCESS, element);
				if (isSuccess) {
					return response;
				} else {
					throw new UnableToGetKycDetails(aadhaarNumber, "Fingerprint");
				}

			} else {
				throw new ConnectionFailedException("Unable to communicate with Aadhaar server");
			}
		} catch (Exception ce) {
			throw new ConnectionFailedException(
					"Unable to communicate with Aadhaar server. Please Conntact your Support team.");
		}
	}

	private AadhaarServerDetails getAadhaarServerDetails() {
		final ResultSetExtractor<AadhaarServerDetails> resultSetExtractor = new AadhaarServerDetailsMapper();
		final String sql = "SELECT esp.name, esp.value FROM c_external_service_properties esp inner join c_external_service es on esp.external_service_id = es.id where es.name = '"
				+ AadhaarServiceConstants.AADHAAR_SERVICE_NAME + "'";
		final AadhaarServerDetails aadhaarServerDetails = this.jdbcTemplate.query(sql, resultSetExtractor,
				new Object[] {});
		return aadhaarServerDetails;
	}

	private static final class AadhaarServerDetailsMapper implements ResultSetExtractor<AadhaarServerDetails> {

		@Override
		public AadhaarServerDetails extractData(ResultSet rs) throws SQLException, DataAccessException {
			String host = null;
			String port = null;
			String certificateType = null;
			while (rs.next()) {
				if (rs.getString("name").equalsIgnoreCase(AadhaarServiceConstants.AADHAAR_HOST)) {
					host = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(AadhaarServiceConstants.AADHAAR_PORT)) {
					port = rs.getString("value");
				} else if (rs.getString("name").equalsIgnoreCase(AadhaarServiceConstants.AADHAAR_SERVER_CERTIFICATE)) {
					certificateType = rs.getString("value");
				}
			}
			return AadhaarServerDetails.instance(host, port, certificateType);
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
		Location location = null;
		if (locationElement != null) {
			String locationType = this.fromJsonHelper.extractStringNamed("locationType", locationElement);
			location = new Location();
			if (locationType.equals("gps")) {
				location.setType(LocationType.gps);
				location.setLongitude(this.fromJsonHelper.extractStringNamed("longitude", locationElement));
				location.setLatitude(this.fromJsonHelper.extractStringNamed("latitude", locationElement));
			} else if (locationType.equals("pincode")) {
				location.setType(LocationType.pincode);
				location.setPincode(this.fromJsonHelper.extractStringNamed("pincode", locationElement));
			}
		}
		if (authType.equals(AadhaarApiConstants.OTP)) {
			return obtainEKycByOtpUsingAadhaarService(aadhaarNumber, authData, location);
		} else {

			return obtainEKycByFingerprintUsingAadhaarService(aadhaarNumber, authData, location);
		}
	}

}
