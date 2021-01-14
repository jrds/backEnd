package org.github.jrds.server;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ChatTest {

    private Main server;

    @Before
    public void setUp() {
        server = new Main();
        server.start();
    }

    @After
    public void tearDown() {
        server.stop();
    }

    @Test
    public void sendAndReceive() {
        String msg = "Test message";
        TestClient c1 = new TestClient("Learner 1");
        TestClient c2 = new TestClient("Educator");

        c1.connect();
        c2.connect();

        try {
            c1.sendChatMessage(msg, c2.getId());
            Message received = c2.getMessageReceived();
            Assert.assertEquals(new ChatMessage("Learner 1", "Educator", msg), received) ;
            
        } finally {
            c1.disconnect();
            c2.disconnect();
        }
    }

    @Test
    public void educatorRecievesMessagesFrom2LearnersSimultaneously(){
        String msg1 = "Learner 1 Test message";
        String msg2 = "Learner 2 Test message";
        //TODO - Leaner 1 etc into constants
        TestClient c1 = new TestClient("Educator");
        TestClient c2 = new TestClient("Learner 1");
        TestClient c3 = new TestClient("Learner 2");

        c1.connect();
        c2.connect();
        c3.connect();

        try {
            c2.sendChatMessage(msg1, c1.getId());
            c3.sendChatMessage(msg2, c1.getId());

            List<Message> messagesReceived = Arrays.asList(c1.getMessageReceived(),c1.getMessageReceived());
            Assert.assertTrue(messagesReceived.contains(new ChatMessage("Learner 1", "Educator", msg1)));
            Assert.assertTrue(messagesReceived.contains(new ChatMessage("Learner 2", "Educator", msg2)));

        } finally {
            c1.disconnect();
            c2.disconnect();
            c3.disconnect();
        }

    }
}
