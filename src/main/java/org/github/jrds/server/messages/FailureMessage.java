package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FailureMessage extends Response {

    private String failureReason;

    @JsonCreator
    public FailureMessage(
            @JsonProperty("to") String to,
            @JsonProperty("failureReason") String failureReason,
            @JsonProperty("id") int id)
    {
        super(to, id);
        this.failureReason = failureReason;
    }

    public String getFailureReason() {
        return failureReason;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public FailureMessage asFailure() {
        return this;
    }
}
