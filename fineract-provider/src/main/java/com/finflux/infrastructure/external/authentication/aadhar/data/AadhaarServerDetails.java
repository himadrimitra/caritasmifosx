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

	private final String host;
	private final String port;
	private final String certificateType;

	private AadhaarServerDetails(final String host, final String port, final String certificateType) {
		this.host = host;
		this.port = port;
		this.certificateType = certificateType;
	}

	public static AadhaarServerDetails instance(final String host, final String port, final String certificateType) {
		return new AadhaarServerDetails(host, port, certificateType);
	}
}
