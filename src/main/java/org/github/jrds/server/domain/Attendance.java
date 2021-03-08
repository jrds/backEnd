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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lesson == null) ? 0 : lesson.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
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
        Attendance other = (Attendance) obj;
        if (lesson == null) {
            if (other.lesson != null)
                return false;
        } else if (!lesson.equals(other.lesson))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }

}
