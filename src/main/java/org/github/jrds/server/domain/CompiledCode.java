package org.github.jrds.server.domain;

import java.time.Instant;

public class CompiledCode
{
    private String code;
    private String compilationResult;
    private Instant timeCompiled;
    private ExecutionStatus executionStatus;

    public CompiledCode(String code, String compilationResult, ExecutionStatus executionStatus)
    {
        this.code = code;
        this.compilationResult = compilationResult;
        this.timeCompiled = Instant.now();
        this.executionStatus = executionStatus;
    }

    public String getCode()
    {
        return code;
    }

    public String getCompilationResult()
    {
        return compilationResult;
    }

    public Instant getTimeCompiled()
    {
        return timeCompiled;
    }

    public ExecutionStatus getCompilationStatus()
    {
        return executionStatus;
    }
}
