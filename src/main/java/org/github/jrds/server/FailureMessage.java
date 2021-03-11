package org.github.jrds.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FailureMessage extends Message {

    private String failureReason;

    @JsonCreator
    public FailureMessage(
            @JsonProperty("to") String to,
            @JsonProperty("failureReason") String failureReason,
            @JsonProperty("id") int id)
    {
        super(null, to, id);
        this.failureReason = failureReason;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
