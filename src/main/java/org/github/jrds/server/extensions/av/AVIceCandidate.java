package org.github.jrds.server.extensions.av;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.messages.Request;

import java.util.Objects;

public class AVIceCandidate extends Request
{

    private final String type;
    private final String content;

    @JsonCreator
    public AVIceCandidate(
            @JsonProperty("from") String from,
            @JsonProperty("to") String to,
            @JsonProperty("type") String type,
            @JsonProperty("content") String content)
    {
        super(from, to);
        this.type = type;
        this.content = Objects.requireNonNull(content);
    }

    public String getType()
    {
        return type;
    }

    public String getContent()
    {
        return content;
    }

    @Override
    public String toString()
    {
        return "AVIceCandidate [from=" + getFrom() + ", to=" + getTo() + ", type=" + getType() + ", content=" + content + "]";
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
        AVIceCandidate that = (AVIceCandidate) o;
        return type.equals(that.type) && content.equals(that.content);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), type, content);
    }
}
