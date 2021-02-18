package org.github.jrds.server;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AttendanceStore {

    private Map<AttendanceKey, Attendance> attendanceStore;
    private Set<String[]> toBeReplaceWithDB;

    public AttendanceStore() {
        attendanceStore = new HashMap<>();
        toBeReplaceWithDB = new HashSet<>();
    }

    public void addAttendance(String userId, String lessonId){
        Attendance a = new Attendance(userId, lessonId);

        attendanceStore.put(keyFor(a), a); 

        String[] attendanceToBeStored = {"Joined", userId, lessonId, (Instant.now().toString())};
        toBeReplaceWithDB.add(attendanceToBeStored);
    }

    public Attendance getAttendance(String userId, String lessonId){
        return attendanceStore.get(keyFor(userId, lessonId));
    }

    private AttendanceKey keyFor(String userId, String lessonId){
        return new AttendanceKey(userId,lessonId);
    }

    
    private AttendanceKey keyFor(Attendance attendance){
        return keyFor(attendance.getUserID(),attendance.getLessonID());
    }

    // TODO - AFTER USER CREATED - these will be testing for the presence of a role or subClass of user once it's implemented. 
    // public Boolean educatorConnected(String lessonId){
    //     return attendanceStore.containsKey(keyFor("Educator", lessonId));
    // }

    // public Boolean specificLearnerConnected(String learnerId, String lessonId){
    //     return attendanceStore.containsKey(keyFor(learnerId, lessonId));
    // }

    public void removeAttendance(Attendance attendance){
        String[] attendanceToBeStored = {"Disconnected", attendance.getUserID(), attendance.getLessonID(), (Instant.now().toString())};
        
        attendanceStore.remove(keyFor(attendance));
        toBeReplaceWithDB.add(attendanceToBeStored);
    }

    public boolean attendanceRegistered(String userId, String lessonId){
        return attendanceStore.containsKey(keyFor(userId, lessonId));
    }

    private static class AttendanceKey {

        private final String userId;
        private final String lessonId;
    
    
        public AttendanceKey(String userId, String lessonId) {
            this.userId = userId;
            this.lessonId = lessonId;
        }
    
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((lessonId == null) ? 0 : lessonId.hashCode());
            result = prime * result + ((userId == null) ? 0 : userId.hashCode());
            return result;
        }
    
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            AttendanceKey other = (AttendanceKey) obj;
            if (lessonId == null) {
                if (other.lessonId != null)
                    return false;
            } else if (!lessonId.equals(other.lessonId))
                return false;
            if (userId == null) {
                if (other.userId != null)
                    return false;
            } else if (!userId.equals(other.userId))
                return false;
            return true;
        }   
    }


}


