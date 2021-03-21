package org.github.jrds.server;

import org.github.jrds.server.domain.Instruction;
import org.github.jrds.server.domain.LessonStructure;
import org.github.jrds.server.domain.User;
import org.github.jrds.server.extensions.lesson.ActiveLessonState;
import org.github.jrds.server.extensions.lesson.InstructionMessage;
import org.github.jrds.server.messages.*;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

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
        LessonStructure l = server.lessonStructureStore.getLessonStructure(lesson1);
        User u = server.usersStore.getUser(eduId);

        Assert.assertEquals(0, l.getAllInstructions().size());

        Instruction instruction = l.createInstruction(testTitle1, testBody1, u);
        Assert.assertEquals(testTitle1, instruction.getTitle());
        Assert.assertEquals(testBody1, instruction.getBody());
        Assert.assertEquals(u, instruction.getAuthor());

        Assert.assertEquals(1, l.getAllInstructions().size());
        Assert.assertNotNull(l.getInstruction(instruction.getId()));
    }


    @Test
    public void educatorCanCreateMultipleInstructions()
    {
        connect(eduId, eduName, lesson1);
        LessonStructure l = server.lessonStructureStore.getLessonStructure(lesson1);
        User u = server.usersStore.getUser(eduId);

        Instruction i1 = l.createInstruction(testTitle1, testBody1, u);
        Instruction i2 = l.createInstruction(testTitle2, testBody2, u);
        Instruction i3 = l.createInstruction(testTitle3, testBody3, u);

        Assert.assertEquals(3, l.getAllInstructions().size());

        Assert.assertNotNull(l.getInstruction(i1.getId()));
        Assert.assertNotNull(l.getInstruction(i2.getId()));
        Assert.assertNotNull(l.getInstruction(i3.getId()));

        Assert.assertEquals(testBody1, l.getAllInstructions().get(0).getBody());
        Assert.assertEquals(testBody2, l.getAllInstructions().get(1).getBody());
        Assert.assertEquals(testBody3, l.getAllInstructions().get(2).getBody());
    }

    @Test
    public void canReorderInstructions()
    {
        connect(eduId, eduName, lesson1);
        LessonStructure l = server.lessonStructureStore.getLessonStructure(lesson1);
        User u = server.usersStore.getUser(eduId);

        Instruction i1 = l.createInstruction(testTitle1, testBody1, u);
        Instruction i2 = l.createInstruction(testTitle2, testBody2, u);
        Instruction i3 = l.createInstruction(testTitle3, testBody3, u);

        i2.moveDown();
        Assert.assertEquals(testBody1, l.getAllInstructions().get(0).getBody());
        Assert.assertEquals(testBody3, l.getAllInstructions().get(1).getBody());
        Assert.assertEquals(testBody2, l.getAllInstructions().get(2).getBody());

        i3.moveUp();
        Assert.assertEquals(testBody3, l.getAllInstructions().get(0).getBody());
        Assert.assertEquals(testBody1, l.getAllInstructions().get(1).getBody());
        Assert.assertEquals(testBody2, l.getAllInstructions().get(2).getBody());

        i3.moveUp();
        Assert.assertEquals(testBody3, l.getAllInstructions().get(0).getBody());
        Assert.assertEquals(testBody1, l.getAllInstructions().get(1).getBody());
        Assert.assertEquals(testBody2, l.getAllInstructions().get(2).getBody());

        i2.moveDown();
        Assert.assertEquals(testBody3, l.getAllInstructions().get(0).getBody());
        Assert.assertEquals(testBody1, l.getAllInstructions().get(1).getBody());
        Assert.assertEquals(testBody2, l.getAllInstructions().get(2).getBody());
    }

    @Test
    public void educatorCanRemoveAnInstruction()
    {
        connect(eduId, eduName, lesson1);
        LessonStructure l = server.lessonStructureStore.getLessonStructure(lesson1);
        User u = server.usersStore.getUser(eduId);

        Instruction i1 = l.createInstruction(testTitle1, testBody1, u);
        Instruction i2 = l.createInstruction(testTitle2, testBody2, u);
        Instruction i3 = l.createInstruction(testTitle3, testBody3, u);

        l.removeInstruction(i2);

        Assert.assertEquals(2, l.getAllInstructions().size());

        Assert.assertNotNull(l.getInstruction(i1.getId()));
        Assert.assertNotNull(l.getInstruction(i3.getId()));
        Assert.assertNull(l.getInstruction(i2.getId()));

        Assert.assertEquals(testBody1, l.getAllInstructions().get(0).getBody());
        Assert.assertEquals(testBody3, l.getAllInstructions().get(1).getBody());
    }


    @Test
    public void educatorCanRemoveAllInstructions()
    {
        connect(eduId, eduName, lesson1);
        LessonStructure l = server.lessonStructureStore.getLessonStructure(lesson1);
        User u = server.usersStore.getUser(eduId);

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
        LessonStructure l = server.lessonStructureStore.getLessonStructure(lesson1);
        User u = server.usersStore.getUser(eduId);

        Instruction instruction = l.createInstruction(testTitle1, testBody1, u);

        instruction.setTitle(testTitle2);

        Instruction refetch = l.getInstruction(instruction.getId());
        Assert.assertEquals(testTitle2, refetch.getTitle());
        Assert.assertEquals(testBody1, refetch.getBody());
    }

    @Test
    public void educatorCanEditInstructionBody()
    {
        connect(eduId, eduName, lesson1);
        LessonStructure l = server.lessonStructureStore.getLessonStructure(lesson1);
        User u = server.usersStore.getUser(eduId);

        Instruction instruction = l.createInstruction(testTitle1, testBody1, u);

        instruction.setBody(testBody2);

        Instruction refetch = l.getInstruction(instruction.getId());
        Assert.assertEquals(testTitle1, refetch.getTitle());
        Assert.assertEquals(testBody2, refetch.getBody());
    }

    @Test
    public void studentCantCreateInstructionToLesson()
    {
        connect(l1Id, eduName, lesson1);
        LessonStructure l = server.lessonStructureStore.getLessonStructure(lesson1);
        User u = server.usersStore.getUser(l1Id);

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
    public void startLessonSendsOneInstructionToAllLearners()
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);
        TestClient c3 = connect(l2Id, l2Name, lesson1);

        User u = server.usersStore.getUser(eduId);
        LessonStructure l = server.lessonStructureStore.getLessonStructure(lesson1);

        l.createInstruction(testTitle1, testBody1, u);

        Assert.assertEquals(ActiveLessonState.NOT_STARTED, Main.defaultInstance.activeLessonStore.getActiveLesson(lesson1).getActiveLessonState());
        c1.startLesson();

        c1.getMessageReceived();
        Assert.assertEquals(ActiveLessonState.IN_PROGRESS, Main.defaultInstance.activeLessonStore.getActiveLesson(lesson1).getActiveLessonState());

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
        LessonStructure l = server.lessonStructureStore.getLessonStructure(lesson1);

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
        LessonStructure l = server.lessonStructureStore.getLessonStructure(lesson1);

        l.createInstruction(testTitle1, testBody1, u);

        c1.startLesson();
        c1.getMessageReceived();

        Assert.assertNotNull(c2.getMessageReceived());
        Assert.assertEquals(0, server.getMessageStats().forUser(l99Id).getSent());
    }

    //TODO how does the educator see the instructions, he's able to access, edit and create, but isn't sent the instruction message

    @Test
    public void studentCannotStartLesson() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);

        User u = server.usersStore.getUser(eduId);
        LessonStructure l = server.lessonStructureStore.getLessonStructure(lesson1);

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
