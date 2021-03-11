package org.github.jrds.server;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.github.jrds.server.messages.FailureMessage;
import org.github.jrds.server.messages.Response;
import org.github.jrds.server.messages.SuccessMessage;

public class ClientWebSocket extends Endpoint {
    private CountDownLatch closureLatch = new CountDownLatch(1);
    private BlockingQueue<Message> messagesReceived = new LinkedBlockingQueue<>();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        MessageHandler.Whole<String> handler = new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                handleTextMessage(session, message);
            }
        };
        session.addMessageHandler(handler);
        System.out.println("Client Socket Connected: " + session);
    }

    private void handleTextMessage(Session sess, String message) {
        try {
            Message msg = mapper.readValue(message, Message.class);
            if (msg instanceof SuccessMessage){
                Main.messageHistory.get(msg.getId()).setConfirmationResponse(Response.SUCCESSFUL);
            }
            else if (msg instanceof FailureMessage) {
                Main.messageHistory.get(msg.getId()).setConfirmationResponse(Response.FAILED);
            }
            else {
                messagesReceived.add(msg);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Client Received TEXT message: " + message);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Client Socket Closed: " + closeReason);
        closureLatch.countDown();
    }

    @Override
    public void onError(Session session, Throwable cause) {
        cause.printStackTrace(System.err);
    }

    public void awaitClosure() throws InterruptedException {
        System.out.println("Client Awaiting closure from remote");
        closureLatch.await(20, TimeUnit.SECONDS);
    }

    Message nextMessageFromQueue() {
        try {
            return messagesReceived.poll(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }
}
