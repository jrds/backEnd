package org.github.jrds.codi.messaging.help;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.codi.core.messages.Request;

public class CancelHelpRequest extends Request
{
    @JsonCreator
    public CancelHelpRequest(@JsonProperty ("learnerId") String leanerId)
    {
        super(leanerId, null);
    }
}
