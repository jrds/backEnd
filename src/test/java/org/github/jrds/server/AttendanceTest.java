package org.github.jrds.server;

import org.junit.Assert;
import org.junit.Test;

public class AttendanceTest extends ApplicationTest {

    @Test
    public void oneAttendanceRecordedCorrectly() {
        connect(l1, lesson1);
        Assert.assertEquals(l1, Main.attendanceStore.getAttendance(l1,lesson1).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(l1,lesson1).getLessonID());
    }

    @Test
    public void twoAttendancesRecordedCorrectly() {
        connect(l1, lesson1);
        connect(l2, lesson2);
 
        Assert.assertEquals(l1, Main.attendanceStore.getAttendance(l1, lesson1).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(l1, lesson1).getLessonID());
        Assert.assertEquals(l2, Main.attendanceStore.getAttendance(l2, lesson2).getUserID());
        Assert.assertEquals(lesson2, Main.attendanceStore.getAttendance(l2, lesson2).getLessonID());
    }

    // TODO - a test to ensure a user can't be added twice - need to potentially throw an error// handle this situation on the server side. 

    @Test
    public void userCantRegisterAttendanceForTheSameLessonTwice(){
        try {
             connect(l1, lesson1);
        }
        catch(Exception e){
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        }
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
        try {
            connect(l99, lesson1);
        } catch (Exception e) {
            //expected
        } 

        Assert.assertNull(Main.attendanceStore.getAttendance(l99, lesson1));
    }

    @Test
    public void unregisteredLessonAttendanceNotRecorded() {
        try {
            connect(l1, "9999");
        } catch (Exception e) { 
            //expected
        } 
     
        Assert.assertNull(Main.attendanceStore.getAttendance(l1, "9999"));
    }

    @Test
    public void educatorConnectionVerificationProvided() {
        connect(edu, lesson1);
        connect(l1, lesson1);
        Assert.assertTrue(Main.attendanceStore.attendanceRegistered(edu,lesson1));
    }

    @Test
    public void educatorConnectionVerificationDenied() {
        connect(l1, lesson1);
        connect(l2, lesson1);
        Assert.assertFalse(Main.attendanceStore.attendanceRegistered(edu,lesson1));
    }
 
 
    // TODO - some code is duplicated accross these tests - need to review this

    @Test
    public void attendanceRemovedFromStoreAfterDisconnect(){
        TestClient c1 = connect(l1, lesson1);
        Assert.assertEquals(l1, Main.attendanceStore.getAttendance(l1, lesson1).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(l1, lesson1).getLessonID());
        
        disconnect(c1);
        Assert.assertNull(Main.attendanceStore.getAttendance(l1, lesson1));
    }

    @Test
    public void attendanceRemovedAndReaddedUponDisconnectAndReconnnect(){
        TestClient c1 = connect(l1, lesson1);
        disconnect(c1);
        
        connect(l1,lesson1);
        Assert.assertEquals(l1, Main.attendanceStore.getAttendance(l1, lesson1).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(l1, lesson1).getLessonID());
    }
    

    @Test
    public void otherAttendancesNotImpactedWhenAnotherIsRemoved(){
        TestClient c1 = connect(l1, lesson1);
        connect(l2, lesson1);
    
        Assert.assertEquals(l1, Main.attendanceStore.getAttendance(l1, lesson1).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(l1, lesson1).getLessonID());
        Assert.assertEquals(l2, Main.attendanceStore.getAttendance(l2, lesson1).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(l2, lesson1).getLessonID());
        
        disconnect(c1);
        Assert.assertNull(Main.attendanceStore.getAttendance(l1, lesson1));
        Assert.assertEquals(l2, Main.attendanceStore.getAttendance(l2, lesson1).getUserID());
        Assert.assertEquals(lesson1, Main.attendanceStore.getAttendance(l2, lesson1).getLessonID());
    }
}