package org.github.jrds.server;

import java.util.List;
import java.util.concurrent.Future;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.github.jrds.server.dto.HelpRequestDto;
import org.github.jrds.server.extensions.chat.ChatMessage;
import org.github.jrds.server.extensions.help.CancelHelpRequestMessage;
import org.github.jrds.server.extensions.help.RequestHelpMessage;
import org.github.jrds.server.extensions.lesson.LessonStartMessage;
import org.github.jrds.server.messages.SessionEndMessage;
import org.github.jrds.server.messages.*;

public class TestClient
{

    private static final String BASE_URL = "ws://localhost:8080/lesson/";

    private final String id; // TODO - REVIEW - added name, so that userStrore & authStore (which treat id as u1900 etc, are the same as what id constitutes here)
    private final String name; // TODO - will be useful when sending messages, as humans need names not ID, but ID is the unique identifier
    private ClientWebSocket clientWebSocket;
    private final ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    public TestClient(String id, String name)
    {
        this.id = id;
        this.name = name;
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


    public Future<Response> sendChatMessage(String msg, String to)
    {
        ChatMessage m = new ChatMessage(id, to, msg);
        return clientWebSocket.sendMessage(m);
    }

    public Future<Response> requestHelp()
    {
        RequestHelpMessage m = new RequestHelpMessage(id);
        return clientWebSocket.sendMessage(m);
    }

    public Future<Response> startLesson()
    {
        LessonStartMessage m = new LessonStartMessage(id);
        return clientWebSocket.sendMessage(m);
    }

    public Future<Response> sendSessionEndMessage()
    {
        SessionEndMessage m = new SessionEndMessage(id);
        return clientWebSocket.sendMessage(m);
    }

    public Future<Response> cancelHelpRequest()
    {
        CancelHelpRequestMessage m = new CancelHelpRequestMessage(id);
        return clientWebSocket.sendMessage(m);
    }

    public Message getMessageReceived()
    {
        return clientWebSocket.nextMessageFromQueue();
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }


    public List<HelpRequestDto> getHelpRequests()
    {
        return clientWebSocket.getOpenHelpRequests();
    }


}
