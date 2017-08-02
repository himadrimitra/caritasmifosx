package com.finflux.infrastructure.external.authentication.aadhar.data;

public class AadhaarServerDetails {

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public String getSaltKey() {
        return this.saltKey;
    }

    public String getSaCode() {
        return this.saCode;
    }

    public String getInitUrl() {
        return this.initUrl;
    }

    public String getKycUrl() {
        return this.kycUrl;
    }

    private final String host;
    private final String port;
    private final String certificateType;
    private final String saltKey;
    private final String saCode;
    private final String initUrl;
    private final String kycUrl;

    private AadhaarServerDetails(final String host, final String port, final String certificateType, final String saltKey,
            final String saCode, final String initUrl, final String kycUrl) {
        this.host = host;
        this.port = port;
        this.certificateType = certificateType;
        this.saltKey = saltKey;
        this.saCode = saCode;
        this.initUrl = initUrl;
        this.kycUrl = kycUrl;
    }

    public static AadhaarServerDetails instance(final String host, final String port, final String certificateType, final String saltKey,
            final String saCode, final String initUrl, final String kycUrl) {
        return new AadhaarServerDetails(host, port, certificateType, saltKey, saCode, initUrl, kycUrl);
    }
}
