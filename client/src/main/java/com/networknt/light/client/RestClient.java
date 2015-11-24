/*
 * Copyright 2015 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.networknt.light.client;

import com.networknt.light.util.ServiceLocator;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A Singleton Class that wraps Spring RestTemplate, AsyncRestTemplate
 *
 * Created by Steve on 11/3/2015.
 */
public class RestClient {
    static final Logger logger = LoggerFactory.getLogger(RestClient.class);

    static final Integer DEFAULT_REACTOR_CONNECT_TIMEOUT = 30000;
    static final Integer DEFAULT_REACTOR_SO_TIMEOUT = 30000;

    static final String CONFIG_NAME = "client";
    static final String REST_TEMPLATE = "restTemplate";
    static final String ASYNC_REST_TEMPLATE = "asyncRestTemplate";
    static final String ROUTES = "routes";
    static final String MAX_CONNECTION_TOTAL = "maxConnectionTotal";
    static final String MAX_CONNECTION_PER_ROUTE = "maxConnectionPerRoute";
    static final String TIMEOUT_MILLISECONDS= "timeoutMilliseconds";
    static final String TLS = "tls";
    static final String VERIFY_HOSTNAME = "verifyHostname";
    static final String TRUST_STORE = "trustStore";
    static final String TRUST_PASS = "trustPass";
    static final String KEY_STORE = "keyStore";
    static final String KEY_PASS = "keyPass";

    static final String REACTOR = "reactor";
    static final String REACTOR_IO_THREAD_COUNT = "ioThreadCount";
    static final String REACTOR_CONNECT_TIMEOUT = "connectTimeout";
    static final String REACTOR_SO_TIMEOUT = "soTimeout";

    private AsyncRestTemplate asyncRestTemplate = null;
    private RestTemplate restTemplate = null;

    private Map<String, Object> configMap = ServiceLocator.getInstance().getConfig(CONFIG_NAME);

    // This eager initialization.
    private static final RestClient instance = new RestClient();

    // private constructor to prevent create another instance.
    private RestClient() {}

    // This is the only way to get instance
    public static RestClient getInstance() {
        return instance;
    }

    public RestTemplate restTemplate() throws Exception {
        if(restTemplate == null) {
            synchronized (RestClient.class) {
                if(restTemplate == null) {
                    restTemplate = new RestTemplate(httpRequestFactory());
                    List<HttpMessageConverter<?>> converters = restTemplate
                            .getMessageConverters();
                    // inject jackson ObjectMapper to converters.
                    for (HttpMessageConverter<?> converter : converters) {
                        if (converter instanceof MappingJackson2HttpMessageConverter) {
                            MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
                            jsonConverter.setObjectMapper(ServiceLocator.getInstance().getMapper());
                        }
                    }
                }
            }
        }
        return restTemplate;
    }

    public AsyncRestTemplate asyncRestTemplate() throws Exception {
        if(asyncRestTemplate == null) {
            synchronized (RestClient.class) {
                if(asyncRestTemplate == null) {
                    asyncRestTemplate = new AsyncRestTemplate(
                            asyncHttpRequestFactory(), restTemplate());
                }
            }
        }
        return asyncRestTemplate;
    }

