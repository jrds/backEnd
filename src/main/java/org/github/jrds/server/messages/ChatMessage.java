package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ChatMessage extends Request
{

    private final String text;

    @JsonCreator
    public ChatMessage(
            @JsonProperty("from") String from,
            @JsonProperty("to") String to,
            @JsonProperty("text") String text)
    {
        super(from, to);
        this.text = Objects.requireNonNull(text);
    }

    public String getText()
    {
        return text;
    }

    @Override
    public String toString()
    {
        return "ChatMessage [from=" + getFrom() + ", to=" + getTo() + ", text=" + text + "]";
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
        ChatMessage that = (ChatMessage) o;
        return text.equals(that.text);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), text);
    }
}
