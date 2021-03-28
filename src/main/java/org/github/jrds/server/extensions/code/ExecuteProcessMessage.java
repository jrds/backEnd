package org.github.jrds.server.extensions.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.server.messages.Info;

public class ExecuteProcessMessage extends Info
{
    private String executionStatus;
    private String executionOutput; //TODO output rather than result
    private String executionEventTime;
    // TODO - take the instant from the compiledCode obj, and convert to time, and send in the message

    // TODO - Write up Future feature for report, visualisation of number of compiler errors over time, and repeated errors.

    @JsonCreator
    public ExecuteProcessMessage(
            @JsonProperty("to") String to,
            @JsonProperty("executionStatus") String executionStatus,
            @JsonProperty("executionOutput") String executionOutput,
            @JsonProperty("executionEventTime") String executionEventTime)
    {
        super(to);
        this.executionStatus = executionStatus;
        this.executionOutput = executionOutput;
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

    public String getExecutionEventTime()
    {
        return executionEventTime;
    }
}
