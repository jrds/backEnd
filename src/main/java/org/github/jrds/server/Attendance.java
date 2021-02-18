package org.github.jrds.server;


public class Attendance {
    private String userId; // TODO - will become just as User once that class is created
    private String lessonId; //TODO - NEXT - use the lesson object not string.
    //TODO - WHEN USER IS MADE - add role, which would be learner or educator, queried from the lesson.


    public Attendance(String userId, String lessonId) {
        this.userId = userId;
        this.lessonId = lessonId;
    }

    public String getUserID() {
        return userId;
    }

    public String getLessonID() {
        return lessonId;
    }  

}
