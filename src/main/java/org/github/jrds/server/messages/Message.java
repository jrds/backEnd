package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import java.util.Objects;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "_type")

@JsonSubTypes({
        @Type(value = Request.class, name = "request"),
        @Type(value = Response.class, name = "response")
})

public abstract class Message {

    protected static int idCounter = 1;

    private String from;
    private String to;
    private int id;


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
        return id == message.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
