package org.github.jrds.codi.messaging.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.codi.core.messages.Info;

public class CodeExecutionInfo extends Info
{
    private String executionStatus;
    private String executionOutput;
    private String executionErrorOutput;
    private String executionEventTime;

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
