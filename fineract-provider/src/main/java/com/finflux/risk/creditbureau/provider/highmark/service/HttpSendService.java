package com.finflux.risk.creditbureau.provider.highmark.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;

import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class HttpSendService {

    public HttpClient getHttpClient() {
        return httpClient;
    }

    private HttpClient httpClient;

    @SuppressWarnings("deprecation")
    public HttpSendService() {

        //System.setProperty("jsse.enableSNIExtension", "false");
        SSLConnectionSocketFactory socketFactory = null;
        try {
            socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .build(), NoopHostnameVerifier.INSTANCE);

            httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e2) {
            e2.printStackTrace();
        }
    }

    public String sendRequest(String url, Map<String, String> headers, String body) {
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            post.addHeader(headerEntry.getKey(), headerEntry.getValue());
        }
        String result = null;
        try {
            HttpEntity entity = new StringEntity(body);
            post.setEntity(entity);
            HttpResponse response = httpClient.execute(post);
            result = EntityUtils.toString(response.getEntity());
            // System.out.println(result);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {
            // TODO Auto-generated method stub

        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {
            // TODO Auto-generated method stub

        }
    }

}
