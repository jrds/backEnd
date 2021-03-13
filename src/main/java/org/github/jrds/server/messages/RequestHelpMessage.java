package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestHelpMessage extends Request
{
    @JsonCreator
    public RequestHelpMessage(@JsonProperty("from") String from)
    {
        super(from, null);
    }
}
