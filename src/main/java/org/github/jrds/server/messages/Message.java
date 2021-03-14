package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "_type")

@JsonSubTypes({@JsonSubTypes.Type(Request.class),@JsonSubTypes.Type(Response.class),@JsonSubTypes.Type(Info.class)})

public abstract class Message
{

    protected static AtomicInteger idCounter = new AtomicInteger(0);

    private final String from;
    private final String to;
    private final int id;


    public Message(String from, String to)
    {
        this.from = from;
        this.to = to;
        this.id = idCounter.incrementAndGet();
    }

    public Message(String from, String to, int id)
    {
        this.from = from;
        this.to = to;
        this.id = id;
    }

    public String getFrom()
    {
        return from;
    }

    public String getTo()
    {
        return to;
    }

    public int getId()
    {
        return id;
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
        Message message = (Message) o;
        return id == message.id;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
