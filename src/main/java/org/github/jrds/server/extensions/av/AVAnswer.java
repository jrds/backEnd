package org.github.jrds.server.extensions.av;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.messages.Request;

import java.util.Objects;

public class AVAnswer extends Request
{

    private final String type;
    private final String answer;

    @JsonCreator
    public AVAnswer(
            @JsonProperty("from") String from,
            @JsonProperty("to") String to,
            @JsonProperty("type") String type,
            @JsonProperty("answer") String answer)
    {
        super(from, to);
        this.type = type;
        this.answer = Objects.requireNonNull(answer);
    }

    public String getType()
    {
        return type;
    }

    public String getAnswer()
    {
        return answer;
    }

    @Override
    public String toString()
    {
        return "AVAnswer [from=" + getFrom() + ", to=" + getTo() + ", type=" + getType() + ", answer=" + answer + "]";
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
        AVAnswer that = (AVAnswer) o;
        return type.equals(that.type) && answer.equals(that.answer);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), type, answer);
    }
}
