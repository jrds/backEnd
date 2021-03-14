package org.github.jrds.server.extensions.help;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.messages.Request;

public class CancelHelpRequestMessage extends Request
{
    @JsonCreator
    public CancelHelpRequestMessage(@JsonProperty ("leanerId") String leanerId)
    {
        super(leanerId, null);
    }
}
