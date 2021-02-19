package org.github.jrds.server;


public class Attendance {
    private String userId; // TODO - will become just as User once that class is created
    private Lesson lesson; //TODO - NEXT - use the lesson object not string.
    //TODO - WHEN USER IS MADE - add role, which would be learner or educator, queried from the lesson.


    public Attendance(String userId, String lessonId) {
        this.userId = userId;
        this.lesson = Main.lessonStore.getLesson(lessonId); // TODO - QUESTION 1 - is it okay that this is accessing lesson store like this?
                                                            // Lesson store is created before attendance store in main - so it wont fail there.
                                                            // I thought that the client should just be passing in the lesson ID
                                                            //they don't/shouldn't need to know what a lesson is.
    }

    public String getUserID() {
        return userId;
    }

    public Lesson getLesson() {
        return lesson;
    }  
}
