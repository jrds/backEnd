package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionEndRequest extends Request
{

    @JsonCreator
    public SessionEndRequest(@JsonProperty("from") String from)
    {
        super(from, null);
    }

    @Override
    public String toString()
    {
        return "SessionEndMessage [from=" + getFrom() + "]";
    }

}
