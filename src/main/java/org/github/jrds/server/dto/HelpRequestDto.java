package org.github.jrds.server.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.Main;
import org.github.jrds.server.domain.HelpRequest;

import java.time.Instant;

public class HelpRequestDto
{
    private final String learnerId;
    private final String learnerName;
    private final Instant timeReceived;
    private final String status;

    @JsonCreator
    public HelpRequestDto(
            @JsonProperty("learnerId") String learnerId,
            @JsonProperty("learnerName") String learnerName,
            @JsonProperty("timeReceived") Instant timeReceived,
            @JsonProperty("status") String status)
    {
        this.learnerId = learnerId;
        this.timeReceived = timeReceived;
        this.status = status;
        this.learnerName = learnerName;
    }

    public HelpRequestDto(HelpRequest helpRequest)
    {
        this(helpRequest.getLearner().getId(), Main.defaultInstance.usersStore.getUser(helpRequest.getLearner().getId()).getName(),helpRequest.getTimeReceived(), helpRequest.getStatus().name());
    }

    public String getLearnerId()
    {
        return learnerId;
    }

    public String getLearnerName()
    {
        return learnerName;
    }

    public Instant getTimeReceived()
    {
        return timeReceived;
    }

    public String getStatus()
    {
        return status;
    }

    public HelpRequest asHelpRequest(String LessonId)
    {
        return Main.defaultInstance.activeLessonStore.getActiveLesson(LessonId).getOpenHelpRequests().get(this.learnerId);
    }
}
