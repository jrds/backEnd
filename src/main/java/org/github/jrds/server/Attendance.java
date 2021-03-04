package org.github.jrds.server;


public class Attendance {
    private User user; 
    private Lesson lesson; 
    private String role;  

    public Attendance(String userId, String lessonId) {
        this.user = Main.usersStore.getUser(userId);
        this.lesson = Main.lessonStore.getLesson(lessonId); // TODO - QUESTION 1 - is it okay that this is accessing lesson store like this?
                                                            // Lesson store is created before attendance store in main - so it wont fail there.
                                                            // I thought that the client should just be passing in the lesson ID
                                                            //they don't/shouldn't need to know what a lesson is.
        if (user.getId().startsWith("u") || user.getId().startsWith("U")){
            this.role = "LEARNER";
        }
        else if (user.getId().startsWith("e") || user.getId().startsWith("E")){
            this.role = "EDUCATOR";
        }
        else {
            System.out.println("User ID isn't correclty formatted");
        }
        // TODO AFTER USER - Make tests for role.
            
    }

    public User getUser() {
        return user;
    }

    public Lesson getLesson() {
        return lesson;
    }  

    public String getRole(){
        return role;
    }
}
