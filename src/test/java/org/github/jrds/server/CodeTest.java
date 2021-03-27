package org.github.jrds.server;

import org.github.jrds.server.domain.ExecutionStatus;

import org.github.jrds.server.extensions.code.CompiledCodeMessage;
import org.github.jrds.server.messages.FailureMessage;
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

        Message m = c1.getMessageReceived();
        Assert.assertTrue(m instanceof CompiledCodeMessage);

        CompiledCodeMessage compiledCodeMessage = ((CompiledCodeMessage)m);

        Assert.assertEquals(ExecutionStatus.COMPILE_SUCCEEDED.toString(), compiledCodeMessage.getCompilationStatus());
        Assert.assertEquals("Hello World :)",compiledCodeMessage.getCompilationResult());

    }


    @Test
    public void newCodeIsProperlyExecuted()
    {
        TestClient c1 = connect(l1Id, l1Name, lesson1);

        String originalCode = "class Hello { public static void main(String[] args) { System.out.println(\"Hello World\"); } }";
        String newCode = "class Smile { public static void main(String[] args) { System.out.println(\":)\"); } }";

        c1.executeCode(originalCode);
        c1.getMessageReceived();

        c1.executeCode(newCode);
        Message m = c1.getMessageReceived();

        Assert.assertTrue(m instanceof CompiledCodeMessage);

        CompiledCodeMessage compiledCodeMessage = ((CompiledCodeMessage)m);
        Assert.assertEquals(ExecutionStatus.COMPILE_SUCCEEDED.toString(), compiledCodeMessage.getCompilationStatus());
        Assert.assertEquals(":)",compiledCodeMessage.getCompilationResult());
    }

    @Test
    public void invalidClassDefinitionFails() throws ExecutionException, InterruptedException, TimeoutException
    {
        TestClient c1 = connect(l1Id, l1Name, lesson1);

        String learnersCode = "class Hello ( public static void main(String[] args) { System.out.println(\"Hello World :)\"); } )";

        Response response = c1.executeCode(learnersCode).get(10, TimeUnit.SECONDS);

        Assert.assertTrue(response instanceof FailureMessage);
        Assert.assertEquals("Not a valid class definition", ((FailureMessage) response).getFailureReason());
    }

    @Test
    public void badSyntaxFails() throws ExecutionException, InterruptedException, TimeoutException
    {
        TestClient c1 = connect(l1Id, l1Name, lesson1);

        String learnersCode = "class Hello { public static void main(String[] args) { System.out.println(\"Hello World\"); }";

        Response response = c1.executeCode(learnersCode).get(10, TimeUnit.SECONDS);
        Message m = c1.getMessageReceived();

        Assert.assertTrue(m instanceof CompiledCodeMessage);

        CompiledCodeMessage compiledCodeMessage = ((CompiledCodeMessage)m);
        Assert.assertEquals(ExecutionStatus.COMPILE_FAILED.toString(), compiledCodeMessage.getCompilationStatus());
        Assert.assertEquals("Hello.java:1: error: reached end of file while parsing\n" +
                "class Hello { public static void main(String[] args) { System.out.println(\"Hello World\"); }\n" +
                "                                                                                           ^\n" +
                "1 error",compiledCodeMessage.getCompilationResult());
    }
}
