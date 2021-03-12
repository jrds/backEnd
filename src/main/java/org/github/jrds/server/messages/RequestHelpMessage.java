package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class RequestHelpMessage extends Request
{

    private Status status;
    private Instant timeEducatorReceivedRequest;

    @JsonCreator
    public RequestHelpMessage(@JsonProperty("to") String to,@JsonProperty("from") String from)
    {
        super(to, from);
        status = Status.ACTIVE;
    }
    // TODO - i think some messages are (to, from) and others are (from, to)

    @Override
    public String toString()
    {
        return "SessionEndMessage [from=" + getFrom() + "]";
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public Instant getTimeEducatorReceivedRequest()
    {
        return timeEducatorReceivedRequest;
    }

    public void setTimeEducatorReceivedRequest(Instant timeEducatorReceivedRequest)
    {
        this.timeEducatorReceivedRequest = timeEducatorReceivedRequest;
    }

}
