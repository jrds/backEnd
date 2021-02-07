package org.github.jrds.server;

import java.util.HashMap;
import java.util.Map;

public class AttendanceStore {

    private Map<String, Attendance> attendanceStore;
    // TODO think about/consider if 2 construcors of attendance then:
    // Key attendance = (userId, lessonId)
    // Value attendance = (userId, lessonId, instance)

    public AttendanceStore() {
        attendanceStore = new HashMap<>();
    }

    public void addAttendance(String userId, String lessonId){
        Attendance a = new Attendance(userId, lessonId);
        String key = a.toString();
        attendanceStore.put(key, a); 
    }

    public Attendance getAttendance(String key){
        return attendanceStore.get(key);
    }

    // TODO next
	// public Attendance getAttendance(String userID, String lessonID) {
	// 	return attendanceStore.;
	// }

    // TODO - CONSIDER - is attendance a documentation of each connection to a lesson or just 1 per lesson ? 
    // I don't think I want to remove attendance if someone leaves the session ?
}
