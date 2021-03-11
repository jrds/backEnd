package org.github.jrds.server.domain;

import java.util.Objects;

public class Attendance {
    private User user;
    private Lesson lesson;
    private Role role;

    public Attendance(User user, Lesson lesson) {
        this.user = Objects.requireNonNull(user, "Invalid user");
        this.lesson = Objects.requireNonNull(lesson, "Invalid lesson"); 
        this.role = lesson.getUserRole(user);

        if (this.role == Role.NONE){
            throw new IllegalArgumentException("User cannot attend this lesson");
        }
        // TODO - Make tests for role.        
    }

    public User getUser() {
        return user;
    }

    public Lesson getLesson() {
        return lesson;
    }  

    public Role getRole(){
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendance that = (Attendance) o;
        return user.equals(that.user) && lesson.equals(that.lesson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, lesson);
    }
}
