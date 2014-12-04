package com.networknt.light.server.handler;

import com.networknt.light.server.LightServer;
import com.networknt.light.server.ServerConstants;
import com.networknt.light.util.ServiceLocator;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;

/**
 * Created by husteve on 8/7/14.
 */
public class WebSocketHandler implements WebSocketConnectionCallback {
    private static final Logger logger = Logger.getLogger(WebSocketHandler.class);

    public WebSocketHandler() {
    }

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        logger.info("Connecting to websocket server.");
        // Validate origin header (security!)
        String originHeader = exchange.getRequestHeader("Origin");
        boolean allowedOriginHeader = (originHeader == null ||
                LightServer.WEBSOCKET_ALLOWED_ORIGIN_HEADER.matcher(originHeader).matches());

        if (!allowedOriginHeader) {
            logger.info(channel.toString() + " disconnected due to invalid origin header: " + originHeader);
            exchange.close();
        }
        else {
            logger.info("Valid origin header, setting up connection.");

            channel.getReceiveSetter().set(new AbstractReceiveListener() {
                @Override
                protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                    final String messageData = message.getData();
                    for(WebSocketChannel session : channel.getPeerConnections()) {
                        WebSockets.sendText(messageData, session, null);
                    }
                }

                @Override
                protected void onError(WebSocketChannel webSocketChannel, Throwable error) {
                    logger.info("Server error : " + error.toString());
                }

                @Override
                protected void onClose(WebSocketChannel clientChannel, StreamSourceFrameChannel streamSourceChannel) throws IOException {
                    logger.info(clientChannel.toString() + " disconnected");
                }
            });
            channel.resumeReceives();
        }
    }
}
