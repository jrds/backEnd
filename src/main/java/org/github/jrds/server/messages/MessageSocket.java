package org.github.jrds.server.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.github.jrds.server.Main;
import org.github.jrds.server.domain.Attendance;
import org.github.jrds.server.domain.Lesson;
import org.github.jrds.server.domain.User;
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
    private final Main server;
    private final CountDownLatch closureLatch = new CountDownLatch(1);
    private final ObjectMapper mapper;
    private final Map<String, Attendance> sessionAttendances = new HashMap<>();
    private final List<MessagingExtension> messagingExtensions = new ArrayList<>();

    public MessageSocket()
    {
        this.server = Main.defaultInstance;
        messagingExtensions.addAll(server.getMessagingExtension());
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        messagingExtensions.stream()
                .map(MessagingExtension::getRequestNamedTypes)
                .flatMap(Collection::stream)
                .forEach(mapper::registerSubtypes);
    }

    @OnOpen
    public void onWebSocketConnect(Session sess) throws Exception
    {
        LOGGER.info("Socket Connected: " + sess);

        String userId = sess.getUserPrincipal().getName();
        User user = server.usersStore.getUser(userId);
        userSessions.put(userId, sess);

        String lessonId = sess.getPathParameters().get("lessonId");
        Lesson lesson = server.lessonStore.getLesson(lessonId);

        if (server.attendanceStore.getAttendancesForALesson(lesson).stream().noneMatch(a -> a.getUser().equals(user)))
        {
            Attendance attendance = new Attendance(user, lesson);
            server.attendanceStore.storeAttendance(attendance);
            sessionAttendances.put(sess.getId(), attendance);
        }
        else
        {
            throw new IllegalStateException("Attendance already in existence for this user, in this lesson");
        }
        // TODO add to assumptions documentation that a user will not have 2 classes at the same time.
    }

    @OnMessage
    public void onWebSocketText(Session sess, String message) throws IOException
    {
        Request request = mapper.readValue(message, Request.class);
        LOGGER.info("Received request: " + request);
        Attendance attendance = Objects.requireNonNull(sessionAttendances.get(sess.getId()), "Invalid Session");

        if (request instanceof SessionEndMessage)
        {
            sess.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Thanks"));
            server.attendanceStore.removeAttendance(attendance);
            userSessions.remove(sess.getUserPrincipal().getName());
            sessionAttendances.remove(sess.getId());
        }
        else
        {
            MessagingExtension extension = messagingExtensions.stream()
                    .filter(e -> e.handles(request))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Unknown message type: " + request.getClass().getName()));
            try
            {
                extension.handle(request, attendance, this);
                sendMessage(new SuccessMessage(request.getFrom(), request.getId()));
            }
            catch (Exception e)
            {
                sendMessage(new FailureMessage(request.getFrom(), e.getMessage(), request.getId()));
            }
        }

//        else
//        {
//            sendMessage(request);
//            sendMessage(new SuccessMessage(request.getFrom(), request.getId()));
//        }
    }

    public void sendMessage(Message message)
    {
        try
        {
            Session to = userSessions.get(message.getTo());
            String json = mapper.writeValueAsString(message);
            to.getAsyncRemote().sendText(json);
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


//TODO - clean up

//TODO - tests to new scenarios
//TODO - full scenario, break it down, keep extending
// e.g writes code that compiles, write code that doesn't compile
// lesson is initated and instructions are sent
// educator prepares instructions in advance, and are released meaning 
// lesson will have instructions, and there's a new messsage type e.g lessonMessage or InstructionMessage.
// will need some trigger/message from the educator

