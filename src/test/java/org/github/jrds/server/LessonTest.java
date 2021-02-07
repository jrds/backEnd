package org.github.jrds.server;

import org.eclipse.jetty.websocket.api.UpgradeException;
import org.junit.Assert;
import org.junit.Test;

public class LessonTest extends ApplicationTest {

    @Test
    public void validUserGrantedAccess(){
        connect(l1, lesson1);
    }

    // TODO - QUESTION - realistically should there be 2 conext methods one granting the access to the application and one to the lesson? 
    // Right now this test and the one below, seen to be doing the same thing, since being changed that connect takes the class too. 
    // As an educator it's likely you would want to log on without goining to join a specific class
    // but not as much as a need for students. 

    @Test 
    public void invalidUserDeniedAccess(){
        try {
            connect("Stranger", lesson1);
            Assert.fail("Expected stranger to be denied access");
        } catch(Exception e){
            Throwable rootCause = findRootCause(e);
            Assert.assertTrue(rootCause instanceof UpgradeException);
            Assert.assertEquals(401, ((UpgradeException)rootCause).getResponseStatusCode());
        }
    }

    @Test
    public void invalidLessonIdConnectionFail(){
        try {
            connect(l1, "Lesson_non_existent");
        }
        catch(Exception e){
            Throwable rootCause = findRootCause(e);
            Assert.assertTrue(rootCause instanceof UpgradeException);
            Assert.assertEquals(400, ((UpgradeException)rootCause).getResponseStatusCode());
        }
    }

    @Test 
    public void registeredUserGrantedLessonAccess(){
        connect(l99, lesson2);
    }


    @Test 
    public void unRegisteredUserDeniedLessonAccess(){
        try {
            connect(l99, lesson1);
            Assert.fail("Expected Learner 99 to be denied lesson access");
        } catch(Exception e){
            Throwable rootCause = findRootCause(e);
            Assert.assertTrue(rootCause instanceof UpgradeException);
            Assert.assertEquals(403, ((UpgradeException)rootCause).getResponseStatusCode());
        }
    }
}
