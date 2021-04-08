package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.github.jrds.server.extensions.lesson.LearnersInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "_type")

@JsonSubTypes({
        @JsonSubTypes.Type(value = LearnerLessonStateInfo.class),
        @JsonSubTypes.Type(value = LearnersInfo.class)
})

public abstract class Info extends Message
{

    public Info(String to)
    {
        super(null, to);
    }
}
