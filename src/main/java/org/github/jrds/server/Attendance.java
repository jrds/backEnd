package org.github.jrds.server;


public class Attendance {
    private String userId; // TODO - will become just as User once that class is created
    private String lessonId;
    // private Instant time; // TODO DIFFICULT - concept of time, useful to have an Instant/Timestamp RE this
    // possible solution is having 2 constructors of attendances. 

    public Attendance(String userId, String lessonId) {
        this.userId = userId;
        this.lessonId = lessonId;
    }

     @Override
    public String toString() {
        return userId + lessonId;
    }

    public String getUserID() {
        return userId;
    }

    public String getLessonID() {
        return lessonId;
    }  

}
