package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "_type")

@JsonSubTypes({
        @JsonSubTypes.Type(value = SessionStartResponseMessage.class),
        @JsonSubTypes.Type(value = SuccessMessage.class)
})

public class SuccessMessage extends Response
{

    @JsonCreator
    public SuccessMessage(@JsonProperty("to") String to, @JsonProperty("id") int id)
    {
        super(to, id);
    }

    @Override
    public boolean isSuccess()
    {
        return true;
    }

    @Override
    public FailureMessage asFailure()
    {
        throw new UnsupportedOperationException("Not a failure");
    }
}
