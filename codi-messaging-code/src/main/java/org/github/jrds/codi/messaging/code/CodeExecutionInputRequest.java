package org.github.jrds.codi.messaging.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.codi.core.messages.Request;

public class CodeExecutionInputRequest extends Request
{
    private String input;

    @JsonCreator
    public CodeExecutionInputRequest(
            @JsonProperty("from") String from,
            @JsonProperty("input") String input)
    {
        super(from, null);
        this.input = input;
    }

    public String getInput()
    {
        return input;
    }
}
