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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.github.jrds.server.domain.Attendance;
import org.github.jrds.server.domain.Lesson;
import org.github.jrds.server.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/lesson/{lessonId}")
public class MessageSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSocket.class);
    private static Map<String, Session> userSessions = new HashMap<>();
    private CountDownLatch closureLatch = new CountDownLatch(1);
    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, Attendance> sessionAttendances = new HashMap<>();

    @OnOpen
    public void onWebSocketConnect(Session sess) throws Exception {
        LOGGER.info("Socket Connected: " + sess);

        String userId = sess.getUserPrincipal().getName();
        User user = Main.usersStore.getUser(userId);
        userSessions.put(userId, sess);
        
        String lessonId = sess.getPathParameters().get("lessonId");
        Lesson lesson = Main.lessonStore.getLesson(lessonId);

        if (Main.attendanceStore.getAttendancesForALesson(lesson).stream().noneMatch(a -> a.getUser().equals(user))) {
            Attendance attendance = new Attendance(user, lesson);
            Main.attendanceStore.storeAttendance(attendance);
            sessionAttendances.put(sess.getId(), attendance);
        } else {
            throw new IllegalStateException("Attendance already in existance for this user, in this lesson");
        }
        // TODO add to assumptions documentation that a user will not have 2 classes at
        // the same time.
    }

    @OnMessage
    public void onWebSocketText(Session sess, String message) throws IOException {
        Message msg = mapper.readValue(message, Message.class);
        LOGGER.info("Received message: " + msg);
        Attendance attendance = Objects.requireNonNull(sessionAttendances.get(sess.getId()), "Invalid Session");

        if (msg instanceof SessionEndMessage) {
            sess.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Thanks"));
            Main.attendanceStore.removeAttendance(attendance);
            userSessions.remove(sess.getUserPrincipal().getName());
            sessionAttendances.remove(sess.getId());
        } else {
            Session to = userSessions.get(msg.getTo());
            to.getAsyncRemote().sendText(message);
        }
    }

    @OnClose
    public void onWebSocketClose(CloseReason reason) {
        LOGGER.info("Socket Closed: " + reason);
        closureLatch.countDown();
    }

    @OnError
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace(System.err);
    }

    public void awaitClosure() throws InterruptedException {
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

