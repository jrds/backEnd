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
        @Type(value = LessonStartMessage.class, name = "lessonStart"),
        @Type(value = SuccessMessage.class, name = "success"),
        @Type(value = FailureMessage.class, name = "failed")
})

public abstract class Message {

    private String from;
    private String to;
    private int id;
    private static int idCounter;

    public Message(String from, String to) {
        this.from = from;
        this.to = to;
        this.id = idCounter++;
    }

    public Message(String from, String to, int id) {
        this.from = from;
        this.to = to;
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(from, message.from) && Objects.equals(to, message.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
