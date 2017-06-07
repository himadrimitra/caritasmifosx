package com.finflux.infrastructure.common.httpaccess;

import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.finflux.infrastructure.external.authentication.aadhar.exception.ConnectionFailedException;

@Service
public class ExternalHttpConnectivity {

	public <T> T performServerPost(final String postURL, final T body, Class<T> rerurnType){
		T response = null;
		try{
			RestTemplate restTemplate = new RestTemplate();
			response = restTemplate.postForObject(postURL, body, rerurnType);
		}catch(Exception ce){
			throw new ConnectionFailedException(
					"Unable to communicate with Aadhaar server. Please Conntact your Support team.");
		}
		return response;
	}

    public String initiateGenerateOtp(final String otpUrl) {

        trustAllSSLCertificates();
        /**
         * HttpsUrlConnection used for connecting to the external url
         **/
        HttpsURLConnection connection = null;

        try {
            URL url = new URL(otpUrl);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            InputStream is = connection.getInputStream();
            URL header = connection.getURL();
            return header.toString();
        } catch (Exception ce) {
            throw new ConnectionFailedException("Unable to communicate with Aadhaar server. Please Conntact your Support team.");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
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
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

            @SuppressWarnings("unused")
            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
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

}
