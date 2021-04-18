package org.github.jrds.codi.misc.tests;

import org.github.jrds.codi.core.domain.Role;
import org.github.jrds.codi.core.messages.SessionStartResponse;
import org.github.jrds.codi.server.testing.ApplicationTest;
import org.github.jrds.codi.server.testing.TestClient;
import org.junit.Assert;
import org.junit.Test;

public class UserTest extends ApplicationTest
{

    @Test
    public void learnerIdentified()
    {
        TestClient client = connect(l1Id, lesson1);
        Assert.assertTrue(client.getSessionStartResponse().isSuccess());
        Assert.assertTrue(client.getSessionStartResponse() instanceof SessionStartResponse);
        Assert.assertEquals("LEARNER", ((SessionStartResponse) client.getSessionStartResponse()).getRole());
        Assert.assertEquals(Role.LEARNER, getAttendance(l1Id, lesson1).getRole());
    }

    @Test
    public void educatorIdentified()
    {
        connect(l1Id, lesson1);
        TestClient educatorClient = connect(eduId, lesson1);
        Assert.assertTrue(educatorClient.getSessionStartResponse().isSuccess());
        Assert.assertTrue(educatorClient.getSessionStartResponse() instanceof SessionStartResponse);
        Assert.assertEquals("EDUCATOR", ((SessionStartResponse) educatorClient.getSessionStartResponse()).getRole());
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
