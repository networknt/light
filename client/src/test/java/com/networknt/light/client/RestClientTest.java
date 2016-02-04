package com.networknt.light.client;

import com.networknt.light.util.ServiceLocator;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.xml.ws.Service;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by steve on 24/11/15.
 */
public class RestClientTest extends TestCase {

    Undertow server = null;
    public void setUp() throws Exception {
        super.setUp();
        server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json;charset=utf-8");
                        exchange.getResponseSender().send(ByteBuffer.wrap(
                                ServiceLocator.getInstance().getMapper().writeValueAsBytes(
                                        Collections.singletonMap("message", "Hello!"))));
                    }
                }).build();
        server.start();
    }

    public void tearDown() throws Exception {
        if(server != null) {
            server.stop();
            Thread.sleep(1000);
            System.out.println("The server is stopped.");
        }
    }

    @Test
    public void testAsyncRestTemplateFuture() throws Exception {
        String url = "http://localhost:8080";
        Future<ResponseEntity<Map>> future = RestClient.getInstance().asyncRestTemplate().getForEntity(
                url, Map.class);
        ResponseEntity<Map> entity = future.get();

        // Asserts
        assertTrue(entity.getStatusCode().equals(HttpStatus.OK));
        assertEquals("Hello!", entity.getBody().get("message"));
        System.out.println("message = " + entity.getBody().get("message"));
    }

    @Test
    public void testAsyncRestTemplateCallback() throws Exception {
        String url = "http://localhost:8080";
        ListenableFuture<ResponseEntity<Map>> future = RestClient.getInstance().asyncRestTemplate()
                .exchange(url, HttpMethod.GET, null,
                        Map.class);

        future.addCallback(new ListenableFutureCallback<ResponseEntity>() {
            @Override
            public void onSuccess(ResponseEntity result) {
                System.out.println("Response received (async callable)");
                assertEquals("Hello!", ((Map)result.getBody()).get("message"));
                System.out.println("message = " + ((Map)result.getBody()).get("message"));
            }

            @Override
            public void onFailure(Throwable t) {
                // Need assertions
                System.out.println("Failure to receive response");
            }
        });
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    public void testRestTemplate() throws Exception {

        String url = "http://localhost:8080";
        ResponseEntity<Map> entity = RestClient.getInstance().restTemplate().getForEntity(url, Map.class);

        // Asserts
        assertTrue(entity.getStatusCode().equals(HttpStatus.OK));
        assertEquals("Hello!", entity.getBody().get("message"));
        System.out.println("message = " + entity.getBody().get("message"));
    }

    public void testSyncOneWaySsl() throws Exception {

        String url = "https://www.google.ca";
        ResponseEntity<String> entity = RestClient.getInstance().restTemplate().getForEntity(url, String.class);

        // Asserts
        assertTrue(entity.getStatusCode().equals(HttpStatus.OK));
        System.out.println("message = " + entity.getBody());
    }

}
