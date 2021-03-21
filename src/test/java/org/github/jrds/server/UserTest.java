package org.github.jrds.server;

import org.github.jrds.server.domain.Role;
import org.github.jrds.server.messages.SessionStartResponseMessage;
import org.junit.Assert;
import org.junit.Test;

public class UserTest extends ApplicationTest
{

    @Test
    public void learnerIdentified()
    {
        TestClient client = connect(l1Id, l1Name, lesson1);
        Assert.assertTrue(client.getSessionStartResponse().isSuccess());
        Assert.assertTrue(client.getSessionStartResponse() instanceof SessionStartResponseMessage);
        Assert.assertEquals("LEARNER", ((SessionStartResponseMessage) client.getSessionStartResponse()).getRole());
        Assert.assertEquals(Role.LEARNER, getAttendance(l1Id, lesson1).getRole());
    }

    @Test
    public void educatorIdentified()
    {
        connect(l1Id, l1Name, lesson1);
        TestClient educatorClient = connect(eduId, eduName, lesson1);
        Assert.assertTrue(educatorClient.getSessionStartResponse().isSuccess());
        Assert.assertTrue(educatorClient.getSessionStartResponse() instanceof SessionStartResponseMessage);
        Assert.assertEquals("EDUCATOR", ((SessionStartResponseMessage) educatorClient.getSessionStartResponse()).getRole());
        Assert.assertEquals(Role.LEARNER, getAttendance(l1Id, lesson1).getRole());
        Assert.assertEquals(Role.EDUCATOR, getAttendance(eduId, lesson1).getRole());
    }

    @Test
    public void nonStandardIdNotAssignedRole()
    {
        // TODO - unsure on how to test this as they don't get passed the auth stage
        // feels wrong to add a "bad user" to the auth
    }

}
