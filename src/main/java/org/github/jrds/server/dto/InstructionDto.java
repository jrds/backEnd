package org.github.jrds.server.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.domain.Instruction;

import java.util.Objects;

public class InstructionDto
{

    private final String title;
    private final String body;
    private final String authorId;


    @JsonCreator
    public InstructionDto(@JsonProperty("title") String title,
                          @JsonProperty("body") String body,
                          @JsonProperty("authorId") String authorId)
    {
        this.title = Objects.requireNonNull(title);
        this.body = Objects.requireNonNull(body);
        this.authorId = Objects.requireNonNull(authorId);
    }

    public InstructionDto(Instruction instruction)
    {
        this(instruction.getTitle(), instruction.getBody(), instruction.getAuthor().getId());
    }

    public String getTitle()
    {
        return title;
    }

    public String getBody()
    {
        return body;
    }

    public String getAuthorId()
    {
        return authorId;
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
        InstructionDto that = (InstructionDto) o;
        return title.equals(that.title) && authorId.equals(that.authorId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(title, authorId);
    }
}