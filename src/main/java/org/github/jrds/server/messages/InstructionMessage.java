package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.domain.Instruction;
import org.github.jrds.server.domain.User;
import org.github.jrds.server.dto.InstructionDto;

import java.util.Objects;

public class InstructionMessage extends Request
{

    private final InstructionDto instruction;

    @JsonCreator
    public InstructionMessage(
            @JsonProperty("from") String from,
            @JsonProperty("to") String to,
            @JsonProperty("instruction") InstructionDto instruction)
    {
        super(from, to);
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
