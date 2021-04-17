package org.github.jrds.codi.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.codi.core.domain.HelpRequest;
import org.github.jrds.codi.core.persistence.PersistenceServices;

import java.time.Instant;
import java.util.ServiceLoader;

public class HelpRequestDto
{
    private static final PersistenceServices persistenceServices = ServiceLoader.load(PersistenceServices.class).findFirst().orElseThrow();

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
        this(helpRequest.getLearner().getId(), persistenceServices.getUsersStore().getUser(helpRequest.getLearner().getId()).getName(),helpRequest.getTimeReceived(), helpRequest.getStatus().name());
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
        return persistenceServices.getActiveLessonStore().getActiveLesson(LessonId).getOpenHelpRequests().get(this.learnerId);
    }
}
