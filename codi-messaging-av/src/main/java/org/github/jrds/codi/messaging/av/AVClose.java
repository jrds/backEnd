package org.github.jrds.codi.messaging.av;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.codi.core.messages.Request;

import java.util.Objects;

public class AVClose extends Request
{

    private final String type;

    @JsonCreator
    public AVClose(
            @JsonProperty("from") String from,
            @JsonProperty("to") String to,
            @JsonProperty("type") String type)
    {
        super(from, to);
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return "AVClose [from=" + getFrom() + ", to=" + getTo() + ", type=" + getType() + "]";
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
        if (!super.equals(o))
        {
            return false;
        }
        AVClose that = (AVClose) o;
        return type.equals(that.type);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), type);
    }
}
