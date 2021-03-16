package org.github.jrds.server.extensions.help;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.domain.Status;
import org.github.jrds.server.dto.HelpRequestDto;
import org.github.jrds.server.messages.Request;

public class UpdateHelpRequestStatusMessage extends Request
{
    private final String learnerId;
    private Status newStatus;

    @JsonCreator
    public UpdateHelpRequestStatusMessage(
            @JsonProperty ("from") String from,
            @JsonProperty ("learnerId") String learnerId,
            @JsonProperty ("status") Status newStatus
    )
    {
        super(from,null);
        this.learnerId = learnerId;
        this.newStatus = newStatus;
    }

    public String getLearnerId()
    {
        return learnerId;
    }

    public Status getNewStatus()
    {
        return newStatus;
    }
}
