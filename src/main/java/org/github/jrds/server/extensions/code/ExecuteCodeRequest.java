package org.github.jrds.server.extensions.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.messages.Request;

public class ExecuteCodeRequest extends Request
{
    private final String codeToExecute;

    @JsonCreator
    public ExecuteCodeRequest(
            @JsonProperty("from") String from,
            @JsonProperty("codeToExecute") String codeToExecute)
    {
        super(from, null);
        this.codeToExecute = codeToExecute;
    }

    public String getCodeToExecute()
    {
        return codeToExecute;
    }
}
