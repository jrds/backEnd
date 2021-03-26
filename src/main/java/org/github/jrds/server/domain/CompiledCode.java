package org.github.jrds.server.domain;

import java.time.Instant;

public class CompiledCode
{
    private String code;
    private String compilationResult;
    private Instant timeCompiled;
    private CompilationStatus compilationStatus;

    public CompiledCode(String code, String compilationResult, CompilationStatus compilationStatus)
    {
        this.code = code;
        this.compilationResult = compilationResult;
        this.timeCompiled = Instant.now();
        this.compilationStatus = compilationStatus;
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

    public CompilationStatus getCompilationStatus()
    {
        return compilationStatus;
    }
}
