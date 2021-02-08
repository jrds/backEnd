package org.github.jrds.server;

import java.util.HashMap;
import java.util.Map;

public class AttendanceStore {

    private Map<String, Attendance> attendanceStore;
    // TODO CONSIDER if 2 construcors of attendance then:
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

    public Boolean educatorConnected(String lessonId){
        String key = "Educator" + lessonId;
        return attendanceStore.containsKey(key);
    }

    public Boolean specificLearnerConnected(String learnerId, String lessonId){
        String key = learnerId + lessonId;
        return attendanceStore.containsKey(key);
    }

    // TODO - CONSIDER - is attendance a documentation of each connection to a lesson or just 1 per lesson ? 
    // I don't think I want to remove attendance if someone leaves the session ?
}
