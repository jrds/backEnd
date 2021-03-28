package org.github.jrds.server;

import org.github.jrds.server.domain.ExecutionStatus;

import org.github.jrds.server.extensions.code.CodeExecutionInfo;
import org.github.jrds.server.messages.FailureResponse;
import org.github.jrds.server.messages.Message;
import org.github.jrds.server.messages.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
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
        Assert.assertEquals("Hello World :)" + System.getProperty("line.separator"), output);
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
        Assert.assertEquals(":)" + System.getProperty("line.separator"), output);
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
        Assert.assertEquals("Hello Standard Out" + System.getProperty("line.separator"), outputs[0]);
        Assert.assertEquals("Hello Standard Err" + System.getProperty("line.separator"), outputs[1]);
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
