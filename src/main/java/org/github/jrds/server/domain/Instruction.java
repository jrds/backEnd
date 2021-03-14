package org.github.jrds.server.domain;

import java.util.Objects;

public class Instruction
{

    private String id;
    private Lesson lesson;
    private String title;
    private String body;
    private final User author;


    Instruction(String id, Lesson lesson, String title, String body, User author)
    {
        this.id = Objects.requireNonNull(id);
        this.lesson = Objects.requireNonNull(lesson);
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
        lesson.moveUp(this);
    }

    public void moveDown()
    {
        lesson.moveDown(this);
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