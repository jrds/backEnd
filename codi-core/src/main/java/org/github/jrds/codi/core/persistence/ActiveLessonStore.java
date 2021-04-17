package org.github.jrds.codi.core.persistence;

import org.github.jrds.codi.core.domain.ActiveLesson;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        return activeLessonStore.getOrDefault(lessonId, null);
        // TODO IntelliJ suggested getOrDefault instead of if, else. Check how this works
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
