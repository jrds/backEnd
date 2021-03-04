package org.github.jrds.server;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AttendanceStore {

    private Map<AttendanceKey, Attendance> attendanceStore;
    private Set<String[]> toBeReplacedWithDB;

    public AttendanceStore() {
        attendanceStore = new HashMap<>();
        toBeReplacedWithDB = new HashSet<>();
    }

    public void storeAttendance(Attendance attendance){
        attendanceStore.put(keyFor(attendance), attendance); 

        String[] attendanceToBeStored = {"Joined", attendance.getUser().getId(), attendance.getLesson().getId(), attendance.getRole().toString(), (Instant.now().toString())};
        toBeReplacedWithDB.add(attendanceToBeStored);
    }

    public Attendance getAttendance(User user, Lesson lesson){
        return attendanceStore.get(keyFor(user.getId(), lesson.getId()));
    }

    public Set<Attendance> getAllAttendances(){
        return new HashSet<>(attendanceStore.values());
    }
    
    public Set<Attendance> getAttendancesForALesson(Lesson lesson){
        return attendanceStore.values().stream()
            .filter(a -> lesson.equals(a.getLesson()))
            .collect(Collectors.toSet());
    }

    private AttendanceKey keyFor(String userId, String lessonId){
        return new AttendanceKey(userId,lessonId);
    }

    
    private AttendanceKey keyFor(Attendance attendance){
        return keyFor(attendance.getUser().getId(),attendance.getLesson().getId());
    }

    public void removeAttendance(Attendance attendance){
        String[] attendanceToBeStored = {"Disconnected", attendance.getUser().getId(), attendance.getLesson().getId(), (Instant.now().toString())};
        
        attendanceStore.remove(keyFor(attendance));
        toBeReplacedWithDB.add(attendanceToBeStored);
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


