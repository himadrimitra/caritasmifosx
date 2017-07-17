package com.finflux.infrastructure.external.authentication.aadhar.data;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;

import com.aadhaarconnect.bridge.capture.model.auth.AuthCaptureData;
import com.aadhaarconnect.bridge.capture.model.auth.AuthCaptureRequest;
import com.aadhaarconnect.bridge.capture.model.common.ConsentType;
import com.aadhaarconnect.bridge.capture.model.common.Location;
import com.aadhaarconnect.bridge.capture.model.common.LocationType;
import com.aadhaarconnect.bridge.capture.model.common.request.CertificateType;
import com.aadhaarconnect.bridge.capture.model.common.request.Modality;
import com.aadhaarconnect.bridge.capture.model.common.request.ModalityType;
import com.aadhaarconnect.bridge.capture.model.kyc.KycCaptureData;
import com.aadhaarconnect.bridge.capture.model.otp.OtpCaptureRequest;
import com.aadhaarconnect.bridge.capture.model.otp.OtpChannel;
import com.aadhaarconnect.bridge.util.AadhaarBridgeUtil;
import com.finflux.infrastructure.external.authentication.aadhar.api.AadhaarApiConstants;
import com.finflux.infrastructure.external.authentication.aadhar.exception.AuthRequestDataBuldFailedException;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.khoslalabs.aadhaar.util.ISO8601;

import in.gov.uidai.authserver.protobuf.Auth.Bio;
import in.gov.uidai.authserver.protobuf.Auth.BioType;
import in.gov.uidai.authserver.protobuf.Auth.Bios;
import in.gov.uidai.authserver.protobuf.Auth.Position;

@Service
public class AadhaarServiceDataAssembler {

    public String otpRequestDataAssembler(final String aadhaarNumber, final CertificateType CertificateType) {
        OtpCaptureRequest otpCaptureRequest = new OtpCaptureRequest();
        otpCaptureRequest.setAadhaar(aadhaarNumber);
        otpCaptureRequest.setAutoSubmit(false);
        otpCaptureRequest.setCertificateType(CertificateType);
        otpCaptureRequest.setChannel(OtpChannel.SMS);
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
            final Location location) {
        AuthCaptureRequest authCaptureRequest = assembleAuthRequestUsingOtp(aadhaarNumber, otp, certificateType, location);
        String json = new Gson().toJson(authCaptureRequest);
        return json;
    }

    private AuthCaptureRequest assembleAuthRequestUsingOtp(String aadhaarNumber, final String otp, CertificateType certificateType,
            final Location location) {
        AuthCaptureRequest authCaptureRequest = new AuthCaptureRequest();
        authCaptureRequest.setAadhaar(aadhaarNumber);
        authCaptureRequest.setCertificateType(certificateType);
        authCaptureRequest.setModality(Modality.otp);
        authCaptureRequest.setOtp(otp);
        Location locationData = setLocation(location.getType(), location.getPincode(), location.getLatitude(), location.getLongitude());
        authCaptureRequest.setLocation(locationData);
        return authCaptureRequest;
    }

    public String authenticateUserUsingFingerprintDataAssembler(String aadhaarNumber, String fingerprint, CertificateType certificateType,
            final Location location) {
        final AuthCaptureData authCaptureData = assembleAndGetAuthCaptureDataForFingerPrint(aadhaarNumber, fingerprint, certificateType,
                location);
        String json = new Gson().toJson(authCaptureData);
        return json;
    }

    public String getKycCaptureData(String aadhaarNumber, String authData, CertificateType certificateType, final Location location,
            final String authType) {
        KycCaptureData kycCaptureData = new KycCaptureData();
        kycCaptureData.setConsent(ConsentType.Y);
        AuthCaptureData authCaptureData = null;

        switch (authType) {
            case AadhaarApiConstants.AUTHTYPE_FINGERPRINT:
                authCaptureData = assembleAndGetAuthCaptureDataForFingerPrint(aadhaarNumber, authData, certificateType, location);
            break;
            case AadhaarApiConstants.AUTHTYPE_OTP:
                AuthCaptureRequest authCaptureRequest = assembleAuthRequestUsingOtp(aadhaarNumber, authData, certificateType, location);
                try {
                    authCaptureData = AadhaarBridgeUtil.buildAuthRequest(authCaptureRequest);
                } catch (ParseException | IOException e) {
                    throw new AuthRequestDataBuldFailedException(authType);
                }
             break ;
            case AadhaarApiConstants.AUTHTYPE_IRIS:
                authCaptureData = assembleAndGetAuthCaptureDataForIris(aadhaarNumber, authData, certificateType, location) ;
                break ;
        }
        kycCaptureData.setAuthCaptureData(authCaptureData); // refer
                                                            // authCaptureData
                                                            // for fp
        kycCaptureData.setTs(ISO8601.now());
        String json = new Gson().toJson(kycCaptureData);
        return json;
    }

