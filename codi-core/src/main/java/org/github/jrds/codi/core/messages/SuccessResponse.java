package org.github.jrds.codi.core.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "_type")

@JsonSubTypes({
        @JsonSubTypes.Type(value = SessionStartResponse.class),
        @JsonSubTypes.Type(value = SuccessResponse.class)
})

public class SuccessResponse extends Response
{

    @JsonCreator
    public SuccessResponse(@JsonProperty("to") String to, @JsonProperty("id") int id)
    {
        super(to, id);
    }

    @Override
    public boolean isSuccess()
    {
        return true;
    }

    @Override
    public FailureResponse asFailure()
    {
        throw new UnsupportedOperationException("Not a failure");
    }
}
