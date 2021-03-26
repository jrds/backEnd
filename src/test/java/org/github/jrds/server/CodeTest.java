package org.github.jrds.server;

import org.github.jrds.server.domain.Attendance;
import org.github.jrds.server.domain.CompilationStatus;

import org.github.jrds.server.extensions.code.CompiledCodeMessage;
import org.github.jrds.server.messages.FailureMessage;
import org.github.jrds.server.messages.Message;
import org.github.jrds.server.messages.Response;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CodeTest extends ApplicationTest
{

    @Test
    public void LearnersCodeCompiles()
    {
        TestClient c1 = connect(l1Id, l1Name, lesson1);

        String learnersCode = "class Hello { public static void main(String[] args) { System.out.println(\"Hello World :)\"); } }";

        c1.compileCode(learnersCode);

        Message m = c1.getMessageReceived();
        Assert.assertTrue(m instanceof CompiledCodeMessage);

        CompiledCodeMessage compiledCodeMessage = ((CompiledCodeMessage)m);

        Assert.assertEquals(CompilationStatus.COMPILED_SUCCESSFULLY.toString(), compiledCodeMessage.getCompilationStatus());
        Assert.assertEquals("Hello World :)",compiledCodeMessage.getCompilationResult());

    }


    @Test
    public void CorrectCodeIsCompiledAfterFirstCompile()
    {
        TestClient c1 = connect(l1Id, l1Name, lesson1);

        String learnersCode = "class Hello { public static void main(String[] args) { System.out.println(\"Hello World\"); } }";
        String learnersCode2 = "class Smile { public static void main(String[] args) { System.out.println(\":)\"); } }";

        c1.compileCode(learnersCode);
        c1.getMessageReceived();

        c1.compileCode(learnersCode2);
        Message m = c1.getMessageReceived();

        Assert.assertTrue(m instanceof CompiledCodeMessage);

        CompiledCodeMessage compiledCodeMessage = ((CompiledCodeMessage)m);
        Assert.assertEquals(CompilationStatus.COMPILED_SUCCESSFULLY.toString(), compiledCodeMessage.getCompilationStatus());
        Assert.assertEquals(":)",compiledCodeMessage.getCompilationResult());
    }

    @Test
    public void allCompiledCodesAreCapturedInTheCorrectOrder(){

        TestClient c1 = connect(l1Id, l1Name, lesson1);

        String learnersCode = "class Hello { public static void main(String[] args) { System.out.println(\"Hello World\"); } }";
        String learnersCode2 = "class Smile { public static void main(String[] args) { System.out.println(\":)\"); } }";
        String learnersCode3 = "class Bye { public static void main(String[] args) { System.out.println(\"Goodbye\"); } }";

        c1.compileCode(learnersCode);
        CompiledCodeMessage m1 = (CompiledCodeMessage) c1.getMessageReceived();
        Instant m1TimeCompiled = Instant.parse(m1.getTimeCompiled());

        c1.compileCode(learnersCode2);
        CompiledCodeMessage m2 = (CompiledCodeMessage) c1.getMessageReceived();
        Instant m2TimeCompiled = Instant.parse(m2.getTimeCompiled());

        c1.compileCode(learnersCode3);
        CompiledCodeMessage m3 = (CompiledCodeMessage) c1.getMessageReceived();
        Instant m3TimeCompiled = Instant.parse(m3.getTimeCompiled());

        Assert.assertTrue((m2TimeCompiled.getEpochSecond() - m1TimeCompiled.getEpochSecond()) > 0 );
        Assert.assertTrue((m3TimeCompiled.getEpochSecond() - m2TimeCompiled.getEpochSecond()) > 0 );


        Assert.assertEquals("Hello World",m1.getCompilationResult());
        Assert.assertEquals(":)",m2.getCompilationResult());
        Assert.assertEquals("Goodbye",m3.getCompilationResult());
    }


    // FIX THIS
    @Test
    public void invalidClassDefinitionsThrowException() throws ExecutionException, InterruptedException, TimeoutException
    {

        TestClient c1 = connect(l1Id, l1Name, lesson1);

        String learnersCode = "class Hello ( public static void main(String[] args) { System.out.println(\"Hello World :)\"); } )";


        Response response = c1.compileCode(learnersCode).get(10, TimeUnit.SECONDS);

        Assert.assertTrue(response instanceof FailureMessage);
        Assert.assertEquals("Not a valid class definition", ((FailureMessage) response).getFailureReason());

        Assert.assertNull(c1.getMessageReceived());
    }
}
