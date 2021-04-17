package org.github.jrds.codi.messaging.chat;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.github.jrds.codi.core.messages.Message;
import org.github.jrds.codi.core.messages.Response;
import org.github.jrds.codi.server.testing.ApplicationTest;
import org.github.jrds.codi.server.testing.ClientWebSocket;
import org.github.jrds.codi.server.testing.TestClient;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChatTest extends ApplicationTest
{
    @BeforeClass
    public static void registerMessageSubtypes()
    {
        ClientWebSocket.registerMessageSubtype(ChatMessage.class);
    }

    private final String msg = "Test message";

    @Test
    public void sendAndReceive() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, lesson1);
        TestClient c2 = connect(l1Id,  lesson1);

        Future<Response> sentMessageFuture = sendChatMessage(c2, c1.getId(), msg);
        Response sentMessageResponse = sentMessageFuture.get(10, TimeUnit.SECONDS);

        Assert.assertTrue(sentMessageResponse.isSuccess());

        Message received = c1.getMessageReceived(ChatMessage.class);

        Assert.assertTrue(messageContentAsExpected(c2.getId(), c1.getId(), msg, (ChatMessage) received));
    }

    @Test
    public void educatorReceivesMessagesFrom2LearnersSimultaneously() throws InterruptedException, ExecutionException, TimeoutException
    {
        String msg1 = "Learner 1 - Test message";
        String msg2 = "Learner 2 - Test message";

        TestClient c1 = connect(eduId, lesson1);
        TestClient c2 = connect(l1Id, lesson1);
        TestClient c3 = connect(l2Id, lesson1);

        Future<Response> sentMessageFuture1 = sendChatMessage(c2, c1.getId(), msg1);
        Response sentMessageResponse1 = sentMessageFuture1.get(10, TimeUnit.SECONDS);

        Future<Response> sentMessageFuture2 = sendChatMessage(c3, c1.getId(), msg2);
        Response sentMessageResponse2 = sentMessageFuture2.get(10, TimeUnit.SECONDS);

        Assert.assertTrue(sentMessageResponse1.isSuccess());
        Assert.assertTrue(sentMessageResponse2.isSuccess());

        List<Message> messagesReceived = Arrays.asList(c1.getMessageReceived(ChatMessage.class), c1.getMessageReceived(ChatMessage.class));

        Assert.assertTrue(messageContentAsExpected(c2.getId(), c1.getId(), msg1, (ChatMessage) messagesReceived.get(0)));

        Assert.assertTrue(messageContentAsExpected(c3.getId(), c1.getId(), msg2, (ChatMessage) messagesReceived.get(1)));
    }

    @Test
    public void educatorReceivesMessageAndResponds() throws InterruptedException, ExecutionException, TimeoutException
    {
        String response = "Educator response - Test message";

        TestClient c1 = connect(eduId, lesson1);
        TestClient c2 = connect(l1Id, lesson1);

        Future<Response> sentMessageFuture1 = sendChatMessage(c2, c1.getId(), msg);
        sentMessageFuture1.get(10, TimeUnit.SECONDS);

        Future<Response> sentMessageFuture2 = sendChatMessage(c1, c2.getId(), response);
        sentMessageFuture2.get(10, TimeUnit.SECONDS);

        Message educatorReceived = c1.getMessageReceived(ChatMessage.class);
        Assert.assertTrue(messageContentAsExpected(c2.getId(), c1.getId(), msg, (ChatMessage) educatorReceived ));

        Message learnerReceived = c2.getMessageReceived(ChatMessage.class);
        Assert.assertTrue(messageContentAsExpected(c1.getId(), c2.getId(), response, (ChatMessage) learnerReceived ));
    }

    @Test
    public void educatorReceivesMessagesFrom2StudentsAndRespondsToEachOneSimultaneously() throws InterruptedException, ExecutionException, TimeoutException
    {
        String msg1 = "Learner 1 - Test message";
        String msg2 = "Learner 2 - Test message";
        String response1 = "Educator response to Learner 1 - Test message";
        String response2 = "Educator response to Learner 2 - Test message";

        TestClient c1 = connect(eduId, lesson1);
        TestClient c2 = connect(l1Id, lesson1);
        TestClient c3 = connect(l2Id, lesson1);

        Future<Response> sentMessageFuture1 = sendChatMessage(c3, c1.getId(), msg1);
        Response sentMessageResponse1 = sentMessageFuture1.get(10, TimeUnit.SECONDS);

        Future<Response> sentMessageFuture2 = sendChatMessage(c2, c1.getId(), msg2);
        Response sentMessageResponse2 = sentMessageFuture2.get(10, TimeUnit.SECONDS);

        Future<Response> sentMessageFuture3 = sendChatMessage(c1, c3.getId(), response1);
        Response sentMessageResponse3 = sentMessageFuture3.get(10, TimeUnit.SECONDS);

        Future<Response> sentMessageFuture4 = sendChatMessage(c1, c2.getId(), response2);
        Response sentMessageResponse4 = sentMessageFuture4.get(10, TimeUnit.SECONDS);

        Message firstMessageReceivedByEducator = c1.getMessageReceived(ChatMessage.class);
        Message secondMessageReceivedByEducator = c1.getMessageReceived(ChatMessage.class);
        Message learner2ResponseMessage = c3.getMessageReceived(ChatMessage.class);
        Message learner1ResponseMessage = c2.getMessageReceived(ChatMessage.class);

        Assert.assertTrue(sentMessageResponse1.isSuccess());
        Assert.assertTrue(sentMessageResponse2.isSuccess());
        Assert.assertTrue(sentMessageResponse3.isSuccess());
        Assert.assertTrue(sentMessageResponse4.isSuccess());

        Assert.assertTrue(messageContentAsExpected(c3.getId(), c1.getId(), msg1, (ChatMessage) firstMessageReceivedByEducator));

        Assert.assertTrue(messageContentAsExpected(c2.getId(), c1.getId(), msg2, (ChatMessage) secondMessageReceivedByEducator));

        Assert.assertTrue(messageContentAsExpected(c1.getId(), c3.getId(), response1, (ChatMessage) learner2ResponseMessage));

        Assert.assertTrue(messageContentAsExpected(c1.getId(), c2.getId(), response2, (ChatMessage) learner1ResponseMessage));
    }

    private Future<Response> sendChatMessage(TestClient from, String to, String text)
    {
        ChatMessage request = new ChatMessage(from.getId(), to, text);
        return from.sendRequest(request);
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
