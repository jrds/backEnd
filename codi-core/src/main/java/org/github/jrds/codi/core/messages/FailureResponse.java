package org.github.jrds.codi.core.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FailureResponse extends Response
{

    private final String failureReason;

    @JsonCreator
    public FailureResponse(
            @JsonProperty("to") String to,
            @JsonProperty("failureReason") String failureReason,
            @JsonProperty("id") int id)
    {
        super(to, id);
        this.failureReason = failureReason;
    }

    public String getFailureReason()
    {
        return failureReason;
    }

    @Override
    public boolean isSuccess()
    {
        return false;
    }

    @Override
    public FailureResponse asFailure()
    {
        return this;
    }
}
