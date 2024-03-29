package org.github.jrds.codi.messaging.lesson;

import org.eclipse.jetty.websocket.api.UpgradeException;
import org.github.jrds.codi.core.dto.UserDto;
import org.github.jrds.codi.core.messages.Message;
import org.github.jrds.codi.server.testing.ApplicationTest;
import org.github.jrds.codi.server.testing.ClientWebSocket;
import org.github.jrds.codi.server.testing.TestClient;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ActiveLessonTest extends ApplicationTest
{

    @BeforeClass
    public static void registerMessageSubtypes()
    {
        ClientWebSocket.registerMessageSubtype(LessonStartRequest.class);
        ClientWebSocket.registerMessageSubtype(LearnersInfo.class);
    }

    @Test
    public void validUserGrantedAccess()
    {
        try
        {
            connect(l1Id, lesson1);
        }
        catch (Exception e)
        {
            Assert.fail("Expected " + l1Id + " to connect");
        }
    }


    @Test
    public void invalidUserDeniedAccess()
    {
        try
        {
            connect("k1234", lesson1);
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
            connect(l1Id, "Lesson_non_existent");
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
        connect(l99Id, lesson2);
    }


    @Test
    public void unRegisteredUserDeniedLessonAccess()
    {
        try
        {
            connect(l99Id, lesson1);
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
        TestClient c1 = connect(eduId, lesson1);
        LearnersInfo learnersInfo1 = (LearnersInfo) c1.getMessageReceived(LearnersInfo.class);
        Assert.assertEquals(0, learnersInfo1.getLearnersInAttendance().size());
        Assert.assertTrue(learnersInfo1.getLearnersExpected().contains(new UserDto(persistenceServices.getUsersStore().getUser(l1Id))));
        Assert.assertTrue(learnersInfo1.getLearnersExpected().contains(new UserDto(persistenceServices.getUsersStore().getUser(l2Id))));

        connect(l1Id, lesson1);
        LearnersInfo learnersInfo2 = (LearnersInfo) c1.getMessageReceived(LearnersInfo.class);
        Assert.assertEquals(1, learnersInfo2.getLearnersInAttendance().size());
        Assert.assertTrue(learnersInfo2.getLearnersInAttendance().contains(new UserDto(persistenceServices.getUsersStore().getUser(l1Id))));
        Assert.assertFalse(learnersInfo2.getLearnersExpected().contains(new UserDto(persistenceServices.getUsersStore().getUser(l1Id))));
        Assert.assertFalse(learnersInfo2.getLearnersInAttendance().contains(new UserDto(persistenceServices.getUsersStore().getUser(l2Id))));
        Assert.assertTrue(learnersInfo2.getLearnersExpected().contains(new UserDto(persistenceServices.getUsersStore().getUser(l2Id))));

        connect(l2Id, lesson1);
        LearnersInfo learnersInfo3 = (LearnersInfo) c1.getMessageReceived(LearnersInfo.class);
        Assert.assertEquals(2, learnersInfo3.getLearnersInAttendance().size());
        Assert.assertTrue(learnersInfo3.getLearnersInAttendance().contains(new UserDto(persistenceServices.getUsersStore().getUser(l1Id))));
        Assert.assertTrue(learnersInfo3.getLearnersInAttendance().contains(new UserDto(persistenceServices.getUsersStore().getUser(l2Id))));
        Assert.assertFalse(learnersInfo3.getLearnersExpected().contains(new UserDto(persistenceServices.getUsersStore().getUser(l1Id))));
        Assert.assertFalse(learnersInfo3.getLearnersExpected().contains(new UserDto(persistenceServices.getUsersStore().getUser(l2Id))));
    }


    @Test
    public void educatorReceivesLearnersInAttendanceInfoOfLearnersWhoJoinedBeforeThem()
    {
        connect(l1Id, lesson1);
        connect(l2Id, lesson1);
        TestClient c3 = connect(eduId, lesson1);

        Message m = c3.getMessageReceived();

        Assert.assertTrue(m instanceof LearnersInfo);
        Assert.assertEquals(2, ((LearnersInfo) m).getLearnersInAttendance().size());
        Assert.assertTrue(((LearnersInfo) m).getLearnersInAttendance().contains(new UserDto(persistenceServices.getUsersStore().getUser(l1Id))));
        Assert.assertTrue(((LearnersInfo) m).getLearnersInAttendance().contains(new UserDto(persistenceServices.getUsersStore().getUser(l2Id))));


    }


}
