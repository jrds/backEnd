package org.github.jrds.codi.messaging.code;

import org.github.jrds.codi.core.domain.ExecutionStatus;
import org.github.jrds.codi.core.messages.FailureResponse;
import org.github.jrds.codi.core.messages.Message;
import org.github.jrds.codi.core.messages.Response;
import org.github.jrds.codi.server.testing.ApplicationTest;
import org.github.jrds.codi.server.testing.ClientWebSocket;
import org.github.jrds.codi.server.testing.TestClient;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractCodeTest extends ApplicationTest
{
    @BeforeClass
    public static void registerMessageSubtypes()
    {
        ClientWebSocket.registerMessageSubtype(ExecuteCodeRequest.class);
        ClientWebSocket.registerMessageSubtype(CodeExecutionInfo.class);
        ClientWebSocket.registerMessageSubtype(LatestLearnerCodeInfo.class);
    }

    @Test
    public abstract void learnersCodeCompilesAndExecutesToImmediateEnd();

    @Test
    public abstract void newCodeIsProperlyExecuted();

    @Test
    public abstract void errorOutputIsCaptured();

    @Test
    public abstract void processInputCanBeProvided();

    @Test
    public abstract void aLongRunningProcessCanBeTerminated();

    @Test
    public abstract void badSyntaxFails() throws Exception;

    @Test
    public abstract void liveCodeUpdatesAreSentToEducator() throws Exception;

    protected String[] waitForExecutionToComplete(TestClient c1)
    {
        StringBuilder stdOut = new StringBuilder();
        StringBuilder stdErr = new StringBuilder();
        boolean isFinished;
        do
        {
            CodeExecutionInfo m = (CodeExecutionInfo) c1.getMessageReceived();
            stdOut.append(m.getExecutionOutput());
            stdErr.append(m.getExecutionErrorOutput());
            isFinished = m.getExecutionStatus().equals(ExecutionStatus.EXECUTION_FINISHED.toString());
        }
        while (!isFinished);
        return new String[] {stdOut.toString(), stdErr.toString()};
    }

    protected Future<Response> executeCode(TestClient client, String code)
    {
        ExecuteCodeRequest request = new ExecuteCodeRequest(client.getId(), code);
        return client.sendRequest(request);
    }

    protected Future<Response> terminateCode(TestClient client)
    {
        TerminateExecutionRequest request = new TerminateExecutionRequest(client.getId());
        return client.sendRequest(request);
    }

    protected Future<Response> sendCodeExecutionInput(TestClient client, String input)
    {
        CodeExecutionInputRequest request = new CodeExecutionInputRequest(client.getId(), input);
        return client.sendRequest(request);
    }

    protected Future<Response> sendLiveCodeToEducatorRequest(TestClient client, String learnersCode)
    {
        UpdateLiveCodeRequest request = new UpdateLiveCodeRequest(client.getId(), learnersCode);
        return client.sendRequest(request);
    }

}
