package org.github.jrds.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.github.jrds.server.domain.Attendance;
import org.github.jrds.server.domain.Instruction;
import org.github.jrds.server.domain.Lesson;
import org.github.jrds.server.domain.User;
import org.github.jrds.server.dto.InstructionDto;
import org.github.jrds.server.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/lesson/{lessonId}")
public class MessageSocket
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSocket.class);
    private static final Map<String, Session> userSessions = new HashMap<>();
    private final Main server;
    private final CountDownLatch closureLatch = new CountDownLatch(1);
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, Attendance> sessionAttendances = new HashMap<>();

    public MessageSocket()
    {
        this.server = Main.defaultInstance;
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
        Message msg = mapper.readValue(message, Message.class);
        LOGGER.info("Received message: " + msg);
        Attendance attendance = Objects.requireNonNull(sessionAttendances.get(sess.getId()), "Invalid Session");

        if (msg instanceof SessionEndMessage)
        {
            sess.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Thanks"));
            server.attendanceStore.removeAttendance(attendance);
            userSessions.remove(sess.getUserPrincipal().getName());
            sessionAttendances.remove(sess.getId());
            //TODO - REVIEW - session end message shouldn't send a success message because we've removed it already.
            //Or could be a success message at the start of this if.
        }
        else if (msg instanceof LessonStartMessage)
        {
            if (msg.getFrom().equals(attendance.getLesson().getEducator().getId()))
            {
                Lesson lesson = attendance.getLesson();
                for (Instruction i : lesson.getAllInstructions())
                {
                    for (User learner : lesson.getLearners())
                    {
                        if (server.attendanceStore.getAttendance(learner, lesson) != null)
                        {
                            InstructionDto instructionDto = new InstructionDto(i);
                            InstructionMessage iM = new InstructionMessage(msg.getFrom(), learner.getId(), instructionDto);
                            sendMessage(iM);
                            sendMessage(new SuccessMessage(msg.getFrom(), msg.getId()));
                        }
                        else
                        {
                            sendMessage(new FailureMessage(msg.getFrom(), "Learner has no registered attendance", msg.getId()));
                            // TODO - consider how to test this - as there could be several messages (mainly success) in the educators queue.
                        }
                    }
                }
            }
            else
            {
                sendMessage(new FailureMessage(msg.getFrom(), "Learner cannot start a lesson", msg.getId()));
            }
        }
        else
        {
            sendMessage(msg);
            sendMessage(new SuccessMessage(msg.getFrom(), msg.getId()));
        }
    }

    private void sendMessage(Message message)
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

