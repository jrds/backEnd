package org.github.jrds.server.extensions.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.messages.Info;

public class CompiledCodeMessage extends Info
{
    private String compilationStatus;
    private String compilationResult; //TODO output rather than result
    private String timeCompiled;
    // TODO - take the instant from the compiledCode obj, and convert to time, and send in the message

    // TODO - Write up Future feature for report, visualisation of number of compiler errors over time, and repeated errors.

    @JsonCreator
    public CompiledCodeMessage(
            @JsonProperty("to") String to,
            @JsonProperty("compilationStatus") String compilationStatus,
            @JsonProperty("compilationResult") String compilationResult,
            @JsonProperty("timeCompiled") String timeCompiled)
    {
        super(to);
        this.compilationStatus = compilationStatus;
        this.compilationResult = compilationResult;
        this.timeCompiled = timeCompiled;
    }

    public String getCompilationStatus()
    {
        return compilationStatus;
    }

    public String getCompilationResult()
    {
        return compilationResult;
    }

    public String getTimeCompiled()
    {
        return timeCompiled;
    }
}
