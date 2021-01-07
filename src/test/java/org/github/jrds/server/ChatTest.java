package org.github.jrds.server;

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
            Assert.assertNotNull(received);
            Assert.assertTrue(received instanceof ChatMessage);
            Assert.assertEquals("Learner 1", received.getFrom());    
            Assert.assertEquals("Educator", received.getTo());    
            Assert.assertEquals(msg, ((ChatMessage) received).getText());
        } finally {
            c1.disconnect();
            c2.disconnect();
        }
    }
}
