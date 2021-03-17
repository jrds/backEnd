package org.github.jrds.server.extensions.help;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.dto.HelpRequestDto;
import org.github.jrds.server.messages.Info;

import java.util.List;

public class OpenHelpRequestsMessage extends Info
{
    private final List<HelpRequestDto> openHelpRequests;

    @JsonCreator
    public OpenHelpRequestsMessage(
            @JsonProperty("to") String to,
            @JsonProperty("openHelpRequests") List<HelpRequestDto> openHelpRequests
    )
    {
        super(to);
        this.openHelpRequests = openHelpRequests;
    }

    public List<HelpRequestDto> getOpenHelpRequests()
    {
        return openHelpRequests;
    }
}
