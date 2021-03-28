package org.github.jrds.server.extensions.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.messages.Info;

public class CodeExecutionInfo extends Info
{
    private String executionStatus;
    private String executionOutput;
    private String executionErrorOutput;
    private String executionEventTime;
    // TODO - Write up Future feature for report, visualisation of number of compiler errors over time, and repeated errors.

    @JsonCreator
    public CodeExecutionInfo(
            @JsonProperty("to") String to,
            @JsonProperty("executionStatus") String executionStatus,
            @JsonProperty("executionOutput") String executionOutput,
            @JsonProperty("executionErrorOutput") String executionErrorOutput,
            @JsonProperty("executionEventTime") String executionEventTime)
    {
        super(to);
        this.executionStatus = executionStatus;
        this.executionOutput = executionOutput;
        this.executionErrorOutput = executionErrorOutput;
        this.executionEventTime = executionEventTime;
    }

    public String getExecutionStatus()
    {
        return executionStatus;
    }

    public String getExecutionOutput()
    {
        return executionOutput;
    }

    public String getExecutionErrorOutput()
    {
        return executionErrorOutput;
    }

    public String getExecutionEventTime()
    {
        return executionEventTime;
    }
}
