package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SuccessMessage extends Response {

    @JsonCreator
    public SuccessMessage(@JsonProperty("to") String to, @JsonProperty("id") int id) {
        super(to, id);
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public FailureMessage asFailure() {
        throw new UnsupportedOperationException("Not a failure");
    }
}
