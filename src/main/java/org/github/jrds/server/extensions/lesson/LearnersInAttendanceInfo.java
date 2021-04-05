package org.github.jrds.server.extensions.lesson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.dto.InstructionDto;
import org.github.jrds.server.dto.UserDto;
import org.github.jrds.server.messages.Info;

import java.util.List;
import java.util.Objects;

public class LearnersInAttendanceInfo extends Info
{

    private List<UserDto> learners;

    @JsonCreator
    public LearnersInAttendanceInfo(
            @JsonProperty("to") String to,
            @JsonProperty("learners") List<UserDto> learners)
    {
        super(to);
        this.learners = Objects.requireNonNull(learners);
    }

    public List<UserDto> getLearners()
    {
        return learners;
    }

}