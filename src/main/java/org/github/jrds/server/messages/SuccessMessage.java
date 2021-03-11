package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.Message;

public class SuccessMessage extends Message {

    @JsonCreator
    public SuccessMessage(@JsonProperty("to") String to, @JsonProperty("id") int id) {
        super(null, to, id);
    }

}
