package org.github.jrds.server;

import org.github.jrds.server.domain.Attendance;
import org.github.jrds.server.domain.LessonStructure;
import org.github.jrds.server.domain.User;

import java.util.Collections;

public class CodeSimulation
{
    public static void main(String[] args)
    {
        User u = new User("x", "ssss");
        LessonStructure lessonStructure = new LessonStructure("a", u, Collections.emptySet());
        Attendance attendance = new Attendance(u, lessonStructure);
        attendance.getCode().setCode("class Hello { public static void main(String[] args) { System.out.println(\"Hello World :)\"); } }");
        attendance.getCode().executeCode();
    }
}
