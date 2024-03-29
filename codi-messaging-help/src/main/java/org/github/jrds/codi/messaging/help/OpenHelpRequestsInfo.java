package org.github.jrds.codi.messaging.help;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.codi.core.dto.HelpRequestDto;
import org.github.jrds.codi.core.messages.Info;

import java.util.List;

public class OpenHelpRequestsInfo extends Info
{
    private final List<HelpRequestDto> openHelpRequests;

    @JsonCreator
    public OpenHelpRequestsInfo(
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
