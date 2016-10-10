package com.finflux.risk.creditbureau.provider.highmark.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class HttpSendService {

    public HttpClient getHttpClient() {
        return httpClient;
    }

    private HttpClient httpClient;

    public HttpSendService() {
        System.setProperty("jsse.enableSNIExtension", "false");
        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore ks = KeyStore.getInstance("JKS");
            final File f = new File("/Users/dhirendra/cacerts/cert");
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                ks.load(fis, "changeit".toCharArray());
                // or ks.load(fis, "thepassword".toCharArray());
                fis.close();
            }
            tmf.init(ks);
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (KeyStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CertificateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            sslContext.init(null, new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
        } catch (KeyManagementException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        httpClient = HttpClientBuilder.create().setSSLContext(sslContext).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

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
