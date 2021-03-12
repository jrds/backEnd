package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.domain.Instruction;
import org.github.jrds.server.domain.User;

import java.util.Objects;

public class InstructionMessage extends Request
{

    private final String title;
    private final String body;

    public InstructionMessage(User educator, User learner, Instruction instruction)
    {
        this(educator.getId(), learner.getId(), instruction.getTitle(), instruction.getBody());
    }

    @JsonCreator
    public InstructionMessage(
            @JsonProperty("from") String from,
            @JsonProperty("to") String to,
            @JsonProperty("title") String title,
            @JsonProperty("body") String body)
    {
        super(from, to);
        this.title = Objects.requireNonNull(title);
        this.body = Objects.requireNonNull(body);
    }

    public String getTitle()
    {
        return title;
    }

    public String getBody()
    {
        return body;
    }

    @Override
    public String toString()
    {
        return "InstructionMessage [ from = " + getFrom() + ", to = " + getTo() + ", title = " + title + ", body = " + body + " ]";
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
        InstructionMessage that = (InstructionMessage) o;
        return title.equals(that.title);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), title);
    }
}
