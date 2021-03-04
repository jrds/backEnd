package org.github.jrds.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LessonStore {

    private Map<String, Lesson> lessonStore;

    public LessonStore(UsersStore usersStore) {
        lessonStore = new HashMap<>();

        // Mocked up lesson store for prototype 
        // These match to the values defined in ApplicationTest.java
        Set<User> learners2905 = new HashSet<User>();
        learners2905.addAll(Arrays.asList(new User[] {usersStore.getUser("u1900"), usersStore.getUser("u1901")}));  

        Set<User> learners5029 = new HashSet<User>();
        learners5029.addAll(Arrays.asList(new User[] {usersStore.getUser("u1900"), usersStore.getUser("u1901"), usersStore.getUser("u9999")}));  

        Lesson l1 = new Lesson("2905", usersStore.getUser("e0001"), learners2905);
        Lesson l2 = new Lesson("5029", usersStore.getUser("e0001"), learners5029);

        storeLesson(l1);
        storeLesson(l2);

    }

    public Lesson getLesson(String lessonId) {
        if (lessonStore.containsKey(lessonId)) {
            return lessonStore.get(lessonId);
        } else {
            return null;
        }
    }

    public void storeLesson(Lesson lesson) {
        lessonStore.put(lesson.getId(), lesson);
    }

    public Set<Lesson> getAllLessons() {
        return new HashSet<>(lessonStore.values());
    } 

}
