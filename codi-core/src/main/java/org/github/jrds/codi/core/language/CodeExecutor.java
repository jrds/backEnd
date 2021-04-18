package org.github.jrds.codi.core.language;

import org.github.jrds.codi.core.domain.ExecutionStatus;

import java.time.Instant;

public interface CodeExecutor
{
    void execute();
    void terminate();
    void input(String input);

    ExecutionStatus getStatus();
    String getCompilationErrors();
    ExecuteCodeOutputs getUnreadOutputs();

    Instant getTimeCompiled();
    Instant getTimeExecutionStarted();
    Instant getTimeExecutionEnded();
}
