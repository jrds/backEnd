package org.github.jrds.server.extensions.help;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.messages.Request;

public class HelpRequest extends Request
{
    @JsonCreator
    public HelpRequest(@JsonProperty("from") String from)
    {
        super(from, null);
    }
}
