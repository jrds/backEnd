package org.github.jrds.server;

import org.junit.Assert;
import org.junit.Test;

public class InstructionsTest extends ApplicationTest {

    @Test
    public void educatorAddsInstructionToLesson(){
        String testTitle = "Instruction Test";
        String testBody = "Body of Test Instruction";

        connect(eduId, eduName, lesson1);
        Lesson l = Main.lessonStore.getLesson(lesson1);
        User u = Main.usersStore.getUser(eduId);

        Assert.assertTrue(l.getAllInstructions().isEmpty());

        l.createInstruction(testTitle, testBody, u);

        Assert.assertFalse(l.getAllInstructions().isEmpty());
        Assert.assertNotNull(l.getInstruction(testTitle)); // TODO - REVIEW this proves the title is as expected so don't need a seperate test for this.
        Assert.assertTrue(l.getInstruction(testTitle).getBody().equals(testBody));
        Assert.assertTrue(l.getInstruction(testTitle).getAuthor().equals(u));
    }


    @Test
    public void educatorCanAddMultipleInstructions(){
        String testTitle1 = "Instruction Test 1";
        String testBody1 = "Body of Test Instruction 1";
        String testTitle2 = "Instruction Test 2";
        String testBody2 = "Body of Test Instruction 2";
        String testTitle3 = "Instruction Test 3";
        String testBody3 = "Body of Test Instruction 3";

        connect(eduId, eduName, lesson1);
        Lesson l = Main.lessonStore.getLesson(lesson1);
        User u = Main.usersStore.getUser(eduId);

        l.removeAllInstructions();
        Assert.assertEquals(0, l.getAllInstructions().size());

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
        
    }

    
    @Test
    public void educatorCanRemoveAllInstructions(){
        // already proven in other tests - is it good to seperate it out? 
    }

    
    @Test
    public void educatorEditAnInstruction(){
        
    }


    @Test
    public void studentCantAddInstructionToLesson(){
        
    }


    @Test
    public void differentEducatorCantAddInstructionToLesson(){
        
    }


    @Test 
    public void instructionsCantHaveTheSameTitle(){

    }


    @Test
    public void instructionsArePresentAfterEducatorDisconnectsAndReconnects(){
        // some sort of permance needs to be tested, so the next time we come to that lesson, the lesson still has the instructions
        // potentially add to the set up/initialisation ? // Needs more thought. 
    }
}
