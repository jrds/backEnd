package org.github.jrds.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/lesson/{lessonId}")
public class MessageSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSocket.class);
    private static Map<String, Session> userSessions = new HashMap<>();
    private CountDownLatch closureLatch = new CountDownLatch(1);
    private ObjectMapper mapper = new ObjectMapper();

    @OnOpen
    public void onWebSocketConnect(Session sess) throws Exception {
        String userId = sess.getUserPrincipal().getName();
        LOGGER.info("Socket Connected: " + sess);
        userSessions.put(userId, sess);
        String lessonId = sess.getPathParameters().get("lessonId");
        if (!Main.attendanceStore.attendanceRegistered(userId, lessonId)) {
            Main.attendanceStore.addAttendance(userId, lessonId);
        } else {
            throw new Exception();
        }
        // TODO add to assumptions documentation that a user will not have 2 classes at
        // the same time.
    }

    // TODO - Add functionality to query attendance (message to request attendance,
    // and response) - At to tests

    @OnMessage
    public void onWebSocketText(Session sess, String message) throws IOException {
        Message msg = mapper.readValue(message, Message.class);
        LOGGER.info("Received message: " + msg);

        if (msg instanceof SessionEndMessage) {
            sess.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Thanks"));
            String userId = sess.getUserPrincipal().getName();
            String lessonId = sess.getPathParameters().get("lessonId");
            Attendance a = new Attendance(userId, lessonId);
            Main.attendanceStore.removeAttendance(a);
            // TODO - QUESTION - should this be recieving the two IDs as strings, or as is?
            userSessions.remove(sess.getUserPrincipal().getName());
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
