package com.ecosense;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class EcosenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcosenseApplication.class, args);
    }

    @Primary
    @Bean(name = "proxyRestTemplate")
    public RestTemplate proxyRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        requestFactory.setReadTimeout(10000000); // TODO timeout value as param. + default value (global preference)?

        Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("proxy.uns.ac.rs", 8080));
        requestFactory.setProxy(proxy);
        return new RestTemplate(requestFactory);
    }

    // TODO delete when dar not in use
    @Bean(name = "sslTrustRestTemplate")
    public RestTemplate sslTrustRestTemplate() throws Exception {

        // Create SSLContext that trusts all certificates
        SSLContext sslContext = SSLContextBuilder
            .create()
            .loadTrustMaterial((chain, authType) -> true)  // Trust all certificates
            .build();

        // Define the proxy 
        HttpHost proxy = new HttpHost("proxy.uns.ac.rs", 8080, "http");

        // Create an HttpClient that uses the SSLContext
        CloseableHttpClient httpClient = HttpClients.custom()
            .setSSLContext(sslContext)
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)  // Disable hostname verification
            .setProxy(proxy)
            .build();

        // Create HttpComponentsClientHttpRequestFactory and set the custom HttpClient
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        // Set timeout values (optional)
        factory.setReadTimeout(10000000); // Set a long read timeout, per your comment
        factory.setConnectTimeout(10000000); // Optional connect timeout

        // Build the RestTemplate using the factory with SSL and proxy configuration
        return new RestTemplate(factory);
    }

    @Bean(name = "nonSSLRestTemplate")
    // public RestTemplate getRestTemplate() throws KeyStoreException,
    // NoSuchAlgorithmException, KeyManagementException {
    // TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
    // SSLContext sslContext =
    // org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null,
    // acceptingTrustStrategy).build();
    // SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext,
    // new NoopHostnameVerifier());
    // CloseableHttpClient httpClient =
    // HttpClients.custom().setSSLSocketFactory(csf).build();
    // HttpComponentsClientHttpRequestFactory requestFactory = new
    // HttpComponentsClientHttpRequestFactory();
    // requestFactory.setHttpClient(httpClient);
    // RestTemplate restTemplate = new RestTemplate(requestFactory);
    // return restTemplate;
    // }
    public RestTemplate nonProxyRestTemplate(RestTemplateBuilder builder)
            throws NoSuchAlgorithmException, KeyManagementException {
        HttpComponentsClientHttpRequestFactory customRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create()
                        .setProxy(new HttpHost("proxy.uns.ac.rs", 8080))
                        .build());

        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        customRequestFactory.setHttpClient(httpClient);
        customRequestFactory.setReadTimeout(0);
        return builder.requestFactory(() -> customRequestFactory).build();
    }

}
