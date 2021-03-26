package org.github.jrds.server.extensions.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.messages.Info;

public class CompiledCodeMessage extends Info
{
    private String compilationStatus;
    private String compilationResult;
    // TODO - take the instant from the compiledCode obj, and convert to time, and send in the message

    @JsonCreator
    public CompiledCodeMessage(
            @JsonProperty("to") String to,
            @JsonProperty("compilationStatus") String compilationStatus,
            @JsonProperty("compilationResult") String compilationResult)
    {
        super(to);
        this.compilationStatus = compilationStatus;
        this.compilationResult = compilationResult;
    }

    public String getCompilationStatus()
    {
        return compilationStatus;
    }

    public String getCompilationResult()
    {
        return compilationResult;
    }
}
