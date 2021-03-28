package org.github.jrds.server.extensions.help;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.messages.Request;

public class CancelHelpRequest extends Request
{
    @JsonCreator
    public CancelHelpRequest(@JsonProperty ("leanerId") String leanerId)
    {
        super(leanerId, null);
    }
}
