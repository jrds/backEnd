package org.github.jrds.server;

import org.github.jrds.server.domain.Attendance;
import org.github.jrds.server.domain.CompilationStatus;

import org.github.jrds.server.extensions.code.CompiledCodeMessage;
import org.github.jrds.server.messages.Message;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class CodeTest extends ApplicationTest
{

    @Test
    public void LearnersCodeCompiles()
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);

        String learnersCode = "class Hello { public static void main(String[] args) { System.out.println(\"Hello World :)\"); } }";

        c2.compileCode(learnersCode);

        Message m = c2.getMessageReceived();
        Assert.assertTrue(m instanceof CompiledCodeMessage);

        CompiledCodeMessage compiledCodeMessage = ((CompiledCodeMessage)m);

        Assert.assertEquals(CompilationStatus.COMPILED_SUCCESSFULLY.toString(), compiledCodeMessage.getCompilationStatus());
        Assert.assertEquals("Hello World :)",compiledCodeMessage.getCompilationResult());

    }


    @Test
    public void CorrectCodeIsCompiledAfterFirstCompile()
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);

        String learnersCode = "class Hello { public static void main(String[] args) { System.out.println(\"Hello World\"); } }";
        String learnersCode2 = "class Smile { public static void main(String[] args) { System.out.println(\":)\"); } }";


        c2.compileCode(learnersCode);
        c2.getMessageReceived();

        c2.compileCode(learnersCode2);
        Message m = c2.getMessageReceived();

        Assert.assertTrue(m instanceof CompiledCodeMessage);

        CompiledCodeMessage compiledCodeMessage = ((CompiledCodeMessage)m);
        Assert.assertEquals(CompilationStatus.COMPILED_SUCCESSFULLY.toString(), compiledCodeMessage.getCompilationStatus());
        Assert.assertEquals(":)",compiledCodeMessage.getCompilationResult());
    }
}
