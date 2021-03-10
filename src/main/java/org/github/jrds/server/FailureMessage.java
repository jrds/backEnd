package org.github.jrds.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FailureMessage extends Message {

    private String failureReason;

    @JsonCreator
    public FailureMessage(
            @JsonProperty("to") String to,
            @JsonProperty("failureReason") String failureReason) {
        super(null, to);
        this.failureReason = failureReason;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
