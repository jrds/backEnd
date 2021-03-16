package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionStartMessage extends Request
{

    @JsonCreator
    public SessionStartMessage(@JsonProperty("from") String from)
    {
        super(from, null);
    }

    @Override
    public String toString()
    {
        return "SessionStartMessage [from=" + getFrom() + "]";
    }

}
