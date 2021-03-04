package org.github.jrds.server;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AttendanceStore {

    private Map<AttendanceKey, Attendance> attendanceStore;
    private Set<String[]> toBeReplacedWithDB;

    public AttendanceStore() {
        attendanceStore = new HashMap<>();
        toBeReplacedWithDB = new HashSet<>();
    }

    public void addAttendance(String userId, String lessonId){
        Attendance a = new Attendance(userId, lessonId);

        attendanceStore.put(keyFor(a), a); 

        String[] attendanceToBeStored = {"Joined", userId, lessonId, a.getRole(), (Instant.now().toString())};
        toBeReplacedWithDB.add(attendanceToBeStored);
    }

    public Attendance getAttendance(String userId, String lessonId){
        return attendanceStore.get(keyFor(userId, lessonId));
    }

    private AttendanceKey keyFor(String userId, String lessonId){
        return new AttendanceKey(userId,lessonId);
    }

    
    private AttendanceKey keyFor(Attendance attendance){
        return keyFor(attendance.getUser().getId(),attendance.getLesson().getId());
    }

    // TODO - NEXT - Create tests for this 
    public Boolean educatorConnected(String educatorId, String lessonId){
        return attendanceStore.get(keyFor(educatorId, lessonId)).getRole().equals("EDUCATOR");
    }
 
    public Boolean specificLearnerConnected(String learnerId, String lessonId){
        return attendanceStore.get(keyFor(learnerId, lessonId)).getRole().equals("LEARNER");
    }

    public void removeAttendance(String userId, String lessonId){
        String[] attendanceToBeStored = {"Disconnected", userId, lessonId, (Instant.now().toString())};
        
        attendanceStore.remove(keyFor(userId, lessonId));
        toBeReplacedWithDB.add(attendanceToBeStored);
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


