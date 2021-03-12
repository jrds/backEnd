package org.github.jrds.server;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.github.jrds.server.messages.ChatMessage;
import org.github.jrds.server.messages.Message;
import org.github.jrds.server.messages.Response;
import org.junit.Assert;
import org.junit.Test;

public class ChatTest extends ApplicationTest
{

    private final String msg = "Test message";

    @Test
    public void sendAndReceive() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);

        Future<Response> sentMessageFuture = c2.sendChatMessage(msg, c1.getId());
        Response sentMessageResponse = sentMessageFuture.get(10, TimeUnit.SECONDS);

        Assert.assertTrue(sentMessageResponse.isSuccess());

        Message received = c1.getMessageReceived();
        Assert.assertTrue(received instanceof ChatMessage);
        ChatMessage receivedChat = (ChatMessage) received;

        Assert.assertEquals(c2.getId(), receivedChat.getFrom());
        Assert.assertEquals(c1.getId(), receivedChat.getTo());
        Assert.assertEquals(msg, receivedChat.getText());
    }

    @Test
    public void educatorReceivesMessagesFrom2LearnersSimultaneously()
    {
        String msg1 = "Learner 1 - Test message";
        String msg2 = "Learner 2 - Test message";

        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);
        TestClient c3 = connect(l2Id, l2Name, lesson1);

        c2.sendChatMessage(msg1, c1.getId());
        c3.sendChatMessage(msg2, c1.getId());

        List<Message> messagesReceived = Arrays.asList(c1.getMessageReceived(), c1.getMessageReceived());
        Assert.assertTrue(messagesReceived.contains(new ChatMessage(l1Id, eduId, msg1)));
        Assert.assertTrue(messagesReceived.contains(new ChatMessage(l2Id, eduId, msg2)));
    }

    @Test
    public void educatorReceivesMessageAndResponds()
    {
        String response = "Educator response - Test message";

        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);

        c2.sendChatMessage(msg, c1.getId());
        Message received = c1.getMessageReceived();
        Assert.assertEquals(new ChatMessage(l1Id, eduId, msg), received);

        c1.sendChatMessage(response, c2.getId());
        Message responseReceived = c2.getMessageReceived();
        Assert.assertEquals(new ChatMessage(eduId, l1Id, response), responseReceived);
    }

    @Test
    public void educatorReceivesMessagesFrom2StudentsAndRespondsToEachOneSimultaneously()
    {
        String msg1 = "Learner 1 - Test message";
        String msg2 = "Learner 2 - Test message";
        String response1 = "Educator response to Learner 1 - Test message";
        String response2 = "Educator response to Learner 2 - Test message";

        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);
        TestClient c3 = connect(l2Id, l2Name, lesson1);

        c2.sendChatMessage(msg1, c1.getId());
        c3.sendChatMessage(msg2, c1.getId());
        c1.sendChatMessage(response1, c3.getId());
        c1.sendChatMessage(response2, c2.getId());

        List<Message> messagesReceived = Arrays.asList(c1.getMessageReceived(), c1.getMessageReceived(),
                c3.getMessageReceived(), c2.getMessageReceived());
        Assert.assertTrue(messagesReceived.contains(new ChatMessage(l1Id, eduId, msg1)));
        Assert.assertTrue(messagesReceived.contains(new ChatMessage(l2Id, eduId, msg2)));
        Assert.assertTrue(messagesReceived.contains(new ChatMessage(eduId, l1Id, response2)));
        Assert.assertTrue(messagesReceived.contains(new ChatMessage(eduId, l2Id, response1)));
    }
}
