package org.github.jrds.server;

import org.github.jrds.server.domain.ExecutionStatus;

import org.github.jrds.server.extensions.chat.ChatMessage;
import org.github.jrds.server.extensions.code.CodeExecutionInfo;
import org.github.jrds.server.extensions.code.LatestLearnerCodeInfo;
import org.github.jrds.server.messages.FailureResponse;
import org.github.jrds.server.messages.Message;
import org.github.jrds.server.messages.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CodeTest extends ApplicationTest
{
    @Test
    public void learnersCodeCompilesAndExecutesToImmediateEnd()
    {
        TestClient c1 = connect(l1Id, l1Name, lesson1);

        String learnersCode = "class Hello { public static void main(String[] args) { System.out.println(\"Hello World :)\"); } }";

        c1.executeCode(learnersCode);
        String output = waitForExecutionToComplete(c1)[0];
        Assert.assertEquals("Hello World :)\n", output);
    }

    @Test
    public void newCodeIsProperlyExecuted()
    {
        TestClient c1 = connect(l1Id, l1Name, lesson1);

        String originalCode = "class Hello { public static void main(String[] args) { System.out.println(\"Hello World\"); } }";
        String newCode = "class Smile { public static void main(String[] args) { System.out.println(\":)\"); } }";

        c1.executeCode(originalCode);
        waitForExecutionToComplete(c1);

        c1.executeCode(newCode);
        String output = waitForExecutionToComplete(c1)[0];
        Assert.assertEquals(":)\n", output);
    }

    @Test
    public void errorOutputIsCaptured()
    {
        TestClient c1 = connect(l1Id, l1Name, lesson1);

        String code = "class Hello {\n" +
                "    public static void main(String[] args) {\n" +
                "       System.out.println(\"Hello Standard Out\");\n" +
                "       System.err.println(\"Hello Standard Err\");\n" +
                "    }\n" +
                "}";

        c1.executeCode(code);
        String[] outputs = waitForExecutionToComplete(c1);
        Assert.assertEquals("Hello Standard Out\n", outputs[0]);
        Assert.assertEquals("Hello Standard Err\n", outputs[1]);
    }

    @Test
    public void processInputCanBeProvided()
    {
        TestClient c1 = connect(l1Id, l1Name, lesson1);

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

        c1.executeCode(code);

        StringBuilder output = new StringBuilder();
        do
        {
            CodeExecutionInfo m = (CodeExecutionInfo) c1.getMessageReceived();
            output.append(m.getExecutionOutput());
        } while (!output.toString().equals("Enter name or quit: "));

        c1.sendCodeExecutionInput("Jordan\n");
        output = new StringBuilder();
        do
        {
            CodeExecutionInfo m = (CodeExecutionInfo) c1.getMessageReceived();
            output.append(m.getExecutionOutput());
        } while (!output.toString().equals("Hello Jordan\nEnter name or quit: "));

        c1.sendCodeExecutionInput("Jack\n");
        output = new StringBuilder();
        do
        {
            CodeExecutionInfo m = (CodeExecutionInfo) c1.getMessageReceived();
            output.append(m.getExecutionOutput());
        } while (!output.toString().equals("Hello Jack\nEnter name or quit: "));

        c1.sendCodeExecutionInput("quit\n");
        waitForExecutionToComplete(c1);
    }

    @Test
    public void aLongRunningProcessCanBeTerminated()
    {
        TestClient c1 = connect(l1Id, l1Name, lesson1);

        String code = "class Hello { public static void main(String[] args) { while (true) { System.out.println(\"Hello World\"); } } }";

        String status;

        c1.executeCode(code);
        for (int i=0; i<5; i++)
        {
            CodeExecutionInfo m = (CodeExecutionInfo) c1.getMessageReceived();
            status = m.getExecutionStatus();
            Assert.assertEquals(ExecutionStatus.EXECUTION_IN_PROGRESS.toString(), status);
        }
        c1.terminateCode();

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
        TestClient c1 = connect(l1Id, l1Name, lesson1);

        String learnersCode = "class Hello ( public static void main(String[] args) { System.out.println(\"Hello World :)\"); } )";

        Response response = c1.executeCode(learnersCode).get(10, TimeUnit.SECONDS);

        Assert.assertTrue(response instanceof FailureResponse);
        Assert.assertEquals("Not a valid class definition", ((FailureResponse) response).getFailureReason());
    }

    @Test
    public void badSyntaxFails() throws ExecutionException, InterruptedException, TimeoutException
    {
        TestClient c1 = connect(l1Id, l1Name, lesson1);

        String learnersCode = "class Hello { public static void main(String[] args) { System.out.println(\"Hello World\"); }";

        Response response = c1.executeCode(learnersCode).get(10, TimeUnit.SECONDS);
        Message m = c1.getMessageReceived();

        Assert.assertTrue(m instanceof CodeExecutionInfo);

        CodeExecutionInfo codeExecutionInfo = ((CodeExecutionInfo)m);
        Assert.assertEquals(ExecutionStatus.COMPILE_FAILED.toString(), codeExecutionInfo.getExecutionStatus());
        Assert.assertEquals("Hello.java:1: error: reached end of file while parsing\n" +
                "class Hello { public static void main(String[] args) { System.out.println(\"Hello World\"); }\n" +
                "                                                                                           ^\n" +
                "1 error", codeExecutionInfo.getExecutionErrorOutput());
    }

    @Test
    public void liveCodeUpdatesAreSentToEducator() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);

        String learnersCode = "class Hello { public static void main(String[] args) { System.out.println(\"Hello World\"); }";

        Future<Response> sentMessageFuture = c2.sendLiveCodeToEducatorRequest(learnersCode);
        Response sentMessageResponse = sentMessageFuture.get(10, TimeUnit.SECONDS);

        Assert.assertTrue(sentMessageResponse.isSuccess());

        Message received = c1.getMessageReceived();

        Assert.assertTrue(received instanceof LatestLearnerCodeInfo);
        System.out.println(received);
    }

    private String[] waitForExecutionToComplete(TestClient c1)
    {
        StringBuilder stdOut = new StringBuilder();
        StringBuilder stdErr = new StringBuilder();
        boolean isFinished = false;
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


}
