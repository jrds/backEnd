package org.github.jrds.server.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.domain.HelpRequest;
import org.github.jrds.server.domain.Instruction;
import org.github.jrds.server.domain.Status;

import java.time.Instant;
import java.util.Objects;

public class HelpRequestDto
{
    private final String learnerId;
    private final Instant timeReceived;
    private final String status;

    @JsonCreator
    public HelpRequestDto(
            @JsonProperty("learnerId") String learnerId,
            @JsonProperty("timeReceived") Instant timeReceived,
            @JsonProperty("status") String status)
    {


        this.learnerId = learnerId;
        this.timeReceived = timeReceived;
        this.status = status;
    }

    public HelpRequestDto(HelpRequest helpRequest)
    {
        this(helpRequest.getLearner().getId(), helpRequest.getTimeReceived(), helpRequest.getStatus().name());
    }

    public HelpRequestDto(String learnerId)
    {
        this.learnerId = learnerId;
        this.timeReceived = null;
        this.status = null;
    }


    public String getLearnerId()
    {
        return learnerId;
    }

    public Instant getTimeReceived()
    {
        return timeReceived;
    }

    public String getStatus()
    {
        return status;
    }
}
