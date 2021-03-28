package org.github.jrds.server.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExecuteCodeProcess
{
    private static final Pattern CLASS = Pattern.compile(".*?class\\s+([A-Za-z_][A-Za-z_]*)\\s*\\{.*");
    private static final Path JAVA_HOME = Paths.get(Objects.requireNonNull(System.getenv("JAVA_HOME"), "You must define JAVA_HOME environment variable"));
    private static final boolean IS_WINDOWS = System.getProperty("os.name").contains("Windows");
    private static final Path JAVA_BIN = JAVA_HOME.resolve("bin");
    private static final Path JAVA_CMD = JAVA_BIN.resolve(IS_WINDOWS ? "java.exe" : "java");
    private static final Path JAVAC_CMD = JAVA_BIN.resolve(IS_WINDOWS ? "javac.exe" : "javac");

    private final Object processLock = new Object();
    private final String className;
    private final String fileName;
    private final String codeToExecute;
    private final Path codeDirectory;
    private volatile ExecutionStatus status = ExecutionStatus.NEW;
    private Instant timeStarted;
    private Instant timeCompiled;
    private Instant timeExecutionStarted;
    private Instant timeExecutionEnded;
    private String compilationErrors;
    private Process executionProcess;
    private Reader processStdOutReader;

    ExecuteCodeProcess(String codeToExecute, Path codeDirectory)
    {
        this.codeToExecute = codeToExecute;
        this.codeDirectory = codeDirectory;
        Matcher matcher = CLASS.matcher(codeToExecute);
        if (matcher.matches())
        {
            className = matcher.group(1);
            fileName = className + ".java";
        }
        else
        {
            throw new IllegalArgumentException("Not a valid class definition");
        }
    }

    void startExecution()
    {
        timeStarted = Instant.now();
        compile();
        timeCompiled = Instant.now();
        if (status == ExecutionStatus.COMPILE_SUCCEEDED)
        {
            try
            {
                timeExecutionStarted = Instant.now();
                executionProcess = new ProcessBuilder().command(JAVA_CMD.toString(), className).directory(codeDirectory.toFile()).start();
                System.out.println("Execution started");
                status = ExecutionStatus.EXECUTION_IN_PROGRESS;
                processStdOutReader = new InputStreamReader(executionProcess.getInputStream());
                executionProcess.onExit().thenRun(() -> {
                    synchronized (processLock) {
                        status = ExecutionStatus.EXECUTION_FINISHED;
                        timeExecutionEnded = Instant.now();
                    }
                });
            }
            catch (IOException e)
            {
                System.out.println("Execution failed to start " + e.getMessage());
                status = ExecutionStatus.EXECUTION_FAILED_TO_START;
            }
        }
    }

    private void compile()
    {
        try
        {
            Path file = codeDirectory.resolve(fileName);
            Files.writeString(file, codeToExecute);
            Process compile = new ProcessBuilder().command(JAVAC_CMD.toString(), file.toString()).directory(codeDirectory.toFile()).start();
            int compileResult = compile.waitFor();
            if (compileResult == 0)
            {
                System.out.println("Compile worked");
                status = ExecutionStatus.COMPILE_SUCCEEDED;
            }
            else
            {
                System.out.println("Compile failed");
                status = ExecutionStatus.COMPILE_FAILED;
                compilationErrors = new BufferedReader(new InputStreamReader(compile.getErrorStream()))
                        .lines()
                        .collect(Collectors.joining("\n"))
                        .replace(file.toString(), fileName);
            }
        }
        catch (IOException | InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    public ExecutionStatus getStatus()
    {
        synchronized (processLock)
        {
            return status;
        }
    }

    public String getCompilationErrors()
    {
        return compilationErrors;
    }

    public Instant getTimeStarted()
    {
        return timeStarted;
    }

    public Instant getTimeCompiled()
    {
        return timeCompiled;
    }

    public Instant getTimeExecutionStarted()
    {
        return timeExecutionStarted;
    }

    public Instant getTimeExecutionEnded()
    {
        return timeExecutionEnded;
    }

    public String getUnreadOutput()
    {
        try
        {
            StringBuilder builder = new StringBuilder();
            char[] chars = new char[1024];
            long t0 = System.currentTimeMillis();
            synchronized (processLock)
            {
                while (processStdOutReader.ready() && (System.currentTimeMillis() - t0) < 800)
                {
                    int read = processStdOutReader.read(chars);
                    builder.append(chars, 0, read);
                }
            }
            return builder.toString();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void terminate()
    {
        synchronized (processLock)
        {
            if (executionProcess.isAlive())
            {
                executionProcess.destroyForcibly();
            }
        }
    }
}
