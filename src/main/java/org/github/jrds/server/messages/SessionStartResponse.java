package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionStartResponse extends SuccessResponse
{
    private String role;
    private String lessonState;

    @JsonCreator
    public SessionStartResponse(
            @JsonProperty("to") String to,
            @JsonProperty("id") int id,
            @JsonProperty("role") String role,
            @JsonProperty("lessonState") String lessonState)
    {
        super(to, id);
        this.role = role;
        this.lessonState = lessonState;
    }

    public String getRole()
    {
        return role;
    }

    public String getLessonState()
    {
        return lessonState;
    }
}
