package org.github.jrds.server;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

import javax.websocket.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.github.jrds.server.dto.HelpRequestDto;
import org.github.jrds.server.messages.*;

public class ClientWebSocket extends Endpoint
{


    private static final String BASE_URL = "ws://localhost:8080/lesson/";

    private final CountDownLatch closureLatch = new CountDownLatch(1);
    private final BlockingQueue<Message> messagesReceived = new LinkedBlockingQueue<>();
    private final ObjectMapper mapper;
    private final Map<Integer, CompletableFuture<Response>> uncompletedFutures = new ConcurrentHashMap<>();
    private List<HelpRequestDto> openHelpRequests = new ArrayList<>();

    private Session session;

    public ClientWebSocket()
    {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }

    public static ClientWebSocket connect(String userId, String lessonId)
    {
        try
        {
            final URI uri = URI.create(BASE_URL + lessonId);

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            ClientWebSocket webSocket = new ClientWebSocket();
            ClientEndpointConfig.Configurator configurator = new ClientEndpointConfig.Configurator()
            {
                @Override
                public void beforeRequest(Map<String, List<String>> headers)
                {
                    headers.put("Authorization", Collections
                            .singletonList("Basic " + Base64.getEncoder().encodeToString((userId + ":pw").getBytes())));
                }
            };
            ClientEndpointConfig clientConfig = ClientEndpointConfig.Builder.create()
                    .configurator(configurator)
                    .build();
            Session newSession = container.connectToServer(webSocket, clientConfig, uri);
            // TODO If time fix by moving lesson validation to a hello message request/response
            try
            {
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
            }
            if (!newSession.isOpen())
            {
                throw new RuntimeException("Failed to open session");
            }
            else
            {
                webSocket.session = newSession;
            }
            return webSocket;

        }
        catch (DeploymentException | IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig config)
    {
        MessageHandler.Whole<String> handler = new MessageHandler.Whole<String>()
        {
            @Override
            public void onMessage(String message)
            {
                handleTextMessage(session, message);
            }
        };
        session.addMessageHandler(handler);
        System.out.println("Client Socket Connected: " + session);
    }

    private void handleTextMessage(Session sess, String message)
    {
        try
        {
            Message msg = mapper.readValue(message, Message.class);
            int msgId = msg.getId();
            if (msg instanceof Response)
            {
                if (uncompletedFutures.containsKey(msgId))
                {
                    uncompletedFutures.remove(msgId).complete((Response) msg);
                }
                else
                {
                    throw new IllegalStateException("Unexpected message");
                }
            }

            else if (msg instanceof OpenHelpRequestsMessage)
            {
                openHelpRequests = ((OpenHelpRequestsMessage) msg).getOpenHelpRequests();
            }
            else
            {
                messagesReceived.add(msg);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        System.out.println("Client Received TEXT message: " + message);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason)
    {
        System.out.println("Client Socket Closed: " + closeReason);
        try
        {
            session.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            closureLatch.countDown();
        }
    }

    @Override
    public void onError(Session session, Throwable cause)
    {
        cause.printStackTrace(System.err);
    }

    public void awaitClosure() throws InterruptedException
    {
        System.out.println("Client Awaiting closure from remote");
        closureLatch.await(20, TimeUnit.SECONDS);
    }

    Message nextMessageFromQueue()
    {
        try
        {
            return messagesReceived.poll(10, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            return null;
        }
    }

    List<HelpRequestDto> getOpenHelpRequests()
    {
        return openHelpRequests;
    }

    public Future<Response> sendMessage(Request message)
    {
        try
        {
            String json = mapper.writeValueAsString(message);
            CompletableFuture<Response> completableFuture = new CompletableFuture<>();
            uncompletedFutures.put(message.getId(), completableFuture);
            session.getBasicRemote().sendText(json);
            return completableFuture;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
