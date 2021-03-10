package org.github.jrds.server;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import java.util.Objects;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "_type")
@JsonSubTypes({
        @Type(value = ChatMessage.class, name = "chat"),
        @Type(value = SessionEndMessage.class, name = "sessionEnd"),
        @Type(value = InstructionMessage.class, name = "instruction"),
        @Type(value = LessonStartMessage.class, name = "lessonStart")
})

public abstract class Message {

    private String from;
    private String to;

    public Message(String from, String to) {
        this.from = Objects.requireNonNull(from);
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return from.equals(message.from) && Objects.equals(to, message.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
