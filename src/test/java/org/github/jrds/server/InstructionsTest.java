package org.github.jrds.server;

import org.github.jrds.server.domain.Lesson;
import org.github.jrds.server.domain.User;
import org.github.jrds.server.messages.*;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.fail;


public class InstructionsTest extends ApplicationTest
{
    private final String testTitle1 = "Instruction Test 1";
    private final String testBody1 = "Body of Test Instruction 1";
    private final String testTitle2 = "Instruction Test 2";
    private final String testBody2 = "Body of Test Instruction 2";
    private final String testTitle3 = "Instruction Test 3";
    private final String testBody3 = "Body of Test Instruction 3";


    @Test
    public void educatorCreatesInstructionToLesson()
    {
        connect(eduId, eduName, lesson1);
        Lesson l = server.lessonStore.getLesson(lesson1);
        User u = server.usersStore.getUser(eduId);

        l.removeAllInstructions();
        Assert.assertEquals(0, l.getAllInstructions().size());

        l.createInstruction(testTitle1, testBody1, u);

        Assert.assertFalse(l.getAllInstructions().isEmpty());
        Assert.assertNotNull(l.getInstruction(testTitle1)); // TODO - REVIEW this proves the title is as expected so don't need a separate test for this.
        Assert.assertEquals(testBody1, l.getInstruction(testTitle1).getBody());
        Assert.assertEquals(u, l.getInstruction(testTitle1).getAuthor());
    }


    @Test
    public void educatorCanCreateMultipleInstructions()
    {

        connect(eduId, eduName, lesson1);
        Lesson l = server.lessonStore.getLesson(lesson1);
        User u = server.usersStore.getUser(eduId);

        l.removeAllInstructions();
        l.createInstruction(testTitle1, testBody1, u);
        l.createInstruction(testTitle2, testBody2, u);
        l.createInstruction(testTitle3, testBody3, u);

        Assert.assertEquals(3, l.getAllInstructions().size());

        Assert.assertNotNull(l.getInstruction(testTitle1));
        Assert.assertNotNull(l.getInstruction(testTitle2));
        Assert.assertNotNull(l.getInstruction(testTitle3));

        Assert.assertEquals(testBody1, l.getAllInstructions().get(0).getBody());
        Assert.assertEquals(testBody2, l.getAllInstructions().get(1).getBody());
        Assert.assertEquals(testBody3, l.getAllInstructions().get(2).getBody());

        Assert.assertEquals(u, l.getAllInstructions().get(0).getAuthor());
        Assert.assertEquals(u, l.getAllInstructions().get(1).getAuthor());
        Assert.assertEquals(u, l.getAllInstructions().get(2).getAuthor());
    }

    //TODO - Create some test for the ordering.
    //Once removing is also a concept will need tests to check they contain the expected instruction.

    @Test
    public void educatorCanRemoveAnInstruction()
    {

        connect(eduId, eduName, lesson1);
        Lesson l = server.lessonStore.getLesson(lesson1);
        User u = server.usersStore.getUser(eduId);

        l.removeAllInstructions();
        l.createInstruction(testTitle1, testBody1, u);
        l.createInstruction(testTitle2, testBody2, u);
        l.createInstruction(testTitle3, testBody3, u);

        l.removeInstruction(testTitle2);

        Assert.assertEquals(2, l.getAllInstructions().size());

        Assert.assertNotNull(l.getInstruction(testTitle1));
        Assert.assertNotNull(l.getInstruction(testTitle3));
        Assert.assertNull(l.getInstruction(testTitle2));

        Assert.assertEquals(testBody1, l.getAllInstructions().get(0).getBody());
        Assert.assertEquals(testBody3, l.getAllInstructions().get(1).getBody());

        Assert.assertEquals(u, l.getAllInstructions().get(0).getAuthor());
        Assert.assertEquals(u, l.getAllInstructions().get(1).getAuthor());
    }


    @Test
    public void educatorCanRemoveAllInstructions()
    {
        // already proven in other tests - is it good to separate it out?
        connect(eduId, eduName, lesson1);
        Lesson l = server.lessonStore.getLesson(lesson1);
        User u = server.usersStore.getUser(eduId);

        l.removeAllInstructions();

        l.createInstruction(testTitle1, testBody1, u);
        l.createInstruction(testTitle2, testBody2, u);
        l.createInstruction(testTitle3, testBody3, u);

        Assert.assertEquals(3, l.getAllInstructions().size());

        l.removeAllInstructions();

        Assert.assertEquals(0, l.getAllInstructions().size());
    }


    @Test
    public void educatorCanEditInstructionTitle()
    {

        connect(eduId, eduName, lesson1);
        Lesson l = server.lessonStore.getLesson(lesson1);
        User u = server.usersStore.getUser(eduId);

        l.removeAllInstructions();
        l.createInstruction(testTitle1, testBody1, u);

        Assert.assertEquals(testTitle1, l.getInstruction(testTitle1).getTitle());

        l.editInstructionTitle(testTitle1, testTitle2, u);
        Assert.assertNotNull(l.getInstruction(testTitle2));
        Assert.assertNull(l.getInstruction(testTitle1));
        Assert.assertEquals(testBody1, l.getInstruction(testTitle2).getBody());
        Assert.assertEquals(u, l.getInstruction(testTitle2).getAuthor());
    }

