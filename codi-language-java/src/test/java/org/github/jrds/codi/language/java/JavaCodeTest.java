package org.github.jrds.codi.language.java;

import org.github.jrds.codi.core.domain.ExecutionStatus;
import org.github.jrds.codi.core.messages.FailureResponse;
import org.github.jrds.codi.core.messages.Message;
import org.github.jrds.codi.core.messages.Response;
import org.github.jrds.codi.messaging.code.*;
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

public class JavaCodeTest extends AbstractCodeTest
{
    @Override
    public void learnersCodeCompilesAndExecutesToImmediateEnd()
    {
        TestClient c1 = connect(l1Id, lesson1);

        String learnersCode = "class Hello { public static void main(String[] args) { System.out.println(\"Hello World :)\"); } }";

        executeCode(c1, learnersCode);
        String output = waitForExecutionToComplete(c1)[0];
        Assert.assertEquals("Hello World :)\n", output);
    }

    @Override
    public void newCodeIsProperlyExecuted()
    {
        TestClient c1 = connect(l1Id, lesson1);

        String originalCode = "class Hello { public static void main(String[] args) { System.out.println(\"Hello World\"); } }";
        String newCode = "class Smile { public static void main(String[] args) { System.out.println(\":)\"); } }";

        executeCode(c1, originalCode);
        waitForExecutionToComplete(c1);

        executeCode(c1, newCode);
        String output = waitForExecutionToComplete(c1)[0];
        Assert.assertEquals(":)\n", output);
    }

    @Override
    public void errorOutputIsCaptured()
    {
        TestClient c1 = connect(l1Id, lesson1);

        String code = "class Hello {\n" +
                "    public static void main(String[] args) {\n" +
                "       System.out.println(\"Hello Standard Out\");\n" +
                "       System.err.println(\"Hello Standard Err\");\n" +
                "    }\n" +
                "}";

        executeCode(c1, code);
        String[] outputs = waitForExecutionToComplete(c1);
        Assert.assertEquals("Hello Standard Out\n", outputs[0]);
        Assert.assertEquals("Hello Standard Err\n", outputs[1]);
    }

    @Override
    public void processInputCanBeProvided()
    {
        TestClient c1 = connect(l1Id, lesson1);

        String code = "import java.util.Scanner;\n" +
                "\n" +
                "class Hello {\n" +
                "    public static void main(String[] args) {\n" +
                "       Scanner sc = new Scanner(System.in);\n" +
                "       String input;\n" +
                "       do\n" +
                "       {\n" +
                "           System.out.print(\"Enter name or quit: \");\n" +
                "           input = sc.nextLine();\n" +
                "           if (!input.equals(\"quit\"))\n" +
                "           {\n" +
                "               System.out.println(\"Hello \" + input);\n" +
                "           }\n" +
                "       }\n" +
                "       while (!input.equals(\"quit\"));\n" +
                "    }\n" +
                "}";

        executeCode(c1, code);

        StringBuilder output = new StringBuilder();
        do
        {
            CodeExecutionInfo m = (CodeExecutionInfo) c1.getMessageReceived();
            output.append(m.getExecutionOutput());
        } while (!output.toString().equals("Enter name or quit: "));

        sendCodeExecutionInput(c1, "Jordan\n");
        output = new StringBuilder();
        do
        {
            CodeExecutionInfo m = (CodeExecutionInfo) c1.getMessageReceived();
            output.append(m.getExecutionOutput());
        } while (!output.toString().equals("Hello Jordan\nEnter name or quit: "));

        sendCodeExecutionInput(c1,"Jack\n");
        output = new StringBuilder();
        do
        {
            CodeExecutionInfo m = (CodeExecutionInfo) c1.getMessageReceived();
            output.append(m.getExecutionOutput());
        } while (!output.toString().equals("Hello Jack\nEnter name or quit: "));

        sendCodeExecutionInput(c1,"quit\n");
        waitForExecutionToComplete(c1);
    }

    @Override
    public void aLongRunningProcessCanBeTerminated()
    {
        TestClient c1 = connect(l1Id, lesson1);

        String code = "class Hello { public static void main(String[] args) { while (true) { System.out.println(\"Hello World\"); } } }";

        String status;

        executeCode(c1, code);
        for (int i=0; i<5; i++)
        {
            CodeExecutionInfo m = (CodeExecutionInfo) c1.getMessageReceived();
            status = m.getExecutionStatus();
            Assert.assertEquals(ExecutionStatus.EXECUTION_IN_PROGRESS.toString(), status);
        }
        terminateCode(c1);

        long t0 = System.currentTimeMillis();
        do
        {
            CodeExecutionInfo m = (CodeExecutionInfo) c1.getMessageReceived();
            status = m.getExecutionStatus();
        }
        while ((System.currentTimeMillis() - t0) < 10000 && !status.equals(ExecutionStatus.EXECUTION_FINISHED.toString()));

        Assert.assertEquals(ExecutionStatus.EXECUTION_FINISHED.toString(), status);
    }

    @Test
    public void invalidClassDefinitionFails() throws ExecutionException, InterruptedException, TimeoutException
    {
        TestClient c1 = connect(l1Id, lesson1);

        String learnersCode = "class Hello ( public static void main(String[] args) { System.out.println(\"Hello World :)\"); } )";

        Response response = executeCode(c1,learnersCode).get(10, TimeUnit.SECONDS);

        Assert.assertTrue(response instanceof FailureResponse);
        Assert.assertEquals("Not a valid class definition", ((FailureResponse) response).getFailureReason());
    }

    @Override
    public void badSyntaxFails() throws ExecutionException, InterruptedException, TimeoutException
    {
        TestClient c1 = connect(l1Id, lesson1);

        String learnersCode = "class Hello { public static void main(String[] args) { System.out.println(\"Hello World\"); }";

        executeCode(c1, learnersCode).get(10, TimeUnit.SECONDS);
        Message m = c1.getMessageReceived();

        Assert.assertTrue(m instanceof CodeExecutionInfo);

        CodeExecutionInfo codeExecutionInfo = ((CodeExecutionInfo)m);
        Assert.assertEquals(ExecutionStatus.COMPILE_FAILED.toString(), codeExecutionInfo.getExecutionStatus());
        Assert.assertEquals("Hello.java:1: error: reached end of file while parsing\n" +
                "class Hello { public static void main(String[] args) { System.out.println(\"Hello World\"); }\n" +
                "                                                                                           ^\n" +
                "1 error", codeExecutionInfo.getExecutionErrorOutput());
    }

    @Override
    public void liveCodeUpdatesAreSentToEducator() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, lesson1);
        TestClient c2 = connect(l1Id, lesson1);

        String learnersCode = "class Hello { public static void main(String[] args) { System.out.println(\"Hello World\"); }";

        Future<Response> sentMessageFuture = sendLiveCodeToEducatorRequest(c2, learnersCode);
        Response sentMessageResponse = sentMessageFuture.get(10, TimeUnit.SECONDS);

        Assert.assertTrue(sentMessageResponse.isSuccess());

        Message received = c1.getMessageReceived(LatestLearnerCodeInfo.class);

        Assert.assertTrue(received instanceof LatestLearnerCodeInfo);
        System.out.println(received);
    }
}
