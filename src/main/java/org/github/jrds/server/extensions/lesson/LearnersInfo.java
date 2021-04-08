package org.github.jrds.server.extensions.lesson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.dto.UserDto;
import org.github.jrds.server.messages.Info;

import java.util.List;
import java.util.Objects;

public class LearnersInfo extends Info
{

    private List<UserDto> learnersInAttendance;
    private List<UserDto> learnersExpected;


    @JsonCreator
    public LearnersInfo(
            @JsonProperty("to") String to,
            @JsonProperty("learnersInAttendance") List<UserDto> learnersInAttendance,
            @JsonProperty("learnersExpected") List<UserDto> learnersExpected)
    {
        super(to);
        this.learnersInAttendance = Objects.requireNonNull(learnersInAttendance);
        this.learnersExpected = Objects.requireNonNull(learnersExpected);
    }

    public List<UserDto> getLearnersInAttendance()
    {
        return learnersInAttendance;
    }

    public List<UserDto> getLearnersExpected()
    {
        return learnersExpected;
    }
}