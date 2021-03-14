package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.messages.Request;

public class SessionEndMessage extends Request
{

    @JsonCreator
    public SessionEndMessage(@JsonProperty("from") String from)
    {
        super(from, null);
    }

    @Override
    public String toString()
    {
        return "SessionEndMessage [from=" + getFrom() + "]";
    }

}
