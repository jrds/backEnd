package org.github.jrds.codi.core.persistence;

public interface PersistenceServices {
    UsersStore getUsersStore();
    LessonStructureStore getLessonStructureStore();
    ActiveLessonStore getActiveLessonStore();
}
