package org.github.jrds.server.persistence;

import org.github.jrds.server.domain.ActiveLesson;
import org.github.jrds.server.domain.LessonStructure;
import org.github.jrds.server.domain.User;

import java.util.*;

public class ActiveLessonStore
{

    private final Map<String, ActiveLesson> activeLessonStore;

    public ActiveLessonStore(LessonStructureStore lessonStructureStore)
    {
        activeLessonStore = new HashMap<>();

        ActiveLesson aL1 = new ActiveLesson(lessonStructureStore.getLessonStructure("2905"));
        ActiveLesson al2 = new ActiveLesson(lessonStructureStore.getLessonStructure("5029"));

        storeActiveLesson(aL1);
        storeActiveLesson(al2);
    }

    public ActiveLesson getActiveLesson(String lessonId)
    {
        if (activeLessonStore.containsKey(lessonId))
        {
            return activeLessonStore.get(lessonId);
        }
        else
        {
            return null;
        }
    }

    public void storeActiveLesson(ActiveLesson activeLesson)
    {
        activeLessonStore.put(activeLesson.getId(), activeLesson);
    }

    public Set<ActiveLesson> getAllActiveLessons()
    {
        return new HashSet<>(activeLessonStore.values());
    }

}
