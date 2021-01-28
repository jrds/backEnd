package org.github.jrds.server;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ChatTest {

    private Main server;
    private String edu = "Educator";
    private String l1 = "Learner 1";
    private String l2 = "Learner 2";
    private String msg = "Test message";

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
        TestClient c1 = new TestClient(l1);
        TestClient c2 = new TestClient(edu);

        c1.connect();
        c2.connect();

        try {
            c1.sendChatMessage(msg, c2.getId());
            Message received = c2.getMessageReceived();
            Assert.assertEquals(new ChatMessage(l1, edu, msg), received);
            
        } finally {
            c1.disconnect();
            c2.disconnect();
        }
    }

    @Test
    public void educatorReceivesMessagesFrom2LearnersSimultaneously(){
        String msg1 = "Learner 1 - Test message";
        String msg2 = "Learner 2 - Test message";

        TestClient c1 = new TestClient(edu);
        TestClient c2 = new TestClient(l1);
        TestClient c3 = new TestClient(l2);

        c1.connect();
        c2.connect();
        c3.connect();

        try {
            c2.sendChatMessage(msg1, c1.getId());
            c3.sendChatMessage(msg2, c1.getId());

            List<Message> messagesReceived = Arrays.asList(c1.getMessageReceived(),c1.getMessageReceived());
            Assert.assertTrue(messagesReceived.contains(new ChatMessage(l1, edu, msg1)));
            Assert.assertTrue(messagesReceived.contains(new ChatMessage(l2, edu, msg2)));

        } finally {
            c1.disconnect();
            c2.disconnect();
            c3.disconnect();
        }

    }

    @Test
    public void educatorReceivesMessageAndResponds(){
        TestClient c1 = new TestClient(edu);
        TestClient c2 = new TestClient(l1);
        
        String response = "Educator response - Test message";

        c1.connect();
        c2.connect();

        try {
            c2.sendChatMessage(msg, c1.getId());
            Message received = c1.getMessageReceived();
            Assert.assertEquals(new ChatMessage(l1, edu, msg), received);
            
            c1.sendChatMessage(response, c2.getId());
            Message responceReceived = c2.getMessageReceived();
            Assert.assertEquals(new ChatMessage(edu, l1, response), responceReceived);
            
        } finally {
            c1.disconnect();
            c2.disconnect();
        }
    }

    @Test
    public void educatorReceivesMessagesFrom2StudentsAndRespondsToEachOneSimultaneously(){
        String msg1 = "Learner 1 - Test message";
        String msg2 = "Learner 2 - Test message";
        String response1 = "Educator response to Learner 1 - Test message";
        String response2 = "Educator response to Learner 2 - Test message";
        

        TestClient c1 = new TestClient(edu);
        TestClient c2 = new TestClient(l1);
        TestClient c3 = new TestClient(l2);

        c1.connect();
        c2.connect();
        c3.connect();

        try {
            c2.sendChatMessage(msg1, c1.getId());
            c3.sendChatMessage(msg2, c1.getId());
            c1.sendChatMessage(response1, c3.getId());
            c1.sendChatMessage(response2, c2.getId());

            List<Message> messagesReceived = Arrays.asList(c1.getMessageReceived(),c1.getMessageReceived(),c3.getMessageReceived(),c2.getMessageReceived());
            Assert.assertTrue(messagesReceived.contains(new ChatMessage(l1, edu, msg1)));
            Assert.assertTrue(messagesReceived.contains(new ChatMessage(l2, edu, msg2)));
            Assert.assertTrue(messagesReceived.contains(new ChatMessage(edu, l1, response2)));
            Assert.assertTrue(messagesReceived.contains(new ChatMessage(edu, l2, response1)));


        } finally {
            c1.disconnect();
            c2.disconnect();
            c3.disconnect();
        }

        
    }


}
