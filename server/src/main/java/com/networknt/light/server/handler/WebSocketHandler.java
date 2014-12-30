package com.networknt.light.server.handler;

import com.networknt.light.server.LightServer;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by husteve on 8/7/14.
 */
public class WebSocketHandler implements WebSocketConnectionCallback {
    static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    public WebSocketHandler() {
    }

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        logger.info("Connecting to websocket server.");
        // Validate if access token is available. Do I need to validate if the request comes from
        // one of configured hosts?


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
                    logger.error("Server error:", error);
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
