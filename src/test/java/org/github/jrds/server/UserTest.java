package org.github.jrds.server;

import org.github.jrds.server.domain.Role;
import org.junit.Assert;
import org.junit.Test;

public class UserTest extends ApplicationTest
{

    @Test
    public void learnerIdentified()
    {
        connect(l1Id, l1Name, lesson1);

        Assert.assertEquals(Role.LEARNER, getAttendance(l1Id, lesson1).getRole());
    }

    @Test
    public void educatorIdentified()
    {
        connect(l1Id, l1Name, lesson1);
        connect(eduId, eduName, lesson1);
        Assert.assertEquals(Role.LEARNER, getAttendance(l1Id, lesson1).getRole());
        Assert.assertEquals(Role.EDUCATOR, getAttendance(eduId, lesson1).getRole());
    }

    @Test
    public void nonStandardIDnotAssignedRole()
    {
        // TODO - unsure on how to test this as they don't get passed the auth stage
        // feels wrong to add a "bad user" to the auth
    }

}
