package org.github.jrds.codi.language.java;

import org.github.jrds.codi.core.domain.ExecutionStatus;
import org.github.jrds.codi.core.language.CodeExecutor;
import org.github.jrds.codi.core.language.ExecuteCodeOutputs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

public class JavaCodeExecutor implements CodeExecutor
{
    private String codeToExecute;
    private Path codeDirectory;
    private JavaExecuteCodeProcess executeCodeProcess;

    public JavaCodeExecutor(String codeToExecute)
    {
        this.codeToExecute = codeToExecute;
        try
        {
            codeDirectory = Files.createTempDirectory("codi");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute()
    {
        executeCodeProcess = new JavaExecuteCodeProcess(codeToExecute, codeDirectory);
        executeCodeProcess.startExecution();
    }

    @Override
    public void terminate()
    {
        if (executeCodeProcess != null)
        {
            executeCodeProcess.terminate();
        }
    }

    @Override
    public void input(String input)
    {
        if (executeCodeProcess != null)
        {
            executeCodeProcess.acceptInput(input);
        }
    }

    @Override
    public ExecutionStatus getStatus()
    {
        return executeCodeProcess == null ? ExecutionStatus.NEW : executeCodeProcess.getStatus();
    }

    @Override
    public String getCompilationErrors()
    {
        return executeCodeProcess == null ? "" : executeCodeProcess.getCompilationErrors();
    }

    @Override
    public ExecuteCodeOutputs getUnreadOutputs()
    {
        return executeCodeProcess == null ? null : executeCodeProcess.getUnreadOutputs();
    }

    @Override
    public Instant getTimeCompiled()
    {
        return executeCodeProcess == null ? null : executeCodeProcess.getTimeCompiled();
    }

    @Override
    public Instant getTimeExecutionStarted()
    {
        return executeCodeProcess == null ? null : executeCodeProcess.getTimeExecutionStarted();
    }

    @Override
    public Instant getTimeExecutionEnded()
    {
        return executeCodeProcess == null ? null : executeCodeProcess.getTimeExecutionEnded();
    }
}
