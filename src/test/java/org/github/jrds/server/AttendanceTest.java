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
    
    // TODO FIRST
    // this is failing because right now it's finding the edu connection from above
    // as right now I think it's getting it from the test above. Can use print statement to check. 
    // implication is the attendance needs to be removed when a user (session) disconnects
    // the idea could be that it rights to something with time stamps within attendanceStore
    // so would be something to go to a DB. 

}