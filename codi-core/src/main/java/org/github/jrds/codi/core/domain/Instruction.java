package org.github.jrds.codi.core.domain;

import java.util.Objects;

public class Instruction
{

    private final String id;
    private final LessonStructure lessonStructure;
    private String title;
    private String body;
    private final User author;


    Instruction(String id, LessonStructure lessonStructure, String title, String body, User author)
    {
        this.id = Objects.requireNonNull(id);
        this.lessonStructure = Objects.requireNonNull(lessonStructure);
        this.title = Objects.requireNonNull(title);
        this.body = body;
        this.author = Objects.requireNonNull(author);
    }


    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getBody()
    {
        return body;
    }


    public void setBody(String body)
    {
        this.body = body;
    }

    public User getAuthor()
    {
        return author;
    }

    public void moveUp()
    {
        lessonStructure.moveUp(this);
    }

    public void moveDown()
    {
        lessonStructure.moveDown(this);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        Instruction that = (Instruction) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

    public String getId()
    {
        return id;
    }
}