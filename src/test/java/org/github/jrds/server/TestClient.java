package org.github.jrds.server;

import java.io.IOException;
import java.net.URI;
import java.util.*;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.github.jrds.server.messages.ChatMessage;
import org.github.jrds.server.messages.LessonStartMessage;
import org.github.jrds.server.messages.SessionEndMessage;

public class TestClient {

    private static final String BASE_URL = "ws://localhost:8080/lesson/";

    private String id; // TODO - REVIEW - added name, so that userStrore & authStore (which treat id as u1900 etc, are the same as what id constitutes here)
    private String name; // TODO - will be useful when sending messages, as humans need names not ID, but ID is the unique identifier
    private ClientWebSocket clientWebSocket;
    private Session session;
    private ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);


    public TestClient(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void connect(String lessonId) {
        try {
            final URI uri = URI.create(BASE_URL + lessonId);

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            clientWebSocket = new ClientWebSocket();
            ClientEndpointConfig.Configurator configurator = new ClientEndpointConfig.Configurator() {
                @Override
                public void beforeRequest(Map<String, List<String>> headers) {
                    headers.put("Authorization", Collections
                            .singletonList("Basic " + Base64.getEncoder().encodeToString((id + ":pw").getBytes())));
                }
            };
            ClientEndpointConfig clientConfig = ClientEndpointConfig.Builder.create()
                .configurator(configurator)
                .build();
            Session newSession = container.connectToServer(clientWebSocket, clientConfig, uri);
            try{
                Thread.sleep(2000);
            } catch (InterruptedException e){

            }
            if (!newSession.isOpen()){
                throw new RuntimeException("Failed to open session");
            }
            else {
                session = newSession;
            }
        } catch (DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        try {
            if(session.isOpen()){
                sendSessionEndMessage();
                // Wait for remote to close
                clientWebSocket.awaitClosure();
                // Close session
                session.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public void sendChatMessage(String msg, String to) { 
        ChatMessage c = new ChatMessage(id, to, msg);
        sendMessage(c);
    }


    public void startLesson(){
        sendMessage(new LessonStartMessage(id));
    }

    public void sendSessionEndMessage() {
        sendMessage(new SessionEndMessage(id));
    }

    public Message getMessageReceived() {
        return clientWebSocket.nextMessageFromQueue();
    }

    public String getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    private void sendMessage(Message message) {
        try {
            String json = mapper.writeValueAsString(message);
            session.getBasicRemote().sendText(json);
            Main.messageHistory.put(message.getId(),message);
            // TODO - issue with lesson start message is there is one outward message and potentially several responses - need to consider how to handle this.
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
