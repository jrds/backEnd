package org.github.jrds.server;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ChatTest extends ApplicationTest {

    // TODO - IMPORTANT!!!! Tests only pass when this file is run seperateley.
    // when all tests are run they all fail. 
    // need to investigate. might have to clear all server info pre each test file
    // this might be a method on applicationtest?
    
     
    private String msg = "Test message";

    @Test
    public void sendAndReceive() {
        TestClient c1 = connect(edu, lesson1);
        TestClient c2 = connect(l1, lesson1);

        c2.sendChatMessage(msg, c1.getId());
        Message received = c1.getMessageReceived();
        Assert.assertEquals(new ChatMessage(l1, edu, msg), received);
    }

    @Test
    public void educatorReceivesMessagesFrom2LearnersSimultaneously() {
        String msg1 = "Learner 1 - Test message";
        String msg2 = "Learner 2 - Test message";

        TestClient c1 = connect(edu, lesson1);
        TestClient c2 = connect(l1, lesson1);
        TestClient c3 = connect(l2, lesson1);

        c2.sendChatMessage(msg1, c1.getId());
        c3.sendChatMessage(msg2, c1.getId());

        List<Message> messagesReceived = Arrays.asList(c1.getMessageReceived(), c1.getMessageReceived());
        Assert.assertTrue(messagesReceived.contains(new ChatMessage(l1, edu, msg1)));
        Assert.assertTrue(messagesReceived.contains(new ChatMessage(l2, edu, msg2)));
    }

    @Test
    public void educatorReceivesMessageAndResponds() {
        String response = "Educator response - Test message";

        TestClient c1 = connect(edu, lesson1);
        TestClient c2 = connect(l1, lesson1);

        c2.sendChatMessage(msg, c1.getId());
        Message received = c1.getMessageReceived();
        Assert.assertEquals(new ChatMessage(l1, edu, msg), received);

        c1.sendChatMessage(response, c2.getId());
        Message responceReceived = c2.getMessageReceived();
        Assert.assertEquals(new ChatMessage(edu, l1, response), responceReceived);
    }

    @Test
    public void educatorReceivesMessagesFrom2StudentsAndRespondsToEachOneSimultaneously() {
        String msg1 = "Learner 1 - Test message";
        String msg2 = "Learner 2 - Test message";
        String response1 = "Educator response to Learner 1 - Test message";
        String response2 = "Educator response to Learner 2 - Test message";

        TestClient c1 = connect(edu, lesson1);
        TestClient c2 = connect(l1, lesson1);
        TestClient c3 = connect(l2, lesson1);

        c2.sendChatMessage(msg1, c1.getId());
        c3.sendChatMessage(msg2, c1.getId());
        c1.sendChatMessage(response1, c3.getId());
        c1.sendChatMessage(response2, c2.getId());

        List<Message> messagesReceived = Arrays.asList(c1.getMessageReceived(), c1.getMessageReceived(),
                c3.getMessageReceived(), c2.getMessageReceived());
        Assert.assertTrue(messagesReceived.contains(new ChatMessage(l1, edu, msg1)));
        Assert.assertTrue(messagesReceived.contains(new ChatMessage(l2, edu, msg2)));
        Assert.assertTrue(messagesReceived.contains(new ChatMessage(edu, l1, response2)));
        Assert.assertTrue(messagesReceived.contains(new ChatMessage(edu, l2, response1)));
    }
}
