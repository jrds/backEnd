package org.github.jrds.server.persistence;

import org.github.jrds.server.domain.Attendance;
import org.github.jrds.server.domain.LessonStructure;
import org.github.jrds.server.domain.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class AttendanceStore
{

    private final Map<AttendanceKey, Attendance> attendanceStore;
    private final Set<String[]> toBeReplacedWithDB;

    public AttendanceStore()
    {
        attendanceStore = new HashMap<>();
        toBeReplacedWithDB = new HashSet<>();
    }

    public void storeAttendance(Attendance attendance)
    {
        attendanceStore.put(keyFor(attendance), attendance);

        String[] attendanceToBeStored = {"Joined", attendance.getUser().getId(), attendance.getLesson().getId(), attendance.getRole().toString(), (Instant.now().toString())};
        toBeReplacedWithDB.add(attendanceToBeStored);
    }

    public Attendance getAttendance(User user, LessonStructure lessonStructure)
    {
        return attendanceStore.get(keyFor(user.getId(), lessonStructure.getId()));
    }

    public Set<Attendance> getAllAttendances()
    {
        return new HashSet<>(attendanceStore.values());
    }

    public Set<Attendance> getAttendancesForALesson(LessonStructure lessonStructure)
    {
        return attendanceStore.values().stream()
                .filter(a -> lessonStructure.equals(a.getLesson()))
                .collect(Collectors.toSet());
    }

    private AttendanceKey keyFor(String userId, String lessonId)
    {
        return new AttendanceKey(userId, lessonId);
    }


    private AttendanceKey keyFor(Attendance attendance)
    {
        return keyFor(attendance.getUser().getId(), attendance.getLesson().getId());
    }

    public void removeAttendance(Attendance attendance)
    {
        String[] attendanceToBeStored = {"Disconnected", attendance.getUser().getId(), attendance.getLesson().getId(), (Instant.now().toString())};

        attendanceStore.remove(keyFor(attendance));
        toBeReplacedWithDB.add(attendanceToBeStored);
    }

    private static class AttendanceKey
    {

        private final String userId;
        private final String lessonId;


        public AttendanceKey(String userId, String lessonId)
        {
            this.userId = Objects.requireNonNull(userId);
            this.lessonId = Objects.requireNonNull(lessonId);
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            AttendanceKey that = (AttendanceKey) o;
            return userId.equals(that.userId) && lessonId.equals(that.lessonId);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(userId, lessonId);
        }
    }


}


