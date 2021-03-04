package org.github.jrds.server;

import org.junit.Assert;
import org.junit.Test;

public class UserTest extends ApplicationTest {

    @Test
    public void userCreatedSuccessfully(){
        String id = "u19011";
        String name = l1Id; 

        User u1 = new User(id, name); 
        Assert.assertEquals(id, u1.getId());
        Assert.assertEquals(name, u1.getName());
    }
    
}
