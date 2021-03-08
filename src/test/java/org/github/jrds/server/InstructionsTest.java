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
        
    }


    @Test
    public void educatorCanRemoveAnInstruction(){
        
    }

    
    @Test
    public void educatorCanRemoveAllInstructions(){
        
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
