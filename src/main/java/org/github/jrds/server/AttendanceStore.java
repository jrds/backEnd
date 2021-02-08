package org.github.jrds.server;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AttendanceStore {

    private Map<String, Attendance> attendanceStore;
    private Set<String[]> toBeReplaceWithDB;

    public AttendanceStore() {
        attendanceStore = new HashMap<>();
        toBeReplaceWithDB = new HashSet<>();
    }

    public void addAttendance(String userId, String lessonId){
        Attendance a = new Attendance(userId, lessonId);
        String key = a.toString();
        attendanceStore.put(key, a); 

        String[] attendanceToBeStored = {"Joined", userId, lessonId, (Instant.now().toString())};
        toBeReplaceWithDB.add(attendanceToBeStored);
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

    public void removeAttendance(Attendance attendance){
        String[] attendanceToBeStored = {"Disconnected", attendance.getUserID(), attendance.getLessonID(), (Instant.now().toString())};
        attendanceStore.remove(attendance.toString());
        toBeReplaceWithDB.add(attendanceToBeStored);
    }
}