    public AuthCaptureData assembleAndGetAuthCaptureDataForFingerPrint(String aadhaarNumber, String fingerprint,
            CertificateType certificateType, final Location location) {
        AuthCaptureRequest authCaptureRequest = new AuthCaptureRequest();
        AuthCaptureData authCaptureData = null;
        authCaptureRequest.setAadhaar(aadhaarNumber);
        authCaptureRequest.setNumOffingersToCapture(Integer.parseInt("1"));
        authCaptureRequest.setNumOffingersToCapture(1);
        authCaptureRequest.setModality(Modality.biometric);
        authCaptureRequest.setModalityType(ModalityType.fp);
        Location locationData = setLocation(location.getType(), location.getPincode(), location.getLatitude(), location.getLongitude());
        authCaptureRequest.setLocation(locationData);
        authCaptureRequest.setCertificateType(certificateType);
        Bios fp = null;
        byte[] fpdata = Base64.decodeBase64(fingerprint);
        fp = Bios.newBuilder()
                .addBio(Bio.newBuilder().setType(BioType.FMR).setPosh(Position.UNKNOWN).setContent(ByteString.copyFrom(fpdata))).build();
        try {
            authCaptureData = AadhaarBridgeUtil.buildAuthRequest(authCaptureRequest, fp);
            return authCaptureData;
        } catch (ParseException | IOException e) {
            throw new AuthRequestDataBuldFailedException("Fingerprint");
        }
    }

    public AuthCaptureData assembleAndGetAuthCaptureDataForIris(String aadhaarNumber, String irisData,
            CertificateType certificateType, final Location location) {
        AuthCaptureRequest authCaptureRequest = new AuthCaptureRequest();
        AuthCaptureData authCaptureData = null;
        authCaptureRequest.setAadhaar(aadhaarNumber);
        authCaptureRequest.setNumOfIrisToCapture(Integer.parseInt("1"));
        authCaptureRequest.setModality(Modality.biometric);
        authCaptureRequest.setModalityType(ModalityType.iris);
        Location locationData = setLocation(location.getType(), location.getPincode(), location.getLatitude(), location.getLongitude());
        authCaptureRequest.setLocation(locationData);
        authCaptureRequest.setCertificateType(certificateType);
        Bios iris = null;
        byte[] biodata  = Base64.decodeBase64(irisData);
        iris = Bios.newBuilder()
                .addBio(Bio.newBuilder().setType(BioType.IIR).setPosh(Position.UNKNOWN).setContent(ByteString.copyFrom(biodata))).build();
        try {
            authCaptureData = AadhaarBridgeUtil.buildAuthRequest(authCaptureRequest, iris);
            return authCaptureData;
        } catch (ParseException | IOException e) {
            throw new AuthRequestDataBuldFailedException("Iris");
        }
    }

    
    public String otpRequestDataAssembler(final String initUrl, final String redirectUrl, final String aadhaarNumber,
            final String requestId, final String purposeType, final String saCode, final String hash) {
        /**
         * constructing the url query parameters
         **/
        try {
            URIBuilder urlParam = new URIBuilder(initUrl);
            urlParam.addParameter(AadhaarApiConstants.SACODE, saCode);
            urlParam.addParameter(AadhaarApiConstants.AADHAARID, aadhaarNumber);
            urlParam.addParameter(AadhaarApiConstants.SUCCESSURL, redirectUrl);
            urlParam.addParameter(AadhaarApiConstants.FAILUREURL, redirectUrl);
            urlParam.addParameter(AadhaarApiConstants.REQUESTID, requestId);
            urlParam.addParameter(AadhaarApiConstants.PURPOSE, purposeType);
            urlParam.addParameter(AadhaarApiConstants.MODALITY, AadhaarApiConstants.AUTHTYPE_OTP);
            urlParam.addParameter(AadhaarApiConstants.HASH, hash);
            return urlParam.toString();
        } catch (URISyntaxException e) {
            throw new AuthRequestDataBuldFailedException("OTP");
        }
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
}
