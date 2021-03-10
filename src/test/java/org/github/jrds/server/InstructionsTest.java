package org.github.jrds.server;

import org.github.jrds.server.domain.Instruction;
import org.github.jrds.server.domain.Lesson;
import org.github.jrds.server.domain.User;
import org.junit.Assert;
import org.junit.Test;

public class InstructionsTest extends ApplicationTest {
    private String testTitle1 = "Instruction Test 1";
    private String testBody1 = "Body of Test Instruction 1";
    private String testTitle2 = "Instruction Test 2";
    private String testBody2 = "Body of Test Instruction 2";
    private String testTitle3 = "Instruction Test 3";
    private String testBody3 = "Body of Test Instruction 3";

    @Test
    public void educatorCreatesInstructionToLesson(){
        connect(eduId, eduName, lesson1);
        Lesson l = Main.lessonStore.getLesson(lesson1); //Should I move these out above like the Strings?
        User u = Main.usersStore.getUser(eduId);

        l.removeAllInstructions();
        Assert.assertEquals(0, l.getAllInstructions().size());

        l.createInstruction(testTitle1, testBody1, u);

        Assert.assertFalse(l.getAllInstructions().isEmpty());
        Assert.assertNotNull(l.getInstruction(testTitle1)); // TODO - REVIEW this proves the title is as expected so don't need a seperate test for this.
        Assert.assertTrue(l.getInstruction(testTitle1).getBody().equals(testBody1));
        Assert.assertTrue(l.getInstruction(testTitle1).getAuthor().equals(u));
    }


    @Test
    public void educatorCanCreateMultipleInstructions(){

        connect(eduId, eduName, lesson1);
        Lesson l = Main.lessonStore.getLesson(lesson1);
        User u = Main.usersStore.getUser(eduId);

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
    public void educatorCanRemoveAnInstruction(){

        connect(eduId, eduName, lesson1);
        Lesson l = Main.lessonStore.getLesson(lesson1);
        User u = Main.usersStore.getUser(eduId);

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
    public void educatorCanRemoveAllInstructions(){
        // already proven in other tests - is it good to seperate it out? 
        connect(eduId, eduName, lesson1);
        Lesson l = Main.lessonStore.getLesson(lesson1);
        User u = Main.usersStore.getUser(eduId);

        l.removeAllInstructions();

        l.createInstruction(testTitle1, testBody1, u);
        l.createInstruction(testTitle2, testBody2, u);
        l.createInstruction(testTitle3, testBody3, u);

        Assert.assertEquals(3, l.getAllInstructions().size());

        l.removeAllInstructions();

        Assert.assertEquals(0, l.getAllInstructions().size());
    }

    
    @Test
    public void educatorCanEditInstructionTitle(){

        connect(eduId, eduName, lesson1);
        Lesson l = Main.lessonStore.getLesson(lesson1);
        User u = Main.usersStore.getUser(eduId);

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
    public void educatorCanEditInstructionBody(){

        connect(eduId, eduName, lesson1);
        Lesson l = Main.lessonStore.getLesson(lesson1);
        User u = Main.usersStore.getUser(eduId);

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
    public void studentCantCreateInstructionToLesson(){
        connect(l1Id, eduName, lesson1);
        Lesson l = Main.lessonStore.getLesson(lesson1);
        User u = Main.usersStore.getUser(l1Id);

        l.removeAllInstructions();     

        try {
            l.createInstruction(testTitle1, testBody1, u);
            Assert.fail("Expected Illegal Argument Exception");
        } catch (IllegalArgumentException e){
            Assert.assertEquals("Only the educator of this lesson can add instructions", e.getMessage());
        }
    }


    @Test 
    public void instructionsCantHaveTheSameTitle(){
        connect(eduId, eduName, lesson1);
        Lesson l = Main.lessonStore.getLesson(lesson1);
        User u = Main.usersStore.getUser(eduId);

        l.removeAllInstructions();     

        l.createInstruction(testTitle1, testBody1, u);
        try {
            l.createInstruction(testTitle1, testBody2, u);
            Assert.fail("Expected illegal arguement exception");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("This title already exists", e.getMessage());
        }
        
    }

    @Test
    public void sendAndReceiveInstruction() {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);
        TestClient c3 = connect(l2Id, l2Name, lesson1);

        User u = Main.usersStore.getUser(eduId);
        Lesson l = Main.lessonStore.getLesson(lesson1);

        l.removeAllInstructions();
        l.createInstruction(testTitle1, testBody1, u);
        l.createInstruction(testTitle2, testBody2, u);

        c1.startLesson();
        Message received1 = c2.getMessageReceived();
        Message received2 = c3.getMessageReceived();

        Assert.assertTrue(received1 instanceof InstructionMessage);
        Assert.assertTrue(received2 instanceof InstructionMessage);

        Assert.assertEquals(testTitle1, ((InstructionMessage)received1).getTitle());
        Assert.assertEquals(testBody1, ((InstructionMessage)received1).getBody());
        Assert.assertEquals(testTitle1, ((InstructionMessage)received2).getTitle());
        Assert.assertEquals(testBody1, ((InstructionMessage)received2).getBody());
    }


    @Test
    public void instructionsArePresentAfterEducatorDisconnectsAndReconnects(){
        // some sort of permanence needs to be tested, so the next time we come to that lesson, the lesson still has the instructions
        // potentially add to the set up/initialisation ? // Needs more thought. 
        Assert.assertTrue(false);

    }


}
