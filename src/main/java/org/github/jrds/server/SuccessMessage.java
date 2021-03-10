package org.github.jrds.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SuccessMessage extends Message {

    @JsonCreator
    public SuccessMessage(@JsonProperty("to") String to) {
        super(null, to);
    }

}
