package org.github.jrds.codi.messaging.lesson;

import org.github.jrds.codi.core.domain.ActiveLessonState;
import org.github.jrds.codi.core.domain.Instruction;
import org.github.jrds.codi.core.domain.LessonStructure;
import org.github.jrds.codi.core.domain.User;
import org.github.jrds.codi.core.messages.*;
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

public class InstructionsTest extends ApplicationTest
{

    @BeforeClass
    public static void registerMessageSubtypes()
    {
        ClientWebSocket.registerMessageSubtype(InstructionInfo.class);
    }


    private final String testTitle1 = "Instruction Test 1";
    private final String testBody1 = "Body of Test Instruction 1";
    private final String testTitle2 = "Instruction Test 2";
    private final String testBody2 = "Body of Test Instruction 2";
    private final String testTitle3 = "Instruction Test 3";
    private final String testBody3 = "Body of Test Instruction 3";


    @Test
    public void educatorCreatesInstructionToLesson()
    {
        connect(eduId, lesson2);
        LessonStructure l = persistenceServices.getLessonStructureStore().getLessonStructure(lesson2);
        User u = persistenceServices.getUsersStore().getUser(eduId);

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
        connect(eduId, lesson2);
        LessonStructure l = persistenceServices.getLessonStructureStore().getLessonStructure(lesson2);
        User u = persistenceServices.getUsersStore().getUser(eduId);

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
    public void educatorCanReorderInstructions()
    {
        connect(eduId, lesson2);
        LessonStructure l = persistenceServices.getLessonStructureStore().getLessonStructure(lesson2);
        User u = persistenceServices.getUsersStore().getUser(eduId);

        l.createInstruction(testTitle1, testBody1, u);
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
        connect(eduId, lesson2);
        LessonStructure l = persistenceServices.getLessonStructureStore().getLessonStructure(lesson2);
        User u = persistenceServices.getUsersStore().getUser(eduId);

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
        connect(eduId, lesson2);
        LessonStructure l = persistenceServices.getLessonStructureStore().getLessonStructure(lesson2);
        User u = persistenceServices.getUsersStore().getUser(eduId);

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
        connect(eduId, lesson1);
        LessonStructure l = persistenceServices.getLessonStructureStore().getLessonStructure(lesson1);
        User u = persistenceServices.getUsersStore().getUser(eduId);

        Instruction instruction = l.createInstruction(testTitle1, testBody1, u);

        instruction.setTitle(testTitle2);

        Instruction refetch = l.getInstruction(instruction.getId());
        Assert.assertEquals(testTitle2, refetch.getTitle());
        Assert.assertEquals(testBody1, refetch.getBody());
    }

    @Test
    public void educatorCanEditInstructionBody()
    {
        connect(eduId, lesson1);
        LessonStructure l = persistenceServices.getLessonStructureStore().getLessonStructure(lesson1);
        User u = persistenceServices.getUsersStore().getUser(eduId);

        Instruction instruction = l.createInstruction(testTitle1, testBody1, u);

        instruction.setBody(testBody2);

        Instruction refetch = l.getInstruction(instruction.getId());
        Assert.assertEquals(testTitle1, refetch.getTitle());
        Assert.assertEquals(testBody2, refetch.getBody());
    }

    @Test
    public void studentCantCreateInstructionToLesson()
    {
        connect(l1Id, lesson1);
        LessonStructure l = persistenceServices.getLessonStructureStore().getLessonStructure(lesson1);
        User u = persistenceServices.getUsersStore().getUser(l1Id);

        try
        {
            l.createInstruction(testTitle1, testBody1, u);
            Assert.fail("Expected Illegal Argument Exception");
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertEquals("Only the educator of this lesson can add instructions", e.getMessage());
        }
    }

    @Test
    public void startLessonSendsOneInstructionToAllLearners() throws InterruptedException, ExecutionException, TimeoutException
    {
        TestClient c1 = connect(eduId, lesson2);
        TestClient c2 = connect(l1Id, lesson2);
        TestClient c3 = connect(l2Id, lesson2);

        User u = persistenceServices.getUsersStore().getUser(eduId);
        LessonStructure l = persistenceServices.getLessonStructureStore().getLessonStructure(lesson2);

        l.createInstruction(testTitle1, testBody1, u);

        Assert.assertEquals(ActiveLessonState.NOT_STARTED, persistenceServices.getActiveLessonStore().getActiveLesson(lesson2).getActiveLessonState());
        startLesson(c1).get(10, TimeUnit.SECONDS);

        Assert.assertEquals(ActiveLessonState.IN_PROGRESS, persistenceServices.getActiveLessonStore().getActiveLesson(lesson2).getActiveLessonState());

        Message received1 = c2.getMessageReceived(InstructionInfo.class);
        Message received2 = c3.getMessageReceived(InstructionInfo.class);


        Assert.assertEquals(testTitle1, ((InstructionInfo) received1).getInstruction().getTitle());
        Assert.assertEquals(testBody1, ((InstructionInfo) received1).getInstruction().getBody());
        Assert.assertEquals(testTitle1, ((InstructionInfo) received2).getInstruction().getTitle());
        Assert.assertEquals(testBody1, ((InstructionInfo) received2).getInstruction().getBody());
    }


    @Test
    public void startLessonSendsMultipleInstructionsToAllLearners()
    {
        TestClient c1 = connect(eduId, lesson2);
        TestClient c2 = connect(l1Id, lesson2);
        TestClient c3 = connect(l2Id, lesson2);

        User u = persistenceServices.getUsersStore().getUser(eduId);
        LessonStructure l = persistenceServices.getLessonStructureStore().getLessonStructure(lesson2);

        l.createInstruction(testTitle1, testBody1, u);
        l.createInstruction(testTitle2, testBody2, u);

        startLesson(c1);
        Message received1 = c2.getMessageReceived();
        Message received2 = c2.getMessageReceived();

        Message received3 = c3.getMessageReceived();
        Message received4 = c3.getMessageReceived();

        Assert.assertEquals(testTitle1, ((InstructionInfo) received1).getInstruction().getTitle());
        Assert.assertEquals(testBody1, ((InstructionInfo) received1).getInstruction().getBody());
        Assert.assertEquals(testTitle1, ((InstructionInfo) received3).getInstruction().getTitle());
        Assert.assertEquals(testBody1, ((InstructionInfo) received3).getInstruction().getBody());

        Assert.assertEquals(testTitle2, ((InstructionInfo) received2).getInstruction().getTitle());
        Assert.assertEquals(testBody2, ((InstructionInfo) received2).getInstruction().getBody());
        Assert.assertEquals(testTitle2, ((InstructionInfo) received4).getInstruction().getTitle());
        Assert.assertEquals(testBody2, ((InstructionInfo) received4).getInstruction().getBody());
    }

    @Test
    public void learnerNotInClassDoesntReceiveInstructions()
    {
        TestClient c1 = connect(eduId, lesson1);
        TestClient c2 = connect(l1Id, lesson1);
        connect(l99Id, lesson2);

        User u = persistenceServices.getUsersStore().getUser(eduId);
        LessonStructure l = persistenceServices.getLessonStructureStore().getLessonStructure(lesson1);

        l.createInstruction(testTitle1, testBody1, u);

        startLesson(c1);
        c1.getMessageReceived();

        Assert.assertNotNull(c2.getMessageReceived());
        Assert.assertEquals(0, MessagingContext.messageStats.forUser(l99Id).getSent());
    }

    //TODO how does the educator see the instructions, he's able to access, edit and create, but isn't sent the instruction message

    @Test
    public void studentCannotStartLesson() throws InterruptedException, ExecutionException, TimeoutException
    {
        connect(eduId, lesson1);
        TestClient c2 = connect(l1Id, lesson1);

        User u = persistenceServices.getUsersStore().getUser(eduId);
        LessonStructure l = persistenceServices.getLessonStructureStore().getLessonStructure(lesson1);

        l.createInstruction(testTitle1, testBody1, u);

        Future<Response> startLessonFuture = startLesson(c2);
        Response startLessonResponse = startLessonFuture.get(10, TimeUnit.SECONDS);

        Assert.assertTrue(startLessonResponse.isFailure());
        Assert.assertEquals("Learner cannot start a lesson", startLessonResponse.asFailure().getFailureReason());
    }


    private Future<Response> startLesson(TestClient client)
    {
        LessonStartRequest request = new LessonStartRequest(client.getId());
        return client.sendRequest(request);
    }



}
