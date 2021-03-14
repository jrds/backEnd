package org.github.jrds.server.extensions.lesson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.messages.Request;

public class LessonStartMessage extends Request
{

    @JsonCreator
    public LessonStartMessage(@JsonProperty("from") String from)
    {
        super(from, null);
    }

    @Override
    public String toString()
    {
        return "LessonStartMessage [from=" + getFrom() + "]";
    }

}
