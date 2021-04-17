package org.github.jrds.codi.messaging.help;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.codi.core.domain.Status;
import org.github.jrds.codi.core.messages.Request;

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
