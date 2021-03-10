package org.github.jrds.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
