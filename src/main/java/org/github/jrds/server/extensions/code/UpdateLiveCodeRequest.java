package org.github.jrds.server.extensions.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.messages.Request;

public class UpdateLiveCodeRequest extends Request
{
    private final String latestCode;

    @JsonCreator
    public UpdateLiveCodeRequest(
            @JsonProperty("from") String from,
            @JsonProperty("latestCode") String latestCode)
    {
        super(from, null);
        this.latestCode = latestCode;
    }

    public String getlatestCode()
    {
        return latestCode;
    }
}
