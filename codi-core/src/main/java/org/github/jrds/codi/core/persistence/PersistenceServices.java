package org.github.jrds.codi.core.persistence;

import org.github.jrds.codi.core.domain.Attendance;

public interface PersistenceServices {
    UsersStore getUsersStore();
    LessonStructureStore getLessonStructureStore();
    ActiveLessonStore getActiveLessonStore();
}
