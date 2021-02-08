package org.github.jrds.server;

import org.junit.Assert;
import org.junit.Test;

public class AttendanceTest extends ApplicationTest {

    @Test
    public void oneAttendanceRecordedCorrectly() {
        connect(l1, lesson1);
        String key = l1 + lesson1;
        // System.out.println(Main.attendanceStore);
        Assert.assertEquals(l1, Main.attendanceStore.getAttendance(key).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(key).getLessonID());
    }

    @Test
    public void twoAttendancesRecordedCorrectly() {
        connect(l1, lesson1);
        connect(l2, lesson2);
        String key1 = l1 + lesson1;
        String key2 = l2 + lesson2;
        Assert.assertEquals(l1, Main.attendanceStore.getAttendance(key1).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(key1).getLessonID());
        Assert.assertEquals(l2, Main.attendanceStore.getAttendance(key2).getUserID());
        Assert.assertEquals(lesson2, Main.attendanceStore.getAttendance(key2).getLessonID());
    }

    // TODO - a test to ensure a user can't be added twice - need to potentially throw an error// handle this situation on the server side. 

    @Test
    public void userCantRegisterAttendanceForTheSameLessonTwice(){
        connect(l1, lesson1);
        try {
            connect(l1, lesson1);
            Assert.fail("Expected connection to fail, because learner 1 is already connnected/attending this lesson");
            //might need an if... to check if the user is already in attendance in connect method to resolve this. 
        } catch(Exception e){
            System.out.println("£££££££££££££££££££££££££££££££££££££££££££££££££££££ " + e);
        }
    }

    @Test
    public void unregisteredStudentAttendanceNotRecorded() {
        connect(l1, lesson1);
        try {
            connect(l99, lesson1);
        } catch (Exception e) {} 
        String key1 = l1 + lesson1;
        String key2 = l99 + lesson1;
        Assert.assertEquals(l1, Main.attendanceStore.getAttendance(key1).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(key1).getLessonID());
        Assert.assertNull(Main.attendanceStore.getAttendance(key2));
    }

    @Test
    public void unregisteredLessonAttendanceNotRecorded() {
        connect(l1, lesson1);
        try {
            connect(l1, "9999");
        } catch (Exception e) {} 
        String key1 = l1 + lesson1;
        String key2 = l1 + "9999";
        Assert.assertEquals(l1, Main.attendanceStore.getAttendance(key1).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(key1).getLessonID());
        Assert.assertNull(Main.attendanceStore.getAttendance(key2));
    }

    @Test
    public void educatorConnectionVerificationProvided() {
        connect(edu, lesson1);
        connect(l1, lesson1);
        Assert.assertTrue(Main.attendanceStore.educatorConnected(lesson1));
    }

    @Test
    public void educatorConnectionVerificationDenied() {
        connect(l1, lesson1);
        connect(l2, lesson1);
        Assert.assertFalse(Main.attendanceStore.educatorConnected(lesson1));
    }
 
 
    // TODO - some code is duplicated accross these tests - need to review this

    @Test
    public void attendanceRemovedFromStoreAfterDisconnect(){
        TestClient c1 = connect(l1, lesson1);
        String key1 = l1 + lesson1;
        Assert.assertEquals(l1, Main.attendanceStore.getAttendance(key1).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(key1).getLessonID());
        disconnect(c1);
        Assert.assertNull(Main.attendanceStore.getAttendance(key1));
    }

    @Test
    public void attenedanceRemovedAndReaddedUponDisconnectAndReconnnect(){
        TestClient c1 = connect(l1, lesson1);
        String key1 = l1 + lesson1;
        Assert.assertEquals(l1, Main.attendanceStore.getAttendance(key1).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(key1).getLessonID());
        disconnect(c1);
        Assert.assertNull(Main.attendanceStore.getAttendance(key1));
        connect(l1,lesson1);
        Assert.assertEquals(l1, Main.attendanceStore.getAttendance(key1).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(key1).getLessonID());
    }
    

    @Test
    public void otherAttendancesNotImpactedWhenAnotherIsRemoved(){
        TestClient c1 = connect(l1, lesson1);
        TestClient c2 = connect(l2, lesson1); // TODO - QUESTION - I don't need this to be a TestClient, but is it better for consistency? 
        String key1 = l1 + lesson1;
        String key2 = l2 + lesson1;
        Assert.assertEquals(l1, Main.attendanceStore.getAttendance(key1).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(key1).getLessonID());
        Assert.assertEquals(l2, Main.attendanceStore.getAttendance(key2).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(key2).getLessonID());
        disconnect(c1);
        Assert.assertNull(Main.attendanceStore.getAttendance(key1));
        Assert.assertEquals(l2, Main.attendanceStore.getAttendance(key2).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(key2).getLessonID());
    }
}