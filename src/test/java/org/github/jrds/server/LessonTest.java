package org.github.jrds.server;

import org.eclipse.jetty.websocket.api.UpgradeException;
import org.junit.Assert;
import org.junit.Test;

public class LessonTest extends ApplicationTest {

    @Test
    public void validUserGrantedAccess(){
        connect(l1);
    }
    
    @Test 
    public void invalidUserDeniedAccess(){
        try {
            connect("Stranger");
            Assert.fail("Expected stranger to be denied access");
        } catch(Exception e){
            Throwable rootCause = findRootCause(e);
            Assert.assertTrue(rootCause instanceof UpgradeException);
            Assert.assertEquals(401, ((UpgradeException)rootCause).getResponseStatusCode());
        }
    }


    @Test 
    public void unauthorisedUserDeniedLessonAccess(){
        try {
            connect(l99);
            Assert.fail("Expected Learner 99 to be denied lesson access");
        } catch(Exception e){
            Throwable rootCause = findRootCause(e);
            Assert.assertTrue(rootCause instanceof UpgradeException);
            Assert.assertEquals(403, ((UpgradeException)rootCause).getResponseStatusCode());
        }
    }


}
