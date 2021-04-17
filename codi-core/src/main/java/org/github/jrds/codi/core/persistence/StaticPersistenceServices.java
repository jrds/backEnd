package org.github.jrds.codi.core.persistence;

public class StaticPersistenceServices implements PersistenceServices
{
    private static ActiveLessonStore activeLessonStore;
    private static UsersStore usersStore;
    private static LessonStructureStore lessonStructureStore;

    static {
        reset();
    }

    public static void reset()
    {
        usersStore = new UsersStore();
        lessonStructureStore = new LessonStructureStore(usersStore);
        activeLessonStore = new ActiveLessonStore(lessonStructureStore);
    }

    @Override
    public UsersStore getUsersStore()
    {
        return usersStore;
    }

    @Override
    public ActiveLessonStore getActiveLessonStore()
    {
        return activeLessonStore;
    }

    @Override
    public LessonStructureStore getLessonStructureStore()
    {
        return lessonStructureStore;
    }
}
