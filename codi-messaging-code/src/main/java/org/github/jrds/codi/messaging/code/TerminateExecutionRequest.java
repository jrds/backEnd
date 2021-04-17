package org.github.jrds.codi.messaging.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.codi.core.messages.Request;

public class TerminateExecutionRequest extends Request
{
    @JsonCreator
    public TerminateExecutionRequest(@JsonProperty("from") String from)
    {
        super(from, null);
    }
}
