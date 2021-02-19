package org.github.jrds.server;

import java.util.HashMap;
import java.util.Map;

public class LessonStore {

    Map<String, Lesson> lessonStore;

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

    public LessonStore() {
        lessonStore = new HashMap<>();
    }

    public Map<String, Lesson> getLessonStore() {
        return lessonStore;
    } 
}
