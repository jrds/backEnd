package org.github.jrds.server;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.github.jrds.server.extensions.chat.ChatMessage;
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
        Assert.assertTrue(messageContentAsExpected(c2.getId(), c1.getId(), msg, (ChatMessage) received));
    }

    @Test
    public void educatorReceivesMessagesFrom2LearnersSimultaneously() throws InterruptedException, ExecutionException, TimeoutException
    {
        String msg1 = "Learner 1 - Test message";
        String msg2 = "Learner 2 - Test message";

        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);
        TestClient c3 = connect(l2Id, l2Name, lesson1);

        Future<Response> sentMessageFuture1 = c2.sendChatMessage(msg1, c1.getId());
        Response sentMessageResponse1 = sentMessageFuture1.get(10, TimeUnit.SECONDS);

        Future<Response> sentMessageFuture2 = c3.sendChatMessage(msg2, c1.getId());
        Response sentMessageResponse2 = sentMessageFuture2.get(10, TimeUnit.SECONDS);

        Assert.assertTrue(sentMessageResponse1.isSuccess());
        Assert.assertTrue(sentMessageResponse2.isSuccess());

        List<Message> messagesReceived = Arrays.asList(c1.getMessageReceived(), c1.getMessageReceived());

        Assert.assertTrue(messagesReceived.get(0) instanceof ChatMessage);
        Assert.assertTrue(messageContentAsExpected(c2.getId(), c1.getId(), msg1, (ChatMessage) messagesReceived.get(0)));

        Assert.assertTrue(messagesReceived.get(1) instanceof ChatMessage);
        Assert.assertTrue(messageContentAsExpected(c3.getId(), c1.getId(), msg2, (ChatMessage) messagesReceived.get(1)));
    }

    @Test
    public void educatorReceivesMessageAndResponds() throws InterruptedException, ExecutionException, TimeoutException
    {
        String response = "Educator response - Test message";

        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);

        Future<Response> sentMessageFuture1 = c2.sendChatMessage(msg, c1.getId());
        Response sentMessageResponse1 = sentMessageFuture1.get(10, TimeUnit.SECONDS);

        Future<Response> sentMessageFuture2 = c1.sendChatMessage(response, c2.getId());
        Response sentMessageResponse2 = sentMessageFuture2.get(10, TimeUnit.SECONDS);

        Message educatorReceived = c1.getMessageReceived();
        Assert.assertTrue(educatorReceived instanceof ChatMessage);
        Assert.assertTrue(messageContentAsExpected(c2.getId(), c1.getId(), msg, (ChatMessage) educatorReceived ));

        Message learnerReceived = c2.getMessageReceived();
        System.out.println(learnerReceived);
        Assert.assertTrue(learnerReceived instanceof ChatMessage);
        Assert.assertTrue(messageContentAsExpected(c1.getId(), c2.getId(), response, (ChatMessage) learnerReceived ));
    }

    @Test
    public void educatorReceivesMessagesFrom2StudentsAndRespondsToEachOneSimultaneously() throws InterruptedException, ExecutionException, TimeoutException
    {
        String msg1 = "Learner 1 - Test message";
        String msg2 = "Learner 2 - Test message";
        String response1 = "Educator response to Learner 1 - Test message";
        String response2 = "Educator response to Learner 2 - Test message";

        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);
        TestClient c3 = connect(l2Id, l2Name, lesson1);

        Future<Response> sentMessageFuture1 = c3.sendChatMessage(msg1, c1.getId());
        Response sentMessageResponse1 = sentMessageFuture1.get(10, TimeUnit.SECONDS);

        Future<Response> sentMessageFuture2 = c2.sendChatMessage(msg2, c1.getId());
        Response sentMessageResponse2 = sentMessageFuture2.get(10, TimeUnit.SECONDS);

        Future<Response> sentMessageFuture3 = c1.sendChatMessage(response1, c3.getId());
        Response sentMessageResponse3 = sentMessageFuture3.get(10, TimeUnit.SECONDS);

        Future<Response> sentMessageFuture4 = c1.sendChatMessage(response2, c2.getId());
        Response sentMessageResponse4 = sentMessageFuture4.get(10, TimeUnit.SECONDS);

        Message firstMessageReceivedByEducator = c1.getMessageReceived();
        Message secondMessageReceivedByEducator = c1.getMessageReceived();
        Message learner2ResponseMessage = c3.getMessageReceived();
        Message learner1ResponseMessage = c2.getMessageReceived();

        Assert.assertTrue(sentMessageResponse1.isSuccess());
        Assert.assertTrue(sentMessageResponse2.isSuccess());
        Assert.assertTrue(sentMessageResponse3.isSuccess());
        Assert.assertTrue(sentMessageResponse4.isSuccess());

        Assert.assertTrue(firstMessageReceivedByEducator instanceof ChatMessage);
        Assert.assertTrue(messageContentAsExpected(c3.getId(), c1.getId(), msg1, (ChatMessage) firstMessageReceivedByEducator));

        Assert.assertTrue(secondMessageReceivedByEducator instanceof ChatMessage);
        Assert.assertTrue(messageContentAsExpected(c2.getId(), c1.getId(), msg2, (ChatMessage) secondMessageReceivedByEducator));

        Assert.assertTrue(learner2ResponseMessage instanceof ChatMessage);
        Assert.assertTrue(messageContentAsExpected(c1.getId(), c3.getId(), response1, (ChatMessage) learner2ResponseMessage));

        Assert.assertTrue(learner1ResponseMessage instanceof ChatMessage);
        Assert.assertTrue(messageContentAsExpected(c1.getId(), c2.getId(), response2, (ChatMessage) learner1ResponseMessage));

    }

    private boolean messageContentAsExpected(String from, String to, String text, ChatMessage cM)
    {
        try
        {
            Assert.assertEquals(from, cM.getFrom());
            Assert.assertEquals(to, cM.getTo());
            Assert.assertEquals(text, cM.getText());
            return true;
        }
        catch (AssertionError e)
        {
            return false;
        }
    }

}
