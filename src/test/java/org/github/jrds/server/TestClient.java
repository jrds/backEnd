package org.github.jrds.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.github.jrds.server.domain.HelpRequest;
import org.github.jrds.server.domain.Status;
import org.github.jrds.server.dto.HelpRequestDto;
import org.github.jrds.server.extensions.chat.ChatMessage;
import org.github.jrds.server.extensions.code.CodeExecutionInputRequest;
import org.github.jrds.server.extensions.code.ExecuteCodeRequest;
import org.github.jrds.server.extensions.code.TerminateExecutionRequest;
import org.github.jrds.server.extensions.help.CancelHelpRequest;
import org.github.jrds.server.extensions.help.NewHelpRequest;
import org.github.jrds.server.extensions.help.UpdateHelpStatusRequest;
import org.github.jrds.server.extensions.lesson.LessonStartRequest;
import org.github.jrds.server.messages.SessionEndRequest;
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

    public Response getSessionStartResponse()
    {
        return clientWebSocket.getSessionStartResponse();
    }

    public Future<Response> sendChatMessage(String msg, String to)
    {
        ChatMessage m = new ChatMessage(id, to, msg);
        return clientWebSocket.sendMessage(m);
    }

    public Future<Response> requestHelp()
    {
        NewHelpRequest m = new NewHelpRequest(id);
        return clientWebSocket.sendMessage(m);
    }

    public Future<Response> startLesson()
    {
        LessonStartRequest m = new LessonStartRequest(id);
        return clientWebSocket.sendMessage(m);
    }

    public Future<Response> executeCode(String code)
    {
        ExecuteCodeRequest m = new ExecuteCodeRequest(id, code);
        return clientWebSocket.sendMessage(m);
    }

    public Future<Response> terminateCode()
    {
        TerminateExecutionRequest m = new TerminateExecutionRequest(id);
        return clientWebSocket.sendMessage(m);
    }

    public Future<Response> sendCodeExecutionInput(String input)
    {
        CodeExecutionInputRequest m = new CodeExecutionInputRequest(id, input);
        return clientWebSocket.sendMessage(m);
    }

    public Future<Response> sendSessionEndMessage()
    {
        SessionEndRequest m = new SessionEndRequest(id);
        return clientWebSocket.sendMessage(m);
    }

    public Future<Response> cancelHelpRequest()
    {
        CancelHelpRequest m = new CancelHelpRequest(id);
        return clientWebSocket.sendMessage(m);
    }

    public Future<Response> updateHelpRequest(HelpRequest helpRequestToUpdate, Status newStatus)
    {
        UpdateHelpStatusRequest m = new UpdateHelpStatusRequest(helpRequestToUpdate.getLearner().getId(), helpRequestToUpdate.getLearner().getId(), newStatus);
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


    public List<HelpRequest> getOpenHelpRequests(String lessonId)
    {
        return new ArrayList<>(Main.defaultInstance.activeLessonStore.getActiveLesson(lessonId).getOpenHelpRequests().values());
    }
}
