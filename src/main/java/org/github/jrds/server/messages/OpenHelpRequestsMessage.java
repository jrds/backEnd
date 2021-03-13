package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.domain.HelpRequest;
import org.github.jrds.server.dto.HelpRequestDto;

import java.util.List;
import java.util.Set;

public class OpenHelpRequestsMessage extends Message
{
    private final List<HelpRequestDto> openHelpRequests;

    public OpenHelpRequestsMessage(
            @JsonProperty("to") String to,
            @JsonProperty ("openHelpRequests") List<HelpRequestDto> openHelpRequests
    )
    {
        super(null, to);
        this.openHelpRequests = openHelpRequests;
    }

    public List<HelpRequestDto> getOpenHelpRequests()
    {
        return openHelpRequests;
    }
}