    private ClientHttpRequestFactory httpRequestFactory() throws Exception {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    private SSLContext sslContext() throws Exception {
        SSLContext sslContext = null;
        Map<String, Object> tlsMap = (Map)configMap.get("tls");
        if(tlsMap != null) {
            SSLContextBuilder builder = SSLContexts.custom();
            // load trust store, this is the server public key certificate
            String trustStoreName = (String)tlsMap.get(TRUST_STORE);
            String trustStorePass = (String)tlsMap.get(TRUST_PASS);
            KeyStore trustStore = null;
            if(trustStoreName != null && trustStorePass != null) {
                InputStream trustStream = getClass().getClassLoader().getResourceAsStream(trustStoreName);
                if(trustStream != null) {
                    trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    try {
                        trustStore.load(trustStream, trustStorePass.toCharArray());
                        builder.loadTrustMaterial(trustStore, new TrustSelfSignedStrategy());
                    } finally {
                        trustStream.close();
                    }
                }
            }
            // load key store for client certificate if two way ssl is used.
            String keyStoreName = (String)tlsMap.get(KEY_STORE);
            String keyStorePass = (String)tlsMap.get(KEY_PASS);
            KeyStore keyStore = null;
            if(keyStoreName != null && keyStorePass != null) {
                InputStream keyStream = getClass().getClassLoader().getResourceAsStream(keyStoreName);
                if(keyStream != null) {
                    keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    try {
                        keyStore.load(keyStream, keyStorePass.toCharArray());
                        builder.loadKeyMaterial(keyStore, keyStorePass.toCharArray());
                    } finally {
                        keyStream.close();
                    }
                }
            }
            sslContext = builder.build();
        }
        return sslContext;
    }

    private HostnameVerifier hostnameVerifier() {
        Map<String, Object> tlsMap = (Map)configMap.get("tls");
        HostnameVerifier verifier = null;
        if(tlsMap != null) {
            Boolean verifyHostname = (Boolean) tlsMap.get(VERIFY_HOSTNAME);
            if (verifyHostname != null && verifyHostname == false) {
                verifier = new NoopHostnameVerifier();
            } else {
                verifier = new DefaultHostnameVerifier();
            }
        }
        return verifier;
    }

    private Registry<ConnectionSocketFactory> registry() throws Exception {

        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(
                sslContext(),
                new String[] { "TLSv1" },
                null,
                hostnameVerifier());

        // Create a registry of custom connection factory
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", sslFactory)
                .build();
    }

    private CloseableHttpClient httpClient() throws Exception {

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry());

        Map<String, Object> httpClientMap = (Map<String, Object>)configMap.get(REST_TEMPLATE);
        connectionManager.setMaxTotal((Integer)httpClientMap.get(MAX_CONNECTION_TOTAL));
        connectionManager.setDefaultMaxPerRoute((Integer) httpClientMap.get(MAX_CONNECTION_PER_ROUTE));
        // Now handle all the specific route defined.
        Map<String, Object> routeMap = (Map<String, Object>)httpClientMap.get(ROUTES);
        Iterator<String> it = routeMap.keySet().iterator();
        while (it.hasNext()) {
            String route = it.next();
            Integer maxConnection = (Integer)routeMap.get(route);
            connectionManager.setMaxPerRoute(new HttpRoute(new HttpHost(
                    route)), maxConnection);
        }
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout((Integer)httpClientMap.get(TIMEOUT_MILLISECONDS))
                .build();

       return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config).build();
    }

    private CloseableHttpAsyncClient asyncHttpClient() throws Exception {
        PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(
                ioReactor(), asyncRegistry());
        Map<String, Object> asyncHttpClientMap = (Map<String, Object>)configMap.get(ASYNC_REST_TEMPLATE);
        connectionManager.setMaxTotal((Integer)asyncHttpClientMap.get(MAX_CONNECTION_TOTAL));
        connectionManager.setDefaultMaxPerRoute((Integer) asyncHttpClientMap.get(MAX_CONNECTION_PER_ROUTE));
        // Now handle all the specific route defined.
        Map<String, Object> routeMap = (Map<String, Object>)asyncHttpClientMap.get(ROUTES);
        Iterator<String> it = routeMap.keySet().iterator();
        while (it.hasNext()) {
            String route = it.next();
            Integer maxConnection = (Integer)routeMap.get(route);
            connectionManager.setMaxPerRoute(new HttpRoute(new HttpHost(
                    route)), maxConnection);
        }
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout((Integer) asyncHttpClientMap.get(TIMEOUT_MILLISECONDS))
                .build();

        return HttpAsyncClientBuilder
                .create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config)
                .build();
    }

    private AsyncClientHttpRequestFactory asyncHttpRequestFactory() throws Exception {
        return new HttpComponentsAsyncClientHttpRequestFactory(
                asyncHttpClient());
    }

    private Registry<SchemeIOSessionStrategy> asyncRegistry() throws Exception {
        // Allow TLSv1 protocol only
        SSLIOSessionStrategy sslSessionStrategy = new SSLIOSessionStrategy(
                sslContext(),
                new String[] { "TLSv1" },
                null,
                hostnameVerifier());

        // Create a registry of custom connection session strategies for supported
        // protocol schemes.
        return RegistryBuilder.<SchemeIOSessionStrategy>create()
                .register("http", NoopIOSessionStrategy.INSTANCE)
                .register("https", sslSessionStrategy)
                .build();
    }

    private ConnectingIOReactor ioReactor() throws Exception {
        // Create I/O reactor configuration
        Map<String, Object> asyncRestTemplateMap = (Map)configMap.get(ASYNC_REST_TEMPLATE);
        Map<String, Object> reactorMap = (Map)asyncRestTemplateMap.get(REACTOR);
        Integer ioThreadCount = (Integer)reactorMap.get(REACTOR_IO_THREAD_COUNT);
        IOReactorConfig.Builder builder = IOReactorConfig.custom();
        builder.setIoThreadCount(ioThreadCount == null? Runtime.getRuntime().availableProcessors(): ioThreadCount);
        Integer connectTimeout = (Integer)reactorMap.get(REACTOR_CONNECT_TIMEOUT);
        builder.setConnectTimeout(connectTimeout == null? DEFAULT_REACTOR_CONNECT_TIMEOUT: connectTimeout);
        Integer soTimeout = (Integer)reactorMap.get(REACTOR_SO_TIMEOUT);
        builder.setSoTimeout(soTimeout == null? DEFAULT_REACTOR_SO_TIMEOUT: soTimeout);
        IOReactorConfig ioReactorConfig = builder.build();
        return new DefaultConnectingIOReactor(ioReactorConfig);
    }


}
