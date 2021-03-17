package org.github.jrds.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.github.jrds.server.dto.HelpRequestDto;
import org.github.jrds.server.extensions.chat.ChatMessage;
import org.github.jrds.server.extensions.help.CancelHelpRequestMessage;
import org.github.jrds.server.extensions.help.OpenHelpRequestsMessage;
import org.github.jrds.server.extensions.help.RequestHelpMessage;
import org.github.jrds.server.extensions.lesson.InstructionMessage;
import org.github.jrds.server.extensions.lesson.LessonStartMessage;
import org.github.jrds.server.messages.Message;
import org.github.jrds.server.messages.Request;
import org.github.jrds.server.messages.Response;
import org.github.jrds.server.messages.SessionStartMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class ClientWebSocket extends Endpoint
{


    private static final String BASE_URL = "ws://localhost:8080/lesson/";

    private final CountDownLatch closureLatch = new CountDownLatch(1);
    private final BlockingQueue<Message> messagesReceived = new LinkedBlockingQueue<>();
    private final ObjectMapper mapper;
    private final Map<Integer, CompletableFuture<Response>> uncompletedFutures = new ConcurrentHashMap<>();
    private final String userId;
    private List<HelpRequestDto> openHelpRequests = new ArrayList<>();

    private Session session;

    public ClientWebSocket(String userId)
    {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.registerSubtypes(
                new NamedType(RequestHelpMessage.class, "requestHelp"),
                new NamedType(CancelHelpRequestMessage.class, "requestHelpCancel"),
                new NamedType(ChatMessage.class, "chat"),
                new NamedType(OpenHelpRequestsMessage.class, "openHelpRequests"),
                new NamedType(InstructionMessage.class, "instruction"),
                new NamedType(LessonStartMessage.class, "lessonStart")
        );
        this.userId = userId;
    }

    public static ClientWebSocket connect(String userId, String lessonId)
    {
        try
        {
            final URI uri = URI.create(BASE_URL + lessonId);

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            ClientWebSocket webSocket = new ClientWebSocket(userId);
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
            if (!newSession.isOpen())
            {
                throw new RuntimeException("Failed to open session");
            }
            else
            {
                webSocket.session = newSession;
                Response response = webSocket.sendMessage(new SessionStartMessage(userId)).get(10, TimeUnit.SECONDS);
                if (response.isFailure())
                {
                    throw new IllegalStateException("Failed to start session: " + response.asFailure().getFailureReason());
                }
            }
            return webSocket;

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig config)
    {
        MessageHandler.Whole<String> handler = new MessageHandler.Whole<>()
        {
            @Override
            public void onMessage(String message)
            {
                handleTextMessage(session, message);
            }
        };
        session.addMessageHandler(handler);
        System.out.println(DateTimeFormatter.ISO_TIME.format(LocalDateTime.now()) + " Client Socket Connected: " + session);
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
            else
            {
                if (msg instanceof OpenHelpRequestsMessage)
                {
                    openHelpRequests = ((OpenHelpRequestsMessage) msg).getOpenHelpRequests();
                }
                messagesReceived.add(msg);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        System.out.println(DateTimeFormatter.ISO_TIME.format(LocalDateTime.now()) + " Client Received:[" + userId + "]: " + message);
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
