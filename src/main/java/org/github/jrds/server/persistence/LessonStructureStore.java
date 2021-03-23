package org.github.jrds.server.persistence;

import org.github.jrds.server.domain.Instruction;
import org.github.jrds.server.domain.LessonStructure;
import org.github.jrds.server.domain.User;

import java.util.*;

public class LessonStructureStore
{

    private final Map<String, LessonStructure> lessonStructureStore;

    public LessonStructureStore(UsersStore usersStore)
    {
        lessonStructureStore = new HashMap<>();

        // Mocked up lesson store for prototype 
        // These match to the values defined in ApplicationTest.java
        Set<User> learners2905 = new HashSet<>(Arrays.asList(usersStore.getUser("u1900"), usersStore.getUser("u1901")));
        Set<User> learners5029 = new HashSet<>(Arrays.asList(usersStore.getUser("u1900"), usersStore.getUser("u1901"), usersStore.getUser("u9999")));

        LessonStructure l1 = new LessonStructure("2905", usersStore.getUser("e0001"), learners2905);
        LessonStructure l2 = new LessonStructure("5029", usersStore.getUser("e0001"), learners5029);

        storeLessonStructure(l1);
        storeLessonStructure(l2);

        l1.createInstruction("The Circle App", "In today's practical we will be developing a small java application. \n" +
                                                         "This application will be used to calculate the diameter and area of a circle, when given it's radius. \n" +
                                                         "Remember: \n" +
                                                         "    Diameter of a circle = 2 x Radius \n" +
                                                         "    Area of a circle = \u03C0r\u00B2 \n" +
                                                         "    Use a value of 3.142 for pi(\u03C0).", usersStore.getUser("e0001"));
        l1.createInstruction("1. Create a class",  "Create a public class called CirleApp. \n" +
                                                             "Make sure you create a main method for the class \n", usersStore.getUser("e0001"));
        l1.createInstruction("2. Declare the necessary variables", "", usersStore.getUser("e0001"));
        l1.createInstruction("3. Declare a Scanner object", "", usersStore.getUser("e0001"));
        l1.createInstruction("4. Write the code framework", "", usersStore.getUser("e0001"));
        l1.createInstruction("5. Write code to take user input", "", usersStore.getUser("e0001"));
        l1.createInstruction("6. Write code to use the input", "", usersStore.getUser("e0001"));
        l1.createInstruction("6. Write code to use the input and the constant together", "", usersStore.getUser("e0001"));
    }

    public LessonStructure getLessonStructure(String lessonId)
    {
        return lessonStructureStore.getOrDefault(lessonId, null);
    }

    public void storeLessonStructure(LessonStructure lessonStructure)
    {
        lessonStructureStore.put(lessonStructure.getId(), lessonStructure);
    }

    public Set<LessonStructure> getAllLessonStructures()
    {
        return new HashSet<>(lessonStructureStore.values());
    }

}
