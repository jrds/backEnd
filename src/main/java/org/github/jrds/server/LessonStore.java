package org.github.jrds.server;

import java.util.HashMap;
import java.util.Map;

public class LessonStore {

    private Map<String, Lesson> lessonStore = new HashMap<>();

    public Lesson getLesson(String lessonId) {
        if (lessonStore.containsKey(lessonId)) {
            return lessonStore.get(lessonId);
        } else {
            return null;
        }
    }

    public void saveLesson(Lesson lesson) {
        lessonStore.put(lesson.getId(), lesson);
    }

}
