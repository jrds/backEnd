package org.github.jrds.codi.core.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.github.jrds.codi.core.domain.ActiveLesson;
import org.github.jrds.codi.core.domain.Attendance;
import org.github.jrds.codi.core.domain.Role;
import org.github.jrds.codi.core.domain.User;
import org.github.jrds.codi.core.persistence.PersistenceServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@ServerEndpoint(value = "/lesson/{lessonId}")
public class MessageSocket
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSocket.class);
    private static final Map<String, Session> userSessions = new HashMap<>();

    private final CountDownLatch closureLatch = new CountDownLatch(1);
    private final ObjectMapper mapper;
    private final Map<String, ActiveLesson> sessionLesson = new HashMap<>();
    private final List<MessagingExtension> messagingExtensions = MessagingContext.messagingExtensions;
    private final MessageStats messageStats = MessagingContext.messageStats;
    private final List<String> mockDB = new ArrayList<>();
    private final PersistenceServices persistenceServices;

    private String lessonId;
    private User user;

    public MessageSocket()
    {
        persistenceServices = ServiceLoader.load(PersistenceServices.class).findFirst().orElseThrow();
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        messagingExtensions.stream()
                .map(MessagingExtension::getRequestTypes)
                .flatMap(Collection::stream)
                .forEach(mapper::registerSubtypes);
    }

    @OnOpen
    public void onWebSocketConnect(Session sess) throws Exception
    {
        LOGGER.info("Socket Connected: " + sess);

        String userId = sess.getUserPrincipal().getName();
        user = persistenceServices.getUsersStore().getUser(userId);
        userSessions.put(userId, sess);
        lessonId = sess.getPathParameters().get("lessonId");
        sessionLesson.put(userId, persistenceServices.getActiveLessonStore().getActiveLesson(lessonId));
    }

    @OnMessage
    public void onWebSocketText(Session sess, String message) throws IOException
    {
        Request request = mapper.readValue(message, Request.class);
        messageStats.incrementReceived(request.getFrom());
        LOGGER.info("Received request: " + message);

        ActiveLesson activeLesson = sessionLesson.get(user.getId());

        if (request instanceof SessionStartRequest)
        {
            if (activeLesson.getActiveLessonAttendances().stream().noneMatch(a -> a.getUser().equals(user)))
            {
                Attendance attendance = activeLesson.registerAttendance(user);
                mockDB.add("JOINED - " + attendance.toString()); //TODO - mock DB only be @ store level not Message socket.
                sendMessage(new SessionStartResponse(request.getFrom(), request.getId(), attendance.getRole().toString(), activeLesson.getActiveLessonState().toString()));
                messagingExtensions.forEach(ext -> ext.userJoined(user, activeLesson, attendance.getRole(), this));
                if (attendance.getRole() == Role.LEARNER){
                    sendMessage(new LearnerLessonStateInfo(request.getFrom(), activeLesson));

                }
            }
            else
            {
                sendMessage(new FailureResponse(request.getFrom(), "Attendance already in existence for this user, in this lesson", request.getId()));
            }
        }
        else
        {
            Attendance attendance = Objects.requireNonNull(activeLesson.getAttendance(user), "Invalid Session");

            if (request instanceof SessionEndRequest)
            {
                sess.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Thanks"));
                mockDB.add("LEFT - " + attendance.toString());
                userSessions.remove(sess.getUserPrincipal().getName());
                persistenceServices.getActiveLessonStore().getActiveLesson(lessonId).removeAttendance(attendance);
                messagingExtensions.forEach(ext -> ext.userLeft(user, activeLesson, attendance.getRole()));
            }
            else
            {
                MessagingExtension extension = messagingExtensions.stream()
                        .filter(e -> e.handles(request))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Unknown message type: " + request.getClass().getName()));
                try
                {
                    extension.handle(request, activeLesson, this);
                    sendMessage(new SuccessResponse(request.getFrom(), request.getId()));
                }
                catch (Exception e)
                {
                    sendMessage(new FailureResponse(request.getFrom(), e.getMessage(), request.getId()));
                }
            }
        }
    }

    public void sendMessage(Message message)
    {
        try
        {
            Session to = userSessions.get(message.getTo());
            String json = mapper.writeValueAsString(message);
            LOGGER.info("Sending: " + json);
            to.getAsyncRemote().sendText(json);
            messageStats.incrementSent(to.getId());
        }
        catch (JsonProcessingException e)
        {
            throw new IllegalStateException(e);
        }
    }

    @OnClose
    public void onWebSocketClose(CloseReason reason)
    {
        LOGGER.info("Socket Closed: " + reason);
        closureLatch.countDown();
    }

    @OnError
    public void onWebSocketError(Throwable cause)
    {
        cause.printStackTrace(System.err);
    }

    public void awaitClosure() throws InterruptedException
    {
        LOGGER.info("Awaiting closure from remote");
        closureLatch.await();
    }

}

