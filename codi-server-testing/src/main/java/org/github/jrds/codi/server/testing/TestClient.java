package org.github.jrds.codi.server.testing;

import org.github.jrds.codi.core.messages.Message;
import org.github.jrds.codi.core.messages.Request;
import org.github.jrds.codi.core.messages.Response;
import org.github.jrds.codi.core.messages.SessionEndRequest;

import java.util.concurrent.Future;

public class TestClient
{
    private final String id; // TODO - REVIEW - added name, so that userStore & authStore (which treat id as u1900 etc, are the same as what id constitutes here)
    private ClientWebSocket clientWebSocket;

    public TestClient(String id)
    {
        this.id = id;
    }

    public void connect(String lessonId)
    {
        clientWebSocket = ClientWebSocket.connect(id, lessonId);
    }

    public void disconnect()
    {
        try
        {
            sendSessionEndMessage();
            // Wait for remote to close
            clientWebSocket.awaitClosure();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public Response getSessionStartResponse()
    {
        return clientWebSocket.getSessionStartResponse();
    }

    public Future<Response> sendRequest(Request request)
    {
        return clientWebSocket.sendMessage(request);
    }


    public Future<Response> sendSessionEndMessage()
    {
        SessionEndRequest m = new SessionEndRequest(id);
        return clientWebSocket.sendMessage(m);
    }

    public Message getMessageReceived()
    {
        return clientWebSocket.nextMessageFromQueue();
    }


    public Message getMessageReceived(Class messageClass)
    {
        Message nextMessage;
        do
        {
            nextMessage = clientWebSocket.nextMessageFromQueue();
        } while (nextMessage != null && !messageClass.isInstance(nextMessage));
        return nextMessage;
    }

    public String getId()
    {
        return id;
    }
}
