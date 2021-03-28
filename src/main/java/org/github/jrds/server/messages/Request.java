package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "_type")

@JsonSubTypes({
        @JsonSubTypes.Type(value = SessionStartRequest.class),
        @JsonSubTypes.Type(value = SessionEndRequest.class)
})

public abstract class Request extends Message
{

    private Response response = null;

    public Request(String from, String to)
    {
        super(from, to);
    }

    public Response getResponse()
    {
        return response;
    }

    public void setResponse(Response response)
    {
        this.response = response;
    }
}
