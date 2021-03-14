package org.github.jrds.server.extensions.lesson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.dto.InstructionDto;
import org.github.jrds.server.messages.Info;
import org.github.jrds.server.messages.Request;

import java.util.Objects;

public class InstructionMessage extends Info
{

    private final InstructionDto instruction;

    @JsonCreator
    public InstructionMessage(
            @JsonProperty("to") String to,
            @JsonProperty("instruction") InstructionDto instruction)
    {
        super(to);
        this.instruction = Objects.requireNonNull(instruction);
    }

    public InstructionDto getInstruction()
    {
        return instruction;
    }

    @Override
    public String toString()
    {
        return "InstructionMessage [ from = " + getFrom() + ", to = " + getTo() + ", instruction = " + instruction + " ]";
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
        return Objects.equals(instruction, that.instruction);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), instruction);
    }
}
