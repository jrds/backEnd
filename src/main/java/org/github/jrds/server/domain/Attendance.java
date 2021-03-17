package org.github.jrds.server.domain;

import java.util.Objects;

public class Attendance
{
    private final User user;
    private final LessonStructure lessonStructure;
    private final Role role;

    public Attendance(User user, LessonStructure lessonStructure)
    {
        this.user = Objects.requireNonNull(user, "Invalid user");
        this.lessonStructure = Objects.requireNonNull(lessonStructure, "Invalid lesson");
        this.role = lessonStructure.getUserRole(user);

        if (this.role == Role.NONE)
        {
            throw new IllegalArgumentException("User cannot attend this lesson");
        }
        // TODO - Make tests for role.        
    }

    public User getUser()
    {
        return user;
    }

    public LessonStructure getLesson()
    {
        return lessonStructure;
    }

    public Role getRole()
    {
        return role;
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
        Attendance that = (Attendance) o;
        return user.equals(that.user) && lessonStructure.equals(that.lessonStructure);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(user, lessonStructure);
    }
}
