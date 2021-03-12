package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "_type")

@JsonSubTypes({
        @JsonSubTypes.Type(value = ChatMessage.class, name = "chat"),
        @JsonSubTypes.Type(value = SessionEndMessage.class, name = "sessionEnd"),
        @JsonSubTypes.Type(value = InstructionMessage.class, name = "instruction"),
        @JsonSubTypes.Type(value = LessonStartMessage.class, name = "lessonStart")
})

public class Request extends Message
{

    private Response response = null;

    public Request(String to, String from)
    {
        super(to, from);
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
