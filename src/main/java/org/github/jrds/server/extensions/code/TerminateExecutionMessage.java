package org.github.jrds.server.extensions.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.messages.Request;

public class TerminateExecutionMessage extends Request
{
    @JsonCreator
    public TerminateExecutionMessage(@JsonProperty("from") String from)
    {
        super(from, null);
    }
}
