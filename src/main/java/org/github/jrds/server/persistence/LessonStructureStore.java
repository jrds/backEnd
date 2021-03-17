package org.github.jrds.server.persistence;

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
        Set<User> learners2905 = new HashSet<User>();
        learners2905.addAll(Arrays.asList(usersStore.getUser("u1900"), usersStore.getUser("u1901")));

        Set<User> learners5029 = new HashSet<User>();
        learners5029.addAll(Arrays.asList(usersStore.getUser("u1900"), usersStore.getUser("u1901"), usersStore.getUser("u9999")));

        LessonStructure l1 = new LessonStructure("2905", usersStore.getUser("e0001"), learners2905);
        LessonStructure l2 = new LessonStructure("5029", usersStore.getUser("e0001"), learners5029);

        storeLessonStructure(l1);
        storeLessonStructure(l2);

    }

    public LessonStructure getLessonStructure(String lessonId)
    {
        if (lessonStructureStore.containsKey(lessonId))
        {
            return lessonStructureStore.get(lessonId);
        }
        else
        {
            return null;
        }
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
