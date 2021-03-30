package org.github.jrds.server.extensions.help;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.domain.Status;
import org.github.jrds.server.messages.Request;

public class NewHelpRequest extends Request
{
    private Status status;

    @JsonCreator
    public NewHelpRequest(
            @JsonProperty("from") String from)
    {
        super(from, null);
        this.status = Status.NEW;
    }
}
