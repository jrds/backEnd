package org.github.jrds.server;

import org.eclipse.jetty.websocket.api.UpgradeException;
import org.github.jrds.server.dto.UserDto;
import org.github.jrds.server.extensions.lesson.LearnersInfo;
import org.github.jrds.server.messages.Message;
import org.junit.Assert;
import org.junit.Test;

public class ActiveLessonTest extends ApplicationTest
{

    @Test
    public void validUserGrantedAccess()
    {
        try
        {
            connect(l1Id, l1Name, lesson1);
        }
        catch (Exception e)
        {
            System.out.println("NOT DESIRED OUTCOME");
            System.out.println(e);
        }
    }


    @Test
    public void invalidUserDeniedAccess()
    {
        try
        {
            connect("k1234", "Unknown", lesson1);
            Assert.fail("Expected stranger to be denied access");
        }
        catch (Exception e)
        {
            Throwable rootCause = findRootCause(e);
            Assert.assertTrue(rootCause instanceof UpgradeException);
            Assert.assertEquals(401, ((UpgradeException) rootCause).getResponseStatusCode());
        }
    }

    @Test
    public void invalidLessonIdConnectionFail()
    {
        try
        {
            connect(l1Id, l1Name, "Lesson_non_existent");
        }
        catch (Exception e)
        {
            Throwable rootCause = findRootCause(e);
            Assert.assertTrue(rootCause instanceof UpgradeException);
            Assert.assertEquals(400, ((UpgradeException) rootCause).getResponseStatusCode());
        }
    }

    @Test
    public void registeredUserGrantedLessonAccess()
    {
        connect(l99Id, l99Name, lesson2);
    }


    @Test
    public void unRegisteredUserDeniedLessonAccess()
    {
        try
        {
            connect(l99Id, l99Name, lesson1);
            Assert.fail("Expected Learner 99 to be denied lesson access");
        }
        catch (Exception e)
        {
            Throwable rootCause = findRootCause(e);
            Assert.assertTrue(rootCause instanceof UpgradeException);
            Assert.assertEquals(403, ((UpgradeException) rootCause).getResponseStatusCode());
        }
    }

    @Test
    public void educatorReceivesLearnersInAttendanceInfoAsTheyJoin()
    {
        TestClient c1 = connect(eduId, eduName, lesson1);
        TestClient c2 = connect(l1Id, l1Name, lesson1);

        c1.getMessageReceived();
        Message m1 = c1.getMessageReceived();

        Assert.assertTrue(m1 instanceof LearnersInfo);
        Assert.assertTrue(((LearnersInfo) m1).getLearnersInAttendance().contains(new UserDto(Main.defaultInstance.usersStore.getUser(l1Id))));
        Assert.assertFalse(((LearnersInfo) m1).getLearnersExpected().contains(new UserDto(Main.defaultInstance.usersStore.getUser(l1Id))));

        Assert.assertFalse(((LearnersInfo) m1).getLearnersInAttendance().contains(new UserDto(Main.defaultInstance.usersStore.getUser(l2Id))));
        Assert.assertTrue(((LearnersInfo) m1).getLearnersExpected().contains(new UserDto(Main.defaultInstance.usersStore.getUser(l2Id))));


        TestClient c3 = connect(l2Id, l2Name, lesson1);

        Message m2 = c1.getMessageReceived();

        Assert.assertTrue(m2 instanceof LearnersInfo);
        Assert.assertTrue(((LearnersInfo) m2).getLearnersInAttendance().size() == 2);
        Assert.assertTrue(((LearnersInfo) m2).getLearnersInAttendance().contains(new UserDto(Main.defaultInstance.usersStore.getUser(l1Id))));
        Assert.assertTrue(((LearnersInfo) m2).getLearnersInAttendance().contains(new UserDto(Main.defaultInstance.usersStore.getUser(l2Id))));
        Assert.assertFalse(((LearnersInfo) m2).getLearnersExpected().contains(new UserDto(Main.defaultInstance.usersStore.getUser(l1Id))));
        Assert.assertFalse(((LearnersInfo) m2).getLearnersExpected().contains(new UserDto(Main.defaultInstance.usersStore.getUser(l2Id))));

        //TODO apply to other tests
    }


    @Test
    public void educatorReceivesLearnersInAttendanceInfoOfLearnersWhoJoinedBeforeThem()
    {
        TestClient c1 = connect(l1Id, l1Name, lesson1);
        TestClient c2 = connect(l2Id, l2Name, lesson1);
        TestClient c3 = connect(eduId, eduName, lesson1);

        Message m = c3.getMessageReceived();

        Assert.assertTrue(m instanceof LearnersInfo);
        Assert.assertTrue(((LearnersInfo) m).getLearnersInAttendance().size() == 2);
        Assert.assertTrue(((LearnersInfo) m).getLearnersInAttendance().contains(new UserDto(Main.defaultInstance.usersStore.getUser(l1Id))));
        Assert.assertTrue(((LearnersInfo) m).getLearnersInAttendance().contains(new UserDto(Main.defaultInstance.usersStore.getUser(l2Id))));


    }
}
