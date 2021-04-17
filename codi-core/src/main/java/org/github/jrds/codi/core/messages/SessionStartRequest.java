package org.github.jrds.codi.core.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionStartRequest extends Request
{

    @JsonCreator
    public SessionStartRequest(@JsonProperty("from") String from)
    {
        super(from, null);
    }

    @Override
    public String toString()
    {
        return "SessionStartMessage [from=" + getFrom() + "]";
    }

}
