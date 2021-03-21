package org.github.jrds.server.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.Main;
import org.github.jrds.server.domain.Role;

public class SessionStartResponseMessage extends SuccessMessage
{
    private String role;

    @JsonCreator
    public SessionStartResponseMessage(
            @JsonProperty("to") String to,
            @JsonProperty("id") int id,
            @JsonProperty("role") String role)
    {
        super(to, id);
        this.role = role;
    }

    public String getRole()
    {
        return role;
    }
}
