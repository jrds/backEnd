package org.github.jrds.codi.server.testing;

import org.junit.Assert;
import org.junit.Test;

public class AttendanceTest extends ApplicationTest
{

    @Test
    public void oneAttendanceRecordedCorrectly()
    {
        connect(l1Id, lesson1);
        Assert.assertEquals(l1Id, getAttendance(l1Id, lesson1).getUser().getId());
        Assert.assertEquals(lesson1, getAttendance(l1Id, lesson1).getLesson().getId());
    }

    @Test
    public void twoAttendancesRecordedCorrectly()
    {
        connect(l1Id, lesson1);
        connect(l2Id, lesson2);

        Assert.assertEquals(l1Id, getAttendance(l1Id, lesson1).getUser().getId());
        Assert.assertEquals(lesson1, getAttendance(l1Id, lesson1).getLesson().getId());
        Assert.assertEquals(l2Id, getAttendance(l2Id, lesson2).getUser().getId());
        Assert.assertEquals(lesson2, getAttendance(l2Id, lesson2).getLesson().getId());
    }


    @Test
    public void userCantRegisterAttendanceForTheSameLessonTwice()
    {
        try
        {
            connect(l1Id, lesson1);
        }
        catch (Exception e)
        {
            // Expected
        }
        try
        {
            connect(l1Id, lesson1);
            Assert.fail("Expected connection to fail, because learner 1 is already connnected/attending this lesson");
            //might need an if... to check if the user is already in attendance in connect method to resolve this. 
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage().contains("Attendance already in existence for this user, in this lesson"));
        }
    }

    @Test
    public void unregisteredLearnerAttendanceNotRecorded()
    {
        try
        {
            connect(l99Id, lesson1);
        }
        catch (Exception e)
        {
            //expected
        }

        Assert.assertNull(getAttendance(l99Id, lesson1));
    }

    @Test
    public void unregisteredLessonAttendanceNotRecorded()
    {
        try
        {
            connect(l1Id, "9999");
        }
        catch (Exception e)
        {
            //expected
        }

        Assert.assertNull(getAttendance(l1Id, "9999"));
    }

    @Test
    public void educatorConnectionVerificationProvided()
    {
        connect(eduId, lesson1);
        connect(l1Id, lesson1);
        Assert.assertNotNull(getAttendance(eduId, lesson1));
    }

    @Test
    public void educatorConnectionVerificationDenied()
    {
        connect(l1Id, lesson1);
        connect(l2Id, lesson1);
        Assert.assertNull(getAttendance(eduId, lesson1));
    }


    @Test
    public void attendanceRemovedFromStoreAfterDisconnect()
    {
        TestClient c1 = connect(l1Id, lesson1);
        Assert.assertEquals(l1Id, getAttendance(l1Id, lesson1).getUser().getId());
        Assert.assertEquals(lesson1, getAttendance(l1Id, lesson1).getLesson().getId());

        disconnect(c1);
        Assert.assertNull(getAttendance(l1Id, lesson1));
    }

    @Test
    public void attendanceRemovedAndReaddedUponDisconnectAndReconnnect()
    {
        TestClient c1 = connect(l1Id, lesson1);
        disconnect(c1);

        connect(l1Id, lesson1);
        Assert.assertEquals(l1Id, getAttendance(l1Id, lesson1).getUser().getId());
        Assert.assertEquals(lesson1, getAttendance(l1Id, lesson1).getLesson().getId());
    }


    @Test
    public void otherAttendancesNotImpactedWhenAnotherIsRemoved()
    {
        TestClient c1 = connect(l1Id, lesson1);
        connect(l2Id, lesson1);

        Assert.assertEquals(l1Id, getAttendance(l1Id, lesson1).getUser().getId());
        Assert.assertEquals(lesson1, getAttendance(l1Id, lesson1).getLesson().getId());
        Assert.assertEquals(l2Id, getAttendance(l2Id, lesson1).getUser().getId());
        Assert.assertEquals(lesson1, getAttendance(l2Id, lesson1).getLesson().getId());

        disconnect(c1);
        Assert.assertNull(getAttendance(l1Id, lesson1));
        Assert.assertEquals(l2Id, getAttendance(l2Id, lesson1).getUser().getId());
        Assert.assertEquals(lesson1, getAttendance(l2Id, lesson1).getLesson().getId());
    }
}