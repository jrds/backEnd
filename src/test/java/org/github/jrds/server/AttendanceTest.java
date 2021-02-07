package org.github.jrds.server;

import org.junit.Assert;
import org.junit.Test;

public class AttendanceTest extends ApplicationTest {
 
    @Test
    public void oneAttendanceRecordedCorrectly(){
        connect(l1, lesson1);
        String key = l1 + lesson1;
        //System.out.println(Main.attendanceStore);
        Assert.assertEquals(l1, Main.attendanceStore.getAttendance(key).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(key).getLessonID());
    }

    @Test
    public void twoAttendancesRecordedCorrectly(){
        connect(l1, lesson1);
        connect(l2, lesson2);
        String key1 = l1 + lesson1;
        String key2 = l2 + lesson2;
        Assert.assertEquals(l1, Main.attendanceStore.getAttendance(key1).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(key1).getLessonID());
        Assert.assertEquals(l2, Main.attendanceStore.getAttendance(key2).getUserID());
        Assert.assertEquals(lesson2, Main.attendanceStore.getAttendance(key2).getLessonID());
    }
    

}