    @Test
    public void educatorCanEditInstructionBody()
    {

        connect(eduId, eduName, lesson1);
        Lesson l = server.lessonStore.getLesson(lesson1);
        User u = server.usersStore.getUser(eduId);

        l.removeAllInstructions();
        l.createInstruction(testTitle1, testBody1, u);

        Assert.assertEquals(testBody1, l.getInstruction(testTitle1).getBody());

        l.editInstructionBody(testTitle1, testBody2, u);
        Assert.assertNotNull(l.getInstruction(testTitle1));
        Assert.assertEquals(testBody2, l.getInstruction(testTitle1).getBody());
        Assert.assertNotEquals(testBody1, l.getInstruction(testTitle1).getBody());
        Assert.assertEquals(u, l.getInstruction(testTitle1).getAuthor());
    }


    @Test
    public void studentCantCreateInstructionToLesson()
    {
        connect(l1Id, eduName, lesson1);
        Lesson l = server.lessonStore.getLesson(lesson1);
        User u = server.usersStore.getUser(l1Id);

        l.removeAllInstructions();

        try
        {
            l.createInstruction(testTitle1, testBody1, u);
            fail("Expected Illegal Argument Exception");
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertEquals("Only the educator of this lesson can add instructions", e.getMessage());
        }
    }


    @Test
    public void instructionsCantHaveTheSameTitle()
    {
        connect(eduId, eduName, lesson1);
        Lesson l = server.lessonStore.getLesson(lesson1);
        User u = server.usersStore.getUser(eduId);

        l.removeAllInstructions();

        l.createInstruction(testTitle1, testBody1, u);
        try
        {
            l.createInstruction(testTitle1, testBody2, u);
            fail("Expected illegal argument exception");
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertEquals("This title already exists", e.getMessage());
        }

    }

    @Test
    public void startLessonSendsOneInstructionToAllLearners()
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);
        TestClient c3 = connect(l2Id, l2Name, lesson1);

        User u = server.usersStore.getUser(eduId);
        Lesson l = server.lessonStore.getLesson(lesson1);

        l.removeAllInstructions();
        l.createInstruction(testTitle1, testBody1, u);

        c1.startLesson();
        Message received1 = c2.getMessageReceived();
        Message received2 = c3.getMessageReceived();

        Assert.assertTrue(received1 instanceof InstructionMessage);
        Assert.assertTrue(received2 instanceof InstructionMessage);

        Assert.assertEquals(testTitle1, ((InstructionMessage) received1).getInstruction().getTitle());
        Assert.assertEquals(testBody1, ((InstructionMessage) received1).getInstruction().getBody());
        Assert.assertEquals(testTitle1, ((InstructionMessage) received2).getInstruction().getTitle());
        Assert.assertEquals(testBody1, ((InstructionMessage) received2).getInstruction().getBody());
    }


    @Test
    public void startLessonSendsMultipleInstructionsToAllLearners()
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);
        TestClient c3 = connect(l2Id, l2Name, lesson1);

        User u = server.usersStore.getUser(eduId);
        Lesson l = server.lessonStore.getLesson(lesson1);

        l.removeAllInstructions();
        l.createInstruction(testTitle1, testBody1, u);
        l.createInstruction(testTitle2, testBody2, u);

        c1.startLesson();
        Message received1 = c2.getMessageReceived();
        Message received2 = c2.getMessageReceived();

        Message received3 = c3.getMessageReceived();
        Message received4 = c3.getMessageReceived();

        Assert.assertEquals(testTitle1, ((InstructionMessage) received1).getInstruction().getTitle());
        Assert.assertEquals(testBody1, ((InstructionMessage) received1).getInstruction().getBody());
        Assert.assertEquals(testTitle1, ((InstructionMessage) received3).getInstruction().getTitle());
        Assert.assertEquals(testBody1, ((InstructionMessage) received3).getInstruction().getBody());

        Assert.assertEquals(testTitle2, ((InstructionMessage) received2).getInstruction().getTitle());
        Assert.assertEquals(testBody2, ((InstructionMessage) received2).getInstruction().getBody());
        Assert.assertEquals(testTitle2, ((InstructionMessage) received4).getInstruction().getTitle());
        Assert.assertEquals(testBody2, ((InstructionMessage) received4).getInstruction().getBody());
    }

    @Test
    public void learnerNotInClassDoesntReceiveInstructions()
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);
        TestClient c3 = connect(l99Id, l99Name, lesson2);

        User u = server.usersStore.getUser(eduId);
        Lesson l = server.lessonStore.getLesson(lesson1);

        l.removeAllInstructions();
        l.createInstruction(testTitle1, testBody1, u);

        c1.startLesson();

        Assert.assertNotNull(c2.getMessageReceived());
        Assert.assertNull(c3.getMessageReceived());
    }

    //TODO how does the educator see the instructions, he's able to access, edit and create, but isn't sent the instruction message

    @Test
    public void studentCannotStartLesson() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);

        User u = server.usersStore.getUser(eduId);
        Lesson l = server.lessonStore.getLesson(lesson1);

        l.removeAllInstructions();
        l.createInstruction(testTitle1, testBody1, u);

        Future<Response> startLessonFuture = c2.startLesson();
        Response startLessonResponse = startLessonFuture.get(10, TimeUnit.SECONDS);

        Assert.assertTrue(startLessonResponse.isFailure());
        Assert.assertEquals("Learner cannot start a lesson", startLessonResponse.asFailure().getFailureReason());
    }

    @Test
    @Ignore
    public void instructionsArePresentAfterEducatorDisconnectsAndReconnects()
    {
        // some sort of permanence needs to be tested, so the next time we come to that lesson, the lesson still has the instructions
        // potentially add to the set up/initialisation ? // Needs more thought. 
        fail();
    }


}
