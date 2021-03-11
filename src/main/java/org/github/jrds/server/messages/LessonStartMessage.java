package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.Message;

public class LessonStartMessage extends Message {



    @JsonCreator
    public LessonStartMessage(@JsonProperty("from") String from) {
        super(from, null);
    }

    @Override
    public String toString() {
        return "LessonStartMessage [from=" + getFrom() + "]";
    }

}
