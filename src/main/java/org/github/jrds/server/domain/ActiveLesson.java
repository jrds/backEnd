package org.github.jrds.server.domain;


import java.util.*;

public class ActiveLesson
{
    private final String id;
    private final LessonStructure associatedLessonStructure;

    public ActiveLesson(LessonStructure associatedLessonStructure)
    {
        this.id = associatedLessonStructure.getId();
        this.associatedLessonStructure = associatedLessonStructure;
    }

    public String getId()
    {
        return id;
    }

    public LessonStructure getAssociatedLessonStructure()
    {
        return associatedLessonStructure;
    }
}