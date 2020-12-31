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
            c1.sendMessage(msg, c2.getId());
            Message received = c2.getMessageReceived();
            Assert.assertNotNull(received);
            //Assert.assertTrue(received instanceof ChatMessage);
            Assert.assertEquals("Learner 1", received.getFrom());    
            Assert.assertEquals("Educator", received.getTo());    
            Assert.assertEquals(msg, received.getText());
        } finally {
            c1.disconnect();
            c2.disconnect();
        }
    }

    // @Test
    // public void msgJson() throws JsonProcessingException {
    //     ObjectMapper mapper = new ObjectMapper();
    //     Message message = new Message("me", "you", "Hello");
    //     String json = mapper.writeValueAsString(message);
    //     System.out.println(message);
    //     System.out.println(json);

    //     ObjectMapper mapper2 = new ObjectMapper();
    //     Message message2 = mapper2.readValue(json, Message.class);
    //     System.out.println(message2);
    // }
}
