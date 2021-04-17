package org.github.jrds.codi.messaging.av;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.codi.core.messages.Request;

import java.util.Objects;

public class AVOffer extends Request
{

    private final String type;
    private final String offer;

    @JsonCreator
    public AVOffer(
            @JsonProperty("from") String from,
            @JsonProperty("to") String to,
            @JsonProperty("type") String type,
            @JsonProperty("offer") String offer)
    {
        super(from, to);
        this.type = type;
        this.offer = Objects.requireNonNull(offer);
    }

    public String getType()
    {
        return type;
    }

    public String getOffer()
    {
        return offer;
    }

    @Override
    public String toString()
    {
        return "AVOffer [from=" + getFrom() + ", to=" + getTo() + ", type=" + getType() + ", offer=" + offer + "]";
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
        AVOffer that = (AVOffer) o;
        return type.equals(that.type) && offer.equals(that.offer);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), type, offer);
    }
}
