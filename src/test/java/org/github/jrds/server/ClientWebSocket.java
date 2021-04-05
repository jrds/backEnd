package org.github.jrds.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.github.jrds.server.dto.HelpRequestDto;
import org.github.jrds.server.extensions.chat.ChatMessage;
import org.github.jrds.server.extensions.code.ExecuteCodeRequest;
import org.github.jrds.server.extensions.code.CodeExecutionInfo;
import org.github.jrds.server.extensions.code.LatestLearnerCodeInfo;
import org.github.jrds.server.extensions.help.CancelHelpRequest;
import org.github.jrds.server.extensions.help.OpenHelpRequestsInfo;
import org.github.jrds.server.extensions.help.NewHelpRequest;
import org.github.jrds.server.extensions.lesson.InstructionInfo;
import org.github.jrds.server.extensions.lesson.LessonStartRequest;
import org.github.jrds.server.messages.*;

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
    private final BlockingQueue<Message> stateMessagesReceived = new LinkedBlockingQueue<>();
    private final ObjectMapper mapper;
    private final Map<Integer, CompletableFuture<Response>> uncompletedFutures = new ConcurrentHashMap<>();
    private final String userId;
    private List<HelpRequestDto> openHelpRequests = new ArrayList<>();

    private Session session;
    private Response sessionStartResponse;

    public ClientWebSocket(String userId)
    {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.registerSubtypes(
                NewHelpRequest.class,
                CancelHelpRequest.class,
                ChatMessage.class,
                OpenHelpRequestsInfo.class,
                InstructionInfo.class,
                LessonStartRequest.class,
                LearnerLessonStateInfo.class,
                ExecuteCodeRequest.class,
                CodeExecutionInfo.class,
                LatestLearnerCodeInfo.class
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
                Response response = webSocket.sendMessage(new SessionStartRequest(userId)).get(10, TimeUnit.SECONDS);
                webSocket.setSessionStartResponse(response);
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

    private void setSessionStartResponse(Response response)
    {
        this.sessionStartResponse = response;
    }

    public Response getSessionStartResponse()
    {
        return sessionStartResponse;
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
            else if (msg instanceof LearnerLessonStateInfo){
                stateMessagesReceived.add(msg);
            }
            else
            {
                if (msg instanceof OpenHelpRequestsInfo)
                {
                    openHelpRequests = ((OpenHelpRequestsInfo) msg).getOpenHelpRequests();
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
