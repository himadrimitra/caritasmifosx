package com.finflux.infrastructure.common.httpaccess;

import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.finflux.infrastructure.external.authentication.aadhar.exception.ConnectionFailedException;

@Service
public class ExternalHttpConnectivity {

	public <T> T performServerPost(final String postURL, final T body, Class<T> rerurnType) {
		T response = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			response = restTemplate.postForObject(postURL, body, rerurnType);
		} catch (Exception ce) {
			throw new ConnectionFailedException(
					"Unable to communicate with Aadhaar server. Please Conntact your Support team.");
		}
		return response;
	}

	/**
	 * prevents the SSL security certificate check
	 **/
	private void trustAllSSLCertificates() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@SuppressWarnings("unused")
			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			@SuppressWarnings("unused")
			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };

		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier hostnameVerifier = new HostnameVerifier() {

				@SuppressWarnings("unused")
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		}

		catch (Exception e) {
			// do nothing
		}
	}

	public URI performForLocation(final String initUrl, final MultiValueMap<String, String> mapParams) {
		trustAllSSLCertificates();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> requestBody = new HttpEntity<MultiValueMap<String, String>>(mapParams,
				headers);
		URI response = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			response = restTemplate.postForLocation(initUrl, requestBody);
		} catch (Exception ce) {
			throw new ConnectionFailedException(
					"Unable to communicate with Aadhaar server. Please Conntact your Support team.");
		}
		return response;
	}

}
